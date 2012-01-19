/* $HeadURL::                                                                                     $
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

import org.ambraproject.models.Article;

import java.util.zip.ZipFile;

/**
 * The article ingester.
 *
 * @author Alex Kudlick
 */
public interface Ingester {

  /**
   * Ingest a new article.  The main steps for this process are :
   * <ol>
   *   <li>Process the article xml to create an {@link org.topazproject.ambra.models.Article} object</li>
   *   <li>Save the Article object to the databse</li>
   *   <li>Add any applicable related article links to existent articles</li>
   *   <li>Create syndications for the article</li>
   *   <li>Store each of the ancillary files in the archive to the {@link org.ambraproject.filestore.FileStoreService}</li>
   * </ol>
   * <p/>
   * TODO: Find a way to rollback from the filestore or database if there's a problem with the other
   *
   *
   * @param ingestArchive - the archive to ingest
   * @param force         if true then don't check whether this article already exists but just save this new article.
   * @return the new article
   * @throws DuplicateArticleIdException if an article exists with the same URI as the new article and <var>force</var>
   *                                     is false
   * @throws IngestException             if there's any other problem ingesting the article
   */
  public Article ingest(ZipFile ingestArchive, boolean force)
      throws DuplicateArticleIdException, IngestException;

}