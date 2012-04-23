/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.ambraproject.views;

import java.util.Date;
import org.ambraproject.models.Rating;
import org.ambraproject.util.TextUtils;

/**
 * Article ratings &amp; comments by user.
 *
 * Data structures used by display &amp; browse for the ratings &amp; comments for an Article by
 * user.
 */
public class RatingView {
  private final Long   ratingId;
  private final Date   created;
  private final Long creatorID;
  private final String creatorName;
  private final int    style;
  private final int    insight;
  private final int    reliability;
  private final double overall;
  private final int    singleRating;
  private final String commentTitle;
  private final String commentValue;
  private final String ciStatement;

  public RatingView(Rating rating) {
    this.ratingId    = rating.getID();
    this.insight     = rating.getInsight();
    this.style       = rating.getStyle();
    this.reliability = rating.getReliability();
    this.singleRating = rating.getSingleRating();
    this.overall     = RatingSummaryView.calculateOverall(this.insight, this.reliability, this.style);

    // escape any markup
    this.commentTitle = TextUtils.escapeHtml(rating.getTitle());
    this.commentValue = TextUtils.escapeHtml(rating.getBody());
    this.ciStatement = TextUtils.escapeHtml(rating.getCompetingInterestBody());

    this.created = rating.getCreated();
    this.creatorID = rating.getCreator().getID();
    this.creatorName = rating.getCreator().getDisplayName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatingView that = (RatingView) o;

    if (insight != that.insight) return false;
    if (Double.compare(that.overall, overall) != 0) return false;
    if (reliability != that.reliability) return false;
    if (singleRating != that.singleRating) return false;
    if (style != that.style) return false;
    if (ciStatement != null ? !ciStatement.equals(that.ciStatement) : that.ciStatement != null) return false;
    if (commentTitle != null ? !commentTitle.equals(that.commentTitle) : that.commentTitle != null) return false;
    if (commentValue != null ? !commentValue.equals(that.commentValue) : that.commentValue != null) return false;
    if (created != null ? !created.equals(that.created) : that.created != null) return false;
    if (creatorName != null ? !creatorName.equals(that.creatorName) : that.creatorName != null) return false;
    if (creatorID != null ? !creatorID.equals(that.creatorID) : that.creatorID != null) return false;
    if (ratingId != null ? !ratingId.equals(that.ratingId) : that.ratingId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = ratingId != null ? ratingId.hashCode() : 0;
    result = 31 * result + (created != null ? created.hashCode() : 0);
    result = 31 * result + (creatorID != null ? creatorID.hashCode() : 0);
    result = 31 * result + (creatorName != null ? creatorName.hashCode() : 0);
    result = 31 * result + style;
    result = 31 * result + insight;
    result = 31 * result + reliability;
    temp = overall != +0.0d ? Double.doubleToLongBits(overall) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + singleRating;
    result = 31 * result + (commentTitle != null ? commentTitle.hashCode() : 0);
    result = 31 * result + (commentValue != null ? commentValue.hashCode() : 0);
    result = 31 * result + (ciStatement != null ? ciStatement.hashCode() : 0);
    return result;
  }

  public String getRatingId() {
    return ratingId.toString();
  }

  public Date getCreated() {
    return created;
  }

  public long getCreatedMillis() {
    return created.getTime();
  }

  public Long getCreatorID() {
    return creatorID;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public int getStyle() {
    return style;
  }

  public int getInsight() {
    return insight;
  }

  public int getReliability() {
    return reliability;
  }

  public double getOverallRounded() {
    return RatingAverage.roundTo(overall, 0.5);
  }

  public int getSingleRating() {
    return singleRating;
  }

  public String getCommentTitle() {
    return commentTitle;
  }

  public String getCommentValue() {
    return commentValue;
  }

  public String getCIStatement() {
    return this.ciStatement;
  }
}
