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
package org.ambraproject.rating.action;

import java.net.URI;
import java.util.Date;

import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingContent;
import org.ambraproject.util.TextUtils;

/**
 * Article ratings &amp; comments by user.
 *
 * Data structures used by display &amp; browse for the ratings &amp; comments for an Article by
 * user.
 */
public class ArticleRatingSummary {
  private URI    ratingId;
  private String articleURI;
  private String articleTitle;
  private Date   created;
  private String creatorURI;
  private String creatorName;
  private int    style;
  private int    insight;
  private int    reliability;
  private double overall;
  private int    singleRating;
  private String commentTitle;
  private String commentValue;
  private String ciStatement;

  public ArticleRatingSummary(String articleURI, String articleTitle) {
    this.articleURI = articleURI;
    this.articleTitle = articleTitle;
  }

  public void setRating(Rating rating) {
    this.ratingId    = rating.getId();
    this.insight     = rating.getBody().getInsightValue();
    this.style       = rating.getBody().getStyleValue();
    this.reliability = rating.getBody().getReliabilityValue();
    this.overall     = rating.getBody().getOverallValue();
    this.singleRating = rating.getBody().getSingleRatingValue();

    // escape any markup
    this.commentTitle = TextUtils.escapeHtml(rating.getBody().getCommentTitle());
    this.commentValue = TextUtils.escapeHtml(rating.getBody().getCommentValue());
    this.ciStatement = TextUtils.escapeHtml(rating.getBody().getCIStatement());

    this.creatorURI = rating.getCreator();
  }

  /**
   * Get the Rating Id as a String.
   *
   * @return Rating Id as a String.
   */
  public String getRatingId() {
    return ratingId.toString();
  }

  public void setArticleURI(String articleURI) {
    this.articleURI = articleURI;
  }
  public String getArticleURI() {
    return articleURI;
  }

  public void setArticleTitle(String articleTitle) {
    this.articleTitle = articleTitle;
  }
  public String getArticleTitle() {
    return articleTitle;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
  public Date getCreated() {
    return created;
  }

  public long getCreatedMillis() {
    return created.getTime();
  }

  public void setCreatorURI(String creatorURI) {
    this.creatorURI = creatorURI;
  }
  public String getCreatorURI() {

    return creatorURI;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setStyle(int style) {
    this.style = style;
  }
  public int getStyle() {
    return style;
  }

  public void setInsight(int insight) {
    this.insight = insight;
  }

  public int getInsight() {
    return insight;
  }

  public void setReliability(int reliability) {
    this.reliability = reliability;
  }
  public int getReliability() {
    return reliability;
  }

  public double getOverallRounded() {
    return RatingContent.roundTo(overall, 0.5);
  }

  public void setSingleRating(int singleRating) {
    this.singleRating = singleRating;
  }

  public int getSingleRating() {
    return singleRating;
  }

  /**
   * Set comment title.
   *
   * Escape HTML markup for protection.
   *
   * @param title Comment title.
   */
  public void setCommentTitle(String title) {

    // protect against markup
    this.commentTitle = TextUtils.escapeHtml(title);
  }

  public String getCommentTitle() {
    return commentTitle;
  }

  /**
   * Set comment value.
   *
   * Escape HTML markup for protection.
   *
   * @param value Comment value.
   */
  public void setCommentValue(String value) {
    // protect against markup
    this.commentValue = TextUtils.escapeHtml(value);
  }

  public String getCommentValue() {
    return commentValue;
  }

  /**
   * Set the CI statment
   * @param value the CI Statement value
   */
  public void setCIStatement(String value) {
    // protect against markup
    this.ciStatement = TextUtils.escapeHtml(value);
  }

  /**
  * Return the CIStatement.
  * @return CIStatement as String.
  */
  public String getCIStatement() {
    return this.ciStatement;
  }
}
