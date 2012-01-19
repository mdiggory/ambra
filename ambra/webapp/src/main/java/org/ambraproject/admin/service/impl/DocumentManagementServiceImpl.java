/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

package org.ambraproject.admin.service.impl;

import org.ambraproject.filestore.FSIDMapper;
import org.ambraproject.filestore.FileStoreService;
import org.ambraproject.models.Article;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.admin.service.DocumentManagementService;
import org.ambraproject.admin.service.OnDeleteListener;
import org.ambraproject.admin.service.OnPublishListener;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.journal.JournalService;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.permission.service.PermissionsService;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author alan Manage documents on server. Ingest and access ingested documents.
 */
public class DocumentManagementServiceImpl implements DocumentManagementService {
  private static final Logger log = LoggerFactory.getLogger(DocumentManagementServiceImpl.class);

  private ArticleService articleService;
  private PermissionsService permissionsService;
  private FileStoreService fileStoreService;

  private String documentDirectory;
  private String ingestedDocumentDirectory;
  private String documentPrefix;
  private JournalService journalService;
  private String plosDoiUrl;
  private String plosEmail;

  private List<OnPublishListener> onPublishListeners;
  private List<OnDeleteListener> onDeleteListeners;
  private String crossrefXslTemplate;

  @Required
  public void setArticleService(final ArticleService articleService) {
    this.articleService = articleService;
  }

  @Required
  public void setDocumentDirectory(final String documentDirectory) {
    this.documentDirectory = documentDirectory;
  }

  @Required
  public void setDocumentPrefix(final String documentPrefix) {
    this.documentPrefix = documentPrefix;
  }

  public String getDocumentDirectory() {
    return documentDirectory;
  }

  public String getIngestedDocumentDirectory() {
    return this.ingestedDocumentDirectory;
  }

   /**
   * Set the {@link FileStoreService} to use to store files
   *
   * @param fileStoreService - the filestore to use
   */
  @Required
  public void setFileStoreService(FileStoreService fileStoreService) {
    this.fileStoreService = fileStoreService;
  }

  @Required
  public void setIngestedDocumentDirectory(final String ingestedDocumentDirectory) {
    this.ingestedDocumentDirectory = ingestedDocumentDirectory;
  }

  public void setOnPublishListeners(List<OnPublishListener> onPublishListeners) {
    this.onPublishListeners = onPublishListeners;
  }

  public void setOnDeleteListeners(List<OnDeleteListener> onDeleteListeners) {
    this.onDeleteListeners = onDeleteListeners;
  }

  /**
   * Set the Xsl template used to create the crossref info doc.  This should be capable of transforming an article xml
   * file.
   *
   * @param crossrefXslTemplate - the name of a classpath accessible xsl template to use in creating the crossref info
   *                            doc
   */
  @Required
  public void setCrossrefXslTemplate(String crossrefXslTemplate) {
    this.crossrefXslTemplate = crossrefXslTemplate;
  }

  /**
   * @param filenameOrURL filenameOrURL
   * @return the local or remote file or url as a java.io.File
   * @throws java.net.URISyntaxException when URL is malformed
   */
  private File getAsFile(final String filenameOrURL) throws URISyntaxException {
    final URL resource = getClass().getResource(filenameOrURL);
    if (null == resource) {
      // access it as a local file resource
      return new File(org.ambraproject.util.FileUtils.getFileName(filenameOrURL));
    } else {
      return new File(resource.toURI());
    }
  }

  /**
   * Unpublishes an article
   *
   * @param objectURI URI of the article to delete
   * @throws Exception if id is invalid or Sending of delete message failed.
   */
  @Transactional(rollbackFor = {Throwable.class})
  public void unPublish(String objectURI, final String authId) throws Exception {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    URI id = URI.create(objectURI);

    articleService.setState(objectURI, authId, Article.STATE_UNPUBLISHED);

    removeFromCrossPubbedJournals(id);

    //When an article is 'unpublished'
    //It should be deleted from any places where it has been syndicated to
    invokeOnDeleteListeners(objectURI);
  }

