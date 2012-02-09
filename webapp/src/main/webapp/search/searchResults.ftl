<#--
  $HeadURL::                                                                            $
  $Id$

  Copyright (c) 2007-2010 by Public Library of Science
  http://plos.org
  http://ambraproject.org

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<#-- begin : main content wrapper -->
<#import "search_variables.ftl" as search>

<#function max x y>
  <#if  (x > y) >
    <#return x />
    <#else>
      <#return y />
  </#if>
</#function>
<#function min x y>
  <#if x < y>
    <#return x />
  <#else>
    <#return y />
  </#if>
</#function>

<#assign max_authors = 5>
<#assign max_editors = 5>
<#assign max_institutions = 5>
<#assign max_subjects = 10>
<#assign max_articletypes = 10>

<#assign filterJournalsAsString>
  <#list filterJournals as journalKey>
    ${freemarker_config.getDisplayName(journalKey)}<#if journalKey_has_next> OR </#if>
  </#list>
</#assign>

<#assign filterSubjectsAsString>
  <#list filterSubjects as subject>
    "${subject}"<#if subject_has_next> AND </#if>
  </#list>
</#assign>

<#--
  This URL is used for both the return link to the Advanced Search form AND the links to other pages of results.
-->
<#if (searchType.length() == 0)>
  ERROR, searchType must be defined.
</#if>

<#--
  Allow viewing of other "pages" of results.  Form submits an advanced search. "startPage" is usually modified.
-->
<form name="otherSearchResultPages" action="${searchURL}" method="get">
  <@s.hidden name="startPage" />
  <@s.hidden name="pageSize" />
  <@s.hidden name="sort" />
  <#--  Simple Search field  -->
  <@s.hidden name="query" />

  <#--  Unformatted Search field (new Advanced Search)  -->
  <@s.hidden name="unformattedQuery" />

  <#--  Find An Article Search fields  -->
  <@s.hidden name="volume" />
  <@s.hidden name="eLocationId" />
  <@s.hidden name="id" />
  <@s.hidden name="filterJournals" />
  <@s.hidden name="filterSubjects" />
  <@s.hidden name="filterArticleType" />
  <@s.hidden name="filterKeyword" />
</form>


<form name="reviseSearch" action="${advancedSearchURL}" method="get">
  <@s.hidden name="noSearchFlag" value="set" />
  <@s.hidden name="pageSize" />
  <@s.hidden name="sort" />
  <#--  Simple Search field
  -->
  <@s.hidden name="query" />

  <#--  Unformatted Search field for the Query Builder (new Advanced Search)
  -->
  <#if searchType == "findAnArticle">
    <input type="hidden" name="unformattedQuery" value="${queryAsExecuted}"/>
  <#else>
    <@s.hidden name="unformattedQuery" />
  </#if>

  <#--  Find An Article Search fields
  -->
  <@s.hidden name="volume" />
  <@s.hidden name="eLocationId" />
  <@s.hidden name="id" />
  <@s.hidden name="filterJournals" />
  <@s.hidden name="filterSubjects" />
  <@s.hidden name="filterArticleType" />
  <@s.hidden name="filterKeyword" />
</form>

