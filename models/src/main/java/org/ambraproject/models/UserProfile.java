/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Class representing a user profile.
 *
 * @author Alex Kudlick 2/9/12
 */
public class UserProfile extends AmbraEntity {

  public static final int STATE_ACTIVE = 0;
  public static final int STATE_SUSPENDED = 1;
  public static final String ALERTS_SEPARATOR = ",";
  public static final String MONTHLY_ALERT_SUFFIX = "_monthly";
  public static final String WEEKLY_ALERT_SUFFIX = "_weekly";

  //  TODO: These properties are legacy and should be removed once annotations are refactored
  private String accountUri;
  private String profileUri;

  private int accountState;
  private String authId;
  private String realName;
  private String givenNames;
  private String surname;
  private String title;
  private String gender;
  private String email;
  private String homePage;
  private String weblog;
  private String publications;
  private String displayName;
  private String suffix;
  private String positionType;
  private String organizationName;
  private String organizationType;
  private String postalAddress;
  private String city;
  private String country;
  private String biography;
  private String interests;
  private String researchAreas;
  //csv of alerts
  private String alertsJournals;

  private boolean organizationVisibility;

  private Set<UserRole> roles;

  public UserProfile() {
    super();
    this.organizationVisibility = false;
    this.accountState = STATE_ACTIVE;
  }

  public UserProfile(String authId, String email, String displayName) {
    this();
    this.authId = authId;
    this.email = email;
    this.displayName = displayName;
  }

  public int getAccountState() {
    return accountState;
  }

  public void setAccountState(int accountState) {
    this.accountState = accountState;
  }

  public String getAuthId() {
    return authId;
  }

  public void setAuthId(String authId) {
    this.authId = authId;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getGivenNames() {
    return givenNames;
  }

  public void setGivenNames(String givenNames) {
    this.givenNames = givenNames;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getHomePage() {
    return homePage;
  }

  public void setHomePage(String homePage) {
    this.homePage = homePage;
  }

  public String getWeblog() {
    return weblog;
  }

  public void setWeblog(String weblog) {
    this.weblog = weblog;
  }

  public String getPublications() {
    return publications;
  }

  public void setPublications(String publications) {
    this.publications = publications;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getPositionType() {
    return positionType;
  }

  public void setPositionType(String positionType) {
    this.positionType = positionType;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getOrganizationType() {
    return organizationType;
  }

  public void setOrganizationType(String organizationType) {
    this.organizationType = organizationType;
  }

  public String getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(String postalAddress) {
    this.postalAddress = postalAddress;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getBiography() {
    return biography;
  }

  public void setBiography(String biography) {
    this.biography = biography;
  }

  public String getInterests() {
    return interests;
  }

  public void setInterests(String interests) {
    this.interests = interests;
  }

  public String getResearchAreas() {
    return researchAreas;
  }

  public void setResearchAreas(String researchAreas) {
    this.researchAreas = researchAreas;
  }

  public boolean getOrganizationVisibility() {
    return organizationVisibility;
  }

  public void setOrganizationVisibility(boolean organizationVisibility) {
    this.organizationVisibility = organizationVisibility;
  }

  public String getAlertsJournals() {
    return alertsJournals;
  }

  public void setAlertsJournals(String alertsJournals) {
    this.alertsJournals = alertsJournals;
  }

  public Set<UserRole> getRoles() {
    return roles;
  }

  public void setRoles(Set<UserRole> roles) {
    this.roles = roles;
  }

  public String getAccountUri() {
    return accountUri;
  }

  public void setAccountUri(String accountUri) {
    this.accountUri = accountUri;
  }

  public String getProfileUri() {
    return profileUri;
  }

  public void setProfileUri(String profileUri) {
    this.profileUri = profileUri;
  }
  
  public List<String> getAlertsList() {
    if (getAlertsJournals() != null) {
      String[] alerts = getAlertsJournals().split(ALERTS_SEPARATOR);
      List<String> alertsList = new ArrayList<String>(alerts.length);
      alertsList.addAll(Arrays.asList(alerts));
      return alertsList;
    } else {
      return new ArrayList<String>(0);
    }
  }

  public void setAlertsList(List<String> alerts) {
    if (alerts != null && !alerts.isEmpty()) {
      this.alertsJournals = StringUtils.join(alerts, ALERTS_SEPARATOR);
    } else {
      this.alertsJournals = null;
    }
  }
  
  public List<String> getWeeklyAlerts() {
    List<String> weeklyAlerts = new ArrayList<String>();
    if (getAlertsJournals() != null) {
      for (String alert : getAlertsJournals().split(ALERTS_SEPARATOR)) {
        if (alert.endsWith(WEEKLY_ALERT_SUFFIX)) {
          weeklyAlerts.add(alert.replaceAll(WEEKLY_ALERT_SUFFIX, ""));
        }
      }
    }
    return weeklyAlerts;
  }

  public List<String> getMonthlyAlerts() {
    List<String> monthlyAlerts = new ArrayList<String>();
    if (getAlertsJournals() != null) {
      for (String alert : getAlertsJournals().split(ALERTS_SEPARATOR)) {
        if (alert.endsWith(MONTHLY_ALERT_SUFFIX)) {
          monthlyAlerts.add(alert.replaceAll(MONTHLY_ALERT_SUFFIX, ""));
        }
      }
    }
    return monthlyAlerts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserProfile)) return false;

    UserProfile profile = (UserProfile) o;

    if (displayName != null ? !displayName.equals(profile.displayName) : profile.displayName != null) return false;
    if (email != null ? !email.equals(profile.email) : profile.email != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = email != null ? email.hashCode() : 0;
    result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "UserProfile{" +
        "email='" + email + '\'' +
        ", displayName='" + displayName + '\'' +
        '}';
  }
}