  /**
   * Disables an article, when an article is disabled, it should not appear in the system
   *
   * @param objectURI URI of the article to delete
   * @throws Exception if id is invalid or Sending of delete message failed.
   */
  @Transactional(rollbackFor = {Throwable.class})
  public void disable(String objectURI, final String authId) throws Exception {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    URI id = URI.create(objectURI);

    articleService.setState(objectURI, authId, Article.STATE_DISABLED);

    removeFromCrossPubbedJournals(id);

    //When an article is 'disabled' it should be deleted from any places where it has been
    //syndicated to and removed from the file store
    //The only way to re-enable this in a correct way is to re-ingest the article.
    removeFromFileSystem(objectURI);
    invokeOnDeleteListeners(objectURI);
  }

  @Transactional(rollbackFor = {Throwable.class})
  public void delete(String articleUri, final String authId) throws Exception
  {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    log.debug("Deleting Article:" + articleUri);

    articleService.delete(articleUri, authId);

    removeFromFileSystem(articleUri);

    invokeOnDeleteListeners(articleUri);
  }

  public void removeFromFileSystem(String articleUri) throws Exception
  {
    String articleRoot = FSIDMapper.zipToFSID(articleUri, "");
    Map<String, String> files = fileStoreService.listFiles(articleRoot);

    for(String file : files.keySet()) {
      String fullFile = FSIDMapper.zipToFSID(articleUri, file);
      fileStoreService.deleteFile(fullFile);
    }

    //We leave the directory in place as mogile doesn't really support removing of keys
    //fileStoreService.deleteFile(articleRoot);
  }

  /**
   * Revert the data out of the ingested queue
   *
   * @param uri the article uri
   * @throws java.io.IOException on an error
   */
  public void revertIngestedQueue(String uri) throws IOException {
    // delete any crossref submission file
    File queueDir = new File(documentDirectory);
    File ingestedDir = new File(ingestedDocumentDirectory);
    File ingestedXmlFile = new File(ingestedDir, uri.replaceAll("[:/.]", "_") + ".xml");

    if (log.isDebugEnabled())
      log.debug("Deleting '" + ingestedXmlFile + "'");

    try {
      FileUtils.forceDelete(ingestedXmlFile);
    } catch (FileNotFoundException fnfe) {
      log.info("'" + ingestedXmlFile + "' does not exist - cannot delete: ", fnfe);
    }

    // move zip back to ingestion queue
    if (!queueDir.equals(ingestedDir)) {
      // strip 'info:doi/10.1371/journal.'
      String fname = uri.substring(documentPrefix.length()) + ".zip";
      File fromFile = new File(ingestedDir, fname);
      File toFile = new File(queueDir, fname);

      try {
        if (log.isDebugEnabled())
          log.debug("Moving '" + fromFile + "' to '" + toFile + "'");
        FileUtils.moveFile(fromFile, toFile);
      } catch (FileNotFoundException fnfe) {
        log.info("Could not move '" + fromFile + "' to '" + toFile + "': ", fnfe);
      }
    }
  }

  /**
   * @return List of filenames of files in uploadable directory on server
   */
  public List<String> getUploadableFiles() {
    List<String> documents = new ArrayList<String>();
    File dir = new File(documentDirectory);
    if (dir.isDirectory()) {
      Collections.addAll(documents, dir.list(new FilenameFilter() {
        //check the file extensions
        @Override
        public boolean accept(File file, String fileName) {
          for (String extension : new String[]{".tar", ".tar.bz", ".tar.bz2",
              ".tar.gz", ".tb2", ".tbz", ".tbz2", ".tgz", ".zip"}) {
            if (fileName.endsWith(extension)) {
              return true;
            }
          }
          return false;
        }
      }));

    }

    Collections.sort(documents);
    return documents;
  }

  /**
   * Move the file to the ingested directory and generate cross-ref.
   *
   * @param file    the file to move
   * @param doi the associated article
   * @throws java.io.IOException on an error
   */
  public void generateIngestedData(File file, String doi)
      throws IOException {
    // Delete the previously ingested article if it exist.
    FileUtils.deleteQuietly(new File(ingestedDocumentDirectory, file.getName()));
    FileUtils.moveFileToDirectory(file, new File(ingestedDocumentDirectory), true);
    log.info("Relocated: " + file + ":" + doi);
  }

