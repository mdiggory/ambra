/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.topazproject.ambra.models;

import java.net.URI;

/**
 * Entity representing Authors or Editors of articles
 *
 * @author Alex Kudlick Date: 6/9/11
 *         <p/>
 *         org.topazproject.ambra.models
 */
public class ArticleContributor {

  private URI id;
  private String fullName;
  private String givenNames;
  private String surnames;
  private String suffix;
  private boolean isAuthor;

  public URI getId() {
    return id;
  }

  public void setId(URI id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getGivenNames() {
    return givenNames;
  }

  public void setGivenNames(String givenNames) {
    this.givenNames = givenNames;
  }

  public String getSurnames() {
    return surnames;
  }

  public void setSurnames(String surnames) {
    this.surnames = surnames;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public boolean getIsAuthor() {
    return isAuthor;
  }

  public void setIsAuthor(boolean author) {
    isAuthor = author;
  }
}
