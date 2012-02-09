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
<#assign numArticles = mostViewedArticles?size>
<#if (numArticles > 0)>
    <ul class="articles">
        <#list mostViewedArticles as article>
        <@s.url id="artURL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="info:doi/${article.first}"/>
            <li><a href="${artURL}" title="Read Open Access Article">${article.second}</a></li>
        </#list>
        <@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="comment"/>
        <#if mostViewedComment??>
            <li class="more">${mostViewedComment}</li>
        </#if>
    </ul>
<#else>
    <p>Most viewed article information is currently not available. Please check back later.</p>
</#if>

