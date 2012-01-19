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

package org.ambraproject.article.service;

/**
 * Signifies that the article with the requested id already exists.
 *
 * @author Ronald Tschalär
 * @author Eric Brown
 * @version $Id$
 */
public class DuplicateArticleIdException extends Exception {
  private final String id;

  /**
   * Create a new exception instance with a default exception message.
   *
   * @param id      the (duplicate) id
   */
  public DuplicateArticleIdException(String id) {
    this(id, "id = '" + id + "'");
  }

  /**
   * Create a new exception instance.
   *
   * @param id      the (duplicate) id
   * @param message the exception message
   */
  public DuplicateArticleIdException(String id, String message) {
    super(message);
    this.id = id;
  }

  /**
   * @return the (duplicate) id
   */
  public String getId() {
    return id;
  }
}
