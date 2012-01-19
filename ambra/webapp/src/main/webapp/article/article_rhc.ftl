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
<!-- begin : right hand column -->
<@s.url id="articleArticleRepXML"  namespace="/article" action="fetchObjectAttachment" includeParams="none" uri="${articleURI}">
  <@s.param name="representation" value="%{'XML'}"/>
</@s.url>
<@s.url id="articleArticleRepPDF"  namespace="/article" action="fetchObjectAttachment" includeParams="none" uri="${articleURI}">
  <@s.param name="representation" value="%{'PDF'}"/>
</@s.url>
<@s.url id="articleCitationURL"  namespace="/article" action="citationList" includeParams="none" articleURI="${articleURI}" />
<@s.url id="emailArticleURL" namespace="/article" action="emailArticle" articleURI="${articleURI}"/>
<@s.url id="relatedArticleURL" namespace="/article" action="fetchRelatedArticle" articleURI="${articleURI}"/>

<div id="rhc" xpathLocation="noDialog">
 
  <div id="download" class="rhcBox_type1">
    <div class="wrap">
      <ul>
        <li class="download icon"><strong>Download:</strong> <a href="${articleArticleRepPDF}">PDF</a> | <a href="${articleCitationURL}">Citation</a> | <a href="${articleArticleRepXML}">XML</a></li>
        <#if tab?? && tab == "article">
          <li class="print icon"><a href="#" onclick="window.print();return false;"><strong>Print article</strong></a></li>
        </#if>
      </ul>
    </div>
  </div>

  <#if articleIssues?? && articleIssues?size gt 0>
    <div id="published" class="rhcBox_type2">
      <p><strong>Published in the</strong>
      <#list articleIssues as oneIssue>
        <@s.a href="${freemarker_config.getJournalUrl(oneIssue[1])}${freemarker_config.context}/article/browseIssue.action?issue=${oneIssue[4]?url}" title="Browse Open-Access Issue">${oneIssue[5]} ${oneIssue[3]} Issue of <em>${freemarker_config.getDisplayName(oneIssue[1])}</em></@s.a>
      </#list></p>
    </div>
  </#if>

  <div id="impact" class="rhcBox_type2">
    <div id="ratingRhc1">
      <#include "/article/article_rhc_rating.ftl">
    </div>
  </div>

  <#if articleInfoX?? && articleInfoX.relatedArticles?size gt 0>
    <div id="related" class="rhcBox_type2">
      <h6>Related Content</h6>
        <dl class="related">
          <dt>Related ${freemarker_config.orgName} Articles</dt>
          <#list articleInfoX.relatedArticles as ra>
          <dd><@s.a href="${freemarker_config.doiResolverURL}${ra.uri?replace('info:doi/','')}" title="Read Open-Access Article"><@articleFormat>${ra.title}</@articleFormat></@s.a></dd>
          </#list>
        </dl>

      <dl class="related">
        <dt>Related Articles on the Web</dt>
        <dd><a href="http://scholar.google.com/scholar?hl=en&lr=&q=related:${article.docURL?url}&btnG=Search">Google Scholar</a></dd>
        <dd id="pubMedRelatedLIInRHC" style="display:none;"><a id="pubMedRelatedURLInRHC">PubMed</a></dd>
      </dl>

      <div class="more clearfix"><a href="${relatedArticleURL}">More</a></div>
    </div>
  <#else>
    <!--  If there are related articles listed in ALM, then Javascript will compose the "Related Content" block  -->
    <form action="">
      <input type="hidden" name="relatedArticleURL" id="relatedArticleURL" value="${article.docURL?url}" />
      <input type="hidden" name="isShouldRenderRelatedContentBox" id="isShouldRenderRelatedContentBox" value="true" />
    </form>
    <div id="relatedContentBoxInRHC" style="display:none;"></div>
  </#if>

  <div id="share" class="rhcBox_type2">
    <h6>Share this Article <a href="${help}#socialBookmarkLinks" class="replaced" id="info" title="More information"><span>info</span></a></h6>
    <ul>
      <li class="bookmarklets">Bookmark:

          <#-- StumbleUpon -->
          <a href="http://www.stumbleupon.com/submit?url=${article.jDocURL}" target="_new"> <img border=0 src="http://cdn.stumble-upon.com/images/16x16_su_solid.gif" alt="StumbleUpon" title="Add to StumbleUpon"></a>
          <#-- for more info, see http://www.stumbleupon.com/buttons.php -->
          <#-- Facebook -->
          <script>function fbs_click() {u='${article.docURL}';t='${article.docTitle?url?replace("'","\\'")}';window.open('http://www.facebook.com/sharer.php?u='+encodeURIComponent(u)+'&t='+encodeURIComponent(t),'sharer','toolbar=0,status=0,width=626,height=436');return false;}</script><a href="http://www.facebook.com/share.php?u=${article.docURL?url}" onclick="return fbs_click()"><img src="http://static.ak.fbcdn.net/images/share/facebook_share_icon.gif" alt="Facebook" title="Add to Facebook" /></a>       <!-- for mor info, see http://www.facebook.com/share_partners.php -->
          <#-- Connotea -->
          <script type="text/javascript">
            function bookmark_in_connotea(u) {
                a=false; x=window; e=x.encodeURIComponent; d=document;
                w=open('http://www.connotea.org/addpopup?continue=confirm&uri='+e(u),
                    'add', 'width=600, height=400, scrollbars, resizable');
                void(x.setTimeout('w.focus()',200));
            }
          </script>
          <a style='cursor: pointer;' onclick='javascript:bookmark_in_connotea("${article.docURL}");'><img src='${freemarker_config.getContext()}/images/icon_connotea_16x16.gif' alt="Connotea" title="Add to Connotea"/></a>
          <#-- See: http://www.connotea.org/wiki/AddToConnoteaButton -->
          <#-- Citeulike -->
          <a href="http://www.citeulike.org/posturl?url=${article.docURL?url}&title=${article.docTitle?url}" target="_new"><img src='${freemarker_config.getContext()}/images/icon_citeulike_16x16.gif' alt="CiteULike" title="Add to CiteULike" /></a>
          <#-- For more info see http://www.citeulike.org/faq/all.adp -->
          <a href="http://www.mendeley.com/import/?url=${article.docURL?url}" title="Add to Mendeley" target="_new"><img src="${freemarker_config.getContext()}/images/icon_mendeley_16x16.gif" alt="Bibliography"></a>
          <#-- Twitter javascript via: http://www.saschakimmel.com/2009/05/how-to-create-a-dynamic-tweet-this-button-with-javascript/ -->
          <script type="text/javascript">
          var twtTitle  = '${article.docTitle?replace("'","\\'")}';
          var twtUrl    = '${article.docURL}';
          var maxLength = 140 - (twtUrl.length + 1);
          if (twtTitle.length > maxLength) {
          twtTitle = twtTitle.substr(0, (maxLength - 3))+'...';
          }
          var twtLink = 'http://twitter.com/intent/tweet?text='+encodeURIComponent(twtTitle + ' ' + twtUrl);
          document.write('<a href="'+twtLink+'" target="_blank"'+'><img src="${freemarker_config.context}/images/icon_twitter.gif"  border="0" alt="Twitter icon" title="Tweet This!" /'+'><'+'/a>');
          </script>
          <#-- Digg
            TODO:Eventually we should be passing the abstract as the bodytext to digg and sending a topic as well
            -->
          <script type="text/javascript">
          digg_url = '${article.docURL}';
          digg_skin = 'icon';
          digg_title = '<@articleFormat><@simpleText>${article.docTitle?replace("'","\\'")}</@simpleText></@articleFormat>';
          digg_bodytext = '';
          digg_topic = '';
          digg_media = 'news';
          digg_window = 'new';

          </script>
          <script src="http://widgets.digg.com/diggthis.js" type="text/javascript"></script>
          <#-- for more info see http://digg.com/tools/integrate -->
      </li>
      <li class="email icon"><a href="${emailArticleURL}">Email this article</a></li>
    </ul>
  </div>
</div>
<!-- end : right hand column -->
