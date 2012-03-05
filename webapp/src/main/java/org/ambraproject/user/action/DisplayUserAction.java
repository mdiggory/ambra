/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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

import org.ambraproject.models.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple class to display a user based on a TopazId
 * 
 * @author Stephen Cheng
 * 
 */
public class DisplayUserAction extends UserProfileAction {
  private static final Logger log = LoggerFactory.getLogger(DisplayUserAction.class);

  private Long userId;

  //TODO: Fetch users by id and not uri.  We need this b/c annotations still have a reference to the account uri
  private String userAccountUri;

  //We don't need this since we're overriding execute
  @Override
  protected String getUserAuthId() {
    return null;
  }

  /**
   * Returns the user based on the userId passed in.
   * 
   * @return webwork status string
   */
  @Transactional(readOnly = true)
  @Override
  public String execute() throws Exception {
    UserProfile user;

    if (userId != null) {
      user = userService.getUser(userId);
    } else {
      user = userService.getUserByAccountUri(userAccountUri);
      
    }

    final String authId = this.getAuthId();

    // check if the user wants to show private fields
    boolean showPrivateFields = user.getOrganizationVisibility();
    if (!showPrivateFields) {
      //if they said no, still show them to admins and that same user
      showPrivateFields = userService.allowAdminAction(authId) || user.getAuthId().equals(authId);
    }

    setFieldsFromProfile(userService.getProfileForDisplay(user, showPrivateFields));
    return SUCCESS;
  }

  /**
   * @param userId
   *          The userId to set.
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUserAccountUri(String userAccountUri) {
    this.userAccountUri = userAccountUri;
  }
}
