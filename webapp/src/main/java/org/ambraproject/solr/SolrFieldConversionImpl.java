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

import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Bean for converting to fields that are meaningful to the SOlr server
 *
 * @author Alex Kudlick Date: Mar 1, 2011
 *         <p/>
 *         org.ambraproject.solr
 */
public class SolrFieldConversionImpl implements SolrFieldConversion{

  private Map<Integer, String> viewCountingFields;
  private List<Integer> sortedDays;
  private String allTimeViewsField;
  private Integer maxDaysToCountViews;

  @Required
  public void setViewCountingFields(Map<Integer, String> viewCountingFields) {
    this.viewCountingFields = viewCountingFields;
    sortedDays = new ArrayList<Integer>();
    sortedDays.addAll(viewCountingFields.keySet());
    Collections.sort(sortedDays);
    maxDaysToCountViews = sortedDays.get(sortedDays.size() - 1);
  }

  @Required
  public void setAllTimeViewsField(String allTimeViewsField) {
    this.allTimeViewsField = allTimeViewsField;
  }

  @Override
  public String getViewCountingFieldName(int numDays) {
    //if we have a field for the number of days, return it
    if (viewCountingFields.get(numDays) != null) {
      return viewCountingFields.get(numDays);
    }
    if(numDays > maxDaysToCountViews) {
      return viewCountingFields.get(maxDaysToCountViews);
    }
    if (numDays < sortedDays.get(0)) {
      return viewCountingFields.get(sortedDays.get(0));
    }

    //roll through and figure out which field is closest to the given number of days
    String field = null;
    for (int i = 1; i < sortedDays.size(); i++) {
      if (numDays < sortedDays.get(i) && numDays > sortedDays.get(i - 1)) {
        int leftSideDistance = numDays - sortedDays.get(i - 1);
        int rightSideDistance = sortedDays.get(i) - numDays;
        Integer index = leftSideDistance < rightSideDistance ? i - 1 : i;
        field = viewCountingFields.get(sortedDays.get(index));
        break;
      }
    }

    return field;
  }

  @Override
  public String getAllTimeViewsField() {
    return allTimeViewsField;  
  }
}
