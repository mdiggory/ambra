/* $HeadURL::                                                                            $
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
package org.ambraproject.article.action;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import org.ambraproject.ApplicationException;
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.article.AuthorExtra;
import org.ambraproject.article.CitationReference;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.FetchArticleService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.journal.JournalService;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.ArticleType;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.ArticleView;
import org.ambraproject.models.UserProfile;
import org.ambraproject.rating.service.RatingsService;
import org.ambraproject.struts2.AmbraFreemarkerConfig;
import org.ambraproject.trackback.TrackbackService;
import org.ambraproject.user.service.UserService;
import org.ambraproject.util.TextUtils;
import org.ambraproject.util.UriUtil;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.ArticleCategory;
import org.ambraproject.views.RatingSummaryView;
import org.ambraproject.views.TrackbackView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Journal;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static org.ambraproject.annotation.service.AnnotationService.AnnotationOrder;

/**
 * This class fetches the information from the service tier for the artcle Tabs.  Common data is defined in the
 * setCommonData.  One method is defined for each tab.
 * <p/>
 * Freemarker builds rest like URLs, inbound and outbound as defined in the /WEB-INF/urlrewrite.xml file. These URLS
 * then map to the methods are referenced in the struts.xml file.
 * <p/>
 * ex: http://localhost/article/related/info%3Adoi%2F10.1371%2Fjournal.pone.0299
 * <p/>
 * Gets rewritten to:
 * <p/>
 * http://localhost/fetchRelatedArticle.action&amp;articleURI=info%3Adoi%2F10.1371%2Fjournal.pone.0299
 * <p/>
 * Struts picks this up and translates it call the FetchArticleRelated method ex: &lt;action name="fetchRelatedArticle"
 * class="org.ambraproject.article.action.FetchArticleAction" method="FetchArticleRelated"&gt;
 */
public class FetchArticleAction extends BaseSessionAwareActionSupport {
  private static final Logger log = LoggerFactory.getLogger(FetchArticleAction.class);
  private final ArrayList<String> messages = new ArrayList<String>();

  private String articleURI;
  private String transformedArticle;
  private String annotationId = "";
  private int pageCount = 0;

  private int totalNumAnnotations = 0;
  private int numCorrections = 0;
  private int numComments = 0;
  private boolean isRetracted = false;
  private List<AnnotationView> formalCorrections = new ArrayList<AnnotationView>();
  private List<AnnotationView> minorCorrections = new ArrayList<AnnotationView>();
  private List<AnnotationView> retractions = new ArrayList<AnnotationView>();

  //commentary holds the comments that are being listed
  private AnnotationView[] commentary = new AnnotationView[0];
  private boolean isDisplayingCorrections = false;

  private boolean isResearchArticle;
  private boolean hasRated;
  private String publishedJournal = "";

  private ArticleInfo articleInfoX;
  private ArticleType articleType;
  private List<List<String>> articleIssues;
  private List<TrackbackView> trackbackList;
  private int trackbackCount;
  private ArrayList<AuthorExtra> authorExtras;
  private ArrayList<CitationReference> references;
  private String journalAbbrev;

  private FetchArticleService fetchArticleService;
  private AnnotationService annotationService;
  private JournalService journalService;
  private RatingsService ratingsService;
  private ArticleService articleService;
  private TrackbackService trackbackService;
  private AmbraFreemarkerConfig ambraFreemarkerConfig;
  private UserService userService;

  private Set<Journal> journalList;
  private RatingSummaryView averageRatings;

