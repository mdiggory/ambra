/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

package org.ambraproject.admin.service.impl;

import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.admin.service.CitationService;
import org.topazproject.ambra.models.ArticleContributor;
import org.topazproject.ambra.models.Citation;
import org.topazproject.ambra.models.UserProfile;
import org.ambraproject.service.HibernateServiceImpl;
import java.net.URI;

/**
 * Service class for managing citations
 * @author Dragisa Krsmanovic
 * @author Joe Osowski
 */
public class CitationServiceImpl extends HibernateServiceImpl implements CitationService {
  /**
   * Update visible fields in citation object.
   * @param citationId Citation ID
   * @param title Title
   * @param displayYear Year. Annotation citations have parenthesis around. For example: (2009)
   * @param journal Journal name
   * @param volume Volume (null by default)
   * @param issue Issue (null by default)
   * @param eLocationId ELocationID (null by default)
   * @param doi DOI. Default:  Article or annotation id without beggining info:doi/. For example: 10.1371/annotation/02eb93ff-2c25-44e7-8580-0388b665ea9e 
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void updateCitation(String citationId, String title, String displayYear, String journal,
                             String volume, String issue, String eLocationId, String doi) {

    Citation citation = getCitation(citationId);

    citation.setTitle(emptyStringToNull(title));
    citation.setDisplayYear(emptyStringToNull(displayYear));
    citation.setJournal(emptyStringToNull(journal));
    citation.setVolume(emptyStringToNull(volume));
    citation.setIssue(emptyStringToNull(issue));
    citation.setELocationId(emptyStringToNull(eLocationId));
    citation.setDoi(emptyStringToNull(doi));

    hibernateTemplate.update(citation);
  }

  /**
   * Add author to citation.
   * @param citationId Citation ID
   * @param surnames Author surname
   * @param givenNames Author given name
   * @param suffix Author suffix
   * @return New author ID
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String addAuthor(String citationId, String surnames, String givenNames, String suffix) {

    Citation citation = getCitation(citationId);

    UserProfile newAuthor = new UserProfile();

    newAuthor.setSurnames(emptyStringToNull(surnames));
    newAuthor.setGivenNames(emptyStringToNull(givenNames));
    newAuthor.setSuffix(emptyStringToNull(suffix));

    hibernateTemplate.save(newAuthor);

    citation.getAuthors().add(newAuthor);

    hibernateTemplate.update(citation);

    return newAuthor.getId().toString();
  }

  /**
   * Delete author from citation
   * @param citationId Citation ID
   * @param authorId Author ID that is being deleted
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteAuthor(String citationId, String authorId) {
    Citation citation = getCitation(citationId);
    UserProfile author = getAuthor(authorId);

    for (UserProfile up : citation.getAuthors())
    {
      if(up.getId().equals(author.getId())) {
        citation.getAuthors().remove(up);

        hibernateTemplate.update(citation);
        hibernateTemplate.delete(author);

        return;
      }
    }

    throw new HibernateException("Author <" + authorId + "> not found in citation <" + citationId + ">.");
  }

  /**
   * Update author
   * @param authorId Author ID
   * @param surnames Author surname
   * @param givenNames Author given name
   * @param suffix Author suffix
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void updateAuthor(String authorId, String surnames, String givenNames, String suffix) {
    UserProfile author = getAuthor(authorId);

    author.setSurnames(emptyStringToNull(surnames));
    author.setGivenNames(emptyStringToNull(givenNames));
    author.setSuffix(emptyStringToNull(suffix));

    hibernateTemplate.update(author);
  }

  /**
   * Add collaborative author
   * @param citationId Citation ID
   * @param collaborativeAuthor Collaborative author
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void addCollaborativeAuthor(String citationId, String collaborativeAuthor) {
    if (collaborativeAuthor != null && !collaborativeAuthor.trim().equals("")) {
      Citation citation = getCitation(citationId);
      citation.getCollaborativeAuthors().add(collaborativeAuthor.trim());
      hibernateTemplate.update(citation);
    }
  }

  /**
   * Remove collaborative author
   * @param citationId Citation ID
   * @param authorIndex Index of the collaborative author in the list
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteCollaborativeAuthor(String citationId, int authorIndex) {
    Citation citation = getCitation(citationId);
    citation.getCollaborativeAuthors().remove(authorIndex);

    hibernateTemplate.update(citation);
  }

  /**
   * Update collaborative author at authorIndex position
   * @param citationId Citation ID
   * @param authorIndex Index of the collaborative author in the list
   * @param collaborativeAuthor Collaborative author
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void updateCollaborativeAuthor(String citationId, int authorIndex, String collaborativeAuthor) {
    if (collaborativeAuthor != null && !collaborativeAuthor.trim().equals("")) {
      Citation citation = getCitation(citationId);
      citation.getCollaborativeAuthors().set(authorIndex, collaborativeAuthor.trim());
      hibernateTemplate.update(citation);
    }
  }

  private UserProfile getAuthor(String authorId) {
    UserProfile author = (UserProfile)hibernateTemplate.get(UserProfile.class, URI.create(authorId));

    if (author == null)
      throw new HibernateException("Author <" + authorId + "> not found.");

    return author;
  }

  private Citation getCitation(String citationId) {
    Citation citation = (Citation)hibernateTemplate.get(Citation.class, URI.create(citationId));

    if (citation == null)
      throw new HibernateException("Citation <" + citationId + "> not found.");

    return citation;
  }


  private String emptyStringToNull(String text) {
    if (text == null || text.trim().equals(""))
      return(null);
    else
      return(text.trim());
  }

  /**
   * Add annotation's citation's author
   *
   * @param citationId citation id
   * @param surnames author surname
   * @param givenNames author given name
   * @param suffix author suffix
   * @return new author id
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String addAnnotationAuthor(String citationId, String surnames, String givenNames, String suffix) {

    Citation citation = getCitation(citationId);

    ArticleContributor newAuthor = new ArticleContributor();

    newAuthor.setSurnames(emptyStringToNull(surnames));
    newAuthor.setGivenNames(emptyStringToNull(givenNames));
    newAuthor.setSuffix(emptyStringToNull(suffix));
    newAuthor.setIsAuthor(true);

    hibernateTemplate.save(newAuthor);

    citation.getAnnotationArticleAuthors().add(newAuthor);

    hibernateTemplate.update(citation);

    return newAuthor.getId().toString();
  }

  private ArticleContributor getAnnotationAuthor(String authorId) {
    ArticleContributor author = (ArticleContributor) hibernateTemplate.get(ArticleContributor.class, URI.create(authorId));

    if (author == null)
      throw new HibernateException("Author <" + authorId + "> not found.");

    return author;
  }

  /**
   * Update annotation's citation's author
   *
   * @param authorId author id
   * @param surnames author surname
   * @param givenNames author given name
   * @param suffix author suffix
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void updateAnnotationAuthor(String authorId, String surnames, String givenNames, String suffix) {
    ArticleContributor author = getAnnotationAuthor(authorId);

    author.setSurnames(emptyStringToNull(surnames));
    author.setGivenNames(emptyStringToNull(givenNames));
    author.setSuffix(emptyStringToNull(suffix));

    hibernateTemplate.update(author);
  }

  /**
   * Delete annotation's citation's author
   *
   * @param citationId citation id
   * @param authorId author id that is being deleted
   */
  @Transactional(rollbackFor = { Throwable.class })
  public void deleteAnnotationAuthor(String citationId, String authorId) {
    Citation citation = getCitation(citationId);
    ArticleContributor author = getAnnotationAuthor(authorId);

    for (ArticleContributor up : citation.getAnnotationArticleAuthors())
    {
      if(up.getId().equals(author.getId())) {
        citation.getAnnotationArticleAuthors().remove(up);

        hibernateTemplate.update(citation);
        hibernateTemplate.delete(author);

        return;
      }
    }

    throw new HibernateException("Author <" + authorId + "> not found in citation <" + citationId + ">.");
  }
}
