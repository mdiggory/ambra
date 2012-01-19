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

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * Utility methods for working with files
 */
public class FileUtils {
  private static final Map<String, String> mimeTypeMap;

  static {
    try {
      mimeTypeMap = new MimeTypeToFileExtMapper().getFileExtListByMimeType();
    } catch (IOException ioe) {
      throw new Error("Error loading mime-type map", ioe);
    }
  }

  /**
   * Return the filename from the given absolute path or url.
   *
   * @param absolutePath absolutePath
   * @return filename
   */
  public static String getFileName(final String absolutePath) {
    int lastIndex = absolutePath.lastIndexOf("/");
    if (lastIndex < 0) {
      lastIndex = absolutePath.lastIndexOf("\\");
    }
    return absolutePath.substring(lastIndex + 1);
  }

  /**
   * Gets all the text content from the given url. It is expected that the url will have all
   * content as a text type.
   *
   * @param url url
   * @return the whole content from the url
   * @throws IOException IOException
   */
  public static String getTextFromUrl(final String url) throws IOException {
    // Read all the text returned by the server
    return IOUtils.toString(new URL(url).openStream(), "UTF-8");
  }

  /**
   * Return the first file extension that maps to the given mimeType.
   *
   * @param mimeType the mimeType
   * @return the file extension
   */
  public static String getDefaultFileExtByMimeType(final String mimeType) {
    String extension = mimeTypeMap.get(mimeType.toLowerCase());
    return null == extension ? "" : extension;
  }
}
