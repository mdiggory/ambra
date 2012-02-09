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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:foaf="http://xmlns.com/foaf/0.1/"
      xmlns:dc="http://purl.org/dc/terms/"
      xmlns:doi="http://dx.doi.org/"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema-datatypes#"
      lang="en" xml:lang="en">
<head>
<#include "/global/global_head.ftl">
</head>
<body>

<!-- begin : container -->
<div id="container">
  <!-- begin : top banner external ad space -->
  <div id="topBanner">
    <#include "global_topbanner.ftl">
  </div>
  <!-- end : top banner external ad space -->

  <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
  <!-- begin : header -->
  <div id="hdr">
  <#else>
  <!-- begin : header -->
  <div id="hdr" class="login">
  </#if>

  <#include "/global/global_header.ftl">
  <!-- begin : navigation -->
  <#include "global_navigation.ftl">
  <!-- end : navigation -->
  </div>
  <!-- end : header --> 
