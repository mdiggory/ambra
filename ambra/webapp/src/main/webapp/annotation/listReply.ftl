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
		<title>Available replies</title>
	</head>

	<body>

    <fieldset>
      <legend>Available replies</legend>

      <#list replies as reply>
          id          =
          <@s.url id="getReplyURL" action="getReply" replyId="${reply.id}"/>
          <@s.a href="%{getReplyURL}">${reply.id}</@s.a> <br/>

          inReplyTo   =${reply.inReplyTo}     <br/>
          root        =${reply.root}     <br/>
          title       =${reply.commentTitle}         <br/>
          creator     =${reply.creator}       <br/>

      <ul>
        <li>
          <@s.url id="deleteReplyURL" action="deleteReply" id="${reply.id}" namespace="/annotation/secure" />
          <@s.a href="%{deleteReplyURL}">delete</@s.a>
        </li>

        <li>
          <@s.url id="listReplyURL" action="listAllReplies" root="${reply.root}" inReplyTo="${reply.id}"/>
          <@s.a href="%{listReplyURL}">list all replies</@s.a>
        </li>

        <li>
          <fieldset>
              <legend>Create an reply</legend>
              <@s.form name="createReplyForm" action="createReplySubmit" namespace="/annotation/secure">
                <@s.textfield name="root" label="What is the root of this reply" value="${reply.root}" required="true" size="50"/>
                <@s.textfield name="inReplyTo" label="What is it in reply to" value="${reply.id}" required="true" size="50"/>
                <@s.textfield name="commentTitle" label="Title"/>
                <@s.textarea name="comment" label="Reply text" rows="'3'" cols="'30'" required="true"/>
                <@s.submit value="create reply" />
              </@s.form>
          </fieldset>
        </li>

        <li>
          <@s.url id="listFlagURL" action="listFlags" target="${reply.id}" />
          <@s.a href="%{listFlagURL}">list flags</@s.a> <br/>
        </li>

        <li>
          <fieldset>
              <legend>Create a flag</legend>
              <@s.form name="createFlagForm" action="createReplyFlagSubmit" method="get" namespace="/annotation/secure">
                <@s.textfield name="target" label="What does it flag" value="${reply.id}" required="true" size="50"/>
                <@s.select name="reasonCode" label="Reason"
                            list="{'spam', 'Offensive', 'Inappropriate'}"/>
                <@s.textarea name="comment" label="Flag text" value="%{'Spammer guy attacks again....'}" rows="'3'" cols="'30'" required="true"/>
                <@s.submit value="create flag" />
              </@s.form>
          </fieldset>
        </li>
      </ul>
      <hr/>
      </#list>

    </fieldset>

  </body>
</html>
