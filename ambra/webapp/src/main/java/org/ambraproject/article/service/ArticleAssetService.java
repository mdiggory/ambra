/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-$today.year by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ambraproject.article.service;

import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import java.util.List;

public interface ArticleAssetService {

  /**
   * Get the Article Representation Assets by URI
   *
   * This probably returns XML and PDF all the time
   *
   * @param articleDoi the doi of the article
   * @param authId the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  public List<ArticleAsset> getArticleXmlAndPdf(final String articleDoi, final String authId) throws NoSuchObjectIdException;

  /**
   * Get the Article Asset by URI. This returns a random asset with a matching doi, and there is almost always more than one (e.g, for each image, there is
   * PNG_L,PNG_M, etc.).  Hence, this only makes sense to be used for Supplementary Info assets.
   *
   * @param assetUri uri
   * @param authId the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  public ArticleAsset getSuppInfoAsset(final String assetUri, final String authId) throws NoSuchObjectIdException;

  /**
   * Get the Article Asset by URI and type.
   *
   * @param assetUri uri
   * @param representation the representation value (XML/PDF)
   * @param authId the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  public ArticleAsset getArticleAsset(final String assetUri, final String representation, final String authId) throws NoSuchObjectIdException;

   /**
   * Return a list of Figures and Tables in DOI order.
   *
   * @param articleDoi DOI.
   * @param authId the authorization ID of the current user
   * @return Figures and Tables for the article in DOI order.
   * @throws NoSuchArticleIdException NoSuchArticleIdException.
   */
  @Transactional(readOnly = true)
  public ArticleAssetWrapper[] listFiguresTables(final String articleDoi, final String authId) throws NoSuchArticleIdException;

}
