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
 * Correction is an abstract Model class that represents types of correction annotation.
 * It is a superclass to MinorCorrection and FormalCorrection
 *
 * @author Alex Worden
 */
@Entity(types = {Correction.RDF_TYPE})
public abstract class Correction extends ArticleAnnotation {
  public static final String RDF_TYPE = Annotea.W3C_TYPE_NS + "Change";
  private static final long serialVersionUID = -7581290891099274371L;
}

