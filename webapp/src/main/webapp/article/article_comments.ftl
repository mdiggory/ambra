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
<@s.url id="thisPageURL" includeParams="get" includeContext="true" encode="false"/>
<@s.url namespace="/article" includeParams="none" id="articleURL" action="fetchArticle" articleURI="${articleInfoX.doi}"/>
<@s.url namespace="/annotation/secure" includeParams="none" id="startDiscussionUrl" action="startDiscussion" articleURI="${articleInfoX.doi}"/>
<@s.url namespace="/article" includeParams="none" id="correctionsURL" action="fetchArticleCorrections" articleURI="${articleInfoX.doi}"/>
<@s.url namespace="/article" includeParams="none" id="commentsURL" action="fetchArticleComments" articleURI="${articleInfoX.doi}"/>

<div id="content" class="article" style="visibility:visible;">
  <#include "article_rhc.ftl">

  <div id="articleContainer">
   <#if isDisplayingCorrections>
    <div id="researchArticle" class="content corrections comments">
   <#else>
    <div id="researchArticle" class="content comments">
   </#if>
      <a id="top" name="top" toc="top" title="Top"></a>
      <#include "article_blurb.ftl">
      <h1 xpathLocation="noSelect"><@articleFormat>${article.docTitle}</@articleFormat></h1>
      <#assign tab="comments" />
      <#include "article_tabs.ftl">
      <!--<div class="rss"><a href="#">Comments RSS</a></div>-->
      <ul>
      <#if (numComments > 0 && isDisplayingCorrections)>
        <li><a href="${commentsURL}" title="View all Comments" class="discuss icon">View all Comments</a></li>
      </#if>
      <#if isRetracted && !isDisplayingCorrections>
        <li><a href="${correctionsURL}" title="View Retraction" class="corrections icon">View Retraction</a></li>
      <#else>
        <#if (numCorrections > 0 && !isDisplayingCorrections)>
        <li><a href="${correctionsURL}" title="View all corrections" class="corrections icon">View all corrections</a></li>
        </#if>
      </#if>
        <li><a href="${startDiscussionUrl}" title="Click to make a new comment on this article" class="discuss icon">Make a new comment on this article</a></li>
      </ul>
      <#if (commentary?size > 0)>
      <table class="directory" cellpadding="0" cellspacing="0">
        <#list commentary as comment>
          <@s.url namespace="/annotation" includeParams="none" id="listThreadURL" action="listThread" root="${comment.ID}"/>
          <@s.url namespace="/user" includeParams="none" id="showUserURL" action="showUser" userId="${comment.creatorID}"/>

          <#if ((comment.xpath)!"")?length == 0>
            <#assign class="discuss"/>
          <#else>
            <#assign class="annotation"/>
          </#if>
          <#if comment.totalNumReplies != 1>
            <#assign label = "responses">
          <#else>
            <#assign label = "response">
          </#if>

          <tr>
            <td class="replies">${comment.totalNumReplies} ${label}<br /></td>
            <td class="title"><a href="${listThreadURL}" title="View Full Discussion Thread" class="${class} icon">${comment.title}</a></td>
            <td class="info">Posted by <a href="${showUserURL}" title="Discussion Author" class="user icon">${comment.creatorDisplayName}</a> on <strong>${comment.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong></td>
          </tr>
          <tr>
            <td colspan="3" class="last">Most recent response on <strong>${comment.lastReplyDate?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong></td>
          </tr>
        </#list>
      </table>
      </#if>
    </div>
  </div>
</div>
<div style="visibility:hidden">
  <#include "/widget/ratingDialog.ftl">
  <#include "/widget/loadingCycle.ftl">
</div>
