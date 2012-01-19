/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/test/java/org/plos/filestore/BaseFileStoreTest.java $
 * $Id: BaseFileStoreTest.java 9144 2011-06-15 22:16:45Z josowski $
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

package org.ambraproject.filestore;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.*;

/**
 * Base class for tests of {@link org.ambraproject.filestore.FileStoreService}
 *
 * @author Alex Kudlick Date: Mar 9, 2011
 *         <p/>
 *         org.ambraproject.filestore
 */
public abstract class BaseFileStoreTest extends AbstractTestNGSpringContextTests {

  private static final String TEMP_OUTPUT_FILE = "temp-output-file";
  private static final int BUFFER_SIZE = 16384;

  /**
   * Helper method to set up a temp file for writing to
   *
   * @return - the temp file
   */
  protected File setUpTempOutputFile() throws IOException {
    File tempOutputFile = new File(TEMP_OUTPUT_FILE);
    if (tempOutputFile.exists()) {
      tempOutputFile.delete();
    }
    tempOutputFile.createNewFile();
    tempOutputFile.deleteOnExit();
    return tempOutputFile;
  }

  /**
   * Helper method to write to a a temporary output file
   * @param inputStream - the input stream to use to write
   * @return - the file that the data was written to
   * @throws IOException - if there was a problem writing the data
   */
  protected File copyToTempOutputFile(InputStream inputStream) throws IOException {
    File tempOutputFile = setUpTempOutputFile();
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(tempOutputFile);
      int bytesRead;
      byte[] buffer = new byte[BUFFER_SIZE];
      while ((bytesRead = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, bytesRead);
      }
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
    return tempOutputFile;
  }
}
