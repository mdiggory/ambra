/*
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

package org.ambraproject.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.testutils.EmbeddedSolrServerFactory;
import org.ambraproject.search.SearchParameters;
import org.ambraproject.search.SearchResultSinglePage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Scott Sterling
 */
public class SearchServiceTest extends BaseTest {

  @Autowired
  protected SearchService searchService;

  @Autowired
  protected EmbeddedSolrServerFactory solrServerFactory;

  private static final String DOI_1         = "10.1371/journal.pgen.1000096";
  private static final String DOI_2         = "10.1371/journal.pbio.1000100";
  private static final String DOI_3         = "10.1371/journal.pbio.1000101";
  private static final String JOURNAL_KEY_1 = "PLoSGenetics";
  private static final String JOURNAL_KEY_2 = "PLoSBiology";
  private static final String CATEGORY_1    = "Category1";
  private static final String CATEGORY_2    = "Category2";

  /**
   * Seed the solr server with some articles to test
   *
   * @throws Exception - if there's a problem
   */
  @BeforeMethod
  public void sendArticlesToSolr() throws Exception {

    // 2 occurrences in "everything": "Spleen"
    Map<String, String[]> document1 = new HashMap<String, String[]>();
    document1.put("id", new String[]{DOI_1});
    document1.put("title", new String[]{"The First Title, with Spleen"});
    document1.put("author", new String[]{"alpha delta epsilon"});
    document1.put("body", new String[]{"Body of the first document: Yak and Spleen"});
    document1.put("everything", new String[]{
        "body first document yak spleen first title with spleen"});
    document1.put("elocation_id", new String[]{"111"});
    document1.put("volume", new String[]{"1"});
    document1.put("doc_type", new String[]{"full"});
    document1.put("publication_date", new String[]{"2008-06-13T00:00:00Z"});
    document1.put("cross_published_journal_key", new String[]{JOURNAL_KEY_1});
    document1.put("subject", new String[]{CATEGORY_1, CATEGORY_2});
    document1.put("article_type_facet", new String[]{"Not an issue image"});

    // 2 occurrences in "everything": "Yak"
    Map<String, String[]> document2 = new HashMap<String, String[]>();
    document2.put("id", new String[]{DOI_2});
    document2.put("title", new String[]{"The Second Title, with Yak"});
    document2.put("author", new String[]{"beta delta epsilon"});
    document2.put("body", new String[]{"Description of the second document: Yak and Islets of Langerhans"});
    document2.put("everything", new String[]{
        "description second document yak islets Langerhans second title with yak"});
    document2.put("elocation_id", new String[]{"222"});
    document2.put("volume", new String[]{"2"});
    document2.put("doc_type", new String[]{"full"});
    document2.put("publication_date", new String[]{"2008-06-20T00:00:00Z"});
    document2.put("cross_published_journal_key", new String[]{JOURNAL_KEY_2});
    document2.put("subject", new String[]{CATEGORY_2});
    document2.put("article_type_facet", new String[]{"Not an issue image"});

    // 2 occurrences in "everything": "Gecko"
    Map<String, String[]> document3 = new HashMap<String, String[]>();
    document3.put("id", new String[]{DOI_3});
    document3.put("title", new String[]{"The Third Title, with Gecko"});
    document3.put("author", new String[]{"gamma delta"});
    document3.put("body", new String[]{"Contents of the second document: Gecko and Islets of Langerhans"});
    document3.put("everything", new String[]{
        "contents of the second document gecko islets langerhans third title with gecko"});
    document3.put("elocation_id", new String[]{"333"});
    document3.put("volume", new String[]{"3"});
    document3.put("doc_type", new String[]{"full"});
    document3.put("publication_date", new String[]{"2008-06-22T00:00:00Z"});
    document3.put("cross_published_journal_key", new String[]{JOURNAL_KEY_2});
    document3.put("subject", new String[]{CATEGORY_1});
    document3.put("article_type_facet", new String[]{"Not an issue image"});

    solrServerFactory.addDocument(document1);
    solrServerFactory.addDocument(document2);
    solrServerFactory.addDocument(document3);

  }

  @DataProvider(name = "queryAndHitNumberForUnformattedQuery")  //  Use for Advanced Search.
  public Object[][] getQueryAndHitNumberForUnformattedQuery() throws Exception {
    return new Object[][]{
        {"*:*", 3},                                                       // Get All Articles
        {"everything:yak", 2},
        {"everything:spleen", 1},
        {"everything:document", 3},
        {"author:delta", 3},                                            //  Specific Field
        {"author:alpha", 1},                                              //  Specific Field
        {"title:title", 3},                                               //  Specific Field
        {"everything:\"Islets of Langerhans\"", 2},                       //  Quoted String
        {"everything:spleen AND everything:yak", 1},                      //  AND
        {"everything:spleen OR everything:yak", 2},                       //  OR
        {"everything:\"Islets of Langerhans\" NOT everything:gecko", 1}   //  NOT
    };
  }

