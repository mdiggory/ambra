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
package org.topazproject.ambra.util;

import junit.framework.TestCase;

import java.util.Set;
import java.util.HashSet;

import org.topazproject.ambra.util.TokenGenerator;

/**
 *
 */
public class TestUniqueTokenGenerator extends TestCase {
  public void testShouldGenerateUniqueTokens() {
    final int loopCount = 1000;
    final Set<String> set = new HashSet<String>(loopCount);
    for (int i = 0; i < loopCount; i++) {
      set.add(TokenGenerator.getUniqueToken());
    }

    assertEquals(loopCount, set.size());
  }
}
