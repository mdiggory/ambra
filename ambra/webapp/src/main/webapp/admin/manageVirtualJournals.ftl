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
    <title>Manage Virtual Journals</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Manage Virtual Journals</h1>
    <!-- Volume management menu -->
    <#include "includes/navigation.ftl">

    <@messages />

    <!-- Update Current Issue -->
    <@s.form method="post" namespace="/admin" action="manageVirtualJournals"
        name="manageVirtualJournals_${journal.key}" id="manageVirtualJournals_${journal.key}" >
    <@s.hidden name="command" value="UPDATE_ISSUE"/>
    <@s.hidden name="journalToModify" value="${journal.key}"/>
    
    <fieldset>
    <legend>Set Current Issue</legend>
    <table border="0" cellpadding="10" cellspacing="0">
      <tr>
        <th align="center">Issue (URI)</th>
          <td>
            <@s.textfield name="currentIssueURI" value="${journal.currentIssue!''}" size="50"/>
          </td>
        </tr>
      <tr>
    </table>
    <@s.submit value="Update"/>
    </@s.form>
    </fieldset>

    <p>

    <!-- create a Volume -->
    <fieldset>
     <legend>Create Volume</legend>
      <@s.form method="post" namespace="/admin"  action="manageVirtualJournals"
          name="createVolume" id="create_volume" >
      <@s.hidden name="command" value="CREATE_VOLUME"/>
      <@s.hidden name="journalToModify" value="${journal.key}"/>

      <table border="0" cellpadding="10" cellspacing="0">
        <tr>
          <th align="center">Volume (URI)</th>
            <td><@s.textfield name="volumeURI" size="50" required="true"/></td>
          </tr>
          <tr>
            <th align="center">Display Name</th>
            <td><@s.textfield name="displayName" size="50" required="true"/></td>
          </tr>
        </table>
        <@s.submit align="right" value="Create"/>
      </@s.form>
    </fieldset>
    <p>

    <!-- list Existing Volumes -->
    <fieldset>
      <legend>Existing Volumes</legend>

      <#-- Check to see if there are Volumes Associated with this Journal -->
      <#if (volumes?size > 0)>
        <@s.form method="post" namespace="/admin" action="manageVirtualJournals"
            name="removeVolumes" id="removeVolumes" >
        <@s.hidden name="command" value="REMOVE_VOLUMES"/>

        <table border="1" cellpadding="10" cellspacing="0">
          <tr>
            <th>Delete</th>
            <th>Display Name</th>
            <th>URI</th>
            <th>Update</th>
          </tr>
          <#list volumes as v>
          <tr>
            <td align="center">
              <@s.checkbox name="volsToDelete" fieldValue="${v.id}"/>
            </td>
            <td>
              ${v.displayName!''}
            </td>
            <td>
              ${v.id}
            </td>
            <td>
              <@s.url namespace="/admin" action="volumeManagement" volumeURI="${v.id}"
                 id="volumeMangement" />
              <@s.a href="${volumeMangement}">Update</@s.a>
            </td>
           </tr>
         </#list>
        </table>
        <@s.submit value="Delete Selected Volumes"/>
        </@s.form>
      <#else>
        <strong>There are no volumes currently associated with ${journal.key}.</strong>
      </#if>
    </fieldset>
  </body>
</html>
