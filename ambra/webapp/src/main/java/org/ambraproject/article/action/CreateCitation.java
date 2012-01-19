/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.util.UriUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Action to create a citation.  Does not care what the output format is.
 *
 * @author Stephen Cheng
 *
 */
@SuppressWarnings("serial")
public class CreateCitation extends BaseActionSupport {
  private static final Logger log = LoggerFactory.getLogger(CreateCitation.class);
  private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
  private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
  private static final String INFO_DOI_PREFIX = "info:doi/";

  private ArticleService articleService;     // OTM service Spring injected.

  private String articleURI;
  private String journal;
  private String eLocationId;
  private String doi;
  private String volume;
  private String title;
  private String issue;
  private String summary;
  private String url;
  private String publisherName;
  private String month;
  private String day;
  private String year;
  private Date publishedDate;

  //needs to not be named 'authors' else it will conflict with some freemarker variables in the global nav bar
  private List<ArticleAuthor> authorList;
  private List<String> collaborativeAuthors;

  /**
   * Get Citation object from database
   */
  @Override
  @Transactional(readOnly = true)
  public String execute () throws Exception {

    try {
      UriUtil.validateUri(articleURI, "articleUri=<" + articleURI + ">");
    } catch (Exception ex) {
      return ERROR;
    }

    try {
      Article article = articleService.getArticle(articleURI, getAuthId());

      title = article.getTitle();

      authorList = article.getAuthors();
      collaborativeAuthors = article.getCollaborativeAuthors();

      doi = article.getDoi().replaceAll(INFO_DOI_PREFIX,"");
      eLocationId = article.geteLocationId();
      volume = article.getVolume();
      journal = article.getJournal();
      issue = article.getIssue();
      summary = article.getDescription();
      url = article.getUrl();
      publisherName = article.getPublisherName();
      publishedDate = article.getDate();

      year = yearFormat.format(publishedDate);
      month = monthFormat.format(publishedDate);
      day = dayFormat.format(publishedDate);


    } catch (NoSuchArticleIdException ex) {
      return ERROR;
    }
    
    return SUCCESS;
  }

  /**
   * @param articleService ArticleService Spring Injected
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @return Returns the articleURI.
   */
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * @param articleURI The articleURI to set.
   */
  public void setArticleURI(String articleURI) {
    this.articleURI = articleURI;
  }

  /**
   * @return Returns the article title
   */
  public String getTitle() {
    return title;
  }

  /**
   *
   * @return the authors of the article
   */
  public List<ArticleAuthor> getAuthorList() {
    return authorList;
  }

  /**
   * Get a list of collaborative authors
   * @return
   */
  public List<String> getCollaborativeAuthors()
  {
    return collaborativeAuthors;
  }

  public String getJournal() {
    return journal;
  }

  public String getELocationId() {
    return eLocationId;
  }

  public String getDoi() {
    return doi;
  }

  public String getIssue() {
    return issue;
  }

  public String getVolume() {
    return volume;

  }

  public String getSummary() {
    return summary;
  }

  public String getMonth() {
    return month;
  }

  public String getDay() {
    return day;
  }

  public String getPublisherName() {
    return publisherName;
  }

  public String getYear() {
    return year;
  }

  public Date getPublishedDate() {
    return publishedDate;
  }

  public String getUrl() {
    return url;
  }
}
