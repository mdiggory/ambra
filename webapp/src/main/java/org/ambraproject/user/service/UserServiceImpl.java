/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

import org.ambraproject.models.ArticleView;
import org.ambraproject.models.UserLogin;
import org.ambraproject.models.UserProfile;
import org.ambraproject.models.UserSearch;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.user.DuplicateDisplayNameException;
import org.ambraproject.util.FileUtils;
import org.ambraproject.util.TextUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.configuration.ConfigurationStore;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Class to roll up web services that a user needs in Ambra. Rest of application should generally
 * use AmbraUser to
 *
 * @author Stephen Cheng
 * @author Joe Osowski
 */
public class UserServiceImpl extends HibernateServiceImpl implements UserService {
  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private static final String ALERTS_CATEGORIES_CATEGORY = "ambra.userAlerts.categories.category";
  private static final String ALERTS_WEEKLY = "ambra.userAlerts.weekly";
  private static final String ALERTS_MONTHLY = "ambra.userAlerts.monthly";

  private PermissionsService permissionsService;
  private Configuration configuration;
  private boolean advancedLogging = false;

  private String emailAddressUrl;
  private String authIdParam;

  @Override
  @Transactional(rollbackFor = {Throwable.class})
  public UserProfile login(final String authId, final UserLogin loginInfo) {
    log.debug("logging in user with auth id {}", authId);
    UserProfile user = getUserByAuthId(authId);
    if (user != null && this.advancedLogging) {
      loginInfo.setUserProfileID(user.getID());
      hibernateTemplate.save(loginInfo);
    }
    return user;
  }

  @Override
  public void updateEmail(Long userId, String email) {
    UserProfile user = (UserProfile) hibernateTemplate.get(UserProfile.class, userId);
    if (user != null) {
      user.setEmail(email);
      hibernateTemplate.update(user);
    }
  }

