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

import java.net.URL;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Retrieve the URL over the network.
 *
 * @author Ronald Tschalär
 * @version $Id$
 */
public class NetworkURLRetriever implements URLRetriever {
  private static final Log log = LogFactory.getLog(NetworkURLRetriever.class);

  /**
   * Retrieve the specified url from the network and return it.
   *
   * @param url the url of the resource to retrieve
   * @param id  the id of the resource to retrieve
   * @return a byte array containing the retrieved content.
   * @throws IOException if there was a problem fetching the content.
   */
  public byte[] retrieve(String url, String id) throws IOException {
    if (log.isDebugEnabled())
      log.debug("Network retriever ('" + url + "')");

    return IOUtils.toByteArray(new URL(url).openStream());
  }
}
