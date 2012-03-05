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

package org.ambraproject.models;

import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudlick 2/16/12
 */
public class ArticleViewTest extends BaseHibernateTest {

  @Test
  public void testSaveArticleView() {
    ArticleView view = new ArticleView();
    view.setArticleID(1l);
    view.setUserID(2l);
    view.setType(ArticleView.Type.PDF_DOWNLOAD);

    Serializable id = hibernateTemplate.save(view);
    ArticleView savedView = (ArticleView) hibernateTemplate.get(ArticleView.class, id);
    assertNotNull(savedView, "didn't save article view");
    assertEquals(savedView.getArticleID(), view.getArticleID(), "saved view had incorrect article id");
    assertEquals(savedView.getUserID(), view.getUserID(), "saved view had incorrect user id");
    assertEquals(savedView.getType(), view.getType(), "saved view had incorrect type");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullArticleID() {
    ArticleView view = new ArticleView();
    view.setUserID(2l);
    view.setType(ArticleView.Type.PDF_DOWNLOAD);
    hibernateTemplate.save(view);
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullUserID() {
    ArticleView view = new ArticleView();
    view.setArticleID(2l);
    view.setType(ArticleView.Type.ARTICLE_VIEW);
    hibernateTemplate.save(view);
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullType() {
    ArticleView view = new ArticleView();
    view.setUserID(2l);
    view.setArticleID(1l);
    hibernateTemplate.save(view);
  }

}
