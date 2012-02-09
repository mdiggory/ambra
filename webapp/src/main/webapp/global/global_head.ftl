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
<#assign pgTitleOrig = freemarker_config.getTitle(templateFile, journalContext)>
<#assign pgTitle = pgTitleOrig>
<#if pgTitleOrig = "CODE_ARTICLE_TITLE" && articleInfoX??> <#--to get article title in w/o a new template for now-->
  <#assign pgTitle = freemarker_config.getArticleTitlePrefix(journalContext) + " " + articleInfoX.unformattedTitle>
</#if>
  <title>${pgTitle}</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link rel="shortcut icon" href="${freemarker_config.context}/images/favicon.ico" type="image/x-icon" />
<@s.url id="homeURL" includeParams="none" includeContext="true" namespace="/" action="home"/>
<link rel="home" title="home" href="${homeURL}" />
<link rel="alternate" type="application/atom+xml"
  title="${freemarker_config.getArticleTitlePrefix(journalContext)} ${rssName?html}"
  href="${Request[freemarker_config.journalContextAttributeKey].baseUrl}${rssPath}" />

<#include "../css/global_css.ftl">

<meta name="description" content="${freemarker_config.getMetaDescription(journalContext)}" />

<meta name="keywords" content="${freemarker_config.getMetaKeywords(journalContext)}" />

<@s.url id="pgURL" includeParams="get" includeContext="true" encode="false"/>

<#-- When the current page is displaying article data, add this extra info for Google Scholar -->
<#if articleInfoX??>
  <meta name="citation_publisher" content="${articleInfoX.publisher}" />

  <#if articleInfoX.unformattedTitle??>
    <meta name="citation_title" content="${articleInfoX.unformattedTitle}"/>
  </#if>

  <#if authorExtras?? >
    <#list authorExtras as author>
      <meta name="citation_author" content="${author.authorName}" />
      <#if author.affiliations?? >
        <#list author.affiliations as affiliation>
          <meta name="citation_author_institution" content="${affiliation?trim}" />
        </#list>
      </#if>
    </#list>
  </#if>

  <#if articleInfoX.date??>
    <meta name="citation_date" content="${articleInfoX.date?date?string("yyyy/M/d")}"/>
  </#if>

  <#assign pdfURL = "${freemarker_config.doiResolverURL}" + "${articleInfoX.id?replace('info:doi/','')}" + ".pdf" />
  <meta name="citation_pdf_url" content="${pdfURL}" />

  <#if article??>
    <#if publishedJournal??><meta name="citation_journal_title" content="${publishedJournal}" /></#if>
    <meta name="citation_firstpage" content="${article.eLocationId}"/>
    <meta name="citation_issue" content="${article.issue}"/>
    <meta name="citation_volume" content="${article.volume}"/>
    <meta name="citation_issn" content="${article.eIssn}"/>
  </#if>

  <#if journalAbbrev??>
    <meta name="citation_journal_abbrev" content="${journalAbbrev}" />
  </#if>

  <#if references??>
    <#list references as reference>
      <meta name="citation_reference" content="${reference.referenceContent}" />
    </#list>
  </#if>

</#if>

<#assign rdfPgURL = pgURL?replace("&amp;", "&")>

<!--
<rdf:RDF xmlns="http://web.resource.org/cc/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
<Work rdf:about="${Request[freemarker_config.journalContextAttributeKey].baseHostUrl}${rdfPgURL}">
   <license rdf:resource="http://creativecommons.org/licenses/by/2.5/" />
</Work>
<License rdf:about="http://creativecommons.org/licenses/by/2.5/">
   <permits rdf:resource="http://web.resource.org/cc/Reproduction" />
   <permits rdf:resource="http://web.resource.org/cc/Distribution" />
   <requires rdf:resource="http://web.resource.org/cc/Notice" />
   <requires rdf:resource="http://web.resource.org/cc/Attribution" />
   <permits rdf:resource="http://web.resource.org/cc/DerivativeWorks" />
</License>
<rdf:Description
     rdf:about="${Request[freemarker_config.journalContextAttributeKey].baseHostUrl}${rdfPgURL}"
     dc:identifier="${Request[freemarker_config.journalContextAttributeKey].baseHostUrl}${rdfPgURL}"
     dc:title="${pgTitle}"
     <#if pgTitleOrig = "CODE_ARTICLE_TITLE">
       <@s.url id="trackbackURL" namespace="/" action="trackback" includeParams="none" trackbackId="${articleURI}"/>
       trackback:ping="${Request[freemarker_config.journalContextAttributeKey].baseHostUrl}${trackbackURL}"
     </#if>/>
</rdf:RDF>

-->
