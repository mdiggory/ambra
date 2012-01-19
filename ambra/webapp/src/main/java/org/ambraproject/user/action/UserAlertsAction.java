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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.user.AmbraUser;
import org.ambraproject.user.service.UserAlert;
import org.ambraproject.ApplicationException;

/**
 * Update action for saving or getting alerts that the user subscribes to.
 */
@SuppressWarnings("serial")
public abstract class UserAlertsAction extends UserActionSupport {
  private static final Logger log = LoggerFactory.getLogger(UserAlertsAction.class);

  private String displayName;
  private String[] monthlyAlerts = new String[]{};
  private String[] weeklyAlerts = new String[]{};
  private static final String MONTHLY_ALERT_SUFFIX = "_monthly";
  private static final String WEEKLY_ALERT_SUFFIX = "_weekly";
  private static final String ALERTS_CATEGORIES_CATEGORY = "ambra.userAlerts.categories.category";
  private static final String ALERTS_WEEKLY = "ambra.userAlerts.weekly";
  private static final String ALERTS_MONTHLY = "ambra.userAlerts.monthly";


  /**
   * Stubs optional user alert data data which may be specified in the configuration. <br>
   *
   * Config FORMAT EXAMPLE:<br>
   *
   * <pre>
   * &lt;userAlerts&gt;
   *   &lt;categories&gt;
   *     &lt;category key=&quot;biology&quot;&gt;PLoS Biology&lt;/category&gt;
   *     &lt;category key=&quot;computational_biology&quot;&gt;PLoS Computational Biology&lt;/category&gt;
   *     &lt;category key=&quot;clinical_trials&quot;&gt;PLoS Hub for Clinical Trials&lt;/category&gt;
   *     &lt;category key=&quot;genetics&quot;&gt;PLoS Genetics&lt;/category&gt;
   *     &lt;category key=&quot;medicine&quot;&gt;PLoS Medicine&lt;/category&gt;
   *     &lt;category key=&quot;pathogens&quot;&gt;PLoS Pathogens&lt;/category&gt;
   *     &lt;category key=&quot;plosntds&quot;&gt;PLoS Neglected Tropical Diseases&lt;/category&gt;
   *     &lt;category key=&quot;plosone&quot;&gt;PLoS ONE&lt;/category&gt;
   *     &lt;/categories&gt;
   *     &lt;monthly&gt;biology, clinical_trials, computational_biology, genetics, medicine, pathogens, plosntds&lt;/monthly&gt;
   *     &lt;weekly&gt;biology, clinical_trials, computational_biology, genetics, medicine, pathogens, plosntds, plosone&lt;/weekly&gt;
   * &lt;/userAlerts&gt;
   * </pre>
   * @return All available user alerts
   */
  @SuppressWarnings("unchecked")
  public Collection<UserAlert> getUserAlerts() {
    ArrayList<UserAlert> alerts = new ArrayList<UserAlert>();

    final Map<String, String> categoryNames = new HashMap<String, String>();

    HierarchicalConfiguration hc = (HierarchicalConfiguration) configuration;
    List<HierarchicalConfiguration> categories = hc.configurationsAt(ALERTS_CATEGORIES_CATEGORY);
    for (HierarchicalConfiguration c : categories) {
      String key = c.getString("[@key]");
      String value = c.getString("");
      categoryNames.put(key, value);
    }

    final String[] weeklyCategories = hc.getStringArray(ALERTS_WEEKLY);
    final String[] monthlyCategories = hc.getStringArray(ALERTS_MONTHLY);

    final Set<Map.Entry<String, String>> categoryNamesSet = categoryNames.entrySet();

    for (final Map.Entry<String, String> category : categoryNamesSet) {
      final String key = category.getKey();
      boolean weeklyCategoryKey = false;
      boolean monthlyCategoryKey = false;
      if (ArrayUtils.contains(weeklyCategories, key)) {
        weeklyCategoryKey = true;
      }
      if (ArrayUtils.contains(monthlyCategories, key)) {
        monthlyCategoryKey = true;
      }
      alerts.add(new UserAlert(key, category.getValue(), weeklyCategoryKey, monthlyCategoryKey));
    }
    return alerts;
  }

  /**
   * Save the alerts.
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String saveAlerts() throws Exception {
    final AmbraUser ambraUser = getAmbraUserToUse();
    if (ambraUser == null) {
      throw new ServletException("Unable to resolve ambra user");
    }

    final Collection<String> alertsList = new ArrayList<String>();
    for (final String alert : monthlyAlerts) {
      if (log.isDebugEnabled()) {
        log.debug("found monthly alert: " + alert);
      }
      alertsList.add(alert + MONTHLY_ALERT_SUFFIX);
    }
    for (final String alert : weeklyAlerts) {
      if (log.isDebugEnabled()) {
        log.debug("found weekly alert: " + alert);
      }
      alertsList.add(alert + WEEKLY_ALERT_SUFFIX);
    }

    final String[] alerts = alertsList.toArray(new String[alertsList.size()]);
    ambraUser.setAlerts(alerts);

    userService.setPreferences(ambraUser);

    return SUCCESS;
  }

  /**
   * Retrieve the alerts for the logged in user
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(readOnly = true)
  public String retrieveAlerts() throws Exception {
    final AmbraUser ambraUser = getAmbraUserToUse();
    if (ambraUser == null) {
      throw new ServletException("Unable to resolve ambra user");
    }

    final Collection<String> monthlyAlertsList = new ArrayList<String>();
    final Collection<String> weeklyAlertsList = new ArrayList<String>();

    final String[] alerts = ambraUser.getAlerts();

    if (null != alerts) {
      for (final String alert : alerts) {
        if (log.isDebugEnabled()) {
          log.debug("Alert: " + alert);
        }
        if (alert.endsWith(MONTHLY_ALERT_SUFFIX)) {
          monthlyAlertsList.add(alert.substring(0, alert.indexOf(MONTHLY_ALERT_SUFFIX)));
        } else if (alert.endsWith(WEEKLY_ALERT_SUFFIX)) {
          weeklyAlertsList.add(alert.substring(0, alert.indexOf(WEEKLY_ALERT_SUFFIX)));
        }
      }
    }

    monthlyAlerts = monthlyAlertsList.toArray(new String[monthlyAlertsList.size()]);
    weeklyAlerts = weeklyAlertsList.toArray(new String[weeklyAlertsList.size()]);

    setDisplayName(ambraUser.getDisplayName());
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

  /**
   * Provides a way to get the AmbraUser to edit
   * @return the AmbraUser to edit
   * @throws ApplicationException ApplicationException
   */
  protected abstract AmbraUser getAmbraUserToUse() throws ApplicationException;

}
