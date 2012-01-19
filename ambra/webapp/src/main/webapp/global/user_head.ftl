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
<#if Request[freemarker_config.journalContextAttributeKey]?exists>
	<#assign journalContext = Request[freemarker_config.journalContextAttributeKey].journal>
<#else>
	<#assign journalContext = "">
</#if>
<title>Ambra</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" href="${freemarker_config.context}/images/favicon.ico" type="image/x-icon" />
<@s.url id="homeURL" includeParams="none" includeContext="true" namespace="/" action="home"/>
<link rel="home" title="home" href="${homeURL}" />
<#include "user_css.ftl">

<meta name="keywords" content="Ambra, RDF" />

