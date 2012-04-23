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
import org.ambraproject.models.AnnotationType;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.TrackbackView;
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
    Comment(AnnotationType.COMMENT.toString()),
    FormalCorrection(AnnotationType.FORMAL_CORRECTION.toString()),
    MinorCorrection(AnnotationType.MINOR_CORRECTION.toString()),
    Retraction(AnnotationType.RETRACTION.toString()),
    Rating(AnnotationType.RATING.toString()),
    Reply(AnnotationType.REPLY.toString()),
    Note(AnnotationType.NOTE.toString()),
    Annotation("Annotation"),
    Article("Article"),
    Issue("Issue"),
    Trackback("Trackback"),
    // Invalid must remain last.
    Invalid(null);

    private String type;

    private FEED_TYPES(String type) {
      this.type = type;
    }

    public String type() {
      return type;
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
   * TODO: We should really stop using this pattern of getting lists of IDs and then getting the actual objects
   */
  @Deprecated
  public List<String> getIssueArticleIds(final ArticleFeedCacheKey cacheKey, String journal, String authId) throws
      URISyntaxException, ApplicationException;

  /**
   * Returns a list of annotationViews based on parameters contained in the cache key. If a start date is not specified
   * then a default date is used but not stored in the key.
   *
   * @param cacheKey cache key.
   * @return <code>List&lt;String&gt;</code> a list of annotation Ids
   * @throws ApplicationException Converts all exceptions to ApplicationException
   */
  public List<AnnotationView> getAnnotations(final AnnotationSearchParameters cacheKey)
      throws ParseException, URISyntaxException;

  /**
   * Returns a list of trackbackViews based on parameters contained in the cache key. If a start date is not specified
   * then a default date is used but not stored in the key.
   *
   * @param cacheKey cache key.
   * @return <code>List&lt;String&gt;</code> a list of annotation Ids
   * @throws ApplicationException Converts all exceptions to ApplicationException
   */
  public List<TrackbackView> getTrackbacks(final AnnotationSearchParameters cacheKey)
      throws ParseException, URISyntaxException;

}
