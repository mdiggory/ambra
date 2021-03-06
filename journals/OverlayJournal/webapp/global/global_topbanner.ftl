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

<#-- depending on the current page, set banner zones -->

<#assign topLeft = 14>
<#assign topRight = 16>

<#if pgURL?contains('browse.action')>
	<#if pgURL?contains('field=date')>
		<#assign topRight = 170>
	<#else>
		<#assign topRight = 229>
	</#if>
<#elseif pgURL?contains('browseIssue.action') || pgURL?contains('browseVolume.action')>
	<#assign topRight = 169>
<#elseif pgURL?contains('advancedSearch.action') || pgURL?contains('simpleSearch.action')>
	<#assign topLeft = 234>
	<#assign topRight = 235>
<#elseif pgURL?contains('article')>
	<#assign topLeft = 100>
	<#assign topRight = 101>
</#if>

<!-- begin : left banner slot -->
<div class="left">
  <img src="${freemarker_config.context}/images/adBanner_placeholder_468x60.png"/>
</div>
<!-- end : left banner slot -->
<!-- begin : right banner slot -->
<div class="right">
  <img src="${freemarker_config.context}/images/adBanner_placeholder_468x60.png"/>
</div>
<!-- end : right banner slot -->

