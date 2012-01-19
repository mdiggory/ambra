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
 * ambra.slideshow
 * 
 * This class builds and controls the slideshow thumbnails to display the appropriate
 * images when the thumbnails are clicked.  When an image is selected, the container 
 * will adjust to envelope the enlarged image.  This also ensures that when the user 
 * clicks on the next and previous links, the highlighting of the thumbnails changes 
 * as well to correspond to the link and vice versa.
 **/

dojo.provide("ambra.slideshow");
dojo.require("ambra.general");
dojo.require("ambra.domUtil");
ambra.slideshow = {
  slides:[],
  
  imgS: "PNG_S",
  
  imgM: "PNG_M",
  
  imgL: "PNG_L",
  
  imgTif: "TIF",
  
  linkView: "",
  
  linkTiff: "",
  
  linkPpt: "",
  
  figImg: "",
  
  figImgWidth: "",
  
  figTitle: "",
  
  figCaption: "",
  
  targetDiv: "",
  
  activeItemIndex: "",
  
  itemCount: "",
  
  setLinkView: function(aObj) {
    this.linkView = aObj;
  },
  
  setLinkTiff: function(aObj) {
    this.linkTiff = aObj;
  },
  
  setLinkPpt: function(aObj) {
    this.linkPpt = aObj;
  },
  
  setFigImg: function(dObj) {
    this.figImg = dObj;
  },

  setFigTitle: function(dObj) {
    this.figTitle = dObj;
  },
  
  setFigCaption: function(dObj) {
    this.figCaption = dObj;
  },
  
  setInitialThumbnailIndex: function() {
    var tn = document.getElementsByTagAndClassName('div', 'figure-window-nav-item');
    this.itemCount = tn.length;
    
    for (var i=0; i<this.itemCount; i++) {
      if (tn[i].className.match('current')) {
        this.activeItemIndex = i;
      }
    }
  },
  
  show: function (obj, index) {
    if (this.linkView) this.linkView.href = this.slides[index].imageLargeUri + "&representation=" + this.imgL;
    if (this.linkTiff) this.linkTiff.href = this.slides[index].imageAttachUri + "&representation=" + this.imgTif;
    if (this.linkPpt) this.linkPpt.href  = this.slides[index].imageAttachUri + "&representation=" + this.imgM;
    
    if (this.figImg) {
      this.figImg.src = this.slides[index].imageUri + "&representation=" + this.imgM;
      this.figImg.title = this.slides[index].titlePlain;
    }
    
    if (this.figTitle) this.figTitle.innerHTML = this.slides[index].title;
    
    if (this.figCaption) this.figCaption.innerHTML = this.slides[index].description;
    
    var tbCurrent = document.getElementsByTagAndClassName('div', 'current');
    
    for (var i=0; i<tbCurrent.length; i++) {
      //alert("tbCurrent[" + i + "] = " + tbCurrent[i].nodeName + "\ntbCurrent[" + i + "].className = " + tbCurrent[i].className);
      //tbCurrent[i].className = tbCurrent[i].className.replace(/\-current/, "");
      dojo.removeClass(tbCurrent[i], "current");
    }
    
    var tbNew = obj.parentNode.parentNode;
    //tbNew.className = tbNew.className.concat("-current");
    dojo.addClass(tbNew, "current");
    
    if (index == 0) 
      dojo.addClass(dojo.byId("previous"), "hidden");
    else
      dojo.removeClass(dojo.byId("previous"), "hidden");
    
    if (index == this.itemCount-1) 
      dojo.addClass(dojo.byId("next"), "hidden");
    else
      dojo.removeClass(dojo.byId("next"), "hidden");
    
    
    this.activeItemIndex = index;
    
    window.setTimeout("ambra.slideshow.adjustViewerHeight()", 100);
    
  },
  
  showSingle: function (obj, index) {
    if (this.linkView) this.linkView.href = this.slides[index].imageLargeUri + "&representation=" + this.imgL;
    if (this.linkTiff) this.linkTiff.href = this.slides[index].imageAttachUri + "&representation=" + this.imgTif;
    if (this.linkPpt) this.linkPpt.href  = this.slides[index].imageAttachUri + "&representation=" + this.imgM;
    
    if (this.figImg) {
      this.figImg.src = this.slides[index].imageUri + "&representation=" + this.imgM;
      this.figImg.title = this.slides[index].titlePlain;
    }
    
    if (this.figTitle) this.figTitle.innerHTML = this.slides[index].title;
    
    if (this.figCaption) this.figCaption.innerHTML = this.slides[index].description;
    
    var tbCurrent = document.getElementsByTagAndClassName('div', 'figure-window-nav-item-current');
    
    for (var i=0; i<tbCurrent.length; i++) {
      //alert("tbCurrent[" + i + "] = " + tbCurrent[i].nodeName + "\ntbCurrent[" + i + "].className = " + tbCurrent[i].className);
      tbCurrent[i].className = tbCurrent[i].className.replace(/\-current/, "");
      
    }
    
    var tbNew = obj.parentNode.parentNode;
    tbNew.className = tbNew.className.concat("-current");
    
  },
  
  getFigureInfo: function (figureObj) {
    if (figureObj.hasChildNodes) {
      var caption = document.createDocumentFragment();
      
      for (var i=0; i<figureObj.childNodes.length; i++) {
        var child = figureObj.childNodes[i];
        
        if (child.nodeName == 'A') {
          for (var n=0; n<child.childNodes.length; n++) {
            var grandchild = child.childNodes[n];
            
            if (grandchild.nodeName == 'IMG') {
              this.figImg = grandchild;
            }
          }
        }
        else if (grandchild.nodeName == 'H5') {
          ambra.domUtil.copyChildren(grandchild, this.figTitle);
        }
        else {
          var newChild = grandchild;
          newChild.getAttributeNode('xpathlocation')='noSelect';
          caption.appendChild(newChild);
        }
      }
      
      ambra.domUtil.copyChildren(caption, this.figCaption);
      
      return;
    }
    else {
      return false;
    }
  },
  
  adjustContainerHeight: function (obj) {
    // get size viewport
    var viewport = dijit.getViewport();
    
    // get the offset of the container
    var objOffset = ambra.domUtil.getCurrentOffset(obj);
    
    var maxContainerHeight = viewport.h - (10 * objOffset.top);
    //alert("objOffset.top = " + objOffset.top + "\nviewport.h = " + viewport.h + "\nmaxContainerHeight = " + maxContainerHeight);
    
    obj.style.height = maxContainerHeight + "px";
    obj.style.overflow = "auto";
  },
  
  adjustViewerHeight: function() {
    var container1 = dojo.byId("figure-window-nav");
    var container2 = dojo.byId("figure-window-container");
    var container1Mb = dojo._getMarginBox(container1).h;
    var container2Mb = dojo._getMarginBox(container2).h;
    
    if (container1Mb > container2Mb) {
      container2.parentNode.style.height = container1Mb + "px";
      container1.style.borderRight = "2px solid #ccc";
      container2.style.borderLeft = "none";
    }
    else {
      container2.parentNode.style.height = "auto";
      container1.style.borderRight = "none";
      container2.style.borderLeft = "2px solid #ccc";
    }    
  },
  
  adjustViewerWidth: function(figureWindow, maxWidth) {
    ambra.domUtil.setContainerWidth(figureWindow, dojo._getMarginBox(ambra.slideshow.figureImg).w, maxWidth, 1);
  },

  showPrevious: function(obj) {
    if (this.activeItemIndex <= 0) {
      return false;
    }
    else {
      var newIndex = this.activeItemIndex - 1;
      var newTnObj = dojo.byId('tn' + newIndex);
      this.show(newTnObj, newIndex);
      
      if (newIndex == 0) 
        dojo.addClass(obj, 'hidden');
      
      if (this.activeItemIndex == this.itemCount-1)
        dojo.removeClass(dojo.byId('next'), 'hidden');
        
      this.activeItemIndex = newIndex;
    }
  },
  
  showNext: function(obj) {
    if (this.activeItemIndex == this.itemCount-1) {
      return false;
    }
    else {
      var newIndex = this.activeItemIndex + 1;
      var newTnObj = dojo.byId('tn' + newIndex);
      this.show(newTnObj, newIndex);
      
      if (newIndex == this.itemCount-1) 
        dojo.addClass(obj, 'hidden');
      
      if (this.activeItemIndex == 0)
        dojo.removeClass(dojo.byId('previous'), 'hidden');
        
      this.activeItemIndex = newIndex;
    }
  },
  
  openViewer: function(url) {
    var newWindow = window.open(url,'plosSlideshow','directories=no,location=no,menubar=no,resizable=yes,status=no,scrollbars=yes,toolbar=no,height=600,width=800');
    
    return false;
  },
  
  closeReturn: function(url) {
    if(window.name == 'plosSlideshow' || window.opener != null)
    {
      self.close();
      window.opener.focus();
    } else {
      document.location = url;
    }
  }
}