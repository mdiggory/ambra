/* $HeadURL$
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

package org.ambraproject.annotation.service;

import org.ambraproject.BaseTest;
import org.ambraproject.annotation.Context;
import org.ambraproject.cache.Cache;
import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationCitation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.Flag;
import org.ambraproject.models.FlagReasonCode;
import org.ambraproject.models.Rating;
import org.ambraproject.models.UserProfile;
import org.ambraproject.views.AnnotationView;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Test for methods of {@link AnnotationService}.  The test just references methods of the interface so that different
 * implementations can be autowired in.  The test creates it's own dummy data and so shouldn't be dependent on external
 * databases
 *
 * @author Alex Kudlick
 */
public class AnnotationServiceTest extends BaseTest {

  @Autowired
  protected AnnotationService annotationService;

  @Autowired
  protected Cache articleHtmlCache; //just used to check that articles get kicked out of the cache when notes are created

  @DataProvider(name = "articleAnnotations")
  public Object[][] getArticleAnnotations() {
    Article article = new Article("id:doi-for-annotationsTopLevel-test");
    article.setJournal("test journal");
    article.setTitle("test article title");
    article.seteLocationId("1234");
    article.setAuthors(Arrays.asList(new ArticleAuthor("article", "author", "1"), new
        ArticleAuthor("article", "author", "2")));
    article.setDate(Calendar.getInstance().getTime());
    article.setCollaborativeAuthors(Arrays.asList("Collab Author 1", "Collab Author 2", "Collab Author 3"));
    dummyDataStore.store(article);

    UserProfile creator = new UserProfile(
        "user-1-annotationsTopLevel",
        "email@annotationsTopLevel.org",
        "user-1-annotationsTopLevel");
    dummyDataStore.store(creator);

    //dates to be able to differentiate expected order of results
    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);
    Calendar lastMonth = Calendar.getInstance();
    lastMonth.add(Calendar.MONTH, -1);

    Map<Long, List<Annotation>> replyMap = new HashMap<Long, List<Annotation>>(2);

