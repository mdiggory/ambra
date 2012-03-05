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

  <div id="content">
    <h1>Ratings</h1>
      <div class="source">
        <span>Original Article</span>
        <@s.url id="fetchArticleURL" namespace="/article" action="fetchArticle" articleURI="${articleURI}"/>

        <a href="${fetchArticleURL}" title="Back to original article" class="article icon">${articleTitle}
        <#if isResearchArticle == true>
          <#if articleOverallRounded?exists>
            <span class="inline-rating inlineRatingEnd">
              <ul class="star-rating rating" title="overall">
                <#assign overallPct = (20 * articleOverallRounded)?string("##0")>
                <li class="current-rating pct${overallPct}">Currently ${articleOverallRounded?string("0.#")}/5 Stars.</li>
              </ul>
            </span>
          </#if>
        <#else> 
          <#if articleSingleRatingRounded?exists>
            <span class="inline-rating inlineRatingEnd">
              <ul class="star-rating rating" title="single">
                <#assign overallPct = (20 * articleSingleRatingRounded)?string("##0")>
                <li class="current-rating pct${overallPct}">Currently ${articleSingleRatingRounded?string("0.#")}/5 Stars.</li>
              </ul>
            </span>
          </#if>
        </#if>
        </a>
        <!--<p><a href="/annotation/getCommentary.action?target=${articleURI}" class="commentary icon">See all commentary</a> on this article</p>-->
      </div>

      <#list articleRatingSummaries as articleRatingSummary>
        <@s.url id="fetchUserURL" namespace="/user" action="showUser" userAccountUri="${articleRatingSummary.creatorURI}"/>
        <div class="response ratingComment">
          <a name="${articleRatingSummary.ratingId}"/>
          <div class="hd">
            <!-- begin : response title : user -->
            <h3>
              <#if articleRatingSummary.commentTitle?exists>
                ${articleRatingSummary.commentTitle}
              </#if>
              <span class="detail">Posted by <a href="${fetchUserURL}" title="Annotation Author" class="user icon">${articleRatingSummary.creatorName}</a>
                on <strong>${articleRatingSummary.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong>
              </span>
            </h3>
            <!-- end : response title : user -->
          </div>
          <!-- begin : response body text -->
          <div class="ratingDetail">
            <div class="posterRating">
              <ol class="ratingAvgs">
              <#if isResearchArticle == true>
                <#if articleRatingSummary.insight?exists>
                  <li><label for="insight">Insight</label>
                      <ul class="star-rating rating" title="insight">
                        <#assign insightPct = (20 * articleRatingSummary.insight)?string("##0")>
                        <li class="current-rating average pct${insightPct}">Currently ${articleRatingSummary.insight?string("0.#")}/5 Stars.</li>
                      </ul>
                  </li>
                </#if>
                <#if articleRatingSummary.reliability?exists>
                  <li><label for="reliability">Reliability</label>
                    <ul class="star-rating rating" title="reliability">
                      <#assign reliabilityPct = (20 * articleRatingSummary.reliability)?string("##0")>
                      <li class="current-rating average pct${reliabilityPct}">Currently ${articleRatingSummary.reliability?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
                <#if articleRatingSummary.style?exists>
                  <li><label for="style">Style</label>
                    <ul class="star-rating rating" title="style">
                      <#assign stylePct = (20 * articleRatingSummary.style)?string("##0")>
                      <li class="current-rating average pct${stylePct}">Currently ${articleRatingSummary.style?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
                <#if articleRatingSummary.overallRounded?exists>
                  <li><label for="overall"><strong>Overall</strong></label>
                    <ul class="star-rating rating" title="overall">
                      <#assign overallPct = (20 * articleRatingSummary.overallRounded)?string("##0")>
                      <li class="current-rating average pct${overallPct}">Currently ${articleRatingSummary.overallRounded?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
              <#else>
                  <li><label for="singleRating">&nbsp;</label>
                    <ul class="star-rating rating single" title="singleRating">
                      <#assign singleRatingPct = (20 * articleRatingSummary.singleRating)?string("##0")>
                      <li class="current-rating average pct${singleRatingPct}">Currently ${articleRatingSummary.singleRating?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
              </#if>
              </ol>
            </div>
            <blockquote>
              <#if articleRatingSummary.commentValue?exists>
                <p>${articleRatingSummary.commentValue}</p>
              </#if>
              <#if (cisStartDateMillis < articleRatingSummary.createdMillis)>
                <div class="cis">
                  <#if (articleRatingSummary.CIStatement?? && articleRatingSummary.CIStatement !="")>
                    <strong>Competing interests declared:</strong> ${articleRatingSummary.CIStatement}
                  <#else>
                    <strong>No competing interests declared.</strong>
                  </#if>
                  </div>
                </#if>
            </blockquote>
          </div>
          <!-- end : response body text -->

          <!-- begin : toolbar options -->
          <div class="toolbar">
            <#if articleRatingSummary.commentTitle?exists>
              <#assign flagTitle = "${articleRatingSummary.commentTitle}">
            <#else>
              <#assign flagTitle = "Flag this rating">
            </#if>
            <ul>
              <li>
                <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
                  <a href="#" onclick="ambra.responsePanel.show(this, _dcf, 'toolbar', '${articleRatingSummary.ratingId}', null, null, 2); return false;" class="flag tooltip" title="Report a Concern">Report a Concern</a>
                <#else>
                  <a href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}" class="flag tooltip" title="Report a Concern">Report a Concern</a>
                </#if>
              </li>
            </ul>
          </div>
          <!-- end : toolbar options -->
        </div>

        <div class="rsep"></div>
      </#list>
    </div>
