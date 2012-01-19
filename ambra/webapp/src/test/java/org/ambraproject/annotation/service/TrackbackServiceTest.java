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

package org.ambraproject.annotation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.models.Trackback;
import org.topazproject.ambra.models.TrackbackContent;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick Date: 5/5/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class TrackbackServiceTest extends BaseTest {

  @Autowired
  protected TrackbackService trackbackService;

  @DataProvider(name = "dummyTrackbacks")
  public Object[][] dummyTrackbacks() throws MalformedURLException {
    Trackback trackback = new Trackback();
    trackback.setId(URI.create("id://trackback-id"));
    trackback.setAnnotates(URI.create("id://trackback-annotates"));
    trackback.setBody(new TrackbackContent(
        "body title",
        "An excerpt for a trackback",
        "my cool blog",
        new URL("http://someurl.com")
    ));

    return new Object[][]{
        {trackback}
    };
  }

  /**
   * Test for trackbackService.saveTrackback()
   *
   * @param trackback - A dummy trackback that hasn't been saved to the database
   * @throws Exception - from trackbackService.saveTrackBack()
   */
  @Test(dataProvider = "dummyTrackbacks")
  public void testSaveTrackback(Trackback trackback) throws Exception {
    boolean saved = trackbackService.saveTrackBack(
        trackback.getBody().getTitle(), //title
        trackback.getBody().getBlog_name(), //blog name
        trackback.getBody().getExcerpt(), //excerpt
        trackback.getBody().getUrl(),  //url
        trackback.getAnnotates(),
        trackback.getBody().getUrl().toString(),
        trackback.getId().toString()
    );

    assertTrue(saved, "TrackbackService should've returned true when saving new trackback");
    saved = trackbackService.saveTrackBack(
        trackback.getBody().getTitle(), //title
        trackback.getBody().getBlog_name(), //blog name
        trackback.getBody().getExcerpt(), //excerpt
        trackback.getBody().getUrl(),  //url
        trackback.getAnnotates(),
        trackback.getBody().getUrl().toString(),
        trackback.getId().toString()
    );

    assertFalse(saved, "TrackbackService should've returned false when saving an already existing trackback");
  }

  @DataProvider(name = "saved trackbacks")
  public Object[][] savedTrackbacks() {
    URI annotates = URI.create("id://test-string-for-trackbacks");
    Trackback trackback = new Trackback();
    trackback.setAnnotates(annotates);
    trackback.setBlog_name("some fake blog");
    trackback.setAnonymousCreator("anonymous creator");

    Trackback trackback2 = new Trackback();
    trackback2.setAnnotates(annotates);
    trackback2.setBlog_name("tech crunch");

    return new Object[][]{
        {annotates.toString(), new String[]{dummyDataStore.store(trackback), dummyDataStore.store(trackback2)}}
    };
  }

  /**
   * Test for trackbackService.getTrackbacks()
   *
   * @param annotates - the annotates property to use in looking up trackbacks
   */
  @Test(dataProvider = "saved trackbacks")
  public void testGetTrackbacks(String annotates, String[] expectedIds) {
    List<Trackback> trackbacks = trackbackService.getTrackbacks(annotates, false);
    assertNotNull(trackbacks, "returned null list of trackbacks");
    assertEquals(trackbacks.size(), expectedIds.length, "Didn't return correct number of trackbacks");
    List<String> actualIds = new ArrayList<String>(trackbacks.size());
    for (Trackback trackback : trackbacks) {
      actualIds.add(trackback.getId().toString());
    }

    assertEqualsNoOrder(actualIds.toArray(new String[actualIds.size()]), expectedIds, "Didn't return correct trackbacks");
  }

}
