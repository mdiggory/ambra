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

import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.core.Environment;

import java.util.Map;
import java.io.IOException;
import java.io.Writer;

/**
 * Freemarker directive for formatting of author last names.
 * It takes nested content and converts it into standard format.
 * For example "Marie-Louise" is converted to "M-L"
 *
 */
public class AuthorNameAbbreviationDirective implements TemplateDirectiveModel {
  public void execute(Environment environment, Map params,
    TemplateModel[] loopVars,
    TemplateDirectiveBody body)
    throws TemplateException, IOException {

    if (!params.isEmpty()) {
      throw new TemplateModelException(
          "AuthorNameAbbreviationDirective doesn't allow parameters.");
    }

    if (loopVars.length != 0) {
      throw new TemplateModelException(
          "AuthorNameAbbreviationDirective doesn't allow loop variables.");
    }

    if (body != null) {
      body.render(new AuthorNameWriter(environment.getOut()));
    }
  }

  /**
   * A {@link Writer} that transforms the author name as character stream
   */
  private static class AuthorNameWriter extends Writer {
    private final Writer out;

    AuthorNameWriter(Writer out) {
      this.out = out;
    }

    public void write(char[] chars, int off, int len) throws IOException {
      out.write(toShortFormat(new String(chars, off, len)));
    }

    public void flush() throws IOException {
      out.flush();
    }

    public void close() throws IOException {
      out.close();
    }

    private String toShortFormat(String name) {
      if (name == null)
        return null;

      String[] givenNames = name.split(" ");
      StringBuilder sb = new StringBuilder();
      for(String givenName :givenNames) {
        if (givenName.length() > 0) {
          if(givenName.matches(".*\\p{Pd}\\p{Lu}.*")) {
            // Handle names with dash
            String[] sarr = givenName.split("\\p{Pd}");
            for (int i = 0; i < sarr.length; i++) {
              if (i > 0) {
                sb.append('-');
              }

              if(sarr[i].length() > 0) {
                sb.append(sarr[i].charAt(0));
              }
            }
          }
          else {
            sb.append(givenName.charAt(0));
          }
        }
      }

      return sb.toString();

    }
  }
}
