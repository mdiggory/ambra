/*
 * $HeadURL$
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

package org.ambraproject.admin.service;

import org.ambraproject.models.Article;
import org.ambraproject.models.Syndication;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.topazproject.ambra.models.Journal;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Scott Sterling
 * @author Alex Kudlick
 */

public class SyndicationServiceTest extends BaseTest {

  @Autowired
  protected SyndicationService syndicationService;

  @DataProvider(name = "storedSyndications")
  public Object[][] getStoredSyndications() {
    String doi = "id:test-article-1";
    Article article = new Article();
    article.setDoi(doi);
    dummyDataStore.store(article);

    Syndication pmc = new Syndication();
    pmc.setDoi(doi);
    pmc.setTarget("PMC");
    pmc.setSubmissionCount(1);
    pmc.setStatus(Syndication.STATUS_IN_PROGRESS);
    pmc.setErrorMessage("hello");
    dummyDataStore.store(pmc);

    Syndication ebsco = new Syndication();
    ebsco.setDoi(doi);
    ebsco.setTarget("EBSCO");
    ebsco.setSubmissionCount(10);
    ebsco.setStatus(Syndication.STATUS_FAILURE);
    ebsco.setErrorMessage("ERROR ERROR");
    dummyDataStore.store(ebsco);

    return new Object[][]{
        {doi, "PMC", pmc},
        {doi, "EBSCO", ebsco}
    };
  }

  @Test(dataProvider = "storedSyndications")
  public void testGetSyndication(String articleId, String target, Syndication original) throws URISyntaxException {
    Syndication syndication = syndicationService.getSyndication(articleId, target);
    assertNotNull(syndication, "returned null syndication");
    compareSyndications(syndication, original);

  }

  @Test(expectedExceptions = {NoSuchArticleIdException.class})
  public void testShouldFailIfNoArticle() throws NoSuchArticleIdException {
    syndicationService.createSyndications("id:article-does-not-exist");
  }

  @Test(expectedExceptions = {SyndicationException.class})
  public void testShouldFailIfNoArchiveName() throws NoSuchArticleIdException {
    Article article = new Article();
    article.setDoi("doi");
    dummyDataStore.store(article);
    syndicationService.syndicate("doi", "FOO");
  }

  /**
   * Test the updateSyndication() method by setting a Syndication's <code>status</code> to the "failure" constant.
   */
  @Test(dataProvider = "storedSyndications",
      dependsOnMethods = {"testGetSyndication", "testUpdateSyndicationUpdateToPending"}, alwaysRun = true)
  public void testUpdateSyndicationToFailure(String articleId, String target,
                                             Syndication original) throws URISyntaxException {
    Long testStart = new Date().getTime();

    String errorMessage = "a new error message";

    Syndication syndication = syndicationService.updateSyndication(articleId, target,
        Syndication.STATUS_FAILURE, errorMessage);

    assertNotNull(syndication, "returned null syndication");
    assertEquals(syndication.getStatus(), Syndication.STATUS_FAILURE, "Syndication didn't get status updated");
    assertEquals(syndication.getErrorMessage(), errorMessage, "syndication didn't get error message updated");
    assertNotNull(syndication.getLastModified(), "Syndication didn't get last modified timestamp set");
    assertTrue(syndication.getLastModified().getTime() >= testStart, "last modified timestamp wasn't after the " +
        "testStart");

    //Check that none of the other properties got messed with
    assertEquals(syndication.getDoi(), original.getDoi(), "syndication had article id changed");
    assertEquals(syndication.getSubmissionCount(), original.getSubmissionCount(), "syndication had submission count changed");
    assertEquals(syndication.getTarget(), original.getTarget(), "syndication had target changed");
    assertMatchingDates(syndication.getCreated(), original.getCreated());
  }

  /**
   * Test the updateSyndication() method by setting a Syndication's <code>status</code> to the "pending" constant.  This
   * update should fail, so the resulting Syndication is the same.
   */
  @Test(dataProvider = "storedSyndications", dependsOnMethods = {"testGetSyndication"}, alwaysRun = true)
  public void testUpdateSyndicationUpdateToPending(String articleId, String target, Syndication original) {
    Syndication syndication = syndicationService.updateSyndication(articleId, target, Syndication.STATUS_PENDING, null);

    //The only things that should change are status and error message
    original.setStatus(Syndication.STATUS_PENDING);
    original.setErrorMessage(null);
    compareSyndications(syndication, original);
  }

  @DataProvider(name = "articleNoSyndications")
  public Object[][] getArticleWithNoSyndications() {
    Article article = new Article();
    article.setDoi("id:test-doi-no-syndications");
    article.setArchiveName("archive");
    dummyDataStore.store(article);

    return new Object[][]{
        {article.getDoi(), new String[]{"FOO", "BAR"}}
    };
  }

