/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

/**
 * @author Alex Kudlick 3/9/12
 */
public class RatingSummary extends AmbraEntity {
  
  private Long articleID;

  private Integer insightNumRatings = 0;
  private Integer insightTotal = 0;

  private Integer reliabilityNumRatings = 0;
  private Integer reliabilityTotal = 0;

  private Integer styleNumRatings = 0;
  private Integer styleTotal = 0;

  private Integer singleRatingNumRatings = 0;
  private Integer singleRatingTotal = 0;

  private Integer usersThatRated = 0;

  public RatingSummary() {
    super();
  }

  /**
   * Remove a rating for this summary
   *
   * @param rating the rating object to remove
   */
  public void removeRating(Rating rating) {
    usersThatRated = usersThatRated - 1;

    if(rating.getInsight() > 0) {
      insightNumRatings = insightNumRatings - 1;
      insightTotal = insightTotal - rating.getInsight();
    }

    if(rating.getReliability() > 0) {
      reliabilityNumRatings = reliabilityNumRatings - 1;
      reliabilityTotal = reliabilityTotal - rating.getReliability();
    }

    if(rating.getStyle() > 0) {
      styleNumRatings = styleNumRatings - 1;
      styleTotal = styleTotal - rating.getStyle();
    }

    if(rating.getSingleRating() > 0) {
      singleRatingNumRatings = singleRatingNumRatings - 1;
      singleRatingTotal = singleRatingTotal - rating.getSingleRating();
    }
  }

  /**
   * Add a rating to this summary
   *
   * @param rating the rating to add to this summary
   */
  public void addRating(Rating rating) {
    usersThatRated = usersThatRated + 1;

    if(rating.getInsight() > 0) {
      insightNumRatings = insightNumRatings + 1;
      insightTotal = insightTotal + rating.getInsight();
    }

    if(rating.getReliability() > 0) {
      reliabilityNumRatings = reliabilityNumRatings + 1;
      reliabilityTotal = reliabilityTotal + rating.getReliability();
    }

    if(rating.getStyle() > 0) {
      styleNumRatings = styleNumRatings + 1;
      styleTotal = styleTotal + rating.getStyle();
    }

    if(rating.getSingleRating() > 0) {
      singleRatingNumRatings = singleRatingNumRatings + 1;
      singleRatingTotal = singleRatingTotal + rating.getSingleRating();
    }
  }

  public RatingSummary(Long articleID) {
    super();
    this.articleID = articleID;
  }

  public Long getArticleID() {
    return articleID;
  }

  public void setArticleID(Long articleID) {
    this.articleID = articleID;
  }

  public Integer getInsightNumRatings() {
    return insightNumRatings;
  }

  public void setInsightNumRatings(Integer insightNumRatings) {
    this.insightNumRatings = insightNumRatings;
  }

  public Integer getInsightTotal() {
    return insightTotal;
  }

  public void setInsightTotal(Integer insightTotal) {
    this.insightTotal = insightTotal;
  }

  public Integer getReliabilityNumRatings() {
    return reliabilityNumRatings;
  }

  public void setReliabilityNumRatings(Integer reliabilityNumRatings) {
    this.reliabilityNumRatings = reliabilityNumRatings;
  }

  public Integer getReliabilityTotal() {
    return reliabilityTotal;
  }

  public void setReliabilityTotal(Integer reliabilityTotal) {
    this.reliabilityTotal = reliabilityTotal;
  }

  public Integer getStyleNumRatings() {
    return styleNumRatings;
  }

  public void setStyleNumRatings(Integer styleNumRatings) {
    this.styleNumRatings = styleNumRatings;
  }

  public Integer getStyleTotal() {
    return styleTotal;
  }

  public void setStyleTotal(Integer styleTotal) {
    this.styleTotal = styleTotal;
  }

  public Integer getSingleRatingNumRatings() {
    return singleRatingNumRatings;
  }

  public void setSingleRatingNumRatings(Integer singleRatingNumRatings) {
    this.singleRatingNumRatings = singleRatingNumRatings;
  }

  public Integer getSingleRatingTotal() {
    return singleRatingTotal;
  }

  public void setSingleRatingTotal(Integer singleRatingTotal) {
    this.singleRatingTotal = singleRatingTotal;
  }

  public Integer getUsersThatRated() {
    return usersThatRated;
  }

  public void setUsersThatRated(Integer usersThatRated) {
    this.usersThatRated = usersThatRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RatingSummary)) return false;

    RatingSummary that = (RatingSummary) o;

    if (articleID != null ? !articleID.equals(that.articleID) : that.articleID != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return articleID != null ? articleID.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "RatingSummary{" +
        "articleID=" + articleID +
        ", insightNumRatings=" + insightNumRatings +
        ", insightTotal=" + insightTotal +
        ", reliabilityNumRatings=" + reliabilityNumRatings +
        ", reliabilityTotal=" + reliabilityTotal +
        ", styleNumRatings=" + styleNumRatings +
        ", styleTotal=" + styleTotal +
        ", singleRatingNumRatings=" + singleRatingNumRatings +
        ", singleRatingTotal=" + singleRatingTotal +
        ", usersThatRated=" + usersThatRated +
        '}';
  }
}
