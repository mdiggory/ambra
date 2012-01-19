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

import org.ambraproject.article.ArchiveProcessException;
import org.ambraproject.models.Article;
import org.w3c.dom.Document;

import java.util.zip.ZipFile;


/**
 * Bean to process a zip archive and return information about the article.  For custom xml schemas or object schemas,
 * implement this interface and supply the bean to the {@link Ingester}. The ingester delegates all article xml
 * processing to this bean
 *
 * @author Alex Kudlick Date: 6/7/11
 *         <p/>
 *         org.ambraproject.article.service
 */
public interface IngestArchiveProcessor {

  /**
   * Process the xml and return a fully-populated, unsaved {@link org.topazproject.ambra.models.Article} object
   *
   *
   * @param archive - the zip file containing article information
   * @param articleXml  - the xml of the article from the archive
   * @return - a dublin core object with properties from the xml
   * @throws org.ambraproject.article.ArchiveProcessException
   *          - if there's a problem parsing the xml
   */
  public Article processArticle(ZipFile archive, Document articleXml) throws ArchiveProcessException;

  /**
   * Pull the article xml out of the archive
   *
   * @param archive - the ingest archive
   * @return - the article xml
   */
  public Document extractArticleXml(ZipFile archive) throws ArchiveProcessException;
}
