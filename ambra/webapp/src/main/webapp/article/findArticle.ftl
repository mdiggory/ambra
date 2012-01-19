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
<!-- begin : main contents wrapper -->
<div id="content" class="static">

<#if Parameters.title?exists>
  <#assign title = Parameters.title>
<#else>
  <#assign title = "">
</#if>

<#if Parameters.author?exists>
  <#assign author = Parameters.author>
<#else>
  <#assign author= "">
</#if>
  <h1>Find this article online</h1>
  <h2>${title?html}</h2>
  <p>Use the following links to find the article:</p>
  <ul>
  <li><a href="${crossRefUrl}"
   onclick="window.open(this.href, 'ambraFindArticle','');return false;" title="Go to article in CrossRef" class="crossref icon">CrossRef</a>
    <#if pubGetUrl??>
      <a href="${pubGetUrl}"
   onclick="window.open(this.href, 'ambraFindArticle','');return false;" title="Get the full text PDF from PubGet">
        <img title="Get the full text PDF from PubGet" src="${freemarker_config.context}/images/icon_pubgetpdf.gif"/></a>
    </#if>
  </li>
  <li><a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=PubMed&cmd=Search&doptcmdl=Citation&defaultField=Title+Word&term=${author?html}%5Bauthor%5D+AND+${title?html}"
   onclick="window.open(this.href, 'ambraFindArticle','');return false;" title="Go to article in PubMed" class="ncbi icon">PubMed/NCBI</a></li>
  <li><a href="http://scholar.google.com/scholar?hl=en&safe=off&q=author%3A${author?html}+%22${title?html}%22"
       onclick="window.open(this.href, 'ambraFindArticle','');return false;" title="Go to article in Google Scholar" class="google icon">Google Scholar</a></li>
  </ul>
  <a href="#" onClick="history.back();return false;" class="article icon">Back to article</a>
</div>