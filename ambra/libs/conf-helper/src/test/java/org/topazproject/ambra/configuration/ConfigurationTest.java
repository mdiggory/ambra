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

import java.util.List;
import java.net.URL;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.topazproject.ambra.configuration.ConfigurationStore;

import junit.framework.TestCase;

public class ConfigurationTest extends TestCase {
  private static Configuration conf = null;

  protected void setUp() throws ConfigurationException {
    ConfigurationStore store = ConfigurationStore.getInstance();
    ClassLoader loader = getClass().getClassLoader();
    System.setProperty("ambra.virtualJournals.templateDir", loader.getResource(".").getFile());
    store.loadConfiguration(loader.getResource("ambra/configuration/defaults-dev.xml"));
    conf = store.getConfiguration();
    /*
     * We want to use a test version of global-defaults.
     * However there is no way to override it. So we add this
     * at the end (last place looked).
     */
    ConfigurationStore.addResources((CombinedConfiguration)conf, 
        "/ambra/configuration/global-defaults-test.xml");
  }

  protected void tearDown() {
  }

  public void testGlobalDefaults() {
    assertEquals("global-defaults conf.test", "hello world", conf.getString("conf.test"));
  }

  public void testDefaults() {
    assertEquals("defaults defaults", "value", conf.getString("defaults"));
  }

  public void testDefaultsOverrideGlobal() {
    assertEquals("defaults override", "override-dev", conf.getString("conf.def"));
  }

  public void testExpandedDefaultsOverride() {
    checkExpTest(conf.getList("exptest.overrides.item"));
  }

  public void testExpandedDefaults() {
    checkExpTest(conf.getList("exptest.local.item"));
  }

  public void testSubsetDefaults1() {
    Configuration conf = this.conf.subset("exptest.local");
    checkExpTest(conf.getList("item"));
  }

  public void testSubsetDefaults2() {
    Configuration conf = this.conf.subset("exptest");
    checkExpTest(conf.getList("local.item"));
  }

  public void testSubsetDefaultsOverride1() {
    Configuration conf = this.conf.subset("exptest.overrides");
    checkExpTest(conf.getList("item"));
  }

  public void testSubsetDefaultsOverride2() {
    Configuration conf = this.conf.subset("exptest");
    checkExpTest(conf.getList("overrides.item"));
  }

  private void checkExpTest(List l) {
    assertNotNull(l);
    assertEquals(3, l.size());
    assertEquals("http://test1:8080/", l.get(0));
    assertEquals("http://test2:8080/", l.get(1));
    assertEquals("http://test1:8080/", l.get(2));
  }


}
