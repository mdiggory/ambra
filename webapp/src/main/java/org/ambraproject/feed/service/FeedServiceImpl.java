/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.feed.service;

import org.ambraproject.ApplicationException;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.article.action.TOCArticleGroup;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.BrowseService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.cache.Cache;
import org.ambraproject.journal.JournalService;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.models.Article;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.solr.SolrException;
import org.ambraproject.solr.SolrFieldConversion;
import org.ambraproject.solr.SolrHttpService;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.topazproject.ambra.models.Journal;
import org.w3c.dom.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>FeedService</code> supplies the API for querying and caching
 * feed request. <code>FeedService</code> is a Spring injected singleton
 * which coordinates access to the <code>annotationService, articleService</code>
 * and <code>feedCache</code>.
 */
public class FeedServiceImpl extends HibernateServiceImpl implements FeedService {
  private static final Logger log = LoggerFactory.getLogger(FeedServiceImpl.class);

  private AnnotationService   annotationService;    // Annotation service Spring injected.
  private ArticleService      articleService;       // Article service Spring injected
  private BrowseService       browseService;        // Browse Article Servcie Spring Injected
  private JournalService      journalService;       // Journal service Spring injected.
  private Cache               feedCache;            // Feed Cache Spring injected
  private SolrHttpService     solrHttpService;      // solr service
  private Configuration       configuration;
  private SolrFieldConversion solrFieldConverter;


  /**
   * Constructor - currently does nothing.
   */
  public FeedServiceImpl(){
  }

  /**
   * Creates and returns a new <code>Key</code> for clients of FeedService.
   *
   * @return Key a new cache key to be used as a data model for the FeedAction.
   */
  public ArticleFeedCacheKey newCacheKey() {
    return new ArticleFeedCacheKey();
  }

