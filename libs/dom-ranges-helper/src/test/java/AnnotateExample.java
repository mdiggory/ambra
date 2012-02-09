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
import java.net.URI;
import java.net.URLDecoder;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerException;

import org.topazproject.dom.ranges.SelectionRange;
import org.topazproject.dom.ranges.SelectionRangeList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

import it.unibo.cs.xpointer.Location;
import it.unibo.cs.xpointer.XPointerAPI;
import it.unibo.cs.xpointer.datatype.LocationList;

/**
 * A sample implementation for creating an annotated document.
 *
 * @author PradeepKrishnan
 */
public class AnnotateExample {
  /**
   * Insert annotation markers in a document.
   *
   * @param document the document to annotate
   * @param annotations the list of annotations on the document
   *
   * @throws URISyntaxException if the annotation context is not valid
   * @throws TransformerException on error in xpointer evaluation
   */
  public static void annotate(Document document, AnnotationInfo[] annotations)
                       throws URISyntaxException, TransformerException {
    String NS = "http://topazproject.org/aml";

    // Step 1: build non overlapping ranges
    SelectionRangeList srl = new SelectionRangeList();

    for (int i = 0; i < annotations.length; i++) {
      URI          context    = new URI(annotations[i].getContext());
      String       expression = URLDecoder.decode(context.getFragment());
      LocationList list       = XPointerAPI.evalFullptr(document, expression);
      int          length     = list.getLength();

      for (int j = 0; j < length; j++) {
        Location location = list.item(j);
        Range    range;

        if (location.getType() == Location.RANGE)
          range = (Range) location.getLocation();
        else {
          range = ((DocumentRange) document).createRange();
          range.selectNode((Node) location.getLocation());
        }

        // Ignore it if this range is collapsed (ie. start == end)
        if (!range.getCollapsed())
          srl.insert(new SelectionRange(range, annotations[i]));
      }
    }

    // Step 2: surround each range
    srl.surroundContents(NS, "aml:annotated", "aml:id", "aml:first");

    // Step 3: create an index describing each element that we inserted
    Element index = document.createElementNS(NS, "aml:regions");

    document.getDocumentElement().appendChild(index);

    int length = srl.size();

    for (int i = 0; i < length; i++) {
      Element rNode = document.createElementNS(NS, "aml:region");
      index.appendChild(rNode);

      AnnotationInfo[] a =
        (AnnotationInfo[]) srl.get(i).getUserDataList().toArray(new AnnotationInfo[0]);

      for (int k = 0; k < a.length; k++) {
        Element meta = document.createElementNS("http://topaz.org/aml", "aml:annotation");
        meta.setAttributeNS("http://purl.org/dc/elements/1.1/", "dc:creator", a[k].getCreator());

        // and similarly for other meta info
        rNode.appendChild(meta);
      }
    }
  }

  private class AnnotationInfo {
    public String getCreator() {
      return "FSM";
    }

    public String getContext() {
      return "#xpointer(string-range('Hello world'))";
    }
  }
}
