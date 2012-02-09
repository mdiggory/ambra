/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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
package org.ambraproject.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Checks that content is not profane. It could be used to check that the user's posts don't contain
 * profane words like F***, GEORGE, BUSH, etc.
 */
public class ProfanityCheckingServiceImpl implements ProfanityCheckingService {
  private Map<String, Pattern> profanePatterns;

  /**
   * Validate that the content is profane or not and return the list of profane words found.
   * @param content content to check for profanity
   * @return list of profane words
   */
  public List<String> validate(final String content) {
    final List<String> profaneWordsFound = new ArrayList<String>();
    if (content != null) {
      final String contentLowerCase = content.toLowerCase();

      for (final Map.Entry<String,Pattern> patternEntry : profanePatterns.entrySet()) {
        final Pattern pattern = patternEntry.getValue();
        if (pattern.matcher(contentLowerCase).find()) {
          profaneWordsFound.add(patternEntry.getKey());
        }
      }
    }
    return profaneWordsFound;
  }

  /**
   * Set the list of profane words.
   * @param profaneWords profaneWords
   */
  public void setProfaneWords(final Collection<String> profaneWords) {
    final Map<String, Pattern> patterns = new HashMap<String, Pattern>(profaneWords.size());
    for (final String profaneWord : profaneWords) {
      final Pattern pattern = Pattern.compile("\\b" + profaneWord.toLowerCase() + "\\b");
      patterns.put(profaneWord, pattern);
    }
    this.profanePatterns = patterns;
  }
}
