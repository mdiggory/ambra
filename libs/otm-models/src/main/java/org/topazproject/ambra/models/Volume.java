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
import java.util.ArrayList;
import java.util.List;

import org.topazproject.otm.CollectionType;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;

/**
 * Volume OTM model class. Extends Aggregation but does not use the 
 * simpleCollection defined there as it's order is not preserved. 
 * Instead, we use issueList to handle the aggregated issues. 
 *
 * @author Jeff Suttor
 * @author Alex Worden
 */
@Entity(types = {"plos:Volume"}, graph = "ri")
public class Volume extends Aggregation {
  private static final long serialVersionUID = -8134172321127413292L;
  private String displayName;
  private URI image;

  /*
   * TODO - "issueList" should probably not be prefixed with dcterms. We'll need a data migration to
   * change this :(
   */

  // The ordered list of DOIs of issues contained in this volume. 
  private List<URI> issueList = new ArrayList<URI>();

  /**
   * Get the image for this Volume.
   *
   * @return URI for the image, may be null.
   */
  public URI getImage() {
    return image;
  }

  /**
   * Set the image for this Volume.
   *
   * @param image arbitrary URI to the image, may be null.
   */
  @Predicate(uri = "plos:image")
  public void setImage(URI image) {
    this.image = image;
  }


  /**
   * Get the display name for this Volume.
   *
   * @return the display name.  will not be null.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the display name for this Volume.
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
   * String representation for debugging.
   * 
   * @return String representation for debugging.
   */
  @Override
  public String toString() {
    return "Volume: [" +
           "displayName: " + getDisplayName() +
           ", image: " + getImage() +
//           ", issueList: " + getIssueList() +
           ", " + super.toString() +
           "]";
  }

  /**
   * Retrieves an ordered list of issue DOIs contained in this volume
   *
   * @return ordered list of issue DOIs contained in this volume. 
   */
  public List<URI> getIssueList() {
    /* Workaround for bug in original implementation...
     * Migrate existing issue list from super.simpleCollection to 
     * this.issueList. Remove all issue from super.simpleCollection.
     * This code can probably be removed after 0.8.2.1 release but it 
     * shouldn't do any harm to leave it in here. 
     */
    if (((this.issueList == null) || (this.issueList.size() == 0)) && 
        ((super.getSimpleCollection() != null) && (super.getSimpleCollection().size() > 0))) {
      this.issueList = super.getSimpleCollection();
    }
    return issueList;
  }

  /**
   * Set the ordered list of issue DOIs contained in this volume
   *
   * @param issueList the list of issues
   */
  @Predicate(uri = "dcterms:issueList", collectionType = CollectionType.RDFSEQ)
  public void setIssueList(List<URI> issueList) {
     this.issueList = issueList;
     super.setSimpleCollection(new ArrayList<URI>());
  }
}
