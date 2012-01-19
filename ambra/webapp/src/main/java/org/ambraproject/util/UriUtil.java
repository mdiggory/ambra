/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Static utility class to validate uris.  Copied from <a href="http://topazproject.org/svn/head/topaz/core/src/main/java/org/topazproject/otm/RdfUtil.java">Topaz</a>
 *
 * @author Alex Kudlick Date: 5/26/11
 *         <p/>
 *         org.ambraproject.util
 */
public class UriUtil {

  private UriUtil() {
    //non-instantiable
  }

  /**
   * Does input validation for uri parameters. Only absolute (non-relative) URIs are valid.
   * <p/>
   * In usage, it essentailly asserts that a URI string is a valid URI and throws a subclass of RuntimeException if
   * not.
   * <p/>
   * As a helpful side-effect, this function also returns the uri as a proper java.net.URI that can be used for further
   * processing.
   *
   * @param uri  the uri string to validate
   * @param name the name of this uri for use in error messages
   * @return Returns the uri
   * @throws NullPointerException     if the uri string is null
   * @throws IllegalArgumentException if the uri is not a valid absolute URI
   */
  public static URI validateUri(String uri, String name) {
    if (uri == null)
      throw new NullPointerException("'" + name + "' cannot be null");

    try {
      URI u = new URI(uri);

      if (!u.isAbsolute())
        throw new URISyntaxException(uri, "missing scheme component", 0);

      return u;
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("'" + name + "' must be a valid absolute URI", e);
    }
  }
}
