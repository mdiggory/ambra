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
package org.ambraproject.search.action;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.ambraproject.ApplicationException;
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.search.SearchParameters;
import org.ambraproject.search.SearchResultSinglePage;
import org.ambraproject.search.SearchHit;
import org.ambraproject.search.service.SearchService;
import org.ambraproject.struts2.AmbraFreemarkerConfig;
import org.ambraproject.web.VirtualJournalContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the user interactions for all searches.
 */
@SuppressWarnings("serial")
public class SearchAction extends BaseSessionAwareActionSupport {

  private static final Logger log = LoggerFactory.getLogger(SearchAction.class);
  private static final String SEARCH_PAGE_SIZE = "ambra.services.search.pageSize";
  private static final String CONFIG_DOI_RESOLVER_URL = "ambra.services.crossref.plos.doiurl";
  private static final int RECENT_SEARCHES_NUMBER_TO_SHOW = 5;
  private static final int MAX_FILTERS_SHOWN = 50;

  // Flag telling this action whether or not the search should be executed.
  private String noSearchFlag;

  // All of the parameters used to execute any search.
  private SearchParameters searchParameters;
  private SearchResultSinglePage resultsSinglePage;

  private SearchService searchService;
  private AmbraFreemarkerConfig ambraFreemarkerConfig;
  
  // Used for display of search results
  private Collection<SearchHit> searchResults;
  private int totalNoOfResults;
  private String queryAsExecuted;
  protected String searchType;

  // Creates list of all searchable Journals for display
  private List<Map> journals;
  private List<Map> subjects;
  private List<Map> articleTypes;
  private boolean filterReset = false;

  // Used when this Action class redirects the user the Article main page.
  private String articleURI;
  private String journalURL;

  /**
   * @return return simple search result
   */
  public String executeSimpleSearch() {
    searchType = "simple";

    setDefaultsCommon();

    final String queryString = getSearchParameters().getQuery();

    if (StringUtils.isBlank(queryString) || queryString.equals("Search articles...")) {
      addFieldError("query", "Please enter a search query.");
      setQuery("");
      
      return INPUT;
    } else {
      try {
        resultsSinglePage = searchService.simpleSearch(getSearchParameters());

        //  TODO: take out these intermediary objects and pass "SearchResultSinglePage" to the FTL
        totalNoOfResults = resultsSinglePage.getTotalNoOfResults();
        searchResults = resultsSinglePage.getHits();
        queryAsExecuted = resultsSinglePage.getQueryAsExecuted();

        //If page size is zero, assume totalPages is zero
        int totPages = (getPageSize() == 0)?0:((totalNoOfResults + getPageSize() - 1) / getPageSize());
        setStartPage(Math.max(0, Math.min(getStartPage(), totPages - 1)));

        // Recent Searches must have both a Request URI and a Request Query String, else the URL is useless.
        if (doSearch() && getRequestURL() != null && getRequestQueryString() != null) {
          addRecentSearch(queryString, getRequestURL() + "?" + getRequestQueryString());
        }
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Search failed for the query string: " + queryString, e);
        return ERROR;
      }

      return SUCCESS;
    }
  }

