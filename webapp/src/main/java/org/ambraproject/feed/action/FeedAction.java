/* $HeadURL::                                                                            $
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

package org.ambraproject.feed.action;

import java.util.List;

import org.ambraproject.feed.service.AnnotationSearchParameters;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.TrackbackView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.feed.service.FeedService;
import org.ambraproject.feed.service.ArticleFeedCacheKey;
import org.ambraproject.feed.service.FeedService.FEED_TYPES;

import com.opensymphony.xwork2.ModelDriven;
import org.w3c.dom.Document;

/**
 * The <code>class ArticleFeed</code> provides an API for criteria based retrieval of articles and
 * article information. The <code>class ArticleFeed</code> implements the Struts ModelDrive
 * interface. The data model used for <code>ArticleFeed</code> is <code>class Key</code>. The the
 * field <code>ArticleFeed.cacheKey</code> is accessible to Struts through the
 * <code>ArticleFeed.getModel</code> and <code>ArticleFeed.getCacheKey</code> bean getter. The
 * ModelDriven Interceptor parses the input parameters, converts them to the appropriate Java types
 * then assigns them to fields in the data model.
 *
 * <p>
 * The <code>ArticleFeed.cacheKey</code> serves the following purposes:
 * <ul>
 * <li> Receives and validates the parameters passed in during a Post/Get.
 * <li> Uses these parameters to compute a hashcode for cache lookups.
 * <li> Used to pass these parameters to AmbraFeedResult via the ValueStack.
 * <li> Registers a cache Invalidator as a cache listner.
 * </ul>
 * <p>
 *
 * ArticleFeed implements the <code>ArticleFeed.execute</code> and <code>ArticleFeed.validate
 * </code> Struts entry points. The <code>ArticleFeed.validate</code> method assigns default values
 * to fields not provided by user input and checks parameters that are provided by the user. By the
 * time Struts invokes the <code>ArticleFeed.execute</code> all model data variables should be in a
 * known and acceptable state for execution. <code>ArticleFeed.execute</code> first checks the feed
 * cache for identical queries or calls <code>ArticleFeed.getFeedData</code> if there is a miss. A
 * list of article ID's is the result. It is up to the result handler to fetch the articles and
 * serialize the output.
 *
 * <p>
 * <ul>
 * <li>Define a hard limit of 200 articles returned in one query.
 * <li>If startDate &gt; endDate then startDate set to endDate.
 * </ul>
 *
 * <h4>Action URI</h4>
 * http://.../article/feed
 * <h4>Parameters</h4>
 * <pre>
 * <strong>
 * Param        Format        Required     Default                 Description </strong>
 * startDate ISO yyyy-MM-dd     No         -3 months   Start Date - search for articles
 *                                                     dated &gt;= to sDate
 * endDate   ISO yyyy-MM-dd     No         today       End Date - search for articles
 *                                                     dated <= to eDate
 * category    String           No         none        Article Category
 * author      String           No         none        Article Author name ex: John+Smith
 * relLinks    Boolean          No         false       If relLinks=true; internal links will be
 *                                                     relative to xmlbase
 * extended    Boolean          No         false       If extended=true; provide additional feed
 *                                                     information
 * title       String           No         none        Sets the title of the feed
 * selfLink    String           No         none        URL of feed that is to be put in the feed
 *                                                     data.
 * IssueURI    String           Yes        none        Issue URI (Required for type=Issue only)
 * maxResults  Integer          No         30          The maximun number of result to return.
 * type        String           No         Article     Article,Annotation,FormalCorrection
 *                                                     MinorCorrection,Retraction,Comment,Issue
 * mostViewed Boolean No False Parameter to enable list of most viewed articles
 *
 * </pre>
 *
 * @see       org.ambraproject.feed.service.ArticleFeedCacheKey
 * @see       org.ambraproject.struts2.AmbraFeedResult
 *
 * @author Jeff Suttor
 * @author Eric Brown
 */
 @SuppressWarnings("UnusedDeclaration")
public class FeedAction extends BaseActionSupport implements ModelDriven {
  private static final Logger log = LoggerFactory.getLogger(FeedAction.class);

  private FeedService          feedService;     // Feed Service Spring injected.
  private ArticleFeedCacheKey  cacheKey;        // The cache key and action data model
  private List<String>         articleIds;      // List of Article IDs; result of search
  private List<AnnotationView> annotations;     // List of Annotations; result of search
  private List<TrackbackView>  trackbacks;      // List of tracks; results of search
  private Document             resultFromSolr;  // list of articles for the rss feed

