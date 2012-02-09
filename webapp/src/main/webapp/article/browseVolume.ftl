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
<div id="content" class="archive">
  <h1>Journal Archive</h1>
  <p>This archive contains links to the full-text of all issues of 
  <em>${freemarker_config.getDisplayName(journalContext)}.</em></p>
  
  <div id="browse-results" class="tundra">
    <#if currentIssue??>
      <h2>Current Issue</h2>
      <div id="issueImage">
        <div id="thumbnail">

          <@s.url id="currentIssueURL" action="browseIssue" namespace="/article"
                  issue="${currentIssue.id}" includeParams="none"/>
  <#if currentIssue.imageArticle?exists>
          <@s.url id="currentIssueImgURL" action="fetchObject" namespace="/article"
                  uri="${currentIssue.imageArticle}.g001" representation="PNG_S" includeParams="none"/>
          <a href="${currentIssueURL}"><img alt="Issue Image" src="${currentIssueImgURL}" width="120" height="120" /></a>
  </#if>
          <a href="${currentIssueURL}">${currentIssue.displayName}</a>
          <p>${currentVolume.displayName}</p>
        </div>
        <h3>About This Image</h3>
        ${currentIssueDescription}
        <div class="clearer">&nbsp;</div>
      </div>
    </#if>
    
<#if volumeInfos?exists>
    <h2>All Issues</h2>
    <div id="jatw" dojoType="dijit.layout.TabContainer" style="width:892px;height:370px;margin-top:30px;visibility:hidden;">
      <#list volumeInfos as volumeInfo>
      <div dojoType="dijit.layout.ContentPane" title="${volumeInfo.displayName}" id="${volumeInfo.displayName}">
        <#list volumeInfo.issueInfos as issueInfo>
        <@s.url id="issueURL" action="browseIssue" namespace="/article" issue="${issueInfo.id}" includeParams="none"/>
        <div class="thumbnail">
<#if issueInfo.imageArticle?exists>
          <@s.url id="issueImgURL" action="fetchObject" namespace="/article" 
                  uri="${issueInfo.imageArticle}.g001" representation="PNG_S" includeParams="none"/>
          <a href="${issueURL}"><img alt="Issue Image" src="${issueImgURL}" width="120" height="120"/></a>
</#if>
          <a href="${issueURL}">${issueInfo.displayName}</a>
        </div>
        </#list>
        <div class="clearer">&nbsp;</div>
      </div>
      </#list>
    </div><!-- end: ambraTabsContainer -->
</#if><!-- end : volumeInfos?exists -->
  </div><!-- end: browse-results -->
</div><!--content-->
