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
  * ambra.displayComment
  * 
  * This object builds the dialog that displays the comments for a specific 
  * annotation bug.
  *
  * @author  Joycelyn Chung			joycelyn@orangetowers.com
  *
  **/
dojo.provide("ambra.displayComment");
dojo.require("ambra.general");
dojo.require("ambra.domUtil");
ambra.displayComment = {
  target: "",
  
  targetSecondary: "",
  
  sectionTitle: "",
  
  sectionDetail: "",
  
  sectionComment: "",

  sectionCIStatement: "",
  
  sectionLink: "",

  ciStatementMsg: "",

  noCIStatementMsg: "",
  
  retrieveMsg: "",
  
  init: function() {
    this.sectionTitle = dojo.byId(commentConfig.sectionTitle);
    this.sectionDetail  = dojo.byId(commentConfig.sectionDetail);
    this.sectionComment = dojo.byId(commentConfig.sectionComment);
    this.sectionCIStatement = dojo.byId(commentConfig.sectionCIStatement);
    this.sectionLink = dojo.byId(commentConfig.sectionLink);
    this.retrieveMsg = dojo.byId(commentConfig.retrieveMsg);
    this.ciStatementMsg = commentConfig.ciStatementMsg;
    this.noCIStatementMsg = commentConfig.noCIStatementMsg;
  },
  
  isMultiple: function(attr) {
    var attrList = this.parseAttributeToList(attr);
    
    return (attrList.length > 1) ? true : false;
  },
  
  setTarget: function(obj) {
    this.target = obj;
  },
  
  setTargetSecondary: function(obj) {
    this.targetSecondary = obj;
  },
  
  setSectionTitle: function(configObj) {
    this.sectionTitle = dojo.byId(configObj.sectionTitle);
  },
  
  setSectionDetail: function(configObj) {
    this.sectionDetail = dojo.byId(configObj.sectionDetail);
  },
  
  setSectionComment: function(configObj) {
    this.sectionComment = dojo.byId(configObj.sectionComment);
  },

  setSectionCIStatement: function(configObj) {
    this.sectionCIStatement = dojo.byId(configObj.sectionCIStatement);
  },

  setCiStatementMsg: function(configObj) {
    this.ciStatementMsg = dojo.byId(configObj.ciStatementMsg);
  },

  setNoCIStatementMsg: function(configObj) {
    this.noCIStatementMsg = dojo.byId(configObj.noCIStatementMsg);
  },

  setSectionLink: function(configObj) {
    this.sectionLink = dojo.byId(configObj.sectionLink);
  },
  
  setRetrieveMsg: function(configObj) {
    this.retrieveMsg = dojo.byId(configObj.retrieveMsg);
  },
  
  /**
   * ambra.displayComment.show(Node node)
   * 
   * Method that triggers the display of the dialog box.
   * 
   * @param    node    Node      Node where the action was triggered and the
   *                              dialog box will positioned relative to.
   * @return  false    boolean    In the link that triggered this call, sending 
   *                               false back prevents the page from forwarding.      
   */
  show: function(node){
    if(_commentDlg)
      _commentDlg.hide();
    
    if(_commentMultiDlg)
      _commentMultiDlg.hide();
    
    this.setTarget(node);
    
    _commentDlg.setMarker(this.target);
    _commentMultiDlg.setMarker(this.target);
    getComment(this.target);
    
    return false;
  },

  /**
   * ambra.displayComment.buildDisplayHeader(JSON jsonObj)
   * 
   * Builds the header of the annotation comment display dialog.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  titleDocFrag  Document fragment    Resulting document fragment created.
   */
  buildDisplayHeader: function (jsonObj) {
    var titleDocFrag = document.createDocumentFragment();
    
    // Insert title link text
    var titleLink = document.createElement('a');
    titleLink.href = _namespace + '/annotation/listThread.action?inReplyTo=' + jsonObj.annotationId + '&root=' + jsonObj.annotationId;
    titleLink.className = "discuss icon";
    titleLink.title="View full note";
    titleLink.innerHTML = jsonObj.annotation.title;
    titleDocFrag.appendChild(titleLink);

    return titleDocFrag;    
  },

  /**
   * ambra.displayComment.buildDisplayDetail(JSON jsonObj)
   * 
   * Builds the details of the annotation comment display dialog.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  detailDocFrag  Document fragment    Resulting document fragment created.
   */
  buildDisplayDetail: function (jsonObj) {
    // Insert creator detail info
    var annotationId = jsonObj.annotationId;
    var tooltipId = jsonObj.annotationId;
    //alert("tooltipId = " + tooltipId);
    
    var creatorId = jsonObj.creatorUserName;
    var creatorLink = document.createElement('a');
    creatorLink.href = _namespace + '/user/showUser.action?userId=' + jsonObj.annotation.creatorID;
//   creatorLink.title = "Annotation Author";
    creatorLink.className = "user icon";
    creatorLink.appendChild(document.createTextNode(creatorId));
    creatorLink.id = tooltipId;
    
/*    var divTooltip = document.createElement('div');
    var dojoType = document.createAttribute('dojoType');
    dojoType.value = "PostTooltip";
    divTooltip.setAttributeNode(dojoType);
    var connectId = document.createAttribute('dojo:connectId');
    connectId.value = tooltipId;
    divTooltip.setAttributeNode(connectId);
    var uniqueId = document.createAttribute('dojo:uniqId');
    uniqueId.value = "tt" + tooltipId;
    divTooltip.setAttributeNode(uniqueId);
    var contentUrl = document.createAttribute('dojo:contentUrl');
    contentUrl.value = _namespace + "/user/displayUserAJAX.action?userId=" + creatorId;
    divTooltip.setAttributeNode(contentUrl);
    var executeScripts = document.createAttribute('dojo:executeScripts');
    executeScripts.value = "true";
    divTooltip.setAttributeNode(executeScripts);
    //var caption = document.createAttribute('dojo:caption');
    //caption.value = "The tooltip";
    //divTooltip.setAttributeNode(caption);*/
    
    var userInfoDiv = document.createElement('div');
    userInfoDiv.className = "userinfo";
    //divTooltip.appendChild(userInfoDiv);
    
    var d = new Date(jsonObj.annotation.createdAsMillis);
    var MONTH_NAMES = new String('JanFebMarAprMayJunJulAugSepOctNovDec');
    var dayInt = d.getUTCDate();
    var day = (dayInt >= 10 ? "" : "0") + dayInt;
    var monthInt = d.getUTCMonth() * 3;
    var month = MONTH_NAMES.substring (monthInt, monthInt + 3);
    var year = d.getUTCFullYear();
    var hrsInt = d.getUTCHours(); 
    var hours = (hrsInt >= 10 ? "" : "0") + hrsInt;
    var minInt = d.getUTCMinutes();
    var minutes = (minInt >= 10 ? "" : "0") + minInt;
    
    var dateStr = document.createElement('strong');
    dateStr.appendChild(document.createTextNode(day + " " + month + " " + year));
    var timeStr = document.createElement('strong');
    timeStr.appendChild(document.createTextNode(hours + ":" + minutes + " GMT"));
    
    var detailDocFrag = document.createDocumentFragment();
    detailDocFrag.appendChild(document.createTextNode('Posted by '));
    detailDocFrag.appendChild(creatorLink);
    detailDocFrag.appendChild(document.createTextNode(' on '));
    detailDocFrag.appendChild(dateStr);
    detailDocFrag.appendChild(document.createTextNode(' at '));
    detailDocFrag.appendChild(timeStr);
    //detailDocFrag.appendChild(divTooltip);
    
    return detailDocFrag;
  },
  
  /**
   * ambra.displayComment.buildDisplayBody(JSON jsonObj)
   * 
   * Builds the comment body of the annotation comment display dialog.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  commentFrag    Document fragment    Resulting document fragment created.
   */
  buildDisplayBody: function (jsonObj) {
    // Insert formatted comment
    var commentFrag = document.createDocumentFragment();
    commentFrag = jsonObj.annotation.truncatedBody;
    
    return commentFrag;
  },

  /**
   * ambra.displayComment.buildDisplayCIStatement(JSON jsonObj)
   *
   * Builds the competing interest body of the annotation comment display dialog.
   *
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   *
   * @return  ciStatementFrag    Document fragment    Resulting document fragment created.
   */
  buildDisplayCIStatement: function (jsonObj) {
    // Insert formatted comment

    //If the annotation was created before the competing interest statement
    //System was implemented, don't assume the user said "No competing interest"
    //Don't display anything
    if (jsonObj.cisStartDateMillis < jsonObj.annotation.createdAsMillis) {
      var ciStatementFrag = document.createDocumentFragment();

      if (jsonObj.annotation.truncatedCompetingInterestStatement) {
        ciStatementFrag = "<div class=\"cis\"><strong>" + this.ciStatementMsg + "</strong>" + jsonObj.annotation.truncatedCompetingInterestStatement + "</div>";
      } else {
        ciStatementFrag = "<div class=\"cis\"><strong>" + this.noCIStatementMsg + "</strong></div>";
      }

      return ciStatementFrag;
    } else {
      return null;
    }
  },
  
  /**
   * ambra.displayComment.buildDisplayViewLink(JSON jsonObj)
   * 
   * Builds the link that takes the user to the discussion section.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  commentLink  Document fragment    Resulting document fragment created.
   */
  buildDisplayViewLink: function (jsonObj) {
    var commentLink = document.createElement('a');
    commentLink.href = _namespace + '/annotation/listThread.action?inReplyTo=' + jsonObj.annotationId + '&root=' + jsonObj.annotationId;
    commentLink.className = 'commentary icon';
    commentLink.title = 'Click to view full thread and respond';
    commentLink.appendChild(document.createTextNode('View/respond to this'));
    
    return commentLink;
  },
  
  /**
   * ambra.displayComment.buildDisplayView(JSON jsonObj)
   * 
   * Builds the comment dialog box for a single comment.  Empties out the inner 
   * containers if text already exists in it.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  <nothing>
   */
  buildDisplayView: function(jsonObj){
    if (ambra.displayComment.sectionTitle.hasChildNodes) ambra.domUtil.removeChildren(ambra.displayComment.sectionTitle);
    ambra.displayComment.sectionTitle.appendChild(this.buildDisplayHeader(jsonObj));
    
    if (ambra.displayComment.sectionDetail.hasChildNodes) ambra.domUtil.removeChildren(ambra.displayComment.sectionDetail);
    ambra.displayComment.sectionDetail.appendChild(this.buildDisplayDetail(jsonObj));

    //alert(commentFrag);
    if (ambra.displayComment.sectionComment.hasChildNodes) ambra.domUtil.removeChildren(ambra.displayComment.sectionComment);
    ambra.displayComment.sectionComment.innerHTML = this.buildDisplayBody(jsonObj);
    //alert("jsonObj.annotation.commentWithUrlLinking = " + jsonObj.annotation.commentWithUrlLinking);

    if (ambra.displayComment.sectionCIStatement.hasChildNodes) ambra.domUtil.removeChildren(ambra.displayComment.sectionCIStatement);

    var cisFragment = this.buildDisplayCIStatement(jsonObj);
    if (cisFragment != null) {
      ambra.displayComment.sectionCIStatement.innerHTML = cisFragment;
    }

    if (ambra.displayComment.sectionLink.hasChildNodes) ambra.domUtil.removeChildren(ambra.displayComment.sectionLink);
    this.sectionLink.appendChild(this.buildDisplayViewLink(jsonObj));
    
    // set correction related styling
    var cmtId = dojo.byId(commentConfig.cmtContainer);
    if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeMinorCorrection) >= 0) {
      // minor correction
      dojo.addClass(cmtId, annotationConfig.styleMinorCorrection);
    }
    else if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeFormalCorrection) >= 0) {
      // formal correction
      dojo.addClass(cmtId, annotationConfig.styleFormalCorrection);
    }
    else if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeRetraction) >= 0) {
      // retraction of the article
      dojo.addClass(cmtId, annotationConfig.styleRetraction);
    }
    else {
      dojo.removeClass(cmtId, annotationConfig.styleMinorCorrection);
      dojo.removeClass(cmtId, annotationConfig.styleFormalCorrection);
      dojo.removeClass(cmtId, annotationConfig.styleRetraction);
    }
  },
  
  /**
   * ambra.displayComment.buildDisplayViewMultiple(JSON jsonObj)
   * 
   * Builds the comment dialog box for a multiple comments.  Empties out the inner 
   * containers if text already exists in it.
   * 
   * @param    jsonObj        JSON object          JSON object containing the data that
   *                                                retrieved from the database.
   * 
   * @return  <nothing>
   */
  buildDisplayViewMultiple: function(jsonObj, iter, container, secondaryContainer){
    var newListItem = document.createElement('li');
    
    if (iter <= 0)
      newListItem.className = 'active';

    var titleNode = document.createElement('span');
    titleNode.innerHTML = jsonObj.annotation.title;
    
    newListItem.appendChild(titleNode);
    //newListItem.appendChild(this.buildDisplayHeader(jsonObj));
    var detailDiv = document.createElement('div');
    detailDiv.className = 'detail';
    detailDiv.appendChild(this.buildDisplayDetail(jsonObj)); 
    newListItem.appendChild(detailDiv);   
    
    var contentDiv = document.createElement('div');
    if (iter <=0)
      contentDiv.className = 'contentwrap active';
    else
      contentDiv.className = 'contentwrap';

    // set correction related styling
    if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeMinorCorrection) >= 0) {
      // minor correction
      dojo.addClass(newListItem, annotationConfig.styleMinorCorrection);
      contentDiv.className += ' ' + annotationConfig.styleMinorCorrection;
    }
    else if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeFormalCorrection) >= 0) {
      // formal correction
      dojo.addClass(newListItem, annotationConfig.styleFormalCorrection);
      contentDiv.className += ' ' + annotationConfig.styleFormalCorrection;
    }
    else if(jsonObj.annotation.type.indexOf(annotationConfig.annTypeRetraction) >= 0) {
      // retraction of this article
      dojo.addClass(newListItem, annotationConfig.styleRetraction);
      contentDiv.className += ' ' + annotationConfig.styleRetraction;
    }

    var cisFragment = this.buildDisplayCIStatement(jsonObj);

    if (cisFragment != null) {
      contentDiv.innerHTML = this.buildDisplayBody(jsonObj) + cisFragment;
    } else {
      contentDiv.innerHTML = this.buildDisplayBody(jsonObj);
    }
    
    var cDetailDiv = document.createElement('div');
    cDetailDiv.className = 'detail';
    /*var commentLink = document.createElement('a');
    commentLink.href = '#';
    commentLink.className = 'commentary icon';
    commentLink.title = 'Click to view full thread and respond';
    commentLink.appendChild(document.createTextNode('View full commentary'));
    
    var responseLink = document.createElement('a');
    responseLink.href = '#';
    responseLink.className = 'respond tooltip';
    responseLink.title = 'Click to respond to this posting';
    responseLink.appendChild(document.createTextNode('Respond to this'));
    
    cDetailDiv.appendChild(commentLink);
    cDetailDiv.appendChild(responseLink);*/
    cDetailDiv.appendChild(this.buildDisplayViewLink(jsonObj));
    contentDiv.appendChild(cDetailDiv);

    if (iter <= 0) {
      container.appendChild(newListItem);
      secondaryContainer.appendChild(contentDiv);
    }
    else {
      var liList = ambra.domUtil.getChildElementsByTagAndClassName(container, 'li', null);
      ambra.domUtil.insertAfter(newListItem, liList[liList.length - 1]);
    
      var divList = ambra.domUtil.getChildElementsByTagAndClassName(secondaryContainer, 'div', null);
      ambra.domUtil.insertAfter(contentDiv, divList[divList.length - 1]);
    }

    var multiDetailDivChild = secondaryContainer.childNodes[secondaryContainer.childNodes.length - 1];
    newListItem.onclick = function() {
        ambra.displayComment.mouseoutComment(ambra.displayComment.target);
        ambra.displayComment.mouseoverComment(ambra.displayComment.target, jsonObj.annotationId);
        ambra.domUtil.swapClassNameBtwnSibling(this, this.nodeName, 'active');
        ambra.domUtil.swapClassNameBtwnSibling(multiDetailDivChild, multiDetailDivChild.nodeName, 'active');
        ambra.domUtil.swapAttributeByClassNameForDisplay(ambra.displayComment.target, ' active', jsonObj.annotationId);
        
        ambra.displayComment.adjustDialogHeight(container, secondaryContainer, 50);
      }
  },
  
  /**
   * ambra.displayComment.mouseoverComment(Node obj, String displayId)
   * 
   * This method gets a map of all element nodes that contain the same display ID
   * and iterates through the map and modifies the classname to show highlight.
   * 
   * @param    obj          Node object        Source element to start highlight.
   * @param    displayId    String            Id reference for display.
   * 
   * @return  <nothing>
   */
  mouseoverComment: function (obj, displayId) {
   var elementList = ambra.domUtil.getDisplayMap(obj, displayId);
   
   // Find the displayId that has the most span nodes containing that has a 
   // corresponding id in the annotationId attribute.  
   var longestAnnotElements;
   for (var i=0; i<elementList.length; i++) {
     if (i == 0) {
       longestAnnotElements = elementList[i];
     }
     else if (elementList[i].elementCount > elementList[i-1].elementCount){
       longestAnnotElements = elementList[i];
     }
   }
   
   //this.modifyClassName(obj);
   
   // the annotationId attribute, modify class name.
   for (var n=0; n<longestAnnotElements.elementCount; n++) {
     var classList = new Array();
     var elObj = longestAnnotElements.elementList[n];

     this.modifyClassName(elObj);
     
     if (n == 0) {
       var bugObj = ambra.domUtil.getChildElementsByTagAndClassName(elObj, 'a', 'bug');
       
       for (var i=0; i<bugObj.length; i++) {
         this.modifyClassName(bugObj[i]);
       }
     }
   }

  },

  /**
   * ambra.displayComment.mouseoutComment(Node obj) 
   * 
   * Resets span tags that were modified to highlight to no highlight.
   * 
   * @param    obj    Node object        Object needed to be reset.
   */
  mouseoutComment: function (obj) {
    var elList = document.getElementsByTagName('span');
    
    for(var i=0; i<elList.length; i++) {
      elList[i].className = elList[i].className.replace(/\-active/, "");
    }
    obj.className = obj.className.replace(/\-active/, "");
  },
  
  /**
   * ambra.displayComment.modifyClassName(Node obj)
   * 
   * Modifies the className
   * 
   * @param    obj    Node object    Source node.
   */
  modifyClassName: function (obj) {
     classList = obj.className.split(" ");
     for (var i=0; i<classList.length; i++) {
       if ((classList[i].match('public') || classList[i].match('private')) && !classList[i].match('-active')) {
         classList[i] = classList[i].concat("-active");
       }
     }
     
     obj.className = classList.join(' ');
  },
  
  /**
   * ambra.displayComment.processBugCount()
   * 
   * Searches the document for tags that has the classname of "bug" indicating
   * that it's an annotation bug.  Looks at the node id which should have a list
   * of IDs corresponding to an annotation.  This ID list is counted and the 
   * result is shown in the bug.
   */
  processBugCount: function () {
    var bugList = document.getElementsByTagAndClassName(null, 'bug');
    
    for (var i=0; i<bugList.length; i++) {
      var bugCount = ambra.domUtil.getDisplayId(bugList[i]);
      var spn = document.createElement('span');

      if (bugCount != null) {
        var displayBugs = bugCount.split(',');
        var count = displayBugs.length;
        var ctText = document.createTextNode(count);
      }
      else {
        var ctText = document.createTextNode('0');
      }
      spn.appendChild(ctText);
      ambra.domUtil.removeChildren(bugList[i]);
      bugList[i].appendChild(spn);
    }
  },
  
  /**
   * ambra.displayComment.adjustDialogHeight(Node container1, Node container2, Integer addPx)
   * 
   * The height of the margin box of container1 and container2 are compared and
   * the height are adjusted accordingly.  
   * 
   * @param    container1    Node object      Container node object.
   * @param    container2    Node object      Container node object.
   * @param    addPx          Integer          Pixel amount to adjust height.
   */
  adjustDialogHeight: function(container1, container2, addPx) {
    var container1Mb = dojo._getMarginBox(container1).h;
    var container2Mb = dojo._getMarginBox(container2).h;
    
    if (container1Mb > container2Mb) {
      container1.parentNode.style.height = (container1Mb + addPx) + "px";
      
      var contentDivs = ambra.domUtil.getChildElementsByTagAndClassName(container2, 'div', 'contentwrap');
      for (var i=0; i<contentDivs.length; i++) {
        if (contentDivs[i].className.match('active')) {
          contentDivs[i].style.height = (container1Mb - container1Mb/(3.59*contentDivs.length)) + "px";
          //contentDivs[i].style.backgroundColor = "#fff";
        }
      }
    }
    else
      container1.parentNode.style.height = (container2Mb + addPx) + "px";
      // TODO jpk dojo1.1 - do we need this?
      //_commentMultiDlg.placeModalDialog();
  }
}

