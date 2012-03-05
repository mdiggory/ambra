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
package org.topazproject.ambra.configuration;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Collection;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.commons.configuration.tree.UnionCombiner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A singleton that manages the load/unload/re-load of Configuration.<p>
 *
 * Configuration consists of a layered set of configuration files where configuration
 * in a higher layer overrides those of the lower layers. Starting from the lowest layer,
 * configuration consists of:
 * <ul>
 *   <li><var>/global-defaults.xml</var> - A resource in this library
 *   <li><var>/defaults.xml</var> - A resource or resources in libraries and webapps using this lib
 *   <li><var>ambra.configuration.overrides</var> - If set, this defines a named resource or URL
 *        of a resource that is added to the configuration tree - usually supplementing
 *        and overriding settings in <var>/global-defaults.xml</var> and <var>/defaults.xml</var>.
 *   <li><var>file:/etc/ambra/ambra.xml</var> (or <var>ambra.configuration</var>) - A set of user
 *        overrides in <var>/etc</var>. The name of this file can be changed for webapps that use
 *        WebAppInitializer by changing web.xml or by setting the ambra.configuraiton system
 *        property.
 *   <li>System properties
 * </ul>
 *
 * @author Pradeep Krishnan
 * @author Eric Brown
 */
public class ConfigurationStore {
  private static final Log                log       = LogFactory.getLog(ConfigurationStore.class);
  private static final ConfigurationStore instance  = new ConfigurationStore();
  private CombinedConfiguration           root = null;

  /**
   * A property used to define the location of the master set of configuration overrides.
   * This is usually a xml or properties file in /etc somewhere. Note that this must be
   * a URL. (For example: file:///etc/ambra/ambra.xml.)
   */
  public static final String CONFIG_URL = "ambra.configuration";

  /**
   * A property used to define overrides. This is primarily to support something like
   * a development mode. If a valid URL, the resource is found from the URL. If not a
   * URL, it is treated as the name of a resource.
   */
  public static final String OVERRIDES_URL = "ambra.configuration.overrides";

  /**
   * Default configuration overrides in /etc
   */
  public static final String DEFAULT_CONFIG_URL = "file:///etc/ambra/ambra.xml";

  /**
   * <p>Name of resource(s) that contain defaults in a given journal</p>
   *
   * <p>There is one per journal.</p>
   */
  public static final String JOURNAL_DIRECTORY = "/configuration/journal.xml";
  public static final String DEFAULTS_RESOURCE = "ambra/configuration/defaults.xml";

  /**
   * The name of the global defaults that exist in this library.<p>
   *
   * It is assumed there is only one of these in the classpath. If somebody defines
   * a second copy of this, the results are undefined. (TODO: Detect this.)
   */
  public static final String GLOBAL_DEFAULTS_RESOURCE = "/ambra/configuration/global-defaults.xml";

  /**
   * The system variable used by Hibernates ID generator to use a prefix for unique identifiers
   * This value is pulled from the ambra.xml 'config.ambra.aliases.id' node.
   */
  public static final String SYSTEM_OBJECT_ID_PREFIX = "SYSTEM_OBJECT_ID_PREFIX";

  /**
   * Advanced usage logging
   */
  public static final String ADVANCED_USAGE_LOGGING = "ambra.advancedUsageLogging";

  /**
   * Location of journal templates
   */
  public static final String JOURNAL_TEMPLATE_DIR = "ambra.virtualJournals.templateDir";
  private static final String JOURNALS = "ambra.virtualJournals.journals";

  /**
   * Create the singleton instance.
   */
  private ConfigurationStore() {
  }

  /**
   * Gets the singleton instance.
   *
   * @return Returns the only instance.
   */
  public static ConfigurationStore getInstance() {
    return instance;
  }

  /**
   * Gets the current configuration root.
   *
   * @return Returns the currently loaded configuration root
   *
   * @throws RuntimeException if the configuration factory is not initialized
   */
  public Configuration getConfiguration() {
    if (root != null)
      return root;

    throw new RuntimeException("ERROR: Configuration not loaded or initialized.");
  }

  /**
   * Overrides all existing configuration with the given conifguration object
   * (useful for JUnit testing!)
   * @param newConfig the new configuration to test
   */
  public void setConfiguration(CombinedConfiguration newConfig) {
    root = newConfig;
  }

