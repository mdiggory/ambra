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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.topazproject.ambra.registration.User;
import org.topazproject.ambra.service.DuplicateLoginNameException;
import org.topazproject.ambra.service.UserDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Hibernate based implementation of the UserDAO.
 */
public class HibernateUserDAO extends HibernateDaoSupport implements UserDAO {
  private static final Log log = LogFactory.getLog(HibernateUserDAO.class);

  /**
   * @see UserDAO#saveOrUpdate(org.topazproject.ambra.registration.User)
   */
  public void saveOrUpdate(final User user) {
    getHibernateTemplate().saveOrUpdate(user);
  }

  /**
   * @see UserDAO#delete(org.topazproject.ambra.registration.User)
   */
  public void delete(final User user) {
     getHibernateTemplate().delete(user);
  }

  /**
   * If more than one user is found it throws an Exception.
   * @see UserDAO#findUserWithLoginName(String)
   */
  public User findUserWithLoginName(final String loginName) {
    return (User) getHibernateTemplate().execute(
      new HibernateCallback(){
        public Object doInHibernate(final Session session) throws HibernateException, SQLException {
          final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(User.class);
          detachedCriteria.add(Restrictions.sqlRestriction
            ("lower(loginName) = lower(?)", loginName, Hibernate.STRING));

          final List list = getHibernateTemplate().findByCriteria(detachedCriteria);

          if (list.size() > 1) {
            final DuplicateLoginNameException duplicateLoginNameException
                     = new DuplicateLoginNameException(loginName);

            log.error("DuplicateLoginName:"+loginName, duplicateLoginNameException);
            throw duplicateLoginNameException;
          }

          if (list.isEmpty()) {
            return null;
          }
          return (User) list.get(0);
        }
      });
  }
}
