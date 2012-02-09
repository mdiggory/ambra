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

import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.Predicate.PropType;
import org.topazproject.otm.annotations.UriPrefix;

/**
 * Reply meta-data.
 *
 * @author Pradeep Krishnan
 */
@Entity(types = {Reply.RDF_TYPE})
@UriPrefix(Reply.NS)
public class Reply extends Annotea<ReplyBlob> {
  private static final long serialVersionUID = 1101839685707158323L;
  public static final String RDF_TYPE = Reply.NS + "Reply";
  /**
   * Thread Namespace
   */
  public static final String NS = "http://www.w3.org/2001/03/thread#";

  private URI    id;
  private String root;
  private String inReplyTo;
  private String type;

  /**
   * Creates a new Reply object.
   */
  public Reply() {
  }

/**
   * Creates a new Reply object.
   *
   * @param id the reply id
   */
  public Reply(URI id) {
    this.id = id;
  }

  /**
   * Get root.
   *
   * @return root as String.
   */
  public String getRoot() {
    return root;
  }

  /**
   * Set root.
   *
   * @param root the value to set.
   */
  @Predicate(type=PropType.OBJECT)
  public void setRoot(String root) {
    this.root = root;
  }

  /**
   * Get inReplyTo.
   *
   * @return inReplyTo as URI.
   */
  public String getInReplyTo() {
    return inReplyTo;
  }

  /**
   * Set inReplyTo.
   *
   * @param inReplyTo the value to set.
   */
  @Predicate(type=PropType.OBJECT)
  public void setInReplyTo(String inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  /**
   * Get id.
   *
   * @return id as URI
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
  @GeneratedValue(uriPrefix = "id:reply/")
  public void setId(URI id) {
    this.id = id;
  }

  @Override
  public String getType() {
    return type;
  }

  /**
   * Set the type of this reply. Usually a value defined here: http://www.w3.org/2001/12/replyType
   *
   * @param type the value to set.
   */
  @Predicate(uri = "rdf:type", type = PropType.OBJECT)
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getWebType() {
    return WEB_TYPE_REPLY;
  }
}
