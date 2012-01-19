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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.article.ArchiveProcessException;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;
import java.util.zip.ZipFile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudlick Date: 6/7/11
 *         <p/>
 *         org.ambraproject.article.service
 */
public class IngestArchiveProcessorTest extends BaseTest {

  @Autowired
  protected IngestArchiveProcessor ingestArchiveProcessor;

  @Test(dataProvider = "sampleArticle", dataProviderClass = SampleArticleData.class)
  public void testProcessArticle(ZipFile archive, Article expectedArticle) throws Exception {
    Document articleXml = ingestArchiveProcessor.extractArticleXml(archive);
    Article result = ingestArchiveProcessor.processArticle(archive, articleXml);
    compareArticles(result, expectedArticle);
    String archiveName = archive.getName().contains(File.separator)
        ? archive.getName().substring(archive.getName().lastIndexOf(File.separator) + 1)
        : archive.getName();
    assertEquals(result.getArchiveName(), archiveName, "Article didn't have archive name set correctly");
  }

  @Test(dataProviderClass = SampleArticleData.class, dataProvider = "sampleAssets",
      dependsOnMethods = "testProcessArticle", alwaysRun = true)
  public void testParseWithSecondaryObjects(ZipFile archive, List<ArticleAsset> expectedAssets) throws ArchiveProcessException {
    Document articleXml = ingestArchiveProcessor.extractArticleXml(archive);
    Article result = ingestArchiveProcessor.processArticle(archive, articleXml);
    assertNotNull(result, "Returned null article");
    assertNotNull(result.getAssets(), "returned null asset list");
    assertEquals(result.getAssets().size(), expectedAssets.size(), "returned incorrect number of assets");

    for (int i = 0; i < result.getAssets().size(); i++) {
      compareAssets(result.getAssets().get(i), expectedAssets.get(i));
    }
    String archiveName = archive.getName().contains(File.separator)
        ? archive.getName().substring(archive.getName().lastIndexOf(File.separator) + 1)
        : archive.getName();
    assertEquals(result.getArchiveName(), archiveName, "Article didn't have archive name set correctly");
  }

}
