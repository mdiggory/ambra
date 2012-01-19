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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.topazproject.otm.CascadeType;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * A user's preferences.
 *
 * <p>Modelling note: when OTM supports maps, the prefs should be changed to a Map.
 *
 * @author Ronald Tschalär
 */
@Entity(graph = "preferences")
public class UserPreferences implements Serializable {
  private static final long serialVersionUID = -3668646514603553808L;

  private URI                 id;
  private String              appId;
  private Set<UserPreference> prefs = new HashSet<UserPreference>();

  /**
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  @Id @GeneratedValue(uriPrefix = "id:preferences/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the application id.
   *
   * @return the application id
   */
  public String getAppId() {
    return appId;
  }

  /**
   * Set the application id.
   *
   * @param appId the application id to set.
   */
  @Predicate(uri = "dcterms:mediator")
  public void setAppId(String appId) {
    this.appId = appId;
  }

  /**
   * Get the preferences.
   *
   * @return the preferences.
   */
  public Set<UserPreference> getPrefs() {
    return prefs;
  }

  /**
   * Set the preferences.
   *
   * @param prefs the preferences.
   */
  @Predicate(uri = "topaz:preference", cascade = {CascadeType.child})
  public void setPrefs(Set<UserPreference> prefs) {
    this.prefs = prefs;
  }

  /** 
   * Get the preferences in form of a map. 
   * 
   * @return the preferences
   */
  public Map<String, String[]> getPrefsAsMap() {
    Map<String, String[]> res = new HashMap<String, String[]>();

    for (UserPreference p : getPrefs())
      res.put(p.getName(), p.getValues());

    return res;
  }

  /** 
   * Set the preferences in form of a map. 
   * 
   * @param prefs the preferences
   */
  public void setPrefsFromMap(Map<String, String[]> prefs) {
    getPrefs().clear();

    for (Map.Entry<String, String[]> e : prefs.entrySet())
      this.prefs.add(new UserPreference(e.getKey(), e.getValue()));
  }
}
