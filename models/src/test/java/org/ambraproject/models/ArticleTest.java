/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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
package org.ambraproject.models;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Basic test for hibernate mappings of article
 *
 * @author Alex Kudlick 11/8/11
 */
public class ArticleTest extends BaseHibernateTest {

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testShouldFailOnNullDoi() {
    hibernateTemplate.save(new Article());
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testDoiUniqueConstraint() {
    Article article1 = new Article();
    article1.setDoi("non-unique doi");
    Article article2 = new Article();
    article2.setDoi("non-unique doi");
    hibernateTemplate.save(article1);
    hibernateTemplate.save(article2);
  }

  @Test
  public void testSaveArticle() {

    Article article = new Article();
    article.setTitle("test title");
    article.setPages("10-20");
    article.setDoi("doi1");
    article.seteLocationId("44");

    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned a null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertNotNull(article.getCreated(), "Create date didn't get generated");

    assertEquals(article.getPages(), "10-20");
    assertEquals(article.geteLocationId(), "44");
    assertEquals(article.getTitle(), "test title", "Incorrect title");
  }

  @Test
  public void testArticleWithPeople() {
    Article article = new Article();
    article.setDoi("doi2");
    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    authors.add(new ArticleAuthor());
    authors.add(new ArticleAuthor());
    article.setAuthors(authors);
    List<ArticleEditor> editors = new ArrayList<ArticleEditor>(2);
    editors.add(new ArticleEditor());
    editors.add(new ArticleEditor());
    article.setEditors(editors);

    List<String> collaborativeAuthors = new ArrayList<String>(2);
    collaborativeAuthors.add("Bill and Melinda Gates Foundation");
    collaborativeAuthors.add("Anti-Mosquito foundation");
    article.setCollaborativeAuthors(collaborativeAuthors);


    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned a null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertEquals(article.getAuthors().size(), 2, "Incorrect number of authors");
    assertEquals(article.getEditors().size(), 2, "Incorrect number of editors");
    assertEquals(article.getCollaborativeAuthors().toArray(), collaborativeAuthors.toArray(),
        "incorrect collaborative authors");

  }

  @Test
  public void testArticleWithAssets() {
    Article article = new Article();
    article.setDoi("doi3");
    List<ArticleAsset> assets = new ArrayList<ArticleAsset>(3);
    for (int i = 0; i < 3 ; i++) {
      ArticleAsset asset = new ArticleAsset();
      asset.setDoi("articleTestdoi-" + i);
      asset.setExtension("articleTestExtension-" + i);
      assets.add(asset);
    }
    article.setAssets(assets);

    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned a null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertEquals(article.getAssets().size(), 3, "Incorrect number of assets");

  }

  @Test
  public void testArticleWithCitedArticles() {
    Article article = new Article();
    article.setDoi("doi4");
    List<CitedArticle> citedArticles = new ArrayList<CitedArticle>(2);
    citedArticles.add(new CitedArticle());
    citedArticles.add(new CitedArticle());
    article.setCitedArticles(citedArticles);


    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned a null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertEquals(article.getCitedArticles().size(), 2, "Incorrect number of cited articles");

  }

  @Test
  public void testArticleWithCategories() {
    Article article = new Article();
    article.setDoi("doi5");
    Set<Category> categories = new HashSet<Category>(2);
    Category category1 = new Category();
    category1.setMainCategory("category1");
    categories.add(category1);
    Category category2 = new Category();
    category2.setMainCategory("category2");
    categories.add(category2);

    article.setCategories(categories);

    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertEquals(article.getCategories().size(), 2, "incorrect number of categories");

  }

  @Test
  public void testArticleWithTypes() {
    Article article = new Article();
    article.setDoi("doi6");
    Set<String> types = new HashSet<String>(2);
    types.add("type 1");
    types.add("type 2");

    article.setTypes(types);

    Long id = (Long) hibernateTemplate.save(article);
    assertNotNull(id, "session returned null id");
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article, "couldn't retrieve article");
    assertEquals(article.getTypes().size(), 2, "incorrect number of types");

  }


}
