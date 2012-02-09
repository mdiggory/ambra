/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.ApplicationException;
import org.topazproject.ambra.models.*;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.user.AmbraUser;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * Class to roll up web services that a user needs in Ambra. Rest of application should generally
 * use AmbraUser to
 *
 * @author Stephen Cheng
 * @author Joe Osowski
 *
 */
public class UserServiceImpl extends HibernateServiceImpl implements UserService {
  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  private static final String USER_LOCK = "UserCache-Lock-";
  private static final String USER_KEY = "UserCache-User-";

  private PermissionsService permissionsService;

  private String applicationId;
  private String emailAddressUrl;

  /**
   * Constructor
   */
  public UserServiceImpl() {
  }

  /**
   * Create a new user account and associate a single authentication id with it.
   *
   * @param authId the user's authentication id from CAS
   * @return the user's internal id
   * @throws ApplicationException if the <var>authId</var> is a duplicate
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String createUser(final String authId) throws ApplicationException {
    // create account
    UserAccount ua = new UserAccount();
    ua.getAuthIds().add(new AuthenticationId(authId));

    hibernateTemplate.saveOrUpdate(ua);

    ua = (UserAccount)hibernateTemplate.load(UserAccount.class, ua.getId());

    return ua.getId().toString();
  }

  /**
   * Deletes the given user from Topaz. Visible for testing only.
   *
   * @param userId
   *          the Topaz User ID
   * @throws ApplicationException ApplicationException
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteUser(final String userId, String authId) throws ApplicationException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    try {
      UserAccount ua = (UserAccount)hibernateTemplate.load(UserAccount.class, new URI(userId));

      if (ua != null)
        hibernateTemplate.delete(ua);
    } catch (URISyntaxException ex) {
      throw new ApplicationException(ex.getMessage(), ex);
    }
  }

  /**
   * Get the account info for the given user.
   * @param topazUserId the Topaz User ID
   * @throws ApplicationException ApplicationException
   * @return UserAccount
   */
  private UserAccount getUserAccount(final String topazUserId) throws ApplicationException {
    UserAccount ua;

    try {
      ua = (UserAccount)hibernateTemplate.load(UserAccount.class, new URI(topazUserId));
    } catch(URISyntaxException ex) {
      throw new ApplicationException(ex.getMessage(),ex);
    }

    if (ua == null)
      throw new ApplicationException("No user-account with id '" + topazUserId + "' found");

    return ua;
  }

  /**
   * Returns the username for a user given a Topaz UserId.  Because usernames cannot be changed and
   * can always be viewed by anyone, we use a simple cache here.
   *
   * @param userId topazUserId
   * @return username username
   * @throws ApplicationException ApplicationException
   */
  @Transactional(readOnly = true)
  public String getUsernameById(final String userId) throws ApplicationException {
    try {
      return getDisplayName(userId);
    } catch (URISyntaxException ex) {
      throw new ApplicationException(ex.getMessage(), ex);
    }
  }

  private String getDisplayName(final String topazUserId) throws ApplicationException, URISyntaxException {
    UserAccount ua = (UserAccount)hibernateTemplate.load(UserAccount.class, new URI(topazUserId));

    if(ua == null) {
      throw new ApplicationException("No user-account with id '" + topazUserId + "' found");
    } else {
     return ua.getProfile().getDisplayName();
    }
  }

  /**
   * Gets the user specified by the Topaz userID passed in
   *
   * @param userId Topaz User ID
   * @return user associated with the topazUserId
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public AmbraUser getUserById(final String userId) throws ApplicationException {
    return getUserWithProfileLoaded(userId);
  }

  /**
   * Get the AmbraUser with only the profile loaded. For getting the preferences also use
   * getUserById
   *
   * @param topazUserId topazUserId
   * @return AmbraUser
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public AmbraUser getUserWithProfileLoaded(final String topazUserId)
      throws ApplicationException {

    UserAccount ua = getUserAccount(topazUserId);
    return new AmbraUser(ua, applicationId);
  }

  /**
   * Gets the us  er specified by the authentication ID (CAS ID currently)
   *
   * @param authId authentication ID
   * @return the user associated with the authID
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public AmbraUser getUserByAuthId(final String authId) throws ApplicationException {
    UserAccount ua = getUserAccountByAuthId(authId);
    if (ua == null)
      return null;

    return new AmbraUser(ua, applicationId);
  }

  /**
   * Gets the user specified by the authentication ID (CAS ID currently)
   *
   * @param authId authentication ID
   * @return the user associated with the authID
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public UserAccount getUserAccountByAuthId(final String authId) throws ApplicationException {
    return (UserAccount)hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Query q = session.createSQLQuery("select ua.userAccountUri from UserAccount ua " +
            "join UserAccountAuthIdJoinTable uaajt on uaajt.userAccountUri = ua.userAccountUri " +
            "join AuthenticationId aid on aid.authenticationIdUri = uaajt.authenticationIdUri " +
            "where aid.value = :id")
          .setString("id", authId);

        Iterator r = q.list().iterator();

        try {
          if(r.hasNext()) {
            URI accountUri = new URI((String)r.next());

            return session.get(UserAccount.class, accountUri);
          } else {
            return null;
          }
        } catch(URISyntaxException ex) {
          throw new HibernateException(ex.getMessage(), ex);
        }
      }
    });
  }

  /**
   * Sets the state of the user account
   *
   * @param userId Topaz User ID
   * @param state  new state of the user
   * @throws ApplicationException ApplicationException
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setState(final String userId, String authId, int state) throws ApplicationException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    UserAccount ua = getUserAccount(userId);
    ua.setState(state);
  }

  /**
   * Gets the current state of the user
   *
   * @param userId Topaz userID
   * @return current state of the user
   * @throws ApplicationException ApplicationException
   */
  @Transactional(readOnly = true)
  public int getState(final String userId) throws ApplicationException {
    UserAccount ua = getUserAccount(userId);
    return ua.getState();
  }

