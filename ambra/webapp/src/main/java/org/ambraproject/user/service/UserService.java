/* $HeadURL::                                                                            $
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
package org.ambraproject.user.service;

import org.ambraproject.ApplicationException;
import org.topazproject.ambra.models.UserAccount;
import org.ambraproject.user.AmbraUser;

/**
 * Class to roll up web services that a user needs in Ambra. Rest of application should generally
 * use AmbraUser to
 *
 * @author Stephen Cheng
 *
 */
public interface UserService {

  /**
   * Create a new user account and associate a single authentication id with it.
   *
   * @param authId the user's authentication id from CAS
   * @return the user's internal id
   * @throws ApplicationException if the <var>authId</var> is a duplicate
   */
  public String createUser(final String authId) throws ApplicationException;

  /**
   * Deletes the given user from the database. Visible for testing only.
   *
   * @param userId
   *          the Topaz User ID
   * @throws ApplicationException ApplicationException
   */
  public void deleteUser(final String userId, final String authId) throws ApplicationException;

  /**
   * Returns the username for a user given a UserId.
   *
   * @param userId topazUserId
   * @return username username
   * @throws ApplicationException ApplicationException
   */
  public String getUsernameById(final String userId) throws ApplicationException;

  /**
   * Gets the user specified by the userID passed in
   *
   * @param userId Topaz User ID
   * @return user associated with the topazUserId
   * @throws ApplicationException on access-check failure
   */
  public AmbraUser getUserById(final String userId) throws ApplicationException;

  /**
   * Get the AmbraUser with only the profile loaded.
   *
   * @param topazUserId topazUserId
   * @return AmbraUser
   * @throws ApplicationException on access-check failure
   */
  public AmbraUser getUserWithProfileLoaded(final String topazUserId)
      throws ApplicationException;
  /**
   * Gets the user specified by the authentication ID (CAS ID currently)
   *
   * @param authId authentication ID
   * @return the user associated with the authID
   * @throws ApplicationException on access-check failure
   */
  public AmbraUser getUserByAuthId(final String authId) throws ApplicationException;

  /**
   * Gets the user specified by the authentication ID (CAS ID currently)
   *
   * @param authId authentication ID
   * @return the user associated with the authID
   * @throws ApplicationException on access-check failure
   */
  public UserAccount getUserAccountByAuthId(String authId) throws ApplicationException;

  /**
   * Sets the state of the user account
   *
   * @param userId Topaz User ID
   * @param state  new state of the user
   * @throws ApplicationException ApplicationException
   */
  public void setState(final String userId, final String authId, int state) throws ApplicationException;

  /**
   * Gets the current state of the user
   *
   * @param userId Topaz userID
   * @return current state of the user
   * @throws ApplicationException ApplicationException
   */
  public int getState(final String userId) throws ApplicationException;

  /**
   * Retrieves first authentication ID for a given Topaz userID.
   *
   * @param userId Topaz userID
   * @return first authentiation ID associated with the Topaz userID
   * @throws ApplicationException ApplicationException
   */
  public String getAuthenticationId(final String userId) throws ApplicationException;

  /**
   * Returns the Topaz userID the authentiation ID passed in is associated with
   *
   * @param authId authentication ID you are looking up
   * @return Topaz userID for a given authentication ID
   * @throws ApplicationException on access-check failure
   */
  public String lookUpUserByAuthId(final String authId) throws ApplicationException;

  /**
   * Returns the Topaz userID the account ID passed in is associated with
   *
   * @param accountId account ID you are looking up
   * @return Topaz userID for a given account ID
   * @throws ApplicationException on access-check failure
   */
  public String lookUpUserByAccountId(final String accountId) throws ApplicationException;

  /**
   * Lookup the topaz id of the user with a given email address
   * @param emailAddress emailAddress
   * @return topaz id of the user
   * @throws ApplicationException on access-check failure
   */
  public String lookUpUserByEmailAddress(final String emailAddress) throws ApplicationException;

  /**
   * Lookup the topaz id of the user with a given name
   * @param name user display name
   * @return topaz id of the user
   * @throws ApplicationException on access-check failure
   */
  public String lookUpUserByDisplayName(final String name) throws ApplicationException;

  /**
   * Takes in an Ambra user and write the profile to the store
   *
   * @param inUser write profile of this user to the store
   * @throws ApplicationException ApplicationException
   * @throws DisplayNameAlreadyExistsException DisplayNameAlreadyExistsException
   */
  public void setProfile(final AmbraUser inUser)
      throws ApplicationException, DisplayNameAlreadyExistsException;

  /**
   * Write the specified user profile and associates it with the specified user ID
   *
   * @param inUser  topaz user object with the profile
   * @param userNameIsRequired whether a username in the profile is required
   * @throws ApplicationException ApplicationException
   * @throws DisplayNameAlreadyExistsException DisplayNameAlreadyExistsException
   */
  public void setProfile(final AmbraUser inUser, final boolean userNameIsRequired)
      throws ApplicationException, DisplayNameAlreadyExistsException;

  /**
   * Writes the preferences for the given user to the store
   *
   * @param inUser
   *          User whose preferences should be written
   * @throws ApplicationException ApplicationException
   */
  public void setPreferences(final AmbraUser inUser) throws ApplicationException;

  /**
   * @see org.ambraproject.user.service.UserService#setRole(String, String[], String)
   */
  public void setRole(final String topazId, final String roleId, final String authId) throws ApplicationException;

  /**
   * Set the roles for the user.
   *
   * @param topazId the user's id
   * @param roleIds the new roles
   * @throws ApplicationException if the user doesn't exist
   */
  public void setRole(final String topazId, final String[] roleIds, final String authId) throws ApplicationException;

  /**
   * Get the roles for the user.
   * @param topazId topazId
   * @return roles
   * @throws ApplicationException
   */
  public String[] getRole(final String topazId) throws ApplicationException;

  /**
   * Checks the action guard.
   * @return boolean
   */
  public boolean allowAdminAction(final String authId);

  public String getEmailAddressUrl();
}
