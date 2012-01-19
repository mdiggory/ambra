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
<h1>Forgot Your Password?</h1>
  <p>We will email instructions on how to reset your password to the email address you used to set up your PLoS journal account.</p>
  <@s.form cssClass="ambra-form" method="post" name="forgotPasswordForm" id="forgotPasswordForm" action="forgotPasswordSubmit" title="Forgot Password Form">
		
	<fieldset>
		<ol>
    	<@s.textfield name="loginName" label="Email Address" id="email" tabindex="1" maxlength="256"/>
		</ol>
        <div class="btnwrap">
          <@s.submit id="submit" value="Submit" tabindex="2"/>
        </div>
	</fieldset>
	
	</@s.form>

	<ul>
	  <li>Already registered? <a href="${ambraUrl}${ambraContext}/profile">Login</a>.</li>
      <li><a href="${context}/register.action">Register for a New Account</a></li>
	  <li><a href="${context}/resendRegistration.action" title="Click here if you need to confirm your e-mail address">Resend e-mail address confirmation</a></li>
	</ul>
	
</div>
<!-- end : main contents -->
<#include "/global/global_bottom.ftl">