  /**
   * Retrieves first authentication ID for a given Topaz userID.
   *
   * @param userId Topaz userID
   * @return first authentiation ID associated with the Topaz userID
   * @throws ApplicationException ApplicationException
   */
  @Transactional(readOnly = true)
  public String getAuthenticationId(final String userId) throws ApplicationException {
    UserAccount ua = getUserAccount(userId);
    return ua.getAuthIds().iterator().next().getValue();
  }

  /**
   * Returns the Topaz userID the authentiation ID passed in is associated with
   *
   * @param authId authentication ID you are looking up
   * @return Topaz userID for a given authentication ID
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public String lookUpUserByAuthId(final String authId) throws ApplicationException {
    return (String)hibernateTemplate.execute(new HibernateCallback() {

      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Query q = session.
            createSQLQuery("select uaajt.userAccountUri from UserAccountAuthIdJoinTable uaajt " +
              "join AuthenticationId aid on aid.authenticationIdUri = uaajt.authenticationIdUri " +
              "where aid.value = :id")
            .setParameter("id", authId);

        Iterator r = q.list().iterator();

        if(r.hasNext()) {
          return r.next();
        } else {
          return null;
        }
      }
    });
  }

  /**
   * Returns the Topaz userID the account ID passed in is associated with
   *
   * @param accountId account ID you are looking up
   * @return Topaz userID for a given account ID
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public String lookUpUserByAccountId(final String accountId) throws ApplicationException {
    UserAccount account = (UserAccount)hibernateTemplate.get(UserAccount.class, URI.create(accountId));

    if (account != null)
      return account.getId().toString();
    else
      return null;
  }

  /**
   * Lookup the topaz id of the user with a given email address
   * @param emailAddress emailAddress
   * @return topaz id of the user
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public String lookUpUserByEmailAddress(final String emailAddress) throws ApplicationException {
    return lookUpUserByProfile("email", URI.create("mailto:" + emailAddress));
  }


  /**
   * Lookup the topaz id of the user with a given name
   * @param name user display name
   * @return topaz id of the user
   * @throws ApplicationException on access-check failure
   */
  @Transactional(readOnly = true)
  public String lookUpUserByDisplayName(final String name) throws ApplicationException {
    return lookUpUserByProfile("displayName", name);
  }

  private String lookUpUserByProfile(final String field, final Object value)
      throws ApplicationException {

    //Following a slightly different pattern here that works with unit tests that talk to the hypersonic
    //We might consider using this pattern for all DB queries.

    DetachedCriteria query = DetachedCriteria.forClass(UserAccount.class)
      .createCriteria("profile")
      .add(Restrictions.eq(field, value));

    List list = hibernateTemplate.findByCriteria(query);

    Iterator r = list.iterator();

    if (!r.hasNext())
      return null;

    return ((UserAccount)r.next()).getId().toString();
  }

