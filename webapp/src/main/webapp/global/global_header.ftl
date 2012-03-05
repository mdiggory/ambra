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
  <#import "global_variables.ftl" as global>
  <!-- begin : logo -->
  <div id="logo" title="${freemarker_config.getDisplayName(journalContext)}"><a href="${homeURL}" title="${freemarker_config.getDisplayName(journalContext)}"><span>${freemarker_config.getDisplayName(journalContext)}</span></a></div>
  <!-- end : logo -->
  <!-- begin : user controls -->



  <@s.url id="feedbackURL" includeParams="none" namespace="/" action="feedbackCreate" page="${global.thisPage}" encode="false"/>

  <#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
  <div id="user">
    <div>
    <@s.url id="editProfileURL" includeParams="none" namespace="/user/secure" action="editProfile" tabId="preferences"/>
        <#if Session[freemarker_config.userAttributeKey].displayName?has_content>
          <p>Welcome, <!--<a href="${freemarker_config.context}/user/showUser.action?userId=${Session[freemarker_config.userAttributeKey].ID}" title="You are logged in as ${Session[freemarker_config.userAttributeKey].displayName}">--><strong>${Session[freemarker_config.userAttributeKey].displayName}</strong></a>! | </p>
        </#if>
        <ul>
          <@s.url id="logoutURL" includeParams="none" namespace="/user/secure" action="secureRedirect" goTo="${freemarker_config.casLogoutURL}?service=${Request[freemarker_config.journalContextAttributeKey].baseUrl}/logout.action"/>
          <li><a href="${editProfileURL}" title="Edit your account preferences and alert settings">Preferences</a> | </li>
          <li><a href="${logoutURL}" title="Logout">Logout</a></li>
        </ul>
    </div>
  </div>

  <#else>

  <div id="user">
    <div>
      <ul>
        <@s.url id="loginURL" includeParams="none" namespace="/user/secure" action="secureRedirect" goTo="${global.thisPage}"/>
        <li><a href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}" class="feedback"><strong>Login</strong></a> | </li>
        <li><a href="${freemarker_config.registrationURL}">Create Account</a> | </li>
        <li class="feedback"><a href="${feedbackURL}" title="Send us your feedback">Feedback</a></li>
      </ul>
    </div>
  </div>

  </#if>

  <!-- end : user controls -->
  <!-- begin search links -->
  <ul id="links"><@s.url id="browseURL" includeParams="none" namespace="/article" action="browse"/><li class="browse"><a href="${browseURL}?field=date" title="Browse Articles">Browse</a></li><@s.url id="rssURL" includeParams="none" namespace="/static" action="rssFeeds"/><li class="rss"><a href="${rssURL}" title="RSS Feeds">RSS</a></li></ul>
  <!-- end : search links -->
  <!-- begin : dashboard -->
  <div id="db">
    <@s.url id="searchURL" includeParams="none" namespace="/search" action="simpleSearch" />
    <form name="searchForm" action="${searchURL}" method="get">
      <input type="hidden" name="from" value="globalSimpleSearch"/>
      <input type="hidden" name="filterJournals" value="${currentJournal}">
      <fieldset>
        <legend>Search</legend>
        <label for="search">Search</label>
        <div class="wrap"><input id="search" type="text" name="query" value="Search articles..." onfocus="if(this.value=='Search articles...')value='';" onblur="if(this.value=='')value='Search articles...';" class="searchField" alt="Search articles..."/></div>
        <input src="${freemarker_config.context}/images/search_btn1.gif" onclick="submit();" value="ftsearch" alt="SEARCH" tabindex="3" class="button" type="image" />
      </fieldset>
    </form>
    <@s.url action="advancedSearch" namespace="/search" includeParams="none" id="advancedSearch"/>
    <form name="gasf" action="${advancedSearch}" method="get">
      <@s.hidden name="noSearchFlag" value="true" />
      <@s.hidden name="query" value="" />
    </form>
    <a id="advSearch" href="#" onclick="if(document.searchForm.query.value!='Search articles...')document.gasf.query.value=document.searchForm.query.value;document.gasf.submit();return false;">Advanced Search</a>
  </div>
  <!-- end : dashboard -->

