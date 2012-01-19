/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.ambraproject.rating.action;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.topazproject.ambra.models.Rating;
import org.topazproject.ambra.models.RatingContent;
import org.topazproject.ambra.models.RatingSummary;
import org.topazproject.ambra.models.RatingSummaryContent;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.user.AmbraUser;
import org.ambraproject.util.ProfanityCheckingService;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * General Rating action class to store and retrieve a users's rating
 *
 * @author Stephen Cheng
 */
@SuppressWarnings("serial")
public class RateAction extends AbstractRatingAction {
  private static final Logger log = LoggerFactory.getLogger(RateAction.class);

  private double                   insight;
  private double                   reliability;
  private double                   style;
  private double                   singleRating;
  private String                   articleURI;
  private boolean                  isResearchArticle;
  private String                   commentTitle;
  private String                   ciStatement;
  private Date                     rateDate;
  private String                   isCompetingInterest;
  private String                   comment;
  private ProfanityCheckingService profanityCheckingService;
  private PermissionsService       permissionsService;

  /**
   * Rates an article for the currently logged in user.  Will look to see if there are
   * existing rating. If so, will update the ratings, otherwise will insert new ones.
   *
   * @return WebWork action status
   */
  @SuppressWarnings("unchecked")
  @Transactional(rollbackFor = { Throwable.class })
  public String rateArticle() {
    final AmbraUser user = getCurrentUser();
    final Date      now  = new Date(System.currentTimeMillis());
    final URI       annotatedArticle;

    try {
      annotatedArticle = new URI(articleURI);
    } catch (URISyntaxException ue) {
      log.info("Could not construct article URI: " + articleURI, ue);

      return ERROR;
    }

    if (user == null) {
      log.info("User is null for rating articles");
      addActionError("Must be logged in");

      return ERROR;
    }

    this.permissionsService.checkLogin(user.getAuthId());

    try {
      isResearchArticle = articleService.isResearchArticle(articleURI, getAuthId());
    } catch (Exception ae) {
      log.info("Could not get article info for: " + articleURI, ae);
      return ERROR;
    }

    if (isResearchArticle) {
      // must rate at least one rating category
      if (insight == 0 && reliability == 0 && style == 0) {
        addActionError("At least one category must be rated");
        return INPUT;
      }
    } else {
      // ensure the single rating specified
      if (singleRating == 0) {
        addActionError("A rating must be specified.");
        return INPUT;
      }
    }

    if (this.isCompetingInterest == null || this.isCompetingInterest.trim().length() == 0) {
      addFieldError("statement", "You must specificy whether you have a competing interest or not");
      return INPUT;
    } else {
      if (Boolean.parseBoolean(isCompetingInterest)) {
        if (StringUtils.isEmpty(ciStatement)) {
          addFieldError("statement", "You must say something in your competing interest statement");
          return INPUT;
        } else {
          if(ciStatement.length() > RatingContent.MAX_CISTATEMENT_LENGTH) {
            addFieldError("statement", "Your competing interest statement is " +
                ciStatement.length() + " characters long, it can not be longer than " +
                RatingContent.MAX_CISTATEMENT_LENGTH + ".");
            return INPUT;
          }
        }
      }
    }

    if (!StringUtils.isEmpty(comment)) {
      if(comment.length() > RatingContent.MAX_COMMENT_LENGTH) {
        addFieldError("comment", "Your comment is " + comment.length() +
            " characters long, it can not be longer than " + RatingContent.MAX_COMMENT_LENGTH + ".");
        return INPUT;
      }
    }

    if (!StringUtils.isEmpty(commentTitle)) {
      if(commentTitle.length() > RatingContent.MAX_TITLE_LENGTH) {
        addFieldError("commentTitle", "Your title is " + commentTitle.length() +
            " characters long, it can not be longer than " + RatingContent.MAX_TITLE_LENGTH + ".");
        return INPUT;
      }
    }    

     // reject profanity in content
    final List<String> profaneWordsInCommentTitle = profanityCheckingService.validate(commentTitle);
    final List<String> profaneWordsInComment      = profanityCheckingService.validate(comment);
    final List<String> profaneWordsInCIStatement  = profanityCheckingService.validate(ciStatement);
    if (profaneWordsInCommentTitle.size() != 0 || profaneWordsInComment.size() != 0 ||
        profaneWordsInCIStatement.size() != 0) {
      addProfaneMessages(profaneWordsInCommentTitle, "commentTitle", "title");
      addProfaneMessages(profaneWordsInComment, "comment", "comment");
      addProfaneMessages(profaneWordsInComment, "ciStatementArea", "statement");
      return INPUT;
    }

    Rating        articleRating;
    RatingSummary articleRatingSummary;
    boolean       newRating = false;

    if (log.isDebugEnabled()) {
      log.debug("Retrieving user Ratings for article: " + articleURI + " and user: " +
                user.getUserId());
    }

    // Ratings by this User for Article
    List<Rating> ratingsList = ratingsService.getRatingsList(articleURI, user.getUserId());

    if (ratingsList.size() == 0) {
      newRating = true;
      articleRating = new Rating();
      articleRating.setAnnotates(annotatedArticle);
      articleRating.setContext("");
      articleRating.setCreator(user.getUserId());
      articleRating.setCreated(now);
      articleRating.setBody(new RatingContent());

      ratingsService.saveRating(articleRating);
    } else if (ratingsList.size() == 1) {
      articleRating = ratingsList.get(0);
    } else {
      // should never happen
      String errorMessage = "Multiple Ratings, " + ratingsList.size() + ", for Article, " +
                            articleURI + ", for user, " + user.getUserId();
      log.error(errorMessage);
      throw new RuntimeException(errorMessage);
    }

    // RatingsSummary for Article
    List<RatingSummary> summaryList = ratingsService.getRatingSummaryList(articleURI);

    if (summaryList.size() == 0) {
      articleRatingSummary = new RatingSummary();
      articleRatingSummary.setAnnotates(annotatedArticle);
      articleRatingSummary.setContext("");
      articleRatingSummary.setCreated(now);
      articleRatingSummary.setBody(new RatingSummaryContent());

      ratingsService.saveRatingSummary(articleRatingSummary);
    } else if (summaryList.size() == 1) {
      articleRatingSummary = summaryList.get(0);
    } else {
      // should never happen
      String errorMessage = "Multiple RatingsSummary, " + summaryList.size() + ", for Article " +
                            articleURI;
      log.error(errorMessage);
      throw new RuntimeException(errorMessage);
    }

    // update Rating + RatingSummary with new values
    articleRating.setCreated(now);
    articleRatingSummary.setCreated(now);

    // if they had a prior insight Rating, remove it from the RatingSummary
    if (articleRating.getBody().getInsightValue() > 0) {
      articleRatingSummary.getBody().
        removeRating(Rating.INSIGHT_TYPE, articleRating.getBody().getInsightValue());
    }

    // update insight Article Rating, don't care if new, update or 0
    articleRating.getBody().setInsightValue((int) insight);

    // if user rated insight, add to to Article RatingSummary
    if (insight > 0) {
      articleRatingSummary.getBody().addRating(Rating.INSIGHT_TYPE, (int) insight);
    }

    // if they had a prior reliability Rating, remove it from the RatingSummary
    if (articleRating.getBody().getReliabilityValue() > 0) {
      articleRatingSummary.getBody().
        removeRating(Rating.RELIABILITY_TYPE, articleRating.getBody().getReliabilityValue());
    }

    // update reliability Article Rating, don't care if new, update or 0
    articleRating.getBody().setReliabilityValue((int) reliability);

    // if user rated reliability, add to to Article RatingSummary
    if (reliability > 0) {
      articleRatingSummary.getBody().addRating(Rating.RELIABILITY_TYPE, (int) reliability);
    }

    // if they had a prior style Rating, remove it from the RatingSummary
    if (articleRating.getBody().getStyleValue() > 0) {
      articleRatingSummary.getBody().
        removeRating(Rating.STYLE_TYPE, articleRating.getBody().getStyleValue());
    }

    // update style Article Rating, don't care if new, update or 0
    articleRating.getBody().setStyleValue((int) style);

    // if user rated style, add to to Article RatingSummary
    if (style > 0) {
      articleRatingSummary.getBody().addRating(Rating.STYLE_TYPE, (int) style);
    }

    // if they had a prior single Rating, remove it from the RatingSummary
    if (articleRating.getBody().getSingleRatingValue() > 0) {
      articleRatingSummary.getBody().
        removeRating(Rating.SINGLE_RATING_TYPE, articleRating.getBody().getSingleRatingValue());
    }

    // update single Article Rating, don't care if new, update or 0
    articleRating.getBody().setSingleRatingValue((int) singleRating);

    // if user rated single, add to to Article RatingSummary
    if (singleRating > 0) {
      articleRatingSummary.getBody().addRating(Rating.SINGLE_RATING_TYPE, (int) singleRating);
    }

    // Rating comment
    articleRating.getBody().setCommentTitle(commentTitle);
    articleRating.getBody().setCommentValue(comment);

    //If the user decides that they no longer have competing interests
    //Let's delete the text.
    if(Boolean.parseBoolean(isCompetingInterest)) {
      articleRating.getBody().setCIStatement(ciStatement);
    } else {
      articleRating.getBody().setCIStatement("");
    }

    // if this is a new Rating, the summary needs to know
    if (newRating) {
      articleRatingSummary.getBody().
        setNumUsersThatRated(articleRatingSummary.getBody().getNumUsersThatRated() + 1);
    }

    return SUCCESS;
  }