  @Override
  public UserProfile getUserByAuthId(String authId) {
    log.debug("Attempting to find user with authID: {}", authId);
    try {
      return (UserProfile) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(UserProfile.class)
              .add(Restrictions.eq("authId", authId))
          , 0, 1).get(0);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Didn't find user for authID: {}", authId);
      return null;
    }
  }

  @Override
  public UserProfile getUserByAccountUri(String accountUri) {
    if (accountUri == null) {
      throw new IllegalArgumentException("provided null account uri");
    }
    try {
      log.debug("Loading user with account uri: {}", accountUri);
      return (UserProfile) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(UserProfile.class)
              .add(Restrictions.eq("accountUri", accountUri)),
          0, 1).get(0);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Didn't find user for accountURI: {}", accountUri);
      return null;
    }
  }

  @Override
  @Transactional(rollbackFor = {Throwable.class})
  public UserProfile saveOrUpdateUser(final UserProfile userProfile) throws DuplicateDisplayNameException {
    //even if you're updating a user, it could be a user with no display name. so we need to make sure they don't pick one that already exists
    Long count = (Long) hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(UserProfile.class)
            .add(Restrictions.eq("displayName", userProfile.getDisplayName()))
            .add(Restrictions.not(Restrictions.eq("authId", userProfile.getAuthId())))
            .setProjection(Projections.rowCount()), 0, 1)
        .get(0);
    if (!count.equals(0l)) {
      throw new DuplicateDisplayNameException();
    }
    //check if a user with the same auth id already exists
    UserProfile existingUser = getUserByAuthId(userProfile.getAuthId());
    if (existingUser != null) {
      log.debug("Found a user with authID: {}, updating profile", userProfile.getAuthId());
      copyFields(userProfile, existingUser);
      hibernateTemplate.update(existingUser);
      return existingUser;
    } else {
      log.debug("Creating a new user with authID: {}; {}", userProfile.getAuthId(), userProfile);
      //TODO: We're generating account and profile uris here to maintain backwards compatibility with annotations
      //once those are refactored we can just call hibernateTemplate.save()
      String prefix = System.getProperty(ConfigurationStore.SYSTEM_OBJECT_ID_PREFIX);
      String accountUri = prefix + "account/" + UUID.randomUUID().toString();
      String profileUri = prefix + "profile/" + UUID.randomUUID().toString();
      userProfile.setAccountUri(accountUri);
      userProfile.setProfileUri(profileUri);
      hibernateTemplate.save(userProfile);
      return userProfile;
    }
  }

  @Override
  @Transactional
  public void setAlerts(String userAuthId, List<String> monthlyAlerts, List<String> weeklyAlerts) {
    UserProfile user = getUserByAuthId(userAuthId);

    log.debug("updating alerts for user: {}; Montly alerts: {}; weekly alerts: {}",
        new Object[]{user.getDisplayName(), StringUtils.join(monthlyAlerts, ","), StringUtils.join(weeklyAlerts, ",")});
    List<String> allAlerts;

    if (monthlyAlerts != null && weeklyAlerts != null) {
      allAlerts = new ArrayList<String>(monthlyAlerts.size() + weeklyAlerts.size());
      allAlerts.addAll(getAlertsList(monthlyAlerts, UserProfile.MONTHLY_ALERT_SUFFIX));
      allAlerts.addAll(getAlertsList(weeklyAlerts, UserProfile.WEEKLY_ALERT_SUFFIX));
    } else if (monthlyAlerts != null) {
      allAlerts = new ArrayList<String>(monthlyAlerts.size());
      allAlerts.addAll(getAlertsList(monthlyAlerts, UserProfile.MONTHLY_ALERT_SUFFIX));
    } else if (weeklyAlerts != null) {
      allAlerts = new ArrayList<String>(weeklyAlerts.size());
      allAlerts.addAll(getAlertsList(weeklyAlerts, UserProfile.WEEKLY_ALERT_SUFFIX));
    } else {
      allAlerts = new ArrayList<String>(0);
    }
    user.setAlertsList(allAlerts);
    hibernateTemplate.update(user);
  }

  /**
   * return a list of alerts strings with the given suffix added, if they don't already have it
   *
   * @param alerts the list of alerts
   * @param suffix the alerts suffix
   * @return a list of alerts strings with the given suffix added, if they don't already have it
   */
  private List<String> getAlertsList(List<String> alerts, String suffix) {
    List<String> result = new ArrayList<String>(alerts.size());
    for (String alert : alerts) {
      if (alert.endsWith(suffix)) {
        result.add(alert);
      } else {
        result.add(alert + suffix);
      }
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public UserProfile getUser(Long userId) {
    if (userId != null) {
      log.debug("Looking up user with id: {}", userId);
      return (UserProfile) hibernateTemplate.get(UserProfile.class, userId);
    } else {
      throw new IllegalArgumentException("Null userId");
    }
  }

  @Override
  public UserProfile getProfileForDisplay(UserProfile userProfile, boolean showPrivateFields) {
    UserProfile display = new UserProfile();
    copyFields(userProfile, display);
    if (!showPrivateFields) {
      log.debug("Removing private fields for display on user: {}", userProfile.getDisplayName());
      display.setOrganizationName(null);
      display.setOrganizationType(null);
      display.setPostalAddress(null);
      display.setPositionType(null);
    }

    //escape html in all string fields
    BeanWrapper wrapper = new BeanWrapperImpl(display);
    for (PropertyDescriptor property : wrapper.getPropertyDescriptors()) {
      if (String.class.isAssignableFrom(property.getPropertyType())) {
        String name = property.getName();
        wrapper.setPropertyValue(name, TextUtils.escapeHtml((String) wrapper.getPropertyValue(name)));
      }
    }


    return display;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<UserAlert> getAvailableAlerts() {
    List<UserAlert> alerts = new ArrayList<UserAlert>();

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
   * Copy fields for updating or display. Does <b>not</b> copy some fields:
   * <ul>
   * <li>ID: never overwrite IDs on hibernate objects</li>
   * <li>userAccountUri: these don't come down from display layer, so we don't want to overwrite with null</li>
   * <li>userProfileUri: these don't come down from display layer, so we don't want to overwrite with null</li>
   * <li>roles: don't want to overwrite a user's roles when updating their profile</li>
   * </ul>
   *
   * @param source
   * @param destination
   */
  private void copyFields(UserProfile source, UserProfile destination) {
    destination.setAccountState(source.getAccountState());
    destination.setAuthId(source.getAuthId());
    destination.setRealName(source.getRealName());
    destination.setGivenNames(source.getGivenNames());
    destination.setSurname(source.getSurname());
    destination.setTitle(source.getTitle());
    destination.setGender(source.getGender());
    destination.setEmail(source.getEmail());
    destination.setHomePage(source.getHomePage());
    destination.setWeblog(source.getWeblog());
    destination.setPublications(source.getPublications());
    destination.setDisplayName(source.getDisplayName());
    destination.setSuffix(source.getSuffix());
    destination.setPositionType(source.getPositionType());
    destination.setOrganizationName(source.getOrganizationName());
    destination.setOrganizationType(source.getOrganizationType());
    destination.setPostalAddress(source.getPostalAddress());
    destination.setCity(source.getCity());
    destination.setCountry(source.getCountry());
    destination.setBiography(source.getBiography());
    destination.setInterests(source.getInterests());
    destination.setResearchAreas(source.getResearchAreas());
    destination.setOrganizationVisibility(source.getOrganizationVisibility());
    destination.setAlertsJournals(source.getAlertsJournals());
  }

  @Override
  public boolean allowAdminAction(final String authId) {
    try {
      permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);
      return true;
    } catch (SecurityException ex) {
      return false;
    }
  }

  @Override
  public String fetchUserEmailFromCas(String authId) {
    String url = emailAddressUrl;
    if (!url.endsWith("?")) {
      url += "?";
    }
    url += (authIdParam + "=" + authId);
    try {
      return FileUtils.getTextFromUrl(url);
    } catch (IOException ex) {
      log.error("Failed to fetch the email address using the url:" + url, ex);
      return null;
    }
  }

  @Override
  @Transactional
  public Long recordArticleView(Long userId, Long articleId, ArticleView.Type type) {
    if (this.advancedLogging) {
      return (Long) hibernateTemplate.save(new ArticleView(userId, articleId, type));
    } else {
      return 0L;
    }
  }

  @Override
  @Transactional
  public Long recordUserSearch(Long userProfileID, String searchTerms, String searchParams) {
    if (this.advancedLogging) {
      return (Long) hibernateTemplate.save(new UserSearch(userProfileID, searchTerms, searchParams));
    } else {
      return 0L;
    }
  }

  /**
   * @param emailAddressUrl The url from which the email address of the given guid can be fetched
   */
  @Required
  public void setEmailAddressUrl(final String emailAddressUrl) {
    this.emailAddressUrl = emailAddressUrl;
  }

  /**
   * @param authIdParam the name of the auth id param to pass to cas in http requests for the user email
   */
  @Required
  public void setAuthIdParam(String authIdParam) {
    this.authIdParam = authIdParam;
  }

  /**
   * Getter for property 'permissionsService'.
   *
   * @return Value for property 'permissionsService'.
   */
  public PermissionsService getPermissionsService() {
    return permissionsService;
  }

  /**
   * Setter for property 'permissionsService'.
   *
   * @param permissionsService Value to set for property 'permissionsService'.
   */
  @Required
  public void setPermissionsService(final PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }

  @Required
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;

    Object val = configuration.getProperty(ConfigurationStore.ADVANCED_USAGE_LOGGING);
    if (val != null && val.equals("true")) {
      advancedLogging = true;
    }
  }
}
