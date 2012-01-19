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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.ApplicationException;
import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingSummary;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.user.AmbraUser;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

/**
 * This service allows client code to operate on ratings objects.
 *
 * @author jonnie.
 */
public class RatingsServiceImpl extends HibernateServiceImpl implements RatingsService {
  private static final Logger     log = LoggerFactory.getLogger(RatingsServiceImpl.class);

  private String applicationId;
  private PermissionsService permissionsService;

  public RatingsServiceImpl() {
  }

//  @Required
//  public void setRatingsPdp(PDP pdp) {
//    pep = new RatingsPEP(pdp);
//  }

  /**
   * Delete the Rating identified by ratingId and update the RatingSummary.
   *
   * @param ratingId the identifier of the Rating object to be deleted
   * @throws org.ambraproject.ApplicationException on an error
   */
  @SuppressWarnings("unchecked")
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteRating(final String ratingId, String authId) throws ApplicationException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        final Rating articleRating = (Rating)hibernateTemplate.load(Rating.class, URI.create(ratingId));

        if (articleRating == null) {
          throw new HibernateException("Failed to get Rating to delete: "+ ratingId);
        }

        final URI articleURI = articleRating.getAnnotates();
        final List<RatingSummary> summaryList = session.createCriteria(RatingSummary.class).
          add(Restrictions.eq("annotates", articleURI.toString())).list();

        if (summaryList.size() <= 0) {
          throw new HibernateException("No RatingSummary object found for article " +
                                         articleURI.toString());
        }
        if (summaryList.size() > 1) {
          throw new HibernateException("Multiple RatingSummary objects found found " +
                                         "for article " + articleURI.toString());
        }

        final RatingSummary articleRatingSummary = summaryList.get(0);
        final int newNumberOfRatings = articleRatingSummary.getBody().getNumUsersThatRated() - 1;
        final int insight = articleRating.getBody().getInsightValue();
        final int reliability = articleRating.getBody().getReliabilityValue();
        final int style = articleRating.getBody().getStyleValue();
        final int single = articleRating.getBody().getSingleRatingValue();

        articleRatingSummary.getBody().setNumUsersThatRated(newNumberOfRatings);
        removeRating(articleRatingSummary, Rating.INSIGHT_TYPE, insight);
        removeRating(articleRatingSummary, Rating.RELIABILITY_TYPE, reliability);
        removeRating(articleRatingSummary, Rating.STYLE_TYPE, style);
        removeRating(articleRatingSummary, Rating.SINGLE_RATING_TYPE, single);

        hibernateTemplate.delete(articleRating);

        return null;
      }
    });
  }

  private void removeRating(RatingSummary articleRatingSummary, String type, int rating) {
    if (rating > 0) {
      articleRatingSummary.getBody().removeRating(type, rating);
    }
  }

  /**
   * Get list of ratings for the given article by the given user
   * @param articleURI
   * @param userID
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Rating> getRatingsList(final String articleURI, final String userID) {
    return (List<Rating>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        return session.createCriteria(Rating.class).
          add(Restrictions.eq("annotates", articleURI)).
          add(Restrictions.eq("creator", userID)).list();

      }
    });
  }

  /**
   * Get list of ratings for the given article
   * @param articleURI
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Rating> getRatingsList(final String articleURI) {
    return (List<Rating>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        return session.createCriteria(Rating.class).
          add(Restrictions.eq("annotates", articleURI))
          .addOrder(Order.asc("created"))
          .list();
      }
    });
  }

  /**
   * Get rating summary list for the given article
   * @param articleURI
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<RatingSummary> getRatingSummaryList(final String articleURI)
  {
    return (List<RatingSummary>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        return session.createCriteria(RatingSummary.class).
          add(Restrictions.eq("annotates", articleURI)).list();
      }
    });
  }

  /**
   * Save a ratingSummary
   * @param ratingSummary
   */
  public void saveRatingSummary(RatingSummary ratingSummary)
  {
    hibernateTemplate.saveOrUpdate(ratingSummary);
  }

  /**
   * Save a rating
   * @param r rating
   */
  public void saveRating(Rating r)
  {
    hibernateTemplate.saveOrUpdate(r);
  }

  /**
   * Get a Rating by Id.
   *
   * @param ratingId Rating Id
   * @param user current ambra user
   * @return Rating
   */
  @Transactional(readOnly = true)
  public Rating getRating(final String ratingId, AmbraUser user) {
    Rating rating = (Rating)hibernateTemplate.load(Rating.class, URI.create(ratingId));

    return rating;
  }

  /**
   * List the set of Ratings in a specific administrative state.
   *
   * @param mediator if present only those annotations that match this mediator are returned
   * @param state    the state to filter the list of annotations by or 0 to return annotations
   *                 in any administrative state
   * @return an array of rating metadata; if no matching annotations are found, an empty array
   *         is returned
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public Rating[] listRatings(final String mediator,final int state) {
    return (Rating[])hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria c = session.createCriteria(Rating.class);

        if (mediator != null)
          c.add(Restrictions.eq("mediator",mediator));

        if (state == 0) {
          c.add(Restrictions.ne("state", "0"));
        } else {
          c.add(Restrictions.eq("state", "" + state));
        }

        return (Rating[]) c.list().toArray(new Rating[c.list().size()]);

      }
    });
  }

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public AverageRatings getAverageRatings(final String articleURI) {
    if (log.isDebugEnabled())
      log.debug("retrieving rating summaries for: " + articleURI);

    return (AverageRatings)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<RatingSummary> summaryList = session.createCriteria(RatingSummary.class).
                                          add(Restrictions.eq("annotates", articleURI)).
                                          list();
        return (summaryList.size() > 0) ? new AverageRatings(summaryList.get(0)) :
                                          new AverageRatings();
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public boolean hasRated(final String articleURI, final AmbraUser user) {
    if (user == null)
      return false;

    if (log.isDebugEnabled()) {
      log.debug("retrieving list of user ratings for article: " + articleURI + " and user: " +
                user.getUserId());
    }

    return (Boolean)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<Rating> ratingsList = session.createCriteria(Rating.class).
           add(Restrictions.eq("annotates", articleURI)).
           add(Restrictions.eq("creator", user.getUserId())).list();

        return ratingsList.size() > 0;
      }
    });
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
