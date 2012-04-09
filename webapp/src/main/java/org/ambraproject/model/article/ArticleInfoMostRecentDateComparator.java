/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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

import java.util.Comparator;
import java.io.Serializable;

/**
 * Comparator class used to sort ArticleInfo objects based on article publication date. 
 * The more recent article will appear first. Where the two publication dates are the 
 * same, the article id is used to preserve a consistent ordering. 
 * 
 * Note that an article publication date is ingested from the original article XML and is
 * based upon year / month / day only. Although getDate() returns a java.util.Date object, 
 * (which stores dates to the nearest millisecond) these should be equal for articles with 
 * the same publication date. 
 *  
 * @author Alex Worden
 *
 */
public class ArticleInfoMostRecentDateComparator implements Comparator<ArticleInfo>, Serializable {
  public int compare(ArticleInfo o1, ArticleInfo o2) {
    if (o1.getDate().after(o2.getDate())) {
      return -1;
    }
    if (o1.getDate().before(o2.getDate())) {
      return 1;
    }

    return o1.getDoi().compareTo(o2.getDoi());
  }
}
