/*
* $HeadURL$
not* $Id$
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

package org.ambraproject.article.service;

import org.ambraproject.ApplicationException;
import org.ambraproject.article.BrowseParameters;
import org.ambraproject.article.BrowseResult;
import org.ambraproject.article.action.TOCArticleGroup;
import org.ambraproject.cache.Cache;
import org.ambraproject.journal.JournalService;
import org.ambraproject.model.IssueInfo;
import org.ambraproject.model.VolumeInfo;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.ArticleType;
import org.ambraproject.model.article.Years;
import org.ambraproject.search.SearchHit;
import org.ambraproject.search.service.SolrServerFactory;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.solr.SolrServiceUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Journal;
import org.topazproject.ambra.models.Retraction;
import org.topazproject.ambra.models.Volume;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Class to get all Articles in system and organize them by date and by category
 * from the SOLR index
 *
 * @author Jen Song
 * @author Joe Osowski
 */
public class BrowseServiceImpl extends HibernateServiceImpl implements BrowseService {
  private static final Logger log = LoggerFactory.getLogger(BrowseServiceImpl.class);

  private static final String ARTBYCAT_LIST_KEY   = "ArtByCat-";
  private static final String DATE_LIST_KEY       = "DateList-";
  private static final String ARTBYDATE_LIST_KEY  = "ArtByDate-";

  private Cache browseSolrCache;
  private JournalService journalService;
  private ArticleService articleService;
  private SolrServerFactory serverFactory;

  private int cacheTimeToLive = 15;
  private Boolean useCache = true;

  //We have two collections here, as list supports ordering
  //And we want to keep the sorts in the order in which they are defined
  private List displaySorts = null;
  private Map validSorts = null;

  private static final Integer getYear(String date) {
    return Integer.valueOf(date.substring(0, 4));
  }

  private static final Integer getMonth(String date) {
    return Integer.valueOf(date.substring(5, 7));
  }

  private static final Integer getDay(String date) {
    return Integer.valueOf(date.substring(8, 10));
  }

  /**
   * @param journalService The journal-service to use.
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * @param articleService The articleService to use.
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @param browseSolrCache The browse solr cache to use.
   */
  @Required
  public void setBrowseSolrCache(Cache browseSolrCache) {
    this.browseSolrCache = browseSolrCache;
    // the time to live will be short enough that we are not going to worry about invalidator logic
  }

  /**
   * Set the configuration class
   *
   * @param config the configuration class to use
   * @throws ApplicationException if required configuration settings are missing
   */
  @Required
  public void setConfiguration(Configuration config) throws ApplicationException {
    if(config.containsKey("ambra.services.browse.sortOptions.option")) {
      validSorts = new HashMap();
      displaySorts = new ArrayList();

      HierarchicalConfiguration hc = (HierarchicalConfiguration)config;
      List<HierarchicalConfiguration> sorts =
          hc.configurationsAt("ambra.services.browse.sortOptions.option");

      for (HierarchicalConfiguration s : sorts) {
        String key = s.getString("[@displayName]");
        String value = s.getString("");
        validSorts.put(key, value);
        displaySorts.add(key);
      }

      ((HierarchicalConfiguration) config).setExpressionEngine(null);
    } else {
      throw new ApplicationException("ambra.services.browse.sortOptions.option not defined " +
          "in configuration.");
    }

    this.cacheTimeToLive = config.getInt("ambra.services.browse.time", 15) * 60;
    this.useCache = config.getBoolean("ambra.services.browse.cache", true);
  }

  /**
   * The map of sorts that are valid for this provider
   * @return
   */
  public List getSorts()
  {
    return this.displaySorts;
  }

