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

package org.ambraproject.action;

import org.apache.struts2.interceptor.SessionAware;

import static org.ambraproject.Constants.AMBRA_USER_KEY;
import static org.ambraproject.Constants.RECENT_SEARCHES_KEY;

import org.ambraproject.user.AmbraUser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Struts2 action support class that is session aware.
 *
 * @author Dragisa krsmanovic
 */

public class BaseSessionAwareActionSupport extends BaseActionSupport implements SessionAware {
  protected Map session;

  public void setSession(Map map) {
    session = map;
  }

  /**
   * Get currently logged in user
   *
   * @return Logged in user object
   */
  protected AmbraUser getCurrentUser() {
    return (AmbraUser) session.get(AMBRA_USER_KEY);
  }

  /**
   * From the HTTP Session, get the searches performed by this user.
   * Each element in the returned Map has a key which is the link text (to be displayed to the user)
   * and a value which is the URL of that link.
   * @return The searches performed by this user
   */
  protected LinkedHashMap<String, String> getRecentSearches() {
    if (session.get(RECENT_SEARCHES_KEY) == null) {
      session.put(RECENT_SEARCHES_KEY, new LinkedHashMap<String, String>());
    }
    return (LinkedHashMap<String, String>) session.get(RECENT_SEARCHES_KEY);
  }

  /**
   * Add a new Recent Search to the Map of Recent Searches stored in HTTP Session Scope.
   * @param displayText The link text which will be displayed to the user
   * @param url The URL which will be executed when the user clicks on the displayText
   */
  protected void addRecentSearch(String displayText, String url) {
    getRecentSearches().put(displayText, url);
  }
}
