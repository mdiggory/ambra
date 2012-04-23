/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.trackback;

import org.ambraproject.BaseTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.Trackback;
import org.ambraproject.views.TrackbackView;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Alex Kudlick Date: 5/5/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class TrackbackServiceTest extends BaseTest {

  @Autowired
  protected TrackbackService trackbackService;

  @Test
  public void testSaveTrackback() throws Exception {
    Article article = new Article("id://trackback-annotates");
    dummyDataStore.store(article);
    String url = "http://someblog.net/foo";
    String title = "A cool blog title";
    String blogName = "My Cool Blog";
    String excerpt = "Tyler tries to embarrass Stefan by throwing a football at him during practice. " +
        "However, Stefan's super-skills of catching a football impresses Mr. Tanner who tries to persuade " +
        "Stefan to join the school's football team. Meanwhile, Elena continues to ignore the semi-psychic " +
        "Bonnie's warnings that Stefan is not who he claims to be. So, Elena invites Stefan and Bonnie over " +
        "for dinner at her house. She hopes that they will get know each other better, but her plan ends up " +
        "backfiring.";

    Long trackbackId = trackbackService.createTrackback(
        article.getDoi(),
        url,
        title,
        blogName,
        excerpt);
    assertNotNull(trackbackId, "Returned null trackback id");

    Trackback storedTrackback = dummyDataStore.get(Trackback.class, trackbackId);
    assertNotNull(storedTrackback, "trackback didn't get stored to the database");
    assertEquals(storedTrackback.getArticleID(), article.getID(), "Trackback had incorrect article id");
    assertEquals(storedTrackback.getUrl(), url, "Trackback had incorrect url");
    assertEquals(storedTrackback.getTitle(), title, "Trackback had incorrect title");
    assertEquals(storedTrackback.getBlogName(), blogName, "Trackback had incorrect blog name");
    assertEquals(storedTrackback.getExcerpt(), excerpt, "Trackback had incorrect excerpt");

    int existingNumberOfTrackbacks = dummyDataStore.getAll(Trackback.class).size();

    try {
      trackbackService.createTrackback(
          article.getDoi(),
          url,
          title,
          blogName,
          excerpt);
      fail("Trackback service failed to throw exception when creating duplicate trackback");
    } catch (DuplicateTrackbackException e) {
      //expected
    }
    assertEquals(dummyDataStore.getAll(Trackback.class).size(), existingNumberOfTrackbacks,
        "Trackback service created a new trackback on duplicate save");
  }


  @DataProvider(name = "articleTrackbacks")
  public Object[][] getArticleTrackbacks() {
    Article article1 = new Article("id:doi-for-trackbackServiceTest1");
    article1.setTitle("Test Article1 for TrackbackServiceTest");
    dummyDataStore.store(article1);

    //make different created dates so we can check ordering
    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    Calendar lastMonth = Calendar.getInstance();
    lastMonth.add(Calendar.MONTH, -1);

    List<TrackbackView> article1Trackbacks = new ArrayList<TrackbackView>(2);

    Trackback trackback1 = new Trackback(article1.getID(), "http://someblog.net/foo");
    trackback1.setTitle("Unit Testing in Java With Mock Http Servers");
    trackback1.setExcerpt("Sometimes you want to write a unit test for beans that make http requests.");
    trackback1.setBlogName("My Cool Blog");
    trackback1.setCreated(Calendar.getInstance().getTime());
    dummyDataStore.store(trackback1);
    article1Trackbacks.add(new TrackbackView(trackback1, article1.getDoi(), article1.getTitle()));

    Trackback trackback2 = new Trackback(article1.getID(), "http://someblog.net/bar");
    trackback2.setTitle("How to Productively Refactor Code");
    trackback2.setExcerpt("Sometimes you want to refactor code.");
    trackback2.setBlogName("My Cool Blog");
    trackback2.setCreated(lastMonth.getTime());
    dummyDataStore.store(trackback2);
    article1Trackbacks.add(new TrackbackView(trackback2, article1.getDoi(), article1.getTitle()));

    Trackback trackback3 = new Trackback(article1.getID(), "http://someblog.net/cookie");
    trackback3.setTitle("Code that Never Works");
    trackback3.setExcerpt("Sometimes you want code that works.");
    trackback3.setBlogName("My Cool Blog");
    trackback3.setCreated(lastYear.getTime());
    dummyDataStore.store(trackback3);
    article1Trackbacks.add(new TrackbackView(trackback3, article1.getDoi(), article1.getTitle()));

    Article article2 = new Article("id:doi-with-no-trackbacks");
    article2.setTitle("Test Article2 for TrackbackServiceTest");
    dummyDataStore.store(article2);


    return new Object[][]{
        {article1, article1Trackbacks},
        {article2, new ArrayList<TrackbackView>(0)}
    };
  }

  @Test(dataProvider = "articleTrackbacks")
  public void testGetTrackbacksForArticle(Article article, List<TrackbackView> expectedTrackbacks) {
    List<TrackbackView> trackbacks = trackbackService.getTrackbacksForArticle(article.getDoi());
    assertNotNull(trackbacks, "Returned null list of trackbacks");
    assertEqualsNoOrder(trackbacks.toArray(), expectedTrackbacks.toArray(), "Returned incorrect trackbacks");
    //check the ordering
    for (int i = 0; i < trackbacks.size() - 1; i++) {
      assertTrue(trackbacks.get(i).getCreated().after(trackbacks.get(i + 1).getCreated()),
          "Trackbacks were out of order;\nelement " + i + " had created date " + trackbacks.get(i).getCreated()
              + "\nelement " + (i + 1) + " had created date " + trackbacks.get(i + 1).getCreated());
    }
  }

  @Test(dataProvider = "articleTrackbacks")
  public void testCountTrackbacks(Article article, List<TrackbackView> expectedTrackbacks) {
    assertEquals(trackbackService.countTrackbacksForArticle(article.getDoi()), expectedTrackbacks.size(),
        "Trackback service returned incorrect count of trackbacks");
  }

}
