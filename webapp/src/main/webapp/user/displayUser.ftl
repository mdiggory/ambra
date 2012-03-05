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
<#assign defaultValue = "<em>No answer</em>">
<#if surnames?has_content>
	<#assign surnames = surnames>
<#else>
	<#assign surnames = defaultValue>
</#if>
<#if givenNames?has_content>
	<#assign givenNames = givenNames>
<#else>
	<#assign givenNames = defaultValue>
</#if>
<#if city?has_content>
	<#assign city = city>
<#else>
	<#assign city = defaultValue>
</#if>
<#if country?has_content>
	<#assign country = country>
<#else>
	<#assign country = "">
</#if>
<#if postalAddress?has_content>
	<#assign postalAddress = postalAddress>
<#else>
	<#assign postalAddress = defaultValue>
</#if>
<#if title?has_content>
	<#assign title = title>
<#else>
	<#assign title = defaultValue>
</#if>
<#if organizationName?has_content>
	<#assign orgName = organizationName>
<#else>
	<#assign orgName = defaultValue>
</#if>
<#if organizationType?has_content>
	<#assign orgType = organizationType>
<#else>
	<#assign orgType = defaultValue>
</#if>
<#if positionType?has_content>
	<#assign positionType = positionType>
<#else>
	<#assign positionType = defaultValue>
</#if>

<#if biographyText?has_content>
	<#assign bio = biographyText>
<#else>
	<#assign bio = defaultValue>
</#if>
<#if researchAreasText?has_content>
	<#assign research = researchAreasText>
<#else>
	<#assign research = defaultValue>
</#if>
<#if interestsText?has_content>
	<#assign interests = interestsText>
<#else>
	<#assign interests = defaultValue>
</#if>
<#if homePage?has_content>
	<#assign homePageText = homePage>
<#else>
	<#assign homePageText = defaultValue>
</#if>
<#if weblog?has_content>
	<#assign weblogText = weblog>
<#else>
	<#assign weblogText = defaultValue>
</#if>



<div id="content" class="profile">

<img src="${freemarker_config.context}/images/avatar.png" />
<h1>${displayName!}</h1>

<ol>
    <li><span class="heading">Title</span><span class="text">${title}</span></li>
    <li><span class="heading">Full Name</span><span class="text">${givenNames} ${surnames}</span></li>

<li><span class="heading">Location</span><span class="text">${city}<#if country == ""><#else>, ${country}</#if></span></li>

<li><span class="heading">Organization Address</span><span class="text">${postalAddress}</span></li>

<li><span class="heading">Organization Type</span><span class="text">${orgType}</span></li>
<li><span class="heading">Organization Name</span><span class="text">${orgName}</span></li>

<li><span class="heading">Your Role</span><span class="text">${positionType}</span></li>

<li><span class="heading">Short Biography</span><span class="text">${bio}</span></li>

<li><span class="heading">Research Areas</span><span class="text">${research}</span></li>

<li><span class="heading">Interests</span><span class="text">${interests}</span></li>

<li><span class="heading">Website URL</span>
<#if homePage?has_content>
    <@s.url id="homePageLink" value="${homePageText}"/>
    <span class="text"><a href="${homePageLink}">${homePageText}</a></span>
<#else>
    <span class="text">${homePageText}</span>
</#if>
</li>

<li><span class="heading">Blog URL</span>
<#if weblog?has_content>
    <@s.url id="weblogLink" value="${weblogText}"/>
    <span class="text"><a href="${weblogLink}">${weblogText}</a></span>
<#else>
    <span class="text">${weblogText}</span>
</#if>
</li>
</ol>

</div>
