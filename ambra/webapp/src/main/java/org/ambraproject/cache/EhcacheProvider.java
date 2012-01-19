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
package org.ambraproject.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * A cache implementation using Ehcache.
 *
 * @author Pradeep Krishnan
 */
public class EhcacheProvider implements Cache {
  private static final Logger   log = LoggerFactory.getLogger(EhcacheProvider.class);

  private final Ehcache      cache;
  private final String       name;
  private final Boolean      allowNulls;

  /**
   * Creates a new EhcacheProvider object.
   *
   * @param cache the ehcache object
   */
  public EhcacheProvider(Ehcache cache) {
    this.cache        = cache;
    this.name         = cache.getName();
    this.allowNulls   = true;
  }

  /**
   * Creates a new EhcacheProvider object.
   *
   * @param cache the ehcache object
   */
  public EhcacheProvider(Ehcache cache, Boolean allowNulls) {
    this.cache        = cache;
    this.name         = cache.getName();
    this.allowNulls   = allowNulls;
  }

  /*
   * inherited javadoc
   */
  public String getName() {
    return name;
  }

  /*
   * inherited javadoc
   */
  public Item get(Object key) {
    CachedItem val = null;

    Element e = cache.get(key);

    if (e != null) {
      val = (e.getObjectValue() instanceof Item) ? (Item) val : new Item(e.getObjectValue());
    }

    return (Item)val;
  }

  /**
   * {@inheritDoc}
   */
  public <T, E extends Exception> T get(final Object key, final Lookup<T, E> lookup)
                                 throws E {
    return get(key, (int) cache.getTimeToLiveSeconds(), lookup);
  }
  
  /*
   * inherited javadoc
   */
  public <T, E extends Exception> T get(final Object key, final int refresh,
                                        final Lookup<T, E> lookup)
                                 throws E {
    Item val = get(key);

    try {
      if ((val == null) && (lookup != null)) {
        val =
          lookup.execute(new Lookup.Operation() {
              public Item execute(boolean degraded) throws Exception {
                if (degraded)
                  log.warn("Degraded mode lookup for key '" + key + "' in cache '"
                           + EhcacheProvider.this.getName() + "'");

                Item val = get(key);

                if (val == null) {
                  Object o = lookup.lookup();
                  val = new Item(o, refresh);
                  if (allowNulls || (o != null))
                    put(key, val);
                  else
                    if (log.isWarnEnabled())
                      log.warn("Cache request to save null when allowNulls = false. '" +
                          key + "' in cache '" + EhcacheProvider.this.getName() + "'",
                          new Exception());
                }
                return val;
              }
            });
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw (E) e;
    }

    return (val == null) ? null : (T) val.getValue();
  }

  /*
   * inherited javadoc
   */
  public void put(final Object key, final Item val) {
    Element e = new Element(key, val.getValue());

    if (val.getTtl() > 0)
      e.setTimeToLive(val.getTtl());

    cache.put(e);
  }

  /*
   * inherited javadoc
   */
  public void remove(final Object key) {
    cache.remove(key);
  }

  /*
   * inherited javadoc
   */
  public void removeAll() {
    cache.removeAll();
  }
}
