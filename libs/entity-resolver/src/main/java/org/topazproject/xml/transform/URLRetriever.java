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

/** 
 * Retrieve the content of a URL.
 *
 * @author Ronald Tschalär
 * @version $Id$
 */
public interface URLRetriever {
  /** 
   * Retrieve the contents of a URL as a byte[]. 
   * 
   * @param url the url of the resource to retrieve
   * @param id  the id of the resource to retrieve
   * @return the contents, or null if not found
   * @throws IOException if an error occurred retrieving the contents (other than not-found)
   */
  public byte[] retrieve(String url, String id) throws IOException;
}
