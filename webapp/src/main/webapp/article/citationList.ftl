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
<!-- begin : main content -->
<@s.url id="jDocURL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="${articleURI}" />
<div id="content" class="static">
  <h1>Download Citation</h1>
 <@s.url id="fetchArticleURL" action="fetchArticle" articleURI="${articleURI}"/>
    <div class="source">
      <span>Article Source:</span>
      <@s.url id="articlePageURL" action="fetchArticle" namespace="/article" articleURI="${articleURI}" includeParams="none"/>
      <@s.a href="%{articlePageURL}" title="Back to original article" cssClass="article icon"><@articleFormat>${title}</@articleFormat></@s.a>
    </div>
  <p class="intro">
    <#assign isCorrection=false/>
    <#include "citation.ftl"/>
  </p>
  <h2>Download the article citation in the following formats:</h2>
  <ul>
    <@s.url id="risURL" namespace="/article" action="getRisCitation" includeParams="none" articleURI="${articleURI}" />
    <li><a href="${risURL}" title="RIS Citation">RIS</a> (compatible with EndNote, Reference Manager, ProCite, RefWorks)</li>
    <@s.url id="bibtexURL" namespace="/article" action="getBibTexCitation" includeParams="none" articleURI="${articleURI}" />
    <li><a href="${bibtexURL}" title="BibTex Citation">BibTex</a> (compatible with BibDesk, LaTeX)</li>
  </ul>
</div>
<!-- end : main contents -->
