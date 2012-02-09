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

package org.ambraproject.article.service;

import org.ambraproject.ApplicationException;
import org.ambraproject.service.HibernateService;
import org.ambraproject.article.AuthorExtra;
import org.ambraproject.article.CitationReference;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetch article service.
 */
public interface FetchArticleService extends HibernateService {
  /**
   * Get the URI transformed as HTML.
   * @param articleURI articleURI
   * @param authId the authorization ID of the current user
   * @return String representing the annotated article as HTML
   * @throws org.ambraproject.ApplicationException ApplicationException
   */
  public String getURIAsHTML(final String articleURI, final String authId) throws Exception;

  /**
   * Return the annotated content as a String
   * @param articleURI articleURI
   * @param authId the authorization ID of the current user
   * @return an the annotated content as a String
   * @throws ParserConfigurationException ParserConfigurationException
   * @throws SAXException SAXException
   * @throws IOException IOException
   * @throws URISyntaxException URISyntaxException
   * @throws org.ambraproject.ApplicationException ApplicationException
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   * @throws javax.xml.transform.TransformerException TransformerException
   */
  public String getAnnotatedContent(final String articleURI, final String authId)
      throws ParserConfigurationException, SAXException, IOException, URISyntaxException,
             ApplicationException, NoSuchArticleIdException,TransformerException;


  /**
   * Get the article xml
   * @param articleURI article uri
   * @param authId the authorization ID of the current user
   * @return article xml
   */
  public Document getArticleDocument(String articleURI, final String authId);


  /**
   * Get the author affiliations for a given article
   * @param doc article xml
   * @return author affiliations
   */
  public ArrayList<AuthorExtra> getAuthorAffiliations(Document doc);

  /**
   * Get references for a given article
   * @param doc article xml
   * @return references
   */
  public ArrayList<CitationReference> getReferences(Document doc);

  /**
   * Returns abbreviated journal name
   * @param doc article xml
   * @return abbreviated journal name
   */
  public String getJournalAbbreviation(Document doc);
}
