/*
 * $HeadURL$
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

package org.ambraproject.search;

import junit.framework.TestCase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.SimpleTimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;

/**
 * TODO: Use different arrays for each of the different array-type fields.
 *
 * @author Scott Sterling
 */
public class SearchParametersTest {

  private static final String   test              =
      "This is a generic test String, with punctuation and some special characters: !@#$%^&*()[]{}";
  private static final String   empty             = "";
  private static final String   trimTestPrefix    = "  \r  \t  \r  ";
  private static final String   trimTestSuffix    = "\t  \r  \t";

  private static final String[] inputStringArray  =
      {null, trimTestPrefix + "element Two", null, null, null,
          "element Six with some special characters: !@#$%^&*()[]{}", "", "", null, "",
          trimTestPrefix + "element Eleven" + trimTestSuffix, "element Twelve" + trimTestSuffix};
  private static final String[] outputStringArray =
      {"element Two", "element Six with some special characters: !@#$%^&*()[]{}", "element Eleven", "element Twelve"};
  private static final String[] whitespaceAndNullElementsStringArray  =
      {trimTestPrefix, null, null, "", "", null, trimTestSuffix, "", "",}; // Should output empty array
  
  @BeforeTest
  public void setUp() {
    SimpleTimeZone.setDefault(SimpleTimeZone.getTimeZone("PST"));
  }

