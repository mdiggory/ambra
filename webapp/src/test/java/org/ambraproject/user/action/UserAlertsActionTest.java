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
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.UserProfile;
import org.ambraproject.user.service.UserAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;

public class UserAlertsActionTest extends BaseWebTest {

  @Autowired
  protected MemberUserAlertsAction action;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }

  @DataProvider(name = "userWithAlerts")
  public Object[][] getUserWithAlerts() {
    UserProfile user = new UserProfile();
    user.setEmail("email@testUserAlerts.org");
    user.setDisplayName("userWithAlertsForTestAlerts");
    user.setAuthId("authIdWithAlertsForTestUserAlerts");
    user.setAlertsJournals("journal_weekly,journal_monthly");
    dummyDataStore.store(user);

    String[] expectedWeeklyAlerts = new String[]{"journal"};
    String[] expectedMonthlyAlerts = new String[]{"journal"};

    //these come from the config
    List<UserAlert> alerts = new ArrayList<UserAlert>(2);
    alerts.add(new UserAlert("journal", "Journal", true, true));
    alerts.add(new UserAlert("journal1", "Journal 1", false, true));


    dummyDataStore.store(user);
    return new Object[][]{
        {user, alerts, expectedWeeklyAlerts, expectedMonthlyAlerts}
    };
  }

  @Test(dataProvider = "userWithAlerts")
  public void testRetrieveAlerts(UserProfile user, List<UserAlert> expectedAlerts,
                                 String[] expectedWeeklyAlerts, String[] expectedMonthlyAlerts) throws Exception {
    login(user);

    String result = action.retrieveAlerts();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(action.getUserAlerts().size(), expectedAlerts.size(), "Action didn't return correct number of alerts");
    for (UserAlert alert : action.getUserAlerts()) {
      UserAlert matchingAlert = null;
      for (UserAlert expectedAlert : expectedAlerts) {
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
    assertEqualsNoOrder(action.getWeeklyAlerts(), expectedWeeklyAlerts, "Action had incorrect weekly alerts for user");
    assertEqualsNoOrder(action.getMonthlyAlerts(), expectedMonthlyAlerts, "Action had incorrect monthly alerts for user");
  }

  @Test
  public void testEditAlerts() throws Exception {
    UserProfile user = new UserProfile();
    user.setEmail("email@testEditAlerts.org");
    user.setAuthId("authIdForTestEditAlerts");
    user.setDisplayName("displayNameForTestEditAlerts");
    user.setAlertsJournals("journal_weekly,journal_monthly");
    dummyDataStore.store(user);

    String[] newWeeklyAlerts = new String[]{"journal_weekly", "journal1_weekly"};
    String[] newMonthlyAlerts = new String[]{};
    String[] expectedAlerts = new String[]{"journal_weekly", "journal1_weekly"};

    ActionContext.getContext().getSession().put(Constants.AUTH_KEY, user.getAuthId());
    action.setSession(ActionContext.getContext().getSession());
    action.setWeeklyAlerts(newWeeklyAlerts);
    action.setMonthlyAlerts(newMonthlyAlerts);

    String result = action.saveAlerts();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    String storedAlerts = dummyDataStore.get(UserProfile.class, user.getID()).getAlertsJournals();
    assertEqualsNoOrder(storedAlerts.split(","), expectedAlerts, "action didn't store correct alerts to the database");
  }

  @Test
  public void testAddAlertsForUserWithNoAlerts() throws Exception {
    UserProfile user = new UserProfile();
    user.setEmail("email@testAddAlerts.org");
    user.setAuthId("authIdForTestAddAlerts");
    user.setDisplayName("displayNameForTestAddAlerts");
    dummyDataStore.store(user);

    String[] newWeeklyAlerts = new String[]{"journal_weekly", "journal1_weekly"};
    String[] newMonthlyAlerts = new String[]{"journal_monthly"};
    String[] expectedAlerts = new String[]{"journal_weekly", "journal1_weekly", "journal_monthly"};

    ActionContext.getContext().getSession().put(Constants.AUTH_KEY, user.getAuthId());
    action.setSession(ActionContext.getContext().getSession());
    action.setWeeklyAlerts(newWeeklyAlerts);
    action.setMonthlyAlerts(newMonthlyAlerts);

    String result = action.saveAlerts();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    String storedAlerts = dummyDataStore.get(UserProfile.class, user.getID()).getAlertsJournals();
    assertEqualsNoOrder(storedAlerts.split(","), expectedAlerts, "action didn't store correct alerts to the database");
  }

}
