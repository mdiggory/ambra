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
    <title>Create an annotation</title>
  </head>
  <body>
    <legend>Messages</legend>

    <fieldset>
      <p>
        <#list actionMessages as message>
        ${message} <br/>
      </#list>
      </p>
    </fieldset>

    <p>
      <fieldset>
          <legend>Create an reply</legend>
          <@s.form name="createReplyForm" action="createReplySubmit">
            <@s.textfield name="root" label="What is the root of this reply" required="true"/>
            <@s.textfield name="inReplyTo" label="What is it in reply to" required="true"/>
            <@s.textfield name="commentTitle" label="Title"/>
            <@s.textarea name="comment" label="Reply text" rows="'3'" cols="'30'" required="true"/>
            <@s.submit value="create reply" />
          </@s.form>
      </fieldset>
    </p>
  </body>
</html>
