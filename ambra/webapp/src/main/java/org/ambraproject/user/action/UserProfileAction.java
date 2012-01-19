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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ambraproject.ApplicationException;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.UserAccountsInterceptor;
import org.ambraproject.user.service.DisplayNameAlreadyExistsException;
import org.ambraproject.util.FileUtils;
import org.ambraproject.util.ProfanityCheckingService;
import org.ambraproject.util.TextUtils;

import static org.ambraproject.Constants.Length;
import static org.ambraproject.Constants.SINGLE_SIGNON_EMAIL_KEY;
import static org.ambraproject.Constants.ReturnCode.NEW_PROFILE;
import static org.ambraproject.Constants.ReturnCode.UPDATE_PROFILE;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Creates a new user in Topaz and sets come Profile properties.  User must be logged in via CAS.
 *
 * @author Stephen Cheng
 *
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
  private static final Pattern spacePattern = Pattern.compile("[\\p{L}\\p{N}\\p{Pc}\\p{Pd}]*");

  private static final Map<String, String> fieldToUINameMapping = new HashMap<String, String>();


  private String displayName, email, realName, topazId;
  private String authId;

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

  private ProfanityCheckingService profanityCheckingService;

  private AmbraUser newUser;

  private boolean isDisplayNameSet = true;
  private boolean displayNameRequired = true;

  /**
   * Will take the CAS ID and create a user in Topaz associated with that auth ID. If auth ID
   * already exists, it will not create another user. Email and Username are required and the
   * profile will be updated.
   *
   * @return status code for webwork
   */
  public String executeSaveUser() throws Exception {
    final AmbraUser ambraUser = getAmbraUserToUse();
    /*
     * if new user then capture the displayname or if display name is blank (when user has been
     * migrated)
     */

    /**
     * Business logic here?!?!  This should be moved to a service bean.
     */

    if ((null == ambraUser) ||
        (displayNameRequired && StringUtils.isBlank(ambraUser.getDisplayName()))) {
        isDisplayNameSet = false;
    }

    if (!validates()) {
      email = fetchUserEmailAddress();

      if (log.isDebugEnabled()) {
        log.debug("Topaz ID: " + topazId + " with authID: " + authId + " did not validate");
      }

      return INPUT;
    }

    authId = getUserIdToFetchEmailAddressFor();

    topazId = userService.lookUpUserByAuthId(authId);
    if (topazId == null) {
      topazId = userService.createUser(authId);

      // update the user-id in the session if we just created an account for ourselves
      if (authId != null && authId.equals(session.get(UserAccountsInterceptor.AUTH_KEY)))
        session.put(UserAccountsInterceptor.USER_KEY, topazId);
    }

    newUser = createAmbraUser();
    // if new or migrated user then capture the displayname
    if (!isDisplayNameSet) {
      newUser.setDisplayName(this.displayName);
    }

    if (log.isDebugEnabled()) {
      log.debug("Topaz ID: " + topazId + " with authID: " + authId);
    }

    try {
      userService.setProfile(newUser, displayNameRequired);
    } catch (DisplayNameAlreadyExistsException ex) {
      email = fetchUserEmailAddress();
      // Empty out the display name from the newUser object
      newUser.setDisplayName(StringUtils.EMPTY);
      isDisplayNameSet = false;
      addErrorForField(DISPLAY_NAME, "is already in use. Please select a different username");

      return INPUT;
    }

    isDisplayNameSet = true;
    return SUCCESS;
  }

  /**
   * Getter for newUser.
   * @return Value of newUser.
   */
  protected AmbraUser getSavedAmbraUser() {
    return newUser;
  }

  private String makeValidUrl(final String url) throws MalformedURLException {
    final String newUrl = StringUtils.stripToEmpty(url);
    if (StringUtils.isEmpty(newUrl) || newUrl.equalsIgnoreCase(HTTP_PREFIX)) {
      return StringUtils.EMPTY;
    }
    return TextUtils.makeValidUrl(newUrl);
  }

    public String executeRetrieveUserProfile() throws Exception {
    final AmbraUser ambraUser = getAmbraUserToUse();
    assignUserFields (ambraUser);

    return SUCCESS;
  }

  private void assignUserFields (AmbraUser ambraUser){
    authId = ambraUser.getAuthId();
    topazId = ambraUser.getUserId();
    email = ambraUser.getEmail();
    displayName = ambraUser.getDisplayName();
    realName = ambraUser.getRealName();
    givenNames = ambraUser.getGivenNames();
    surnames = ambraUser.getSurnames();
    title = ambraUser.getTitle();
    positionType = ambraUser.getPositionType();
    organizationType = ambraUser.getOrganizationType();
    organizationName = ambraUser.getOrganizationName();
    postalAddress = ambraUser.getPostalAddress();
    biographyText = ambraUser.getBiographyText();
    interestsText = ambraUser.getInterestsText();
    researchAreasText = ambraUser.getResearchAreasText();
    homePage = ambraUser.getHomePage();
    weblog = ambraUser.getWeblog();
    city = ambraUser.getCity();
    country = ambraUser.getCountry();

    if(ambraUser.getOrganizationVisibility() == false) {
      orgVisibility = PRIVATE;
    } else {
      orgVisibility = PUBLIC;
    }
  }

  /**
   * Prepopulate the user profile data as available
   * @return return code for webwork
   * @throws Exception Exception
   */
  public String prePopulateUserDetails() throws Exception {
    final AmbraUser ambraUser = getAmbraUserToUse();

    isDisplayNameSet = false;
    // If the user has no topaz id
    if (null == ambraUser) {
      email = fetchUserEmailAddress();
      log.debug("new profile with email: " + email);
      return NEW_PROFILE;
    } else if (StringUtils.isBlank(ambraUser.getDisplayName())) {
      // if the user has no display name, possibly getting migrated from an old system
      try {
        log.debug("this is an existing user with email: " + ambraUser.getEmail());

        assignUserFields (ambraUser);
      } catch(NullPointerException  ex) {
        /*
         * Fetching email in the case where profile creation failed and so email did not get saved.
         * Will not be needed when all the user accounts with a profile have a email set up This is
         * to display the email address to the user on the profile page, not required for saving
         */
        email = fetchUserEmailAddress();
        if (log.isDebugEnabled()) {
          log.debug("Profile was not found, so creating one for user with email:" + email);
        }
      }
      return UPDATE_PROFILE;
    }
    // else just forward the user to a success page, which might be the home page.
    isDisplayNameSet = true;
    return SUCCESS;
  }

  private AmbraUser createAmbraUser() throws Exception {
    AmbraUser ambraUser = getAmbraUserToUse();
    if (null == ambraUser || StringUtils.isEmpty(ambraUser.getEmail())) {
      if (ambraUser == null) {
        ambraUser = new AmbraUser(this.authId);
      }
      // Set the email address if the email address did not get saved during profile creation
      ambraUser.setEmail(fetchUserEmailAddress());
    }

    ambraUser.setUserId(this.topazId);
    ambraUser.setRealName(this.realName);
    ambraUser.setTitle(this.title);
    ambraUser.setSurnames(this.surnames);
    ambraUser.setGivenNames(this.givenNames);
    ambraUser.setPositionType(this.positionType);
    ambraUser.setOrganizationType(this.organizationType);
    ambraUser.setOrganizationName(this.organizationName);
    ambraUser.setPostalAddress(this.postalAddress);
    ambraUser.setBiographyText(this.biographyText);
    ambraUser.setInterestsText(this.interestsText);
    ambraUser.setResearchAreasText(this.researchAreasText);

    final String homePageUrl = StringUtils.stripToNull(makeValidUrl(homePage));
    ambraUser.setHomePage(homePageUrl);

    final String weblogUrl = StringUtils.stripToNull(makeValidUrl(weblog));
    ambraUser.setWeblog(weblogUrl);

    ambraUser.setCity(this.city);
    ambraUser.setCountry(this.country);

    if(PRIVATE.equals(this.orgVisibility)) {
      ambraUser.setOrganizationVisibility(false);
    } else if(PUBLIC.equals(this.orgVisibility)) {
      ambraUser.setOrganizationVisibility(true);
    } else {
      ambraUser.setOrganizationVisibility(false);
    }

    return ambraUser;
  }

  /**
   * Provides a way to get the AmbraUser to edit
   * @return the AmbraUser to edit
   * @throws org.ambraproject.ApplicationException ApplicationException
   */
  protected abstract AmbraUser getAmbraUserToUse() throws ApplicationException;

  public String getEmail() {
    return email;
  }

  /**
   * @param email The email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return Returns the realName.
   */
  public String getRealName() {
    return realName;
  }

  /**
   * @param realName The firstName to set.
   */
  public void setRealName(String realName) {
    this.realName = realName;
  }

  private boolean validates() {
    boolean isValid = true;

    // Don't validate displayName if the user already has a display name
    if (!isDisplayNameSet && displayNameRequired) {
      if (spacePattern.matcher(displayName).matches()) {
        final int usernameLength = displayName.length();
        if (usernameLength < Integer.parseInt(Length.DISPLAY_NAME_MIN)
              || usernameLength > Integer.parseInt(Length.DISPLAY_NAME_MAX)) {
          addErrorForField(DISPLAY_NAME, "must be between " + Length.DISPLAY_NAME_MIN + " and " + Length.DISPLAY_NAME_MAX + " characters");
          isValid = false;
        }
      } else {
        addErrorForField(DISPLAY_NAME, "may only contain letters, numbers, dashes, and underscores");
        isValid = false;
      }
    }
    if (StringUtils.isBlank(givenNames)) {
      addErrorForField(GIVEN_NAMES, "cannot be empty");
      isValid = false;
    }
    if (StringUtils.isBlank(surnames)) {
      addErrorForField(SURNAMES, "cannot be empty");
      isValid = false;
    }

    if (isInvalidUrl(homePage)) {
      addErrorForField(HOME_PAGE, "URL is not valid");
      isValid = false;
    }
    if (isInvalidUrl(weblog)) {
      addErrorForField(WEBLOG, "URL is not valid");
      isValid = false;
    }

    if (profanityCheckFailed()) {
      isValid = false;
    }

    return isValid;
  }

  private void addErrorForField(final String fieldName, final String messageSuffix) {
    addFieldError(fieldName, fieldToUINameMapping.get(fieldName) +  " " + messageSuffix);
  }

  private boolean isInvalidUrl(final String url) {
    if (StringUtils.isEmpty(url)) {
      return false;
    }
    return !TextUtils.verifyUrl(url) && !TextUtils.verifyUrl(HTTP_PREFIX + url);
  }

  private boolean profanityCheckFailed() {
    boolean isProfane = false;
    final String[][] fieldsToValidate = getFieldMappings();

    for (final String[] field : fieldsToValidate) {
      if (isProfane(field[0], field[1])) {
        isProfane = true;
      }
    }

    return isProfane;
  }

  private String[][] getFieldMappings() {
    final String[][] toValidateArray = new String[][]{
            {DISPLAY_NAME, displayName},
            {REAL_NAME, realName},
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
    return toValidateArray;
  }

  private boolean isProfane(final String fieldName, final String fieldValue) {
    final List<String> profaneWords = profanityCheckingService.validate(fieldValue);
    if (profaneWords.size() > 0 ) {
      addProfaneMessages(profaneWords, fieldName, fieldToUINameMapping.get(fieldName));
      return true;
    }
    return false;
  }

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @param displayName
   *          The displayName to set.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * @return Returns the topazId.
   */
  public String getInternalId() {
    return topazId;
  }

  /**
   * @param internalId The topazId to set.
   */
  public void setInternalId(String internalId) {
    this.topazId = internalId;
  }

  /**
   * Getter for property 'biographyText'.
   *
   * @return Value for property 'biographyText'.
   */
  public String getBiographyText() {
    return biographyText;
  }

  /**
   * Setter for property 'biographyText'.
   *
   * @param biographyText Value to set for property 'biographyText'.
   */
  public void setBiographyText(final String biographyText) {
    this.biographyText = biographyText;
  }

  /**
   * Getter for property 'city'.
   *
   * @return Value for property 'city'.
   */
  public String getCity() {
    return city;
  }

  /**
   * Setter for property 'city'.
   *
   * @param city Value to set for property 'city'.
   */
  public void setCity(final String city) {
    this.city = city;
  }

  /**
   * Getter for property 'country'.
   *
   * @return Value for property 'country'.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Setter for property 'country'.
   *
   * @param country Value to set for property 'country'.
   */
  public void setCountry(final String country) {
    this.country = country;
  }

  /**
   * Getter for property 'givennames'.
   *
   * @return Value for property 'givennames'.
   */
  public String getGivenNames() {
    return givenNames;
  }

  /**
   * Setter for property 'givenNames'.
   *
   * @param givenNames Value to set for property 'givenNames'.
   */
  public void setGivenNames(final String givenNames) {
    this.givenNames = givenNames;
  }

  /**
   * Getter for property 'interestsText'.
   *
   * @return Value for property 'interestsText'.
   */
  public String getInterestsText() {
    return interestsText;
  }

  /**
   * Setter for property 'interestsText'.
   *
   * @param interestsText Value to set for property 'interestsText'.
   */
  public void setInterestsText(final String interestsText) {
    this.interestsText = interestsText;
  }

  /**
   * Getter for property 'organizationType'.
   *
   * @return Value for property 'organizationType'.
   */
  public String getOrganizationType() {
    return organizationType;
  }

  /**
   * Setter for property 'organizationType'.
   *
   * @param organizationType Value to set for property 'organizationType'.
   */
  public void setOrganizationType(final String organizationType) {
    this.organizationType = organizationType;
  }

  /**
   * Getter for property 'positionType'.
   *
   * @return Value for property 'positionType'.
   */
  public String getPositionType() {
    return positionType;
  }

  /**
   * Setter for property 'positionType'.
   *
   * @param positionType Value to set for property 'positionType'.
   */
  public void setPositionType(final String positionType) {
    this.positionType = positionType;
  }

  /**
   * Getter for property 'postalAddress'.
   *
   * @return Value for property 'postalAddress'.
   */
  public String getPostalAddress() {
    return postalAddress;
  }

  /**
   * Setter for property 'postalAddress'.
   *
   * @param postalAddress Value to set for property 'postalAddress'.
   */
  public void setPostalAddress(final String postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Getter for property 'researchAreasText'.
   *
   * @return Value for property 'researchAreasText'.
   */
  public String getResearchAreasText() {
    return researchAreasText;
  }

  /**
   * Setter for property 'researchAreasText'.
   *
   * @param researchAreasText Value to set for property 'researchAreasText'.
   */
  public void setResearchAreasText(final String researchAreasText) {
    this.researchAreasText = researchAreasText;
  }

  /**
   * Getter for property 'topazId'.
   *
   * @return Value for property 'topazId'.
   */
  public String getTopazId() {
    return topazId;
  }

  /**
   * Setter for property 'topazId'.
   *
   * @param topazId Value to set for property 'topazId'.
   */
  public void setTopazId(final String topazId) {
    this.topazId = topazId;
  }

  /**
   * Getter for property 'organizationName'.
   * @return Value for property 'organizationName'.
   */
  public String getOrganizationName() {
    return organizationName;
  }

  /**
   * Setter for property 'organizationName'.
   * @param organizationName Value to set for property 'organizationName'.
   */
  public void setOrganizationName(final String organizationName) {
    this.organizationName = organizationName;
  }

  /**
   * Getter for property 'surnames'.
   * @return Value for property 'surnames'.
   */
  public String getSurnames() {
    return surnames;
  }

  /**
   * Setter for property 'surnames'.
   * @param surnames Value to set for property 'surnames'.
   */
  public void setSurnames(final String surnames) {
    this.surnames = surnames;
  }

  /**
   * Getter for title.
   * @return Value of title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Setter for title.
   * @param title Value to set for title.
   */
  public void setTitle(final String title) {
    this.title = title;
  }

    /**
   * Getter for orgVisibility.
   * We must set the value of the checkbox as a string because setting the value as a boolean does not work in the struts action. The unchecked state does not get to the action.. See: http://www.coderanch.com/t/448207/Struts/Struts-Checkbox-set-value-back
   * @return Value of orgVisibility.
   */
  public String getOrgVisibility() {
    if (PUBLIC.equals(orgVisibility)) {
      return PUBLIC;
    } else {
      return PRIVATE;
    }
  }

  /**
   * Setter for orgVisibility.
   * We must set the value of the checkbox as a string because setting the value as a boolean does not work in the struts action. The unchecked state does not get to the action.. See: http://www.coderanch.com/t/448207/Struts/Struts-Checkbox-set-value-back
   * @param orgVisibility Value to set for orgVisibility.
   */
  public void setOrgVisibility(final String orgVisibility) {
      if (PUBLIC.equals(orgVisibility)) {
        this.orgVisibility = PUBLIC;
      } else {
        this.orgVisibility = PRIVATE;
      }
  }

  /**
*
   * Getter for homePage.
   * @return Value of homePage.
   */
  public String getHomePage() {
    return homePage;
  }

  /**
   * Setter for homePage.
   * @param homePage Value to set for homePage.
   */
  public void setHomePage(final String homePage) {
    this.homePage = homePage.trim();
  }

  /**
   * Getter for weblog.
   * @return Value of weblog.
   */
  public String getWeblog() {
    return weblog;
  }

  /**
   * Setter for weblog.
   * @param weblog Value to set for weblog.
   */
  public void setWeblog(final String weblog) {
    this.weblog = weblog.trim();
  }

  /**
   * Getter for profanityCheckingService.
   * @return Value of profanityCheckingService.
   */
  public ProfanityCheckingService getProfanityCheckingService() {
    return profanityCheckingService;
  }

  /**
   * Setter for profanityCheckingService.
   * @param profanityCheckingService Value to set for profanityCheckingService.
   */
  public void setProfanityCheckingService(final ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  /**
   * Getter for isDisplayNameSet.
   */
  protected void setIsDisplayNameSet(final boolean inIsDisplayNameSet) {
    isDisplayNameSet = inIsDisplayNameSet;
  }

  /**
   * Getter for isDisplayNameSet.
   * @return Value of isDisplayNameSet.
   */
  public boolean getIsDisplayNameSet() {
    return isDisplayNameSet;
  }

  /**
   * Get the user id(guid) of the user for which we want to get the email address for.
   * @return user id
   */
  protected abstract String getUserIdToFetchEmailAddressFor() throws ApplicationException;

  protected String fetchUserEmailAddress() throws ApplicationException {
    String presetEmail = (String) session.get(SINGLE_SIGNON_EMAIL_KEY);
    if (presetEmail != null)
      return presetEmail;

    final String emailAddressUrl = getEmailAddressUrl();
    final String userId = getUserIdToFetchEmailAddressFor();
    final String url = emailAddressUrl + userId;
    try {
      return FileUtils.getTextFromUrl(url);
    } catch (IOException ex) {
      final String errorMessage = "Failed to fetch the email address using the url:" + url;
      log.error(errorMessage, ex);
      throw new ApplicationException(errorMessage, ex);
    }
  }

  private String getEmailAddressUrl() {
    return userService.getEmailAddressUrl();
  }

  /**
   * Setter for displayNameRequired. To be used only if the display name is not required to be set,
   * for example when the administrator is creating the account for a user.
   * @param displayNameRequired Value to set for displayNameRequired.
   */
  public void setDisplayNameRequired(final boolean displayNameRequired) {
    this.displayNameRequired = displayNameRequired;
  }
}
