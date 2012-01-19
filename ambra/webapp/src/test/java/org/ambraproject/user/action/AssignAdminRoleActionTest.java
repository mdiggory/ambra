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
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;
import org.topazproject.ambra.models.UserAccount;
import org.topazproject.ambra.models.UserProfile;
import org.ambraproject.user.service.UserService;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 9/12/11
 */
public class AssignAdminRoleActionTest extends BaseWebTest {

  @Autowired
  protected AssignAdminRoleAction assignAdminRoleAction;

  @Autowired
  protected UserService userService;

  @DataProvider(name = "savedUser")
  public Object[][] getSavedUser() {
    UserProfile userProfile = new UserProfile();
    userProfile.setId(URI.create("id:test-user-123"));
    dummyDataStore.store(userProfile);
    UserAccount userAccount = new UserAccount();
    userAccount.setProfile(userProfile);
    userAccount.setId(URI.create("id:test-user-account-123"));
    dummyDataStore.store(userAccount);

    return new Object[][]{
        {userAccount.getId().toString()}
    };
  }

  @Test(dataProvider = "savedUser", groups = {ADMIN_GROUP})
  public void testAssignAdminRoleAction(String userId) throws Exception {
    setupAdminContext();
    assignAdminRoleAction.setTopazId(userId);
    assertEquals(assignAdminRoleAction.execute(), Action.SUCCESS, "execute didn't return success");
    assertEquals(userService.getRole(userId)[0], Constants.ADMIN_ROLE, "stored user didn't have admin role");
  }

}
