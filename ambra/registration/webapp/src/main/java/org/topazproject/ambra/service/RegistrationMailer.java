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
package org.topazproject.ambra.service;

import org.topazproject.ambra.registration.User;

public interface RegistrationMailer {

  /**
   * Send a email address verification email to the user
   * @param user user
   */
  public void sendEmailAddressVerificationEmail(final User user);

  /**
   * Send a email address verification email to the user's new email address
   * @param user user
   */
  public void sendNewLoginVerificationEmail(final User user);

  /**
   * Send a forgot password verification email to the user
   * @param user user
   */
  public void sendForgotPasswordVerificationEmail(final User user);
}
