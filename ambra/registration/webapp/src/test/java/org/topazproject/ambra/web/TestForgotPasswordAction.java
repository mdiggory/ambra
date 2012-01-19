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
import org.topazproject.ambra.web.ConfirmationAction;
import org.topazproject.ambra.web.ForgotPasswordAction;

import com.opensymphony.xwork2.Action;

public class TestForgotPasswordAction extends BaseAmbraRegistrationTestCase {
  public void testShouldFailToAcceptForgotPasswordEmailAsItIsNotRegistered()
    throws Exception {
    final String email = "viru-forgot-password-not-registered@home.com";

    final ForgotPasswordAction forgotPasswordAction = getForgotPasswordAction();
    forgotPasswordAction.setLoginName(email);
    assertEquals(Action.INPUT, forgotPasswordAction.execute());
  }

  public void testShouldSendEmailForForgotPasswordEmailEvenIfTheEmailItIsNotVerified()
    throws Exception {
    final String email = "viru-forgot-password-not-verified-yet@home.com";

    assertEquals(Action.SUCCESS, createUser(email, "virupasswd"));
    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);
    assertFalse(beforeVerificationUser.isVerified());

    final ForgotPasswordAction forgotPasswordAction = getForgotPasswordAction();
    forgotPasswordAction.setLoginName(email);
    assertEquals(Action.SUCCESS, forgotPasswordAction.execute());
    assertTrue(forgotPasswordAction.getActionErrors().isEmpty());
  }

  public void testShouldAcceptForgotPasswordRequestIfItIsNotActive() throws Exception {
    final String email = "viru-forgot-password-not-active-yet@home.com";
    createUser(email, "virupasswd");
    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);
    assertTrue(beforeVerificationUser.isActive());

    final ForgotPasswordAction forgotPasswordAction = getForgotPasswordAction();
    forgotPasswordAction.setLoginName(email);
    assertEquals(Action.SUCCESS, forgotPasswordAction.execute());
    assertTrue(forgotPasswordAction.getActionErrors().isEmpty());
  }

  public void testShouldSendEmailForForgotPasswordEmailIfTheEmailIsVerifiedAndActive()
    throws Exception {
    final String email = "viru-forgot-password-verified-and-active@home.com";
    createUser(email, "virupasswd");

    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    confirmationAction.setEmailVerificationToken(beforeVerificationUser.getEmailVerificationToken());

    assertEquals(Action.SUCCESS, confirmationAction.execute());

    final ForgotPasswordAction forgotPasswordAction = getForgotPasswordAction();
    forgotPasswordAction.setLoginName(email);
    assertEquals(Action.SUCCESS, forgotPasswordAction.execute());
    assertTrue(forgotPasswordAction.getActionErrors().isEmpty());
  }

  public void testShouldSendFailToVerifyForgotPasswordTokenIfItIsWrong() throws Exception {
    final String email = "viru-forgot-password-verified-and-active-number2@home.com";
    createUser(email, "virupasswd");

    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    confirmationAction.setEmailVerificationToken(beforeVerificationUser.getEmailVerificationToken());
    assertEquals(Action.SUCCESS, confirmationAction.execute());
    assertTrue(confirmationAction.getActionErrors().isEmpty());

    final ForgotPasswordAction forgotPasswordAction = getForgotPasswordAction();
    forgotPasswordAction.setLoginName(email);
    assertEquals(Action.SUCCESS, forgotPasswordAction.execute());
    assertTrue(forgotPasswordAction.getActionErrors().isEmpty());

    final User forgotPasswordUser = getRegistrationService().getUserWithLoginName(email);
    assertNotNull(forgotPasswordUser.getResetPasswordToken());
    assertTrue(forgotPasswordUser.getResetPasswordToken().length() > 0);
  }
}
