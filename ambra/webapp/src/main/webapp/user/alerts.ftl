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
<#include "initForEditedBy.ftl">

<#function isFound collection value>
  <#list collection as element>
    <#if element = value>
      <#return "true">
    </#if>
  </#list>
  <#return "false">
</#function>
<#if Parameters.tabId?exists>
   <#assign tabId = Parameters.tabId>
<#else>
   <#assign tabId = "">
</#if>

<#if editedByAdmin>
  <#assign actionValue="saveAlertsByAdmin"/>
  <#assign namespaceValue="/admin"/>
<#else>
  <#assign actionValue="saveUserAlerts"/>
  <#assign namespaceValue="/user/secure"/>
</#if>
  <@s.form action="${actionValue}" namespace="${namespaceValue}" method="post" cssClass="ambra-form" method="post" title="Alert Form" name="userAlerts">
  <fieldset id="alert-form">
		<legend><strong>Email Alerts</strong></legend>
		<ol>
        	<li>
                <span class="alerts-title">&nbsp;</span>
                <ol>
        		<li class="alerts-weekly">
        			<label for="checkAllWeekly">
        			<#if tabId?has_content>
        				<input type="checkbox" value="checkAllWeekly" name="checkAllWeekly" onfocus="ambra.horizontalTabs.setTempValue(this);" onclick="ambra.formUtil.selectAllCheckboxes(this, document.userAlerts.weeklyAlerts); ambra.horizontalTabs.checkValue(this);" /> Select All
        			<#else>
        				<input type="checkbox" value="checkAllWeekly" name="checkAllWeekly" onclick="ambra.formUtil.selectAllCheckboxes(this, document.userAlerts.weeklyAlerts);" /> Select All
        			</#if>
        			</label>
        		</li>
        		<li>
        			<label for="checkAllMonthly">
         			<#if tabId?has_content>
        				<input type="checkbox" value="checkAllMonthly" name="checkAllMonthly" onfocus="ambra.horizontalTabs.setTempValue(this);" onclick="ambra.formUtil.selectAllCheckboxes(this, document.userAlerts.monthlyAlerts); ambra.horizontalTabs.checkValue(this);" /> Select All
        			<#else>
        				<input type="checkbox" value="checkAllMonthly" name="checkAllMonthly" onclick="ambra.formUtil.selectAllCheckboxes(this, document.userAlerts.monthlyAlerts);" /> Select All
        			</#if>
        			</label>
        		</li>
        	</ol>
			</li>
      <#list userAlerts as ua>
        <li>
          <span class="alerts-title">${ua.name}</span>
          <ol>
            <li class="alerts-weekly">
              <#if ua.weeklyAvailable>
                <label for="${ua.key}">
				<#if tabId?has_content>
	              <@s.checkbox name="weeklyAlerts" onfocus="ambra.horizontalTabs.setTempValue(this);" onclick="ambra.horizontalTabs.checkValue(this); ambra.formUtil.selectCheckboxPerCollection(this.form.checkAllWeekly, this.form.weeklyAlerts);" onchange="ambra.horizontalTabs.checkValue(this);" fieldValue="${ua.key}" value="${isFound(weeklyAlerts, ua.key)}"/>
				<#else>
	              <@s.checkbox name="weeklyAlerts" onclick="ambra.formUtil.selectCheckboxPerCollection(this.form.checkAllWeekly, this.form.weeklyAlerts);" fieldValue="${ua.key}" value="${isFound(weeklyAlerts, ua.key)}"/>
				</#if>
                Weekly </label>
              </#if>
            </li>

            <li>
              <#if ua.monthlyAvailable>
                <label for="${ua.key}">
    			<#if tabId?has_content>
	              <@s.checkbox name="monthlyAlerts" onfocus="ambra.horizontalTabs.setTempValue(this);" onclick="ambra.horizontalTabs.checkValue(this); ambra.formUtil.selectCheckboxPerCollection(this.form.checkAllMonthly, this.form.monthlyAlerts);" onchange="ambra.horizontalTabs.checkValue(this);"  fieldValue="${ua.key}" value="${isFound(monthlyAlerts, ua.key)}"/>
    			<#else>
                  <@s.checkbox name="monthlyAlerts" onclick="ambra.formUtil.selectCheckboxPerCollection(this.form.checkAllMonthly, this.form.monthlyAlerts);"  fieldValue="${ua.key}" value="${isFound(monthlyAlerts, ua.key)}"/>
    			</#if>
                  Monthly </label>
              <#else>
              </#if>
            </li>
          </ol>
      </#list>
	          </li>

		</ol>
		<br clear="all" />

<#include "submit.ftl">

	</fieldset>
  </@s.form>


