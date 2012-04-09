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
import org.ambraproject.journal.JournalService;
import org.ambraproject.model.UserProfileInfo;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.ArticleType;
import org.ambraproject.model.article.CitationInfo;
import org.ambraproject.model.article.RelatedArticleInfo;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.ambraproject.models.UserProfile;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Journal;
import org.topazproject.ambra.models.Retraction;
import org.topazproject.ambra.models.Volume;

import java.net.URI;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Joe Osowski
 */

public class ArticleServiceImpl extends HibernateServiceImpl implements ArticleService {
  private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

  private PermissionsService permissionsService;
  private JournalService journalService;

  /**
   * Determines if the articleURI is of type researchArticle
   *
   * @param article The URI of the article
   * @param authId the authorization ID of the current user
   * @return True if the article is a research article
   * @throws org.ambraproject.ApplicationException
   *                                  if there was a problem talking to the OTM
   * @throws NoSuchArticleIdException When the article does not exist
   */
  public boolean isResearchArticle(final Article article, final String authId)
      throws ApplicationException, NoSuchArticleIdException {
    // resolve article type and supported properties
    ArticleType articleType = ArticleType.getDefaultArticleType();

    for (String artTypeUri : article.getTypes()) {
      if (ArticleType.getKnownArticleTypeForURI(URI.create(artTypeUri)) != null) {
        articleType = ArticleType.getKnownArticleTypeForURI(URI.create(artTypeUri));
        break;
      }
    }

    if (articleType == null) {
      throw new ApplicationException("Unable to resolve article type for: " + article.getDoi());
    }

    return ArticleType.isResearchArticle(articleType);
  }

