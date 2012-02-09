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

import java.util.Properties;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Look up the id in a resource cache.
 *
 * @author Ronald Tschalär
 * @version $Id$
 */
public class ResourceURLRetriever implements URLRetriever {
  private static final Log   log = LogFactory.getLog(ResourceURLRetriever.class);

  private final URLRetriever delegate;
  private final Properties   urlMap;
  private final String       relativePrefix;
  private final Class        resourceLoader;

  public ResourceURLRetriever(URLRetriever delegate, Properties urlMap, String relativePrefix) {
    this.delegate = delegate;
    this.urlMap = urlMap;
    this.relativePrefix = relativePrefix;
    this.resourceLoader = ResourceURLRetriever.class;
  }

  public ResourceURLRetriever(URLRetriever delegate, Properties urlMap, Class resourceLoader) {
    this.delegate = delegate;
    this.urlMap = urlMap;
    this.relativePrefix = null;
    this.resourceLoader = resourceLoader;
  }

  public byte[] retrieve(String url, String id) throws IOException {
    String resource = (id != null) ? urlMap.getProperty(id) : null;

    if (log.isDebugEnabled())
      log.debug("Resource retriever ('" + id + "'): " + (resource != null ? "found" : "not found"));

    if (resource == null)
      return (delegate != null) ? delegate.retrieve(url, id) : null;

    // Deal with relative prefixes
    if (relativePrefix != null && !resource.startsWith("/"))
      resource = relativePrefix + resource;

    return IOUtils.toByteArray(resourceLoader.getResourceAsStream(resource));
  }
}
