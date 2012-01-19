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
<html>
  <head>
    <title>Ambra: Administration: Annotation Details</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Annotation Details</h1>

    <@s.url id="adminTop" namespace="/admin" action="adminTop"/>
    <p style="text-align: right">
      <@s.a href="${adminTop}">Admin Top</@s.a>
    </p>
    <hr/>

    <@messages />

    <fieldset>
      <legend><b>Annotation Details</b></legend>
	<table width="100%">
          <tr><td width="100px">&nbsp;</td><td/></tr>
          <tr><td nowrap="nowrap">Annotates</td><td><a href="${freemarker_config.context}/article/${annotation.annotates}">${annotation.annotates}</a></td></tr>
          <tr><td nowrap="nowrap">Creator</td><td><a href="${freemarker_config.context}/user/showUser.action?userId=${annotation.creator}">${annotation.creatorName}</a></td></tr>
          <tr><td nowrap="nowrap">Created</td><td>${annotation.createdAsDate?datetime}</td></tr>
          <tr><td nowrap="nowrap">Annotation Id</td><td><a href="${freemarker_config.context}/annotation/listThread.action?inReplyTo=${annotation.id}&root=${annotation.id}">${annotation.id}</a></td></tr>
          <tr>
            <td colspan="2">
              <fieldset><legend><b>Title</b></legend>${annotation.commentTitle}</fieldset>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <fieldset><legend><b>Content</b></legend>${annotation.commentWithUrlLinking}</fieldset>
            </td>
          </tr>
        </table>
    </fieldset>

  </body>
</html>
