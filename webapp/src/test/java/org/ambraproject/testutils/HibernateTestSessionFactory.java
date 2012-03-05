/*
 * $HeadURL$
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

package org.ambraproject.testutils;

import org.ambraproject.BaseTest;
import org.ambraproject.models.UserProfile;
import org.ambraproject.models.UserRole;
import org.ambraproject.permission.service.PermissionsService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.topazproject.ambra.configuration.ConfigurationStore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;


/**
 * Session Factory to use for hibernate unit tests.  This allows us to hook in to the session factory to create default
 * users, and turn off foreign keys
 *
 * @author Alex Kudlick Date: 5/4/11
 *         <p/>
 *         org.topazproject.ambra
 */
public class HibernateTestSessionFactory extends LocalSessionFactoryBean {


  public HibernateTestSessionFactory() {
    super();
    System.setProperty(ConfigurationStore.SYSTEM_OBJECT_ID_PREFIX, "test:doi/0.0/");
  }


  @Override
  public void afterPropertiesSet() throws IOException {
    super.afterPropertiesSet();
    try {
      HibernateTemplate hibernateTemplate = new HibernateTemplate((SessionFactory) getObject());
      // Create an admin user to test admin functions
      UserRole adminRole = new UserRole(PermissionsService.ADMIN_ROLE);
      hibernateTemplate.save(adminRole);

      UserProfile admin = new UserProfile();
      admin.setAuthId(BaseTest.DEFAULT_ADMIN_AUTHID);
      admin.setEmail("admin@test.org");
      admin.setDisplayName("testAdmin");
      admin.setRoles(new HashSet<UserRole>(1));
      admin.getRoles().add(adminRole);
      hibernateTemplate.save(admin);


      UserProfile nonAdmin = new UserProfile();
      nonAdmin.setAuthId(BaseTest.DEFUALT_USER_AUTHID);
      nonAdmin.setEmail("nonAdmin@test.org");
      nonAdmin.setDisplayName("testNonAdmin");
      hibernateTemplate.save(nonAdmin);
      //save the default journal
      hibernateTemplate.save(BaseTest.defaultJournal);

      //turn off foreign keys
      hibernateTemplate.execute(new HibernateCallback() {
        @Override
        public Object doInHibernate(Session session) throws HibernateException, SQLException {
          session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
              connection.createStatement().execute("SET REFERENTIAL_INTEGRITY FALSE;");
            }
          });
          return null;
        }
      });
    } catch (DataAccessException ex) {
      //must've already inserted the users
    }
  }
}
