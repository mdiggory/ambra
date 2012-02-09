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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * XPointer related utility methods.
 * 
 * @author jkirton
 * @see <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a>
 * @see <a href="http://www.w3.org/TR/xptr-xpointer">http://www.w3.org/TR/xptr-xpointer</a>
 */
public abstract class XPointerUtils {
  /**
   * Creates a string-range xpointer fragment.
   * 
   * @param location
   * @param string The string to match
   * @param offset 
   * @param length
   * @param occurrenceOrdinal The 1-based number to indicate the nth occurrence of the matching text.
   * @return string-range xpointer fragment
   * @see <a href="http://www.w3.org/TR/WD-xptr#stringrange">http://www.w3.org/TR/WD-xptr#stringrange</a>
   */
  public static String createStringRangeFragment(String location, String string, int offset,
                                                 int length, int occurrenceOrdinal) {
    return "string-range(" + location + ", '" + string + "', " + offset + ", " + length +
           ")[" + occurrenceOrdinal + "]";
  }

  /**
   * Creates a string-range xpointer fragment.
   * 
   * @param location
   * @param string
   * @param offset
   * @return string-range xpointer fragment
   * @see <a href="http://www.w3.org/TR/WD-xptr#stringrange">http://www.w3.org/TR/WD-xptr#stringrange</a>
   */
  public static String createStringRangeFragment(String location, String string, int offset) {
    return "string-range(" + location + ", '" + string + "')[" + offset + "]";
  }

  /**
   * @param startPoint
   * @param endPoint
   * @return range-to xpointer fragment
   */
  public static String createRangeToFragment(String startPoint, String endPoint) {
    return startPoint + "/range-to(" + endPoint + ")";
  }

  /**
   * Creates an xpointer string.
   * 
   * @param prefix
   * @param localPart
   * @param encoding The encoding to employ for encoding the local part URI
   * @return xpointer String
   * @throws UnsupportedEncodingException
   */
  public static String createXPointer(String prefix, String localPart, String encoding)
      throws UnsupportedEncodingException {
    return prefix + "#xpointer(" + URLEncoder.encode(localPart, encoding) + ")";
  }
}
