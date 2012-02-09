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

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;

import java.io.Serializable;

/**
 * Represents the body of an Annotation object.
 *
 * @author Pradeep Krishnan
 */
@Entity()
public class AnnotationBlob extends ByteArrayBlob implements CompetingInterest, Serializable {
  private static final long serialVersionUID = -1746684496797088399L;
  private String id;
  private String ciStatement;

  /**
   * Max length of the body content
   */
  public static final int MAX_BODY_LENGTH = 64000;

  /**
   * Max length of the competing interest statement
   */
  public static final int MAX_CISTATEMENT_LENGTH = 5000;

  /**
   * Creates a new AnnotationBlob object.
   */
  public AnnotationBlob() {
  }

  /**
   * Creates a new AnnotationBlob object.
   *
   * @param contentType the content-type
   */
  public AnnotationBlob(String contentType) {
    super(contentType);
  }

  /**
   * Get id.
   *
   * @return id as String.
   */
  public String getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id the value to set.
   */
  @Id
  @GeneratedValue(uriPrefix = "annoteaBodyId:",
      generatorClass = "org.topazproject.ambra.models.support.BlobIdGenerator")
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get the competing Interest statement
   * @return the competing interest statement
   */
  public String getCIStatement() {
    return ciStatement;
  }

  /**
   * Set the competing interest statement
   * @param ciStatement The statement to save
   */
  public void setCIStatement(String ciStatement) {
    this.ciStatement = ciStatement;
  }
}
