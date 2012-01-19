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
dojo.provide("ambra.widget.RegionalDialog");

dojo.require("dojo.io.iframe");
dojo.require("dijit.Dialog");
dojo.require("ambra.domUtil");

// summary
//	Mixin for widgets implementing a modal dialog
dojo.declare(
	"ambra.widget.RegionalDialogBase", 
	[dijit.Dialog],
{
  duration: 250,
  refocus: false,
  _onKey:function(e){}, // disable key shortcuts (for now) as the necessary ambra override methods will *NOT* be called!

	_changeTipDirection: function(isTipDown, xShift) {
		var dTip = this.tipDownNode;
		var dTipu = this.tipUpNode;
		
		dTip.className = dTip.className.replace(/\son/, "");
  	dTipu.className = dTipu.className.replace(/\son/, ""); 
		
		var targetTip = (isTipDown) ? dTip : dTipu;
		
		targetTip.className = targetTip.className.concat(" on");

    //if (dojo.isIE < 7) 
	  // 	targetTip.style.marginLeft = (xShift) ? xShift + "px" : "auto";
	  //else
	  targetTip.style.left = (xShift) ? xShift + "px" : "auto";
	},
		
	placeModalDialog: function() {
		var docscroll = dojo._docScroll();
		var viewport = dijit.getViewport();
		var markerOffset = ambra.domUtil.getCurrentOffset(this.markerNode);
		var mb = dojo.marginBox(this.containerNode);

    /*
    console.debug('RegionalDialog. placeModalDialog(): '
    + '\ndocscroll x:' + docscroll.x + ',y:' + docscroll.y 
    + '\nviewport w:' + viewport.w + ',h:' + viewport.h
    + '\nmarkerOffset left:' + markerOffset.left+ ',top:' + markerOffset.top
    + '\nmb.w:' + mb.w+ ',h:' + mb.h);
		*/
    
    var mbWidth = mb.w;
		var mbHeight = mb.h;
		var vpWidth = viewport.w;
		var vpHeight = viewport.h;
		var scrollX = docscroll.x;
		var scrollY = docscroll.y;
		
		// The height of the tip.
		var tipHeight = 22;
		
		// The width of the tip.
		var tipWidth = 39;
		
		// The minimum distance from the left edge of the dialog box to the left edge of the tip.
		var tipMarginLeft = 22;
		
		// The minimum distance from the right edge of the dialog box to the right edge of the tip.
		var tipMarginRight = 22;
		
		// The minimum distance from either side edge of the dialog box to the corresponding side edge of the viewport.
		var mbMarginX = 10;
		
		// The minimum distance from the top or bottom edge of the dialog box to the top or bottom, respectively, of the viewport.
		var mbMarginY = 10;

    // The height of the bug. This is used when the tip points up to figure out how far down to push everything.
    var bugHeight = 15;

    // The minimum x-offset of the dialog box top-left corner, relative to the page.
    var xMin = scrollX + mbMarginX;
    
    // The minimum y-offset of the dialog box top-left corner, relative to the page.
    var yMin = scrollY + mbMarginY;
    
    // The maximum x-offset of the dialog box top-left corner, relative to the page.
    var xMax = scrollX + vpWidth - mbMarginX - mbWidth;

    // The maximum y-offset of the dialog box top-left corner, relative to the page.
    var yMax = scrollY + vpHeight - mbMarginY - mbHeight;
    
    // The minimum x-offset of the tip left edge, relative to the page.
    var xTipMin = xMin + tipMarginLeft;

    // The maximum x-offset of the tip left edge, relative to the page.
    var xTipMax = xMax + mbWidth - tipMarginRight - tipWidth;

    // True if the tip is pointing down (the default)
    var tipDown = true;

    // Sanity check to make sure that the viewport is large enough to accomodate the dialog box, the tip, and the minimum margins
    if (xMin > xMax || yMin > yMax || xTipMin > xTipMax) {
      // TODO handle this!
    }
		
		// Default values put the box generally above and to the right of the annotation "bug"
    var xTip = markerOffset.left - (tipWidth / 2) + 4;
    var yTip = markerOffset.top - tipHeight - (tipHeight/4) + 8;
    
    var x = xTip - tipMarginLeft;
    var y = yTip - mbHeight;

    // If the box is too far to the left, try sliding it over to the right. The tip will slide with it, and thus no longer be pointing directly to the bug.
    if (x < xMin) {
      x = xMin;
      if (xTip < xTipMin) {
        xTip = xTipMin;
      }
    }
    // If the box is too far to the right, slide it over to the left, but leave the tip in the same place if possible.
    else if (x > xMax) {
      x = xMax;
      if (xTip > xTipMax) {
        xTip = xTipMax;
      }
    }

    // If the box is too far up, flip it over and put it below the annotation.
    if (y < yMin) {
      tipDown = false; // flip the tip

      yTip = markerOffset.top + bugHeight - (tipHeight/4);
      y = yTip + tipHeight;
      
      if (y > yMax) {
        // this is bad, because it means that there isn't enough room above or below the annotation for the dialog box, the tip, and/or the minimum margins
      }
    }
    
    var xTipDiff = markerOffset.left - x;
    
    if(xTipDiff < tipMarginLeft) {
      xTipPos = tipMarginLeft - (tipWidth / 4);
      x = x - (tipMarginLeft - xTipDiff);
    }
    else {
      xTipPos = xTipDiff - (tipWidth / 4);
      //x = x - (tipMarginLeft - xTipDiff);
    }
          
    this._changeTipDirection(tipDown, xTipPos);

		with(this.domNode.style){
			left = x + "px";
			top = y + "px";
		}

    console.debug("RegionalDialogBase.placeModalDialog: \nleft = " + this.domNode.style.left + "\ntop = "  + this.domNode.style.top);
	},
		
  // override position as we want control over dialog placement!
  _position: function() {
    this.placeModalDialog();
  }
});

dojo.declare(
	"ambra.widget.RegionalDialog",
	[ambra.widget.RegionalDialogBase],
{
  templatePath: dojo.moduleUrl('ambra.widget', 'templates/RegionalDialog.html'),
	
  show: function() {
    console.debug('RegionalDialog.show()');
    this.inherited(arguments);
	},

  // onscroll hook
  // NOTE: we must override this method to avoid infinite looping in Safari (v3.1.1)!! 
  layout: function() {
    // summary: position the Dialog and the underlay
    if(this.domNode.style.visibility != "hidden"){
      this._underlay.layout();
      if(!dojo.isSafari) this._position();
    }
  },

  onLoad: function(){
    console.debug('RegionalDialog.onLoad()');
    this.inherited(arguments);
  },
  
	setMarker: function(node) {
	  // summary
	  // when specified is clicked, pass along the marker object
	  this.markerNode = node;
	},
	
	setTipUp: function(node) {
	  // summary
	  // when specified is clicked, pass along the marker object
	  this.tipUpNode = node;
	},
	
	setTipDown: function(node) {
	  // summary
	  // when specified is clicked, pass along the marker object
	  this.tipDownNode = node;
	}
});
