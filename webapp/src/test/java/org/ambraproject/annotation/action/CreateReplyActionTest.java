/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.annotation.action;

import com.opensymphony.xwork2.Action;
import org.ambraproject.BaseWebTest;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Article;
import org.ambraproject.models.UserProfile;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * @author Alex Kudlick 3/15/12
 */
public class CreateReplyActionTest extends BaseWebTest {

  @Autowired
  protected CreateReplyAction action;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }

  @Test
  public void testCreateReply() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForCreateReplyAction",
        "email@createReplyAction.org",
        "displayNameForCreateReplyAction");
    dummyDataStore.store(creator);
    login(creator);
    Article article = new Article("id:doi-for-create-reply-action");
    Long articleId = Long.valueOf(dummyDataStore.store(article));

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    String body = "test comment for reply";
    String title = "test title for reply";
    action.setInReplyTo(annotationId);
    action.setComment(body);
    action.setCommentTitle(title);
    action.setIsCompetingInterest(false);
    action.setCiStatement(null);

    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 0,
        "Action returned field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));

    assertNotNull(action.getReplyId(), "Action returned null reply id");

    Annotation storedReply = dummyDataStore.get(Annotation.class, action.getReplyId());
    assertNotNull(storedReply, "didn't store reply to the database");
    assertEquals(storedReply.getType(), AnnotationType.REPLY, "stored reply had incorrect type");
    assertEquals(storedReply.getArticleID(), articleId, "stored reply had incorrect article id");
    assertEquals(storedReply.getParentID(), annotationId, "stored reply had incorrect parent id");
    assertEquals(storedReply.getTitle(), title, "stored reply had incorrect title");
    assertEquals(storedReply.getBody(), body, "stored reply had incorrect body");
    assertNull(storedReply.getCompetingInterestBody(), "stored reply had a competing interest statement added");
  }

  @Test
  public void testCreateReplyWithCiStatement() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForCreateReplyWithCiStatementAction",
        "email@createReplyWithCiStatementAction.org",
        "displayNameForCreateReplyWithCiStatementAction");
    dummyDataStore.store(creator);
    login(creator);
    Article article = new Article("id:doi-for-create-reply-action");
    Long articleId = Long.valueOf(dummyDataStore.store(article));

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    String body = "test comment for reply";
    String title = "test title for reply";
    String ciStatement = "test competing interest statement";
    action.setInReplyTo(annotationId);
    action.setComment(body);
    action.setCommentTitle(title);
    action.setIsCompetingInterest(true);
    action.setCiStatement(ciStatement);

    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 0,
        "Action returned field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));

    assertNotNull(action.getReplyId(), "Action returned null reply id");

    Annotation storedReply = dummyDataStore.get(Annotation.class, action.getReplyId());
    assertNotNull(storedReply, "didn't store reply to the database");
    assertEquals(storedReply.getType(), AnnotationType.REPLY, "stored reply had incorrect type");
    assertEquals(storedReply.getArticleID(), articleId, "stored reply had incorrect article id");
    assertEquals(storedReply.getParentID(), annotationId, "stored reply had incorrect parent id");
    assertEquals(storedReply.getTitle(), title, "stored reply had incorrect title");
    assertEquals(storedReply.getBody(), body, "stored reply had incorrect body");
    assertEquals(storedReply.getCompetingInterestBody(), ciStatement, "stored reply had incorrect competing interest statement");
  }

  @Test
  public void testCreateReplyWithNoBody() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForCreateReplyActionNoBody",
        "email@createReplyActionNoBody.org",
        "displayNameForCreateReplyActionNoBody");
    dummyDataStore.store(creator);
    login(creator);
    Article article = new Article("id:doi-for-create-reply-action");
    Long articleId = Long.valueOf(dummyDataStore.store(article));

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    action.setInReplyTo(annotationId);
    action.setComment(null);
    action.setCommentTitle("foo");
    action.setIsCompetingInterest(false);
    action.setCiStatement(null);

    String result = action.execute();
    assertEquals(result, Action.INPUT, "Action didn't return input");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned unexpected error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 1,
        "Action returned unexpected field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));
    assertNotNull(action.getFieldErrors().get("comment"), "Action didn't return field error for comment");

  }

  @Test
  public void testCreateReplyWithNoTitle() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForCreateReplyActionNoBody",
        "email@createReplyActionNoBody.org",
        "displayNameForCreateReplyActionNoBody");
    dummyDataStore.store(creator);
    login(creator);
    Article article = new Article("id:doi-for-create-reply-action");
    Long articleId = Long.valueOf(dummyDataStore.store(article));

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    action.setInReplyTo(annotationId);
    action.setComment("foo");
    action.setCommentTitle(null);
    action.setIsCompetingInterest(false);
    action.setCiStatement(null);

    String result = action.execute();
    assertEquals(result, Action.INPUT, "Action didn't return input");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned unexpected error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 1,
        "Action returned unexpected field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));
    assertNotNull(action.getFieldErrors().get("commentTitle"), "Action didn't return field error for title");

  }

  @Test
  public void testCreateReplyWithCompetingInterestCheckedAndNoCiStatement() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForCreateReplyActionNoTitle",
        "email@createReplyActionNoTitle.org",
        "displayNameForCreateReplyActionNoTitle");
    dummyDataStore.store(creator);
    login(creator);
    Article article = new Article("id:doi-for-create-reply-action");
    Long articleId = Long.valueOf(dummyDataStore.store(article));

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    action.setInReplyTo(annotationId);
    action.setComment("foo");
    action.setCommentTitle("bar");
    action.setIsCompetingInterest(true);
    action.setCiStatement(null);

    String result = action.execute();
    assertEquals(result, Action.INPUT, "Action didn't return input");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned unexpected error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 1,
        "Action returned unexpected field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));
    assertNotNull(action.getFieldErrors().get("statement"), "Action didn't return field error for ci statement");

  }

}
