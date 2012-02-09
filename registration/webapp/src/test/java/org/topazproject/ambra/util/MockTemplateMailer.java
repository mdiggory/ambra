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
package org.topazproject.ambra.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.topazproject.ambra.registration.User;
import org.topazproject.ambra.service.RegistrationMailer;

import java.util.Map;

public class MockTemplateMailer implements RegistrationMailer {
  private RegistrationMailer mailer;

  public void setMailer(RegistrationMailer mailer) {
    this.mailer = mailer;
  }

  public void mail(final String toEmailAddress, final String subject,
      final Map<String, Object> context, final String textTemplateFilename,
      final String htmlTemplateFilename) {
  }

  public void massMail(final Map<String, Map<String, Object>> emailAddressContextMap,
      final String subject, final String textTemplateFilename,
      final String htmlTemplateFilename) {
  }

  public void sendEmailAddressVerificationEmail(final User user) {
  }

  public void sendNewLoginVerificationEmail(User user) {
    mailer.sendNewLoginVerificationEmail(user);
  }

  public void sendForgotPasswordVerificationEmail(final User user) {
  }

  public void setFreemarkerConfig(final FreeMarkerConfigurer freemarkerConfig) {
  }

  public void setFromEmailAddress(final String fromEmailAddress) {
  }

  public void setMailSender(final JavaMailSender mailSender) {
  }

  public void setTextTemplateFilename(final String textTemplateFilename) {
  }

  public void setHtmlTemplateFilename(final String htmlTemplateFilename) {
  }

  public void setVerifyEmailMap(final Map<String, String> verifyEmailMap) {
  }

  public Map<String, String> getVerifyEmailMap() {
    return null;
  }

  public void setForgotPasswordVerificationEmailMap
    (final Map<String, String> forgotPasswordVerificationEmailMap) {
  }
}
