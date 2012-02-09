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

<%@ page session="false" %>

<%
  String serviceId = (String) request.getAttribute("serviceId");
%>

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

<script>
  window.location.href="<%= serviceId %>";
</script>

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
  <noscript>
    <p>Click <a href="<%= serviceId %>">here</a> to access the service you requested.</p>
  </noscript>
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
