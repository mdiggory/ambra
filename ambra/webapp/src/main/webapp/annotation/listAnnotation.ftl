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
<html>
	<head>
		<title>Annotations list</title>
	</head>

	<body>

    <fieldset>
      <legend>Available annotations</legend>

      <#list annotations as annotation>
          id          =
          <@s.url id="getAnnotationURL" includeParams="none" action="getAnnotation" annotationId="${annotation.id}"/>
          <@s.a href="%{getAnnotationURL}">${annotation.id}</@s.a> <br/>
          annotates   =${annotation.annotates}     <br/>
          title       =${annotation.commentTitle}         <br/>
          creator     =${annotation.creator}       <br/>
          context     =${annotation.context!""}       <br/>

      <ul>
        <li>
          <@s.url id="listReplyURL" action="listAllReplies" root="${annotation.id}" inReplyTo="${annotation.id}"/>
          <@s.a href="%{listReplyURL}">list all replies</@s.a> <br/>
        </li>

        <li>
          <@s.url id="listThreadedRepliesURL" action="listThreadedReplies" root="${annotation.id}" inReplyTo="${annotation.id}"/>
          <@s.a href="%{listThreadedRepliesURL}">list threaded replies</@s.a> <br/>
        </li>

        <li>
          <@s.url includeParams="none" id="listThreadURL" action="listThread" root="${annotation.id}" inReplyTo="${annotation.id}"/>
          <@s.a href="%{listThreadURL}">list threaded replies</@s.a> <br/>
        </li>

        <li>
          <fieldset>
              <legend>Create an reply</legend>
              <@s.form name="createReplyForm" action="createReplySubmit" method="post" namespace="/annotation/secure" enctype="multipart/form-data">
                <@s.textfield name="root" label="What is the root of this reply" value="${annotation.id}" required="true" size="50"/>
                <@s.textfield name="inReplyTo" label="What is it in reply to" value="${annotation.id}" required="true" size="50"/>
                <@s.textfield name="commentTitle" label="Title"/>
                <@s.textarea name="comment" label="Reply text" rows="'3'" cols="'30'" required="true" value="%{'a reply to an annotation'}"/>
                <@s.submit value="create reply" />
              </@s.form>
          </fieldset>
        </li>
        <li>
          <@s.url id="listFlagURL" action="listFlags" target="${annotation.id}" />
          <@s.a href="%{listFlagURL}">list flags</@s.a> <br/>
        </li>
        <li>
          <fieldset>
              <legend>Create a flag</legend>
              <@s.form name="createFlagForm" action="createAnnotationFlagSubmit" method="post" namespace="/annotation/secure" enctype="multipart/form-data">
                <@s.textfield name="target" label="What does it flag" value="${annotation.id}" required="true" size="50"/>
                <@s.select name="reasonCode" label="Reason"
                            list="{'spam', 'Offensive', 'Inappropriate'}"/>
                <@s.textarea name="comment" label="Flag text" value="%{'Spammer guy attacks again....'}" rows="'3'" cols="'30'" required="true"/>
                <@s.submit value="create flag" />
              </@s.form>
          </fieldset>
        </li>
        <li>
          <@s.url id="makePublicAnnotationURL" action="setAnnotationPublic" targetId="${annotation.id}" namespace="/annotation/secure"/>
          <@s.a href="%{makePublicAnnotationURL}">Set Annotation as public</@s.a> <br/>
        </li>
        <li>
          <@s.url id="deleteAnnotationURL" action="deleteAnnotation" annotationId="${annotation.id}" namespace="/annotation/secure" />
          <@s.a href="%{deleteAnnotationURL}">delete annotation</@s.a><br/>
        </li>
      </ul>
       <hr/>
      </#list>

    </fieldset>

  </body>
</html>
