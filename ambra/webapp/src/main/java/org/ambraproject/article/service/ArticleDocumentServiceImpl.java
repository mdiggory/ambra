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

import org.ambraproject.filestore.FSIDMapper;
import org.ambraproject.filestore.FileStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.journal.JournalService;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.service.HibernateServiceImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.sql.Blob;

/**
 * @author Alex Kudlick
 */
public class ArticleDocumentServiceImpl extends HibernateServiceImpl implements ArticleDocumentService {

  private static final Logger log = LoggerFactory.getLogger(ArticleDocumentServiceImpl.class);

  private DocumentBuilderFactory documentBuilderFactory;
  private JournalService journalService;
  private FileStoreService fileStoreService;


  /**
   * Set XML document builder factory. It will be used to parse Article XML.
   *
   * @param documentBuilderFactory DocumentBuilderFactory
   */
  @Required
  public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
    this.documentBuilderFactory = documentBuilderFactory;
  }

  /**
   * Set journal service.
   *
   * @param journalService Journal service
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  @Required
  public void setFileStoreService(FileStoreService fileStoreService) {
    this.fileStoreService = fileStoreService;
  }

  public Document getDocument(Blob blob) throws Exception {
    InputStream inputStream = null;
    try {
      inputStream = blob.getBinaryStream();
      return parseXmlInputStream(inputStream);
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  /**
   * @param articleId Article ID
   * @return Article XML + additional info
   * @throws java.io.IOException If reading XML document failed
   */
  @Transactional(readOnly = true)
  public Document getFullDocument(String articleId) throws Exception {
    InputStream inputStream = null;
    //TODO: find a standard way to get the xml file name for a doi

    try {
      String fileID = FSIDMapper.doiTofsid(articleId,"XML");
      inputStream = fileStoreService.getFileInStream(fileID);
      Document document = parseXmlInputStream(inputStream);
      appendJournals(URI.create(articleId), document);
      return document;
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  /**
   * Helper method to parse an input stream for the article xml. Does not close the input stream
   *
   * @param inputStream - the input stream to parse
   * @return - the parsed xml document
   * @throws Exception - if there was a problem parsing the input stream
   */
  private Document parseXmlInputStream(InputStream inputStream) throws Exception {
    InputSource xmlInputSource = new InputSource(inputStream);

    DocumentBuilder documentBuilder;
    synchronized (documentBuilderFactory) {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    return documentBuilder.parse(xmlInputSource);
  }

  private void appendJournals(URI articleId, Document doc) {

    // We have to skip cache here because this method is called inside the transaction and cache
    // invalidation happens on commit.
    Set<Journal> journals = journalService.getJournalsForObject(articleId.toString());

    Element additionalInfoElement = doc.createElementNS(XML_NAMESPACE, "ambra");
    Element journalsElement = doc.createElementNS(XML_NAMESPACE, "journals");

    doc.getDocumentElement().appendChild(additionalInfoElement);
    additionalInfoElement.appendChild(journalsElement);

    for (Journal journal : journals) {
      Element journalElement = doc.createElementNS(XML_NAMESPACE, "journal");

      Element eIssn = doc.createElementNS(XML_NAMESPACE, "eIssn");
      eIssn.appendChild(doc.createTextNode(journal.geteIssn()));
      journalElement.appendChild(eIssn);

      Element key = doc.createElementNS(XML_NAMESPACE, "key");
      key.appendChild(doc.createTextNode(journal.getKey()));
      journalElement.appendChild(key);

      Element name = doc.createElementNS(XML_NAMESPACE, "name");
      name.appendChild(doc.createTextNode(journal.getTitle()));
      journalElement.appendChild(name);

      journalsElement.appendChild(journalElement);
    }
  }

}
