/* $HeadURL::                                                                            $
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
package org.ambraproject.annotation.service;

import it.unibo.cs.xpointer.Location;
import it.unibo.cs.xpointer.XPointerAPI;
import it.unibo.cs.xpointer.datatype.LocationList;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.ambraproject.views.AnnotationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topazproject.dom.ranges.SelectionRange;
import org.topazproject.dom.ranges.SelectionRangeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

/**
 * Creates an annotated version of the content.
 *
 * @author Pradeep Krishnan
 */
public class Annotator {
  private static final Logger log    = LoggerFactory.getLogger(Annotator.class);
  private static String    AML_NS = "http://topazproject.org/aml/";

  /**
   * Annotates a document.
   *
   * @param document the source document
   * @param annotations the list of annotations to apply
   * @return the annotated document
   * @throws URISyntaxException if at least one annotation context is an invalid URI
   * @throws TransformerException if at least one annotation context is an invalid xpointer
   *         expression
   */
  public static Document annotateAsDocument(Document document, AnnotationView[] annotations)
                           throws URISyntaxException, TransformerException {
    LocationList[] lists = evaluate(document, annotations);

    Regions regions = new Regions(document);
    for (int i = 0; i < lists.length; i++) {
      if (lists[i] != null) {
        regions.addRegion(lists[i], annotations[i]);
      }
    }

    if (log.isDebugEnabled())
      log.debug("Surrounding selection ranges ...");

    regions.surroundContents(AML_NS, "aml:annotated", "aml:id", "aml:first");

    if (log.isDebugEnabled())
      log.debug("Creating region element ...");

    Element rRoot = regions.createElement(AML_NS, "aml:region", "aml:annotation", "aml:id");

    if (log.isDebugEnabled())
      log.debug("Creating annotation namespaces ...");

    Element aRoot = document.createElementNS(AML_NS, "aml:annotations");
    AnnotationModel.appendNSAttr(aRoot);

    for (int i = 0; i < annotations.length; i++) {
      AnnotationView annotation = annotations[i];
      if ((lists[i] != null) && (annotation.getXpath() != null)) {
        if (log.isDebugEnabled())
          log.debug("Creating annotation node for " + annotation.getID() + "...");

        Element a = document.createElementNS(AML_NS, "aml:annotation");
        a.setAttributeNS(AML_NS, "aml:id", annotation.getID().toString());
        aRoot.appendChild(a);
        AnnotationModel.appendToNode(a, annotation);
      }
    }

    if (log.isDebugEnabled())
      log.debug("Assembling result document ...");

    return assembleResultDoc(document, rRoot, aRoot);
  }

  private static Document assembleResultDoc(Document document, Element regions, Element annotations) {
    String  xmlns = "http://www.w3.org/2000/xmlns/";

    Element source = document.getDocumentElement();
    source.setAttributeNS(xmlns, "xmlns:aml", AML_NS);

    source.appendChild(regions);
    source.appendChild(annotations);

    return document;
  }

  private static LocationList[] evaluate(Document document, AnnotationView[] annotations)
    throws URISyntaxException, TransformerException {
    LocationList[] lists = new LocationList[annotations.length];

    String annotationContext;

    for (int i = 0; i < annotations.length; i++) {
      lists[i] = null;
      annotationContext = annotations[i].getXpath();
      if (annotationContext != null) {
        URI    context    = new URI(annotationContext);
        String expression = context.getFragment();
        if (expression != null) {
          try {
            expression = URLDecoder.decode(expression, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new Error(e);
          }

          try {
            if (log.isDebugEnabled())
              log.debug("Evaluating xpointer : " + expression);
            
            long timestamp = System.currentTimeMillis();
            lists[i] = XPointerAPI.evalFullptr(document, expression);
            if (log.isDebugEnabled())
              log.debug(Long.toString((System.currentTimeMillis() - timestamp) / 1000l) + " sec. to evaluate " + expression);
            
          } catch (Exception e) {
            /*
             * Trap the error here and continue.  One bad annotation shouldn't
             * cause the article to fail rendering.
             */
            log.error ("Could not evaluate xPointer: " + expression + " in " +
                       annotations[i].getID(), e);
          }
        }
      }
    }
    return lists;
  }

  private static class Regions extends SelectionRangeList {
    private final Document document;

    public Regions(Document document) {
      this.document = document;
    }

    public void addRegion(LocationList list, AnnotationView annotation) {
      int length = list.getLength();

      for (int i = 0; i < length; i++)
        addRegion(list.item(i), annotation);
    }

    public void addRegion(Location location, AnnotationView annotation) {
      Range range;

      if (location.getType() == Location.RANGE)
        range = (Range) location.getLocation();
      else {
        range = ((DocumentRange) document).createRange();
        range.selectNode((Node) location.getLocation());
      }

      // Ignore it if this range is collapsed (ie. start == end)
      if (!range.getCollapsed()) {
        if (log.isDebugEnabled())
          log.debug("Inserting selection range for " + annotation.getID());
        insert(new SelectionRange(range, annotation));
      }
    }

    @SuppressWarnings("unchecked")
    public Element createElement(String nsUri, String elemQName, String annotationsQName,
                                 String idAttrQName) {
      int     length = size();
      Element root = document.createElementNS(nsUri, elemQName + "s");

      for (int i = 0; i < length; i++) {
        Element rNode = document.createElementNS(nsUri, elemQName);
        rNode.setAttributeNS(nsUri, idAttrQName, "" + (i + 1));

        int numComments = 0;
        int numMinorCorrections = 0;
        int numFormalCorrections = 0;
        int numRetractions = 0;

        List<AnnotationView> annotations = get(i).getUserDataList();

        for (AnnotationView a : annotations) {
          Element aNode = document.createElementNS(nsUri, annotationsQName);
          aNode.setAttributeNS(nsUri, idAttrQName, a.getID().toString());
          rNode.appendChild(aNode);

          assert a.getType() != null;

          switch(a.getType()) {
            case NOTE:
              numComments++;
              break;
            case MINOR_CORRECTION:
              numMinorCorrections++;
              break;
            case FORMAL_CORRECTION:
              numFormalCorrections++;
              break;
            case RETRACTION:
              numRetractions++;
              break;
          }
        }

        rNode.setAttributeNS(nsUri, "aml:numComments", Integer.toString(numComments));
        rNode.setAttributeNS(nsUri, "aml:numMinorCorrections",
                             Integer.toString(numMinorCorrections));
        rNode.setAttributeNS(nsUri, "aml:numFormalCorrections",
                             Integer.toString(numFormalCorrections));
        rNode.setAttributeNS(nsUri, "aml:numRetractions", Integer.toString(numRetractions));

        root.appendChild(rNode);
      }

      return root;
    }
  }
}
