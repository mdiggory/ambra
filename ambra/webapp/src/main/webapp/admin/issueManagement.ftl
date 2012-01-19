<#--
 $$HeadURL:: $
 $$Id$

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
    <title>Issue Management</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Issue Management</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <!-- Update a Issue -->
    <fieldset>
     <legend>Update Issue</legend>
      <@s.form method="post" namespace="/admin" action="issueManagement"
          id="update_issue" name="updateIssue">
      <@s.hidden name="command" value="UPDATE_ISSUE"/>
      <@s.hidden name="volumeURI" value="${volumeURI!''}"/>
      <table border="0" cellpadding="10" cellspacing="0">
        <tr>
          <th align="center">Issue (URI)</th>
            <td><@s.textfield name="issueURI" value="${issue.id}" size="50" required="true" /></td>
          </tr>
          <tr>
            <th align="center">Display Name</th>
            <td>
              <@s.textfield name="displayName" value="${issue.displayName}" size="50"/>
            </td>
          </tr>
          <tr>
            <th align="center">Image Article (URI)</th>
            <td>
              <@s.textfield name="imageURI" value="${issue.image!''}" size="50" />
            </td>
          </tr>
          <tr>
            <th align="center">Article URI List
                <br><kbd>(Manual ordering changes only)</kbd></br>
            </th>
            <td>
                <@s.textfield name="articleListCSV" value="${articleOrderCSV}" size="100"/>
            </td>
          </tr>
          <tr>
            <th align="center">Manual Order Enabled</th>
            <td align="left">
              <#if issue.respectOrder>
                <@s.checkbox name="respectOrder" value="true" fieldValue="true"/>
              <#else>
                <@s.checkbox name="respectOrder" value="false" fieldValue="true"/>
              </#if>
            </td>
          </tr>
        </table>
        <@s.submit align="right" value="Update"/>
      </@s.form>
    </fieldset>

    <!-- Add an Article -->
    <fieldset>
     <legend>Add Articles</legend>
      <@s.form method="post" namespace="/admin" action="issueManagement"
          name="addArticle" id="add_article">
      <@s.hidden name="command" value="ADD_ARTICLE"/>
      <@s.hidden name="issueURI" value="${issue.id}"/>
      <@s.hidden name="volumeURI" value="${volumeURI!''}"/>
      <table border="0" cellpadding="10" cellspacing="0">
        <tr>
          <th align="center">Article (URIs)</th>
            <td><@s.textfield name="articleURI" size="50" /></td>
          </tr>
        <tr>
          <td colspan="2">
            <kbd>(A comma separated list of article URIs can be used for multiple entries.)</kbd>
          </td>
        </tr>
        </table>
        <@s.submit align="right" value="Add"/>
      </@s.form>
    </fieldset>

    <!-- list Existing Issues For this Volume-->
    <fieldset>
      <legend>Articles in Issue</legend>

      <#if (articleGrps?size > 0)>
      <@s.form  method="post" namespace="/admin" action="issueManagement"
          name="removeArticles" id="removeArticles">
      <@s.hidden name="command" value="REMOVE_ARTICLES"/>
      <@s.hidden name="issueURI" value="${issue.id}"/>
      <@s.hidden name="volumeURI" value="${volumeURI!''}"/>
      <#list articleGrps as grp>
      <b>${grp.heading}</b>
      <table border="1" cellpadding="10" cellspacing="0">
        <tr>
            <th>Delete</th>
            <th>Article URI</th>
            <th>Title</th>
        </tr>
        <#list grp.articles as a>
        <@s.url id="articleURL" includeParams="none" namespace="/article" action="fetchArticle"
                 articleURI="${a.id}"/>
        <tr>
          <td align="center">
              <@s.checkbox name="articlesToRemove" fieldValue="${a.id}"/>
          </td>
          <td>
             ${a.id}
          </td>
          <td>
             <a target="_article" href="${articleURL}">${a.title}</a>
          </td>
         </tr>
         </#list>
       </table>
       </#list>
         <b>Orphaned Articles</b>
         <table border="1" cellpadding="10" cellspacing="0">
           <tr>
             <td colspan="2">
               <ul>
               <li><kbd>Articles with types no longer in the configuration file.</kbd></li>
               <li><kbd>Incorrect URIs not associated with an article.</kbd></li>
               </ul>
             </td>
           </tr>
           <tr>
             <th>Delete</th>
             <th>Article URI</th>
           </tr>
           <#list orphans as orphan>
           <tr>
             <td align="center">
                <@s.checkbox name="articlesToRemove" fieldValue="${orphan}"/>
             </td>
             <td>
               ${orphan}
             </td>
            </tr>
           </#list>
         </table>
      <@s.submit value="Remove Selected Articles"/>
      </@s.form>
    <#else>
       <strong>There are no articles associated with this issue.</strong>
    </#if>
    </fieldset>
   </body>
</html>