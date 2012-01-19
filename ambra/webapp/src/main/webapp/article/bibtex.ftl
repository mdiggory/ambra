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
<#assign authorListFormatted = "">
<#list authorList as author>
  <#assign authorListFormatted = authorListFormatted + author.surnames>
  <#assign authorListFormatted = authorListFormatted + ", ">
  <#if author.suffix?exists>
    <#assign authorListFormatted = authorListFormatted + author.suffix>
    <#assign authorListFormatted = authorListFormatted + ", ">
  </#if>
  <#assign authorListFormatted = authorListFormatted + author.givenNames!"">
  <#if author_has_next><#assign authorListFormatted = authorListFormatted + " AND "></#if>
</#list>
<#if collaborativeAuthors?has_content>
  <#assign authorListFormatted = authorListFormatted + " AND ">
  <#list collaborativeAuthors as collab>
    <#assign authorListFormatted = authorListFormatted + collab>
    <#if collab_has_next><#assign authorListFormatted = authorListFormatted + ", "></#if>
  </#list>
</#if>

@article{${doi},
    author = {${authorListFormatted}},
    journal = {${journal}},
    publisher = {${publisherName}},
    title = {${title}},
    year = {${year!"0000"}},
    month = {${month!}},
    volume = {${volume!}},
    url = {${url!}},
    pages = {${eLocationId!}},
    abstract = {${summary!}},
    number = {${issue!}},
    doi = {${doi}}
}        




