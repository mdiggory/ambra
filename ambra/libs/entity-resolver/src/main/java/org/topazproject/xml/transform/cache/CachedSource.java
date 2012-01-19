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
package org.topazproject.xml.transform.cache;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.Source;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.xml.transform.EntityResolvingSource;
import org.topazproject.xml.transform.URLRetriever;
import org.topazproject.xml.transform.NetworkURLRetriever;
import org.topazproject.xml.transform.ResourceURLRetriever;
import org.topazproject.xml.transform.MemoryCacheURLRetriever;
import org.topazproject.xml.transform.CustomEntityResolver;

/**
 * Creates a {@link Source} that can be used in a {@link javax.xml.transform.Transformer#transform}
 * call with most topaz/ambra entities cached.
 *
 * For example:
 * <pre>
 *   Transformer transformer = ...
 *   InputSource myInputSource = new InputSource(new FileReader(inFileName));
 *   transformer.transform(new CachedSource(myInputSource), new StreamResult(outFileName));
 * </pre>
 *
 * @author Eric Brown and Ronald Tschalär
 * @version $Id$
 */
public class CachedSource extends EntityResolvingSource implements Source {
  private static final Log            log = LogFactory.getLog(CachedSource.class);
  private static final String         DEF_MAP  = "url_rsrc_map.properties";
  private static final EntityResolver resolver;

  static {
    URLRetriever r = new NetworkURLRetriever();
    try {
      Properties  urlMap = new Properties();
      InputStream is = CachedSource.class.getResourceAsStream(DEF_MAP);
      if (is != null) {
        urlMap.load(is);
        r = new ResourceURLRetriever(r, urlMap, CachedSource.class);
      } else
        log.error("url-map resource '" + DEF_MAP + "' not found - no map loaded");
    } catch (IOException ioe) {
      log.error("Error loading entity-cache map - continuing without it", ioe);
    }
    resolver = new CustomEntityResolver(new MemoryCacheURLRetriever(r));
  }

  /**
   * Construct a new CachedSource object.
   *
   * @param src is the source of the XML document
   * @throws SAXException if we're unable to create an XMLReader (should never happen)
   */
  public CachedSource(InputSource src) throws SAXException {
    super(src, resolver);
  }

  /**
   * @return the EntityResolver.
   */
  public static EntityResolver getResolver() {
    return resolver;
  }
}
