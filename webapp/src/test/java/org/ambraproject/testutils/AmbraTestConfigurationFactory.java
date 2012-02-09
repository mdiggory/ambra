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

import java.io.*;
import java.util.Random;

/**
 * Factory for creating {@link org.apache.commons.configuration.Configuration} objects.  Writes the configuration to a temporary
 * file so that other projects can use this from jars.
 *
 * @author Alex Kudlick Date: 5/11/11
 *         <p/>
 *         org.topazproject.ambra
 */
public class AmbraTestConfigurationFactory {

  public static Configuration getConfiguration(String relativeConfigLocation) throws Exception {
    ConfigurationStore configurationStore = ConfigurationStore.getInstance();
    File tmpFile = writeToTempFile(relativeConfigLocation);
    configurationStore.loadConfiguration(tmpFile.toURI().toURL());
    return configurationStore.getConfiguration();
  }

  private static File writeToTempFile(String source) throws IOException {
    int r = Math.abs(new Random().nextInt());
    String filename = System.getProperty("java.io.tmpdir") + File.separatorChar + "ambra-test-config-" + r + ".xml";
    File destinationFile = new File(filename);
    destinationFile.deleteOnExit();

    InputStream inputStream = null;
    OutputStream outputStream = null;

    try {
      inputStream = getInputStream(source);
      outputStream = new FileOutputStream(destinationFile);
      final byte[] buf = new byte[1024];
      int len;
      while ((len = inputStream.read(buf)) > 0) {
        outputStream.write(buf, 0, len);
      }
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          //suppress
        }
      }
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          //suppress
        }
      }
    }
    return destinationFile;
  }

  private static InputStream getInputStream(String resource) throws FileNotFoundException {
    InputStream inputStream;
    inputStream = AmbraTestConfigurationFactory.class.getResourceAsStream(resource);
    if (inputStream == null) {
      //try with the class loader
      inputStream = AmbraTestConfigurationFactory.class.getClassLoader().getResourceAsStream(resource);
    }
    if (inputStream == null) {
      //last resort try opening a file
      inputStream = new FileInputStream(resource);
    }
    return inputStream;
  }

}
