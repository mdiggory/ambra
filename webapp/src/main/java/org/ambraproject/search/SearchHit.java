/*
 * $HeadURL$
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
package org.ambraproject.search;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Value object that holds the result of a single search item
 *
 */
public class SearchHit implements Serializable {

  private static final long serialVersionUID = 2450207404766168639L;

  private final float  hitScore;
  private final String uri;
  private final String title;
  private final String highlight;
  private final Date   date;
  private final String creator;
  private final Collection<String> listOfCreators;
  private final String issn;
  private final String journalTitle;
  private final String articleTypeForDisplay;
  private String abstractPrimary;

  /**
   * Create a search hit with the values set
   *
   * @param hitScore Hit score
   * @param uri Article ID
   * @param title Article title
   * @param highlight Highlights
   * @param creators Creators
   * @param date Article date
   * @param issn eIssn of the journal
   * @param journalTitle Journal title
   * @param articleTypeForDisplay Article type
   */
  public SearchHit(Float hitScore, String uri, String title, String highlight,
                   Collection<String> creators, Date date, String issn,
                   String journalTitle, String articleTypeForDisplay) {
    if (hitScore == null) {
      this.hitScore   = 0f;
    } else {
      this.hitScore   = hitScore;
    }
    this.uri        = uri;
    this.title      = title;
    this.highlight  = highlight;
    this.creator    = StringUtils.join(creators, ", ");
    this.listOfCreators = creators;
    this.date       = date;
    this.issn      = issn;
    this.journalTitle = journalTitle;
    this.articleTypeForDisplay = articleTypeForDisplay;
  }

  /**
   * @return the hit object's uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Getter for property 'hitScore'.
   * @return Value for property 'hitScore'.
   */
  public float getHitScore() {
    return hitScore;
  }

  /**
   * Getter for property 'creator'.
   * @return Value for property 'creator'.
   */
  public String getCreator() {
    return creator;
  }

  /**
   * Getter for property 'date'.
   * @return Value for property 'date'.
   */
  public Date getDate() {
    return date;
  }

  /**
   * Getter for property 'highlight'.
   * @return Value for property 'highlight'.
   */
  public String getHighlight() {
    return highlight;
  }

  /**
   * Getter for property 'title'.
   * @return Value for property 'title'.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get the issn for the Journal to which this SearchHit belongs.
   * @return The issn for the Journal to which this SearchHit belongs.
   */
  public String getIssn() {
    return issn;
  }

  /**
   * Get the Dublin Core Title for the Journal to which this SearchHit belongs.
   * @return Dublin Core Title for the Journal to which this SearchHit belongs.
   */
  public String getJournalTitle() {
    return journalTitle;
  }

  /**
   * Get the type of the Article as a String ready for display.
   * @return Type of the Article as a String ready for display
   */
  public String getArticleTypeForDisplay() {
    return articleTypeForDisplay;
  }

  /**
   * sets the primary abstract
   * @param newAbstract
   */
  public void setAbstractPrimary(String newAbstract) {
    this.abstractPrimary = newAbstract;
  }

  /**
   * Get the primary abstract
   * (abstract element without any attributes)
   * @return primary abstract
   */
  public String getAbstractPrimary() {
    return abstractPrimary;
  }

  /**
   * Get the creators in a list
   * @return
   */
  public Collection<String> getListOfCreators() {
    return this.listOfCreators;
  }

  @Override
  public String toString() {
    return "SearchHit{" +
        "hitScore=" + hitScore +
        ", uri='" + uri + '\'' +
        ", title='" + title + '\'' +
        ", highlight='" + highlight + '\'' +
        ", date=" + date +
        ", creator='" + creator + '\'' +
        ", issn='" + issn + '\'' +
        ", journalTitle='" + journalTitle + '\'' +
        ", articleTypeForDisplay='" + articleTypeForDisplay + '\'' +
        ", abstractPrimaryDisplay='" + abstractPrimary + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SearchHit searchHit = (SearchHit) o;

    return uri.equals(searchHit.uri);

  }

  @Override
  public int hashCode() {
    return uri.hashCode();
  }

}