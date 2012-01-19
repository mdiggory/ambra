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

package org.ambraproject.admin.action;

import org.ambraproject.models.Syndication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.ambraproject.admin.service.DocumentManagementService;
import org.ambraproject.admin.service.SyndicationService;
import org.ambraproject.article.service.*;
import org.ambraproject.models.Article;
import org.ambraproject.struts2.ManualTransactionManagement;
import org.ambraproject.util.UriUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

@SuppressWarnings("serial")
public class AdminTopAction extends BaseAdminActionSupport {

  private static final Logger log = LoggerFactory.getLogger(AdminTopAction.class);
  private static Boolean sync = false;
  private Collection<String> uploadableFiles;
  private List<Article> publishableArticles;
  private Map<String, List<Syndication>> syndicationMap;
  private List<Syndication> syndications;

  private ArticleService articleService;
  private DocumentManagementService documentManagementService;
  private Ingester ingester;
  private SyndicationService syndicationService;
  private IngestArchiveProcessor ingestArchiveProcessor;
  private PlatformTransactionManager transactionManager;

  // Fields used for delete
  private String article;
  private String action;

  // Fields used for ingest
  private String[] filesToIngest;
  private Boolean force = false;

  // Fields used for processArticles
  private String[] articles;
  private String[] syndicates;

  // Used only for resyndicating articles that previously failed their syndications.
  private String[] resyndicates;
  private Boolean isFailedSyndications = false;

  private String orderBy;


  /**
   * Main entry point to AdminTopAction.
   *
   * @return SUCCESS/ERROR
   * @throws Exception
   */
  @Override
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    if (!setCommonFields())
      return ERROR;

