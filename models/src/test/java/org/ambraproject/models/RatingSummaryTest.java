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

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Alex Kudlick 3/7/12
 */
public class RatingSummaryTest extends BaseHibernateTest {

  @Test
  public void testSaveRatingSummary() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    RatingSummary ratingSummary = new RatingSummary(123345l);
    ratingSummary.setInsightNumRatings(12349);
    ratingSummary.setInsightTotal(123245);

    ratingSummary.setReliabilityNumRatings(1234);
    ratingSummary.setReliabilityTotal(459);

    ratingSummary.setStyleNumRatings(123415660);
    ratingSummary.setStyleTotal(8592);

    ratingSummary.setSingleRatingNumRatings(123456434);
    ratingSummary.setSingleRatingTotal(9485);

    Serializable id = hibernateTemplate.save(ratingSummary);

    RatingSummary storedRating = (RatingSummary) hibernateTemplate.get(RatingSummary.class, id);
    assertNotNull(storedRating, "didn't store rating");
    assertEquals(storedRating.getInsightNumRatings(), ratingSummary.getInsightNumRatings(), "Stored incorrect insight num ratings");
    assertEquals(storedRating.getInsightTotal(), ratingSummary.getInsightTotal(), "Stored incorrect insight average");

    assertEquals(storedRating.getReliabilityNumRatings(), ratingSummary.getReliabilityNumRatings(), "Stored incorrect reliability num ratings");
    assertEquals(storedRating.getReliabilityTotal(), ratingSummary.getReliabilityTotal(), "Stored incorrect reliability average");

    assertEquals(storedRating.getStyleNumRatings(), ratingSummary.getStyleNumRatings(), "Stored incorrect style num ratings");
    assertEquals(storedRating.getStyleTotal(), ratingSummary.getStyleTotal(), "Stored incorrect style average");

    assertEquals(storedRating.getSingleRatingNumRatings(), ratingSummary.getSingleRatingNumRatings(), "Stored incorrect overall num ratings");
    assertEquals(storedRating.getSingleRatingTotal(), ratingSummary.getSingleRatingTotal(), "Stored incorrect overall average");

    assertNotNull(storedRating.getCreated(), "rating summary didn't get created date set");
    assertTrue(storedRating.getCreated().getTime() >= testStart, "created date wasn't after test start");
  }

  @Test
  public void testUpdate() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    RatingSummary ratingSummary = new RatingSummary(9921l);
    Serializable id = hibernateTemplate.save(ratingSummary);
    ratingSummary.setInsightTotal(66954);

    hibernateTemplate.update(ratingSummary);

    RatingSummary savedSummary = (RatingSummary) hibernateTemplate.get(RatingSummary.class, id);
    assertEquals(savedSummary.getInsightTotal(), ratingSummary.getInsightTotal(), "didn't update insight average");
    assertNotNull(savedSummary.getLastModified(), "Didn't get last modified date set");
    assertTrue(savedSummary.getLastModified().getTime() >= testStart, "last modified date wasn't after test start");
  }

  @Test
  public void testDoesNotAllowArticleIDToChange() {
    final Long originalArticleID = 111111l;
    RatingSummary ratingSummary = new RatingSummary(originalArticleID);
    Serializable id = hibernateTemplate.save(ratingSummary);
    ratingSummary.setArticleID(1111121l);
    hibernateTemplate.update(ratingSummary);

    assertEquals(((RatingSummary) hibernateTemplate.get(RatingSummary.class, id)).getArticleID(), originalArticleID,
        "update changed article id");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullArticleID() {
    hibernateTemplate.save(new RatingSummary());
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testSaveWithDuplicateArticleID() {
    try {
      hibernateTemplate.save(new RatingSummary(314159l));
    } catch (DataAccessException e) {
      fail("Exception during the first attempt to save", e);
    }
    hibernateTemplate.save(new RatingSummary(314159l));
  }

}
