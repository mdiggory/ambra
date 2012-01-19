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

package org.ambraproject.admin.service;

import org.ambraproject.BaseTest;
import org.ambraproject.models.Article;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Volume;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudick  1/3/12
 */
public class AdminServiceTest extends BaseTest {

  @Autowired
  protected AdminService adminService;

  @DataProvider(name = "issueToUpdate")
  public Object[][] getIssuesToUpdate() {
    URI blankIssue = URI.create(dummyDataStore.store(new Issue()));

    Article imageArticle = new Article();
    imageArticle.setDoi("id:foo-doi-for-updating-issue");
    imageArticle.setDescription("This description should overwrite what's on the issue");
    imageArticle.setTitle("This title should overwrite what's on the issue");
    dummyDataStore.store(imageArticle);

    Issue existingIssue = new Issue();
    existingIssue.setTitle("title should get overwritten");
    existingIssue.setDescription("description should get overwritten");
    existingIssue.setDisplayName("old display name");
    existingIssue.setArticleList(new ArrayList<URI>());
    existingIssue.getArticleList().add(URI.create("id:issue-article-1"));
    existingIssue.getArticleList().add(URI.create("id:issue-article-2"));
    existingIssue.getArticleList().add(URI.create("id:issue-article-3"));

    dummyDataStore.store(existingIssue);

    List<URI> addedArticleList = new ArrayList<URI>(existingIssue.getArticleList());
    addedArticleList.add(URI.create("id:issue-article-4"));

    List<URI> removedArticleList = new ArrayList<URI>(existingIssue.getArticleList());
    removedArticleList.remove(1);

    return new Object[][]{
        {blankIssue, imageArticle, "brand new display name", new ArrayList<URI>(0), false},
        {blankIssue, imageArticle, "added an article", addedArticleList, true},
        {blankIssue, imageArticle, "removed an article", removedArticleList, true}
    };
  }

  @Test(dataProvider = "issueToUpdate")
  public void testUpdateIssue(URI issueId, Article imageArticle, String displayName, List<URI> articleList, boolean respectOrder) throws URISyntaxException {
    Issue result = adminService.updateIssue(issueId, URI.create(imageArticle.getDoi()),
        displayName, articleList, respectOrder);
    assertNotNull(result, "returned null issue");
    assertEquals(result.getId(), issueId, "returned issue with incorrect id");
    assertEquals(result.getArticleList().toArray(), articleList.toArray(), "issue had incorrect article list");
    assertEquals(result.getImage(), URI.create(imageArticle.getDoi()), "issue had incorrect image uri");
    assertEquals(result.getDescription(), imageArticle.getDescription(), "issue didn't get description updated from image article");
    assertEquals(result.getTitle(), imageArticle.getTitle(), "issue didn't get title updated from article");
    assertEquals(result.getDisplayName(), displayName, "issue had incorrect display name");
    assertEquals(result.getRespectOrder(), respectOrder, "issue had incorrect respectOrder attribute");

    //check the properties on the issue from the db
    Issue storedIssue = dummyDataStore.get(issueId, Issue.class);
    assertEquals(storedIssue.getArticleList().toArray(), articleList.toArray(), "issue with incorrect id");
    assertEquals(storedIssue.getImage(), URI.create(imageArticle.getDoi()), "issue had incorrect image uri");
    assertEquals(storedIssue.getDescription(), imageArticle.getDescription(), "issue didn't get description updated from image article");
    assertEquals(storedIssue.getTitle(), imageArticle.getTitle(), "issue didn't get title updated from article");
    assertEquals(storedIssue.getDisplayName(), displayName, "issue had incorrect display name");
    assertEquals(storedIssue.getRespectOrder(), respectOrder, "issue had incorrect respectOrder attribute");
  }

  @DataProvider(name = "createIssue")
  public Object[][] getIssueToCreate() {
    Article article = new Article();
    article.setDoi("id:doi-for-creating-issue");
    article.setTitle("Once Upon a Time");
    article.setDescription("Centers on a woman with a troubled past who is drawn into a small town in " +
        "Maine where the magic and mystery of Fairy Tales just may be real. ");
    dummyDataStore.store(article);
    Volume volume = new Volume();
    dummyDataStore.store(volume);

    List<URI> expectedArticleList = new ArrayList<URI>(3);
    expectedArticleList.add(URI.create("id:for-creating-issue1"));
    expectedArticleList.add(URI.create("id:for-creating-issue2"));
    expectedArticleList.add(URI.create("id:for-creating-issue3"));

    String articleCsv = StringUtils.join(expectedArticleList, ",");
    
    return new Object[][]{
        {volume, URI.create("id:new-issue-uri"), article, "some new display name", articleCsv, expectedArticleList}
    };
  }
  
  @Test(dataProvider = "createIssue")
  public void testCreateIssue(Volume vol, URI issueURI, Article imageArticle, 
                              String displayName, String articleListCsv, List<URI> expectedArticleList) {
    Issue issue = adminService.createIssue(vol, issueURI, URI.create(imageArticle.getDoi()), displayName, articleListCsv);
    assertNotNull(issue, "created null issue");
    assertEquals(issue.getId(), issueURI, "issue had incorrect id");
    assertEquals(issue.getArticleList().toArray(), expectedArticleList.toArray(), "issue had incorrect article list");
    assertEquals(issue.getImage(), URI.create(imageArticle.getDoi()), "issue had incorrect image uri");
    assertEquals(issue.getDescription(), imageArticle.getDescription(), "issue didn't get description updated from image article");
    assertEquals(issue.getTitle(), imageArticle.getTitle(), "issue didn't get title updated from article");
    assertEquals(issue.getDisplayName(), displayName, "issue had incorrect display name");

    Volume storedVolume = dummyDataStore.get(vol.getId(), Volume.class);
    assertTrue(storedVolume.getIssueList().contains(issue.getId()), "issue didn't get added to volume");

    Issue storedIssue = dummyDataStore.get(issueURI, Issue.class);
    assertEquals(storedIssue.getArticleList().toArray(), expectedArticleList.toArray(), "storedIssue had incorrect article list");
    assertEquals(storedIssue.getImage(), URI.create(imageArticle.getDoi()), "storedIssue had incorrect image uri");
    assertEquals(storedIssue.getDescription(), imageArticle.getDescription(), "storedIssue didn't get description updated from image article");
    assertEquals(storedIssue.getTitle(), imageArticle.getTitle(), "storedIssue didn't get title updated from article");
    assertEquals(storedIssue.getDisplayName(), displayName, "storedIssue had incorrect display name");
  }
}
