/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

/**
 * @author Alex Kudlick 3/7/12
 */
public class Rating extends Annotation {

  private Integer insight = 0;
  private Integer reliability = 0;
  private Integer style = 0;
  private Integer singleRating = 0;

  public Rating() {
    super();
    setType(AnnotationType.RATING);
  }

  public Rating(UserProfile creator, Long articleID) {
    super(creator, AnnotationType.RATING, articleID);
  }

  public Integer getInsight() {
    return insight;
  }

  public void setInsight(Integer insight) {
    this.insight = insight;
  }

  public Integer getReliability() {
    return reliability;
  }

  public void setReliability(Integer reliability) {
    this.reliability = reliability;
  }

  public Integer getStyle() {
    return style;
  }

  public void setStyle(Integer style) {
    this.style = style;
  }

  public Integer getSingleRating() {
    return singleRating;
  }

  public void setSingleRating(Integer singleRating) {
    this.singleRating = singleRating;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Rating)) return false;
    if (!super.equals(o)) return false;

    Rating rating = (Rating) o;

    if (insight != null ? !insight.equals(rating.insight) : rating.insight != null) return false;
    if (singleRating != null ? !singleRating.equals(rating.singleRating) : rating.singleRating != null) return false;
    if (reliability != null ? !reliability.equals(rating.reliability) : rating.reliability != null) return false;
    if (style != null ? !style.equals(rating.style) : rating.style != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (insight != null ? insight.hashCode() : 0);
    result = 31 * result + (reliability != null ? reliability.hashCode() : 0);
    result = 31 * result + (style != null ? style.hashCode() : 0);
    result = 31 * result + (singleRating != null ? singleRating.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Rating{" +
        "article=" + getArticleID() +
        ", insight=" + insight +
        ", reliability=" + reliability +
        ", style=" + style +
        ", singleRating=" + singleRating +
        '}';
  }
}
