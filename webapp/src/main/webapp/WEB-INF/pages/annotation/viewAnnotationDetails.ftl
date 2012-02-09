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
		<title>Annotation details</title>
	</head>

	<body>

    <fieldset>
        <legend>Annotation details</legend>

          id          =${annotation.id}            <br/>
          annotates   =${annotation.annotates}     <br/>
          title       =${annotation.commentTitle}         <br/>
          body        =${annotation.comment}          <br/>
          bodyWithUrlLinking        =${annotation.commentWithUrlLinking}          <br/>
          context     =${annotation.context}       <br/>
          created     =${annotation.created}       <br/>
          creator     =${annotation.creator}       <br/>
          mediator    =${annotation.mediator}      <br/>
          type        =${annotation.type}          <br/>

          <@s.url id="createReplyURL" action="createReplySubmit" root="${annotation.id}" inReplyTo="${annotation.id}" namespace="/annotation/secure"/>
          <@s.a href="%{createReplyURL}">create reply</@s.a> <br/>

          <@s.url id="listReplyURL" action="listAllReplies" root="${annotation.id}" inReplyTo="${annotation.id}"/>
          <@s.a href="%{listReplyURL}">list all replies</@s.a> <br/>

          <@s.url id="listFlagURL" action="listAllFlags" target="${annotation.id}" />
          <@s.a href="%{listFlagURL}">list all flags</@s.a> <br/>

    </fieldset>

  </body>
</html>
