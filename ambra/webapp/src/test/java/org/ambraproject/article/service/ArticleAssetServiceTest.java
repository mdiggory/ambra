/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.article.service;

import org.ambraproject.BaseTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudick  12/29/11
 */
public class ArticleAssetServiceTest extends BaseTest {

  @Autowired
  protected ArticleAssetService articleAssetService;

  @DataProvider(name = "articleAssets")
  public Object[][] getArticleAssets() {
    Article article = new Article();
    article.setDoi("id:test-doi-for-assets");
    article.setState(Article.STATE_UNPUBLISHED); //use admin auth id to retrieve
    article.setAssets(new ArrayList<ArticleAsset>(2));

    ArticleAsset asset1 = new ArticleAsset();
    asset1.setDoi("id:test-doi-for-assets.t001");
    asset1.setExtension("foo");
    asset1.setContentType("application/pdf");
    asset1.setDescription("A FOO asset");
    asset1.setSize(1032587L);
    article.getAssets().add(asset1);

    ArticleAsset asset2 = new ArticleAsset();
    asset2.setDoi("id:test-doi-for-assets.t002");
    asset2.setExtension("bar");
    asset2.setContentType("tif");
    asset2.setDescription("A Bar asset");
    asset2.setSize(92473587L);
    article.getAssets().add(asset2);

    dummyDataStore.store(article); //cascade to the assets
    return new Object[][]{
        {asset1.getDoi(), asset1.getExtension(), asset1},
        {asset2.getDoi(), asset2.getExtension(), asset2}
    };
  }

  //This tests the bad method of getAsset that just returns the first instance of an asset with the given doi
  @Test(dataProvider = "articleAssets")
  public void testGetSingleAsset(String assetUri, String notUsed, ArticleAsset expectedAsset) throws NoSuchObjectIdException {
    ArticleAsset asset = articleAssetService.getSuppInfoAsset(assetUri, DEFAULT_ADMIN_AUTHID);
    assertNotNull(asset, "returned null article asset");
    compareAssets(asset, expectedAsset);
  }

  @Test(dataProvider = "articleAssets")
  public void testGetAsset(String assetUri, String extension, ArticleAsset expectedAsset) throws NoSuchObjectIdException {
    ArticleAsset asset = articleAssetService.getArticleAsset(assetUri, extension, DEFAULT_ADMIN_AUTHID);
    assertNotNull(asset, "returned null article asset");
    compareAssets(asset, expectedAsset);
  }


  @DataProvider(name = "badArticleAssets")
  public Object[][] getBadArticleAssets() {
    Article notYetPublished = new Article();
    notYetPublished.setDoi("id:unpubbed-doi-for-assets");
    notYetPublished.setState(Article.STATE_UNPUBLISHED);
    notYetPublished.setAssets(new ArrayList<ArticleAsset>(1));

    ArticleAsset asset1 = new ArticleAsset();
    asset1.setDoi("id:unpubbed-doi-for-assets.t001");
    asset1.setExtension("foo");
    notYetPublished.getAssets().add(asset1);

    Article deliberatelyUnpublished = new Article();
    deliberatelyUnpublished.setDoi("id:disable-doi-for-assets");
    deliberatelyUnpublished.setState(Article.STATE_DISABLED);
    deliberatelyUnpublished.setAssets(new ArrayList<ArticleAsset>(1));

    ArticleAsset asset2 = new ArticleAsset();
    asset2.setDoi("id:disabled-doi-for-assets.t002");
    asset2.setExtension("bar");
    deliberatelyUnpublished.getAssets().add(asset2);

    dummyDataStore.store(notYetPublished); //cascade to the assets
    return new Object[][]{
        //users shouldn't see unpublished asset
        {asset1.getDoi(), asset1.getExtension(), DEFUALT_USER_AUTHID},
        //even admins shouldn't see disabled asset
        {asset2.getDoi(), asset2.getExtension(), DEFAULT_ADMIN_AUTHID}
    };
  }

  @Test(dataProvider = "badArticleAssets", expectedExceptions = {NoSuchObjectIdException.class})
  public void testShouldFailForUnpublishedArticles(String assetUri, String extension, String authId) throws NoSuchObjectIdException {
    articleAssetService.getArticleAsset(assetUri, extension, authId);
  }

  @DataProvider(name = "articleXmlAndPdf")
  public Object[][] getArticleXmlAndPdf() {
    Article article = new Article();
    article.setDoi("id:doi-for-getXmlAndPdf");
    article.setAssets(new ArrayList<ArticleAsset>(2));

    ArticleAsset xml = new ArticleAsset();
    xml.setContentType("text/xml");
    xml.setExtension("XML");
    xml.setDoi("id:doi-for-getXmlAndPdf");
    article.getAssets().add(xml);

    ArticleAsset pdf = new ArticleAsset();
    pdf.setContentType("application/pdf");
    pdf.setExtension("PDF");
    pdf.setDoi("id:doi-for-getXmlAndPdf");
    article.getAssets().add(pdf);

    dummyDataStore.store(article);

    return new Object[][]{
        {article.getDoi(), xml, pdf}
    };
  }

