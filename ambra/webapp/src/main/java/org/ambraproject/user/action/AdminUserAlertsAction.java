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

import org.ambraproject.ApplicationException;
import org.ambraproject.user.AmbraUser;

/**
 * User Alerts Action that is called by the admin to update a user's alerts preferences
 * (distinct from the one that might be called by a member user to edit preferences)
 */
public class AdminUserAlertsAction extends UserAlertsAction {
  private String topazId;

  /**
   * Setter for topazId.
   * @param topazId Value to set for topazId.
   */
  public void setTopazId(final String topazId) {
    this.topazId = topazId;
  }

  /**
   * Getter for topazId.
   * @return Value of topazId.
   */
  public String getTopazId() {
    return topazId;
  }

  /**
   * Using by freemarker to indicate that the user profile is being edited by an admin
   * @return true
   */
  public boolean getIsEditedByAdmin() {
    return true;
  }

  /**
   * Get user being modified by administrator
   * @return AmbraUser object of the user specified in topazId  
   * @throws ApplicationException
   */
  @Override
  protected AmbraUser getAmbraUserToUse() throws ApplicationException {
    return userService.getUserById(topazId);
  }
}
