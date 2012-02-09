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
package org.ambraproject.user.service;

/**
 * UserAlert - Encapsulates a single user alert entry as defined in the configuration in the
 * optional "ambra.userAlerts" section.
 */
public class UserAlert {
  private final String key;
  private final String name;
  private final boolean weeklyAvailable;
  private final boolean monthlyAvailable;

  /**
   * Constructor
   * @param key
   * @param name
   * @param weeklyAvailable
   * @param monthlyAvailable
   */
  public UserAlert(final String key, final String name, final boolean weeklyAvailable,
                   final boolean monthlyAvailable) {
    this.key = key;
    this.name = name;
    this.weeklyAvailable = weeklyAvailable;
    this.monthlyAvailable = monthlyAvailable;
  }

  /**
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * @return The key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Is available monthly?
   * @return true/false
   */
  public boolean isMonthlyAvailable() {
    return monthlyAvailable;
  }

  /**
   * Is available weekly?
   * @return true/false
   */
  public boolean isWeeklyAvailable() {
    return weeklyAvailable;
  }
}
