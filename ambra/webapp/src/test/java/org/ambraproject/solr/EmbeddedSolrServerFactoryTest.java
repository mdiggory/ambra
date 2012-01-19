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

package org.ambraproject.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.testutils.EmbeddedSolrServerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Basic test for {@link EmbeddedSolrServerFactory} . The working directory for this test needs to be set to the ambra
 * webapp home, so that the solr server factory can find the config files
 *
 * @author Alex Kudlick Date: 5/13/11
 *         <p/>
 *         org.topazproject.ambra
 */
public class EmbeddedSolrServerFactoryTest {

  private static EmbeddedSolrServerFactory solrServerFactory;


  @Test(priority = -1)
  public void testConstructor() throws Exception {
    String schemaFile = this.getClass().getClassLoader().getResource("solr/test-solr-schema.xml").getFile();
    String configFile = this.getClass().getClassLoader().getResource("solr/test-solr-config.xml").getFile();

    solrServerFactory = new EmbeddedSolrServerFactory(schemaFile, configFile);
    solrServerFactory = new EmbeddedSolrServerFactory();
  }

  @Test
  public void testGetServer() throws SolrServerException {
    SolrServer server = solrServerFactory.getServer();
    assertNotNull(server, "created null server");
  }

  @Test(dependsOnMethods = {"testGetServer"})
  public void testBadQuery() throws SolrServerException {
    SolrServer server = solrServerFactory.getServer();
    SolrParams params = new SolrQuery("text that is not found");
    QueryResponse response = server.query(params);
    assertEquals(0L, response.getResults().getNumFound());
  }

  @DataProvider(name = "documentMap")
  public Object[][] getDocument() throws Exception {
    Map<String, String[]> document = new HashMap<String, String[]>();
    document.put("id", new String[]{"test"});
    document.put("title", new String[]{"test_title"});
    document.put("alternate_title", new String[]{"alt_title_1", "alt_title_2"});

    return new Object[][]{
        {document}
    };
  }

  @Test(dataProvider = "documentMap", dependsOnMethods = {"testGetServer"})
  public void testAddDocument(Map<String, String[]> document) throws Exception {
    solrServerFactory.addDocument(document);
  }

  @Test(dependsOnMethods = "testAddDocument")
  public void testBasicQuery() throws SolrServerException {
    SolrServer server = solrServerFactory.getServer();
    SolrParams params = new SolrQuery("*:*");
    SolrDocumentList results = server.query(params).getResults();
    assertTrue(results.getNumFound() > 0, "didn't return any results");
  }

  @Test(dependsOnMethods = "testBasicQuery", dataProvider = "documentMap")
  public void testIdQuery(Map<String, String[]> document) throws SolrServerException {
    String id = document.get("id")[0];
    SolrServer server = solrServerFactory.getServer();
    SolrParams params = new SolrQuery("id:" + id);
    SolrDocumentList results = server.query(params).getResults();
    assertEquals(results.getNumFound(), 1, "didn't find article by id");
  }

  @Test(dependsOnMethods = "testAddDocument", dataProvider = "documentMap")
  public void testMultiValuedField(Map<String, String[]> document) throws Exception {
    String value1 = document.get("alternate_title")[0];
    String value2 = document.get("alternate_title")[1];

    Long numFound = solrServerFactory.getServer()
        .query(new SolrQuery("alternate_title:" + value1))
        .getResults().getNumFound();
    assertTrue(numFound > 0l, "query didn't work on multivalued field");

    numFound = solrServerFactory.getServer()
        .query(new SolrQuery("alternate_title:" + value2))
        .getResults().getNumFound();
    assertTrue(numFound > 0l, "query didn't work on multivalued field");
  }
  @AfterClass
  public void tearDown() throws Exception {
    solrServerFactory.tearDown();
  }
}
