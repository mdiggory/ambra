/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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

package org.ambraproject.solr;

import org.ambraproject.util.Pair;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Object used by {@link org.ambraproject.solr.SolrHttpServiceImpl} to cache the results of a query for most
 * viewed articles
 *
 * @author Alex Kudlick Date: Mar 14, 2011
 *         <p/>
 *         org.ambraproject.solr
 */
public class MostViewedCache {

  private GregorianCalendar cacheDate;
  private List<Pair<String, String>> articles;
  public static final int CACHE_TIME = 15;
  public static final int CACHE_TIME_UNITS = Calendar.MINUTE;

  public MostViewedCache(GregorianCalendar cacheDate, List<Pair<String, String>> articles) {
    this.cacheDate = cacheDate;
    this.articles = articles;
  }

  public MostViewedCache(List<Pair<String, String>> articles) {
    this.articles = articles;
    this.cacheDate = new GregorianCalendar();
  }

  /**
   * Get the date when these results were cached
   * @return - the cache date
   */
  public GregorianCalendar getCacheDate() {
    return cacheDate;
  }

  /**
   * Get the articles stored in this cache object
   * @return - an ordered list of doi's (first entry) and titles (second entry)
   */
  public List<Pair<String, String>> getArticles() {
    return articles;
  }

  /**
   * Check whether the cached results are still valid
   *
   * @return - a boolean indicating whether the cached results were retrieved within the cache time (i.e. are still
   *         valid to use)
   */
  public boolean isValid() {
    GregorianCalendar time = new GregorianCalendar();
    time.add(CACHE_TIME_UNITS, -CACHE_TIME);
    return time.before(this.cacheDate) && articles != null;
  }

}
