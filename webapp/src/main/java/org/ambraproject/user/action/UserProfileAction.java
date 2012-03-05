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

package org.ambraproject.user.action;

import org.ambraproject.ApplicationException;
import org.ambraproject.Constants;
import org.ambraproject.models.UserProfile;
import org.ambraproject.user.DuplicateDisplayNameException;
import org.ambraproject.util.ProfanityCheckingService;
import org.ambraproject.util.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

import static org.ambraproject.Constants.SINGLE_SIGNON_EMAIL_KEY;

/**
 * Creates a new user in Topaz and sets come Profile properties.  User must be logged in via CAS.
 *
 * @author Stephen Cheng
 */
public abstract class UserProfileAction extends UserActionSupport {
  private static final Logger log = LoggerFactory.getLogger(UserProfileAction.class);

  public static final String PRIVATE = "private";
  public static final String PUBLIC = "public";

  private static final String GIVEN_NAMES = "givenNames";
  private static final String REAL_NAME = "realName";
  private static final String POSTAL_ADDRESS = "postalAddress";
  private static final String ORGANIZATION_TYPE = "organizationType";
  private static final String ORGANIZATION_NAME = "organizationName";
  private static final String TITLE = "title";
  private static final String POSITION_TYPE = "positionType";
  private static final String DISPLAY_NAME = "displayName";
  private static final String SURNAMES = "surnames";
  private static final String CITY = "city";
  private static final String COUNTRY = "country";
  private static final String BIOGRAPHY_TEXT = "biographyText";
  private static final String INTERESTS_TEXT = "interestsText";
  private static final String RESEARCH_AREAS_TEXT = "researchAreasText";
  private static final String HOME_PAGE = "homePage";
  private static final String WEBLOG = "weblog";

  private static final String HTTP_PREFIX = "http://";
  private static final Pattern validDisplayNamePattern = Pattern.compile("[\\p{L}\\p{N}\\p{Pc}\\p{Pd}]*");

  private String email;
  private String displayName;
  private String givenNames;
  private String surnames;
  private String positionType;
  private String organizationType;
  private String organizationName;
  private String postalAddress;
  private String biographyText;
  private String interestsText;
  private String researchAreasText;
  private String homePage;
  private String weblog;
  private String city;
  private String country;
  private String title;
  private String orgVisibility;
  //users don't actually edit this value, but we need to pass it to the freemarker in a hidden input to get it back on the save action
  private String alertsJournals;

  //Need this to tell us to show the display name text box if the user entered in a duplicate
  private boolean showDisplayName = false;

  private ProfanityCheckingService profanityCheckingService;

  /**
   * Subclasses must override to provide a way to get the auth id of the user to edit.  E.g. MemberUserProfileAction must return the authId of
   * the current user, but adminUserProfileAction can return the authId of another user
   *
   * @return
   */
  protected abstract String getUserAuthId();

  /**
   * Subclasses can override this method if they want to perform actions after the user has been saved
   *
   * @param userProfile the user profile object that was saved
   */
  protected void afterSave(UserProfile userProfile) {
    //meant to be overrided
  }

  public String execute() throws Exception {
    String authId = getUserAuthId();
    UserProfile userProfile = userService.getUserByAuthId(authId);
    if (userProfile != null) {
      setFieldsFromProfile(userProfile);
      //If there is no display name, this is an old user without one, and we need to return a specific code
      //(otherwise we'll loop infinitely because EnsureAccountUserInterceptor will keep returning UPDATE_PROFILE
      if (displayName == null || displayName.isEmpty()) {
        return Constants.ReturnCode.UPDATE_PROFILE;
      } else {
        return SUCCESS;
      }
    } else {
      //this is a new user
      email = fetchUserEmailAddress();
      return Constants.ReturnCode.NEW_PROFILE;
    }
  }

  public String executeSaveUser() throws Exception {
    boolean valid = validateInput();
    if (!valid) {
      return INPUT;
    }

    UserProfile profile = createProfileFromFields();
    //Make sure to set the auth id so the user service can see if this account already exists
    String userAuthId = getUserAuthId();
    profile.setAuthId(userAuthId);
    try {
      UserProfile savedProfile = userService.saveOrUpdateUser(profile);
      afterSave(savedProfile);
    } catch (DuplicateDisplayNameException e) {
      addFieldError(DISPLAY_NAME, "A user already exists with the given user name");
      showDisplayName = true;
      return INPUT;
    }
    return SUCCESS;
  }

