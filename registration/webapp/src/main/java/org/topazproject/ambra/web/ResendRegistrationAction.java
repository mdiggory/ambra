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

import org.topazproject.ambra.service.NoUserFoundWithGivenLoginNameException;
import org.topazproject.ambra.service.RegistrationMailer;
import org.topazproject.ambra.service.RegistrationService;
import org.topazproject.ambra.service.UserAlreadyVerifiedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action that takes in a user's login id and looks him up.  If he is found, then the
 * address confirmation email is sent.  If the user is already verified, it will inform
 * the user.
 * 
 * @author stevec
 *
 */
public class ResendRegistrationAction extends BaseAction {
  private static final Log log = LogFactory.getLog(ResendRegistrationAction.class);

  private String loginName;

  private RegistrationService registrationService;
  private RegistrationMailer registrationVerificationMailer;

  public String execute() throws Exception {
    try {
      registrationService.sendRegistrationEmail(loginName);
    } catch (final NoUserFoundWithGivenLoginNameException noUserEx) {
      final String message = "No user found for the given e-mail address: " + loginName;
      addActionError(noUserEx.getMessage());
      log.trace(message, noUserEx);
      addFieldError("loginName", message);
      return INPUT;
    } catch (final UserAlreadyVerifiedException uave) {
      if (log.isDebugEnabled()) {
        log.debug("User " + loginName + " is already verified.");
      }
      addFieldError("loginName", loginName + " has already been verified.");
      return INPUT;
    }
    return SUCCESS;
  }

  /**
   * @return Returns the loginName.
   */
  @RequiredStringValidator(message="E-mail address is required")
  public String getLoginName() {
    return loginName;
  }

  /**
   * @param loginName The loginName to set.
   */
  public void setLoginName(String loginName) {
    if (loginName != null) {
      loginName = loginName.trim();
    }
    this.loginName = loginName;
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