  /**
   * Get the dates of all articles with a <code>state</code> of <code>ACTIVE</code> (meaning the articles have been
   * published). The outer map is a map of years, the next inner map a map of months, and finally the innermost is a
   * list of days. <br/>
   *
   * @param  journalKey the current journal
   *
   * @return the article dates.
   */
  @Transactional(readOnly = true)
  public Years getArticleDatesForJournal(final String journalKey) {
    if (this.useCache) {
      String cacheKey = DATE_LIST_KEY + journalKey;

      return browseSolrCache.get(cacheKey, this.cacheTimeToLive,
          new Cache.SynchronizedLookup<Years, RuntimeException>(cacheKey.intern()) {
            @SuppressWarnings("synthetic-access")
            @Override
            public Years lookup() throws RuntimeException {
              return loadArticleDates(journalKey);
            }
          });
    } else {
      return loadArticleDates(journalKey);
    }
  }

  /**
   * Get articles in the given category. One "page" of articles will be returned, i.e. articles pageNum * pageSize ..
   * (pageNum + 1) * pageSize - 1 . Note that less than a pageSize articles may be returned, either because it's the end
   * of the list or because some articles are not accessible.
   *
   * @param params A collection filters / parameters to browse by
   * @return the articles.
   */
  @Transactional(readOnly = true)
  public BrowseResult getArticlesBySubject(final BrowseParameters params) {
    BrowseResult result;

    if (this.useCache) {
      final String cacheKey = ARTBYCAT_LIST_KEY + params.getJournalKey() + "-" +
        StringUtils.join(params.getSubjects(),"-") + "-" + params.getSort() + "-" +
        "-" + params.getPageNum() + "-" + params.getPageSize();

      result = browseSolrCache.get(cacheKey, this.cacheTimeToLive,
          new Cache.SynchronizedLookup<BrowseResult, RuntimeException>(cacheKey.intern()) {
            @Override
            public BrowseResult lookup() throws RuntimeException {
              return getArticlesBySubjectViaSolr(params);
            }
          });
    } else {
      result = getArticlesBySubjectViaSolr(params);
    }

    return result;
  }

  /**
   * Get articles in the given date range, from newest to oldest, of the given article type(s). One "page" of articles
   * will be returned, i.e. articles pageNum * pageSize .. (pageNum + 1) * pageSize - 1 . Note that less than a pageSize
   * articles may be returned, either because it's the end of the list or because some articles are not accessible.
   * <p/>
   * Note: this method assumes the dates are truly just dates, i.e. no hours, minutes, etc.
   * <p/>
   * If the <code>articleTypes</code> parameter is null or empty, then all types of articles are returned.
   * <p/>
   * This method should never return null.
   *
   * @param params A collection filters / parameters to browse by
   * @return the articles.
   */
  @Transactional(readOnly = true)
  public BrowseResult getArticlesByDate(final BrowseParameters params) {

    BrowseResult result;

    if (this.useCache) {
      String mod = params.getJournalKey() + "-" + params.getStartDate().getTimeInMillis() + "-" +
        params.getEndDate().getTimeInMillis() + "-" + params.getSort();
      String cacheKey = ARTBYDATE_LIST_KEY + mod + "-" + params.getPageNum() + "-" + params.getPageSize();

      result = browseSolrCache.get(cacheKey, this.cacheTimeToLive,
          new Cache.SynchronizedLookup<BrowseResult, RuntimeException>(cacheKey.intern()) {
            @Override
            public BrowseResult lookup() throws RuntimeException {
              return getArticlesByDateViaSolr(params);
            }
          });
    } else {
      result = getArticlesByDateViaSolr(params);
    }

    return result;
  }

  /**
   * Get a list of article-counts for each category.
   *
   * @param journalKey The current journal
   * @return the category infos.
   */
  @Transactional(readOnly = true)
  public SortedMap<String, Long> getSubjectsForJournal(final String journalKey) {
    if (this.useCache) {
      final String cacheKey = ARTBYCAT_LIST_KEY + journalKey;

      return browseSolrCache.get(cacheKey, this.cacheTimeToLive,
          new Cache.SynchronizedLookup<SortedMap<String, Long>, RuntimeException>(cacheKey.intern()) {
            @Override
            public SortedMap<String, Long> lookup() throws RuntimeException {
              return getSubjectsForJournalViaSolr(journalKey);
            }
          });
    } else {
      return getSubjectsForJournalViaSolr(journalKey);
    }
  }

