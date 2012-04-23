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
<#import "/global/global_variables.ftl" as global>
<#assign cisStartDateMillis = freemarker_config.cisStartDateMillis>
<#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
  <#assign loginURL = "#">
<#else>
  <#assign loginURL = "${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}">
</#if>
  <#macro writeReplyDetails reply replyToAuthorId replyToAuthorName>
    <@s.url namespace="/user" includeParams="none" id="showUserURL" action="showUser" userId="${reply.creatorID?c}"/>
    <@s.url namespace="/user" includeParams="none" id="authorURL" action="showUser" userId="${replyToAuthorId}"/>
      <div class="response">
        <div class="hd">
          <!-- begin response title -->
          <h3><a name="${reply.ID?c}">${reply.title}</a></h3>
          <!-- end : response title -->
          <!-- begin : response poster details -->
          <div class="detail">
            <a href="${showUserURL}" class="user icon">${reply.creatorDisplayName}</a> replied to <a href="${authorURL}" class="user icon">${replyToAuthorName}</a> on <strong>${reply.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong></div>
          <!-- end : response poster details -->
        </div>
        <!-- begin : response body text -->
        <blockquote>
          ${reply.body}
          <#--
            If the reply was created before the competing interest statement
            System was implemented, don't display anything
          -->
          <#if (cisStartDateMillis < reply.createdAsMillis)>
            <div class="cis">
            <#if reply.competingInterestStatement?has_content>
              <strong>Competing interests declared:</strong> ${reply.competingInterestStatement}
            <#else>
              <strong>No competing interests declared.</strong>
            </#if>
            </div>
          </#if>
        </blockquote>
        <!-- end : response body text -->
        <!-- begin : toolbar options -->
        <div class="toolbar">
          <ul>
            <li>
            <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
              <a href="${loginURL}" onclick="ambra.responsePanel.show(this, _dcf, 'toolbar', '${reply.ID?c}', null, null, 1); return false;" class="flag tooltip" title="Report a Concern">Report a Concern</a>
            <#else>
              <a href="${loginURL}" class="flag tooltip" title="Report a Concern">Report a Concern</a>
            </#if>
            </li>
            <li>
            <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
              <a href="${loginURL}" onclick="ambra.responsePanel.show(this, _dcr, 'toolbar', '${baseAnnotation.ID?c}', '${reply.ID?c}', '${reply.title?js_string}'); return false;" class="respond tooltip" title="Click to respond">Respond to this Posting</a>
            <#else>
              <a href="${loginURL}" class="respond tooltip" title="Click to respond">Respond to this Posting</a>
            </#if>
            </li>
          </ul>
        </div>
        <!-- end : toolbar options -->
        <#list reply.replies as subReply>
          <@writeReplyDetails reply=subReply replyToAuthorId=reply.creatorID?c replyToAuthorName=reply.creatorDisplayName/>
        </#list>
      </div>
  </#macro>

  <#assign styleCorrections = "" />
  <#if baseAnnotation.correction>
    <#assign styleCorrections = " corrections" />
  </#if>
  <!-- begin : main content -->
  <div id="content" class="${styleCorrections}">
    <h1>${baseAnnotation.title}</h1>
    <div class="source">
      <span>Original Article</span>
      <@s.url id="origArticle" includeParams="none" namespace="/article" action="fetchArticle" annotationId="${baseAnnotation.ID?c}" articleURI="${articleInfo.doi}"/>
      <a href="${origArticle}" title="Back to original article" class="article icon">${articleInfo.title}</a>
    </div>
    <div class="response original">
      <div class="hd">
        <!-- begin response title -->
        <h3><a name="${baseAnnotation.ID?c}">${baseAnnotation.title}</a></h3>
        <!-- end : response title -->
        <!-- begin : response poster detail -->
        <@s.url namespace="/user" includeParams="none" id="baseAuthorURL" action="showUser" userId="${baseAnnotation.creatorID?c}"/>

        <div class="detail">Posted by <a href="${baseAuthorURL}" title="Annotation Author" class="user icon">${baseAnnotation.creatorDisplayName}</a> on <strong>${baseAnnotation.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong>
        </div>
        <!-- end : response poster details -->
      </div>
      <!-- begin : response body text -->
      <blockquote>
        ${baseAnnotation.body}

          <#--
            If the reply was created before the competing interest statement
            System was implemented, don't display anything
          -->
        <#if (cisStartDateMillis < baseAnnotation.createdAsMillis)>
          <div class="cis">
            <#if baseAnnotation.competingInterestStatement?? && baseAnnotation.competingInterestStatement != "">
              <strong>Competing interests declared:</strong> ${baseAnnotation.competingInterestStatement}
            <#else>
              <strong>No competing interests declared.</strong>
            </#if>
          </div>
        </#if>
          <#--Include citation.ftl with variables set-->
        <#if baseAnnotation.citation??>
          <div class="citation">
            <strong>Citation: </strong>
            <#assign isCorrection = true/>
            <#assign authorList = baseAnnotation.citation.authors/>
            <#assign collaborativeAuthors = baseAnnotation.citation.collabAuthors/>
            <#if baseAnnotation.citation.doi??>
              <#assign doi = articleDoi/>
            </#if>
            <#if baseAnnotation.citation.year??>
              <#assign year = baseAnnotation.citation.year/>
            </#if>
            <#if baseAnnotation.citation.title??>
              <#assign title = baseAnnotation.citation.title/>
            </#if>
            <#if baseAnnotation.citation.journal??>
              <#assign journal = baseAnnotation.citation.journal/>
            </#if>
<#--    Shouldn't show an ElocationId on correction citations
            <#if baseAnnotation.citation.eLocationId?? >
              <#assign eLocationId = baseAnnotation.citation.eLocationId/>
            </#if>
-->
            <#assign doi = baseAnnotation.annotationUri/>
            <#include "/article/citation.ftl"/>
          </div>
        </#if>
      </blockquote>
      <!-- end : response body text -->
      <!-- begin : toolbar options -->
      <div class="toolbar">
        <ul>
          <li>
          <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
            <a href="${loginURL}" onclick="ambra.responsePanel.show(this, _dcf, 'toolbar', '${baseAnnotation.ID?c}', null, null, 0); return false;" class="flag tooltip" title="Report a Concern">Report a Concern</a>
          <#else>
            <a href="${loginURL}" class="flag tooltip" title="Report a Concern">Report a Concern</a>
          </#if>
          </li>
          <li>
          <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
            <a href="${loginURL}" onclick="ambra.responsePanel.show(this, _dcr, 'toolbar', '${baseAnnotation.ID?c}', '${baseAnnotation.ID?c}', '${baseAnnotation.title?js_string}'); return false;" class="respond tooltip" title="Click to respond">Respond to this Posting</a>
          <#else>
            <a href="${loginURL}" class="respond tooltip" title="Click to respond">Respond to this Posting</a>
          </#if>
          </li>
        </ul>
      </div>
      <!-- end : toolbar options -->
    </div>
    <!-- begin : response note that all responses TO this response get enclosed within this response container  -->
    <#list baseAnnotation.replies as reply>
      <@writeReplyDetails reply=reply replyToAuthorId=baseAnnotation.creatorID?c replyToAuthorName=baseAnnotation.creatorDisplayName/>
    </#list>
    <!-- end : response -->
  </div>

  <@s.url id="commentsURL" namespace="/article" action="fetchArticleComments" includeParams="none" articleURI="${articleInfo.doi}"/>
  <p><a href="${commentsURL}" class="commentary icon">See all ongoing discussions</a> on this article</p>

  <!-- end : main contents -->




