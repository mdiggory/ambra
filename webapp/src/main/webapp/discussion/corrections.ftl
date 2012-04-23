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
<div id="content" class="corrections">
  <h1>Corrections</h1>
  <@s.url namespace="/article" includeParams="none" id="articleURL" action="fetchArticle" articleURI="${articleInfo.doi}"/>
  <@s.url namespace="/annotation/secure" includeParams="none" id="startDiscussionUrl" action="startDiscussion" articleURI="${articleInfo.doi}"/>

  <div class="source">
    <span>Original Article</span><a href="${articleURL}" title="Back to original article" class="article icon">${articleInfo.title}</a>
  </div>
  <#if commentary?size == 0>
    <p>There are currently no corrections on this article. 
      You can <a href="${startDiscussionUrl}" title="Click to start a discussion on this article" class="discuss icon">start a discussion</a> or return to the original article to add an annotation.<p>
  <#else>
    <table class="directory" cellpadding="0" cellspacing="0">
      <#list commentary as comment>
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

        <@s.url namespace="/annotation" includeParams="none" id="listThreadURL" action="listThread" root="${comment.annotation.id}" inReplyTo="${comment.annotation.id}"/>
        <@s.url namespace="/user" includeParams="none" id="showUserURL" action="showUser" userAccountUri="${comment.annotation.creator}"/>
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
    <p>You can also <a href="${startDiscussionUrl}" title="Click to make a new comment on this article" class="discuss icon">make a new comment</a> on this article.</p>
  </#if>
</div>
