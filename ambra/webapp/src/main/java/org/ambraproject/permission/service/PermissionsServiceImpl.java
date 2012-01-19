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
package org.ambraproject.permission.service;

import org.apache.struts2.ServletActionContext;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.user.UserAccountsInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * A simple role based permissions service
 *
 * @author Joe Osowski
 */
public class PermissionsServiceImpl extends HibernateServiceImpl implements PermissionsService {
  private static final Logger log = LoggerFactory.getLogger(PermissionsServiceImpl.class);

  /**
   * Creates a new PermissionsService object.
   *
   */
  public PermissionsServiceImpl() {
  }


  /**
   * Does the user associated with the current security principle have the given role?
   * @param role
   * @return
   */
  public void checkRole(final String role, final String authId) throws SecurityException
  {
    try {
      hibernateTemplate.execute(new HibernateCallback() {
        public Object doInHibernate(Session session) throws HibernateException, SQLException {

          //TODO: Load the role(s) associated with the user
          Query q = session.createSQLQuery("select " +
            "r.roleUri " +
            "from " +
            "AuthenticationId a " +
            "join UserAccountAuthIdJoinTable au on au.authenticationIdUri = a.authenticationIdUri " +
            "join UserAccountRoleJoinTable ur on ur.userAccountUri = au.userAccountUri " +
            "join UserRole r on r.userRoleUri = ur.roleUri " +
            "where a.value = :authId")
            .setString("authId", authId);

          List<String> results = q.list();

          //If any of this user's roles match the passed in role, don't throw an exception and just return
          for(String curRole : results) {
            if(role.equals(curRole)) {
              return null;
            }
          }

          throw new SecurityException("Current user does not have defined role of " + role);
        }
      });
    } catch (Exception ex) {
      //If ANYTHING fails, make sure it's casted as a security exception so the front end handles it
      throw new SecurityException(ex.getMessage(), ex);
    }
  }

  public void checkLogin(String authId) throws SecurityException
  {
    if(authId != null) {
      return;
    }

    throw new SecurityException("Current user is not logged in");
  }
}
