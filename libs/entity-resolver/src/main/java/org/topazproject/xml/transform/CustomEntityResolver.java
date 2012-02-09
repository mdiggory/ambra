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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cache entities for an {@link org.xml.sax.XMLReader}.
 *
 * For example: (also see {@link org.topazproject.xml.transform.cache.CachedSource})
 * <pre>
 *   Transformer    transformer   = ...
 *   URLRetriever   retriever     = ...
 *   InputSource    myInputSource = new InputSource(new FileReader(inFileName));
 *   EntityResolver resolver      = new CustomEntityResolver(retriever);
 *   EntityResolvingSource source = new EntityResolvingSource(myInputSource, resolver);
 *   transformer.transform(source, new StreamResult(outFileName));
 * </pre>
 *
 * @author Eric Brown and Ronald Tschalär
 * @version $Id$
 */
public class CustomEntityResolver implements EntityResolver {
  private static final Log log = LogFactory.getLog(CustomEntityResolver.class);
  private URLRetriever retriever;

  /**
   * Create a new EntityResolver that will use the supplied <code>URLRetriever</code> to
   * retrieve entities.
   *
   * @param retriever the object to retrieve entities.
   */
  public CustomEntityResolver(URLRetriever retriever) {
    this.retriever = retriever;
  }

  /**
   * Resolve the specified entity using the configured <code>URLRetriever</code> (and any
   * of its delegates).
   *
   * @param publicId The public identifier of the external entity being referenced, or null if
   *                 none was supplied.
   * @param systemId The system identifier of the external entity being referenced.
   * @return An InputSource object describing the new input source, or null to request that the
   *         parser open a regular URI connection to the system identifier.
   * @throws IOException Indicates a problem retrieving the entity.
   */
  public InputSource resolveEntity(String publicId, String systemId) throws IOException {
    if (log.isDebugEnabled())
      log.debug("Resolving entity '" + systemId + "'");

    byte[] res = retriever.retrieve(systemId, publicId);
    if (log.isDebugEnabled())
      log.debug("Entity '" + systemId + "' " + (res != null ? "found" : "not found"));

    if (res == null)
      return null;

    InputSource is = new InputSource(new ByteArrayInputStream(res));
    is.setPublicId(publicId);
    is.setSystemId(systemId);

    return is;
  }
}
