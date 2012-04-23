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

package org.ambraproject.article.service;

import org.ambraproject.BaseTest;
import org.ambraproject.article.action.TOCArticleGroup;
import org.ambraproject.model.IssueInfo;
import org.ambraproject.model.VolumeInfo;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.ArticleType;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Journal;
import org.topazproject.ambra.models.Volume;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test for methods of {@link BrowseService} that don't use solr.  Working directory for the test should be set to
 * either the new hope home directory or the ambra webapp home
 *
 * @author Alex Kudlick Date: 5/16/11
 *         <p/>
 *         org.topazproject.ambra.testutils
 */
public class BrowseServiceTest extends BaseTest {

  @Autowired
  protected BrowseService browseService;

  private String articleType1 = "id://test-article-type-1";
  private String articleType2 = "id://test-article-type-2";

  @BeforeClass
  public void addArticleTypes() {
    ArticleType.addArticleType(
        URI.create(articleType1),        //type uri
        "code",              //code
        "Research Article",  //heading
        "Research Articles"  //plural heading
    );
    ArticleType.addArticleType(
        URI.create(articleType2),    //type uri
        "code2",         //code
        "Test Article",  //heading
        "Test Articles"  //plural heading
    );
  }

  @DataProvider(name = "issueIds")
  public Object[][] issueIds() {
    Article imageArticle = new Article();
    imageArticle.setDoi("id://test-image");
    imageArticle.setTitle("Bill and Ted's Excellent Adventure");
    imageArticle.setDescription("Two seemingly dumb teens struggle " +
        "to prepare a historical presentation with the help of a time machine. ");

    dummyDataStore.store(imageArticle);

    Issue testIssue = new Issue();
    testIssue.setDisplayName("Display name of test issue number 1");
    testIssue.setImage(URI.create(imageArticle.getDoi()));
    testIssue.setRespectOrder(true);
    testIssue.setTitle(imageArticle.getTitle());
    testIssue.setDescription(imageArticle.getDescription());

    URI issueId = URI.create(dummyDataStore.store(testIssue));

    return new Object[][]{
        {issueId, testIssue}
    };
  }

  @Test(dataProvider = "issueIds")
  public void testGetIssueInfo(URI issueDoi, Issue expectedIssue) {
    IssueInfo issueInfo = browseService.getIssueInfo(issueDoi);
    assertNotNull(issueInfo, "returned null issue info");
    assertEquals(issueInfo.getId(), issueDoi, "returned issue info with incorrect id");
    assertEquals(issueInfo.getDisplayName(), expectedIssue.getDisplayName(),
        "returned issue info with incorrect display name");
    assertEquals(issueInfo.getImageArticle(), expectedIssue.getImage(),
        "returned issue info with incorrect issue uri");
    assertEquals(issueInfo.getDescription(), expectedIssue.getDescription(),
        "returned issue info with incorrect description");
  }

  @DataProvider(name = "issueArticles")
  public Object[][] getIssueArticles() {
    Article article1 = new Article();
    article1.setDoi("id:test-article-for-issue-list1");

    Article article2 = new Article();
    article2.setDoi("id:test-article-for-issue-list2");

    Article article3 = new Article();
    article3.setDoi("id:test-article-for-issue-list3");

    dummyDataStore.store(article1);
    dummyDataStore.store(article2);
    dummyDataStore.store(article3);

    List<URI> articleIds = new ArrayList<URI>(3);
    articleIds.add(URI.create(article1.getDoi()));
    articleIds.add(URI.create(article2.getDoi()));
    articleIds.add(URI.create(article3.getDoi()));

    Issue issue = new Issue();
    issue.setArticleList(articleIds);
    issue.setId(URI.create(dummyDataStore.store(issue)));

    return new Object[][]{
        {issue, articleIds}
    };
  }

  @Test(dataProvider = "issueArticles")
  public void testGetArticleList(Issue issue, List<URI> expectedArticleURIs) {
    List<URI> results = browseService.getArticleList(issue);
    assertNotNull(results, "returned null list of article ids");
    assertEquals(results.size(), expectedArticleURIs.size(), "returned incorrect number of article ids");
    assertEqualsNoOrder(results.toArray(), expectedArticleURIs.toArray(), "returned incorrect article ids");
  }

