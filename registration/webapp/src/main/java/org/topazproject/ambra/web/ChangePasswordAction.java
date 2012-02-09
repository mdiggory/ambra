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
 * 
 */
package org.topazproject.ambra.web;

import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.ambra.ApplicationException;
import org.topazproject.ambra.service.NoUserFoundWithGivenLoginNameException;
import org.topazproject.ambra.service.PasswordInvalidException;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.UserNotVerifiedException;

import static org.topazproject.ambra.Constants.Length.PASSWORD_MAX;
import static org.topazproject.ambra.Constants.Length.PASSWORD_MIN;

/**
 * Change the password action.
 */
public class ChangePasswordAction extends BaseAction {
  private String loginName;
  private String oldPassword;
  private String newPassword1;
  private String newPassword2;

  private RegistrationService registrationService;
  private static final Log log = LogFactory.getLog(ChangePasswordAction.class);

  public String execute() throws Exception {
    try {
      registrationService.changePassword(loginName, oldPassword, newPassword1);

    } catch (final NoUserFoundWithGivenLoginNameException e) {
      final String message = "No user found with given e-mail address:"+ loginName;
      addFieldError("loginName", message);
      log.trace(message, e);
      return INPUT;
    } catch (final PasswordInvalidException e) {
      final String message = "Invalid password entered";
      addFieldError("oldPassword", message);
      log.trace(message + "for loginName" + loginName, e);
      return INPUT;
    } catch (final UserNotVerifiedException e) {
      final String message = "User not verified:" + loginName;
      addFieldError("loginName", message);
      log.trace(message, e);
      return INPUT;
    } catch (final ApplicationException e) {
      addFieldError("loginName", e.getMessage());
      addActionError(e.getMessage());
      log.error("Application error", e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * @return loginName
   */
  @RegexFieldValidator(message = "You must enter a valid e-mail", expression = EMAIL_REGEX)
  @RequiredStringValidator(message="You must enter an e-mail address")
  public String getLoginName() {
    return loginName;
  }

  /**
   * Set loginName
   * @param loginName loginName
   */
  public void setLoginName(String loginName) {
    if (loginName != null) {
      loginName = loginName.trim();
    }
    this.loginName = loginName;
  }

  /**
   * @return oldPassword
   */
  @RequiredStringValidator(message="You must enter your original password")
  public String getOldPassword() {
    return oldPassword;
  }

  /**
   * Set oldPassword
   * @param oldPassword oldPassword
   */
  public void setOldPassword(final String oldPassword) {
    this.oldPassword = oldPassword;
  }

  /**
   * @return newPassword1 newPassword1
   */
  @RequiredStringValidator(message="You must enter a new password", shortCircuit=true)
  @FieldExpressionValidator(fieldName= "newPassword1", expression= "newPassword1==newPassword2",
      message="New passwords must match", shortCircuit=true)
  @StringLengthFieldValidator(minLength= PASSWORD_MIN, maxLength = PASSWORD_MAX,
      message="Password length must be between " + PASSWORD_MIN + " and "
      + PASSWORD_MAX, shortCircuit=true)
  public String getNewPassword1() {
    return newPassword1;
  }

  /**
   * Set newPassword1
   * @param newPassword1 newPassword1
   */
  public void setNewPassword1(final String newPassword1) {
    this.newPassword1 = newPassword1;
  }

  /**
   * @return newPassword2
   */
  public String getNewPassword2() {
    return newPassword2;
  }

  /**
   * Set newPassword2
   * @param newPassword2 newPassword2
   */
  public void setNewPassword2(final String newPassword2) {
    this.newPassword2 = newPassword2;
  }

  /**
   * sets the registration service.  used by Spring
   * 
   * @param registrationService
   */
  public void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }
}
