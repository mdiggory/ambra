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
dojo.provide("ambra.displayAnnotationContext");
dojo.require("ambra.widget.ContextAction");
dojo.require("ambra.annotation");

var contextActionTT = null;
var contextActionEventConnectors = [];

var annotationContextConstants = {
  topNodeId:"researchArticle",
  excludeSelection:"noSelect",
  ignoreSelection:"noDialog",

  badSelection:"badSelection",
  badDocumentArea:"badDocumentArea",
  badRangeSelection:"badRangeSelection",
  badElementSelection:"badElementSelection"
};

ambra.displayAnnotationContext = {
  target:"",
  mouseUpEvent:0,
  mouseDownEvent:1,
  selectionError:"",

  /**
   * Initializes the context menu
   */
  init:function(id) {
    console.log("init called:" + id);
    this.target = id;
    this.connect();
    console.log("onmouseupconnected:" + contextActionEventConnectors[this.mouseUpEvent]);
  },

  /**
   * Disconnect events
   */
  disconnect:function(e) {
    dojo.disconnect(contextActionEventConnectors[this.mouseUpEvent]);
    dojo.disconnect(contextActionEventConnectors[this.mouseDownEvent]);
  },

  /**
   * Connect events
   */
  connect:function() {
    //onmouseUp over the body, called the startSelect method
    contextActionEventConnectors[this.mouseUpEvent] = dojo.connect(document.body, "onmouseup", ambra.displayAnnotationContext, "startSelect");

    //onmouseDown close any existing context menus
    contextActionEventConnectors[this.mouseDownEvent] = dojo.connect(document.body, "onmousedown", ambra.displayAnnotationContext, "closeContext");
  },

  /**
   * Called onmouseup of the comment button
   * Disconnect all events and display the annotation dialog box
   */
  startComment:function(e) {
    console.log("startComment called:");

    //Disable new popups while the createAnnotation Window is open
    this.disconnect(e);

    this.closeContext();

    createAnnotationOnMouseDown();
  },

  cancelContext:function(evt)
  {
    ambra.displayAnnotationContext.closeContext(evt);
    evt.cancelBubble=true;

    return false;
  },

  /**
   * Close the context menu if it is open
   */
  closeContext:function(e) {
    if (contextActionTT) {
      contextActionTT.close();
    }
  },

  /**
   * When a text range spans elements
   * For validation we need to inspect each
   * Element individually.  This method takes the range
   * And makes a mini dom in a browser agnostic way
   * that can be inspected
   */
  getSelectionFragment:function(textRange) {
      var selectedFragment = document.createDocumentFragment();

      if (dojo.isIE) {
        //IE
        var html = textRange.range.htmlText;
        var tempNode = document.createElement("div");

        tempNode.innerHTML = html;
        ambra.domUtil.copyChildren(tempNode, selectedFragment);
      } else if (dojo.isSafari) {
        //Insertion for Safari
        selectedFragment = textRange.range.cloneContents();
      } else {
        // Insertion for Firefox
        selectedFragment = textRange.range.cloneContents();
      }

      return selectedFragment;
  },

  /**
   * Return a range object in a browser agnostic format
   * Dojo has some support for this and at a later date we should
   * migrate to using their implementation
   */
  getRangeOfSelection:function() {
    var rangeInfo = new Object();

    if (dojo.isIE) {
      //IE
      rangeInfo = this.findIeRange();
    } else if (window.getSelection || document.getSelection) {
      //Gecko
      rangeInfo = this.findGeckoRange();
    } else {
      //TODO: Unsupported Browser we should really exception.  TBD: error handling mechansim
      return null;
    }

    return rangeInfo;
  },

  /**
   * Return a range object in a browser agnostic format
   * Dojo has some support for this and at a later date we should
   * migrate to using their implementation
   */
  findIeRange: function() {
    if (document.selection.type == "Text") {
      var range = document.selection.createRange();

      if(range.text.length == 0) {
        return null;
      }

      var startRange = range.duplicate();
      var endRange = startRange.duplicate();

      startRange.collapse(true);
      endRange.collapse(false);

      var ieRange = {
          range:range,
          startParent:startRange.parentElement(),
          endParent:endRange.parentElement(),
          selection:document.selection
      };

      return ieRange;

    } else {
      //Selection spans invalid objects, just return null
      return null;
    }
  },

  /**
   * Return a range object in a browser agnostic format
   * Dojo has some support for this and at a later date we should
   * migrate to using their implementation
   */
  findGeckoRange: function() {
    var rangeSelection = window.getSelection ? window.getSelection() :
                         document.getSelection ? document.getSelection() : 0;

    console.log("Inside findMozillaRange");

    if (rangeSelection != "" && rangeSelection != null) {
      console.log("Inside findMozillaRange");
      var range;

      // Firefox
      if (typeof rangeSelection.getRangeAt != "undefined") {
         range = rangeSelection.getRangeAt(0);
      }

      // Safari
      else if (typeof rangeSelection.baseNode != "undefined") {
        range = window.createRange ? window.createRange() :
                     document.createRange ? document.createRange() : 0;

        range.setStart(rangeSelection.baseNode, rangeSelection.baseOffset);
        range.setEnd(rangeSelection.extentNode, rangeSelection.extentOffset);

        if (range.collapsed) {
          range.setStart(rangeSelection.extentNode, rangeSelection.extentOffset);
          range.setEnd(rangeSelection.baseNode, rangeSelection.baseOffset);
        }
      }

      //element-based selection range validation if applicable
      if (!this.validateLists(range)) {
        return null;
      }

      var geckoRange = {
        range:range,
        startParent:range.startContainer,
        endParent:range.endContainer,
        selection:rangeSelection
      };

      return geckoRange;
    }
  },

  /**
   * If items selected are part of a list validate that it's a valid selection
   */
  validateLists: function(range) {
    if (document.selection && document.selection.createRange) {
      // IE
      // textualize
      // NOTE: IE's range.findText method will by design return false
      // if the selection spans over multiple elements when the selection range is element based
      if(!range.findText(range.text, 0, 0)) {
        this.selectionError = annotationContextConstants.badSelection;
        return false;
      }
    } else if (window.getSelection) {
      // Gecko
      var nt = range.startContainer.nodeType;

      console.log('validateLists.Nodetype:' + nt);

      if (nt == 1) {
        // element-based user selection...
        // enfore for element selections the range spans only a single element in its entirety
        if(range.endContainer.nodeType != 1 || range.startContainer != range.endContainer) {
          this.selectionError = annotationContextConstants.badElementSelection;
          return false;
        }
        // enforce that only one element is selectable when we have an element-based user selection
        // (this is usually an li tag)
        if(Math.abs(range.startOffset - range.endOffset) != 1) {
          this.selectionError = annotationContextConstants.badElementSelection;
          return false;
        }

        // it is presumed all contained text w/in the following container (node) is selected
        var ftn = ambra.domUtil.findTextNode(range.startContainer.childNodes[range.startOffset], true);
        var ltn = ambra.domUtil.findTextNode(range.startContainer.childNodes[range.endOffset - 1], false);
        range.setStart(ftn, 0);
        range.setEnd(ltn, ltn.length);

        return true;
      }
      else if (nt == 3) {
        // text-based user selection...
        // ensure we are not spanning multiple li tags
        // NOTE: verified w/ Susanne DeRisi
        // TODO finish
        return true;
      } else {
        // un-handled node type
        //TODO: Throw Exception?
        return false;
      }
    } else {
      //TODO: Bad browser?  Throw exception.
      return false;
    }
  },

  /**
   * Validate the passed in textRange and set the appropriate error code
   */
  validateSelection:function(textRange) {
    //Leaving these here for future use in bebugging new browsers
    //console.log('validateSelection.textRange:' + textRange.range);
    //console.log('validateSelection.startParent:' + textRange.startParent);
    //console.log('validateSelection.endParent:' + textRange.endParent);

    var startRes = this.recurseParentForXpath(textRange.startParent);

    //If either the end or the beginning of the selection falls into an "ignored" area
    //Of the document, ignore and exit, (don't report error)
    if (startRes == false && this.selectionError == annotationContextConstants.badDocumentArea) {
      return false;
    }

    var endRes = this.recurseParentForXpath(textRange.endParent);

    //If either the end or the beginning of the selection falls into an "ignored" area
    //Of the document, ignore and exit, (don't report error)
    if (endRes == false && this.selectionError == annotationContextConstants.badDocumentArea) {
      return false;
    }

    //console.log('validateSelection.startRes.id:' + startRes.id);
    //console.log('validateSelection.endRes.id:' + endRes.id);
    //console.log('validateSelection.startRes:' + startRes);
    //console.log('validateSelection.endRes:' + endRes);

    if (startRes == false || endRes == false) {
      //This is REALLY subjective.  But I've commented this out to resolve issue:
      //http://ambraproject.org/trac/ticket/1291
      //if(textRange.startParent != textRange.endParent) {
      //  this.selectionError = annotationContextConstants.badRangeSelection;
      //}
      return false;
    }

    if (textRange.startParent == textRange.endParent) {
      return true;
    } else {
      var selectedFragment = this.getSelectionFragment(textRange);

      if(!this.recurseChildrenForBadXpath(selectedFragment)) {
        //In this specific case, we want to display an error message other then the default
        this.selectionError = annotationContextConstants.badRangeSelection;
        return false;
      } else {
        return true;
      }
    }
  },

  /**
   * Look at all the children containted withing the current
   * node looking for any items that are not selectable
   */
  recurseChildrenForBadXpath:function(objNode) {
    console.log('recurseChildrenForBadXpath:' + objNode);

    //Last element of this conditional is for IE6.  As IE6 has no hasAttribute method
    if (objNode.hasAttribute && objNode.hasAttribute("xpathLocation") || objNode.xpathLocation) {
      if (objNode.attributes["xpathLocation"].value == annotationContextConstants.excludeSelection) {
        console.log('objNode.attributes["xpathLocation"].value:' + objNode.attributes["xpathLocation"].value);
        this.selectionError = annotationContextConstants.badSelection;
        return false;
      }

      if (objNode.attributes["xpathLocation"].value == annotationContextConstants.ignoreSelection) {
        console.log('objNode.attributes["xpathLocation"].value:' + objNode.attributes["xpathLocation"].value);
        console.log('recurseChildrenForBadXpath: return false');
        this.selectionError = annotationContextConstants.badDocumentArea;
        return false;
      }
    }

    console.log('objNode.childNodes.length:' + objNode.childNodes.length);

    if (objNode.childNodes.length > 0) {
      for(var a = 0; a < objNode.childNodes.length; a++) {
        if(!this.recurseChildrenForBadXpath(objNode.childNodes[a])) {
          console.log('recurseChildrenForBadXpath: return false');
          return false;
        }
      }
    }
    console.log('recurseChildrenForBadXpath: return true');
    return true;
  },

  /**
   * Recurse up the dom tree to make sure the current element
   * is selectable.   This will recurse up the dom tree until an item
   * is found with an id of "annotationContextConstants.topNodeId"
   */
  recurseParentForXpath:function(objNode) {
    console.log('id:' + objNode.id);
    console.log('topNodeId:' + annotationContextConstants.topNodeId);

    if (objNode.id == annotationContextConstants.topNodeId) {
      return true;
    } else {
      //Last element of this conditional is for IE6.  As IE6 has no hasAttribute method
      if (objNode.hasAttribute && objNode.hasAttribute("xpathLocation") || objNode.xpathLocation) {
        console.log('objNode.xpathLocation:' + objNode.attributes["xpathLocation"].value);
        if(objNode.attributes["xpathLocation"].value == annotationContextConstants.excludeSelection) {
          this.selectionError = annotationContextConstants.badSelection;
          return false;
        }

        console.log('objNode.xpathLocation:' + objNode.attributes["xpathLocation"].value);
        if (objNode.attributes["xpathLocation"].value == annotationContextConstants.ignoreSelection) {
          this.selectionError = annotationContextConstants.badDocumentArea;
          return false;
        }

        return true;
      }

      //This will happen if one end of the selection spans to outside the body element
      if (objNode.parentNode == null) {
        this.selectionError = annotationContextConstants.badDocumentArea;
        return false;
      }

      return this.recurseParentForXpath(objNode.parentNode);
    }
  },

  /**
   * Start select begins the validation and then display of the dialog process
   * The setTimeout is used here to resolve problems with FF2.  Firefox 2 Handles
   * Events a little differently.  By using setTimeout, we're assured this code is executed
   * Last after all other events
   */
  startSelect:function(e) {
    //this.displayDialog(this.mouseX(e), this.mouseY(e));
    setTimeout(dojo.hitch(this, 'displayDialog', e.pageX, e.pageY), 100);
  },

   /**
    * This validates the selection and figure out what
    * context menu to display
    */
  displayDialog:function(/*Integer*/ x, /*Integer*/ y) {
    console.log('selectText:x,y:' + x + "," + y);

    /*
     * This is a little redundant as the window should be closed onmousedown
     * But certain browsers (FF2) behave differently.  This should capture all cases
     */
    if (contextActionTT != null){
      contextActionTT.close();
    }

    var textRange = this.getRangeOfSelection();

    if (textRange) {
      if (this.validateSelection(textRange)) {
        if (!loggedIn) {
          contextActionTT = dijit.byId("ContextActionDialogNotLogged");
        } else {
          contextActionTT = dijit.byId("ContextActionDialog");
        }
      } else {
        if (this.selectionError == annotationContextConstants.badDocumentArea) {
          //Perform a no-op on the current selection
          console.log('no-op' + annotationContextConstants.ignoreSelection);
          return;
        }

        if (this.selectionError == annotationContextConstants.badSelection) {
          contextActionTT = dijit.byId("ContextActionDialogBadSelection");
        }

        if (this.selectionError == annotationContextConstants.badElementSelection) {
          contextActionTT = dijit.byId("ContextActionDialogBadSelection");
        }

        if (this.selectionError == annotationContextConstants.badRangeSelection) {
          contextActionTT = dijit.byId("ContextActionDialogBadRangeSelection");
        }

        console.log('this.selectionError:' + this.selectionError);
      }

      console.log(contextActionTT.id);

      contextActionTT.connectId = this.target;
      contextActionTT.open(dojo.byId(this.target), x, y);
    }
  }
};