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
import java.util.List;
import java.util.ArrayList;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.CollectionType;

/**
 * Marker class to mark an Aggregation as a "Issue".
 *
 * @author Jeff Suttor
 */
@Entity(types = {"plos:Issue"}, graph = "ri")
public class Issue extends Aggregation {
  private static final long serialVersionUID = -4532961080689709777L;

  private String    displayName;
  private List<URI> articleList = new ArrayList<URI>();
  private boolean   respectOrder = false;
  private URI       image;


  /**
   * Get the image for this Issue.
   *
   * @return URI for the image, may be null.
   */
  public URI getImage() {
    return image;
  }

  /**
   * Set the image for this Issue.
   *
   * @param image arbitrary URI to the image, may be null.
   */
  @Predicate(uri = "plos:image")
  public void setImage(URI image) {
    this.image = image;
  }

  /**
   * Get the display name for this Issue.
   *
   * @return the display name.  will not be null.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the display name for this Issue.
   *
   * The display name should be human friendly.
   *
   * @param displayName the display name, may not be null.
   */
  @Predicate(uri = "plos:displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   *
   * @param articleList   list of URIs
   */
  @Predicate(uri = "plos:orderedArticles", collectionType = CollectionType.RDFSEQ)
  public void setArticleList(List<URI> articleList) {
    this.articleList = articleList;
  }

  /**
   *
   * @return  a list of article URIs
   */
  public List<URI> getArticleList() {
    return this.articleList;
  }

  /**
   * Set respectOrder.
   *
   * @param respectOrder the value to set.
   */
  @Predicate(uri = "plos:respectOrder")
  public void setRespectOrder(boolean respectOrder) {
    this.respectOrder = respectOrder;
  }

  /**
   * Get respectOrder.
   *
   * @return respectOrder as boolean.
   */
  public boolean getRespectOrder() {
    return respectOrder;
  }

  /**
   * String representation for debugging.
   * 
   * @return String representation for debugging.
   */
  @Override
  public String toString() {
    return "Issue: [" +
           "displayName: " + getDisplayName() +
           ", image: " + getImage() +
           ", repsectOrder: " + getRespectOrder() +
           ", " + super.toString() +
           "]";
  }
}
