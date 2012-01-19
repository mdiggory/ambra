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

import org.topazproject.ambra.BaseAmbraRegistrationTestCase;
import org.topazproject.ambra.registration.User;
import org.topazproject.ambra.registration.UserImpl;
import org.topazproject.ambra.service.UserDAO;

/**
 *
 */
public class TestHibernate extends BaseAmbraRegistrationTestCase {
  private UserDAO userDao;

  public void testHibernate() {
    userDao.saveOrUpdate(new UserImpl("steve@home.com", "stevec"));
  }

  public void setUserDAO(final UserDAO userDao) {
      this.userDao = userDao;
  }

  public void testDeleteUser() {
    User user = new UserImpl("deleteUser@home.com", "delete");
    userDao.saveOrUpdate(user);
    user = userDao.findUserWithLoginName("deleteUser@home.com");
    assertNotNull(user);
    userDao.delete(user);
    user = userDao.findUserWithLoginName("deleteUser@home.com");
    assertNull(user);
  }

  public void testDeleteUserWithCaseInsensitiveEmailAddressCheck() {
    User user = new UserImpl("deleteUser@home.com", "delete");
    userDao.saveOrUpdate(user);
    user = userDao.findUserWithLoginName("DELETEUSER@HOME.COM");
    assertNotNull(user);
    userDao.delete(user);
    user = userDao.findUserWithLoginName("deleteuser@home.com");
    assertNull(user);
  }
}
