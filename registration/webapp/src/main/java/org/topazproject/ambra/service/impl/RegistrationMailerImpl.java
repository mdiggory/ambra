/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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
package org.topazproject.ambra.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.topazproject.ambra.email.impl.FreemarkerTemplateMailer;
import org.topazproject.ambra.registration.User;
import org.topazproject.ambra.service.RegistrationMailer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegistrationMailerImpl extends FreemarkerTemplateMailer implements RegistrationMailer {
  private Map<String, String> verifyEmailMap;
  private Map<String, String> changeEmailMap;
  private Map<String, String> forgotPasswordVerificationEmailMap;
  private static final Log log = LogFactory.getLog(RegistrationMailerImpl.class);

  /**
   * Setter for verifyEmailMap.
   * @param verifyEmailMap verifyEmailMap
   */
  public void setVerifyEmailMap(final Map<String, String> verifyEmailMap) {
    this.verifyEmailMap = Collections.unmodifiableMap(verifyEmailMap);
  }

  /**
   * Setter for changeEmailMap.
   * @param changeEmailMap changeEmailMap
   */
  public void setChangeEmailMap(final Map<String, String> changeEmailMap) {
    this.changeEmailMap = Collections.unmodifiableMap(changeEmailMap);
  }

  /**
   * Setter for forgotPasswordVerificationEmailMap.
   * @param forgotPasswordVerificationEmailMap Value to set for forgotPasswordVerificationEmailMap.
   */
  public void setForgotPasswordVerificationEmailMap
    (final Map<String, String> forgotPasswordVerificationEmailMap) {
    this.forgotPasswordVerificationEmailMap = forgotPasswordVerificationEmailMap;
  }

  /**
   * Send a email address verification email to the user
   * @param user user
   */
  public void sendEmailAddressVerificationEmail(final User user) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(verifyEmailMap);
    newMapFields.put("user", user);
    newMapFields.put("name", getFromEmailName());
    if (log.isDebugEnabled()) {
      log.debug("sending email address verification for " + ((user != null) ? user.getLoginName() : null));
    }
    sendEmail(user.getLoginName(), newMapFields);
  }

  /**
   * Send a email address verification email to the user's new email address
   * @param user user
   */
  public void sendNewLoginVerificationEmail(final User user) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(changeEmailMap);
    newMapFields.put("user", user);
    newMapFields.put("name", getFromEmailName());
    if (log.isDebugEnabled()) {
      log.debug("sending change email address verification for " +
                ((user != null) ? user.getNewLoginName() : null));
    }
    sendEmail(user.getNewLoginName(), newMapFields);
  }

  /**
   * Send a forgot password verification email to the user
   * @param user user
   */
  public void sendForgotPasswordVerificationEmail(final User user) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(forgotPasswordVerificationEmailMap);
    newMapFields.put("user", user);
    newMapFields.put("name", getFromEmailName());
    if (log.isDebugEnabled()) {
      log.debug("sending forgot password email for " + ((user != null) ? user.getLoginName() : null));
    }
    sendEmail(user.getLoginName(), newMapFields);
  }
}
