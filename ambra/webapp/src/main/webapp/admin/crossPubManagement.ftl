<#--
 $HeadURL$
 $Id$

 Copyright (c) 2006-2010 by Public Library of Science
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
    <title>Manage Cross Published Articles</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Manage Cross Published Articles</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <!-- XPub and article URI -->
    <fieldset>
    <legend>Cross Publish Article(s)</legend>
    <@s.form method="post" namespace="/admin" action="crossPubManagement"
        name="crossPubManagement_${journal.key}" id="crossPubManagement_${journal.key}" >
    <@s.hidden name="command" value="ADD_ARTICLES"/>
    <table border="0" cellpadding="10" cellspacing="0">
      <tr>
        <th align="center">Article (URI)</th>
          <td>
            <@s.textfield name="articlesToAdd"  size="50"/>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <small>A comma separated list of article URIs can be used for multiple entries.</small>
          </td>
        </tr>
      <tr>
    </table>
    <@s.submit value="Add"/>
    </@s.form>
    </fieldset>

      <!-- list Cross Published Articles -->
    <fieldset>
      <legend>Cross Published Articles</legend>

      <#if (journal.simpleCollection?size > 0)>
      <@s.form method="post" namespace="/admin" action="crossPubManagement"
          name="crossPubManagement_${journal.key}" id="crossPubManagement_${journal.key}" >
      <@s.hidden name="command" value="REMOVE_ARTICLES"/>
      <table border="1" cellpadding="10" cellspacing="0">
        <tr>
            <th>Remove</th>
            <th>Article URI</th>
        </tr>
        <#list journal.simpleCollection as uri>
        <@s.url id="articleURL" includeParams="none" namespace="/article" action="fetchArticle"
            articleURI="${uri}"/>
        <tr>
          <td align="center">
              <@s.checkbox name="articlesToRemove" fieldValue="${uri}"/>
          </td>
          <td>
             <a target="_article" href="${articleURL}">${uri}</a>
          </td>
         </tr>
         </#list>
      </table>
      <@s.submit value="Remove from Journal"/>
      </@s.form>
      <#else>
        There are currently no cross published articles associated with this journal.
      </#if>
    </fieldset>
  </body>
</html>
