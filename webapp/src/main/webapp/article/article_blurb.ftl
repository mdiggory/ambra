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
<div id="contentHeader"><p>Open Access</p><p id="articleType">${articleType.heading}
<#if articleType.code??>
  <#if articleType.code != "research_article">
    <a class="info" title="What is the ${articleType.heading} article type?" href="#${articleType.code}">Info</a>
  </#if>
<#else>
  --!!ARTICLE TYPE CODE UNDEFINED!!--
</#if></p></div>
<#if (article.publisher?length > 0)>
  <div id="publisher"><p>${article.publisher}</p></div>
</#if>
