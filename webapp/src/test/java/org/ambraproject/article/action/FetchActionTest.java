/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.article.action;

import org.ambraproject.BaseWebTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Base class for {@link FetchArticleActionTest} and {@link FetchObjectActionTest} since those both need to use the same doi, and we need
 * to make sure we store the same information for that article
 *
 * @author Alex Kudlick 2/16/12
 */
public abstract class FetchActionTest extends BaseWebTest {

  @Autowired
  @Qualifier("articleInFilestore")
  protected String doi;

  protected Article article;

  @BeforeMethod
  public void storeArticle() {
    article = new Article();
    article.setDoi(doi);
    article.setTitle("Title from the database");
    article.setArchiveName("archive name from the database");
    article.seteIssn("eIssn from the database");
    article.setDescription("description from the database");
    article.setAssets(new ArrayList<ArticleAsset>(1));
    article.getAssets().add(new ArticleAsset());
    article.getAssets().get(0).setDoi(article.getDoi());
    article.getAssets().get(0).setExtension("XML");
    article.setCategories(new HashSet<Category>(0));

    dummyDataStore.store(article);
  }
}
