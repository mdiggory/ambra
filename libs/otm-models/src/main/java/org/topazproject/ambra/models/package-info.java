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

@Graphs({
  @Graph(id = "ri", uri = Ambra.GRAPH_PREFIX + "filter:graph=ri",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "articles", uri = Ambra.GRAPH_PREFIX + "filter:graph=ri",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "annotations", uri = Ambra.GRAPH_PREFIX + "filter:graph=ri",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "users", uri = Ambra.GRAPH_PREFIX + "filter:graph=users",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "grants", uri = Ambra.GRAPH_PREFIX + "filter:graph=grants",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "revokes", uri = Ambra.GRAPH_PREFIX + "filter:graph=revokes",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "pp", uri = Ambra.GRAPH_PREFIX + "filter:graph=pp",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "preferences", uri = Ambra.GRAPH_PREFIX + "filter:graph=preferences",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "profiles", uri = Ambra.GRAPH_PREFIX + "filter:graph=profiles",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "ratings", uri = Ambra.GRAPH_PREFIX + "filter:graph=ratings",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "alerts", uri = Ambra.GRAPH_PREFIX + "filter:graph=alerts",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "criteria", uri = Ambra.GRAPH_PREFIX + "filter:graph=criteria",
         type = Ambra.TYPE_PREFIX + "filter"),
  @Graph(id = "str", uri = Ambra.GRAPH_PREFIX + "str",
         type = Ambra.TYPE_PREFIX + "StringCompare"),
  @Graph(id = "xsd", uri = Ambra.GRAPH_PREFIX + "xsd",
         type = Rdf.mulgara + "XMLSchemaModel"),
  @Graph(id = "prefix", uri = Ambra.GRAPH_PREFIX + "prefix",
         type = Rdf.mulgara + "PrefixGraph")
})
@Aliases({
  @Alias(alias = "foaf",    value = Rdf.foaf),
  @Alias(alias = "dc",      value = Rdf.dc),
  @Alias(alias = "dcterms", value = Rdf.dc_terms),
  @Alias(alias = "dcmi",    value = Rdf.dcmi),
  @Alias(alias = "topaz",   value = Rdf.topaz),
  @Alias(alias = "plos",    value = Ambra.plos),
  @Alias(alias = "cc",      value = Ambra.creativeCommons),
  @Alias(alias = "bibtex",  value = Ambra.bibtex),
  @Alias(alias = "prism",   value = Ambra.prism),
  @Alias(alias = "r",       value = Reply.NS),
  @Alias(alias = "annotea", value = Annotea.W3C_NS),
  @Alias(alias = "bio",     value = UserProfile.BIO_URI),
  @Alias(alias = "address", value = UserProfile.ADDR_URI),
  @Alias(alias = "contact", value = UserProfile.contact)
})
package org.topazproject.ambra.models;

import org.topazproject.otm.annotations.Alias;
import org.topazproject.otm.annotations.Aliases;
import org.topazproject.otm.annotations.Graph;
import org.topazproject.otm.annotations.Graphs;
import org.topazproject.otm.Rdf;
