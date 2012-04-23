/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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
import org.ambraproject.hibernate.URIGenerator;
import org.ambraproject.models.UserProfile;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.views.RatingView;
import org.ambraproject.views.RatingSummaryView;
import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.models.Rating;
import org.ambraproject.models.RatingSummary;
import java.util.ArrayList;
import java.util.List;

/**
 * This service allows client code to operate on ratings objects.
 *
 * @author Joe Osowski.
 */
public class RatingsServiceImpl extends HibernateServiceImpl implements RatingsService {
  private static final Logger log = LoggerFactory.getLogger(RatingsServiceImpl.class);

  private String applicationId;
  private PermissionsService permissionsService;

  public RatingsServiceImpl() {
  }

  /**
   * Delete the Rating identified by ratingId and update the RatingSummary.
   *
   * @param ratingID the identifier of the Rating object to be deleted
   * @throws org.ambraproject.ApplicationException on an error
   */
  @SuppressWarnings("unchecked")
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteRating(final Long ratingID, String authId) throws ApplicationException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    final Rating articleRating = (Rating)hibernateTemplate.get(Rating.class, ratingID);

    if (articleRating == null) {
      throw new HibernateException("Failed to get Rating to delete: " + ratingID);
    }

    final long articleID = articleRating.getArticleID();

    final RatingSummary ratingSummary = getRatingSummary(articleID);

    if (ratingSummary == null) {
      throw new HibernateException("No RatingSummary object found for articleID: " + articleID);
    }

    ratingSummary.removeRating(articleRating);

    hibernateTemplate.update(ratingSummary);
    hibernateTemplate.delete(articleRating);
  }

  /**
   * Get the rating for the given article by the given user
   * @param articleID
   * @param user
   * @return
   */
  @SuppressWarnings("unchecked")
  public Rating getRating(final long articleID, final UserProfile user) {
    List results = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Rating.class)
        .add(Restrictions.eq("articleID", articleID))
        .add(Restrictions.eq("creator", user)));

    if(results.size() > 1) {
      throw new RuntimeException("Found more then one Rating per article, per user!");
    }

    if(results.size() == 1) {
      return (Rating)results.get(0);
    }

    return null;
  }

  /**
   * Get list of ratingsummary views for the given article
   * @param articleID
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<RatingView> getRatingViewList(final Long articleID) {
    List results = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Rating.class)
        .add(Restrictions.eq("articleID", articleID))
        .addOrder(Order.asc("created")));
    
    // create ArticleRatingSummary(s)
    List<RatingView> summaries = new ArrayList<RatingView>();

    for (Object rating : results) {
      RatingView summary = new RatingView((Rating)rating);

      summaries.add(summary);
    }

    return summaries;
  }

  /**
   * Get rating summary list for the given article
   * @param articleID the articleID to get the summary for
   * @return
   */
  @SuppressWarnings("unchecked")
  public RatingSummary getRatingSummary(final long articleID)
  {
    List results = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(RatingSummary.class)
        .add(Restrictions.eq("articleID", articleID)));

    if(results.size() > 1) {
      throw new RuntimeException("Found more then one RatingSummary for article:" + articleID);
    }

    if(results.size() == 1) {
      return (RatingSummary)results.get(0);
    }

    return null;
  }

  /**
   * Save a rating
   * @param rating rating to save
   */
  public void saveRating(Rating rating)
  {
    //Update ratingSummary as well as rating
    //The ratings summary has the aggregate of all ratings
    RatingSummary ratingSummary = getRatingSummary(rating.getArticleID());

    if (ratingSummary == null) {
      //RatingSummary is null, assume no ratings exist
      ratingSummary = new RatingSummary();
      ratingSummary.setArticleID(rating.getArticleID());
      //Add the new values to the summary
      ratingSummary.addRating(rating);

      //Assume this is a new rating, lets create the annotationURI
      rating.setAnnotationUri(URIGenerator.generate(rating));

      hibernateTemplate.save(rating);
      hibernateTemplate.save(ratingSummary);
    } else {
      //If ratingSummary exists, check to see if the user already rated the article
      Rating oldRating = this.getRating(rating.getArticleID(), rating.getCreator());
      if(oldRating != null) {
        //If so, lets remove the old values from the summary
        ratingSummary.removeRating(oldRating);

        //Copy the new rating values to the existing rating record
        oldRating.setInsight(rating.getInsight());
        oldRating.setReliability(rating.getReliability());
        oldRating.setStyle(rating.getStyle());
        oldRating.setSingleRating(rating.getSingleRating());
        oldRating.setBody(rating.getBody());
        oldRating.setTitle(rating.getTitle());
        oldRating.setCompetingInterestBody(rating.getCompetingInterestBody());

        //Add the new values to the summary
        ratingSummary.addRating(rating);

        hibernateTemplate.update(oldRating);
        hibernateTemplate.update(ratingSummary);
      } else {
        //This is a new rating, lets create the annotationURI
        rating.setAnnotationUri(URIGenerator.generate(rating));
        //Add the new values to the summary
        ratingSummary.addRating(rating);

        hibernateTemplate.save(rating);
        hibernateTemplate.update(ratingSummary);
      }
    }
  }

  /**
   * Get a Rating by Id.
   *
   * @param ratingID Rating Id
   * @return Rating
   */
  @Transactional(readOnly = true)
  public Rating getRating(final Long ratingID) {
    return (Rating)hibernateTemplate.get(Rating.class, ratingID);
  }

  /**
   * Get a Rating by URI.
   *
   * @param annotationUri Rating URI
   * @return Rating
   */
  @Transactional(readOnly = true)
  public Rating getRating(final String annotationUri) {
    List results = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Rating.class)
        .add(Restrictions.eq("annotationUri", annotationUri)));

    if(results.size() > 1) {
      throw new RuntimeException("Found more then one Rating for annotationUri:" + annotationUri);
    }

    if(results.size() == 1) {
      return (Rating)results.get(0);
    }

    return null;
  }

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public RatingSummaryView getAverageRatings(final long articleID) {
    log.debug("retrieving rating summaries for: {}", articleID);

    RatingSummary ratingSummary = getRatingSummary(articleID);

    return (ratingSummary != null) ? new RatingSummaryView(ratingSummary) :
                                          new RatingSummaryView();
  }

  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public boolean hasRated(final long articleID, final UserProfile user) {
    if (user == null) {
      return false;
    }

    List results = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Rating.class)
        .add(Restrictions.eq("articleID", articleID))
        .add(Restrictions.eq("creator", user))
        .setProjection(Projections.rowCount()));

    Long count = (Long)results.get(0);

    if(count == 1L) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Set the id of the application
   * @param applicationId applicationId
   */
  @Required
  public void setApplicationId(final String applicationId) {
    this.applicationId = applicationId;
  }

  @Required
  public void setPermissionsService(final PermissionsService ps) {
    this.permissionsService = ps;
  }

  /**
   * @return the application id
   */
  public String getApplicationId() {
    return applicationId;
  }
}
