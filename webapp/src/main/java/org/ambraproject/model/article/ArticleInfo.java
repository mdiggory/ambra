/* $HeadURL$
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
package org.ambraproject.model.article;

import org.ambraproject.model.UserProfileInfo;
import org.ambraproject.models.Category;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Projection;
import org.topazproject.otm.annotations.View;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The info about a single article that the UI needs.
 */
@View(query=
        "select a.id id, dc.date date, dc.title title, ci, " +
        "(select a.articleType from Article aa) at, " +
        "(select aa2.id rid, aa2.dublinCore.title rtitle from Article aa2 " +
        "   where aa2 = a.relatedArticles.article) relatedArticles, " +
        "(select fc.id from FormalCorrection fc where fc.annotates = a.id) corrections, " +
        "(select r.id from Retraction r where r.annotates = a.id) retractions " +
        "from Article a, CitationInfo ci " +
        "where a.id = :id and dc := a.dublinCore and ci.id = dc.bibliographicCitation.id;")
public class ArticleInfo implements Serializable {

  private static final long serialVersionUID = 3823215602197299918L;

  public Long                    id;
  public String                  doi;
  public Date                    date;
  private String                 title;
  public List<RelatedArticleInfo> relatedArticles = new ArrayList<RelatedArticleInfo>();
  public List<String>            authors = new ArrayList<String>();
  public List<String>            collaborativeAuthors = new ArrayList<String>();
  public Set<ArticleType>        articleTypes = new HashSet<ArticleType>();
  public Set<URI>                corrections = new HashSet<URI>();
  public Set<URI>                retractions = new HashSet<URI>();
  public Set<String>             journals = new HashSet<String>();
  private String                 publisher;
  private List<String>           subjects = new LinkedList<String>();
  private String                 description;
  private Set<String>            types;
  private String                 eIssn;
  private String                 pages;
  private Set<Category>          categories;
  private String                 eLocationId;
  private String                 volume;
  private String                 issue;

  private transient String unformattedTitle;

  /**
   * Set the ID of this Article. This is the Article DOI. 
   * 
   * @param doi Article ID.
   */
  @Id
  public void setDoi(String doi) {
    this.doi = doi;
  }

  /**
   * Get the id.
   *
   * @return the id.
   */
  public String getDoi() {
    return doi;
  }

  /**
   * Get the set of all Article types associated with this Article.
   *
   * @return the Article types.
   */
  public Set<ArticleType> getArticleTypes() {
    return articleTypes;
  }

  /**
   * Get the date that this article was published.
   *
   * @return the date.
   */
  public Date getDate() {
    return date;
  }

  /**
   * Set the Date that this article was published
   * @param date Article date.
   */
  @Projection("date")
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Get the title.
   *
   * @return the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Set the title of this Article.
   *  
   * @param articleTitle Title.
   */
  @Projection("title")
  public void setTitle(String articleTitle) {
    title = articleTitle;
    unformattedTitle = null;
  }

  /**
   * Get an unformatted version of the Article Title. 
   * @return Unformatted title.
   */
  public String getUnformattedTitle() {
    if ((unformattedTitle == null) && (title != null)) {
      unformattedTitle = title.replaceAll("</?[^>]*>", "");
    }
    return unformattedTitle;
  }

  /**
   * Get article description.
   * @return Description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set article description.
   * @param description Description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the authors.
   *
   * @return the authors.
   */
  public List<String> getAuthors() {
    return authors;
  }

  /**
   * Get the collaborative authors
   * @return collaborative authors
   */
  public List<String> getCollaborativeAuthors() {
    return collaborativeAuthors;
  }

  /**
   * Get the related articles.
   *
   * @return the related articles.
   */
  public List<RelatedArticleInfo> getRelatedArticles() {
    return relatedArticles;
  }

  /**
   * get the journals that this article is cross published in
   * @return a list of journals
   */
  public Set<String> getJournals() {
    return journals;
  }

  /**
   * set the journals that this article is cross published in
   * @param journals a set of journals
   */
  public void setJournals(Set<String> journals) {
    this.journals = journals;
  }

  @Projection("ci")
  public void setCi(CitationInfo ci) {
    // get the authors
    authors.clear();
    for (UserProfileInfo upi : ci.getAuthors()) {
      authors.add(upi.getRealName());
    }

    // get the collaborative authors
    collaborativeAuthors.clear();
    for (String collaborativeAuthor : ci.getCollaborativeAuthors()) {
      collaborativeAuthors.add(collaborativeAuthor);
    }
  }

  @Projection("at")
  public void setAt(Set<String> at) {
    articleTypes.clear();
    for (String a : at)
      articleTypes.add(ArticleType.getArticleTypeForURI(URI.create(a), true));
  }

  @Projection("relatedArticles")
  public void setRelatedArticles(List<RelatedArticleInfo> relatedArticles) {
    this.relatedArticles = relatedArticles;
  }

  public Set<URI> getCorrections() {
    return corrections;
  }

  @Projection("corrections")
  public void setCorrections(Set<URI> corrections) {
    this.corrections = corrections;
  }

  public Set<URI> getRetractions() {
    return retractions;
  }

  @Projection("retractions")
  public void setRetractions(Set<URI> retractions) {
    this.retractions = retractions;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  /**
   * Get article subject from Article.categories.
   * @return Article subjects
   */
  public List<String> getSubjects() {
    return subjects;
  }

  /**
   * Get a displayable version of the Article Type by doing a lookup on the every element
   * of the Set of all Article Type URIs for this Article.
   * Defaults to "Unclassified".  Never throw an exception.
   * <p/>
   * The first successful lookup is used under the assumption that there is only one legit value.
   * This is a terrible assumption but, because of the terrible implementation of article types,
   * there are few other reasonable options.  This method is a miserable hack that should be
   * removed as soon as article types are implemented in a useful manner.
   *
   * @return The first displayable article type from the Set of Article Types for this Article
   */
  public String getArticleTypeForDisplay() {
    String articleTypeForDisplay = "Unclassified";
    try {
      ArticleType articleType = null;
      for (ArticleType artType : getArticleTypes()) {
        if (ArticleType.getKnownArticleTypeForURI(artType.getUri())!= null) {
          articleType = ArticleType.getKnownArticleTypeForURI(artType.getUri());
          break;
        }
      }
      articleTypeForDisplay = (articleType.getHeading());
    } catch (Exception e) {  // Do not rock the boat.
    }
    return articleTypeForDisplay;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String geteIssn() {
    return eIssn;
  }

  public void seteIssn(String eIssn) {
    this.eIssn = eIssn;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public Set<String> getTypes() {
    return types;
  }

  public void setTypes(Set<String> types) {
    this.types = types;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public void setCategories(Set<Category> categories) {
    this.categories = categories;
  }

  public String geteLocationId() {
    return eLocationId;
  }

  public void seteLocationId(String eLocationId) {
    this.eLocationId = eLocationId;
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
}
