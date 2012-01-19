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
<#import "/global/global_variables.ftl" as global>
<#--
  TODO: Moves events defined here into the ambra library and wire up the events onload
-->
<div dojoType="ambra.widget.ContextAction" id="ContextActionDialog" class="contextActionDialog">
  <div class="dialog context">
    <div class="tipu" id="caTipu"></div>
    <div class="contextActionContent">
      <h5><img src="${freemarker_config.context}/images/tooltip_addannotation.gif" /> Add a note to this text.</h5>
      <@s.url id="competingInterestURL" action="competing" namespace="/static" includeParams="none"/>
      Please follow our <a href="${comment}">guidelines for notes and comments</a> and review our <@s.a href="%{competingInterestURL}">competing interests policy</@s.a>. Comments that do not conform to our guidelines will be promptly removed and the user account disabled. The following must be avoided:
      <ul>
        <li>Remarks that could be interpreted as allegations of misconduct</li>
        <li>Unsupported assertions or statements</li>
        <li>Inflammatory or insulting language</li>
      </ul>
      <form name="contextActionForm" id="contextActionForm" class="clearfix buttons" method="post" action="">
        <input type="button" name="Continue" value="Continue" id="ContextActionDialogContinueButton" onmouseup="ambra.displayAnnotationContext.startComment(event);" title="Add a note to this text" class="primary"/>
        <input type="button" name="Cancel" value="Cancel" id="ContextActionDialogCancelButton" onclick="return false;" onmouseup="ambra.displayAnnotationContext.cancelContext(event);" title="Close this Window"/>
      </form>
    </div>
    <div class="tip" id="caTip"></div>
  </div>
</div>
<div dojoType="ambra.widget.ContextAction" id="ContextActionDialogNotLogged" class="contextActionDialog">
  <div class="dialog context">
    <div class="tipu" id="canlTipu"></div>
    <div class="contextActionContent">
      <h5><img src="${freemarker_config.context}/images/tooltip_addannotation.gif" /> Add a note to this text.</h5>
      You must be logged in to add a note to an article.
      You may log in by <a onmousedown="ambra.displayAnnotationContext.disconnect(event);" href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${global.thisPage}">clicking here</a> or <a href="#" onclick="return false;" onmouseup="ambra.displayAnnotationContext.cancelContext(event);">cancel this note</a>.
    </div>
    <div class="tip" id="canlTip"></div>
  </div>
</div>
<div dojoType="ambra.widget.ContextAction" id="ContextActionDialogBadSelection" class="contextActionDialog">
  <div class="dialog context">
    <div class="tipu" id="canBDTipu"></div>
    <div class="contextActionContent">
      <h5 class="annotation icon"><img src="${freemarker_config.context}/images/tooltip_addannotation.gif" /> Add a note to this text.</h5>
      You cannot annotate this area of the document. <a href="#" onclick="return false;" onmouseup="ambra.displayAnnotationContext.cancelContext(event);">Close</a>
    </div>
    <div class="tip" id="canBDTip"></div>
  </div>
</div>
<div dojoType="ambra.widget.ContextAction" id="ContextActionDialogBadRangeSelection" class="contextActionDialog">
  <div class="dialog context">
    <div class="tipu" id="canbrTipu"></div>
    <div class="contextActionContent">
      <h5><img src="${freemarker_config.context}/images/tooltip_addannotation.gif" /> Add a note to this text.</h5>
      You cannot create an annotation that spans different sections of the document; please adjust your selection.<br/>
      <a href="#" onclick="return false;" onmouseup="ambra.displayAnnotationContext.cancelContext(event);">Close</a>
    </div>
    <div class="tip" id="canbrTip"></div>
  </div>
</div>
