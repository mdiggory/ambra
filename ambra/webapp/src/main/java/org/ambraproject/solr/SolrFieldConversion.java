/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.solr;

/**
 * Service for converting to names of fields that are meaningful to solr
 * <p/>
 * <p/>
 * User: Alex Kudlick Date: Mar 1, 2011
 * <p/>
 * org.ambraproject.solr
 */
public interface SolrFieldConversion {

  /**
   * Get the name of the field in solr that counts views for articles over the number of days given.  If none matches,
   * the method should return the field that counts over the closest number of days
   *
   * @param numDays - The number of days over which to count views
   * @return - the field name in solr
   */
  public String getViewCountingFieldName(int numDays);

  /**
   *
   * @return - the solr field that counts views over all time
   */
  public String getAllTimeViewsField();
}
