/* $HeadURL::                                                                            $
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

import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.rating.service.RatingsService;
import org.ambraproject.rating.service.RatingsService.AverageRatings;

/**
 * General Rating action class to store and retrieve summary ratings on an article.
 *
 * @author stevec
 */
@SuppressWarnings("serial")
public class GetAverageRatingsAction extends AbstractRatingAction {
  private AverageRatings averageRatings;

  private String articleURI;
  private boolean isResearchArticle;
  private boolean hasRated = false;

  /**
   * Execute the ratings summary action.
   *
   * @return WebWork action status
   */
  @Override
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    averageRatings = ratingsService.getAverageRatings(articleURI);
    hasRated = ratingsService.hasRated(articleURI, getCurrentUser());
    isResearchArticle = articleService.isResearchArticle(articleURI, getAuthId());
    return SUCCESS;
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
   * Gets the URI of the article being rated.
   *
   * @return Returns the articleURI.
   */
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  /*
  * Gets averageRatings info
  *
  * @return returns averageRatings info
  * */
  public RatingsService.AverageRatings getAverageRatings() {
    return averageRatings;
  }

  /**
   * Tests if this article has been rated.
   *
   * @return Returns the hasRated.
   */
  public boolean getHasRated() {
    return hasRated;
  }
}
