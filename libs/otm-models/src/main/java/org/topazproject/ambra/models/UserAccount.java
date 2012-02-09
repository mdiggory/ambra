/* $HeadURL::                                                                                     $
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

package org.topazproject.ambra.models;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.topazproject.otm.CascadeType;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * A user's account.
 *
 * @author Ronald Tschalär
 */
@Entity(types = {"foaf:OnlineAccount"}, graph = "users")
public class UserAccount implements Serializable {
  private static final long serialVersionUID = 5552880968060090198L;
  /** the state indicating the user account is active: {@value} */
  public static final int ACNT_ACTIVE    = 0;
  /** the state indicating the user account is suspended: {@value} */
  public static final int ACNT_SUSPENDED = 1;

  private URI                   id;
  private int                   state = ACNT_ACTIVE;
  private Set<AuthenticationId> authIds = new HashSet<AuthenticationId>();
  private Set<UserRole>         roles = new HashSet<UserRole>();
  private Set<UserPreferences>  preferences = new HashSet<UserPreferences>();
  private UserProfile           profile;

  /**
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  @Id @GeneratedValue(uriPrefix = "id:account/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the state.
   *
   * @return the state.
   */
  public int getState() {
    return state;
  }

  /**
   * Set the state.
   *
   * @param state the state.
   */
  @Predicate(uri = "topaz:accountState")
  public void setState(int state) {
    this.state = state;
  }

  /**
   * Get the authentication ids.
   *
   * @return the authentication ids.
   */
  public Set<AuthenticationId> getAuthIds() {
    return authIds;
  }

  /**
   * Set the authentication ids.
   *
   * @param authIds the authentication ids.
   */
  @Predicate(uri = "topaz:hasAuthId", cascade = {CascadeType.child})
  public void setAuthIds(Set<AuthenticationId> authIds) {
    this.authIds = authIds;
  }

  /**
   * Get the roles.
   *
   * @return the roles.
   */
  public Set<UserRole> getRoles() {
    return roles;
  }

  /**
   * Set the roles.
   *
   * @param roles the roles.
   */
  @Predicate(uri = "topaz:hasRoles", cascade = {CascadeType.child})
  public void setRoles(Set<UserRole> roles) {
    this.roles = roles;
  }

  /**
   * Get the preferences.
   *
   * @return the preferences.
   */
  public Set<UserPreferences> getPreferences() {
    return preferences;
  }

  /**
   * Set the preferences.
   *
   * @param preferences the preferences.
   */
  @Predicate(uri = "topaz:hasPreferences", graph="preferences", 
             cascade = {CascadeType.child})
  public void setPreferences(Set<UserPreferences> preferences) {
    this.preferences = preferences;
  }

  /**
   * Get the preferences for a given application id.
   *
   * @param appId the application id
   * @return the preferences, or null if none found.
   */
  public UserPreferences getPreferences(String appId) {
    for (UserPreferences p : getPreferences()) {
      if (p.getAppId().equals(appId))
        return p;
    }
    return null;
  }

  /**
   * Get the user's profile.
   *
   * @return the profile.
   */
  public UserProfile getProfile() {
    return profile;
  }

  /**
   * Set the user's profile.
   *
   * @param profile the profile.
   */
  @Predicate(uri = "foaf:holdsAccount", inverse = Predicate.BT.TRUE, graph="profiles",
             cascade = {CascadeType.child})
  public void setProfile(UserProfile profile) {
    this.profile = profile;
  }
}
