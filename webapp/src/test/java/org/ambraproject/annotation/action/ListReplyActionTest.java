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
import org.ambraproject.models.AnnotationCitation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.CorrectedAuthor;
import org.ambraproject.models.UserProfile;
import org.ambraproject.views.AnnotationCitationView;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.AuthorView;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 3/12/12
 */
public class ListReplyActionTest extends BaseWebTest {

  @Autowired
  protected ListReplyAction action;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }

  @Test
  public void testExecute() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForListReplyActionTest",
        "email@ListReplyActionTest.org",
        "displayNameForListReplyActionTest");
    dummyDataStore.store(creator);
    Article article = new Article("id:doi-for-listReplyActionTest");
    article.setTitle("test title for article");
    dummyDataStore.store(article);

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, article.getID());
    annotation.setTitle("Great Article");
    annotation.setBody("This is what I've been waiting for.");
    dummyDataStore.store(annotation);

    Annotation reply = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply.setTitle("That's Wrong!");
    reply.setBody("You should never end a sentence with a preposition.");
    reply.setParentID(annotation.getID());
    dummyDataStore.store(reply);

    action.setRoot(annotation.getID());
    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 0,
        "Action returned field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));

    AnnotationView baseAnnotation = action.getBaseAnnotation();
    assertNotNull(baseAnnotation, "action had null base annotation");
    assertEquals(baseAnnotation.getTitle(), annotation.getTitle(), "Base annotation had incorrect title");
    assertEquals(baseAnnotation.getBody(), "<p>" + annotation.getBody() + "</p>", "base annotation had incorrect body");
    assertNull(baseAnnotation.getCitation(), "base annotation had a citation when none was expected");
    assertFalse(baseAnnotation.isCorrection(),"Returned annotation labeled as a correction when it wasn't");

    assertNotNull(baseAnnotation.getReplies(), "Action had null list of replies");
    assertEquals(baseAnnotation.getReplies().length, 1, "Action had incorrect number of replies");
    assertEquals(baseAnnotation.getReplies()[0].getTitle(), reply.getTitle(), "reply had incorrect title");
    assertEquals(baseAnnotation.getReplies()[0].getBody(), "<p>" + reply.getBody() + "</p>", "reply had incorrect body");
    assertNotNull(action.getArticleInfo(), "Action had null article info");
    assertEquals(action.getArticleInfo().getDoi(), article.getDoi(), "action had incorrect article doi");
    assertEquals(action.getArticleInfo().getTitle(), article.getTitle(), "action had incorrect article doi");
  }

  @Test
  public void testExecuteWithCorrection() throws Exception {
    UserProfile creator = new UserProfile(
        "authIdForListReplyActionTestWithCorrection",
        "email@ListReplyActionTestWithCorrection.org",
        "displayNameForListReplyActionTestWithCorrection");
    dummyDataStore.store(creator);
    Article article = new Article("id:doi-for-listReplyActionTestWithCorrection");
    article.setTitle("test title");
    article.setJournal("test journal");
    article.seteLocationId("2139-12385");
    article.setAuthors(new ArrayList<ArticleAuthor>(2));
    article.getAuthors().add(new ArticleAuthor("John", "Smith", "M.D."));
    article.getAuthors().add(new ArticleAuthor("Harvey", "Weinstein", "Ph.D."));
    article.setCollaborativeAuthors(Arrays.asList("foo", "bar"));
    dummyDataStore.store(article);

    Annotation annotation = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, article.getID());
    annotation.setTitle("Correction");
    annotation.setBody("This article contained sentences in which a preposition was the last word.");
    dummyDataStore.store(annotation);

    annotation.setAnnotationCitation(new AnnotationCitation(article));
    dummyDataStore.update(annotation);

    action.setRoot(annotation.getID());
    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 0,
        "Action returned field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));

    AnnotationView baseAnnotation = action.getBaseAnnotation();
    assertNotNull(baseAnnotation, "action had null base annotation");
    assertEquals(baseAnnotation.getTitle(), "Formal Correction: " + annotation.getTitle(), "Base annotation had incorrect title");
    assertEquals(baseAnnotation.getBody(), "<p>" + annotation.getBody() + "</p>", "base annotation had incorrect body");
    assertTrue(baseAnnotation.isCorrection(), "annotation should have been labeled as a correction");

    assertNotNull(baseAnnotation.getReplies(), "Action had null list of replies");
    assertEquals(baseAnnotation.getReplies().length, 0, "Action had incorrect number of replies");

    AnnotationCitationView actualCitation = baseAnnotation.getCitation();
    AnnotationCitation expectedCitation = annotation.getAnnotationCitation();
    assertNotNull(actualCitation, "Action returned null citation");
    assertEquals(actualCitation.getTitle(), expectedCitation.getTitle(),
        "Action's citation had incorrect title");
    assertEquals(actualCitation.getJournal(), expectedCitation.getJournal(),
        "Action's citation had incorrect journal");
    assertEquals(actualCitation.geteLocationId(), expectedCitation.getELocationId(),
        "Action's citation had incorrect eLocationId");
    assertEquals(actualCitation.getCollabAuthors(), expectedCitation.getCollaborativeAuthors().toArray(),
        "Action's citation had incorrect collab authors");
    assertNotNull(actualCitation.getAuthors(), "Action's citation had no authors");
    assertEquals(actualCitation.getAuthors().length, expectedCitation.getAuthors().size(),
        "Action's citation had incorrect number of authors");
    for (int i = 0; i < actualCitation.getAuthors().length; i++) {
      AuthorView actualAuthor = actualCitation.getAuthors()[i];
      CorrectedAuthor expectedAuthor = expectedCitation.getAuthors().get(i);
      assertEquals(actualAuthor.getGivenNames(), expectedAuthor.getGivenNames(),
          "Author " + (i + 1) + " had incorrect given names");
      assertEquals(actualAuthor.getSurnames(), expectedAuthor.getSurName(),
          "Author " + (i + 1) + " had incorrect surname");
      assertEquals(actualAuthor.getSuffix(), expectedAuthor.getSuffix(),
          "Author " + (i + 1) + " had incorrect suffix");
    }
  }
}
