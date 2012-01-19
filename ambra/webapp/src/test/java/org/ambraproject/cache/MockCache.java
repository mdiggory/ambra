/* $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class MockCache implements Cache {

  private Map<Object, CachedItem> map;
  private String name;

  public MockCache() {
    map = new HashMap<Object, CachedItem>();
  }

  public Map<Object, CachedItem> getMap() {
    return map;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Item get(Object key) {
    return (Item) map.get(key);
  }

  public <T, E extends Exception> T get(Object key, Lookup<T, E> lookup) throws E {
    // setting the max-age of entries in the cache to -1 for indefinite
    return get(key, -1, lookup);
  }
  
  public <T, E extends Exception> T get(Object key, int refresh, Lookup<T, E> lookup) throws E {

    Item val = get(key);

    if (val == null) {
      val = new Item(lookup.lookup());
      put(key, val);
    }

    return (T)val.getValue();
  }

  public void put(Object key, Item val) {
    map.put(key, val);
  }

  public void remove(Object key) {
    map.remove(key);
  }

  public void removeAll() {
    map.clear();
  }

  public Set<?> getKeys() {
    return map.keySet();
  }
}
