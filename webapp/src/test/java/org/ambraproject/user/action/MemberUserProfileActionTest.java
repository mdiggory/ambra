/* $HeadURL::                                                                            $
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
package org.ambraproject.user.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.ambraproject.action.BaseActionSupport;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.ambraproject.ApplicationException;
import org.ambraproject.BaseWebTest;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.service.UserService;
import org.ambraproject.util.ProfanityCheckingService;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test for {@link MemberUserProfileAction}.  This test creates mock instances that are prevented from talking to CAS,
 * but behave the same in all other respects
 *
 * @author Alex Kudlick
 */
public class MemberUserProfileActionTest extends BaseWebTest {

  /**
   * We're getting the services autowired here so that we can create mock action classes that don't talk to CAS for the
   * user email address.  We set the services on them so they behave normally in other respects.
   */
  @Autowired
  protected UserService userService;
  @Autowired
  private ProfanityCheckingService profanityCheckingService;
  @Autowired
  protected Configuration configuration;

  private static final String TEST_EMAIL = "userActionTest@topazproject.org";
  private static final String REAL_NAME = "Test User";
  private static final String USERNAME = "TEST_USERNAME";
  private static final String GIVENNAMES = "my GIVENNAMES";
  private static final String SURNAMES = "my Surnames";
  private static final String POSITION_TYPE = "my POSITION_TYPE";
  private static final String ORGANIZATION_TYPE = "my organizationType";
  private static final String POSTAL_ADDRESS = "my postalAddress";
  private static final String BIOGRAPHY_TEXT = "my biographyText";
  private static final String INTERESTS_TEXT = "my interestsText";
  private static final String RESEARCH_AREAS_TEXT = "my researchAreasText";
  private static final String CITY = "my city";
  private static final String COUNTRY = "my country";


  @Test
  public void testCreateUser() throws Exception {
    UserProfileAction createUserAction = createMemberUserProfileAction();
    assertEquals(createUserAction.executeSaveUser(), Action.SUCCESS, "executeSaveUser() didn't return success");

    String userId = createUserAction.getInternalId();
    assertNotNull(userId, "user action had null internal id");
    checkSavedUser(userId);
  }


  /**
   * Creates a mock {@link MemberUserProfileAction} that just uses {@link #TEST_EMAIL} instead of talking to CAS for the
   * user's email.  Has the session and all services set on it
   *
   * @return a {@link MemberUserProfileAction} that doesn't talk to CAS
   */
  protected MemberUserProfileAction createMemberUserProfileAction() {
    final MemberUserProfileAction newCreateUserAction = new MemberUserProfileAction() {
      /**
       * For this unit test, we don't need or want to talk to CAS
       * @return {@link MemberUserProfileActionTest#TEST_EMAIL}
       */
      @Override
      protected String fetchUserEmailAddress() {
        return TEST_EMAIL;
      }
    };
    newCreateUserAction.setSession(ActionContext.getContext().getSession());
    newCreateUserAction.setProfanityCheckingService(profanityCheckingService);
    newCreateUserAction.setUserService(userService);

    newCreateUserAction.setEmail(TEST_EMAIL);
    newCreateUserAction.setRealName(REAL_NAME);
    newCreateUserAction.setDisplayName(USERNAME);
    newCreateUserAction.setGivenNames(GIVENNAMES);
    newCreateUserAction.setSurnames(SURNAMES);
    newCreateUserAction.setPositionType(POSITION_TYPE);
    newCreateUserAction.setOrganizationType(ORGANIZATION_TYPE);
    newCreateUserAction.setPostalAddress(POSTAL_ADDRESS);
    newCreateUserAction.setBiographyText(BIOGRAPHY_TEXT);
    newCreateUserAction.setInterestsText(INTERESTS_TEXT);
    newCreateUserAction.setResearchAreasText(RESEARCH_AREAS_TEXT);
    newCreateUserAction.setCity(CITY);
    newCreateUserAction.setCountry(COUNTRY);

    return newCreateUserAction;
  }

  /**
   * Checks that the values for the user stored with the given id are the constants from this class
   *
   * @param userId the id to check
   * @throws ApplicationException if there's an error retrieving the user
   */
  private void checkSavedUser(String userId) throws ApplicationException {
    AmbraUser savedUser = userService.getUserById(userId);
    assertNotNull(savedUser, "Failed to save user");
    assertEquals(savedUser.getEmail(), TEST_EMAIL, "saved user didn't have correct email");
    assertEquals(savedUser.getRealName(), REAL_NAME, "saved user didn't have correct real name");
    assertEquals(savedUser.getDisplayName(), USERNAME, "saved user didn't have correct display name");
    assertEquals(savedUser.getGivenNames(), GIVENNAMES, "saved user didn't have correct given names");
    assertEquals(savedUser.getSurnames(), SURNAMES, "saved user didn't have correct surnames");
    assertEquals(savedUser.getPositionType(), POSITION_TYPE, "saved user didn't have correct position type");
    assertEquals(savedUser.getOrganizationType(), ORGANIZATION_TYPE, "saved user didn't have correct organization type");
    assertEquals(savedUser.getPostalAddress(), POSTAL_ADDRESS, "saved user didn't have correct postal address");
    assertEquals(savedUser.getBiographyText(), BIOGRAPHY_TEXT, "saved user didn't have correct biography text");
    assertEquals(savedUser.getInterestsText(), INTERESTS_TEXT, "saved user didn't have correct interests text");
    assertEquals(savedUser.getResearchAreasText(), RESEARCH_AREAS_TEXT, "saved user didn't have correct research areas text");
    assertEquals(savedUser.getCity(), CITY, "saved user didn't have correct city");
    assertEquals(savedUser.getCountry(), COUNTRY, "saved user didn't have correct country");
  }

  @Override
  protected BaseActionSupport getAction() {
    return createMemberUserProfileAction();
  }
}
