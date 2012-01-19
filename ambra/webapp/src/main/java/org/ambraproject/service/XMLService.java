/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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

import org.ambraproject.ApplicationException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Convenience class to aggregate common methods used to deal with XML transforms on articles.
 * Used to transform article with annotations, captions of tables/figures, and citation information.
 *
 * @author Stephen Cheng
 * @author Alex Worden
 * @author Joe Osowski
 *
 */
public interface XMLService {


  /**
   * Pass in an XML string fragment, and will return back a string representing the document after
   * going through the XSL transform.
   *
   * @param description
   * @return Transformed document as a String
   * @throws ApplicationException
   */
  public String getTransformedDocument(String description) throws ApplicationException;

  /**
   * Given an XML Document as input, will return an XML string representing the document after
   * transformation.
   *
   * @param doc
   * @return XML String of transformed document
   * @throws ApplicationException
   */
  public String getTransformedDocument(Document doc) throws ApplicationException;

  public byte[] getTransformedByArray(byte[] xml) throws ApplicationException;

  public InputStream getTransformedInputStream(InputStream xml) throws ApplicationException;

  /**
   * Given a string as input, will return an XML string representing the document after
   * transformation.
   *
   * @param description
   * @return string
   * @throws ApplicationException
   */
  public String getTransformedDescription(String description) throws ApplicationException;

  /**
   * Convenience method to create a DocumentBuilder with the factory configs
   *
   * @return Document Builder
   * @throws ParserConfigurationException
   */
  public DocumentBuilder createDocBuilder() throws ParserConfigurationException;


  /**
   * @return Returns the articleRep.
   */
  public String getArticleRep();

  /**
   * @param filenameOrURL filenameOrURL
   * @throws URISyntaxException URISyntaxException
   * @return the local or remote file or url as a java.io.File
   */
  public File getAsFile(final String filenameOrURL) throws URISyntaxException;
}
