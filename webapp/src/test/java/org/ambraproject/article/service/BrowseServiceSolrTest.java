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

package org.ambraproject.article.service;

import org.ambraproject.article.BrowseParameters;
import org.ambraproject.article.BrowseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.model.article.Days;
import org.ambraproject.model.article.Months;
import org.ambraproject.model.article.Years;
import org.ambraproject.search.SearchHit;
import org.ambraproject.testutils.EmbeddedSolrServerFactory;

import java.util.*;

import static org.testng.Assert.*;

/**
 * Test for methods of {@link BrowseService} that use solr.  working directory for the test should be set to either the
 * new hope home directory or the ambra webapp home directory
 *
 * @author Alex Kudlick Date: 5/17/11
 *         <p/>
 *         org.ambraproject.article.service
 */
public class BrowseServiceSolrTest extends BaseTest {

  private static final String JOURNAL_KEY = "PLoSGenetics";
  private static final String CATEGORY_1_LEVEL_1 = "Category1";
  private static final String CATEGORY_2_LEVEL_1 = "Category2";
  @Autowired
  protected BrowseService browseService;
  //Just getting the solr factory to seed it with data
  @Autowired
  protected EmbeddedSolrServerFactory solrServerFactory;

  private boolean articlesHaveBeenSentToSolr = false;

  private static final String DOI1 = "10.1371/journal.pgen.1000096";
  private static final String DOI2 = "10.1371/journal.pgen.1000100";

  /**
   * Seed the solr server with some articles to test
   *
   * @throws Exception - if there's a problem
   */
  @BeforeMethod
  public void sendArticlesToSolr() throws Exception {
    if (!articlesHaveBeenSentToSolr) {
      Map<String, String[]> document1 = new HashMap<String, String[]>();
      document1.put("id", new String[]{DOI1});
      document1.put("doc_type", new String[]{"full"});
      document1.put("article_type_facet", new String[]{"Not an issue image"});
      document1.put("publication_date", new String[]{"2008-06-13T00:00:00Z"});
      document1.put("cross_published_journal_key", new String[]{JOURNAL_KEY});
      document1.put("subject_level_1", new String[]{CATEGORY_1_LEVEL_1, CATEGORY_2_LEVEL_1});

      Map<String, String[]> document2 = new HashMap<String, String[]>();
      document2.put("id", new String[]{DOI2});
      document2.put("doc_type", new String[]{"full"});
      document2.put("article_type_facet", new String[]{"Not an issue image"});
      document2.put("publication_date", new String[]{"2008-06-20T00:00:00Z"});
      document2.put("cross_published_journal_key", new String[]{JOURNAL_KEY});
      document2.put("subject_level_1", new String[]{CATEGORY_2_LEVEL_1});

      solrServerFactory.addDocument(document1);
      solrServerFactory.addDocument(document2);
      //we only want to do this once, but can't do it with a BeforeClass method because the
      //dependency injection wouldn't have occurred.  (see
      // http://stackoverflow.com/questions/5192562/spring-autowiring-happens-after-beforeclass-when-running-test-with-maven-surefir)
      articlesHaveBeenSentToSolr = true;
    }
  }

  @DataProvider(name = "articleDates")
  public Object[][] getArticleDates() {
    String journalKey = JOURNAL_KEY;
    Years years = new Years();
    years.getMonths(2008).getDays(6).add(13);
    years.getMonths(2008).getDays(6).add(20);

    return new Object[][]{
        {journalKey, years}
    };
  }

  @Test(dataProvider = "articleDates")
  public void testGetArticleDates(String journalKey, Years expected) {
    Years years = browseService.getArticleDatesForJournal(journalKey);
    assertNotNull(years, "returned null results");
    assertEquals(years.size(), expected.size(), "returned incorrect number of years");
    for (Integer year : expected.keySet()) {
      assertTrue(years.containsKey(year), "didn't return expected year: " + year);
      Months expectedMonths = expected.getMonths(year);
      Months actualMonths = years.getMonths(year);
      assertEquals(actualMonths.size(), expectedMonths.size(),
          "returned incorrect number of months for year: " + year);
      for (Integer month : expectedMonths.keySet()) {
        assertTrue(actualMonths.containsKey(month), "didn't return a month entry for " + year + "-" + month);
        Days expectedDays = expectedMonths.getDays(month);
        Days actualDays = actualMonths.getDays(month);
        assertEquals(actualDays.size(), expectedDays.size(),
            "returned incorrect number of days for " + year + "-" + month);
        for (Integer day : expectedDays) {
          assertTrue(actualDays.contains(day), "didn't return entry for expected day "
              + year + "-" + month + "-" + day);
        }
      }
    }
  }

