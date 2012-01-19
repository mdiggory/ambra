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
    <title>Ambra: Administration</title>
<#include "includes/header.ftl">
    <script type="text/javascript">
        function confirmResyndicate(date, syndicate) {
            if (date != null && syndicate != null) {
                return confirm('This article was already syndicated to ' + syndicate + ' on '
                        + date + '.  Are you sure you want to syndicate it again?');
            } else {
                return confirm('This article was already syndicated. ' +
                        'Are you sure you want to syndicate it again?');
            }
        }

        function confirmMarkAsFailed(date, syndicate) {
            if (date != null && syndicate != null) {
                return confirm('The syndication of this article to ' + syndicate + ' began on '
                        + date + '.  Are you sure you want to mark this syndication as having failed?');
            } else {
                return confirm('The current status of this syndication is "in progress". ' +
                        'Are you sure you want to mark it as having failed?');
            }
        }

    </script>
</head>
<body>
<!--
  The time zone for the rest of the application is set to "UTC" (i.e., GMT).  Since all of the
  users who access this page are in the Pacific time zone, however, this FTL will be rendered with
  the local time.
-->
<#setting time_zone="America/Los_Angeles">

<h1 style="text-align: center">Ambra: Administration</h1>
<#include "includes/navigation.ftl">

<div id="messages">
<@messages />
</div>

<fieldset class="syndicationHistory">
    <legend><strong>Article Syndication History</strong></legend>

<#if syndicationHistory??>
    <ul class="syndHist">
        <#list syndicationHistory as syndicate>
            <li><strong>Article:</strong> ${syndicate.doi}</li>
            <#if syndicate.status == 'PENDING'>
                <li class="pending">This article has not yet been syndicated to
                ${syndicate.target}</li>
            </#if>
            <#if syndicate.status == 'IN_PROGRESS'>
                <li class="inprogress">
                    Syndication to ${syndicate.target}
                    is in progress as of ${syndicate.lastSubmitTimestamp?datetime}<br/>
                    Syndication may take up to 24 hours. Please check back later.
                <@s.form action="markSyndicationAsFailed" method="post" namespace="/admin">
                <@s.hidden name="target" value="${syndicate.target}"/>
                <@s.hidden name="article" value="${article}"/>
                    <input type="submit" value="Mark As Failed"
                           onClick="return confirmMarkAsFailed('${syndicate.lastSubmitTimestamp?datetime}','${syndicate.target}');"/>
                </@s.form>
                </li>
            </#if>
            <#if syndicate.status == 'SUCCESS'>
                <li class="success">Successfully syndicated to ${syndicate.target} on
                ${syndicate.lastSubmitTimestamp?datetime}</li>
            </#if>
            <#if syndicate.status == 'FAILURE'>
                <li class="error">Syndication to ${syndicate.target} Failed on
                ${syndicate.lastModified?datetime}
                    <#if syndicate.errorMessage??>
                        with the message: <br/>
                    ${syndicate.errorMessage}
                        <#else>
                            but there is no message describing this syndication failure
                    </#if>
                </li>
            </#if>
            <#if syndicate.submissionCount == 0>
                <#if syndicate.status != 'PENDING'>
                    <li>Never submitted to ${syndicate.target}</li>
                </#if>
                <#elseif syndicate.submissionCount == 1>
                    <li>Submitted to ${syndicate.target} 1 time</li>
                <#else>
                    <li>Submitted to ${syndicate.target}
                    ${syndicate.submissionCount} times
                    </li>
            </#if>
            <#if syndicate.errorMessage?? && syndicate.status != 'FAILURE'>
                <li>Previous error messages: ${syndicate.errorMessage}</li>
            </#if>
        </#list>
    </ul>
    <#else>Error: syndication history empty!</#if>

<#-- If any of the current syndications is complete. -->
<#if finishedSyndications?? && finishedSyndications?size gt 0>
    <hr/>
    <#if finishedSyndications?size == 1>
        <#list finishedSyndications as syndicate>
            Syndicate to ${syndicate.target}
        <@s.form name="reSyndicateArticle" action="reSyndicateArticle" method="post" namespace="/admin">
        <@s.hidden name="target" value="${syndicate.target}"/>
        <@s.hidden name="article" value="${article}"/>
            <#if syndicate.status == 'PENDING'>
                <input type="submit" value="Syndicate"/>
                <#else>
                    <input type="submit" value="Syndicate"
                           onClick="return confirmResyndicate('${syndicate.lastSubmitTimestamp?datetime}','${syndicate.target}');"/>
            </#if>
        </@s.form>
        </#list>
        <#else>
        <@s.form name="reSyndicateArticle" action="reSyndicateArticle" method="post" namespace="/admin">
        <@s.hidden name="article" value="${article}"/>
            Syndicate to:
            <#list finishedSyndications as syndicate>
                <input type="checkbox" name="target" value="${syndicate.target}"
                       checked=true/> ${syndicate.target}<#if syndicate_has_next>, </#if>

                <#if syndicate.status == 'PENDING'>
                    <input type="submit" value="Syndicate"/>
                    <#else>
                        <input type="submit" value="Syndicate" onClick="return confirmResyndicate(null,null);"/>
                </#if>
            </#list>
        </@s.form>
    </#if>
</#if>
</fieldset>

<fieldset>
    <legend><strong>Get Syndication History for Another Article</strong></legend>
<@s.form name="articleSyndicationHistory" action="articleSyndicationHistory" method="post" namespace="/admin">
    Article Uri: <input type="article" name="article" label="Article Uri" size="80"/>&nbsp;<input type="submit"
                                                                                                  name="action"
                                                                                                  value="Go"/>
</@s.form>
</fieldset>

</body>
</html>