  /**
   * Fetch common data the article HTML text
   *
   * @return "success" on succes, "error" on error
   */
  @Transactional
  public String fetchArticle() {
    try {

      setCommonData();
      //get the corrections without replies loaded up, and ordered oldest to newest. We don't need to show a count of replies on the main article page
      AnnotationView[] annotationViews = annotationService.listAnnotationsNoReplies(
          articleInfoX.getId(),
          EnumSet.of(AnnotationType.FORMAL_CORRECTION,AnnotationType.MINOR_CORRECTION,AnnotationType.RETRACTION),
          AnnotationOrder.OLDEST_TO_NEWEST
      );
      for (AnnotationView annotationView : annotationViews) {
        switch (annotationView.getType()) {
          case FORMAL_CORRECTION:
            formalCorrections.add(annotationView);
            break;
          case MINOR_CORRECTION:
            minorCorrections.add(annotationView);
            break;
          case RETRACTION:
            retractions.add(annotationView);
            break;
        }
      }
      numCorrections = annotationViews.length;


      transformedArticle = fetchArticleService.getArticleAsHTML(articleInfoX);
    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }

    //If the user is logged in, record this as an article view
    UserProfile user = getCurrentUser();
    if (user != null) {
      try {
        userService.recordArticleView(user.getID(), articleInfoX.getId(), ArticleView.Type.ARTICLE_VIEW);
      } catch (Exception e) {
        log.error("Error recording an article view for user: " + user.getID() + " and article: " + articleInfoX.getId());
      }
    }
    return SUCCESS;
  }

