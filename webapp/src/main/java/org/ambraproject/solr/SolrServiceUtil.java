/* $HeadURL$
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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.solr;

import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Contains utility functions for solr
 */
public class SolrServiceUtil {

  private static final Logger log = LoggerFactory.getLogger(SolrServiceUtil.class);

  /**
   * Returns a field value for a given field name
   * @param document search result
   * @param fieldName field name
   * @param type data type
   * @return field value
   */
  public static <T> T getFieldValue(SolrDocument document, String fieldName, Class<T> type, String message) {
    Object value = document.getFieldValue(fieldName);
    if (value != null) {
      if (type.isInstance(value)) {
        return type.cast(value);
      } else {
        log.error("Field " + fieldName + " is not of type " + type.getName() + " for " + message);
      }
    } else {
      log.warn("No \'" + fieldName + "\' field for " + message);
    }

    return null;
  }

  /**
   * Returns a field value for a given field name
   * @param document search result
   * @param fieldName field name
   * @param type data type
   * @return field value
   */
  public static <T> List<T> getFieldMultiValue(SolrDocument document, String message, Class<T> type, String fieldName) {
    List<T> authorList = new ArrayList<T>();
    Object authors = document.getFieldValue(fieldName);
    if (authors != null) {
      if (authors instanceof Collection) {
        authorList.addAll((Collection<T>) authors);
      } else {
        T value = getFieldValue(document, fieldName, type, message);
        if (value != null) {
          authorList.add(value);
        }
      }
    } else {
      log.warn("No \'" + fieldName + "\' field for " + message);
    }
    return authorList;
  }
}
