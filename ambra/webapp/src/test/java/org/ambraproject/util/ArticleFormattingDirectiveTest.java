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
 * @author Dragisa Krsmanovic
 */
public class ArticleFormattingDirectiveTest {

  @DataProvider(name = "content")
  public String[][] createContent() {
    return new String[][]{
        {"foo<italic>bar</italic>",             "foo<i>bar</i>"},
        {"foo<italic>bar</italic> and <italic>yar</italic>", "foo<i>bar</i> and <i>yar</i>"},
        {"foo&lt;italic&gt;bar&lt;/italic&gt;", "foo<i>bar</i>"},
        {"foo<bold>bar</bold>",                 "foo<b>bar</b>"},
        {"foo<italic>bar</italic> and <bold>yar</bold>", "foo<i>bar</i> and <b>yar</b>"},
        {"foo<monospace>bar</monospace>",       "foo<span class=\"monospace\">bar</span>"},
        {"foo<overline>bar</overline>",         "foo<span class=\"overline\">bar</span>"},
        {"foo<sc>bar</sc>",                     "foo<small>bar</small>"},
        {"foo<strike>bar</strike>",             "foo<s>bar</s>"},
        {"foo<underline>bar</underline>",       "foo<u>bar</u>"},
        {"foo<named-content xmlns:xlink= \"http://www.w3.org/1999/xlink\" " +
            "content-type=\"genus-species\" xlink:type=\"simple\">bar</named-content>",
            "foo<i>bar</i>"},
        {"foo<named-content     content-type=\"genus-species\" xlink:type=\"simple\" " +
            "xmlns:xlink= \"http://www.w3.org/1999/xlink\" >bar</named-content>",
            "foo<i>bar</i>"},
        {"foo<named-content>bar</named-content>","foo<i>bar</i>"},
        {"foo<email>bar@plos.org</email>",    "foo<a href=\"mailto:bar@plos.org\">bar@plos.org</a>"},
        {"foo<email xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"simple\">bar@plos.org</email>",
            "foo<a href=\"mailto:bar@plos.org\">bar@plos.org</a>"},
        {"Something <bold>is<email>nested</email></bold> here",
            "Something <b>is<a href=\"mailto:nested\">nested</a></b> here"},
        {"<sec id=\"st1\">\n\t\t<title/></sec>","</sec>"},
        {"<sec id=\"st1\">\n\t\t<title /></sec>","</sec>"}
    };
  }

  @Test(dataProvider = "content")
  public void testFormatting(String content, String expected) throws Exception {
    assertEquals(ArticleFormattingDirective.format(content), expected);
  }

}
