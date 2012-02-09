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

package org.ambraproject.web;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.io.IOException;

/**
 * @author Dragisa Krsmanovic
 * TODO: Test ranges
 */
public class HttpResourceServerTest {
  private static final String EXPECTED_TEXT = "Hello World !";
  private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<test>Hello World !</test>";
  private URL xmlUrl;
  private URL txtUrl;

  @BeforeClass
  protected void setUpClass() throws Exception {
    xmlUrl = this.getClass().getResource("/TestResource.xml");
    txtUrl = this.getClass().getResource("/TestResource.txt");
    DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory
        .newInstance("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", getClass().getClassLoader());
    documentBuilderfactory.setNamespaceAware(true);
    documentBuilderfactory.setValidating(false);
    documentBuilderfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    XMLUnit.setControlDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setTestDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
    XMLUnit.setTransformerFactory("net.sf.saxon.TransformerFactoryImpl");
    XMLUnit.setXSLTVersion("2.0");
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreWhitespace(true);
  }


  @Test
  public void testServerResourceTxt() throws IOException {
    MockHttpServletResponse responseMock = new MockHttpServletResponse();
    MockHttpServletRequest requestMock = new MockHttpServletRequest();
    HttpResourceServer server = new HttpResourceServer();
    server.serveResource(requestMock, responseMock, new HttpResourceServer.URLResource(txtUrl), null);
    assertEquals(responseMock.getContentAsString(), EXPECTED_TEXT, "Wrong content served");
    assertEquals(responseMock.getContentType(), "text/plain", "Wrong content type");
    assertEquals(responseMock.getContentLength(), EXPECTED_TEXT.length(),
        "Wrong content length");
  }

  @Test
  public void testServerResourceXml() throws IOException, SAXException {
    MockHttpServletResponse responseMock = new MockHttpServletResponse();
    MockHttpServletRequest requestMock = new MockHttpServletRequest();
    HttpResourceServer server = new HttpResourceServer();
    server.serveResource(requestMock, responseMock, new HttpResourceServer.URLResource(xmlUrl), null);
    Diff diff = new Diff(EXPECTED_XML, responseMock.getContentAsString());
    assertTrue(diff.identical(), diff.toString());
    assertEquals(responseMock.getContentType(), "application/xml", "Wrong content type");
    assertEquals(responseMock.getContentLength(), responseMock.getContentAsString().length(),
        "Wrong content length");
  }

  @Test
  public void testServerResourceForHead() throws IOException {
    MockHttpServletResponse responseMock = new MockHttpServletResponse();
    MockHttpServletRequest requestMock = new MockHttpServletRequest();
    requestMock.setMethod("HEAD");
    HttpResourceServer server = new HttpResourceServer();
    server.serveResource(requestMock, responseMock, new HttpResourceServer.URLResource(txtUrl), null);
    assertEquals(responseMock.getContentAsString(), "", "Content is not empty");
    assertEquals(responseMock.getContentType(), "text/plain", "Wrong content type");
    assertEquals(responseMock.getContentLength(), EXPECTED_TEXT.length(),
        "Wrong content length");
  }

  @Test
  public void testServerResourceWithContent() throws IOException {
    MockHttpServletResponse responseMock = new MockHttpServletResponse();
    MockHttpServletRequest requestMock = new MockHttpServletRequest();
    HttpResourceServer server = new HttpResourceServer();
    server.serveResource(requestMock, responseMock, true, new HttpResourceServer.URLResource(txtUrl));
    assertEquals(responseMock.getContentAsString(), EXPECTED_TEXT, "Wrong content served");
    assertEquals(responseMock.getContentType(), "text/plain", "Wrong content type");
    assertEquals(responseMock.getContentLength(), EXPECTED_TEXT.length(),
        "Wrong content length");
  }

  @Test
  public void testServerResourceWithoutContent() throws IOException {
    MockHttpServletResponse responseMock = new MockHttpServletResponse();
    MockHttpServletRequest requestMock = new MockHttpServletRequest();
    HttpResourceServer server = new HttpResourceServer();
    server.serveResource(requestMock, responseMock, false, new HttpResourceServer.URLResource(txtUrl));
    assertEquals(responseMock.getContentAsString(), "", "Content is not empty");
    assertEquals(responseMock.getContentType(), "text/plain", "Wrong content type");
    assertEquals(responseMock.getContentLength(), EXPECTED_TEXT.length(),
        "Wrong content length");
  }
}
