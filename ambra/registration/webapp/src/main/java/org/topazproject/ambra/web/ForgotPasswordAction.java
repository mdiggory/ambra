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

/**
 * Used when a user makes a forgot password request.
 */
public class ForgotPasswordAction extends BaseAction {
  private static final Log log = LogFactory.getLog(ForgotPasswordAction.class);

  private RegistrationService registrationService;
  private String loginName;

  public String execute() throws Exception {
    try {
      registrationService.sendForgotPasswordMessage(loginName);
    } catch (final NoUserFoundWithGivenLoginNameException noUserEx) {
      final String message = "No user found for the given e-mail address:" + loginName;
      addActionError(noUserEx.getMessage());
      log.trace(message, noUserEx);
      addFieldError("loginName", message);
      return INPUT;
    } catch (final ApplicationException e) {
      addActionError(e.getMessage());
      log.error(e, e);
      addFieldError("loginName", e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * @return loginName
   */
  @RegexFieldValidator(message = "You must enter a valid e-mail", fieldName="loginName",
      expression = EMAIL_REGEX)
  @RequiredStringValidator(message="E-mail address is required")
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

  /**
   * Set registrationService
   * @param registrationService registrationService
   */
  public void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }
}
