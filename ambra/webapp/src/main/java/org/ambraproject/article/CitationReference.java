/* $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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

package org.ambraproject.article;

import java.util.ArrayList;

public class CitationReference {
  private final String JOURNAL_TYPE = "journal";
  private final String OTHER_TYPE = "other";

  private String citationType;
  private String title;
  private String journalTitle;
  private String volume;
  private String number;
  private String pages;
  private String year;
  private String publisher;

  private ArrayList<String> authors = new ArrayList<String>();

  public String getCitationType() {
    return citationType;
  }

  public void setCitationType(String citationType) {
    this.citationType = citationType;
  }
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getJournalTitle() {
    return journalTitle;
  }

  public void setJournalTitle(String journalTitle) {
    this.journalTitle = journalTitle;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public ArrayList<String> getAuthors() {
    return authors;
  }

  public void setAuthors(ArrayList<String> authors) {
    this.authors = authors;
  }
  
  public String getReferenceContent() {
    StringBuffer sb = new StringBuffer();
    
    if (title != null) {
      sb.append("citation_title=").append(this.title).append("; ");
    }

    for (String author: authors) {
      sb.append("citation_author=").append(author).append("; ");
    }
    
    if (journalTitle != null) {
      if (citationType != null) {
        if (citationType.equals(JOURNAL_TYPE)) {
          sb.append("citation_journal_title=").append(journalTitle).append("; ");
        } else if (citationType.equals(OTHER_TYPE)) {
          sb.append("citation_title=").append(journalTitle).append("; ");
        }
      }
    }
    if (volume != null) {
      sb.append("citation_volume=").append(volume).append("; ");
    }
    if (number != null) {
      sb.append("citation_number=").append(number).append("; ");
    }
    if (pages != null) {
      sb.append("citation_pages=").append(pages).append("; ");
    }
    if (year != null) {
      sb.append("citation_date=").append(year).append("; ");
    }
    if (publisher != null) {
      sb.append("citation_publisher=").append(publisher).append("; ");
    }
    
    return sb.toString();
  }
  

}