  /**
   * Get the ratings and comment for the logged in user
   *
   * @return WebWork action status
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public String retrieveRatingsForUser() {
    final AmbraUser user = getCurrentUser();

    if (user == null) {
      log.info("User is null for retrieving user ratings");
      addActionError("Must be logged in");

      return ERROR;
    }

    List<Rating> ratingsList = ratingsService.getRatingsList(articleURI, user.getUserId());

    if (ratingsList.size() < 1) {
      log.debug("didn't find any matching ratings for user: " + user.getUserId());
      addActionError("No ratings for user");
      return ERROR;
    }

    Rating rating = ratingsList.get(0);

    setInsight(rating.getBody().getInsightValue());
    setReliability(rating.getBody().getReliabilityValue());
    setStyle(rating.getBody().getStyleValue());
    setSingleRating(rating.getBody().getSingleRatingValue());

    setCommentTitle(rating.getBody().getCommentTitle());
    setComment(rating.getBody().getCommentValue());
    setCiStatement(rating.getBody().getCIStatement());
    setRateDate(rating.getCreated());

    return SUCCESS;
  }

  /**
   * Return the competing Interest statement
   * @return the statement
   */
  public String getCiStatement() {
    return ciStatement;
  }

  /**
   * Set the competing interest statement
   * @param ciStatement the statement
   */
  public void setCiStatement(String ciStatement) {
    this.ciStatement = ciStatement;
  }  

