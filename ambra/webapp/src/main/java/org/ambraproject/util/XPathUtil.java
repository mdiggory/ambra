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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Convenience utility bean for querying xml using XPath expressions.
 *
 * @author Alex Kudlick Date: 6/6/11
 *         <p/>
 *         org.ambraproject.util
 */
public class XPathUtil {

  /**
   * The XPath instance used to create expressions
   */
  private XPath xPath = XPathFactory.newInstance().newXPath();


  /**
   * Set the namespace context to be used by this instance of Xpath.  This enables the selection of namespaced
   * attributes and nodes (i.e. nodes and attributes with a colon in them)
   *
   * @param pairs - a String array where each entry is of the form prefix=namespaceURI.  In an XML document, the
   *                   prefix is the part that comes just after the xmlns part, and the namespaceURI is the value of
   *                   that attribute. E.g. to use the namespaces from the document below
   *                   <pre>
   *                     &lt;rootNode xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"&gt;
   *                     ...
   *                     &lt;/rootNode&gt;
   *                   </pre>
   *                   you would pass in an array like <pre>{"xsi=http://www.w3.org/2001/XMLSchema-instance","web:http=//java.sun.com/xml/ns/javaee/web-app_2_5.xsd"}</pre>
   */
  public void setNamespaceContext(final String[] pairs) {
    xPath.setNamespaceContext(new NamespaceContext() {
      @Override
      public String getNamespaceURI(String prefix) {
        for (String namespace : pairs) {
          if (namespace.startsWith(prefix + "=")) {
            return namespace.substring(namespace.indexOf("=") + 1);
          }
        }
        return null;
      }

      @Override
      public String getPrefix(String namespaceURI) {
        for (String namespace : pairs) {
          if (namespace.endsWith(namespaceURI)) {
            return namespace.substring(0, namespace.indexOf("="));
          }
        }
        return null;
      }

      @Override
      public Iterator getPrefixes(String namespaceURI) {
        return null;
      }
    });
  }

  /**
   * Stores compiled XPath expressions for performance.  To ensure thread safety, this is only accessed from {@link
   * XPathUtil#getXPathExpression(String)}
   */
  private Map<String, XPathExpression> storedExpressions = new HashMap<String, XPathExpression>();

  private synchronized XPathExpression getXPathExpression(String xpath) throws XPathExpressionException {
    if (storedExpressions.containsKey(xpath)) {
      return storedExpressions.get(xpath);
    } else {
      XPathExpression expression = xPath.compile(xpath);
      storedExpressions.put(xpath, expression);
      return expression;
    }
  }

  /**
   * Select a single node from the document
   *
   * @param document   - the document to select from
   * @param expression - the xpath expression to use to get the node
   * @return - the node described by the XPath expression, or null if there is none
   * @throws XPathExpressionException - if there's a problem parsing the expression
   */
  public Node selectSingleNode(Node document, String expression) throws XPathExpressionException {
    return (Node) getXPathExpression(expression).evaluate(document, XPathConstants.NODE);
  }

  /**
   * Select a single node from the input source.
   *
   * @param inputSource - the input source to select from
   * @param expression  - the xpath expression to use to get the node
   * @return - the node described by the XPath expression, or null if there is none
   * @throws XPathExpressionException - if there's a problem parsing the expression
   */
  public Node selectSingleNode(InputSource inputSource, String expression) throws XPathExpressionException {
    return (Node) getXPathExpression(expression).evaluate(inputSource, XPathConstants.NODE);
  }

  /**
   * Select multiple nodes from the document
   *
   * @param document   - the document to select from
   * @param expression - the xpath expression to use to get the node
   * @return - a NodeList containing the nodes described by the XPath expression, or null if there are none
   * @throws XPathExpressionException - if there's a problem parsing the expression
   */
  public NodeList selectNodes(Node document, String expression) throws XPathExpressionException {
    return (NodeList) getXPathExpression(expression).evaluate(document, XPathConstants.NODESET);
  }

  /**
   * Select multiple nodes from the input source
   *
   * @param inputSource - the input source to select from
   * @param expression  - the xpath expression to use to get the node
   * @return - a NodeList containing the nodes described by the XPath expression, or null if there are none
   * @throws XPathExpressionException - if there's a problem parsing the expression
   */
  public NodeList selectNodes(InputSource inputSource, String expression) throws XPathExpressionException {
    return (NodeList) getXPathExpression(expression).evaluate(inputSource, XPathConstants.NODESET);
  }

  /**
   * Evaluate the given XPath expression, trimming the result
   *
   * @param document   - the document in which to evaluate the expression
   * @param expression - the expression to evaluate
   * @return - the result of evaluation, as a string
   * @throws XPathExpressionException - if there's a problem  parsing the given expression
   */
  public String evaluate(Node document, String expression) throws XPathExpressionException {
    return getXPathExpression(expression).evaluate(document).trim();
  }


  /**
   * Evaluate the given XPath expression, trimming the result
   *
   * @param inputSource - the input source in which to evaluate the expression
   * @param expression  - the expression to evaluate
   * @return - the result of evaluation, as a string
   * @throws XPathExpressionException - if there's a problem  parsing the given expression
   */
  public String evaluate(InputSource inputSource, String expression) throws XPathExpressionException {
    return getXPathExpression(expression).evaluate(inputSource).trim();
  }

  /**
   * Evaluate the give expression with the given return type
   * <p/>
   * The return types are determined from the given QName as follows: <ul> <li>XPathConstants.NODE ->
   * org.w3c.dom.Node</li> <li>XPathConstants.NODESET -> org.w3c.dom.NodeList</li> <li>XPathConstants.BOOLEAN ->
   * java.lang.Boolean</li> <li>XPathConstants.NUMBER -> java.lang.Integer</li> <li>XPathConstants.STRING ->
   * java.lang.String</li> </ul>
   * <p/>
   * The return value of this method will be automatically cast to the type of a variable declared, so be sure that
   * variables are of types described above.
   *
   * @param document   - the context in which to evaluate the xpath expression
   * @param expression - the xpath expression to evaluate
   * @param returnType - an xpath constant denoting the return type.
   * @return - the result of the evaluation, of the given type
   * @throws XPathExpressionException - if there is a problem evaluating the expression
   */
  public Object evaluate(Node document, String expression, QName returnType) throws XPathExpressionException {
    return getXPathExpression(expression).evaluate(document, returnType);
  }

  /**
   * Evaluate the give expression with the given return type
   * <p/>
   * The return types are determined from the given QName as follows: <ul> <li>XPathConstants.NODE ->
   * org.w3c.dom.Node</li> <li>XPathConstants.NODESET -> org.w3c.dom.NodeList</li> <li>XPathConstants.BOOLEAN ->
   * java.lang.Boolean</li> <li>XPathConstants.NUMBER -> java.lang.Integer</li> <li>XPathConstants.STRING ->
   * java.lang.String</li> </ul>
   * <p/>
   * The return value of this method will be automatically cast to the type of a variable declared, so be sure that
   * variables are of types described above.
   *
   * @param inputSource - the context in which to evaluate the xpath expression
   * @param expression  - the xpath expression to evaluate
   * @param returnType  - an xpath constant denoting the return type.
   * @return - the result of the evaluation, of the given type
   * @throws XPathExpressionException - if there is a problem evaluating the expression
   */
  public Object evaluate(InputSource inputSource, String expression, QName returnType) throws XPathExpressionException {
    return getXPathExpression(expression).evaluate(inputSource, returnType);
  }
}
