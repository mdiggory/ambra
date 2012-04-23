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

import org.ambraproject.ApplicationException;
import org.ambraproject.annotation.Context;
import org.ambraproject.annotation.ContextFormatter;
import org.ambraproject.cache.Cache;
import org.ambraproject.hibernate.URIGenerator;
import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Article;
import org.ambraproject.models.Flag;
import org.ambraproject.models.FlagReasonCode;
import org.ambraproject.models.UserProfile;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.views.AnnotationView;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Kudlick Date: 4/29/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class AnnotationServiceImpl extends HibernateServiceImpl implements AnnotationService {
  private static final Logger log = LoggerFactory.getLogger(AnnotationServiceImpl.class);
  private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

  private Cache articleHtmlCache;


  /**
   * @param articleHtmlCache The Article(transformed) cache to use
   */
  @Required
  public void setArticleHtmlCache(org.ambraproject.cache.Cache articleHtmlCache) {
    this.articleHtmlCache = articleHtmlCache;
  }

  /**
   * Get a list of all annotations satisfying the given criteria.
   *
   * @param startDate  search for annotation after start date.
   * @param endDate    is the date to search until. If null, search until present date
   * @param annotTypes List of annotation types
   * @param maxResults the maximum number of results to return, or 0 for no limit
   * @param journal    journalName
   * @return the (possibly empty) list of article annotations.
   * @throws ParseException     if any of the dates or query could not be parsed
   * @throws URISyntaxException if an element of annotType cannot be parsed as a URI
   */
  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<AnnotationView> getAnnotations(final Date startDate, final Date endDate,
                                             final Set<String> annotTypes, final int maxResults, final String journal)
      throws ParseException, URISyntaxException {
    /***
     * There may be a more efficient way to do this other than querying the database twice, at some point in time
     * we might improve how hibernate does the object mappings
     *
     * This execute returns annotationIDs, article DOIs and titles, which are needed to construction the annotionView
     * object
     */
    Map<Long, String[]> results = (Map<Long, String[]>) hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {

        /**
         * We have to do this with SQL because of how the mappings are currently defined
         * And hence, there is no way to unit test this
         */

        StringBuilder sqlQuery = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>(3);

        sqlQuery.append("select ann.annotationID, art.doi, art.title ");
        sqlQuery.append("from annotation ann ");
        sqlQuery.append("join article art on art.articleID = ann.articleID ");
        sqlQuery.append("join Journal j on art.eIssn = j.eIssn ");
        sqlQuery.append("where j.journalKey = :journal ");
        params.put("journal", journal);

        if (startDate != null) {
          sqlQuery.append(" and ann.created > :startDate");
          params.put("startDate", startDate);
        }

        if (endDate != null) {
          sqlQuery.append(" and ann.created < :endDate");
          params.put("endDate", endDate);
        }

        if (annotTypes != null) {
          sqlQuery.append(" and ann.type in (:annotTypes)");
          params.put("annotTypes", annotTypes);
        }

        sqlQuery.append(" order by ann.created desc");

        SQLQuery query = session.createSQLQuery(sqlQuery.toString());
        query.setProperties(params);

        if (maxResults > 0) {
          query.setMaxResults(maxResults);
        }

        List<Object[]> tempResults = query.list();
        Map<Long, String[]> results = new HashMap<Long, String[]>(tempResults.size());

        for (Object[] obj : tempResults) {
          //This forces this method to return Long values and not BigInteger
          results.put((((Number) obj[0]).longValue()), new String[]{(String) obj[1], (String) obj[2]});
        }

        return results;
      }
    });

    //The previous query puts annotationID and doi into the map. annotationID is key
    //I do this to avoid extra doi lookups later in the code.

    if (results.size() > 0) {
      DetachedCriteria criteria = DetachedCriteria.forClass(Annotation.class)
          .add(Restrictions.in("ID", results.keySet()))
          .addOrder(Order.desc("created"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

      List<Annotation> annotations = hibernateTemplate.findByCriteria(criteria);
      List<AnnotationView> views = new ArrayList<AnnotationView>(annotations.size());

      for (Annotation ann : annotations) {
        String articleDoi = results.get(ann.getID())[0];
        String articleTitle = results.get(ann.getID())[1];
        views.add(buildAnnotationView(ann, articleDoi, articleTitle, false));
      }

      return views;
    } else {
      return new ArrayList<AnnotationView>();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public AnnotationView[] listAnnotations(final Long articleID,
                                          final Set<AnnotationType> annotationTypes,
                                          final AnnotationOrder order) {
    //Basic criteria
    DetachedCriteria criteria = DetachedCriteria.forClass(Annotation.class)
        .add(Restrictions.eq("articleID", articleID))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    //restrict by type
    if (annotationTypes != null && !annotationTypes.isEmpty()) {
      criteria.add(Restrictions.in("type", annotationTypes));
    }
    switch (order) {
      case OLDEST_TO_NEWEST:
        criteria.addOrder(Order.asc("created"));
        break;
      case MOST_RECENT_REPLY:
        //Still going to have to sort the results after creating views, because 'Most Recent Reply' isn't something that's stored on the database level
        //but ordering newest to oldest makes it more likely that the annotations will be in close to the correct order by the time we sort
        criteria.addOrder(Order.desc("created"));
        break;
    }
    List annotationResults = hibernateTemplate.findByCriteria(criteria);


    //Don't want to call buildAnnotationView() here because that would involve loading up the reply map for each annotation,
    // when we only need to do it once. So load up the info we need to build annotation views here
    Object[] articleTitleAndDoi;
    try {
      articleTitleAndDoi = (Object[]) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("ID", articleID))
              .setProjection(Projections.projectionList()
                  .add(Projections.property("doi"))
                  .add(Projections.property("title"))),
          0, 1).get(0);

    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("article " + articleID + " didn't exist");
    }
    String articleDoi = (String) articleTitleAndDoi[0];
    String articleTitle = (String) articleTitleAndDoi[1];

    Map<Long, List<Annotation>> replyMap = buildReplyMap(articleID);


    List<AnnotationView> viewResults = new ArrayList<AnnotationView>(annotationResults.size());

    for (Object annotation : annotationResults) {
      viewResults.add(new AnnotationView((Annotation) annotation, articleDoi, articleTitle, replyMap));
    }

    if (order == AnnotationOrder.MOST_RECENT_REPLY) {
      //Order the results by the most recent reply date
      Collections.sort(viewResults, new Comparator<AnnotationView>() {
        @Override
        public int compare(AnnotationView view1, AnnotationView view2) {
          return -1 * view1.getLastReplyDate().compareTo(view2.getLastReplyDate());
        }
      });
    }

    return viewResults.toArray(new AnnotationView[viewResults.size()]);
  }

  @Override
  @Transactional(readOnly = true)
  public AnnotationView[] listAnnotationsNoReplies(final Long articleID,
                                                   final Set<AnnotationType> annotationTypes,
                                                   final AnnotationOrder order) {
    if (order == AnnotationOrder.MOST_RECENT_REPLY) {
      throw new IllegalArgumentException("Cannot specify Most Recent Reply order type when replies are not being loaded up");
    }
    //Basic criteria
    DetachedCriteria criteria = DetachedCriteria.forClass(Annotation.class)
        .add(Restrictions.eq("articleID", articleID))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    //restrict by type
    if (annotationTypes != null && !annotationTypes.isEmpty()) {
      criteria.add(Restrictions.in("type", annotationTypes));
    }
    switch (order) {
      case OLDEST_TO_NEWEST:
        criteria.addOrder(Order.asc("created"));
        break;
    }
    List annotationResults = hibernateTemplate.findByCriteria(criteria);
    //Don't want to call buildAnnotationView() here because that would involve finding the article title and doi for each annotation,
    // when we only need to do it once. So load up the info we need to build annotation views here
    Object[] articleTitleAndDoi;
    try {
      articleTitleAndDoi = (Object[]) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("ID", articleID))
              .setProjection(Projections.projectionList()
                  .add(Projections.property("doi"))
                  .add(Projections.property("title"))),
          0, 1).get(0);

    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("article " + articleID + " didn't exist");
    }
    String articleDoi = (String) articleTitleAndDoi[0];
    String articleTitle = (String) articleTitleAndDoi[1];

    List<AnnotationView> viewResults = new ArrayList<AnnotationView>(annotationResults.size());
    for (Object annotation : annotationResults) {
      viewResults.add(new AnnotationView((Annotation) annotation, articleDoi, articleTitle, null));
    }
    return viewResults.toArray(new AnnotationView[viewResults.size()]);
  }

  @Override
  public int countAnnotations(Long articleID, Set<AnnotationType> annotationTypes) {
    if (annotationTypes != null && !annotationTypes.isEmpty()) {
      return ((Number) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Annotation.class)
              .add(Restrictions.eq("articleID", articleID))
              .add(Restrictions.in("type", annotationTypes))
              .setProjection(Projections.rowCount())
      ).get(0)).intValue();
    } else {
      return ((Number) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Annotation.class)
              .add(Restrictions.eq("articleID", articleID))
              .setProjection(Projections.rowCount())
      ).get(0)).intValue();
    }
  }


  @Override
  public Long createComment(UserProfile user, String articleDoi, String title, String body, String ciStatement, Context context, boolean flagAsCorrection) {
    if (articleDoi == null) {
      throw new IllegalArgumentException("Attempted to create comment with null article id");
    } else if (user == null || user.getID() == null) {
      throw new IllegalArgumentException("Attempted to create comment without a creator");
    } else if (body == null || body.isEmpty()) {
      throw new IllegalArgumentException("Attempted to create comment with no body");
    }
    String xpath = null;
    if (context != null) {
      try {
        //ContextFormatter.asXPointer() can return null or empty if the context doesn't indicate a path
        xpath = ContextFormatter.asXPointer(context);
      } catch (ApplicationException e) {
        throw new IllegalArgumentException("Invalid context", e);
      }
    }
    log.debug("Creating comment on article: {}; title: {}; body: {}", new Object[]{articleDoi, title, body});
    Long articleID;
    try {
      articleID = (Long) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", articleDoi))
              .setProjection(Projections.id())
      ).get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Invalid doi: " + articleDoi);
    }
    //If the xpointer was valid, it's an inline note. Else it's a general comment
    AnnotationType type;
    if (!StringUtils.isEmpty(xpath)) {
      type = AnnotationType.NOTE;
      //kick the article out of cache
      articleHtmlCache.remove(articleDoi);
    } else {
      type = AnnotationType.COMMENT;
    }

    //generate an annotation uri
    Annotation comment = new Annotation(user, type, articleID);
    comment.setAnnotationUri(URIGenerator.generate(comment));
    comment.setTitle(title);
    comment.setBody(body);
    comment.setCompetingInterestBody(ciStatement);
    comment.setXpath(xpath);
    Long id = (Long) hibernateTemplate.save(comment);
    if (flagAsCorrection) {
      Flag flag = new Flag(user, FlagReasonCode.CORRECTION, comment);
      flag.setComment("Note created and flagged as a correction");
      hibernateTemplate.save(flag);
    }

    return id;
  }

  @Override
  public Long createReply(UserProfile user, Long parentId, String title, String body, @Nullable String ciStatement) {
    if (parentId == null) {
      throw new IllegalArgumentException("Attempting to create reply with null parent id");
    }
    log.debug("Creating reply to {}; title: {}; body: {}", new Object[]{parentId, title, body});
    Long articleID;
    try {
      articleID = (Long) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Annotation.class)
              .add(Restrictions.eq("ID", parentId))
              .setProjection(Projections.property("articleID")), 0, 1)
          .get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Invalid annotation id: " + parentId);
    }

    Annotation reply = new Annotation(user, AnnotationType.REPLY, articleID);
    reply.setParentID(parentId);
    reply.setTitle(title);
    reply.setBody(body);
    reply.setCompetingInterestBody(ciStatement);
    reply.setAnnotationUri(URIGenerator.generate(reply));
    return (Long) hibernateTemplate.save(reply);
  }

  @Override
  @SuppressWarnings("unchecked")
  public AnnotationView getFullAnnotationView(Long annotationId) {
    if (annotationId == null) {
      throw new IllegalArgumentException("No annotation id specified");
    }
    log.debug("populating view object for annotation {}", annotationId);
    Annotation annotation = (Annotation) hibernateTemplate.get(Annotation.class, annotationId);
    if (annotation == null) {
      throw new IllegalArgumentException("Specified id that does not correspond to an annotation; " + annotationId);
    }

    return buildAnnotationView(annotation, true);
  }

  @Override
  public AnnotationView getBasicAnnotationView(Long annotationId) {
    if (annotationId == null) {
      throw new IllegalArgumentException("No annotation id specified");
    }
    log.debug("populating view object for annotation {}", annotationId);
    Annotation annotation = (Annotation) hibernateTemplate.get(Annotation.class, annotationId);
    if (annotation == null) {
      throw new IllegalArgumentException("Specified id that does not correspond to an annotation; " + annotationId);
    }
    return buildAnnotationView(annotation, false);
  }

  @Override
  public AnnotationView getBasicAnnotationViewByUri(String annotationUri) {
    if (annotationUri == null) {
      throw new IllegalArgumentException("No annotation URI specified");
    }
    log.debug("populating view object for annotation {}", annotationUri);
    Annotation annotation;
    try {
      annotation = (Annotation) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Annotation.class)
              .add(Restrictions.eq("annotationUri", annotationUri))
              .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
      ).get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Specified URI that does not correspond to an annotation; " + annotationUri);
    }
    return buildAnnotationView(annotation, false);
  }

  @SuppressWarnings("unchecked")
  private AnnotationView buildAnnotationView(Annotation annotation,
                                             String articleDoi,
                                             String articleTitle,
                                             boolean loadAllReplies) {
    Map<Long, List<Annotation>> fulReplyMap = null;
    if (loadAllReplies) {
      fulReplyMap = buildReplyMap(annotation.getArticleID());
    }

    return new AnnotationView(annotation, articleDoi, articleTitle, fulReplyMap);
  }

  /**
   * Build up a map of id, and replies to that id so we can initialize the reply tree
   *
   * @param articleId
   * @return
   */
  @SuppressWarnings("unchecked")
  private Map<Long, List<Annotation>> buildReplyMap(Long articleId) {
    Map<Long, List<Annotation>> fullReplyMap = new HashMap<Long, List<Annotation>>();
    List<Annotation> allReplies = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Annotation.class)
            .add(Restrictions.eq("articleID", articleId))
            .add(Restrictions.eq("type", AnnotationType.REPLY))
    );
    for (Annotation reply : allReplies) {
      //parent id should never be null on a reply
      if (reply.getParentID() == null) {
        log.warn("Found a reply with null parent id.  Reply id: " + reply.getID());
      } else {
        if (!fullReplyMap.containsKey(reply.getParentID())) {
          fullReplyMap.put(reply.getParentID(), new ArrayList<Annotation>());
        }
        fullReplyMap.get(reply.getParentID()).add(reply);
      }
    }
    return Collections.unmodifiableMap(fullReplyMap);
  }

  @SuppressWarnings("unchecked")
  private AnnotationView buildAnnotationView(Annotation annotation, boolean loadAllReplies) {
    Object values[];
    try {
      values = (Object[]) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("ID", annotation.getArticleID()))
              .setProjection(Projections.projectionList()
                  .add(Projections.property("doi"))
                  .add(Projections.property("title"))),
          0, 1).get(0);

    } catch (IndexOutOfBoundsException e) {
      //this should never happen
      throw new IllegalStateException("Annotation " + annotation.getID() + " pointed to an article that didn't exist;" +
          " articleID: " + annotation.getArticleID());
    }

    String articleDoi = (String) values[0];
    String articleTitle = (String) values[1];

    return buildAnnotationView(annotation, articleDoi, articleTitle, loadAllReplies);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public Long createFlag(UserProfile user, Long annotationId, FlagReasonCode reasonCode, String body) {
    if (annotationId == null) {
      throw new IllegalArgumentException("No annotation id specified");
    }
    Annotation flaggedAnnotation = (Annotation) hibernateTemplate.get(Annotation.class, annotationId);
    if (flaggedAnnotation == null) {
      throw new IllegalArgumentException("Id " + annotationId + " didn't correspond to an annotation");
    }

    log.debug("Creating flag on annotation: {} with reason code: {}", annotationId, reasonCode);
    Flag flag = new Flag(user, reasonCode, flaggedAnnotation);
    flag.setComment(body);
    return (Long) hibernateTemplate.save(flag);
  }

}