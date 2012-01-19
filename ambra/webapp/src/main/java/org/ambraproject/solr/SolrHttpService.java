/*
 * $HeadURL$
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.solr;

import org.w3c.dom.Document;

import java.util.Map;

/**
 * Interface for beans to make http requests to the PLoS Solr server
 * <p/>
 * <p/>
 * User: Alex Kudlick Date: Feb 15, 2011
 * <p/>
 * org.ambraproject.solr
 */
public interface SolrHttpService {

  /**
   * Basic method for making a request to the Solr server, with a Map of key/value pairs to pass as parameters
   *
   * @param params - the params to pass to solr.  See <a href="http://wiki.plos.org/pmwiki.php/Topaz/SOLRSchema#SolrFieldList">
   *               this wiki page</a> for a list of solr fields
   * @return - A Document wrapper around the Solr response
   */
  public Document makeSolrRequest(Map<String, String> params) throws SolrException;


}
