/**
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://www.plos.org
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

package org.ambraproject.admin.service;

import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.SortedMap;

/**
 * Interface class for admin cache actions
 *
 * @author josowski
 */
public interface CacheService {

  /**
   * get cache stats data for display by manageCaches.ftl
   *
   * @return SortedMap of strings
   */
  SortedMap<String, String[]> getCacheData();

  /**
   * Remove all keys from a cache
   *
   * @param cacheName the name of the cache to removeAll from
   */
  void removeAllKeys(String cacheName);

  /**
   * Remove a key from a cache
   *
   * @param cacheName Which cache to act on
   * @param cacheKey Which key to act on
   * @return True if the key was successfully removed
   */
  boolean removeSingleKey(String cacheName, String cacheKey);

  /**
   * Clear cache statistics
   *
   * @param cacheName Which cache to act on
   */
  void clearStatistics(String cacheName);

  /**
   *
   * Return the value of a single cache key
   *
   * @param cacheName Which cache to act on
   * @param cacheKey Which key to act on
   * @return the value of the cache object
   */
  Object getSingleKey(String cacheName, String cacheKey);

  /**
   * Return a list of all keys in a cache
   *
   * @param cacheName Which cache to act on
   * @return a list of keys in the cache
   */
  List getAllKeys(String cacheName);

  @Required
  void setCacheManager(CacheManager cm);
}
