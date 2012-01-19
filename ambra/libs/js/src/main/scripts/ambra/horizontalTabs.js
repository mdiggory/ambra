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
 * ambra.horizontalTabs
 * 
 * The horizontal tabs are the secondary navigation that can be found on the
 * home page and the profile page.  This class uses a map object set in the 
 * configuration file that will be used for building the tabs.  This map
 * object contains key-value pairs, for example,
 * 			tabKey:    "recentlyPublished",
 *      title:     "Recently Published",
 *      className: "published",
 *      urlLoad:   "/article/recentArticles.action",
 *      urlSave:   ""
 * 
 * These values are set for each tab.  Using the setters, initialize the 
 * tab in the page init.js files.
 **/


dojo.provide("ambra.horizontalTabs");
dojo.require("ambra.general");
dojo.require("ambra.formUtil");
ambra.horizontalTabs = {
  
  proceedFlag:false,
  tempValue:'',
  changeFlag:false,
  
  tabPaneSet: "",
  
  tabsListObject: "",
  
  tabsContainer: "",
  
  targetFormObj: "",
  
  targetObj: "",
  
  newTarget: "",
  
  setTabPaneSet: function (obj) {
    this.tabPaneSet = obj;
  },
  
  setTabsListObject: function (listObj) {
    this.tabsListObject = listObj;
  },
  
  setTabsContainer: function (listObj) {
    this.tabsContainer = listObj;
  },
  
  setTargetFormObj: function (formObj) {
    this.targetFormObj = formObj;
  },
  
  setTargetObj: function (targetObj) {
    this.targetObj = targetObj;
  },
  
  setNewTarget: function (newTarget) {
    this.newTarget = newTarget;
  },
  
  getMapObject: function (value) {
    if (value) {
      for (var i=0; i<this.tabsListObject.length; i++) {
        if (this.tabsListObject[i].tabKey == value)
          return this.tabsListObject[i];
      }
    }
    else {
      return this.tabsListObject[0];
    }
  },
  
  init: function(initId) {
    var targetObj;
    
    if (initId)
      targetObj = this.getMapObject(initId);
    else 
      targetObj = this.getMapObject();
    
    this.buildTabs(targetObj);
    this.tabSetup(targetObj);
    //this.attachFormEvents(formObj);
  },
  
  initSimple: function(initId) {
    var targetObj;
    
    if (initId)
      targetObj = this.getMapObject(initId);
    else 
      targetObj = this.getMapObject();
    
    this.buildTabsHome(targetObj);
    if(targetObj != null) this.setTargetObj(targetObj);
  },
  
  initUsers: function(initId) {
    var targetObj;

    if (initId)
      targetObj = this.getMapObject(initId);
    else
      targetObj = this.getMapObject();

    this.buildTabsUsers(targetObj);
    this.tabSetup(targetObj);
  },

  tabSetup: function (targetObj) {
    this.setTargetObj(targetObj);
    
    var formName = this.targetObj.formName;
    var formObj = document.forms[formName];
    
    this.setTargetFormObj(formObj);
    //ambra.formUtil.createHiddenFields(this.targetFormObj);
    
    //alert("formObj.formSubmit = " + formObj.formSubmit.value);
    /*dojo.connect(formObj.formSubmit, "onclick", function() {
        //alert("tabKey = " + ambra.horizontalTabs.targetObj.tabKey);
        submitContent(ambra.horizontalTabs.targetObj);
      }
    );*/
    
    formObj.formSubmit.onclick = function () {
        submitContent();
      }
  },

  setTempValue: function (obj) {
    if (obj.type == "radio") {
      var radioName = obj.name;
      
      var radioObjs = obj.form.elements[radioName];
        
      for (var n=0; n<radioObjs.length; n++) {
        if (radioObjs[n].checked) {
          this.tempValue = radioObjs[n].value;
        }
      }
    }
    else if (obj.type == "checkbox") {
      var checkboxName = obj.name;
      
      var checkboxObjs = obj.form.elements[checkboxName];
      
      if (checkboxObjs.length) {  
        for (var n=0; n<checkboxObjs.length; n++) {
          if (checkboxObjs[n].checked) {
            this.tempValue = checkboxObjs[n].value;
          }
        }
      }
      else {
        this.tempValue = checkboxObjs.checked;
      }
    }
    else if (obj.type == "select-one") {
      //alert("formObj.elements[" + i + "][" + obj.selectedIndex + "].value = " + obj[obj.selectedIndex].value);
      this.tempValue = obj[obj.selectedIndex].value; 
    }
    else {
      this.tempValue = obj.value;
    }
    
    //alert("tempValue = " + tempValue);
  },
  
  checkValue: function (obj) {
    //alert("obj = " + obj.type);
    if (obj.type == "radio") {
      var radioName = obj.name;
      
      var radioObjs = obj.form.elements[radioName];

      //alert("obj.form.elements[" + checkboxName + "].length = " + obj.form.elements[checkboxName].toSource());
      for (var n=0; n<radioObjs.length; n++) {
        if (radioObjs[n].checked) {
          if (this.tempValue != radioObjs[n].value)
            this.changeFlag = true;
        }
      }
    }
    else if (obj.type == "checkbox") {
      var checkboxName = obj.name;
      
      var checkboxObjs = obj.form.elements[checkboxName];
        
      //alert("obj.form.elements[" + checkboxName + "].length = " + obj.form.elements[checkboxName].toSource());
      if (checkboxObjs.length) {
        for (var n=0; n<checkboxObjs.length; n++) {
          if (checkboxObjs[n].checked) {
            if (this.tempValue != checkboxObjs[n].value)
              this.changeFlag = true;
          }
        }
      }
      else {
        if (this.tempValue != checkboxObjs.checked) 
          this.changeFlag = true;
      }
    }
    else if (obj.type == "select-one") {
      //alert("formObj.elements[" + i + "][" + obj.selectedIndex + "].value = " + obj[obj.selectedIndex].value);
      if (this.tempValue != obj[obj.selectedIndex].value)
        this.changeFlag = true;
    }
    else {
      if (this.tempValue != obj.value)
        this.changeFlag = true;
    }
    
    //alert("changeFlag = " + changeFlag);
  },
  
  attachFormEvents: function (formObj) {
    ambra.horizontalTabs.tempValue = "";

    for (var i=0; i<formObj.elements.length; i++) {
      if (formObj.elements[i].type != 'hidden') {
        var formName = formObj.name;
        var fieldName = formObj.elements[i].name;
        //alert("formName = " + formName + "\n" +
        //      "fieldName = " + fieldName);
        dojo.connect(document.forms[formName].elements[fieldName], "onfocus", function() {
        //    alert("tempValue = " + tempValue + "\n" +
        //          "this.id = " + document.forms[formName].elements[fieldName].value);
            ambra.horizontalTabs.tempValue = this.value;
          }  
        );

        dojo.connect(formObj.elements[i], "onchange", function() {
        //    alert("tempValue = " + tempValue + "\n" +
        //          "this.value = " + this.value);
          
            if (ambra.horizontalTabs.tempValue == this.value) 
              ambra.horizontalTabs.changeFlag = true;
          }  
        );
      }
    }
  },
  
  buildTabs: function(obj) {
    for (var i=0; i<this.tabsListObject.length; i++) {
      var li = document.createElement("li");
      li.id = this.tabsListObject[i].tabKey;
      if (obj.className) li.className = obj.className;
      if (this.tabsListObject[i].tabKey == obj.tabKey) {
        //li.className = li.className.concat(" active");
        dojo.addClass(li, "active");
      }
      li.onclick = function () { 
          ambra.horizontalTabs.show(this.id);
          return false; 
        }
      li.appendChild(document.createTextNode(this.tabsListObject[i].title));

      this.tabsContainer.appendChild(li);
    }
    
    this.tempValue = "";
  },
  
  buildTabsHome: function(obj) {
    for (var i=0; i<this.tabsListObject.length; i++) {
      var li = document.createElement("li");
      li.id = this.tabsListObject[i].tabKey;
      if (this.tabsListObject[i].className) li.className = this.tabsListObject[i].className;
      if (obj != null && this.tabsListObject[i].tabKey == obj.tabKey) {
        dojo.addClass(li, "active");
      }
      li.onclick = function () { 
          ambra.horizontalTabs.showHome(this.id);
          return false; 
        }
      var span = document.createElement("span");
      span.appendChild(document.createTextNode(this.tabsListObject[i].title));
      li.appendChild(span);

      this.tabsContainer.appendChild(li);
    }
  },

  buildTabsUsers: function(obj) {
    for (var i=0; i<this.tabsListObject.length; i++) {
      var li = document.createElement("li");
      li.id = this.tabsListObject[i].tabKey;
      if (this.tabsListObject[i].className) li.className = this.tabsListObject[i].className;
      if (obj != null && this.tabsListObject[i].tabKey == obj.tabKey) {
        dojo.addClass(li, "active");
      }
      li.onclick = function () {
          ambra.horizontalTabs.show(this.id);
          return false;
        }
      var span = document.createElement("span");
      span.appendChild(document.createTextNode(this.tabsListObject[i].title));
      li.appendChild(span);

      this.tabsContainer.appendChild(li);
    }
  },

  toggleTab: function(obj) {
    for (var i=0; i<this.tabsListObject.length; i++) {
      var tabNode = dojo.byId(this.tabsListObject[i].tabKey);
      
      if (tabNode.className.match("active"))
        dojo.removeClass(tabNode, "active");
        //tabNode.className = tabNode.className.replace(/active/, "").trim();
    }
    
    var targetNode = dojo.byId(obj.tabKey);
    dojo.addClass(targetNode, "active");
    //targetNode.className = targetNode.className.concat(" active");
  },
  
  confirmChange: function (formObj) {
    //var isChanged = false;
    //isChanged = ambra.formUtil.hasFieldChange(ambra.horizontalTabs.targetFormObj);
   
    //alert("[confirmChange] changeFlag = " + changeFlag);
    if (this.changeFlag) {
      var warning = confirm("You have made changes, are you sure you want to leave this tab without saving?  If you want to proceed, click \"OK\".  Otherwise click \"Cancel\" to go to save.");
      
      this.proceedFlag = warning;
    }
    else {
      this.proceedFlag = true;
    }
  },
    
  getContent: function() {
    if (!this.proceedFlag) {
      _ldc.hide();
  
      this.targetFormObj.formSubmit.focus();
      return false;
    }
    else {
      //ambra.formUtil.removeHiddenFields(this.targetFormObj);
      loadContent(this.newTarget);
    }
  },

  saveContent: function(targetId) {
    var newTarget = this.getMapObject(targetId);
    
    submitContent(newTarget);
  },

  show: function(id) {
    var newTarget = this.getMapObject(id);
    this.setNewTarget(newTarget);
    _ldc.show();
    this.confirmChange();
    
    setTimeout("getContentFunc()", 1000);
  },
  
  showHome: function(id) {
    var newTarget = this.getMapObject(id);
    this.setNewTarget(newTarget);
    
    loadContentHome(newTarget);
  }
  
}  

