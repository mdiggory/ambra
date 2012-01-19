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
    <title>Ambra: Administration: Edit Annotation</title>

    <script type="text/javascript">
      function confirmToDeleteAuthor(authorIndex, name) {
        if (confirm('Are you sure you want to delete author ' + name + ' ?')) {
          document.manageAnnotationSave.citationAuthorDeleteIndex.value = authorIndex;
          document.manageAnnotationSave.submit();
        }
      }
      function confirmToDeleteCollaborativeAuthor(authorIndex, name ) {
        if (confirm('Are you sure you want to delete this collaborative author ' + name +' ?')) {
          document.manageAnnotationSave.citationCollaborativeAuthorDeleteIndex.value = authorIndex;
          document.manageAnnotationSave.submit();
        }
      }
    </script>

    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Edit Annotation</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <fieldset>
      <legend><b>Load Annotation</b></legend>
      <@s.form name="manageAnnotationLoad" action="manageAnnotationLoad" namespace="/admin" method="post">
        <table>
          <tr><td><b>Annotation ID</b></td><td><@s.textfield name="annotationId" value="${annotationId!}" size="60"/></td></tr>
          <tr><td colspan="2"><@s.submit value="Load Annotation" /></td></tr>
        </table>
      </@s.form>
    </fieldset>

    <#if annotation??>
      <@s.form name="manageAnnotationSave" action="manageAnnotationSave" namespace="/admin" method="post">

      <fieldset>
        <legend><b>Annotation Details</b></legend>
          <@s.hidden name="annotationId" label="hiddenAnnotationId" required="true" value="${annotationId!}"/>
        <table>
          <tr><td><b>Title</b></td>
            <td>${annotation.title!"No Title for this Annotation"}</td></tr>
          <tr><td valign="top"><b>Body</b></td>
            <td><@s.textarea name="annotationBody" value="${annotationBody!}" rows="9" cols="100"/></td></tr>
          <tr><td><b>Context</b></td>
            <td><@s.textarea name="annotationContext" value="${annotationContext!}" rows="3" cols="100"/></td></tr>
          <tr><td><b>Id</b></td>
            <td><a href="${freemarker_config.context}/annotation/listThread.action?inReplyTo=${annotationId!}&root=${annotationId!}">${annotationId!}</a></td></tr>
          <tr><td><b>Type</b></td>
            <td>${annotation.type!"No Type"}</td></tr>
          <tr><td><b>Created</b></td>
            <td>${annotation.createdAsDate?string("EEEE, MMMM dd, yyyy, hh:mm:ss a '('zzz')'")!"No Creation Date"}</td></tr>
          <tr><td><b>Creator</b></td>
            <@s.url id="showUser" namespace="/user" action="showUser" userId="${annotation.creator!}"/>
            <td><@s.a href="${showUser}">${annotation.creator!"No Creator"}</@s.a></td></tr>
          <tr><td><b>Annotates</b></td>
            <td><a href="${freemarker_config.context}/article/${annotation.annotates!}">${annotation.annotates!"No Annotates value"}</a></td></tr>
          <tr><td><b>Conflict of Interest</b></td>
            <td>${annotation.cIStatement!"No Conflict of Interest Statement"}</td></tr>
        </table>
      </fieldset>

      <@s.submit value="Save Annotation" />

      <#if annotation.formalCorrection || annotation.retraction>
      <#if ! annotation.citation??>
        <b>No Citation for this Annotation<b>
      <#else>
            <@s.hidden name="citationId" label="hiddenCitationId" required="true" value="${citationId!}"/>
        <fieldset>
          <legend><b>Annotation Citation</b></legend>
            <table>
              <tr><td><b>Citation Title</b></td>
                <td><@s.textfield name="citationTitle" value="${citationTitle!}" size="40"/></td></tr>
              <tr><td><b>Year</b></td>
                <td><@s.textfield name="citationDisplayYear" value="${citationDisplayYear!}" size="10"/></td></tr>
              <tr><td><b>Volume</b></td>
                <td><@s.textfield name="citationVolumeNumber" value="${citationVolumeNumber!}" size="10"/></td></tr>
              <tr><td><b>Issue</b></td>
                <td><@s.textfield name="citationIssue" value="${citationIssue!}" size="10"/></td></tr>
              <tr><td><b>Journal</b></td>
                <td><@s.textfield name="citationJournal" value="${citationJournal!}" size="20"/></td></tr>
              <tr><td><b>eLocationId</b></td>
                <td><@s.textfield name="citationELocationId" value="${citationELocationId!}" size="40"/></td></tr>
              <tr><td><b>DOI</b></td>
                <td><@s.textfield name="citationDoi" value="${citationDoi!}" size="40"/></td></tr>
              <tr><td><b>URL</b></td>
                <td>${annotation.citation.url!"No URL"}</td></tr>
              <tr><td><b>Note</b></td>
                <td>${annotation.citation.note!"No Note"}</td></tr>
              <tr><td><b>Summary</b></td>
                <td>${annotation.citation.summary!"No Summary"}</td></tr>
              <tr><td colspan="2">
                <fieldset>
                  <legend><b>Citation Authors</b></legend>
                    <table>
                      <#if citationAuthorIds?? && (citationAuthorIds?size > 0)>
                          <tr><td><b>Given Names</b></td><td><b>Surnames</b></td><td><b>Suffixes</b></td></tr>
                          <@s.hidden name="citationAuthorDeleteIndex" label="citationAuthorDeleteIndex" required="true" value="-1"/>
                        <#list citationAuthorIds as authorId>
                          <@s.hidden name="citationAuthorIds" label="hiddenCitationAuthorIds" required="true" value="${authorId!}"/>
                          <tr><td><@s.textfield name="citationAuthorGivenNames" value="${citationAuthorGivenNames[authorId_index]!}" size="20"/></td>
                            <td><@s.textfield name="citationAuthorSurnames" value="${citationAuthorSurnames[authorId_index]!}" size="20"/></td>
                            <td><@s.textfield name="citationAuthorSuffixes" value="${citationAuthorSuffixes[authorId_index]!}" size="20"/></td>
                            <td><a href="#" onClick="confirmToDeleteAuthor(${authorId_index}, '${citationAuthorGivenNames[authorId_index]!} ${citationAuthorSurnames[authorId_index]!}');return false;">Delete Author</a></td></tr>
                        </#list>
                          <tr><td><@s.textfield name="citationAuthorGivenNames" value="" size="20"/></td>
                            <td><@s.textfield name="citationAuthorSurnames" value="" size="20"/></td>
                            <td><@s.textfield name="citationAuthorSuffixes" value="" size="20"/></td>
                            <td><a href="#" onClick="document.manageAnnotationSave.submit()">Add Author</a></td></tr>
                      <#else>
                        There are currently no Authors associated to this Citation.
                        <@s.hidden name="citationAuthorDeleteIndex" label="citationAuthorDeleteIndex" required="true" value="-1"/>
                        <tr><td><@s.textfield name="citationAuthorGivenNames" value="" size="20"/></td>
                          <td><@s.textfield name="citationAuthorSurnames" value="" size="20"/></td>
                          <td><@s.textfield name="citationAuthorSuffixes" value="" size="20"/></td>
                          <td><a href="#" onClick="document.manageAnnotationSave.submit()">Add Author</a></td></tr>
                      </#if>
                  </table>
                </fieldset>
              </td></tr>

              <tr><td colspan="2">
                <fieldset>
                  <legend><b>Citation Collaborative Authors</b></legend>
                  <table>
                      <#if citationCollaborativeAuthorNames?? && (citationCollaborativeAuthorNames?size > 0)>
                          <@s.hidden name="citationCollaborativeAuthorDeleteIndex" label="citationCollaborativeAuthorDeleteIndex" required="true" value="-1"/>
                        <#list citationCollaborativeAuthorNames as authorName>
                          <tr><td><@s.textfield name="citationCollaborativeAuthorNames" value="${authorName!}" size="20"/></td>
                            <td><a href="#" onClick="confirmToDeleteCollaborativeAuthor(${authorName_index}, '${authorName}');return false;">Delete Collaborative Author</a></td></tr>
                        </#list>
                          <tr><td><@s.textfield name="citationCollaborativeAuthorNames" value="" size="20"/></td>
                            <td><a href="#" onClick="document.manageAnnotationSave.submit()">Add Collaborative Author</a></td></tr>
                      <#else>
                        There are currently no Collaborative Authors associated to this Citation.
                          <@s.hidden name="citationCollaborativeAuthorDeleteIndex" label="citationCollaborativeAuthorDeleteIndex" required="true" value="-1"/>
                          <tr><td><@s.textfield name="citationCollaborativeAuthorNames" value="" size="20"/></td>
                            <td><a href="#" onClick="document.manageAnnotationSave.submit()">Add Collaborative Author</a></td></tr>
                      </#if>
                  </table>
                </fieldset>
              </td></tr>

            </table>
        </fieldset>
        <@s.submit value="Save Annotation" />
      </#if>
      </#if>
      </@s.form>
    </#if>

  </body>
</html>