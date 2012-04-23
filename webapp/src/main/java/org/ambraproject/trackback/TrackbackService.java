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

package org.ambraproject.trackback;

import org.ambraproject.views.TrackbackView;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Track back service.
 */
public interface TrackbackService {

  /**
   * Find all trackbacks that match the passed in values.
   *
   * @param startDate the startDate
   * @param endDate the endDate
   * @param maxResults max results
   * @param journal the journal to filter on
   * @return a list of trackback view objects
   */
  public List<TrackbackView> getTrackbacks(Date startDate, Date endDate, int maxResults, String journal);

  /**
   * Saves a track back, and returns the id of the trackback.
   *
   *
   * @param articleDoi the doi of the article being annotated
   * @param url        the url of the blog
   * @param title      The title of the blog
   * @param blogName   the name of the blog
   * @param excerpt    the excerpt from the blog
   * @return the id of the stored trackback
   * @throws DuplicateTrackbackException if a trackback for the same article and url already exists
   */
  public Long createTrackback(final String articleDoi,
                              final String url,
                              final String title,
                              @Nullable final String blogName,
                              final String excerpt) throws DuplicateTrackbackException;

  /**
   * Check whether the given trackback url contains a link to the article url
   *
   * @param blogUrl the url of the blog
   * @param doi     the doi fo the article
   * @return true if the blog contains a link to the article, false if it doesn't
   * @throws Exception on an error parsing the blog url
   */
  public boolean blogLinksToArticle(String blogUrl, String doi) throws IOException;

  /**
   * Get a list of trackbacks on the given article, ordered newest to oldest
   *
   *
   * @param articleDoi the doi of the article
   * @return an ordered list of trackbacks
   */
  public List<TrackbackView> getTrackbacksForArticle(String articleDoi);

  /**
   * Count the number of trackbacks on the given article
   *
   * @param articleDoi the doi of the article to use
   * @return the number of trackbacks on the article
   */
  public int countTrackbacksForArticle(String articleDoi);

}
