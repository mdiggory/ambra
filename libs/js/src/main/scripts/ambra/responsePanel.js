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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * ambra.responsePanel
 * 
 * This class sets up and displays the response panel in the commentary page.  It also
 * sets up and displays the flagging panel.
 **/

dojo.provide("ambra.responsePanel");
dojo.require("ambra.domUtil");
dojo.require("ambra.formUtil");
dojo.require("dojo.fx");

ambra.responsePanel = {
  togglePanel:{},
  
  upperContainer: "",
  
  newPanel: "",
  
  targetForm: "",
  
  previousNode: "",
  
  setPanel: function(panel) {
    this.newPanel = panel;
  },
  
  setForm: function(formObj) {
    this.targetForm = formObj;
  },
  
  show: function(curNode, targetObj, targetElClassName, baseId, replyId, threadTitle, actionIndex) {
    //Only display one responsePanel at a time
    this.hide();

    this.setPanel(targetObj.widget);
    this.setForm(targetObj.form);

    targetObj.baseId = (baseId) ? baseId : "";
    targetObj.replyId = (replyId) ? replyId : "";
    targetObj.actionIndex = (actionIndex) ? actionIndex : 0;

    this.upperContainer = ambra.domUtil.getFirstAncestorByClass(curNode, targetElClassName);
    this.upperContainer.style.display = "none";
    this.togglePanel.newPanel = this.newPanel;
    this.togglePanel.upperContainer = this.upperContainer;
    
    ambra.domUtil.insertAfter(this.newPanel, this.upperContainer, false);

    if (this.previousUpperContainer) this.previousUpperContainer.style.display = "block";

    if (targetObj.requestType == "flag") {
      this.resetFlaggingForm(targetObj);
    }
    
    this.newPanel.style.display = "block";

    if (threadTitle) {
      this.targetForm.responseTitle.value = 'RE: ' + threadTitle;
      this.targetForm.commentTitle.value = 'RE: ' + threadTitle;
    }

    if (this.targetForm.ciData) {
      if (this.targetForm.ciData[0].checked == true) {
        this.targetForm.ciStatementArea.disabled = true;
      }
    }
        
    this.previousUpperContainer = this.upperContainer;
  },
  
  hide: function() {
    if (this.togglePanel.newPanel) this.togglePanel.newPanel.style.display = "none";
    if (this.togglePanel.upperContainer) this.togglePanel.upperContainer.style.display = "block";
    if (this.previousUpperContainer) this.previousUpperContainer = null;
  },
  
  submit: function(targetObj) {

    //Pending where this is called from (namely the start discussion page)
    //this.targetForm may not be set.
    if(!this.targetForm) {
      this.targetForm = targetObj.form;
    }
    //Make sure the values we submit, match what we see
    if (this.targetForm.responseArea.value == this.targetForm.responseArea.title) {
      this.targetForm.comment.value = "";
    }

    if (this.targetForm.ciData) {
      if (this.targetForm.ciData[0].checked == true) {
        this.targetForm.ciStatement.value = "";
      } else {
        if (this.targetForm.ciStatementArea.value != this.targetForm.ciStatementArea.title) {
          this.targetForm.ciStatement.value = this.targetForm.ciStatementArea.value;
        }
      }
    }
    
    submitResponseInfo(targetObj);
  },
  
  resetFlaggingForm: function(targetObj) {
    this.getFlagForm();  
    this.targetForm.reasonCode[0].checked = true;
    this.targetForm.comment.value = "";
    this.targetForm.responseArea.value = targetObj.responseCue;

    //This method gets used on more then one form.  Sometimes CI will not be there
    if(this.targetForm.ciData) {
      this.targetForm.isCompetingInterest.value = "false";
      this.targetForm.ciData[0].checked = false;
      this.targetForm.ciData[1].checked = true;
      this.targetForm.ciStatementArea.value = "";
      this.targetForm.ciStatement.value = "";
    }
    
    var submitMsg = targetObj.error;
    ambra.domUtil.removeChildren(submitMsg);
  },
  
  getFlagConfirm: function() {
    dojo.byId('flagForm').style.display = "none";
    dojo.byId('flagConfirm').style.display = "block";  
  },
  
  getFlagForm: function() {
    dojo.byId('flagForm').style.display = "block";
    dojo.byId('flagConfirm').style.display = "none";  
  }
}

