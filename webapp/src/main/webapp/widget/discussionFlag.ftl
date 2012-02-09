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
<!-- begin : post response -->
<div class="flag pane" id="FlaggingPanel" >
  <div id="flagForm">
    <h5>Why should this posting be reviewed?</h5>
    <p>See also <a href="${freemarker_config.getContext()}/static/commentGuidelines.action"
                   class="instructions">Guidelines for Notes, Comments and Corrections</a>.</p>
    <form name="discussionFlag" method="post" action="">
      <input type="hidden" name="urlParam" value="" />
      <input type="hidden" name="comment" value="" />

      <div id="flagSubmitMsg" class="error" style="display:none;"></div>

      <fieldset>
        <legend>Identify Reason for Flagging</legend>
        <ol class="radio">
          <li><label for="spam">Spam</label>
            <input type="radio" value="spam" name="reasonCode" checked /></li>
          <li><label for="offensive">Offensive</label>
            <input type="radio" value="offensive" name="reasonCode" /></li>
          <li><label for="inappropriate">Inappropriate</label>
            <input type="radio" value="inappropriate" name="reasonCode" /></li>
          <li><label for="other">Other</label>
            <input type="radio" value="other" name="reasonCode" /></li>

        </ol>

        <label for="reponse"><span class="none">Enter your response</span>
          <!-- error message style <em>Please enter your response</em> --></label>

        <textarea id="responseArea" title="Add any additional information here..."
                  class="response" name="response" >Add any additional information here...</textarea>

        <div class="buttons">
          <input name="cancel" value="Cancel" type="button" id="btnCancel" title="Click to close and cancel"/>
          <input name="submit" value="Submit" type="button" id="btnSubmit" title="Click to submit your response" class="primary"/>
        </div>

      </fieldset>
    </form>
  </div>
  <div id="flagConfirm">
    <h5 class="flag icon">Thank You!</h5>
    <p>Thank you for taking the time to flag this posting; we review flagged postings on a regular basis.</p>
    <form>
      <div class="btnwrap"><input name="close" value="Close" type="button" id="btnFlagConfirmClose"
                                title="Close this dialogue box"/></div>
    </form>
  </div>
</div>
<!-- end : post response -->
