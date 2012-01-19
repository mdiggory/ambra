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

package org.ambraproject.crossref;

/**
 * Object that stores CrossRef article metadata.
 * <p/>
 * See <a href="http://www.crossref.org/help/Content/04_Queries_and_retrieving/Piped%20Queries.htm">CrossRef Piped Queries</a>
 * <p/>
 * Field descriptions:
 * <p/>
 * ISBN, ISBN/ISSN: Journal, book or conference proceedings ISBN or ISSN
 * TITLE: journal title or abbreviation
 * SER_TITLE: The serial title.
 * VOL_TITLE: The book title.
 * FIRST AUTHOR, AUTHOR/EDITOR: First Author surname.
 * VOLUME: Book/Conference proceedings volume
 * EDITION_NUMBER: Edition number for the book or conference proceeding (e.g. 3)
 * PAGE: First page.
 * YEAR: Year book or conference proceedings was published.
 * COMPONENT_NUMBER: Chapter, section or part inside the book/conf. proceeding (e.g. Section 3)
 * DOI: The DOI, left blank in the query
 * RESOURCE_TYPE: The resource type (full_text, abstract_only or bibliographic_record)
 * KEY: submitted buy the user to track queries (e.g. echoed back in the corresponding query result)
 *
 * @author Dragisa Krsmanovic
 */
public class CrossRefArticle {

  private String isbn;
  private String title;
  private String serTitle;
  private String volTitle;
  private String firstAuthor;
  private String volume;
  private String editionNumber;
  private String page;
  private String year;
  private String componentNumber;
  private String doi;
  private String resourceType;
  private String key;

  /**
   * Get ISBN, ISBN/ISSN: Journal, book or conference proceedings ISBN or ISSN.
   *
   * @return ISBN, ISBN/ISSN
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Set ISBN, ISBN/ISSN: Journal, book or conference proceedings ISBN or ISSN.
   * @param isbn ISBN, ISBN/ISSN
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  /**
   * Get TITLE: journal title or abbreviation
   * @return Title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Set TITLE: journal title or abbreviation
   * @param title Title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Get SER_TITLE: The serial title.
   * @return Serial title
   */
  public String getSerTitle() {
    return serTitle;
  }

  /**
   * Set SER_TITLE: The serial title.
   * @param serTitle Serial title
   */
  public void setSerTitle(String serTitle) {
    this.serTitle = serTitle;
  }

  /**
   * Get VOL_TITLE: The book title.
   * @return Volume title
   */
  public String getVolTitle() {
    return volTitle;
  }

  /**
   * Set VOL_TITLE: The book title.
   * @param volTitle Volume title
   */
  public void setVolTitle(String volTitle) {
    this.volTitle = volTitle;
  }

  /**
   * Get FIRST AUTHOR, AUTHOR/EDITOR: First Author surname.
   * @return First Author surname
   */
  public String getFirstAuthor() {
    return firstAuthor;
  }

  /**
   * Set FIRST AUTHOR, AUTHOR/EDITOR: First Author surname.
   * @param firstAuthor First Author surname
   */
  public void setFirstAuthor(String firstAuthor) {
    this.firstAuthor = firstAuthor;
  }

  /**
   * Get VOLUME: Book/Conference proceedings volume
   * @return Volume
   */
  public String getVolume() {
    return volume;
  }

  /**
   * Set VOLUME: Book/Conference proceedings volume
   * @param volume Volume
   */
  public void setVolume(String volume) {
    this.volume = volume;
  }

  /**
   * Get EDITION_NUMBER, ISSUE: Edition number for the book or conference proceeding (e.g. 3)
   * @return Edition number / Issue
   */
  public String getEditionNumber() {
    return editionNumber;
  }

  /**
   * Set EDITION_NUMBER, ISSUE: Edition number for the book or conference proceeding (e.g. 3)
   * @param editionNumber Edition number / Issue
   */
  public void setEditionNumber(String editionNumber) {
    this.editionNumber = editionNumber;
  }

  /**
   * Get PAGE: First page.
   * @return First page
   */
  public String getPage() {
    return page;
  }

  /**
   * Set PAGE: First page.
   * @param page First page
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * Get YEAR: Year book or conference proceedings was published.
   * @return Year
   */
  public String getYear() {
    return year;
  }

