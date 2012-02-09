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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.ambra.service.NoUserFoundWithGivenLoginNameException;
import org.topazproject.ambra.service.PasswordInvalidException;
import org.topazproject.ambra.service.RegistrationMailer;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.UserAlreadyExistsException;
import org.topazproject.ambra.service.password.PasswordServiceException;

import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;

/**
 * @author stevec
 *
 */
public class ChangeEmailAction extends BaseAction {
  private String login;
  private String password;
  private String newLogin1;
  private String newLogin2;

  private RegistrationMailer registrationVerificationMailer;
  private RegistrationService registrationService;
  private static final Log log = LogFactory.getLog(ResendRegistrationAction.class);

  public String execute() throws Exception {
    try {
      registrationService.changeLogin(login, password, newLogin1);
    } catch (NoUserFoundWithGivenLoginNameException noUserFoundEx) {
      if (log.isDebugEnabled()) {
        log.debug ("No user found with login: " + login, noUserFoundEx);
      }
      addFieldError("login", "No user found for this e-mail/password pair");
      return INPUT;
    } catch (PasswordInvalidException pie) {
      if (log.isDebugEnabled()) {
        log.debug ("No user found with login: " + login + " and password", pie);
      }
      addFieldError("login", "No user found for this e-mail/password pair");
      return INPUT;
    } catch (PasswordServiceException pse) {
      if (log.isDebugEnabled()) {
        log.debug ("Unable to check password for user: + login", pse);
      }
      addFieldError("login", "Error checking password.");
      return ERROR;
    } catch (UserAlreadyExistsException uaee) {
      if (log.isDebugEnabled()) {
        log.debug ("User with login: " + newLogin1 + " already exists", uaee);
      }
      addFieldError("newLogin1", newLogin1 + " is already in use.");
      return INPUT;
    }
    return SUCCESS;
  }

  /**
   * @return Returns the login.
   */
  @RequiredStringValidator(message="You must enter your original e-mail address")
  public String getLogin() {
    return login;
  }

  /**
   * @param login The login to set.
   */
  public void setLogin(String login) {
    if (login != null) {
      login = login.trim();
    }
    this.login = login;
  }

  /**
   * @return Returns the newLogin1.
   */
  @RegexFieldValidator(message = "You must enter a valid e-mail", expression = EMAIL_REGEX)
  @RequiredStringValidator(message="You must enter an e-mail address")
  @FieldExpressionValidator(fieldName="newLogin2", expression = "newLogin1==newLogin2",
      message="E-mail addresses must match")
  @StringLengthFieldValidator(maxLength = "256",
      message="E-mail address must be less than 256 characters")
  public String getNewLogin1() {
    return newLogin1;
  }

  /**
   * @param newLogin1 The newLogin1 to set.
   */
  public void setNewLogin1(String newLogin1) {
    if (newLogin1 != null) {
      newLogin1 = newLogin1.trim();
    }
    this.newLogin1 = newLogin1;
  }

  /**
   * @return Returns the newLogin2.
   */
  public String getNewLogin2() {
    return newLogin2;
  }

  /**
   * @param newLogin2 The newLogin2 to set.
   */
  public void setNewLogin2(String newLogin2) {
    if (newLogin2 != null) {
      newLogin2 = newLogin2.trim();
    }
    this.newLogin2 = newLogin2;
  }

  /**
   * @return Returns the password.
   */
  @RequiredStringValidator(message="You must enter a password", shortCircuit=true)
  public String getPassword() {
    return password;
  }

  /**
   * @param password The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @param registrationService The registrationService to set.
   */
  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  /**
   * @return Returns the registrationMailer.
   */
  public RegistrationMailer getRegistrationVerificationMailer() {
    return registrationVerificationMailer;
  }

  /**
   * @param registrationMailer The registrationMailer to set.
   */
  public void setRegistrationVerificationMailer(RegistrationMailer registrationMailer) {
    this.registrationVerificationMailer = registrationMailer;
  }
}
