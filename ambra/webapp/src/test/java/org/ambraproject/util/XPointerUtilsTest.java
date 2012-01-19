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

package org.ambraproject.util;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

/**
 * @author Dragisa Krsmanovic
 */
public class XPointerUtilsTest {

  @Test
  public void createRangeToFragment() {
    assertEquals(XPointerUtils.createRangeToFragment("start", "end"), "start/range-to(end)");
  }

  @Test
  public void createStringRangeFragment() {
    assertEquals(XPointerUtils.createStringRangeFragment("location", "text",  5),
        "string-range(location, 'text')[5]");
  }

  @Test
  public void createStringRangeFragment2() {
    assertEquals(XPointerUtils.createStringRangeFragment("location", "text",  5, 11, 3),
        "string-range(location, 'text', 5, 11)[3]");
  }

  @Test
  public void createXPointer() throws UnsupportedEncodingException {
    assertEquals(XPointerUtils.createXPointer("prefix", "one:two/three four-five+six;", "UTF-8"),
        "prefix#xpointer(one%3Atwo%2Fthree+four-five%2Bsix%3B)");
  }

}