  /**
   * Gets the URI of the article being rated.
   *
   * @return Returns the articleURI.
   */
  @RequiredStringValidator(message = "Article URI is a required field")
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * Sets the URI of the article being rated.
   *
   * @param articleURI The articleUri to set.
   */
  public void setArticleURI(String articleURI) {
    if (articleURI != null && articleURI.equals(this.articleURI)) {
      return;
    }
    this.articleURI = articleURI;
  }

  /**
   * @return the isResearchArticle
   */
  public boolean getIsResearchArticle() {
    return isResearchArticle;
  }

  /**
   * Gets the style rating.
   *
   * @return Returns the style.
   */
  public double getStyle() {
    return style;
  }

  /**
   * Sets the style rating.
   *
   * @param style The elegance to set.
   */
  public void setStyle(double style) {
    this.style = style;
  }

  /**
   * Gets the insight rating.
   *
   * @return Returns the insight.
   */
  public double getInsight() {
    return insight;
  }

  /**
   * Sets the insight rating.
   *
   * @param insight The insight to set.
   */
  public void setInsight(double insight) {
    if (log.isDebugEnabled())
      log.debug("setting insight to: " + insight);

    this.insight = insight;
  }

  /**
   * @param isCompetingInterest does this annotation have competing interests?
   * */
  public void setIsCompetingInterest(final String isCompetingInterest) {
    this.isCompetingInterest = isCompetingInterest;
  }

  /**
   * Gets the reliability rating.
   *
   * @return Returns the security.
   */
  public double getReliability() {
    return reliability;
  }

  /**
   * Sets the reliability rating.
   *
   * @param reliability The security to set.
   */
  public void setReliability(double reliability) {
    this.reliability = reliability;
  }

  /**
   * Gets the overall rating.
   *
   * @return Returns the overall.
   */
  public double getOverall() {
    return RatingContent.calculateOverall(getInsight(), getReliability(), getStyle());
  }

  /**
   * @return the singleRating
   */
  public double getSingleRating() {
    return singleRating;
  }

  /**
   * @param singleRating the singleRating to set
   */
  public void setSingleRating(double singleRating) {
    this.singleRating = singleRating;
  }

  /**
   * Gets the rating comment.
   *
   * @return Returns the comment.
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the ratings comment.
   *
   * @param comment The comment to set.
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Gets the rating comment title.
   *
   * @return Returns the commentTitle.
   */
  public String getCommentTitle() {
    return commentTitle;
  }

  /**
   * Sets the rating comment title.
   *
   * @param commentTitle The commentTitle to set.
   */
  public void setCommentTitle(String commentTitle) {
    this.commentTitle = commentTitle;
  }

  /**
   * Get the date the article was rated
   * @return the date the article was rated
   */
  public Date getRateDate() {
    return this.rateDate;
  }

  /**
   * Get the date the article was rated in milliseconds
   * @return the date the article was rated in milliseconds
   */
  public Long getRateDateMillis() {
    if(this.rateDate != null) {
      return this.rateDate.getTime();
    } else {
      return null;
    }
  }

  /**
   * Sets the date of the article Rate
   * @param rateDate the date the article is rated
   */
  public void setRateDate(Date rateDate) {
    this.rateDate = rateDate;
  }

  /**
   * Set the profanityCheckingService.
   *
   * @param profanityCheckingService profanityCheckingService
   */
  public void setProfanityCheckingService(final ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  @Required
  public void setPermissionsService(PermissionsService ps) {
    this.permissionsService = ps;
  }

  /**
   * Returns Milliseconds representation of the CIS start date
   * @return Milliseconds representation of the CIS start date 
   * @throws Exception on bad config data or config entry not found.
   */
  public long getCisStartDateMillis() throws Exception {
    try {
      return DateFormat.getDateInstance(DateFormat.SHORT).parse(this.configuration.getString("ambra.platform.cisStartDate")).getTime();
    } catch (ParseException ex) {
      throw (Exception) new Exception("Could not find or parse the cisStartDate node in the ambra platform configuration.  Make sure the ambra/platform/cisStartDate node exists.").initCause(ex);
    }
  }
}
