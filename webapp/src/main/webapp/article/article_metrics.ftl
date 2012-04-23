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
<@s.url id="crossRefPageURL"  namespace="/article" action="fetchArticleCrossRef" includeParams="none" articleURI="${articleURI}" />
<div id="content" class="article" style="visibility:visible;">
  <#include "article_rhc.ftl">

  <div id="articleContainer">
    <form action="">
      <input type="hidden" name="doi" id="doi" value="${articleURI}" />
      <input type="hidden" name="crossRefPageURL" id="crossRefPageURL" value="${crossRefPageURL}" />
    </form>

    <div id="researchArticle" class="content related">
      <a id="top" name="top" toc="top" title="Top"></a>
      <#include "article_blurb.ftl">
      <h1 xpathLocation="noSelect"><@articleFormat>${article.docTitle}</@articleFormat></h1>
      <#assign tab="metrics" />
      <#include "article_tabs.ftl">

      <a id="usage" name="usage"></a>
      <h2>Article Usage <a href="${help}#articleMetrics" class="replaced" id="info" title="More information"><span>info</span></a></h2>

      <h3>Summary Data for <i>THIS_JOURNAL_NAME</i></h3>

      <a id="citations" name="citations"></a>
      <h2>Citations <a href="${help}#articleCitations" class="replaced" id="info" title="More information"><span>info</span></a></h2>

      <div>Search for citations in <a href="http://scholar.google.com/scholar?hl=en&lr=&cites=${article.docURL?url}">Google Scholar</a>.</div>

      <a id="other" name="other"></a>
      <h2>Other Indicators of Impact <a href="${help}#articleImpactIndicators" class="replaced" id="info" title="More information"><span>info</span></a></h2>

      <h3>Reader Comments</h3>
      <@s.a href="${commentsTabURL}">Comments (${numDiscussions}) and Notes (${numComments})</@s.a>

      <h3>Blog Coverage</h3>
      Search for related blog posts on <a href="http://blogsearch.google.com/blogsearch?as_q=%22${articleInfoX.unformattedTitle?url}%22">Google Blogs</a>

      <h3>Trackbacks</h3>
      <#if trackbackCount < 1 || trackbackCount gt 1>
        <@s.a href="${relatedTabURL}#trackbackLinkAnchor">${trackbackCount} trackbacks</@s.a>
      <#else>
        <@s.a href="${relatedTabURL}#trackbackLinkAnchor">1 trackback</@s.a>
      </#if>


<!--  Rating: Begin  This was copied from article_rhc_rating.ftl  -->

      <h2>PLoS Readers</h2>
      
  <div id="ratingOnArticleMetricsTab" class="metrics_tile">
    <div class="metrics_tile_ratings">
      <@s.url id="ratingsURL" namespace="/rate" action="getArticleRatings" includeParams="none" articleURI="${articleURI}"/>
      <strong>Average Rating</strong>
      <br/>
      <#if (averageRatings.numUsersThatRated != 1)>
        <a href="${ratingsURL}" class="userRatings">(${averageRatings.numUsersThatRated} User Ratings)</a>
      <#else>
        <a href="${ratingsURL}" class="userRatings">(${averageRatings.numUsersThatRated} User Rating)</a>
      </#if>
      <ol class="ratingAvgs">
        <#if isResearchArticle == true>
            <li><label for="insight">Insight</label>
                <ul class="star-rating rating" title="insight">
                  <#assign insightPct = (10 * (2 * averageRatings.insight.average)?round)?string("##0")>
                  <li class="current-rating average pct${insightPct}">Currently ${averageRatings.insight.average?string("0.#")}/5 Stars.</li>
                </ul>
            </li>
            <li><label for="reliability">Reliability</label>
              <ul class="star-rating rating" title="reliability">
                <#assign reliabilityPct = (10 * (2 * averageRatings.reliability.average)?round)?string("##0")>
                <li class="current-rating average pct${reliabilityPct}">Currently ${averageRatings.reliability.average?string("0.#")}/5 Stars.</li>
              </ul>
            </li>
            <li><label for="style">Style</label>
              <ul class="star-rating rating" title="style">
                <#assign stylePct = (10 * (2 * averageRatings.style.average)?round)?string("##0")>
                <li class="current-rating average pct${stylePct}">Currently ${averageRatings.style.average?string("0.#")}/5 Stars.</li>
              </ul>
            </li>
            <li><label for="overall"><strong>Overall</strong></label>
              <ul class="star-rating rating" title="overall">
                <#assign overallPct = (10 * (2 * averageRatings.roundedOverall)?round)?string("##0")>
                <li class="current-rating average pct${overallPct}">Currently ${averageRatings.roundedOverall?string("0.#")}/5 Stars.</li>
              </ul>
            </li>
        <#else>
            <li><label for="singleRating">&nbsp;</label>
              <ul class="star-rating rating single" title="singleRating">
                <#assign singleRatingPct = (10 * (2 * averageRatings.single.average)?round)?string("##0")>
                <li class="current-rating average pct${singleRatingPct}">Currently ${averageRatings.single.average?string("0.#")}/5 Stars.</li>
              </ul>
            </li>
        </#if>
      </ol>
    </div>

    <div class="metrics_tile_footer">
          <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
            <#if HasRated>
              <a href="javascript:void(0);" onclick="return ambra.rating.show('edit');" class="rateThis">Edit My Rating</a>
              <#else>
              <a href="javascript:void(0);" onclick="return ambra.rating.show();" class="rateThis">Rate this article</a>
            </#if>
            <#else>
              <a href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}">Rate this article</a>
          </#if>
    </div>
  </div>
<!--  Rating: End  -->


    </div>
  </div>
  <div style="visibility:hidden">
    <#include "/widget/ratingDialog.ftl">
    <#include "/widget/loadingCycle.ftl">
  </div>
</div>
