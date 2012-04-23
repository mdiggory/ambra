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
import org.ambraproject.models.Rating;
import org.ambraproject.models.RatingSummary;
import org.ambraproject.views.RatingView;
import org.ambraproject.views.RatingSummaryView;

import java.util.List;

/**
 * This service allows client code to operate on ratings objects.
 *
 * @author Joe Osowski.
 */
public interface RatingsService {
  /**
   * Delete the Rating identified by ratingId and update the RatingSummary.
   *
   * @param ratingID the identifier of the Rating object to be deleted
   * @param authId the authID of the current user
   *
   * @throws ApplicationException on an error
   */
  public void deleteRating(final Long ratingID, final String authId) throws ApplicationException;

  /**
   * Save a rating
   * @param rating the dating to save
   */
  public void saveRating(Rating rating);

  /**
   * Get a Rating by Id (URI).
   *
   * @param ratingURI Rating URI
   * @return Rating
   */
  public Rating getRating(final String ratingURI);

  /**
   * Get a Rating by Id.
   *
   * @param ratingId Rating Id
   * @return Rating
   */
  public Rating getRating(final Long ratingId);

  /**
   * Get rating summary list for the given article
   * @param articleID the ID of the article
   * @return
   */
  public RatingSummary getRatingSummary(final long articleID);

  /**
   * Get the rating for the given article by the given user
   * @param articleID the ID of the article
   * @param user the current user
   * @return
   */
  public Rating getRating(final long articleID, final UserProfile user);

  /**
   * Get list of ratings for the given article
   * @param articleID The article to get the ratings summary for
   * @return
   */
  public List<RatingView> getRatingViewList(final Long articleID);

  /**
   * Get the average ratings
   *
   * @param articleID the ID of the article
   * @return
   */
  public RatingSummaryView getAverageRatings(final long articleID);

  /**
   * Has the passed in user rated the article?
   *
   * @param articleID the ID of the article
   * @param user the user to check for a rating with
   * @return did the user rate the article?
   */
  public boolean hasRated(final long articleID, final UserProfile user);
}