function getContentFunc () {
  ambra.horizontalTabs.getContent();
}

function loadContent(targetObj) {
  var refreshArea = dojo.byId(profileConfig.tabPaneSetId);
  var targetUri = targetObj.urlLoad + "?tabId=" + targetObj.tabKey;

  _ldc.show();
  dojo.xhrGet({
    url: _namespace + targetUri,
    handleAs:'text',
    headers: { "AJAX_USER_AGENT": "Dojo/" +  dojo.version },
    error: function(response, ioArgs){
      handleXhrError(response, ioArgs);
    },
    load: function(response, ioArgs){
      refreshArea.innerHTML = response;
      ambra.horizontalTabs.toggleTab(targetObj);
      ambra.horizontalTabs.tabSetup(targetObj);
      ambra.horizontalTabs.tempValue = "";
      ambra.horizontalTabs.changeFlag = false;
      ambra.horizontalTabs.proceedFlag = true;
      _ldc.hide();
    }
  });
}  

function loadContentHome(targetObj) {
  var refreshArea = dojo.byId(homeConfig.tabPaneSetId);
  var targetUri = targetObj.urlLoad;

  dojo.xhrGet({
    url: _namespace + targetUri,
    headers: { "AJAX_USER_AGENT": "Dojo/" +  dojo.version },
    error: function(response, ioArgs){
      handleXhrError(response, ioArgs);
    },
    load: function(response, ioArgs){
      refreshArea.innerHTML = response;
      ambra.horizontalTabs.setTargetObj(targetObj);
      ambra.horizontalTabs.toggleTab(targetObj);
    }
  });
}  

function submitContent() {
  var refreshArea = dojo.byId(profileConfig.tabPaneSetId);
  var srcObj = ambra.horizontalTabs.targetObj;
  var targetUri = srcObj.urlSave;
  
  var formObj = document.forms[srcObj.formName];
  var formValueObj = ambra.formUtil.createFormValueObject(formObj);
  
  _ldc.show();
  dojo.xhrPost({
    url: _namespace + targetUri,
    content: formValueObj,
    headers: { "AJAX_USER_AGENT": "Dojo/" +  dojo.version },
    error: function(response, ioArgs){
      handleXhrError(response, ioArgs);
    },
    load: function(response, ioArgs){
      ambra.horizontalTabs.tabSetup(srcObj);
      refreshArea.innerHTML = response;
      ambra.horizontalTabs.tempValue = "";
      ambra.horizontalTabs.changeFlag = false;
      
      var formObj = document.forms[srcObj.formName];
      
      formObj.formSubmit.onclick = function () { submitContent(); }
      
      var errorNodes = document.getElementsByTagAndClassName(null, "form-error");
      
      if (errorNodes.length >= 0)
        jumpToElement(errorNodes[0]);
      else
        jumpToElement(errorNodes);
        
      _ldc.hide();
    }
  });
}  

