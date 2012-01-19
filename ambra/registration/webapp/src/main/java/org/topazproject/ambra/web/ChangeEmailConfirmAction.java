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

import org.topazproject.ambra.ApplicationException;
import org.topazproject.ambra.service.NoUserFoundWithGivenLoginNameException;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.VerificationTokenInvalidException;

import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;

/**
 * @author stevec
 *
 */
public class ChangeEmailConfirmAction extends BaseAction {
  private String emailVerificationToken;
  private String loginName;
  private RegistrationService registrationService;

  private static final Log log = LogFactory.getLog(ChangeEmailConfirmAction.class);

  public String execute() throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("Verifying change email request for user: " + loginName + " with token: "
          + emailVerificationToken);
    }
    try {
      registrationService.verifyChangeUser(loginName, emailVerificationToken);
    } catch (final VerificationTokenInvalidException e) {
      final String message = "Verification token invalid: " +
        emailVerificationToken+", e-mail: " + loginName;
      addActionError(message);
      if (log.isTraceEnabled()) {
        log.trace(message, e);
      }
      return ERROR;
    } catch (final NoUserFoundWithGivenLoginNameException e) {
      final String message = "No user found with given e-mail address: "+ loginName;
      addActionError(message);
      addFieldError("login", message);
      if (log.isTraceEnabled()) {
        log.trace(message, e);
      }
      return ERROR;
    } catch (final ApplicationException e) {
      addActionError(e.getMessage());
      addFieldError("loginName", e.getMessage());
      if (log.isWarnEnabled()) {
        log.warn(e, e);
      }
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Get registrationService
   * @return registrationService
   */
  public RegistrationService getRegistrationService() {
    return this.registrationService;
  }

  /**
   * Set registrationService
   * @param registrationService registrationService
   */
  public void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  /**
   * @return emailVerificationToken
   */
  @RequiredStringValidator(message="Verification token missing")
  public String getEmailVerificationToken() {
    return emailVerificationToken;
  }

  /**
   * Set emailVerificationToken
   * @param emailVerificationToken emailVerificationToken
   */
  public void setEmailVerificationToken(final String emailVerificationToken) {
    this.emailVerificationToken = emailVerificationToken;
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
   * Set loginName
   * @param loginName loginName
   */
  public void setLoginName(final String loginName) {
    this.loginName = loginName;
  }
}
