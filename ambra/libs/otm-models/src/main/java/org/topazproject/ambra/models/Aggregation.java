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

import org.topazproject.otm.CascadeType;
import org.topazproject.otm.annotations.*;
import org.topazproject.otm.criterion.DetachedCriteria;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An aggregation of resources. 
 * 
 * @author Pradeep Krishnan
 * @author Jeff Suttor
 * @author Ronald Tschalär
 * @author Eric Brown
 * @author Amit Kapoor
 */
@Entity(types = {"http://purl.org/dc/dcmitype/Collection"}, graph = "ri")
public abstract class Aggregation implements Serializable {
  private static final long serialVersionUID = -5388685823985053203L;
  private URI                            id;
  private List<URI>                      simpleCollection = new ArrayList<URI>();
  // FIXME: DetachedCriteria is not serializable. Ehcache will throw error when serializing Aggregation.
  private List<DetachedCriteria>         smartCollectionRules = new ArrayList<DetachedCriteria>();
  private Aggregation                    supersedes;
  private Aggregation                    supersededBy;
  private String                         title;

  private String                         description;
  private Date                           created;

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
  @GeneratedValue(uriPrefix = "id:aggregation/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Set the title of the aggregation
   * @param title
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * Return the title of the aggregation
   * @return
   */
  public String getTitle()
  {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the created date
   * @return
   */
  public Date getCreated() {
    return created;
  }

  /**
   * Set the created date
   * @param created
   */
  public void setCreated(Date created) {
    this.created = created;
  }

  /**
   * Get simple collection of articles.
   *
   * @return collection of article URI
   */
  public List<URI> getSimpleCollection() {
    return simpleCollection;
  }

  /**
   * Set simple collection of articles.
   *
   * @param simpleCollection collection of article URI
   */
  @Predicate(uri = "dcterms:hasPart")
  public void setSimpleCollection(List<URI> simpleCollection) {
    this.simpleCollection = simpleCollection;
  }

  /**
   * Get smart collection rules for articles (and other entities).
   *
   * @return smart collection rules
   */
  public List<DetachedCriteria> getSmartCollectionRules() {
    return smartCollectionRules;
  }

  /**
   * Set smart collection rules.
   *
   * @param smartCollectionRules as a list of detached criteria
   */
  @Predicate(uri = "plos:smartCollectionRules", cascade = {CascadeType.child})
  public void setSmartCollectionRules(List<DetachedCriteria> smartCollectionRules) {
    this.smartCollectionRules = smartCollectionRules;
  }

  /**
   * Get supersedes.
   *
   * @return supersedes as Aggregation.
   */
  public Aggregation getSupersedes() {
    return supersedes;
  }

  /**
   * Set supersedes.
   *
   * @param supersedes the value to set.
   */
  @Predicate(uri = "dcterms:replaces")
  public void setSupersedes(Aggregation supersedes) {
    this.supersedes = supersedes;
  }

  /**
   * Get supersededBy.
   *
   * @return supersededBy as Aggregation.
   */
  public Aggregation getSupersededBy() {
    return supersededBy;
  }

  /**
   * Set supersededBy.
   *
   * @param supersededBy the value to set.
   */
  @Predicate(uri = "dcterms:isReplacedBy")
  public void setSupersededBy(Aggregation supersededBy) {
    this.supersededBy = supersededBy;
  }

  /**
   * String representation for debugging.
   * 
   * @return String representation for debugging.
   */
  @Override
  public String toString() {
    return "Aggregation: [" +
           "id: " + getId() +
/*           ", simpleCollection: " + getSimpleCollection() +
           ", " + getSmartCollectionRules() +
           ", " + getDublinCore() +*/
           "]";
  }
}