<#macro renderSearchPaginationLinks url totalPages currentPageParam class>
  <#--
    currentPage is zero based
    SOLR (the action class) expects a startPage parameter of 0 to N
    We change this to a nonZero value here to make things a bit more readable

    It supports the following use cases and will output the following:

    Current page is the start or end
    Current Page is 1:
    < 1 2 3  ... 10 >
    Current Page is 10:
    < 1 ...8 9 10 >

    Current page is 5:
    (Current page is greater then 2 pages away from start or end)
    < 1 ...4 5 6 ... 10 >

    Current page is less then 2 pages away from start or end:
    Current Page is 8:
    < 1 ...7 8 9 10 >
    < 1 2 3 4 ... 10 >
  -->
  <#assign currentPage = currentPageParam + 1/>

  <#if (totalPages gt 1 )>
    <div class="${class}">
      <#if (totalPages lt 4) >
        <#if (currentPage gt 1) >
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[currentPage - 2] />">&lt;</a>&nbsp;
        <#else>
          <span class="arrow">&lt;</span>
        </#if>

        <#list 1..totalPages as pageNumber>
          <#if pageNumber == currentPage>
            ${currentPage}
          <#else>
            <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[pageNumber - 1] />">${pageNumber}</a>
          </#if>
        </#list>

        <#if (currentPage lt totalPages)>
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[currentPage] />">&gt;</a>
        <#else>
         <span class="arrow">&gt;</span>
        </#if>
      <#else>
        <#if (currentPage gt 1) >
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[currentPage - 2] />">&lt;</a>
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[0] />">1</a>
        <#else>
           <span class="arrow">&lt;</span><strong>1</strong>
        </#if>
        <#if (currentPage gt 3) >
          ...
        </#if>

        <#--
          Yes the following statements are confusing,
          but it should take care of all the use cases defined at the top
        --->
        <#list min(currentPage - 1,0)..max(3,(currentPage + 1)) as pageNumber>
          <#if ((pageNumber > 1 && pageNumber < totalPages && pageNumber > (currentPage - 2)
            || ((pageNumber == (totalPages - 2)) && (pageNumber > (currentPage - 3)))))>
            <#if (currentPage == pageNumber)>
              <strong>${pageNumber}</strong>
            <#else>
              <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[pageNumber - 1] />">${pageNumber}</a>
            </#if>
          </#if>
        </#list>
        <#if (currentPage lt (totalPages - 2))>
          ...
        </#if>
        <#if (currentPage lt totalPages)>
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[totalPages - 1] />">${totalPages}</a>
          <a href="${url}?<@URLParameters parameters=searchParameters names="startPage" values=[currentPage] />">&gt;</a>
        <#else>
          <strong>${totalPages}</strong>
          <span class="arrow">&gt;</span>
        </#if>
      </#if>
    </div>
  </#if>
</#macro>

