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
<#include "../admin/includes/globals.ftl">
<html>
  <head>
    <title>Ambra: Administration: Manage Users</title>
    <#include "../admin/includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Manage Users</h1>
    <#include "../admin/includes/navigation.ftl">
    
    <@messages />

    <p>
      <fieldset>
          <legend><b>Find User by Authorization Id</b></legend>
          <@s.form name="findUserByUserIdForm" action="findUserByAuthId" namespace="/admin" method="post">
            <@s.textfield name="authId" label="Auth Id" required="true"/>
            <@s.submit value="Find Auth Id" />
          </@s.form>
      </fieldset>
    </p>

    <p>
      <fieldset>
          <legend><b>Find User by Account Id</b></legend>
          <@s.form name="findUserByUserIdForm" action="findUserByAccountId" namespace="/admin" method="post">
            <@s.textfield name="accountId" label="Account Id" required="true"/>
            <@s.submit value="Find Account Id" />
          </@s.form>
      </fieldset>
    </p>

    <p>
      <fieldset>
          <legend><b>Find User by Email</b></legend>
          <@s.form name="findUserByEmailAddressForm" action="findUserByEmailAddress" namespace="/admin" method="post">
            <@s.textfield name="emailAddress" label="Email Address" required="true"/>
            <@s.submit value="Find User Email" />
          </@s.form>
      </fieldset>
    </p>

    <p>
      <fieldset>
          <legend><b>Find User by Name</b></legend>
          <@s.form name="findUserByNameForm" action="findUserByName" namespace="/admin" method="post">
            <@s.textfield name="name" label="User Name" required="true"/>
            <@s.submit value="Find User Name" />
          </@s.form>
      </fieldset>
    </p>

    <#if topazUserIdList?exists>
      <p>
        <fieldset>
          <legend><b>Edit User Profiles</b></legend>
          <ul>
            <#list topazUserIdList as topazId>
              <@s.url id="editProfileByAdminURL" action="editProfileByAdmin" namespace="/admin" topazId="${topazId}" includeParams="none"/>
              <@s.url id="editPreferencesByAdminURL" action="retrieveUserAlertsByAdmin" namespace="/admin" topazId="${topazId}" includeParams="none"/>
              <li>
                Edit <@s.a href="%{editProfileByAdminURL}">profile</@s.a> for ${topazId}
              </li>
            </#list>
          </ul>
        </fieldset>
      </p>
    </#if>

    <p>
      <fieldset>
        <legend><b>Assign Admin Role To User</b></legend>
        <@s.form name="assignAdminRoleForm" action="assignAdminRole" namespace="/admin" method="post">
          <@s.textfield name="topazId" label="Id" required="true"/>
          &nbsp;
          <@s.submit value="Assign Admin Role"/>
        </@s.form>
      </fieldset>
    </p>
  </body>
</html>
