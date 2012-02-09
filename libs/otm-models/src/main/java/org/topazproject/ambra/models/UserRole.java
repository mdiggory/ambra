/* $HeadURL::                                                                                     $
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

package org.topazproject.ambra.models;

import java.io.Serializable;
import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * A user's role.
 *
 * @author Ronald Tschalär
 */
@Entity(graph = "users")
public class UserRole implements Serializable {
  private static final long serialVersionUID = 5354238965669244098L;

  private URI    id;
  private String role;

  /**
   * Create new empty role.
   */
  public UserRole() {
  }

  /**
   * Create new role given role.
   *
   * @param role the role
   */
  public UserRole(String role) {
    this.role = role;
  }

  /**
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  @Id @GeneratedValue(uriPrefix = "id:roles/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get role.
   *
   * @return role as String.
   */
  public String getRole() {
    return role;
  }

  /**
   * Set role.
   *
   * @param role the value to set.
   */
  @Predicate(uri = "topaz:role")
  public void setRole(String role) {
    this.role = role;
  }
}
