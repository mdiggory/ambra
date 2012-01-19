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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.rowset.serial.SerialBlob;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Blob;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test for the {@link org.ambraproject.article.service.ArticleDocumentService} implementation. For the hibernate
 * implementation, working directory for the test must be either the new hope base directory or the ambra webapp home
 * directory
 *
 * @author Alex Kudlick Date: 5/12/11
 *         <p/>
 *         org.ambraproject.article.service
 */
public class ArticleDocumentServiceTest extends BaseTest {

  @Autowired
  protected ArticleDocumentService articleDocumentService;
  @Autowired
  protected DocumentBuilderFactory documentBuilderFactory;

  @DirtiesContext
  @Test(priority = 1)
  public void clearTest () {
    // Insure that we reset the datbase before running this test.
  }

  @DataProvider(name = "blob")
  public Object[][] blob() throws Exception {
    String xml = "<xml><childNode>some test xml</childNode></xml>";
    Document parsedXml = documentBuilderFactory
        .newDocumentBuilder()
        .parse(new ByteArrayInputStream(xml.getBytes()));

    return new Object[][]{
        {new SerialBlob(xml.getBytes()), parsedXml}
    };
  }

  @Test(dataProvider = "blob")
  public void testParseBlob(Blob blob, Document expectedXml) throws Exception {
    Document xml = articleDocumentService.getDocument(blob);
    assertNotNull(xml, "returned null xml");
    assertEquals(xml.getElementsByTagName("*").getLength(),
        expectedXml.getElementsByTagName("*").getLength(),
        "returned xml with incorrect number of tags");
  }

  @DataProvider(name = "articleId")
  public Object[][] articleId() {
    return new Object[][]{
        {"info:doi/10.1371/journal.pgen.1000096", "10.1371/journal.pgen.1000096"}
    };
  }

  @Test(dataProvider = "articleId")
  public void testGetFullXml(String articleId, String doi) throws Exception {
    Document xml = articleDocumentService.getFullDocument(articleId);
    assertNotNull(xml, "returned null xml document");

    //check the doi from the xml
    NodeList articleIdNodes = xml.getElementsByTagName("article-id");
    for (int i = 0; i < articleIdNodes.getLength(); i++) {
      Node node = articleIdNodes.item(i);
      if (node.getAttributes().getNamedItem("pub-id-type").getNodeValue().equals("doi")) {
        assertEquals(node.getChildNodes().item(0).getNodeValue(), doi, "returned article xml with incorrect doi");
        break;
      }
    }
  }
}
