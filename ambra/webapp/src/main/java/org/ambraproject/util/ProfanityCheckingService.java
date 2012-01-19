/* $HeadURL::                                                                            $
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
package org.ambraproject.util;

import java.util.Collection;
import java.util.List;

/**
 * Checks that content is not profane. It could be used to check that the user's posts don't contain
 * profane words like F***, GEORGE, BUSH, etc.
 */
public interface ProfanityCheckingService {

  /**
   * Validate that the content is profane or not and return the list of profane words found.
   * @param content content to check for profanity
   * @return list of profane words
   */
  public List<String> validate(final String content);

  /**
   * Set the list of profane words.
   * @param profaneWords profaneWords
   */
  public void setProfaneWords(final Collection<String> profaneWords);
}
