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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * User: Alex Kudlick Date: Mar 1, 2011
 * <p/>
 * org.ambraproject.solr
 */
public class SolrFieldConversionTest extends BaseTest {

  @Autowired
  protected SolrFieldConversion solrFieldConverter;

  //Constants from the application context
  public static final String TWO_WEEK_FIELD = "two_week_field";
  public static final String ONE_MONTH_FIELD = "one_month_field";
  public static final String ALL_TIME_FIELD = "all_time_views";

  @DataProvider(name = "viewCountingFields")
  public Object[][] viewCountingFields() {
    return new Object[][]{
        {14, TWO_WEEK_FIELD},
        {10, TWO_WEEK_FIELD},
        {17, TWO_WEEK_FIELD},
        {30, ONE_MONTH_FIELD},
        {28, ONE_MONTH_FIELD},
        {40, ONE_MONTH_FIELD},
    };
  }

  @Test(dataProvider = "viewCountingFields")
  public void testViewCountingConversion(Integer numDays, String expectedField) {
    assertEquals(solrFieldConverter.getViewCountingFieldName(numDays), expectedField,
        "Didn't return correct field for " + numDays + " days");
  }

  @Test
  public void testGetAllTimeViewsField() {
    assertEquals(solrFieldConverter.getAllTimeViewsField(), ALL_TIME_FIELD,
        "Didn't return correct field for all time views");
  }

}