  /**
   * Determines if the articleURI is of type researchArticle
   *
   * @param articleInfo The URI of the article
   * @param authId the authorization ID of the current user
   * @return True if the article is a research article
   * @throws org.ambraproject.ApplicationException
   *                                  if there was a problem talking to the OTM
   * @throws NoSuchArticleIdException When the article does not exist
   */
  public boolean isResearchArticle(final ArticleInfo articleInfo, final String authId)
      throws ApplicationException, NoSuchArticleIdException {
    ArticleType articleType = ArticleType.getDefaultArticleType();

    for (String artTypeUri : articleInfo.getTypes()) {
      if (ArticleType.getKnownArticleTypeForURI(URI.create(artTypeUri)) != null) {
        articleType = ArticleType.getKnownArticleTypeForURI(URI.create(artTypeUri));
        break;
      }
    }

    if (articleType == null) {
      throw new ApplicationException("Unable to resolve article type for: " + articleInfo.getDoi());
    }

    return ArticleType.isResearchArticle(articleType);
  }


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
                                              boolean isOrderAscending) throws ApplicationException {

    List<ArticleInfo> articlesInfo = new ArrayList<ArticleInfo>();
    Order order = isOrderAscending ? Order.asc(orderField) : Order.desc(orderField);
    List<Object[]> results = hibernateTemplate.findByCriteria(DetachedCriteria.forClass(Article.class)
        .add(Restrictions.eq("eIssn", eIssn))
        .add(Restrictions.eq("state",Article.STATE_UNPUBLISHED))
        .addOrder(order)
        .setProjection(Projections.projectionList()
            .add(Projections.property("doi"))
            .add(Projections.property("date"))));

    for(Object[] rows : results) {
      ArticleInfo articleInfo = new ArticleInfo();
      articleInfo.setDoi(rows[0].toString());
      articleInfo.setDate((Date)rows[1]);
      articlesInfo.add(articleInfo);
    }

    return articlesInfo;
  }

  /**
   * Delete an article.
   *
   * @param articleDoi the uri of the article
   * @param authId the authorization ID of the current user
   *
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  @Transactional(rollbackFor = {Throwable.class})
  public void delete(final String articleDoi, final String authId) throws NoSuchArticleIdException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    List articles = hibernateTemplate.findByCriteria(DetachedCriteria.forClass(Article.class)
          .add(Restrictions.eq("doi", articleDoi)));

    if (articles.size() == 0) {
      throw new NoSuchArticleIdException(articleDoi);
    }

    Article article = (Article)articles.get(0);

    hibernateTemplate.delete(article);
  }

  /**
   * Change an articles state.
   *
   * @param articleDoi uri
   * @param authId the authorization ID of the current user
   * @param state   state
   *
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  @Transactional(rollbackFor = {Throwable.class})
  public void setState(final String articleDoi, final String authId, final int state) throws NoSuchArticleIdException {
    permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);

    List articles = hibernateTemplate.findByCriteria(DetachedCriteria.forClass(Article.class)
          .add(Restrictions.eq("doi", articleDoi)));

    if (articles.size() == 0) {
      throw new NoSuchArticleIdException(articleDoi);
    }

    Article a = (Article)articles.get(0);
    a.setState(state);

    //Remove relationships if this article is being disabled
    //they will be created on re-ingest if necessary, but if both articles in a reciprocal relationship are
    //disabled and have the relationships removed from xml, we want to the relationships to be gone when both are reingested
    if (state == Article.STATE_DISABLED) {
      a.getRelatedArticles().clear();
    }

    hibernateTemplate.update(a);

    //log whenever someone disables or unpublishes an article
    if ((state == Article.STATE_UNPUBLISHED ||
          state == Article.STATE_DISABLED)
        && log.isInfoEnabled()) {
      DetachedCriteria criteria = DetachedCriteria.forClass(UserProfile.class)
          .setProjection(Projections.property("displayName"))
          .add(Restrictions.eq("authId",authId));
      String userName = (String) hibernateTemplate.findByCriteria(criteria, 0, 1).get(0);
      userName = userName == null ? "UNKNOWN" : userName;
      log.info("User '{}' {} the article {}",
          new String[] {userName, state == Article.STATE_DISABLED ?
            "disabled" : "unpublished", articleDoi});
    }
  }

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
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<String> getArticleDOIs(final ArticleServiceSearchParameters params) throws ParseException {

    List<Article> articles = getArticles(params);
    List<String> articleIds = new ArrayList<String>(articles.size());

    for (Article article : articles) {
      articleIds.add(article.getDoi());
    }

    return articleIds;
  }

  /**
   * Get all of the articles satisfying the given criteria.

   * @param params
   *
   * @return all of the articles satisfying the given criteria (possibly null) Key is the Article DOI.  Value is the
   *         Article itself.
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<Article> getArticles(final ArticleServiceSearchParameters params) {

    return (List<Article>) this.hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria query = session.createCriteria(Article.class);

        if (params.getStates() != null && params.getStates().length > 0) {
          List<Integer> statesList = new ArrayList<Integer>(params.getStates().length);
          for (int state : params.getStates()) {
            statesList.add(state);
          }
          query.add(Restrictions.in("state", statesList));
        }
        if (params.geteIssn() != null) {
          query.add(Restrictions.eq("eIssn", params.geteIssn()));
        }
        if (params.getOrderField() != null) {
          if (params.isOrderAscending()) {
            query.addOrder(Order.asc(params.getOrderField()));
          } else {
            query.addOrder(Order.desc(params.getOrderField()));
          }
        }

        if (params.getStartDate() != null) {
          query.add(Restrictions.ge("date", params.getStartDate()));
        }
        if (params.getEndDate() != null) {
          query.add(Restrictions.le("date", params.getEndDate()));
        }

        if (params.getMaxResults() > 0) {
          query.setMaxResults(params.getMaxResults());
        }
        //Filter the results post-db access since the kind of restriction we want isn't easily representable in sql -
        //we want only articles such that the list of full names of the author list contains ALL the names in the given
        //author array.  This is NOT an 'in' restriction or an 'equals' restriction
        List<Article> queryResults = query.list();
        List<Article> filteredResults = new ArrayList<Article>(queryResults.size());

        for (Article article : queryResults) {
          if (matchesAuthorFilter(params.getAuthors(), article.getAuthors())
              && matchesCategoriesFilter(params.getCategories(), article.getCategories())) {
            filteredResults.add(article);
          }
        }

        return filteredResults;
      }
    });
  }

  /**
   * Get an Article by URI.
   *
   * @param articleDoi URI of Article to get.
   * @param authId the authorization ID of the current user
   * @return Article with specified URI or null if not found.
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  @Transactional(readOnly = true, noRollbackFor = {SecurityException.class})
  public Article getArticle(final String articleDoi, final String authId) throws NoSuchArticleIdException {
    // sanity check parms
    if (articleDoi == null)
      throw new IllegalArgumentException("articleDoi == null");

    List articles = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Article.class)
            .add(Restrictions.eq("doi", articleDoi))
            //TODO: We should optimize this some how.  But these
            //Optimizations as defined below break the browse page.
            //Also when these are specified, we get more then one article back in the collection
            //.setFetchMode("assets", FetchMode.EAGER)
            //.setFetchMode("authors", FetchMode.EAGER)
            //.setFetchMode("editors", FetchMode.EAGER)
            //.setFetchMode("citedArticles", FetchMode.EAGER)
            );

    if (articles.size() == 0) {
      throw new NoSuchArticleIdException(articleDoi);
    }

    Article article = (Article)articles.get(0);

    //If the article is unpublished, it should not be returned if the user is not an admin
    if (article.getState() == Article.STATE_UNPUBLISHED) {
      try {
        permissionsService.checkRole(PermissionsService.ADMIN_ROLE, authId);
      } catch(SecurityException se) {
        throw new NoSuchArticleIdException(articleDoi);
      }
    }

    //If the article is disabled, don't display it ever
    if (article.getState() == Article.STATE_DISABLED) {
      throw new NoSuchArticleIdException(articleDoi);
    }

    return article;
  }

  /**
   * Get articles based on a list of Article id's.
   *
   * @param articleDois list of article id's
   * @param authId the authorization ID of the current user
   * @return <code>List&lt;Article&gt;</code> of articles requested
   * @throws java.text.ParseException when article ids are invalid
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   */
  @Transactional(readOnly = true)
  public List<Article> getArticles(List<String> articleDois, final String authId) throws ParseException, NoSuchArticleIdException
  {
    List<Article> articleList = new ArrayList<Article>();

    for (String articleDoi : articleDois)
      articleList.add(getArticle(articleDoi, authId));

    return articleList;
  }

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
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<List<String>> getArticleIssues(final String articleDoi) {
    return (List<List<String>>) hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {

        List<Object[]> articleIssues = session.createSQLQuery(
            "select {j.*}, {v.*}, {i.*} " +
                "from AggregationSimpleCollection ial, VolumeIssueList vil, JournalVolumeList jvl, Issue i, Volume v, " +
                     "Journal j " +
                "where ial.uri = :articleURI " +
                "and vil.issueUri = ial.aggregationArticleUri " +
                "and jvl.volumeUri = vil.aggregationUri " +
                "and i.aggregationUri = ial.aggregationArticleUri " +
                "and v.aggregationUri = vil.aggregationUri " +
                "and j.aggregationUri = jvl.aggregationUri " +
                "order by i.created desc ")
            .addEntity("j", Journal.class)
            .addEntity("v", Volume.class)
            .addEntity("i", Issue.class)
            .setString("articleURI", articleDoi)
            .list();

        List<List<String>> finalResults = new ArrayList<List<String>>(articleIssues.size());
        for (Object[] row : articleIssues) {
          Journal journal = (Journal) row[0];
          Volume volume = (Volume) row[1];
          Issue issue = (Issue) row[2];

          List<String> secondaryList = new ArrayList<String>();
          secondaryList.add(journal.getId().toString());  // Journal URI
          secondaryList.add(journal.getKey());            // Journal Key
          secondaryList.add(volume.getId().toString());   // Volume URI
          secondaryList.add(volume.getDisplayName());     // Volume name
          secondaryList.add(issue.getId().toString());    // Issue URI
          secondaryList.add(issue.getDisplayName());      // Issue name
          finalResults.add(secondaryList);
        }

        return finalResults;
      }
    });
  }

  /**
   * Get the articleInfo object for an article
   * @param articleDoi the ID of the article
   * @param authId the authorization ID of the current user
   * @return articleInfo
   */
  @Transactional(readOnly = true)
  @Override
  @SuppressWarnings("unchecked")
  public ArticleInfo getArticleInfo(final String articleDoi, final String authId) throws NoSuchArticleIdException {
    final Article article;
    article = getArticle(articleDoi, authId);
    final ArticleInfo articleInfo = new ArticleInfo();

    articleInfo.setId(article.getID());
    //Set properties from the dublin core
    articleInfo.setDoi(article.getDoi());
    articleInfo.setDate(article.getDate());
    articleInfo.setTitle(article.getTitle());
    articleInfo.setDescription(article.getDescription());
    articleInfo.setPublisher(article.getPublisherName());
    articleInfo.seteIssn(article.geteIssn());
    articleInfo.setCategories(article.getCategories());
    articleInfo.setTypes(article.getTypes());
    articleInfo.setPages(article.getPages());
    articleInfo.setIssue(article.getIssue());
    articleInfo.setVolume(article.getVolume());
    articleInfo.seteLocationId(article.geteLocationId());
    //Set the citation info
    CitationInfo citationInfo = new CitationInfo();
    citationInfo.setId(URI.create(article.getDoi()));
    citationInfo.setCollaborativeAuthors(article.getCollaborativeAuthors());
    //authors (list of UserProfileInfo)
    //TODO: Refactor ArticleInfo and CitationInfo objects
    //there's no reason why authors need to be attached to the citation
    List<UserProfileInfo> authors = new ArrayList<UserProfileInfo>();
    for (ArticleAuthor ac : article.getAuthors()) {
      UserProfileInfo author = new UserProfileInfo();
      author.setRealName(ac.getFullName());
      authors.add(author);
    }
    citationInfo.setAuthors(authors);
    articleInfo.setCi(citationInfo);
    //set categories
    if (article.getCategories() != null) {
      for (Category category : article.getCategories()) {
        if (category.getMainCategory() != null
            && ! articleInfo.getSubjects().contains(category.getMainCategory())) {
          articleInfo.getSubjects().add(category.getMainCategory());
        }
        if (category.getSubCategory() != null
            && ! articleInfo.getSubjects().contains(category.getSubCategory())) {
          articleInfo.getSubjects().add(category.getSubCategory());
        }
      }
      Collections.sort(articleInfo.getSubjects());
    }
    //set article type
    if (article.getTypes() != null) {
      articleInfo.setAt(article.getTypes());
    }

    //set journals
    /*fixed for NHOPE-103 */
    articleInfo.setJournals(journalService.getJournalNameForObject(URI.create(articleDoi)));
    //set Corrections
    articleInfo.setCorrections(new HashSet<URI>());
    articleInfo.getCorrections().addAll(hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(FormalCorrection.class)
            .add(Restrictions.eq("annotates", article.getDoi()))
            .setProjection(Projections.id())));
    //set Retractions
    articleInfo.setRetractions(new HashSet<URI>());
    articleInfo.getRetractions().addAll(hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Retraction.class)
            .add(Restrictions.eq("annotates", article.getDoi()))
            .setProjection(Projections.id())));
    
    //get related articles
    //this results in more queries than doing a join, but getArticle() already has security logic built in to it
    //and a very small percentage of articles even have related articles
    articleInfo.setRelatedArticles(new ArrayList<RelatedArticleInfo>(article.getRelatedArticles().size()));
    for (ArticleRelationship relationship : article.getRelatedArticles()) {
      if (relationship.getOtherArticleDoi() != null) {
        try {
          Article otherArticle = getArticle(relationship.getOtherArticleDoi(), authId);
          RelatedArticleInfo relatedArticleInfo = new RelatedArticleInfo();
          relatedArticleInfo.setUri(URI.create(otherArticle.getDoi()));
          relatedArticleInfo.setTitle(otherArticle.getTitle());
          if (!articleInfo.getRelatedArticles().contains(relatedArticleInfo)) {
            articleInfo.getRelatedArticles().add(relatedArticleInfo);
          }
        } catch (NoSuchArticleIdException e) {
          //exclude this article
        }
      }
    }

    log.debug("loaded ArticleInfo: id={}, articleTypes={}, " +
        "date={}, title={}, authors={}, related-articles={}", 
        new Object[] {articleInfo.getDoi(), articleInfo.getArticleTypeForDisplay(), articleInfo.getDate(),
            articleInfo.getTitle(), Arrays.toString(articleInfo.getAuthors().toArray()), 
            Arrays.toString(articleInfo.getRelatedArticles().toArray())});
    return articleInfo;
  }



  /**
   * Help0er method for getArticleIds() since the restriction we want doesn't appear
   *
   * @param authorFilter
   * @param authors
   * @return
   */
  private boolean matchesAuthorFilter(String[] authorFilter, List<ArticleAuthor> authors) {
    if (authorFilter != null && authorFilter.length > 0) {
      List<String> authorNames = new ArrayList<String>(authors.size());
      for (ArticleAuthor author : authors) {
        authorNames.add(author.getFullName());
      }
      for (String author : authorFilter) {
        if (!authorNames.contains(author)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Helper method for getArticleIds(), since the type of restriction needed doesn't seem to be do-able in HQL or with
   * criteria - we want articles where each of the Strings in the category array is the main category of one of the
   * article's categories
   *
   * @param filterCategories - the array of main categories that was passed in to getArticleIds() to use to filter
   *                         results
   * @param categorySet      - the 'categories' property of an article
   * @return - true if the article passes the category filter, false otherwise
   */
  private boolean matchesCategoriesFilter(String[] filterCategories, Set<Category> categorySet) {
    if (filterCategories != null && filterCategories.length > 0) {

      if (filterCategories.length > categorySet.size()) {
        return false; //can't possibly contain all the categories if there's more of them than you have
      }

      //Just get the main category
      Set<String> mainCategories = new HashSet<String>(categorySet.size());
      for (Category category : categorySet) {
        mainCategories.add(category.getMainCategory());
      }
      //check that all the filter categories are in their
      for (String cat : filterCategories) {
        if (!mainCategories.contains(cat)) {
          return false;
        }
      }

    }
    return true;
  }

  /**
   * @param permissionsService the permissions service to use
   */
  @Required
  public void setPermissionsService(PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }

  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }
}