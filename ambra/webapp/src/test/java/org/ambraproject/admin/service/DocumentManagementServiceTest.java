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

import org.custommonkey.xmlunit.XMLUnit;
import org.ambraproject.filestore.FSIDMapper;
import org.ambraproject.filestore.FileStoreException;
import org.ambraproject.filestore.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.UnexpectedRollbackException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.models.Article;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Joe Osowski
 */
public class DocumentManagementServiceTest extends BaseTest {
  @Autowired
  protected DocumentManagementService documentManagementService;
  @Autowired
  protected ArticleService articleService;
  @Autowired
  protected FileStoreService fileStoreService;

  final String articleArchive = "pone.0000202.zip";
  final String articleXmlFile = "info_doi_10_1371_journal_pone_0000202.xml";
  final String articleUri = "info:doi/10.1371/journal.pone.0000202";
  final String articleUri2 = "info:doi/10.1371/journal.pone.0000203";
  final String articleUri3 = "info:doi/10.1371/journal.pone.0000204";
  final String crossRefArticleXML = "pone.0000008.xml";
  final String crossRefArticleDOI = "info:doi/10.1371/journal.pone.0000008";
  final String crossRefArticleResultXML = "info_doi_10_1371_journal_pone_0000008.xml";
  final String crossRefArticleResultTestXML = "pone.0000008.crossref.xml";

  @DataProvider(name = "storedUnpublishedArticles")
  public Object[][] getStoredUnpublishedArticles()
  {
    Article article = new Article();
    article.setState(Article.STATE_UNPUBLISHED);
    article.setDoi(articleUri);

    dummyDataStore.store(article);

    return new Object[][] {
      { articleUri }
    };
  }

  @DataProvider(name = "storedPublishedArticles")
  public Object[][] getStoredpublishedArticles()
  {
    Article article = new Article();
    article.setState(Article.STATE_ACTIVE);
    article.setDoi(articleUri2);

    dummyDataStore.store(article);

    return new Object[][] {
      { articleUri2 }
    };
  }

  @DataProvider(name = "storedPublishedArticles2")
  public Object[][] getStoredpublishedArticles2()
  {
    Article article = new Article();
    article.setState(Article.STATE_ACTIVE);
    article.setDoi(articleUri3);

    dummyDataStore.store(article);

    return new Object[][] {
      { articleUri3 }
    };
  }

  @Test(dataProvider = "storedUnpublishedArticles")
  public void testGenerateIngestedData(String article) throws IOException, NoSuchArticleIdException
  {
    //I don't use the article ID passed in as it should just be the constant "articleUri".
    //But the dataprovider needed to be called to be sure the dummyDataStore is populated.

    //Set up temp files.
    String ingestDir = documentManagementService.getDocumentDirectory();
    String ingestedDir = documentManagementService.getIngestedDocumentDirectory();

    String sourceFile = ingestDir + "/" + articleArchive;
    String destFile = ingestedDir + "/" + articleArchive;

    assertTrue((new File(sourceFile)).createNewFile(), "Couldn't create temp file.");
    assertTrue((new File(destFile)).createNewFile(), "Couldn't create temp file.");

    //Test that the dest file is overwritten and the source is moved
    Article a = articleService.getArticle(articleUri, DEFAULT_ADMIN_AUTHID);
    documentManagementService.generateIngestedData(new File(sourceFile), a.getDoi());

    //Test that the file exists
    assertTrue((new File(destFile)).exists(), "File doesn't exist: " + destFile);

    //Reset state
    assertTrue((new File(destFile)).delete(), "Can't delete file: " + destFile);

    //Now test the source is moved
    assertTrue((new File(sourceFile)).createNewFile(), "Couldn't create temp file.");
    documentManagementService.generateIngestedData(new File(sourceFile), a.getDoi());

    assertFalse((new File(sourceFile)).exists(), "Old file not moved: " + sourceFile);
    assertTrue((new File(destFile)).exists(), "New file not moved to correct place: " + sourceFile);

    //Reset state
    assertTrue((new File(destFile)).delete(), "Can't delete file: " + destFile);  }

  @Test
  public void testGenerateCrossrefInfoDoc() throws FileNotFoundException, IOException, SAXException,
    TransformerException
  {
    String ingestedDir = documentManagementService.getIngestedDocumentDirectory();
    InputStream xml = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(crossRefArticleXML);
    Document doc = XMLUnit.buildTestDocument(new InputSource(xml));

    documentManagementService.generateCrossrefInfoDoc(doc, URI.create(crossRefArticleDOI));

    //Assert that file was created.
    File f = new File(ingestedDir + "/" + crossRefArticleResultXML);

    assertTrue(f.exists(), "File does not exist:" + ingestedDir + "/" + crossRefArticleResultXML);

    //Compare the resulting file with a base line
    InputStream xml1 = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(crossRefArticleResultTestXML);

    XMLUnit.compareXML(new InputSource(xml1),new InputSource(new FileInputStream(f)));

    //cleanup
    assertTrue(f.delete(), "File was not removed: " + ingestedDir + "/" + crossRefArticleResultXML);
  }

