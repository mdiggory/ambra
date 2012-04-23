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

import java.io.Serializable;

/**
 * View wrapper for rating average values
 * @author Joe Osowski
 */
public class RatingAverage {
  private final int total;
  private final int count;
  private final double average;
  private final double rounded;

  RatingAverage(int total, int count) {
    this.total = total;
    this.count = count;
    average = (count == 0) ? 0 : ((double)total) / count;
    rounded = roundTo(average, 0.5);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RatingAverage that = (RatingAverage) o;

    if (Double.compare(that.average, average) != 0) return false;
    if (count != that.count) return false;
    if (Double.compare(that.rounded, rounded) != 0) return false;
    if (total != that.total) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = total;
    result = 31 * result + count;
    temp = average != +0.0d ? Double.doubleToLongBits(average) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = rounded != +0.0d ? Double.doubleToLongBits(rounded) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
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

  @Override
  public String toString() {
    return "total = " + total + ", count = " + count + ", average = " + average +
      ", rounded = " + rounded;
  }

  public int getTotal() {
    return total;
  }

  public int getCount() {
    return count;
  }

  public double getAverage() {
    return average;
  }

  public double getRounded() {
    return rounded;
  }
}
