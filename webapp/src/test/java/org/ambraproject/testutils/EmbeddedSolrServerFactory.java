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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.ambraproject.testutils;

import org.ambraproject.search.service.SolrServerFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.util.AbstractSolrTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * Class added for unit tests to use an in-memory embedded solr server.
 * <p/>
 * Extends the topazproject SolrServerFactory so it can be passed to existing beans. Documents are added to the server
 * by passing a map to the addDocument method.
 *
 * @author Alex Kudlick Date: 5/13/11
 *         <p/>
 *         org.ambraproject.testutils
 */
public class EmbeddedSolrServerFactory extends SolrServerFactory {

  private SolrServer server;
  private final Factory factory;

  /**
   * Initialize the server stored by the factory by specifying the locations of the schema and config files
   *
   * @param schemaFile - either the classpath location or the absolute file location of the solr schema xml to use
   * @param configFile - either the classpath location or the absolute file location  of the solr config xml to use
   * @throws Exception - from factory.setup()
   */
  public EmbeddedSolrServerFactory(String schemaFile, String configFile) throws Exception {
    super();
    try {
      this.factory = new Factory(schemaFile, configFile);
      server = factory.createServer();
    } catch (Exception e) {
      throw new Exception("Error setting up server; " +
          "be sure that your working directory is set correctly to find the schema and config files; nested exception is " + e.getMessage(), e);
    }
  }

  /**
   * Initialize the server stored in the factory by using the defaults of <ul>
   * <li>solr/test-solr-schema.xml</li>
   * <li>solr/test-solr-config.xml</li>
   * </ul>
   * <p/>
   * in the classpath for the schema and config files, respectively.
   *
   * @throws Exception - URISyntaxException when loading resources or else Exception when setting up the server
   */
  public EmbeddedSolrServerFactory() throws Exception {
    this("solr/test-solr-schema.xml", "solr/test-solr-config.xml");
  }

  public void tearDown() throws Exception {
    factory.tearDown();
    server = null;
  }

  /**
   * Add a document to the server stored here. Note that "id" is a required field
   *
   * @param document - a map from solr field names to values.  The value array should have more than one entry only for
   *                 fields that are multivalued (see the test schema.xml)
   * @throws Exception - from the server.add() method
   */
  public void addDocument(Map<String, String[]> document) throws Exception {
    SolrInputDocument inputDocument = new SolrInputDocument();
    for (String fieldName : document.keySet()) {
      for (String value : document.get(fieldName)) {
        inputDocument.addField(fieldName, value);
      }
    }
    server.add(inputDocument);
    server.commit();
  }

  /**
   * Add a document to the server stored here.  Note that "id" is a required field
   *
   * @param document each row should be a field, with the first entry the field name, and the rest of the entries the values for the field.
   *                 Only multi-valued fields should have more than one value
   * @throws Exception from there server.add() method
   */
  public void addDocument(String[][] document) throws Exception {
    SolrInputDocument inputDocument = new SolrInputDocument();
    for (String[] row : document) {
      String fieldName = row[0];
      for (String value : Arrays.copyOfRange(row, 1, row.length)) {
        inputDocument.addField(fieldName, value);
      }
    }
    server.add(inputDocument);
    server.commit();
  }

  /**
   * Get Embedded Solr Server instance
   *
   * @return Solr server
   */
  @Override
  public SolrServer getServer() {
    return server;
  }

  /**
   * factory class that does the work of creating the embedded server.  Extends AbstractSolrTestCase so we can reuse
   * their code
   */
  private static class Factory extends AbstractSolrTestCase {
    private String schemaFile;
    private String configFile;

    public Factory(String schemaFile, String configFile) throws Exception {
      super();
      //Copy the schema and config files to temp files, to that if any project is using this class from a jar they can
      //since we can't call class.getResource() on a file from a jar
      int r = Math.abs(new Random().nextInt());
      this.schemaFile = System.getProperty("java.io.tmpdir") + File.separator + "ambra-test-solr-schema-" + r + ".xml";
      this.configFile = System.getProperty("java.io.tmpdir") + File.separator + "ambra-test-solr-config-" + r + ".xml";

      writeFile(schemaFile, this.schemaFile);
      writeFile(configFile, this.configFile);

      super.setUp();
      System.setProperty("solr.solr.home", System.getProperty("java.io.tmpdir"));
    }

    private void writeFile(String source, String destination) throws IOException {
      File destinationFile = new File(destination);
      destinationFile.deleteOnExit();
      InputStream inputStream = null;
      OutputStream outputStream = null;

      try {
        inputStream = getInputStream(source);
        outputStream = new FileOutputStream(destinationFile);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
          outputStream.write(buf, 0, len);
        }
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            //suppress
          }
        }
        if (outputStream != null) {
          try {
            outputStream.close();
          } catch (IOException e) {
            //suppress
          }
        }
      }
    }

    private InputStream getInputStream(String source) throws FileNotFoundException {
      InputStream inputStream;
      inputStream = EmbeddedSolrServerFactory.class.getResourceAsStream(source);
      if (inputStream == null) {
        //try with the class loader
        inputStream = EmbeddedSolrServerFactory.class.getClassLoader().getResourceAsStream(source);
      }
      if (inputStream == null) {
        //perhaps it describes a file
        inputStream = new FileInputStream(source);
      }
      return inputStream;
    }

    @Override
    public String getSchemaFile() {
      return schemaFile;
    }

    @Override
    public String getSolrConfigFile() {
      return configFile;
    }

    public SolrServer createServer() {
      //h is a TestHarness from AbstractSolrTestCase that gets set up on calling setup()
      return new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
    }

    @Override
    public void tearDown() throws Exception {
      super.preTearDown();
      super.tearDown();
    }
  }

}
