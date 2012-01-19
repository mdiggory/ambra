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

package org.ambraproject.admin.service.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.ApplicationException;
import org.ambraproject.admin.service.FlagManagementService;
import org.ambraproject.admin.service.FlaggedCommentRecord;
import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.Flag;
import org.ambraproject.annotation.service.ReplyService;
import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.Annotea;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.Reply;
import org.ambraproject.service.HibernateServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manage flagged annotations on the server. This allows for administration follow-up of community
 * feedback/comments etc.
 *
 * @author alan
 * @author Joe Osowski
 */
public class FlagManagementServiceImpl extends HibernateServiceImpl implements FlagManagementService {
  private static final Logger log = LoggerFactory.getLogger(FlagManagementServiceImpl.class);
  private AnnotationService annotationService;
  private ReplyService replyService;
  private AnnotationConverter converter;

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Collection<FlaggedCommentRecord> getFlaggedComments() throws ApplicationException {
    List<Object[]> results = (List<Object[]>) hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        //SELECT a.* FROM Annotation a join Annotation b on a.annotates = b.annotationUri
        //This is a little weird, but flags are created as annotations on annotations.
        //So we just do a recursive join to find flags
        return session.createSQLQuery("SELECT a.annotationUri, a.annotates " +
          "FROM Annotation a join Annotation b on a.annotates = b.annotationUri " +
          "AND b.Class in ('Comment','MinorCorrection','Retraction', 'FormalCorrection', 'Rating')" +
          "UNION SELECT a.annotationUri, a.annotates " +
          "FROM Annotation a join Reply b on a.annotates = b.replyUri").list();
      }
    });
    ArrayList<FlaggedCommentRecord> commentrecords = new ArrayList<FlaggedCommentRecord>();

    for (Object[] row : results) {
      String id = (String)row[0];
      Flag flag = null;
      try {
        ArticleAnnotation ann = annotationService.getArticleAnnotation(id);
        if (ann != null)
          flag = new Flag(converter.convert(ann, true, true));
      } catch (SecurityException e) {
        if (log.isInfoEnabled())
          log.info("No permission to load Flag: " + id, e);
      }

      if (flag == null)
        continue;

      Annotea<?> a = null;

      //A bit of a kludge, but we need to identify if the current record is a reply and query for the
      //record differently
      if(((String)row[1]).indexOf("/reply/") > 0) {
        a = (Annotea<?>) replyService.getReply((String) row[1]);
      } else {
        a = (Annotea<?>) annotationService.getAnnotation((String)row[1]);
      }

      String title = (a instanceof Rating) ? ((Rating)a).getBody().getCommentTitle() : a.getTitle();
      String root  = (a instanceof Reply) ? ((Reply)a).getRoot() : null;
      String wt    = a.getWebType();

      boolean isGeneralComment = (a instanceof Annotation) && ((Annotation)a).getContext() == null;

      String reasonCode;
      String commentBody;
      boolean isBroken = false;
      try {
        reasonCode = flag.getReasonCode();
      } catch (ApplicationException e) {
        isBroken = true;
        reasonCode = "-missing-";
        log.error("Error parsing reason code for flag " + flag.getId(), e);
      }

      try {
        commentBody = flag.getComment();
      } catch (ApplicationException e) {
        isBroken = true;
        commentBody = "-missing-";
        log.error("Error parsing comment for flag " + flag.getId(), e);
      }

      FlaggedCommentRecord fcr = new FlaggedCommentRecord(flag.getId(), flag.getAnnotates(), title,
          commentBody, flag.getCreated(), flag.getCreatorName(), flag.getCreator(),
          root, reasonCode, wt, isGeneralComment, isBroken);
      commentrecords.add(fcr);
    }

    Collections.sort(commentrecords);
    return commentrecords;
  }

  public void setReplyService(ReplyService replyService) {
    this.replyService = replyService;
  }

  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  @Required
  public void setAnnotationConverter(AnnotationConverter converter) {
    this.converter = converter;
  }
}