  /**
   * Takes in an Ambra user and write the profile to the store
   *
   * @param inUser write profile of this user to the store
   * @throws ApplicationException ApplicationException
   * @throws org.ambraproject.user.service.DisplayNameAlreadyExistsException DisplayNameAlreadyExistsException
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setProfile(final AmbraUser inUser)
      throws ApplicationException, DisplayNameAlreadyExistsException {
    if (inUser != null) {
      setProfile(inUser, false);
    } else {
      throw new ApplicationException("User is null");
    }
  }

  /**
   * Takes in an Ambra user and write the profile to the store
   *
   * @param inUser write profile of this user to the store
   * @param privateFields fields marked private by the user
   * @param userNameIsRequired whether userNameIsRequired
   * @throws ApplicationException ApplicationException
   * @throws org.ambraproject.user.service.DisplayNameAlreadyExistsException DisplayNameAlreadyExistsException
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setProfile(final AmbraUser inUser, final String[] privateFields,
                         final boolean userNameIsRequired)
      throws ApplicationException, DisplayNameAlreadyExistsException {
    if (inUser != null) {
      setProfile(inUser, userNameIsRequired);
    } else {
      throw new ApplicationException("User is null");
    }
  }

  /**
   * Write the specified user profile and associates it with the specified user ID
   *
   * @param inUser  topaz user object with the profile
   * @param userNameIsRequired whether a username in the profile is required
   * @throws ApplicationException ApplicationException
   * @throws org.ambraproject.user.service.DisplayNameAlreadyExistsException DisplayNameAlreadyExistsException
   */
  public void setProfile(final AmbraUser inUser, final boolean userNameIsRequired)
      throws ApplicationException, DisplayNameAlreadyExistsException {

    if (userNameIsRequired) {
      final String userId = lookUpUserByProfile("displayName", inUser.getDisplayName());
      if ((null != userId) && !userId.equals(inUser.getUserId())) {
        throw new DisplayNameAlreadyExistsException();
      }
    }

    UserAccount ua = getUserAccount(inUser.getUserId());
    UserProfile old = null;

    try {
      old = ua.getProfile();
    } catch (ObjectNotFoundException ex) {}

    UserProfile nu = inUser.getUserProfile();
    // if we are swapping out a profile with another with the same id, evict the old
    if ((old != null) && (nu != null) && (old != nu) && old.getId().equals(nu.getId())) {
      if (log.isDebugEnabled())
        log.debug("Evicting old profile (" + old + ") with id " + old.getId());
      hibernateTemplate.evict(old);
    }
    if (nu != null) {
      ua.setProfile(nu);
      hibernateTemplate.saveOrUpdate(ua); // force it since 'nu' may have been evicted
      if (log.isDebugEnabled())
        log.debug("Evicting nu profile (" + nu + ") with id " + nu.getId());
    }
  }

  /**
   * Writes the preferences for the given user to the store
   *
   * @param inUser
   *          User whose preferences should be written
   * @throws ApplicationException ApplicationException
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setPreferences(final AmbraUser inUser) throws ApplicationException {
    if (inUser == null)
      throw new ApplicationException("User is null");

    UserAccount ua = getUserAccount(inUser.getUserId());

    UserPreferences p = ua.getPreferences(applicationId);
    if (p == null) {
      p = new UserPreferences();
      p.setAppId(applicationId);
      ua.getPreferences().add(p);
    }
    inUser.getUserPrefs(p);

  }

  /**
   * @see org.ambraproject.user.service.UserServiceImpl#setRole(String, String[], String)
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setRole(final String topazId, final String roleId, final String authId) throws ApplicationException {
    setRole(topazId, new String[] { roleId }, authId);
  }

  /**
   * Set the roles for the user.
   *
   * @param topazId the user's id
   * @param roleIds the new roles
   * @throws ApplicationException if the user doesn't exist
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void setRole(final String topazId, final String[] roleIds, final String authId) throws ApplicationException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    UserAccount ua = getUserAccount(topazId);
    ua.getRoles().clear();
    for (String r : roleIds)
      ua.getRoles().add(new UserRole(r));
    hibernateTemplate.update(ua);
  }

  /**
   * Get the roles for the user.
   * @param topazId topazId
   * @return roles
   * @throws ApplicationException
   */
  @Transactional(readOnly = true)
  public String[] getRole(final String topazId) throws ApplicationException {
    UserAccount ua = getUserAccount(topazId);

    String[] res = new String[ua.getRoles().size()];
    int idx = 0;
    for (UserRole ur : ua.getRoles())
      res[idx++] = ur.getRole();

    return res;
  }

  /**
   * Checks the action guard.
   * @return boolean
   */
  public boolean allowAdminAction(final String authId) {
    try {
      permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);
      return true;
    } catch (SecurityException ex) {
      return false;
    }
  }

  /**
   * @return Returns the appId.
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * @param appId
   *          The appId to set.
   */
  @Required
  public void setApplicationId(String appId) {
    this.applicationId = appId;
  }

  /**
   * @return the url from which the email address of the given guid can be fetched
   */
  public String getEmailAddressUrl() {
    return emailAddressUrl;
  }

  /**
   * @param emailAddressUrl
   *          The url from which the email address of the given guid can be fetched
   */
  public void setEmailAddressUrl(final String emailAddressUrl) {
    this.emailAddressUrl = emailAddressUrl;
  }

  /**
   * Getter for property 'permissionsService'.
   *
   * @return Value for property 'permissionsService'.
   */
  public PermissionsService getPermissionsService() {
    return permissionsService;
  }

  /**
   * Setter for property 'permissionsService'.
   *
   * @param permissionsService Value to set for property 'permissionsService'.
   */
  public void setPermissionsService(final PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }
}
