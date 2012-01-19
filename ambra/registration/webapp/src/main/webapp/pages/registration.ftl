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
<h1>Sign Up for an Ambra Profile</h1>
    <p>Fields marked with <span class="required">*</span> are required.</p>

  <@s.form cssClass="ambra-form" method="post" name="registrationFormPart1" action="registerSubmit" title="Registration Form">
  <fieldset>
	<ol>
	<@s.textfield label="Your Email Address" name="loginName1" required=true tabindex="1" maxlength="256" after="<div class='note top'>A confirmation and instructions for completing your registration will be sent to this e-mail address.</div>" />
	<@s.password label="Password " name="password1" tabindex="2" required=true maxlength="255" after="<div class='note'>Must be at least 6 characters</div>"/>
	<@s.password label="Please re-type your password " name="password2" required=true tabindex="3" maxlength="128"/>
	</ol>
	<div class="btnwrap">
		<@s.submit value="Submit" tabindex="4"/>
	</div>
	</fieldset>
  </@s.form>
  <ul>
	  <li>Already registered? <a href="${ambraUrl}${ambraContext}/profile">Login</a>.</li>
      <li><a href="${context}/forgotPassword.action" title="Click here if you forgot your password">Forgotten Password?</a></li>
      <li><a href="${context}/resendRegistration.action" title="Click here if you need to confirm your e-mail address">Resend e-mail address confirmation</a></li>  
   </ul>
</div>
<!-- end : main contents -->

<#include "/global/global_bottom.ftl">
