/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.permission;

import org.ambraproject.BaseTest;
import org.ambraproject.models.UserRole;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;


public class PermissionServiceTest extends BaseTest {

  @Autowired
  protected PermissionsService permissionsService;
  @Autowired
  protected UserService userService;

  @Test
  public void testCheckRoleOnAdmin() {
    //ensure that the admin auth id is a user
    assertNotNull(userService.getUserByAuthId(DEFAULT_ADMIN_AUTHID), "Admin auth id was not a user");
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, DEFAULT_ADMIN_AUTHID);
  }

  @Test(expectedExceptions = {SecurityException.class})
  public void testCheckRoleOnNonAdmin() {
    //ensure that the user auth id is a user
    assertNotNull(userService.getUserByAuthId(DEFUALT_USER_AUTHID),"user auth id was not a user");
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, DEFUALT_USER_AUTHID);
  }

  @Test(expectedExceptions = {SecurityException.class})
  public void testCheckNewRole() {
    UserRole role = new UserRole("some new role");
    dummyDataStore.store(role);

    permissionsService.checkRole(role.getRoleName(), DEFAULT_ADMIN_AUTHID);
  }

  @Test
  public void testCheckLogin() {
    try {
      permissionsService.checkLogin(null);
      fail("Permission Service didn't throw exception on null login");
    } catch (SecurityException e) {
      //expected
    }
    permissionsService.checkLogin(DEFAULT_ADMIN_AUTHID);
  }
}