  /**
   * Test the createSyndication() method for an Article which has no associated Syndication objects.
   */
  @Test(dataProvider = "articleNoSyndications")
  public void testCreateSyndicationsNoExistingSyndications(String articleId, String[] expectedTargets) throws
      NoSuchArticleIdException {
    Long testStart = new Date().getTime();

    List<Syndication> syndications = syndicationService.createSyndications(articleId);
    assertNotNull(syndications, "returned null list of syndications");
    assertEquals(syndications.size(), expectedTargets.length, "returned incorrect number of syndications");
    for (int i = 0; i < syndications.size(); i++) {
      Syndication syndication = syndications.get(i);
      assertNotNull(syndication, "returned null syndication");
      assertEquals(syndication.getDoi(), articleId, "returned syndication with incorrect doi");
      assertEquals(syndication.getTarget(), expectedTargets[i], "returned syndication with incorrect target");
      assertTrue(syndication.getCreated().getTime() >= testStart, "syndication didn't get create timestamp set");
      assertEquals(syndication.getStatus(), Syndication.STATUS_PENDING, "syndication didn't get correct status");
    }

  }

  @DataProvider(name = "articleWithSyndications")
  public Object[][] getArticleWithSyndications() {
    Article article = new Article();
    String doi = "id:article-with-syndications";
    article.setDoi(doi);
    article.setArchiveName("archive");
    dummyDataStore.store(article);

    Syndication syndication = new Syndication();
    syndication.setDoi(doi);
    syndication.setStatus(Syndication.STATUS_IN_PROGRESS);
    syndication.setLastModified(new Date());
    syndication.setTarget("FOO");
    dummyDataStore.store(syndication);


    return new Object[][]{
        {doi, new String[]{"FOO", "BAR"}, new Syndication[]{syndication}}
    };
  }

  /**
   * Test the createSyndication() method for an Article which already has some associated Syndication objects. Expects
   * the existing syndications to be for targets that appear before the new ones
   */
  @Test(dataProvider = "articleWithSyndications")
  public void testCreateSyndicationsWithExistingSyndications(String articleId, String[] expectedTargets, Syndication[] existingSyndications)
      throws URISyntaxException, NoSuchArticleIdException {
    Long testStart = new Date().getTime();

    List<Syndication> syndications = syndicationService.createSyndications(articleId);
    assertNotNull(syndications, "returned null list of syndications");
    assertEquals(syndications.size(), expectedTargets.length, "returned incorrect number of syndications");

    for (int i = 0; i < existingSyndications.length; i++) {
      compareSyndications(syndications.get(i), existingSyndications[i]);
    }

    //new syndications
    for (int i = existingSyndications.length; i < syndications.size(); i++) {
      Syndication syndication = syndications.get(i);
      assertNotNull(syndication, "returned null syndication");
      assertEquals(syndication.getDoi(), articleId, "returned syndication with incorrect target");
      assertEquals(syndication.getTarget(), expectedTargets[i], "returned syndication with incorrect target");
      assertTrue(syndication.getCreated().getTime() >= testStart, "syndication didn't get create timestamp updated");
      assertEquals(syndication.getStatus(), Syndication.STATUS_PENDING, "syndication didn't get correct status");
    }
  }

  @DataProvider(name = "failedAndInProgressSyndications")
  public Object[][] getFailedAndInProgressSyndications() {
    String journalKey = "PLoSBio";
    Journal journal = new Journal();
    journal.setKey(journalKey);
    journal.seteIssn("111-120-56");
    dummyDataStore.store(journal);

    Article article = new Article();
    article.seteIssn(journal.geteIssn());
    String articleId = "id:test-article-for-failed-and-in-progress";
    article.setDoi(articleId);
    dummyDataStore.store(article);

    Syndication failed = new Syndication();
    failed.setStatus(Syndication.STATUS_FAILURE);
    failed.setDoi(article.getDoi());
    failed.setSubmissionCount(1);
    failed.setLastModified(new Date());
    failed.setTarget("FOO1");
    dummyDataStore.store(failed);

    Syndication inProgress = new Syndication();
    inProgress.setStatus(Syndication.STATUS_IN_PROGRESS);
    inProgress.setDoi(article.getDoi());
    inProgress.setSubmissionCount(1);
    inProgress.setLastModified(new Date());
    inProgress.setTarget("FOO2");
    dummyDataStore.store(inProgress);

    Syndication successful = new Syndication();
    successful.setStatus(Syndication.STATUS_SUCCESS);
    successful.setDoi(article.getDoi());
    successful.setSubmissionCount(10);
    successful.setLastModified(new Date());
    successful.setTarget("FOO3");
    dummyDataStore.store(successful);

    Calendar oldDate = Calendar.getInstance();
    oldDate.add(Calendar.YEAR, -1);

    Syndication old = new Syndication();
    old.setStatus(Syndication.STATUS_FAILURE);
    old.setSubmissionCount(10);
    old.setDoi(article.getDoi());
    old.setLastModified(oldDate.getTime());
    old.setTarget("FOO4");
    dummyDataStore.store(old);

    return new Object[][]{
        {journalKey, new Syndication[]{failed, inProgress}}
    };
  }