  /**
   * Queries for a list of articles from solr using the parameters set in cacheKey
   *
   * @param cacheKey
   * @return solr search result that contains list of articles
   */
  public Document getArticles(final ArticleFeedCacheKey cacheKey) {
    Map<String, String> params = new HashMap<String, String>();
    // result format
    params.put("wt", "xml");
    // what I want returned, the fields needed for rss feed
    params.put("fl", "id,title_display,publication_date,author_without_collab_display,author_collab_only_display," +
      "author_display,volume,issue,article_type,subject_hierarchy,abstract_primary_display,copyright");

    // filters
    String fq = "doc_type:full " +
      "AND !article_type_facet:\"Issue Image\" " +
      "AND cross_published_journal_key:" + cacheKey.getJournal();

    String[] categories = cacheKey.getCategories();
    if (categories != null && categories.length > 0) {
      StringBuffer sb = new StringBuffer();
      for (String category : categories) {
        sb.append("\"").append(category).append("\" AND ");
      }
      params.put("q", "subject_level_1:(" + sb.substring(0, sb.length() - 5) + ")");
    }

    if (cacheKey.getAuthor() != null) {
      fq = fq + " AND author:\"" + cacheKey.getAuthor() + "\"";
    }

    String startDate = "*";
    String endDate = "*";
    boolean addDateRange = false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (cacheKey.getSDate() != null) {
      startDate = sdf.format(cacheKey.getSDate().getTime());
      startDate = startDate + "T00:00:00Z";
      addDateRange = true;
    }
    if (cacheKey.getEDate() != null) {
      endDate = sdf.format(cacheKey.getEDate().getTime());
      endDate = endDate + "T00:00:00Z";
      addDateRange = true;
    }

    if (addDateRange == true) {
      fq = fq + " AND publication_date:[" + startDate + " TO " + endDate + "]";
    }

    params.put("fq", fq);

    // number of results
    params.put("rows", Integer.toString(cacheKey.getMaxResults()));

    // sort the result

    if (cacheKey.isMostViewed())  {
      // Sorts RSS Feed for the most viewed articles linked from the most viewed tab.
      String mostViewedKey = "ambra.virtualJournals." + journalService.getCurrentJournalName() + ".mostViewedArticles";
      Integer days = configuration.getInt(mostViewedKey + ".timeFrame");
      String sortField = (days != null) ? solrFieldConverter.getViewCountingFieldName(days)
        : solrFieldConverter.getAllTimeViewsField();
      params.put("sort", sortField + " desc");
    } else {
      params.put("sort", "publication_date desc");
    }

    Document result = null;
    try {
      result = solrHttpService.makeSolrRequest(params);
    } catch (SolrException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   *
   * @param cacheKey is both the feedAction data model and cache key.
   * @param journal Current journal
   * @return List&lt;String&gt; if article Ids.
   * @throws ApplicationException ApplicationException
   * @throws java.net.URISyntaxException URISyntaxException
   */
  public List<String> getIssueArticleIds(final ArticleFeedCacheKey cacheKey, String journal, String authId) throws
      URISyntaxException, ApplicationException {
    List<String> articleList  = new ArrayList<String>();
    URI issurURI = (cacheKey.getIssueURI() != null) ? URI.create(cacheKey.getIssueURI()) : null;

    if (issurURI == null) {
      Journal curJrnl = journalService.getJournal(journal);
      issurURI = curJrnl.getCurrentIssue();
    }

    List<TOCArticleGroup> articleGroups = browseService.getArticleGrpList(issurURI, authId);

    for(TOCArticleGroup ag : articleGroups)
      for(ArticleInfo article : ag.articles)
        articleList.add( article.doi);

    return articleList;
  }

  /**
   * Given a list of articleIds return a list of articles corresponding to the Ids.
   *
   * @param articleIds  List&lt;String&gt; of article Ids to fetch
   * @parem authId the current user authId
   *
   * @return <code>List&lt;Article&gt;</code> of articles
   * @throws java.text.ParseException ParseException
   */
  public List<Article> getArticles(List<String> articleIds, final String authId) throws ParseException, NoSuchArticleIdException {
    return articleService.getArticles(articleIds, authId);
  }

  /**
   * Returns a list of annotation Ids based on parameters contained in
   * the cache key. If a start date is not specified then a default
   * date is used but not stored in the key.
   *
   * @param cacheKey cache key.
   * @return <code>List&lt;String&gt;</code> a list of annotation Ids
   * @throws ApplicationException   Converts all exceptions to ApplicationException
   */
  public List<String> getAnnotationIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException {

    if (cacheKey.isUseCache()) {
      // Create a local lookup based on the feed URI.
      Cache.Lookup<List<String>, ApplicationException> lookUp =
        new Cache.SynchronizedLookup<List<String>, ApplicationException>(cacheKey) {
          public List<String> lookup() throws ApplicationException {
            return fetchAnnotationIds(cacheKey);
          }
        };
      // Get articel ID's from the feed cache or add it
      return feedCache.get(cacheKey, -1, lookUp);
    }

    // OR, for the cases in which we do not want to use the cache, just do a direct lookup.
    return fetchAnnotationIds(cacheKey);
  }

  /**
   * Returns a list of reply Ids based on parameters contained in
   * the cache key. If a start date is not specified then a default
   * date is used but not stored in the key.
   *
   * @param cacheKey cache key
   * @return <code>List&lt;String&gt;</code> a list of reply Ids
   * @throws ApplicationException   Converts all exceptions to ApplicationException
   */
  public List<String> getReplyIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException {

    if (cacheKey.isUseCache()) {
      // Create a local lookup based on the feed URI.
      Cache.Lookup<List<String>, ApplicationException> lookUp =
        new Cache.SynchronizedLookup<List<String>, ApplicationException>(cacheKey) {
          public List<String> lookup() throws ApplicationException {
            return fetchReplyIds(cacheKey);
          }
        };
      // Get articel ID's from the feed cache or add it
      return feedCache.get(cacheKey, -1, lookUp);
    }

    // OR, for the cases in which we do not want to use the cache, just do a direct lookup.
    return fetchReplyIds(cacheKey);
  }

  private List<String> fetchAnnotationIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException {

    List<String> annotIds;
    try {
      annotIds = annotationService.getFeedAnnotationIds(
                 cacheKey.getStartDate(), cacheKey.getEndDate(), cacheKey.getAnnotationTypes(),
                 cacheKey.getMaxResults(), cacheKey.getJournal());

    } catch (Exception ex) {
      throw new ApplicationException(ex);
    }
    return  annotIds;
  }

  private List<String> fetchReplyIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException {

    List<String> replyIds;
    try {
      replyIds = annotationService.getReplyIds(
                 cacheKey.getStartDate(), cacheKey.getEndDate(), cacheKey.getAnnotationTypes(),
                 cacheKey.getMaxResults(), cacheKey.getJournal());

    } catch (Exception ex) {
      throw new ApplicationException(ex);
    }
    return  replyIds;
  }

  /**
   * @param journalService   Journal Service
   */
  @SuppressWarnings("synthetic-access")
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * @param articleService Article Service
   */
  @SuppressWarnings("synthetic-access")
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @param annotationService   Annotation Service
   */
  @SuppressWarnings("synthetic-access")
  @Required
  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }


  /**
   * @param browseService   Browse Service
   */
  @SuppressWarnings("synthetic-access")
  @Required
  public void setBrowseService(BrowseService browseService) {
    this.browseService = browseService;
  }


  /**
   * @param feedCache  Feed Cache
   */
  @SuppressWarnings("synthetic-access")
  @Required
  public void setFeedCache(Cache feedCache) {
    this.feedCache = feedCache;
  }

  /**
   * Set solr http service
   * @param solrHttpService solr http service
   */
  public void setSolrHttpService(SolrHttpService solrHttpService) {
    this.solrHttpService = solrHttpService;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public void setSolrFieldConverter(SolrFieldConversion solrFieldConverter) {
    this.solrFieldConverter = solrFieldConverter;
  }
}
