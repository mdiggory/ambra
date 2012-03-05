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

import org.ambraproject.models.UserProfile;
import org.ambraproject.permission.service.PermissionsService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Reply;
import org.topazproject.ambra.models.ReplyBlob;
import org.topazproject.ambra.models.ReplyThread;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Alex Kudlick Date: 5/4/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class ReplyServiceImpl extends BaseAnnotationServiceImpl implements ReplyService {

  private static final Logger log = LoggerFactory.getLogger(ReplyServiceImpl.class);

  private String defaultType;
  private PermissionsService permissionsService;

  /**
   * Set the default annotation type.
   *
   * @param defaultType defaultType
   */
  public void setDefaultType(String defaultType) {
    this.defaultType = defaultType;
  }

  @Required
  public void setPermissionsService(PermissionsService ps) {
    this.permissionsService = ps;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public String createReply(String root, String inReplyTo, String title, String mimeType, String body, String ciStatement, UserProfile user) throws Exception {
    permissionsService.checkLogin(user.getAuthId());

    final String contentType = getContentType(mimeType);
    String userId = user.getAccountUri();
    ReplyBlob blob = new ReplyBlob(contentType);
    blob.setCIStatement(ciStatement);
    blob.setBody(body.getBytes(getEncodingCharset()));

    //TODO: Topaz was creating ReplyThread objects, so we are here, but maybe that's actually a bug?
    final Reply reply = new ReplyThread();
    reply.setMediator(getApplicationId());
    reply.setType(defaultType);
    reply.setRoot(root);
    reply.setInReplyTo(inReplyTo);
    reply.setTitle(title);
    reply.setCreator(userId);
    reply.setBody(blob);
    reply.setCreated(new Date());

    String newId = hibernateTemplate.save(reply).toString();

    return newId;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void deleteReplies(final String root, final String authId, final String inReplyTo) throws SecurityException {

    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        final List<Reply> replies = session.createCriteria(Reply.class)
            .add(Restrictions.eq("root", root))
            .add(Restrictions.eq("inReplyTo", inReplyTo))
            .list();

        for (Reply r : replies)
          deleteReplies(r.getId().toString(), authId);

        return null;
      }
    });
  }

  @Override
  public void deleteReplies(String target, String authId) throws SecurityException {
    if (log.isDebugEnabled()) {
      log.debug("deleting reply and descendants with id: " + target);
    }
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    final List<Reply> replies = new ArrayList<Reply>();
    final ReplyThread root = (ReplyThread) hibernateTemplate.get(ReplyThread.class, URI.create(target));

    if (root != null) {
      addAllDescendants(replies, root);
    }

    for (Reply r : replies) {
      hibernateTemplate.delete(r);
    }
  }

  /**
   * Recursively add descendants of the given thread to the given list
   *
   * @param replyList - the list to add replies to
   * @param root      - the root thread to use to add descendants to the list
   */
  private void addAllDescendants(List<Reply> replyList, ReplyThread root) {
    replyList.add(root);

    for (ReplyThread t : root.getReplies())
      addAllDescendants(replyList, t);
  }

  @Override
  public List<Reply> getReplies(List<String> replyIds) {
    List<Reply> replies = new ArrayList<Reply>();

    for (String id : replyIds) {
      try {
        replies.add(getReply(id));
      } catch (IllegalArgumentException iae) {
        if (log.isDebugEnabled())
          log.debug("Ignored illegal reply id:" + id);
      } catch (SecurityException se) {
        if (log.isDebugEnabled())
          log.debug("Filtering URI " + id + " from Reply list due to PEP SecurityException", se);
      }
    }
    return replies;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Reply> getReplies(final Date startDate, final Date endDate,
      final Set<String> annotTypes, final int maxResults) throws ParseException, URISyntaxException {
    //We want all replies between the dates whose "root" property is an article annotation of one of the given types
    //Query for "roots"
    return (List<Reply>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<String> rootList = new ArrayList<String>();

        for (Class annotClass : classesForTypes(annotTypes)) {
          //Need to convert ids to strings,
          // since Annotations have URI ids and Replies have String root property ... sigh
          List uriList = session.createCriteria(annotClass)
              .setProjection(Projections.id()).list();
          for (Object o : uriList) {
            rootList.add(o.toString());
          }
        }

        //Create the criteria for replies
        Criteria criteria = session.createCriteria(Reply.class)
            .add(Restrictions.between("created", startDate, endDate))
            .add(Restrictions.in("root", rootList))
            .addOrder(Order.desc("created"));

        if (maxResults > 0) {
          criteria.setMaxResults(maxResults);
        }

        List<Reply> results = criteria.list();
        List<Reply> filteredResults = new ArrayList<Reply>(results.size());
        //Need to filter the replies that you don't have permissions to list
        for (Reply reply : results) {
          try {
            filteredResults.add(reply);
          } catch (SecurityException se) {
            if (log.isDebugEnabled())
              log.debug("Filtering reply " + reply.getId() + " from Annotation list due to PEP SecurityException", se);
          }
        }
        return filteredResults;
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Reply getReply(final String replyId) throws SecurityException, IllegalArgumentException {
    return (Reply)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        try {
          //Could be a reply or replythread, so create a criteria
          return (Reply) session.createCriteria(Reply.class)
              .add(Restrictions.eq("id", URI.create(replyId)))
              .list()
              .get(0);
        } catch (IndexOutOfBoundsException e) {
          //This means the list was empty
          throw new IllegalArgumentException("ID: " + replyId + " didn't correspond to a stored reply");
        }
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Reply[] listReplies(final String root, final String inReplyTo) throws SecurityException {
    return (Reply[])hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<Reply> replies = session.createCriteria(Reply.class)
            .add(Restrictions.eq("root", root))
            .add(Restrictions.eq("inReplyTo", inReplyTo))
            .list();

        List<Reply> filteredReplies = new ArrayList<Reply>(replies.size());

        for (Reply reply : replies) {
          try {
            filteredReplies.add(reply);
          } catch (Throwable t) {
            if (log.isDebugEnabled())
              log.debug("no permission for viewing reply " + reply.getId() +
                  " and therefore removed from list");
          }
        }
        return filteredReplies.toArray(new Reply[filteredReplies.size()]);
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Reply[] listAllReplies(final String root, final String inReplyTo) throws SecurityException {
    return (Reply[])hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<Reply> allReplies = new ArrayList<Reply>();

        if (root.equals(inReplyTo)) {
          allReplies = session.createCriteria(Reply.class)
              .add(Restrictions.eq("root", root))
              .addOrder(Order.asc("created"))
              .list();
        } else {
          //Get all replies that have the right 'root' property
          List<Reply> rootReplies = session.createCriteria(Reply.class)
              .add(Restrictions.eq("root", root))
              .add(Restrictions.eq("inReplyTo", inReplyTo))
              .list();

          //Recursively add all reply chains
          for (Reply reply : rootReplies) {
            allReplies.addAll(recursivelyWalkReplyChain(reply, root));
          }
        }

        return allReplies.toArray(new Reply[allReplies.size()]);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private List<Reply> recursivelyWalkReplyChain(final Reply reply, final String root) {
    return (List<Reply>)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        List<Reply> replies = new ArrayList<Reply>();

        replies.add(reply);

        List<Reply> nextLevel = session.createCriteria(Reply.class)
            .add(Restrictions.eq("inReplyTo", reply.getId().toString()))
            .add(Restrictions.eq("root", root))
            .list();

        for (Reply r : nextLevel) {
          replies.addAll(recursivelyWalkReplyChain(r, root));
        }

        return replies;
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Reply[] listReplies(final String mediator, final int state) throws SecurityException {
    return (Reply[])hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria criteria = session.createCriteria(Reply.class);

        if (mediator != null)
          criteria.add(Restrictions.eq("mediator", mediator));

        if (state == 0)
          criteria.add(Restrictions.ne("state", 0));
        else
          criteria.add(Restrictions.eq("state", state));

        List<Reply> replies = criteria.list();

        return replies.toArray(new Reply[replies.size()]);
      }
    });
  }
}
