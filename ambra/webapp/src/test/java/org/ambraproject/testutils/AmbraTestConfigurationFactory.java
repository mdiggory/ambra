/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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
package org.ambraproject.testutils;

import org.apache.commons.configuration.Configuration;
import org.topazproject.ambra.configuration.ConfigurationStore;

import java.io.File;
import java.net.URL;

/**
 * Factory for creating {@link org.apache.commons.configuration.Configuration} objects.  Provided so that unit tests can
 * use a test config file
 *
 * @author Alex Kudlick Date: 5/11/11
 *         <p/>
 *         org.topazproject.ambra
 */
public class AmbraTestConfigurationFactory {

  public static Configuration getConfiguration(String relativeConfigLocation) throws Exception {
    ConfigurationStore configurationStore = ConfigurationStore.getInstance();
    String absoluteUrl = "file://" + new File(relativeConfigLocation).getAbsolutePath();
    configurationStore.loadConfiguration(new URL(absoluteUrl));
    return configurationStore.getConfiguration();
  }

}