  private boolean validateInput() {
    boolean isValid = true;
    //check the display name
    if (displayName == null) {
      addFieldError(DISPLAY_NAME, "Please enter a username");
      isValid = false;
    } else {
      if (validDisplayNamePattern.matcher(displayName).matches()) {
        final int usernameLength = displayName.length();
        if (usernameLength < Integer.parseInt(Constants.Length.DISPLAY_NAME_MIN)
            || usernameLength > Integer.parseInt(Constants.Length.DISPLAY_NAME_MAX)) {
          addFieldError(DISPLAY_NAME, "must be between " + Constants.Length.DISPLAY_NAME_MIN +
              " and " + Constants.Length.DISPLAY_NAME_MAX + " characters");
          isValid = false;
        }
      } else {
        addFieldError(DISPLAY_NAME, "username may only contain letters, numbers, dashes, and underscores");
        isValid = false;
      }
    }

    if (givenNames == null || StringUtils.isBlank(givenNames)) {
      addFieldError(GIVEN_NAMES, "Given name cannot be empty");
      isValid = false;
    }
    if (surnames == null || StringUtils.isBlank(surnames)) {
      addFieldError(SURNAMES, "Surname cannot be empty");
      isValid = false;
    }

    if (isInvalidUrl(homePage)) {
      addFieldError(HOME_PAGE, "Home page URL is not valid");
      isValid = false;
    }
    if (isInvalidUrl(weblog)) {
      addFieldError(WEBLOG, "Weblog URL is not valid");
      isValid = false;
    }

    isValid = checkProfanity() && isValid;

    return isValid;
  }

  private boolean checkProfanity() {
    boolean isValid = true;
    final String[][] fieldsToValidate = new String[][]{
        {DISPLAY_NAME, displayName},
        {TITLE, title},
        {SURNAMES, surnames},
        {GIVEN_NAMES, givenNames},
        {POSITION_TYPE, positionType},
        {ORGANIZATION_NAME, organizationName},
        {ORGANIZATION_TYPE, organizationType},
        {POSTAL_ADDRESS, postalAddress},
        {BIOGRAPHY_TEXT, biographyText},
        {INTERESTS_TEXT, interestsText},
        {RESEARCH_AREAS_TEXT, researchAreasText},
        {HOME_PAGE, homePage},
        {WEBLOG, weblog},
        {CITY, city},
        {COUNTRY, country},
    };
    for (final String[] field : fieldsToValidate) {
      final String fieldName = field[0];
      final String fieldValue = field[1];
      final List<String> profaneWords = profanityCheckingService.validate(fieldValue);
      if (profaneWords.size() > 0) {
        addProfaneMessages(profaneWords, fieldName, fieldName);
        isValid = false;
      }
    }
    return isValid;
  }

  private boolean isInvalidUrl(final String url) {
    //allow null or or empty value for urls
    if (url == null || url.isEmpty()) {
      return false;
    } else {
      return !TextUtils.verifyUrl(url) && !TextUtils.verifyUrl(HTTP_PREFIX + url);
    }
  }

  protected void setFieldsFromProfile(UserProfile ambraUser) {
    email = ambraUser.getEmail();
    displayName = ambraUser.getDisplayName();
    givenNames = ambraUser.getGivenNames();
    surnames = ambraUser.getSurname();
    title = ambraUser.getTitle();
    positionType = ambraUser.getPositionType();
    organizationType = ambraUser.getOrganizationType();
    organizationName = ambraUser.getOrganizationName();
    postalAddress = ambraUser.getPostalAddress();
    biographyText = ambraUser.getBiography();
    interestsText = ambraUser.getInterests();
    researchAreasText = ambraUser.getResearchAreas();
    homePage = ambraUser.getHomePage();
    weblog = ambraUser.getWeblog();
    city = ambraUser.getCity();
    country = ambraUser.getCountry();
    alertsJournals = ambraUser.getAlertsJournals();

    if (!ambraUser.getOrganizationVisibility()) {
      orgVisibility = PRIVATE;
    } else {
      orgVisibility = PUBLIC;
    }
  }

