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

/**
 * A simple role based permissions service
 *
 * If needed at a later point in time we might add permissions to roles and add
 * a hasPermissions method here and make roles transparent.  But being we only have
 * one role (admin) it's overkill for now
 *
 * @author Joe Osowski
 */
public interface PermissionsService {
  public static String ADMIN_ROLE = "admin";
  /**
   * Does the user associated with the current security principle have the given role?
   * @param role
   * @return
   */
  public void checkRole(String role, String authId) throws SecurityException;

  public void checkLogin(String authId) throws SecurityException;
}
