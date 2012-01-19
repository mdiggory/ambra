/* $HeadURL::                                                                                     $
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
package org.topazproject.xml.transform;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.lang.ref.SoftReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Look up the URL in an in-memory cache. If the URL is not cached, then look it up
 * via the delegate and store it in the cache for later use. Cached entities are
 * <code>SoftReference</code>s such that the garbage collector can re-claim them if
 * memory is low.
 *
 * @author Ronald Tschalär
 * @version $Id$
 */
public class MemoryCacheURLRetriever implements URLRetriever {
  private static final Log   log = LogFactory.getLog(MemoryCacheURLRetriever.class);
  private final Map          cache = new HashMap();
  private final URLRetriever delegate;

  /**
   * Create a <code>URLRetriever</code> that will cache content fetched by its delegate.
   *
   * @param delegate the <code>URLRetriever</code> to use on a cache-miss.
   */
  public MemoryCacheURLRetriever(URLRetriever delegate) {
    this.delegate = delegate;
  }

  /**
   * Lookup the <code>id</code> in the cache. If found, return results. Otherwise, call
   * delegate to fetch content.
   * 
   * @param url the url of the resource to retrieve
   * @param id  the id of the resource to retrieve
   * @return the contents, or null if not found
   * @throws IOException if an error occurred retrieving the contents (other than not-found)
   */
  public synchronized byte[] retrieve(String url, String id) throws IOException {
    SoftReference ref = (SoftReference) cache.get(id);
    byte[] res = (ref != null) ? (byte[]) ref.get() : null;

    if (log.isDebugEnabled())
      log.debug("Memory cache('" + id + "'): " +
                (res != null ? "found" : ref != null ? "expired" : "not found"));

    if (res != null || delegate == null)
      return res;

    res = delegate.retrieve(url, id);
    if (res == null)
      return null;

    if (log.isDebugEnabled())
      log.debug("Caching '" + id + "'");

    cache.put(id, new SoftReference(res));
    return res;
  }
}
