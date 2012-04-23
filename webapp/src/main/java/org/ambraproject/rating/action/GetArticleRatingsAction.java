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

import org.ambraproject.models.Article;
import org.ambraproject.models.RatingSummary;
import org.ambraproject.views.RatingView;
import org.ambraproject.views.RatingAverage;
import org.ambraproject.views.RatingSummaryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Rating action class to retrieve all ratings for an Article.
 *
 * @author Joe Osowski
 *
 */
@SuppressWarnings("serial")
public class GetArticleRatingsAction extends AbstractRatingAction {
  protected static final Logger log = LoggerFactory.getLogger(GetArticleRatingsAction.class);

  private String articleURI;
  private String articleTitle;
  private String articleDescription;
  private boolean isResearchArticle;
  private RatingSummaryView averageRatings;
  private List<RatingView> articleRatingViews;
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
    averageRatings = ratingsService.getAverageRatings(article.getID());
    isResearchArticle = articleService.isResearchArticle(article, getAuthId());

    // assume if valid RatingsPEP.GET_RATINGS, OK to GET_STATS
    // RatingSummary for this Article
    RatingSummary ratingSummary = ratingsService.getRatingSummary(article.getID());

    if(ratingSummary != null) {
      RatingSummaryView ar = new RatingSummaryView(ratingSummary);
      articleOverall = ar.getOverall();
      articleSingleRating = ar.getSingle().getAverage();
    } else {
      log.warn("Unexpected: null RatingSummary for " + articleURI);
      articleOverall = 0;
      articleSingleRating = 0;
    }

    // list of Ratings that annotate this article
    articleRatingViews = ratingsService.getRatingViewList(article.getID());

    log.debug("created ArticleRatingSummaries, {}, for: {}", articleRatingViews.size(), articleURI);

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

    return RatingAverage.roundTo(articleOverall, 0.5);
  }

  /**
   * Gets the single rounded rating of the article being rated.
   *
   * @return Single rounded rating.
   */
  public double getArticleSingleRatingRounded() {

    return RatingAverage.roundTo(articleSingleRating, 0.5);
  }

  /**
   * Gets the title of the article being rated.
   *
   * @return Returns the articleTitle.
   */
  public String getArticleTitle() {
    return articleTitle;
  }

  /**
   * Gets the description of the Article being rated.
   *
   * @return Returns the articleDescription.
   */
  public String getArticleDescription() {
    return articleDescription;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  /**
   * Gets all ratings for the Article.
   *
   * @return Returns Ratings for the Article.
   */
  public Collection<RatingView> getArticleRatings() {
    return articleRatingViews;
  }

  /*
  * Gets averageRatings info
  *
  * @return returns averageRatings info
  */
  public RatingSummaryView getAverageRatings() {
    return averageRatings;
  }

 /**
  * Has this article been rated?
  *
  * @return true if the article has been rated
  */
  public boolean getHasRated() {
    return (articleRatingViews.size() > 0);
  }
}
