/* $HeadURL::                                                                                     $
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
package org.topazproject.ambra.models;

import org.topazproject.otm.CascadeType;
import org.topazproject.otm.CollectionType;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.Predicate.PropType;
import org.topazproject.otm.annotations.UriPrefix;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Citation information
 *
 * @author Eric Brown
 * @author Amit Kapoor
 */
@Entity(types = {"bibtex:Entry"}, graph = "ri")
@UriPrefix("topaz:")
public class Citation implements Serializable {
  private static final long serialVersionUID = 4580588685312881324L;

  private URI id;
  private String key;
  private Integer year;
  private String displayYear;
  private String month;
  private String day;
  private Integer volumeNumber;
  private String volume;
  private String issue;
  private String title;
  private String publisherLocation;
  private String publisherName;
  private String pages;
  private String eLocationId;
  private String journal;
  private String note;
  private List<String> collaborativeAuthors = new ArrayList<String>();
  private String url;
  private String doi;
  private String summary;
  private String citationType;

  /**
   * This may seem a bit redundant, and it is.  But we want to keep the profiles for people that were cited in articles
   * in a table separate from the UserProfile table (which is where the authors and editors lists live).  We're still
   * keeping the authors list and editors list for annotations for the time being, since those point to valid
   * UserProfiles (since they had to be made by users).  So, authors / editors of Annotations can be found either the
   * authors or the editors list, but authors and editors of articles that were referenced in a plos article are found
   * here.
   * <p/>
   * TODO: make a distinction between these two types of 'Citation' ... In fact, it's not clear that Annotations should
   * have a Citation object
   *
   * The reason why we need Citation for Annotation is that things like FormalCorrection can have modified version
 	 * of the article citation (hand edited in admin panel) and get syndicated out by hand to places like PMC
   */
  private List<CitedPerson> referencedArticleAuthors;
  private List<CitedPerson> referencedArticleEditors;

  /**
   * Annotation's Citation's authors and editors
   */
  private List<ArticleContributor> annotationArticleAuthors;
  private List<ArticleContributor> annotationArticleEditors;

  /**
   * Get id.
   *
   * @return id as URI.
   */
  public URI getId() {
    return id;
  }

