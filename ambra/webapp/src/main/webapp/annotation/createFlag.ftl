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
    <title>Create an flag</title>
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
        TODO: remove as it is merged with listAnnotation.ftl
          <legend>Create a flag</legend>
          <@s.form name="createFlagForm" action="createFlagSubmit" method="get">
            <@s.textfield name="target" label="What does it flag" required="true"/>
            <@s.select name="reasonCode" label="Reason"
                        list="{'spam', 'Offensive', 'Inappropriate'}"/> 
            <@s.textarea name="comment" label="Flag text" value="%{'Spammer guy attacks again. Who wants more viagra...'}" rows="'3'" cols="'30'" required="true"/>
            <@s.submit value="create flag" />
          </@s.form>
      </fieldset>
    </p>
  </body>
</html>
