/* $HeadURL$
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

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.CascadeType;

/**
 * Represents a formal correction annotation.
 *
 * @author Alex Worden
  */
@Entity(types = {FormalCorrection.RDF_TYPE})
public class FormalCorrection extends Correction {
  private static final long serialVersionUID = -4518918101775161797L;

  public  static final String RDF_TYPE         = Annotea.TOPAZ_TYPE_NS + "FormalCorrection";

  private Citation bibliographicCitation;

  public String getType() {
    return RDF_TYPE;
  }

  @Override
  public String getWebType() {
    return WEB_TYPE_FORMAL_CORRECTION;
  }

  public Citation getBibliographicCitation() {
    return bibliographicCitation;
  }

  @Predicate(uri = "dcterms:bibliographicCitation", cascade = { CascadeType.child })
  public void setBibliographicCitation(Citation bibliographicCitation) {
    this.bibliographicCitation = bibliographicCitation;
  }
}
