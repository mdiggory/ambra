/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/test/java/org/plos/filestore/LocalFileStoreTest.java $
 * $Id: LocalFileStoreTest.java 9144 2011-06-15 22:16:45Z josowski $
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;

import static org.testng.Assert.*;

/**
 * Test for the local {@link org.ambraproject.filestore.impl.FileSystemImpl} file store service.  Working directory for the test
 * must be set to the file store project base directory
 *
 * @author Alex Kudlick Date: Mar 8, 2011
 *         <p/>
 *         org.ambraproject.filestore
 */
@ContextConfiguration(locations = "local-file-system-context.xml")
public class LocalFileStoreTest extends BaseFileStoreTest {

  @Qualifier("filesToStoreDir")
  @Autowired
  protected File filesToStoreDir;
  @Qualifier("fileStoreBaseDir")
  @Autowired
  protected File fileStoreBaseDir;

  @Autowired
  protected FileStoreService fileStoreService;

  @DataProvider(name = "fileIds")
  public Object[][] getFileIds() {
    return new Object[][]{
        {"10.1371/journal.pgen.1000096/MANIFEST.xml", 2392},
        {"10.1371/journal.pgen.1000096/pgen.1000096.g001.PNG_S", 18773},
        {"10.1371/journal.pgen.1000100/pgen.1000100.t002.tif", 329700},
        {"10.1371/journal.pgen.1000103/pgen.1000103.xml", 139837}
    };
  }

  @Test(dataProvider = "fileIds")
  public void testGetFileById(String id, long expectedLength) throws FileStoreException, IOException {
    InputStream inputStream = null;
    try {
      inputStream = fileStoreService.getFileInStream(id);
      assertNotNull(inputStream, "Returned null input stream");
      File tempOutputFile = copyToTempOutputFile(inputStream);
      assertEquals(tempOutputFile.length(), expectedLength, "File didn't have correct length");
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  @Test(dataProvider = "fileIds", invocationTimeOut = 10000, invocationCount = 1)
  public void testGetByteArray(String id, long expectedLength) throws FileStoreException {
    byte[] fileData = fileStoreService.getFileByteArray(id);
    assertNotNull(fileData, "returned null byte array for file");
    assertEquals(fileData.length, expectedLength, "byte array had incorrect length");
  }

  @Test(dataProvider = "fileIds")
  public void testCopyFileFromStore(String id, long expectedLength) throws FileStoreException, IOException {
    final File destFile = setUpTempOutputFile();
    fileStoreService.copyFileFromStore(id, destFile);
    assertEquals(destFile.length(), expectedLength, "Destination file didn't have correct size");
  }

  @DataProvider(name = "filesToStore")
  public Object[][] getFilesToStore() {
    return new Object[][]{
        {"10.1371/journal.pntd.0000241/pntd.0000241.xml", new File(filesToStoreDir, "pntd.0000241.xml"), 106254},
        {"10.1371/journal.ppat.1000011/ppat.1000011.xml", new File(filesToStoreDir, "ppat.1000011.xml"), 88530},
        {"10.1371/journal.pone.0002020/pone.0002020.g003.tif", new File(filesToStoreDir, "pone.0002020.g003.tif"), 692428}
    };
  }

  @Test(dataProvider = "filesToStore", groups = {"fileOut"})
  public void testGetFileOutStream(String fsid, File fileToCopy, long expectedLength) throws FileStoreException, IOException {

    OutputStream outputStream = fileStoreService.getFileOutStream(fsid, expectedLength);
    assertNotNull(outputStream, "Returned null output stream");
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(fileToCopy);
      fileStoreService.copy(inputStream, outputStream);

      File storedFile = new File(fileStoreBaseDir, fsid);
      assertTrue(storedFile.exists(), "Didn't store file");
      assertEquals(storedFile.length(), expectedLength, "Stored file didn't have correct length");

    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      outputStream.close();
    }
    assertTrue(fileToCopy.exists(), "File that was copied was deleted");
    assertEquals(fileToCopy.length(), expectedLength, "File that was copied was changed; the number of bytes changed");
  }

  @Test(dataProvider = "filesToStore", groups = {"copyFileTo"},
      dependsOnMethods = {"testGetFileOutStream"},ignoreMissingDependencies = true)
  public void testCopyFileToStore(String fsid, File fileToCopy, long expectedLength) throws FileStoreException {
    fileStoreService.copyFileToStore(fsid, fileToCopy);
    File copiedFile = new File(fileStoreBaseDir, fsid);
    assertTrue(copiedFile.exists(),"Didn't write file");
    assertEquals(copiedFile.length(), expectedLength, "Stored file didn't have correct size");
    assertTrue(fileToCopy.exists(), "File that was copied was deleted");
    assertEquals(fileToCopy.length(), expectedLength, "File that was copied was changed; the number of bytes changed");
  }

  @AfterGroups(groups = {"fileOut","copyFileTo"})
  public void deleteStoredFiles() {
    for (File directory : new File[]{
        new File(fileStoreBaseDir, "10.1371/journal.pntd.0000241"),
        new File(fileStoreBaseDir, "10.1371/journal.ppat.1000011"),
        new File(fileStoreBaseDir, "10.1371/journal.pone.0002020")}) {
      if (directory.exists()) {
        for (File file : directory.listFiles()) {
          file.delete();
        }
        directory.delete();
      }

    }

  }
}
