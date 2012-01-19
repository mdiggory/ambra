/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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
package org.ambraproject.models;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.testng.annotations.AfterClass;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base Test for simple model tests (just to see that hibernate mappping files are valid).
 * <p/>
 * Maintains a session for subclasses to use.
 *
 * @author Alex Kudlick 11/8/11
 */
public abstract class BaseHibernateTest {

  //The session factory creates an in-memory db on creation,so we only need one
  private static final SessionFactory sessionFactory;
  protected final HibernateTemplate hibernateTemplate;

  static {
    //turn off foreign keys for testing
    sessionFactory = new Configuration().configure().buildSessionFactory();
    Session session = sessionFactory.openSession();
    session.doWork(new Work() {
      @Override
      public void execute(Connection connection) throws SQLException {
        connection.createStatement().execute("SET REFERENTIAL_INTEGRITY FALSE;");
      }
    });
    session.close();
  }

  public BaseHibernateTest() {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }
}
