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
package org.ambraproject.user.action;

import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.ApplicationException;
import org.ambraproject.user.AmbraUser;

/**
 * User Profile Action to be used by the admin to update the profile of any member user
 * (distinct from the one that might be called by member user themselves to edit their profile)
 */
public class AdminUserProfileAction extends UserProfileAction {
  private boolean userIsHalfCreated = false;

  @Override
  protected AmbraUser getAmbraUserToUse() throws ApplicationException {
    final AmbraUser ambraUser = userService.getUserById(getTopazId());

    //To be used to fix the partial created user profile for a user
    if ("".equals(ambraUser.getEmail())) {
      setDisplayNameRequired(false);
      userIsHalfCreated = true;
    } else if ("".equals(ambraUser.getDisplayName())) {
      setDisplayNameRequired(false);
      setIsDisplayNameSet(false);
    }

    return ambraUser;
  }

  @Override
  @Transactional(readOnly = true)
  public String executeRetrieveUserProfile() throws Exception {
    final String status = super.executeRetrieveUserProfile();
    if (userIsHalfCreated) {
      setEmail(fetchUserEmailAddress());
    }
    return status;
  }

  @Override
  protected String getUserIdToFetchEmailAddressFor() throws ApplicationException {
    return userService.getAuthenticationId(getTopazId());
  }

  /** Using by freemarker to indicate that the user profile is being edited by an admin */
  public boolean getIsEditedByAdmin() {
    return true;
  }
}
