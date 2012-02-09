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

<#assign checkedstr = "checked=\"checked\"">
<#assign slctdstr = "selected=\"selected\"">
<#macro slctd tstr str><#if tstr == str>${slctdstr}</#if></#macro>
<#macro chkd tstr str><#if tstr == str>${checkedstr}</#if></#macro>
<#macro chkdlist tstr strlist><#list strlist as str><#if tstr == str>${checkedstr}</#if></#list></#macro>
<!-- begin : unformatted search form -->
<div id="content" class="search">

  <!-- begin : right-hand column -->
  <div id="rhc">

  <!-- begin : Find An Article block -->
    <div class="rhcBox_type2">
      <@s.url id="findAnArticleSearchURL" includeParams="none" namespace="/search" action="findAnArticleSearch" />
      <h6>Find an Article</h6>
      <form id="quickFind" name="findAnArticleSearchForm" onsubmit="return true;" action="${findAnArticleSearchURL}" method="get" enctype="multipart/form-data" class="advSearch" title="Find An Article Search Form">
        <@s.hidden name="pageSize" />
        <p>Use citation information to quickly find a specific article.</p>
        <fieldset>
          <legend>Enter the following information:</legend>
          <div class="selectJour">
            <label>Journal:</label>
            <select name="filterJournals" id="filterJournalsPicklistId" title="Journals Picklist" alt="Journals Pick List">
              <#if journals??>
                <#list journals as journal>
                  <option id="filterJournalsPicklist_${journal.name}" value="${journal.name}"<#if journal.name == journalContext> selected="selected"</#if> title="${freemarker_config.getDisplayName(journal.name)}">${freemarker_config.getDisplayName(journal.name)}</option>
                </#list>
              </#if>
            </select>
          </div>
          <ol class="text">
            <li style="margin:0.5em 0;"><label for="volumeId">Volume:</label><input type="text" name="volume" id="volumeId" value="${volume?html!""}" title="Volume" alt="Volume Text Field"/><span class="example" style="margin-left:1em;">e.g.: 3</span></li>
            <li><label for="eLocationIdId">eNumber:</label><input type="text" name="eLocationId" id="eLocationIdId" value="${eLocationId?html!""}" title="eNumber" alt="eNumber Text Field"/><span class="example" style="margin-left:1em;">e.g.: e2243</span></li>
          </ol>
        </fieldset>
        <fieldset>
          <legend><span>Or</span></legend>
          <ul class="text">
            <li><label for="idId">Article DOI:</label><input type="text" name="id" id="idId" size=17" value="${id?html!""}" title="Article DOI" alt="Article DOI Text Field"/><span class="example" style="display:block;">e.g.: 10.1371/journal.pone.0002243</span></li>
          </ul>
        </fieldset>
        <div class="btnwrap">
          <input type="button" id="buttonGoId" class="primary" value="Go" title="Go" alt="Go Button"/>
        </div>
      </form>
    </div>
  <!-- end : Find An Article block -->

    <div>
    <p>Instructions for "Construct Your Search"</p>
      <ol>
        <li>Choose a field to search from the picklist.</li>
        <li>Enter search term(s).</li>
        <li>Click the AND OR or NOT buttons to add terms to the search box.</li>
        <li>Repeat steps as necessary.</li>
        <li>Select journals and/or subject categories below, if desired.</li>
        <li>Click Search to run your query, or click Preview to see the result count of your query in the Search History section.</li>
      </ol>

      Special Characters
      <ul>
        <li>The following characters have special meanings to the query engine</li>
        <li><strong> : ! & " ' ^ + - | ( ) [ ] { } \ </strong></li>
        <li>Therefore, all of these characters will be "escaped" by preceding each one with a backslash character</li>
        <li>The wildcard characters <strong>?</strong> and <strong>*</strong> are <i>not</i> escaped</li>
      </ul>
      Special Words
      <ul>
        <li>The upper-case words <strong>AND</strong>, <strong>OR</strong>, <strong>NOT</strong>,
          and <strong>TO</strong> have special meanings to the query engine, so these words
          will be changed to lower-case when they are used as searchable terms</li>
      </ul>
    </div>
  </div>

  <!-- begin : primary content area -->
  <div class="content">
    <h1>Advanced Search</h1>
    <#assign currentJournalName = freemarker_config.getDisplayName(journalContext) />
    <#if (fieldErrors?? && numFieldErrors > 0)>
      <div class="error">
        <#list fieldErrors?keys as key>
          <#list fieldErrors[key] as errorMessage>
            ${errorMessage}
          </#list>
        </#list>
      </div>
    </#if>

