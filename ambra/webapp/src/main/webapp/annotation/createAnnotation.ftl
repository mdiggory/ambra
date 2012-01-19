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
          <legend>Create an annotation</legend>
          <@s.form name="createAnnotationForm" action="createAnnotationSubmit" method="post" enctype="multipart/form-data">
            <@s.textfield name="target" label="What does it annotate" required="true"/>
            <@s.textfield name="startPath" label="Start path" value="%{'id(\"x20060728a\")/p[1]'}" required="true"/>
            <@s.textfield name="startOffset" label="Start offset" value="%{'288'}" required="true"/>
            <@s.textfield name="endPath" label="End path" value="%{'id(\"x20060801a\")/h3[1]'}" required="true"/>
            <@s.textfield name="endOffset" label="End offset" value="%{'39'}" required="true"/>
            <@s.textfield name="commentTitle" label="Title" value="%{'title1'}"/>
            <@s.textfield name="supercedes" label="Older Annotation to supersede" value="%{'doi:anOlderAnnotation'}"/>
            <@s.checkbox name="public" label="Is it Public?" fieldValue="true"/>
            <@s.textarea name="comment" label="Annotation text" value="%{'This article seems to cover the same grounds as this ...'}" rows="'3'" cols="'30'" required="true"/>
            <@s.submit value="create annotation" />
          </@s.form>
      </fieldset>
    </p>
  </body>
</html>
