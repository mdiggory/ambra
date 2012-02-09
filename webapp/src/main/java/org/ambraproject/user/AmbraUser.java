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
package org.ambraproject.user;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.topazproject.ambra.models.UserAccount;
import org.topazproject.ambra.models.UserPreferences;
import org.topazproject.ambra.models.UserProfile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to roll up access for a user into an Ambra appplication specific object.
 * @author Stephen Cheng
 */
public class AmbraUser {
  private static final Logger log = LoggerFactory.getLogger(AmbraUser.class);
  // Constants for application specific User Preferences
  private static final String ALERTS_CATEGORIES = "alertsJournals";

  /** the current user-id */
  private String      userId;

  /** authID for the user in this app */
  private String      authId;

  /** authID for the user in this app */
  private UserProfile userProfile;

  /** the user-preferences as a map */
  private Map<String, String[]> userPrefs;

  /**
   * Initializes a new Ambra user
   *
   * @param ua the user-account
   * @param appId Application ID
   *
   */
  public AmbraUser(UserAccount ua, String appId) {
    this.userId   = ua.getId().toString();
    this.authId   = ua.getAuthIds().iterator().next().getValue();

    if (ua.getProfile() == null) {
      userProfile = new UserProfile();
    } else {
      userProfile = ua.getProfile();
    }

    UserPreferences p;
    try {
      p = ua.getPreferences(appId);
    } catch (SecurityException se) {
      if (log.isDebugEnabled())
        log.debug("get-preferences was disallowed on '" + ua.getId() + "'", se);
      p = null;
    }
    if (p != null)
      userPrefs = p.getPrefsAsMap();
    else
      userPrefs = new HashMap<String, String[]>();
  }

  /**
   * Initializes a new Ambra user and sets the authentication ID
   *
   * @param authId authentication ID of new user
   */
  public AmbraUser(String authId) {
    this.userId = null;
    this.authId = authId;

    userProfile = new UserProfile();
    userPrefs   = new HashMap<String, String[]>();
  }

  /**
   * Initializes a new Ambra user from another one.
   *
   * @param pou the other user-object
   */
  protected AmbraUser(AmbraUser pou) {
    userId      = pou.userId;
    authId      = pou.authId;
    userProfile = pou.userProfile;
    userPrefs   = pou.userPrefs;
  }

  /**
   * Method to check to see if this was a migrated Ambra user or not.
   *
   * @return Returns true if the user is initialized (i.e. has chosen a username)
   */
  public boolean isInitialized() {
    return (userProfile.getDisplayName() != null);
  }

  /**
   * @return Returns the user profile.
   */
  public UserProfile getUserProfile() {
    return userProfile;
  }

  /**
   * @param p the user preferences to fill in.
   */
  public void getUserPrefs(UserPreferences p) {
    p.setPrefsFromMap(userPrefs);
  }

  /**
   * @return Returns the authId.
   */
  public String getAuthId() {
    return authId;
  }

  /**
   * @param authId
   *          The authId to set.
   */
  public void setAuthId(String authId) {
    this.authId = authId;
  }

  /**
   * @return Returns the biography.
   */
  public String getBiography() {
    return getNonNull(userProfile.getBiography());
  }

  /**
   * @param biography
   *          The biography to set.
   */
  public void setBiography(String biography) {
    userProfile.setBiography(biography);
  }

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName() {
    return getNonNull(userProfile.getDisplayName());
  }

  /**
   * @param displayName
   *          The displayName to set.
   */
  public void setDisplayName(String displayName) {
    userProfile.setDisplayName(displayName);
  }

  /**
   * @return Returns the email.
   */
  public String getEmail() {
    return getNonNull(userProfile.getEmailAsString());
  }

  /**
   * @param email
   *          The email to set.
   */
  public void setEmail(String email) {
    userProfile.setEmailFromString(email);
  }

  /**
   * @return Returns the gender.
   */
  public String getGender() {
    return getNonNull(userProfile.getGender());
  }

  /**
   * @param gender The gender to set.
   */
  public void setGender(String gender) {
    userProfile.setGender(gender);
  }

  /**
   * @return Returns the homePage.
   */
  public String getHomePage() {
    return getNonNull(userProfile.getHomePage());
  }

  /**
   * @param homePage The homePage to set.
   */
  public void setHomePage(String homePage) {
    userProfile.setHomePage(homePage != null ? URI.create(homePage) : null);
  }

  /**
   * @return Returns the interests.
   */
  public String[] getInterests() {
    Set<URI> i = userProfile.getInterests();

    String[] res = new String[i.size()];
    int idx = 0;
    for (URI uri : i)
      res[idx++] = uri.toString();

    return res;
  }

  /**
   * @param interests The interests to set.
   */
  public void setInterests(String[] interests) {
    Set<URI> i = userProfile.getInterests();
    i.clear();
    for (String interest : interests) {
      if (interest != null)
        i.add(URI.create(interest));
    }
  }

  /**
   * @return Returns the publications.
   */
  public String getPublications() {
    return getNonNull(userProfile.getPublications());
  }

