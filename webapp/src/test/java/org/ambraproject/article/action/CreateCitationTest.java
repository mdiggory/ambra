/* $HeadURL$
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

package org.ambraproject.article.action;

import org.ambraproject.models.ArticleAuthor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.Article;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Basic test for {@link CreateCitation} action
 * @author Alex Kudlick
 */
public class CreateCitationTest extends BaseWebTest {

  @Autowired
  protected CreateCitation action;

  @DataProvider(name = "testCitation")
  public Object[][] getTestCitation() {
    String articleUri = "id:create-citation-test";
    Article article = new Article();
    article.setDoi(articleUri);

    article.setTitle("test title");
    article.seteLocationId("eLocationId");
    article.setDate(new Date());

    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setFullName("Michael B. Eisen");
    author1.setGivenNames("Michael B.");
    author1.setSurnames("Eisen");
    authors.add(author1);
    ArticleAuthor author2 = new ArticleAuthor();
    author2.setFullName("William T. Johnson");
    author2.setGivenNames("William T.");
    author2.setSurnames("Johnson");
    authors.add(author2);
    article.setAuthors(authors);
    dummyDataStore.store(article);

    return new Object[][]{
        {articleUri, authors}
    };
  }


  @Test(dataProvider = "testCitation")
  public void testExecute(String articleUri, List<ArticleAuthor> expectedAuthors) throws Exception {
    action.setArticleURI(articleUri);
    assertEquals(action.execute(), BaseActionSupport.SUCCESS, "Action didn't return success");
    assertEquals(action.getAuthorList().size(), expectedAuthors.size(), "Didn't return correct number of authors");

    for (int i = 0; i < expectedAuthors.size(); i++) {
      ArticleAuthor actual = action.getAuthorList().get(i);
      ArticleAuthor expected = expectedAuthors.get(i);
      assertEquals(actual.getFullName(), expected.getFullName(), "Author didn't have correct full name");
      assertEquals(actual.getSurnames(), expected.getSurnames(), "Author didn't have correct surname");
      assertEquals(actual.getGivenNames(), expected.getGivenNames(), "Author didn't have correct given name");
    }
  }

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }
}
