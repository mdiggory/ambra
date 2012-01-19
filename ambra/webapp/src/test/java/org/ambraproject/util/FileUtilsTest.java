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

import java.net.URISyntaxException;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static org.testng.Assert.assertEquals;

public class FileUtilsTest {

  @DataProvider(name = "files")
  public String[][] createFiles() {
    return new String[][]{
        {"http://java.sun.com/j2se/1.4.2/docs/api/javax/xml/transform/TransformerFactory.html",
          "TransformerFactory.html"},
        {"/1.4.2/docs/api/javax/xml/transform/TransformerFactory.txt", "TransformerFactory.txt"},
        {"C:\\1.4.2\\docs\\api\\javax\\xml\\transform\\TransformerFactory.txt",
          "TransformerFactory.txt"}
    };
  }

  @DataProvider(name = "mimeTypes")
  public String[][] createMimeTypes() {
    return new String[][]{
        {"image/tiff", "tif"},
        {"text/html", "html"},
        {"text/xml", "xml"},
        {"application/postscript", "ps"},
    };
  }

  @Test(dataProvider = "files")
  public void testFileNameExtraction(String filename, String expected) throws URISyntaxException {
    assertEquals(FileUtils.getFileName(filename), expected);
  }

  @Test(dataProvider = "mimeTypes")
  public void testFileExtForMimeType(String mimeType, String expected) throws Exception {
    assertEquals(FileUtils.getDefaultFileExtByMimeType(mimeType), expected);
  }
}
