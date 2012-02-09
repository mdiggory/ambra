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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.testutils;

import java.io.Serializable;
import java.util.List;

/**
 * Convenience bean provided to store objects in a dummy database for testing.  An interface is provided so that tests
 * can be unaware of the underlying test database. Furthermore, the implementation should catch database exception caused by duplicate
 * key errors, so that tests can rely on dataproviders, which get called multiple times, to store data.,
 *
 * @author Alex Kudlick Date: 5/2/11
 *         <p/>
 *         org.topazproject.ambra
 */
public interface DummyDataStore {

  /**
   * Store the object in the test database.
   *
   * @param object - the object to store
   * @return - The id generated for the object
   */
  public String store(Object object);

  /**
   * Store the provided objects in the test database
   *
   * @param objects - A list of the objects to store
   * @param <T>     - the type of objects being stored
   * @return - A List of ids generated for the stored objects, in order.
   */
  public <T> List<String> store(List<T> objects);

  /**
   * Update an object that's already been added to the store
   *
   * @param object - the object to update
   */
  public void update(Object object);

  /**
   * Get the object from the store with the given id
   *
   *
   * @param clazz the class of the object to get
   * @param id    the id of the object to get
   * @return the object with the specified id and class
   */
  public <T> T get(Class<T> clazz, Serializable id);

  /**
   * Return a list of all the stored instances of the specified class
   * @param clazz the class to retrieve
   * @param <T> the type to return
   * @return all the stored instances of the given class
   */
  public <T> List<T> getAll(Class<T> clazz);

  /**
   *  Delete an object from the store
   * @param object the object to delete
   */
  public void delete(Object object);

  /**
   * Delete all objects of the given class
   * @param clazz the class to delete
   */
  public void deleteAll(Class clazz);
}
