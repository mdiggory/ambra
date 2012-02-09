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
<#assign hasFieldErrors = fieldErrors?exists && fieldErrors[parameters.name]?exists/>

<#if hasFieldErrors>
<#list fieldErrors[parameters.name] as error>
<#if parameters.id?exists>
 errorFor="${parameters.id}"<#rt/>
</#if>
>
</#list>
</#if>
<#if parameters.labelposition?default("") == 'top'>
<#if parameters.label?exists> <label<#t/>
<#if parameters.id?exists>
 for="${parameters.id?html}"<#rt/>
</#if>
>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") != 'right'>
        <span class="required">*</span><#t/>
</#if>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") == 'right'>
 <span class="required">*</span><#t/>
</#if>
<#t/>
<#if parameters.tooltip?exists>
    <img src='<@s.url value="/struts/tooltip/tooltip.gif" encode='false' includeParams='none' />' alt="${parameters.tooltip}" title="${parameters.tooltip}" onmouseover="return escape('${parameters.tooltip?js_string}');" />
</#if>
</#if>

        <#include "/${parameters.templateDir}/${parameters.theme}/checkbox-core.ftl" />
<#if parameters.label?exists>
${parameters.label?html}<#t/>
</label><#t/>
</#if>

<#else>

<#if parameters.labelposition?default("") == 'left'>

<#if parameters.label?exists> <label<#t/>
<#if parameters.id?exists>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") != 'right'>
        <span class="required">*</span><#t/>
</#if>
${parameters.label?html}<#t/>
<#if parameters.required?default(false) && parameters.requiredposition?default("right") == 'right'>
 <span class="required">*</span><#t/>
</#if>
<#t/>
<#if parameters.tooltip?exists>
    <img src='<@s.url value="/struts/tooltip/tooltip.gif" encode="false" includeParams='none'/>' alt="${parameters.tooltip}" title="${parameters.tooltip}" onmouseover="return escape('${parameters.tooltip?js_string}');" />
</#if>
</label><#t/>
</#if>
</#if>

<#if parameters.labelposition?default("") == 'right'>
    <#if parameters.required?default(false)>
        <span class="required">*</span><#t/>
    </#if>
    <#if parameters.tooltip?exists>
        <img src='<@s.url value="/struts/tooltip/tooltip.gif" encode="false" includeParams='none'/>' alt="${parameters.tooltip}" title="${parameters.tooltip}" onmouseover="return escape('${parameters.tooltip?js_string}');" />
    </#if>
</#if>

<#if parameters.labelposition?default("") != 'top'>
                	<#include "/${parameters.templateDir}/${parameters.theme}/checkbox-core.ftl" />
</#if>                    

<#if parameters.labelposition?default("") != 'top' && parameters.labelposition?default("") != 'left'>
<#if parameters.label?exists> <label<#t/>
<#if parameters.id?exists>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="checkboxErrorLabel"<#rt/>
<#else>
 class="checkboxLabel"<#rt/>
</#if>
>${parameters.label?html}</label><#rt/>
</#if>
</#if>
</#if>
