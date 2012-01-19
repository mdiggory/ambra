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

package org.ambraproject.admin.action;

import org.ambraproject.models.Syndication;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.models.Article;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Scott Sterling
 * @author Alex Kudlick
 */
public class ArticleSyndicationHistoryTest extends BaseWebTest {

  @Autowired
  protected ArticleSyndicationHistory actionClass;

  @DataProvider(name = "syndicationHistory")
  private Object[][] getSyndications() {
    String doi = "id:fake-article";
    Syndication syndication = new Syndication(doi, "testTargetOne");
    dummyDataStore.store(syndication);
    Syndication syndication2 = new Syndication(doi, "testTargetTwo");
    dummyDataStore.store(syndication2);

    List<String> expectedTargets = new ArrayList<String>(2);
    expectedTargets.add(syndication.getTarget());
    expectedTargets.add(syndication2.getTarget());

    return new Object[][]{
        {doi, expectedTargets}
    };
  }

  @Test(dataProvider = "syndicationHistory")
  public void testGetSyndicationHistory(String doi, List<String> expectedTargets) throws Exception {
    setupAdminContext();
    actionClass.setRequest(getDefaultRequestAttributes());
    actionClass.setArticle(doi);
    actionClass.execute();

    List<Syndication> syndicationHistory = actionClass.getSyndicationHistory();
    assertNotNull(syndicationHistory, "syndication history was null");
    assertEquals(syndicationHistory.size(), expectedTargets.size(), "incorrect number of syndications");
    for (Syndication syndication : syndicationHistory) {
      assertTrue(expectedTargets.contains(syndication.getTarget()),
          "returned syndication with unexpected target: " + syndication.getTarget());
    }
  }

  @DataProvider(name = "finishedSyndications")
  public Object[][] getFinishedSyndications() {
    String doi = "id:doi-for-finished-syndications";
    List<String> allTargets = new ArrayList<String>(4);
    List<String> completedTargets = new ArrayList<String>(3);


    Syndication finishedSyndication1 = new Syndication();
    finishedSyndication1.setStatus(Syndication.STATUS_SUCCESS);
    finishedSyndication1.setTarget("target 1");
    finishedSyndication1.setDoi(doi);
    dummyDataStore.store(finishedSyndication1);
    allTargets.add(finishedSyndication1.getTarget());
    completedTargets.add(finishedSyndication1.getTarget());

    Syndication finishedSyndication2 = new Syndication();
    finishedSyndication2.setStatus(Syndication.STATUS_FAILURE);
    finishedSyndication2.setTarget("target 2");
    finishedSyndication2.setDoi(doi);
    dummyDataStore.store(finishedSyndication2);
    allTargets.add(finishedSyndication2.getTarget());
    completedTargets.add(finishedSyndication2.getTarget());

    Syndication finishedSyndication3 = new Syndication();
    finishedSyndication3.setStatus(Syndication.STATUS_PENDING);
    finishedSyndication3.setTarget("target 3");
    finishedSyndication3.setDoi(doi);
    dummyDataStore.store(finishedSyndication3);
    allTargets.add(finishedSyndication3.getTarget());
    completedTargets.add(finishedSyndication3.getTarget());

    Syndication inProgressSyndication = new Syndication();
    inProgressSyndication.setStatus(Syndication.STATUS_IN_PROGRESS);
    inProgressSyndication.setTarget("target 4");
    inProgressSyndication.setDoi(doi);
    dummyDataStore.store(inProgressSyndication);
    allTargets.add(inProgressSyndication.getTarget());

    return new Object[][]{
        {doi, allTargets, completedTargets}
    };
  }

