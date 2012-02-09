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

import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.ambra.ApplicationException;
import org.topazproject.ambra.service.NoUserFoundWithGivenLoginNameException;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.VerificationTokenInvalidException;

import static org.topazproject.ambra.Constants.Length.PASSWORD_MAX;
import static org.topazproject.ambra.Constants.Length.PASSWORD_MIN;

/**
 * Used to present the user with reset password facility after having forgotten their password.
 */
public class ForgotPasswordChangePasswordAction extends BaseAction {
  private static final Log log = LogFactory.getLog(ForgotPasswordChangePasswordAction.class);

  private String loginName;
  private String resetPasswordToken;
  private String password1;
  private String password2;

  private RegistrationService registrationService;

  public String execute() throws Exception {
    try {
      if (validatePassword()) {
        registrationService.resetPassword(loginName, resetPasswordToken, password1);
      } else {
        return INPUT;
      }
    } catch (final NoUserFoundWithGivenLoginNameException e) {
      addActionError("No user found with the login name");
      return INPUT;
    } catch (final VerificationTokenInvalidException e) {
      addActionError("Verification token is invalid");
      return ERROR;
    } catch (final ApplicationException e) {
      log.warn("Error changing password", e);
      addFieldError("password1",  e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Validate the reset password request.
   * @return {@link #SUCCESS} if request is valid, else {@link #ERROR}
   * @throws Exception Exception
   */
  public String validateResetPasswordRequest() throws Exception {
    try {
      registrationService.getUserWithResetPasswordToken(loginName, resetPasswordToken);
    } catch (final NoUserFoundWithGivenLoginNameException e) {
      addActionError("No user found with the login name");
      return ERROR;
    } catch (final VerificationTokenInvalidException e) {
      addActionError("Verification token is invalid");
      return ERROR;
    } catch (final ApplicationException e) {
      log.warn("Error validating password request", e);
      addFieldError("password1",  e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * @return loginName
   */
  @RegexFieldValidator(message = "You must enter a valid e-mail", fieldName="loginName",
      expression = EMAIL_REGEX)
  @RequiredStringValidator(message="E-mail address not specified")
  public String getLoginName() {
    return loginName;
  }

  /**
   * Set loginName.
   * @param loginName loginName
   */
  public void setLoginName(final String loginName) {
    this.loginName = loginName;
  }

  /**
   * @return the reset password token
   */
  @RequiredStringValidator(message="Verification token missing")
  public String getResetPasswordToken() {
    return resetPasswordToken;
  }

  /**
   * Set resetPasswordToken.
   * @param resetPasswordToken token used to verify the reset the password request.
   */
  public void setResetPasswordToken(final String resetPasswordToken) {
    this.resetPasswordToken = resetPasswordToken;
  }

  private boolean validatePassword() {
    if (null == password1) {
      addFieldError("password1",  "Password is required");
      return false;
    } else if (!password1.equals(password2)) {
      addFieldError("password1",  "Passwords must match");
      return false;
    } else {
      final int passwordLength = password1.length();
      if (passwordLength < Integer.parseInt(PASSWORD_MIN) ||
          passwordLength > Integer.parseInt(PASSWORD_MAX)) {
        addFieldError("password1",  "Password length must be between " + PASSWORD_MIN +
            " and " + PASSWORD_MAX);
        return false;
      }
    }

    return true;
  }

  /**
   * @return password1
   */
  public String getPassword1() {
    return password1;
  }

  /**
   * Set password1
   * @param password1 password1
   */
  public void setPassword1(final String password1) {
    this.password1 = password1;
  }

  /**
   * @return password2
   */
  public String getPassword2() {
    return password2;
  }

  /**
   * Set password2
   * @param password2 password2
   */
  public void setPassword2(final String password2) {
    this.password2 = password2;
  }

  /**
   * Set registrationService
   * @param registrationService registrationService
   */
  public void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }
}
