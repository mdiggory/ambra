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

package org.ambraproject.feed.service;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.Serializable;

/**
 * @author Dragisa Krsmanovic
 * @author Joe Osowski
 */
public class AnnotationSearchParameters implements Serializable {

  private static final long serialVersionUID = 2340342378978L;

  private final String journal;
  private final Date startDate;
  private final Date endDate;
  private Set<String> annotationTypes;
  private final int maxResults;

  /**
   * New AnnotationSearchParams class
   * @param feedCacheKey
   */
  public AnnotationSearchParameters(ArticleFeedCacheKey feedCacheKey) {
    this.journal = feedCacheKey.getJournal();
    this.startDate = feedCacheKey.getSDate();
    this.endDate = feedCacheKey.getEDate();
    this.maxResults = feedCacheKey.getMaxResults();

    if (feedCacheKey.feedType() != FeedService.FEED_TYPES.Annotation) {
      HashSet<String> types = new HashSet<String>();
      types.add(feedCacheKey.feedType().type());
      this.annotationTypes = types;
    }
  }

  public String getJournal() {
    return journal;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public int getMaxResults() {
    return maxResults;
  }

  public Set<String> getAnnotationTypes() {
    return annotationTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AnnotationSearchParameters that = (AnnotationSearchParameters) o;

    if (maxResults != that.maxResults) return false;
    if (annotationTypes != null ? !annotationTypes.equals(that.annotationTypes) : that.annotationTypes != null)
      return false;
    if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
    if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
    if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = journal != null ? journal.hashCode() : 0;
    result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
    result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
    result = 31 * result + (annotationTypes != null ? annotationTypes.hashCode() : 0);
    result = 31 * result + maxResults;
    return result;
  }
}