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
package org.topazproject.ambra;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.password.PasswordDigestService;
import org.topazproject.ambra.web.ChangePasswordAction;
import org.topazproject.ambra.web.ConfirmationAction;
import org.topazproject.ambra.web.ForgotPasswordAction;
import org.topazproject.ambra.web.RegisterAction;

/**
 * Base test case for the registration unit tests. It provides spring injection from one of its
 * superclasses.
 *
 */
public abstract class BaseAmbraRegistrationTestCase
  extends AbstractDependencyInjectionSpringContextTests {

  protected RegistrationService registrationService;
  private ConfirmationAction confirmationAction;
  private RegisterAction registerAction;
  private PasswordDigestService passwordDigestService;
  private ChangePasswordAction changePasswordAction;

  protected String[] getConfigLocations() {
    return new String[] {"testApplicationContext.xml"};
  }

  public final void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  protected ConfirmationAction getConfirmationAction() {
    return confirmationAction;
  }

  public void setConfirmationAction(final ConfirmationAction confirmationAction) {
    this.confirmationAction = confirmationAction;
  }

  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  protected RegisterAction getRegistrationAction() {
    return registerAction;
  }

  public void setRegisterAction(final RegisterAction registerAction) {
    this.registerAction = registerAction;
  }

  protected ForgotPasswordAction getForgotPasswordAction() {
    final ForgotPasswordAction forgotPasswordAction = new ForgotPasswordAction();
    forgotPasswordAction.setRegistrationService(getRegistrationService());
    return forgotPasswordAction;
  }

  public void setPasswordDigestService(final PasswordDigestService passwordDigestService) {
    this.passwordDigestService = passwordDigestService;
  }

  protected PasswordDigestService getPasswordDigestService() {
    return passwordDigestService;
  }

  protected ChangePasswordAction getChangePasswordAction() {
    return changePasswordAction;
  }

  public void setChangePasswordAction(final ChangePasswordAction changePasswordAction) {
    this.changePasswordAction = changePasswordAction;
  }

  protected final String createUser(final String email, final String password) throws Exception {
    final RegisterAction registerAction = getRegistrationAction();
    registerAction.setLoginName1(email);
    registerAction.setPassword1(password);
    registerAction.setPassword2(password);
    return registerAction.execute();
  }
}
