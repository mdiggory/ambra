<#--
  $HeadURL::                                                                            $
  Id: newUser.ftl 5405 2008-04-10 16:28:43Z alex $
  
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
<!-- begin : main content wrapper -->
<div id="content"  class="static">
<h2>${freemarker_config.orgName} Profiles: Create a Profile</h2>
	<p><strong>Create or Update Your Profile</strong></p>
	<p>Fields marked with an <span class="required">*</span> are required. </p>

<@s.form name="createNewUserForm" action="createNewUser" namespace="/user" method="post" title="User Information Form" cssClass="ambra-form">

<fieldset>
  <legend>Your Profile</legend>
  <ol>
    <li><label for="email">Email address</label>
      ${email}
    </li>
      <@s.textfield name="displayName" label="Username" required="true" tabindex="101" after="Your user name will appear publicly"/>
			<li>
				<ol>
          <@s.textfield name="givenNames" label="First/Given Name" required="true" tabindex="102" />
          <@s.textfield name="surnames" label="Last/Family Name" required="true" tabindex="103"/>
				</ol>
			</li>
		</ol>
	</fieldset>
	<fieldset>
	<legend>Your Extended Profile</legend>
		<ol>
			<li>
        <@s.textarea name="postalAddress" label="Address" cssClass="long-input"  rows="5" cols="50" tabindex="106" />
				<ol>
          <@s.textfield name="city" label="City" required="true" tabindex="107"/>
          <@s.textfield name="country" label="Country" required="true" tabindex="111"/>
				</ol>
				<fieldset class="public-private">
				<legend>Choose display settings for your address </legend>
          <@s.radio name="extendedVisibility" label="Public" list="{'public'}" checked="true" tabindex="112" cssClass="radio" />
          <@s.radio name="extendedVisibility" label="Private" list="{'private'}" tabindex="113" cssClass="radio" />
				</fieldset>
			</li>
			<li class="form-last-item">
				<ol>
          <@s.action name="selectList" namespace="" id="selectList"/>
          <@s.select label="Organization Type" name="organizationType" value="organizationType"
          list="%{#selectList.allOrganizationTypes}" tabindex="114" />
          <@s.textfield name="organizationName" label="Organization Name" tabindex="115" />
				</ol>
				<ol>
            <@s.select label="Title" name="title" value="title"
            list="%{#selectList.allTitles}" tabindex="116" />

            <@s.select label="Position Type" name="positionType" value="positionType"
            list="%{#selectList.allPositionTypes}" tabindex="117" />
				</ol>
				<fieldset class="public-private">
				<legend>Choose display settings for your organization and title</legend>
          <@s.radio name="orgVisibility" label="Public" list="{'public'}" tabindex="118" cssClass="radio" />
          <@s.radio name="orgVisibility" label="Private" list="{'private'}" tabindex="119" cssClass="radio" />
				</fieldset>
		  </li>
		</ol>
	</fieldset>
	<fieldset>
		<legend>Optional Public Information</legend>
		<ol>
      <@s.textarea name="biographyText" label="About Me" rows="5" cols="50" tabindex="120"/>
      <@s.textfield name="researchAreasText" label="Research Areas" cssClass="long-input" tabindex="121" />
      <@s.textfield name="interestsText" label="Interests"  cssClass="long-input" tabindex="122" />
			<li>
        <@s.textfield name="homePage" label="Home page"  cssClass="long-input" tabindex="123" />
        <@s.textfield name="weblog" label="Weblog"  cssClass="long-input" tabindex="124" />
			</li>
		</ol>
    <@s.submit value="Submit" tabindex="125"/>
	</fieldset>

</@s.form>


</div>
<!-- end : main content wrapper -->
