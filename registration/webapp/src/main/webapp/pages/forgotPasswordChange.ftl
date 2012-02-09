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
<#include "/global/global_config.ftl">
<#include "/global/global_top.ftl">

<#if Parameters.loginName?exists>
	<#assign thisLoginName = Parameters.loginName>
<#elseif loginName?exists>
	<#assign thisLoginName = loginName>
<#else>
	<#assign thisLoginName = "">
</#if>

<#if Parameters.resetPasswordToken?exists>
	<#assign thisPasswordToken = Parameters.resetPasswordToken>
<#elseif resetPasswordToken?exists>
	<#assign thisPasswordToken = resetPasswordToken>
<#else>
	<#assign thisPasswordToken = "">
</#if>

<!-- begin : main content -->
<div id="content">
<h1>Change Your Password</h1>
  <@s.form cssClass="ambra-form" method="post" name="changePasswordForm" id="changePasswordForm" action="forgotPasswordChangeSubmit" title="Change Password Form">
	  <@s.hidden name="loginName" value="${thisLoginName}"/>
    <@s.hidden name="resetPasswordToken" value="${thisPasswordToken}" />
	<fieldset>
		<ol class="field-list">
      <@s.password name="password1" label="New password " id="newPassword1" tabindex="1" maxlength="255" after=" (Password must be at least 6 characters)"/>
      <@s.password name="password2" label="Please re-type your new password " id="newPassword2" tabindex="2" maxlength="255" />
		</ol>
        <div class="btnwrap">
	        <@s.submit id="submit" value="Submit" tabindex="3"/>
        </div>
	</fieldset>
	
	</@s.form>

</div>
<!-- end : main contents -->

<#include "/global/global_bottom.ftl">