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
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Freemarker directive for formatting and removing characters that may break javascript
 *
 */
public class SimpleTextDirective implements TemplateDirectiveModel {
  private static final Logger log = LoggerFactory.getLogger(SimpleTextDirective.class);
  
  private static final Pattern[] PATTERNS = {
        Pattern.compile("[\\n\\r]"),
        Pattern.compile("[“”\"]"), // Smart Quotes
        Pattern.compile("\u00a9"), // Copyright
        Pattern.compile("\u00ae"), // Registered Trademark
        Pattern.compile("\u2122"), // Trademark
        Pattern.compile("\u2013"), // ndash
        Pattern.compile("\u2014")  // mdash

  };

  private static final String[] REPLACEMENTS = {
      "",
      "&quot;",
      "&copy;",
      "&reg;",
      "&trade;",
      "&ndash;",
      "&mdash;"
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

   private static class AmbraTextWriter extends Writer {
    private final Writer out;

    AmbraTextWriter(Writer out) {
      this.out = out;
    }

    public void write(char[] chars, int off, int len) throws IOException {
      out.write(plainText(new String(chars, off, len)));
    }

    public void flush() throws IOException {
      out.flush();
    }

    public void close() throws IOException {
      out.close();
    }
  }

  /**
   * This will convert a string to use the US-ASCII character set
   * It removes carrige returns, it replaces some characters with appropriate html entity codes
   * and it removes anything it doesn't understand
   * 
   * @param str input string.
   * @return converted string.
   */
  public static String plainText(String str) {
    if (str == null)
      return null;

    String result = str;

    //First find any known characters and replace them logically
    for (int i = 0; i < PATTERNS.length; i++) {
      result = PATTERNS[i].matcher(result).replaceAll(REPLACEMENTS[i]);
    }

    //Now, if there is any other characters we don't yet know about
    //Let's handle it somewhat gracefully
    Charset charset = Charset.forName("US-ASCII");
    CharsetDecoder decoder = charset.newDecoder();
    CharsetEncoder encoder = charset.newEncoder();

    //Basically ignore weird characters 
    encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
    
    ByteBuffer bbuf;
    try {
      bbuf = encoder.encode(CharBuffer.wrap(result));
      CharBuffer cbuf = decoder.decode(bbuf);

      result = cbuf.toString();
    } catch (CharacterCodingException ex) {
      //Lets not bring down the whole process if we error out
      log.error("Error trying to decode string: '" + str + "'", ex);

      return "?";
    }

    return result;
  }
}

