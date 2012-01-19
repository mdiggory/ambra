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

package org.ambraproject.search.service;

import org.apache.camel.Handler;
import org.apache.commons.configuration.Configuration;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.ApplicationException;
import org.ambraproject.admin.service.OnCrossPubListener;
import org.ambraproject.admin.service.OnDeleteListener;
import org.ambraproject.admin.service.OnPublishListener;
import org.ambraproject.article.service.ArticleDocumentService;
import org.ambraproject.queue.MessageSender;
import org.ambraproject.queue.Routes;
import org.ambraproject.service.AmbraMailer;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.models.Article;
import org.w3c.dom.Document;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

/**
 * Service class that handles article search indexing. It is plugged in as OnPublishListener into
 * DocumentManagementService.
 *
 * @author Dragisa Krsmanovic
 */
public class ArticleIndexingServiceImpl extends HibernateServiceImpl
  implements OnPublishListener, OnDeleteListener, OnCrossPubListener, ArticleIndexingService {

  private static final Logger log = LoggerFactory.getLogger(ArticleIndexingServiceImpl.class);

  protected static final int DEFAULT_INCREMENT_LIMIT_SIZE = 200;

  private AmbraMailer mailer;
  private ArticleDocumentService articleDocumentService;
  private MessageSender messageSender;
  private String indexingQueue;
  private String deleteQueue;
  private int incrementLimitSize;

  @Required
  public void setArticleDocumentService(ArticleDocumentService articleDocumentService) {
    this.articleDocumentService = articleDocumentService;
  }

  @Required
  public void setAmbraMailer(AmbraMailer m) {
    this.mailer = m;
  }

  @Required
  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  @Required
  public void setAmbraConfiguration(Configuration ambraConfiguration) {
    indexingQueue = ambraConfiguration.getString("ambra.services.search.articleIndexingQueue", null);
    if (indexingQueue != null && indexingQueue.trim().length() == 0) {
      indexingQueue = null;
    }
    log.info("Article indexing queue set to " + indexingQueue);
    
    deleteQueue = ambraConfiguration.getString("ambra.services.search.articleDeleteQueue", null);
    if (deleteQueue != null && deleteQueue.trim().length() == 0) {
      deleteQueue = null;
    }
    log.info("Article delete queue set to " + deleteQueue);

    incrementLimitSize = ambraConfiguration.getInt("ambra.services.search.incrementLimitSize",
      DEFAULT_INCREMENT_LIMIT_SIZE);
  }

  /**
   * Method that is fired on article publish operation.
   * <p/>
   * Message is sent to an asynchronous, SEDA queue and from there it's sent to plos-queue. That
   * way we ensure that publish operation will succeed even if ActiveMQ is down.
   *
   * @see Routes
   * @param articleId ID of the published article
   * @throws Exception if message send fails
   */
  @Transactional(readOnly = true)
  public void articlePublished(String articleId) throws Exception {
    if (indexingQueue != null) {
      log.info("Indexing published article " + articleId);
      indexOneArticle(articleId);
    } else {
      log.warn("Article indexing queue not set. Article " + articleId + " will not be indexed.");
    }
  }

  /**
   * Method that is fired on article delete operation.
   * <p/>
   * Message is sent to an asynchronous, SEDA queue and from there it's sent to plos-queue. That
   * way we ensure that delete operation will succeed even if ActiveMQ is down.
   *
   * @see Routes
   * @param articleId ID of the deleted article
   * @throws Exception if message send fails
   */
  public void articleDeleted(String articleId) throws Exception {
    if (deleteQueue != null) {
      log.info("Deleting article " + articleId + " from search index.");
      messageSender.sendMessage(Routes.SEARCH_DELETE, articleId);
    } else {
      log.warn("Article index delete queue not set. Article " + articleId + " will not be deleted from search index.");
    }
  }

  /**
   * Method that is fired on article cross publish operation.
   * <p/>
   * Message is sent to an asynchronous, SEDA queue and from there it's sent to plos-queue. That
   * way we ensure that cross publish operation will succeed even if ActiveMQ is down.
   *
   * @see Routes
   * @param articleId ID of the cross published article
   * @throws Exception if message send fails
   */
  @Transactional(readOnly = true)
  public void articleCrossPublished(String articleId) throws Exception {
    if (indexingQueue != null) {
      log.info("Indexing cross published article " + articleId);
      indexArticle(articleId);
    } else {
      log.warn("Article indexing queue not set. Article " + articleId + " will not be re-indexed.");
    }
  }

  public void startIndexingAllArticles() throws Exception {
     // Message content is unimportant here
    messageSender.sendMessage(Routes.SEARCH_INDEXALL, "start");
  }

  /**
   * Index one article. Disables filters so can be applied in any journal context.
   *
   * @param articleId Article ID
   * @throws Exception If operation fails
   */
  @Transactional(readOnly = true)
  public void indexArticle(String articleId) throws Exception {

    if (indexingQueue == null) {
      throw new ApplicationException("Article indexing queue not set. Article " + articleId + " will not be re-indexed.");
    }

    Document doc = articleDocumentService.getFullDocument(articleId);

    if (doc == null) {
      log.error("Search indexing failed for " + articleId + ". Returned document is NULL.");
      return;
    }

    messageSender.sendMessage(indexingQueue, doc);
  }

  /**
   * Same as indexArticle() except that it doesn't disable filters.
   *
   * @param articleId Article ID
   * @throws Exception If operation fails
   */
  private void indexOneArticle(String articleId) throws Exception {

    Document doc = articleDocumentService.getFullDocument(articleId);

    if (doc == null) {
      log.error("Search indexing failed for " + articleId + ". Returned document is NULL.");
      return;
    }

    messageSender.sendMessage(Routes.SEARCH_INDEX, doc);
  }


    /**
   * Send all articles for re-indexing.
   * <p/>
   * Queries to fetch all articles and to get all cross-published articles are separated to
   * speed up the process.
   * <p/>
   * This is Apache Camel handler. It is invoked asynchronously after user submits a message to SEDA
   * queue.
   *
   * @return Email message body
   * @throws Exception
   * @see org.ambraproject.queue.Routes
   */
  @Handler
  public String indexAllArticles() throws Exception {
    if (indexingQueue != null) {
      long timestamp = System.currentTimeMillis();

      Result result = indexAll(articleDocumentService, messageSender,
        indexingQueue, mailer, this.incrementLimitSize);

      StringBuilder message = new StringBuilder();
      message.append("Finished indexing ")
          .append(Integer.toString(result.total))
          .append(" articles in ")
          .append(Long.toString((System.currentTimeMillis() - timestamp) / 1000l))
          .append(" sec.");
      log.info(message.toString());

      if (result.failed > 0) {
        log.warn("Failed indexing " + result.failed + " articles");
        message.append("\nFailed indexing ")
            .append(Integer.toString(result.failed))
            .append(" articles.");
      }

      if (result.partialUpdate) {
        message.append("\nThere was an error while trying to index all the articles.  Only a subset of articles " +
          "have been reindexed.  Try reindexing all the articles again later.");
      }

      return message.toString();
    } else {
      throw new ApplicationException("Indexing queue not defined");
    }
  }

  /**
   * Index all documents
   *
   * @param articleDocumentService ArticleDocumentService
   * @param messageSender MessageSender
   * @param indexingQueue IndexingQueue
   * @param mailer ambra mailer

   * @param incrementLimitSize batch size for processing articles
   * @return Number of articles indexed
   * @throws Exception If operation fails
   */
  @SuppressWarnings("unchecked")
  private Result indexAll(
      final ArticleDocumentService articleDocumentService,
      final MessageSender messageSender,
      final String indexingQueue, final AmbraMailer mailer,
      final int incrementLimitSize) throws Exception {

    return (Result)hibernateTemplate.execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        boolean bContinue = true;

        List<URI> failedArticles = new ArrayList<URI>();
        int totalIndexed = 0;
        int totalFailed = 0;
        boolean partialUpdate = false;
        int offset = 0;

        while (bContinue) {
          try {
            // get the list of articles
            SQLQuery sql = session.createSQLQuery("select doi from article where state = :state");

            sql.setParameter("state", Article.STATE_ACTIVE);
            sql.setFirstResult(offset);
            sql.setMaxResults(incrementLimitSize);

            List<String> articleDois = sql.list();

            for(String articleId : articleDois) {
              try {
                // get the article xml and add the necessary information to the xml
                Document doc = articleDocumentService.getFullDocument(articleId);

                // send the article xml to plos-queue to be indexed
                messageSender.sendMessage(indexingQueue, doc);
                totalIndexed++;
              } catch (Exception e) {
                log.error("Error indexing article " + articleId, e);
                totalFailed++;
              }
            }

            offset = offset + incrementLimitSize;
            log.info("Offset " + offset);

            if (offset > (totalIndexed + totalFailed)) {
              // we have processed all the articles, exit the while loop
              bContinue = false;
            }
          } catch (Exception e) {
            bContinue = false;
            log.error("Error while gathering a list of articles", e);

            StringBuilder message = new StringBuilder("Error while gathering a list of articles. \n");
            message.append(e.getMessage());
            mailer.sendError(message.toString());

            partialUpdate = true;
          }
        } // end of while

        if(failedArticles.size() > 0) {
          StringBuilder message = new StringBuilder("Error getting XML for articles:\n");

          for(URI article : failedArticles) {
            message.append(article.toString());
            message.append("\n");
          }

          mailer.sendError(message.toString());
        }

        return new Result(totalIndexed, totalFailed, partialUpdate);
      }
    });
  }

  /**
   * Transfer object for 3 values
   */
  private static class Result {
    public final int total;
    public final int failed;
    public final boolean partialUpdate;

    private Result(int total, int failed, boolean partialUpdate) {
      this.total = total;
      this.failed = failed;
      this.partialUpdate = partialUpdate;
    }
  }

}
