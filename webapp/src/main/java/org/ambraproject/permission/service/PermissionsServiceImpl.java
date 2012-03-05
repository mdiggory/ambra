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

import org.ambraproject.models.UserProfile;
import org.ambraproject.service.HibernateServiceImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * A simple role based permissions service
 *
 * @author Joe Osowski
 * @author Alex Kudlick
 */
public class PermissionsServiceImpl extends HibernateServiceImpl implements PermissionsService {

  /**
   * Does the user associated with the current security principle have the given role?
   *
   * @param role
   * @return
   */
  public void checkRole(final String role, final String authId) throws SecurityException {
    Long count = (Long) hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(UserProfile.class)
            .add(Restrictions.eq("authId", authId))
            .createCriteria("roles")
            .add(Restrictions.eq("roleName", role))
            .setProjection(Projections.rowCount())
    ).get(0);
    if (count.equals(0l)) {
      throw new SecurityException("Current user does not have the defined role of " + role);
    }
  }

  public void checkLogin(String authId) throws SecurityException {
    if (authId != null) {
      return;
    }

    throw new SecurityException("Current user is not logged in");
  }
}