  /**
   * Get Issue information.
   *
   * @param issueDoi DOI of Issue.
   * @return the Issue information.
   */
  @Transactional(readOnly = true)
  public IssueInfo getIssueInfo(final URI issueDoi) {
    return (IssueInfo)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        // get the Issue
        final Issue issue = (Issue) hibernateTemplate.get(Issue.class, issueDoi);
        if (issue == null) {
          log.error("Failed to retrieve Issue for doi='" + issueDoi.toString() + "'");
          return null;
        }

        // get the image Article
        URI imageArticle = null;
        String description = issue.getDescription();
        if ((issue.getImage() != null)
            && (issue.getImage().toString().length() != 0)) {
          imageArticle = issue.getImage();
        }

        // derive prev/next Issue, "parent" Volume
        URI prevIssueURI = null;
        URI nextIssueURI = null;
        Volume parentVolume = null;

        List<String> results = session
            .createSQLQuery("select aggregationUri from VolumeIssueList where issueUri = :doi")
            .setParameter("doi", issueDoi.toString()).list();
        if (results.size() > 0) {
          URI volumeURI = URI.create(results.get(0));
          parentVolume = (Volume)session.load(Volume.class,  volumeURI);
        }
        if (parentVolume != null) {
          final List<URI> issues = parentVolume.getIssueList();
          final int issuePos = issues.indexOf(issueDoi);
          prevIssueURI = (issuePos == 0) ? null : issues.get(issuePos - 1);
          nextIssueURI = (issuePos == issues.size() - 1) ? null : issues.get(issuePos + 1);
        } else {
          log.warn("Issue: " + issue.getId() + ", not contained in any Volumes");
        }

        IssueInfo issueInfo = new IssueInfo(issue.getId(), issue.getDisplayName(), prevIssueURI,
            nextIssueURI, imageArticle, description,
            parentVolume == null ? null : parentVolume.getId());
        issueInfo.setArticleUriList(getArticleList(issue));
        return issueInfo;

      }
    });
  }

  /**
   * Return the ID of the latest issue from the latest volume. If no issue exists in the latest volume, then look at the
   * previous volume and so on. The Current Issue for each Journal should be configured via the admin console. This
   * method is a reasonable way to get the most recent Issue if Current Issue was not set.
   *
   * @param journal The journal in which to seek the most recent Issue
   * @return The most recent Issue from the most recent Volume, or null if there are no Issues
   */
  public URI getLatestIssueFromLatestVolume(Journal journal) {
    List<VolumeInfo> vols = getVolumeInfosForJournal(journal);
    if (vols.size() > 0) {
      for (VolumeInfo volInfo : vols) {
        IssueInfo latestIssue = null;
        List<IssueInfo> issuesInVol = volInfo.getIssueInfos();
        if (issuesInVol.size() > 0) {
          latestIssue = issuesInVol.get(issuesInVol.size() - 1);
        }
        if (latestIssue != null) {
          return latestIssue.getId();
        }
      }
    }
    return null;
  }

  /**
   * Returns the list of ArticleInfos contained in this Issue. The list will contain only ArticleInfos for Articles that
   * the current user has permission to view.
   *
   * @param issueDOI Issue ID
   * @param authId the AuthId of the current user
   * @return List of ArticleInfo objects.
   */
  @Transactional(readOnly = true)
  public List<ArticleInfo> getArticleInfosForIssue(final URI issueDOI, String authId){

    IssueInfo iInfo = getIssueInfo(issueDOI);
    List<ArticleInfo> aInfos = new ArrayList<ArticleInfo>();

    for (URI articleDoi : iInfo.getArticleUriList()) {
      try{
        ArticleInfo ai = articleService.getArticleInfo(articleDoi.toString(), authId);
        aInfos.add(ai);
      }catch (NoSuchArticleIdException ex){
        log.warn("Article " + articleDoi + " missing; member of Issue " + issueDOI);
      }
    }
    return aInfos;
  }

  /**
   * Get a VolumeInfo for the given id. This only works if the volume is in the current journal.
   *
   * @param id Volume ID
   * @return VolumeInfo
   */
  @Transactional(readOnly = true)
  public VolumeInfo getVolumeInfo(URI id, String journalKey) {
    // Attempt to get the volume infos from the cached journal list...
    List<VolumeInfo> volumes = getVolumeInfosForJournal(journalService.getJournal(journalKey));
    for (VolumeInfo vol : volumes) {
      if (id.equals(vol.getId())) {
        return vol;
      }
    }

    /*
     * If we have no luck with the cached journal list, attempt to load the volume re-using
     * loadVolumeInfos();
     */
    List<URI> l = new ArrayList<URI>();
    l.add(id);
    List<VolumeInfo> vols = loadVolumeInfos(l);
    if ((vols != null) && (vols.size() > 0)) {
      return vols.get(0);
    }
    return null;
  }

  /**
   * Returns a list of VolumeInfos for the given Journal. VolumeInfos are sorted in reverse order to reflect most common
   * usage. Uses the pull-through cache.
   *
   * @param journal To find VolumeInfos for.
   * @return VolumeInfos for journal in reverse order.
   */
  @Transactional(readOnly = true)
  public List<VolumeInfo> getVolumeInfosForJournal(final Journal journal) {
    final List<URI> volumeDois = journal.getVolumes();
    List<VolumeInfo> volumeInfos = loadVolumeInfos(volumeDois);
    Collections.reverse(volumeInfos);
    return volumeInfos;
  }

  /**
   * Get VolumeInfos. Note that the IssueInfos contained in the volumes have not been instantiated with the
   * ArticleInfos.
   *
   * @param volumeDois to look up.
   * @return volumeInfos.
   */
  private List<VolumeInfo> loadVolumeInfos(final List<URI> volumeDois) {
    List<VolumeInfo> volumeInfos = new ArrayList<VolumeInfo>();

    // get the Volumes
    for (int onVolumeDoi = 0; onVolumeDoi < volumeDois.size(); onVolumeDoi++) {
      final URI volumeDoi = volumeDois.get(onVolumeDoi);
      final Volume volume = (Volume) hibernateTemplate.get(Volume.class, volumeDoi);
      if (volume == null) {
        log.error("unable to load Volume: " + volumeDoi);
        continue;
      }

      List<IssueInfo> issueInfos = new ArrayList<IssueInfo>();
      for (final URI issueDoi : volume.getIssueList()) {
        issueInfos.add(getIssueInfo(issueDoi));
      }

      // calculate prev/next
      final URI prevVolumeDoi = (onVolumeDoi == 0) ? null : volumeDois.get(onVolumeDoi - 1);
      final URI nextVolumeDoi = (onVolumeDoi == volumeDois.size() - 1) ? null
          : volumeDois.get(onVolumeDoi + 1);
      final VolumeInfo volumeInfo = new VolumeInfo(volume.getId(), volume.getDisplayName(),
          prevVolumeDoi, nextVolumeDoi, volume.getImage(), volume.getDescription(), issueInfos);
      volumeInfos.add(volumeInfo);
    }

    return volumeInfos;
  }

  private Years loadArticleDates(String journalKey) {
    Years dates = new Years();

    SolrQuery query = createCommonQuery(journalKey);

    query.setFacet(true);
    query.addFacetField("publication_date");
    query.setFacetLimit(-1);
    query.setFacetMinCount(1);

    query.setRows(0);

    try {
      QueryResponse response = this.serverFactory.getServer().query(query);
      FacetField ff = response.getFacetField("publication_date");
      List<FacetField.Count> counts = ff.getValues();
      for (FacetField.Count count : counts) {
        String publicationDate = count.getName();
        Integer y = getYear(publicationDate);
        Integer m = getMonth(publicationDate);
        Integer d = getDay(publicationDate);
        dates.getMonths(y).getDays(m).add(d);
      }
    } catch (SolrServerException e) {
      log.error("Unable to execute a query on the Solr Server.", e);
    }

    return dates;
  }

  /**
   * Given a list of Article Groups with correctly ordered articles create a CSV string of article URIs. The URIs will
   * be in the order that they show up on the TOC.
   *
   * @param articleGroups the list of TOCArticleGroup to process.
   * @return a string of a comma separated list of article URIs
   */
  public String articleGrpListToCSV(List<TOCArticleGroup> articleGroups) {
    StringBuilder articleList = new StringBuilder();
    Iterator i = articleGroups.listIterator();

    // Group Loop
    while (i.hasNext()) {
      TOCArticleGroup ag = (TOCArticleGroup) i.next();
      Iterator y = ag.articles.listIterator();

      // Article Loop
      while (y.hasNext()) {
        ArticleInfo ai = (ArticleInfo) y.next();
        articleList.append(ai.doi);

        if (y.hasNext())
          articleList.append(',');
      }
      if (i.hasNext())
        articleList.append(',');
    }
    return articleList.toString();
  }

  /**
   *
   */
  public List<TOCArticleGroup> getArticleGrpList(URI issueURI, String authId) {
    Issue issue = (Issue) hibernateTemplate.get(Issue.class, issueURI);

    // If the issue does not exist then return an empty list
    if (issue == null)
      return new ArrayList<TOCArticleGroup>();

    return getArticleGrpList(issue, authId);
  }

  /**
   *
   */
  public List<TOCArticleGroup> getArticleGrpList(Issue issue, String authId){
    List<TOCArticleGroup> groupList = new ArrayList<TOCArticleGroup>();

    for (ArticleType at : ArticleType.getOrderedListForDisplay()) {
      TOCArticleGroup newArticleGroup = new TOCArticleGroup(at);
      groupList.add(newArticleGroup);
    }

    return buildArticleGroups(issue, groupList, authId);
  }

  /**
   *
   */
  public List<TOCArticleGroup> buildArticleGroups(Issue issue, List<TOCArticleGroup> articleGroups,
                                                  String authId){
    List<ArticleInfo> articlesInIssue = getArticleInfosForIssue(issue.getId(), authId);
    /*
     * For every article that is of the same ArticleType as a TOCArticleGroup, add it to that group.
     * Articles can appear in multiple TOCArticleGroups.
     */
    for (ArticleInfo ai : articlesInIssue)
      for (TOCArticleGroup ag : articleGroups)
        for (ArticleType articleType : ai.getArticleTypes())
          if (ag.getArticleType().equals(articleType)) {
            ag.addArticle(ai);
            break;
          }

    Iterator iter = articleGroups.listIterator();
    Integer i = 0;

    while (iter.hasNext()) {
      TOCArticleGroup ag = (TOCArticleGroup) iter.next();
      // remove the group if it has no articles
      if (ag.articles.size() == 0) {
        iter.remove();
        continue;
      }
      // If we respect order then don't sort.
      if (!issue.getRespectOrder()) {
        ag.setId("tocGrp_" + (i++));
        ag.sortArticles();
      }
    }
    return articleGroups;
  }

  /**
   * Get ordered list of articles. Either from articleList or from simpleCollection if articleList is empty.
   *
   * @param issue
   * @return List of article URI's
   */
  public List<URI> getArticleList(Issue issue) {
    List<URI> articleList = issue.getArticleList();
    if (articleList.isEmpty() && !issue.getSimpleCollection().isEmpty())
      return new ArrayList<URI>(issue.getSimpleCollection());

    return articleList;
  }

  /**
   * ArticleInfo objects store lists of FormalCorrection id's because ArticleInfo is object that is not cached in the
   * ObjectCache and it cannot hold referencews to OTM entities because it will cause it to fail serialization. We need
   * to fetch FormalCorrection objects and put them in a Map so they can be displayed.
   *
   * @param articleGroups List of article groups
   * @return Map of all FormalCorrection objects contained in articleGroups
   */
  public Map<URI, FormalCorrection> getCorrectionMap(List<TOCArticleGroup> articleGroups) {
    Map<URI, FormalCorrection> correctionMap = new HashMap<URI, FormalCorrection>();

    for (TOCArticleGroup articleGroup : articleGroups) {
      for (ArticleInfo articleInfo : articleGroup.getArticles()) {
        if (articleInfo.getCorrections() != null) {
          for (URI correctionId : articleInfo.getCorrections()) {
            correctionMap.put(
                correctionId,
                (FormalCorrection) hibernateTemplate.get(FormalCorrection.class, correctionId)
            );
          }
        }
      }
    }

    return correctionMap;
  }

  /**
   * ArticleInfo objects store lists of Retraction id's because ArticleInfo is object that is not cached in the
   * ObjectCache and it cannot hold referencews to OTM entities because it will cause it to fail serialization. We need
   * to fetch Retraction objects and put them in a Map so they can be displayed.
   *
   * @param articleGroups List of article groups
   * @return Map of all Retraction objects contained in articleGroups
   */
  public Map<URI, Retraction> getRetractionMap(List<TOCArticleGroup> articleGroups) {
    Map<URI, Retraction> retractionMap = new HashMap<URI, Retraction>();

    for (TOCArticleGroup articleGroup : articleGroups) {
      for (ArticleInfo articleInfo : articleGroup.getArticles()) {
        if (articleInfo.getRetractions() != null) {
          for (URI retractionId : articleInfo.getRetractions()) {
            retractionMap.put(
                retractionId,
                (Retraction) hibernateTemplate.get(Retraction.class, retractionId)
            );
          }
        }
      }
    }

    return retractionMap;
  }

  /**
   * Sets the solr server factory object
   * @param serverFactory solr server factory
   */
  public void setServerFactory(SolrServerFactory serverFactory) {
    this.serverFactory = serverFactory;
  }

  /**
   * Get a list of article counts for each category
   *
   * @param journalKey the current journal
   *
   * @return category info
   */
  private SortedMap<String, Long> getSubjectsForJournalViaSolr(String journalKey) {

    SortedMap<String, Long> categories = new TreeMap<String, Long>();

    SolrQuery query = createCommonQuery(journalKey);

    query.setFacet(true);
    query.addFacetField("subject_level_1");
    query.setFacetLimit(-1);
    query.setFacetMinCount(1);

    query.setRows(0);

    try {
      QueryResponse response = this.serverFactory.getServer().query(query);
      FacetField ff = response.getFacetField("subject_level_1");
      List<FacetField.Count> counts = ff.getValues();
      if (counts != null) {
        for (FacetField.Count count : counts) {
          categories.put(count.getName(), count.getCount());
        }
      }
    } catch (SolrServerException e) {
      log.error("Unable to execute a query on the Solr Server.", e);
    }

    return categories;
  }

  /**
   * Returns a list of articles for a given category
   * @param params a collection filters / parameters to browse by
   * @return articles
   */
  private BrowseResult getArticlesBySubjectViaSolr(BrowseParameters params) {
    BrowseResult result = new BrowseResult();
    ArrayList<SearchHit> articles = new ArrayList<SearchHit>();
    long total = 0;

    SolrQuery query = createCommonQuery(params.getJournalKey());

    query.addField("title_display");
    query.addField("author_display");
    query.addField("article_type");
    query.addField("publication_date");
    query.addField("id");
    query.addField("abstract_primary_display");
    query.addField("eissn");

    if (params.getSubjects() != null && params.getSubjects().length > 0) {
      StringBuffer subjectQuery = new StringBuffer();
      for (String subject : params.getSubjects()) {
        subjectQuery.append("\"").append(subject).append("\"").append(" AND ");
      }
      // remove the last " AND "
      query.setQuery("subject_level_1:(" +  subjectQuery.substring(0, subjectQuery.length() - 5) + ")");
    }

    // we use subject_level_1 field instead of subject_facet field because
    // we are only interested in the top level subjects
    query.setFacet(true);
    query.addFacetField("subject_level_1");
    query.setFacetMinCount(1);
    query.setFacetSort("index");

    setSort(query, params);

    query.setStart(params.getPageNum() * params.getPageSize());
    query.setRows(params.getPageSize());

    try {
      QueryResponse response = this.serverFactory.getServer().query(query);
      SolrDocumentList documentList = response.getResults();
      total = documentList.getNumFound();

      for (SolrDocument document : documentList) {
        SearchHit sh = createArticleBrowseDisplay(document, query.toString());
        articles.add(sh);
      }

      result.setSubjectFacet(facetCountsToHashMap(response.getFacetField("subject_level_1")));
    } catch (SolrServerException e) {
      log.error("Unable to execute a query on the Solr Server.", e);
    }

    result.setTotal(total);
    result.setArticles(articles);

    return result;
  }

  /**
   * Sorting values stored in the config, this parses through those values and
   * sets up the correct parameters for SOLR
   *
   * @param query The SolrQuery which will have a <i>sort</i> clause attached
   * @param params The SearchParameters DTO which contains the <code>sort</code> field used by this method
   */
  private void setSort(SolrQuery query, BrowseParameters params) throws RuntimeException {
    log.debug("SearchParameters.sort = {}", params.getSort());

    String sortKey = params.getSort();

    if(sortKey != null && sortKey.trim().length() > 0) {
      String sortValue = (String)validSorts.get(sortKey);

      if(sortValue == null) {
        throw new RuntimeException("Invalid sort of '" + params.getSort() + "' specified.");
      }

      //First tokenize up defined sorts into tokens on comma: ","
      StringTokenizer sortTokens = new StringTokenizer(sortValue,",");

      while(sortTokens.hasMoreTokens()) {
        //Now tokenize each sort command on space
        StringTokenizer curSort = new StringTokenizer(sortTokens.nextToken());
        String fieldName = curSort.nextToken(); // First token
        String sortDirection = null;

        if (curSort.hasMoreTokens()) {
          sortDirection = curSort.nextToken(); // Second token
        }

        if (sortDirection == null || !sortDirection.toLowerCase().equals("asc")) {
          query.addSortField(fieldName, SolrQuery.ORDER.desc);
        } else {
          query.addSortField(fieldName, SolrQuery.ORDER.asc);
        }
      }

    } else {
      //If no sort is specified default to publication_date
      query.addSortField("publication_date", SolrQuery.ORDER.desc);
    }
    //If everything else is equal, order by id
    query.addSortField("id", SolrQuery.ORDER.desc);
  }

  /**
   * Returns list of articles in a given date range, from newest to oldest
   * @param params the collection class of parameters.
   * @return the articles
   */
  private BrowseResult getArticlesByDateViaSolr(BrowseParameters params) {
    BrowseResult result = new BrowseResult();
    ArrayList<SearchHit> articles = new ArrayList<SearchHit>();
    long totalSize = 0;

    SolrQuery query = createCommonQuery(params.getJournalKey());

    query.addField("title_display");
    query.addField("author_display");
    query.addField("article_type");
    query.addField("publication_date");
    query.addField("id");
    query.addField("abstract_primary_display");
    query.addField("eissn");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String sDate = sdf.format(params.getStartDate().getTime());
    String eDate = sdf.format(params.getEndDate().getTime());

    sDate = sDate + "T00:00:00Z";
    eDate = eDate + "T00:00:00Z";

    query.addFilterQuery("publication_date:[" + sDate + " TO " + eDate + "]");

    StringBuffer sb = new StringBuffer();
    if (params.getArticleTypes() != null && params.getArticleTypes().size() > 0) {
      for (URI uri : params.getArticleTypes()) {
        String path = uri.getPath();
        int index = path.lastIndexOf("/");
        if (index != -1) {
          String articleType = path.substring(index + 1);
          sb.append("\"").append(articleType).append("\"").append(" OR ");
        }
      }
      String articleTypesQuery = sb.substring(0, sb.length() - 4);

      if (articleTypesQuery.length() > 0) {
        query.addFilterQuery("article_type_facet:" + articleTypesQuery);
      }
    }

    setSort(query, params);

    query.setStart(params.getPageNum() * params.getPageSize());
    query.setRows(params.getPageSize());

    log.info("getArticlesByDate Solr Query:" + query.toString());

    try {
      QueryResponse response = this.serverFactory.getServer().query(query);
      SolrDocumentList documentList = response.getResults();
      totalSize = documentList.getNumFound();

      for (SolrDocument document : documentList) {
        SearchHit sh = createArticleBrowseDisplay(document, query.toString());
        articles.add(sh);
      }

    } catch (SolrServerException e) {
      log.error("Unable to execute a query on the Solr Server.", e);
    }


    result.setArticles(articles);
    result.setTotal(totalSize);

    return result;
  }

  /**
   * Creates a commonly used SolrQuery object
   * @return pre-populated SolrQuery object
   */
  private SolrQuery createCommonQuery(String journalKey) {
    SolrQuery query = new SolrQuery("*:*");

    query.addFilterQuery("doc_type:full");
    query.addFilterQuery("!article_type_facet:\"Issue Image\"");
    query.addFilterQuery("cross_published_journal_key:" + journalKey);

    return query;
  }

  /**
   * Populates the SearchHit object using SolrDocument object (from search result)
   * @param document one search result
   * @param query query
   * @return populated SearchHit object
   */
  private SearchHit createArticleBrowseDisplay(SolrDocument document, String query) {

    String id = SolrServiceUtil.getFieldValue(document, "id", String.class, query);
    String message = id == null ? query : id;
    String title = SolrServiceUtil.getFieldValue(document, "title_display", String.class, message);
    Date publicationDate = SolrServiceUtil.getFieldValue(document, "publication_date", Date.class, message);
    String eissn = SolrServiceUtil.getFieldValue(document, "eissn", String.class, message);
    String articleType = SolrServiceUtil.getFieldValue(document, "article_type", String.class, message);
    String abstractDisplay = SolrServiceUtil.getFieldValue(document, "abstract_primary_display", String.class, message);

    List<String> authorList = SolrServiceUtil.getFieldMultiValue(document, message, String.class, "author_display");

    SearchHit hit = new SearchHit(null, id, title, null, authorList, publicationDate, eissn, null, articleType);
    hit.setAbstractPrimary(abstractDisplay);

    return hit;
  }

  /**
   * Checks to see if solr is up or not
   * @throws org.apache.solr.client.solrj.SolrServerException
   * @throws java.io.IOException
   */
  public void pingSolr() throws SolrServerException, IOException {
    this.serverFactory.getServer().ping();
  }

  //TODO: refactor this somehow, this method (for the most part) is also in SOLRSearchService
  private Map<String, Long> facetCountsToHashMap(FacetField field)
  {
    List<FacetField.Count> counts = field.getValues();
    TreeMap<String, Long> result = new TreeMap<String, Long>();

    if(counts != null) {
      for (FacetField.Count count : counts) {
        result.put(count.getName(), count.getCount());
      }
      return result;
    } else {
      return null;
    }
  }
}