  @Test
  public void testQuery() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setQuery(test);
    assertEquals(sp.getQuery(), test);
    sp.setQuery(null);
    assertEquals(sp.getQuery(), empty); // Should never return null
    sp.setQuery(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getQuery(), test); // test "trim" of field
  }

  @Test
  public void testUnformattedQuery() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setUnformattedQuery(test);
    assertEquals(sp.getUnformattedQuery(), test);
    sp.setUnformattedQuery(null);
    assertEquals(sp.getUnformattedQuery(), empty); // Should never return null
    sp.setUnformattedQuery(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getUnformattedQuery(), test); // test "trim" of field
  }

  @Test
  public void testVolume() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setVolume(test);
    assertEquals(sp.getVolume(), test);
    sp.setVolume(null);
    assertEquals(sp.getVolume(), empty); // Should never return null
    sp.setVolume(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getVolume(), test); // test "trim" of field
  }

  @Test
  public void testELocationId() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setELocationId(test);
    assertEquals(sp.getELocationId(), test);
    sp.setELocationId(null);
    assertEquals(sp.getELocationId(), empty); // Should never return null
    sp.setELocationId(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getELocationId(), test); // test "trim" of field
  }

  @Test
  public void testId() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setId(test);
    assertEquals(sp.getId(), test);
    sp.setId(null);
    assertEquals(sp.getId(), empty); // Should never return null
    sp.setId(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getId(), test); // test "trim" of field
  }

  @Test
  public void testLimitToCategory() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setFilterSubjects(whitespaceAndNullElementsStringArray);
    assertEquals(sp.getFilterSubjects().length, 0);
    sp.setFilterSubjects(null);
    assertEquals(sp.getFilterSubjects().length, 0); // Should never return null
    sp.setFilterSubjects(inputStringArray);
    for (int i = 0 ; i < sp.getFilterSubjects().length ; i++) {
      assertEquals(sp.getFilterSubjects()[i], outputStringArray[i]);
    }
  }

  @Test
  public void testfilterJournals() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setFilterJournals(whitespaceAndNullElementsStringArray);
    assertEquals(sp.getFilterJournals().length, 0);
    sp.setFilterJournals(null);
    assertEquals(sp.getFilterJournals().length, 0); // Should never return null
    sp.setFilterJournals(inputStringArray);
    for (int i = 0 ; i < sp.getFilterJournals().length ; i++) {
      assertEquals(sp.getFilterJournals()[i], outputStringArray[i]);
    }
  }

  @Test
  public void testSort() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setSort(test);
    assertEquals(sp.getSort(), test);
    sp.setSort(null);
    assertEquals(sp.getSort(), empty); // Should never return null
    sp.setSort(trimTestPrefix + test + trimTestSuffix);
    assertEquals(sp.getSort(), test); // test "trim" of field
  }

  @Test
  public void testStartPage() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setStartPage(473);
    assertEquals(sp.getStartPage(), 473);
  }

  @Test
  public void testPageSize() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setPageSize(216);
    assertEquals(sp.getPageSize(), 216);
  }

  /**
   * Test that everything is copied and that all copies are deep copies, meaning that there are
   * no references from the copy back to objects in the original.
   * @throws Exception
   */
  @Test
  public void testCopy() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setQuery("query: " + test);
    sp.setVolume("volume: " + test);
    sp.setELocationId("eLocationId: " + test);
    sp.setId("id: " + test);
    sp.setUnformattedQuery("unformattedQuery: " + test);
    sp.setFilterSubjects(inputStringArray);
    sp.setFilterJournals(inputStringArray);
    sp.setSort("sort: " + test);
    sp.setStartPage(473);
    sp.setPageSize(216);
    
    SearchParameters spCopy = sp.copy();

    assertEquals(spCopy.getQuery(), sp.getQuery());
    sp.setQuery(test);
    assertNotSame(spCopy.getQuery(), sp.getQuery());

    assertEquals(spCopy.getUnformattedQuery(), sp.getUnformattedQuery());
    sp.setUnformattedQuery(test);
    assertNotSame(spCopy.getUnformattedQuery(), sp.getUnformattedQuery());

    assertEquals(spCopy.getVolume(), sp.getVolume());
    sp.setVolume(test);
    assertNotSame(spCopy.getVolume(), sp.getVolume());

    assertEquals(spCopy.getELocationId(), sp.getELocationId());
    sp.setELocationId(test);
    assertNotSame(spCopy.getELocationId(), sp.getELocationId());

    assertEquals(spCopy.getId(), sp.getId());
    sp.setId(test);
    assertNotSame(spCopy.getId(), sp.getId());

    for (int i = 0 ; i < sp.getFilterSubjects().length ; i++) {
      assertEquals(sp.getFilterSubjects()[i], spCopy.getFilterSubjects()[i]);
    }
    sp.setFilterSubjects(new String[]{});
    assertNotSame(spCopy.getFilterSubjects().length, sp.getFilterSubjects().length);
    
    for (int i = 0 ; i < sp.getFilterJournals().length ; i++) {
      assertEquals(sp.getFilterJournals()[i], spCopy.getFilterJournals()[i]);
    }
    sp.setFilterJournals(new String[]{});
    assertNotSame(spCopy.getFilterJournals().length, sp.getFilterJournals().length);
    
    assertEquals(spCopy.getSort(), sp.getSort());
    sp.setSort(test);
    assertNotSame(spCopy.getSort(), sp.getSort());

    assertEquals(spCopy.getStartPage(), sp.getStartPage());
    sp.setStartPage(932);
    assertNotSame(spCopy.getStartPage(), sp.getStartPage());
    
    assertEquals(spCopy.getPageSize(), sp.getPageSize());
    sp.setPageSize(47);
    assertNotSame(spCopy.getPageSize(), sp.getPageSize());

    spCopy = null;
    assertFalse(sp.equals(spCopy));
  }

  @Test
  public void testToString() throws Exception {
    SearchParameters sp = new SearchParameters();
    sp.setQuery("queryString");
    sp.setUnformattedQuery("unformattedQueryString");
    sp.setVolume("volumeString");
    sp.setELocationId("eLocationIdString");
    sp.setId("idString");
    sp.setFilterSubjects(inputStringArray);
    sp.setFilterJournals(inputStringArray);
    sp.setFilterKeyword("keyword");
    sp.setFilterArticleType("articleType");
    sp.setSort("sortString");
    sp.setStartPage(473);
    sp.setPageSize(216);

    assertEquals(sp.toString(), "SearchParameters{query='queryString'," +
        " unformattedQuery='unformattedQueryString'," +
        " volume='volumeString'," +
        " eLocationId='eLocationIdString'," +
        " id='idString'," +
        " filterSubjects=[element Two, element Six with some special characters: !@#$%^&*()[]{}, element Eleven, element Twelve]," +
        " filterKeyword='keyword'," +
        " filterArticleType='articleType'," + 
        " filterJournals=[element Two, element Six with some special characters: !@#$%^&*()[]{}, element Eleven, element Twelve]," +
        " sort='sortString'," +
        " startPage=473," +
        " pageSize=216}");
  }
}
