/* $HeadURL$
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
package org.ambraproject.model;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * The info about a single Volume that the UI needs.
 * 
 * This class is immutable.
 */
public class VolumeInfo implements Serializable {

  private URI          id;
  private String       displayName;
  private URI          prevVolume;
  private URI          nextVolume;
  private URI          imageArticle;
  private String       description;
  private List<IssueInfo> issueInfos;

  // XXX TODO, List<URI> w/Issue DOI vs. List<IssueInfo>???
  public VolumeInfo(URI id, String displayName, URI prevVolume, URI nextVolume, URI imageArticle,
    String description, List<IssueInfo> issueInfos) {

    this.id = id;
    this.displayName = displayName;
    this.prevVolume = prevVolume;
    this.nextVolume = nextVolume;
    this.imageArticle = imageArticle;
    this.description = description;
    this.issueInfos = issueInfos;
  }

  /**
   * Get the id.
   *
   * @return the id.
   */
  public URI getId() {
    return id;
  }

  /**
   * Get the displayName.
   *
   * @return the displayName.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Get the previous Volume.
   *
   * @return the previous Volume.
   */
  public URI getPrevVolume() {
    return prevVolume;
  }

  /**
   * Get the next Volume.
   *
   * @return the next Volume.
   */
  public URI getNextVolume() {
    return nextVolume;
  }

  /**
   * Get the image Article DOI.
   *
   * @return the image Article DOI.
   */
  public URI getImageArticle() {
    return imageArticle;
  }

  /**
   * Get the description.
   *
   * @return the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the issueInfos.
   *
   * @return the issueInfos.
   */
  public List<IssueInfo> getIssueInfos() {
    return issueInfos;
  }
}
