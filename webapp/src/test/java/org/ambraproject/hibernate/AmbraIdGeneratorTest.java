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

package org.ambraproject.hibernate;

import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.topazproject.ambra.models.*;

import java.io.Serializable;
import java.net.URI;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick Date: 5/27/11
 *         <p/>
 *         org.ambraproject.hibernate
 */
@ContextConfiguration
public class AmbraIdGeneratorTest extends AbstractTestNGSpringContextTests {

  @Autowired
  protected AmbraIdGenerator idGenerator;
  @Autowired
  protected Session session;

  @DataProvider(name = "unsavedObjects")
  public Object[][] getUnsavedObjects() {
    return new Object[][]{
        {new Comment(), URI.class, idGenerator.getPrefix() + "annotation/" },
        {new MinorCorrection(), URI.class, idGenerator.getPrefix() + "annotation/" },
        {new FormalCorrection(), URI.class, idGenerator.getPrefix() + "annotation/" },
        {new UserPreference(), URI.class, idGenerator.getPrefix() + "preferences/"},
        {new Issue(), URI.class, idGenerator.getPrefix() + "aggregation/"}
    };
  }

  @Test(dataProvider = "unsavedObjects")
  public void testGeneration(Object object, Class expectedIdClass, String expectedPrefix) {
    Serializable id = idGenerator.generate((SessionImplementor) session, object);
    assertNotNull(id, "generated null id");
    assertFalse(id.toString().isEmpty(), "returned empty id");
    assertEquals(id.getClass(), expectedIdClass, "returned id of incorrect class");
    assertTrue(id.toString().startsWith(expectedPrefix), "Generated id didn't start with correct prefix; expected: "
        + expectedPrefix + " but found " + id);
  }

  @DataProvider(name = "objectsWithIdSet")
  public Object[][] getObjectsWithExpectedId() {
    Comment comment = new Comment();
    comment.setId(URI.create("id://comment-id"));

    Issue issue = new Issue();
    issue.setId(URI.create("id://issue-id"));

    return new Object[][]{
        {comment, comment.getId()},
        {issue, issue.getId()},
    };
  }

  @Test(dataProvider = "objectsWithIdSet")
  public void testGenerationWithIdAlreadySet(Object object, Serializable expectedId) {
    Serializable id = idGenerator.generate((SessionImplementor) session, object);
    assertNotNull(id, "generated null id");
    assertFalse(id.toString().isEmpty(), "returned empty id");
    assertEquals(id, expectedId, "returned incorrect id");
  }

  @DataProvider(name = "savedObjects")
  public Object[][] getSavedObjects() {
    session.beginTransaction();
    Comment comment = new Comment();
    comment.setId((URI) session.save(comment));

    Issue issue = new Issue();
    issue.setId((URI) session.save(issue));

    session.getTransaction().commit();
    return new Object[][]{
        {comment, comment.getId()},
        {issue, issue.getId()}
    };
  }

  @Test(dataProvider = "savedObjects")
  public void testGenerationOnSavedObjects(Object object, Serializable expectedId) {
    Serializable id = idGenerator.generate((SessionImplementor) session, object);
    assertNotNull(id, "generated null id");
    assertFalse(id.toString().isEmpty(), "returned empty id");
    assertEquals(id, expectedId, "returned incorrect id");
  }
}
