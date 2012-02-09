/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.ambra.doi;

/**
 * Service Bean for the {@link ResolverServlet}.  Performs operations for which database access is needed
 *
 * @author alex 9/6/11
 */
public interface ResolverDAOService {

  /**
   * Determine whether the given doi corresponds to an article
   *
   * @param doi the doi to look up
   * @return true if the given doi is an article, false otherwise
   */
  public boolean doiIsArticle(String doi);

  /**
   * Determine if the given doi corresponds to an annotation
   *
   * @param doi the doi to look up
   * @return true if the given doi corresponds to an ambra annotation, false otherwise
   */
  public boolean doiIsAnnotation(String doi);

  /**
   * Get the root doi that is annotated by the given annotation doi.  e.g. if the given doi is a comment on an article,
   * return the article doi.  If it annotates a comment on an article, return the article.
   *
   * @param doi the annotation doi
   * @return the resource that is annotated by the annotation doi
   * @throws AnnotationLoopException in case there is a loop among the annotations
   */
  public String getAnnotatedRoot(String doi) throws AnnotationLoopException;
}
