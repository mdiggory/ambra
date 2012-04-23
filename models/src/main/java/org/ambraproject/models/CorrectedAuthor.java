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
public class CorrectedAuthor extends AmbraEntity {
  
  private String givenNames;
  private String surName;
  private String suffix;

  public CorrectedAuthor() {
    super();
  }

  public CorrectedAuthor(ArticleAuthor author) {
    super();
    this.givenNames = author.getGivenNames();
    this.surName = author.getSurnames();
    this.suffix = author.getSuffix();
  }

  public CorrectedAuthor(String givenNames, String surName, String suffix) {
    super();
    this.givenNames = givenNames;
    this.surName = surName;
    this.suffix = suffix;
  }

  public String getGivenNames() {
    return givenNames;
  }

  public void setGivenNames(String givenNames) {
    this.givenNames = givenNames;
  }

  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CorrectedAuthor)) return false;

    CorrectedAuthor that = (CorrectedAuthor) o;

    if (givenNames != null ? !givenNames.equals(that.givenNames) : that.givenNames != null) return false;
    if (suffix != null ? !suffix.equals(that.suffix) : that.suffix != null) return false;
    if (surName != null ? !surName.equals(that.surName) : that.surName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = givenNames != null ? givenNames.hashCode() : 0;
    result = 31 * result + (surName != null ? surName.hashCode() : 0);
    result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CorrectedAuthor{" +
        "givenNames='" + givenNames + '\'' +
        ", surName='" + surName + '\'' +
        ", suffix='" + suffix + '\'' +
        '}';
  }
}