  @Test(dataProvider = "issueArticles")
  public void testGetArticleInfosForIssue(Issue issue, List<URI> expectedArticleURIs) {
    List<ArticleInfo> results = browseService.getArticleInfosForIssue(issue.getId(), DEFAULT_ADMIN_AUTHID);
    assertNotNull(results, "returned null list of article infos");
    assertEquals(results.size(), expectedArticleURIs.size(), "returned incorrect number of results");
    List<URI> actualArticleURIs = new ArrayList<URI>(results.size());
    for (ArticleInfo articleInfo : results) {
      actualArticleURIs.add(URI.create(articleInfo.getDoi()));
    }
    assertEqualsNoOrder(actualArticleURIs.toArray(), expectedArticleURIs.toArray(),
        "returned incorrect list of articles");
  }


  @DataProvider(name = "volumes")
  public Object[][] getVolumes() {
    Article imageArticle1 = new Article();
    imageArticle1.setDoi("id://volume-1-image");
    dummyDataStore.store(imageArticle1);

    Article imageArticle2 = new Article();
    imageArticle2.setDoi("id://volume-2-image");
    dummyDataStore.store(imageArticle2);

    Volume volume1 = new Volume();
    volume1.setDisplayName("Volume 1");
    volume1.setImage(URI.create(imageArticle1.getDoi()));
    URI volume1Id = URI.create(dummyDataStore.store(volume1));

    Volume volume2 = new Volume();
    volume2.setDisplayName("Volume 2");
    volume2.setImage(URI.create(imageArticle2.getDoi()));
    URI volume2Id = URI.create(dummyDataStore.store(volume2));

    List<URI> volumeIds = new ArrayList<URI>();
    volumeIds.add(volume1Id);
    volumeIds.add(volume2Id);

    Journal journal = new Journal();
    journal.setKey("test-journal-key");
    journal.setVolumes(volumeIds);
    dummyDataStore.store(journal);

    return new Object[][]{
        {volume1Id, journal.getKey(), volume1},
        {volume2Id, journal.getKey(), volume2}
    };
  }

  @Test(dataProvider = "volumes")
  public void testGetVolumeInfo(URI volumeId, String journalKey, Volume expectedVolume) {
    VolumeInfo volumeInfo = browseService.getVolumeInfo(volumeId, journalKey);
    assertNotNull(volumeInfo, "returned null volume info");
    assertEquals(volumeInfo.getId(), volumeId, "returned volume info with incorrect id");
    assertEquals(volumeInfo.getImageArticle(), expectedVolume.getImage(),
        "returned volume with incorrect image article");
    assertEquals(volumeInfo.getDisplayName(), expectedVolume.getDisplayName(),
        "returned volume info with incorrect display name");
  }

  @DataProvider(name = "article")
  public Object[][] getArticle() {
    Article article = new Article();
    article.setDoi("id://test-article-47");

    article.setTitle("test title for article info");
    article.setDate(new Date());
    article.setDescription("test, test, test, this is a test");

    List<String> authorNames = new ArrayList<String>(2);
    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setFullName("Some fake author");
    dummyDataStore.store(author1);
    authors.add(author1);

    ArticleAuthor author2 = new ArticleAuthor();
    author2.setFullName("Michael Eisen");
    dummyDataStore.store(author2);
    authors.add(author2);

    article.setAuthors(authors);
    dummyDataStore.store(article);
    authorNames.add(author1.getFullName());
    authorNames.add(author2.getFullName());
    return new Object[][]{
        {article.getDoi(), article, authorNames}
    };
  }

  @DataProvider(name = "volumeList")
  public Object[][] getVolumeList() {
    Volume volume1 = new Volume();
    volume1.setDisplayName("Volume 1");
    volume1.setImage(URI.create("id://volume-1-image"));
    URI volume1Id = URI.create(dummyDataStore.store(volume1));

    Volume volume2 = new Volume();
    volume2.setDisplayName("Volume 2");
    volume2.setImage(URI.create("id://volume-2-image"));
    URI volume2Id = URI.create(dummyDataStore.store(volume2));

    List<URI> volumeIds = new ArrayList<URI>();
    volumeIds.add(volume1Id);
    volumeIds.add(volume2Id);

    Journal journal = new Journal();
    journal.setKey("test-journal");
    journal.setVolumes(volumeIds);

    return new Object[][]{
        {journal, volumeIds}
    };
  }

