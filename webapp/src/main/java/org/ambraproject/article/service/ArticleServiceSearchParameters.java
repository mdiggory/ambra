/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-$today.year by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ambraproject.article.service;

import java.util.Date;

/**
 * @author Joe Osowski
 */
public class ArticleServiceSearchParameters
{
  private Date startDate;
  private Date endDate;
  private String[] categories;
  private String[] authors;
  private String eIssn;
  private int[] states;
  private String orderField;
  private boolean isOrderAscending;
  private int maxResults;

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String[] getCategories() {
    return categories;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }

  public String[] getAuthors() {
    return authors;
  }

  public void setAuthors(String[] authors) {
    this.authors = authors;
  }

  public String geteIssn() {
    return eIssn;
  }

  public void seteIssn(String eIssn) {
    this.eIssn = eIssn;
  }

  public int[] getStates() {
    return states;
  }

  public void setStates(int[] states) {
    this.states = states;
  }

  public String getOrderField() {
    return orderField;
  }

  public void setOrderField(String orderField) {
    this.orderField = orderField;
  }

  public boolean isOrderAscending() {
    return isOrderAscending;
  }

  public void setOrderAscending(boolean orderAscending) {
    isOrderAscending = orderAscending;
  }

  public int getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(int maxResults) {
    this.maxResults = maxResults;
  }
}
