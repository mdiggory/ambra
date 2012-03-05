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

/**
 * Class representing a role for a user
 *
 * @author Alex Kudlick 2/9/12
 */
public class UserRole extends AmbraEntity {

  private String roleName;

  public UserRole() {
    super();
  }

  public UserRole(String roleName) {
    this();
    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserRole)) return false;

    UserRole role = (UserRole) o;

    if (roleName != null ? !roleName.equals(role.roleName) : role.roleName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return roleName != null ? roleName.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "UserRole{" +
        "roleName='" + roleName + '\'' +
        '}';
  }
}
