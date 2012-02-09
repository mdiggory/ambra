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
import org.ambraproject.article.BrowseParameters;
import org.ambraproject.article.BrowseResult;
import org.ambraproject.article.service.BrowseService;
import org.ambraproject.model.article.Years;
import org.ambraproject.search.SearchHit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author stevec
 */
@SuppressWarnings("serial")
public class BrowseArticlesAction extends BaseActionSupport {
  private static final Logger log  = LoggerFactory.getLogger(BrowseArticlesAction.class);

  private static final int PAGE_SIZE    = 10;
  private static final String DATE_FIELD   = "date";

  private static final int UNSET      = -1;
  private static final int PAST_MONTH = -2;
  private static final int PAST_3MON  = -3;
  private static final int LAST_DAY = 1;

  private String field;
  private String sort;
  private String forceNoSubject = "false";

  private int startPage;
  private int pageSize = PAGE_SIZE;
  private int year     = UNSET;
  private int month    = UNSET;
  private int day      = UNSET;

  private BrowseService browseService;

  private SortedMap<String, Long> subjects;
  private Map<String, Long> subjectFacet;
  private String[] selectedSubjects = new String[0];

  private Years articleDates;
  private List<SearchHit> articleList;
  private long totalArticles;
  private String startDateParam;
  private String endDateParam;


  @Override
  public String execute() throws Exception {
    try {
      browseService.pingSolr();
    } catch (Exception e) {
      log.error("Could not talk to the Solr Server", e);
      return ERROR;
    }

    if (DATE_FIELD.equals(getField())) {
      return browseDate();
    }
    return browseCategory();
  }

  private String browseCategory() {
    //ForceNosubject provide a way for the freemarker template to display articles
    //Without a subject filter.  As some templates expect a default to one subject area
    //If none are selected.

    // if we are on a browse page with default sort value set in the configuration,
    // and the sort value is not set, use the default sort value
    String configKey = "ambra.virtualJournals." + this.getCurrentJournal() + ".ambra.services.browse.defaultSubjectSort";
    if (this.configuration.containsKey(configKey)) {
      String defaultSubjectSort = this.configuration.getString(configKey);

      if (this.sort == null || (this.sort != null && this.sort.length() == 0)) {
        this.sort = defaultSubjectSort;
      }
    }

    //If emptySubjects is true, or some subjects are selected.
    if (
        (forceNoSubject != null && forceNoSubject.equals("true")) ||
        (this.selectedSubjects != null && this.selectedSubjects.length > 0)
      ) {
      BrowseParameters params = new BrowseParameters();

      params.setSubjects(this.selectedSubjects);
      params.setPageNum(this.startPage);
      params.setPageSize(this.pageSize);
      params.setJournalKey(this.getCurrentJournal());
      params.setSort(this.sort);

      BrowseResult browseResult = this.browseService.getArticlesBySubject(params);

      this.articleList = browseResult.getArticles();
      this.totalArticles = browseResult.getTotal();
      this.subjects = browseService.getSubjectsForJournal(this.getCurrentJournal());
      this.subjectFacet = browseResult.getSubjectFacet();
    } else {
      this.subjects = browseService.getSubjectsForJournal(this.getCurrentJournal());

      //Are subjects ever empty?
      if(subjects != null && subjects.size() > 0) {
        //Select first subject in list if no subjects selected
        this.selectedSubjects = new String[] { this.subjects.firstKey() };

        BrowseParameters params = new BrowseParameters();

        params.setSubjects(this.selectedSubjects);
        params.setPageNum(this.startPage);
        params.setPageSize(this.pageSize);
        params.setJournalKey(this.getCurrentJournal());
        params.setSort(this.sort);

        BrowseResult browseResult = browseService.getArticlesBySubject(params);

        this.articleList = browseResult.getArticles();
        this.totalArticles = browseResult.getTotal();
        this.subjectFacet = browseResult.getSubjectFacet();
      } else {
        this.articleList = Collections.emptyList();
        this.subjectFacet = Collections.emptyMap();
        this.totalArticles = 0;
      }
    }

    return SUCCESS;
  }

