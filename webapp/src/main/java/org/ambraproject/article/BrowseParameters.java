/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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
package org.ambraproject.article;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

/**
 * A pojo for passing Browse Parameters around
 *
 * @author Joe Osowski
 */
public class BrowseParameters {
  private String[] subjects;
  private Calendar startDate;
  private Calendar endDate;
  private List<URI> articleTypes;
  private int pageNum;
  private int pageSize;
  private String journalKey;
  private String sort;

  /**
   * Set the field to sort by
   * @param sort
   */
  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getSort() {
    return sort;
  }

  /**
   * Set the subjects to filter by
   * @param subjects
   */
  public void setSubjects(String[] subjects) {
    this.subjects = subjects;
  }

  public String[] getSubjects() {
    return subjects;
  }

  /**
   * the page-number for which to return articles; 0-based
   * @param pageNum
   */
  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageNum() {
    return pageNum;
  }

  /**
   *  set the number of articles per page, or -1 for all articles
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageSize() {
    return pageSize;
  }

  /**
   * Set the key of the current journal
   * @param journalKey
   */
  public void setJournalKey(String journalKey) {
    this.journalKey = journalKey;
  }

  public String getJournalKey() {
    return journalKey;
  }

  /**
   * set the earliest date for which to return articles (inclusive)
   * @return
   */
  public void setStartDate(Calendar startDate) {
    this.startDate = startDate;
  }

  public Calendar getStartDate() {
    return startDate;
  }

  /**
   * set the latest date for which to return articles (exclusive)
   * @param endDate
   */
  public void setEndDate(Calendar endDate) {
    this.endDate = endDate;
  }

  public Calendar getEndDate() {
    return endDate;
  }

  /**
   * The URIs indicating the types of articles which will be returned, or null for all types
   * @param articleTypes
   */
  public void setArticleTypes(List<URI> articleTypes) {
    this.articleTypes = articleTypes;
  }

  public List<URI> getArticleTypes() {
    return articleTypes;
  }
}
