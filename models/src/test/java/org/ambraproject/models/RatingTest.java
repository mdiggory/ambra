/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 3/7/12
 */
public class RatingTest extends BaseHibernateTest {

  @Test
  public void testSaveRating() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    UserProfile creator = new UserProfile(
        "authIDForSavingRating",
        "email@saveRating.org",
        "displayNameForSavingRating");
    hibernateTemplate.save(creator);

    Rating rating = new Rating(creator, 1232132l);
    rating.setInsight(5);
    rating.setReliability(3);
    rating.setBody("Feeling their pain from having lived through the same experience, " +
        "Emma desperately tries to help two homeless children find their birth father before " +
        "they're separated and put into the foster care system. Meanwhile, back in the fairytale " +
        "world that was, the Evil Queen coerces Hansel and Gretel into stealing an important artifact " +
        "from a blind witch.");

    Serializable id = hibernateTemplate.save(rating);
    Rating storedRating = (Rating) hibernateTemplate.get(Rating.class, id);
    assertNotNull(storedRating, "didn't store rating");
    assertNotNull(storedRating.getCreator(), "didn't associate rating to creator");
    assertEquals(storedRating.getCreator().getID(), creator.getID(), "Associated rating to incorrect user");
    assertEquals(storedRating.getArticleID(), rating.getArticleID(), "Didn't store correct article id");
    assertEquals(storedRating.getInsight(), rating.getInsight(), "Didn't store correct insight value");
    assertEquals(storedRating.getReliability(), rating.getReliability(), "Didn't store correct reliability value");
    assertEquals(storedRating.getBody(), rating.getBody(), "Didn't store correct comment");
    assertNotNull(storedRating.getCreated(), "rating didn't get created date set");
    assertTrue(storedRating.getCreated().getTime() >= testStart, "created date wasn't after test start");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullCreator() {
    hibernateTemplate.save(new Rating(null, 123123l));
  }
  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullArticle() {
    UserProfile creator = new UserProfile(
        "authIDForSavingRatingWithNoArticle",
        "email@saveRatingWithNoArticle.org",
        "displayNameForSavingRatingWithNoArticle");
    hibernateTemplate.save(creator);
    hibernateTemplate.save(new Rating(creator, null));
  }

  @Test
  public void testDoesNotCascadeDelete() {
    UserProfile creator = new UserProfile(
        "authIdForCascadeDeleteOnRating",
        "email@CascadeDeleteOnRating.org",
        "displayNameForCascadeDeleteOnRating"
    );
    Serializable creatorId = hibernateTemplate.save(creator);

    Rating rating = new Rating(creator, 1231423l);
    hibernateTemplate.save(rating);
    hibernateTemplate.delete(rating);

    assertNotNull(hibernateTemplate.get(UserProfile.class, creatorId), "Flag deleted creator");
  }

  @Test
  public void testUpdate() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    UserProfile creator = new UserProfile(
        "authIDForUpdateRating",
        "email@updateRating.org",
        "displayNameForUpdateRating");
    hibernateTemplate.save(creator);

    Rating rating = new Rating(creator, 1232132l);
    rating.setInsight(1);
    rating.setReliability(1);
    Serializable id = hibernateTemplate.save(rating);

    rating.setInsight(5);
    rating.setReliability(2);

    hibernateTemplate.update(rating);

    Rating storedRating = (Rating) hibernateTemplate.get(Rating.class, id);
    assertNotNull(storedRating, "Rating wasn't stored to the db");
    assertEquals(storedRating.getInsight(), Integer.valueOf(5), "rating didn't get insight value updated");
    assertEquals(storedRating.getReliability(), Integer.valueOf(2), "rating didn't get reliability value updated");
    assertNotNull(storedRating.getLastModified(), "Rating didn't have a last modified date");
    assertTrue(storedRating.getLastModified().getTime() >= testStart, "last modified wasn't after test start");
  }

}
