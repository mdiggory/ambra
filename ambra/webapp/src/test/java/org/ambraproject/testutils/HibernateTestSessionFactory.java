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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.configuration.ConfigurationStore;
import org.topazproject.ambra.models.AuthenticationId;
import org.topazproject.ambra.models.UserAccount;
import org.topazproject.ambra.models.UserProfile;
import org.topazproject.ambra.models.UserRole;
import org.ambraproject.permission.service.PermissionsService;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;


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
      UserAccount ua = new UserAccount();
      ua.setId(URI.create("AdminAccountID"));

      UserRole ur = new UserRole();
      ur.setRole(PermissionsService.ADMIN_ROLE);
      ua.getRoles().add(ur);
      UserProfile up = new UserProfile();
      up.setRealName("Foo user");
      ua.setProfile(up);
      ua.getAuthIds().add(new AuthenticationId(BaseTest.DEFAULT_ADMIN_AUTHID));

      hibernateTemplate.save(ua);

      // Create a dummy joe blow user
      ua = new UserAccount();
      ua.setId(URI.create("DummyTestUserID"));
      up = new UserProfile();
      up.setRealName("Dummy user");
      up.setEmailFromString("testcase@topazproject.org");
      up.setCity("my city");
      ua.setProfile(up);
      ua.getAuthIds().add(new AuthenticationId(BaseTest.DEFUALT_USER_AUTHID));

      hibernateTemplate.save(ua);

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
