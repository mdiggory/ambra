/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/test/java/org/plos/filestore/FSIDMapperTest.java $
 * $Id: FSIDMapperTest.java 9599 2011-09-27 00:03:46Z josowski $
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

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test the FSIDMapper
 *
 * @author Joe Osowski
 *
 */
@ContextConfiguration(locations = "local-file-system-context.xml")
public class FSIDMapperTest extends BaseFileStoreTest {

  @DataProvider(name = "files")
  public Object[][] files() {
    //List of values and expected results
    return new Object[][] {
      { "info:doi/10.1371/journal.image.ppat.v04.i08", "image.ppat.v04.i09.g001.PNG_L",
          "/10.1371/image.ppat.v04.i08/image.ppat.v04.i09.g001.png_l" },
      { "info:doi/10.1371/image.ppat.v04.i09", "image.ppat.v04.i09.g001.PNG_L",
          "/10.1371/image.ppat.v04.i09/image.ppat.v04.i09.g001.png_l" },
      { "info:doi/10.1371/image.ppat.v04.i09", "image.ppat.v04.i09.XML",
          "/10.1371/image.ppat.v04.i09/image.ppat.v04.i09.xml" },
      { "info:doi/10.1371/image.ppat.v04.i09", "MANIFEST.xml",
          "/10.1371/image.ppat.v04.i09/manifest.xml" },
      { "info:doi/10.1371/journal.pbio.0000092", "pbio.0000092.g001.PNG_S",
          "/10.1371/pbio.0000092/pbio.0000092.g001.png_s" },
      { "info:doi/10.1371/image.pntd.v05.i09", "image.pbio.v01.i01.g001.PNG_L",
        "/10.1371/image.pntd.v05.i09/image.pbio.v01.i01.g001.png_l" }
    };
  }

  @DataProvider(name = "fileRequests")
  public Object[][] fileRequests() {
    //List of values and expected results
    return new Object[][] {
      { "info:doi/10.1371/image.ppat.v04.i09", "PDF", "/10.1371/image.ppat.v04.i09/image.ppat.v04.i09.pdf" },
      { "info:doi/10.1371/image.ppat.v04.i09", "XML", "/10.1371/image.ppat.v04.i09/image.ppat.v04.i09.xml" },
      { "info:doi/10.1371/journal.pone.0020231.g001", "PNG_S", "/10.1371/pone.0020231/pone.0020231.g001.png_s" },
      { "info:doi/10.1371/journal.pone.0020231.t002", "PNG_S", "/10.1371/pone.0020231/pone.0020231.t002.png_s" }
    };
  }

  @Test(dataProvider = "files")
  public void testZipToFSID(String doi, String filename, String expectedResult) throws Exception {
    String result = FSIDMapper.zipToFSID(doi, filename);
    assertEquals(result, expectedResult, "File names are not equal");
  }

  @Test(dataProvider = "fileRequests")
  public void testDoiTofsid(String uri, String type, String expectedResult) {
    String result = FSIDMapper.doiTofsid(uri, type);

    assertEquals(result, expectedResult, "File names are not equal");
  }
}