  @Test(dataProvider = "storedPublishedArticles")
  void testDisable(String article) throws Exception
  {
    final String file1 = FSIDMapper.doiTofsid(article + ".fileone", "txt");
    final String file2 = FSIDMapper.doiTofsid(article + ".filetwo", "txt");

    //Create some files to remove from the filestore
    OutputStream fs = fileStoreService.getFileOutStream(file1, 50);
    fs.write("File left blank".getBytes());
    fs.close();

    fs = fileStoreService.getFileOutStream(file2, 50);
    fs.write("File left blank".getBytes());
    fs.close();

    //Check article initial state
    Article a = articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
    assertEquals(a.getState(),Article.STATE_ACTIVE, "Article state not Active");

    documentManagementService.disable(article, DEFAULT_ADMIN_AUTHID);

    try {
      articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
      fail("Article not disabled");
    } catch (NoSuchArticleIdException ex) {}

    //assert removed from file system
    try {
      fileStoreService.getFileByteArray(file1);
      fail("File not deleted: " + file1);
    } catch (FileStoreException ex) {}

    try {
      fileStoreService.getFileByteArray(file2);
      fail("File not deleted: " + file2);
    } catch (FileStoreException ex) {}
  }

  @Test(expectedExceptions = { SecurityException.class })
  void testDisableSecurity() throws Exception
  {
    documentManagementService.disable(articleUri, DEFUALT_USER_AUTHID);
  }

  @Test(dataProvider = "storedPublishedArticles")
  void testDelete(String article) throws Exception
  {
    //TODO: SQL too complex for testing DB to understand.
    //Refactor in the future
    //documentManagementService.delete();
  }

  @Test(dataProvider = "storedPublishedArticles", expectedExceptions = { SecurityException.class })
  void testDeleteSecurity(String article) throws Exception
  {
    documentManagementService.delete(article, DEFUALT_USER_AUTHID);
  }

  @Test(dataProvider = "storedPublishedArticles2")
  void testUnPublish(String article) throws Exception
  {
    Article a = articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
    assertEquals(a.getState(), Article.STATE_ACTIVE, "Article not set as published: " + article);

    documentManagementService.unPublish(article, DEFAULT_ADMIN_AUTHID);

    a = articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
    assertEquals(a.getState(), Article.STATE_UNPUBLISHED, "Article not set as unpublished: " + article);
  }

  @Test(dataProvider = "storedPublishedArticles2", expectedExceptions = { SecurityException.class })
  void testUnPublishSecurity(String article) throws Exception
  {
    documentManagementService.unPublish(article, DEFUALT_USER_AUTHID);
  }

  @Test(dataProvider = "storedUnpublishedArticles")
  void testPublish(String article) throws NoSuchArticleIdException
  {
    Article a = articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
    assertEquals(a.getState(), Article.STATE_UNPUBLISHED, "Article not set as unpublished: " + article);

    documentManagementService.publish(new String[] { article }, DEFAULT_ADMIN_AUTHID);

    a = articleService.getArticle(article, DEFAULT_ADMIN_AUTHID);
    assertEquals(a.getState(), Article.STATE_ACTIVE, "Article not set as published: " + article);
  }

  @Test(dataProvider = "storedUnpublishedArticles", expectedExceptions = { UnexpectedRollbackException.class })
  void testPublishSecurity(String article)
  {
    documentManagementService.publish(new String[] { article }, DEFUALT_USER_AUTHID);
  }

  @Test
  void testGetUploadableFiles()
  {
    List<String> files = documentManagementService.getUploadableFiles();
    String ingestDir = documentManagementService.getDocumentDirectory();

    Object[] files2 = (new File(ingestDir)).list(new FilenameFilter() {
      @Override
      public boolean accept(File file, String s) {
        return s.endsWith("zip");
      }
    });

    assertEqualsNoOrder(files.toArray(), files2, "Uploadable files list differs.");
  }

  @Test
  void testRevertIngestedQueue() throws IOException
  {
    //Set up a file to be moved back from the ingest queue
    //Method shouldn't actually operate on file contents.  So I create temp files for this test
    String ingestDir = documentManagementService.getDocumentDirectory();
    String ingestedDir = documentManagementService.getIngestedDocumentDirectory();

    assertTrue((new File(ingestedDir + "/" + articleArchive)).createNewFile(), "Couldn't create temp file.");
    assertTrue((new File(ingestedDir + "/" + articleXmlFile)).createNewFile(), "Couldn't create temp file.");

    documentManagementService.revertIngestedQueue(articleUri);

    assertFalse((new File(ingestedDir, articleArchive)).exists(), "Archive file not reverted: " + articleArchive);
    assertFalse((new File(ingestedDir, articleXmlFile)).exists(), "Archive XML file not reverted: " + articleXmlFile);

    File ingestFile = new File(ingestDir, articleArchive);

    //Confirm file existance and clean up
    assertTrue(ingestFile.delete(), "File " + articleArchive + " has not been moved back to the ingest folder.");
  }
}
