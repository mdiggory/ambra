/* $HeadURL$
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
package org.ambraproject.model;

import java.io.Serializable;

import java.net.URI;

import org.topazproject.otm.Rdf;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * Just the full name.
 */
@Entity(types = {"foaf:Person"}, graph = "profiles")
public class UserProfileInfo implements Serializable {
  private URI id;
  private String realName;

  /**
   * Get id.
   *
   * @return id as URI.
   */
  public URI getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id the value to set.
   */
  @Id
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get realName.
   *
   * @return realName as String.
   */
  public String getRealName() {
    return realName;
  }

  /**
   * Set realName.
   *
   * @param realName the value to set.
   */
  @Predicate(ref="FoafPerson:realName")
  public void setRealName(String realName) {
    this.realName = realName;
  }
}