  /**
   * @return return a search result based on the <code>unformattedSearch</code> parameter,
   * moderated by filters based on the and the journal and category properties.
   */
  public String executeUnformattedSearch() {
    searchType = "unformatted";

    setDefaultsCommon();

    if ( ! doSearch()) {
      try {
        setFiltersData();
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Querying for search meta data has failed: ", e);
        return ERROR;
      }
      return INPUT;
    }

    if (StringUtils.isBlank(getSearchParameters().getUnformattedQuery())) {
      addFieldError("unfilteredQuery", "Please enter a search query.");

      try {
        setFiltersData();
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Querying for search meta data has failed: ", e);
        return ERROR;
      }

      return INPUT;
    } else {
      try {
        resultsSinglePage = searchService.advancedSearch(getSearchParameters());

        //  TODO: take out these intermediary objects and pass "SearchResultSinglePage" to the FTL
        totalNoOfResults = resultsSinglePage.getTotalNoOfResults();
        searchResults = resultsSinglePage.getHits();
        queryAsExecuted = resultsSinglePage.getQueryAsExecuted();

        int totPages = (totalNoOfResults + getPageSize() - 1) / getPageSize();
        setStartPage(Math.max(0, Math.min(getStartPage(), totPages - 1)));

        // Recent Searches must have both a Request URI and a Request Query String, else the URL is useless.
        if (doSearch() && getRequestURL() != null && getRequestQueryString() != null) {
          addRecentSearch(queryAsExecuted, getRequestURL() + "?" + getRequestQueryString());
        }
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Search failed for the query string: "
            + getSearchParameters().getUnformattedQuery(), e);
        return ERROR;
      }
      return SUCCESS;
    }
  }

  /**
   * @return return a search result based on the <code>unformattedSearch</code> parameter,
   * moderated by filters based on the and the journal and category properties.
   */
  public String executeFindAnArticleSearch() {
    searchType = "findAnArticle";

    setDefaultsCommon();

    if ( ! doSearch()) {

      try {
        //filterJournals are NOT set in form in global_header.ftl
        setFiltersData();
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Querying for search meta data has failed: ", e);
        return ERROR;
      }

      return INPUT;
    }

    if (StringUtils.isBlank(getSearchParameters().getVolume())
        && StringUtils.isBlank(getSearchParameters().getELocationId())
        && StringUtils.isBlank(getSearchParameters().getId())) {
      addFieldError("volume", "Please enter at least one search term.");

      try {
        //filterJournals are NOT set in form in global_header.ftl
        setFiltersData();
      } catch (ApplicationException e) {
        addActionError("Search failed");
        log.error("Querying for search meta data has failed: ", e);
        return ERROR;
      }      

      return INPUT;
    }

    try {
      resultsSinglePage = searchService.findAnArticleSearch(getSearchParameters());

      // If only ONE result, then send the user to fetchArticle.action for that article
      if (resultsSinglePage.getTotalNoOfResults() == 1) {

        articleURI = "info:doi/" + resultsSinglePage.getHits().get(0).getUri();
        journalURL = ambraFreemarkerConfig.getJournalUrlFromIssn(resultsSinglePage.getHits().get(0).getIssn());
        if (journalURL == null || journalURL.trim().length() < 1) {
          journalURL = configuration.getString(CONFIG_DOI_RESOLVER_URL, "");
        }
        // add request context to the url
        VirtualJournalContext vjc = this.getVirtualJournalContext();
        String rc = vjc.getRequestContext();
        if (rc != null && rc.trim().length() > 0) {
          journalURL = journalURL + rc;
        }

        return "redirectToArticle"; // Tells struts.xml to send the user to fetchArticle.action
      }

      //  TODO: take out these intermediary objects and pass "SearchResultSinglePage" to the FTL
      totalNoOfResults = resultsSinglePage.getTotalNoOfResults();
      searchResults = resultsSinglePage.getHits();
      queryAsExecuted = resultsSinglePage.getQueryAsExecuted();

      int totPages = (totalNoOfResults + getPageSize() - 1) / getPageSize();
      setStartPage(Math.max(0, Math.min(getStartPage(), totPages - 1)));
      
      // Recent Searches must have both a Request URI and a Request Query String, else the URL is useless.
      if (doSearch() && getRequestURL() != null && getRequestQueryString() != null) {
        addRecentSearch(queryAsExecuted, getRequestURL() + "?" + getRequestQueryString());
      }
    } catch (ApplicationException e) {
      addActionError("Search failed");
      log.error("Search failed for the findAnArticle query using the SearchParameters object: "
          + getSearchParameters().toString(), e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Set values used for processing and/or display by all searches.
   */
  protected void setDefaultsCommon() {
    // Set default for "pageSize".
    if (getPageSize() == 0) {
      setPageSize(configuration.getInt(SEARCH_PAGE_SIZE, 10));
    }
  }

  /**
   * If no search is performed, we need some data to populate the drop down and select lists
   * If no results are found, reset the filters and try query again
   * @throws ApplicationException
   */
  protected void setFiltersData() throws ApplicationException {
    //Eventually we'll want to migrate this action to use a database to get this data
    //But for now, SOLR seems the best place
    SearchResultSinglePage sr = searchService.getFilterData(getSearchParameters());
    journals = sr.getJournalFacet();
    subjects = sr.getSubjectFacet();

    if(subjects != null && subjects.size() > MAX_FILTERS_SHOWN) {
      subjects = subjects.subList(0,MAX_FILTERS_SHOWN);
    }

    articleTypes = sr.getArticleTypeFacet();

    if(articleTypes != null && articleTypes.size() > MAX_FILTERS_SHOWN) {
      articleTypes = articleTypes.subList(0,MAX_FILTERS_SHOWN);
    }

    filterReset = sr.getFiltersReset();
  }

  //  Getters and Setters that belong to this Action

  /**
   * @return the noSearchFlag
   */
  public String getNoSearchFlag() {
    return noSearchFlag;
  }

  /**
   * Have the filters been removed for existing query? 
   * @return true if the filter has been reset
   */
  public boolean getFilterReset() {
    return filterReset;
  }

  /**
   * Which source of the query performed.  Controls which search form and display methodology to use.
   * @return The type of search that was performed
   */
  public String getSearchType() {
    return searchType;
  }

  /**
   * The query String that was used to get the search results.
   * @return The actual query that was perfomed
   */
  public String getQueryAsExecuted() {
    return queryAsExecuted;
  }

  public SearchResultSinglePage getResultsSinglePage() {
    return resultsSinglePage;
  }

  /**
   * @param noSearchFlag the noSearchFlag to set
   */
  public void setNoSearchFlag(String noSearchFlag) {
    this.noSearchFlag = noSearchFlag;
  }

  private boolean doSearch() {
    return noSearchFlag == null;
  }

  /**
   * @return the search results.
   */
  public Collection<SearchHit> getSearchResults() {
    return searchResults;
  }

  public List getSorts()
  {
    return searchService.getSorts();
  }

  public List getPageSizes()
  {
    return searchService.getPageSizes();
  }

  /**
   * The total number of search results
   *
   * @return Value for property 'totalNoOfResults'.
   */
  public int getTotalNoOfResults() {
    return totalNoOfResults;
  }

  /**
   * A list of journals that the current search results appear in
   * @return a journals List and frequency count
   */
  public List<Map> getJournals() {
    return journals;
  }

  /**
   * A list of all the subjects that appear in the current search results
   * @return a subject list and frequency count
   */
  public List<Map> getSubjects()
  {
    return subjects;
  }

  /**
   * A list of all the article types that appear in the current search results
   * @return an article type list and frequency count
   */
  public List<Map> getArticleTypes()
  {
    return articleTypes;
  }

  /**
   * Set the searchService
   *
   * @param searchService searchService
   */
  @Required
  public void setSearchService(final SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Set the config class containing all of the properties used by the Freemarker templates so
   * those values can be used within this Action class.
   * @param ambraFreemarkerConfig All of the configuration properties used by the Freemarker templates
   */
  @Required
  public void setAmbraFreemarkerConfig(final AmbraFreemarkerConfig ambraFreemarkerConfig) {
    this.ambraFreemarkerConfig = ambraFreemarkerConfig;
  }

  /**
   * Return the object used to store all of the parameters used to create a query.
   * If no searchParameter exists, then a new one is created.
   *
   * @return The object used to store all of the parameters used to create a query
   */
  public SearchParameters getSearchParameters() {
    if (searchParameters == null) {
      searchParameters = new SearchParameters();
    }
    return searchParameters;
  }

  //  Getters and Setters that belong to SearchParameters class

  /**
   * Set the simple query
   *
   * @param query query
   */
  public void setQuery(final String query) {
    getSearchParameters().setQuery(query);
  }

  /**
   * Getter for property 'query'.
   *
   * @return Value for property 'query'.
   */
  public String getQuery() {
    return getSearchParameters().getQuery();
  }

  /**
   * Set the <code>unformattedQuery</code> parameter.
   * Not associated to Simple Search.
   *
   * @param unformattedQuery
   *   The <code>unformattedQuery</code> which will be run against the search engine
   */
  public void setUnformattedQuery(final String unformattedQuery) {
    getSearchParameters().setUnformattedQuery(unformattedQuery);
  }

  /**
   * Get the <code>unformattedQuery</code> parameter.
   * Not associated to Simple Search.
   *
   * @return Value for property <code>unformattedQuery</code>,
   *   the unformattedQuery which will be run against the search engine
   */
  public String getUnformattedQuery() {
    return getSearchParameters().getUnformattedQuery();
  }

  /**
   * Set the startPage
   *
   * @param startPage startPage
   */
  public void setStartPage(final int startPage) {
    getSearchParameters().setStartPage(startPage);
  }

  /**
   * Getter for property 'startPage'.
   *
   * @return Value for property 'startPage'.
   */
  public int getStartPage() {
    return getSearchParameters().getStartPage();
  }

  /**
   * Set the pageSize
   *
   * @param pageSize pageSize
   */
  public void setPageSize(final int pageSize) {
    getSearchParameters().setPageSize(pageSize);
  }

  /**
   * Getter for property 'pageSize'.
   *
   * @return Value for property 'pageSize'.
   */
  public int getPageSize() {
    return getSearchParameters().getPageSize();
  }

  /**
   * Setter for 'sort', the clause which orders the query results.
   * @param sort The sort order for the search results
   */
  public void setSort(final String sort) {
    getSearchParameters().setSort(sort);
  }

  /**
   * Getter for 'sort', the clause which orders the query results.
   * @return The sort order for the search results
   */
  public String getSort() {
    return getSearchParameters().getSort();
  }

  public void setVolume(final String volume) {
    getSearchParameters().setVolume(volume);
  }

  public String getVolume() {
    return getSearchParameters().getVolume();
  }

  public void setELocationId(final String eLocationId) {
    getSearchParameters().setELocationId(eLocationId);
  }
  
  public String getELocationId() {
    return getSearchParameters().getELocationId();
  }

  public void setId(final String id) {
    getSearchParameters().setId(id);
  }
  public String getId() {
    return getSearchParameters().getId();
  }

  public void setFilterJournals(String[] filterJournals) {
    getSearchParameters().setFilterJournals(rectify(filterJournals));
  }

  public String[] getFilterJournals() {
    return getSearchParameters().getFilterJournals();
  }

  public void setFilterSubjects(String[] category) {
    getSearchParameters().setFilterSubjects(rectify(category));
  }

  public String[] getFilterSubjects() {
    return getSearchParameters().getFilterSubjects();
  }

  public void setFilterArticleType(String type) {
    getSearchParameters().setFilterArticleType(type);
  }

  public String getFilterArticleType() {
    return getSearchParameters().getFilterArticleType();
  }

  public void setFilterKeyword(String keyword) {
    getSearchParameters().setFilterKeyword(keyword);
  }

  public String getFilterKeyword() {
    return getSearchParameters().getFilterKeyword();
  }

  /**
   * The URI of one article.
   * Used when this Action class redirects the user the Article main page.
   * @return the URI of one article
   */
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * The URL for the Journal in which <code>articleURI</code> is published.
   * This is necessary for properly constructing a URL for <code>articleURI</code> because Ambra
   * does not automatically redirect the user to articles in other Journals.
   * @return The URL for the Journal in which <code>articleURI</code> is published
   */
  public String getJournalURL() {
    return journalURL;
  }

  /**
   * Converts a String array whose first element may be a comma delimited String
   * into a new String array whose elements are the split comma delimited elements.
   *
   * @param arr String array that may contain one or more elements having a comma delimited String.
   * @return Rectified String[] array or
   *         <code>null</code> when the given String array is <code>null</code>.
   */
  private String[] rectify(String[] arr) {
    if (arr != null && arr.length == 1 && arr[0].length() > 0) {
      arr = arr[0].split(",");
      for (int i = 0; i < arr.length; i++) {
        arr[i] = arr[i] == null ? null : arr[i].trim();
      }
    }
    return arr;
  }

  /**
   * From the HTTP Session, get the searches performed by this user.
   * Each element in the returned Map has a key which is the link text (to be displayed to the user)
   * and a value which is the URL of that link.
   * @return The searches performed by this user
   */
  public LinkedHashMap<String, String> getRecentSearches() {
    return super.getRecentSearches();
  }

  /**
   * Add a new Recent Search to the Map of Recent Searches stored in HTTP Session Scope.
   * If there are at least RECENT_SEARCHES_NUMBER_TO_SHOW elements in the Map,
   * then remove the oldest element before adding a new element.
   * If the element with the key <code>displayText</code> already exists, then remove that element
   * before adding the new recent search.
   *
   * @param displayText The link text which will be displayed to the user
   * @param url The URL which will be executed when the user clicks on the displayText
   */
  protected void addRecentSearch(String displayText, String url) {
    // If either parameter is bogus, then fail silently.
    if (displayText == null || displayText.trim().length() < 1 || url == null || url.trim().length() < 1) {
      log.warn("Unable to add to Recent Searches (in Session Scope) the key/value pair: displayText = \'"
        + displayText + "\' and url = \'" + url + "\'");
      return;
    }
    if (getRecentSearches().containsKey(displayText)) {
      getRecentSearches().remove(displayText);
    } else if (getRecentSearches().size() >= RECENT_SEARCHES_NUMBER_TO_SHOW) {
      getRecentSearches().remove(getRecentSearches().keySet().iterator().next()); // Remove the first element.
    }
    getRecentSearches().put(displayText, url);
  }

  /**
   * Get the URL from the submitted HttpServletRequest.
   * @return The URL submitted, up to the question mark
   */
  private String getRequestURL() {
    return getHttpServletRequest().getRequestURL().toString();
  }

  /**
   * Get the Query String from the submitted HttpServletRequest.
   * @return The Query String submitted, meaning the part of the URL after the question mark
   */
  private String getRequestQueryString() {
    return getHttpServletRequest().getQueryString();
  }

  /**
   * Convenience method allowing direct access to the HttpServletRequest which was just submitted
   *
   * TODO: replace this method of accessing the HTTP Request Attributes with something less ugly.  Action classes should only have to deal with POJOs.
   *
   * @return The HttpServletRequest which was just submitted
   */
  private HttpServletRequest getHttpServletRequest() {
    return ((ServletRequestAttributes)(requestAttributes.get(
              "org.springframework.web.context.request.RequestContextListener.REQUEST_ATTRIBUTES")))
        .getRequest();
  }
}
