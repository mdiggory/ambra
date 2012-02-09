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

package org.ambraproject.article.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.FindArticleService;
import org.ambraproject.article.service.ThisArticleFound;

/**
 * Display online resources for the references in an article.
 * </p>
 * Lookup is performed on <a href="http://www.crossref.org">CrossRef</a>
 *
 * @author Dragisa Krsmanovic
 */
public class FindArticleAction extends BaseActionSupport {

  private static final Logger log = LoggerFactory.getLogger(FindArticleAction.class);

  private String author;
  private String title;

  private String crossRefUrl;
  private String pubGetUrl;

  private FindArticleService findArticleService;


  @Override
  public String execute() throws Exception {


    ThisArticleFound article = findArticleService.findArticle(title, author);

    if (article == null || article.getDoi() == null) {
      log.debug("No articles found on CrossRef");
      crossRefUrl = configuration.getString("ambra.services.crossref.guestquery.url");
    } else {
      crossRefUrl = "http://dx.doi.org/" + article.getDoi();
      pubGetUrl = article.getPubGetUri();
    }

    return SUCCESS;
  }

  @Required
  public void setFindArticleService(FindArticleService findArticleService) {
    this.findArticleService = findArticleService;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCrossRefUrl() {
    return crossRefUrl;
  }

  public String getPubGetUrl() {
    return pubGetUrl;
  }

}
