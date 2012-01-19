/* $HeadURL::$
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

/**
 * Represents a minor correction annotation.
 *
 * @author Alex Worden
  */
@Entity(types = {MinorCorrection.RDF_TYPE})
public class MinorCorrection extends Correction {
  private static final long serialVersionUID = -5642159242091256749L;
  public  static final String RDF_TYPE         = Annotea.TOPAZ_TYPE_NS + "MinorCorrection";

  public String getType() {
    return RDF_TYPE;
  }

  @Override
  public String getWebType() {
    return WEB_TYPE_MINOR_CORRECTION;
  }
}
