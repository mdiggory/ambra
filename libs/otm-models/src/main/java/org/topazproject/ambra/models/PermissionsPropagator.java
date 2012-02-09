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
package org.topazproject.ambra.models;

import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;

/**
 * This class is so oql queries can use the propagate-permissions-to predicate; once oql
 * supports specifying models this won't be necessary. As such this class is not an entity.
 *
 * @author Ronald Tschalär
 */
@Entity
public class PermissionsPropagator {
  private URI propPermsTo;

  /**
   * Get propPermsTo.
   *
   * @return propPermsTo as URI.
   */
  public URI getPropPermsTo() {
    return propPermsTo;
  }

  /**
   * Set propPermsTo.
   *
   * @param propPermsTo the value to set.
   */
  @Predicate(uri = "topaz:propagate-permissions-to", graph = "pp")
  public void setPropPermsTo(URI propPermsTo) {
    this.propPermsTo = propPermsTo;
  }
}