<div id="content" class="search">

  <#assign noOfResults = (startPage + 1) * pageSize>
  <#if (noOfResults >= totalNoOfResults)>
    <#assign noOfResults = totalNoOfResults>
    <#assign hasMore = 0>
  <#else>
    <#assign hasMore = 1>
  </#if>

  <#-- Compute number of pages -->
  <#assign totalPages = ((totalNoOfResults + pageSize - 1) / pageSize)?int>
  <#-- Start the form here so the journal checkboxes are included on the form submit -->

  <#if searchType == "simple">
    <form name="searchFormOnSearchResultsPage" id="searchFormOnSearchResultsPage" action="${searchURL}" method="get">
  <#else>
    <form name="searchFormOnSearchResultsPage" id="searchFormOnSearchResultsPage" action="${advancedSearchURL}" method="get">
  </#if>

  <@s.hidden name="startPage" />

  <#--  Find An Article Search fields  -->
  <@s.hidden name="volume" />
  <@s.hidden name="eLocationId" />
  <@s.hidden name="id" />

  <@s.hidden name="filterArticleType" />
  <@s.hidden name="filterKeyword" />

  <#if totalNoOfResults == 0>
    <@s.hidden name="filterJournals" />
  </#if>

  <div id="search-facets">
    <#if ((totalNoOfResults gt 0) && (fieldErrors?size == 0))>
      <#if (resultsSinglePage.journalFacet??)>
        <dl id="journalFacet" class="facet">
          <dt>View results in Journal(s):</dt>
          <#list resultsSinglePage.journalFacet as f>
          <dd><label><input type="checkbox" name="filterJournals" value="${f.name}"<#if (filterJournals?seq_contains(f.name)) > checked="true"</#if>>
          ${freemarker_config.getDisplayName(f.name)} (${f.count})</label></dd>
        </#list>
        </dl>
      </#if>

      <#if (resultsSinglePage.subjectFacet??)>
        <dl id="subjectFacet" class="facet">
          <dt>Subject Categories</dt>
          <#list resultsSinglePage.subjectFacet as f>
            <#if f_index lt max_subjects>
              <dd><label><input type="checkbox" name="filterSubjects" value="${f.name}"<#if (filterSubjects?seq_contains(f.name)) > checked="true"</#if>>
              ${f.name} (${f.count})</label></dd>
            </#if>
          </#list>
          <#if resultsSinglePage.subjectFacet?size gt max_subjects>
            <dd><label><a href="${facetMoreURL}?<@URLParameters parameters=searchParameters />&searchType=${searchType}&facetName=subjects"">See more...</a></label></dd>
          </#if>
        </dl>
      </#if>

      <#if (resultsSinglePage.keywordFacet??)>
        <dl id="keywordFacet" class="facet">
          <dt>Where my keywords appear</dt>
        <#list resultsSinglePage.keywordFacet as f>
          <dd><a href="${searchURL}?<@URLParameters parameters=searchParameters names="filterKeyword,startPage" values=[f.name, 0] />&from=keywordFilterLink">${f.name} (${f.count})</a></dd>
        </#list>
        </dl>
      </#if>

      <#-- If article type is specified, don't display this list -->
      <#if filterArticleType == "">
        <#if (resultsSinglePage.articleTypeFacet??)>
          <dl id="articleTypeFacet" class="facet">
            <dt>Article Types</dt>
            <#list resultsSinglePage.articleTypeFacet as f>
              <#if f_index lt max_articletypes>
                <dd><a href="${searchURL}?<@URLParameters parameters=searchParameters names="filterArticleType,startPage" values=[f.name, 0] />&from=articleTypeFilterLink">${f.name} (${f.count})</a></dd>
              </#if>
            </#list>
            <#if resultsSinglePage.articleTypeFacet?size gt max_articletypes>
              <dd><label><a href="${facetMoreURL}?<@URLParameters parameters=searchParameters />&searchType=${searchType}&facetName=articleTypes"">See more...</a></label></dd>
            </#if>
          </dl>
        </#if>
      </#if>
    </#if>
  </div>

  <div id="search-results">
    <#if (fieldErrors?? && numFieldErrors > 0)>
      <div class="error">
        <#list fieldErrors?keys as key>
          <#list fieldErrors[key] as errorMessage>
            ${errorMessage}
          </#list>
        </#list>
      </div>
    <#else>

  <#--  <p>Debug: noOfResults: ${noOfResults}, totalNoOfResults: ${totalNoOfResults}, startPage: ${startPage}, pageSize: ${pageSize}, hasMore: ${hasMore}</p>
  -->
      <div id="searchMore">
        <@s.url id="searchHelpURL" includeParams="none" namespace="/static" action="searchHelp" />
        <#if searchType == "simple">
          <div class="simple">
            <label for="searchEdit">You searched for:</label>
            <input type="text" value="${query?html}" id="searchEdit" name="query"/>
            <input type="submit"  value="Search again" class="button"/>
            <div>
              <a href="#" onclick="document.reviseSearch.submit();return false;">Advanced Search</a> | <a href="${searchHelpURL}">Help</a>
            </div>
          </div>
        <#else>
          <#if totalNoOfResults == 0>
            <h3><b>There are no results</b></h3>
          </#if>
          <div class="advanced">
            <label for="searchEdit">You searched for:</label>
            <textarea id="searchEdit" name="unformattedQuery">${queryAsExecuted?html}</textarea>
            <div>
              <input type="submit"  value="Search again" class="button"/> or
              <a href="#" onclick="document.reviseSearch.unformattedQuery.value=document.searchFormOnSearchResultsPage.unformattedQuery.value;document.reviseSearch.submit();return false;">Edit on Advanced Search page</a> | <a href="${searchHelpURL}">Help</a>
            </div>
          </div>
        </#if>
      </div>

      <div id="result-count">
        <#if totalNoOfResults == 0>
          <#if ((filterSubjects?size > 0) || (filterJournals?size > 0) || (filterArticleType?length > 1))>
          You searched for articles that have all of the following:<br/>
          <br/>
          </#if>
          <#if (filterSubjects?size > 0)>
            Subject categories:
            <b><#list filterSubjects as subject>"${subject}" <#if (subject_index) gt filterSubjects?size - 3><#if subject_has_next> and </#if><#else><#if subject_has_next>, </#if></#if></#list></b>
            <br/>
          </#if>
          <#if (filterJournals?size > 0)>
            Journals:
            <b><#list filterJournals as journal>"${freemarker_config.getDisplayName(journal)}"<#if (journal_index) gt filterJournals?size - 3><#if journal_has_next> and </#if><#else><#if journal_has_next>, </#if></#if></#list></b>
            <br/>
          </#if>
          <#if (filterArticleType?length > 1)>
            Article Type: ${filterArticleType}
            <br/>
          </#if>
          <br/>
          Please <a href="#" onclick="document.reviseSearch.submit();return false;" value="">refine your search</a> and try again. <br/>
          <br/>
          <br/>
        <#else>
          <#assign startIndex = startPage * pageSize>
          <strong>${totalNoOfResults} Results</strong>
        </#if>
      </div>

      <#if (totalNoOfResults gt 0)>
        <div class="filter-block">
          <div class="wrap">
          <#if ((filterJournals?size = 0) && (filterArticleType = "") && (filterSubjects?size = 0) && (filterKeyword = ""))>
            <span class="title">No filters currently applied</span>
          <#else>
            <span class="title">Filters currently applied:</span>
            <span class="clear-filter"><a id="clearAllFilters" href="${searchURL}?<@URLParameters parameters=searchParameters names="filterKeyword,filterArticleType,filterJournals,filterSubjects,startPage" values="" />">Clear all filters</a></span>
            <span class="clearer"></span>
          </#if>

          <#if (filterJournals?size > 0)>
            <div class="filter-block-line"><img src="${freemarker_config.context}/images/icon_delete.gif"
              id="clearJournalFilter" title="Clear this filter"/></a>&nbsp;Journals: ${filterJournalsAsString}</div>
          <#else>
            <div class="filter-block-line">Viewing results from all journals</div>
          </#if>

          <#if (filterArticleType != "")>
            <div class="filter-block-line"><a href="${searchURL}?<@URLParameters parameters=searchParameters names="filterArticleType,startPage" values="" />&from=articleTypeClearFilterLink"><img src="${freemarker_config.context}/images/icon_delete.gif" id="clearArticleTypeFilter" title="Clear this filter"/></a>
              Article Type: ${filterArticleType}</div>
          </#if>

          <#if (filterSubjects?size > 0)>
            <div class="filter-block-line"><img src="${freemarker_config.context}/images/icon_delete.gif"
               id="clearSubjectFilter" title="Clear this filter"/>&nbsp;Subject Category: ${filterSubjectsAsString}</div>
          </#if>
          <#if (filterKeyword != "")>
            <div class="filter-block-line">
              <a href="${searchURL}?<@URLParameters parameters=searchParameters names="filterKeyword,startPage" values="" />&from=keywordFilterClearLink"><img src="${freemarker_config.context}/images/icon_delete.gif" id="clearKeywordFilter" title="Clear this filter"/></a>&nbsp;Searching in: ${filterKeyword}</div>
          </#if>
          </div>
        </div>

        <#if (totalPages lt 2 )>
          <div class="clearfix btmline">
        <#else>
          <div class="clearfix">
        </#if>
          <div class="resultSort">Sort results by:
            <select name="sort" id="sortPicklist">
              <#list sorts as sortItem>
                <#if ((!sort?? || (sort?? && sort == "")) && (sortItem_index == 0))>
                  <option selected value="${sortItem}">${sortItem}</option>
                <#else>
                  <#if (sort?? && (sort == sortItem))>
                    <option selected value="${sortItem}">${sortItem}</option>
                  <#else>
                    <option value="${sortItem}">${sortItem}</option>
                  </#if>
                </#if>
              </#list>
            </select>
          </div>
          <div class="pageSize">
            Show
            <select name="pageSize" id="pageSizePickList">
              <#if ( ! pageSize?? ) || (pageSize?length lt 1)>
                <#assign pageSize = 10>
              </#if>
              <#list pageSizes as size>
                <#if (size == pageSize?string)>
                  <option value="${size}" selected="selected">${size}</option>
                <#else>
                  <option value="${size}">${size}</option>
                </#if>
              </#list>
            </select>
            p/pg
          </div>
        </div>
      </form>
      <@renderSearchPaginationLinks searchURL totalPages startPage "pageNums pgTop" />

      <#-- The following id is used by dojo to find this node -->
      <ul id="searchResults">
        <#list searchResults as hit>
          <li doi="${hit.uri}" pdate="${hit.date.getTime()?string.computer}">
            <span class="article">
             <@s.url id="fetchArticleURL" action="fetchArticle" namespace="/article" articleURI="info:doi/${hit.uri}" includeParams="none"/>
             <@s.a href="${(freemarker_config.getJournalUrlFromIssn(hit.issn))!(freemarker_config.doiResolverURL)}%{fetchArticleURL}" title="Read Open-Access Article"><@articleFormat>${hit.title}</@articleFormat></@s.a>
            </span>
            <span class="authors"> <#-- hitScore: ${hit.hitScore} --> ${hit.creator!""}</span>
            <#if hit.highlight??><span class="cite">${hit.highlight}</span></#if>
            <#if filterJournals?size == 1 && filterJournals?first == freemarker_config.getIssn(journalContext)>
              <#if hit.journalTitle?? && hit.getIssn() != freemarker_config.getIssn(journalContext)>
                <strong><em>${hit.journalTitle}</em></strong><em>:</em>
              </#if>
            <#else>
              <#if hit.journalTitle??>
                <strong><em>${hit.journalTitle}</em></strong><em>:</em>
              </#if>
            </#if>
            <#if hit.articleTypeForDisplay??>
              ${hit.articleTypeForDisplay},
            </#if>
            <#if hit.date??>
              published ${hit.date?string("dd MMM yyyy")}
            </#if>
            <#if hit.uri??>
             <span class="uri">${hit.uri?replace("info:doi/", "doi:")}</span>
            </#if>
          </li>
        </#list>
      </ul>

      <@renderSearchPaginationLinks searchURL totalPages startPage "pageNums pgBtm"/>

      <#else> <#-- Always close the searchFormOnSearchResultsPage form -->
        </form>
      </#if> <#-- Close of search results count if -->
    </#if> <#-- Close of else for search errors -->
  </div> <#-- search-results -->


  <div id="search-related">

    <#assign recentSearchDisplayTextMaxLength = 28>
    <#if recentSearches?? && recentSearches?size gt 0>
      <h3>Recent Searches</h3>
      <dl id="recentSearches" class="facet">
        <#list recentSearches?keys?reverse as key>
          <#if key?length gt recentSearchDisplayTextMaxLength>
            <dd><a href="${recentSearches[key]}" title="${key}">${key?substring(0,recentSearchDisplayTextMaxLength-2)}...</a></dd>
          <#else>
            <dd><a href="${recentSearches[key]}" title="${key}">${key}</a></dd>
          </#if>
        </#list>
      </dl>
    </#if>

    <#if ((totalNoOfResults gt 0) && (fieldErrors?size == 0))>
      <h3>More by...</h3>
      <#if (resultsSinglePage.authorFacet??)>
        <dl id="authorFacet" class="facet">
          <dt>Authors</dt>
          <#list resultsSinglePage.authorFacet as f>
            <#if f_index < max_authors>
              <dd><a href="${advancedSearchURL}?unformattedQuery=author%3A%22${f.name?url}%22&from=authorLink&sort=${sorts[0]?url}">
              ${f.name}</a></dd>
            </#if>
          </#list>
        </dl>
      </#if>

      <#if (resultsSinglePage.editorFacet??)>
        <dl id="editorFacet" class="facet">
          <dt>Editors</dt>
          <#list resultsSinglePage.editorFacet as f>
            <#if f_index < max_editors>
              <dd><a href="${advancedSearchURL}?unformattedQuery=editor%3A%22${f.name?url}%22&from=editorLink&sort=${sorts[0]?url}">
              ${f.name}</a></dd>
            </#if>
          </#list>
        </dl>
      </#if>

      <#if (resultsSinglePage.institutionFacet??)>
        <dl id="institutionsFacet" class="facet">
          <dt>Institutions:</dt>
          <#list resultsSinglePage.institutionFacet as f>
            <#if f_index < max_institutions>
              <dd><a href="${advancedSearchURL}?unformattedQuery=affiliate%3A%22${f.name?url}%22&from=institutionLink&sort=${sorts[0]?url}">
              ${f.name}</a></dd>
            </#if>
          </#list>
        </dl>
      </#if>
    </#if>

  </div>
</div> <#-- content -->
<#-- end : main content wrapper -->

<div style="display:none">
<#include "/widget/loadingCycle.ftl">
</div>
