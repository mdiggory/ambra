/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.annotation;

import org.ambraproject.ApplicationException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 2/21/12
 */
public class ContextFormatterTest {

  @Test
  public void test() throws ApplicationException {
    String expectedXPointer = "info:doi/10.1371/journal.pbio.0020083#xpointer(string-range%28%2Farticle%5B1%5D%2Fbody%5B1%5D%2Fsec%5B1%5D%2Fp%5B3%5D%2C+%27%27%2C+107%2C+533%29%5B1%5D)";
    String result = ContextFormatter.asXPointer(
        new Context(
            "/article[1]/body[1]/sec[1]/p[3]",
            107,
            "/article[1]/body[1]/sec[1]/p[3]",
            640,
            "info:doi/10.1371/journal.pbio.0020083")
    );
    assertEquals(result, expectedXPointer, "returned incorrect xpointer");
  }

  @Test
  public void testAcrossSections() throws ApplicationException {
    String expectedXPointer = "info:doi/10.1371/journal.pbio.0020083#xpointer(string-range%28%2Farticle%5B1%5D%2Fbody%5B1%5D%2Fsec%5B1%5D%2Fp%5B3%5D%2C+%27%27%29%5B916%5D%2Frange-to%28string-range%28%2Farticle%5B1%5D%2Fbody%5B1%5D%2Fsec%5B1%5D%2Fp%5B4%5D%2C+%27%27%29%5B219%5D%29)";
    String result = ContextFormatter.asXPointer(
        new Context(
            "/article[1]/body[1]/sec[1]/p[3]",
            916,
            "/article[1]/body[1]/sec[1]/p[4]",
            219,
            "info:doi/10.1371/journal.pbio.0020083")
    );
    assertEquals(result, expectedXPointer, "returned incorrect xpointer");
  }
}
