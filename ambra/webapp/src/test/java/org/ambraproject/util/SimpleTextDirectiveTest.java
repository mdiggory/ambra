/* $HeadURL$
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
import static org.testng.Assert.assertEquals;

/**
 * @author Joe Osowski
 */
public class SimpleTextDirectiveTest {

  @DataProvider(name = "content")
  public String[][] createContent() {
    return new String[][]{
        { "“test”:\ntest","&quot;test&quot;:test" },
        { "\"test\"","&quot;test&quot;" },
        { "©®", "&copy;&reg;" },
        { "™", "&trade;" },
        { "\n\r\n–—\r", "&ndash;&mdash;" },
        { "other \rstrange characters:中英对照:removed", "other strange characters::removed" }
    };
  }

  @Test(dataProvider = "content")
  public void testFormatting(String content, String expected) throws Exception {
    assertEquals(SimpleTextDirective.plainText(content), expected);
  }

}

