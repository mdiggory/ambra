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

import java.io.Serializable;
import java.net.URI;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.GeneratedValue;

/**
 * Model for related articles.
 *
 * <p>Note that the article field is modelled as a URI, <em>not</em> an {@link Article Article}.
 * The reason for this has to do with OTM filters and the fact that the article being referenced
 * may not (yet) be present. If the article is not present, then any filters on articles will
 * cause OTM to fill in a null for the article on retrieval, and a subsequent save will then
 * remove the reference.
 *
 * @author Ronald Tschalär
 */
@Entity(types = {"plos:RelatedArticle"}, graph = "ri")
public class RelatedArticle implements Serializable {
  private static final long serialVersionUID = -4229157024423830497L;

  private URI    id;
  private URI    article;
  private String relationType;

  /**
   * Return the identifier of the object
   *
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * Set the identifier of the object
   *
   * @param id the id to set
   */
  @Id @GeneratedValue(uriPrefix = "id:relatedArticle/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the article uri.
   *
   * @return the article uri.
   */
  public URI getArticle() {
    return article;
  }

  /**
   * Set the article uri.
   *
   * @param article the article uri.
   */
  @Predicate(uri = "dcterms:references")
  public void setArticle(URI article) {
    this.article = article;
  }

  /**
   * Get the relation type.
   *
   * @return the relation type.
   */
  public String getRelationType() {
    return relationType;
  }

  /**
   * Set the relation type.
   *
   * @param relationType the relation type.
   */
  @Predicate(uri = "plos:articleRelationType")
  public void setRelationType(String relationType) {
    this.relationType = relationType;
  }

  @Override
  public String toString() {
    return "RelatedArticle{" +
        "id=" + id +
        ", article=" + article +
        ", relationType='" + relationType + '\'' +
        '}';
  }
}
