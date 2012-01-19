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
<#include "includes/globals.ftl">
<#if rating.body.commentTitle?exists>
  <#assign commentTitle = rating.body.commentTitle>
<#else>
  <#assign commentTitle = '"Rating has no Title"'>
</#if>
<#if rating.body.commentValue?exists>
  <#assign commentText = rating.body.commentValue>
<#else>
  <#assign commentText = '"Rating has no Text"'>
</#if>
<html>
  <head>
    <title>Ambra: Administration: Rating Details</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Rating Details</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <fieldset>
      <legend><b>Load Rating</b></legend>
      <@s.form name="manageRatingLoad" action="viewRating" namespace="/admin" method="get">
        <table>
          <tr><td><b>Rating ID</b></td><td><@s.textfield name="ratingId" value="${rating.id}" size="60"/></td></tr>
          <tr><td colspan="2"><@s.submit value="Load Rating" /></td></tr>
        </table>
      </@s.form>
    </fieldset>

    <@s.url id="ratingArticleURL" action="getArticleRatings" namespace="/rate" includeParams="none" articleURI="${rating.annotates}"></@s.url>
    
    <fieldset>
      <legend><b>Rating Details</b></legend>
      <table width="100%">
        <tr><td width="100px">&nbsp;</td><td/></tr>
        <tr><td>Id</td><td>${rating.body.id}</td></tr>
        <tr>
            <td>Article Id:</td>
            <td><@s.a href="${ratingArticleURL}" title="Article Ratings">${rating.annotates}</@s.a></td>
        </tr>
        <tr><td>Title</td><td>${commentTitle!}</td></tr>
        <tr><td>Created</td><td>${rating.created?datetime}</td></tr>
        <tr><td>Creator</td><td><a href="../user/showUser.action?userId=${rating.creator}">${rating.creator}</a></td></tr>
        <tr><td>Body</td><td>${commentText!}</td></tr>
        <tr>
          <td>Conflict of Interest</td>
          <td>${rating.cIStatement!"No Conflict of Interest Statement"}</td>
        </tr>
      </table>
    </fieldset>

  </body>
</html>

