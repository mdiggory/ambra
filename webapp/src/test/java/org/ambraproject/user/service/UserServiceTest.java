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

import com.opensymphony.xwork2.Action;
import org.ambraproject.action.BaseActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.Test;
import org.ambraproject.ApplicationException;
import org.ambraproject.BaseTest;
import org.ambraproject.BaseWebTest;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.action.DisplayUserAction;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

public class UserServiceTest extends BaseWebTest {

  @Autowired
  protected UserService userService;

  @Autowired
  protected DisplayUserAction displayUserAction;

  private static final String TEST_EMAIL = "testcase@topazproject.org";
  private static final String REAL_NAME = "Test User";
  private static final String AUTH_ID = "Test AuthID";
  private static final String USERNAME= "USERSERVICE_TEST_USERNAME";
  private static final String DEFAULT_ADMIN_AUTHID = "AdminAuthorizationID";

  private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

  @Test
  public void testCreateAdminUser() throws Exception {
    setupAdminContext();
    final String ADMIN_PREFIX = "admintest";
    final String USER_EMAIL = ADMIN_PREFIX + UserServiceTest.TEST_EMAIL;
    final String REAL_NAME = ADMIN_PREFIX + UserServiceTest.REAL_NAME;
    final String AUTH_ID = ADMIN_PREFIX + UserServiceTest.AUTH_ID;
    final String USERNAME = ADMIN_PREFIX + UserServiceTest.USERNAME;

    final String topazId = createUser(AUTH_ID, USER_EMAIL, USERNAME, REAL_NAME);
    ArrayList<String> topazIds = new ArrayList<String>();
    topazIds.add(topazId);  //add for teardown

    displayUserAction.setUserId(topazId);
    assertEquals(Action.SUCCESS, displayUserAction.execute());
    final AmbraUser pou = displayUserAction.getPou();
    assertEquals(USER_EMAIL, pou.getEmail());
    assertEquals(REAL_NAME, pou.getRealName());
    assertEquals(USERNAME, pou.getDisplayName());

    {
      final String roleId = "admin";
      userService.setRole(topazId, roleId, DEFAULT_ADMIN_AUTHID);
      final String[] returnedRoles = userService.getRole(topazId);
      assertTrue(Arrays.toString(returnedRoles).contains(roleId));
    }

    {
      final String[] roles = new String[]{"role1", "role2"};
      userService.setRole(topazId,roles,DEFAULT_ADMIN_AUTHID);
      final String[] returnedRoles = userService.getRole(topazId);
      Arrays.sort(roles);
      Arrays.sort(returnedRoles);
      String s1 =  Arrays.toString(roles);
      String s2 =  Arrays.toString(returnedRoles);
      assertTrue(s1.compareTo(s2) == 0);
    }

    for (final String tId : topazIds) {
      userService.deleteUser(tId, DEFAULT_ADMIN_AUTHID);
    }
  }

  @Test
  public void testCreateUserWithFieldVisibilitySet() throws Exception {
    setupAdminContext();

    final String USER_EMAIL = UserServiceTest.TEST_EMAIL;
    final String REAL_NAME = UserServiceTest.REAL_NAME;
    final String AUTH_ID = UserServiceTest.AUTH_ID;
    final String USERNAME = UserServiceTest.USERNAME;

    final String topazId = createUser(AUTH_ID, USER_EMAIL, USERNAME, REAL_NAME);
    ArrayList<String> topazIds = new ArrayList<String>();
    topazIds.add(topazId);  //add for teardown

    for (final String tId : topazIds) {
      userService.deleteUser(tId,DEFAULT_ADMIN_AUTHID);
    }

  }

  private String createUser(final String AUTH_ID, final String USER_EMAIL, final String USERNAME, final String REAL_NAME) throws ApplicationException, DisplayNameAlreadyExistsException {
    String topazId = userService.createUser(AUTH_ID);
    log.debug("topazId = " + topazId);

    final AmbraUser newUser = new AmbraUser(AUTH_ID);
    newUser.setUserId(topazId);
    newUser.setEmail(USER_EMAIL);
    newUser.setDisplayName(USERNAME);
    newUser.setRealName(REAL_NAME);

    userService.setProfile(newUser, true);
    return topazId;
  }

  @Override
  protected BaseActionSupport getAction() {
    return displayUserAction;
  }
}