  protected UserProfile createProfileFromFields() throws Exception {
    UserProfile userProfile = new UserProfile();
    userProfile.setDisplayName(this.displayName);
    userProfile.setEmail(this.email);
    userProfile.setTitle(this.title);
    userProfile.setSurname(this.surnames);
    userProfile.setGivenNames(this.givenNames);
    userProfile.setRealName(this.givenNames + " " + this.surnames);
    userProfile.setPositionType(this.positionType);
    userProfile.setOrganizationType(this.organizationType);
    userProfile.setOrganizationName(this.organizationName);
    userProfile.setPostalAddress(this.postalAddress);
    userProfile.setBiography(this.biographyText);
    userProfile.setInterests(this.interestsText);
    userProfile.setResearchAreas(this.researchAreasText);
    userProfile.setAlertsJournals(this.alertsJournals);

    final String homePageUrl = StringUtils.stripToNull(makeValidUrl(homePage));
    userProfile.setHomePage(homePageUrl);

    final String weblogUrl = StringUtils.stripToNull(makeValidUrl(weblog));
    userProfile.setWeblog(weblogUrl);

    userProfile.setCity(this.city);
    userProfile.setCountry(this.country);

    if (PRIVATE.equals(this.orgVisibility)) {
      userProfile.setOrganizationVisibility(false);
    } else if (PUBLIC.equals(this.orgVisibility)) {
      userProfile.setOrganizationVisibility(true);
    } else {
      userProfile.setOrganizationVisibility(false);
    }

    return userProfile;
  }

  /**
   * Get the email address of the user being edited. This is taken from the session, if it's there, or else from CAS
   * <p/>
   * We only need to call this if we're editing a new user whose email isn't in ambra's database
   *
   * @return the email address of the user being edited
   * @throws ApplicationException if there was a problem talking to the CAS server
   */
  @SuppressWarnings("unchecked")
  protected String fetchUserEmailAddress() throws ApplicationException {
    String presetEmail = (String) session.get(SINGLE_SIGNON_EMAIL_KEY);
    if (presetEmail != null) {
      return presetEmail;
    } else {
      String email = userService.fetchUserEmailFromCas(getUserAuthId());
      if (email == null) {
        throw new ApplicationException("Unable to fetch user email address for authid: " + getUserAuthId());
      }
      session.put(SINGLE_SIGNON_EMAIL_KEY, email);
      return email;
    }
  }

  private String makeValidUrl(final String url) throws MalformedURLException {
    final String newUrl = StringUtils.stripToEmpty(url);
    if (StringUtils.isEmpty(newUrl) || newUrl.equalsIgnoreCase(HTTP_PREFIX)) {
      return StringUtils.EMPTY;
    }
    return TextUtils.makeValidUrl(newUrl);
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getGivenNames() {
    return givenNames;
  }

  public void setGivenNames(String givenNames) {
    this.givenNames = givenNames;
  }

  public String getSurnames() {
    return surnames;
  }

  public void setSurnames(String surnames) {
    this.surnames = surnames;
  }

  public String getPositionType() {
    return positionType;
  }

  public void setPositionType(String positionType) {
    this.positionType = positionType;
  }

  public String getOrganizationType() {
    return organizationType;
  }

  public void setOrganizationType(String organizationType) {
    this.organizationType = organizationType;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(String postalAddress) {
    this.postalAddress = postalAddress;
  }

  public String getBiographyText() {
    return biographyText;
  }

  public void setBiographyText(String biographyText) {
    this.biographyText = biographyText;
  }

  public String getInterestsText() {
    return interestsText;
  }

  public void setInterestsText(String interestsText) {
    this.interestsText = interestsText;
  }

  public String getResearchAreasText() {
    return researchAreasText;
  }

  public void setResearchAreasText(String researchAreasText) {
    this.researchAreasText = researchAreasText;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getOrgVisibility() {
    if (PUBLIC.equals(orgVisibility)) {
      return PUBLIC;
    } else {
      return PRIVATE;
    }
  }

  public void setOrgVisibility(String orgVisibility) {
    if (PUBLIC.equals(orgVisibility)) {
      this.orgVisibility = PUBLIC;
    } else {
      this.orgVisibility = PRIVATE;
    }
  }

  public void setProfanityCheckingService(ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  public String getAlertsJournals() {
    return alertsJournals;
  }

  public void setAlertsJournals(String alertsJournals) {
    this.alertsJournals = alertsJournals;
  }

  public boolean isShowDisplayName() {
    return showDisplayName;
  }
}
