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
<#macro pagination>
  <#assign totalPages = (totalArticles/pageSize)?int>
  <#if totalArticles % pageSize != 0>
    <#assign totalPages = totalPages + 1>
  </#if>
  <#if (totalPages gt 1) >
    <div class="resultsTab">
      <#if (startPage gt 0)>
        <@s.url id="prevPageURL" action="browse" namespace="/article" startPage="${startPage - 1}" pageSize="${pageSize}" includeParams="get"/>
        <@s.a href="%{prevPageURL}">&lt; Prev</@s.a> |
      </#if>
      <#list 1..totalPages as pageNumber>
        <#if (startPage == (pageNumber-1))>
          <strong>${pageNumber}</strong>
        <#else>
          <@s.url id="browsePageURL" action="browse" namespace="/article" startPage="${pageNumber - 1}" pageSize="${pageSize}" field="${field}" includeParams="get"/>
          <@s.a href="%{browsePageURL}">${pageNumber}</@s.a>
        </#if>
        <#if pageNumber != totalPages>|</#if>
      </#list>
      <#if (startPage lt totalPages - 1 )>
        <@s.url id="nextPageURL" action="browse" namespace="/article" startPage="${startPage + 1}" pageSize="${pageSize}" field="${field}" includeParams="get"/>
        <@s.a href="%{nextPageURL}">Next &gt;</@s.a>
      </#if>
    </div> <!-- results tab-->
  </#if>
</#macro>

<div id="content" class="browse static">
  <!-- begin : banner -->
  <div id="bannerRight"><!--skyscraper-->
     <a href="#"><img src="../images/adBanner_placeholder_120x600.png" alt=""/></a>
  </div>
  <!-- end : banner -->
  <h1>Browse Articles</h1>
  <#if field == "date">
    <#include "browseNavDate.ftl">
  <#else>
    <#include "browseNavSubject.ftl">
  </#if>

  <#assign startPgIndex = startPage * pageSize>
  <#assign endPgIndex = startPgIndex + pageSize - 1>
  <#if endPgIndex gte totalArticles>
    <#assign endPgIndex = totalArticles - 1 >
  </#if>

  <div id="search-results">
    <#if endPgIndex gte 0><p><strong>${startPgIndex + 1} - ${endPgIndex + 1}</strong> of </#if><strong>${totalArticles}</strong> article<#if totalArticles != 1>s</#if> published ${infoText}.</p>
    <@pagination />
    <ul>
      <#list articleList as art>
        <li>
          <@s.url id="fetchArticleURL" action="fetchArticle" namespace="/article" articleURI="${art.uri}" includeParams="none"/>
          <span class="article"><@s.a href="%{fetchArticleURL}" title="Read Open-Access Article"><@articleFormat>${art.title}</@articleFormat></@s.a></span>
          <span class="authors">
            <#list art.authors as auth><#if auth_index gt 0>, </#if>${auth}</#list>
          </span>
          ${art.articleTypeForDisplay}, published ${art.date?string("dd MMM yyyy")}<br />${art.uri?replace("info:doi/", "doi:")}
        </li>
      </#list>
    </ul>
    <@pagination />
  </div> <!-- search results -->
</div> <!--content-->
