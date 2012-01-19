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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;

public class ProfanityCheckingServiceTest {

  private ProfanityCheckingServiceImpl service;
  private Collection<String> profaneWordList;
  private Collection<String> expectedAss;

  @BeforeClass
  public void setUp() {
    service = new ProfanityCheckingServiceImpl();
    profaneWordList = new ArrayList<String>();
    profaneWordList.add("ASS");
    profaneWordList.add("bush");
    service.setProfaneWords(profaneWordList);
    expectedAss = new ArrayList<String>();
    expectedAss.add("ASS");
  }

  @DataProvider(name = "asses")
  public String[][] createAss() {
    return new String[][]{
        {"ass"},
        {" ass"},
        {"  ass"},
        {"  \nass"},
        {"aSS"},
        {"am ass"},
        {".ass"},
        {" some ass"},
        {"+ass"},
        {"-ass"},
        {" ass"},
        {" some before Ass and some after"},
        {" some \n before and some after\n before Ass and some after"},
        {" (Ass "},
        {"[Ass]"},
    };
  }

  @DataProvider(name = "nonProfanity")
  public String[][] createNonProfanity() {
    return new String[][]{
        {"ambush"},
        {" some ambush"},
        {" amBush "},
        {" some before amBush and some after"},
        {" some \n before some before some \n before amBush and some after \n adter"}
    };
  }


  @Test(dataProvider = "asses")
  public void testShouldCatchProfaneText(String word) {
    assertEquals(service.validate(word), expectedAss, "Profanity not caught");
  }

  @Test
  public void testShouldCatchMultipleProfaneText() {
    assertEquals(service.validate("[Ass] and bush"), profaneWordList, "Profanity not caught");
  }

  @Test(dataProvider = "nonProfanity")
  public void testShouldAllowTextWhichIsNotProfane(String word) {
    assertEquals(service.validate(word).size(), 0, "Wrong word caught");
  }

}
