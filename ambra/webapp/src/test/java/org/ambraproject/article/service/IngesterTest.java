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

import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.models.Issue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.testng.Assert.*;

/**
 * Test for implementation of ingester.  Working directory for the test should be set to the ambra webapp home
 * directory
 *
 * @author Alex Kudlick Date: 5/11/11
 *         <p/>
 *         org.ambraproject.article.service
 */
public class IngesterTest extends BaseTest {

  @Autowired
  protected Ingester ingester;

  @Qualifier("filestoreDir")
  @Autowired
  protected String fileStoreDir; 

  private static final String[] ingestedDirectories =
      new String[] {"pmed.0050082","pgen.1002295","pntd.0000241","pmed.1001027","image.pcol.v01.i10"};

  //clean up after the test
  @AfterClass
  public void deleteIngestedDir() throws IOException {
    File baseDir = new File(fileStoreDir, "10.1371");
    if (baseDir.exists()) {
      for (String dir : ingestedDirectories) {
        FileUtils.deleteDirectory(new File(baseDir, dir));
      }
    }
  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleArticle")
  public void testIngest(ZipFile ingestArchive, Article expectedArticle) throws Exception {
    Long[] relatedArticleIds = storeRelatedArticles();
    //Article Relationship that should be included in the ingested article
    ArticleRelationship relationship3 = new ArticleRelationship();
    relationship3.setOtherArticleDoi("info:doi/10.1371/journal.pbio.0020034");
    relationship3.setType("companion");
    relationship3.setParentArticle(expectedArticle);
    expectedArticle.getRelatedArticles().add(relationship3);

    Article ingestedArticle = ingester.ingest(ingestArchive, false);
    compareArticles(ingestedArticle, expectedArticle);

    Article storedArticle = dummyDataStore.get(ingestedArticle.getID(), Article.class);
    assertNotNull(storedArticle, "Article wasn't stored to the db");
    compareArticles(storedArticle, expectedArticle);
    assertNotNull(storedArticle.getCreated(), "Article didn't get create date set");

    checkRelatedArticles(relatedArticleIds, ingestedArticle);
  }

  private void checkRelatedArticles(Long[] otherArticleIds, Article ingestedArticle) {
    for (Long id : otherArticleIds) {
      Article otherArticle = dummyDataStore.get(id, Article.class);
      boolean foundMatch = false;
      for (ArticleRelationship relationship : otherArticle.getRelatedArticles()) {
        if (relationship.getOtherArticleDoi().equals(ingestedArticle.getDoi())) {
          assertEquals(relationship.getOtherArticleID(),ingestedArticle.getID(),
              "Related article didn't get 'OtherArticleID' set correctly");
          foundMatch = true;
          break;
        }
      }
      assertTrue(foundMatch, "Article " + otherArticle + " didn't get a relationship linking to new article");
    }
  }

  /**
   * Store articles that the ones to be ingested relate to, so we can check the reciprocal linking on related articles
   * @return an array  of ids for articles that should get relationships added to the new article
   */
  public Long[] storeRelatedArticles() {
    //First article to relate to with an existing 'relationship' object. This should get it's 'otherArticleID' set
    Article articleWithExistingRelationship = new Article();
    articleWithExistingRelationship.setDoi("info:doi/10.1371/journal.pmed.0050061");
    List<ArticleRelationship> relationships = new ArrayList<ArticleRelationship>(1);
    relationships.add(new ArticleRelationship());
    relationships.get(0).setParentArticle(articleWithExistingRelationship);
    relationships.get(0).setOtherArticleDoi("info:doi/10.1371/journal.pmed.0050082");
    relationships.get(0).setType("companion");
    articleWithExistingRelationship.setRelatedArticles(relationships);
    Long id1 = Long.valueOf(dummyDataStore.store(articleWithExistingRelationship));

    //Second article to relate to.  This should get a relationship added
    Article articleWithNoRelationship = new Article();
    articleWithNoRelationship.setDoi("info:doi/10.1371/journal.pbio.0000064");
    Long id2 = Long.valueOf(dummyDataStore.store(articleWithNoRelationship));
    
    //Third article that relates to new article, but is not linked to by the new article
    Article articleWithRelationshipNotLinkedTo = new Article();
    articleWithRelationshipNotLinkedTo.setDoi("info:doi/10.1371/journal.pbio.0020034");
    List<ArticleRelationship> relationships1 = new ArrayList<ArticleRelationship>(1);
    relationships1.add(new ArticleRelationship());
    relationships1.get(0).setOtherArticleDoi("info:doi/10.1371/journal.pmed.0050082");
    relationships1.get(0).setType("companion");
    relationships1.get(0).setParentArticle(articleWithRelationshipNotLinkedTo);
    articleWithRelationshipNotLinkedTo.setRelatedArticles(relationships1);

    Long id3 = Long.valueOf(dummyDataStore.store(articleWithRelationshipNotLinkedTo));
    return new Long[]{id1, id2, id3};
  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleArticle",
      dependsOnMethods = "testIngest", expectedExceptions = DuplicateArticleIdException.class)
  public void testDuplicateArticleException(ZipFile ingestArchive, Article notUsed) throws DuplicateArticleIdException, IngestException {
    ingester.ingest(ingestArchive, false);
  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleArticle",
      dependsOnMethods = {"testIngest", "testDuplicateArticleException"})
  public void testIngestWithForce(ZipFile ingestArchive, Article expectedArticle) throws DuplicateArticleIdException, IngestException {
    //Article Relationship that should be included in the ingested article
    ArticleRelationship relationship3 = new ArticleRelationship();
    relationship3.setOtherArticleDoi("info:doi/10.1371/journal.pbio.0020034");
    relationship3.setType("companion");
    relationship3.setParentArticle(expectedArticle);
    expectedArticle.getRelatedArticles().add(relationship3);


    Long id = ingester.ingest(ingestArchive, true).getID();
    Article storedArticle = dummyDataStore.get(id, Article.class);
    assertNotNull(storedArticle, "Article wasn't stored to the db");
    compareArticles(storedArticle, expectedArticle);
  }
  
  //Check that ingest didn't create duplicate categories 
  @Test(dependsOnMethods = {"testIngestWithForce", "testIngestWithAssetsAndForce"})
  public void testIngestDoesntCreateDuplicateCategories() {
    Set<Category> categories = new HashSet<Category>(9);
    List<Category> storedCategories = dummyDataStore.getAll(Category.class);
    assertTrue(storedCategories.size() > 0, "didn't store any categories to the database");
    for (Category category : storedCategories) {
      assertFalse(categories.contains(category), "Stored duplicate category " + category);
      categories.add(category);
    }
  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleAssets",
      dependsOnMethods = {"testIngestWithForce"}, alwaysRun = true)
  public void testIngestWithAssets(ZipFile archive, List<ArticleAsset> expectedAssets) throws DuplicateArticleIdException, IngestException, NoSuchArticleIdException {
    final Article article = ingester.ingest(archive, false);
    List<ArticleAsset> actualAssets = article.getAssets();

    assertNotNull(actualAssets, "had null list of assets");
    assertEquals(actualAssets.size(), expectedAssets.size(), "incorrect number of assets");
    for (int i = 0; i < actualAssets.size(); i++) {
      compareAssets(actualAssets.get(i), expectedAssets.get(i));
    }

    List<ArticleAsset> storedAssets = null;
    try {
      storedAssets = dummyDataStore.get(article.getID(), Article.class).getAssets();
    } catch (NullPointerException e) {
      fail("Article wasn't stored to the db");
    }

    assertNotNull(storedAssets, "stored article had null list of assets");
    assertEquals(storedAssets.size(), expectedAssets.size(), "stored article had incorrect number of assets");
    for (int i = 0; i < storedAssets.size(); i++) {
      compareAssets(storedAssets.get(i), expectedAssets.get(i));
    }

  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleAssets",
      dependsOnMethods = "testIngestWithAssets")
  public void testIngestWithAssetsAndForce(ZipFile archive, List<ArticleAsset> notUsed) throws DuplicateArticleIdException, IngestException {
    ingester.ingest(archive, true);
  }

  @DataProvider(name = "imageArticle")
  public Object[][] getImageArticle() throws Exception {
    File testFile = new File(IngesterTest.class.getClassLoader().getResource("test-ingest-image-article.zip").toURI());
    ZipFile archive = new ZipFile(testFile);    
    //put the article in the db so we can reingest it
    Article article = new Article();
    article.setDoi("info:doi/10.1371/image.pcol.v01.i10");
    dummyDataStore.store(article);

    Issue issue = new Issue();
    issue.setImage(URI.create(article.getDoi()));
    issue.setDescription("This description should get overwritten");
    String issueId = dummyDataStore.store(issue);
    
    return new Object[][]{
        {archive, URI.create(issueId)}
    };
  }
  
  @Test(dataProvider = "imageArticle", dependsOnMethods = {"testIngestWithForce", "testIngestDoesntCreateDuplicateCategories"})
  public void testReIngestImageArticle(ZipFile archive, URI issueId) throws DuplicateArticleIdException, IngestException {
    Article article = ingester.ingest(archive, true);
    Issue issue = dummyDataStore.get(issueId, Issue.class);
    assertEquals(issue.getDescription(), article.getDescription(),
        "issue for image article didn't have description updated");
  }

  /**
   * Regression test for the bug described <a href="https://developer.plos.org/jira/browse/NHOPE-222">here</a>.  Ingest
   * an article, then add images to the zip, and try to reingest.
   *
   * @param originalArchive
   * @param alteredArchive
   * @param expectedPartsDois
   * @throws Exception
   */
  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "alteredZip",
      groups = "regressionTest", dependsOnMethods = {"testIngest", "testIngestWithForce",
      "testIngestWithAssets", "testIngestWithAssetsAndForce","testIngestDoesntCreateDuplicateCategories"}, alwaysRun = true)
  public void testForceIngestAfterZipHasChanged(ZipFile originalArchive, ZipFile alteredArchive, List<String> expectedPartsDois) throws Exception {

    Article article = ingester.ingest(originalArchive, false); //get the original article in the db
    try {
      article = ingester.ingest(alteredArchive, true);//force ingestion
    } catch (Exception e) {
      fail("There was an error ingesting the altered archive", e);
    }

    Article storedArticle = dummyDataStore.get(article.getID(), Article.class);

    assertEquals(article.getAssets().size(), expectedPartsDois.size(), "stored incorrect number of parts");
    //get the parts' ids into a list to compare them
    List<String> actualPartsDois = new ArrayList<String>(expectedPartsDois.size());
    for (ArticleAsset part : storedArticle.getAssets()) {
      assertNotNull(part, "parts didn't get stored to the db");
      actualPartsDois.add(part.getDoi());
    }
    assertEquals(actualPartsDois.toArray(), expectedPartsDois.toArray(), "stored incorrect part ids");

    try {
      ingester.ingest(originalArchive, true); //re-ingest the original archive
    } catch (Exception e) {
      fail("There was an error reingesting the original archive", e);
    }
  }
}
