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

package org.topazproject.ambra.registration;

import java.sql.Timestamp;

/**
 * Interface for Ambra Reistration User
 */
public interface User {
  /**
   * Get the Login Name
   * @return Login Name
   */
  String getLoginName();

  /**
   * Set the Users Login Name
   * @param email  Set User Login Name
   */
  void setLoginName(String email);

  /**
   * Get the Login Name
   * @return Login Name
   */
  String getNewLoginName();

  /**
   * Set the User's replacement Login Name
   * @param email  Set User Login Name
   */
  void setNewLoginName(String email);

  /**
   * Get the User Password
   * @return User Password
   */
  String getPassword();

  /**
   * Set the User Password
   * @param password password
   */
  void setPassword(String password);

  /**
   * Set the User to varified
   * @param  verified verified
   */
  void setVerified(final boolean verified);

  /**
   * Is the user verified
   * @return  Boolean true/false
   */
  boolean isVerified();

  /**
   * Set the User to active
   * @param active active
   */
  void setActive(final boolean active);

  /**
   * Is the user active
   * @return  Boolean true/false
   */
  boolean isActive();

  /**
   * Get the User Id
   * @return User Id
   */
  String getId();

  /**
   * Set the User Id
   * @param id id
   */
  void setId(final String id);

  /**
   * Get the Email Verification Token
   * @return Email Verification Token
   */
  String getEmailVerificationToken();

  /**
   * Set the Email Verification Token
   * @param emailVerificationToken emailVerificationToken
   */
  void setEmailVerificationToken(String emailVerificationToken);

  /**
   * Get the Timestamp the User was created
   * @return Timestamp when the user was created
   */
  Timestamp getCreatedOn();

  /**
   * Set the Timestamp the User was created
   * @param createdOn createdOn
   */
  void setCreatedOn(final Timestamp createdOn);

  /**
   * Get the Timestamp of last update
   * @return Timestamp of last update
   */
  Timestamp getUpdatedOn();

  /**
   * Set the Timestamp of last update
   * @param updatedOn updatedOn
   */
  void setUpdatedOn(final Timestamp updatedOn);

  /**
   * Get the Reset Password Token
   * @return Reset Password Token
   */
  String getResetPasswordToken();

  /**
   * Set the Reset Password Token
   * @param resetPasswordToken resetPasswordToken
   */
  void setResetPasswordToken(final String resetPasswordToken);
}
