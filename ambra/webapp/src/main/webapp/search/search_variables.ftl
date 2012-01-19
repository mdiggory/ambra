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

<@s.url id="facetMoreURL" includeParams="none" namespace="/search" action="listFacet" />

<#if searchType == "simple">
  <@s.url id="searchURL" includeParams="none" namespace="/search" action="simpleSearch" />
<#elseif searchType == "unformatted">
  <@s.url id="searchURL" includeParams="none" namespace="/search" action="advancedSearch" />
<#elseif searchType == "findAnArticle">
  <@s.url id="searchURL" includeParams="none" namespace="/search" action="findAnArticleSearch" />
<#else>
  <#--  TODO: Set this default to something reasonable and set "noSearchFlag = true" and probably give a good error message.
  -->
  <@s.url id="searchURL" includeParams="none" namespace="/search" action="simpleSearch" />
</#if>

<@s.url id="advancedSearchURL" includeParams="none" namespace="/search" action="advancedSearch" />