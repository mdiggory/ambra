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
<div dojoType="ambra.widget.RegionalDialog" id="AnnotationDialog" style="padding:0;margin:0;">
  <div class="dialog annotate">
    <div class="tipu" id="dTipu"></div>
    <div class="comment">
      <h5><span>Post Your Note (For Public Viewing)</span></h5>
      <div class="posting pane">
        <form name="createAnnotation" id="createAnnotation" method="post" action="">
          <input type="hidden" name="target" value="${articleURI}" />
          <input type="hidden" name="startPath" value="" />
          <input type="hidden" name="startOffset" value="" />
          <input type="hidden" name="endPath" value="" />
          <input type="hidden" name="endOffset" value="" />
          <input type="hidden" name="commentTitle" id="commentTitle" value="" />
          <input type="hidden" name="comment" id="commentArea" value="" />
          <input type="hidden" name="ciStatement" id="statementArea" value="" />
          <input type="hidden" name="isCompetingInterest" id="isCompetingInterest" value="false" />
          <input type="hidden" name="noteType" id="noteType" value="" />
          <fieldset>
            <legend>Compose Your Note</legend>
            <span id="submitMsg" class="error" style="display:none;"></span>
            <table class="layout">
              <tr>
                <td>
                  <label for="cNoteType">This is a </label><select name="cNoteType" id="cNoteType"><option value="note">note</option><option value="correction">correction</option></select>
                  <@s.url id="wacl" namespace="/static" action="commentGuidelines" includeParams="none" anchor="corrections" target="${articleURI}" />
                  <span id="cdls" style="visibility:hidden;margin-left:0.3em; white-space:nowrap;"><a href="${wacl}">What are corrections?</a></span>
                  <label for="cTitle"><span class="none">Enter your note title</span><!-- error message text <em>A title is required for all public notes</em>--></label>
                  <input type="text" name="cTitle" id="cTitle" value="Enter your note title..." class="title" alt="Enter your note title..." />
                  <label for="cArea"><span class="none">Enter your note</span><!-- error message text <em>Please enter your note</em>--></label>
                  <textarea name="cArea" id="cArea" value="Enter your note..." alt="Enter your note...">Enter your note...</textarea>
                  <input type="hidden" name="isPublic" value="true" />
                </td>
                <td>&nbsp;</td>
                <td class="coi">
                  <fieldset>
                    <legend>Declare any competing interests.</legend>
                    <ul>
                      <li><label><input id="isCompetingInterestNo" type="radio" checked="checked" name="competingInterest" value="false"  /> No, I don't have any competing interests to declare.</label></li>
                      <li><label><input id="isCompetingInterestYes" type="radio" name="competingInterest" value="true"  /> Yes, I have competing interests to declare (enter below):</label></li>
                    </ul>
                    <textarea name="ciStatementArea" id="ciStatementArea" disabled value="Enter your competing interests..." alt="Enter your competing interests...">Enter your competing interests...</textarea>
                  </fieldset>
                </td>
              </tr>
              <tr>
                <td colspan="3" class="buttons">
                  <input type="button" value="Cancel" title="Click to close and cancel" id="btn_cancel"/>
                  <input type="button" value="Submit" title="Click to post your note publicly" id="btn_post" class="primary"/>
                </td>
              </tr>
              <tr>
                <td colspan="3">
                  <p>Notes and Corrections can include the following markup tags:</p>
                  <p><strong>Emphasis:</strong> ''<em>italic</em>''&nbsp;&nbsp;'''<strong>bold</strong>'''&nbsp;&nbsp;'''''<strong><em>bold italic</em></strong>'''''</p>
                  <p><strong>Other:</strong> ^^<sup>superscript</sup>^^&nbsp;&nbsp;~~<sub>subscript</sub>~~</p>
                </td>
              </tr>
            </table>
          </fieldset>
        </form>
      </div>
    </div>
    <div class="tip" id="dTip"></div>
  </div>
</div>
