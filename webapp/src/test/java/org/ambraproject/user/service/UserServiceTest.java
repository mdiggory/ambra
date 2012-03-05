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

import org.ambraproject.BaseTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleView;
import org.ambraproject.models.UserLogin;
import org.ambraproject.models.UserProfile;
import org.ambraproject.models.UserRole;
import org.ambraproject.models.UserSearch;
import org.ambraproject.user.DuplicateDisplayNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class UserServiceTest extends BaseTest {

  @Autowired
  protected UserService userService;

  @DataProvider(name = "userProfile")
  private Object[][] getUserProfile() {
    UserProfile userProfile = new UserProfile();
    userProfile.setDisplayName("nameForTestLogin");
    userProfile.setEmail("emailForTest@Login.org");
    userProfile.setAuthId("authIdForTestLogin");
    userProfile.setAccountUri("id:account/test-account-uri");
    Long id = Long.valueOf(dummyDataStore.store(userProfile));

    return new Object[][]{
        {id, userProfile}
    };
  }

  @Test(dataProvider = "userProfile")
  public void testGetUser(Long id, UserProfile userProfile) {
    UserProfile result = userService.getUser(id);
    assertNotNull(result, "User Service returned null user");
    assertEquals(result.getDisplayName(), userProfile.getDisplayName(), "User Service returned user with incorrect display name");
    assertEquals(result.getEmail(), userProfile.getEmail(), "User Service returned user with incorrect email");
    assertEquals(result.getAuthId(), userProfile.getAuthId(), "User Service returned user with incorrect auth id");
  }

  @Test(dataProvider = "userProfile")
  public void testGetUserByAuthId(Long id, UserProfile userProfile) {
    UserProfile result = userService.getUserByAuthId(userProfile.getAuthId());
    assertNotNull(result, "user service returned null profile");
    assertEquals(result.getID(), id, "user service returned incorrect user profile");
  }

  @Test(dataProvider = "userProfile")
  public void testGetUserByAccountUri(Long id, UserProfile userProfile) {
    UserProfile result = userService.getUserByAccountUri(userProfile.getAccountUri());
    assertNotNull(result, "user service returned null profile");
    assertEquals(result.getID(), id, "user service returned incorrect user profile");
  }

  @Test(dataProvider = "userProfile")
  public void testLogin(Long id, UserProfile userProfile) throws Exception {
    UserLogin login = new UserLogin("sessionId", "IP", "userAgent");
    int numLogins = getUserLogins(id).size();
    userService.login(userProfile.getAuthId(), login);

    List<UserLogin> storedLogins = getUserLogins(id);
    assertEquals(storedLogins.size(), numLogins + 1, "login didn't get stored to the database");
    assertEquals(storedLogins.get(storedLogins.size() - 1).getUserAgent(), login.getUserAgent(),
        "stored login had incorrect user agent");
    assertEquals(storedLogins.get(storedLogins.size() - 1).getIP(), login.getIP(),
        "stored login had incorrect IP");
    assertEquals(storedLogins.get(storedLogins.size() - 1).getSessionId(), login.getSessionId(),
        "stored login had incorrect sessionID");
  }

  @Test(dataProvider = "userProfile")
  public void testRecordArticleView(Long userId, UserProfile userProfile) {
    Article article = new Article();
    article.setDoi("id:test-article-to-record-viewing");
    Long articleID = Long.valueOf(dummyDataStore.store(article));

    Long viewId = userService.recordArticleView(userId, articleID, ArticleView.Type.ARTICLE_VIEW);
    assertNotNull(viewId, "returned null view id");
    ArticleView storedView = dummyDataStore.get(ArticleView.class, viewId);
    assertNotNull(storedView, "didn't store article view");
    assertEquals(storedView.getType(), ArticleView.Type.ARTICLE_VIEW, "Stored view had incorrect type");
    assertEquals(storedView.getArticleID(), articleID, "Stored view had incorrect article id");
    assertEquals(storedView.getUserID(), userId, "Stored view had incorrect type");
  }

  @Test
  public void testLogSearchTerms() {
    userService.recordUserSearch(5L, "search terms", "search params");

    List<UserSearch> allSearches = dummyDataStore.getAll(UserSearch.class);

    assertEquals(allSearches.size(), 1);
    assertEquals(allSearches.get(0).getUserProfileID().longValue(), 5L);
    assertEquals(allSearches.get(0).getSearchParams(), "search params");
    assertEquals(allSearches.get(0).getSearchTerms(), "search terms");
  }

  @Test
  public void testLoginWithNonexistentUser() {
    UserProfile login = userService.login("this-isnot-areal-authid", new UserLogin());
    assertNull(login, "User service didn't return null for non-existent user");
  }

  @Test(dataProvider = "userProfile")
  public void testUpdateEmail(Long id, UserProfile userProfile) {
    String newEmail = "new@UpdateEmail.org";
    userService.updateEmail(id, newEmail);


    String storedEmail = dummyDataStore.get(UserProfile.class, id).getEmail();

    assertEquals(storedEmail, newEmail, "user service didn't update the user's email");
  }

  @Test
  public void testAllowAdminAction() throws Exception {
    //make sure the auth ids correspond to new kinds of user
    userService.getUserByAuthId(DEFAULT_ADMIN_AUTHID);
    userService.getUserByAuthId(DEFUALT_USER_AUTHID);

    assertTrue(userService.allowAdminAction(DEFAULT_ADMIN_AUTHID), "User Service didn't allow admin action for admin user");
    assertFalse(userService.allowAdminAction(DEFUALT_USER_AUTHID), "User Service allowed admin action for non-admin user");
  }

  @Test
  public void testSaveUser() throws DuplicateDisplayNameException {
    UserProfile userProfile = new UserProfile();
    userProfile.setEmail("email@saveProfile.org");
    userProfile.setDisplayName("displayNameForSavingUser");
    userProfile.setAuthId("authIdForSavingUser");
    userProfile.setBiography("Emma Swan is a 28 year-old bail bondswoman from Boston who is strong and self-reliant, " +
        "though still somehow not quite at home in her own skin. Abandoned at birth, Emma grew up in the foster care " +
        "system. She has learned to rely only on herself, never letting anyone else get close enough to let her down. " +
        "She takes a special pleasure in out-maneuvering skips on the job, and seems to prefer hauling them " +
        "in by their ears.");

    UserProfile result = userService.saveOrUpdateUser(userProfile);
    assertNotNull(result, "User service returned null user profile");
    assertNotNull(result.getID(), "User service returned profile with null id");
    UserProfile savedUser = dummyDataStore.get(UserProfile.class, result.getID());
    assertEquals(savedUser.getDisplayName(), userProfile.getDisplayName(), "Saved user had incorrect display name");
    assertEquals(savedUser.getEmail(), userProfile.getEmail(), "Saved user had incorrect email");
    assertEquals(savedUser.getAuthId(), userProfile.getAuthId(), "Saved user had incorrect authId");
    assertEquals(savedUser.getBiography(), userProfile.getBiography(), "Saved user had incorrect biography");

    assertNotNull(savedUser.getAccountUri(), "user service didn't generate account uri");
    try {
      URI.create(savedUser.getAccountUri());
    } catch (Exception e) {
      fail("account uri wasn't a valid URI", e);
    }
    assertNotNull(savedUser.getProfileUri(), "user service didn't generate profile uri");
    try {
      URI.create(savedUser.getProfileUri());
    } catch (Exception e) {
      fail("profile uri wasn't a valid URI", e);
    }
  }

  @Test
  public void testUpdateUser() throws DuplicateDisplayNameException {
    UserProfile userProfile = new UserProfile();
    userProfile.setAuthId("authIdForUpdatingUser");
    userProfile.setEmail("email@updateUser.org");
    userProfile.setDisplayName("displayNameForUpdatingUser");
    userProfile.setBiography("Regina is the mayor of Storybrooke and Henry’s adoptive mother—responsibilities she has " +
        "been balancing, without help, since she adopted Henry as a newborn. Despite the demands of her job, Regina " +
        "is an extremely attentive mother to Henry. At times, though, she can be a bit overbearing. This is " +
        "especially true whenever she crosses paths with Emma, Henry’s birthmother, with whom she makes no effort " +
        "to play nice.");
    Long id = Long.valueOf(dummyDataStore.store(userProfile));
    String newBio = "Upon Emma’s arrival in Storybrooke, Regina senses the very real threat she presents to her " +
        "relationship with Henry and immediately takes action to run Emma out of town. Nothing is too drastic " +
        "for Regina, who seems able to mobilize the entire population of Storybrooke to hassle Emma during her stay.";

    userProfile.setBiography(newBio);
    userService.saveOrUpdateUser(userProfile);

    String storedBio = dummyDataStore.get(UserProfile.class, id).getBiography();
    assertEquals(storedBio, newBio, "User didn't get biography updated");
  }
  
  @Test
  public void testUpdateUserDoesNotOverwriteRoles() throws DuplicateDisplayNameException {
    UserProfile user = new UserProfile("authIdForUpdateTestOverwriteRoles", 
        "email@overwriteRoles.org", 
        "displayNameForOverwriteRoles");
    user.setRoles(new HashSet<UserRole>(dummyDataStore.getAll(UserRole.class)));
    int numRoles = user.getRoles().size();
    assertTrue(numRoles > 0, "There were no stored roles to assign"); //shouldn't happen

    Long id = Long.valueOf(dummyDataStore.store(user));
    
    user.setRoles(new HashSet<UserRole>());

    userService.saveOrUpdateUser(user);
    Set<UserRole> storedRoles = dummyDataStore.get(UserProfile.class, id).getRoles();
    assertEquals(storedRoles.size(), numRoles, "Roles got overwritten");
  }

  @Test
  public void testUpdateDoesNotOverwriteAccountAndProfileUri() throws DuplicateDisplayNameException {
    String accountUri = "id:test-account-uri-for-overwrite-check";
    String profileUri = "id:test-profile-uri-for-overwrite-check";
    UserProfile user = new UserProfile("authIdForUpdateTestOverwriteUris",
        "email@overwriteUris.org",
        "displayNameForOverwriteUris");
    user.setAccountUri(accountUri);
    user.setProfileUri(profileUri);
    Long id = Long.valueOf(dummyDataStore.store(user));

    user.setProfileUri(null);
    user.setAccountUri(null);

    userService.saveOrUpdateUser(user);
    UserProfile storedUser = dummyDataStore.get(UserProfile.class, id);
    assertEquals(storedUser.getAccountUri(), accountUri, "account uri got overwritten");
    assertEquals(storedUser.getProfileUri(), profileUri, "account uri got overwritten");
  }

  @Test(expectedExceptions = {DuplicateDisplayNameException.class})
  public void testSaveUserWithDuplicateDisplayName() throws DuplicateDisplayNameException {
    UserProfile userProfile1 = new UserProfile();
    userProfile1.setAuthId("authIdForDupDisplayName");
    userProfile1.setEmail("email1@dupDisplayName.org");
    userProfile1.setDisplayName("thisDisplayNameWillBeDuplicated");

    UserProfile userProfile2 = new UserProfile();
    userProfile2.setAuthId("authIdForDupDisplayName2");
    userProfile2.setEmail("email2@dupDisplayName.org");
    userProfile2.setDisplayName("thisDisplayNameWillBeDuplicated");

    try {
      userService.saveOrUpdateUser(userProfile1);
    } catch (Exception e) {
      fail("user service threw exception when saving first user");
    }
    userService.saveOrUpdateUser(userProfile2);
  }

  @Test(expectedExceptions = {DuplicateDisplayNameException.class})
  public void testUpdateUserWithDupDisplayName() throws DuplicateDisplayNameException {
    UserProfile userProfile1 = new UserProfile("authIdForUpdateDupDisplayName", "email@updateDupDisplayName1.org", "updateThisAndDup");
    UserProfile userProfile2 = new UserProfile("authIdForUpdateDupDisplayName2", "email@updateDupDisplayName2.org", null);

    dummyDataStore.store(userProfile1);
    dummyDataStore.store(userProfile2);

    userProfile2.setDisplayName(userProfile1.getDisplayName());
    userService.saveOrUpdateUser(userProfile2);
  }

  @Test
  public void testGetAvailableAlerts() {
    //these come from the config
    List<UserAlert> alerts = new ArrayList<UserAlert>(2);
    alerts.add(new UserAlert("journal", "Journal", true, true));
    alerts.add(new UserAlert("journal1", "Journal 1", false, true));

    for (UserAlert alert : userService.getAvailableAlerts()) {
      UserAlert matchingAlert = null;
      for (UserAlert expectedAlert : alerts) {
        if (alert.getKey().equals(expectedAlert.getKey())) {
          matchingAlert = expectedAlert;
          break;
        }
      }
      assertNotNull(matchingAlert, "didn't find a matching alert for " + alert);
      assertEquals(alert.isMonthlyAvailable(), matchingAlert.isMonthlyAvailable(), "alert had incorrect monthly availability");
      assertEquals(alert.isWeeklyAvailable(), matchingAlert.isWeeklyAvailable(), "alert had incorrect weekly availability");
      assertEquals(alert.getName(), matchingAlert.getName(), "alert had incorrect name");
    }
  }

  @Test(dataProvider = "userProfile")
  public void testSetAlerts(Long id, UserProfile userProfile) {
    List<String> monthlyAlerts = new ArrayList<String>(2);
    monthlyAlerts.add("this_is_a_new_alert");
    monthlyAlerts.add("this_is_a_new_alert2");
    List<String> weeklyAlerts = new ArrayList<String>(1);
    weeklyAlerts.add("this_is_a_new_weekly_alert");

    String expectedAlerts = "";
    for (String monthly : monthlyAlerts) {
      expectedAlerts += (monthly + UserProfile.MONTHLY_ALERT_SUFFIX + UserProfile.ALERTS_SEPARATOR);
    }
    for (String weekly : weeklyAlerts) {
      expectedAlerts += (weekly + UserProfile.WEEKLY_ALERT_SUFFIX + UserProfile.ALERTS_SEPARATOR);
    }
    //remove the last comma
    expectedAlerts = expectedAlerts.substring(0, expectedAlerts.lastIndexOf(UserProfile.ALERTS_SEPARATOR));


    userService.setAlerts(userProfile.getAuthId(), monthlyAlerts, weeklyAlerts);

    UserProfile storedUser = dummyDataStore.get(UserProfile.class, id);
    assertEquals(storedUser.getAlertsJournals(), expectedAlerts, "User Service stored incorrect alerts");
    assertEquals(storedUser.getAlertsList().toArray(), expectedAlerts.split(","),
        "User Service stored incorrect alerts");

    //Now try removing all the alerts
    userService.setAlerts(userProfile.getAuthId(), new ArrayList<String>(0), null);
    storedUser = dummyDataStore.get(UserProfile.class, id);
    assertNull(storedUser.getAlertsJournals(), "User Service didn't remove alerts");
    assertEquals(storedUser.getAlertsList().size(), 0, "User Service didn't remove alerts");
    assertEquals(storedUser.getMonthlyAlerts().size(), 0, "User Service didn't remove monthly alerts");
    assertEquals(storedUser.getWeeklyAlerts().size(), 0, "User Service didn't remove weekly alerts");
  }

  @Test
  public void testGetUserForDisplay() {
    UserProfile userProfile = new UserProfile();
    userProfile.setDisplayName("foo_mcFoo");
    userProfile.setGivenNames("Foo");
    userProfile.setOrganizationName("foo");
    userProfile.setOrganizationType("university");
    userProfile.setPostalAddress("123 fake st");
    userProfile.setPositionType("a position type");
    userProfile.setBiography("<a href=\"http://www.trainwithmeonline.com/stretching_exercises.html\"" +
        " rel=\"dofollow\">stretching exercises</a>");

    UserProfile display = userService.getProfileForDisplay(userProfile, false);
    assertEquals(userProfile.getDisplayName(), userProfile.getDisplayName(), "user service changed the display name");
    assertEquals(userProfile.getGivenNames(), userProfile.getGivenNames(), "user service changed the given names");
    assertNull(display.getOrganizationName(), "user service didn't clear organization name");
    assertNull(display.getOrganizationType(), "user service didn't clear organization type");
    assertNull(display.getPositionType(), "user service didn't clear position type");
    assertNull(display.getPostalAddress(), "user service didn't clear organization address");
    assertEquals(display.getBiography(),
        "&lt;a href=&quot;http://www.trainwithmeonline.com/stretching_exercises.html&quot;" +
            " rel=&quot;dofollow&quot;&gt;stretching exercises&lt;/a&gt;",
        "User Service didn't escape html in biography");


    display = userService.getProfileForDisplay(userProfile, true);
    assertEquals(display.getOrganizationName(),userProfile.getOrganizationName(),
        "user service didn't show organization name with showPrivateFields set to true");
    assertEquals(display.getOrganizationType(),userProfile.getOrganizationType(),
        "user service didn't show organization type with showPrivateFields set to true");
    assertEquals(display.getPostalAddress(), userProfile.getPostalAddress(),
        "user service didn't show organization address with showPrivateFields set to true");
  }

  private List<UserLogin> getUserLogins(Long userId) {
    List<UserLogin> allLogins = dummyDataStore.getAll(UserLogin.class);
    List<UserLogin> userLogins = new ArrayList<UserLogin>();
    for (UserLogin login : allLogins) {
      if (login.getUserProfileID().equals(userId)) {
        userLogins.add(login);
      }
    }
    return userLogins;
  }
}
