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

import org.apache.struts2.json.annotations.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.user.service.UserService;


/**
 * Base class for user actions in order to have a userService object accessible
 * 
 * @author Stephen Cheng
 * 
 */
public class UserActionSupport extends BaseSessionAwareActionSupport {
  private static final Logger log = LoggerFactory.getLogger(UserActionSupport.class);

  protected UserService userService;

  /**
   * @param userService
   *          The userService to set.
   */
  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }
}
