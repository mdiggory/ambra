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

package org.ambraproject.article.service;

import java.sql.Blob;
import org.w3c.dom.Document;

import java.net.URI;

/**
 * Article document service is a utility for working with article XML.
 *
 * @author Dragisa Krsmanovic
 */
public interface ArticleDocumentService {

  public static final String XML_NAMESPACE = "http://www.ambraproject.org/article/additionalInfo";

  /**
   * Get full parsed Article XML Document.
   *
   * @param blob Article XML blob.
   * @return XML as DOM Document
   * @throws Exception If article XML read or parse fails
   */
  public Document getDocument(Blob blob) throws Exception;


  /**
   * Get full parsed Article XML Document with all additional data.
   *
   * Additional data defined in"http://www.ambraproject.org/article/additionalInfo namespace.
   * See articleAdditionalInfo.xsd for XML schema.
   * <ul>
   * <li>Journal list</li>
   * </ul>
   *
   * @param articleId Article ID
   * @return DOM document containing article XML and additional info.
   * @throws Exception If article XML read or parse fails
   */
  public Document getFullDocument(String articleId) throws Exception;



}
