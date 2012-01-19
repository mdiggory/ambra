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
import java.io.StringReader;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

import it.unibo.cs.xpointer.Location;
import it.unibo.cs.xpointer.XPointerAPI;
import it.unibo.cs.xpointer.datatype.LocationList;

/**
 * Tests the SelectionRangeList algorithm.
 *
 * @author Pradeep Krishnan
 */
public class SelectionRangeListTest {
  private static final String testXml =
    "<!DOCTYPE doc [<!ELEMENT testid (testid)*>"
    + " <!ATTLIST testid id ID #REQUIRED > ] > <doc> <chapter> <title>Chapter I</title> "
    + " <para>Hello world, indeed, <em>wonderful</em> world</para></chapter> "
    + " <para>This is a test <span>for <em>skipped</em><!-- comment --> child</span> nodes"
    + " in sub-range</para>"
    + " <x:a xmlns:x=\"foo\"> <x:a xmlns:x=\"bar\"/> </x:a> "
    + " <testid id=\"id1\"> <testid id=\"id2\"/> </testid> </doc>";

  private Document document;
  private Regions  regions;

  /**
   * Sets up the test.
   *
   * @throws SAXException on parse failure
   * @throws ParserConfigurationException on parse failure
   * @throws IOException on parse failure
   */
  @BeforeMethod
  public void setUp() throws SAXException, ParserConfigurationException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder        builder = factory.newDocumentBuilder();
    document   = builder.parse(new InputSource(new StringReader(testXml)));
    regions    = new Regions(document);
  }

  /**
   * Tests non-overlapping regions added in order.
   */
  @Test
  public void nonOverlappingRegionsAddedInOrder() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello world'))", "xpointer(string-range(/,'wonderful'))" };
    String[] userData = { "test01/0", "test01/1" };
    int[][]  regions  = {
                          { 0 },
                          { 1 }
                        };
    String[][] surrounds = {
                             { "Hello world" },
                             { "wonderful" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests non-overlapping regions added out of order.
   */
  @Test
  public void nonOverlappingRegionsAddedOutOfOrder() {
    String[] expressions =
      { "xpointer(string-range(/,'wonderful'))", "xpointer(string-range(/,'Hello world'))" };
    String[] userData = { "test02/0", "test02/1" };
    int[][]  regions  = {
                          { 1 },
                          { 0 }
                        };
    String[][] surrounds = {
                             { "Hello world" },
                             { "wonderful" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests overlapping regions added in order.
   */
  @Test
  public void overlappingRegionsAddedInOrder1() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello'))", "xpointer(string-range(/,'llo world'))" };
    String[] userData = { "test03/0", "test03/1" };
    int[][]  regions  = {
                          { 0 },
                          { 0, 1 },
                          { 1 }
                        };
    String[][] surrounds = {
                             { "He" },
                             { "llo" },
                             { " world" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests overlapping regions added in order.
   */
  @Test
  public void overlappingRegionsAddedInOrder2() {
    String[] expressions =
      { "xpointer(string-range(/,'llo world'))", "xpointer(string-range(/,'Hello'))" };
    String[] userData = { "test04/0", "test04/1" };
    int[][]  regions  = {
                          { 1 },
                          { 0, 1 },
                          { 0 }
                        };
    String[][] surrounds = {
                             { "He" },
                             { "llo" },
                             { " world" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests containing region with same start.
   */
  @Test
  public void containingRegionWithSameStart() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello world'))", "xpointer(string-range(/,'Hello'))" };
    String[] userData = { "test05/0", "test05/1" };
    int[][]  regions  = {
                          { 0, 1 },
                          { 0 },
                        };
    String[][] surrounds = {
                             { "Hello" },
                             { " world" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests containing region with same end.
   */
  @Test
  public void containingRegionWithSameEnd() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello world,'))", "xpointer(string-range(/,'world,'))" };
    String[] userData = { "test06/0", "test06/1" };
    int[][]  regions  = {
                          { 0 },
                          { 0, 1 },
                        };
    String[][] surrounds = {
                             { "Hello " },
                             { "world," }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests containing region with no shared start and end.
   */
  @Test
  public void containingRegionWithNoSharedStartAndEnd() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello world,'))", "xpointer(string-range(/,'ello world'))" };
    String[] userData = { "test07/0", "test07/1" };
    int[][]  regions  = {
                          { 0 },
                          { 0, 1 },
                          { 0 }
                        };
    String[][] surrounds = {
                             { "H" },
                             { "ello world" },
                             { "," }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests containing region with shared start and end.
   */
  @Test
  public void containingRegionWithSharedStartAndEnd() {
    String[] expressions =
      { "xpointer(string-range(/,'Hello world,'))", "xpointer(string-range(/,'Hello world,'))" };
    String[] userData = { "test08/0", "test08/1" };
    int[][]  regions  = {
                          { 0, 1 },
                        };
    String[][] surrounds = {
                             { "Hello world," }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests partially selected regions.
   */
  @Test
  public void patriallySelectedRegions() {
    String[] expressions =
      { "xpointer(string-range(/,'indeed, wonder'))", "xpointer(string-range(/,'ful world'))" };
    String[] userData = { "test09/0", "test09/1" };
    int[][]  regions  = {
                          { 0 },
                          { 1 }
                        };
    String[][] surrounds = {
                             { "indeed, ", "wonder" },
                             { "ful", " world" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests regions of child nodes of a fully selected range.
   */
  @Test
  public void regionsOfChildNodesOfAFullySelectedRange() {
    String[] expressions = { "xpointer(string-range(/,'indeed, wonderful world'))" };
    String[] userData = { "test10/0" };
    int[][]  regions  = {
                          { 0 }
                        };
    String[][] surrounds = {
                             { "indeed, ", "wonderful", " world" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests skipping of comment nodes of a fully selected range.
   */
  @Test
  public void skippingOfCommentNodesOfAFullySelectedRange1() {
    String[] expressions = { "xpointer(string-range(/,'test for skipped child nodes'))" };
    String[] userData = { "test11/0" };
    int[][]  regions  = {
                          { 0 }
                        };
    String[][] surrounds = {
                             { "test ", "for ", "skipped", " child", " nodes" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Tests skipping of comment nodes of a fully selected range.
   */
  @Test
  public void skippingOfCommentNodesOfAFullySelectedRange2() {
    String[] expressions = { "xpointer(string-range(/,'for skipped child'))" };
    String[] userData = { "test12/0" };
    int[][]  regions  = {
                          { 0 }
                        };
    String[][] surrounds = {
                             { "for ", "skipped", " child" }
                           };

    doit(expressions, userData, regions, surrounds);
  }

  /**
   * Executes the test.
   *
   * @param expressions xpointer expressions to select a region
   * @param userData user data associated with each expression
   * @param expectedRegions expected regions (identified by userData)
   * @param expectedSurrounds ranges that can be surrounded by a parent element
   *
   * @throws RuntimeException if an error in evaluating xpointer expression
   */
  private void doit(String[] expressions, String[] userData, int[][] expectedRegions,
                   String[][] expectedSurrounds) {
    int i;
    int j;

    assertEquals(expressions.length, userData.length);
    assertEquals(expectedRegions.length, expectedSurrounds.length);

    try {
      for (i = 0; i < expressions.length; i++) {
        LocationList list = XPointerAPI.evalFullptr(document, expressions[i]);
        regions.addRegion(list, userData[i]);
      }
    } catch (TransformerException e) {
      throw new RuntimeException("", e);
    }

    assertEquals(expectedRegions.length, regions.size(), "size of regions don't match");

    for (i = 0; i < expectedRegions.length; i++) {
      SelectionRange r = regions.get(i);
      List           u = r.getUserDataList();

      assertEquals(u.size(), expectedRegions[i].length);

      for (j = 0; j < expectedRegions[i].length; j++) {
        String data = userData[expectedRegions[i][j]];
        assertTrue(u.contains(data), "regions[" + i + "] must contain userData " + data);
      }
      //System.out.println("regions[" + i + "]=" + r);
      Range[] sub = r.getSurroundableRanges();

      assertEquals(expectedSurrounds[i].length, sub.length,
          "count of elements required to surround region[" + i + "]");

      for (j = 0; j < expectedSurrounds[i].length; j++)
        assertEquals(expectedSurrounds[i][j], sub[j].toString(),
            "surroundable[" + i + "][" + j + "]");
    }
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
