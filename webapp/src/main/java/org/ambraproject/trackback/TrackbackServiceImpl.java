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

package org.ambraproject.trackback;

import org.ambraproject.models.Article;
import org.ambraproject.models.Trackback;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.views.TrackbackView;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Journal;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Kudlick Date: 5/5/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class TrackbackServiceImpl extends HibernateServiceImpl implements TrackbackService {
  private static final Logger log = LoggerFactory.getLogger(TrackbackServiceImpl.class);

  private Configuration configuration;

  @Required
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<TrackbackView> getTrackbacks(final Date startDate, final Date endDate,
                                           final int maxResults, final String journal) {

    /***
     * There may be a more efficient way to do this other than querying the database twice, at some point in time
     * we might improve how hibernate does the object mappings
     *
     * This execute returns trackbackIDs, article DOIs and titles, which are needed to construction the trackbackView
     * object
     */
    Map<Long, String[]> results = (Map<Long, String[]>)hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {

        /**
         * We have to do this with SQL because of how the mappings are currently defined
         * And hence, there is no way to unit test this
         */

        StringBuilder sqlQuery = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>(3);

        sqlQuery.append("select track.trackbackID, art.doi, art.title ");
        sqlQuery.append("from trackback track ");
        sqlQuery.append("join article art on art.articleID = track.articleID ");
        sqlQuery.append("join Journal j on art.eIssn = j.eIssn ");
        sqlQuery.append("where j.journalKey = :journal ");
        params.put("journal", journal);

        if (startDate != null) {
          sqlQuery.append(" and track.created > :startDate");
          params.put("startDate", startDate);
        }

        if (endDate != null) {
          sqlQuery.append(" and track.created < :endDate");
          params.put("endDate", endDate);
        }

        sqlQuery.append(" order by track.created desc");

        SQLQuery query = session.createSQLQuery(sqlQuery.toString());
        query.setProperties(params);

        if (maxResults > 0) {
          query.setMaxResults(maxResults);
        }

        List<Object[]> tempResults = query.list();
        Map<Long, String[]> results = new HashMap<Long, String[]>(tempResults.size());

        for(Object[] obj : tempResults) {
          //This forces this method to return Long values and not BigInteger
          results.put((((Number)obj[0]).longValue()), new String[] { (String)obj[1], (String)obj[2] });
        }

        return results;
      }
    });

    //The previous query puts annotationID and doi into the map. annotationID is key
    //I do this to avoid extra doi lookups later in the code.

    if(results.size() > 0) {
      DetachedCriteria criteria = DetachedCriteria.forClass(org.ambraproject.models.Trackback.class)
          .add(Restrictions.in("ID", results.keySet()))
          .addOrder(Order.desc("created"));

      List<org.ambraproject.models.Trackback> trackbacks = hibernateTemplate.findByCriteria(criteria);
      List<TrackbackView> views = new ArrayList<TrackbackView>(trackbacks.size());

      for(org.ambraproject.models.Trackback track : trackbacks) {
        String articleDoi = results.get(track.getID())[0];
        String articleTitle = results.get(track.getID())[1];
        views.add(new TrackbackView(track, articleDoi, articleTitle));
      }

      return views;
    } else {
      return new ArrayList<TrackbackView>();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Long createTrackback(String articleDoi, String url, String title, String blogName, String excerpt) throws DuplicateTrackbackException {
    if (articleDoi == null) {
      throw new IllegalArgumentException("No DOI specified");
    } else if (url == null || title == null || excerpt == null || blogName == null) {
      throw new IllegalArgumentException("URL, title, excerpt, and blog name must be provided");
    }

    Long articleId;
    try {
      articleId = (Long) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", articleDoi))
              .setProjection(Projections.id()), 0, 1
      ).get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("DOI: " + articleDoi + " didn't correspond to an article");
    }

    List<Long> existingTrackbacks = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Trackback.class)
            .add(Restrictions.eq("articleID", articleId))
            .add(Restrictions.eq("url", url))
            .setProjection(Projections.id())
    );
    if (existingTrackbacks.size() > 0) {
      throw new DuplicateTrackbackException(articleDoi, url);
    } else {
      log.debug("Creating trackback for article: {}; url: {}", articleDoi, url);
      Trackback trackback = new Trackback();
      trackback.setArticleID(articleId);
      trackback.setTitle(title);
      trackback.setBlogName(blogName);
      trackback.setUrl(url);
      trackback.setExcerpt(excerpt);
      return (Long) hibernateTemplate.save(trackback);
    }
  }

  @Override
  public boolean blogLinksToArticle(String blogUrl, String doi) throws IOException {
    log.debug("Validating blog at {} for {}", blogUrl, doi);

    // Trick gets Swing's HTML parser
    HTMLEditorKit.Parser parser = (new HTMLEditorKit() {
      public Parser getParser() {
        return super.getParser();
      }
    }).getParser();


    // Read HTML file into string
    StringBuilder html = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = new URL(blogUrl).openStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        html.append(line);
      }

      //parse the html, looking for links
      LinkCallback callback = new LinkCallback(configuration, hibernateTemplate, doi);
      parser.parse(new StringReader(html.toString()), callback, true);
      return callback.foundLink();
    } finally {
      //close our reader (closes all the encapsulated streams)
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          log.error("Error closing buffered input reader to " + blogUrl, e);
        }
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TrackbackView> getTrackbacksForArticle(String articleDoi) {
    if (StringUtils.isEmpty(articleDoi)) {
      throw new IllegalArgumentException("No Doi specified");
    }
    Long articleId;
    String articleTitle;
    try {
      Object[] articleRow = (Object[]) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", articleDoi))
              .setProjection(Projections.projectionList()
                  .add(Projections.id())
                  .add(Projections.property("title"))
              ), 0, 1
      ).get(0);
      articleId = (Long) articleRow[0];
      articleTitle = (String) articleRow[1];
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Doi " + articleDoi + " didn't correspond to an article");
    }
    log.debug("loading up trackbacks for article {}", articleDoi);

    List<Trackback> trackbacks = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Trackback.class)
            .add(Restrictions.eq("articleID", articleId))
            .addOrder(Order.desc("created"))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
    );
    List<TrackbackView> results = new ArrayList<TrackbackView>(trackbacks.size());
    for (Trackback trackback : trackbacks) {
      results.add(new TrackbackView(trackback, articleDoi, articleTitle));
    }

    log.info("Loaded {} trackbacks for {}", results.size(), articleDoi);
    return results;
  }

  @Override
  public int countTrackbacksForArticle(String articleDoi) {
    if (StringUtils.isEmpty(articleDoi)) {
      throw new IllegalArgumentException("Didn't specify an article doi");
    }
    Long articleId;
    try {
      articleId = (Long) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", articleDoi))
              .setProjection(Projections.id()), 0, 1
      ).get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Doi: " + articleDoi + " didn't correspond to an article");
    }
    return ((Number) hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Trackback.class)
            .add(Restrictions.eq("articleID", articleId))
            .setProjection(Projections.rowCount())
    ).get(0)).intValue();
  }

  /**
   * Parser callback that just checks to see if we found a link with the article url in it. Checks for the following
   * urls: <ol> <li>URL with www, and unencoded doi</li> <li>URL with www, and encoded doi</li> <li>URL without www, and
   * unencoded doi</li> <li>URL without www, and encoded doi</li> </ol>
   */
  private static final class LinkCallback extends HTMLEditorKit.ParserCallback {

    private boolean foundLink = false;
    private String url;
    private String urlWww;
    private String urlEncoded;
    private String urlWwwEncoded;

    private LinkCallback(Configuration configuration, HibernateTemplate hibernateTemplate, String doi) {
      //Make sure we have an unencoded an an encoded doi
      String doiEncoded;
      if (doi.contains("%")) {
        try {
          //doi is already encoded
          doiEncoded = doi;
          doi = URLDecoder.decode(doi, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          //doi is already assigned
          doiEncoded = doi;
        }
      } else {
        try {
          //doi is not encoded
          doiEncoded = URLEncoder.encode(doi, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          doiEncoded = doi;
        }
      }

      //look up the url for the journal in which the article was published
      String eIssn;
      try {
        eIssn = (String) hibernateTemplate.findByCriteria(
            DetachedCriteria.forClass(Article.class)
                .add(Restrictions.eq("doi", doi))
                .setProjection(Projections.property("eIssn")),
            0, 1).get(0);
      } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Doi " + doi + " didn't correspond to an article");
      }
      String journalName = (String) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Journal.class)
              .add(Restrictions.eq("eIssn", eIssn))
              .setProjection(Projections.property("key")),
          0, 1).get(0);
      String journalUrl = configuration.getString("ambra.virtualJournals." + journalName + ".url");
      journalUrl += '/' + configuration.getString("ambra.platform.articleAction");

      //Now build up the list of acceptable urls
      if (journalUrl.startsWith("http://www.")) {
        urlWww = journalUrl + doi;
        urlWwwEncoded = journalUrl + doiEncoded;

        url = "http://" + journalUrl.substring(11) + doi;
        urlEncoded = "http://" + journalUrl.substring(11) + doiEncoded;
      } else {
        url = journalUrl + doi;
        urlEncoded = journalUrl + doiEncoded;

        urlWww = "http://www." + journalUrl.substring(7) + doi;
        urlWwwEncoded = "http://www." + journalUrl.substring(7) + doiEncoded;
      }
    }

    //Callback method
    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int pos) {
      if (HTML.Tag.A == tag) {
        String href = (String) attributes.getAttribute(HTML.Attribute.HREF);
        if (href == null) {
          return;
        }
        //eliminate the anchor part of the url, if there is one
        int hashPos = href.lastIndexOf('#');
        if (hashPos != -1) {
          href = href.substring(0, hashPos);
        }
        //is this a link to the article?
        if (href.equals(urlEncoded) ||
            href.equals(urlWwwEncoded) ||
            href.equals(url) ||
            href.equals(urlWww)) {
          foundLink = true;
        }
      }
    }

    public boolean foundLink() {
      return foundLink;
    }
  }

}