  @DataProvider(name = "queryAndHitNumberForQuery")  //  Use for Simple Search.
  public Object[][] getQueryAndHitNumberForQuery() throws Exception {
    return new Object[][]{
        {"alpha", 1},
        {"delta", 3},
        {"yak", 2},
        {"spleen", 1},
        {"document", 3},
        {"\"Islets of Langerhans\"", 2},                       //  Quoted String
        {"yak spleen", 2},                                     //  Two Terms (implicit OR statement)
        {"thisWordDoesNotExist", 0}
    };
  }

  @DataProvider(name = "volumeAndELocationIdAndIdAndJournal")  //  Use for Find An Article Search.
  public Object[][] getVolumeAndELocationIdAndId() throws Exception {
    return new Object[][]{
        {"1", "111", DOI_1, JOURNAL_KEY_1},
        {"2", "222", DOI_2, JOURNAL_KEY_2},
        {"3", "333", DOI_3, JOURNAL_KEY_2}
    };
  }

  /**
   * Get a SearchParameters with all the required (non-query) values already set.
   * @return a SearchParameters with all the required (non-query) values already set
   */
  private static final SearchParameters getSearchParameters() {
    SearchParameters searchParameters = new SearchParameters();
    searchParameters.setPageSize(10);
    searchParameters.setStartPage(0);

    return searchParameters;
  }

  @Test(dataProvider = "queryAndHitNumberForQuery")
  public void testSimpleSearch(String query, int numberOfHits) throws Exception {
    SearchParameters searchParameters = getSearchParameters();
    searchParameters.setQuery(query);
    SearchResultSinglePage resultSinglePage = searchService.simpleSearch(searchParameters);

    Assert.isTrue(resultSinglePage.getHits().size() == numberOfHits,
        "Simple Search for '" + query + "' returned " + resultSinglePage.getHits().size()
            + " articles, but it should have returned " + numberOfHits + " articles");
  }

  @Test(dataProvider = "queryAndHitNumberForUnformattedQuery")
  public void testAdvancedSearch(String query, int numberOfHits) throws Exception {
    SearchParameters searchParameters = getSearchParameters();
    searchParameters.setUnformattedQuery(query);
    SearchResultSinglePage resultSinglePage = searchService.advancedSearch(searchParameters);

    Assert.isTrue(resultSinglePage.getHits().size() == numberOfHits,
        "Advanced Search for '" + query + "' returned " + resultSinglePage.getHits().size()
            + " articles, but it should have returned " + numberOfHits + " articles");
  }

  @Test(dataProvider = "volumeAndELocationIdAndIdAndJournal")
  public void testFindAnArticleSearch(String volume, String eLocationId, String id, String journal)
      throws Exception {
    SearchParameters searchParameters = getSearchParameters();
    searchParameters.setFilterJournals(new String[] {journal});

    // Search for just the Id (DOI).
    searchParameters.setId(id);
    SearchResultSinglePage resultSinglePage = searchService.findAnArticleSearch(searchParameters);
    Assert.isTrue(resultSinglePage.getHits().size() == 1, "Find An Article Search found no articles"
      + " with id = '" + id + "' in journal '" + journal + "'");

    //  Search for just the Volume
    searchParameters.setId(null);
    searchParameters.setVolume(volume);
    searchParameters.setFilterJournals(new String[] {journal});
    resultSinglePage = searchService.findAnArticleSearch(searchParameters);
    Assert.isTrue(resultSinglePage.getHits().size() == 1, "Find An Article Search found no articles"
      + " with volume = '" + volume + "' in journal '" + journal + "'");

    //  Search for the Volume and ELocationId.
    searchParameters.setId(null);
    searchParameters.setELocationId(eLocationId);
    searchParameters.setFilterJournals(new String[] {journal});
    resultSinglePage = searchService.findAnArticleSearch(searchParameters);
    Assert.isTrue(resultSinglePage.getHits().size() == 1, "Find An Article Search found no articles"
      + " with volume = '" + volume + "' and eLocationId = '" + eLocationId
      + "' in journal '" + journal + "'");
  }

  @Test
  public void testGetFilterData() throws Exception {
    SearchParameters searchParameters = getSearchParameters();
    searchParameters.setUnformattedQuery("*:*");
    SearchResultSinglePage resultSinglePage = searchService.getFilterData(searchParameters);

    Assert.isTrue(resultSinglePage.getJournalFacet().size() == 2,
        "For the query '*:*', get Filter Data expected 2 Journal Facets but, instead, received "
            + resultSinglePage.getJournalFacet().size());

    Assert.isTrue(resultSinglePage.getArticleTypeFacet().size() == 1,
        "For the query '*:*', get Filter Data expected 1 Article Type Facet but, instead, received "
            + resultSinglePage.getArticleTypeFacet().size());

    Assert.isTrue(resultSinglePage.getSubjectFacet().size() == 2,
        "For the query '*:*', get Filter Data expected 2 Subject Facets but, instead, received "
            + resultSinglePage.getSubjectFacet().size());
  }
}
