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
      <#macro writeReplyDetails root reply>
        <fieldset>
            <legend>${reply.ID?c}</legend>
          <span style="background:burlywood">
              title       =${reply.title}         <br/>
              body        =${reply.body}          <br/>
              created     =${reply.created}       <br/>
              creator     =${reply.creator}       <br/>
              type        =${reply.type}          <br/>

              <@s.url id="createReplyURL" action="createReplySubmit" root="${root}" inReplyTo="${reply.ID?c}" namespace="/annotation/secure"/>
              <@s.a href="%{createReplyURL}">create reply</@s.a> <br/>

              <@s.url id="listReplyURL" action="listAllReplies" root="${root}" inReplyTo="${reply.ID?c}"/>
              <@s.a href="%{listReplyURL}">list all replies</@s.a> <br/>

              <@s.url id="listThreadedRepliesURL" action="listThreadedReplies" root="${root}" inReplyTo="${reply.ID?c}"/>
              <@s.a href="%{listThreadedRepliesURL}">list threaded replies</@s.a> <br/>

              <@s.url id="listFlagURL" action="listAllFlags" target="${reply.ID?c}" />
              <@s.a href="%{listFlagURL}">list all flags</@s.a> <br/>
        </fieldset>
        <li>
          <ul>
            <#list reply.replies as subReply>
              <@writeReplyDetails root subReply/>
            </#list>
          </ul>
        </li>
      </#macro>

      <ul>
        <#list replies as reply>
          <@writeReplyDetails root reply/>
        </#list>
      </ul>
    </fieldset>

  </body>
</html>
