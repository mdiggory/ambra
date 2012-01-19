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
  <div id="browseNav">
    <div>
      <form class="browseForm" action="browse.action" method="get" name="browseForm">
        <fieldset>
          <legend>How would you like to browse?</legend>
          <ol>
            <li><label for="date"><input type="radio" onclick="document.browseForm.submit()" name="field" id="date" value="date" /> By Publication Date</label></li>
            <li><label for="subject"><input type="radio" name="field" id="subject" value="subject" checked="checked" /> By Subject</label></li>
          </ol>
        </fieldset>
      </form>
    </div>

    <ul class="subjects">
      <#assign infoText = "">
      <#list subjects?keys as subjectName>
        <#if selectedSubjects?seq_contains(subjectName)>
          <li class="current">${subjectName} (${subjects[subjectName]})</li>
          <#assign infoText = "in <strong>" + subjectName+ "</strong>">
        <#else>
          <@s.url id="browseURL" action="browse" namespace="/article" field="${field}" selectedSubjects="${subjectName}" includeParams="none"/>
          <li><@s.a href="%{browseURL}">${subjectName} (${subjects[subjectName]})</@s.a></li>
        </#if>
      </#list>
    </ul>
  </div> <!-- browse nav -->

