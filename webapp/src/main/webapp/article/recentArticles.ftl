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
<#assign numArticles = recentArticles?size>
<ul class="articles">
  <#if (numArticles > 0)>
    <#assign randomIndices = action.randomNumbers(numArticlesToShow, numArticles)>
    <#list randomIndices as random>
      <#assign article = recentArticles[random]>
      <#if random_index % 2 == 0>
        <li class="even">
      <#else>
        <li>
      </#if>
      <@s.url id="articleURL" includeParams="none" namespace="/article" action="fetchArticle" articleURI="${article.id}"/>
      <a href="${articleURL}" title="Read Open Access Article">${article.title}</a>
      </li>
    </#list>
  </#if>
  <li><a href="${freemarker_config.context}/article/browse.action?field=date" title="Browse Articles">Browse all recently published articles</a></li>
</ul>
