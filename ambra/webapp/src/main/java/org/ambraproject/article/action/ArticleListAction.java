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

package org.ambraproject.article.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.ArticleServiceSearchParameters;
import org.ambraproject.util.DateParser;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * TODO: Determine if this class is still being used.  Delete if not.
 *
 * Fetch as list of available articles.
 */
public class ArticleListAction extends BaseActionSupport {
  private ArrayList<String> messages = new ArrayList<String>();
  private ArticleService articleService;
  private Collection<String> articles;
  private String startDate;
  private String endDate;

  private static final Logger log = LoggerFactory.getLogger(ArticleListAction.class);

  public String execute() throws Exception {
    //  TODO: IF these articles should be restricted to the current journal, then use a Journal eIssn in this method call.

    ArticleServiceSearchParameters params = new ArticleServiceSearchParameters();
    params.setStartDate(DateParser.parse(startDate));
    params.setEndDate(DateParser.parse(endDate));
    params.setMaxResults(0);

    articles = articleService.getArticleDOIs(params);

    return SUCCESS;
  }

  /**
   * @return the list of available articles
   */
  public Collection<String> getArticles() {
    return articles;
  }

  public ArrayList<String> getMessages() {
    return messages;
  }

  /**
   * @return end date
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * Set end date
   * @param endDate endDate
   */
  public void setEndDate(final String endDate) {
    this.endDate = endDate;
  }

  /**
   * @return start date
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * Set start date
   * @param startDate startDate
   */
  public void setStartDate(final String startDate) {
    this.startDate = startDate;
  }

  /**
   * Sets the ArticleService.
   *
   * @param articleService The articleService
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }
}
