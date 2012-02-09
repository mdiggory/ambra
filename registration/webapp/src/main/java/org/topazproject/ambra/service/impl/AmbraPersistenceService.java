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
package org.topazproject.ambra.service.impl;

import org.topazproject.ambra.service.PersistenceService;
import org.topazproject.ambra.service.UserDAO;

/**
 * Ambra implementation of the Persistence Service.
 */
public class AmbraPersistenceService implements PersistenceService {
  private UserDAO userDAO;

  /**
   * @see PersistenceService#setUserDAO(UserDAO)
   */
  public void setUserDAO(final UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * @see org.topazproject.ambra.service.PersistenceService#getUserDAO() 
   */
  public UserDAO getUserDAO() {
    return userDAO;
  }
}
