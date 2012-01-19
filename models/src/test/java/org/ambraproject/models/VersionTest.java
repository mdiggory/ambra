/*
 * $HeadURL$
 * $Id$
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
package org.ambraproject.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Joe Osowski
 */
public class VersionTest extends BaseHibernateTest {

  @Test
  public void testVersion() {
    Version version = new Version();

    version.setName("Ambra 1.0");
    version.setVersion(100);
    version.setUpdateInProcess(false);

    Long id = (Long) hibernateTemplate.save(version);
    assertNotNull(id, "session generated null id");
    version = (Version) hibernateTemplate.get(Version.class, id);

    assertEquals(version.getName(), "Ambra 1.0", "incorrect version name");
    assertEquals(version.getVersion(), 100, "incorrect version");
    assertEquals(version.getUpdateInProcess(), false, "incorrect update in progress");
  }
}
