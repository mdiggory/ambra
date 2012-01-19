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

package org.topazproject.ambra.models;

import java.io.Serializable;
import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/** 
 * This defines a single user preference.
*
 * @author Ronald Tschalär
 */
@Entity(graph = "preferences")
public class UserPreference implements Serializable {
  private static final long serialVersionUID = 6852214623240068537L;

  private URI      id;
  private String   name;
  private String[] values;

  /** 
   * Create an empty user-preference. 
   */
  public UserPreference() {
  }

  /** 
   * Create a new user-preference with the given values. 
   *
   * @param name   the name of the preference
   * @param values the values of the preference 
   */
  public UserPreference(String name, String[] values) {
    this.name   = name;
    this.values = values;
  }

  /**
   * Get the id.
   *
   * @return the id.
   */
  public URI getId() {
    return id;
  }

  /**
   * Set the id.
   *
   * @param id the id.
   */
  @Id @GeneratedValue(uriPrefix = "id:preferences/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the name of the preference.
   *
   * @return the preference name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the preference.
   *
   * @param name the preference name
   */
  @Predicate(uri = "topaz:prefName")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the values of the preference.
   *
   * @return the preference values; may be null. Note that the order of the entries will be
   *         arbitrary.
   */
  public String[] getValues() {
    return values;
  }

  /**
   * Set the values of the preference.
   *
   * @param values the preference values; may be null. Note that the order will not be preserved.
   */
  @Predicate(uri = "topaz:prefValue")
  public void setValues(String[] values) {
    this.values = values;
  }
}
