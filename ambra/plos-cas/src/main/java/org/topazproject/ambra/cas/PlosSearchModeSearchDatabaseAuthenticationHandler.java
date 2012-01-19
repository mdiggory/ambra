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

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;


/**
 * This class authenticates users based on a submitted username/password pair.
 * <p/>
 * The password verification logic in this class parallels the password creation process
 *   in the ambra-registration application.
 */
public class PlosSearchModeSearchDatabaseAuthenticationHandler
    extends AbstractJdbcUsernamePasswordAuthenticationHandler
    implements InitializingBean {
  private static final Logger log = LoggerFactory.getLogger(PlosSearchModeSearchDatabaseAuthenticationHandler.class);

  private String fieldUser;
  private String fieldPassword;
  private String tableUsers;

  // In the ambra-registration application, for a NEW password, a "salt" of random hexadecimal
  //   characters is created and prepended to the (encrypted) password.
  // When attempting verification of a password, this "salt" is re-acquired by doing a "substring"
  //   on the password in the database.
  private final int saltStringLength = 6;

  /**
   * Determine if the credentials supplied are valid.  The actual work is handled by the
   *   private method <em>authenticateUser(UsernamePasswordCredentials credentials)</em>
   * <p/>
   * Replicates logic from class org.topazproject.ambra.auth.handler.AuthDatabaseHandler
   *     (from the "ecqs-bundle" (aka: "ecqs") project)
   *   and org.topazproject.ambra.service.password.PasswordDigestService
   *     (from the "registration" (aka: "ambra-registration" project)
   *   and org.topazproject.ambra.auth.db.DatabaseContext (from the "cas-mod"
   *     (aka: "ambra-cas-mod") project)
   *
   * @param credentials Contains the username and password submitted by the user
   * @return true if the submitted password matches the stored password for this username,
   *   otherwise false
   * @throws AuthenticationException Is rarely thrown.  This method handles most of its own
   *   Exceptions and so throws this Exception only when something catastrophic happens
   */
  public final boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
    // Store the user's email address for redisplay (to the user, on the login form) if login fails.
    // Necessary because credentials.username is set to the user's GUID during the login process.
    String emailAddress = credentials.getUsername();

    //  This method does the heavy lifting of actually authenticating the user.
    final boolean isUserAuthenticated = authenticateUser(credentials);

    // If the user FAILED to log in, then show the user their email address, NOT their GUID.
    if ( ! isUserAuthenticated) {
      if (emailAddress != null) {
        credentials.setUsername(emailAddress);
      } else {
        credentials.setUsername("");
      }
    }

    return isUserAuthenticated;
  }

  /**
   * Determine if the credentials supplied are valid.
   *
   * @param credentials Contains the username and password submitted by the user
   * @return true if the submitted password matches the stored password for this username,
   *   otherwise false
   * @throws AuthenticationException Is rarely thrown.  This method handles most of its own
   *   Exceptions and so throws this Exception only when something catastrophic happens
   */
  private final boolean authenticateUser(UsernamePasswordCredentials credentials) throws AuthenticationException {
    // If the username looks like an email address, then look up the user's
    //   actual ID (a.k.a., "guid") from the database.
    // Set the "username" property (in the "credentials" object) to the value of this ID so that
    //   (if this login attempt is successful) the auth certificate will be issued with a "name"
    //   which is the value of the ID, not a "name" which is the email address.
    // Issuing a certificate with a "name" which is the email address will allow the user to log in,
    //   but will break Ambra's User Profile pages.
    if (credentials.getUsername() != null && credentials.getUsername().indexOf('@') > 0) {

      String queryForGuid = "SELECT id "
        + " FROM " + this.tableUsers
        + " WHERE LOWER(loginname) = LOWER(?) ;";

      if (log.isDebugEnabled()) {
       log.debug("Query to get GUID (id) from user-submitted email address (loginname): " + queryForGuid);
      }

      try {
        Map<String, Object> guidMap = getJdbcTemplate().queryForMap(queryForGuid, credentials.getUsername());

        Iterator<String> mapKeyIterator = guidMap.keySet().iterator();
        while (mapKeyIterator.hasNext()) {
          String key = mapKeyIterator.next();
          String value = (String)(guidMap.get(key));
          credentials.setUsername(value);
          if (log.isDebugEnabled()) {
           log.debug("Just set credentials.getUsername() to: " + credentials.getUsername());
          }
        }
      } catch (Exception e) {
        log.error("Database query failed, so return a value of false.  User-submitted email address = "
            + credentials.getUsername(), e);
      }
    }

    // First, make sure the Username is Active AND has been Verified.
    boolean isUserVerifiedAndActive = isUserVerifiedAndActive(credentials.getUsername());

    if (log.isDebugEnabled()) {
      if (isUserVerifiedAndActive) {
        log.debug("isUserVerifiedAndActive is TRUE, so processing will continue");
      } else {
        log.debug("isUserVerifiedAndActive is FALSE, so this method will now return.");
      }
    }

    if ( ! isUserVerifiedAndActive) {
      return false;
    }

    String queryForPassword = "SELECT " + this.fieldPassword
      + " FROM " + this.tableUsers
      + " WHERE "
        + " LOWER(" + this.fieldUser + ") = LOWER(?) ;";

    if (log.isDebugEnabled()) {
     log.debug("Query to get the password from the user ID: " + queryForPassword);
    }

    String passwordFromDatabase = null;
    try {
      Map<String, Object> passwordMap = getJdbcTemplate().queryForMap(queryForPassword, credentials.getUsername());

      Iterator<String> mapKeyIterator = passwordMap.keySet().iterator();
      while (mapKeyIterator.hasNext()) {
        String key = mapKeyIterator.next();
        String value = (String)(passwordMap.get(key));
        passwordFromDatabase = value;
      }
    } catch (Exception e) {
      log.error("Database query failed, so return a value of false.", e);
      return false;
    }

    boolean isSubmittedPasswordVerified = false;
    try {
      isSubmittedPasswordVerified = verifyPassword(credentials.getPassword(), passwordFromDatabase);
    } catch (Exception e) {
      log.error("Unable to even test whether the submitted password matches the stored password." +
          "  Now throwing AuthenticationException.");
      throw (AuthenticationException)e;
    }

    if (log.isDebugEnabled()) {
      if (isSubmittedPasswordVerified) {
        log.debug("isSubmittedPasswordVerified is TRUE.");
      } else {
        log.debug("isSubmittedPasswordVerified is FALSE.");
      }
    }

    return isSubmittedPasswordVerified;
  }

  /**
   * Verify that the user password (from the database)
   *   matches the digested password (submitted by the user as part of the authentication process)
   * 
   * @param passwordToVerify Submitted by the user during an attempt to authenticate
   * @param passwordFromDatabase Salted and digested password previously set by the user
   * @return true if the two passwords match (after salting and encoding), false otherwise
   * @throws Exception Thrown when there are problems with the password encoder
   */
  public boolean verifyPassword(final String passwordToVerify, final String passwordFromDatabase) throws Exception {
    final String newPasswordDigested = getDigestedPassword(passwordToVerify, getSalt(passwordFromDatabase));
    return passwordFromDatabase.equals(newPasswordDigested);
  }

  /**
   * The "salt" is a short String (randomly generated for each password and stored with that
   *   password in the database) which is used to obfuscate each password
   *
   * @param passwordFromDatabase The password that has been queried from the database
   * @return The "salt" which was extracted from <strong>passwordFromDatabase</strong>
   */
  private String getSalt(final String passwordFromDatabase) {
    return passwordFromDatabase.substring(0, saltStringLength);
  }

  /**
   * Return a salted digest of the user-submitted password.
   * The ambra-registration application "salts", then "digests" (i.e., "hashes") the password before
   *   encrypting and storing it; this method replicates that salt-and-digest process.
   *
   * @param password Submitted by the user during an attempt to authenticate
   * @param salt A short String (randomly generated for each password and stored with that password
   *   in the database) which is used to obfuscate each password
   * @return The password after it has been appropriately salted and digested.  At this point, the
   *   user-submitted password is ready to be compared to a password queried from the database
   * @throws Exception Thrown by the password-encoding process
   */
  private String getDigestedPassword(final String password, final String salt) throws Exception {
      return salt + getPasswordEncoder().encode(salt + password);
  }

  /**
   * Make sure the <em>username</em> exists AND has been "verified" AND is currently "active"
   *   by querying the database that is maintained by the ambra-registration application.
   *
   * @param username Submitted by the user are part of an authentication attempt
   * @return true if the "active" and "verified" columns in the database both have values of "true"
   *   for this username
   */
  private boolean isUserVerifiedAndActive (String username) {
    String queryForVerification = "SELECT COUNT('x') from "
      + this.tableUsers
      + " WHERE "
        + " LOWER(" + this.fieldUser + ") = LOWER(?) "
      + " AND verified = true "
      + " AND active = true ;";

    if (log.isDebugEnabled()) {
      log.debug("Query to check if a user is both verified and active: " + queryForVerification);
    }

    try {
      final int count = getJdbcTemplate().queryForInt(queryForVerification, username);
      return count > 0;
    } catch (Exception e) { 
      log.error("Failed when trying getJdbcTemplate().queryForInt(...) so returning false.", e);
      return false;
    }
  }

  /**
   * If you want things executed or values set IMMEDIATELY AFTER the bean is created, then do so here.
   * This method is NEVER called during the user validation process.
   * <p/>
   * This (empty) method has been declared here to satisfy the requirements of the
   *   org.springframework.beans.factory.InitializingBean interface.
   */
  public void afterPropertiesSet() throws Exception {
  }

  public final void setFieldPassword(final String fieldPassword) {
    this.fieldPassword = fieldPassword;
  }

  public final void setFieldUser(final String fieldUser) {
    this.fieldUser = fieldUser;
  }

  public final void setTableUsers(final String tableUsers) {
    this.tableUsers = tableUsers;
  }

}
