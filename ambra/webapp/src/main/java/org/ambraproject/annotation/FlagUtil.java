/* $HeadURL::                                                                            $
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
package org.ambraproject.annotation;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ambraproject.ApplicationException;
import org.ambraproject.util.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Util functions to be used for Flag related tasks like created and extracting flag attributes.
 */
public class FlagUtil {
  private static final String FLAG_NODE = "flag";
  private static final String REASON_CODE = "reasonCode";
  private static final String COMMENT_NODE = "comment";
  private static final DocumentBuilderFactory documentBuilderFactory =
    DocumentBuilderFactory.newInstance();

  /**
   * Return the comment from an xml string
   * @param xmlDocument xmlDocument
   * @return the comment
   * @throws ApplicationException ApplicationException
   */
  public static String getComment(final String xmlDocument) throws ApplicationException {
    try {
      final Element root = getRootNode(xmlDocument);
      final Node reasonCode = root.getElementsByTagName(COMMENT_NODE).item(0);
      return reasonCode.getTextContent();
    } catch (Exception ex) {
      throw new ApplicationException(ex);
    }
  }

  /**
   * Return the reasonCode from an xml string
   * @param xmlDocument xmlDocument
   * @return the rason code
   * @throws ApplicationException ApplicationException
   */
  public static String getReasonCode(final String xmlDocument) throws ApplicationException {
    try {
      final Element root = getRootNode(xmlDocument);
      return root.getAttribute(REASON_CODE);
    } catch (Exception e) {
      throw new ApplicationException(e);
    }
  }

  private static Element getRootNode(final String xmlDocument)
    throws SAXException, IOException, ParserConfigurationException {
    final Document doc = documentBuilderFactory.newDocumentBuilder()
                            .parse(new ByteArrayInputStream(xmlDocument.getBytes("UTF-8")));
    return doc.getDocumentElement();
  }

  /**
   * Create the body as XML string for the flag comment given a reasonCode and a commentText
   * @param reasonCode reasonCode
   * @param commentText commentText
   * @return the flag body
   * @throws Exception Exception
   */
  public static String createFlagBody(final String reasonCode, final String commentText)
    throws Exception {
    final Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();
    final Element rootElement = doc.createElement(FLAG_NODE);
    doc.appendChild(rootElement);

    rootElement.setAttribute(REASON_CODE, reasonCode);
    final Element commentElement = doc.createElement(COMMENT_NODE);
    commentElement.setTextContent(commentText);
    rootElement.appendChild(commentElement);

    return TextUtils.getAsXMLString(doc);
  }
}
