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
<#if Parameters.tabId?exists>
   <#assign tabId = Parameters.tabId>
<#else>
   <#assign tabId = "">
</#if>
<#if displayName?exists>
   <#assign username = displayName>
<#else>
   <#assign username = "">
</#if>


<div id="content">
	<h1>${freemarker_config.orgName} Profile: ${username}</h1>
	
	<div class="horizontalTabs">
		<ul id="tabsContainer">
		</ul>
		
		<div id="tabPaneSet" class="contentwrap">
		  <#if tabId == "alerts">
				<#include "alerts.ftl">
		  <#else>
				<#include "user.ftl">
			</#if>
		</div>
	</div>
	
</div>

<#include "/widget/loadingCycle.ftl">
