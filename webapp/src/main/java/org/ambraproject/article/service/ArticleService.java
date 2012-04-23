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

import org.ambraproject.ApplicationException;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.models.Article;

import java.text.ParseException;
import java.util.List;

/**
 * @author Joe Osowski
 */

public interface ArticleService {
  public static final String ORDER_BY_FIELD_DATE_CREATED = "created";
  public static final String ORDER_BY_FIELD_ARTICLE_DOI = "doi";
  public static final String ORDER_BY_FIELD_DATE_PUBLISHED = "date";

  /**
   * Determines if the articleURI is of type researchArticle
   *
   * @param article The URI of the article
   * @param authId the authorization ID of the current user
   * @return True if the article is a research article
   * @throws ApplicationException
   *                                  if there was a problem talking to the OTM
   * @throws NoSuchArticleIdException When the article does not exist
   */
  public boolean isResearchArticle(final Article article, final String authId)
    throws NoSuchArticleIdException, ApplicationException;

  /**
   * Determines if the articleURI is of type researchArticle
   *
   * @param articleInfo The URI of the article
   * @param authId the authorization ID of the current user
   * @return True if the article is a research article
   * @throws ApplicationException
   *                                  if there was a problem talking to the OTM
   * @throws NoSuchArticleIdException When the article does not exist
   */
  public boolean isResearchArticle(final ArticleInfo articleInfo, final String authId)
      throws NoSuchArticleIdException, ApplicationException;

  /**
   * Returns a Map of publishable articles in the order indicated.  The key of each element is the DOI (URI) of the
   * Article.  The value of each element is the Article itself.
   *
   * @param eIssn            eIssn of the Journal that this Article belongs to (no filter if null or empty)
   * @param orderField       field on which to sort the results (no sort if null or empty) Should be one of the
   *                         ORDER_BY_FIELD_ constants from this class
   * @param isOrderAscending controls the sort order (default is "false" so default order is descending)
   * @return Map of publishable articles sorted as indicated
   * @throws org.ambraproject.ApplicationException
   *
   */
  public List<ArticleInfo> getPublishableArticles(String eIssn, String orderField,
                                              boolean isOrderAscending) throws ApplicationException;

  /**
   * Get a List of all of the Journal/Volume/Issue combinations that contain the <code>articleURI</code> which was
   * passed in. Each primary List element contains a secondary List of six Strings which are, in order: <ul>
   * <li><strong>Element 0: </strong> Journal URI</li> <li><strong>Element 1: </strong> Journal key</li>
   * <li><strong>Element 2: </strong> Volume URI</li> <li><strong>Element 3: </strong> Volume name</li>
   * <li><strong>Element 4: </strong> Issue URI</li> <li><strong>Element 5: </strong> Issue name</li> </ul> A Journal
   * might have multiple Volumes, any of which might have multiple Issues that contain the <code>articleURI</code>. The
   * primary List will always contain one element for each Issue that contains the <code>articleURI</code>.
   *
   * @param articleDoi Article DOI that is contained in the Journal/Volume/Issue combinations which will be returned
   * @return All of the Journal/Volume/Issue combinations which contain the articleURI passed in
   */
  public List<List<String>> getArticleIssues(final String articleDoi);

  /**
   * Change an articles state.
   *
   * @param articleDoi uri
   * @param authId the authorization ID of the current user
   * @param state   state
   *
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  public void setState(final String articleDoi, final String authId, final int state) throws NoSuchArticleIdException;

  /**
   * Get the ids of all articles satisfying the given criteria.
   * <p/>
   * This method calls <code>getArticles(...)</code> then parses the Article IDs from that List.
   * <p/>
   *
   * @param params
   * @return the (possibly empty) list of article ids.
   * @throws java.text.ParseException if any of the dates could not be parsed
   */
  public List<String> getArticleDOIs(final ArticleServiceSearchParameters params) throws ParseException;

  /**
   * Get all of the articles satisfying the given criteria.

   * @param params
   *
   * @return all of the articles satisfying the given criteria (possibly null) Key is the Article DOI.  Value is the
   *         Article itself.
   */
  public List<Article> getArticles(final ArticleServiceSearchParameters params);

  /**
   * Get an Article by URI.
   *
   * @param articleDoi URI of Article to get.
   * @param authId the authorization ID of the current user
   * @return Article with specified URI or null if not found.
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  public Article getArticle(final String articleDoi, final String authId)
    throws NoSuchArticleIdException;

  /**
   * Get an Article by ID.
   *
   * @param articleID ID of Article to get.
   * @param authId the authorization ID of the current user
   * @return Article with specified URI or null if not found.
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  public Article getArticle(final Long articleID, final String authId)
      throws NoSuchArticleIdException;

  /**
   * Get articles based on a list of Article id's.
   *
   * @param articleDois list of article id's
   * @param authId the authorization ID of the current user
   * @return <code>List&lt;Article&gt;</code> of articles requested
   * @throws java.text.ParseException when article ids are invalid
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  public List<Article> getArticles(final List<String> articleDois, final String authId)
    throws ParseException, NoSuchArticleIdException;


  /**
   * Get the articleInfo object for an article
   * @param articleDoi the ID of the article
   * @param authId the authorization ID of the current user
   * @return articleInfo
   */
  public ArticleInfo getArticleInfo(final String articleDoi, final String authId) throws NoSuchArticleIdException;

  /**
   * Get a basic view object for the article with the corresponding id
   *
   * @param articleID the id of the article to get
   * @return a simple wrapper around string properties of the article
   */
  public ArticleInfo getBasicArticleView(Long articleID) throws NoSuchArticleIdException;


  /**
   * Get a basic view object for the article by doi
   * @param articleDoi the doi of the article
   * @return a simple wrapper around the article with only title and doi set
   * @throws NoSuchArticleIdException if the article doesn't exist
   */
  public ArticleInfo getBasicArticleView(String articleDoi) throws NoSuchArticleIdException;

}
