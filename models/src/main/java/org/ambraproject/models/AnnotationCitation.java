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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds citation data for formal corrections and retractions
 *
 * @author Alex Kudlick 3/7/12
 */
public class AnnotationCitation extends AmbraEntity {

  private String title;
  private String volume;
  private String issue;
  private String journal;
  
  private String publisher;
  
  private String year;
  private String eLocationId;
  private String url;
  
  private String note;
  private String summary;
  
  private List<CorrectedAuthor> authors;
  private List<String> collaborativeAuthors;

  public AnnotationCitation() {
    super();
  }

  /**
   * Copy information from the given article. Does not set the {@link #note} or {@link #summary} properties
   * @param article the article being copied
   */
  public AnnotationCitation(Article article) {
    super();
    this.title = article.getTitle();
    this.volume = article.getVolume();
    this.issue = article.getIssue();
    this.journal = article.getJournal();
    this.publisher = article.getPublisherName();
    this.eLocationId = article.geteLocationId();
    this.url = article.getUrl();
    this.summary = article.getDescription();
    
    //Date formats are not thread safe, need to create a new one or lock
    if (article.getDate() != null) {
      this.year = new SimpleDateFormat("yyyy").format(article.getDate());
    }

    //The article's collections could be persistent collections, so we don't want to copy a reference to them here
    if (article.getCollaborativeAuthors() != null) {
      this.collaborativeAuthors = new ArrayList<String>(article.getCollaborativeAuthors().size());
      for (String collabAuthor : article.getCollaborativeAuthors()) {
        this.collaborativeAuthors.add(collabAuthor);
      }
    }
    if (article.getAuthors() != null) {
      this.authors = new ArrayList<CorrectedAuthor>(article.getAuthors().size());
      for (ArticleAuthor author : article.getAuthors()) {
        this.authors.add(new CorrectedAuthor(author));
      }
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getIssue() {
    return issue;
  }

  public void setIssue(String issue) {
    this.issue = issue;
  }

  public String getJournal() {
    return journal;
  }

  public void setJournal(String journal) {
    this.journal = journal;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getELocationId() {
    return eLocationId;
  }

  public void setELocationId(String eLocationId) {
    this.eLocationId = eLocationId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public List<CorrectedAuthor> getAuthors() {
    return authors;
  }

  public void setAuthors(List<CorrectedAuthor> authors) {
    this.authors = authors;
  }

  public List<String> getCollaborativeAuthors() {
    return collaborativeAuthors;
  }

  public void setCollaborativeAuthors(List<String> collaborativeAuthors) {
    this.collaborativeAuthors = collaborativeAuthors;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AnnotationCitation)) return false;

    AnnotationCitation that = (AnnotationCitation) o;

    if (eLocationId != null ? !eLocationId.equals(that.eLocationId) : that.eLocationId != null) return false;
    if (issue != null ? !issue.equals(that.issue) : that.issue != null) return false;
    if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;
    if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (url != null ? !url.equals(that.url) : that.url != null) return false;
    if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
    if (year != null ? !year.equals(that.year) : that.year != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (volume != null ? volume.hashCode() : 0);
    result = 31 * result + (issue != null ? issue.hashCode() : 0);
    result = 31 * result + (journal != null ? journal.hashCode() : 0);
    result = 31 * result + (year != null ? year.hashCode() : 0);
    result = 31 * result + (eLocationId != null ? eLocationId.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AnnotationCitation{" +
        "title='" + title + '\'' +
        ", volume='" + volume + '\'' +
        ", issue='" + issue + '\'' +
        ", journal='" + journal + '\'' +
        ", year='" + year + '\'' +
        ", eLocationId='" + eLocationId + '\'' +
        '}';
  }
}