  /**
   * @param publications The publications to set.
   */
  public void setPublications(String publications) {
    userProfile.setPublications(publications != null ? URI.create(publications) : null);
  }

  /**
   * @return Returns the realName.
   */
  public String getRealName() {
    return getNonNull(userProfile.getRealName());
  }

  /**
   * @param realName The realName to set.
   */
  public void setRealName(String realName) {
    userProfile.setRealName(realName);
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return getNonNull(userProfile.getTitle());
  }

  /**
   * @param title The title to set.
   */
  public void setTitle(String title) {
    userProfile.setTitle(title);
  }

  /**
   * @return Returns the userId.
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId The userId to set.
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return Returns the weblog.
   */
  public String getWeblog() {
    return getNonNull(userProfile.getWeblog());
  }

  /**
   * @param weblog The weblog to set.
   */
  public void setWeblog(String weblog) {
    userProfile.setWeblog(weblog != null ? URI.create(weblog) : null);
  }

  /**
   *
   * @return Returns the city.
   */
  public String getCity() {
    return getNonNull(userProfile.getCity());
  }

  /**
   *
   * @param city The city to set.
   */
  public void setCity(String city) {
    userProfile.setCity(city);
  }

  /**
   *
   * @return Returns the country.
   */
  public String getCountry() {
    return getNonNull(userProfile.getCountry());
  }

  /**
   *
   * @param country The country to set.
   */
  public void setCountry(String country) {
    userProfile.setCountry(country);
  }

  /**
   *
   * @return array of categories user is subscribed to
   */
  public String[] getAlerts() {
    return userPrefs.get(AmbraUser.ALERTS_CATEGORIES);
  }

  /**
   *
   * @param inAlerts the array of alert categories to set
   */
  public void setAlerts(String[] inAlerts) {
    userPrefs.put(AmbraUser.ALERTS_CATEGORIES, inAlerts);
  }

  /**
   * @return givennames givenNames
   */
  public String getGivenNames() {
    return getNonNull(userProfile.getGivenNames());
  }

  /**
   * @param givenNames givenNames
   */
  public void setGivenNames(final String givenNames) {
    userProfile.setGivenNames(givenNames);
  }

  /**
   * @return positionType positionType
   */
  public String getPositionType() {
    return getNonNull(userProfile.getPositionType());
  }

  /**
   * @param surnames surnames
   */
  public void setSurnames(final String surnames) {
    userProfile.setSurnames(surnames);
  }

  /**
   * @return surnames surnames
   */
  public String getSurnames() {
    return getNonNull(userProfile.getSurnames());
  }

  /**
   * @param positionType positionType
   */
  public void setPositionType(final String positionType) {
    userProfile.setPositionType(positionType);
  }

  /**
   * @return organizationType organizationType
   */
  public String getOrganizationType() {
    return getNonNull(userProfile.getOrganizationType());
  }

  /**
   * @param organizationType organizationType
   */
  public void setOrganizationType(final String organizationType) {
    userProfile.setOrganizationType(organizationType);
  }

  /**
   * @return organizationName organizationName
   */
  public String getOrganizationName() {
    return getNonNull(userProfile.getOrganizationName());
  }

  /**
   * @param organizationName organizationName
   */
  public void setOrganizationName(final String organizationName) {
    userProfile.setOrganizationName(organizationName);
  }

  /**
   * Set the organizational visibility
   *
   * @param visibility organization visibility
   */
  public void setOrganizationVisibility(boolean visibility) {
    userProfile.setOrganizationVisibility(visibility);
  }

  /**
   * @return OrganizationVisibility Organizational visibility
   */
  public boolean getOrganizationVisibility() {
    return userProfile.getOrganizationVisibility();
  }

  /**
   * @return postalAddress postalAddress
   */
  public String getPostalAddress() {
    return getNonNull(userProfile.getPostalAddress());
  }

  /**
   * @param postalAddress postalAddress
   */
  public void setPostalAddress(final String postalAddress) {
    userProfile.setPostalAddress(postalAddress);
  }

  /**
   * @return biographyText biographyText
   */
  public String getBiographyText() {
    return getNonNull(userProfile.getBiographyText());
  }

  /**
   * @param biographyText biographyText
   */
  public void setBiographyText(final String biographyText) {
    userProfile.setBiographyText(biographyText);
  }

  /**
   * @return interestsText interestsText
   */
  public String getInterestsText() {
    return getNonNull(userProfile.getInterestsText());
  }

  /**
   * @param interestsText interestsText
   */
  public void setInterestsText(final String interestsText) {
    userProfile.setInterestsText(interestsText);
  }

  /**
   * @return researchAreasText researchAreasText
   */
  public String getResearchAreasText() {
    return getNonNull(userProfile.getResearchAreasText());
  }

  /**
   * @param researchAreasText researchAreasText
   */
  public void setResearchAreasText(final String researchAreasText) {
    userProfile.setResearchAreasText(researchAreasText);
  }

  private String getNonNull(final String value) {
    return null == value ? StringUtils.EMPTY : value;
  }

  private String getNonNull(final URI value) {
    return null == value ? StringUtils.EMPTY : value.toString();
  }
}
