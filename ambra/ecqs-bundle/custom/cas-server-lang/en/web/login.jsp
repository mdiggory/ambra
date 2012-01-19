<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
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
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Ambra</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<!-- global_css.ftl -->
<style type="text/css" media="all"> @import "css/user_screen.css";</style>
<style type="text/css" media="all"> @import "css/user_forms.css";</style>

<!--
<rdf:RDF xmlns="http://web.resource.org/cc/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
<Work rdf:about="">
   <license rdf:resource="http://creativecommons.org/licenses/by/2.5/" />
</Work>
<License rdf:about="http://creativecommons.org/licenses/by/2.5/">
   <permits rdf:resource="http://web.resource.org/cc/Reproduction" />
   <permits rdf:resource="http://web.resource.org/cc/Distribution" />
   <requires rdf:resource="http://web.resource.org/cc/Notice" />
   <requires rdf:resource="http://web.resource.org/cc/Attribution" />
   <permits rdf:resource="http://web.resource.org/cc/DerivativeWorks" />
</License>
</rdf:RDF>
-->

<script type="text/javascript" src="../ambra-registration/javascript/config_default.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/dojo/dojo/dojo.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/init_global.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/init_navigation.js"></script>
    
</head>
<body>

<!-- begin : container -->
<div id="container">
	
<!-- begin : header -->
<div id="hdr">
	<div id="logo"><a href="" title="Ambra"><span>Ambra</span></a></div>
	<div id="tagline"><span>An Open Source end-to-end electronic publishing system</span></div>
</div>
<!-- end : header -->

<!-- begin : navigation -->
<ul id="nav">
	<li class="none"><a href="" title="Home Page" tabindex="101">Home</a></li>
	<li class="none"><a href="" title="My Profile" tabindex="102">My Profile</a></li>
	<li class="journalnav"><a href="" title="About" class="drop" tabindex="111">About</a>
		<ul>
			<li><a href="" title="Open Access">Open Access</a></li>
			<li><a href="" title="Blog">Blog</a></li>
		</ul>
	</li>
	<li class="journalnav"><a href="" title="Journals" class="drop" tabindex="109">Journals</a>
		<ul>
			<li><a href="" title="Overlay">Overlay Journal</a></li>
		</ul>
	</li>
</ul>
<!-- end : navigation -->

<div id="content">
  <h1>Login to Your Ambra Account</h1>
  <p>Fields marked with <span class="required">*</span> are required.</p>
  <form method="post" name="login_form" class="ambra-form">
    <fieldset>
      <legend>Login</legend>
      <% boolean loginError = (request.getAttribute("edu.yale.its.tp.cas.badUsernameOrPassword") != null);%>
      <input type="hidden" name="lt" value="<%= request.getAttribute("edu.yale.its.tp.cas.lt") %>" />
      <ol>
        <% if (loginError) { %>
          <li class="form-error"><p class="required">Please correct the error below.</p></li>
        <% } else if (request.getAttribute("edu.yale.its.tp.cas.service") == null) { %>
          <li><em>You may login now in order to access protected services later.</em></li>
        <% } else if (request.getAttribute("edu.yale.its.tp.cas.badLoginTicket") != null) { %>
          <li><em>Bad Login Ticket: Please check to make sure you are coming from a PLoS site.</em></li>
        <% } else { %>                        
          <!-- <em>You have requested accests to a site that requires authentication.</em> -->               
        <% } %>
        <li<%=(loginError?" class=form-error":"")%>>
          <label for="username">E-mail</label>
          <input type="text" name="username" tabindex="1"/>
          <%=loginError?" Please enter a valid e-mail address and password":""%>
        </li>
        <li>
          <label for="password">Password</label>
          <input type="password" name="password" tabindex="2"/>
        </li>
      </ol>
      <div class="btnwrap">
        <input type="submit" name="login" value="Login" tabindex="3"/>
      </div>
    </fieldset>
  </form>
  <ul>
    <li><a href="/ambra-registration/register.action" tabindex="12">Register for a 
    New Account</a></li>
    <li><a href="/ambra-registration/forgotPassword.action" 
      title="Click here if you forgot your password" tabindex="11">Forgotten Password?</a></li>
	  <li><a href="/ambra-registration/resendRegistration.action" 
      title="Click here if you need to confirm your e-mail address" tabindex="11">Resend e-mail
      address confirmation</a></li>
	</ul>
</div>
</div>
<!-- end : container -->	

<!-- begin : footer -->
<div id="ftr">
  <ul>
    <li><a href="" title="Privacy Statement" tabindex="501">Privacy Statement</a></li>
    <li><a href="" title="Terms of Use" tabindex="502"></a></li>
  </ul>
</div>
<!-- end : footer -->
</body>
</html>
