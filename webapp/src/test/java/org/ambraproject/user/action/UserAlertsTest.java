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

import static com.opensymphony.xwork2.Action.SUCCESS;
import static org.testng.Assert.*;
import org.apache.commons.lang.ArrayUtils;

import static org.ambraproject.Constants.AMBRA_USER_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.Constants;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.service.UserService;
import org.ambraproject.util.ProfanityCheckingService;

import java.util.HashMap;
import java.util.Map;

public class UserAlertsTest extends BaseTest {
  @Autowired
  protected UserService userService;

  @Autowired
  protected ProfanityCheckingService profanityCheckingService;

  final String AUTH_ID = UserAlertsTest.class.getName();

  @Test
  @DirtiesContext
  public void testCreateAlerts() throws Exception {
//    getUserService().deleteUser("info:doi/10.1371/account/141");
    final String topazId = createUser(AUTH_ID);
    final UserAlertsAction alertsAction = getMockUserAlertsAction(AUTH_ID, topazId);
    final String[] weeklyAlertCategories = new String[]{
                "biology",
                "clinical_trials",
                "computational_biology",
                "genetics",
                "pathogens"
          };

    final String[] monthlyAlertCategories = new String[]{
                "plosone",
                "clinical_trials",
                "genetics",
                "pathogens"
          };
    final String ALERT_EMAIL = "alert@emailaddress.com";

    alertsAction.setMonthlyAlerts(monthlyAlertCategories);
    alertsAction.setWeeklyAlerts(weeklyAlertCategories);

    assertEquals(SUCCESS, alertsAction.saveAlerts());
    assertEquals(SUCCESS, alertsAction.retrieveAlerts());

    for (final String monthlyAlert : alertsAction.getMonthlyAlerts()) {
      assertTrue(ArrayUtils.contains(monthlyAlertCategories, monthlyAlert));
    }

    for (final String weeklyAlert : alertsAction.getWeeklyAlerts()) {
      assertTrue(ArrayUtils.contains(weeklyAlertCategories, weeklyAlert));
    }

    userService.deleteUser(topazId, DEFAULT_ADMIN_AUTHID);
  }

  protected UserAlertsAction getMockUserAlertsAction(final String authId, final String topazId) {
    final UserAlertsAction newUserAlertsAction = new MemberUserAlertsAction();

    newUserAlertsAction.setSession(createMockSessionMap(authId, topazId));
    newUserAlertsAction.setUserService(userService);

    return newUserAlertsAction;
  }

  private String createUser(final String authId) throws Exception {
    final UserProfileAction createUserAction = getMockCreateUserAction(authId);
    createUserAction.setEmail("UserAlertsTest@test.com");
    createUserAction.setRealName("UserAlertsTest test com");
    createUserAction.setGivenNames("UserAlerts Give name");
    createUserAction.setSurnames("UserAlerts Sur name");
    //wto: createUserAction.setAuthId(authId);
    createUserAction.setDisplayName("UserAlertsTest");
    assertEquals(SUCCESS, createUserAction.executeSaveUser());
    final String topazId = createUserAction.getInternalId();
    assertNotNull(topazId);

    return topazId;
  }

  protected UserProfileAction getMockCreateUserAction(final String authId) {
    final UserProfileAction newCreateUserAction = new MemberUserProfileAction() {

      /**
       * For this unit test, we don't need or want to talk to CAS
       * @return
       */
      @Override
      protected String fetchUserEmailAddress()
      {
        return "test@ambraroject.org";
      }
    };

    newCreateUserAction.setSession(createMockSessionMap(authId, null));
    newCreateUserAction.setProfanityCheckingService(profanityCheckingService);
    newCreateUserAction.setUserService(userService);

    return newCreateUserAction;
  }

  private Map<String, Object> createMockSessionMap(final String authId, final String topazId) {
    final AmbraUser ambraUser = new AmbraUser(authId);
    if (null != topazId) {
      ambraUser.setUserId(topazId);
    }

    final Map<String, Object> sessionMap = new HashMap<String, Object>();
    sessionMap.put(AMBRA_USER_KEY, ambraUser);
    sessionMap.put(Constants.SINGLE_SIGNON_USER_KEY, authId);

    return sessionMap;
  }

}
