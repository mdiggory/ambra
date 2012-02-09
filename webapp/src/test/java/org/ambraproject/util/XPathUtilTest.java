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

package org.ambraproject.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick Date: 6/6/11
 *         <p/>
 *         org.ambraproject.util
 */
public class XPathUtilTest {

  private final File testXmlFile;
  private final Document testXml;
  private XPathUtil xPathUtil = new XPathUtil();

  public XPathUtilTest() throws Exception {
    testXmlFile = new File(getClass().getClassLoader().getResource("ambra-test-config.xml").toURI());
    DocumentBuilder documentBuilder = DocumentBuilderFactoryCreator.createFactory().newDocumentBuilder();
    testXml = documentBuilder.parse(testXmlFile);
  }


  @DataProvider(name = "singleTextNode")
  public Object[][] getSingleTextNode() throws ParserConfigurationException, FileNotFoundException {
    return new Object[][]{
        {"//config/ambra/services/browse/cache", "false"},
        {"//browse/cache", "false"},
        {"//platform/name", "PLoS"},
        {"//config/ambra/platform/name", "PLoS"}
    };
  }

  @Test(dataProvider = "singleTextNode")
  public void testSelectSingleNode(String xpath, String expectedText) throws XPathExpressionException, FileNotFoundException {
    Node node = xPathUtil.selectSingleNode(testXml, xpath);
    assertNotNull(node, "returned null node");
    String text = node.getChildNodes().item(0).getNodeValue();
    assertEquals(text, expectedText, "returned node had incorrect text");

    //test the input source method
    InputSource inputSource = new InputSource(new FileInputStream(testXmlFile));
    node = xPathUtil.selectSingleNode(testXml, xpath);
    assertNotNull(node, "returned null node");
    text = node.getChildNodes().item(0).getNodeValue();
    assertEquals(text, expectedText, "returned node had incorrect text");
  }

  @DataProvider(name = "nodeLists")
  public Object[][] getParentNodes() {
    return new Object[][]{
        {"/config/ambra", 1},
        {"//config/ambra/services/*", 3},
        {"//config/ambra/*", 4},
        {"//platform/email/*", 3}
    };
  }

  @Test(dataProvider = "nodeLists")
  public void testSelectNodes(String xpath, int expectedNodeCount) throws XPathExpressionException, FileNotFoundException {
    NodeList nodes = xPathUtil.selectNodes(testXml, xpath);
    assertNotNull(nodes, "returned null node list");
    assertEquals(nodes.getLength(), expectedNodeCount, "returned node list had incorrect number of child nodes");

    //test the input source method
    InputSource inputSource = new InputSource(new FileInputStream(testXmlFile));
    nodes = xPathUtil.selectNodes(inputSource, xpath);
    assertNotNull(nodes, "returned null node list");
    assertEquals(nodes.getLength(), expectedNodeCount, "returned node list had incorrect number of child nodes");
  }

  @DataProvider(name = "expressions")
  public Object[][] getExpressions() {
    return new Object[][]{
        {"//config/ambra/services/browse/cache/text()", "false"},
        {"//browse/cache/text()", "false"},
        {"//config/ambra/platform/name/text()", "PLoS"},
        {"//config/ambra/platform/copyright/@type", "creativecommons"},
        {"//config/ambra/platform/freemarker/almHost/text()", "http://alm.plos.org"},
        {"count(//config/ambra/platform/freemarker/almHost)", "1"}
    };
  }

  @Test(dataProvider = "expressions")
  public void testEvaluate(String xpath, Object expectedResult) throws XPathExpressionException, FileNotFoundException {
    Object result = xPathUtil.evaluate(testXml, xpath);
    assertNotNull(result, "Returned null result");
    assertEquals(result, expectedResult, "Returned incorrect result");

    //test the input source method
    InputSource inputSource = new InputSource(new FileInputStream(testXmlFile));
    result = xPathUtil.evaluate(inputSource, xpath);
    assertNotNull(result, "Returned null result");
    assertEquals(result, expectedResult, "Returned incorrect result");
  }

