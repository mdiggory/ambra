/* $HeadURL::                                                                            $
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

package org.ambraproject.annotation;

import java.util.Comparator;
import java.util.Date;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ambraproject.annotation.service.WebAnnotation;
import org.ambraproject.annotation.service.WebReply;
import org.ambraproject.util.DateParser;
import org.ambraproject.util.InvalidDateException;

/**
 * Simple wrapper class around an Annotation and the associated list of replies.
 * Implements the compartor interface to sort by reverse chronological order.
 *
 * @author Stephen Cheng
 *
 */
public class Commentary {
  private WebAnnotation annotation;
  private int numReplies;
  private String lastModified;
  private WebReply[]replies;
  private static final Logger log = LoggerFactory.getLogger(Commentary.class);

  public Commentary() {
  }

  /**
   * @return Returns the annotation.
   */
  public WebAnnotation getAnnotation() {
    return annotation;
  }

  /**
   * @param annotation The annotation to set.
   */
  public void setAnnotation(WebAnnotation annotation) {
    this.annotation = annotation;
  }

  /**
   * @return Returns the replies.
   */
  public WebReply[] getReplies() {
    return replies;
  }

  /**
   * @param replies The replies to set.
   */
  public void setReplies(WebReply[] replies) {
    this.replies = replies;
  }

  /**
   * @return Returns the lastModified.
   */
  public String getLastModified() {
    return lastModified;
  }

  /**
   * @param lastModified The lastModified to set.
   */
  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModifiedAsDate() {
    String theDate;

    if (lastModified == null) {
      theDate = annotation.getCreated();
    } else {
      theDate = lastModified;
    }
    try {
      if (log.isDebugEnabled()) {
        log.debug("parsing date for reply: " + this.annotation.getId() +
                  "; dateString is: " + theDate);
      }
      return DateParser.parse (theDate);
    } catch (InvalidDateException ide) {
      log.error("Could not parse date for commnetary: " + this.annotation.getId() +
                "; dateString is: " + theDate, ide);
    }
    return null;
  }

  /**
   * @return Returns the numReplies.
   */
  public int getNumReplies() {
    return numReplies;
  }

  /**
   * @param numReplies The numReplies to set.
   */
  public void setNumReplies(int numReplies) {
    this.numReplies = numReplies;
  }

  public static class Sorter implements Comparator<Commentary>, Serializable {
    /**
     * This comparator does a reverse sort based on the last reply to the annotation.  If not replies
     * are present, the annotation time is used.
     *
     * @param a the first Commentary object to be compared
     * @param b the second Commentary object to be compared
     * @return a number less than 0 if <var>a</var> less than <var>b</var>, a number greater
     *         than 0 if <var>a</var> greater than <var>b</var>, or 0 if
     *         <var>a</var> equals <var>b</var>
     */
    public int compare (Commentary a, Commentary b){
      String dateA, dateB;
      if (a.getNumReplies() == 0) {
        dateA = a.getAnnotation().getCreated();
      } else {
        dateA = a.getLastModified();
      }
      if (b.getNumReplies() == 0) {
        dateB = b.getAnnotation().getCreated();
      } else {
        dateB = b.getLastModified();
      }
      return dateB.compareTo(dateA);
    }
  }
}
