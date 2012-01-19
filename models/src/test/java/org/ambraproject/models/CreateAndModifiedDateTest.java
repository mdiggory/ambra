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

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 11/9/11
 */
public class CreateAndModifiedDateTest extends BaseHibernateTest {

  @Test
  public void testWithArticle() throws InterruptedException {
    long testStart = new Date().getTime();
    Article article = new Article();
    article.setDoi("doi for create and modified date");
    Long id = (Long) hibernateTemplate.save(article);
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article.getCreated(), "didn't generate create date");
    assertTrue(article.getCreated().getTime() >= testStart, "create date wasn't after the test start");

    Thread.sleep(1500);
    article.setTitle("hello");
    hibernateTemplate.update(article);
    article = (Article) hibernateTemplate.get(Article.class, id);
    assertNotNull(article.getLastModified(), "didn't generate modified date");
    assertTrue(article.getLastModified().after(article.getCreated()), "modified date wasn't after created date");
  }

  @Test
  public void testCascadeCreateDate() {
    long testStart = new Date().getTime();
    Article article = new Article();
    article.setDoi("doi for cascade create date");
    List<ArticleAsset> assets = new ArrayList<ArticleAsset>(2);
    for (int i = 0; i < 2; i++) {
      ArticleAsset asset = new ArticleAsset();
      asset.setDoi("cascadeCreateDateDoi-" + i);
      asset.setExtension("cascadeCreateDateExtension-" + i);
      assets.add(asset);
    }
    article.setAssets(assets);

    Long id = (Long) hibernateTemplate.save(article);
    article = (Article) hibernateTemplate.get(Article.class, id);
    for (ArticleAsset asset : article.getAssets()) {
      assertNotNull(asset.getCreated(), "ArticleAsset didn't get create date set");
      assertTrue(asset.getCreated().getTime() >= testStart, "create date wasn't after test start");
    }
  }

  @Test
  public void testCascadeModifiedDate() throws InterruptedException {
    long testStart = new Date().getTime();
    Article article = new Article();
    article.setDoi("doi for cascade modified date");
    List<ArticleAsset> assets = new ArrayList<ArticleAsset>(2);
    for (int i = 0; i < 2; i++) {
      ArticleAsset asset = new ArticleAsset();
      asset.setDoi("cascadeModifiedDateDoi-" + i);
      asset.setExtension("cascadeModifiedDateExtension-" + i);
      assets.add(asset);
    }
    article.setAssets(assets);
    Long articleId = (Long) hibernateTemplate.save(article);

    article = (Article) hibernateTemplate.get(Article.class, articleId);
    article.getAssets().get(0).setContentType("new content type");

    Thread.sleep(1500);
    hibernateTemplate.update(article);

    article = (Article) hibernateTemplate.get(Article.class, articleId);

    ArticleAsset asset = article.getAssets().get(0);
    assertEquals(asset.getContentType(), "new content type", "asset didn't get cascaded update");
    assertNotNull(asset.getLastModified(), "asset didn't get last modified date set");
    assertTrue(asset.getLastModified().after(asset.getCreated()), "last modified date wasn't after the created date");
    assertTrue(asset.getLastModified().getTime() >= testStart, "last modified date wasn't after the test start");
  }
}
