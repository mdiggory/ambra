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
      </div>

      <#list articleRatings as articleRating>
        <@s.url id="fetchUserURL" namespace="/user" action="showUser" userId="${articleRating.creatorID?c}"/>
        <div class="response ratingComment">
          <a name="${articleRating.ratingId}"/>
          <div class="hd">
            <!-- begin : response title : user -->
            <h3>
              <#if articleRating.commentTitle?exists>
                ${articleRating.commentTitle}
              </#if>
              <span class="detail">Posted by <a href="${fetchUserURL}" title="Annotation Author" class="user icon">${articleRating.creatorName}</a>
                on <strong>${articleRating.created?string("dd MMM yyyy '</strong>at<strong>' HH:mm zzz")}</strong>
              </span>
            </h3>
            <!-- end : response title : user -->
          </div>
          <!-- begin : response body text -->
          <div class="ratingDetail">
            <div class="posterRating">
              <ol class="ratingAvgs">
              <#if isResearchArticle == true>
                <#if articleRating.insight?exists>
                  <li><label for="insight">Insight</label>
                      <ul class="star-rating rating" title="insight">
                        <#assign insightPct = (20 * articleRating.insight)?string("##0")>
                        <li class="current-rating average pct${insightPct}">Currently ${articleRating.insight?string("0.#")}/5 Stars.</li>
                      </ul>
                  </li>
                </#if>
                <#if articleRating.reliability?exists>
                  <li><label for="reliability">Reliability</label>
                    <ul class="star-rating rating" title="reliability">
                      <#assign reliabilityPct = (20 * articleRating.reliability)?string("##0")>
                      <li class="current-rating average pct${reliabilityPct}">Currently ${articleRating.reliability?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
                <#if articleRating.style?exists>
                  <li><label for="style">Style</label>
                    <ul class="star-rating rating" title="style">
                      <#assign stylePct = (20 * articleRating.style)?string("##0")>
                      <li class="current-rating average pct${stylePct}">Currently ${articleRating.style?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
                <#if articleRating.overallRounded?exists>
                  <li><label for="overall"><strong>Overall</strong></label>
                    <ul class="star-rating rating" title="overall">
                      <#assign overallPct = (20 * articleRating.overallRounded)?string("##0")>
                      <li class="current-rating average pct${overallPct}">Currently ${articleRating.overallRounded?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
                </#if>
              <#else>
                  <li><label for="singleRating">&nbsp;</label>
                    <ul class="star-rating rating single" title="singleRating">
                      <#assign singleRatingPct = (20 * articleRating.singleRating)?string("##0")>
                      <li class="current-rating average pct${singleRatingPct}">Currently ${articleRating.singleRating?string("0.#")}/5 Stars.</li>
                    </ul>
                  </li>
              </#if>
              </ol>
            </div>
            <blockquote>
              <#if articleRating.commentValue?exists>
                <p>${articleRating.commentValue}</p>
              </#if>
              <#if (cisStartDateMillis < articleRating.createdMillis)>
                <div class="cis">
                  <#if (articleRating.CIStatement?? && articleRating.CIStatement !="")>
                    <strong>Competing interests declared:</strong> ${articleRating.CIStatement}
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
            <#if articleRating.commentTitle?exists>
              <#assign flagTitle = "${articleRating.commentTitle}">
            <#else>
              <#assign flagTitle = "Flag this rating">
            </#if>
            <ul>
              <li>
                <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
                  <a href="#" onclick="ambra.responsePanel.show(this, _dcf, 'toolbar', '${articleRating.ratingId}', null, null, 2); return false;" class="flag tooltip" title="Report a Concern">Report a Concern</a>
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
