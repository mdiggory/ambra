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
<#macro related articleInfo>
  <#if articleInfo.relatedArticles?size gt 0>
    <dl class="related">
      <dt>Related <em>${freemarker_config.orgName}</em> Articles</dt>
      <#list articleInfo.relatedArticles as ra>
      <#assign docURL = "http://dx.plos.org/" + "${ra.uri?replace('info:doi/','')}">
      <dd><a href="${docURL}" title="Read Open Access Article">${ra.title}</a></dd>
      </#list>
     </dl>
  </#if>
</#macro>

<!-- begin : toc content -->
<div id="content" class="toc">
  <a id="top" name="top" toc="top" title="Top"></a>
  <!-- begin : right-hand column -->
  <div id="rhc">
    <div id="sideNav">
          <p id="issueNav">
        <#if issueInfo.prevIssue?exists>
          <@s.url id="prevIssueURL" action="browseIssue" namespace="/article" issue="${issueInfo.prevIssue}" includeParams="none"/>
          <a href="${prevIssueURL}">&lt;Previous Issue</a>
           | 
        </#if>
        <#if issueInfo.parentVolume?exists>
          <@s.url id="archiveURL" action="browseVolume" namespace="/article" includeParams="gotoVolume=${issueInfo.parentVolume}"/>
          <a href="${archiveURL}">Archive</a>
           | 
        </#if>
        <#if issueInfo.nextIssue?exists>
          <@s.url id="nextIssueURL" action="browseIssue" namespace="/article" issue="${issueInfo.nextIssue}" includeParams="none"/>
          <a href="${nextIssueURL}">Next Issue&gt;</a>
        </#if>
      </p>
      <div id="floatMarker">&nbsp;</div>
      <div id="postcomment">
        <div id="sectionNavTop" class="tools">
          <ul id="tocUl">
            <li><a class="first" href="#top">Top</a></li>
            <#list articleGroups as articleGrp>
              <#if (articleGrp.count > 1)>
                <#assign articleHeader="${articleGrp.pluralHeading!articleGrp.heading!'No Header Defined'}">
              <#else>
                <#assign articleHeader="${articleGrp.heading!'No Header Defined'}">
              </#if>
              <li><a href="#${articleGrp.id}">${articleHeader!"No Header Defined"}</a></li>
            </#list>
          </ul>
        </div><!-- end : sectionNav -->
      </div>
    </div><!-- end : sideNav -->
  </div><!-- end : right-hand column -->
  <!-- begin : primary content area -->
  <div class="content">
  <h1>Table of Contents | ${issueInfo.displayName} ${volumeInfo.displayName}</h1>
    <#if issueInfo.imageArticle?has_content>
      <@s.url id="imageSmURL" action="fetchObject" namespace="/article" uri="${issueInfo.imageArticle}.g001" representation="PNG_S" includeParams="none"/>
      <@s.url id="imageLgURL" action="slideshow" namespace="/article" uri="${issueInfo.imageArticle}" imageURI="${issueInfo.imageArticle}.g001" includeParams="none"/>
      <div id="issueImage">
        <div id="thumbnail">
  <a href="${imageLgURL}" onclick="window.open(this.href,'ambraSlideshow','directories=no,location=no,menubar=no,resizable=yes,status=no,scrollbars=yes,toolbar=no,height=600,width=850');return false;">
      <img alt="Issue Image" src="${imageSmURL}"/>
    </a>
    <a href="${imageLgURL}" onclick="window.open(this.href,'ambraSlideshow','directories=no,location=no,menubar=no,resizable=yes,status=no,scrollbars=yes,toolbar=no,height=600,width=850');return false;">
      View large image
    </a>
        </div>
        <h3>About This Image</h3>
        ${issueDescription}
      </div>
    </#if>
    <div class="clearer">&nbsp;</div>
    <!-- begin : articleTypes -->
    <div id="articleTypeList">
      <#list articleGroups as articleGrp>
        <a id="${articleGrp.id}" class="noshow" title="${articleGrp.heading}">&nbsp;</a>
        <#if (articleGrp.count > 1)>
          <#assign articleHeader="${articleGrp.pluralHeading!articleGrp.heading!'No Header Defined'}">
        <#else>
          <#assign articleHeader="${articleGrp.heading!'No Header Defined'}">
        </#if>
        <h2>${articleHeader!"No Header Defined"}</h2>
        <#list articleGrp.articles as articleInfo>
          <div class="article">
            <@s.url id="fetchArticleURL" action="fetchArticle" namespace="/article" articleURI="${articleInfo.doi}"
            includeParams="none"/>
            <h3><@s.a href="%{fetchArticleURL}" title="Read Open Access Article"><@articleFormat>${articleInfo.title}</@articleFormat></@s.a></h3>
            <p class="authors"><#list articleInfo.authors as auth><#if auth_index gt 0>, </#if>${auth?trim}</#list></p>
            <@related articleInfo=articleInfo/>
            <#if articleInfo.corrections?? && (articleInfo.corrections?size > 0)>
              <div class="fch">
              <p class="fch"><strong> Formal Correction:</strong></p>
              <ol class="fclist">
              <#list articleInfo.corrections as correctionId>
                <#assign correction = correctionMap.getValue(correctionId)>
                <#if correction??>
                  <@s.url namespace="/annotation" action="listThread" id="correctionUrl" inReplyTo="${correction.id}" root="${correction.id}"/>
                  <li>
                    <p>${correction.title} (<@s.a href="%{correctionUrl}">More...</@s.a>)</p>
                  </li>
                </#if>
              </#list>
              </ol>
              </div>
            </#if>
            <#if articleInfo.retractions?? && (articleInfo.retractions?size > 0)>
              <div class="retractionHtmlId">
                <p class="retractionHtmlId"><strong> Retraction:</strong> This article has been retracted.
                <#list articleInfo.retractions as retractionId>
                  <#assign retraction = retractionMap.getValue(retractionId)>
                  <#if retraction??>
                    <@s.url namespace="/annotation" action="listThread" id="retractionUrl" inReplyTo="${retraction.id}" root="${retraction.id}"/> (<@s.a href="%{retractionUrl}">More...</@s.a>)
                  </#if>
                </#list>
                </p>
              </div>
            </#if>        
          </div>
        </#list>
      </#list>
    </div>
    <!-- end : articleTypes -->
  </div>
</div> <!-- end : toc content-->