  /**
   * Try and find the query in the feed cache or query the Article OTM Service if nothing
   * is found. The parameters are valid by this point.
   *
   * @throws Exception Exception
   */
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    //TODO: Rename the cacheKey param to be something more logical once we no longer use cache here
    FEED_TYPES t = cacheKey.feedType();

    String status = SUCCESS;
    
    switch (t) {
      case Annotation:
        //Trackbacks are (logically but not physically) a form of annotation, if this type of feed is selected
        //We wanted it included
        trackbacks = feedService.getTrackbacks(new AnnotationSearchParameters(cacheKey));
      case FormalCorrection:
      case MinorCorrection:
      case Retraction:
      case Comment:
      case Note:
      case Rating:
      case Reply:
        //The getAnnotations method performs filters for all of the above types.
        //(Or not if Annotation is selected) AnnotationSearchParameters will not populate the annotationTypes property
        //If the type specified is Annotation.  It's also worth noting here, while annotationTypes is a collection
        //We never allow more then one value to be specified currently though a lot of the code supports it
        annotations = feedService.getAnnotations(new AnnotationSearchParameters(cacheKey));
        break;
      case Trackback:
        trackbacks = feedService.getTrackbacks(new AnnotationSearchParameters(cacheKey));
        break;
      case Article:
        resultFromSolr = feedService.getArticles(cacheKey);
        if (resultFromSolr == null) {
          status = ERROR;
        }
        break;
      case Issue:
        //TODO: We should stop using IDs, and put the actual issues on the stack for
        // the AmbraFeedResult class to consume
        articleIds = feedService.getIssueArticleIds(cacheKey, getCurrentJournal(), getAuthId());
        break;
    }

    return status;
  }

  /**
   * Validate the input parameters or create defaults when they are not provided.  Struts calls this
   * automagically after the parameters are parsed and the proper fields are set in the data model.
   * It is assumed that all necessary fields are checked for validity and created if not specified.
   * The <code>ArticleFeed.execute</code> should be able to use them without any further checks.
   */
  @Override
  public void validate () {
    /*
     * The cacheKey must have both the current Journal and start date.  Current Journal is set here
     * and startDate will be set in the data model validator.
     */
    cacheKey.setJournal(getCurrentJournal());
    cacheKey.validate(this);
    if (log.isErrorEnabled()) {

      for (Object key : getFieldErrors().keySet()) {
        log.error("Validate error: " + getFieldErrors().get(key) + " on " + key +
            " for cache key: " + cacheKey);
      }
    }
  }

  /**
   * Set <code>feedService</code> field to the article Feed service singleton.
   *
   * @param  feedService  the object transaction model reference
   */
  @Required
  public void setFeedService(final FeedService feedService) {
    this.feedService = feedService;
  }

  /**
   * This is the results of the query which consist of a list of article or annotation ID's.
   *
   * @return the list of article/annotation ID's returned from the query.
   */
  public List<String> getIds() {
    return articleIds;
  }

  /**
   * This is the results of the query which consist of a list of annotations.
   *
   * @return the list of article/annotation ID's returned from the query.
   */
  public List<AnnotationView> getAnnotations() {
    return annotations;
  }

  /**
   * This is the results of the query which consist of a list of trackbacks.
   *
   * @return the list of article/annotation ID's returned from the query.
   */
  public List<TrackbackView> getTrackbacks() {
    return trackbacks;
  }

  /**
   * Return the cache key being used by this action.
   *
   * @return  Key to the cache which is also the data model of the action
   */
  public ArticleFeedCacheKey getCacheKey() {
    return this.cacheKey;
  }

  /**
   * Return the a cache key which is also the data model for the model driven interface.
   *
   * @return Key to the cache which is also the data model of the action
   */
  public Object getModel() {
    /*
     * getModel is invoked several times by different interceptors
     * Make sure caheKey is not created twice.
     */
    return (cacheKey == null) ? cacheKey = feedService.newCacheKey() : cacheKey;
  }

  /**
   * Returns the solr search result that contains the list of articles
   * @return solr search result
   */
  public Document getResultFromSolr() {
    return resultFromSolr;
  }
}
