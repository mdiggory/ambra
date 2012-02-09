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
<#if isEditedByAdmin?exists && isEditedByAdmin == true>
  <#assign editedByAdmin = true>
<#else>
  <#assign editedByAdmin = false>
</#if>

<#assign addressingUser = "Your">
<#if editedByAdmin>
  <#if displayName?exists>
     <#assign addressingUser = displayName +"'s" >
  </#if>
<br/>
<@s.url id="adminTopURL" action="adminTop" namespace="/admin" includeParams="none"/>
<@s.a href="%{adminTopURL}">back to admin console</@s.a>  
<br/>
<br/>
<@s.url id="editProfileByAdminURL" action="editProfileByAdmin" namespace="/admin" topazId="${topazId}" includeParams="none"/>
<@s.url id="editPreferencesByAdminURL" action="retrieveUserAlertsByAdmin" namespace="/admin" topazId="${topazId}" includeParams="none"/>
  Edit <@s.a href="%{editProfileByAdminURL}">profile</@s.a>
  or <@s.a href="%{editPreferencesByAdminURL}">alerts/preferences</@s.a> for <strong>${topazId}</strong>
<br/>

</#if>

