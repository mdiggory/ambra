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
dojo.provide("ambra.widget.ContextAction");

dojo.require("dojo.io.iframe");
dojo.require("dijit.Tooltip");

(function() {
  var contextActionTT = null;

  dojo.declare("ambra.widget._MasterContextAction", [dijit._MasterTooltip], {
      offSetX:39,
      offSetY:22,

      /**
       * This method is a direct copy of the parent's show method, less one call
       * This method has been changed to call the above method while also passing through the event object
       */

      show: function(/*String*/ id, /*String*/ innerHTML, /*DomNode*/ aroundNode, /*String[]?*/ position, /*Integer*/ eventX, /*Integer*/ eventY) {
        /*
         * summary:
         * Display tooltip w/specified contents to right specified node
         * (To left if there's no space on the right, or if LTR==right)
         */

        if (this.aroundNode && this.aroundNode == aroundNode) {
          return;
        }

        if (this.fadeOut.status() == "playing") {
          // previous tooltip is being hidden; wait until the hide completes then show new one
          this._onDeck=arguments;
          return;
        }

        this.containerNode.innerHTML = innerHTML;

        /*
         *  Firefox bug. when innerHTML changes to be shorter than previous
         *  one, the node size will not be updated until it moves.
         */
        this.domNode.style.top = (this.domNode.offsetTop + 1) + "px";

        //var pos = ambra.placeOnScreenAroundNode(this.domNode, aroundNode, align, dojo.hitch(this, "orient"), evt);
        var choices = this.makePos(["BL", "TL", "TR", "BR"], eventX, eventY);

        var pos = dijit._place(this.domNode, choices);

        //show it
        dojo.style(this.domNode, "opacity", 0);

        var targetTip = null;

        if (pos.corner.charAt(0) == "B") {
          targetTip = dojo.query(".tip", this.domNode);
        } else {
          targetTip = dojo.query(".tipu", this.domNode);
        }

        //console.log(id);

        var node = dojo.byId(id);

        dojo.style(targetTip[0], "display", "block");

        this.fadeIn.play();
        this.isShowingNow = true;
        this.aroundNode = aroundNode;
      },

      makePos:function(/*String[]?*/corners, eventX, eventY) {
        var res = [];

        for(var a = 0; a < corners.length; a++) {
          switch(corners[a]) {
            case "BL":
              res[res.length] = { corner:corners[a], pos:{x:eventX - this.offSetX, y:eventY - this.offSetY } };
              break;
            case "TL":
              res[res.length] = { corner:corners[a], pos:{x:eventX - this.offSetX, y:eventY + this.offSetY } };
              break;
            case "TR":
              res[res.length] = { corner:corners[a], pos:{x:eventX + this.offSetX, y:eventY + this.offSetY } };
              break;
            case "BR":
              res[res.length] = { corner:corners[a], pos:{x:eventX + this.offSetX, y:eventY - this.offSetY } };
              break;
          }

          if(res[res.length - 1].pos.x < 0) {
            res[res.length - 1].pos.x = 0;
          }

          //console.log(this.minSize);
        }

        //console.log('test');

        return res;
     }
  });

  dojo.declare("ambra.widget.ContextAction", [dijit.Tooltip], {
    templatePath: dojo.moduleUrl('ambra.widget', 'templates/ContextAction.html'),
    templateString: "ambra.widget.ContextAction",

    /*
     * Over ride the dojo Tooltip method
     * This will allow the mouse to hover over the created tooltip dialog
     * Dojo doesn't do this by default
     */
    _onUnHover: function(/*Event*/ e) {
      return;
    },

    open: function(/*DomNode*/ target, /*Integer*/ eventX, /*Integer*/ eventY) {
      // summary: display the tooltip; usually not called directly.
      target = target || this._connectNodes[0];

      if (!target){ return; }

      if (this._showTimer) {
        clearTimeout(this._showTimer);
        delete this._showTimer;
      }

      ambra.showContextAction(this.domNode.id, this.label || this.domNode.innerHTML, target, this.position, eventX, eventY);

      this._connectNode = target;
    },

    close: function() {
      // summary: hide the tooltip; usually not called directly.
      ambra.hideContextAction(this._connectNode);
      delete this._connectNode;

      if (this._showTimer) {
        clearTimeout(this._showTimer);
        delete this._showTimer;
      }
    }
  });

  ambra.showContextAction = function(/* String */id,/*String*/ innerHTML, /*DomNode*/ aroundNode, /*String[]?*/ position, /*Integer*/ eventX, /*Integer*/ eventY) {
    /*
     * summary:
     * Display tooltip w/specified contents in specified position.
     * See description of dijit.Tooltip.defaultPosition for details on position parameter.
     * If position is not specified then dijit.Tooltip.defaultPosition is used.
     */
    if (!ambra._MasterCA) {
      ambra._MasterCA = new ambra.widget._MasterContextAction();
    }

    //console.log('test: ' + ambra._MasterCA);
    return ambra._MasterCA.show(id, innerHTML, aroundNode, position, eventX, eventY);
  };

  ambra.hideContextAction = function(aroundNode) {
    // summary: hide the tooltip
    if (!ambra._MasterCA) {
      ambra._MasterCA = new ambra.widget._MasterContextAction();
    }
    
    return ambra._MasterCA.hide(aroundNode);
  };

  dijit.Tooltip.defaultPosition = ["left", "below"];
})();