  @DataProvider(name = "expressionsWithQName")
  public Object[][] getExpressionsWithQName() {
    return new Object[][]{
        {"count(//config/ambra/platform/freemarker/almHost)", XPathConstants.NUMBER, Double.class, 1.0},
        {"count(//config/ambra/platform/freemarker/almHost)", XPathConstants.STRING, String.class, "1"},
        {"count(//config/ambra/platform/freemarker/almHost) > 2", XPathConstants.BOOLEAN, Boolean.class, false},
        {"//config/ambra/platform", XPathConstants.NODE, Node.class, false},
        {"//config/ambra/platform/email/*", XPathConstants.NODESET, NodeList.class, false},
        {"//config/ambra/platform/email/*/text()", XPathConstants.NODESET, NodeList.class, false},
        {"//config/ambra/platform/../platform/email/*/text()", XPathConstants.NODESET, NodeList.class, false},
    };
  }


  @Test(dataProvider = "expressionsWithQName")
  public <T> void testEvaluateWithQName(String xpath, QName qName, Class<T> expectedClass, T expectedResult) throws XPathExpressionException, FileNotFoundException {
    Object result = xPathUtil.evaluate(testXml, xpath, qName);
    assertNotNull(result, "return null result");
    assertTrue(expectedClass.isAssignableFrom(result.getClass()), "Result was incorrect class;" +
        " expected " + expectedClass + " but got " + result.getClass());
    if (!qName.equals(XPathConstants.NODE) && !qName.equals(XPathConstants.NODESET)) {
      assertEquals(result, expectedResult, "returned incorrect result");
    }

    //test the input source method
    InputSource inputSource = new InputSource(new FileInputStream(testXmlFile));
    result = xPathUtil.evaluate(inputSource, xpath, qName);
    assertNotNull(result, "return null result");
    assertTrue(expectedClass.isAssignableFrom(result.getClass()), "Result was incorrect class;" +
        " expected " + expectedClass + " but got " + result.getClass());
    if (!qName.equals(XPathConstants.NODE) && !qName.equals(XPathConstants.NODESET)) {
      assertEquals(result, expectedResult, "returned incorrect result");
    }
  }

  @DataProvider(name = "subselects")
  public Object[][] getSubselects() throws XPathExpressionException {
    return new Object[][]{
        {xPathUtil.selectSingleNode(testXml, "//config/ambra"), "//services/browse/cache/text()", "false"},
        {xPathUtil.selectSingleNode(testXml, "//config/ambra"), "//cache/text()", "false"},
        {xPathUtil.selectSingleNode(testXml, "//browse"), "//cache/text()", "false"},
        {xPathUtil.selectSingleNode(testXml, "/config/ambra/platform"), "//copyright/@type", "creativecommons"},
        {xPathUtil.selectSingleNode(testXml, "/config/ambra/platform"), "//freemarker/almHost/text()", "http://alm.plos.org"},
    };
  }

  @Test(dataProvider = "subselects")
  public void testSubSelects(Node subNode, String xpath, String expectedResult) throws XPathExpressionException {
    String result = xPathUtil.evaluate(subNode, xpath);
    assertNotNull(result, "returned null result");
    assertEquals(result, expectedResult, "returned incorrect result");
  }

  @DataProvider(name = "nonexistentNodes")
  public Object[][] getNonexistentNodes() {
    return new Object[][]{
        {"//config/notANode", false},
        {"//config/ambra/platform/noNodeHere", false},
        {"//config/ambra/platform/@notAnAttribute", true}
    };
  }

  @Test(dataProvider = "nonexistentNodes")
  public void testNonExistentNodes(String xpath, boolean useEvaluate) throws XPathExpressionException {
    if (!useEvaluate) {
      Node node = xPathUtil.selectSingleNode(testXml, xpath);
      assertNull(node, "selectSingleNode() returned non-null result");
      NodeList nodeList = xPathUtil.selectNodes(testXml, xpath);
      assertEquals(nodeList.getLength(), 0, "selectNodes() returned non-empty result");
    } else {
      String result = xPathUtil.evaluate(testXml, xpath);
      assertTrue(result.isEmpty(), "evaluate() returned non-empty result");
    }
  }


}
