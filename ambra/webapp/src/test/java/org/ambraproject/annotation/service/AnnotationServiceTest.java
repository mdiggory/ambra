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

import org.ambraproject.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.AnnotationBlob;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Journal;
import org.topazproject.ambra.models.MinorCorrection;
import org.topazproject.ambra.models.Reply;
import org.topazproject.ambra.models.Retraction;
import org.ambraproject.testutils.MockAmbraUser;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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

  @Test(alwaysRun = true)
  public void testGetAllAnnotationClasses() {
    Set<Class<? extends ArticleAnnotation>> classes = annotationService.getAllAnnotationClasses();
    assertNotNull(classes, "AnnotationService returned null set of annotation classes");
    assertTrue(classes.size() > 0, "AnnotationService returned empty set of classes");
  }

  /**
   * Data Provider for annotationService.createAnnotation().
   *
   * @return - 1 element arrays of dummy annotations which haven't been saved to the data store
   */
  @DataProvider(name = "dummyAnnotations")
  public Object[][] dummyAnnotations() {
    FormalCorrection formalCorrection = new FormalCorrection();
    formalCorrection.setAnnotates(URI.create("id://annotates"));
    formalCorrection.setContext("context");
    formalCorrection.setTitle("Formal Correction of a Very Formal Error");
    formalCorrection.setCreator("id://John-Smith");

    Comment comment = new Comment();
    comment.setAnnotates(URI.create("id://another-annotation"));
    comment.setContext("context");
    comment.setTitle("Comment title");
    comment.setCreator("John Smith the 3rd");

    return new Object[][]{
        {formalCorrection},
        {comment}
    };
  }

  /**
   * Test for annotationService.createAnnotation() method.
   *
   * @param annotation - mock annotation object with at least the following properties set: <ul> <li>annotates</li>
   *                   <li>context</li> <li>title</li> <li>creator</li> </ul>.  These are copied for the new annotation
   * @throws Exception - from annotationService.createAnnotation()
   */
  @Test(dataProvider = "dummyAnnotations")
  public void testCreateAnnotation(ArticleAnnotation annotation) throws Exception {
    String id = annotationService.createAnnotation(
        annotation.getClass(), //class
        "text/plain",   //mime type of body
        annotation.getAnnotates().toString(), //target
        annotation.getContext(), //context
        null, //olderAnnotation
        annotation.getTitle(), //title
        "Test body with hash code " + annotation.hashCode(), //body
        "ciStatement", //ci statement
        true, //isPublic
        new MockAmbraUser(annotation.getCreator()) //AmbraUser
    );
    assertNotNull(id, "generated null id for annotation");
    assertFalse(id.isEmpty(), "generated empty id for annotation");
  }

  @Test(dataProvider = "dummyAnnotations")
  public void testCreateComment(ArticleAnnotation annotation) throws Exception {
    String id = annotationService.createComment(
        "text/plain",   //mime type of body
        annotation.getAnnotates().toString(), //target
        null, //olderAnnotation
        annotation.getContext(), //context
        annotation.getTitle(), //title
        "Test body with hash code " + annotation.hashCode(), //body
        "ciStatement", //ci statement
        true, //isPublic
        new MockAmbraUser(annotation.getCreator()) //AmbraUser
    );
    assertNotNull(id, "generated null id for comment");
    assertFalse(id.isEmpty(), "generated empty id for annotation");
  }

  @Test
  public void testCreateFlag() throws Exception {
    String id = annotationService.createFlag(
        "id://target", //target
        "reason code", //context
        "body",
        "text/plain",   //mime type of body
        new MockAmbraUser("test creator") //AmbraUser
    );
    assertNotNull(id, "generated null id for Flag");
    assertFalse(id.isEmpty(), "generated empty id for annotation");
  }

  /**
   * Data Provider for annotationService.createAnnotation().
   *
   * @return - 1 element arrays of dummy annotations which haven't been saved to the data store
   */
  @DataProvider(name = "annotationsToDelete")
  public Object[][] annotationsToDelete() {
    FormalCorrection formalCorrection = new FormalCorrection();
    formalCorrection.setTitle("Formal Correction to delete");

    Comment comment = new Comment();
    comment.setAnnotates(URI.create("id://another-annotation"));

    return new Object[][]{
        {dummyDataStore.store(formalCorrection)},
        {dummyDataStore.store(comment)}
    };
  }

  @Test(dataProvider = "annotationsToDelete", expectedExceptions = IllegalArgumentException.class)

  public void testDeleteAnnotation(String annotationId) {
    annotationService.deleteAnnotation(annotationId, DEFAULT_ADMIN_AUTHID);
    annotationService.getAnnotation(annotationId); //Should throw IllegalArgumentException
  }

  /**
   * DataProvider for testGetAnnotations. Also seeds the annotation Service with the dummy annotations created.
   * <p/>
   * Note that this implicitly tests annotationService.createAnnotation()
   *
   * @return - A list of annotation Ids, and a list of the corresponding dummy annotations
   * @throws Exception - from annotationService.createAnnotation()
   */
  @DataProvider(name = "annotationIdList")
  public Object[][] annotationIdList() throws Exception {

    List<ArticleAnnotation> annotations = new LinkedList<ArticleAnnotation>();
    FormalCorrection formalCorrection = new FormalCorrection();
    formalCorrection.setAnnotates(URI.create("id://annotates"));
    formalCorrection.setContext("context");
    formalCorrection.setTitle("Formal Correction of a Very Formal Error");
    formalCorrection.setCreator("id://John-Smith");
    formalCorrection.setCreated(new Date());

    Comment comment = new Comment();
    comment.setAnnotates(URI.create("id://another-annotation"));
    comment.setContext("context");
    comment.setTitle("Comment title");
    comment.setCreator("John Smith the 3rd");
    comment.setCreated(new Date());

    annotations.add(formalCorrection);
    annotations.add(comment);

    List<String> ids = dummyDataStore.store(annotations);

    Journal journal = new Journal();
    journal.setKey("PLoSONE");
    journal.seteIssn("1932-6203");
    dummyDataStore.store(journal);

    Article article = new Article();
    article.setDoi(formalCorrection.getAnnotates().toString());
    article.seteIssn("1932-6203");

    dummyDataStore.store(article);

    Reply reply1 =  new Reply();
    reply1.setRoot(comment.getId().toString());
    reply1.setAnonymousCreator("anonymous creator for reply1");
    reply1.setCreated(new Date());
    reply1.setTitle("Test Reply1");
    dummyDataStore.store(reply1);

    Reply reply2 =  new Reply();
    reply2.setRoot(comment.getId().toString());
    reply2.setAnonymousCreator("anonymous creator for reply2");
    reply2.setCreated(new Date());
    reply2.setTitle("Test Reply2");
    dummyDataStore.store(reply2);

    Article article1 = new Article();
    article1.setDoi(comment.getAnnotates().toString());
    article1.seteIssn("1932-6203");

    dummyDataStore.store(article1);

    return new Object[][]{
        {ids, annotations}
    };
  }

  /**
   * DataProvider for testGetAnnotation. Also seeds the data store with the dummy annotations created.
   *
   * @return - A list of annotation Ids, and a list of the corresponding dummy annotations
   * @throws Exception - from annotationService.createAnnotation()
   */
  @DataProvider(name = "annotationIds")
  @SuppressWarnings("unchecked")
  public Object[][] annotationIds() throws Exception {
    Comment comment2 = new Comment();
    comment2.setAnnotates(URI.create("id://test-annotates-1"));
    comment2.setContext("context");
    comment2.setCreator("Mr. Comment Creator");
    comment2.setCreated(new Date());

    //Set the body on the comment
    AnnotationBlob body = new AnnotationBlob();
    body.setBody("test body".getBytes());
    body.setCIStatement("ci statement");
    comment2.setBody(body);

    MinorCorrection minorCorrection = new MinorCorrection();
    minorCorrection.setAnnotates(URI.create("id://test"));
    minorCorrection.setContext("minor correction");
    minorCorrection.setTitle("A Minor Correction");
    minorCorrection.setCreator("Kurt Goedel");
    minorCorrection.setCreated(new Date());

    AnnotationBlob body2 = new AnnotationBlob();
    body2.setBody("test body2".getBytes());
    body2.setCIStatement("ci statement");
    minorCorrection.setBody(body2);

    return new Object[][]{
        {dummyDataStore.store(comment2), comment2},
        {dummyDataStore.store(minorCorrection), minorCorrection}
    };
  }

  /**
   * Test annotationService.getAnnotation(); the Annotations should have been added by the dataprovider
   *
   * @param annotationId       - the id for the annotation to get
   * @param expectedAnnotation - an annotation object with expected properties
   */
  @Test(dataProvider = "annotationIds")
  public void testGetAnnotation(String annotationId, Annotation expectedAnnotation) {
    Annotation annotation = annotationService.getAnnotation(annotationId);
    assertNotNull(annotation, "returned null annotation");
    assertEquals(annotation.getId(), URI.create(annotationId),
        "Annotation didn't have correct id");
    compareBasicAnnotationProperties(annotation, expectedAnnotation);
  }

  /**
   * Test for annotationService.getArticleAnnotation()
   *
   * @param annotationId       - the id to look up
   * @param expectedAnnotation - an annotation object with expected properties
   */
  @Test(dataProvider = "annotationIds")
  public void testGetArticleAnnotation(String annotationId, ArticleAnnotation expectedAnnotation) {
    ArticleAnnotation annotation = annotationService.getArticleAnnotation(annotationId);
    assertNotNull(annotation, "returned null article annotation");
    assertEquals(annotation.getId(), URI.create(annotationId),
        "ArticleAnnotation didn't have correct id");
    compareBasicAnnotationProperties(annotation, expectedAnnotation);
  }

  /**
   * Test annotationService.getAnnotations(), which takes a list of annotation ids.  Annotations should've been added by
   * the dataprovider
   *
   * @param annotationIds       - a list of annotation ids to pass to the annotationService
   * @param expectedAnnotations - a list of Annotations with expected properties, corresponding to the id list
   */
  @Test(dataProvider = "annotationIdList")
  public void testGetAnnotations(List<String> annotationIds, List<Annotation> expectedAnnotations) {
    List<Annotation> annotations = annotationService.getAnnotations(annotationIds);
    assertNotNull(annotations, "Returned null list of annotations");
    assertEquals(annotations.size(), annotationIds.size(), "didn't return correct number of annotations");
    for (int i = 0; i < annotations.size(); i++) {
      Annotation annotation = annotations.get(i);
      assertNotNull(annotation, "Returned a null annotation");
      assertEquals(annotation.getId(), URI.create(annotationIds.get(i)),
          "Annotation didn't have correct id");
      compareBasicAnnotationProperties(annotation, expectedAnnotations.get(i));
    }
  }

  /**
   * DataProvider for tests of annotationService.getFeedAnnotationIds() and annotationService.getReplyIds()
   *
   * @param testMethod - the test method being invoked.  We take this in so we can store a Reply object for
   *                   testGetReplyIds
   * @return - start date, end date, and a limit of ids to return
   */
  @DataProvider(name = "startAndEndDates")
  public Object[][] startAndEndDates(Method testMethod) {
    Calendar tenMinutesAgo = Calendar.getInstance();
    tenMinutesAgo.add(Calendar.MINUTE, -10);

    return new Object[][]{
        {tenMinutesAgo.getTime(), new Date(), 1},
        {tenMinutesAgo.getTime(), new Date(), 0},
        {tenMinutesAgo.getTime(), new Date(), 2}
    };
  }


  @Test(dataProvider = "startAndEndDates", dependsOnMethods = {"testGetAnnotation", "testGetAnnotations"})
  public void testGetFeedAnnotationIds(Date start, Date end, int limit) throws Exception {
    Set<String> annotTypes = new HashSet<String>();
    annotTypes.add(FormalCorrection.RDF_TYPE);
    String journal = "PLoSONE";

    List<String> feedAnnotationIds = annotationService.getFeedAnnotationIds(start, end, annotTypes, limit, journal);
    assertNotNull(feedAnnotationIds, "null list of feed annotation ids");
    assertTrue(feedAnnotationIds.size() > 0, "empty list of feed annotation ids");
    //limit = 0 specifies no limit
    assertTrue(limit == 0 || feedAnnotationIds.size() <= limit, "returned more ids than the specified limit");

    for (String id : feedAnnotationIds) {
      assertFalse(id.isEmpty(), "empty feed annotation id");
      try {
        URI.create(id);
      } catch (Exception e) {
        fail("annotation id that wasn't a valid URI");
      }
    }
  }

  @Test(dataProvider = "startAndEndDates", dependsOnMethods = {"testGetAnnotation", "testGetAnnotations"})
  public void testGetReplyIds(Date start, Date end, int limit) throws Exception {
    Set<String> annotTypes = new HashSet<String>();
    annotTypes.add(Comment.RDF_TYPE);

    String journal = "PLoSONE";
    List<String> replyIds = annotationService.getReplyIds(start, end, annotTypes, limit, journal);
    assertNotNull(replyIds, "null list of reply ids");
    assertTrue(replyIds.size() > 0, "empty list of reply ids");
    //limit = 0 specifies no limit
    assertTrue(limit == 0 || replyIds.size() <= limit, "returned more ids than the specified limit");

    for (String id : replyIds) {
      assertFalse(id.isEmpty(), "empty reply id");
      try {
        URI.create(id);
      } catch (Exception e) {
        fail("reply id that wasn't a valid URI");
      }
    }
  }

  /**
   * Data provicer for testListAnnotations().  Create annotations that all point to the same target
   *
   * @return - the target uri and a list of expected ids for the annotations
   */
  @DataProvider(name = "targetAnnotationList")
  public Object[][] annotationList() {
    URI target = URI.create("id://target-for-list-annoations");
    Comment comment = new Comment();
    comment.setAnnotates(target);
    FormalCorrection formalCorrection = new FormalCorrection();
    formalCorrection.setAnnotates(target);

    List<Annotation> annotations = new ArrayList<Annotation>();
    annotations.add(comment);
    annotations.add(formalCorrection);
    List<String> ids = dummyDataStore.store(annotations);

    return new Object[][]{
        {target, ids}
    };
  }

  /**
   * Test for annotationService.listAnnotations() method that takes a target as argument
   * @param target - the URI of a target object for the annotations
   * @param expectedIds - a list of ids for annotations that annotate the given target, and so should be returned
   */
  @Test(dataProvider = "targetAnnotationList")
  public void testListAnnotationsByTarget(URI target, List<String> expectedIds) {
    ArticleAnnotation[] annotations = annotationService.listAnnotations(target.toString(),
        annotationService.getAllAnnotationClasses());
    assertNotNull(annotations, "returned null list of annotations");
    assertEquals(annotations.length, expectedIds.size(), "Didn't return correct number of annotations");

    List<String> actualIds = new ArrayList<String>(annotations.length);
    for (ArticleAnnotation annotation : annotations) {
      actualIds.add(annotation.getId().toString());
    }

    for (String expectedId : expectedIds) {
      assertTrue(actualIds.contains(expectedId), "Didn't return expected annotation with id: " + expectedId);
    }
  }

  @DataProvider(name = "mediatorAnnotationList")
  public Object[][] mediatorAnnotationList() {
    String firstMediator = "test-mediator";
    int firstState = 1;
    Comment comment = new Comment();
    comment.setMediator(firstMediator);
    comment.setState(firstState);

    MinorCorrection minorCorrection = new MinorCorrection();
    minorCorrection.setMediator(firstMediator);
    minorCorrection.setState(firstState);

    List<String> expectedIds1 = new ArrayList<String>();
    expectedIds1.add(dummyDataStore.store(comment));
    expectedIds1.add(dummyDataStore.store(minorCorrection));

    String secondMediator = "test-mediator-number-2";
    int secondState = 25844;
    Comment comment2 = new Comment();
    comment2.setMediator(secondMediator);
    comment2.setState(secondState);
    List<String> expectedIds2 = new ArrayList<String>();
    expectedIds2.add(dummyDataStore.store(comment2));

    return new Object[][]{
        {firstMediator, firstState, expectedIds1},
        {secondMediator, secondState, expectedIds2}
    };
  }

  /**
   * Test for annotationService.listAnnotations() method that takes a mediator and state as arguments
   * @param mediator - a test mediator to use
   * @param state - a test state
   * @param expectedIds - a list of ids of annotations that have the given mediator and state
   */
  @Test(dataProvider = "mediatorAnnotationList")
  public void testListAnnotationsByMediator(String mediator, int state, List<String> expectedIds) {
    ArticleAnnotation[] annotations = annotationService.listAnnotations(mediator, state);
    assertNotNull(annotations, "returned null list of annotations");
    assertEquals(annotations.length, expectedIds.size(), "Didn't return correct number of annotations");

    List<String> actualIds = new ArrayList<String>(annotations.length);
    for (ArticleAnnotation annotation : annotations) {
      actualIds.add(annotation.getId().toString());
    }

    for (String expectedId : expectedIds) {
      assertTrue(actualIds.contains(expectedId), "Didn't return expected annotation with id: " + expectedId);
    }
  }

  /**
   * Test for the annotationService.updateBodyAndContext() method
   * @param annotationId - id of an annotation to update
   * @param expectedAnnotation - annotation with expected properties set to compare
   * @throws Exception - from updateBodyAndContext
   */
  @Test(dataProvider = "annotationIds")
  public void testUpdateBodyAndContext(String annotationId, Annotation expectedAnnotation) throws Exception {
    String newContext = "new context";
    String newBody = "new body";

    annotationService.updateBodyAndContext(annotationId, newBody, newContext, DEFUALT_USER_AUTHID);
    ArticleAnnotation annotation = annotationService.getArticleAnnotation(annotationId);
    assertNotNull(annotation.getBody(), "Didn't create a body for annotation");
    assertEquals(new String(annotation.getBody().getBody()), newBody, "new body didn't get set");
    assertEquals(annotation.getContext(), newContext, "new context didn't get set");
    expectedAnnotation.setContext(newContext);
    compareBasicAnnotationProperties(annotation, expectedAnnotation);
  }

  @DataProvider(name = "annotationClassConversion")
  public Object[][] classConversionDataProvider() {
    Article article = new Article();
    article.setDoi("id://test-article");

    dummyDataStore.store(article);

    String annotates = article.getDoi();
    Comment comment2 = new Comment();
    comment2.setAnnotates(URI.create(annotates));
    comment2.setContext("context");
    comment2.setCreator("Mr. Comment Creator");
    comment2.setCreated(new Date());

    //Set the body on the comment
    AnnotationBlob body = new AnnotationBlob();
    body.setBody("test body".getBytes());
    body.setCIStatement("ci statement");
    comment2.setBody(body);

    MinorCorrection minorCorrection = new MinorCorrection();
    minorCorrection.setAnnotates(URI.create(annotates));
    minorCorrection.setContext("minor correction");
    minorCorrection.setTitle("A Minor Correction");
    minorCorrection.setCreator("Kurt Godel");
    minorCorrection.setCreated(new Date());
    AnnotationBlob body2 = new AnnotationBlob();
    body2.setBody("test body2".getBytes());
    body2.setCIStatement("ci statement");
    minorCorrection.setBody(body2);

    return new Object[][]{
        {dummyDataStore.store(comment2), MinorCorrection.class, comment2},
        {dummyDataStore.store(minorCorrection), FormalCorrection.class, minorCorrection}
    };
  }

  /**
   * Test for annotationService.convertAnnotationToType()
   * @param annotationId - id of the annotation to convert
   * @param newClass - the class to convert to
   * @param expectedAnnotation
   * @throws Exception
   */
  @Test(dataProvider = "annotationClassConversion")
  public void testConvertAnnotationToType(String annotationId, Class<? extends ArticleAnnotation> newClass, Annotation expectedAnnotation) throws Exception {
    String newId = annotationService.convertAnnotationToType(annotationId, newClass);
    assertNotNull(newId, "generated null id for converted annotation");
    assertEquals(newId,annotationId,"Didn't return same Id for annotation");
    Annotation newAnnotation = annotationService.getAnnotation(newId);
    assertTrue(newClass.isAssignableFrom(newAnnotation.getClass()), "Didn't convert annotation to correct class; " +
        "expected a subclass of " + newClass.getSimpleName() + " but got " + newAnnotation.getClass().getSimpleName());
    compareBasicAnnotationProperties(newAnnotation, expectedAnnotation);
  }

  /**
   * Compare some basic properties on annotations.
   * <p/>
   * The only properties we compare are: <ul> <li>annotates</li> <li>context</li> <li>title</li> <li>creator</li> </ul>
   * <p/>
   *
   * @param actualAnnotation   - the annotation returned by annotationService
   * @param expectedAnnotation - a dummy annotation with expected properties
   */
  private void compareBasicAnnotationProperties(Annotation actualAnnotation, Annotation expectedAnnotation) {
    assertEquals(actualAnnotation.getAnnotates(), expectedAnnotation.getAnnotates(),
        "Didn't have correct annotates value");
    assertEquals(actualAnnotation.getContext(), expectedAnnotation.getContext(),
        "Didn't have correct context");
    assertEquals(actualAnnotation.getTitle(), expectedAnnotation.getTitle(),
        "Didn't have correct title");
    assertEquals(actualAnnotation.getCreator(), expectedAnnotation.getCreator(),
        "Didn't have correct creator");
  }

  /**
   * @return - a set of RDF Types
   */
  public static Set<String> getAnnotationTypes() {
    Set<String> annotTypes = new HashSet<String>();
    annotTypes.add(Comment.RDF_TYPE);
    annotTypes.add(FormalCorrection.RDF_TYPE);
    annotTypes.add(MinorCorrection.RDF_TYPE);
    annotTypes.add(Retraction.RDF_TYPE);
    return annotTypes;
  }

}
