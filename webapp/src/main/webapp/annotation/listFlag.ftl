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
		<title>Flags list</title>
	</head>

	<body>

    <fieldset>
        <legend>Available flags</legend>

        <#list flags as flag>
          id          =
          <@s.url id="getFlagURL" action="getFlag" flagId="${flag.id}"/>
          <@s.a href="%{getFlagURL}">${flag.id}</@s.a> <br/>

          annotates   =${flag.annotates}     <br/>
          comment       =${flag.comment}         <br/>
          reasonCode       =${flag.reasonCode}         <br/>
          creator     =${flag.creator}       <br/>

          <br/>
      
          <@s.url id="deleteFlagURL" action="deleteFlag" flagId="${flag.id}" namespace="/annotation/secure" />
          <@s.a href="%{deleteFlagURL}">delete</@s.a><br/>
          <hr/>
        </#list>

    </fieldset>

  </body>
</html>
