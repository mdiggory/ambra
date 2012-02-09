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
package org.topazproject.ambra.auth.service;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.topazproject.ambra.auth.db.DatabaseContext;
import org.topazproject.ambra.auth.service.UserService;

import java.util.Properties;

public class UserServiceTest extends TestCase {
  private static final Log log = LogFactory.getLog(UserServiceTest.class);

  public void testUserIsFound() throws Exception {
    final Properties dbProperties = new Properties();
    dbProperties.setProperty("url", "jdbc:postgresql://localhost/postgres");
    dbProperties.setProperty("user", "postgres");
    dbProperties.setProperty("password", "postgres");

    final DatabaseContext context = DatabaseContext.createDatabaseContext("org.postgresql.Driver",
                                                                          dbProperties, 2, 10, "select 1");

    final UserService userService = new UserService(context, "select id from plos_user where loginname=?",
                                                    "select loginname from plos_user where id=?");
    final String testUsername = "viru";

    final String guid = userService.getGuid(testUsername);
    assertNotNull(guid);
    final String username = userService.getEmailAddress(guid);
    assertNotNull(username);
    assertEquals(testUsername, username);
    log.debug(context.getStatus());
  }
}
