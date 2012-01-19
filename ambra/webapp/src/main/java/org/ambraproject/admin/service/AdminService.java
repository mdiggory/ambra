/* $HeadURL$
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

package org.ambraproject.admin.service;

import org.ambraproject.journal.JournalService;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Volume;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * AdminService encapsulates the basic services needed by all administrative
 * actions.
 */
public interface AdminService {


  /**************************************************
   * Journal Management Methods                     *
   **************************************************/


  /**
   * Give a SEPARATOR delimitted string of volume URIs convert them
   * to a list of separated URIs.
   *
   * @param csvStr the list of string of URIs.
   * @return a list of URI created from the string csvStr .
   * @throws URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public List<URI> parseCSV(String csvStr) throws URISyntaxException;

  /**
   * Test a single URI for validity. Currently the only requirement is that
   * the URI must be absolute.
   *
   * @param uri the URI to validate.
   * @return true if the URI is acceptable.
   * @throws URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public boolean validURI(URI uri) throws URISyntaxException;

  /**
   * @param journalName Keyname of current journal
   * @param article     article URI
   * @throws Exception If listener failed
   */
  public void addXPubArticle(String journalName, URI article) throws Exception;

  /**
   * @param journalName Keyname of current journal
   * @param article     article URI
   * @throws Exception If listener failed
   */
  public void removeXPubArticle(String journalName, URI article) throws Exception;

  /**
   * Set current Journal issue URI.
   *
   * @param journalName Keyname of current journal
   * @param issueURI the URI of the current issue for the journal being modified.
   */
  public void setJrnlIssueURI(String journalName, URI issueURI);

  /**
   * Update the persistant store with the new journal changes.
   *
   * @throws RuntimeException if the sesion encounters an error during
   *                      the update.
   */
  public void flushStore();

  /**************************************************
   * Volume Management Methods                      *
   **************************************************/

  /**
   * Return a Volume object specified by URI.
   *
   * @param volURI the URI of the volume.
   * @return the volume object requested.
   * @throws RuntimeException throws RuntimeException if any one of the Volume URIs supplied
   *                      by the journal does not exist.
   */
  public Volume getVolume(URI volURI);

  /**
   * Uses the list of volume URIs maintained by the journal
   * to create a list of Volume objects.
   *
   * @param journalName Keyname of the current journal
   * @return the list of volumes for the current journal (never null)
   * @throws RuntimeException throws RuntimeException if any one of the Volume URIs supplied
   *                      by the journal does not exist.
   */
  public List<Volume> getVolumes(String journalName);
  /**
   * Create a new Volume and add it to the current Journal's list
   * of volumes it contains.
   *
   * @param journalName Keyname of the current journal
   * @param volURI    the uri of the new volume.
   * @param dsplyName the display name of the volume.
   * @param issueList a SPARATOR delimted list of issue doi's associated with
   *                  this volume.
   * @return the volume object that was created. ( returns null if there
   *         is no journal or volURI already exists ).
   * @throws RuntimeException       thrown when the Volume or Journal cannot be
   *                            saved or updated by the session.
   * @throws URISyntaxException thrown when values in issueList cannot be converted
   *                            to a URI
   */
  public Volume createVolume(String journalName, URI volURI, String dsplyName, String issueList)
      throws URISyntaxException;

  /**
   * Delete a Volume using the volumes URI.  Remove references to it from the journal
   * volume list.
   *
   * @param journalName Keyname of the current journal
   * @param volURI the volume to delete.
   * @throws RuntimeException throws RuntimeException if session cannot
   *                      delete the volume.
   */
  public void deleteVolume(String journalName, URI volURI);

  /**
   * Update a Volume.
   *
   * @param volume    the volume to update.
   * @param dsplyName the display name for the volume.
   * @param issueList a SEPARATOR delimitted string of issue doi's.
   * @return Volume   the update volume object.
   * @throws RuntimeException       throws and RuntimeException if the session is unable to
   *                            update the volume persistanct store.
   * @throws URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public Volume updateVolume(Volume volume, String dsplyName, List<URI> issueList)
      throws URISyntaxException;

  /**
   * Update a Volume using the URI. Retrieves volume from the persistant store
   * using the URI.
   *
   * @param volURI    the volume to update.
   * @param dsplyName the display name for the volume.
   * @param issueList a SEPARATOR delimitted string of issue doi's.
   * @return Volume   the update volume object.
   * @throws RuntimeException       throws and RuntimeException if the session is unable to
   *                            update the volume persistanct store.
   * @throws URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public Volume updateVolume(URI volURI, String dsplyName, List<URI> issueList)
      throws URISyntaxException;

  /**************************************************
   * Issue Management Methods                       *
   **************************************************/
  /**
   * Delete an Issue and remove it from each volume that references it.
   *
   * @param issue the issue that is to deleted.
   * @throws RuntimeException if session is not able to delete issue
   */
  public void deleteIssue(Issue issue);

  /**
   * Delete an Issue specified by URI. Remove it from each volume that references it.
   *
   * @param issueURI the uri of the issue to delete.
   * @throws RuntimeException if session is not able to delete issue
   */
  public void deleteIssue(URI issueURI);

