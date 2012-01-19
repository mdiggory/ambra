/* $HeadURL:: $
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
package org.topazproject.ambra.doi;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test for DOI resolver. Dataproviders use values from the sql used to set up the test database
 * (create-resolver-test-db.sql)
 *
 * @author Alex Kudlick
 */
public class ResolverDAOServiceTest extends BaseResolverTest {

  private ResolverDAOService resolverDAOService;

  @BeforeClass(dependsOnMethods = "createDB")
  public void setup() {
    insertArticleRow("info:doi/test-article-1");
    insertArticleRow("info:doi/test-article-2");
    insertAnnotationRow("info:doi/test-annotation-1", "info:doi/test-article-1");
    insertAnnotationRow("info:doi/test-annotation-2", "info:doi/test-article-2");
    insertAnnotationRow("info:doi/test-annotation-3", "info:doi/test-annotation-2");
    insertAnnotationRow("info:doi/test-annotation-4", null);

    //Annotations that loop back on to each other
    insertAnnotationRow("info:doi/loop-annotation-1","info:doi/loop-annotation-2");
    insertAnnotationRow("info:doi/loop-annotation-2","info:doi/loop-annotation-3");
    insertAnnotationRow("info:doi/loop-annotation-3","info:doi/loop-annotation-1");

    resolverDAOService = new JdbcResolverService(dataSource);
  }

  @DataProvider(name = "articleDois")
  public Object[][] getArticleDois() {
    return new Object[][]{
        {"info:doi/test-article-1", true},
        {"info:doi/test-article-2", true},
        {"info:doi/test-annotation-1", false},
        {"info:doi/bogus-doi", false}
    };
  }

  @Test(dataProvider = "articleDois")
  public void testIsArticle(String doi, boolean isArticle) {
    if (isArticle) {
      assertTrue(resolverDAOService.doiIsArticle(doi), "DOI wasn't correctly determined to be an article");
    } else {
      assertFalse(resolverDAOService.doiIsArticle(doi), "DOI wasn't correctly determined not to be an article");
    }
  }

  @DataProvider(name = "annotationDois")
  public Object[][] getAnnotationDois() {
    return new Object[][]{
        {"info:doi/test-annotation-1", true},
        {"info:doi/test-annotation-2", true},
        {"info:doi/test-article-1", false},
        {"info:doi/bogus-doi", false},
    };
  }

  @Test(dataProvider = "annotationDois")
  public void testIsAnnotation(String doi, boolean isAnnotation) {
    if (isAnnotation) {
      assertTrue(resolverDAOService.doiIsAnnotation(doi), "DOI wasn't correctly determined to be an anntotation");
    } else {
      assertFalse(resolverDAOService.doiIsAnnotation(doi), "DOI wasn't correctly determined not to be an annotation");
    }
  }

  @DataProvider(name = "annotatedRoots")
  public Object[][] getAnnotatedRoots() {
    return new Object[][]{
        {"info:doi/test-annotation-1", "info:doi/test-article-1"},
        {"info:doi/test-annotation-2", "info:doi/test-article-2"},
        {"info:doi/test-annotation-3", "info:doi/test-article-2"},
        {"info:doi/test-annotation-4", "info:doi/test-annotation-4"}
    };
  }

  @Test(dataProvider = "annotatedRoots")
  public void testGetAnnotatedRoot(String doi, String annotatedRoot) throws AnnotationLoopException {
    assertEquals(resolverDAOService.getAnnotatedRoot(doi), annotatedRoot, "Didn't return correct annotated root");
  }

  @DataProvider(name = "loopedAnnotations")
  public Object[][] getLoopedAnnotationUris() {
    return new Object[][]{
        {"info:doi/loop-annotation-1"},
        {"info:doi/loop-annotation-2"},
        {"info:doi/loop-annotation-3"},
    };
  }

  @Test(dataProvider = "loopedAnnotations",expectedExceptions = {AnnotationLoopException.class})
  public void testLoopDetection(String doi) throws AnnotationLoopException {
    resolverDAOService.getAnnotatedRoot(doi);
  }
}
