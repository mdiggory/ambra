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

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

import java.util.Map;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.Writer;

/**
 * Freemarker directive for converting XML formatting into HTML formatting.
 * <br/>
 * For example &lt;italic&gt;foo&lt;/italic&gt; is converted to &lt;i&gt;foo&lt;/i&gt;
 * <br/>
 * Conversion table:
 * <table border="1">
 *  <tr>
 *    <th>XML</th>
 *    <th>HTML</th>
 *  </tr>
 *  <tr>
 *    <td>&lt;italic&gt;</td><td>&lt;i&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;bold&gt;</td><td>&lt;b&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;monospace&gt;</td><td>&lt;span class="monospace"&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;overline&gt;</td><td>&lt;span class="overline"&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;sc&gt;</td><td>&lt;small&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;strike&gt;</td><td>&lt;s&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;underline&gt;</td><td>&lt;u&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;named-content xmlns:xlink="http://www.w3.org/1999/xlink"
 * content-type="genus-species" xlink:type="simple"&gt;</td>
 *    <td>&lt;i&gt;</td>
 *  </tr>
 *  <tr>
 *    <td>&lt;email xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple"&gt;</td><td>&lt;a href="mailto:<i>email_address</i>"&gt;</td>
 *  </tr>
 * </table>
 */
public class ArticleFormattingDirective implements TemplateDirectiveModel {


  private static final Pattern[] PATTERNS = {
      Pattern.compile("(?:<|&lt;)italic(?:>|&gt;)(.*?)(?:<|&lt;)/italic(?:>|&gt;)"),
      Pattern.compile("<named-content(?:" +
          "(?:\\s+xmlns:xlink\\s*=\\s*\"http://www.w3.org/1999/xlink\"\\s*)|" +
          "(?:\\s+content-type\\s*=\\s*\"genus-species\"\\s*)|" +
          "(?:\\s+xlink:type\\s*=\\s*\"simple\"\\s*)" +
          ")*>(.*?)</named-content>"),
      Pattern.compile("<bold>(.*?)</bold>"),
      Pattern.compile("<monospace>(.*?)</monospace>"),
      Pattern.compile("<overline>(.*?)</overline>"),
      Pattern.compile("<sc>(.*?)</sc>"),
      Pattern.compile("<strike>(.*?)</strike>"),
      Pattern.compile("<underline>(.*?)</underline>"),
      Pattern.compile("<email(?:" +
          "(?:\\s+xmlns:xlink\\s*=\\s*\"http://www.w3.org/1999/xlink\"\\s*)|" +
          "(?:\\s+xlink:type\\s*=\\s*\"simple\"\\s*)" +
          ")*>(.*?)</email>"),
      Pattern.compile("<sec id=\\\"st1\\\">[\n\t ]*<title ?/>")
  };

  private static final String[] REPLACEMENTS = {
      "<i>$1</i>",
      "<i>$1</i>",
      "<b>$1</b>",
      "<span class=\"monospace\">$1</span>",
      "<span class=\"overline\">$1</span>",
      "<small>$1</small>",
      "<s>$1</s>",
      "<u>$1</u>",
      "<a href=\"mailto:$1\">$1</a>",
      ""
  };

  public void execute(Environment environment, Map params, TemplateModel[] loopVars,
                      TemplateDirectiveBody body)
      throws TemplateException, IOException {

    if (!params.isEmpty()) {
      throw new TemplateModelException(
          "ArticleFormattingDirective doesn't allow parameters.");
    }

    if (loopVars.length != 0) {
      throw new TemplateModelException(
          "ArticleFormattingDirective doesn't allow loop variables.");
    }

    if (body != null) {
      body.render(new AmbraTextWriter(environment.getOut()));
    }
  }

  /**
   * A {@link java.io.Writer} that transforms the author name as character stream
   */
  private static class AmbraTextWriter extends Writer {
    private final Writer out;

    AmbraTextWriter(Writer out) {
      this.out = out;
    }

    public void write(char[] chars, int off, int len) throws IOException {
      out.write(format(new String(chars, off, len)));
    }

    public void flush() throws IOException {
      out.flush();
    }

    public void close() throws IOException {
      out.close();
    }
  }

  /**
   * Static method that does conversion. Can be used in Java code.
   * @param str input string.
   * @return converted string.
   */
  public static String format(String str) {
    if (str == null)
      return null;

    String result = str;
    for (int i = 0; i < PATTERNS.length; i++) {
      result = PATTERNS[i].matcher(result).replaceAll(REPLACEMENTS[i]);
    }

    return result;

  }
}
