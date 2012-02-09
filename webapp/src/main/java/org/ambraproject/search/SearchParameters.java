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

package org.ambraproject.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manage all of the parameters for a search.
 * <p/>
 * All String input in trimmed.
 * The <code>get...()</code> methods that return Strings will never return null.
 * If any field is set to null, then its <code>get...</code> method (that returns a String)
 * will return an empty (zero-length) String.
 * This is done to simplify display logic in the web layer.
 * <p/>
 * In every method that sets a String, a NULL is replaced with a String of zero length.
 * <p/>
 * In every method that sets a String Array, a NULL is replaced with an array with zero elements.
 * Every array element is trimmed.  Elements that are trimmed to zero length are removed from the array.
 * <p/>
 * Dates are stored as Date objects, whether they are set from Strings or Dates.
 * Empty date Strings create null Date objects.
 * Therefore, <code>getStartDate()</code> and <code>getEndDate()</code> can return null,
 * so <code>getStartDateAsString()</code> and <code>getEndDateAsString()</code> should be used when
 * working with the web interface.
 *
 * @author Scott Sterling
 */
public class SearchParameters implements Serializable {

  private static final long serialVersionUID = -7837640277704487950L;

  // Used only by Simple Search
  private String        query                 = "";

  // Used only by Query Builder search
  private String        unformattedQuery      = "";

  // Used by Find An Article search
  private String        volume                = "";
  private String        eLocationId           = "";
  private String        id                    = ""; // One DOI.  These are indexed as "ID" in Solr

  // Used to create query filters on any search
  private String[]      filterSubjects      = {}; // Only search in these subjects
  private String        filterKeyword         = ""; // Only search in this document part
  private String        filterArticleType     = ""; // Only search for this article type
  private String[]      filterJournals        = {}; // Only search these Journals. If no elements, then default to the current journal

  // Controls results order
  private String        sort                  = "";

  //  Formatting elements
  private int           startPage = 0;
  private int           pageSize = 0;

  public String getQuery() {
    return query;
  }
  public void setQuery(String query) {
    if (query == null || query.trim().length() < 1)
      this.query = "";
    else
      this.query = query.trim();
  }

  /**
   * Get a query that is meant to be handed directly to the query engine (e.g., Solr) without any interpretation or reformatting.
   * This query is a separate entity from the other queries brokered by this class.  In other
   * words, it is <strong>not</strong> composed from any other fields in this class.
   *
   * @return A query meant to be handed directly to the query engine
   *   without any interpretation or reformatting
   */
  public String getUnformattedQuery() {
    return unformattedQuery;
  }

  /**
   * Set a query that is meant to be handed directly to the query engine (e.g., Solr)
   * without any interpretation or reformatting.
   * This query is a separate entity from the other queries brokered by this class.  In other
   * words, it is <strong>not</strong> composed from any other fields in this class.
   *
   * @param unformattedQuery A query meant to be handed directly to the query engine
   *   without any interpretation or reformatting
   */
  public void setUnformattedQuery(String unformattedQuery) {
    if (unformattedQuery == null || unformattedQuery.trim().length() < 1)
      this.unformattedQuery = "";
    else
      this.unformattedQuery = unformattedQuery.trim();
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    if (volume == null || volume.trim().length() < 1)
      this.volume = "";
    else
      this.volume = volume.trim();
  }

  public String getFilterArticleType() {
    return filterArticleType;
  }

  public void setFilterArticleType(String articleType) {
    if (articleType == null || articleType.trim().length() < 1)
      this.filterArticleType = "";
    else
      this.filterArticleType = articleType.trim();
  }

  public String getFilterKeyword() {
    return filterKeyword;
  }

  public void setFilterKeyword(String keyword) {
    if (keyword == null || keyword.trim().length() < 1)
      this.filterKeyword = "";
    else
      this.filterKeyword = keyword.trim();
  }

  public String getELocationId() {
    return eLocationId;
  }

  public void setELocationId(String eLocationId) {
    if (eLocationId == null || eLocationId.trim().length() < 1)
      this.eLocationId = "";
    else
      this.eLocationId = eLocationId.trim();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    if (id == null || id.trim().length() < 1)
      this.id = "";
    else
      this.id = id.trim();
  }

  public String[] getFilterSubjects() {
    return filterSubjects;
  }

  public void setFilterSubjects(String[] subjects) {
    if (subjects == null || subjects.length < 1) {
      this.filterSubjects = new String[]{};
    } else {
      List<String> filterSubjectsList = new ArrayList<String>();
      for (String subject : subjects) {
        if (subject != null && subject.trim().length() > 0)
          filterSubjectsList.add(subject.trim());
      }
      this.filterSubjects = new String[filterSubjectsList.size()];
      filterSubjectsList.toArray(this.filterSubjects);
    }
  }

  public String[] getFilterJournals() {
    return filterJournals;
  }

  public void setFilterJournals(String[] journals) {
    if (journals == null || journals.length < 1) {
      this.filterJournals = new String[]{};
    } else {
      List<String> filterJournalsList = new ArrayList<String>();
      for (String journal : journals) {
        if (journal != null && journal.trim().length() > 0)
          filterJournalsList.add(journal.trim());
      }
      this.filterJournals = new String[filterJournalsList.size()];
      filterJournalsList.toArray(this.filterJournals);
    }
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    if (sort == null || sort.trim().length() < 1)
      this.sort = "";
    else
      this.sort = sort.trim();
  }

  public int getStartPage() {
    return startPage;
  }

  public void setStartPage(int startPage) {
    this.startPage = startPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Creates a deep copy of this SearchParameters object.
   *
   * @return a deep copy of this SearchParameters object.
   */
  public SearchParameters copy() {
    SearchParameters sp = new SearchParameters();
    sp.setQuery(this.getQuery());
    sp.setUnformattedQuery(this.getUnformattedQuery());
    sp.setVolume(this.getVolume());
    sp.setELocationId(this.getELocationId());
    sp.setId(this.getId());
    sp.setFilterArticleType(this.getFilterArticleType());
    sp.setFilterKeyword(this.getFilterKeyword());
    sp.setFilterSubjects(this.getFilterSubjects());
    sp.setFilterJournals(this.getFilterJournals().clone());
    sp.setSort(this.getSort());
    sp.setStartPage(this.getStartPage());
    sp.setPageSize(this.getPageSize());
    return sp;
  }

  @Override
  public String toString() {
    return "SearchParameters{" +
        "query='" + query + "'" +
        ", unformattedQuery='" + unformattedQuery + "'" +
        ", volume='" + volume + "'" +
        ", eLocationId='" + eLocationId + "'" +
        ", id='" + id + "'" +
        ", filterSubjects=" + (filterSubjects == null ? null : Arrays.asList(filterSubjects)) +
        ", filterKeyword='" + filterKeyword + "'" +
        ", filterArticleType='" + filterArticleType + "'" +
        ", filterJournals=" + (filterJournals == null ? null : Arrays.asList(filterJournals)) +
        ", sort='" + sort + "'" +
        ", startPage=" + startPage +
        ", pageSize=" + pageSize +
        '}';
  }
}

