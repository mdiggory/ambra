/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.journal;

import org.ambraproject.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.models.Journal;
import org.topazproject.otm.criterion.Criterion;
import org.topazproject.otm.criterion.DetachedCriteria;
import org.topazproject.otm.criterion.EQCriterion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

/**
 *
 */
public class JournalServiceTest extends BaseTest {
  @Autowired
  protected JournalService journalService;

  //we only want to insert the journals once
  private static boolean hasInsertedData = false;

  @BeforeMethod
  public void insertJournalData() {
    //set up journals in the db
    if (!hasInsertedData) {
      hasInsertedData = true;
      for (Object[] row : journalData()) {
        Journal j = new Journal();
        j.setKey((String) row[0]);
        j.seteIssn((String) row[1]);
        j.setTitle((String) row[2]);

        //Setup smart collection for the journal
        EQCriterion eQCriterion = new EQCriterion();
        eQCriterion.setFieldName("eIssn");
        eQCriterion.setValue(j.geteIssn());

        dummyDataStore.store(eQCriterion);

        List<Criterion> cList = new ArrayList<Criterion>();
        cList.add(eQCriterion);

        DetachedCriteria detachedCriteria = new DetachedCriteria();
        detachedCriteria.setCriterionList(cList);

        dummyDataStore.store(detachedCriteria);

        List<DetachedCriteria> dcList = new ArrayList<DetachedCriteria>();
        dcList.add(detachedCriteria);

        j.setSmartCollectionRules(dcList);

        dummyDataStore.store(j);
      }
    }
  }

  @DataProvider(name = "journals")
  public Object[][] journalData() {
    return new Object[][]{
        {"journal-test", "1100-0000", "journal-title"},
        {"journal-test1", "1100-0001", "journal-title1"},
        {"journal-test2", "1100-0002", "journal-title2"}
    };
  }

  @Test(dataProvider = "journals")
  public void getJournalTest(final String journalKey, final String eIssn, final String title) {
    Journal j = journalService.getJournal(journalKey);

    assertEquals(j.getKey(), journalKey, "incorrect journal key");
    assertEquals(j.geteIssn(), eIssn, "incorrect journal eIssn");
    assertEquals(j.getTitle(), title, "incorrect journal title");
  }

  @Test(dataProvider = "journals")
  public void getJournalByEissn(final String journalKey, final String eIssn, final String title) {
    Journal j = journalService.getJournalByEissn(eIssn);

    assertEquals(j.getKey(), journalKey, "incorrect journal key");
    assertEquals(j.geteIssn(), eIssn, "incorrect journal eIssn");
    assertEquals(j.getTitle(), title, "incorrect journal title");
  }

  @Test
  public void getAllJournalsTest() {
    //note that this method doesn't actually read the db, but the configuration
    Set<String> journalKeys = journalService.getAllJournalNames();
    assertEqualsNoOrder(journalKeys.toArray(), new Object[] { "journal", "journal1" },
        "incorrect journal keys");
  }

  @DataProvider(name = "articles")
  public Object[][] articles() {
    Journal journal = journalService.getJournal("journal-test");
    journal.getSimpleCollection().add(URI.create("info:doi/1"));
    dummyDataStore.update(journal);


    Article article = new Article();
    article.setDoi("info:doi/2");
    article.seteIssn("1100-0001"); //eIssn for the second journal
    dummyDataStore.store(article);

    return new Object[][]{
        {"journal-test", "info:doi/1"},
        {"journal-test1", "info:doi/2"}
    };
  }

  @Test(dataProvider = "articles", dependsOnMethods = {"getJournalTest", "getJournalByEissn"})
  public void getJournalsForObjectTest(final String journalKey, final String doi) {
    Set<Journal> journals = journalService.getJournalsForObject(doi);
    assertNotNull(journals, "returned null set of journals");
    assertEquals(journals.size(), 1, "incorrect number of journals");
    assertEquals(((Journal) journals.toArray()[0]).getKey(), journalKey, "returned incorrect journal");
  }
}
