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
public class TrackbackTest extends BaseHibernateTest {

  @Test
  public void testSaveTrackback() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    Trackback trackback = new Trackback(123142l, "http://www.imdb.com/title/tt1843230/episodes?year=2012");
    trackback.setExcerpt("Regina and Mr. Gold play dirty politics and take opposite sides when Emma runs...");
    trackback.setBlogName("My Cool Blog");
    Serializable id = hibernateTemplate.save(trackback);

    Trackback storedTrackback = (Trackback) hibernateTemplate.get(Trackback.class, id);
    assertNotNull(storedTrackback, "didn't store trackback");
    assertEquals(storedTrackback.getArticleID(), trackback.getArticleID(), "Stored incorrect article id");
    assertEquals(storedTrackback.getUrl(), trackback.getUrl(), "stored incorrect url");
    assertEquals(storedTrackback.getExcerpt(), trackback.getExcerpt(), "stored incorrect excerpt");

    assertNotNull(storedTrackback.getCreated(), "trackback didn't get created date set");
    assertTrue(storedTrackback.getCreated().getTime() >= testStart, "created date wasn't after test start");
  }

  @Test
  public void testUpdate() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    Trackback trackback = new Trackback(12314l, "http://www.imdb.com/title/tt1843230/");
    trackback.setTitle("title");
    trackback.setBlogName("blog name");
    trackback.setExcerpt("excerpt");

    Serializable id = hibernateTemplate.save(trackback);
    String newUrl = "http://www.someblog.net";
    trackback.setUrl(newUrl);
    hibernateTemplate.update(trackback);

    Trackback storedTrackback = (Trackback) hibernateTemplate.get(Trackback.class, id);
    assertEquals(storedTrackback.getUrl(), newUrl, "trackback didn't get url updated");
    assertNotNull(storedTrackback.getLastModified(), "trackback didn't get last modified set");
    assertTrue(storedTrackback.getLastModified().getTime() >= testStart, "last modified date wasn't after test start");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullArticleID() {
    Trackback trackback = new Trackback(null, "url");
    trackback.setTitle("title");
    trackback.setBlogName("blog name");
    trackback.setExcerpt("excerpt");
    hibernateTemplate.save(trackback);
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueArticleUrlKey() {
    Trackback trackback = new Trackback(1111111l, "http://www.imdb.com/title/tt1405406/");
    trackback.setTitle("title");
    trackback.setBlogName("blog name");
    trackback.setExcerpt("excerpt");

    try {
      hibernateTemplate.save(trackback);
    } catch (DataAccessException e) {
      fail("Exception while saving first trackback");
    }
    hibernateTemplate.save(trackback);
  }
}
