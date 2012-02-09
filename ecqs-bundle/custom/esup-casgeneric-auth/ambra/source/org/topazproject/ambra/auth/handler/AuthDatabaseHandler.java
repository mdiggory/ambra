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
package org.topazproject.ambra.auth.handler;

import org.dom4j.Element;

import org.esupportail.cas.server.util.BasicHandler;

import org.topazproject.ambra.auth.db.DatabaseContext;
import org.topazproject.ambra.auth.db.DatabaseException;

import org.topazproject.ambra.service.password.PasswordDigestService;

/**
 * Ambra user authentication handler that verifies that the user with a given userid/adminPassword
 * exists in the database
 * 
 * To use this authentication handler, edit the file "genericHandler.xml" located at:
 * custom/esup-casgeneric-auth/ambra/webpages/WEB-INF/genericHandler.xml with 
 *
 * <pre>
 *  <authentication debug="on">
 *    <handler>
 *      <classname>org.topazproject.ambra.auth.handler.AuthDatabaseHandler</classname>
 *      <config>
 *        <table>${esup-casgeneric.auth.ambra.table}</table>
 *        <login_column>${esup-casgeneric.auth.ambra.login-column}</login_column>
 *        <password_column>${esup-casgeneric.auth.ambra.password-column}</password_column>
 *        <encryption>${esup-casgeneric.auth.ambra.encryption}</encryption>
 *        <encoding_charset>${esup-casgeneric.auth.ambra.encoding-charset}</encoding_charset>
 *      </config>
 *    </handler>
 *  </authentication>
 * </pre>
 */
public class AuthDatabaseHandler extends BasicHandler {
  private PasswordDigestService passwordService;
  private String passwordSqlQuery;
  private String verifiedUserSqlQuery;

  /**
   * Analyse the XML configuration to set netId and adminPassword attributes (constructor).
   * 
   * @param handlerElement the XML element that declares the handler in the configuration file
   * @param configDebug debugging mode of the global configuration (set by default to the handler)
   *
   * @throws Exception when the handler not configured correctly
   */
  public AuthDatabaseHandler(final Element handlerElement, final Boolean configDebug)
    throws Exception  {
    super(handlerElement, configDebug);
    traceBegin();
    trace("AuthDatabaseHandler constructor called");

    // check that a config element is present
    checkConfigElement(true);

    init();
    traceEnd();
  }

  private void init() throws Exception {
    passwordService = getPasswordService();
    passwordSqlQuery = getPasswordSqlQuery();
    verifiedUserSqlQuery = getVerifiedUserSqlQuery();
  }

  private PasswordDigestService getPasswordService() throws Exception {
    final String encryption = getConfigSubElementContent("encryption");
    final String encodingCharset = getConfigSubElementContent("encoding_charset");
    PasswordDigestService passwordDigestService = new PasswordDigestService();
    passwordDigestService.setAlgorithm(encryption);
    passwordDigestService.setEncodingCharset(encodingCharset);
    return passwordDigestService;
  }

  /**
   * Try to authenticate a user (compare with the db password).
   *
   * @param userLogin the user's adminUser
   * @param userPassword the user's adminPassword
   *
   * @return BasicHandler.SUCCEDED on success,
   * BasicHandler.FAILED_CONTINUE or BasicHandler.FAILED_STOP otherwise.
   */
  public int authenticate(final String userLogin, final String userPassword) {
    traceBegin();

    trace("Checking user's password...");

    try {
      final DatabaseContext databaseContext = getDatabaseContext();
      final String verificationStatus
              = databaseContext.getSingleStringValueFromDb(verifiedUserSqlQuery, userLogin);
      if (!isTrue(verificationStatus)) {
        trace("User: " + userLogin + " is not yet verified or is inactive");
      } else {
        final String savedDigestPassword =
          databaseContext.getSingleStringValueFromDb(passwordSqlQuery, userLogin);

        final boolean verified = passwordService.verifyPassword(userPassword, savedDigestPassword);

        if (verified) {
          trace("User's password matches.");
          return SUCCEEDED;
        }
        trace("User: " + userLogin + "'s password does not match");
      }
      return FAILED_STOP;
    } catch (final Exception ex) {
      trace("User's password does not match " + ex.getMessage());
      return FAILED_STOP;
    } finally {
      traceEnd();
    }
  }

  private boolean isTrue(final String verificationStatus) {
    final String booleanStr = verificationStatus.toLowerCase();
    return (Boolean.valueOf(booleanStr) || booleanStr.startsWith("t")) ? true : false;
  }

  private DatabaseContext getDatabaseContext() throws DatabaseException {
    return DatabaseContext.getInstance();
  }

  /**
   * Read the SQL query from the configuration (deduces it from other parameters).
   * @return a String.
   * @throws Exception Exception
   */
  private String getPasswordSqlQuery() throws Exception {
    traceBegin();

    final String table = getConfigSubElementContent("table");
    final String loginColumn = getConfigSubElementContent("login_column");
    final String passwordColumn = getConfigSubElementContent("password_column");

    final String query = "SELECT " + passwordColumn + " FROM " + table + " WHERE " +
                         loginColumn + " = ?";

    traceEnd(query);
    return query;
  }

  /**
   * Read the verify SQL query from the configuration (deduces it from other parameters).
   * @return a String.
   * @throws Exception Exception
   */
  private String getVerifiedUserSqlQuery() throws Exception {
    traceBegin();

    final String table = getConfigSubElementContent("table");
    final String loginColumn = getConfigSubElementContent("login_column");
    final String verifyColumn = getConfigSubElementContent("verify_column");
    final String isActiveColumn = getConfigSubElementContent("active_column");

    final StringBuilder query = new StringBuilder("SELECT ");
    query.append(verifyColumn);
    query.append(" FROM ").append(table);
    query.append(" WHERE ").append(isActiveColumn).append (" = true AND ").
          append(loginColumn).append(" = ?");

    traceEnd(query.toString());
    return query.toString();
  }

  private String getConfigSubElementContent(final String elementName) throws Exception {
    final String value = getConfigSubElementContent(elementName, true);
    trace(elementName + " = " + value);
    return value;
  }
}
