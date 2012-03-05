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
package org.ambraproject.user.action;

import org.ambraproject.models.UserProfile;
import org.ambraproject.user.service.UserAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Update action for saving or getting alerts that the user subscribes to.
 */
@SuppressWarnings("serial")
public abstract class UserAlertsAction extends UserActionSupport {
  private static final Logger log = LoggerFactory.getLogger(UserAlertsAction.class);

  private String displayName;
  private String[] monthlyAlerts = new String[]{};
  private String[] weeklyAlerts = new String[]{};

  /**
   * Subclasses must override this to provide the id of the user being edited
   *
   * @return the id of the user to edit
   */
  protected abstract String getUserAuthId();

  public Collection<UserAlert> getUserAlerts() {
    return userService.getAvailableAlerts();
  }

  /**
   * Save the alerts.
   *
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(rollbackFor = {Throwable.class})
  public String saveAlerts() throws Exception {
    final String authId = getUserAuthId();
    if (authId == null) {
      throw new ServletException("Unable to resolve ambra user");
    }
    userService.setAlerts(authId, Arrays.asList(monthlyAlerts), Arrays.asList(weeklyAlerts));
    return SUCCESS;
  }

  /**
   * Retrieve the alerts for the logged in user
   *
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(readOnly = true)
  public String retrieveAlerts() throws Exception {
    final String authId = getUserAuthId();
    if (authId == null) {
      throw new ServletException("Unable to resolve ambra user");
    }

    final UserProfile user = userService.getUserByAuthId(authId);
    final List<String> monthlyAlertsList = user.getMonthlyAlerts();
    final List<String> weeklyAlertsList = user.getWeeklyAlerts();

    monthlyAlerts = monthlyAlertsList.toArray(new String[monthlyAlertsList.size()]);
    weeklyAlerts = weeklyAlertsList.toArray(new String[weeklyAlertsList.size()]);
    displayName = user.getDisplayName();

    return SUCCESS;
  }

  /**
   * @return categories that have monthly alerts
   */
  public String[] getMonthlyAlerts() {
    return monthlyAlerts;
  }

  /**
   * Set the categories that have monthly alerts
   *
   * @param monthlyAlerts monthlyAlerts
   */
  public void setMonthlyAlerts(final String[] monthlyAlerts) {
    this.monthlyAlerts = monthlyAlerts;
  }

  /**
   * @return weekly alert categories
   */
  public String[] getWeeklyAlerts() {
    return weeklyAlerts;
  }

  /**
   * Set weekly alert categories
   *
   * @param weeklyAlerts weeklyAlerts
   */
  public void setWeeklyAlerts(String[] weeklyAlerts) {
    this.weeklyAlerts = weeklyAlerts;
  }

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @param displayName The displayName to set.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}
