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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import org.ambraproject.user.AmbraUser;
import java.util.Collection;

/**
 * Simple class to display a user based on a TopazId
 * 
 * @author Stephen Cheng
 * 
 */
public class DisplayUserAction extends UserActionSupport {
  private static final Logger log = LoggerFactory.getLogger(DisplayUserAction.class);

  private AmbraUser pou;
  private String userId;
  private Collection<String> privateFields;

  /**
   * Returns the user based on the userId passed in.
   * 
   * @return webwork status string
   */
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("retrieving user profile for: " + userId);
    }
    pou = new AmbraUserDecorator(userService.getUserWithProfileLoaded(userId));

    // the user has indicated that they do not want their additional information displayed
    if (pou.getOrganizationVisibility() == false) {

      String authId = this.getAuthId();
      boolean isAdminUser = false;
      if (authId != null) {
        isAdminUser = userService.allowAdminAction(authId);
      }

      if (isAdminUser || pou.getAuthId().equals(authId)) {
        // if you are an admin user, you can see everything
        // if you are looking at your own profile, you can see everything.
      } else {
        // hide the list of additional information fields
        // organization address
        // organization type
        // organization name
        // your role

        pou.setOrganizationType(null);
        pou.setOrganizationName(null);
        pou.setPostalAddress(null);
        pou.setPositionType(null);
      }
    }

    return SUCCESS;
  }

  /**
   * @return Returns the pou.
   */
  public AmbraUser getPou() {
    return pou;
  }

  /**
   * @param pou
   *          The pou to set.
   */
  public void setPou(AmbraUser pou) {
    this.pou = pou;
  }

  /**
   * @return Returns the userId.
   */
  @RequiredStringValidator(message = "Topaz id is required.")
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId
   *          The userId to set.
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return the field names that are private.
   */
  public Collection<String> getPrivateFields() {
    return privateFields;
  }
}
