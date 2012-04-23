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

import org.ambraproject.models.AnnotationCitation;
import org.ambraproject.models.CorrectedAuthor;

import java.util.Arrays;

/**
 * Immutable wrapper around {@link AnnotationCitation} for display layer
 * @author Alex Kudlick 3/12/12
 */
public class AnnotationCitationView {
  
  private final Long ID;
  private final AuthorView[] authors;
  private final String[] collabAuthors;
  private final String title;
  private final String journal;
  private final String eLocationId;
  private final String year;
  private final String volume;
  private final String note;
  private final String summary;
  private final String issue;

  public AnnotationCitationView(AnnotationCitation citation) {
    this.ID = citation.getID();
    this.title = citation.getTitle();
    this.journal = citation.getJournal();
    this.eLocationId = citation.getELocationId();
    this.year = citation.getYear();
    this.volume = citation.getVolume();
    this.note = citation.getNote();
    this.summary = citation.getSummary();
    this.issue = citation.getIssue();

    if (citation.getCollaborativeAuthors() != null) {
      this.collabAuthors = citation.getCollaborativeAuthors().toArray(new String[citation.getCollaborativeAuthors().size()]);
    } else {
      this.collabAuthors = null;
    }

    if (citation.getAuthors() != null) {
      this.authors = new AuthorView[citation.getAuthors().size()];
      for (int i = 0; i < citation.getAuthors().size(); i++) {
        CorrectedAuthor author = citation.getAuthors().get(i);
        this.authors[i] = new AuthorView(author.getGivenNames(), author.getSurName(), author.getSuffix());
      }
    } else {
      this.authors = null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AnnotationCitationView that = (AnnotationCitationView) o;

    if (!Arrays.equals(authors, that.authors)) return false;
    if (!Arrays.equals(collabAuthors, that.collabAuthors)) return false;
    if (eLocationId != null ? !eLocationId.equals(that.eLocationId) : that.eLocationId != null) return false;
    if (issue != null ? !issue.equals(that.issue) : that.issue != null) return false;
    if (journal != null ? !journal.equals(that.journal) : that.journal != null) return false;
    if (note != null ? !note.equals(that.note) : that.note != null) return false;
    if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
    if (year != null ? !year.equals(that.year) : that.year != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = authors != null ? Arrays.hashCode(authors) : 0;
    result = 31 * result + (collabAuthors != null ? Arrays.hashCode(collabAuthors) : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (journal != null ? journal.hashCode() : 0);
    result = 31 * result + (eLocationId != null ? eLocationId.hashCode() : 0);
    result = 31 * result + (year != null ? year.hashCode() : 0);
    result = 31 * result + (volume != null ? volume.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    result = 31 * result + (issue != null ? issue.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AnnotationCitationView{" +
        "authors=" + (authors == null ? null : Arrays.asList(authors)) +
        ", collabAuthors=" + (collabAuthors == null ? null : Arrays.asList(collabAuthors)) +
        ", title='" + title + '\'' +
        ", journal='" + journal + '\'' +
        ", eLocationId='" + eLocationId + '\'' +
        ", year='" + year + '\'' +
        ", volume='" + volume + '\'' +
        ", note='" + note + '\'' +
        ", summary='" + summary + '\'' +
        ", issue='" + issue + '\'' +
        '}';
  }

  public AuthorView[] getAuthors() {
    return authors.clone();
  }

  public String[] getCollabAuthors() {
    return collabAuthors.clone();
  }

  public String getTitle() {
    return title;
  }

  public String getJournal() {
    return journal;
  }

  public String geteLocationId() {
    return eLocationId;
  }

  public String getYear() {
    return year;
  }

  public Long getID() {
    return ID;
  }

  public String getVolume() {
    return volume;
  }

  public String getNote() {
    return note;
  }

  public String getSummary() {
    return summary;
  }

  public String getIssue() {
    return issue;
  }
}