  private String browseDate() {
    Calendar startDate = Calendar.getInstance();
    startDate.set(Calendar.HOUR_OF_DAY, 0);
    startDate.set(Calendar.MINUTE,      0);
    startDate.set(Calendar.SECOND,      0);
    startDate.set(Calendar.MILLISECOND, 0);

    Calendar endDate;

    boolean isLastDay = false;

    if (((startDateParam != null) && startDateParam.length() > 0) ||
        ((endDateParam != null) && endDateParam.length() > 0) ) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        if (startDateParam != null) {
          startDate.setTime(sdf.parse(startDateParam));
        } else {
          startDate.setTimeInMillis(0);
        }
        endDate = Calendar.getInstance();
        if (endDateParam != null) {
          endDate.setTime(sdf.parse(endDateParam));
        }
      } catch (Exception e) {
        log.error("Failed to parse start / end Date params");
        return ERROR;
      }
    } else {
      if (getYear() > UNSET && getMonth() > UNSET && getDay () > UNSET) {
        // user has clicked on a date link
        startDate.set(getYear(), getMonth() - 1, getDay());
        endDate = (Calendar) startDate.clone();

      } else if (getYear() > UNSET && getMonth() > UNSET) {
        // user has clicked on a month link
        startDate.set(getYear(), getMonth() - 1, 1);
        endDate = (Calendar) startDate.clone();
        int lastDay = endDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        endDate.set(Calendar.DAY_OF_MONTH, lastDay);

      } else if (getMonth() == PAST_MONTH) {
        // user has clicked on the past month link
        endDate = (Calendar) startDate.clone();
        startDate.add(Calendar.MONTH, -1);

      } else if (getMonth() == PAST_3MON) {
        // user has clicked on the past 3 months link
        endDate = (Calendar) startDate.clone();
        startDate.add(Calendar.MONTH, -3);

      } else if (getDay() == LAST_DAY) {
        // user has clicked on the past day link
        endDate = (Calendar) startDate.clone();
        startDate.add(Calendar.DATE, -1);
        isLastDay = true;
      }
      else {
        // user has clicked on the past week link
        endDate = (Calendar) startDate.clone();
        startDate.add(Calendar.DATE, -7);
      }
    }
    BrowseParameters params = new BrowseParameters();

    params.setStartDate(startDate);
    params.setEndDate(endDate);
    params.setPageNum(this.startPage);
    params.setPageSize(this.pageSize);
    params.setJournalKey(this.getCurrentJournal());
    params.setSort(this.sort);

    BrowseResult result = browseService.getArticlesByDate(params);

    // want the last day that we have articles published
    if (isLastDay) {
      while (result.getArticles() == null || (result.getArticles() != null && result.getArticles().size() == 0)) {
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        params.setStartDate(startDate);
        result = browseService.getArticlesByDate(params);
      }
    }

    this.articleList = result.getArticles();
    this.totalArticles = result.getTotal();
    this.articleDates = browseService.getArticleDatesForJournal(this.getCurrentJournal());
    this.subjectFacet = null;
    //When filtering by date, select all the subjects
    this.subjects = browseService.getSubjectsForJournal(this.getCurrentJournal());

    return SUCCESS;
  }

  /**
   * @return Returns the field.
   */
  public String getField() {
    if (field == null) {
      field = "";
    }
    return field;
  }

  /**
   * @param field The field to set.
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * @return Returns the start.
   */
  public int getStartPage() {
    return startPage;
  }

  /**
   * @param startPage The start to set.
   */
  public void setStartPage(int startPage) {
    this.startPage = startPage;
  }

  /**
   * @return Returns the day.
   */
  public int getDay() {
    return day;
  }

  /**
   * @param day The day to set.
   */
  public void setDay(int day) {
    this.day = day;
  }

  /**
   * @return Returns the month.
   */
  public int getMonth() {
    return month;
  }

  /**
   * @param month The month to set.
   */
  public void setMonth(int month) {
    this.month = month;
  }

  /**
   * @return Returns the year.
   */
  public int getYear() {
    return year;
  }

  /**
   * @param year The year to set.
   */
  public void setYear(int year) {
    this.year = year;
  }

  /**
   * @return Returns the pageSize.
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * @param pageSize The pageSize to set.
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * return a set of the articleDates broken down by year, month, and day.
   *
   * @return Collection of dates
   */
  public Years getArticleDates() {
    return articleDates;
  }

  /**
   * @return Returns a list a complete list of subjects
   */
  public Map<String, Long> getSubjects() {
    return this.subjects;
  }

  /**
   * @return Returns a facetedList of subjects.
   * That is subject areas that are included in the current result set
   */
  public Map<String, Long> getFacetedSubjects() {
    return this.subjectFacet;
  }
  
  /**
   * @return Returns the article list for this page.
   */
  public Collection<SearchHit> getArticleList() {
    return articleList;
  }

  /**
   * @return Returns the total number of articles in the category or date-range
   */
  public long getTotalArticles() {
    return totalArticles;
  }

  /**
   * Setter for 'sort', the clause which orders the query results.
   * @param sort The sort order for the search results
   */
  public void setSort(final String sort) {
    this.sort = sort;
  }

  /**
   * Getter for 'sort', the clause which orders the query results.
   * @return The sort order for the search results
   */
  public String getSort() {
    return this.sort;
  }

  public List getSorts()
  {
    return browseService.getSorts();
  }

  /**
   * @param browseService The browseService to set.
   */
  @Required
  public void setBrowseService(BrowseService browseService) {
    this.browseService = browseService;
  }

  private static String canonicalCategoryPath(String categoryName) {
    try {
      return URLEncoder.encode(categoryName, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new Error(e);
    }
  }

  public void setStartDateParam(String startDateParam) {
    this.startDateParam = startDateParam;
  }

  public void setEndDateParam(String endDateParam) {
    this.endDateParam = endDateParam;
  }

  /**
   * Gets the list of selected subjects
   * @return
   */
  public String[] getSelectedSubjects() {
    return this.selectedSubjects;
  }

  /**
   * Sets a list of selected subjects
   * @return
   */
  public void setSelectedSubjects(String[] selectedSubjects) {
    this.selectedSubjects = selectedSubjects;
  }

  /**
   * Set the forceNoSubject value
   * @param forceNoSubject
   */
  public void setForceNoSubject(String forceNoSubject)
  {
    this.forceNoSubject = forceNoSubject;
  }

  /**
   * @return get the forceNoSubject value
   */
  public String getForceNoSubject()
  {
    return this.forceNoSubject;
  }
}
