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
package org.ambraproject.rating.service;

import org.ambraproject.ApplicationException;
import org.ambraproject.models.UserProfile;
import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingContent;
import org.topazproject.ambra.models.RatingSummary;

import java.io.Serializable;
import java.util.List;

/**
 * This service allows client code to operate on ratings objects.
 *
 * @author jonnie.
 */
public interface RatingsService {
  /**
   * Delete the Rating identified by ratingId and update the RatingSummary.
   *
   * @param ratingId the identifier of the Rating object to be deleted
   * @throws ApplicationException on an error
   */
  public void deleteRating(final String ratingId, final String authId) throws ApplicationException;

  /**
   * Save a rating
   * @param rating
   */
  public void saveRating(Rating rating);

  /**
   * Save a ratingSummary
   * @param ratingSummary
   */
  public void saveRatingSummary(RatingSummary ratingSummary);

  /**
   * Get a Rating by Id.
   *
   * @param ratingId Rating Id
   * @return Rating
   */
  public Rating getRating(final String ratingId) ;

  /**
   * Get rating summary list for the given article
   * @param articleURI
   * @return
   */
  public List<RatingSummary> getRatingSummaryList(final String articleURI);

  /**
   * Get list of ratings for the given article by the given user
   * @param articleURI
   * @param userID
   * @return
   */
  public List<Rating> getRatingsList(final String articleURI, final String userID);

  /**
   * Get list of ratings for the given article
   * @param articleURI
   * @return
   */
  public List<Rating> getRatingsList(final String articleURI);

  /**
   * List the set of Ratings in a specific administrative state.
   *
   * @param mediator if present only those annotations that match this mediator are returned
   * @param state    the state to filter the list of annotations by or 0 to return annotations
   *                 in any administrative state
   * @return an array of rating metadata; if no matching annotations are found, an empty array
   *         is returned
   */
  public Rating[] listRatings(final String mediator,final int state);

  public AverageRatings getAverageRatings(final String articleURI);

  public boolean hasRated(String articleURI, UserProfile user);

  public static class Average implements Serializable {
    private static final long serialVersionUID = -2890067268188424471L;

    private final double total;
    private final int    count;
    private final double average;
    private final double rounded;

    Average(double total, int count) {
      this.total = total;
      this.count = count;
      average = (count == 0) ? 0 : total/count;
      rounded = RatingContent.roundTo(average, 0.5);
    }

    @Override
    public String toString() {
      return "total = " + total + ", count = " + count + ", average = " + average +
             ", rounded = " + rounded;
    }

    public double getTotal() {
      return total;
    }

    public int getCount() {
      return count;
    }

    public double getAverage() {
      return average;
    }

    public double getRounded() {
      return rounded;
    }    
  }

  public static class AverageRatings implements Serializable {
    private static final long serialVersionUID = -1666766336307635633L;

    private final Average style;
    private final Average insight;
    private final Average reliability;
    private final Average single;
    private final int     numUsersThatRated;
    private final double  overall;
    private final double  roundedOverall;

    AverageRatings() {
      style = new Average(0, 0);
      insight = new Average(0, 0);
      reliability = new Average(0, 0);
      single = new Average(0, 0);
      numUsersThatRated = 0;
      overall = 0;
      roundedOverall = 0;
    }

    AverageRatings(RatingSummary ratingSummary) {
      insight = new Average(ratingSummary.getBody().getInsightTotal(),
         ratingSummary.getBody().getInsightNumRatings());
      reliability = new Average(ratingSummary.getBody().getReliabilityTotal(),
         ratingSummary.getBody().getReliabilityNumRatings());
      style = new Average(ratingSummary.getBody().getStyleTotal(),
         ratingSummary.getBody().getStyleNumRatings());
      single = new Average(ratingSummary.getBody().getSingleRatingTotal(),
         ratingSummary.getBody().getSingleRatingNumRatings());

      numUsersThatRated = ratingSummary.getBody().getNumUsersThatRated();
      overall = RatingContent.calculateOverall(insight.average, reliability.average, style.average);
      roundedOverall = RatingContent.roundTo(overall, 0.5);
    }

    @Override
    public String toString() {
      return "style = [" + style + "], " +
             "insight = [" + insight + "], " +
             "reliability = [" + reliability + "], " +
             "single = [" + single + "], " +
             "numUsersThatRated = " + numUsersThatRated +
             ", overall = " + overall +
             ", roundedOverall = " + roundedOverall;
    }

    public Average getStyle() {
      return style;
    }

    public Average getInsight() {
      return insight;
    }

    public Average getReliability() {
      return reliability;
    }

    public Average getSingle() {
      return single;
    }

    public int getNumUsersThatRated() {
      return numUsersThatRated;
    }

    public double getOverall() {
      return overall;
    }

    public double getRoundedOverall()
    {
      return roundedOverall;
    }      
  }
}
