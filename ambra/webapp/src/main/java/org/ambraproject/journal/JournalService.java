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

package org.ambraproject.journal;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Required;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.service.HibernateService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * A collection of methods for manipulating and querying journal information
 *
 * @author Joe Osowski
 */
public interface JournalService extends HibernateService {

  /**
   * Get the specified journal.
   *
   * @param jName  the journal's name
   * @return the journal, or null if not found
   */
  public Journal getJournal(String jName);

  /**
   * Get the Journal from its <strong>eIssn</strong>.
   *
   * @param eIssn  the journal's eIssn value.
   * @return the journal, or null if not found
   */
  public Journal getJournalByEissn(String eIssn);

  /**
   * Get the set of all the known journals.
   *
   * @return all the journals, or the empty set if there are none
   */
  public Set<Journal> getAllJournals();

  /**
   * Get the names of all the known journals.
   *
   * @return the list of names; may be empty if there are no known journals
   */
  public Set<String> getAllJournalNames();

  /**
   * This method makes services dependent on servlet context.
   * Use getCurrentJournal() method in Action class instead.
   * .
   * Get the name of the current journal.
   *
   * @return the name of the current journal, or null if there is no current journal
   */
  @Deprecated
  public String getCurrentJournalName();

  /**
   * Get the list of journals which carry the given object (e.g. article).
   *
   * @param doi the doi of the object
   * @return the list of journals which carry this object; will be empty if this object
   *         doesn't belong to any journal
   */
  public Set<Journal> getJournalsForObject(String doi);

/**
   * Get the list of journal Name which carry the given object (e.g. article).
   *
   * @param oid the info:&lt;oid&gt; uri of the object
   * @return the list of journal Name which carry this object;
   */
  public Set<String> getJournalNameForObject(URI oid);


  /**
   * Set the ambra configuration
   * @param configuration
   */
  @Required
  public void setConfiguration(Configuration configuration);
}
