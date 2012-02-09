/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.annotation.service;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Trackback;
import org.topazproject.ambra.models.TrackbackContent;
import org.ambraproject.service.HibernateServiceImpl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alex Kudlick Date: 5/5/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class TrackbackServiceImpl extends HibernateServiceImpl implements TrackbackService {
  private static final Logger log = LoggerFactory.getLogger(TrackbackServiceImpl.class);

  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public ArrayList<Trackback> getTrackbacks(final String annotates, final boolean getBodies) {
    return (ArrayList<Trackback>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        if (log.isDebugEnabled())
          log.debug("retrieving trackbacks for: " + annotates);

        List<Trackback> results = session.createCriteria(Trackback.class)
            .add(Restrictions.eq("annotates", annotates))
            .addOrder(Order.desc("created"))
            .list();

        //need to return an ArrayList
        ArrayList<Trackback> trackbacks = new ArrayList<Trackback>(results);
        //TODO: We may just not do lazy loading at all, in which case this would be unnecessary
        if (getBodies) {
          for (Trackback trackback : trackbacks) {
            TrackbackContent body = trackback.getBody();
            if (body != null) {
              body.getBlog_name();
              body.getExcerpt();
              body.getId();
              body.getUrl();
              body.getTitle();
            }
          }
        }
        return trackbacks;
      }
    });
  }

  /**
   *
   * @param title The title of the article
   * @param blog_name the blog name
   * @param excerpt the excertp from the blog
   * @param permalink a permalink to the article
   * @param annotates - the annotates property for the trackback
   * @param url - the url to use in looking if the trackback already exists
   * @param trackbackId - was only used to kick trackbacks out of the cache, so not used any more
   * @return - true if a trackback was created, false if it already existed
   * @throws Exception
   */
  @Override
  @SuppressWarnings("unchecked")
  @Transactional(rollbackFor = Throwable.class)
  public boolean saveTrackBack(final String title, final String blog_name, final String excerpt,
                               final URL permalink, final URI annotates, final String url,
                               final String trackbackId) throws Exception {

    return (Boolean)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        try {
          //Check to see if the trackback exists by using
          //The articleDOI and URL as the unique key
          List<Trackback> trackbackList = session
            .createCriteria(Trackback.class)
            .add(Restrictions.eq("annotates", annotates))
            .createCriteria("body")
            .add(Restrictions.eq("url", new URL(url)))
            .list();

          if (trackbackList.size() == 0) {
            if (log.isDebugEnabled()) {
              log.debug("No previous trackback found for: " + permalink);
            }
            Trackback trackback = new Trackback();
            trackback.setBody(new TrackbackContent(title, excerpt, blog_name, permalink));
            trackback.setAnnotates(annotates);
            trackback.setCreated(new Date());

            session.save(trackback);

            return true;
          } else {
            return false;
          }
        } catch(MalformedURLException ex) {
          throw new HibernateException(ex.getMessage(), ex);
        }
      }
    });
  }
}
