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
package org.topazproject.ambra.models;

import java.io.Serializable;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.UriPrefix;

/**
 * RatingsSummaryContent is the body for Ratings Summary.
 *
 * @author stevec
 * @author Jeff Suttor
 */
@UriPrefix("topaz:RatingSummaryContent/")
@Entity(graph = "ri", types = {"topaz:RatingSummaryContent"})
public class RatingSummaryContent implements Serializable {
    private static final long serialVersionUID = -1216965209929476658L;

  private String id;

  private int    insightNumRatings;
  private double insightTotal;
  private int    reliabilityNumRatings;
  private double reliabilityTotal;
  private int    styleNumRatings;
  private double styleTotal;
  private int    singleRatingNumRatings;
  private double singleRatingTotal;
  private int    numUsersThatRated;

  /**
   * Creates a new RatingSummaryContent object.
   */
  public RatingSummaryContent() {
    this(0, 0, 0, 0, 0, 0, 0);
  }

  /**
   * Creates a new RatingSummaryContent object.
   *
   * @param insightNumRatings     number of insight ratings
   * @param insightTotal          total of insight ratings
   * @param reliabilityNumRatings number of reliability ratings
   * @param reliabilityTotal      total of reliability ratings
   * @param styleNumRatings       number of style ratings
   * @param styleTotal            total of style ratings
   * @param numUsersThatRated     number of users that make a rating
   */
  public RatingSummaryContent(int insightNumRatings, double insightTotal, int reliabilityNumRatings,
                              double reliabilityTotal, int styleNumRatings, double styleTotal,
                              int numUsersThatRated) {
    this.insightNumRatings     = insightNumRatings;
    this.insightTotal          = insightTotal;
    this.reliabilityNumRatings = reliabilityNumRatings;
    this.reliabilityTotal      = reliabilityTotal;
    this.styleNumRatings       = styleNumRatings;
    this.styleTotal            = styleTotal;
    this.numUsersThatRated     = numUsersThatRated;
  }

  /**
   * Constructor - For aggregating {@link RatingContent}s that 
   * are representative of single valued ratings.
   * @param singleRatingNumRatings number of single value ratings
   * @param singleRatingTotal the total ratings
   * @param numUsersThatRated the number of users who rated
   */
  public RatingSummaryContent(int singleRatingNumRatings, double singleRatingTotal,
      int numUsersThatRated) {
    super();
    this.singleRatingNumRatings = singleRatingNumRatings;
    this.singleRatingTotal = singleRatingTotal;
    this.numUsersThatRated = numUsersThatRated;
  }

  /**
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id to set.
   */
  @Id
  @GeneratedValue(uriPrefix = "id:ratingSummaryContent/")
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return Returns the number of insight Ratings.
   */
  public int getInsightNumRatings() {
    return insightNumRatings;
  }

  /**
   * @param insightNumRatings The number of insight ratings.
   */
  @Predicate
  public void setInsightNumRatings(int insightNumRatings) {
    this.insightNumRatings = insightNumRatings;
  }

  /**
   * @return Returns the total of insight Ratings.
   */
  public double getInsightTotal() {
    return insightTotal;
  }

  /**
   * @param insightTotal The total of insight ratings.
   */
  @Predicate
  public void setInsightTotal(double insightTotal) {
    this.insightTotal = insightTotal;
  }

  /**
   * @return Returns the number of reliability Ratings.
   */
  public int getReliabilityNumRatings() {
    return reliabilityNumRatings;
  }
  /**
   * @param reliabilityNumRatings The number of reliability ratings.
   */
  @Predicate
  public void setReliabilityNumRatings(int reliabilityNumRatings) {
    this.reliabilityNumRatings = reliabilityNumRatings;
  }

  /**
   * @return Returns the total of reliability Ratings.
   */
  public double getReliabilityTotal() {
    return reliabilityTotal;
  }
  /**
   * @param reliabilityTotal The total of reliability ratings.
   */
  @Predicate
  public void setReliabilityTotal(double reliabilityTotal) {
    this.reliabilityTotal = reliabilityTotal;
  }

  /**
   * @return Returns the number of style Ratings.
   */
  public int getStyleNumRatings() {
    return styleNumRatings;
  }
  /**
   * @param styleNumRatings The number of style ratings.
   */
  @Predicate
  public void setStyleNumRatings(int styleNumRatings) {
    this.styleNumRatings = styleNumRatings;
  }

  /**
   * @return Returns the total of style Ratings.
   */
  public double getStyleTotal() {
    return styleTotal;
  }
  /**
   * @param styleTotal The total of style ratings.
   */
  @Predicate
  public void setStyleTotal(double styleTotal) {
    this.styleTotal = styleTotal;
  }

  /**
   * @return the singleRatingNumRatings
   */
  public int getSingleRatingNumRatings() {
    return singleRatingNumRatings;
  }

  /**
   * @param singleRatingNumRatings the singleRatingNumRatings to set
   */
  @Predicate
  public void setSingleRatingNumRatings(int singleRatingNumRatings) {
    this.singleRatingNumRatings = singleRatingNumRatings;
  }

  /**
   * @return the singleRatingTotal
   */
  public double getSingleRatingTotal() {
    return singleRatingTotal;
  }

