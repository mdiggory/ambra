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

package org.ambraproject.admin.service;

import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * @author alan Manage documents on server. Ingest and access ingested documents.
 */
public interface DocumentManagementService {

  /**
   * Disables an article, when an article is disabled, it should not appear in the system
   *
   * @param objectURI URI of the article to delete
   * @param authId the authorization ID of the current user
   * @throws Exception if id is invalid or Sending of delete message failed.
   */
  public void disable(String objectURI, final String authId) throws Exception;

  /**
   * Delete the article from the system and all associated data
   *
   * @param objectURI URI of the article to delete
   * @param authId the authorization ID of the current user
   * @throws Exception if id is invalid or the delete failed.
   */
  public void delete(String objectURI, final String authId) throws Exception;

  /**
   * Marks an article as not published
   *
   * @param objectURI URI of the article to unpublish
   * @param authId the authorization ID of the current user
   * @throws Exception if id is invalid or Sending of delete message failed.
   */
  public void unPublish(String objectURI, final String authId) throws Exception;

  /**
   * Revert the data out of the ingested queue
   *
   * @param uri the article uri
   * @throws IOException on an error
   */
  public void revertIngestedQueue(String uri) throws IOException;

  /**
   * @return List of filenames of files in uploadable directory on server
   */
  public List<String> getUploadableFiles();

  /**
   * Move the file to the ingested directory and generate cross-ref.
   *
   * @param file    the file to move
   * @param doi the associated article
   * @throws IOException on an error
   */
  public void generateIngestedData(File file, String doi)
      throws IOException;

  /**
   * Generate the crossref info doc and put it in the ingested document directory
   *
   * @param articleXml - the article xml document
   * @param articleId - the article id
   * @throws javax.xml.transform.TransformerException - if there's a problem transforming the article xml
   */
  public void generateCrossrefInfoDoc(Document articleXml, URI articleId) throws TransformerException;

  /**
   * @param uris uris to be published. Send CrossRef xml file to CrossRef - if it is _received_ ok then set article stat
   *             to active
                                                                                        AIS
   * @return a list of messages describing what was successful and what failed
   */
  public List<String> publish(String[] uris, final String authId);

  /**
   * Get the DocumentDirectory setting
   *
   * @return DocumentDirectory
   */
  public String getDocumentDirectory();

  /**
   * Get the IngestedDocumentDirectory setting
   *
   * @return IngestedDocumentDirectory
   */
  public String getIngestedDocumentDirectory();

  /**
   * Remove all the files associated with an article from the file repository
   *
   * @param articleUri
   * @throws Exception
   */
  public void removeFromFileSystem(String articleUri) throws Exception;
}
