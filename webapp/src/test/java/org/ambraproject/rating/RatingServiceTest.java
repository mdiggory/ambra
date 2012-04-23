/* $HeadURL http://ambraproject.org/svn/ambra/branches/ANH-Conversion/ambra/webapp/src/test/java/org/topazproject/ambra/rating/RatingServiceImplTest.java $
 * $Id AnnotationServiceTest.java 9530 2011-09-09 23:22:00Z msingh $
 * Copyright (c) 2006-2011 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.rating;

import org.ambraproject.ApplicationException;
import org.ambraproject.BaseTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.UserProfile;
import org.ambraproject.rating.service.RatingsService;
import org.ambraproject.views.RatingSummaryView;
import org.ambraproject.views.RatingView;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.models.Rating;
import org.ambraproject.models.RatingSummary;
import java.net.MalformedURLException;
import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * Test for methods of {@link RatingsService}.
 */

public class RatingServiceTest extends BaseTest {

  @Autowired
  protected RatingsService ratingsService;

  @DataProvider(name = "ratingToDelete")
  public Object[][] ratingToDelete() throws MalformedURLException {
    Article article = new Article();
    article.setDoi("id://article-id-delete");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-delete");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setAnnotationUri("info:doi/rating-1-delete");
    rating.setArticleID(article.getID());
    rating.setSingleRating(1);
    rating.setInsight(1);
    rating.setReliability(1);
    rating.setStyle(1);
    rating.setBody("body");
    rating.setTitle("title");
    rating.setCreator(userProfile);
    dummyDataStore.store(rating);

    RatingSummary ratingSummary = new RatingSummary();
    ratingSummary.setArticleID(article.getID());
    ratingSummary.addRating(rating);
    dummyDataStore.store(ratingSummary);

    return new Object[][]{
      { rating.getID(), article.getID() }
    };
  }

  /**
   * Test for ratingService.deleteRating()
   *
   * @param ratingID rating which has to be deleted
   * @throws ApplicationException
   */

  @Test(dataProvider = "ratingToDelete")
  public void testDeleteRatings(Long ratingID, Long articleID) throws ApplicationException {
    RatingSummary ratingSummary = ratingsService.getRatingSummary(articleID);

    assertEquals(ratingSummary.getInsightNumRatings(), Integer.valueOf(1));
    assertEquals(ratingSummary.getInsightTotal(), Integer.valueOf(1));
    assertEquals(ratingSummary.getReliabilityNumRatings(), Integer.valueOf(1));
    assertEquals(ratingSummary.getReliabilityTotal(), Integer.valueOf(1));
    assertEquals(ratingSummary.getSingleRatingNumRatings(), Integer.valueOf(1));
    assertEquals(ratingSummary.getSingleRatingTotal(), Integer.valueOf(1));
    assertEquals(ratingSummary.getStyleNumRatings(), Integer.valueOf(1));
    assertEquals(ratingSummary.getStyleTotal(), Integer.valueOf(1));

    ratingsService.deleteRating(ratingID, DEFAULT_ADMIN_AUTHID);

    Rating rating = ratingsService.getRating(ratingID);
    assertNull(rating);

    ratingSummary = ratingsService.getRatingSummary(articleID);

    assertEquals(ratingSummary.getInsightNumRatings(), Integer.valueOf(0));
    assertEquals(ratingSummary.getInsightTotal(), Integer.valueOf(0));
    assertEquals(ratingSummary.getReliabilityNumRatings(), Integer.valueOf(0));
    assertEquals(ratingSummary.getReliabilityTotal(), Integer.valueOf(0));
    assertEquals(ratingSummary.getSingleRatingNumRatings(), Integer.valueOf(0));
    assertEquals(ratingSummary.getSingleRatingTotal(), Integer.valueOf(0));
    assertEquals(ratingSummary.getStyleNumRatings(), Integer.valueOf(0));
    assertEquals(ratingSummary.getStyleTotal(), Integer.valueOf(0));
  }

  @DataProvider(name = "ratingList")
  public Object[][] ratingList() throws MalformedURLException {
    Article article = new Article();
    article.setDoi("id://article-id-rating-list");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);
    
    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-rating-list");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    //Use the ratings service to save, as it also populates the ratingSummary table
    ratingsService.saveRating(rating);

    userProfile = new UserProfile();
    userProfile.setRealName("user-two-rating-list");
    dummyDataStore.store(userProfile);

