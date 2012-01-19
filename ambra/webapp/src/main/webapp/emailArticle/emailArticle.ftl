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
<title>Email this article</title>
<body>

<@s.url id="fetchArticleURL" action="fetchArticle" articleURI="${articleURI}"/>
<b>Title:</b> <@s.a href="%{fetchArticleURL}">${title}</@s.a> <br/>

<b>Description :</b> ${description}
<@s.form name="emailThisArticle" cssClass="pone-form" action="emailThisArticleSubmit" namespace="/article" method="post" title="Email this article">
  <@s.hidden name="articleURI"/>
  <tr>
    <td>Recipient's E-mail address:</td>
    <td>
      <@s.textfield name="emailTo" size="40"/>
    </td>
  </tr>
  <tr>
    <td>Your E-mail address:</td>
    <td>
      <@s.textfield name="emailFrom" size="40"/>
    </td>
  </tr>
  <tr>
    <td>Your name:</td>
    <td>
      <@s.textfield name="senderName" size="40"/>
    </td>
  </tr>
  <tr>
    <td>Your comments to add to the E-mail:</td>
    <td>
      <@s.textarea name="note" cols="40" rows="5" value="%{'I thought you would find this article interesting.'}"/>
    </td>
  </tr>
  <@s.submit value="send"/>
</@s.form>
</body>
</html>