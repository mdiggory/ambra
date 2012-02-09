/*
 * $HeadURL$
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

package org.ambraproject.article.service;

import java.io.Serializable;

/**
 * Object that holds the results of findThisArticle action.
 * It is cached in CrossRefCache.
 *
 * Objects are immutable.
 *
 * @author Dragisa Krsmanovic
 */
public class ThisArticleFound implements Serializable {

  private static final long serialVersionUID = -6175982734580575411L;

  private final String doi;
  private final String pubGetUri;
  private final int hashCode;

  public ThisArticleFound(String doi, String pubGetUri) {
    this.doi = doi;
    this.pubGetUri = pubGetUri;
    // Since this object is immutable, we can pre-calculate hash code
    this.hashCode = doi != null ? doi.hashCode() : 0;
  }

  public String getDoi() {
    return doi;
  }

  public String getPubGetUri() {
    return pubGetUri;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThisArticleFound that = (ThisArticleFound) o;

    if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return "ThisArticleFound{" +
        "doi='" + doi + '\'' +
        ", pubGetUri='" + pubGetUri + '\'' +
        '}';
  }
}
