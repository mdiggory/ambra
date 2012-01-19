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
 * ambra.navigation
 * 
 * This class builds the table of content navigation in the right-hand column. 
 **/
dojo.provide("ambra.navigation");
dojo.require("ambra.general");

ambra.navigation = {

  /**
   * Build the table of contents box
   * @param tocBox the DIV tag that will hold the table of contents,
   *        it is assumed this box is not visible, and the display
   *        will be set to inline after the table of contents is created
   * @param tocParentNode the node inside of the tocBOX to append the table of
   *        contents unordered list to
   */
  buildTOC: function(tocBox, tocParentNode) {
    var ul = dojo.byId('tocUl');

    //presume we already have the list built
    if (!ul) {
      var tocEl = document.getElementsByTagAndAttributeName(null, 'toc');

      if (tocEl.length > 0) {
        var ul = document.createElement('ul');
        ul.setAttribute('id','tocUl');

        for (var i=0; i<tocEl.length; i++) {
          var li = document.createElement('li');
          var anchor = document.createElement('a');

          anchor.href = "#" + tocEl[i].getAttributeNode('toc').nodeValue;
          anchor.title = tocEl[i].getAttributeNode('title').nodeValue;

          if (i == tocEl.length -1) {
            anchor.className = 'last';
          }

          var tocText = tocEl[i].getAttributeNode('title').nodeValue;

          if(tocText.length > 25) {
            tocText = tocText.trimOnWord(25) + '...';
          }

          var tocTextNode = document.createTextNode(tocText);

          anchor.appendChild(tocTextNode);

          li.appendChild(anchor);
          ul.appendChild(li);
        }

        tocParentNode.appendChild(ul);
        tocBox.style.display='inline';
      }
    }
  }
}

dojo.addOnLoad(function() {
  if (dojo.isIE) {
    var navContainer = dojo.byId("nav");

    if (!navContainer) {
      return;
    }

    for (var i=0; i<navContainer.childNodes.length; i++) {
      if (navContainer.childNodes[i].nodeName == "LI") {
        var navLi = navContainer.childNodes[i];

        navLi.onmouseover = function() {
          this.className = this.className.concat(" over");
        }

        navLi.onmouseout = function() {
          this.className = this.className.replace(/\sover/, "");
          this.className = this.className.replace(/over/, "");
        }
      }
    }
  }
});
