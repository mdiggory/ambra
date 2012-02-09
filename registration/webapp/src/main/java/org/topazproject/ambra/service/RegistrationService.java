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
import org.topazproject.ambra.service.password.PasswordServiceException;

/**
 * Provides a means for a user account creation, password change, user verification, user activation
 */
public interface RegistrationService {
  /**
   * Create user.
   * @param loginName loginName
   * @param password password
   * @return created user
   * @throws UserAlreadyExistsException UserAlreadyExistsException
   * @throws org.topazproject.ambra.service.password.PasswordServiceException PasswordServiceException
   */
  User createUser(final String loginName, final String password) throws UserAlreadyExistsException,
                  PasswordServiceException;

  /**
   * Change password.
   * @param loginName loginName
   * @param password password
   * @param newLogin newLogin
   * @throws PasswordInvalidException PasswordInvalidException
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   * @throws org.topazproject.ambra.service.password.PasswordServiceException PasswordServiceException
   * @throws UserAlreadyExistsException UserAlreadyExistsException
   */
  void changeLogin (final String loginName, final String password, final String newLogin)
    throws NoUserFoundWithGivenLoginNameException, PasswordInvalidException, PasswordServiceException,
           UserAlreadyExistsException;

  /**
   * Will send an email with an address verification link in it if the user has not already been verified
   * 
   * @param loginName username of the user for whom a registration mail should be sent
   * @throws NoUserFoundWithGivenLoginNameException
   * @throws UserAlreadyVerifiedException
   */
  void sendRegistrationEmail (final String loginName) throws NoUserFoundWithGivenLoginNameException,
       UserAlreadyVerifiedException;

  /**
   * Get user with loginName
   * @param loginName loginName
   * @return user
   */
  User getUserWithLoginName(final String loginName);

  /**
   * Set the user as verified.
   * @param user user
   */
  void setVerified(final User user);

  /**
   * Deactivate the user.
   * @param user user
   */
  void deactivate(final User user);

  /**
   * Verify the users account
   * @param loginName loginName
   * @param emailVerificationToken emailVerificationToken
   * @throws VerificationTokenInvalidException VerificationTokenInvalidException
   * @throws UserAlreadyVerifiedException UserAlreadyVerifiedException
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   */
  void verifyUser(final String loginName, final String emailVerificationToken)
    throws VerificationTokenInvalidException, UserAlreadyVerifiedException,
           NoUserFoundWithGivenLoginNameException;

  /**
   * Verify the users account and changes the login to newLoginName
   * @param loginName loginName
   * @param emailVerificationToken emailVerificationToken
   * @throws VerificationTokenInvalidException VerificationTokenInvalidException
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   * @throws UserAlreadyExistsException UserAlreadyExistsException
   */
  void verifyChangeUser(final String loginName, final String emailVerificationToken)
    throws VerificationTokenInvalidException, NoUserFoundWithGivenLoginNameException,
           UserAlreadyExistsException;

  /**
   * Send a forgot password message.
   * @param loginName loginName
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   */
  void sendForgotPasswordMessage(final String loginName) throws NoUserFoundWithGivenLoginNameException;

  /**
   * Change password.
   * @param loginName loginName
   * @param oldPassword oldPassword
   * @param newPassword newPassword
   * @throws PasswordInvalidException PasswordInvalidException
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   * @throws UserNotVerifiedException UserNotVerifiedException
   * @throws org.topazproject.ambra.service.password.PasswordServiceException PasswordServiceException
   */
  void changePassword(final String loginName, final String oldPassword, final String newPassword)
    throws NoUserFoundWithGivenLoginNameException, PasswordInvalidException, UserNotVerifiedException,
           PasswordServiceException;

  /**
   * Reset the user's password to a new one.
   * @param loginName login name
   * @param resetPasswordToken reset password token
   * @param newPassword new password
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   * @throws VerificationTokenInvalidException VerificationTokenInvalidException
   * @throws org.topazproject.ambra.service.password.PasswordServiceException PasswordServiceException
   */
  void resetPassword(final String loginName, final String resetPasswordToken, final String newPassword)
    throws NoUserFoundWithGivenLoginNameException, VerificationTokenInvalidException,
           PasswordServiceException;

  /**
   * Return the user with the given loginName and resetPasswordToken
   * @param loginName loginName
   * @param resetPasswordToken resetPasswordToken
   * @return User
   * @throws NoUserFoundWithGivenLoginNameException NoUserFoundWithGivenLoginNameException
   * @throws VerificationTokenInvalidException VerificationTokenInvalidException
   */
  User getUserWithResetPasswordToken(final String loginName, final String resetPasswordToken)
    throws NoUserFoundWithGivenLoginNameException, VerificationTokenInvalidException;
}
