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

import org.topazproject.ambra.auth.db.DatabaseContext;
import org.topazproject.ambra.auth.db.DatabaseException;

import java.sql.SQLException;

/**
 * Used to fetch the various properties, like guid, for a given user.
 */
public class UserService {
  private final DatabaseContext context;
  private final String usernameToGuidSql;
  private final String guidToUsernameSql;

  public UserService(final DatabaseContext context, final String usernameToGuidSql,
                     final String guidToUsernameSql) {
    this.usernameToGuidSql = usernameToGuidSql;
    this.guidToUsernameSql = guidToUsernameSql;
    this.context = context;
  }

  /**
   * Given a loginname it will return the guid for it from the database
   * @param loginname loginname
   * @return the guid for the loginname
   * @throws DatabaseException DatabaseException
   */
  public String getGuid(final String loginname) throws DatabaseException {
    try {
      return context.getSingleStringValueFromDb(usernameToGuidSql, loginname);
    } catch (SQLException e) {
      throw new DatabaseException("Unable to get loginame from db", e);
    }
  }

  /**
   * Given a guid it will return the username for it from the database
   * @param guid guid
   * @return the guid for the guid
   * @throws DatabaseException DatabaseException
   */
  public String getEmailAddress(final String guid) throws DatabaseException {
    try {
      return context.getSingleStringValueFromDb(guidToUsernameSql, guid);
    } catch (SQLException e) {
      throw new DatabaseException("Unable to get email address from db", e);
    }
  }
}
