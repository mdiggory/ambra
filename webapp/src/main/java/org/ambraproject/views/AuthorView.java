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

/**
 * Immutable view wrapper around an author
 * @author Alex Kudlick 3/12/12
 */
public class AuthorView {
  private final String givenNames;
  private final String surnames;
  private final String suffix;

  public AuthorView(String givenNames, String surnames, String suffix) {
    this.givenNames = givenNames;
    this.surnames = surnames;
    this.suffix = suffix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AuthorView that = (AuthorView) o;

    if (givenNames != null ? !givenNames.equals(that.givenNames) : that.givenNames != null) return false;
    if (suffix != null ? !suffix.equals(that.suffix) : that.suffix != null) return false;
    if (surnames != null ? !surnames.equals(that.surnames) : that.surnames != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = givenNames != null ? givenNames.hashCode() : 0;
    result = 31 * result + (surnames != null ? surnames.hashCode() : 0);
    result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
    return result;
  }

  public String getGivenNames() {
    return givenNames;
  }

  public String getSurnames() {
    return surnames;
  }

  public String getSuffix() {
    return suffix;
  }
}
