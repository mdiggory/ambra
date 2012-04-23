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
<div id="content" class="article" style="visibility:visible;">
  <#include "article_rhc.ftl">

  <div id="articleContainer">
    <form action="">
      <input type="hidden" name="doi" id="doi" value="${articleURI}" />
    </form>

    <div id="researchArticle" class="content related">
      <a id="top" name="top" toc="top" title="Top"></a>
      <#include "article_blurb.ftl">
      <h1 xpathLocation="noSelect"><@articleFormat>${article.docTitle}</@articleFormat></h1>
      <#assign tab="related" />
      <#include "article_tabs.ftl">

      <h2>Related Articles <a href="${help}#relatedArticles" class="replaced" id="info" title="More information"><span>info</span></a></h2>

      <h3>Related Articles on the Web</h3>
      <a href="http://scholar.google.com/scholar?hl=en&lr=&q=related:${article.docURL?url}&btnG=Search">Google Scholar</a>

      <h3>Citations</h3>
      <div>Search for citations on <a href="http://scholar.google.com/scholar?hl=en&lr=&cites=${article.docURL?url}">Google Scholar</a>.</div>

      <h2>Related Blog Posts <a href="${help}#relatedBlogPosts" class="replaced" id="info" title="More information"><span>info</span></a></h2>
      Search for related blog posts on <a href="http://blogsearch.google.com/blogsearch?as_q=%22${articleInfoX.unformattedTitle?url}%22">Google Blogs</a>

      <h3>Trackbacks</h3>
      <div>To trackback this article use the following trackback URL:<br/>
        <@s.url namespace="/" includeParams="none" id="trackbackURL" action="trackback" doi="${articleURI}"/>
        <#assign trackbackLink = Request[freemarker_config.journalContextAttributeKey].baseHostUrl + trackbackURL>
        <div class="trackbackURL"><strong>${trackbackLink}</strong></div>
      </div>

      <#list trackbackList as t>
        <div class="trackback">
          <#if t.title?has_content>
            <#assign title = t.title>
          <#else>
            <#assign title = t.url>
          </#if>
          <p class="header">
          <#if t.blogName?has_content>
          <span class="blog">${t.blogName}</span>
          <#else>
          An unknown source
          </#if>
           referenced this article in "<a href="${t.url}" title="${t.title?replace('"',"")!""}" class="post">${title}</a>" <span class="timestamp">on <strong>${t.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong></span></p>
          <#if t.excerpt?has_content>
          <p class="excerpt">"${t.excerpt}"</p>
          </#if>
        </div>
      </#list>
    </div>
  </div>
  <div class="clearer">&nbsp;</div><!-- Fixes a bug in IE6 where a long rhc overlaps the footer. -->
  <div style="visibility:hidden">
    <#include "/widget/ratingDialog.ftl">
    <#include "/widget/loadingCycle.ftl">
  </div>
</div>
