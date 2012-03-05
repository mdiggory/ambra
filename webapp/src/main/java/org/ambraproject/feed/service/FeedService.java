/* $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
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
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.models.Article;
import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.MinorCorrection;
import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingSummary;
import org.topazproject.ambra.models.Reply;
import org.topazproject.ambra.models.Retraction;
import org.topazproject.ambra.models.Trackback;
import org.w3c.dom.Document;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

/**
 * The <code>FeedService</code> supplies the API for querying and caching feed request. <code>FeedService</code> is a
 * Spring injected singleton which coordinates access to the <code>annotationService, articleService</code> and
 * <code>feedCache</code>.
 */
public interface FeedService {

  //  When the "formatting" parameter is set to this value, the feed will show the complete text of every available field.
  public static final String FEED_FORMATTING_COMPLETE = "complete";

  /**
   * The feedAction data model has a types parameter which specifies the feed type (currently Article or Annotation).
   * Invalid is used when the types parameter does not match any of the current feed types.
   */
  public static enum FEED_TYPES {
    Article(null, Article.class),
    Annotation(org.topazproject.ambra.models.Annotation.RDF_TYPE, Annotation.class),
    Comment(org.topazproject.ambra.models.Comment.RDF_TYPE, Comment.class),
    FormalCorrection(org.topazproject.ambra.models.FormalCorrection.RDF_TYPE, FormalCorrection.class),
    MinorCorrection(org.topazproject.ambra.models.MinorCorrection.RDF_TYPE, MinorCorrection.class),
    Retraction(org.topazproject.ambra.models.Retraction.RDF_TYPE, Retraction.class),
    Trackback(org.topazproject.ambra.models.Trackback.RDF_TYPE, Trackback.class),
    Rating(org.topazproject.ambra.models.Rating.RDF_TYPE, Rating.class),
    RatingSummary(org.topazproject.ambra.models.RatingSummary.RDF_TYPE, RatingSummary.class),
    Reply(org.topazproject.ambra.models.Reply.RDF_TYPE, Reply.class),
    Issue(null, null),
    // Invalid must remain last.
    Invalid(null, null);

    private String rdftype;
    private Class clazz;

    private FEED_TYPES(String rdfType, Class clazz) {
      this.rdftype = rdfType;
      this.clazz = clazz;
    }

    public String rdfType() {
      return rdftype;
    }

    public Class isClass() {
      return clazz;
    }
  }


  /**
   * Creates and returns a new <code>Key</code> for clients of FeedService.
   *
   * @return Key a new cache key to be used as a data model for the FeedAction.
   */
  public ArticleFeedCacheKey newCacheKey();

  /**
   * Queries for a list of articles from solr using the parameters set in cacheKey
   *
   * @param cacheKey
   *
   * @return solr search result that contains list of articles
   */
  public Document getArticles(final ArticleFeedCacheKey cacheKey);

  /**
   * @param cacheKey is both the feedAction data model and cache key.
   * @param journal Current journal
   * @parem authId the current user authId
   * @return List&lt;String&gt; if article Ids.
   * @throws ApplicationException ApplicationException
   * @throws URISyntaxException   URISyntaxException
   */
  public List<String> getIssueArticleIds(final ArticleFeedCacheKey cacheKey, String journal, String authId) throws
      URISyntaxException, ApplicationException;

  /**
   * Given a list of articleIds return a list of articles corresponding to the Ids.
   *
   * @param articleIds List&lt;String&gt; of article Ids to fetch
   * @parem authId the current user authId
   *
   * @return <code>List&lt;Article&gt;</code> of articles
   * @throws ParseException ArchiveProcessException
   */
  public List<Article> getArticles(List<String> articleIds, final String authId) throws ParseException, NoSuchArticleIdException;

  /**
   * Returns a list of annotation Ids based on parameters contained in the cache key. If a start date is not specified
   * then a default date is used but not stored in the key.
   *
   * @param cacheKey cache key.
   * @return <code>List&lt;String&gt;</code> a list of annotation Ids
   * @throws ApplicationException Converts all exceptions to ApplicationException
   */
  public List<String> getAnnotationIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException;

  /**
   * Returns a list of reply Ids based on parameters contained in the cache key. If a start date is not specified then a
   * default date is used but not stored in the key.
   *
   * @param cacheKey cache key
   * @return <code>List&lt;String&gt;</code> a list of reply Ids
   * @throws ApplicationException Converts all exceptions to ApplicationException
   */
  public List<String> getReplyIds(final AnnotationFeedCacheKey cacheKey)
      throws ApplicationException;

}