/**
 * clears out any error messages and then calls sendResponseInfo
 * @param targetObj
 */
function submitResponseInfo(targetObj) {
  if(targetObj.error.style.display != 'none') {
    var ani = dojo.fx.wipeOut({ node:targetObj.error, duration: 500 });
    dojo.connect(ani, "onEnd", function () { sendResponseInfo(targetObj); });
    ani.play();
  } else {
    sendResponseInfo(targetObj);
  }
}

/**
 * Send the information to the server and handle errors
 * @param targetObj
 */
function sendResponseInfo(targetObj) {
  var submitMsg = targetObj.error;
  var targetForm = targetObj.form;

  ambra.domUtil.removeChildren(submitMsg);
  //ambra.formUtil.disableFormFields(targetForm);

  var urlParam = "";
  if (targetObj.requestType == "flag"){
    urlParam = targetObj.formAction[targetObj.actionIndex] + "?target=" + targetObj.baseId;
  }
  else if (targetObj.requestType == "new"){
    urlParam = targetObj.formAction;
    ambra.formUtil.disableFormFields(targetForm);
  }
  else { 
    urlParam = targetObj.formAction + "?root=" + targetObj.baseId + "&inReplyTo=" + targetObj.replyId;
    ambra.formUtil.disableFormFields(targetForm);
  }
   
  _ldc.show();
  dojo.xhrPost({
    url: (_namespace + urlParam),
    form: targetForm,
    handleAs:'json-comment-filtered',
    error: function(response, ioArgs){
      handleXhrError(response, ioArgs);
      ambra.formUtil.enableFormFields(targetForm);
    },
    load: function(response, ioArgs){
      var jsonObj = response;
      if (jsonObj.actionErrors.length > 0) {
       var errorMsg = "";
       for (var i=0; i<jsonObj.actionErrors.length; i++) {
         errorMsg = errorMsg + jsonObj.actionErrors[i] + "\n";
       }
       var err = document.createTextNode(errorMsg);
       submitMsg.appendChild(err);
       dojo.fx.wipeIn({ node:submitMsg, duration: 500 }).play();
       ambra.formUtil.enableFormFields(targetForm);
       _ldc.hide();
     }
     else if (jsonObj.numFieldErrors > 0) {
       var fieldErrors = document.createDocumentFragment();
       for (var item in jsonObj.fieldErrors) {
         var errorString = "";
         var err = jsonObj.fieldErrors[item];
         if (err) {
           errorString += err;
           var error = document.createTextNode(errorString.trim());
           var brTag = document.createElement('br');

           fieldErrors.appendChild(error);
           fieldErrors.appendChild(brTag);
         }
       }
       submitMsg.appendChild(fieldErrors);
       dojo.fx.wipeIn({ node:submitMsg, duration: 500 }).play();
       ambra.formUtil.enableFormFields(targetForm);

       if (targetForm.ciData) {
         if (targetForm.ciData[0].checked == true) {
           targetForm.ciStatementArea.disabled = true;
         }
       }

       _ldc.hide();
     }
     else {
       if (targetObj.requestType == "flag"){
         _ldc.hide();
         ambra.responsePanel.getFlagConfirm();
       }
       else if (targetObj.requestType == "new"){
         var rootId = jsonObj.annotationId;
         window.location.href = _namespace + "/annotation/listThread.action?inReplyTo=" + rootId +"&root=" + rootId;
       }
       else {
         if (dojo.isIE)
           ambra.domUtil.insertAfter(ambra.responsePanel.togglePanel.newPanel, document.lastChild, false);
         getDiscussion(targetObj);
         ambra.responsePanel.hide();
         ambra.formUtil.textCues.reset(targetForm.responseArea, targetObj.responseCue);
         ambra.formUtil.enableFormFields(targetForm);
       }
     }
    }
   });
}

function getDiscussion(targetObj) {
  var refreshArea = dojo.byId(responseConfig.discussionContainer);

  _ldc.show();
  dojo.xhrGet({
    url: _namespace + "/annotation/listThreadRefresh.action?root=" + targetObj.baseId + "&inReplyTo=" + targetObj.baseId,
    error: function(response, ioArgs){
      handleXhrError(response, ioArgs);
    },
    load: function(response, ioArgs){
      refreshArea.innerHTML = response;
      _ldc.hide();
    }
  });
}

