/*
 * $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var responseConfig = {
  responseForm :"discussionResponse",
  discussionContainer :"discussionContainer"
};

var _dcr = new Object();
var _ldc;

dojo.addOnLoad( function() {
  _ldc = dijit.byId("LoadingCycle");

  _dcr.widget = dojo.byId("DiscussionPanel");
  _dcr.btnCancel = dojo.byId("btnCancelResponse");
  _dcr.btnSubmit = dojo.byId("btnPostResponse");
  _dcr.form = document.discussionResponse;
  _dcr.formAction = "/annotation/secure/createDiscussionSubmit.action";
  _dcr.responseTitleCue = "Enter your comment title...";
  _dcr.responseCue = "Enter your comment...";
  _dcr.ciStatementTitleCue = "Enter your competing interests...";
  _dcr.error = dojo.byId('responseSubmitMsg');
  _dcr.requestType = "new";
  _dcr.baseId = _dcr.form.target.value;
  _dcr.replyId = _dcr.form.target.value;

  var responseTitle = _dcr.form.responseTitle;
  var responseArea = _dcr.form.responseArea;
  var ciStatementArea = _dcr.form.ciStatementArea;

  dojo.connect(_dcr.btnSubmit, "click", function(e) {
    ambra.responsePanel.submit(_dcr);
  });

  dojo.connect(_dcr.btnCancel, "click", function(e) {
    document.location = '../../article/' + escape(_dcr.baseId).replace(/\//g,"%2F");
  });  

  dojo.connect(responseTitle, "focus", function(e) {
    ambra.formUtil.textCues.off(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "focus", function(e) {
    ambra.formUtil.textCues.off(responseArea, _dcr.responseCue);
  });

  dojo.connect(responseTitle, "blur", function(e) {
    var fldResponseTitle = _dcr.form.commentTitle;
    if (responseTitle.value != "" && responseTitle.value != _dcr.responseCue) {
      fldResponseTitle.value = responseTitle.value;
    } else {
      fldResponseTitle.value = "";
    }
    ambra.formUtil.textCues.on(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "blur", function(e) {
    var fldResponse = _dcr.form.comment;
    if (responseArea.value != "" && responseArea.value != _dcr.responseCue) {
      fldResponse.value = responseArea.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(responseArea, _dcr.responseCue);
  });

  dojo.connect(responseTitle, "change", function(e) {
    var fldResponseTitle = _dcr.form.commentTitle;
    if (responseTitle.value != "" && responseTitle.value != _dcr.responseCue) {
      fldResponseTitle.value = responseTitle.value;
    } else {
      fldResponseTitle.value = "";
    }
  });

  dojo.connect(responseArea, "change", function(e) {
    var fldResponse = _dcr.form.comment;
    if (responseArea.value != "" && responseArea.value != _dcr.responseCue) {
      fldResponse.value = responseArea.value;
    } else {
      fldResponse.value = responseArea.value;
    }
  });

  
dojo.connect(responseArea, "focus", function(e) {
    ambra.formUtil.textCues.off(responseArea, _dcr.responseCue);
  });

  dojo.connect(responseTitle, "blur", function(e) {
    var fldResponseTitle = _dcr.form.commentTitle;
    if (responseTitle.value != "" && responseTitle.value != _dcr.responseCue) {
      fldResponseTitle.value = responseTitle.value;
    } else {
      fldResponseTitle.value = "";
    }
    ambra.formUtil.textCues.on(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "change", function(e) {
    var fldResponse = _dcr.form.comment;
    if (responseArea.value != "" && responseArea.value != _dcr.responseCue) {
      fldResponse.value = responseArea.value;
    } else {
      fldResponse.value = responseArea.value;
    }
  });

  dojo.connect(ciStatementArea, "onfocus", function(e) {
    ambra.formUtil.textCues.off(ciStatementArea, _dcr.ciStatementTitleCue);
  });

  dojo.connect(ciStatementArea, "onblur", function(e) {
    var fldResponse = _dcr.form.ciStatement;
    if (ciStatementArea.value != "" && ciStatementArea.value != _dcr.ciStatementTitleCue) {
      fldResponse.value = ciStatementArea.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(ciStatementArea, _dcr.ciStatementTitleCue);
  });

  dojo.connect(ciStatementArea, "onchange", function(e) {
    var fldResponse = _dcr.form.ciStatement;
    if (ciStatementArea.value != "" && ciStatementArea.value != _dcr.ciStatementTitleCue) {
      fldResponse.value = ciStatementArea.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(ciStatementArea, _dcr.ciStatementTitleCue);
  });

  dojo.connect(_dcr.form.competingInterest[0], "click", function () {
    var fldTitle = _dcr.form.isCompetingInterest;

    _dcr.form.ciStatementArea.disabled = true;

    fldTitle.value = "false";
  });

  dojo.connect(_dcr.form.competingInterest[1], "click", function () {
    var fldTitle = _dcr.form.isCompetingInterest;

    _dcr.form.ciStatementArea.disabled = false;

    fldTitle.value = "true";
  });

});
