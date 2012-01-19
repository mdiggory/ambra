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

<!-- begin : main content -->
<div id="content">
<h1>Change Your Password</h1>
	<p>Fields marked with <span class="required">*</span> are required. </p>
  <@s.form cssClass="ambra-form" method="post" name="changePasswordForm" id="changePasswordForm" action="changePasswordSubmit" title="Change Password Form">

	<fieldset>
		<ol class="field-list">
    	<@s.textfield name="loginName" label="E-mail " required="true" id="email" tabindex="1" maxlength="256"/>
      <@s.password name="oldPassword" label="Old password " required="true" id="oldPassword" tabindex="2" maxlength="255"/>
      <@s.password name="newPassword1" label="New password " required="true" id="newPassword1" tabindex="3" maxlength="255" after=" (Password must be at least 6 characters)"/>
      <@s.password name="newPassword2" label="Please re-type your new password " required="true" id="newPassword2" tabindex="4" maxlength="255" />
		</ol>
	<div class="btnwrap">
	  <@s.submit id="submit" value="Submit" tabindex="5"/>
	</div>
	</fieldset>
	
	</@s.form>

</div>
<!-- end : main contents -->

<#include "/global/global_bottom.ftl">