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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A listener class for web-apps to load a configuration at startup.
 *
 * @author Pradeep Krishnan
 * @author Eric Brown
 */
public class WebAppListener implements ServletContextListener {
  private static Log log = LogFactory.getLog(WebAppListener.class);

  /**
   * Destroy the configuration singleton since this web application is getting un-deployed.
   *
   * @param event the destroyed event
   */
  public void contextDestroyed(ServletContextEvent event) {
    ConfigurationStore.getInstance().unloadConfiguration();
  }

  /**
   * Initialize the configuration singleton since this web application is getting deployed.<p>
   *
   * By default, WebAppListener uses the default ConfigurationStore initialization. This
   * usually means using /etc/.../ambra.xml. This can be overridden by setting the
   * org.topazproject.ambra.configuration system property or webapp context variable to a URL or a name
   * resolvable as a resource.
   *
   * @param event The servlet event associated with initializing this context
   * @throws Error on non-recoverable config load error
   */
  public void contextInitialized(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    FactoryConfig  config = getFactoryConfig(context);

    try {
      URL url;

      // Locate the config url.
      if (config.name.startsWith("/WEB-INF")) {
        url = context.getResource(config.name);

        if (url == null)
          throw new MalformedURLException("'" + config.name + "' not found in the web-app context");
      } else {
        try {
          // First see if it is a valid URL
          url = new URL(config.name);
        } catch (MalformedURLException e) {
          // Otherwise, load as a resource
          url = WebAppListener.class.getResource(config.name);
          if (url == null)
            throw e;
        }
      }

      // Now load the config
      log.info("Loading '" + url + "' (" + config.name + ") configured via " + config.source);
      ConfigurationStore.getInstance().loadConfiguration(url);

      // Setup an application scope attribute that something like freemarker or struts might use
      context.setAttribute("config", ConfigurationStore.getInstance().getConfiguration());
    } catch (MalformedURLException e) {
      log.fatal(config.name + " defined by " + config.source + " is not a valid URL or resource", e);
      throw new Error("Failed to load configuration", e);
    } catch (ConfigurationException e) {
      log.fatal("Failed to initialize configuration factory.", e);
      throw new Error("Failed to load configuration", e);
    }
  }

  private FactoryConfig getFactoryConfig(ServletContext context) {
    // Allow JVM level property to override everything else
    String name = System.getProperty(ConfigurationStore.CONFIG_URL);

    if (name != null)
      return new FactoryConfig(name, "JVM System property " + ConfigurationStore.CONFIG_URL);

    // Now look for a config specified in web.xml
    name = context.getInitParameter(ConfigurationStore.CONFIG_URL);

    if (name != null)
      return new FactoryConfig(name, "Web-app context initialization parameter " +
                                     ConfigurationStore.CONFIG_URL);

    // Return a default
    return new FactoryConfig(ConfigurationStore.DEFAULT_CONFIG_URL, "default");
  }

  private static class FactoryConfig {
    String name;
    String source;

    FactoryConfig(String name, String source) {
      this.name     = name;
      this.source   = source;
    }
  }
}
