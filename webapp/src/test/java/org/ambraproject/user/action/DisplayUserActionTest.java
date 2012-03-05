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

package org.ambraproject.user.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.UserProfile;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Alex Kudlick 2/15/12
 */
public class DisplayUserActionTest extends BaseWebTest{

  @Autowired
  protected DisplayUserAction action;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }

  @DataProvider(name = "user")
  public Object[][] getUser() {
    UserProfile user = new UserProfile();
    user.setAccountUri("id:test-account-uri-forDisplayUserActionTest");
    user.setEmail("email@DisplayUserActionTest.org");
    user.setDisplayName("displayNameForDisplayUserActionTest");
    user.setAuthId("authIdNameForDisplayUserActionTest");
    user.setCity("Storybrooke");
    user.setCountry("USA");
    user.setOrganizationVisibility(false);
    user.setOrganizationName("this should not be displayed unless you're this user or admin");
    dummyDataStore.store(user);
    return new Object[][]{
        {user}
    };
  }

  @Test(dataProvider = "user")
  public void testExecute(UserProfile user) throws Exception {
    action.setUserId(user.getID());
    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getEmail(), user.getEmail(), "Action returned incorrect email");
    assertEquals(action.getDisplayName(), user.getDisplayName(), "Action returned incorrect display name");
    assertEquals(action.getCity(), user.getCity(), "Action returned incorrect city");
    assertEquals(action.getCountry(), user.getCountry(), "Action returned incorrect country");
    assertNull(action.getOrganizationName(), "action didn't hide organization name");
  }

  @Test(dataProvider = "user")
  public void testExecuteWithAccountUri(UserProfile user) throws Exception {
    action.setUserAccountUri(user.getAccountUri());
    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getEmail(), user.getEmail(), "Action returned incorrect email");
    assertEquals(action.getDisplayName(), user.getDisplayName(), "Action returned incorrect display name");
    assertEquals(action.getCity(), user.getCity(), "Action returned incorrect city");
    assertEquals(action.getCountry(), user.getCountry(), "Action returned incorrect country");
    assertNull(action.getOrganizationName(), "action didn't hide organization name");
  }

  @Test(dataProvider = "user", dependsOnMethods = {"testExecute"})
  public void testExecuteByAdmin(UserProfile user) throws Exception {
    setupAdminContext();
    action.setUserId(user.getID());
    
    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getEmail(), user.getEmail(), "Action returned incorrect email");
    assertEquals(action.getDisplayName(), user.getDisplayName(), "Action returned incorrect display name");
    assertEquals(action.getCity(), user.getCity(), "Action returned incorrect city");
    assertEquals(action.getCountry(), user.getCountry(), "Action returned incorrect country");
    assertEquals(action.getOrganizationName(), user.getOrganizationName(), "action didn't show organization name");
  }

  @Test(dataProvider = "user", dependsOnMethods = {"testExecute"})
  public void testExecuteBySameUser(UserProfile user) throws Exception {

    Map<String,Object> requestAttributes = getDefaultRequestAttributes();
    requestAttributes.put(Constants.AUTH_KEY, user.getAuthId());
    setupContext(requestAttributes);

    action.setUserId(user.getID());

    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getEmail(), user.getEmail(), "Action returned incorrect email");
    assertEquals(action.getDisplayName(), user.getDisplayName(), "Action returned incorrect display name");
    assertEquals(action.getCity(), user.getCity(), "Action returned incorrect city");
    assertEquals(action.getCountry(), user.getCountry(), "Action returned incorrect country");
    assertEquals(action.getOrganizationName(), user.getOrganizationName(), "action didn't show organization name");
  }
}
