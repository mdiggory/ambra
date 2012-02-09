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

<#import "article_variables.ftl" as article>
<#import "/global/global_variables.ftl" as global>
<@s.url id="createDiscussionURL" namespace="/annotation/secure" action="startDiscussion" includeParams="none" target="${articleURI}" />
<div id="researchArticle" class="content">
  <span rel="dc:publisher" href="${Request[freemarker_config.journalContextAttributeKey].baseUrl}"></span>
  <a id="top" name="top"></a>
  <@s.url id="thisPageURL" includeParams="get" includeContext="true" encode="false"/>
  <@s.url id="feedbackURL" includeParams="none" namespace="/" action="feedbackCreate" page="${global.thisPageURL?url}"/>
  <#include "article_blurb.ftl">
  <h1 xpathLocation="noSelect" property="dc:title" datatype="" rel="dc:type" href="http://purl.org/dc/dcmitype/Text"><@articleFormat>${article.docTitle}</@articleFormat></h1>
  <#if article.description ??><div property="dc:description" datatype="" style="display:none; visibility:hidden;"><@articleFormat>${article.description}</@articleFormat></div></#if>
  <span property="dc:date" content="${article.date}" datatype="xsd:date" rel="dc:identifier" href="http://dx.doi.org/${article.shortDOI}"></span>
  <#if article.articleSubjects??>
  <#list article.articleSubjects as articleSubject>
  <span property="dc:subject" content="${articleSubject}"></span>
  </#list>
  </#if>
  <#assign tab="article" />
  <#include "article_tabs.ftl">
  <#if (numRetractions > 0)>
  <div id="retractionHtmlId" class="retractionHtmlId" xpathLocation="noSelect">
    <div id="retractionlist">
    <#list retractions as retraction>
      <@s.url id="retractionFeedbackURL" includeParams="get" namespace="/annotation" action="listThread" inReplyTo="${retraction.id}" root="${retraction.id}" />
      <div><p class="retractionHtmlId">Retraction: ${retraction.title}</p>
        ${retraction.escapedComment} (<a href="${retractionFeedbackURL}">comment on this retraction</a>)</div>
    </#list>
    </div>
  </div>
  </#if>
  <#if (numFormalCorrections > 0)>
  <div id="fch" class="fch" xpathLocation="noSelect">
    <p class="fch"><strong> Formal Correction:</strong> This article has been <em>formally corrected</em> to address the following errors.</p>
    <ol id="fclist" class="fclist">
    <#list formalCorrections as correction>
      <li><span>${correction.getCommentWithUrlLinkingNoPTags(true)} (<a class="formalCorrectionHref" href="#" annid="${correction.id}">read formal correction)</a></span></li>
    </#list>
    </ol>
  </div>
  </#if>
  <div id="articleMenu" xpathLocation="noSelect">
    <div class="wrap">
      <ul>
        <li class="annotation icon">To <strong>add a note</strong>, highlight some text. <a href="#" onclick="toggleAnnotation(this, 'public'); return false;" title="Click to turn notes on/off">Hide notes</a></li>
        <li class="discuss icon">
          <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
            <a href="${createDiscussionURL}">Make a general comment</a>
          <#else>
            <a href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}">Make a general comment</a>
          </#if>
        </li>
      </ul>
      <div id="sectionNavTopBox" style="display:none;">
        <p><strong>Jump to</strong></p>
        <div id="sectionNavTop" class="tools"></div>
      </div>
    </div>
  </div>
  <@s.property value="transformedArticle" escape="false"/>
</div>
