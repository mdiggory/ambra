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

import com.opensymphony.xwork2.Action;

public class TestConfirmationAction extends BaseAmbraRegistrationTestCase {
  public void testShouldSetUserAsVerified() throws Exception {
    final String email = "viru-verifying@home.com";
    final String password = "virupasswd";
    final User beforeVerificationUser = getRegistrationService().createUser(email, password);
    assertFalse(beforeVerificationUser.isVerified());

    final String emailVerificationToken = beforeVerificationUser.getEmailVerificationToken();
    assertNotNull(emailVerificationToken);
    assertTrue(emailVerificationToken.length() > 0);

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    confirmationAction.setEmailVerificationToken(emailVerificationToken);
    assertEquals(Action.SUCCESS, confirmationAction.execute());

    final User verifiedUser = getRegistrationService().getUserWithLoginName(email);
    assertTrue(verifiedUser.isVerified());
  }

  public void testShouldNotVerifyUserAsVerificationTokenIsInvalid() throws Exception {
    final String email = "viru-verifying-another-time@home.com";
    final User beforeVerificationUser = getRegistrationService().createUser(email, "virupasswd");

    assertFalse(beforeVerificationUser.isVerified());
    final String emailVerificationToken = beforeVerificationUser.getEmailVerificationToken();

    assertNotNull(emailVerificationToken);
    assertTrue(emailVerificationToken.length() > 0);

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    //change the verification token
    confirmationAction.setEmailVerificationToken(emailVerificationToken+"11");
    assertEquals(Action.ERROR, confirmationAction.execute());

    final User verifiedUser = getRegistrationService().getUserWithLoginName(email);
    assertFalse(verifiedUser.isVerified());
  }

  public void testVerifyUserShouldFailAsLoginNameDoesNotExist() throws Exception {
    final String email = "viru-verifying-a-loginnamethatdoes-notexist@home.com";

    assertNull(getRegistrationService().getUserWithLoginName(email));

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    confirmationAction.setEmailVerificationToken("emailVerificationToken");
    assertEquals(Action.ERROR, confirmationAction.execute());
  }

  public void testShouldGiveErrorMessageAsUserIsAlreadyVerified() throws Exception {
    final String email = "viru-verifying-again@home.com";
    final String password = "virupasswd";

    createUser(email, password);
    final User beforeVerificationUser = getRegistrationService().getUserWithLoginName(email);
    assertFalse(beforeVerificationUser.isVerified());

    final String emailVerificationToken = beforeVerificationUser.getEmailVerificationToken();
    assertNotNull(emailVerificationToken);
    assertTrue(emailVerificationToken.length() > 0);

    final ConfirmationAction confirmationAction = getConfirmationAction();
    confirmationAction.setLoginName(email);
    confirmationAction.setEmailVerificationToken(emailVerificationToken);
    assertEquals(Action.SUCCESS, confirmationAction.execute());

    //try to verify the email address again
    assertEquals(Action.SUCCESS, confirmationAction.execute());

    final User verifiedUser = getRegistrationService().getUserWithLoginName(email);
    assertTrue(verifiedUser.isVerified());
  }
}
