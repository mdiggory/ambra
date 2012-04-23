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
 * Types of annotations
 */
public enum AnnotationType {
  COMMENT("Comment"), //comment on an article
  RATING("Rating"),
  REPLY("Reply"),
  NOTE("Note"), //Inline note
  MINOR_CORRECTION("MinorCorrection"),
  FORMAL_CORRECTION("FormalCorrection"),
  RETRACTION("Retraction");

  private String string;

  private AnnotationType(String string) {
    this.string = string;
  }

  public boolean isCorrection() {
    return MINOR_CORRECTION == this || FORMAL_CORRECTION == this || RETRACTION == this;
  }

  @Override
  public String toString() {
    return this.string;
  }

  public static AnnotationType fromString(String string) {
    if (COMMENT.string.equals(string)) {
      return COMMENT;
    } else if (NOTE.string.endsWith(string)) {
      return NOTE;
    } else if (RATING.string.equals(string)) {
      return RATING;
    } else if (REPLY.string.equals(string)) {
      return REPLY;
    } else if (MINOR_CORRECTION.string.equals(string)) {
      return MINOR_CORRECTION;
    } else if (FORMAL_CORRECTION.string.equals(string)) {
      return FORMAL_CORRECTION;
    } else if (RETRACTION.string.equals(string)) {
      return RETRACTION;
    } else {
      throw new IllegalArgumentException("Unknown annotation type: " + string);
    }
  }
}