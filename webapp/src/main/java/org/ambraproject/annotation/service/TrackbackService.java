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

package org.ambraproject.annotation.service;

import org.topazproject.ambra.models.Trackback;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Track back service.
 */
public interface TrackbackService {

  /**
   * Get the trackbacks that annotate the given id
   * @param annotates The trackbackID
   * @param getBodies set to true to NOT lazyload trackback bodies
   * @return an arrayList of trackbacks
   */
  public ArrayList<Trackback> getTrackbacks (final String annotates, final boolean getBodies);

  /**
   * Saves a track back
   * @param title The title of the article
   * @param blog_name the blog name
   * @param excerpt the excertp from the blog
   * @param permalink a permalink to the article
   * @param trackback
   * @param url
   * @param trackbackId
   * @return true if the trackback was saved, false if the trackback already existed
   * @throws Exception
   */
  public boolean saveTrackBack(final String title, final String blog_name,
                               final String excerpt, final URL permalink, final URI trackback,
                               final String url, final String trackbackId) throws Exception;

}
