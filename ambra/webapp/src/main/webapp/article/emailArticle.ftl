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
<div id="content">
  <h1>E-mail this Article</h1>
  <@s.url id="fetchArticleURL" action="fetchArticle" articleURI="${articleURI}"/>
  <div class="source">
    <span>Article Source:</span>
    <@s.url id="articlePageURL" action="fetchArticle" namespace="/article" articleURI="${articleURI}" includeParams="none"/>
    <@s.a href="%{articlePageURL}" title="Back to original article" cssClass="article icon"><@articleFormat>${title}</@articleFormat></@s.a>
  </div>  
  <p>Fields marked with an <span class="required">*</span> are required. </p>
  <@s.form name="emailThisArticle" cssClass="pone-form" action="emailThisArticleSubmit" namespace="/article" method="post" title="E-mail this article" enctype="multipart/form-data">
    <@s.hidden name="articleURI"/>
    <@s.hidden name="title"/>
    <@s.hidden name="journalName" value="${freemarker_config.getDisplayName(journalContext)}"/>
    <fieldset>
      <legend>Complete this form</legend>
      <ol>
        <@s.textarea rows="${maxEmails}" label="Recipients' E-mail addresses (one per line, max ${maxEmails})" required="true" name="emailTo" size="40" />
        <@s.textfield label="Your E-mail address" required="true" name="emailFrom" size="40" />
        <@s.textfield label="Your name" required="true" name="senderName" size="40" />
        <@s.textarea label="Your comments to add to the E-mail" value="%{'I thought you would find this article interesting.'}" name="note" rows="5" cols="40"/>          
      </ol>
      <@s.submit value="Send"/>
    </fieldset>
  </@s.form>  
  <@s.url id="privacyURL" includeParams="none" namespace="/static" action="privacy"/>
  <p class="citation"><a href="${privacyURL}">Privacy Statement</a></p>
</div>
