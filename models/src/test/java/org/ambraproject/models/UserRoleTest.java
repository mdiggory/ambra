/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 2/9/12
 */
public class UserRoleTest extends BaseHibernateTest {
  
  @Test
  public void testSaveRole() {
    UserRole role = new UserRole();
    role.setRoleName("admin");
    Serializable id = hibernateTemplate.save(role);

    UserRole storedRole = (UserRole) hibernateTemplate.get(UserRole.class, id);
    assertEquals(storedRole.getRoleName(), "admin", "stored role didn't have correct name");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullRole() {
    hibernateTemplate.save(new UserRole());
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueRoleConstraint() {
    UserRole role1 = new UserRole();
    role1.setRoleName("foo");
    UserRole role2 = new UserRole();
    role2.setRoleName("foo");

    hibernateTemplate.save(role1);
    hibernateTemplate.save(role2);
  }
}
