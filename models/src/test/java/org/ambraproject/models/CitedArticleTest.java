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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudlick 11/9/11
 */
public class CitedArticleTest extends BaseHibernateTest {

  @Test
  public void testSaveCitedArticle() {
    CitedArticle citedArticle = new CitedArticle();
    citedArticle.setCitationType("citation type");

    Long id = (Long) hibernateTemplate.save(citedArticle);

    assertNotNull(id, "session generated null id");
    citedArticle = (CitedArticle) hibernateTemplate.get(CitedArticle.class, id);
    assertNotNull(citedArticle, "couldn't retrieve cited article");
    assertNotNull(citedArticle.getCreated(),"Create date didn't get generated");

    assertEquals(citedArticle.getCitationType(), "citation type", "incorrect citation type");
  }

  @Test
  public void testSaveCitedArticleWithPeople() {
    CitedArticle citedArticle = new CitedArticle();
    List<CitedArticleAuthor> authors = new ArrayList<CitedArticleAuthor>(2);
    authors.add(new CitedArticleAuthor());
    authors.add(new CitedArticleAuthor());
    citedArticle.setAuthors(authors);

    List<CitedArticleEditor> editors = new ArrayList<CitedArticleEditor>(3);
    editors.add(new CitedArticleEditor());
    editors.add(new CitedArticleEditor());
    editors.add(new CitedArticleEditor());
    citedArticle.setEditors(editors);

    Long id = (Long) hibernateTemplate.save(citedArticle);

    assertNotNull(id, "session generated null id");
    citedArticle = (CitedArticle) hibernateTemplate.get(CitedArticle.class, id);
    assertNotNull(citedArticle, "couldn't retrieve cited article");
    assertEquals(citedArticle.getAuthors().size(), 2, "incorrect number of authors");
    assertEquals(citedArticle.getEditors().size(), 3, "incorrect number of editors");
  }

}
