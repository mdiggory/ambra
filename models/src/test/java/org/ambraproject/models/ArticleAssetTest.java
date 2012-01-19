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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 11/9/11
 */
public class ArticleAssetTest extends BaseHibernateTest {

  @Test
  public void testSaveAsset() {
    ArticleAsset asset =new ArticleAsset();
    asset.setContentType("pdf1");
    asset.setDoi("doi23");
    asset.setExtension("extension12");

    Long id = (Long) hibernateTemplate.save(asset);
    assertNotNull(id, "generated null id");
    asset = (ArticleAsset) hibernateTemplate.get(ArticleAsset.class, id);
    assertNotNull(asset, "couldn't retrieve asset");
    assertNotNull(asset.getCreated(),"Create date didn't get generated");

    assertEquals(asset.getContentType(), "pdf1", "incorrect content type");
    assertEquals(asset.getDoi(),"doi23","incorrect doi");

  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullDoi() {
    ArticleAsset articleAsset = new ArticleAsset();
    articleAsset.setExtension("foo1");
    hibernateTemplate.save(articleAsset);
  }
  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullExtension() {
    ArticleAsset articleAsset = new ArticleAsset();
    articleAsset.setDoi("foo1");
    hibernateTemplate.save(articleAsset);
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueConstraint() {
    ArticleAsset asset1 = new ArticleAsset();
    asset1.setDoi("doiNOTUNIQUE");
    asset1.setExtension("extensionNOTUNIQUE");
    ArticleAsset asset2 = new ArticleAsset();
    asset2.setDoi(asset1.getDoi());
    asset2.setExtension(asset1.getExtension());
    hibernateTemplate.save(asset1);
    hibernateTemplate.save(asset2);
  }

}
