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
package org.topazproject.ambra.models;

import java.io.Serializable;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.UriPrefix;

/**
 * RatingContent is the body of a Rating. It stores, insight, reliability, style and comment
 * values.
 *
 * @author stevec
 * @author Jeff Suttor
 */
@UriPrefix("topaz:RatingContent/")
@Entity(graph = "ri", types = {"topaz:RatingContent"})
public class RatingContent implements Serializable, CompetingInterest {
  private static final long serialVersionUID = -8354040136278478548L;

  /**
   * Weight to use for single-rating calculation.
   */
  public static final int INSIGHT_WEIGHT = 6;

  /**
   * Weight to use for single-rating calculation.
   */
  public static final int RELIABILITY_WEIGHT = 5;

  /**
   * Weight to use for single-rating calculation.
   */
  public static final int STYLE_WEIGHT = 4;

  /**
   * Max length of the body content
   */  
  public static final int MAX_TITLE_LENGTH = 500;

  /**
   * Max length of the competing interest statement
   */
  public static final int MAX_COMMENT_LENGTH = 5000;

  /**
   * Max length of the competing interest statement
   */
  public static final int MAX_CISTATEMENT_LENGTH = 5000;  

  private String           id;
  private int              insightValue;
  private int              reliabilityValue;
  private int              styleValue;
  private int              singleRatingValue;
  private String           commentTitle;
  private String           commentValue;
  private String           ciStatement;

  /**
   * Creates a new RatingContent object with default values.
   */
  public RatingContent() {
    this(0, 0, 0, null, null);
  }

  /**
   * Constructor - Used for single ratings (just one overall rating "category")
   * 
   * @param singleRatingValue the rating value to assign
   * @param commentTitle the comment title
   * @param commentValue the comment body
   */
  public RatingContent(int singleRatingValue, String commentTitle, String commentValue) {
    this.singleRatingValue   = singleRatingValue;
    this.commentTitle        = commentTitle;
    this.commentValue        = commentValue;
  }

  /**
   * Creates a new RatingContent object with specified values.
   * 
   * @param insight the insight rating value
   * @param reliability the reliability rating value
   * @param style the style rating value
   * @param commentTitle the comment title
   * @param commentValue the comment body
   */
  public RatingContent(int insight, int reliability, int style, String commentTitle, String commentValue) {
    this.insightValue       = insight;
    this.reliabilityValue   = reliability;
    this.styleValue         = style;
    this.commentTitle       = commentTitle;
    this.commentValue       = commentValue;
  }

  /**
   * Get insightValue value.
   *
   * @return Insight value.
   */
  public int getInsightValue() {
    return insightValue;
  }

  /**
   * Set insightValue value.
   *
   * @param insight value.
   */
  @Predicate
  public void setInsightValue(int insight) {
    this.insightValue = insight;
  }

  /**
   * Get reliabilityValue value.
   *
   * @return Reliability value.
   */
  public int getReliabilityValue() {
    return reliabilityValue;
  }

  /**
   * Set reliabilityValue value.
   *
   * @param reliability value.
   */
  @Predicate
  public void setReliabilityValue(int reliability) {
    this.reliabilityValue = reliability;
  }

  /**
   * Get styleValue value.
   *
   * @return Style value.
   */
  public int getStyleValue() {
    return styleValue;
  }

  /**
   * Set styleValue value.
   *
   * @param style value.
   */
  @Predicate
  public void setStyleValue(int style) {
    this.styleValue = style;
  }

  /**
   * Get single rating value value.
   *
   * @return single rating value
   */
  public int getSingleRatingValue() {
    return singleRatingValue;
  }

  /**
   * Set single rating value.
   *
   * @param singleRatingValue single rating value
   */
  @Predicate
  public void setSingleRatingValue(int singleRatingValue) {
    this.singleRatingValue = singleRatingValue;
  }

  /**
   * Get overall (weighted) value.
   *
   * @return Overall value.
   */
  public double getOverallValue() {
    return calculateOverall(getInsightValue(), getReliabilityValue(), getStyleValue());
  }

  /**
   * Get comment title.
   *
   * @return Comment title.
   */
  public String getCommentTitle() {
    return commentTitle;
  }

  /**
   * Set comment title.
   *
   * @param commentTitle title.
   */
  @Predicate
  public void setCommentTitle(String commentTitle) {
    this.commentTitle = commentTitle;
  }

  /**
   * Get comment value.
   *
   * @return Comment value.
   */
  public String getCommentValue() {
    return commentValue;
  }

  /**
   * Set comment value.
   *
   * @param commentValue value.
   */
  @Predicate
  public void setCommentValue(String commentValue) {
    this.commentValue = commentValue;
  }

  /**
   * Get the competing Interest statement
   * @return the competing interest statement
   */
  public String getCIStatement() {
    return ciStatement;
  }

  /**
   * Set the competing interest statement
   * @param ciStatement The statement to save
   */
  public void setCIStatement(String ciStatement) {
    this.ciStatement = ciStatement;
  }

  /**
   * Gets the id. 
   *
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id The id to set.
   */
  @Id
  @GeneratedValue(uriPrefix = "id:ratingContent/")
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Round a rating.
   *
   * @param x Rating value to round.
   * @param r Typically 0.5, to round to half stars
   *
   * @return Rounded rating value
   */
  public static double roundTo(double x, double r) {
    if (r == 0) {
      return x;
    }

    return Math.round(x * (1 / r)) / (1 / r);
  }

  /**
   * Calculate weighted overall.
   *
   * @param insightValue Insight value.
   * @param reliabilityValue Reliability value.
   * @param styleValue Style value.
   *
   * @return Weighted overall.
   */
  public static double calculateOverall(int insightValue, int reliabilityValue, int styleValue) {
    return calculateOverall((double) insightValue, (double) reliabilityValue, (double) styleValue);
  }

  /**
   * Calculate weighted overall.
   *
   * @param insightValue Insight value.
   * @param reliabilityValue Reliability value.
   * @param styleValue Style value.
   *
   * @return Weighted overall.
   */
  public static double calculateOverall(double insightValue, double reliabilityValue,
                                        double styleValue) {
    int    runningWeight = 0;
    double runningTotal  = 0;

    if (insightValue > 0) {
      runningWeight += RatingContent.INSIGHT_WEIGHT;
      runningTotal += (insightValue * RatingContent.INSIGHT_WEIGHT);
    }

    if (reliabilityValue > 0) {
      runningWeight += RatingContent.RELIABILITY_WEIGHT;
      runningTotal += (reliabilityValue * RatingContent.RELIABILITY_WEIGHT);
    }

    if (styleValue > 0) {
      runningWeight += RatingContent.STYLE_WEIGHT;
      runningTotal += (styleValue * RatingContent.STYLE_WEIGHT);
    }

    return runningTotal / runningWeight;
  }
}
