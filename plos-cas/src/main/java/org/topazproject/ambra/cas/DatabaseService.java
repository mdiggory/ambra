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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.ambra.cas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Provides connectivity and query methods for getting values out of the
 *   <em>plos_user</em> table in the <em>casdb</em> database.
 * <p/>
 * At the time of its creation, this class was only used by GetGuidReturnEmailFilter.
 * It was broken out as its own class to maintain the separation of Service from Display layers.
 */
public class DatabaseService {
  private static final Logger log = LoggerFactory.getLogger(DatabaseService.class);

  private static final String queryLoginnameFromId = "SELECT loginname FROM plos_user WHERE LOWER(id) = LOWER(?)";
  private ApplicationContext applicationContext;

  /**
   * Does nothing but set the Application Context parameter
   *
   * @param applicationContext Used to acquire the DataSource which
   *   provides connectivity to the database
   */
  public DatabaseService(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Given an ID, this method will return the username (assumed to be the email address)
   *   for that user from the database
   * 
   * @param guid The ID for a user
   * @return The <em>username</em> for the user with the ID which matches the <em>guid</em> parameter
   * @throws SQLException for any problem encountered when talking to the database
   */
  public String getEmailAddressFromGuid (final String guid) throws SQLException {
    String returnValue = null;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      DataSource dataSource = (DataSource)applicationContext.getBean("casDataSourceBean");

      connection = dataSource.getConnection();
      preparedStatement = connection.prepareStatement(queryLoginnameFromId);
      preparedStatement.setString(1, guid);

      resultSet = preparedStatement.executeQuery();
      resultSet.next();
      returnValue = resultSet.getString(1);
    } catch (Exception e) {
      log.error("Unable to query Email Address for GUID = " + guid, e);
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (connection != null) {
        connection.close();
      }
    }

    if (returnValue != null) {
      return returnValue;
    } else {
      return "Unable to lookup email address from the CAS server. Please contact the system administrator.";
    }
  }
}
