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
package org.topazproject.ambra.auth.web;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.topazproject.ambra.configuration.ConfigurationStore;
import org.topazproject.ambra.auth.AuthConstants;
import org.topazproject.ambra.auth.db.DatabaseContext;
import org.topazproject.ambra.auth.db.DatabaseException;
import org.topazproject.ambra.auth.service.UserService;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * Initialize the DatabaseContext and UserService for cas.<p>
 *
 * Be sure to add to CAS' web.xml as a servlet context listner. Uses commons-config
 * for configuration.
 *
 * @author Viru
 * @author Eric Brown
 */
public class AuthServletContextListener implements ServletContextListener {
  private static final Log log = LogFactory.getLog(AuthServletContextListener.class);

  private static final String PREF = "ambra.services.cas.db.";

  private DatabaseContext dbContext;

  public void contextInitialized(final ServletContextEvent event) {
    final ServletContext context = event.getServletContext();

    Configuration conf = ConfigurationStore.getInstance().getConfiguration();
    String url = conf.getString(PREF + "url");

    final Properties dbProperties = new Properties();
    dbProperties.setProperty("url", url);
    dbProperties.setProperty("user", conf.getString(PREF + "user"));
    dbProperties.setProperty("password", conf.getString(PREF + "password"));

    try {
      dbContext = DatabaseContext.createDatabaseContext(conf.getString(PREF + "driver"),
                                                        dbProperties, conf.getInt(PREF + "initialSize"),
                                                        conf.getInt(PREF + "maxActive"),
                                                        conf.getString(PREF + "connectionValidationQuery"));
    } catch (final DatabaseException ex) {
      throw new Error("Failed to initialize the database context to '" + url + "'", ex);
    }

    final UserService userService = new UserService(dbContext, conf.getString(PREF + "usernameToGuidSql"),
                                                    conf.getString(PREF + "guidToUsernameSql"));
    context.setAttribute(AuthConstants.USER_SERVICE, userService);
  }

  public void contextDestroyed(final ServletContextEvent event) {
    try {
      dbContext.close();
    } catch (final DatabaseException ex) {
      log.error("Failed to shutdown the database context", ex);
    }
  }
}
