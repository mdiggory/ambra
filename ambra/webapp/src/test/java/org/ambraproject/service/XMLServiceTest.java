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

package org.ambraproject.service;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.HTMLDocumentBuilder;
import org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.ApplicationException;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.article.service.NoSuchObjectIdException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertTrue;

/**
 * @author Dragisa Krsmanovic
 * @author Joe Osowski
 */
public class XMLServiceTest {
  private static final String OBJINFO_NAMESPACES = "xmlns:mml=\"http://www.w3.org/1998/Math/MathML\" " +
      "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
      "xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" " +
      "xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" " +
      "xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
      "xmlns:util=\"http://dtd.nlm.nih.gov/xsl/util\"";

  private XMLServiceImpl secondaryObjectService;
  private XMLServiceImpl viewNLMService;

  @BeforeClass
  protected void setUp() throws Exception {

    DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory.newInstance("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", getClass().getClassLoader());
    documentBuilderfactory.setNamespaceAware(true);
    documentBuilderfactory.setValidating(false);
    documentBuilderfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    Map<String, String> xmlFactoryProperties = new HashMap<String, String>();
    xmlFactoryProperties.put("javax.xml.transform.TransformerFactory",
        "net.sf.saxon.TransformerFactoryImpl");
    xmlFactoryProperties.put("javax.xml.transform.Transformer",
        "net.sf.saxon.Controller");

    XMLUnit.setControlDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setTestDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setTransformerFactory("net.sf.saxon.TransformerFactoryImpl");
    XMLUnit.setXSLTVersion("2.0");

    Configuration configiration = new BaseConfiguration();
    configiration.setProperty("ambra.platform.appContext", "test-context");

    secondaryObjectService = new XMLServiceImpl();
    secondaryObjectService.setArticleRep("XML");
    secondaryObjectService.setXmlFactoryProperty(xmlFactoryProperties);
    secondaryObjectService.setAmbraConfiguration(configiration);
    secondaryObjectService.setXslTemplate("/objInfo.xsl");
    secondaryObjectService.init();

    viewNLMService = new XMLServiceImpl();
    viewNLMService.setArticleRep("XML");
    viewNLMService.setXmlFactoryProperty(xmlFactoryProperties);
    viewNLMService.setAmbraConfiguration(configiration);
    viewNLMService.setXslTemplate("/viewnlm-v2.xsl");
    viewNLMService.init();
  }

  @DataProvider(name = "objInfoSamples")
  public String[][] createObjInfoSamples() {
    return new String[][]{
        {"<p>Hello World</p>", "<p>Hello World</p>"},
        {"<sc>hello world</sc>", "<small " + OBJINFO_NAMESPACES + ">HELLO WORLD</small>"},
        {"<bold>Hello World</bold>", "<b " + OBJINFO_NAMESPACES + ">Hello World</b>"},
        {"<abbrev xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
            "xlink:href=\"test\" id=\"123\">Hello World</abbrev>",
            "<a " + OBJINFO_NAMESPACES + " href=\"test\" id=\"123\">Hello World</a>"},
        {"<abbrev id=\"123\">Hello World</abbrev>",
            "<span " + OBJINFO_NAMESPACES + " class=\"capture-id\" id=\"123\">Hello World</span>"}
    };
  }


  @Test(dataProvider = "objInfoSamples")
  public void testObjInfoTransformation(String source, String expected)
      throws URISyntaxException, ApplicationException, SAXException, IOException,
      ParserConfigurationException {

    final DocumentBuilder builder = viewNLMService.createDocBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(source)));
    String result = secondaryObjectService.getTransformedDocument(doc);

    Diff diff = new Diff(expected, result);
    assertTrue(diff.identical(), diff.toString());
  }


  @DataProvider(name = "viewNLMFiles")
  public String[][] createViewNLMSamples() {
    return new String[][]{
        {"/article/article1.xml","/article/result1.html"}
    };
  }

  @Test(dataProvider = "viewNLMFiles")
  public void testViewNLMTransformation(String articleFilename, String resultFilename)
      throws IOException, SAXException, NoSuchArticleIdException, NoSuchObjectIdException,
      URISyntaxException, ApplicationException, ParserConfigurationException {

    final DocumentBuilder builder = viewNLMService.createDocBuilder();
    Document doc = builder.parse(new InputSource(new
        StringReader(getFileAsString(articleFilename))));

    String result = viewNLMService.getTransformedDocument(doc);
    verify();

    String expected = getFileAsString(resultFilename);

    HTMLDocumentBuilder htmlDocumentBuilder =
        new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));

    Diff diff = new Diff(htmlDocumentBuilder.parse(expected), htmlDocumentBuilder.parse(result));

    assertTrue(diff.identical(), diff.toString());
  }

  private String getFileAsString(String resultFilename) throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream(resultFilename), "UTF-8"));
    StringBuilder expected = new StringBuilder();

    String line = reader.readLine();
    while (line != null) {
      expected.append(line);
      expected.append('\n');
      line = reader.readLine();
    }

    return expected.toString();
  }

  private InputStream getFileAsStream(String resultFilename) throws IOException {
    return getClass().getResourceAsStream(resultFilename);
  }
}