  @Test(dataProvider = "finishedSyndications")
  public void testAlreadyCompletedSyndications(String doi, List<String> allTargets, List<String> completedTargets)
      throws Exception {
    setupAdminContext();
    actionClass.setArticle(doi);
    actionClass.setRequest(getDefaultRequestAttributes());
    actionClass.execute();
    List<Syndication> syndicationHistory = actionClass.getSyndicationHistory();
    List<Syndication> finishedSyndications = actionClass.getFinishedSyndications();

    //check the results for overall history
    assertNotNull(syndicationHistory, "null syndication history");
    assertNotNull(syndicationHistory, "syndication history was null");
    assertEquals(syndicationHistory.size(), allTargets.size(), "incorrect number of syndications");
    for (Syndication syndication : syndicationHistory) {
      assertTrue(allTargets.contains(syndication.getTarget()),
          "returned syndication with unexpected target: " + syndication.getTarget());
    }

    //check the results for just the finished syndications
    assertNotNull(finishedSyndications, "null finished syndications");
    assertEquals(finishedSyndications.size(), completedTargets.size(), "incorrect number of completed syndications");
    for (Syndication syndication : finishedSyndications) {
      assertTrue(completedTargets.contains(syndication.getTarget()), "returned incorrect target for completed syndications");
    }
  }

  @DataProvider(name = "syndicationToMarkAsFailed")
  public Object[][] getSyndicationToMarkAsFailed() {
    String doi = "id:doi-for-marking-failure";
    String target = "target";
    Syndication syndication = new Syndication(doi, target);
    syndication.setStatus(Syndication.STATUS_IN_PROGRESS);
    Long id = Long.valueOf(dummyDataStore.store(syndication));
    return new Object[][]{
        {doi, target, id}
    };
  }

  @Test(dataProvider = "syndicationToMarkAsFailed")
  public void testMarkAsFailed(String doi, String target, Long originalId) throws Exception {
    setupAdminContext();
    actionClass.setRequest(getDefaultRequestAttributes());
    actionClass.setArticle(doi);
    actionClass.setTarget(new String[]{target});
    actionClass.markSyndicationAsFailed();

    Syndication syndication = dummyDataStore.get(originalId, Syndication.class);

    assertNotNull(syndication, "syndication wasn't in the database");
    assertEquals(syndication.getStatus(), Syndication.STATUS_FAILURE, "syndication had incorrect status");
  }

  @DataProvider(name = "syndicationsToResyndicate")
  public Object[][] getSyndicationsToResyndicate() {
    String doi = "id:doi-to-resyndicate";
    Article article = new Article();
    article.setDoi(doi);
    article.setArchiveName("archive");
    dummyDataStore.store(article);

    Syndication syndication1 = new Syndication(doi, "FOO");
    syndication1.setSubmissionCount(2);
    syndication1.setStatus(Syndication.STATUS_FAILURE);
    Long id1 = Long.valueOf(dummyDataStore.store(syndication1));

    Syndication syndication2 = new Syndication(doi, "BAR");
    syndication2.setSubmissionCount(0);
    syndication2.setStatus(Syndication.STATUS_SUCCESS);
    Long id2 = Long.valueOf(dummyDataStore.store(syndication2));

    return new Object[][]{
        {doi, syndication1.getTarget(), id1, syndication1.getSubmissionCount()},
        {doi, syndication2.getTarget(), id2, syndication2.getSubmissionCount()}
    };
  }

  @Test(dataProvider = "syndicationsToResyndicate")
  public void testResyndicate(String doi, String target, Long originalId, int oldSubmissionCount) {
    setupAdminContext();
    long testStart = new Date().getTime();
    actionClass.setRequest(getDefaultRequestAttributes());
    actionClass.setArticle(doi);
    actionClass.setTarget(new String[]{target});
    actionClass.resyndicate();

    Syndication syndication = dummyDataStore.get(originalId, Syndication.class);

    assertNotNull(syndication, "syndication wasn't in the database");
    assertEquals(syndication.getStatus(), Syndication.STATUS_IN_PROGRESS, "syndication had incorrect status");
    assertEquals(syndication.getSubmissionCount(), oldSubmissionCount + 1,
        "syndication didn't get submission count incremented");
    assertTrue(syndication.getLastSubmitTimestamp().getTime() >= testStart,
        "syndication didn't get submit timestamp set to be after test start");

  }
}
