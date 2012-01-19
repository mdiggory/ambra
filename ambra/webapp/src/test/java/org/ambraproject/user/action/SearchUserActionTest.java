/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.user.action;

import com.opensymphony.xwork2.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.ApplicationException;
import org.ambraproject.BaseWebTest;
import org.topazproject.ambra.models.AuthenticationId;
import org.topazproject.ambra.models.UserAccount;
import org.topazproject.ambra.models.UserProfile;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.service.UserService;

import java.net.URI;
import java.util.HashSet;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick 9/12/11
 */
public class SearchUserActionTest extends BaseWebTest {

  @Autowired
  protected SearchUserAction searchUserAction;
  @Autowired
  protected UserService userService;


  @DataProvider(name = "savedUser")
  public Object[][] getSavedUser() throws Exception {
    UserProfile userProfile = new UserProfile();
    userProfile.setEmail(URI.create("mailto:test@plos.org"));
    userProfile.setGivenNames("John P.");
    userProfile.setSurnames("Smith");
    userProfile.setRealName("John P. Smith");
    userProfile.setDisplayName("jps");
    userProfile.setPositionType("doctor");
    userProfile.setOrganizationType("university");
    userProfile.setPostalAddress("123 fake st");
    userProfile.setBiographyText("born; lived; died");
    userProfile.setInterestsText("interesting stuff");
    userProfile.setResearchAreasText("areas to research");
    userProfile.setCity("city");
    userProfile.setCountry("country");
    userProfile.setId(URI.create("id:test-user-profile"));

    dummyDataStore.store(userProfile);

    HashSet<AuthenticationId> authIds = new HashSet<AuthenticationId>(1);

    AuthenticationId authenticationId = new AuthenticationId();
    authenticationId.setValue("jdalskjfgoainvkajhdlgfkhaoeiw13094");
    authenticationId.setRealm("local");
    dummyDataStore.store(authenticationId);
    authIds.add(authenticationId);

    UserAccount userAccount = new UserAccount();
    userAccount.setProfile(userProfile);
    userAccount.setAuthIds(authIds);
    userAccount.setId(URI.create("id:test-user-account"));
    dummyDataStore.store(userAccount);

    return new Object[][]{
        {userAccount}
    };
  }


  @Test(dataProvider = "savedUser")
  public void testSearchUserByAuthId(UserAccount storedUser) throws Exception {
    setupAdminContext();
    String authId = storedUser.getAuthIds().toArray(new AuthenticationId[1])[0].getValue();
    searchUserAction.setRequest(getDefaultRequestAttributes());
    searchUserAction.setAuthId(authId);
    assertEquals(searchUserAction.executeFindUserByAuthId(), Action.SUCCESS,
        "searchUserAction.executeFindUserByAuthId() didn't return success");

    final String[] topazUserIdList = searchUserAction.getTopazUserIdList();
    assertTrue(topazUserIdList.length == 1, "Didn't return correct number of users");
    checkSavedUser(topazUserIdList[0], storedUser);
    searchUserAction.setAuthId(null);
  }

  @Test(dataProvider = "savedUser")
  public void testSearchUserByEmail(UserAccount storedUser) throws Exception {
    setupAdminContext();
    String emailAddress = storedUser.getProfile().getEmail().toString().replaceAll("mailto:","");
    searchUserAction.setEmailAddress(emailAddress);
    searchUserAction.setRequest(getDefaultRequestAttributes());

    assertEquals(searchUserAction.executeFindUserByEmailAddress(), Action.SUCCESS,
        "searchUserAction.executeFindUserByEmailAddress() didn't return success");

    final String[] topazUserIdList = searchUserAction.getTopazUserIdList();
    assertTrue(topazUserIdList.length == 1, "didn't return correct number of user profiles");
    checkSavedUser(topazUserIdList[0], storedUser);
    searchUserAction.setEmailAddress(null);
  }

  @Test(dataProvider = "savedUser")
  public void testSearchUserByAccountId(UserAccount storedUser) throws Exception {
    setupAdminContext();
    searchUserAction.setAccountId(storedUser.getId().toString());
    searchUserAction.setRequest(getDefaultRequestAttributes());

    assertEquals(searchUserAction.executeFindUserByAccountId(), Action.SUCCESS,
        "searchUserAction.executeFindUserByAccountId() didn't return success");

    final String[] topazUserIdList = searchUserAction.getTopazUserIdList();
    assertTrue(topazUserIdList.length == 1, "didn't return correct number of user profiles");
    checkSavedUser(topazUserIdList[0], storedUser);
    searchUserAction.setAccountId(null);
  }

  @Test(dataProvider = "savedUser")
  public void testSearchUserByName(UserAccount storedUser) throws Exception {
    setupAdminContext();
    searchUserAction.setName(storedUser.getProfile().getDisplayName());
    searchUserAction.setRequest(getDefaultRequestAttributes());

    assertEquals(searchUserAction.executeFindUserByName(), Action.SUCCESS,
        "searchUserAction.executeFindUserByName() didn't return success");

    final String[] topazUserIdList = searchUserAction.getTopazUserIdList();
    assertTrue(topazUserIdList.length == 1, "didn't return correct number of user profiles");
    checkSavedUser(topazUserIdList[0], storedUser);
    searchUserAction.setName(null);
  }

  /**
   * Checks that the values for the user stored with the given id are the constants from this class
   *
   * @param userId the id to check
   * @throws org.ambraproject.ApplicationException
   *          if there's an error retrieving the user
   */
  private void checkSavedUser(String userId, UserAccount expectedUser) throws ApplicationException {
    AmbraUser savedUser = userService.getUserById(userId);
    UserProfile profile = expectedUser.getProfile();
    assertNotNull(savedUser, "Failed to save user");
    assertEquals("mailto:" + savedUser.getEmail(), profile.getEmail().toString(), "saved user didn't have correct email");
    assertEquals(savedUser.getRealName(), profile.getRealName(), "saved user didn't have correct real name");
    assertEquals(savedUser.getDisplayName(), profile.getDisplayName(), "saved user didn't have correct display name");
    assertEquals(savedUser.getGivenNames(), profile.getGivenNames(), "saved user didn't have correct given names");
    assertEquals(savedUser.getSurnames(), profile.getSurnames(), "saved user didn't have correct surnames");
    assertEquals(savedUser.getPositionType(), profile.getPositionType(), "saved user didn't have correct position type");
    assertEquals(savedUser.getOrganizationType(), profile.getOrganizationType(), "saved user didn't have correct organization type");
    assertEquals(savedUser.getPostalAddress(), profile.getPostalAddress(), "saved user didn't have correct postal address");
    assertEquals(savedUser.getBiographyText(), profile.getBiographyText(), "saved user didn't have correct biography text");
    assertEquals(savedUser.getInterestsText(), profile.getInterestsText(), "saved user didn't have correct interests text");
    assertEquals(savedUser.getResearchAreasText(), profile.getResearchAreasText(), "saved user didn't have correct research areas text");
    assertEquals(savedUser.getCity(), profile.getCity(), "saved user didn't have correct city");
    assertEquals(savedUser.getCountry(), profile.getCountry(), "saved user didn't have correct country");
  }
}

