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

package org.ambraproject.solr;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Alex Kudlick Date: Mar 14, 2011
 *         <p/>
 *         org.ambraproject.solr
 */
public class MostViewedCacheTest {

  @DataProvider(name = "cacheObjects")
  public Object[][] articleListCaches() {
    GregorianCalendar lastYear = new GregorianCalendar();
    lastYear.add(Calendar.YEAR,-1);
    GregorianCalendar oneHourAgo = new GregorianCalendar();
    oneHourAgo.add(Calendar.HOUR, -1);
    GregorianCalendar threeDaysAgo = new GregorianCalendar();
    threeDaysAgo.add(Calendar.DAY_OF_YEAR,-3);
    GregorianCalendar tenMinutesAgo = new GregorianCalendar();
    tenMinutesAgo.add(Calendar.MINUTE,-10);
    GregorianCalendar now = new GregorianCalendar();
    List<Pair<String, String>> dummyArticles = new ArrayList<Pair<String, String>>();

    return new Object[][]{
        {new MostViewedCache(now,dummyArticles),true},
        {new MostViewedCache(lastYear,dummyArticles),false},
        {new MostViewedCache(oneHourAgo,dummyArticles),false},
        {new MostViewedCache(threeDaysAgo,dummyArticles),false},
        {new MostViewedCache(tenMinutesAgo,dummyArticles),true}
    };
  }

  @Test(dataProvider = "cacheObjects")
  public void test(MostViewedCache cacheEntry, boolean isValid) {
    assertEquals(isValid, cacheEntry.isValid());
  }
}
