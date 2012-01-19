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
    <title>Ambra: Administration: Manage Flags</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Edit Annotation</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <#if flaggedComments?has_content>
      <fieldset>
        <legend><b>Flagged Comments</b></legend>
        <@s.form name="manageFlagsAction" action="processFlags" method="post" namespace="/admin">
          <table width="100%">
            <tr><td><b>Time</b></td><td><b>Comment</b></td><td><b>By</b></td><td><b>Refers To</b></td><td><b>Reason</b></td><td><b>Action</b></td></tr>
            <tr><td colspan="6"><hr/></td></tr>
            <#list flaggedComments as flaggedComment>
              <#if flaggedComment.isAnnotation>
                <@s.url id="flagURL" namespace="/admin" action="manageAnnotationLoad" annotationId="${flaggedComment.target}"/>
                <#if flaggedComment.correction>
                  <#assign deleteLabel = "Delete Correction">
                <#else>
                  <#assign deleteLabel = "Delete Comment">
                </#if>
              <#elseif flaggedComment.isRating>
                <@s.url id="flagURL" namespace="/admin" action="viewRating" ratingId="${flaggedComment.target}"/>
                <#assign deleteLabel = "Delete Rating">
              <#elseif flaggedComment.isReply>
                <@s.url id="flagURL" namespace="/admin" action="viewReply" replyId="${flaggedComment.target}"/>
                <#assign deleteLabel = "Delete Reply (Sub-thread)">
              </#if>
              <#if flaggedComment.targetTitle?exists>
                <#assign targetTitle = flaggedComment.targetTitle>
              <#else>
                <#assign targetTitle = '"Flagged Annotation has no Title"'>
              </#if>
              <tr>
                <td>${flaggedComment.created}</td>
                <td width="20%">${flaggedComment.flagComment!}</td>
                <td><a href="../user/displayUser.action?userId=${flaggedComment.creatorid}"/>${flaggedComment.creator}</a></td>
                <td width="20%"><a href="${flagURL}">${targetTitle}</a></td>
                <td>${flaggedComment.reasonCode!}</td>
                <td>
                  <#if flaggedComment.broken >
                    <strong>There was a problem fetching this flag.</strong>
                  <#else>
                    <@s.checkbox name="commentsToUnflag" label="Remove Flag" fieldValue="${flaggedComment.target}_${flaggedComment.flagId}_${flaggedComment.targetType}"/>
                    <br/>
                    <@s.checkbox name="commentsToDelete" label="${deleteLabel}" fieldValue="${flaggedComment.root}_${flaggedComment.target}_${flaggedComment.targetType}"/>
                    <#if flaggedComment.isAnnotation && !flaggedComment.isGeneralComment >
                      <br/>
                      Convert to:
                      <br/>
                      <#if !flaggedComment.isMinorCorrection() >
                        <@s.checkbox name="convertToMinorCorrection" label="Minor Correction"
                              fieldValue="${flaggedComment.flagId}_${flaggedComment.target}"/>
                        <br/>
                      </#if>
                      <#if !flaggedComment.isFormalCorrection() >
                        <@s.checkbox name="convertToFormalCorrection" label="Formal Correction"
                              fieldValue="${flaggedComment.flagId}_${flaggedComment.target}"/>
                        <br/>
                      </#if>
                      <#if !flaggedComment.isRetraction() >
                        <@s.checkbox name="convertToRetraction" label="Retraction"
                              fieldValue="${flaggedComment.flagId}_${flaggedComment.target}"/>
                        <br/>
                      </#if>
                      <#if flaggedComment.isCorrection() >
                        <@s.checkbox name="convertToNote" label="Note"
                              fieldValue="${flaggedComment.flagId}_${flaggedComment.target}"/>
                        <br/>
                      </#if>
                    </#if>
                  </#if>
                </td>
              </tr>
              <tr><td colspan="6"><hr/></td></tr>
            </#list>
          </table>
          <@s.submit value="Process Selected Flags" />
        </@s.form>
        <br/>
      </fieldset>
      <br/>
    </#if>

  </body>
</html>