  @Test(dataProvider = "volumeList")
  public void testGetVolumeInfosForJournal(Journal journal, List<URI> expectedIds) {
    List<VolumeInfo> volumeInfos = browseService.getVolumeInfosForJournal(journal);
    assertNotNull(volumeInfos, "returned null list of volume infos");
    assertEquals(volumeInfos.size(), expectedIds.size(), "returned incorrect number of volume infos");
    List<URI> actualIds = new ArrayList<URI>(volumeInfos.size());
    for (VolumeInfo volumeInfo : volumeInfos) {
      actualIds.add(volumeInfo.getId());
    }
    assertEqualsNoOrder(actualIds.toArray(), expectedIds.toArray(), "Didn't return expected volumes");
  }

  @DataProvider(name = "articleGroupList")
  public Object[][] getArticleGroupList() {
    Set<String> bothTypes = new HashSet<String>(2);
    bothTypes.add(articleType1);
    bothTypes.add(articleType2);

    Set<String> firstType = new HashSet<String>(1);
    firstType.add(articleType1);

    Set<String> secondType = new HashSet<String>(1);
    secondType.add(articleType2);

    Article articleWithBothTypes = new Article();
    articleWithBothTypes.setDoi("id://article-1");
    articleWithBothTypes.setTypes(bothTypes);
    articleWithBothTypes.setDate(new Date());

    Article articleWithFirstType = new Article();
    articleWithFirstType.setDoi("id://test-article2");
    articleWithFirstType.setTypes(firstType);
    articleWithFirstType.setDate(new Date());

    Article articleWithSecondType = new Article();
    articleWithSecondType.setDoi("id://test-article-3");
    articleWithSecondType.setTypes(secondType);
    articleWithSecondType.setDate(new Date());

    Article articleWithSecondType2 = new Article();
    articleWithSecondType2.setDoi("id://test-article-4");
    articleWithSecondType2.setTypes(secondType);
    articleWithSecondType2.setDate(new Date());

    dummyDataStore.store(articleWithBothTypes);
    dummyDataStore.store(articleWithFirstType);
    dummyDataStore.store(articleWithSecondType);
    dummyDataStore.store(articleWithSecondType2);

    List<URI> articleList = new ArrayList<URI>();
    articleList.add(URI.create(articleWithBothTypes.getDoi()));
    articleList.add(URI.create(articleWithFirstType.getDoi()));
    articleList.add(URI.create(articleWithSecondType.getDoi()));
    articleList.add(URI.create(articleWithSecondType2.getDoi()));

    Issue issue = new Issue();
    issue.setArticleList(articleList);
    issue.setSimpleCollection(articleList);
    issue.setId(URI.create(dummyDataStore.store(issue)));

    Map<String, Integer> numExpectedPerGroup = new HashMap<String, Integer>(2);
    numExpectedPerGroup.put(articleType1, 2);
    numExpectedPerGroup.put(articleType2, 3);

    return new Object[][]{
        {issue, numExpectedPerGroup}
    };
  }

  @Test(dataProvider = "articleGroupList")
  public void testGetArticleGroupList(Issue issue, Map<URI, Integer> numExpectedPerGroup) {
    List<TOCArticleGroup> results = browseService.getArticleGrpList(issue, DEFAULT_ADMIN_AUTHID);
    assertNotNull(results, "returned null article group list");
    assertEquals(results.size(), numExpectedPerGroup.size(), "returned incorrect number of groups");
    for (TOCArticleGroup articleGroup : results) {
      Integer expectedCount = numExpectedPerGroup.get(articleGroup.getArticleType().getUri().toString());
      assertNotNull(expectedCount,
          "returned group for unexpected article type: " + articleGroup.getArticleType().getUri());
      assertEquals(articleGroup.getCount(), expectedCount.intValue(),
          "returned incorrect number of articles for article group: " + articleGroup);
    }
  }

  @DataProvider(name = "articleGroupListWithIssueId")
  public Object[][] getArticleGroupListWithIssueId() {
    Issue issue = (Issue) getArticleGroupList()[0][0];
    Map<URI, Integer> numExpectedPerGroup = (Map<URI, Integer>) getArticleGroupList()[0][1];
    return new Object[][]{
        {issue.getId(), numExpectedPerGroup}
    };
  }

  @Test(dataProvider = "articleGroupListWithIssueId")
  public void testGetArticleGroupListByIssueId(URI issueId, Map<URI, Integer> numExpectedPerGroup) {
    List<TOCArticleGroup> results = browseService.getArticleGrpList(issueId, DEFAULT_ADMIN_AUTHID);
    assertNotNull(results, "returned null article group list");
    assertEquals(results.size(), numExpectedPerGroup.size(), "returned incorrect number of groups");
    for (TOCArticleGroup articleGroup : results) {
      Integer expectedCount = numExpectedPerGroup.get(articleGroup.getArticleType().getUri().toString());
      assertNotNull(expectedCount,
          "returned group for unexpected article type: " + articleGroup.getArticleType().getUri());
      assertEquals(articleGroup.getCount(), expectedCount.intValue(),
          "returned incorrect number of articles for article group");
    }
  }

