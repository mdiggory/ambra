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
  * ambra.corrections
  *
  * Decorates an article with correction specific markup. 
  * 
  * @author jkirton (jopaki@gmail.com)
  * @author Joe Osowski
  **/

dojo.provide("ambra.corrections");
dojo.require("ambra.domUtil");
dojo.require("ambra.displayComment");

ambra.corrections = {
  aroot: null, // the top-most element of the article below which corrections are applied
  fclist: null, // the formal corrections ordered list element ref
  arrElmFc:null, // array of the formal correction elements for the article

  /**
   * ambra.corrections.init
   *
   * Connect events to the display for article annotations
   */
  init: function() {
    // [re-]identify node refs (as the article container is subject to refresh)
    this.aroot = dojo.byId(annotationConfig.articleContainer);
    this.fclist = dojo.query('.' + formalCorrectionConfig.fcLinkClass);
    this.arrElmFc = dojo.query('.' + annotationConfig.styleFormalCorrection, this.aroot);

    for(var x = 0; x < this.fclist.length; x++) {
      dojo.connect(this.fclist[x], "onclick", ambra.corrections.onClickFC);
    }
  },//init

  /**
   * _findFrmlCrctnByAnnId
   *
   * Finds a formal correction node given an annotation id
   * by searching the formal corrections node array property of this object
   *
   * @param annId The annotation (guid) id
   * @return The found formal correction node or null if not found
   */
  _findFrmlCrctnByAnnId: function(annId) {
    if (this.arrElmFc != null && annId != null) {
      for (var i = 0; i < this.arrElmFc.length; i++) {
        var naid = dojo.attr(this.arrElmFc[i], 'annotationid');

        if(naid != null && naid.indexOf(annId) >= 0)
          return this.arrElmFc[i];
      }
    }
    
    return null;
  },

  _getAnnAnchor: function(ancestor) {
    var cns = ancestor.childNodes;

    if(cns) {
      for (var i=0; i< cns.length; i++) {
        if(cns[i].nodeName == 'A') {
          return cns[i];
        }
      }
    }
    
    return null;
  },

  /**
   * onClickFC
   *
   * Event handler for links in the formal correctionn header's ordered list of formal corrections.
   *
   * Scrolls into view the portion of the article containing the given correction (annotation) id
   * then opens the note (comment) window for the bound bug.
   *
   * @param e event
   */
  onClickFC: function(e) {
    var annId = dojo.attr(e.target, formalCorrectionConfig.annid);
    e.preventDefault();

    var fcn = ambra.corrections._findFrmlCrctnByAnnId(annId);

    if (fcn) {
      var annAnchor = ambra.corrections._getAnnAnchor(fcn);

      if(!annAnchor) {
        throw 'Unable to resolve annotation anchor!';
      }

      ambra.displayComment.show(annAnchor);

      // ensure the dialog is scrolled into view
      jumpToAnnotation(annId);
    }

    return false;
  }
}
