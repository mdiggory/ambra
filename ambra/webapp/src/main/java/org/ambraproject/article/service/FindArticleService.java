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

package org.ambraproject.article.service;

/**
 * Service class that encapsulates work done on findThisArticle action.
 *
 * <ol>
 * <li>Search CrossRef based on article title and primary author</li>
 * <li>If article DOI is found on CrossRef, search PubGet for open access PDF of that article</li>
 * </ol>
 *
 * @author Dragisa Krsmanovic
 */
public interface FindArticleService {

  /**
   * Find article on CrossRef
   * @param title Article's title
   * @param author Article's primary author
   * @return ThisArticleFound containing DOI and, optionally PubGet URI. If not found, DOI is set to null. 
   */
  public ThisArticleFound findArticle(String title, String author);

}