  /**
   * @param singleRatingTotal the singleRatingTotal to set
   */
  @Predicate
  public void setSingleRatingTotal(double singleRatingTotal) {
    this.singleRatingTotal = singleRatingTotal;
  }

  /**
   * @return Returns the overall Rating.
   */
  public double getOverall() {

    return RatingContent.calculateOverall(
      getInsightTotal() / getInsightNumRatings(),
      getReliabilityTotal() / getReliabilityNumRatings(),
      getStyleTotal() / getStyleNumRatings());
  }

  /**
   * @return The calculated single rating. 
   */
  public double getSingleRating() {
    return getSingleRatingTotal() / getSingleRatingNumRatings();
  }

  /**
   * @return Number of users that rated.
   */
  public int getNumUsersThatRated() {
    return numUsersThatRated;
  }

  /**
   * @param numUsersThatRated Number of users that rated.
   */
  @Predicate
  public void setNumUsersThatRated(int numUsersThatRated) {
    this.numUsersThatRated = numUsersThatRated;
  }

  /**
   * Add a Rating value to the RatingSummary
   *
   * @param ratingType Type of Rating to add.
   * @param value Value of Rating to add.
   */
  public void addRating(String ratingType, int value) {
    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      insightNumRatings += 1;
      insightTotal      += value;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      reliabilityNumRatings += 1;
      reliabilityTotal      += value;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      styleNumRatings += 1;
      styleTotal      += value;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      singleRatingNumRatings += 1;
      singleRatingTotal      += value;
    } else {
      // should never happen
      String errorMessage = "Invalid type, " + ratingType + ", when adding a Rating to a RatingSummary.";
      throw new RuntimeException(errorMessage);
    }
  }

  /**
   * Remove a Rating value from the RatingSummary
   *
   * @param ratingType Type of Rating to remove.
   * @param value Value of Rating to remove.
   */
  public void removeRating(String ratingType, int value) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      insightNumRatings -= 1;
      insightTotal      -= value;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      reliabilityNumRatings -= 1;
      reliabilityTotal      -= value;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      styleNumRatings -= 1;
      styleTotal      -= value;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      singleRatingNumRatings -= 1;
      singleRatingTotal      -= value;
    } else {
      // should never happen
      String errorMessage = "Invalid type, " + ratingType +
                            ", when removing a Rating from a RatingSummary.";
      throw new RuntimeException(errorMessage);
    }
  }

  /**
   * Retrieve the average for a Rating type.
   *
   * @param ratingType Type of Rating.
   * @return Returns the average rating value.
   */
  public double retrieveAverage(String ratingType) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      return insightTotal / insightNumRatings;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      return reliabilityTotal / reliabilityNumRatings;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      return styleTotal / styleNumRatings;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      return singleRatingTotal / singleRatingNumRatings;
    } else {
      // should never happen
      throw new RuntimeException("Invalid type, " + ratingType +
                            ", when retrieving the average Rating from a RatingSummary.");
    }
  }

  /**
   * Set the number of ratings.
   *
   * @param ratingType Type of Rating.
   * @param numRatings the number of ratings
   */
  public void assignNumRatings(String ratingType, int numRatings) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      insightNumRatings = numRatings;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      reliabilityNumRatings = numRatings;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      styleNumRatings = numRatings;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      singleRatingNumRatings = numRatings;
    } else {
      // should never happen
      throw new RuntimeException("Invalid type, " + ratingType +
                            ", when assigning the number of Ratings for a RatingSummary.");
    }
  }

  /**
   * Get the number of Ratings for a Rating type.
   *
   * @param ratingType Type of Rating.
   * @return Number of Ratings.
   */
  public int retrieveNumRatings(String ratingType) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      return insightNumRatings;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      return reliabilityNumRatings;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      return styleNumRatings;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      return singleRatingNumRatings;
    } else {
      // should never happen
      throw new RuntimeException("Invalid type, " + ratingType +
                            ", when retriving the number of Ratings for a RatingSummary.");
    }
  }

  /**
   * Set the total value of the Rating.
   *
   * @param ratingType Type of Rating.
   * @param total Total value of Rating.
   */
  public void assignTotal(String ratingType, double total) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      insightTotal = total;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      reliabilityTotal = total;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      styleTotal = total;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      singleRatingTotal = total;
    } else {
      // should never happen
      throw new RuntimeException("Invalid type, " + ratingType +
                            ", when assigning a total Ratings value for a RatingSummary.");
    }
  }

  /**
   * Get the total value of the Rating.
   *
   * @param ratingType Type of Rating.
   * @return total rating
   */
  public double retrieveTotal(String ratingType) {

    if (ratingType.equals(Rating.INSIGHT_TYPE)) {
      return insightTotal;
    } else if (ratingType.equals(Rating.RELIABILITY_TYPE)) {
      return reliabilityTotal;
    } else if (ratingType.equals(Rating.STYLE_TYPE)) {
      return styleTotal;
    } else if (ratingType.equals(Rating.SINGLE_RATING_TYPE)) {
      return singleRatingTotal;
    } else {
      // should never happen
      throw new RuntimeException("Invalid type, " + ratingType +
                            ", when retrieving a total Ratings value for a RatingSummary.");
    }
  }
}