  /**
   * @param uris uris to be published.
   * @return a list of messages describing what was successful and what failed
   */
  @Transactional(rollbackFor = {Throwable.class})
  public List<String> publish(String[] uris, final String authId) {
    final List<String> msgs = new ArrayList<String>();

    // publish articles
    for (String article : uris) {
      try {
        // mark article as active
        articleService.setState(article, authId, Article.STATE_ACTIVE);
        invokeOnPublishListeners(article);

        msgs.add("Published: " + article);
        log.info("Published article: '" + article + "'");
      } catch (Exception e) {
        log.error("Could not publish article: '" + article + "'", e);
        msgs.add("Error publishing: '" + article + "' - " + e.toString());
      }
    }
    return msgs;
  }

  /**
   * Generate the xml doc to send to crossref
   * <p/>
   *
   * @param articleXml - the article xml
   * @param articleId  - the article Id
   * @throws javax.xml.transform.TransformerException
   *          - if there's a problem transforming the article xml
   */
  @Override
  public void generateCrossrefInfoDoc(Document articleXml, URI articleId) throws TransformerException {
    log.info("Generating crossref info doc for article " + articleId);
    File crossrefXslFile;
    try {
      crossrefXslFile = new File(getClass().getClassLoader().getResource(crossrefXslTemplate).toURI());
    } catch (Exception e) {
      crossrefXslFile = new File(crossrefXslTemplate);
    }
    Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(crossrefXslFile));
    t.setParameter("plosDoiUrl", plosDoiUrl);
    t.setParameter("plosEmail", plosEmail);

    File target_xml =
        new File(ingestedDocumentDirectory, getCrossrefDocFileName(articleId));

    t.transform(new DOMSource(articleXml, articleId.toString()), new StreamResult(target_xml));
  }

  /**
   * Invokes all objects that are registered to listen to article publish event.
   *
   * @param articleId Article ID
   * @throws Exception If listener method failed
   */
  private void invokeOnPublishListeners(String articleId) throws Exception {
    if (onPublishListeners != null) {
      for (OnPublishListener listener : onPublishListeners) {
        listener.articlePublished(articleId);
      }
    }
  }

  private void removeFromCrossPubbedJournals(URI id) {
    for (Journal j : journalService.getAllJournals()) {
      List<URI> col = j.getSimpleCollection();
      if (col != null)
        while (col.contains(id))
          col.remove(id);
    }
  }

  /**
   * Invokes all objects that are registered to listen to article delete event.
   *
   * @param articleId Article ID
   * @throws Exception If listener method failed
   */
  private void invokeOnDeleteListeners(String articleId) throws Exception {
    if (onDeleteListeners != null) {
      for (OnDeleteListener listener : onDeleteListeners) {
        listener.articleDeleted(articleId);
      }
    }
  }

  /**
   * Convert from an article id to the name of the crossref info doc for that file
   *
   * @param articleId - the article id
   * @return a string usable as a distinct filename - ':', '/' and '.' -&gt; '_'
   */
  private String getCrossrefDocFileName(URI articleId) {
    return articleId.toString().replace(':', '_').replace('/', '_').replace('.', '_') + ".xml";
  }

  /**
   * Sets the JournalService.
   *
   * @param journalService The JournalService to set.
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * Sets the PermissionsService.
   *
   * @param permService The PermissionsService to set.
   */
  @Required
  public void setPermissionsService(PermissionsService permService) {
    this.permissionsService = permService;
  }

  /**
   * @param plosDoiUrl The plosDxUrl to set.
   */
  @Required
  public void setPlosDoiUrl(String plosDoiUrl) {
    this.plosDoiUrl = plosDoiUrl;
  }

  /**
   * @param plosEmail The plosEmail to set.
   */
  @Required
  public void setPlosEmail(String plosEmail) {
    this.plosEmail = plosEmail;
  }
}
