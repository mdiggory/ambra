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
package org.ambraproject.model.article;

import java.io.Serializable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.ambraproject.model.UserProfileInfo;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * Just the list of authors.
 */
@Entity(types = {"bibtex:Entry"}, graph = "ri")
public class CitationInfo implements Serializable {
  private URI id;
  private List<UserProfileInfo> authors = new ArrayList<UserProfileInfo>();
  private List<String> collaborativeAuthors = new ArrayList<String>();

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
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get authors
   * @return list of authors
   */
  public List<UserProfileInfo> getAuthors() {
    return authors;
  }

  /**
   * Set authors
   * @param authors
   */
  @Predicate(ref = "Citation:authors")
  public void setAuthors(List<UserProfileInfo> authors) {
    this.authors = authors;
  }

  /**
   * Get collaborative authors
   * @return list of collaborative authors
   */
  public List<String> getCollaborativeAuthors() {
    return collaborativeAuthors;
  }

  /**
   * Set collaborative authors
   * @param collaborativeAuthors
   */
  @Predicate(ref = "Citation:collaborativeAuthors")
  public void setCollaborativeAuthors(List<String> collaborativeAuthors) {
    this.collaborativeAuthors = collaborativeAuthors;
  }
}
