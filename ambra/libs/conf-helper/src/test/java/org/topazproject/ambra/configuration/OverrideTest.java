/* $HeadURL::                                                                                      $
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
package org.topazproject.ambra.configuration;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.topazproject.ambra.configuration.ConfigurationStore;

import junit.framework.TestCase;

public class OverrideTest extends TestCase {
  private static Configuration conf = null;

  protected void setUp() throws ConfigurationException {
    System.setProperty(ConfigurationStore.OVERRIDES_URL, "ambra/configuration/defaults-dev.xml");
    System.setProperty("conf.test", "goodbye world");
    ConfigurationStore store = ConfigurationStore.getInstance();
    store.loadConfiguration(null);
    conf = store.getConfiguration();
  }

  protected void tearDown() {
    // Make an attempt to remove the system property for other test classes
    Properties p = System.getProperties();
    p.remove(ConfigurationStore.OVERRIDES_URL);
    p.remove("conf.test");
    System.setProperties(p);
  }

  public void testDefaultsOverrideGlobal() {
    assertEquals("defaults override", "override-dev", conf.getString("conf.def"));
  }

  public void testSystemPropertyOverride() {
    assertEquals("system property override", "goodbye world", conf.getString("conf.test"));
  }
}