  @DataProvider(name = "articleCategoryCount")
  public Object[][] getArticleCategoryCount() {
    Map<String, Long> counts = new HashMap<String, Long>(2);
    counts.put(CATEGORY_1_LEVEL_1, 1L);
    counts.put(CATEGORY_2_LEVEL_1, 2L);
    return new Object[][]{
        {JOURNAL_KEY, counts}
    };
  }

  @Test(dataProvider = "articleCategoryCount")
  public void testGetArticleCountsByCategory(String journalKey, Map<String, Long> expectedCounts) {
    SortedMap<String, Long> results = browseService.getSubjectsForJournal(journalKey);
    assertNotNull(results, "returned null results");
    assertEquals(results.size(), expectedCounts.size(), "returned incorrect number of categories");
    for (String category : expectedCounts.keySet()) {
      assertTrue(results.containsKey(category), "didn't return expected category " + category);
      assertEquals(results.get(category), expectedCounts.get(category),
          "Returned incorrect count for category " + category);
    }
  }

  @DataProvider(name = "articlesByCategory")
  public Object[][] getArticlesForCategory() {
    return new Object[][]{
        {JOURNAL_KEY, CATEGORY_1_LEVEL_1, new String[]{DOI1}},
        {JOURNAL_KEY, CATEGORY_2_LEVEL_1, new String[] {DOI1, DOI2}}
    };
  }

  @Test(dataProvider = "articlesByCategory")
  public void testGetArticlesByCategory(String journalKey, String category, String[] expectedIds) {
    BrowseParameters params = new BrowseParameters();
    params.setSubjects(new String[] { category });
    params.setPageNum(0);
    params.setPageSize(100);
    params.setJournalKey(journalKey);

    BrowseResult browseResult = browseService.getArticlesBySubject(params);
    List<SearchHit> results = browseResult.getArticles();

    assertNotNull(results, "returned null results");
    assertEquals(results.size(), expectedIds.length, "returned incorrect number of results");
    List<String> actualIds = new ArrayList<String>(results.size());
    for (SearchHit result : results) {
      actualIds.add(result.getUri());
    }
    assertEqualsNoOrder(actualIds.toArray(), expectedIds, "didn't return correct articles");
  }

  @DataProvider(name = "articlesByDate")
  public Object[][] getArticlesByDate() {
    Calendar twoThousandSeven = Calendar.getInstance();
    twoThousandSeven.set(Calendar.YEAR, 2007);

    Calendar now = Calendar.getInstance();
    Calendar midway = Calendar.getInstance();
    midway.set(Calendar.YEAR, 2008);
    midway.set(Calendar.MONTH, 5);
    midway.set(Calendar.DAY_OF_MONTH, 15);

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DAY_OF_MONTH, -1);


    return new Object[][]{
        {JOURNAL_KEY, twoThousandSeven, now, new String[] {DOI1, DOI2}},
        {JOURNAL_KEY, twoThousandSeven, midway, new String[]{DOI1}},
        {JOURNAL_KEY, midway, now, new String[]{DOI2}},
        {JOURNAL_KEY, yesterday, now, new String[0]}
    };
  }

  @Test(dataProvider = "articlesByDate", alwaysRun = true)
  public void testGetArticlesByDate(String journalKey, Calendar startDate, Calendar endDate, String[] expectedIds) {
    BrowseParameters params = new BrowseParameters();
    params.setStartDate(startDate);
    params.setEndDate(endDate);
    params.setPageNum(0);
    params.setPageSize(100);
    params.setJournalKey(journalKey);

    BrowseResult browseResult = browseService.getArticlesByDate(params);

    List<SearchHit> results = browseResult.getArticles();
    assertNotNull(results, "returned null results");
    assertEquals(results.size(), expectedIds.length, "returned incorrect number of results");
    List<String> actualIds = new ArrayList<String>(results.size());
    for (SearchHit result : results) {
      actualIds.add(result.getUri());
    }
    assertEqualsNoOrder(actualIds.toArray(), expectedIds, "didn't return correct articles");
  }


}
