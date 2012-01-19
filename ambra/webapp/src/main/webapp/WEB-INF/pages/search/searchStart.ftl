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
    <title>Search</title>
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
        <legend>Simple Search</legend>
        <@s.form name="simpleSearchForm" action="simpleSearch" namespace="/search" method="post">
          <@s.textfield name="query" label="Query" required="true"/>
          <@s.submit value="simple search" />
        </@s.form>
      </fieldset>

      <fieldset>
        <legend>Advanced Search</legend>
        <@s.form name="advancedSearchForm" action="advancedSearch" namespace="/search" method="post">
          <@s.textfield name="title" label="Title" />
          <@s.textfield name="text" label="Text" />
          <@s.textfield name="description" label="Description" />
          <@s.textfield name="creator" label="Creator" />
          <@s.submit value="advanced search" />
        </@s.form>
      </fieldset>
    </p>
  </body>
</html>
