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
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Scott Sterling
 * @author Joe Osowski
 */
public class ArticleAssetServiceImpl extends HibernateServiceImpl implements ArticleAssetService {
  private PermissionsService permissionsService;
  private ArticleService articleService;
  private String smallImageRep;
  private String largeImageRep;
  private String mediumImageRep;
  private static final List<String> FIGURE_AND_TABLE_CONTEXT_ELEMENTS = new ArrayList<String>(2);

  static {
    FIGURE_AND_TABLE_CONTEXT_ELEMENTS.add("fig");
    FIGURE_AND_TABLE_CONTEXT_ELEMENTS.add("table-wrap");
  }

  /**
   * Get the Article Asset by URI.
   *
   * @param assetUri uri
   * @param authId   the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  public ArticleAsset getSuppInfoAsset(final String assetUri, final String authId) throws NoSuchObjectIdException {
    // sanity check parms
    if (assetUri == null)
      throw new IllegalArgumentException("URI == null");
    checkPermissions(assetUri, authId);

    try {
      return (ArticleAsset) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(ArticleAsset.class)
              .add(Restrictions.eq("doi", assetUri)), 0, 1)
          .get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new NoSuchObjectIdException(assetUri);
    }
  }

  /**
   * Get the Article Representation Assets by URI
   * <p/>
   * This probably returns XML and PDF all the time
   *
   * @param articleDoi uri
   * @param authId     the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<ArticleAsset> getArticleXmlAndPdf(final String articleDoi, final String authId)
      throws NoSuchObjectIdException {
    checkPermissions(articleDoi, authId);
    return hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(ArticleAsset.class)
            .add(Restrictions.eq("doi", articleDoi)));
  }

  /**
   * Get the Article Asset by URI and type.
   *
   * @param assetUri       uri
   * @param representation the representation value (XML/PDF)
   * @param authId         the authorization ID of the current user
   * @return the object-info of the object
   * @throws NoSuchObjectIdException NoSuchObjectIdException
   */
  @Transactional(readOnly = true)
  public ArticleAsset getArticleAsset(final String assetUri, final String representation, final String authId)
      throws NoSuchObjectIdException {

    // sanity check parms
    if (assetUri == null)
      throw new IllegalArgumentException("URI == null");

    if (representation == null) {
      throw new IllegalArgumentException("representation == null");
    }
    checkPermissions(assetUri, authId);
    try {
      return (ArticleAsset) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(ArticleAsset.class)
              .add(Restrictions.eq("doi", assetUri))
              .add(Restrictions.eq("extension", representation)), 0, 1).get(0);
    } catch (DataAccessException e) {
      throw new NoSuchObjectIdException(assetUri);
    }
  }

  @SuppressWarnings("unchecked")
  private void checkPermissions(String assetDoi, String authId) throws NoSuchObjectIdException {
    int state;
    try {
      state = (Integer) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .setProjection(Projections.property("state"))
              .createCriteria("assets")
              .add(Restrictions.eq("doi", assetDoi)), 0, 1).get(0);
    } catch (IndexOutOfBoundsException e) {
      //article didn't exist
      throw new NoSuchObjectIdException(assetDoi);
    }

    //If the article is in an unpublished state, none of the related objects should be returned
    if (Article.STATE_UNPUBLISHED == state) {
      try {
        permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);
      } catch (SecurityException se) {
        throw new NoSuchObjectIdException(assetDoi);
      }
    }

    //If the article is disabled don't return the object ever
    if (Article.STATE_DISABLED == state) {
      throw new NoSuchObjectIdException(assetDoi);
    }
  }

  @Transactional(readOnly = true)
  public ArticleAssetWrapper[] listFiguresTables(final String articleDoi, final String authId) throws NoSuchArticleIdException {
    Article article = articleService.getArticle(articleDoi, authId);
    //keep track of dois we've added to the list so we don't duplicate assets for the same image
    Set<String> dois = new HashSet<String>(article.getAssets().size());
    List<ArticleAssetWrapper> results = new ArrayList<ArticleAssetWrapper>(article.getAssets().size());
    for (ArticleAsset asset : article.getAssets()) {
      if (FIGURE_AND_TABLE_CONTEXT_ELEMENTS.contains(asset.getContextElement()) && !dois.contains(asset.getDoi())) {
        results.add(new ArticleAssetWrapper(asset, smallImageRep, mediumImageRep, largeImageRep));
        dois.add(asset.getDoi());
      }
    }
    return results.toArray(new ArticleAssetWrapper[results.size()]);
  }

  /**
   * @param permissionsService the permissions service to use
   */
  @Required
  public void setPermissionsService(PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }

  /**
   * @param articleService the article service to use
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * Set the small image representation
   *
   * @param smallImageRep smallImageRep
   */
  public void setSmallImageRep(final String smallImageRep) {
    this.smallImageRep = smallImageRep;
  }

  /**
   * Set the medium image representation
   *
   * @param mediumImageRep mediumImageRep
   */
  public void setMediumImageRep(final String mediumImageRep) {
    this.mediumImageRep = mediumImageRep;
  }

  /**
   * Set the large image representation
   *
   * @param largeImageRep largeImageRep
   */
  public void setLargeImageRep(final String largeImageRep) {
    this.largeImageRep = largeImageRep;
  }
}
