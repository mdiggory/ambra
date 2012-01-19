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
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudlick 11/9/11
 */
public class ArticleRelationshipTest extends BaseHibernateTest {

  @Test
  public void testSaveRelationship() {
    Article article = new Article();
    article.setDoi("doi");

    ArticleRelationship articleRelationship = new ArticleRelationship();
    articleRelationship.setOtherArticleDoi("some other doi");
    articleRelationship.setParentArticle(article);

    List<ArticleRelationship> relationships = new ArrayList<ArticleRelationship>(1);
    relationships.add(articleRelationship);
    article.setRelatedArticles(relationships);

    Long id = (Long) hibernateTemplate.save(article); //should cascade to relationships

    articleRelationship = ((Article) hibernateTemplate.get(Article.class, id)).getRelatedArticles().get(0);

    assertNotNull(articleRelationship, "couldn't retrieve relationship");
    assertNotNull(articleRelationship.getCreated(),"Create date didn't get generated");
    assertEquals(articleRelationship.getParentArticle(), article, "incorrect parent article");
    assertEquals(articleRelationship.getOtherArticleDoi(), "some other doi", "incorrect other article doi");

  }
}
