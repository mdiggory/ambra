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
<#--
 We only support US English.  This forces freemarker to stay in the correct mode
 regardless of what the user's browser sends us.
-->
<#setting locale="en_US">
<@s.url id="thisPageURL" includeParams="get" includeContext="true" encode="false"/>
<#-- make sure the 'thisPageURL' variable is in the imported namespace
  as well as the root-->
<#assign thisPageURL = "${thisPageURL}">
<#-- remove duplicate articleURI specification, e.g. /article/doi?articleURL=doi -->
<#if thisPageURL?matches(r"^(/.+)?/article.*/info(:|%3A)doi(/|%2F).+")>
  <#assign thisPage = thisPageURL?replace(r"\??articleURI=info%3Adoi%2F.{30}", "", "r")?replace("&amp;", "&")?url>
<#-- dont to anything if we're already on the feedbackCreate.action page -->
<#elseif thisPageURL?matches(r"^(/.+)?/feedbackCreate.action.*")>
  <#assign thisPage = "${freemarker_config.context}/feedbackCreate.action">
<#else>
  <#assign thisPage = thisPageURL?replace("&amp;", "&")?url>
</#if>