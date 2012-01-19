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

import java.io.Serializable;
import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * Annotation meta-data.
 *
 * @param <T> The annotation body type
 * 
 * @author Pradeep Krishnan
 */
@Entity(types = {Annotation.RDF_TYPE})
public abstract class Annotation<T extends Serializable> extends Annotea<T> {
  private static final long serialVersionUID = -8982085063664548873L;

  public static final String RDF_TYPE = Annotea.W3C_NS + "Annotation";

  private URI           id;
  private URI           annotates;
  private String        context;
  private Annotation<T> supersedes;
  private Annotation<T> supersededBy;

  /**
   * Creates a new Annotation object.
   */
  public Annotation() {
  }

  /**
   * Get annotates.
   *
   * @return annotates as Uri.
   */
  public URI getAnnotates() {
    return annotates;
  }

  /**
   * Set annotates.
   *
   * @param annotates the value to set.
   */
  @Predicate
  public void setAnnotates(URI annotates) {
    this.annotates = annotates;
  }

  /**
   * Get context.
   *
   * @return context as String.
   */
  public String getContext() {
    return context;
  }

  /**
   * Set context.
   *
   * @param context the value to set.
   */
  @Predicate
  public void setContext(String context) {
    this.context = context;
  }

  /**
   * Get supersedes.
   *
   * @return supersedes.
   */
  public Annotation<T> getSupersedes() {
    return supersedes;
  }

  /**
   * Set supersedes.
   *
   * @param supersedes the value to set.
   */
  @Predicate(uri = "dcterms:replaces")
  public void setSupersedes(Annotation<T> supersedes) {
    this.supersedes = supersedes;
  }

  /**
   * Get supersededBy.
   *
   * @return supersededBy.
   */
  public Annotation<T> getSupersededBy() {
    return supersededBy;
  }

  /**
   * Set supersededBy.
   *
   * @param supersededBy the value to set.
   */
  @Predicate(uri = "dcterms:isReplacedBy")
  public void setSupersededBy(Annotation<T> supersededBy) {
    this.supersededBy = supersededBy;
  }

  /**
   * Get id.
   *
   * @return id as URI.
   */
  public URI getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id the value to set.
   */
  @Id
  @GeneratedValue(uriPrefix = "id:annotation/")
  public void setId(URI id) {
    this.id = id;
  }

}
