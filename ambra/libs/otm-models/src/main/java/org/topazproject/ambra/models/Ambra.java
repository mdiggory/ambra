/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.topazproject.ambra.models;

/**
 * Definitions of some standard uris.
 *
 * @author Amit Kapoor
 */
public interface Ambra {
  /** Graph prefix */
  public static final String GRAPH_PREFIX       = "local:///topazproject#";
  /** Graph type prefix */
  public static final String TYPE_PREFIX        = "http://topazproject.org/graphs#";
  /** PLoS namespace */
  public static final String plos               = "http://rdf.plos.org/RDF/";
  /** Creative Commons namespace */
  public static final String creativeCommons    = "http://web.resource.org/cc/";
  /** Bibtex namespace */
  public static final String bibtex             = "http://purl.org/net/nknouf/ns/bibtex#";
  /** Prism namespace */
  public static final String prism              = "http://prismstandard.org/namespaces/1.2/basic/";
}