  /**
   * Fetch common data and annotations
   *
   * @return "success" on success, "error" on error
   */
  @Transactional(readOnly = true)
  public String fetchArticleComments() {
    try {
      setCommonData();

      //let the view layer know whether we are displaying corrections or not, so we can have different links
      isDisplayingCorrections = false;

      commentary = annotationService.listAnnotations(
          articleInfoX.getId(),
          EnumSet.of(AnnotationType.COMMENT, AnnotationType.NOTE),
          AnnotationOrder.MOST_RECENT_REPLY);

      //have to count the corrections so we know whether to show a 'Show All corrections' link
      numCorrections = annotationService.countAnnotations(articleInfoX.getId(),
          EnumSet.of(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION));
      //have to indicate if there's a retraction so we know whether to show a 'Show Retraction' link
      isRetracted = annotationService.countAnnotations(articleInfoX.getId(), EnumSet.of(AnnotationType.RETRACTION)) > 0;

    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetch common data and article corrections
   *
   * @return "success" on succes, "error" on error
   */
  @Transactional(readOnly = true)
  public String fetchArticleCorrections() {
    try {
      setCommonData();
      //let the view layer know whether we are displaying corrections or not, so we can have different links
      isDisplayingCorrections = true;
      commentary = annotationService.listAnnotations(
          articleInfoX.getId(),
          EnumSet.of(AnnotationType.FORMAL_CORRECTION,AnnotationType.MINOR_CORRECTION,AnnotationType.RETRACTION),
          AnnotationOrder.MOST_RECENT_REPLY
      );


      //have to count comments so we know whether to show a 'View All Comments' link
      numComments = annotationService.countAnnotations(articleInfoX.getId(),
          EnumSet.of(AnnotationType.COMMENT, AnnotationType.NOTE));

    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetches common data and the trackback list.
   *
   * @return "success" on succes, "error" on error
   */
  @Transactional(readOnly = true)
  public String fetchArticleMetrics() {
    try {
      setCommonData();

      trackbackCount = trackbackService.countTrackbacksForArticle(articleURI);

    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetches common data and the trackback list.
   *
   * @return "success" on succes, "error" on error
   */
  @Transactional(readOnly = true)
  public String fetchArticleRelated() {
    try {
      setCommonData();

      trackbackList = trackbackService.getTrackbacksForArticle(articleURI);

    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Fetches common data and nothing else
   *
   * @return "success" on succes, "error" on error
   */
  @Transactional(readOnly = true)
  public String fetchArticleCrossRef() {
    try {
      setCommonData();
    } catch (NoSuchArticleIdException e) {
      messages.add("No article found for id: " + articleURI);
      log.info("Could not find article: " + articleURI, e);
      return ERROR;
    } catch (Exception e) {
      messages.add(e.getMessage());
      log.error("Error retrieving article: " + articleURI, e);
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Sets up data used by the right hand column in the freemarker templates
   * <p/>
   * TODO: Review the data fetched by this; it's called on every tab and fetches more than is necessary (e.g.
   * articleInfo)
   *
   * @throws ApplicationException     when there is an error talking to the OTM
   * @throws NoSuchArticleIdException when the article can not be found
   */
  private void setCommonData() throws ApplicationException, NoSuchArticleIdException {
    try {
      UriUtil.validateUri(articleURI, "articleURI=<" + articleURI + ">");
    } catch (Exception e) {
      throw new NoSuchArticleIdException(articleURI, e.getMessage(), e);
    }

    articleInfoX = articleService.getArticleInfo(articleURI, getAuthId());
    averageRatings = ratingsService.getAverageRatings(articleInfoX.getId());
    journalList = journalService.getJournalsForObject(articleURI);
    isResearchArticle = articleService.isResearchArticle(articleInfoX, getAuthId());
    hasRated = ratingsService.hasRated(articleInfoX.getId(), getCurrentUser());
    articleIssues = articleService.getArticleIssues(articleURI);
    //count all the comments and corrections
    totalNumAnnotations = annotationService.countAnnotations(articleInfoX.getId(),
        EnumSet.of(AnnotationType.NOTE, AnnotationType.COMMENT, AnnotationType.MINOR_CORRECTION,
            AnnotationType.FORMAL_CORRECTION, AnnotationType.RETRACTION));

    articleType = ArticleType.getDefaultArticleType();
    for (String artType : this.articleInfoX.getTypes()) {
      URI articleTypeUri = URI.create(artType);
      if (ArticleType.getKnownArticleTypeForURI(articleTypeUri) != null) {
        articleType = ArticleType.getKnownArticleTypeForURI(articleTypeUri);
        break;
      }
    }

    String pages = this.articleInfoX.getPages();

    if (pages != null && pages.indexOf("-") > 0 && pages.split("-").length > 1) {
      String t = pages.split("-")[1];

      try {
        pageCount = Integer.parseInt(t);
      } catch (NumberFormatException ex) {
        log.warn("Not able to parse page count from citation pages property with value of (" + t + ")");
        pageCount = 0;
      }
    }

    Document doc = this.fetchArticleService.getArticleDocument(articleInfoX);
    authorExtras = this.fetchArticleService.getAuthorAffiliations(doc);
    references = this.fetchArticleService.getReferences(doc);
    journalAbbrev = this.fetchArticleService.getJournalAbbreviation(doc);

    /**
     An article can be cross published, but we want the source journal.
     If in this collection an article eIssn matches the article's eIssn keep that value.
     freemarker_config.getDisplayName(journalContext)}">
     **/
    for (Journal j : journalList) {
      if (articleInfoX.geteIssn().equals(j.geteIssn())) {
        publishedJournal = ambraFreemarkerConfig.getDisplayName(j.getKey());
      }
    }
  }

  /**
   * Set the fetch article service
   *
   * @param articleService articleService
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * Set the fetch article service
   *
   * @param fetchArticleService fetchArticleService
   */
  @Required
  public void setFetchArticleService(final FetchArticleService fetchArticleService) {
    this.fetchArticleService = fetchArticleService;
  }

  /**
   * @param trackBackservice The trackBackService to set.
   */
  @Required
  public void setTrackBackService(TrackbackService trackBackservice) {
    this.trackbackService = trackBackservice;
  }

  /**
   * Set the ratings service.
   *
   * @param ratingsService the ratings service
   */
  @Required
  public void setRatingsService(final RatingsService ratingsService) {
    this.ratingsService = ratingsService;
  }

  /**
   * @param annotationService The annotationService to set.
   */
  @Required
  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  /**
   * @param journalService The journalService to set.
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }


  /**
   * @return articleURI
   */
  @RequiredStringValidator(message = "Article URI is required.")
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * Set articleURI to fetch the article for.
   *
   * @param articleURI articleURI
   */
  public void setArticleURI(final String articleURI) {
    this.articleURI = articleURI;
  }

  /**
   * @return transformed output
   */
  public String getTransformedArticle() {
    return transformedArticle;
  }

  /**
   * Return the ArticleInfo from the Browse cache.
   * <p/>
   *
   * @return Returns the articleInfoX.
   */
  public ArticleInfo getArticleInfoX() {
    return articleInfoX;
  }

  /**
   * @return Returns the annotationId.
   */
  public String getAnnotationId() {
    return annotationId;
  }

  /**
   * @param annotationId The annotationId to set.
   */
  public void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }

  /**
   * Gets the article Type
   *
   * @return Returns articleType
   */
  public ArticleType getArticleType() {
    return articleType;
  }

  public ArrayList<String> getMessages() {
    return messages;
  }

  /**
   * @return Returns the journalList.
   */
  public Set<Journal> getJournalList() {
    return journalList;
  }

  /**
   * @return Returns the names and URIs of all of the Journals, Volumes, and Issues to which this Article has been
   *         attached.  This includes "collections", but does not include the
   */
  public List<List<String>> getArticleIssues() {
    return articleIssues;
  }

  /**
   * Tests if this article has been rated by the current user
   *
   * @return Returns the hasRated.
   */
  public boolean getHasRated() {
    return hasRated;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  /*
  * Gets averageRatings info
  *
  * @return returns averageRatings info
  * */
  public RatingSummaryView getAverageRatings() {
    return averageRatings;
  }

  /**
   * @return Returns the trackbackList.
   */
  public List<TrackbackView> getTrackbackList() {
    return trackbackList;
  }

  public int getTrackbackCount() {
    return trackbackCount;
  }

  public String getPublishedJournal() {
    return publishedJournal;
  }

  /**
   * If available, return the current count of pages.
   *
   * @return the current article's page count
   */
  public int getPageCount() {
    return pageCount;
  }

  /**
   * Return a comma delimited string of authors.
   *
   * @return Comma delimited string of authors
   */
  public String getAuthorNames() {
    StringBuilder sb = new StringBuilder();

    //Fixed for JIRA Id: NHOPE-88

    for (AuthorExtra author : authorExtras) {
      if (sb.length() > 0) {
        sb.append(", ");
      }

      sb.append(author.getAuthorName());
    }

    return sb.toString();
  }

  /**
   * @return an array of formal corrections
   */
  public List<AnnotationView> getFormalCorrections() {
    return this.formalCorrections;
  }

  /**
   * @return an array of retractions
   */
  public List<AnnotationView> getRetractions() {
    return this.retractions;
  }

  public List<AnnotationView> getMinorCorrections() {
    return minorCorrections;
  }

  public int getNumComments() {
    return numComments;
  }

  public int getNumCorrections() {
    return numCorrections;
  }

  public int getTotalNumAnnotations() {
    return totalNumAnnotations;
  }

  public AnnotationView[] getCommentary() {
    return commentary;
  }

  public boolean getIsDisplayingCorrections() {
    return isDisplayingCorrections;
  }

  public boolean getIsRetracted() {
    return isRetracted;
  }

  /**
   * Return a list of this article's main categories
   *
   * @return a Set<String> of category names
   * @throws ApplicationException when the article has not been set
   */
  public List<String> getMainCategories() throws ApplicationException {
    Set<String> mainCats = new HashSet<String>();

    if (articleInfoX == null) {
      throw new ApplicationException("Article not set");
    }

    for (ArticleCategory curCategory : articleInfoX.getCategories()) {
      mainCats.add(curCategory.getMainCategory());
    }

    List<String> mainCatsList = new LinkedList<String>(mainCats);
    Collections.sort(mainCatsList);

    return mainCatsList;
  }

  /**
   * Set the config class containing all of the properties used by the Freemarker templates so those values can be used
   * within this Action class.
   *
   * @param ambraFreemarkerConfig All of the configuration properties used by the Freemarker templates
   */
  @Required
  public void setAmbraFreemarkerConfig(final AmbraFreemarkerConfig ambraFreemarkerConfig) {
    this.ambraFreemarkerConfig = ambraFreemarkerConfig;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Returns a list of author affiliations
   *
   * @return author affiliations
   */
  public ArrayList<AuthorExtra> getAuthorExtras() {
    return this.authorExtras;
  }

  /**
   * Returns a list of citation references
   *
   * @return citation references
   */
  public ArrayList<CitationReference> getReferences() {
    return this.references;
  }

  /**
   * Returns abbreviated journal name
   *
   * @return abbreviated journal name
   */
  public String getJournalAbbrev() {
    return this.journalAbbrev;
  }

  /**
   * Returns article description
   * <p/>
   * //TODO: This is a pretty heavy weight function that gets called for every article request to get a value that
   * rarely changes.  Should we just make the value in the database correct on ingest?
   *
   * @return
   */
  public String getArticleDescription() {
    return TextUtils.transformXMLtoHtmlText(articleInfoX.getDescription());
  }

}
