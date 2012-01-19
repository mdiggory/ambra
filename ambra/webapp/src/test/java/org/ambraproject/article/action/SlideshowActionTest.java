/* $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.01/ambra/webapp/src/test/java/org/topazproject/ambra/article/action/CreateCitationTest.java $
 * $Id: CreateCitationTest.java 9666 2011-10-07 00:03:23Z akudlick $
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

package org.ambraproject.article.action;

import org.ambraproject.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.action.BaseActionSupport;

import static org.testng.Assert.assertEquals;


/**
 * Basic test for {@link SlideshowAction} action
 * @author Joe Osowski
 */
public class SlideshowActionTest extends BaseWebTest {

  @Autowired
  protected SlideshowAction action;

  @DataProvider(name = "testSlideshow")
  public Object[][] getTestCitation() {
    String articleUri = "id:test-article-uri";
    Article article = new Article();
    article.setDoi(articleUri);

    dummyDataStore.store(article);

    return new Object[][]{ { articleUri } };
  }


  @Test(dataProvider = "testSlideshow")
  public void testExecute(String articleUri) throws Exception
  {
    /* Test that if an article exists without related content, input is returned */
    action.setUri(articleUri);
    assertEquals(action.execute(), BaseActionSupport.INPUT, "Action didn't return input");
  }
}