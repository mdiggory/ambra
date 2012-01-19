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

package org.ambraproject.admin.action;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import org.ambraproject.admin.service.CacheService;       

/**
 * Display Cache statistics and perform various operations on caches
 *
 * @author jsuttor
 * @author russ
 */
@SuppressWarnings("serial")
public class ManageCachesAction extends BaseAdminActionSupport {
  private static final Logger log = LoggerFactory.getLogger(ManageCachesAction.class);

  private CacheService cacheService;

  private SortedMap<String, String[]> cacheStats;
  private List cacheKeys;

  // fields used for execute
  private String cacheAction;
  private String cacheName;
  private String cacheKey;

  @SuppressWarnings("unchecked")
  @Override
  public String execute() throws Exception {

    // create a faux journal object for template
    initJournal();

    // any actions?
    if ("removeAll".equals(cacheAction) && cacheName != null) {
      cacheService.removeAllKeys(cacheName);
      addActionMessage(cacheName + ".removeAll() executed.");
    }
    else if ("remove".equals(cacheAction) && cacheName != null && cacheKey != null) {
      if (cacheService.removeSingleKey(cacheName, cacheKey)) {
        addActionMessage(cacheName + ".remove(" + cacheKey + ") = TRUE");
      }
      else {
        addActionError(cacheName + ".remove(" + cacheKey + ") = FALSE");
      }
    }
    else if ("clearStatistics".equals(cacheAction) && cacheName != null) {
      cacheService.clearStatistics(cacheName);
      addActionMessage(cacheName + ".clearStatistics() executed.");
    }
    else if ("get".equals(cacheAction) && cacheName != null && cacheKey != null) {
      final Object value = cacheService.getSingleKey(cacheName, cacheKey);
      addActionMessage(cacheName + ".get(" + cacheKey + ") = " + value);
    }
    else if ("getKeys".equals(cacheAction) && cacheName != null) {
      cacheKeys = cacheService.getAllKeys(cacheName);
      try {
        Collections.sort(cacheKeys);
        addActionMessage(cacheName + ".getKeysNoDuplicateCheck() executed.");
      }
      catch (ClassCastException e) {
        log.warn("Caught ClassCastException while sorting cachekeys for " + cacheName, e);
        addActionMessage(cacheName + ".getKeysNoDuplicateCheck() executed. (unsorted list)");
      }
    }

    cacheStats = cacheService.getCacheData();
    return SUCCESS;
  }

  /**
   * Get cacheName.
   *
   * @return Cache name.
   */
  public String getCacheName() {
    return cacheName;
  }

  /**
   * Get cacheStats.
   *
   * @return Cache stats.
   */
  public SortedMap<String, String[]> getCacheStats() {
    return cacheStats;
  }

  /**
   * Get cacheKeys.
   *
   * @return Cache keys.
   */
  @SuppressWarnings("unchecked")
  public List getCacheKeys() {
    return cacheKeys;
  }

  /**
   * Set cache service
   *
   * @param cacheService the CacheService to use
   */
  @Required
  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  /**
   * @param cacheAction The cacheAction to use.
   */
  @Required
  public void setCacheAction(String cacheAction) {
    this.cacheAction = cacheAction;
  }

  /**
   * @param cacheName The cacheName to use.
   */
  @Required
  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }

  /**
   * @param cacheKey The cachekey to use.
   */
  @Required
  public void setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
  }
}