<@s.url id="unformattedSearchURL" includeParams="none" namespace="/search" action="advancedSearch" />
    <form id="unformattedSearchFormId" name="unformattedSearchForm" onsubmit="return true;" action="${unformattedSearchURL}" method="get" enctype="multipart/form-data" class="advSearch" title="Advanced Search">
      <@s.hidden name="pageSize" />
      <@s.hidden name="sort" />

      <@s.url id="searchHelpURL" includeParams="none" namespace="/static" action="searchHelp" />
      <fieldset id="queryBuilder">
        <legend style="margin-bottom:0.5em;"><span>Construct Your Search <a href="${searchHelpURL}">Help</a></span></legend>

        <div style="margin:0 0 5px 0;">
          <select name="queryField" id="queryFieldId" title="Search Field" alt="Search Field Pick List">
            <option value="" disabled="disabled">----- Popular -----</option>
            <option value="everything" selected="selected" title="All Fields">All Fields</option>
            <option value="title" title="Title">Title</option>
            <option value="author" title="Author">Author</option>
            <option value="body" title="Body">Body</option>
            <option value="abstract" title="Abstract">Abstract</option>
            <option value="subject" title="Subject">Subject</option>
            <option value="publication_date" title="Publication Date">Publication Date</option>
            <option value="" disabled="disabled">----- Other -----</option>
            <option value="accepted_date" title="Accepted Date">Accepted Date</option>
            <option value="id" title="Article DOI">Article DOI (Digital Object Identifier)</option>
            <option value="article_type" title="Article Type">Article Type</option>
            <option value="affiliate" title="Author Affiliations">Author Affiliations</option>
            <option value="competing_interest" title="Competing Interest Statement">Competing Interest Statement</option>
            <option value="conclusions" title="Conclusions">Conclusions</option>
            <option value="editor" title="Editor">Editor</option>
            <option value="elocation_id" title="eNumber">eNumber</option>
            <option value="figure_table_caption" title="Figure &amp; Table Caption">Figure &amp; Table Captions</option>
            <option value="financial_disclosure" title="Financial Disclosure Statement">Financial Disclosure Statement</option>
            <option value="introduction" title="Introduction">Introduction</option>
            <option value="issue" title="Issue Number">Issue Number</option>
            <option value="materials_and_methods" title="Materials and Methods">Materials and Methods</option>
            <option value="received_date" title="Received Date">Received Date</option>
            <option value="reference" title="References">References</option>
            <option value="results_and_discussion" title="Results and Discussion">Results and Discussion</option>
            <option value="supporting_information" title="Supporting Information">Supporting Information</option>
            <option value="volume" title="Volume Number">Volume Number</option>
          </select>

          <span id="queryTermDivBlockId" style="display:inline">
          <#if query?? && query?length gt 0>
            <input type="text" name="queryTerm" id="queryTermId" onblur="if(this.value=='')value='Enter search terms';" onfocus="if(this.value=='Enter search terms')value='';" value="${query}" title="Search Term" alt="Search Term Text Field"/>
          <#else>
            <input type="text" name="queryTerm" id="queryTermId" onblur="if(this.value=='')value='Enter search terms';" onfocus="if(this.value=='Enter search terms')value='';" value="${"Enter search terms"}" title="Search Term" alt="Search Term Text Field"/>
          </#if>
          </span>
          <span id="startAndEndDateDivBlockId" style="display:none">
            <input type="text" name="startDateAsString" maxlength="10" value="${startDateAsString!"YYYY-MM-DD"}" id="startDateAsStringId" disabled="true" title="Start Date" alt="Start Date Text Field"/>
            to
            <input type="text" name="endDateAsString" maxlength="10" value="${endDateAsString!"YYYY-MM-DD"}" id="endDateAsStringId" disabled="true" title="End Date" alt="End Date Text Field"/>
          </span>
        </div>
        
        <div style="margin:5px 75px 5px 0; text-align:center;">Add to your search with:
          <input type="button" name="queryConjunctionAnd" id="queryConjunctionAndId" value="AND" title="AND" alt="And Button"/>
          <input type="button" name="queryConjunctionOr" id="queryConjunctionOrId" value="OR" title="OR" alt="Or Button"/>
          <input type="button" name="queryConjunctionNot" id="queryConjunctionNotId" value="NOT" title="NOT" alt="Not Button"/>
        </div>

        <div style="margin:5px 0;">
          <textarea name="unformattedQuery" id="unformattedQueryId" title="Query" alt="Query Text Area">${unformattedQuery?html}</textarea>
        </div>
        <div class="btnwrap">
          <input type="button" id="buttonSearchId" class="primary" value="Search" title="Search" alt="Search Button"/>
          <input type="button" name="clearUnformattedQueryButton" id="clearUnformattedQueryButtonId" value="Clear Query" title="Clear Query" alt="Clear Query Button"/>
        </div>
      </fieldset>

      <#if filterReset>
        There are no results for your search query.<br/>
        <br/>
        <span id="filterReset">
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
        </span>
        <br/>
        <input type="button" name="clearFiltersButton1" id="clearFiltersButtonId1" value="Clear Filters"/>
      </#if>

      <fieldset id="journals">
        <legend><span>Filter by Journal</span></legend>
        <ol>
          <li><label><input id="journalsOpt_all" type="radio" name="journalOpt" value="all"<#if (filterJournals?size == 0)> checked="true"</#if> title="Search All Journals" alt="Search All Journals Radio Button"/> Search all journals</label></li>
          <li><label><input id="journalsOpt_slct" type="radio" name="journalOpt" value="some"<#if (filterJournals?size gt 0)> checked="true"</#if> title="Search Selected Journals" alt="Search Selected Journals Radio Button"/> Only search in the following journals:</label></li>
          <li class="options">
            <fieldset id="fsJournalOpt">
              <ul>
                <#if journals??>
                  <#list journals as journal>
                    <li><input id="filterJournals_${journal.name}" name="filterJournals" value="${journal.name}" type="checkbox" <@chkdlist tstr=journal.name strlist=(filterJournals![])/> title="Select ${freemarker_config.getDisplayName(journal.name)}" alt="Select Journal ${freemarker_config.getDisplayName(journal.name)} Check Box"/>&nbsp;<label for="filterJournals_${journal.name}">${freemarker_config.getDisplayName(journal.name)}</label></li>
                  </#list>
                <#else>
                  <br/>
                  <span id="filterReset" style="color:red;">ERROR: There are matching journals in the system.</span>
                </#if>
              </ul>
            </fieldset>
          </li>
        </ol>
      </fieldset>

      <fieldset id="subjCats">
        <legend><span>Filter by Subject Category</span></legend>
        <ol>
          <li><label><input id="subjectOption_all" type="radio" checked="checked" name="subjectCatOpt" value="all" <#if (filterSubjects?size == 0) > checked="true"</#if> title="Search All Subject Categories" alt="Search All Subject Categories Radio Button"/> Search all subject categories</label></li>
          <li><label><input id="subjectOption_some" type="radio" name="subjectCatOpt" value="some" <#if (filterSubjects?size gt 0)> checked="true"</#if> title="Search Selected Subject Categories" alt="Search Selected Subject Categories Radio Button"/> Only look for articles with the following subject categories:</label></li>
          <li class="options">
            <fieldset id="fsSubjectOpt">
              <#if (filterSubjects?size gt 0)><p>Listed below are all subject categories from <b title="Articles that already match your entered search terms">matching</b> articles.</p></#if>
              <#if (unformattedQuery?length gt 0)>
                <p>(#) indicates the number of articles with
                  <#if (filterSubjects?size lte 0)><b title="Articles that already match your entered search terms"></#if>matching<#if (filterSubjects?size lte 0)></b></#if> terms in each subject.</p>
              <#else>
                <p>(#) indicates the number of articles in each subject.</p>
              </#if>
              <#if subjects?? && subjects?size gt 0>
                <#assign colSize = (subjects?size / 2) + 0.5>
                <ul>
                  <#list subjects?sort_by("name") as subject>
                    <#if (subject_index + 1) lte colSize>
                      <#assign subjectId = subject.name?replace(" ","_","r")>
                      <li><input id="filterSubjects_${subjectId}" name="filterSubjects" value="${subject.name}" type="checkbox" <#if (filterSubjects?seq_contains(subject.name)) > checked="true"</#if> title="Select Subject Category ${subject.name}" alt="Select Subject Category ${subject.name} Check Box"/>&nbsp;<label for="filterSubjects_${subjectId}">${subject.name} (${subject.count})</label></li>
                    </#if>
                  </#list>
                </ul>
                <ul>
                  <#list subjects?sort_by("name") as subject>
                    <#if (subject_index + 1) gt colSize>
                      <#assign subjectId = subject.name?replace(" ","_","r")>
                      <li><input id="filterSubjects_${subjectId}" name="filterSubjects" value="${subject.name}" type="checkbox" <#if (filterSubjects?seq_contains(subject.name)) > checked="true"</#if> title="Select Subject Category ${subject.name}" alt="Select Subject Category ${subject.name} Check Box"/>&nbsp;<label for="filterSubjects_${subjectId}">${subject.name} (${subject.count})</label></li>
                    </#if>
                  </#list>
                </ul>
              <#else>
               <br/>
               <span id="filterReset" style="color:red;">There are no matching subjects in the current result set.</span>
              </#if>
            </fieldset>
          </li>
        </ol>
      </fieldset>

      <fieldset id="subjCats">
        <legend><span>Filter by Article Type</span></legend>
        <ol>
          <li><label><input id="articleType_all" type="radio" checked="checked" name="filterArticleTypeOpt" value="all" <#if (filterArticleType?length == 0)> checked="true"</#if> title="Search All Article Types" alt="Search All Article Types Radio Button"/> Search all article types</label></li>
          <li><label><input id="articleType_one" type="radio" name="filterArticleTypeOpt" value="some" <#if (filterArticleType?length gt 0)> checked="true"</#if> title="Search For Only Selected Article Type" alt="Search Only For Selected Article Type Radio Button"/> Search for one of the following:</label></li>
          <li class="options">
            <fieldset id="fsarticleTypOpt">
              <#if articleTypes?? && articleTypes?size gt 0>
                <#assign colSize = (articleTypes?size / 2) + 0.5>
                <ul>
                  <#list articleTypes?sort_by("name") as articleType>
                    <#if (articleType_index + 1) lte colSize>
                      <#assign articleTypeId = articleType.name?replace(" ","_","r")>
                      <li><input id="filterArticleType_${articleTypeId}" name="filterArticleType" value="${articleType.name}" type="radio" <#if (filterArticleType == articleType.name) > checked="true"</#if> title="Select Article Type ${articleType.name}" alt="Select Article Type ${articleType.name} Check Box"/>&nbsp;<label for="filterArticleType_${articleTypeId}">${articleType.name}</label></li>
                    </#if>
                  </#list>
                </ul>
                <ul>
                  <#list articleTypes?sort_by("name") as articleType>
                    <#if (articleType_index + 1) gt colSize>
                      <#assign articleTypeId = articleType.name?replace(" ","_","r")>
                      <li><input id="filterArticleType_${articleTypeId}" name="filterArticleType" value="${articleType.name}" type="radio" <#if (filterArticleType == articleType.name) > checked="true"</#if> title="Select Article Type ${articleType.name}" alt="Select Article Type ${articleType.name} Check Box"/>&nbsp;<label for="filterArticleType_${articleTypeId}">${articleType.name}</label></li>
                    </#if>
                  </#list>
                </ul>
              <#else>
                <br/>
                <span id="filterReset" style="color:red;">ERROR: There are no matching article types in the system.</span>
              </#if>
            </fieldset>
          </li>
        </ol>
      </fieldset>

      <div class="btnwrap">
        <input type="button" id="buttonSearchId2" class="primary" value="Search" title="Search" alt="Search Button"/>
        <input type="button" name="clearFiltersButton2" id="clearFiltersButtonId2" value="Clear Filters" title="Clear Filters" alt="Clear Filters"/>
      </div>
      
    </form>

<!--  TODO: table is a terrible way to format!  Please fix the preceding layout AND the instructional text!
-->
  </div><!-- end : primary content area -->
</div><!-- end : unformatted search form -->