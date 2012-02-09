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

package org.ambraproject.action;

import com.opensymphony.xwork2.Action;
import org.ambraproject.BaseWebTest;
import org.ambraproject.search.SearchHit;
import org.ambraproject.testutils.EmbeddedSolrServerFactory;
import org.ambraproject.util.Pair;
import org.ambraproject.web.VirtualJournalContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.topazproject.ambra.models.Journal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 2/7/12
 */
public class HomepageActionTest extends BaseWebTest {
  @Autowired
  protected HomePageAction action;

  @Autowired
  protected EmbeddedSolrServerFactory solr;

  private DateFormat dateFormatter;

  public HomepageActionTest() {
    dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }

  @DataProvider(name = "expectedInfo")
  public Object[][] getExpectedInfo() throws Exception {
    //make sure to use a journal for this test, so we don't get 'recent' articles that were added by other unit tests
    Journal journal = new Journal();
    journal.seteIssn("8675-309");
    journal.setKey("HomePageActionTestJournal");
    dummyDataStore.store(journal);

    Calendar recentDate = Calendar.getInstance();
    recentDate.add(Calendar.DAY_OF_MONTH, -1);

    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);
    String lastYearString = dateFormatter.format(lastYear.getTime());

    List<Pair<String, String>> recentArticles = new ArrayList<Pair<String, String>>(5);

    for (int i = 1; i <= 4; i++) {
      //Make sure the articles are order by publication date (just for fun, they are sorted randomly by the action)
      recentDate.add(Calendar.HOUR_OF_DAY, -1);
      solr.addDocument(new String[][]{
          {"id", "test-id-" + i},
          {"title_display", "title for article " + i},
          {"publication_date", dateFormatter.format(recentDate.getTime())},
          {"subject_level_1", "Biology"},
          {"article_type_facet", "article"},
          {"doc_type", "full"},
          {"cross_published_journal_key", journal.getKey()}
      });
      recentArticles.add(new Pair<String, String>("test-id-" + i, "title for article " + i));
    }
    solr.addDocument(new String[][]{
        {"id", "old-article"},
        {"title_display", "This article should not show up in recent articles"},
        {"publication_date", lastYearString},
        {"subject_level_1", "Chemistry"},
        {"article_type_facet", "article"},
        {"doc_type", "full"},
        {"cross_published_journal_key", journal.getKey()}
    });
    solr.addDocument(new String[][]{
        {"id", "article-in-other-journal"},
        {"title_display", "This article should not show up in recent articles"},
        {"publication_date", dateFormatter.format(recentDate.getTime())},
        {"subject_level_1", "Chemistry"},
        {"article_type_facet", "article"},
        {"doc_type", "full"},
        {"cross_published_journal_key", "someOtherKey"}
    });

    SortedMap<String, Long> subjectCounts = new TreeMap<String, Long>();
    subjectCounts.put("Biology", 4l);
    subjectCounts.put("Chemistry", 1l);

    return new Object[][]{
        {journal, recentArticles, subjectCounts}
    };
  }

  @Test(dataProvider = "expectedInfo")
  public void testExecute(Journal journal, List<Pair<String, String>> expectedRecentArticles, SortedMap<String, Long> expectedCategoryInfos) {
    //make sure to use a journal for this test, so we don't get 'recent' articles that were added by other unit tests
    final Map<String, Object> request = getDefaultRequestAttributes();
    request.put(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT, new VirtualJournalContext(
        journal.getKey(),
        "dfltJournal",
        "http",
        80,
        "localhost",
        "ambra-webapp",
        new ArrayList<String>()));

    action.setRequest(request);
    final String result = action.execute();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(action.getActionMessages().size(), 0,
        "Action returned messages on default request: " + StringUtils.join(action.getActionMessages(), ";"));
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));

    assertEquals(action.getRecentArticles().size(), expectedRecentArticles.size(), "Action returned incorrect number of recent articles");
    for (Pair<String, String> recentArticle : expectedRecentArticles) {
      SearchHit matchingArticle = null;
      for (SearchHit searchHit : action.getRecentArticles()) {
        if (searchHit.getUri().equals(recentArticle.getFirst())) {
          matchingArticle = searchHit;
          break;
        }
      }
      assertNotNull(matchingArticle, "Didn't return expected recent article " + recentArticle.getFirst());
      assertEquals(matchingArticle.getTitle(), recentArticle.getSecond(),
          "Article " + matchingArticle.getUri() + " had incorrect title");
    }

    //category infos are used to put links to the browse by subject pages
    assertEquals(action.getCategoryInfos().size(), expectedCategoryInfos.size(),
        "Action returned incorrect number of category infos");
    for (String category : expectedCategoryInfos.keySet()) {
      assertTrue(action.getCategoryInfos().containsKey(category), "Action didn't return category: " + category);
      assertEquals(action.getCategoryInfos().get(category), expectedCategoryInfos.get(category),
          "Action returned incorrect value for category: " + category);
    }
  }

}
