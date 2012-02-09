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
 */
public class AnnotationFeedCacheKey implements Serializable, Comparable {

  private static final long serialVersionUID = 2340342378978L;

  public enum Type {ANNOTATIONS, REPLIES}

  private final String journal;
  private final Type type;
  private final Date startDate;
  private final Date endDate;
  private Set<String> annotationTypes;
  private final int maxResults;
  private final boolean useCache;
  private final String formatting;

  private final int hashCode;

  public AnnotationFeedCacheKey(Type type, ArticleFeedCacheKey feedCacheKey) {
    this.journal = feedCacheKey.getJournal();
    this.type = type;
    this.startDate = feedCacheKey.getSDate();
    this.endDate = feedCacheKey.getEDate();
    this.maxResults = feedCacheKey.getMaxResults();
    if (feedCacheKey.feedType() != FeedService.FEED_TYPES.Annotation) {
      HashSet<String> types = new HashSet<String>();
      types.add(feedCacheKey.feedType().rdfType());
      this.annotationTypes = types;
    }
    this.useCache = feedCacheKey.isUseCache();
    this.formatting = feedCacheKey.getFormatting();

    this.hashCode = calculateHash();
  }

  private int calculateHash() {
    int inc = 7;

    int hash = 463 + this.journal.hashCode()
        + this.type.hashCode() * inc;

    if (this.startDate != null) {
      hash = (hash * inc) + this.startDate.hashCode();
    }
    if (this.endDate != null) {
      hash = (hash * inc) + this.endDate.hashCode();
    }
    hash = (hash * inc) + this.maxResults;
    if (this.annotationTypes != null) {
      for (String type : this.annotationTypes) {
        hash = (hash * inc) + type.hashCode();
      }
    }
    return hash;
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

  public Type getType() {
    return type;
  }

  public Set<String> getAnnotationTypes() {
    return annotationTypes;
  }

  public boolean isUseCache() {
    return useCache;
  }

  public String getFormatting() {
    return formatting;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("AnnotationFeedCacheKey{")
        .append("journal=").append(journal)
        .append(", type=").append(type);
    if (startDate != null) {
      result.append(", startDate=").append(startDate);
    }
    if (endDate != null) {
      result.append(", endDate=").append(endDate);
    }
    if (annotationTypes != null && annotationTypes.size() > 0) {
      result.append(", annotationTypes=");
      boolean first = true;
      for (String annotationType : annotationTypes) {
        if (first)
          first = false;
        else
          result.append(';');
        result.append(annotationType);
      }
    }
    result.append(", maxResults=").append(maxResults);
    result.append(", useCache=").append(useCache);
    if (formatting != null) {
      result.append(", formatting=").append(formatting);
    }
    result.append('}');
    return result.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof AnnotationFeedCacheKey))
      return false;

    if (o == this)
      return true;

    AnnotationFeedCacheKey key = (AnnotationFeedCacheKey)o;

    if (!key.getJournal().equals(this.journal))
      return false;

    if (key.getType() != this.type)
      return false;

    if (!dateEquals(key.getStartDate(), this.startDate))
      return false;

    if (!dateEquals(key.getEndDate(), this.endDate))
      return false;

    if (key.getMaxResults() != this.maxResults)
      return false;

    if (key.getAnnotationTypes() != null && key.getAnnotationTypes().size() > 0) {
      if (this.annotationTypes == null)
        return false;
      if (this.annotationTypes.size() != key.getAnnotationTypes().size())
        return false;

      for (String type : key.getAnnotationTypes()) {
        if (!this.annotationTypes.contains(type))
          return false;
      }

    } else {
      if (this.annotationTypes != null && this.annotationTypes.size() > 0)
        return false;
    }

    return true;
  }

  private boolean dateEquals(Date date, Date thisDate) {
    if (date == null) {
      if (thisDate != null)
        return false;
    } else {
      if (!date.equals(thisDate))
        return false;
    }
    return true;
  }

  public int compareTo(Object o) {
    if (!(o instanceof AnnotationFeedCacheKey))
      return -1;

    AnnotationFeedCacheKey key = (AnnotationFeedCacheKey)o;

    if (!key.getJournal().equals(this.journal)) {
      return this.journal.compareTo(key.journal);
    }

    if (key.getType() != this.type) {
      if (key.getType() == Type.ANNOTATIONS)
        return 1;
      else
        return -1;
    }

    int cs = compareDates(key.getStartDate(), this.startDate);
    if (cs != 0)
      return cs;

    int ce = compareDates(key.getEndDate(), this.endDate);
    if (ce != 0)
      return ce;

    if (key.getMaxResults() != this.maxResults)
      return key.getMaxResults() < this.maxResults ? 1: -1;

    if (key.getAnnotationTypes() == null || key.getAnnotationTypes().size() == 0) {
      if (this.annotationTypes != null && this.annotationTypes.size() > 0)
        return 1;
    } else {
      if (this.annotationTypes == null)
        return -1;

      if (this.annotationTypes.size() > key.getAnnotationTypes().size()) {
        return 1;
      } else if (this.annotationTypes.size() < key.getAnnotationTypes().size()) {
        return -1;
      } else {
        // sets are of the same length
        Iterator<String> keyIterator = new TreeSet<String>(key.getAnnotationTypes()).iterator();
        Iterator<String> thisIterator = new TreeSet<String>(this.annotationTypes).iterator();
        while (keyIterator.hasNext()) {
          String keyType =  keyIterator.next();
          String thisType =  thisIterator.next();
          int ct = thisType.compareTo(keyType);
          if (ct != 0)
            return ct;
        }

      }
    }
      
    return 0;
  }

  private int compareDates(Date date, Date thisDate) {
    if (date == null) {
      if (thisDate != null)
        return 1;
      else
        return 0;
    } else {
      if (thisDate == null)
        return -1;
      else
        return thisDate.compareTo(date);

    }
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

}