function getComment(obj) {
    _ldc.show();
    var targetUri = ambra.domUtil.getDisplayId(obj);
    var uriArray = targetUri.split(",");

    if (uriArray.length > 1) {
      var targetContainer = document.getElementById('multilist');
      ambra.domUtil.removeChildren(targetContainer);
      var targetContainerSecondary = document.getElementById('multidetail');
      ambra.domUtil.removeChildren(targetContainerSecondary);
    }
    else {
      var targetContainer =  dijit.byId("CommentDialog");
    }
    
    var maxShown = 4;
    var stopPt = (uriArray.length < maxShown) ? uriArray.length : maxShown;
    
    var count = 0;
    
    for (var i=0; i<stopPt; i++) {
      //alert("uriArray[" + i + "] = " + uriArray[i]);
      dojo.xhrGet({
        url: _namespace + "/annotation/getAnnotation.action?annotationId=" + uriArray[i],
        handleAs:'json-comment-filtered',
        error: function(response, ioArgs){
          handleXhrError(response, ioArgs);
        },
        load: function(response, ioArgs){
         _ldc.hide();
         var jsonObj = response;
         if (jsonObj.actionErrors.length > 0) {
           var errorMsg = "";
           for (var i=0; i<jsonObj.actionErrors.length; i++) {
             errorMsg = errorMsg + jsonObj.actionErrors[i] + "\n";
           }
           alert("ERROR [actionErrors]: " + errorMsg);
         }
         else if (jsonObj.numFieldErrors > 0) {
           var fieldErrors;
           //alert("jsonObj.numFieldErrors = " + jsonObj.numFieldErrors);
           for (var item in jsonObj.fieldErrors) {
             var errorString = "";
             for (var i=0; i<jsonObj.fieldErrors[item].length; i++) {
               errorString += jsonObj.fieldErrors[item][i];
             }
             fieldErrors = fieldErrors + item + ": " + errorString + "<br/>";
           }
           alert("ERROR [numFieldErrors]: " + fieldErrors);
         }
         else {
            var isMulti = (uriArray.length > 1);
           if(isMulti) {             
             ambra.displayComment.buildDisplayViewMultiple(jsonObj, targetContainer.childNodes.length, targetContainer, targetContainerSecondary);
             
             if (targetContainer.childNodes.length == stopPt) {
               ambra.displayComment.mouseoverComment(ambra.displayComment.target, uriArray[0]);
                
               _commentMultiDlg.show();

               ambra.displayComment.adjustDialogHeight(targetContainer, targetContainerSecondary, 50);
             }
           }
           else {
             ambra.displayComment.buildDisplayView(jsonObj);
             ambra.displayComment.mouseoverComment(ambra.displayComment.target);
             _commentDlg.show();
           }
         }
        }
       });
    }

  }
