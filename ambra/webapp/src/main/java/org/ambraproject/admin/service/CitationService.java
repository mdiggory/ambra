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

package org.ambraproject.admin.service;

/**
 * Service class for managing citations
 * @author Dragisa Krsmanovic
 */
public interface CitationService {


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
  public void updateCitation(String citationId, String title, String displayYear, String journal,
                             String volume, String issue, String eLocationId, String doi);
  /**
   * Add author to citation.
   * @param citationId Citation ID
   * @param surnames Author surname
   * @param givenNames Author given name
   * @param suffix Author suffix
   * @return New author ID
   */
  public String addAuthor(String citationId, String surnames, String givenNames, String suffix);
  /**
   * Delete author from citation
   * @param citationId Citation ID
   * @param authorId Author ID that is being deleted
   */
  public void deleteAuthor(String citationId, String authorId);

  /**
   * Update author
   * @param authorId Author ID
   * @param surnames Author surname
   * @param givenNames Author given name
   * @param suffix Author suffix
   */
  public void updateAuthor(String authorId, String surnames, String givenNames, String suffix);
  /**
   * Add collaborative author
   * @param citationId Citation ID
   * @param collaborativeAuthor Collaborative author
   */
  public void addCollaborativeAuthor(String citationId, String collaborativeAuthor);

  /**
   * Remove collaborative author
   * @param citationId Citation ID
   * @param authorIndex Index of the collaborative author in the list
   */
  public void deleteCollaborativeAuthor(String citationId, int authorIndex);

  /**
   * Update collaborative author at authorIndex position
   * @param citationId Citation ID
   * @param authorIndex Index of the collaborative author in the list
   * @param collaborativeAuthor Collaborative author
   */
  public void updateCollaborativeAuthor(String citationId, int authorIndex, String collaborativeAuthor);

  /**
   * Add annotation's citation's author
   *
   * @param citationId citation id
   * @param surnames author surname
   * @param givenNames author given name
   * @param suffix author suffix
   * @return new author id
   */
  public String addAnnotationAuthor(String citationId, String surnames, String givenNames, String suffix);

  /**
   * Update annotation's citation's author
   * 
   * @param authorId author id
   * @param surnames author surname
   * @param givenNames author given name
   * @param suffix author suffix
   */
  public void updateAnnotationAuthor(String authorId, String surnames, String givenNames, String suffix);

  /**
   * Delete annotation's citation's author
   *
   * @param citationId citation id
   * @param authorId author id that is being deleted
   */
  public void deleteAnnotationAuthor(String citationId, String authorId);
  
}
