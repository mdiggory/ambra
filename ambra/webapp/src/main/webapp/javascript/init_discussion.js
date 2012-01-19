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
var _dcf = new Object();
var _ldc;

dojo.addOnLoad( function() {
  _ldc = dijit.byId("LoadingCycle");

  _dcr.widget = dojo.byId("DiscussionPanel");
  _dcr.widget.style.display = "none";
  _dcr.btnCancel = dojo.byId("btnCancelResponse");
  _dcr.btnSubmit = dojo.byId("btnPostResponse");
  _dcr.form = document.discussionResponse;
  _dcr.formAction = "/annotation/secure/createReplySubmit.action";
  _dcr.responseTitleCue = "Enter your response title...";
  _dcr.ciStatementTitleCue = "Enter your competing interests...";
  _dcr.responseCue = "Enter your response...";
  _dcr.error = dojo.byId('responseSubmitMsg');
  _dcr.requestType = "response";
  var responseTitle = _dcr.form.responseTitle;
  var responseArea = _dcr.form.responseArea;
  var ciStatementArea = _dcr.form.ciStatementArea;

  dojo.connect(_dcr.btnCancel, "onclick", function(e) {
    ambra.responsePanel.hide(_dcr.widget);
    var submitMsg = _dcr.error;
    ambra.domUtil.removeChildren(submitMsg);
  });

  dojo.connect(_dcr.btnSubmit, "onclick", function(e) {
    ambra.responsePanel.submit(_dcr);
  });

  dojo.connect(responseTitle, "onfocus", function(e) {
    ambra.formUtil.textCues.off(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "onfocus", function(e) {
    ambra.formUtil.textCues.off(responseArea, _dcr.responseCue);
  });

  dojo.connect(responseTitle, "onblur", function(e) {
    var fldResponseTitle = _dcr.form.commentTitle;
    if (responseTitle.value != "" && responseTitle.value != _dcr.responseTitleCue) {
      fldResponseTitle.value = responseTitle.value;
    } else {
      fldResponseTitle.value = "";
    }

    ambra.formUtil.textCues.on(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "onblur", function(e) {
    var fldResponse = _dcr.form.comment;
    if (responseArea.value != "" && responseArea.value != _dcr.responseCue) {
      fldResponse.value = responseArea.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(responseArea, _dcr.responseCue);
  });

  dojo.connect(responseTitle, "onchange", function(e) {
    var fldResponseTitle = _dcr.form.commentTitle;
    if (responseTitle.value != "" && responseTitle.value != _dcr.responseTitleCue) {
      fldResponseTitle.value = responseTitle.value;
    } else {
      fldResponseTitle.value = "";
    }

    ambra.formUtil.textCues.on(responseTitle, _dcr.responseTitleCue);
  });

  dojo.connect(responseArea, "onchange", function(e) {
    var fldResponse = _dcr.form.comment;
    if (responseArea.value != "" && responseArea.value != _dcr.responseCue) {
      fldResponse.value = responseArea.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(responseArea, _dcr.responseCue);
  });

  _dcf.widget = dojo.byId("FlaggingPanel");
  _dcf.widget.style.display = "none";
  _dcf.btnCancel = dojo.byId("btnCancel");
  _dcf.btnSubmit = dojo.byId("btnSubmit");
  _dcf.btnFlagClose = dojo.byId("btnFlagConfirmClose");
  _dcf.form = document.discussionFlag;
  _dcf.formAction = new Array("/annotation/secure/createAnnotationFlagSubmit.action",
      "/annotation/secure/createReplyFlagSubmit.action",
      "/rate/secure/createRatingFlagSubmit.action");
  _dcf.responseCue = "Add any additional information here...";
  _dcf.error = dojo.byId('flagSubmitMsg');
  _dcf.requestType = "flag";
  var responseAreaFlag = _dcf.form.responseArea;

  dojo.connect(_dcf.btnCancel, "onclick", function(e) {
    ambra.responsePanel.hide();
  });

  dojo.connect(_dcf.btnFlagClose, "onclick", function(e) {
    ambra.responsePanel.hide();
    ambra.responsePanel.resetFlaggingForm(_dcf);
  });

  dojo.connect(_dcf.btnSubmit, "onclick", function(e) {
    ambra.responsePanel.submit(_dcf);
  });

  dojo.connect(responseAreaFlag, "onfocus", function(e) {
    ambra.formUtil.textCues.off(responseAreaFlag, _dcf.responseCue);
  });

  dojo.connect(responseAreaFlag, "onblur", function(e) {
    var fldResponse = _dcf.form.comment;
    if (responseAreaFlag.value != "" && responseAreaFlag.value != _dcf.responseCue) {
      fldResponse.value = responseAreaFlag.value;
    } else {
      fldResponse.value = "";
    }
    ambra.formUtil.textCues.on(responseAreaFlag, _dcf.responseCue);
  });

  dojo.connect(responseAreaFlag, "onchange", function(e) {
    var fldResponse = _dcf.form.comment;
    if (responseAreaFlag.value != "" && responseAreaFlag.value != _dcf.responseCue) {
      fldResponse.value = responseAreaFlag.value;
    } else {
      fldResponse.value = "";
    }
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

});