  /**
   * Set id.
   *
   * @param id the value to set.
   */
  @Id
  @GeneratedValue(uriPrefix = "id:citation/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * The key or label referencing a citation from within another source (such as an article).
   *
   * @return the key or label for the citation (if available)
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key or label for this citation
   */
  @Predicate(uri = "bibtex:hasKey", dataType = "xsd:string")
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * The year of publication or, for an unpublished work, the year it was written. Generally it should consist of four
   * numerals, such as 1984, although the standard styles can handle any year whose last four nonpunctuation characters
   * are numerals, such as '(about 1984)'
   *
   * @return the year of the citation (if available)
   */
  public Integer getYear() {
    return year;
  }

  /**
   * TODO: Restore to correct datatype. Stored as double because of bug in Mulgara
   *
   * @param year the year of the citation
   */
  @Predicate(uri = "bibtex:hasYear", dataType = "xsd:double")
  public void setYear(Integer year) {
    this.year = year;
  }

  /**
   * The year of publication or, for an unpublished work, the year it was written. The reason for this predicate is
   * because of misuse of this field in references. This field should only be used if the data cannot be mapped to
   * Integer.
   *
   * @return the year of the citation (if available)
   */
  public String getDisplayYear() {
    return displayYear;
  }

  /**
   * @param displayYear the year of the citation
   */
  @Predicate(uri = "plos:temporal#displayYear", dataType = "xsd:string")
  public void setDisplayYear(String displayYear) {
    this.displayYear = displayYear;
  }

  /**
   * @return the month of the citation (if available)
   */
  public String getMonth() {
    return month;
  }

  /**
   * @param month the month of the citation
   */
  @Predicate(uri = "bibtex:hasMonth", dataType = "xsd:string")
  public void setMonth(String month) {
    this.month = month;
  }

  /**
   * @return the day of the citation (if available)
   */
  public String getDay() {
    return day;
  }

  /**
   * @param day the day of the citation
   */
  @Predicate(uri = "bibtex:hasDay", dataType = "xsd:string")
  public void setDay(String day) {
    this.day = day;
  }

  /**
   * @return the volume this citation is in
   */
  public Integer getVolumeNumber() {
    return volumeNumber;
  }

  /**
   * TODO: Restore to correct datatype. Stored as double because of bug in Mulgara
   *
   * @param volumeNumber the volume of this citation
   */
  @Predicate(uri = "bibtex:hasVolume", dataType = "xsd:double")
  public void setVolumeNumber(Integer volumeNumber) {
    this.volumeNumber = volumeNumber;
  }

  /**
   * This should be used only if volumeNumber cannot be used.
   *
   * @return the volume this citation is in
   */
  public String getVolume() {
    return volume;
  }

  /**
   * @param volume the volume of the journal
   */
  @Predicate(uri = "prism:volume", dataType = "xsd:string")
  public void setVolume(String volume) {
    this.volume = volume;
  }

  /**
   * Return the number of a journal, magazine, technical report, or of a work in a series. An issue of a journal or
   * magazine is usually identified by its volume and number; the organization that issues a technical report usually
   * gives it a number; and sometimes books are given numbers in a named series.
   *
   * @return the issue of the citation's article
   */
  public String getIssue() {
    return issue;
  }

  /**
   * @param issue the issue of the citation's article
   */
  @Predicate(uri = "bibtex:hasNumber", dataType = "xsd:string")
  public void setIssue(String issue) {
    this.issue = issue;
  }

  /**
   * Return the title. Typically, a title will be a name by which the resource is formally known.
   *
   * @return the title of the citation's article
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title of the citation's article
   */
  @Predicate(uri = "dc:title", dataType = "rdf:XMLLiteral")
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Return the publisher's location. This is usually the address of the publisher or other type of institution. For
   * major publishing houses, van Leunen recommends omitting the information entirely. For small publishers, on the
   * other hand, you can help the reader by giving the complete address.
   *
   * @return the publisher's location
   */
  public String getPublisherLocation() {
    return publisherLocation;
  }

  /**
   * @param publisherLocation the location of the publisher
   */
  @Predicate(uri = "bibtex:hasAddress", dataType = "xsd:string")
  public void setPublisherLocation(String publisherLocation) {
    this.publisherLocation = publisherLocation;
  }

  /**
   * @return the publisher's name
   */
  public String getPublisherName() {
    return publisherName;
  }

  /**
   * @param publisherName the name of the publisher
   */
  @Predicate(uri = "bibtex:hasPublisher", dataType = "xsd:string")
  public void setPublisherName(String publisherName) {
    this.publisherName = publisherName;
  }

  /**
   * Return the pages. This is one or more page numbers or range of numbers, such as 42-111 or 7,41,73-97 or 43+ (the
   * `+' in this last example indicates pages following that don't form a simple range). To make it easier to maintain
   * Scribe-compatible databases, the standard styles convert a single dash (as in 7-33) to the double dash used in TeX
   * to denote number ranges (as in 7-33).
   *
   * @return the pages the citation is on
   */
  public String getPages() {
    return pages;
  }

  /**
   * @param pages the pages the citation is from
   */
  @Predicate(uri = "bibtex:hasPages", dataType = "xsd:string")
  public void setPages(String pages) {
    this.pages = pages;
  }

  /**
   * Return the start page
   *
   * @return the citation start page
   */
  public String getELocationId() {
    return eLocationId;
  }

  /**
   * Set the citation start page.
   *
   * @param eLocationId the start page
   */
  @Predicate
  public void setELocationId(String eLocationId) {
    this.eLocationId = eLocationId;
  }

  /**
   * The journal name. Abbreviations are provided for many journals; see the Local Guide.
   *
   * @return journal the source of the citation
   */
  public String getJournal() {
    return journal;
  }

  /**
   * @param journal the journal of the citation
   */
  @Predicate(uri = "bibtex:hasJournal", dataType = "xsd:string")
  public void setJournal(String journal) {
    this.journal = journal;
  }

  /**
   * A note is any additional information that can help the reader. The first word should be capitalized.
   *
   * @return the note associated with this citation
   */
  public String getNote() {
    return note;
  }

  /**
   * @param note the note for this citation
   */
  @Predicate(uri = "bibtex:hasNote", dataType = "xsd:string")
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * @return the COLLABORATORS ON this citation
   */
  public List<String> getCollaborativeAuthors() {
    return collaborativeAuthors;
  }

  /**
   * @param collaborativeAuthors the collaborators on this citation
   */
  @Predicate(uri = "plos:hasCollaborativeAuthorList", collectionType = CollectionType.RDFSEQ,
      cascade = {CascadeType.child})
  public void setCollaborativeAuthors(List<String> collaborativeAuthors) {
    this.collaborativeAuthors = collaborativeAuthors;
  }

  /**
   * The WWW Universal Resource Locator that points to the item being referenced. This often is used for technical
   * reports to point to the ftp or web site where the postscript source of the report is located.
   *
   * @return the URL for the object
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the URL for the object
   */
  @Predicate(uri = "bibtex:hasURL", dataType = "xsd:string")
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return DOI for the citation
   */
  public String getDoi() {
    return doi;
  }

  /**
   * @param doi the DOI of the citation
   */
  @Predicate(uri = "dc:identifier")
  public void setDoi(String doi) {
    this.doi = doi;
  }

  /**
   * Return the abstract/summary of the object.
   *
   * @return the abstract/summary of the object
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Set the abstract/summary of the object
   *
   * @param summary the summary/abstract of the object
   */
  @Predicate(uri = "bibtex:hasAbstract", dataType = "xsd:string")
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Set the citation type. Bibtex specifies different type of citations and this field is intended to track that.
   * Please note that the string passed should be a valid URI.
   *
   * @param citationType the string representation of the URI for the type
   * @throws IllegalArgumentException if the string is not a valid URI.
   */
  @Predicate(uri = "rdf:type", type = PropType.OBJECT)
  public void setCitationType(String citationType) {
    if (citationType == null) {
      this.citationType = null;
    } else {
      assert URI.create(citationType) != null : "Invalid Ambra Citation Type" + citationType;
      this.citationType = citationType;
    }
  }

  /**
   * Return the type of the citation. The returned string is an URI.
   *
   * @return the citation type as a string representation of a URI.
   */
  public String getCitationType() {
    return citationType;
  }

  /**
   * This may seem a bit redundant, and it is.  But we want to keep the profiles for people that were cited in articles
   * in a table separate from the UserProfile table (which is where the authors and editors lists live).  We're still
   * keeping the authors list and editors list for annotations for the time being, since those point to valid
   * UserProfiles (since they had to be made by users).  So, authors / editors of Annotations can be found either the
   * authors or the editors list, but authors and editors of articles that were referenced in a plos article are found
   * here.
   * <p/>
   * TODO: make a distinction between these two types of 'Citation' ... In fact, it's not clear that Annotations should
   * have a Citation object
   */
  public List<CitedPerson> getReferencedArticleAuthors() {
    return referencedArticleAuthors;
  }

  /**
   * This may seem a bit redundant, and it is.  But we want to keep the profiles for people that were cited in articles
   * in a table separate from the UserProfile table (which is where the authors and editors lists live).  We're still
   * keeping the authors list and editors list for annotations for the time being, since those point to valid
   * UserProfiles (since they had to be made by users).  So, authors / editors of Annotations can be found either the
   * authors or the editors list, but authors and editors of articles that were referenced in a plos article are found
   * here.
   * <p/>
   * TODO: make a distinction between these two types of 'Citation' ... In fact, it's not clear that Annotations should
   * have a Citation object
   */
  public void setReferencedArticleAuthors(List<CitedPerson> referencedArticleAuthors) {
    this.referencedArticleAuthors = referencedArticleAuthors;
  }

  public List<CitedPerson> getReferencedArticleEditors() {
    return referencedArticleEditors;
  }

  public void setReferencedArticleEditors(List<CitedPerson> referencedArticleEditors) {
    this.referencedArticleEditors = referencedArticleEditors;
  }

  public List<ArticleContributor> getAnnotationArticleAuthors() {
    return annotationArticleAuthors;
  }

  public void setAnnotationArticleAuthors(List<ArticleContributor> annotationArticleAuthors) {
    this.annotationArticleAuthors = annotationArticleAuthors;
  }

  public List<ArticleContributor> getAnnotationArticleEditors() {
    return annotationArticleEditors;
  }

  public void setAnnotationArticleEditors(List<ArticleContributor> annotationArticleEditors) {
    this.annotationArticleEditors = annotationArticleEditors;
  }
}
