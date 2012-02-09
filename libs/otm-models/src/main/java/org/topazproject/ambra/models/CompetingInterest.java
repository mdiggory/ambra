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

import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.UriPrefix;
import org.topazproject.otm.annotations.Entity;

/**
 * Represents objects that will need a competing interest statement
 */
@Entity(graph = "ri")
@UriPrefix("plos:CompetingInterests/")
public interface CompetingInterest {
  /**
   * Get the competing Interest statement
   * @return the competing interest statement
   */
  public String getCIStatement();

  /**
   * Set the competing interest statement
   * @param ciStatement The statement to save
   */
  @Predicate
  public void setCIStatement(String ciStatement);
}
