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

package org.ambraproject.admin.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.search.service.ArticleIndexingService;

/**
 * @author Dragisa Krsmanovic
 */
public class IndexArticlesAction extends BaseAdminActionSupport {

  private static final Logger log = LoggerFactory.getLogger(IndexArticlesAction.class);

  private ArticleIndexingService articleIndexingService;
  private String articleId;
  private String email;

  @Override
  public String execute() throws Exception {
    // create a faux journal object for template
    initJournal();

    return SUCCESS;  // default action is just to display the template
  }

  @Required
  public void setArticleIndexingService(ArticleIndexingService articleIndexingService) {
    this.articleIndexingService = articleIndexingService;
  }

  public String getEmail() {
    return email;
  }

  public String getArticleId() {
    return articleId;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public String indexAll() throws Exception {
    // create a faux journal object for template
    initJournal();

    email = configuration.getString("ambra.services.search.indexingMailReceiver", null);

    if (email != null) {
      articleIndexingService.startIndexingAllArticles();
      return SUCCESS;
    } else {
      addActionError("ambra.services.search.indexingMailReceiver not defined");
      return ERROR;
    }
  }

  public String indexOne() throws Exception {
    // create a faux journal object for template
    initJournal();

    if (articleId == null) {
      addActionError("Article ID not provided");
      return SUCCESS;
    }

    articleId = articleId.trim().toLowerCase();

    try {
      articleIndexingService.indexArticle(articleId);
      addActionMessage(articleId + " sent for re-indexing");
    } catch (Exception e) {
      log.error("Re-indexing of article " + articleId + " failed", e);
      addActionError(e.getMessage());
    }
    return SUCCESS;
  }

}
