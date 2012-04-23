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
package org.ambraproject.views;

import org.ambraproject.models.RatingSummary;
import java.io.Serializable;

/**
 * View wrapper for rating averages
 * @author Joe Osowski
 */
public class RatingSummaryView {
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

  private final RatingAverage style;
  private final RatingAverage insight;
  private final RatingAverage reliability;
  private final RatingAverage single;
  private final int     numUsersThatRated;
  private final double  overall;
  private final double  roundedOverall;

  public RatingSummaryView() {
    style = new RatingAverage(0, 0);
    insight = new RatingAverage(0, 0);
    reliability = new RatingAverage(0, 0);
    single = new RatingAverage(0, 0);
    numUsersThatRated = 0;
    overall = 0;
    roundedOverall = 0;
  }

  public RatingSummaryView(RatingSummary ratingSummary) {
    insight = new RatingAverage(ratingSummary.getInsightTotal(),
      ratingSummary.getInsightNumRatings());
    reliability = new RatingAverage(ratingSummary.getReliabilityTotal(),
      ratingSummary.getReliabilityNumRatings());
    style = new RatingAverage(ratingSummary.getStyleTotal(),
      ratingSummary.getStyleNumRatings());
    single = new RatingAverage(ratingSummary.getSingleRatingTotal(),
      ratingSummary.getSingleRatingNumRatings());

    numUsersThatRated = ratingSummary.getUsersThatRated();
    overall = calculateOverall(insight.getAverage(), reliability.getAverage(), style.getAverage());
    roundedOverall = RatingAverage.roundTo(overall, 0.5);
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
   * @param insight Insight value.
   * @param reliability Reliability value.
   * @param style Style value.
   *
   * @return Weighted overall.
   */
  public static double calculateOverall(double insight, double reliability,
                                        double style) {
    int    runningWeight = 0;
    double runningTotal  = 0;

    if (insight > 0) {
      runningWeight += INSIGHT_WEIGHT;
      runningTotal += (insight * INSIGHT_WEIGHT);
    }

    if (reliability > 0) {
      runningWeight += RELIABILITY_WEIGHT;
      runningTotal += (reliability * RELIABILITY_WEIGHT);
    }

    if (style > 0) {
      runningWeight += STYLE_WEIGHT;
      runningTotal += (style * STYLE_WEIGHT);
    }

    return runningTotal / runningWeight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatingSummaryView that = (RatingSummaryView) o;

    if (numUsersThatRated != that.numUsersThatRated) return false;
    if (Double.compare(that.overall, overall) != 0) return false;
    if (Double.compare(that.roundedOverall, roundedOverall) != 0) return false;
    if (insight != null ? !insight.equals(that.insight) : that.insight != null) return false;
    if (reliability != null ? !reliability.equals(that.reliability) : that.reliability != null) return false;
    if (single != null ? !single.equals(that.single) : that.single != null) return false;
    if (style != null ? !style.equals(that.style) : that.style != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = style != null ? style.hashCode() : 0;
    result = 31 * result + (insight != null ? insight.hashCode() : 0);
    result = 31 * result + (reliability != null ? reliability.hashCode() : 0);
    result = 31 * result + (single != null ? single.hashCode() : 0);
    result = 31 * result + numUsersThatRated;
    temp = overall != +0.0d ? Double.doubleToLongBits(overall) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = roundedOverall != +0.0d ? Double.doubleToLongBits(roundedOverall) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "style = [" + style + "], " +
      "insight = [" + insight + "], " +
      "reliability = [" + reliability + "], " +
      "single = [" + single + "], " +
      "numUsersThatRated = " + numUsersThatRated +
      ", overall = " + overall +
      ", roundedOverall = " + roundedOverall;
  }

  public RatingAverage getStyle() {
    return style;
  }

  public RatingAverage getInsight() {
    return insight;
  }

  public RatingAverage getReliability() {
    return reliability;
  }

  public RatingAverage getSingle() {
    return single;
  }

  public int getNumUsersThatRated() {
    return numUsersThatRated;
  }

  public double getOverall() {
    return overall;
  }

  public double getRoundedOverall()
  {
    return roundedOverall;
  }
}

