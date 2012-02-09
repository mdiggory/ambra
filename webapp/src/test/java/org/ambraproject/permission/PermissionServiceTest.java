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

import org.springframework.beans.factory.annotation.Autowired;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.annotation.service.ReplyService;
import org.topazproject.ambra.models.*;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.user.AmbraUser;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


public class PermissionServiceTest extends BaseTest{

  @Autowired
  PermissionsService permissionsService;

  final String ADMIN = "admin";
  final String OTHER_ROLE = "otherrole";
  final String AUTHID = "authIdUri";
  final String AUTHID_VALUE = "testvalue";
  final String USER_ROLE_ID = "testroleuri";

  @DataProvider(name = "loginData")
  public Object[][] loginData() throws UnsupportedEncodingException {
    AmbraUser user = new AmbraUser("testuser");

    return new Object[][]{
     {user}
    };
  }

   /**
   * Test for permissionservice.checklLogin()
   *
   * @param user AmbraUser
   */

  @Test(dataProvider = "loginData")
  public void testCheckLogin(AmbraUser user){
    try{
      permissionsService.checkLogin(user.getAuthId());
    }catch (SecurityException se){
      fail("user is null");
    }
  }

  @DataProvider(name = "roleData")
  public Object[][] roleData(){

    Set<AuthenticationId> ai = new HashSet<AuthenticationId>();

    AuthenticationId authId = new AuthenticationId();
    authId.setId(URI.create(AUTHID));
    authId.setValue(AUTHID_VALUE);
    dummyDataStore.store(authId);

    ai.add(authId);

    Set<UserRole> ur = new HashSet<UserRole>();

    UserRole userRole = new UserRole();
    userRole.setId(URI.create(USER_ROLE_ID));
    userRole.setRole(ADMIN);
    dummyDataStore.store(userRole);

    ur.add(userRole);

    UserAccount ua = new UserAccount();
    ua.setRoles(ur);
    ua.setAuthIds(ai);

    dummyDataStore.store(ua);

    return new Object[][]{
        {ua}
    };
  }

  /**
   * Test for permissionservice.checkRole()
   *
   * @param ua UserAccount
   */

  @Test(dataProvider = "roleData")
  public void testCheckRoleForAdminRole(UserAccount ua){
    try {
      for(AuthenticationId authId: ua.getAuthIds()){
        permissionsService.checkRole(ADMIN, authId.getValue());  //should not throw security exception
      }
    }catch (SecurityException se){
      fail("this user doesn't have required role");
    }
  }

  /**
   * Test for permissionservice.checkRole()
   * @param ua
   * @throws SecurityException
   */
  @Test(dataProvider = "roleData", expectedExceptions = { SecurityException.class } )
  public void testCheckRoleForOtherRole(UserAccount ua){
    for(AuthenticationId authId: ua.getAuthIds()){
        permissionsService.checkRole(OTHER_ROLE, authId.getValue());
      }

  }

}