  @Test(dataProvider = "failedAndInProgressSyndications")
  public void testGetFailedAndInProgressSyndications(String journalKey, Syndication[] expectedSyndications) throws URISyntaxException {
    List<Syndication> list = syndicationService.getFailedAndInProgressSyndications(journalKey);
    assertNotNull(list, "returned null syndication list");
    assertEquals(list.size(), expectedSyndications.length, "returned incorrect number of syndications");
    for (Syndication syndication : list) {
      boolean foundMatch = false;
      for (Syndication expected : expectedSyndications) {
        if (expected.getTarget().equals(syndication.getTarget())) {
          foundMatch = true;
          compareSyndications(syndication, expected);
          break;
        }
      }
      assertTrue(foundMatch, "returned an unexpected syndication with target: " + syndication.getTarget());
    }
  }


  @DataProvider(name = "articleForSyndication")
  public Object[][] getArticleForSyndication() {
    Article article = new Article();
    String articleId = "id:article-for-syndication";
    article.setArchiveName("archive.zip");
    article.setDoi(articleId);
    dummyDataStore.store(article);

    Syndication previousSyndication = new Syndication();
    previousSyndication.setStatus(Syndication.STATUS_IN_PROGRESS);
    previousSyndication.setSubmissionCount(1);
    previousSyndication.setDoi(article.getDoi());
    previousSyndication.setTarget("FOO");
    dummyDataStore.store(previousSyndication);


    return new Object[][]{
        {articleId, "FOO", 1},
        {articleId, "BAR", 0}
    };
  }

  /**
   * Test the syndicate() method.  Simulates the successful creation of a message and the pushing of that message to the
   * plos-queue functionality.
   */
  @Test(dataProvider = "articleForSyndication")
  public void testSyndicate(String articleId, String target, int previousSubmissionCount) throws Exception {
    Long testStart = new Date().getTime();

    Syndication syndication = syndicationService.syndicate(articleId, target);
    assertNotNull(syndication, "returned null syndication");
    assertEquals(syndication.getDoi(), articleId, "syndication had incorrect article id");
    assertEquals(syndication.getTarget(), target, "syndication had incorrect target");
    assertEquals(syndication.getSubmissionCount(), previousSubmissionCount + 1, "syndication didn't get submission count updated");

    assertNotNull(syndication.getCreated(), "syndication had null create timestamp");

    assertNotNull(syndication.getLastSubmitTimestamp(), "syndication didn't get submit timestamp set");
    assertTrue(syndication.getLastSubmitTimestamp().getTime() >= testStart,
        "syndication didn't have submit timestamp set to be after test start");

    if (previousSubmissionCount == 0) {
      //syndication should've been created during the test
      assertTrue(syndication.getCreated().getTime() >= testStart, "syndication didn't get create timestamp set " +
          "correctly");
    } else {
      assertTrue(syndication.getCreated().getTime() <= testStart, "syndication had create timestamp changed");
    }


    assertEquals(syndication.getStatus(), Syndication.STATUS_IN_PROGRESS, "syndication didn't get status set correctly");
  }

  @DataProvider(name = "articleWithSyndicationsToQuery")
  public Object[][] getArticleWithSyndicationsToQuery() {
    String articleId = "id:article-with-syndications-to-query";
    Article article = new Article();
    article.setDoi(articleId);
    dummyDataStore.store(article);

    List<String> expectedTargets = new ArrayList<String>(2);
    Syndication syndication1 = new Syndication();
    syndication1.setDoi(article.getDoi());
    syndication1.setTarget("FOO10000");
    dummyDataStore.store(syndication1);
    expectedTargets.add(syndication1.getTarget());

    Syndication syndication2 = new Syndication();
    syndication2.setDoi(article.getDoi());
    syndication2.setTarget("FOO10001");
    dummyDataStore.store(syndication2);
    expectedTargets.add(syndication2.getTarget());


    return new Object[][]{
        {articleId, expectedTargets}
    };
  }

  @Test(dataProvider = "articleWithSyndicationsToQuery")
  public void testGetSyndications(String articleId, List<String> expectedTargets) throws NoSuchArticleIdException {
    List<Syndication> syndications = syndicationService.getSyndications(articleId);
    assertNotNull(syndications, "returned null list of syndications");
    assertEquals(syndications.size(), expectedTargets.size(), "returned incorrect number of syndications");
    for (Syndication syndication : syndications) {
      assertNotNull(syndication, "returned null syndication");
      assertEquals(syndication.getDoi(), articleId, "syndication had incorrect article id");
      assertTrue(expectedTargets.contains(syndication.getTarget()),
          "returned syndication for unexpected target: " + syndication.getTarget());
    }
  }

  private void compareSyndications(Syndication actual, Syndication expected) {
    assertEquals(actual.getDoi(), expected.getDoi(), "syndication had incorrect article id");
    assertEquals(actual.getStatus(), expected.getStatus(), "syndication had incorrect status");
    assertEquals(actual.getErrorMessage(), expected.getErrorMessage(), "syndication had incorrect error message");
    assertEquals(actual.getSubmissionCount(), expected.getSubmissionCount(), "syndication had incorrect submission count");
    assertEquals(actual.getTarget(), expected.getTarget(), "syndication had incorrect target");
    assertMatchingDates(actual.getLastModified(), expected.getLastModified());
    assertMatchingDates(actual.getCreated(), expected.getCreated());
  }
}