    /**********ANNOTATIONS ON THE FIRST ARTICLE***********/
    /******************RETURN ORDER SHOULD BE mCorrection, fCorrection****************************/
    //fCorrection created is last month, mCorrection created is last year, but the replies to mCorrection were created now
    Annotation fCorrection = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, article.getID());
    fCorrection.setAnnotationUri("formal-correction-annotationsTopLevel-fc1");
    fCorrection.setTitle("formal-correction-annotationsTopLevel-fc1");
    fCorrection.setBody("formal-correction-annotationsTopLevel-body-fc1");
    fCorrection.setAnnotationCitation(new AnnotationCitation(article));
    fCorrection.setCreated(lastMonth.getTime());
    dummyDataStore.store(fCorrection);

    Annotation mCorrection = new Annotation(creator, AnnotationType.MINOR_CORRECTION, article.getID());
    mCorrection.setAnnotationUri("minor-correction-annotationsTopLevel-mc1");
    mCorrection.setTitle("minor-correction-annotationsTopLevel-mc1");
    mCorrection.setBody("minor-correction-annotationsTopLevel-body-mc1");
    mCorrection.setAnnotationCitation(new AnnotationCitation(article));
    mCorrection.setCreated(lastYear.getTime());
    dummyDataStore.store(mCorrection);

    Annotation mCorrectionReply1 = new Annotation(creator, AnnotationType.REPLY, mCorrection.getArticleID());
    mCorrectionReply1.setParentID(mCorrection.getID());
    mCorrectionReply1.setAnnotationUri("reply1-to-minor-correction-uri");
    mCorrectionReply1.setTitle("reply1-to-minor-correction-title");
    mCorrectionReply1.setBody("reply1-to-minor-correction-body");
    dummyDataStore.store(mCorrectionReply1);

    Annotation mCorrectionReply2 = new Annotation(creator, AnnotationType.REPLY, mCorrection.getArticleID());
    mCorrectionReply2.setParentID(mCorrection.getID());
    mCorrectionReply2.setAnnotationUri("reply2-to-minor-correction-uri");
    mCorrectionReply2.setTitle("reply2-to-minor-correction-title");
    mCorrectionReply2.setBody("reply2-to-minor-correction-body");
    dummyDataStore.store(mCorrectionReply2);
    replyMap.put(mCorrection.getID(), Arrays.asList(mCorrectionReply1, mCorrectionReply2));

    Article article1 = new Article("id:doi-for-annotationsTopLevel-test-1");
    article1.setJournal("test journal");
    article1.seteLocationId("1234");
    article1.setTitle("test article1 title");
    article1.setAuthors(Arrays.asList(new ArticleAuthor("article", "author", "1"), new
        ArticleAuthor("article", "author", "2")));
    article1.setDate(Calendar.getInstance().getTime());
    article1.setCollaborativeAuthors(Arrays.asList("Collab Author 1", "Collab Author 2", "Collab Author 3"));
    dummyDataStore.store(article1);

    /**********ANNOTATIONS ON THE SECOND ARTICLE***********/
    /**********RETURN ORDER SHOULD BE fCorrection1, fCorrection2**/
    //fCorrection1 and it's reply have created date last year, but the reply to reply date is now
    //fCorrection2 and it's reply have created date last month
    Annotation fCorrection1 = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, article1.getID());
    fCorrection1.setAnnotationUri("formal-correction-1-annotationsTopLevel-fc2");
    fCorrection1.setTitle("formal-correction-1-annotationsTopLevel-fc2");
    fCorrection1.setBody("formal-correction-1-annotationsTopLevel-body-fc2");
    fCorrection1.setAnnotationCitation(new AnnotationCitation(article1));
    fCorrection1.setCreated(lastMonth.getTime());
    dummyDataStore.store(fCorrection1);

    Annotation fCorrection1Reply1 = new Annotation(creator, AnnotationType.REPLY, fCorrection1.getArticleID());
    fCorrection1Reply1.setParentID(fCorrection1.getID());
    fCorrection1Reply1.setAnnotationUri("reply1-to-formal-correction1-uri");
    fCorrection1Reply1.setTitle("reply1-to-formal-correction1-title");
    fCorrection1Reply1.setBody("reply1-to-formal-correction1-body");
    fCorrection1Reply1.setCreated(lastMonth.getTime());
    dummyDataStore.store(fCorrection1Reply1);

    Annotation fCorrection1ReplyToReply = new Annotation(creator, AnnotationType.REPLY, fCorrection1.getArticleID());
    fCorrection1ReplyToReply.setParentID(fCorrection1Reply1.getID());
    fCorrection1ReplyToReply.setAnnotationUri("reply-to-reply-to-formal-correction1-uri");
    fCorrection1ReplyToReply.setTitle("reply-to-reply-to-formal-correction1-title");
    fCorrection1ReplyToReply.setBody("reply-to-reply-to-formal-correction1-body");
    dummyDataStore.store(fCorrection1ReplyToReply);

    replyMap.put(fCorrection1.getID(), Arrays.asList(fCorrection1Reply1));
    replyMap.put(fCorrection1Reply1.getID(), Arrays.asList(fCorrection1ReplyToReply));

    Annotation fCorrection2 = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, article1.getID());
    fCorrection2.setAnnotationUri("formal-correction-1-annotationsTopLevel-fc3");
    fCorrection2.setTitle("formal-correction-1-annotationsTopLevel-fc3");
    fCorrection2.setBody("formal-correction-1-annotationsTopLevel-body-fc3");
    fCorrection2.setAnnotationCitation(new AnnotationCitation(article1));
    fCorrection2.setCreated(lastYear.getTime());
    dummyDataStore.store(fCorrection2);

    Annotation fCorrection2Reply1 = new Annotation(creator, AnnotationType.REPLY, fCorrection1.getArticleID());
    fCorrection2Reply1.setParentID(fCorrection2.getID());
    fCorrection2Reply1.setAnnotationUri("reply1-to-formal-correction2-uri");
    fCorrection2Reply1.setTitle("reply1-to-formal-correction2-title");
    fCorrection2Reply1.setBody("reply1-to-formal-correction2-body");
    fCorrection2Reply1.setCreated(lastMonth.getTime());
    dummyDataStore.store(fCorrection2Reply1);

    replyMap.put(fCorrection2.getID(), Arrays.asList(fCorrection2Reply1));


    Article article2 = new Article("id:doi-for-annotationsTopLevel-test-2");
    article2.setJournal("test journal");
    article2.setTitle("test article2 title");
    article2.seteLocationId("1234");
    article2.setAuthors(Arrays.asList(new ArticleAuthor("article", "author", "1"), new
        ArticleAuthor("article", "author", "2")));
    article2.setDate(Calendar.getInstance().getTime());
    article2.setCollaborativeAuthors(Arrays.asList("Collab Author 1", "Collab Author 2", "Collab Author 3"));
    dummyDataStore.store(article2);

    UserProfile creator2 = new UserProfile(
        "user-2-annotationsTopLevel",
        "email2@annotationsTopLevel.org",
        "user-2-annotationsTopLevel");
    dummyDataStore.store(creator2);

    /**********ANNOTATIONS ON THE THIRD ARTICLE***********/
    /**********RETURN ORDER SHOULD BE comment, retraction*******/
    Annotation retraction = new Annotation(creator2, AnnotationType.RETRACTION, article2.getID());
    retraction.setAnnotationUri("retraction-1-annotationsTopLevel-r1");
    retraction.setTitle("retraction-1-annotationsTopLevel-r1");
    retraction.setBody("retraction-1-annotationsTopLevel-body-r1");
    retraction.setAnnotationCitation(new AnnotationCitation(article2));
    retraction.setCreated(lastYear.getTime());
    dummyDataStore.store(retraction);

    Annotation comment = new Annotation(creator2, AnnotationType.COMMENT, article2.getID());
    comment.setAnnotationUri("comment-annotationsTopLevel-r1");
    comment.setTitle("comment-annotationsTopLevel-r1");
    comment.setBody("comment-annotationsTopLevel-body-r1");
    comment.setCreated(lastMonth.getTime());
    dummyDataStore.store(comment);

    AnnotationView fCorrectionView = new AnnotationView(fCorrection, article.getDoi(), article.getTitle(), replyMap);
    AnnotationView mCorrectionView = new AnnotationView(mCorrection, article.getDoi(), article.getTitle(), replyMap);
    AnnotationView fCorrection1View = new AnnotationView(fCorrection1, article1.getDoi(), article1.getTitle(), replyMap);
    AnnotationView fCorrection2View = new AnnotationView(fCorrection2, article1.getDoi(), article1.getTitle(), replyMap);
    AnnotationView retractionView = new AnnotationView(retraction, article2.getDoi(), article2.getTitle(), replyMap);
    AnnotationView commentView = new AnnotationView(comment, article2.getDoi(), article2.getTitle(), replyMap);
    //Pass in query parameters and expected results for each set of articles / citations
    return new Object[][]{
        {
            article,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{
                mCorrectionView, //mCorrection has a more recent reply
                fCorrectionView
            },
            replyMap
        },
        {
            article,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION),
            AnnotationService.AnnotationOrder.OLDEST_TO_NEWEST,
            new AnnotationView[]{
                mCorrectionView, //mCorrection has created date last year, fCorrection last month
                fCorrectionView
            },
            replyMap
        },
        {
            article1,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{
                fCorrection1View, //fCorrection1 has a more recent reply to reply
                fCorrection2View
            },
            replyMap
        },
        {
            article1,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION),
            AnnotationService.AnnotationOrder.OLDEST_TO_NEWEST,
            new AnnotationView[]{
                fCorrection2View, //fCorrection2 has created date last year
                fCorrection1View
            },
            replyMap
        },
        {
            article1,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{
                fCorrection1View, //fCorrection1 has a more recent reply to reply
                fCorrection2View
            },
            replyMap
        },
        {
            article,
            EnumSet.of(AnnotationType.RETRACTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{},
            replyMap
        },
        {
            article2,
            EnumSet.of(AnnotationType.RETRACTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{
                retractionView
            },
            replyMap
        },
        {
            article2,
            EnumSet.of(AnnotationType.RETRACTION, AnnotationType.COMMENT),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{
                commentView,  //comment has more recent created date
                retractionView
            },
            replyMap
        },
        {
            article2,
            EnumSet.of(AnnotationType.RETRACTION, AnnotationType.COMMENT),
            AnnotationService.AnnotationOrder.OLDEST_TO_NEWEST,
            new AnnotationView[]{
                retractionView,
                commentView  //comment has more recent created date
            },
            replyMap
        },
        {
            article2,
            EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION),
            AnnotationService.AnnotationOrder.MOST_RECENT_REPLY,
            new AnnotationView[]{},
            replyMap
        }
    };
  }

  @Test(dataProvider = "articleAnnotations")
  public void testListAnnotations(
      final Article article,
      final Set<AnnotationType> annotationTypes,
      final AnnotationService.AnnotationOrder order,
      final AnnotationView[] expectedViews,
      final Map<Long, List<Annotation>> fullReplyMap
  ) {
    AnnotationView[] resultViews = annotationService.listAnnotations(article.getID(), annotationTypes, order);
    //Not just calling assertEquals here, so we can give a more informative message (the .toSting() on the arrays is huge)
    assertNotNull(resultViews, "returned null array of results");
    assertEquals(resultViews.length, expectedViews.length, "returned incorrect number of results");
    for (int i = 0; i < resultViews.length; i++) {
      assertEquals(resultViews[i].getCitation(), expectedViews[i].getCitation(),
          "Result " + (i + 1) + " had incorrect citation with order " + order);
      assertEquals(resultViews[i], expectedViews[i], "Result " + (i + 1) + " was incorrect with order " + order);
      recursivelyCheckReplies(resultViews[i], fullReplyMap);
    }

  }

  @Test(dataProvider = "articleAnnotations")
  public void testListAnnotationsNoReplies(
      final Article article,
      final Set<AnnotationType> annotationTypes,
      final AnnotationService.AnnotationOrder order,
      final AnnotationView[] expectedViews,
      final Map<Long, List<Annotation>> notUsed
  ) {
    if (order != AnnotationService.AnnotationOrder.MOST_RECENT_REPLY) {
      AnnotationView[] resultViews = annotationService.listAnnotationsNoReplies(article.getID(), annotationTypes, order);
      //Not just calling assertEquals here, so we can give a more informative message (the .toSting() on the arrays is huge)
      assertNotNull(resultViews, "returned null array of results");
      assertEquals(resultViews.length, expectedViews.length, "returned incorrect number of results");
      for (int i = 0; i < resultViews.length; i++) {
        assertEquals(resultViews[i].getCitation(), expectedViews[i].getCitation(),
            "Result " + (i + 1) + " had incorrect citation with order " + order);
        assertEquals(resultViews[i], expectedViews[i], "Result " + (i + 1) + " was incorrect with order " + order);
        assertTrue(ArrayUtils.isEmpty(resultViews[i].getReplies()), "returned annotation with replies loaded up");
      }
    }
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testListAnnotationNoRepliesWithBadOrderArgument() {
    annotationService.listAnnotationsNoReplies(1231l, null, AnnotationService.AnnotationOrder.MOST_RECENT_REPLY);
  }

  @Test
  public void testCreateComment() throws Exception {
    Article article = new Article("id:doiForCreateCommentByService");
    UserProfile user = new UserProfile(
        "authIdForCreateCommentService",
        "email@createCommentService.org",
        "displayNAmeForCreateCommentService");
    dummyDataStore.store(article);
    dummyDataStore.store(user);

    String body = "Test body";
    String title = "test title";
    String ciStatement = "ciStatement";
    Context context = new Context(null, 0, null, 0, article.getDoi());
    Long id = annotationService.createComment(user, article.getDoi(), title, body, ciStatement, context, false);
    assertNotNull(id, "Returned null annotation id");

    Annotation storedAnnotation = dummyDataStore.get(Annotation.class, id);
    assertNotNull(storedAnnotation, "didn't store annotation to the db");

    assertEquals(storedAnnotation.getArticleID(), article.getID(), "stored annotation had incorrect article id");
    assertEquals(storedAnnotation.getBody(), body, "stored annotation had incorrect body");
    assertEquals(storedAnnotation.getTitle(), title, "stored annotation had incorrect title");
    assertEquals(storedAnnotation.getCompetingInterestBody(), ciStatement, "stored annotation had incorrect ci statement");
    assertEquals(storedAnnotation.getType(), AnnotationType.COMMENT, "Stored annotation had incorrect type");
    assertNull(storedAnnotation.getXpath(), "Stored annotation had an xpath associated with it");
    assertNotNull(storedAnnotation.getAnnotationUri(), "Service didn't generate an annotation uri");
  }

  @Test
  public void testCreateInlineNote() throws Exception {
    Article article = new Article("id:doiForCreateInlineNoteByService");
    UserProfile user = new UserProfile(
        "authIdForCreateInlineNoteByService",
        "email@createInlineNoteByService.org",
        "displayNAmeForCreateInlineNoteByService");
    dummyDataStore.store(article);
    dummyDataStore.store(user);

    //put the article in the cache to see that it gets kicked out
    articleHtmlCache.put(article.getDoi(), new Cache.Item(article));

    String body = "Test body";
    String title = "test title";
    String ciStatement = "ciStatement";
    Context context = new Context("/article[1]/body[1]/sec[1]/p[3]", 107, "/article[1]/body[1]/sec[1]/p[3]", 640, article.getDoi());
    String expectedXpath = article.getDoi() + "#xpointer(string-range%28%2Farticle%5B1%5D%2Fbody%5B1%5D%2Fsec%5B1%5D%2Fp%5B3%5D%2C+%27%27%2C+107%2C+533%29%5B1%5D)";
    Long id = annotationService.createComment(user, article.getDoi(), title, body, ciStatement, context, false);
    assertNotNull(id, "Returned null annotation id");

    Annotation storedAnnotation = dummyDataStore.get(Annotation.class, id);
    assertNotNull(storedAnnotation, "didn't store annotation to the db");

    assertEquals(storedAnnotation.getArticleID(), article.getID(), "stored annotation had incorrect article id");
    assertEquals(storedAnnotation.getBody(), body, "stored annotation had incorrect body");
    assertEquals(storedAnnotation.getTitle(), title, "stored annotation had incorrect title");
    assertEquals(storedAnnotation.getCompetingInterestBody(), ciStatement, "stored annotation had incorrect ci statement");
    assertEquals(storedAnnotation.getType(), AnnotationType.NOTE, "Stored annotation had incorrect type");
    assertEquals(storedAnnotation.getXpath(), expectedXpath, "stored annotation had incorrect xpath associated with it");
    assertNotNull(storedAnnotation.getAnnotationUri(), "Service didn't generate an annotation uri");

    assertNull(articleHtmlCache.get(article.getDoi()), "Article didn't get kicked out of cache");
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testCreateCommentWithNullBody() throws Exception {
    Article article = new Article("id:doiForCreateWithNullBody");
    UserProfile user = new UserProfile(
        "authIdForCreateWithNullBody",
        "email@CreateWithNullBody.org",
        "displayNAmeForCreateWithNullBody");
    dummyDataStore.store(article);
    dummyDataStore.store(user);

    annotationService.createComment(user, article.getDoi(), "foo", null, "foo", null, false);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testCreateCommentWithNullUser() throws Exception {
    Article article = new Article("id:doiForCreateWithNullUser");
    dummyDataStore.store(article);

    annotationService.createComment(null, article.getDoi(), "foo", "foo", "foo", null, false);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testCreateCommentWithBadDoi() throws Exception {
    Article article = new Article("id:doiForCreateWithBadDoi");
    UserProfile user = new UserProfile(
        "authIdForCreateWithBadDoi",
        "email@CreateWithBadDoi.org",
        "displayNAmeForCreateWithBadDoi");
    dummyDataStore.store(user);

    annotationService.createComment(user, article.getDoi(), "foo", "foo", "foo", null, false);
  }

  @Test
  public void testCreateAndFlagAsCorrection() {
    Article article = new Article("id:doiForCreateAndFlagByService");
    UserProfile user = new UserProfile(
        "authIdForCreateAndFlagByService",
        "email@CreateAndFlagByService.org",
        "displayNAmeForCreateAndFlagByService");
    dummyDataStore.store(article);
    dummyDataStore.store(user);

    String body = "Test body";
    String title = "test title";
    String ciStatement = "ciStatement";
    Context context = new Context("/article[1]/body[1]/sec[1]/p[3]", 107, "/article[1]/body[1]/sec[1]/p[3]", 640, article.getDoi());
    String expectedXpath = article.getDoi() + "#xpointer(string-range%28%2Farticle%5B1%5D%2Fbody%5B1%5D%2Fsec%5B1%5D%2Fp%5B3%5D%2C+%27%27%2C+107%2C+533%29%5B1%5D)";

    Long id = annotationService.createComment(user, article.getDoi(), title, body, ciStatement, context, true);
    assertNotNull(id, "Returned null annotation id");

    Annotation storedAnnotation = dummyDataStore.get(Annotation.class, id);
    assertNotNull(storedAnnotation, "didn't store annotation to the db");

    assertEquals(storedAnnotation.getArticleID(), article.getID(), "stored annotation had incorrect article id");
    assertEquals(storedAnnotation.getBody(), body, "stored annotation had incorrect body");
    assertEquals(storedAnnotation.getTitle(), title, "stored annotation had incorrect title");
    assertEquals(storedAnnotation.getCompetingInterestBody(), ciStatement, "stored annotation had incorrect ci statement");
    assertEquals(storedAnnotation.getType(), AnnotationType.NOTE, "Stored annotation had incorrect type");
    assertEquals(storedAnnotation.getXpath(), expectedXpath, "stored annotation had incorrect xpath associated with it");
    assertNotNull(storedAnnotation.getAnnotationUri(), "Service didn't generate an annotation uri");

    List<Flag> allFlags = dummyDataStore.getAll(Flag.class);
    assertTrue(allFlags.size() > 0, "didn't create a flag for comment");
    boolean foundFlag = false;
    for (Flag flag : allFlags) {
      if (flag.getFlaggedAnnotation().getID().equals(id)) {
        foundFlag = true;
        assertEquals(flag.getReason(), FlagReasonCode.CORRECTION, "Flag had incorrect reason code");
      }
    }
    assertTrue(foundFlag, "Didn't create a flag for annotation");
  }

  @DataProvider(name = "storedAnnotation")
  public Object[][] getStoredAnnotation() {
    Article article = new Article("id:doi-for-annotationService-test");
    article.setJournal("test journal");
    article.seteLocationId("12340-37951");
    article.setVolume("articl volume");
    article.setTitle("article test title");
    article.setIssue("article issue");
    article.setDate(Calendar.getInstance().getTime());
    article.setCollaborativeAuthors(Arrays.asList("The Skoll Foundation", "The Bill and Melinda Gates Foundation"));
    dummyDataStore.store(article);
    UserProfile creator = new UserProfile(
        "authIdForAnnotationService",
        "email@annotationService.org",
        "displayNameForAnnotationService");
    dummyDataStore.store(creator);

    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, article.getID());
    annotation.setTitle("test title for annotation service test");
    annotation.setBody("test body for annotation service test");
    annotation.setXpath("test xpath");
    annotation.setAnnotationUri("id:annotationWithReplies");
    annotation.setAnnotationCitation(new AnnotationCitation(article));
    annotation.getAnnotationCitation().setSummary("Citation summary");
    annotation.getAnnotationCitation().setNote("Citation note");
    dummyDataStore.store(annotation.getAnnotationCitation());
    dummyDataStore.store(annotation);

    /*
       the reply tree structure is ...
                   original
                    /       \
                reply1   reply2
                           /        \
                         reply3  reply4
                                       \
                                       reply5
    */
    Map<Long, List<Annotation>> replies = new HashMap<Long, List<Annotation>>(3);

    List<Annotation> repliesToOriginal = new ArrayList<Annotation>(3);
    List<Annotation> repliesToReply2 = new ArrayList<Annotation>(2);
    List<Annotation> repliesToReply4 = new ArrayList<Annotation>(1);

    replies.put(annotation.getID(), repliesToOriginal);
    //set created dates for replies so we can check ordering by date
    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    //Reply to the original annotation
    Annotation reply1 = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply1.setParentID(annotation.getID());
    reply1.setTitle("title for reply to original comment");
    reply1.setBody("body for reply to original comment");
    reply1.setAnnotationUri("id:reply1ForTestAnnotationView");
    dummyDataStore.store(reply1);
    repliesToOriginal.add(reply1);

    //Reply to the original annotation. This should show up first since it has last year as created date
    Annotation reply2 = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply2.setParentID(annotation.getID());
    reply2.setTitle("title for 2nd reply to original comment");
    reply2.setBody("body for 2nd reply to original comment");
    reply2.setAnnotationUri("id:reply2ForTestAnnotationView");
    reply2.setCreated(lastYear.getTime());
    dummyDataStore.store(reply2);
    repliesToOriginal.add(reply2);

    replies.put(reply2.getID(), repliesToReply2);
    //reply to reply2
    Annotation reply3 = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply3.setParentID(reply2.getID());
    reply3.setTitle("title for 1st reply to reply");
    reply3.setBody("body for 1st reply to reply");
    reply3.setAnnotationUri("id:reply3ForTestAnnotationView");
    dummyDataStore.store(reply3);
    repliesToReply2.add(reply3);

    //reply to reply2. This should show up first since it has last year as created date
    Annotation reply4 = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply4.setParentID(reply2.getID());
    reply4.setTitle("title for 2nd reply to reply");
    reply4.setBody("body for 2nd reply to reply");
    reply4.setAnnotationUri("id:reply4ForTestAnnotationView");
    reply4.setCreated(lastYear.getTime());
    dummyDataStore.store(reply4);
    repliesToReply2.add(reply4);

    replies.put(reply4.getID(), repliesToReply4);

    //reply to reply4
    Annotation reply5 = new Annotation(creator, AnnotationType.REPLY, article.getID());
    reply5.setParentID(reply4.getID());
    reply5.setTitle("title for 3rd level reply");
    reply5.setBody("body for 3rd level reply");
    reply5.setAnnotationUri("id:reply5ForTestAnnotationView");
    dummyDataStore.store(reply5);
    repliesToReply4.add(reply5);


    return new Object[][]{
        {annotation, replies},
        {reply1, replies},
        {reply2, replies},
        {reply3, replies},
        {reply4, replies}
    };
  }


  @Test(dataProvider = "storedAnnotation")
  public void testGetFullAnnotationView(Annotation annotation, Map<Long, List<Annotation>> fullReplyMap) {
    AnnotationView result = annotationService.getFullAnnotationView(annotation.getID());
    assertNotNull(result, "Returned null annotation view");

    String expectedDoi = dummyDataStore.get(Article.class, annotation.getArticleID()).getDoi();
    String expectedTitle = dummyDataStore.get(Article.class, annotation.getArticleID()).getTitle();

    assertEquals(result.getArticleDoi(), expectedDoi, "AnnotationView had incorrect article doi");
    assertEquals(result.getArticleTitle(), expectedTitle, "AnnotationView had incorrect article title");

    checkAnnotationProperties(result, annotation);
    recursivelyCheckReplies(result, fullReplyMap);
    //check ordering of replies
    for (int i = 0; i < result.getReplies().length - 1; i++) {
      assertTrue(result.getReplies()[i].getCreated().before(result.getReplies()[i + 1].getCreated()),
          "Replies were out of order for annotation " + annotation.getAnnotationUri() +
              " (should be earliest -> latest)");
    }
  }

  private void recursivelyCheckReplies(AnnotationView annotationView, Map<Long, List<Annotation>> fullReplyMap) {
    List<Annotation> expectedReplies = fullReplyMap.get(annotationView.getID());
    if (expectedReplies != null) {
      assertEquals(annotationView.getReplies().length, expectedReplies.size(), "Returned incorrect number of replies");
      for (AnnotationView actual : annotationView.getReplies()) {
        boolean foundMatch = false;

        for (Annotation expected : expectedReplies) {
          if (actual.getID().equals(expected.getID())) {
            foundMatch = true;
            assertEquals(actual.getTitle(), expected.getTitle(), "Annotation view had incorrect title");
            assertEquals(actual.getBody(), "<p>" + expected.getBody() + "</p>", "Annotation view had incorrect body");
            assertEquals(actual.getCompetingInterestStatement(),
                expected.getCompetingInterestBody() == null ? "" : expected.getCompetingInterestBody(),
                "Annotation view had incorrect ci statement");
            assertEquals(actual.getAnnotationUri(), expected.getAnnotationUri(), "Annotation view had incorrect annotation uri");
          }
        }
        assertTrue(foundMatch, "Returned unexpected reply: " + actual);
        //recursively check the replies
        recursivelyCheckReplies(actual, fullReplyMap);
      }
    } else {
      assertTrue(ArrayUtils.isEmpty(annotationView.getReplies()),
          "Returned replies when none were expected: " + Arrays.deepToString(annotationView.getReplies()));
    }
  }

  @Test
  public void testAnnotationViewEscapesHtml() {
    UserProfile creator = new UserProfile(
        "authIdForAnnotationViewEscapesHtml",
        "email@EscapesHtml.org",
        "displayNameForAnnotationViewEscapesHtml"
    );
    dummyDataStore.store(creator);
    Long articleId = Long.valueOf(dummyDataStore.store(new Article("id:doi-for-AnnotationViewEscapesViewHtml")));
    String title = "hello <p /> world!";
    String expectedTitle = "hello &lt;p /&gt; world!";
    String body = "You & I";
    String expectedBody = "<p>You &amp; I</p>";
    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    annotation.setTitle(title);
    annotation.setBody(body);
    dummyDataStore.store(annotation);

    AnnotationView result = annotationService.getFullAnnotationView(annotation.getID());
    assertNotNull(result, "returned null annotation view");
    assertEquals(result.getTitle(), expectedTitle, "AnnotationView didn't escape html in title");
    assertEquals(result.getBody(), expectedBody, "AnnotationView didn't escape html in body");
  }

  @Test
  public void testAnnotationViewCorrectionTitles() {
    UserProfile creator = new UserProfile(
        "authIdForAnnotationViewCorrectionTitles",
        "email@CorrectionTitles.org",
        "displayNameForAnnotationViewCorrectionTitles"
    );
    dummyDataStore.store(creator);

    Article article = new Article();
    article.setDoi("id:doi-for-annotationViewCorrectionsTitles");
    article.setTitle("test article");
    Long articleID = Long.valueOf(dummyDataStore.store(article));

    String title = "Dummy Title";
    Annotation formalCorrection = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, articleID);
    formalCorrection.setTitle(title);
    dummyDataStore.store(formalCorrection);

    Annotation minorCorrection = new Annotation(creator, AnnotationType.MINOR_CORRECTION, articleID);
    minorCorrection.setTitle(title);
    dummyDataStore.store(minorCorrection);

    Annotation retraction = new Annotation(creator, AnnotationType.RETRACTION, articleID);
    retraction.setTitle(title);
    dummyDataStore.store(retraction);

    AnnotationView formalCorrectionView = annotationService.getFullAnnotationView(formalCorrection.getID());
    assertNotNull(formalCorrectionView, "returned null annotation view");
    assertEquals(formalCorrectionView.getTitle(), "Formal Correction: " + title, "Formal Correction didn't have correct title");

    AnnotationView minorCorrectionView = annotationService.getFullAnnotationView(minorCorrection.getID());
    assertNotNull(minorCorrectionView, "returned null annotation view");
    assertEquals(minorCorrectionView.getTitle(), "Minor Correction: " + title, "Minor Correction didn't have correct title");

    AnnotationView retractionView = annotationService.getFullAnnotationView(retraction.getID());
    assertNotNull(retractionView, "returned null annotation view");
    assertEquals(retractionView.getTitle(), "Retraction: " + title, "Retraction didn't have correct title");
  }

  @Test(dataProvider = "storedAnnotation")
  public void testGetBasicAnnotationViewById(Annotation annotation, Map<Long, List<Annotation>> fullReplyMap) {
    AnnotationView result = annotationService.getBasicAnnotationView(annotation.getID());
    assertNotNull(result, "Returned null annotation view");
    String expectedDoi = dummyDataStore.get(Article.class, annotation.getArticleID()).getDoi();
    String expectedTitle = dummyDataStore.get(Article.class, annotation.getArticleID()).getTitle();

    assertEquals(result.getArticleDoi(), expectedDoi, "AnnotationView had incorrect article doi");
    assertEquals(result.getArticleTitle(), expectedTitle, "AnnotationView had incorrect article title");

    checkAnnotationProperties(result, annotation);
    assertTrue(ArrayUtils.isEmpty(result.getReplies()), "Returned annotation with replies");
  }

  @Test(dataProvider = "storedAnnotation")
  public void testGetBasicAnnotationViewByURI(Annotation annotation, Map<Long, List<Annotation>> fullReplyMap) {
    AnnotationView result = annotationService.getBasicAnnotationViewByUri(annotation.getAnnotationUri());
    assertNotNull(result, "Returned null annotation view");
    String expectedDoi = dummyDataStore.get(Article.class, annotation.getArticleID()).getDoi();
    String expectedTitle = dummyDataStore.get(Article.class, annotation.getArticleID()).getTitle();

    assertEquals(result.getArticleDoi(), expectedDoi, "AnnotationView had incorrect article doi");
    assertEquals(result.getArticleTitle(), expectedTitle, "AnnotationView had incorrect article title");

    checkAnnotationProperties(result, annotation);
    assertTrue(ArrayUtils.isEmpty(result.getReplies()), "Returned annotation with replies");
  }

  @Test
  public void testCreateFlag() throws Exception {
    UserProfile creator = new UserProfile("authIdForCreateFlag", "email@createFlag.org", "displayNameForCreateFlag");
    dummyDataStore.store(creator);
    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, 123l);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    String comment = "This is spam";
    Long flagId = annotationService.createFlag(creator, annotationId, FlagReasonCode.SPAM, comment);
    assertNotNull(flagId, "returned null flag id");
    Flag storedFlag = dummyDataStore.get(Flag.class, flagId);
    assertNotNull(storedFlag, "didn't store flag to the database");
    assertEquals(storedFlag.getReason(), FlagReasonCode.SPAM, "stored flag had incorrect reason code");
    assertEquals(storedFlag.getComment(), comment, "stored flag had incorrect comment");
    assertNotNull(storedFlag.getFlaggedAnnotation(), "stored flag had null annotation");
    assertEquals(storedFlag.getFlaggedAnnotation().getID(), annotationId, "stored flag had incorrect annotation id");
  }

  @Test
  public void testCreateReply() {
    UserProfile creator = new UserProfile(
        "authIdForCreateReply",
        "email@createReply.org",
        "displayNameForCreateReply");
    dummyDataStore.store(creator);
    Article article = new Article("id:doi-for-create-reply");
    Long articleId = Long.valueOf(dummyDataStore.store(article));
    Annotation annotation = new Annotation(creator, AnnotationType.COMMENT, articleId);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    String title = "test title for reply";
    String body = "test body for reply";
    Long replyId = annotationService.createReply(creator, annotationId, title, body, null);
    assertNotNull(replyId, "returned null reply id");
    Annotation storedReply = dummyDataStore.get(Annotation.class, replyId);
    assertNotNull(storedReply, "didn't store reply to the database");
    assertEquals(storedReply.getType(), AnnotationType.REPLY, "Stored reply had incorrect type");
    assertEquals(storedReply.getParentID(), annotation.getID(), "Stored reply had incorrect parent id");
    assertEquals(storedReply.getArticleID(), annotation.getArticleID(), "stored reply had incorrect article id");
    assertNotNull(storedReply.getAnnotationUri(), "reply didn't get an annotation uri generated");
    assertEquals(storedReply.getTitle(), title, "reply had incorrect title");
    assertEquals(storedReply.getBody(), body, "reply had incorrect body");
  }

  @Test
  public void testCountComments() {
    UserProfile user = new UserProfile("authIdForTestCountComments", "email@testCountComments.org", "displayNameTestCountComments");
    dummyDataStore.store(user);
    Article article = new Article("id:doi-test-count-comments");
    dummyDataStore.store(article);

    Long commentId = Long.valueOf(dummyDataStore.store(new Annotation(user, AnnotationType.COMMENT, article.getID())));
    dummyDataStore.store(new Annotation(user, AnnotationType.FORMAL_CORRECTION, article.getID()));
    dummyDataStore.store(new Annotation(user, AnnotationType.MINOR_CORRECTION, article.getID()));
    dummyDataStore.store(new Annotation(user, AnnotationType.RETRACTION, article.getID()));
    dummyDataStore.store(new Annotation(user, AnnotationType.NOTE, article.getID()));
    dummyDataStore.store(new Rating(user, article.getID()));

    Annotation reply = new Annotation(user, AnnotationType.REPLY, article.getID());
    reply.setParentID(commentId);
    dummyDataStore.store(reply);

    assertEquals(annotationService.countAnnotations(article.getID(),
        EnumSet.of(AnnotationType.NOTE, AnnotationType.COMMENT)),
        2, "annotation service returned incorrect count of comments and notes");
    assertEquals(annotationService.countAnnotations(article.getID(),
        EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION, AnnotationType.RETRACTION)),
        3, "annotation service returned incorrect count of corrections");
    assertEquals(annotationService.countAnnotations(article.getID(), EnumSet.allOf(AnnotationType.class)),
        7, "annotation service returned incorrect count of comments and notes");
  }
}
