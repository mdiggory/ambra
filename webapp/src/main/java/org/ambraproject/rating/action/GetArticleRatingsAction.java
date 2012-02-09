/*$HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.ambraproject.rating.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.models.Article;
import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingContent;
import org.topazproject.ambra.models.RatingSummary;
import org.topazproject.ambra.models.RatingSummaryContent;
import org.ambraproject.rating.service.RatingsService;
import org.ambraproject.rating.service.RatingsService.AverageRatings;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.service.UserService;

/**
 * Rating action class to retrieve all ratings for an Article.
 *
 * @author Jeff Suttor
 */
@SuppressWarnings("serial")
public class GetArticleRatingsAction extends AbstractRatingAction {
  protected static final Logger log = LoggerFactory.getLogger(GetArticleRatingsAction.class);

  private UserService userService;

  private String articleURI;
  private String  articleTitle;
  private String articleDescription;
  private boolean isResearchArticle;
  private AverageRatings averageRatings;
  private final List<ArticleRatingSummary> articleRatingSummaries =
                                             new ArrayList<ArticleRatingSummary>();
  private double articleOverall = 0;
  private double articleSingleRating = 0;

  /**
   * Execute the ratings action.
   *
   * @return WebWork action status
   */
  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public String execute() throws Exception {
    final Article article = articleService.getArticle(articleURI, getAuthId());
    assert article != null : "article of URI: " + articleURI + " not found.";

    articleTitle = article.getTitle();
    articleDescription = article.getDescription();
    averageRatings = ratingsService.getAverageRatings(articleURI);

    isResearchArticle = articleService.isResearchArticle(articleURI, getAuthId());

    // assume if valid RatingsPEP.GET_RATINGS, OK to GET_STATS
    // RatingSummary for this Article
    List<RatingSummary> summaryList = ratingsService.getRatingSummaryList(articleURI);

    if(summaryList.size() == 1) {
      RatingSummaryContent rsc = summaryList.get(0).getBody();
      articleOverall = rsc.getOverall();
      articleSingleRating = rsc.getSingleRating();
    } else {
      log.warn("Unexpected: " + summaryList.size() + " RatingSummary for " + articleURI);
      articleOverall = 0;
      articleSingleRating = 0;
    }

    // list of Ratings that annotate this article
    List<Rating> articleRatings = ratingsService.getRatingsList(articleURI);

    if(log.isDebugEnabled()) {
      log.debug("retrieved all ratings, " + articleRatings.size() + ", for: " + articleURI);
    }

    // create ArticleRatingSummary(s)
    for (Rating rating : articleRatings) {
      ArticleRatingSummary summary = new ArticleRatingSummary(getArticleURI(), getArticleTitle());
      summary.setRating(rating);
      summary.setCreated(rating.getCreated());
      summary.setArticleURI(getArticleURI());
      summary.setArticleTitle(getArticleTitle());
      summary.setCreatorURI(rating.getCreator());
      // get public 'name' for user
      AmbraUser au = userService.getUserById(rating.getCreator());
      if (au != null) {
        summary.setCreatorName(au.getDisplayName());
      } else {
        summary.setCreatorName("Unknown");
        log.error("Unable to look up UserAccount for " + rating.getCreator() +
                  " for Rating " + rating.getId());
      }
      articleRatingSummaries.add(summary);
    }

    if(log.isDebugEnabled()) {
      log.debug("created ArticleRatingSummaries, " + articleRatingSummaries.size() +
                ", for: " + articleURI);
    }

    return SUCCESS;
  }

  /**
   * Gets the URI of the article being rated.
   *
   * @return Returns the articleURI.
   */
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * Sets the URI of the article being rated.
   *
   * @param articleURI The articleUri to set.
   */
  public void setArticleURI(String articleURI) {
    this.articleURI = articleURI;
  }

  /**
   * Gets the Overall rounded rating of the article being rated.
   *
   * @return Overall rounded rating.
   */
  public double getArticleOverallRounded() {

    return RatingContent.roundTo(articleOverall, 0.5);
  }

  /**
   * Gets the single rounded rating of the article being rated.
   *
   * @return Single rounded rating.
   */
  public double getArticleSingleRatingRounded() {

    return RatingContent.roundTo(articleSingleRating, 0.5);
  }

  /**
   * Gets the title of the article being rated.
   *
   * @return Returns the articleTitle.
   */
  public String getArticleTitle() {

    if(articleTitle != null) {
      return articleTitle;
    }

    articleTitle = "Article title place holder for testing, resolve " + articleURI;
    return articleTitle;
  }

  /**
   * Sets the title of the article being rated.
   *
   * @param articleTitle The article's title.
   */
  public void setArticleTitle(String articleTitle) {
    this.articleTitle = articleTitle;
  }

  /**
   * Gets the description of the Article being rated.
   *
   * @return Returns the articleDescription.
   */
  public String getArticleDescription() {
    if(articleDescription != null) {
      return articleDescription;
    }

    articleDescription = "Article Description place holder for testing, resolve " + articleURI;
    return articleDescription;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  /**
   * @param isResearchArticle the isResearchArticle to set
   */
  public void setIsResearchArticle(boolean isResearchArticle) {
    this.isResearchArticle = isResearchArticle;
  }

  @Required
  public void setUserService(UserService us) {
    this.userService = us;
  }


  /**
   * Gets all ratings for the Article.
   *
   * @return Returns Ratings for the Article.
   */
  public Collection<ArticleRatingSummary> getArticleRatingSummaries() {
    return articleRatingSummaries;
  }

  /*
  * Gets averageRatings info
  *
  * @return returns averageRatings info
  */
  public RatingsService.AverageRatings getAverageRatings() {
    return averageRatings;
  }

 /**
  * Has this article been rated?
  *
  * @return true if the article has been rated
  */
  public boolean getHasRated() {
    return (articleRatingSummaries.size() > 0);
  }
}
