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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Value object that denotes a single page of search result.<p>
 *
 * Presumably this is part of the data-model passed to freemarker.
 *
 */
public class SearchResultSinglePage implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(SearchResultSinglePage.class);

  private static final long serialVersionUID = -6357441619326748590L;

  private final int totalNoOfResults;
  private final int pageSize;
  private final List<SearchHit> hits;
  private final String queryAsExecuted;

  private List<Map> journalFacet;
  private boolean filtersReset = false;
  private List<Map> subjectFacet;
  private List<Map> articleTypeFacet;
  private List<Map> keywordFacet;
  private List<Map> authorFacet;
  private List<Map> editorFacet;
  private List<Map> institutionFacet;

  public SearchResultSinglePage(final int totalResults, final int pageSize,
                          final List<SearchHit> hits, String queryAsExecuted) {
    this.totalNoOfResults = totalResults;
    this.pageSize = pageSize;
    this.hits = hits;
    this.queryAsExecuted = queryAsExecuted;

    if (log.isDebugEnabled())
      log.debug("Got " + hits.size() + " on page of " + pageSize + " of total " + totalResults
        + " when executing the query " + queryAsExecuted);
  }

  /**
   * If no results are found, try query again without filters.  In this case if
   * Search results are found, the filters are reset and this will return true
   * @return
   */
  public boolean getFiltersReset() {
    return filtersReset;
  }

  public void setFiltersReset(boolean filtersReset) {
    this.filtersReset = filtersReset;
  }

  public List<Map> getJournalFacet() {
    return journalFacet;
  }

  public void setJournalFacet(List<Map> value) {
    journalFacet = value;
  }

  public List<Map> getSubjectFacet() {
    return subjectFacet;
  }

  public void setSubjectFacet(List<Map> value) {
    subjectFacet = value;
  }

  public List<Map> getArticleTypeFacet() {
    return articleTypeFacet;
  }

  public void setArticleTypeFacet(List<Map> articleTypeFacet) {
    this.articleTypeFacet = articleTypeFacet;
  }

  public List<Map> getAuthorFacet() {
    return authorFacet;
  }

  public void setAuthorFacet(List<Map> authorFacet) {
    this.authorFacet = authorFacet;
  }

  public List<Map> getEditorFacet() {
    return editorFacet;
  }

  public void setEditorFacet(List<Map> editorFacet) {
    this.editorFacet = editorFacet;
  }

  public List<Map> getInstitutionFacet() {
    return institutionFacet;
  }

  public void setInstitutionFacet(List<Map> institutionFacet) {
    this.institutionFacet = institutionFacet;
  }

  public List<Map> getKeywordFacet() {
    return keywordFacet;
  }

  public void setKeywordFacet(List<Map> keywordFacet) {
    this.keywordFacet = keywordFacet;
  }

  /**
   * Getter for property 'hits'.
   * @return Value for property 'hits'.
   */
  public List<SearchHit> getHits() {
    return hits;
  }

  /**
   * Getter for property 'pageSize'.
   * @return Value for property 'pageSize'.
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * Getter for property 'totalNoOfResults'.
   * @return Value for property 'totalNoOfResults'.
   */
  public int getTotalNoOfResults() {
    return totalNoOfResults;
  }

  /**
   * The query (without filters) that was executed to get the contents of this
   * SearchResultSinglePage object.
   * @return The query (without filters) that was executed
   */
  public String getQueryAsExecuted() {
    return queryAsExecuted;
  }
}