  /**
   * Load/Reload the configuration from the factory config url.
   *
   * @param configURL URL to the config file for ConfigurationFactory
   * @throws ConfigurationException when the config factory configuration has an error
   */
  public void loadConfiguration(URL configURL) throws ConfigurationException {
    root = new CombinedConfiguration(new OverrideCombiner());

    // System properties override everything
    root.addConfiguration(new SystemConfiguration());

    // Load from ambra.configuration -- /etc/... (optional)
    if (configURL != null) {
      try {
        root.addConfiguration(getConfigurationFromUrl(configURL));
        log.info("Added URL '" + configURL + "'");
      } catch (ConfigurationException ce) {
        if (!(ce.getCause() instanceof FileNotFoundException))
          throw ce;
        log.info("Unable to open '" + configURL + "'");
      }
    }

    // Add ambra.configuration.overrides (if defined)
    String overrides = System.getProperty(OVERRIDES_URL);
    if (overrides != null) {
      try {
        root.addConfiguration(getConfigurationFromUrl(new URL(overrides)));
        log.info("Added override URL '" + overrides + "'");
      } catch (MalformedURLException mue) {
        // Must not be a URL, so it must be a resource
        addResources(root, overrides);
      }
    }

    CombinedConfiguration defaults = new CombinedConfiguration(new UnionCombiner());
    // Add defaults.xml from classpath
    addResources(defaults, DEFAULTS_RESOURCE);
    // Add journal.xml from journals/journal-name/configuration/journal.xml
    addJournalResources(root, defaults, JOURNAL_DIRECTORY);
    root.addConfiguration(defaults);

    // Add global-defaults.xml (presumably found in this jar)
    addResources(root, GLOBAL_DEFAULTS_RESOURCE);

    if (log.isDebugEnabled())
      log.debug("Configuration dump: " + System.getProperty("line.separator") +
                ConfigurationUtils.toString(root));

    /**
     * This prefix is needed by the AmbraIdGenerator to create prefixes for object IDs.
     * Because of the way the AmbraIdGenerator class is created by hibernate, passing in values
     * is very difficult.  If a better method is discovered... by all means use that.  Until that time
     * I've created a system level property to store this prefix.
     */
    String objectIDPrefix = root.getString("ambra.platform.guid-prefix");

    if(objectIDPrefix == null) {
      throw new RuntimeException("ambra.platform.guid-prefix node is not found in the defined configuration file.");
    }

    System.setProperty(SYSTEM_OBJECT_ID_PREFIX, objectIDPrefix);
  }

  /**
   * Use the default commons configuration specified by this library.
   *
   * @throws ConfigurationException when the configuration can't be found.
   */
  public void loadDefaultConfiguration() throws ConfigurationException {
    // Allow JVM level property to override everything else
    String name = System.getProperty(CONFIG_URL);
    if (name == null)
      name = DEFAULT_CONFIG_URL;

    try {
      loadConfiguration(new URL(name));
    } catch (MalformedURLException e) {
      throw new ConfigurationException("Invalid value of '" + name + "' for '" + CONFIG_URL +
                                       "'. Must be a valid URL.");
    }
  }

  /**
   * Unload the current configuration.
   */
  public void unloadConfiguration() {
    root = null;
  }

  /**
   * Given a URL, determine whether it represents properties or xml and load it as a
   * commons-config Configuration instance.
   */
  private static AbstractConfiguration getConfigurationFromUrl(URL url)
      throws ConfigurationException {
    if (url.getFile().endsWith("properties"))
      return new PropertiesConfiguration(url);
    else
      return new XMLConfiguration(url);
  }

  /**
   * Iterate over all the resources of the given name and add them to our root
   * configuration.
   * @param root the root configuration to add to
   * @param resource the resource to add
   * @throws ConfigurationException on an error in adding the new config
   */
  public static void addResources(CombinedConfiguration root, String resource)
      throws ConfigurationException {
    Class<?> klass = ConfigurationStore.class;
    if (resource.startsWith("/")) {
      root.addConfiguration(getConfigurationFromUrl(klass.getResource(resource)));
      log.info("Added resource '" + resource + "' to configuration");
    } else {
      try {
        Enumeration<URL> rs = klass.getClassLoader().getResources(resource);
        while (rs.hasMoreElements()) {
          URL resourceUrl = rs.nextElement();
          root.addConfiguration(getConfigurationFromUrl(resourceUrl));
          log.info("Added resource '" + resourceUrl + "' from path '" + resource + "' to configuration");
        }
      } catch (IOException ioe) {
        throw new Error("Unexpected error loading resources", ioe);
      }
    }
  }

  public static void addJournalResources(CombinedConfiguration root, CombinedConfiguration defaults, String path)
      throws ConfigurationException {

     Collection<String> journals = root.getList(JOURNALS);
     String journalTemplatePath = root.getString(ConfigurationStore.JOURNAL_TEMPLATE_DIR, "/");

    for (String journal : journals) {

      String resourcePath = journalTemplatePath
          + (journalTemplatePath.endsWith("/") ? "journals/" : "/journals/")
          + journal
          + path;

      File defaultsXmlFile = new File(resourcePath);
      if (defaultsXmlFile.isFile() && defaultsXmlFile.canRead()) {
        defaults.addConfiguration(new XMLConfiguration(defaultsXmlFile));
        log.info("Added resource '" + resourcePath + "' to configuration");
      }
    }
  }
}
