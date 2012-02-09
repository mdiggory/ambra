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
package org.ambraproject.util;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;


public class TextUtilsTest {

  @DataProvider(name = "brokenUrls")
  public String[][] createBrokenData() {
    return new String[][]{
        {"http://"},
        {"ftp://"},
        {"..."},
        {"\\"},
        {"http://google.com\\"},
        {"http://www.google.com\\"},
        {"google.com\\"},
        {"--"},
        {"httpss:\\..."},
        {"ftps://www.google.com"},
        {"asdasdasd"},
        {"123123"},
        {"http://www.yahoo.com:asas"},
        {"http://www.   yahoo.com:asas"},
    };
  }

  @DataProvider(name = "correctUrls")
  public String[][] createCorrectData() {
    return new String[][]{
        {"http://www.yahoo.com"},
        {"http://www.yahoo.com:9090"},
        {"http://www.yahoo.com/"},
        {"https://www.yahoo.com/"},
        {"ftp://www.yahoo.com/"},
        {"http://www.google.com//something#somewhere"},
        {"ftp://..."},
    };
  }

  @DataProvider(name = "makeUrls")
  public String[][] createMakeData() {
    return new String[][]{
        {"www.google.com", "http://www.google.com"},
        {"http://www.google.com", "http://www.google.com"},
        {"ftp://www.google.com", "ftp://www.google.com"},
        {"https://www.google.com", "https://www.google.com"},
    };
  }

  @DataProvider(name = "malicious")
  public String[][] createMaliciousData() {
    return new String[][]{
        {"<"},
        {"something<script"},
        {"something>"},
        {"someth&ing"},
        {"someth%ing"},
        {">something"},
        {"s%omething"},
        {"somet)hing"},
        {"(something"},
        {"someth'ing+"},
        {"somethin\"g"}
    };
  }

  @DataProvider(name = "nonMalicious")
  public String[][] createNonMaliciousData() {
    return new String[][]{
        {"something."}
    };
  }

  @DataProvider(name = "hyperlinks")
  public String[][] createHyperlinks() {
    return new String[][]{
        {"Ïwww.google.com", "Ï<a href=\"http://www.google.com\">www.google.com</a>"},
        {"Ï", "Ï"}
    };
  }

  @DataProvider(name = "escapeHyperlinks")
  public String[][] createEscapeHyperlinks() {
    return new String[][]{
        {"Ïwww.google.com", "<p>&Iuml;<a href=\"http://www.google.com\">www.google.com</a></p>"},
        {"Ï", "<p>&Iuml;</p>"}
    };
  }

  @DataProvider(name = "tagsToBeStripped")
  public String[][] createTagsToBeStripped() {
    return new String[][]{
        {"Test string with no tags.", "Test string with no tags."}, // no tags
        {"<i><a href=\"http://www.google.com\">who?</a></i>", "who?"}, // nested tags, attributes
        {"www.google.com</a></p>", "www.google.com"}, // unpaired tags
        {"2>1 and 3 > 2 and 4> 3 and 4 >5", "2>1 and 3 > 2 and 4> 3 and 4 >5"}, // not tags
        {"1<2 and 2 < 3 and 3< 4 and 4< 5", "1<2 and 2 < 3 and 3< 4 and 4< 5"}, // not tags
        {"2>1 and 2<3", "2>1 and 2<3"}, // not tags
        {"1<2 and <p> and <p/>3>2", "1<2 and  and 3>2"}, // brackets and tags
        {"<i/> and 2>1<i>", " and 2>1"}, // brackets and tags
        {"<p></p>", ""} // nothing but tags
    };
  }

  @Test(dataProvider = "brokenUrls")
  public void testValidatesBrokenUrl(String url) {
    assertFalse(TextUtils.verifyUrl(url));
  }

  @Test(dataProvider = "correctUrls")
  public void testValidatesCorrectUrl(String url) {
    assertTrue(TextUtils.verifyUrl(url));
  }

  @Test(dataProvider = "makeUrls")
  public void testMakeUrl(String url, String expected) throws Exception {
    assertEquals(TextUtils.makeValidUrl(url), expected);
  }

  @Test(dataProvider = "malicious")
  public void testMaliciousContent(String data) {
    assertTrue(TextUtils.isPotentiallyMalicious(data));
  }

  @Test(dataProvider = "nonMalicious")
  public void testNonMaliciousContent(String data) {
    assertFalse(TextUtils.isPotentiallyMalicious(data));
  }

  @Test(dataProvider = "hyperlinks")
  public void testHyperLink(String hyperlink, String expected) {
    assertEquals(TextUtils.hyperlink(hyperlink), expected);
  }

  @Test(dataProvider = "escapeHyperlinks")
  public void testEscapeAndHyperlink(String hyperlink, String expected) {
    assertEquals(TextUtils.escapeAndHyperlink(hyperlink), expected);
  }

  @Test(dataProvider = "tagsToBeStripped")
  public void testSimpleStripAllTags(String before, String after) {
    assertEquals(TextUtils.simpleStripAllTags(before), after);
  }

}
