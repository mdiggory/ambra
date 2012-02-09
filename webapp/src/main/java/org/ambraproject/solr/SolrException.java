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

/**
 * User: Alex Kudlick Date: Feb 17, 2011
 */
public class SolrException extends Exception{
  public SolrException() {
  }

  public SolrException(String message) {
    super(message);
  }

  public SolrException(String message, Throwable cause) {
    super(message, cause);
  }

  public SolrException(Throwable cause) {
    super(cause);
  }
}
