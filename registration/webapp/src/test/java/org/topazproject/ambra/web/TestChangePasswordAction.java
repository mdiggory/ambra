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
package org.topazproject.ambra.web;

import org.topazproject.ambra.BaseAmbraRegistrationTestCase;
import org.topazproject.ambra.registration.User;
import org.topazproject.ambra.web.ChangePasswordAction;

import com.opensymphony.xwork2.Action;

public class TestChangePasswordAction extends BaseAmbraRegistrationTestCase {

  public void testShouldChangeUserPassword() throws Exception {
    final String email = "user-changing-their-oldPassword@home.com";
    final String oldPassword = "changethispassword";

    createUser(email, oldPassword);

    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);
    getRegistrationService().verifyUser(email, beforeVerificationUser.getEmailVerificationToken());

    final ChangePasswordAction changePasswordAction = getChangePasswordAction();
    changePasswordAction.setLoginName(email);
    changePasswordAction.setOldPassword(oldPassword);
    changePasswordAction.setNewPassword1("new"+oldPassword);
    assertEquals(Action.SUCCESS, changePasswordAction.execute());
    assertEquals(0, changePasswordAction.getFieldErrors().size());
  }

  public void testShouldFailToChangeUserPasswordIfUserNotVerified() throws Exception {
    final String email = "unverified-user-changing-their-oldPassword@home.com";
    final String oldPassword = "changethispassword";

    createUser(email, oldPassword);

    final ChangePasswordAction changePasswordAction = getChangePasswordAction();
    changePasswordAction.setLoginName(email);
    changePasswordAction.setOldPassword(oldPassword);
    changePasswordAction.setNewPassword1("new"+oldPassword);
    changePasswordAction.setNewPassword2("new"+oldPassword);
    assertEquals(Action.INPUT, changePasswordAction.execute());
    assertEquals(1, changePasswordAction.getFieldErrors().size());
  }

  public void testShouldFailToChangeUserPasswordIfOldPasswordIsWrong() throws Exception {
    final String email = "testShouldFailToChangeUserPasswordIfOldPasswordIsWrong@home.com";
    final String oldPassword = "changethispassword";

    createUser(email, oldPassword);
    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);
    getRegistrationService().verifyUser(email, beforeVerificationUser.getEmailVerificationToken());

    final ChangePasswordAction changePasswordAction = getChangePasswordAction();
    changePasswordAction.setLoginName(email);
    changePasswordAction.setOldPassword(oldPassword+"change");
    changePasswordAction.setNewPassword1("new"+oldPassword);
    changePasswordAction.setNewPassword2("new"+oldPassword);
    assertEquals(Action.INPUT, changePasswordAction.execute());
    assertEquals(1, changePasswordAction.getFieldErrors().size());
  }

  public void testShouldFailToChangeUserPasswordIfUserNotFound() throws Exception {
    final String email = "testShouldFailToChangeUserPasswordIfUserNotFound@home.com";
    final String oldPassword = "changethispassword";

    final ChangePasswordAction changePasswordAction = getChangePasswordAction();
    changePasswordAction.setLoginName(email);
    changePasswordAction.setOldPassword(oldPassword);
    changePasswordAction.setNewPassword1("new"+oldPassword);
    changePasswordAction.setNewPassword2("new"+oldPassword);
    assertEquals(Action.INPUT, changePasswordAction.execute());
    assertEquals(1, changePasswordAction.getFieldErrors().size());
  }
}