    return SUCCESS;
  }

  /**
   * Struts action method
   *
   * @return Struts result
   * @throws Exception when error occurs
   */
  @Transactional(rollbackFor = {Throwable.class})
  public String unpublish() throws Exception {
    if (article != null)
      article = article.trim();

    try {
      UriUtil.validateUri(article, "Article Uri");

      documentManagementService.unPublish(article, getAuthId());

      addActionMessage("Successfully unpublished article: " + article);

    } catch (Exception e) {
      addActionError("Failed to successfully unpublish article: " + article + ". <br>" + e);
      log.error("Failed to successfully unpublish article: " + article, e);
    }

    if (!setCommonFields())
      return ERROR;

    return SUCCESS;
  }

  /**
   * Struts action method
   *
   * @return Struts result
   * @throws Exception when error occurs
   */
  @Transactional(rollbackFor = {Throwable.class})
  public String disableArticle() throws Exception {
    Boolean result = true;

    if (article != null)
      article = article.trim();

    UriUtil.validateUri(article, "Article Uri");

    result = disableOneArticle(article);

    if (!setCommonFields())
      return ERROR;

    if (result) {
      return SUCCESS;
    } else {
      return ERROR;
    }
  }

  /**
   * Ingest the files made available in the ingestion-queue.
   * <p/>
   * TODO: Don't manually manage transactions here. We're creating a transaction for each ingest because with the {@link
   * org.ambraproject.struts2.TransactionInterceptor} there would be a transaction wrapped around the whole
   * request, and a failure of one article to ingest would cause them all to fail.
   * <p/>
   * See the javadoc on that the {@link org.ambraproject.struts2.TransactionInterceptor} for the refactoring
   * necessary to pass transaction management off to the framework
   *
   * @return SUCCESS/ERROR
   */
  @ManualTransactionManagement
  public String ingest() {
    if (filesToIngest != null) {
      for (String file : filesToIngest) {

        final String filename = file.trim();
        final File archive = new File(documentManagementService.getDocumentDirectory(), filename);
        log.info("Starting ingest of " + filename);
        org.ambraproject.models.Article article = null;

        try {
          TransactionTemplate template = new TransactionTemplate(transactionManager);
          article = (org.ambraproject.models.Article) template.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
              try {
                return ingester.ingest(new ZipFile(archive), force);
              } catch (DuplicateArticleIdException de) {
                addActionError("Error ingesting: " + filename + " - " + getMessages(de));
                transactionStatus.setRollbackOnly();
                log.info("attempted to ingest duplicate article without force; archive: " + filename, de);
                return null;
              } catch (Exception e) {
                addActionError("Error ingesting: " + filename + " - " + getMessages(e));
                transactionStatus.setRollbackOnly();
                log.info("Error ingesting article: " + filename, e);
                return null;
              }
            }
          });

        } catch (Exception e) {
          log.warn("error committing transaction during ingest of " + filename, e);
          addActionError("Error ingesting: " + filename + " - " + getMessages(e));
        }
        if (article != null) {
          try {
            documentManagementService.generateCrossrefInfoDoc(
                ingestArchiveProcessor.extractArticleXml(new ZipFile(archive)), URI.create(article.getDoi()));
            documentManagementService.generateIngestedData(archive, article.getDoi());
            log.info("Successfully ingested archive '" + filename + ", stored article " + article.getDoi());
            addActionMessage("Ingested: " + filename);
          } catch (Exception e) {
            log.error("error moving files after successful ingest", e);
            addActionError("Error ingesting: " + filename + " - " + getMessages(e));
          }
        }
      }
    }

    // create a faux journal object for template
    TransactionTemplate template = new TransactionTemplate(transactionManager);
    template.setReadOnly(true);
    return (String) template.execute(new TransactionCallback() {
      @Override
      public Object doInTransaction(TransactionStatus status) {
        if (!setCommonFields()) {
          return ERROR;
        }

        return SUCCESS;
      }
    });
  }

  /**
   * Extract the message string from an exception object.
   *
   * @param t The throwable containing the message that will be extracted.
   * @return message string
   */
  private static String getMessages(Throwable t) {
    StringBuilder msg = new StringBuilder();
    while (t != null) {
      msg.append(t.toString());
      t = t.getCause();
      if (t != null)
        msg.append("<br/>\n");
    }
    return msg.toString();
  }

  /**
   * Process submitted article list for either deletion of publication and syndication.
   *
   * @return SUCCESS/ERROR
   */
  public String processArticles() {
    if ("Disable and Revert Ingest".equals(action)) {
      Boolean result = disableArticles();
      if (!setCommonFields())
        return ERROR;

      return result ? SUCCESS : ERROR;
    }

    if ("Publish and Syndicate".equals(action)) {
      //If publish succeeds but syndication fails, we'll report and log the error and keep going
      //But if pub fails, we won't want to syndicate.  Hence the follow line
      Boolean result = publishArticles() && syndicateArticles(syndicates);

      if (!setCommonFields())
        return ERROR;

      return result ? SUCCESS : ERROR;
    }

    // sort the publishable articles by published date in ascending order
    if ("Sort by Pub Date Asc".equals(action)) {
      orderBy = "pubdate asc";
      if (!setCommonFields())
        return ERROR;

      return SUCCESS;
    }

    // sort the publishable articles by published date in descending order
    if ("Sort by Pub Date Desc".equals(action)) {
      orderBy = "pubdate desc";
      if (!setCommonFields())
        return ERROR;

      return SUCCESS;
    }

    // sort the publishable articles by doi in ascending order
    if ("Sort by DOI Asc".equals(action)) {
      orderBy = "doi asc";

      if (!setCommonFields())
        return ERROR;

      return SUCCESS;
    }

    // sort the publishable articles by doi in descending order
    if ("Sort by DOI Desc".equals(action)) {
      orderBy = "doi desc";

      if (!setCommonFields())
        return ERROR;

      return SUCCESS;
    }

    setCommonFields();
    addActionError("Invalid action received: " + action);

    return ERROR;
  }

  /**
   * Submit for syndication all the FAILED Syndications that were displayed on this page.
   *
   * @return SUCCESS/ERROR
   */
  public String resyndicateFailedArticles() {
    Boolean result = syndicateArticles(resyndicates);
    if (!setCommonFields())
      return ERROR;

    return result ? SUCCESS : ERROR;
  }

  /**
   * Queue for syndication the set of user selected articles.
   *
   * @return true if syndication was successfully queued.
   */
  private boolean syndicateArticles(String[] synArray) {
    if (synArray == null)
      return true;

    Boolean result = true;

    for (String t : synArray) {
      //Create syndication task for this article and these DOIS:
      String[] values = t.split("::");

      if (values.length != 2) {
        addActionMessage("Can not parse received value:" + t);
        continue;
      }
      String doi;
      String target = values[1];
      try {
        doi = URLDecoder.decode(values[0], "UTF-8");
      } catch (UnsupportedEncodingException ex) {
        addActionMessage("EncodingException:" + ex.getMessage());
        log.error("EncodingException", ex);
        result = false;
        continue;
      }
      Syndication syndication = null;
      try {
        syndication = syndicationService.syndicate(doi, target);
      } catch (NoSuchArticleIdException e) {
        addActionError("Article " + doi + " does not exist. Couldn't syndicate");
        log.error("Attempted to syndicate to a non-existent article: " + doi, e);
        continue;
      }

      if (Syndication.STATUS_FAILURE.equals(syndication.getStatus())) {
        addActionError("Exception: " + syndication.getErrorMessage());
        log.error("Error syndicating articles", syndication.getErrorMessage());
        result = false;
      } else {
        addActionMessage("Syndicated: " + doi + " to " + target);
      }

    }

    return result;
  }

  /**
   * Publish the a set of user selected articles that have already been ingested.
   *
   * @return true if all the articles are published.
   */
  private boolean publishArticles() {
    if (articles == null) {
      addActionError("No articles selected to publish.");
      return false;
    }

    try {
      List<String> msgs = documentManagementService.publish(articles, getAuthId());
      for (String msg : msgs)
        addActionMessage(msg);

      return true;
    } catch (Exception e) {
      addActionError("Exception: " + e);
      log.error("Error publishing archives", e);
      return false;
    }
  }

  /**
   * Disables articles with DOIs specified by the user.
   *
   * @return true if article successfully deleted.
   */
  private Boolean disableArticles() {
    Boolean result = true;
    if (articles == null) {
      addActionError("No articles selected to disable.");
      return false;
    }

    for (String article : articles) {
      result = result && disableOneArticle(article);
    }

    return result;
  }

  private Boolean disableOneArticle(String articleDoi) {
    try {
      documentManagementService.disable(articleDoi, getAuthId());
    } catch (Exception e) {
      addActionError("Error disabling: " + articleDoi + " - " + e);
      return false;
    }

    try {
      documentManagementService.revertIngestedQueue(articleDoi);
    } catch (Exception ioe) {
      log.warn("Error cleaning up spool directories for '" + articleDoi +
          "' - manual cleanup required", ioe);
      addActionError("Failed to move " + articleDoi + " back to ingestion queue: " + ioe);

      return false;
    }

    addActionMessage("Successfully disabled Article: " + articleDoi);

    return true;
  }

  /**
   * @param s array of articles to process
   */
  public void setArticles(String[] s) {
    articles = s;
  }

  /**
   * All the individual actions handled by adminTopAction need to provide a common set on information for the ftl to
   * display.
   *
   * @return true if there was no error when setting the fields
   */
  private boolean setCommonFields() {
    // create a faux journal object for template.  Ensures correct display of page.
    initJournal();

    // catch all Exceptions to keep Admin console active (vs. Site Error)
    try {
      uploadableFiles = documentManagementService.getUploadableFiles();

      if (orderBy == null) {
        orderBy = "doi asc";
      }

      if (orderBy.equals("doi asc")) {
        publishableArticles = articleService.getPublishableArticles(
            getJournal().geteIssn(), ArticleService.ORDER_BY_FIELD_ARTICLE_DOI, true);
      } else if (orderBy.equals("doi desc")) {
        publishableArticles = articleService.getPublishableArticles(
            getJournal().geteIssn(), ArticleService.ORDER_BY_FIELD_ARTICLE_DOI, false);
      } else if (orderBy.equals("pubdate asc")) {
        publishableArticles = articleService.getPublishableArticles(
            getJournal().geteIssn(), ArticleService.ORDER_BY_FIELD_DATE_PUBLISHED, true);
      } else if (orderBy.equals("pubdate desc")) {
        publishableArticles = articleService.getPublishableArticles(
            getJournal().geteIssn(), ArticleService.ORDER_BY_FIELD_DATE_PUBLISHED, false);
      }

      // get the recent article syndication activity for display
      syndications = syndicationService.getFailedAndInProgressSyndications(getCurrentJournal());
      // check whether any of the recent article syndications have a FAILED status. Default to false.
      for (Syndication syn : syndications) {
        isFailedSyndications = syn.getStatus().equals(Syndication.STATUS_FAILURE);
        if (isFailedSyndications)
          break;
      }

      syndicationMap = new HashMap<String, List<Syndication>>();
      //Map syndications to the publishable articles
      for (Article article : publishableArticles) {
        syndicationMap.put(article.getDoi(), syndicationService.getSyndications(article.getDoi()));
      }

    } catch (Exception e) {
      log.error("Admin console Exception", e);
      addActionError("Exception: " + e);
      return false;
    }
    return true;
  }


  /**
   * Sets the ArticleService.
   *
   * @param articleService The article persistence service
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * Sets the DocumentManagementService.
   *
   * @param documentManagementService The document management service
   */
  @Required
  public void setDocumentManagementService(DocumentManagementService documentManagementService) {
    this.documentManagementService = documentManagementService;
  }

  /**
   * Sets service used to syndicate these articles to external organizations
   *
   * @param syndicationService The service used to syndicate these articles to external organizations
   */
  @Required
  public void setSyndicationService(SyndicationService syndicationService) {
    this.syndicationService = syndicationService;
  }

  /**
   * Set the service used to ingest new articles
   *
   * @param ingester - the ingester to use
   */
  @Required
  public void setIngester(Ingester ingester) {
    this.ingester = ingester;
  }

  /**
   * Set the IngestArchiveProcessor to use to extract article xml from an ingest archive
   *
   * @param ingestArchiveProcessor - the xml processor to use
   */
  @Required
  public void setIngestArchiveProcessor(IngestArchiveProcessor ingestArchiveProcessor) {
    this.ingestArchiveProcessor = ingestArchiveProcessor;
  }

  /**
   * Set the transaction manager used to manage transactions for ingesting
   * <p/>
   * TODO: obviate the need for this.  See comments on ingest()
   *
   * @param transactionManager - the transaction manager to use
   */
  @Required
  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }


  /**
   * Gets the collection of uploadable files
   *
   * @return List of file names
   */
  public Collection<String> getUploadableFiles() {
    return uploadableFiles;
  }

  /**
   * Get the auto ingest sync state
   *
   * @return a true if auto ingest is in progress.
   */
  public Boolean getAutoIngesting() {
    return sync;
  }

  /**
   * Gets all of ingested articles that are not published yet, keyed off of their Article IDs.
   *
   * @return Map of articles keyed off of their Article IDs
   */
  public List<Article> getPublishableArticles() {
    return publishableArticles;
  }

  /**
   * Gets all of ingested articles that are not published yet, keyed off of their Article IDs.
   *
   * @return Map of articles keyed off of their Article IDs
   */
  public Map<String, List<Syndication>> getPublishableSyndications() {
    return syndicationMap;
  }

  /**
   * Form field setter
   *
   * @param a article id
   */
  public void setArticle(String a) {
    article = a;
  }

  /**
   * Form field setter for action The action parameter is the value of the button pressed on the front End, and
   * determines what actions to take
   *
   * @param s the current action
   */
  public void setAction(String s) {
    action = s;
  }

  /**
   * Form field setter
   *
   * @return the current action
   */
  public String getAction() {
    return action;
  }

  /**
   * Form field setter
   *
   * @param files list of files
   */
  public void setFilesToIngest(String[] files) {
    filesToIngest = files;
  }

  /**
   * Set the array of article/syndication options for syndicating articles when those articles are published
   *
   * @param s
   */
  public void setSyndicates(String[] s) {
    syndicates = s;
  }

  /**
   * Set the array of article/syndication options for syndicating articles that have previously had their syndications
   * fail.
   *
   * @param r
   */
  public void setResyndicates(String[] r) {
    resyndicates = r;
  }

  /**
   * Whether there is at least one FAILED syndication in the list returned by the <code>getSyndications()</code>
   * method.
   *
   * @return true if there are any FAILED syndications that will be shown on the page
   */
  public Boolean getIsFailedSyndications() {
    return isFailedSyndications;
  }

  /**
   * Form field setter
   *
   * @param flag true or false
   */
  public void setForce(boolean flag) {
    force = flag;
  }

  /**
   * Get the most recent syndication activity
   *
   * @return a list of syndications
   */
  public List<Syndication> getSyndications() {
    return syndications;
  }

  /**
   * Sets the list of syndications to display
   *
   * @param syndications a list of syndications
   */
  public void setSyndications(List<Syndication> syndications) {
    this.syndications = syndications;
  }

  /**
   * Getter for order by publishable articles
   *
   * @return order by publishable articles
   */
  public String getOrderByPublishableArticles() {
    return orderBy;
  }
}