  /**
   * Set YEAR: Year book or conference proceedings was published.
   * @param year Year
   */
  public void setYear(String year) {
    this.year = year;
  }

  /**
   * Get COMPONENT_NUMBER: Chapter, section or part inside the book/conf. proceeding (e.g. Section 3)
   * @return Component number
   */
  public String getComponentNumber() {
    return componentNumber;
  }

  /**
   * Set COMPONENT_NUMBER: Chapter, section or part inside the book/conf. proceeding (e.g. Section 3)
   * @param componentNumber Component number
   */
  public void setComponentNumber(String componentNumber) {
    this.componentNumber = componentNumber;
  }

  /**
   * Get DOI: The DOI, left blank in the query
   * @return DOI
   */
  public String getDoi() {
    return doi;
  }

  /**
   * Set DOI: The DOI, left blank in the query
   * @param doi DOI
   */
  public void setDoi(String doi) {
    this.doi = doi;
  }

  /**
   * Get RESOURCE_TYPE: The resource type (full_text, abstract_only or bibliographic_record)
   * @return Resource type
   */
  public String getResourceType() {
    return resourceType;
  }

  /**
   * Set RESOURCE_TYPE: The resource type (full_text, abstract_only or bibliographic_record)
   * @param resourceType Resource type
   */
  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  /**
   * Get KEY: submitted buy the user to track queries (e.g. echoed back in the corresponding query result)
   * @return Key
   */
  public String getKey() {
    return key;
  }

  /**
   * Set KEY: submitted buy the user to track queries (e.g. echoed back in the corresponding query result)
   * @param key Key
   */
  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "CrossRefArticle{" +
        "isbn='" + isbn + '\'' +
        ", title='" + title + '\'' +
        ", serTitle='" + serTitle + '\'' +
        ", volTitle='" + volTitle + '\'' +
        ", firstAuthor='" + firstAuthor + '\'' +
        ", volume='" + volume + '\'' +
        ", editionNumber='" + editionNumber + '\'' +
        ", page='" + page + '\'' +
        ", year='" + year + '\'' +
        ", componentNumber='" + componentNumber + '\'' +
        ", doi='" + doi + '\'' +
        ", resourceType='" + resourceType + '\'' +
        ", key='" + key + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CrossRefArticle that = (CrossRefArticle) o;

    if (componentNumber != null ? !componentNumber.equals(that.componentNumber) : that.componentNumber != null) {
      return false;
    }
    if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
    if (editionNumber != null ? !editionNumber.equals(that.editionNumber) : that.editionNumber != null) {
      return false;
    }
    if (firstAuthor != null ? !firstAuthor.equals(that.firstAuthor) : that.firstAuthor != null) {
      return false;
    }
    if (isbn != null ? !isbn.equals(that.isbn) : that.isbn != null) return false;
    if (key != null ? !key.equals(that.key) : that.key != null) return false;
    if (page != null ? !page.equals(that.page) : that.page != null) return false;
    if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) {
      return false;
    }
    if (serTitle != null ? !serTitle.equals(that.serTitle) : that.serTitle != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (volTitle != null ? !volTitle.equals(that.volTitle) : that.volTitle != null) return false;
    if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
    if (year != null ? !year.equals(that.year) : that.year != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = isbn != null ? isbn.hashCode() : 0;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (serTitle != null ? serTitle.hashCode() : 0);
    result = 31 * result + (volTitle != null ? volTitle.hashCode() : 0);
    result = 31 * result + (firstAuthor != null ? firstAuthor.hashCode() : 0);
    result = 31 * result + (volume != null ? volume.hashCode() : 0);
    result = 31 * result + (editionNumber != null ? editionNumber.hashCode() : 0);
    result = 31 * result + (page != null ? page.hashCode() : 0);
    result = 31 * result + (year != null ? year.hashCode() : 0);
    result = 31 * result + (componentNumber != null ? componentNumber.hashCode() : 0);
    result = 31 * result + (doi != null ? doi.hashCode() : 0);
    result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
    result = 31 * result + (key != null ? key.hashCode() : 0);
    return result;
  }
}