  @Test(dataProvider = "articleXmlAndPdf")
  public void testGetArticleXmlAndPdf(String doi, ArticleAsset xml, ArticleAsset pdf) throws NoSuchObjectIdException {
    List<ArticleAsset> articleXmlAndPdf = articleAssetService.getArticleXmlAndPdf(doi, DEFAULT_ADMIN_AUTHID);
    assertNotNull(articleXmlAndPdf, "returned null list for xml and pdf");
    assertEquals(articleXmlAndPdf.size(), 2, "returned incorrect number of assets");

    //sort the list so it's in PDF, XML order
    Collections.sort(articleXmlAndPdf, new Comparator<ArticleAsset>() {
      @Override
      public int compare(ArticleAsset articleAsset, ArticleAsset articleAsset1) {
        return articleAsset.getExtension().compareTo(articleAsset1.getExtension());
      }
    });
    compareAssets(articleXmlAndPdf.get(0), pdf);
    compareAssets(articleXmlAndPdf.get(1), xml);
  }

  @DataProvider(name = "badXmlAndPdf")
  public Object[][] getUnpubbedArticles() {
    Article notYetPubbed = new Article();
    notYetPubbed.setState(Article.STATE_UNPUBLISHED);
    notYetPubbed.setDoi("id:not-yet-pubbed-for-xmlandpdf");

    Article deliberatelyUnpubbed = new Article();
    deliberatelyUnpubbed.setDoi("id:unpubbed-for-xml-and-pdf");
    deliberatelyUnpubbed.setState(Article.STATE_DISABLED);

    dummyDataStore.store(notYetPubbed);
    dummyDataStore.store(deliberatelyUnpubbed);

    return new Object[][]{
        {notYetPubbed.getDoi(), DEFUALT_USER_AUTHID},
        {deliberatelyUnpubbed.getDoi(), DEFAULT_ADMIN_AUTHID}
    };
  }

  @Test(dataProvider = "badXmlAndPdf", expectedExceptions = {NoSuchObjectIdException.class})
  public void testXmlAndPdfFailsForUnpublishedArticles(String doi, String authId) throws NoSuchObjectIdException {
    articleAssetService.getArticleXmlAndPdf(doi, authId);
  }

  @DataProvider(name = "figuresTables")
  public Object[][] getFiguresTables() {
    Article article = new Article();
    article.setDoi("id:doi-for-fig-table-method");
    article.setState(Article.STATE_UNPUBLISHED); //use admin auth id to retrieve
    article.setAssets(new ArrayList<ArticleAsset>(5));

    ArticleAsset asset1 = new ArticleAsset();
    asset1.setContextElement("fig");
    asset1.setDoi("id:doi-for-fig-table-method.t001");
    asset1.setExtension("png_s");
    asset1.setTitle("some title");
    article.getAssets().add(asset1);

    ArticleAsset asset2 = new ArticleAsset();
    asset2.setContextElement("fig");
    asset2.setDoi("id:doi-for-fig-table-method.t001");
    asset2.setExtension("png_m");
    asset2.setTitle("some title");
    article.getAssets().add(asset2);

    ArticleAsset asset3 = new ArticleAsset();
    asset3.setContextElement("table-wrap");
    asset3.setDoi("id:doi-for-fig-table-method.t002");
    asset3.setExtension("png_s");
    asset3.setTitle("some other title");
    article.getAssets().add(asset3);

    ArticleAsset asset4 = new ArticleAsset();
    asset4.setContextElement("table-wrap");
    asset4.setDoi("id:doi-for-fig-table-method.t002");
    asset4.setExtension("png_m");
    asset4.setTitle("some other title");
    article.getAssets().add(asset4);

    ArticleAsset notAFigureOrTable = new ArticleAsset();
    notAFigureOrTable.setContextElement("body");
    notAFigureOrTable.setDoi("id:doi-for-fig-table-method");
    notAFigureOrTable.setExtension("XML");
    article.getAssets().add(notAFigureOrTable);

    dummyDataStore.store(article);

    //1 and 2 have same doi, 3 and 4 have same doi
    //so the method should only return two asset wrappers
    ArticleAssetWrapper wrapper1 = new ArticleAssetWrapper(asset1, "png_s", "png_m", "png_l");
    ArticleAssetWrapper wrapper2 = new ArticleAssetWrapper(asset3, "png_s", "png_m", "png_l");

    return new Object[][]{
        {article.getDoi(), new ArticleAssetWrapper[]{wrapper1, wrapper2}}
    };
  }

  @Test(dataProvider = "figuresTables")
  public void testListFiguresTables(String articleDoi, ArticleAssetWrapper[] expectedAssets) throws NoSuchArticleIdException {
    ArticleAssetWrapper[] assetWrappers = articleAssetService.listFiguresTables(articleDoi, DEFAULT_ADMIN_AUTHID);
    assertNotNull(assetWrappers, "returned null array of asset wrappers");
    assertEquals(assetWrappers, expectedAssets, "returned incorrect asset wrappers");
  }

  @DataProvider(name = "badArticles")
  public Object[][] getBadArticles() {
    Article unpubbed = new Article();
    unpubbed.setDoi("id:unpubbedForListFiguresTables");
    unpubbed.setState(Article.STATE_UNPUBLISHED);
    dummyDataStore.store(unpubbed);
    
    Article disabled = new Article();
    disabled.setDoi("id:disabledForListFiguresTables");
    disabled.setState(Article.STATE_DISABLED);
    dummyDataStore.store(disabled);

    return new Object[][]{
        {unpubbed.getDoi(), DEFUALT_USER_AUTHID},
        {disabled.getDoi(), DEFAULT_ADMIN_AUTHID}
    };
  }
  
  @Test(dataProvider = "badArticles", expectedExceptions = {NoSuchArticleIdException.class})
  public void testListFiguresTablesOnUnpubbedArticle(String doi, String authId) throws NoSuchArticleIdException {
    articleAssetService.listFiguresTables(doi, authId);
  }
}
