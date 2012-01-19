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

/**
 * Used to indicate that the verification token is invalid.
 */
public class VerificationTokenInvalidException extends Exception {
  /**
   * Constructor with loginName and verificationToken
   * @param loginName loginName
   * @param verificationToken verificationToken
   */
  public VerificationTokenInvalidException(final String loginName, final String verificationToken) {
    super("loginName:" + loginName + ", verificationToken:" + verificationToken);
  }
}