  /**
   * Get an Issue specified by URI.
   *
   * @param issueURI the issue's URI.
   * @return the Issue object specified by URI.
   * @throws RuntimeException if the session get incounters an error.
   */
  public Issue getIssue(URI issueURI);
  /**
   * Get a list of issues from the specified volume.
   *
   * @param volumeURI the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws RuntimeException if the session get incounters an error.
   */
  public List<Issue> getIssues(URI volumeURI);

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volume the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws RuntimeException if the session get incounters an error.
   */
  public List<Issue> getIssues(Volume volume);

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volume the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws RuntimeException if the session get incounters an error.
   */
  public String getIssuesCSV(Volume volume);

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volURI the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws RuntimeException if the session get incounters an error.
   */
  public String getIssuesCSV(URI volURI);

  /**
   * Create an Issue. When an issue is created new DublinCore meta-data needs
   * to be attached to the issue. The data consists of a string list of doi's
   * delimited by SEPARATOR. The new issue is attached to the lastest volume
   * for the journal context.
   *
   * @param vol         Volume
   * @param issueURI    the issue to update.
   * @param imgURI      a URI for the article/image associated with this volume.
   * @param dsplyName   the display name for the volume.
   * @param articleList a SEPARATOR delimitted string of article doi's.
   * @return the issue created or null if unable to create the issue
   *         or the issue exist.
   * @throws RuntimeException throws RuntimeException if the session fails to save the
   *                      issue or update the volume.
   */
  public Issue createIssue(Volume vol, URI issueURI, URI imgURI, String dsplyName,
                           String articleList);

  /**
   * Update an Issue. Since this is an update it is assumed the issue is already
   * associated with aa volume.
   *
   * @param issueURI     the issue to update.
   * @param imgURI       a URI for the article/image associated with this volume.
   * @param dsplyName    the display name for the volume.
   * @param articleList  a SEPARATOR delimitted string of article doi's.
   * @param respectOrder respect the order manual ordering of articles within
   *                     articleTypes.
   * @return the updated issue or null if the issue does not exist.
   * @throws RuntimeException   throws RuntimeException if session cannot update the issue.
   * @throws URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public Issue updateIssue(URI issueURI, URI imgURI, String dsplyName,
                           List<URI> articleList, boolean respectOrder) throws URISyntaxException;

  /*
  *
  */

  public Issue removeArticle(Issue issue, URI articleURI);


  /*
   *
   */
  public Issue addArticle(Issue issue, URI articleURI);

  /**************************************************
   *                OTM queries.                    *
   **************************************************/

  /**
   * Get a list of volume URIs for this journal context.
   *
   * @param maxResults the maximum number of URIs to put into the list.
   *                   maxResults = 0 will return all URIs.
   * @param ascending  sort URI's in ascending order if true.
   * @return a list of volumes associated with this
   *         journal (never null).
   * @throws RuntimeException if session is not able create and execute a query.
   */
  public List<Volume> getVolumes(int maxResults, boolean ascending);

  /**
   * Get a list of issues for this journal context.
   *
   * @param maxResults the maximum number of URIs to put into the list.
   *                   maxResults = 0 will return all URIs.
   * @param ascending  sort URI's in ascending order if true.
   * @return the list of issue URIs for this journal context.
   * @throws RuntimeException if session is not able create or execute the query.
   */
  public List<Issue> getIssues(int maxResults, boolean ascending);

  /**
   * Get a list of volume URIs that reference this issue.
   *
   * @param issueURI URI of issue to find parents for.
   * @return the list of parent volumes that refernce this issue.
   * @throws RuntimeException if session is not able create or execute the query.
   */
  public List<Volume> getIssueParents(URI issueURI);


  /**
   * A faux journal object that can be accessed by Freemarker Templates.
   */
  public static final class JournalInfo {
    private String key, eissn;
    private String smartCollectionRulesDescriptor;
    private String image, currentIssue;
    private List<String> volumes;
    private List<String> simpleCollection;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String geteIssn() {
      return eissn;
    }

    public void seteIssn(String eissn) {
      this.eissn = eissn;
    }

    public String getSmartCollectionRulesDescriptor() {
      return smartCollectionRulesDescriptor;
    }

    public void setSmartCollectionRulesDescriptor(String smartCollectionRulesDescriptor) {
      this.smartCollectionRulesDescriptor = smartCollectionRulesDescriptor;
    }

    public String getImage() {
      return image;
    }

    public void setImage(String image) {
      this.image = image;
    }

    public String getCurrentIssue() {
      return currentIssue;
    }

    public void setCurrentIssue(String currentIssue) {
      this.currentIssue = currentIssue;
    }

    public List<String> getVolumes() {
      return volumes;
    }

    public void setVolumes(List<String> volumes) {
      this.volumes = volumes;
    }

    public List<String> getSimpleCollection() {
      return simpleCollection;
    }

    public void setSimpleCollection(List<String> simpleCollection) {
      this.simpleCollection = simpleCollection;
    }

    @Override
    public String toString() {
      return key;
    }
  }

  /**
   * A faux Journal object that can be accessed by the freemarker
   * template.
   *
   * @param journalName Keyname of the current journal
   * @return faux Journal object JournalInfo.
   */
  public JournalInfo createJournalInfo(String journalName);

  /*
   * set the journal service
   */
  public void setJournalService(JournalService journalService);
}