    Rating rating1 = new Rating();
    rating1.setArticleID(article.getID());
    rating1.setCreator(userProfile);
    //Use the ratings service to save, as it also populates the ratingSummary table
    ratingsService.saveRating(rating1);

    return new Object[][]{
      { article.getID(), rating.getCreator() },
      { article.getID(), rating1.getCreator() }
    };
  }

  /**
   * Test for ratingService.getRatingsList()
   * @param articleID article for which ratings need to be fetched
   * @param user that created the rating
   */
  @Test(dataProvider = "ratingList")
  public void testGetRatingsList(Long articleID, UserProfile user) {
    boolean hasRated = ratingsService.hasRated(articleID, user);
    assertTrue(hasRated, "should return true");

    hasRated = ratingsService.hasRated(0L, user);
    assertFalse(hasRated, "should return false");

    Rating rating  = ratingsService.getRating(articleID, user);
    assertNotNull(rating, "returned null list of annotation");

    List<RatingView> ratings = ratingsService.getRatingViewList(articleID);
    assertNotNull(ratings, "null list of rating ");
    assertEquals(ratings.size(), 2, "should return 2 objects");
  }


  @DataProvider(name = "ratingListSummary")
  public Object[][] ratingListSummary() throws MalformedURLException {
    Article article = new Article();
    article.setDoi("id://article-id-1-list-summary");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-list-summary");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setInsight(2);
    dummyDataStore.store(rating);

    rating = new Rating();
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setInsight(3);
    dummyDataStore.store(rating);

    RatingSummary ratingSummary = new RatingSummary();
    ratingSummary.setInsightNumRatings(2);
    ratingSummary.setInsightTotal(5);
    ratingSummary.setArticleID(article.getID());
    dummyDataStore.store(ratingSummary);

    return new Object[][]{
      { article.getID() }
    };
  }

  /**
   * Test for ratingService.getRatingSummaryList()
   * @param articleID article for which ratings need to be fetched
   */

  @Test(dataProvider = "ratingListSummary")
  public void testGetRatingSummaryList(Long articleID){
    RatingSummary ratingSummary = ratingsService.getRatingSummary(articleID);

    assertNotNull(ratingSummary, "rating summary is null");
  }

  @DataProvider(name = "rating")
  public Object[][] rating() throws MalformedURLException {
    Article article = new Article();
    article.setDoi("id://article-id-rating");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setAnnotationUri("id://rating-id-rating-1");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    dummyDataStore.store(rating);

    return new Object[][]{
      { rating.getID(), rating.getAnnotationUri() },
    };
  }

  /**
   * Test for ratingService.getRating()
   * @param ratingID rating need to be fetched
   * @param annotationURI the annotation URI of the rating
   */

  @Test(dataProvider = "rating")
  public void testGetRating(Long ratingID, String annotationURI){
    Rating rating = ratingsService.getRating(ratingID);
    assertNotNull(rating, "null rating ");

    rating = ratingsService.getRating(annotationURI);
    assertNotNull(rating, "null rating ");
  }

  @DataProvider(name = "articleList")
  public Object[][] articleList() throws MalformedURLException {

    Article article = new Article();
    article.setDoi("id://article-id-article-list-2");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    Article article2 = new Article();
    article2.setDoi("id://article-id2-article-list-2");
    article2.seteIssn("testeIssn2");
    dummyDataStore.store(article2);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-1-article-list-2");
    dummyDataStore.store(userProfile);

    UserProfile userProfile1 = new UserProfile();
    userProfile1.setRealName("user-2-article-list-2");
    dummyDataStore.store(userProfile1);

    return new Object[][]{
      { article.getID(), userProfile, userProfile1 },
      { article2.getID(), userProfile, userProfile1 }
    };
  }

  @Test(dataProvider = "articleList")
  public void testSaveRating(Long articleID, UserProfile userProfile, UserProfile userProfile1)
    throws ApplicationException {
    Rating rating = new Rating();

    rating.setArticleID(articleID);
    rating.setCreator(userProfile);
    rating.setInsight(1);
    rating.setReliability(2);
    rating.setStyle(3);
    rating.setSingleRating(3);
    
    ratingsService.saveRating(rating);

    Long firstRatingID = rating.getID();
    
    //Make sure summary data was updated.
    RatingSummary rs = ratingsService.getRatingSummary(articleID);
    
    assertEquals(rs.getSingleRatingTotal(), Integer.valueOf(3));
    assertEquals(rs.getSingleRatingNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getInsightTotal(), Integer.valueOf(1));
    assertEquals(rs.getInsightNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getReliabilityTotal(), Integer.valueOf(2));
    assertEquals(rs.getReliabilityNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getStyleTotal(), Integer.valueOf(3));
    assertEquals(rs.getStyleNumRatings(), Integer.valueOf(1));

    rating = new Rating();

    rating.setArticleID(articleID);
    rating.setCreator(userProfile1);
    rating.setInsight(2);
    rating.setReliability(4);
    rating.setStyle(5);
    rating.setSingleRating(0);

    ratingsService.saveRating(rating);

    //Make sure summary data was updated (ratings added together)
    rs = ratingsService.getRatingSummary(articleID);
    assertEquals(rs.getSingleRatingTotal(), Integer.valueOf(3));
    assertEquals(rs.getSingleRatingNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getInsightTotal(), Integer.valueOf(3));
    assertEquals(rs.getInsightNumRatings(), Integer.valueOf(2));
    assertEquals(rs.getReliabilityTotal(), Integer.valueOf(6));
    assertEquals(rs.getReliabilityNumRatings(), Integer.valueOf(2));
    assertEquals(rs.getStyleTotal(), Integer.valueOf(8));
    assertEquals(rs.getStyleNumRatings(), Integer.valueOf(2));

    ratingsService.deleteRating(firstRatingID, DEFAULT_ADMIN_AUTHID);

    //Make sure summary data was updated (rating deleted)
    rs = ratingsService.getRatingSummary(articleID);
    assertEquals(rs.getSingleRatingTotal(), Integer.valueOf(0));
    assertEquals(rs.getSingleRatingNumRatings(), Integer.valueOf(0));
    assertEquals(rs.getInsightTotal(), Integer.valueOf(2));
    assertEquals(rs.getInsightNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getReliabilityTotal(), Integer.valueOf(4));
    assertEquals(rs.getReliabilityNumRatings(), Integer.valueOf(1));
    assertEquals(rs.getStyleTotal(), Integer.valueOf(5));
    assertEquals(rs.getStyleNumRatings(), Integer.valueOf(1));
  }

  @DataProvider(name = "singleRating")
  public Object[][] singleRating() throws MalformedURLException {

    Article article = new Article();
    article.setDoi("id://article-id-single-rating");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-single-rating");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setAnnotationUri("id://rating-id-1-single-rating");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setSingleRating(1);
    rating.setInsight(2);
    rating.setReliability(4);
    rating.setStyle(3);

    //Use the ratings service to save, as it also populates the ratingSummary table
    ratingsService.saveRating(rating);

    return new Object[][]{
      { article.getID() },
    };
  }

  @Test(dataProvider = "singleRating")
  void testGetAverageSingleRating(Long articleID)
  {
    List<RatingView> ratings = ratingsService.getRatingViewList(articleID);

    assertEquals(ratings.size(), 1);

    assertEquals(ratings.get(0).getSingleRating(), 1);
    assertEquals(ratings.get(0).getInsight(), 2);
    assertEquals(ratings.get(0).getReliability(), 4);
    assertEquals(ratings.get(0).getStyle(), 3);
    assertEquals(ratings.get(0).getSingleRating(), 1);
    //Rounded method is weighted and then rounded to the nearest .5
    assertEquals(ratings.get(0).getOverallRounded(), 3.0);

    RatingSummaryView ratingSummaryView = ratingsService.getAverageRatings(articleID);

    assertEquals(ratingSummaryView.getInsight().getTotal(), 2);
    assertEquals(ratingSummaryView.getInsight().getAverage(), 2.0);
    assertEquals(ratingSummaryView.getInsight().getCount(), 1);
    assertEquals(ratingSummaryView.getInsight().getRounded(), 2.0);

    assertEquals(ratingSummaryView.getReliability().getTotal(), 4);
    assertEquals(ratingSummaryView.getReliability().getAverage(), 4.0);
    assertEquals(ratingSummaryView.getReliability().getCount(), 1);
    assertEquals(ratingSummaryView.getReliability().getRounded(), 4.0);

    assertEquals(ratingSummaryView.getStyle().getTotal(), 3);
    assertEquals(ratingSummaryView.getStyle().getAverage(), 3.0);
    assertEquals(ratingSummaryView.getStyle().getCount(), 1);
    assertEquals(ratingSummaryView.getStyle().getRounded(), 3.0);

    assertEquals(ratingSummaryView.getSingle().getTotal(), 1);
    assertEquals(ratingSummaryView.getSingle().getAverage(), 1.0);
    assertEquals(ratingSummaryView.getSingle().getCount(), 1);
    assertEquals(ratingSummaryView.getSingle().getRounded(), 1.0);

    assertEquals(ratingSummaryView.getNumUsersThatRated(), 1);
    assertEquals(ratingSummaryView.getOverall(), 2.933333333333333);
    assertEquals(ratingSummaryView.getRoundedOverall(), 3.0);
  }

  @DataProvider(name = "multipleRatings")
  public Object[][] multilpeRating() throws MalformedURLException {
    Article article = new Article();
    article.setDoi("id://article-id-multi-ratings");
    article.seteIssn("testeIssn");
    dummyDataStore.store(article);

    UserProfile userProfile = new UserProfile();
    userProfile.setRealName("user-multi-ratings");
    dummyDataStore.store(userProfile);

    Rating rating = new Rating();
    rating.setAnnotationUri("id://rating-id-1-multi-ratings");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setSingleRating(1);
    rating.setInsight(2);
    rating.setReliability(4);
    rating.setStyle(3);
    //Use the ratings service to save, as it also populates the ratingSummary table
    ratingsService.saveRating(rating);

    userProfile = new UserProfile();
    userProfile.setRealName("user2-multi-ratings");
    dummyDataStore.store(userProfile);

    rating = new Rating();
    rating.setAnnotationUri("id://rating-id-2-multi-ratings");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setSingleRating(3);
    rating.setInsight(1);
    rating.setReliability(1);
    rating.setStyle(2);
    ratingsService.saveRating(rating);

    userProfile = new UserProfile();
    userProfile.setRealName("user3-multi-ratings");
    dummyDataStore.store(userProfile);

    rating = new Rating();
    rating.setAnnotationUri("id://rating-id-3-multi-ratings");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setSingleRating(3);
    rating.setInsight(4);
    rating.setReliability(3);
    rating.setStyle(1);
    ratingsService.saveRating(rating);

    userProfile = new UserProfile();
    userProfile.setRealName("user4-multi-ratings");
    dummyDataStore.store(userProfile);

    rating = new Rating();
    rating.setAnnotationUri("id://rating-id-4-multi-ratings");
    rating.setArticleID(article.getID());
    rating.setCreator(userProfile);
    rating.setSingleRating(0);
    rating.setInsight(4);
    rating.setReliability(3);
    rating.setStyle(1);
    ratingsService.saveRating(rating);

    return new Object[][]{
      { article.getID() },
    };
  }

  @Test(dataProvider = "multipleRatings")
  void testGetAverageMultiRating(Long articleID)
  {
    RatingSummaryView ratingSummaryView = ratingsService.getAverageRatings(articleID);

    assertEquals(ratingSummaryView.getInsight().getTotal(), 11);
    assertEquals(ratingSummaryView.getInsight().getCount(), 4);
    assertEquals(ratingSummaryView.getInsight().getAverage(), 2.75);
    assertEquals(ratingSummaryView.getInsight().getRounded(), 3.0);

    assertEquals(ratingSummaryView.getReliability().getTotal(), 11);
    assertEquals(ratingSummaryView.getReliability().getCount(), 4);
    assertEquals(ratingSummaryView.getReliability().getAverage(), 2.75);
    assertEquals(ratingSummaryView.getReliability().getRounded(), 3.0);

    assertEquals(ratingSummaryView.getStyle().getTotal(), 7);
    assertEquals(ratingSummaryView.getStyle().getCount(), 4);
    assertEquals(ratingSummaryView.getStyle().getAverage(), 1.75);
    assertEquals(ratingSummaryView.getStyle().getRounded(), 2.0);

    assertEquals(ratingSummaryView.getSingle().getTotal(), 7);
    assertEquals(ratingSummaryView.getSingle().getCount(), 3);
    assertEquals(ratingSummaryView.getSingle().getAverage(), 2.3333333333333335);
    assertEquals(ratingSummaryView.getSingle().getRounded(), 2.5);

    assertEquals(ratingSummaryView.getNumUsersThatRated(), 4);
    assertEquals(ratingSummaryView.getOverall(), 2.4833333333333335);
    assertEquals(ratingSummaryView.getRoundedOverall(), 2.5);
  }
}
