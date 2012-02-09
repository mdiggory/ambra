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
package org.topazproject.dom.ranges;

import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

import org.xml.sax.SAXException;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import it.unibo.cs.xpointer.Location;
import it.unibo.cs.xpointer.XPointerAPI;
import it.unibo.cs.xpointer.datatype.LocationList;

/**
 * Test for Bug#298.
 *
 * @author Pradeep Krishnan
 */
public class Bug298Test {
  private Document document;
  private Regions  regions;
  private String   expression =
    "xpointer(string-range(/article[1]/body[1]/sec[3]/p[4], '')[511]/range-to(string-range(/article[1]/body[1]/sec[4]/sec[1]/sec[1]/p[1], '')[38]))";
  private String   article    = "/pone.15.xml";

  /**
   * Sets up the test.
   *
   * @throws SAXException on parse failure
   * @throws ParserConfigurationException on parse failure
   * @throws IOException on parse failure
   */
  @BeforeClass
  public void setUp() throws SAXException, ParserConfigurationException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder        builder = factory.newDocumentBuilder();
    document                       = builder.parse(getClass().getResourceAsStream(article));
    regions                        = new Regions(document);
  }

  @Test
  public void bug298Test() throws Exception {
    LocationList list = XPointerAPI.evalFullptr(document, expression);
    regions.addRegion(list, "test");
    regions.surroundContents("http://topazproject.org/aml", "aml:annotated", "aml:id", "aml:first");
  }

  private static class Regions extends SelectionRangeList {
    private Document document;

    public Regions(Document document) {
      this.document = document;
    }

    public void addRegion(LocationList list, Object userData) {
      int length = list.getLength();

      for (int i = 0; i < length; i++)
        addRegion(list.item(i), userData);
    }

    public void addRegion(Location location, Object userData) {
      Range range;

      if (location.getType() == Location.RANGE)
        range = (Range) location.getLocation();
      else {
        range = ((DocumentRange) document).createRange();
        range.selectNode((Node) location.getLocation());
      }

      // Ignore it if this range is collapsed (ie. start == end)
      if (!range.getCollapsed())
        insert(new SelectionRange(range, userData));
    }
  }
}