  @Test(dataProvider = "articleGroupList",
      dependsOnMethods = {"testGetArticleGroupListByIssueId", "testGetArticleGroupList"},
      alwaysRun = true, ignoreMissingDependencies = true)
  public void testBuildArticleGroups(Issue issue, Map<URI, Integer> notUsedInThisTest) {
    List<TOCArticleGroup> articleGroups = browseService.getArticleGrpList(issue, DEFAULT_ADMIN_AUTHID);

    List<TOCArticleGroup> builtArticleGroups = browseService.buildArticleGroups(issue, articleGroups, DEFAULT_ADMIN_AUTHID);

    assertNotNull(builtArticleGroups, "returned null list of built article groups");
    assertEquals(builtArticleGroups.size(), articleGroups.size(), "returned incorrect number of article groups");
    for (int i = 0; i < builtArticleGroups.size(); i++) {
      TOCArticleGroup builtGroup = builtArticleGroups.get(i);
      TOCArticleGroup group = articleGroups.get(i);
      assertNotNull(builtGroup, "returned null built article group");
      assertEquals(builtGroup.getArticleType(), group.getArticleType(),
          "returned built group with incorrect article type");
      assertEquals(builtGroup.getCount(), group.getCount(), "returned article group with incorrect article count");
    }
  }

  @Test(dataProvider = "articleGroupList",
      dependsOnMethods = {"testGetArticleGroupListByIssueId", "testGetArticleGroupList"},
      alwaysRun = true, ignoreMissingDependencies = true)
  public void testGetArticleGrpListToCsv(Issue issue, Map<URI, Integer> notUsedInThisTest) {
    List<TOCArticleGroup> articleGroups = browseService.getArticleGrpList(issue, DEFAULT_ADMIN_AUTHID);

    String csv = browseService.articleGrpListToCSV(articleGroups);
    assertNotNull(csv, "returned null csv");
    assertTrue(csv.length() > 0, "returned empty csv");
    for (TOCArticleGroup articleGroup : articleGroups) {
      for (ArticleInfo articleInfo : articleGroup.getArticles()) {
        assertTrue(csv.indexOf(articleInfo.getDoi()) != -1,
            "csv didn't contain expected article id: " + articleInfo.getDoi());
      }
    }

  }

  @DataProvider(name = "latestIssue")
  public Object[][] getLatestIssue() {
    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DAY_OF_MONTH, -1);

    Issue olderIssue = new Issue();
    olderIssue.setCreated(yesterday.getTime());
    olderIssue.setId(URI.create("info:doi/old.issue"));

    Issue newerIssue = new Issue();
    newerIssue.setCreated(new Date());
    newerIssue.setId(URI.create("info:doi/new.issue"));

    List<URI> issues = new ArrayList<URI>(2);
    issues.add(URI.create(dummyDataStore.store(olderIssue)));
    issues.add(URI.create(dummyDataStore.store(newerIssue)));

    Volume olderVolume = new Volume();
    olderVolume.setCreated(yesterday.getTime());
    olderVolume.setId(URI.create("info:doi/old.volume"));
    olderVolume.setIssueList(issues);

    Volume newerVolume = new Volume();
    newerVolume.setCreated(new Date());
    newerVolume.setId(URI.create("info:doi/new.volume"));
    newerVolume.setIssueList(issues);

    List<URI> volumes = new ArrayList<URI>(2);
    volumes.add(URI.create(dummyDataStore.store(newerVolume)));
    volumes.add(URI.create(dummyDataStore.store(olderVolume)));

    Journal journal = new Journal();
    journal.setVolumes(volumes);
    journal.setId(URI.create(dummyDataStore.store(journal)));

    return new Object[][]{
        {journal, newerIssue.getId()}
    };
  }

  @Test(dataProvider = "latestIssue")
  public void testGetLatestIssueFromLatestVolume(Journal journal, URI expectedURI) {
    URI result = browseService.getLatestIssueFromLatestVolume(journal);
    assertNotNull(result, "returned null URI");
    assertEquals(result, expectedURI, "returned incorrect URI");
  }


}
