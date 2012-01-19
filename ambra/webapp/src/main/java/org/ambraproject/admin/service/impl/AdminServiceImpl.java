/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

package org.ambraproject.admin.service.impl;

import org.ambraproject.admin.service.AdminService;
import org.ambraproject.admin.service.OnCrossPubListener;
import org.ambraproject.journal.JournalService;
import org.ambraproject.models.Article;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.util.UriUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Journal;
import org.topazproject.ambra.models.Volume;
import org.topazproject.otm.criterion.DetachedCriteria;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * AdminService encapsulates the basic services needed by all administrative
 * actions.
 */
public class AdminServiceImpl extends HibernateServiceImpl implements AdminService {

  // Services set by Spring
  private JournalService journalService;

  // Private fields
  private static final String SEPARATORS = "[,;]";
  private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

  private List<OnCrossPubListener> onCrossPubListener;

  public void setOnCrossPubListener(List<OnCrossPubListener> onCrossPubListener) {
    this.onCrossPubListener = onCrossPubListener;
  }

  /**************************************************
   * Journal Management Methods                     *
   **************************************************/

  /**
   * Get a coppy of the Volume DOIs as a String List.
   *
   * @param journal Journal
   * @return the current volume list for the journal.
   */
  private List<String> getJrnlVolDOIs(Journal journal) {
    List<URI> volURIs = journal.getVolumes();
    List<String> volStr = new ArrayList<String>();

    for (URI volURI : volURIs) {
      volStr.add(volURI.toString());
    }

    return volStr;
  }

  /**
   * Give a SEPARATOR delimitted string of volume URIs convert them
   * to a list of separated URIs.
   *
   * @param csvStr the list of string of URIs.
   * @return a list of URI created from the string csvStr .
   * @throws java.net.URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public List<URI> parseCSV(String csvStr) throws URISyntaxException {
    List<URI> listURIs = new ArrayList<URI>();

    if ((csvStr != null) && (csvStr.length() > 0)) {
      String[] elements = csvStr.split(SEPARATORS);

      for (String element : elements) {
        URI uri = UriUtil.validateUri(element.trim(), "CSV Uri list");
        listURIs.add(uri);
      }
    }
    return listURIs;
  }

  /**
   * Test a single URI for validity. Currently the only requirement is that
   * the URI must be absolute.
   *
   * @param uri the URI to validate.
   * @return true if the URI is acceptable.
   * @throws java.net.URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public boolean validURI(URI uri) throws URISyntaxException {
    // Currently the only requirement is for the uri to be absolute.
    return uri.isAbsolute();
  }

  /**
   * @param journalName Keyname of current journal
   * @param article     article URI
   * @throws Exception If listener failed
   */
  public void addXPubArticle(String journalName, URI article) throws Exception {
    Journal journal = journalService.getJournal(journalName);
    List<URI> collection = journal.getSimpleCollection();
    if (!collection.contains(article)) {
      collection.add(article);
      updateStore(journal);
      flushStore();
      invokeOnCrossPublishListeners(article.toString());
    }
  }

  /**
   * @param journalName Keyname of current journal
   * @param article     article URI
   * @throws Exception If listener failed
   */
  public void removeXPubArticle(String journalName, URI article) throws Exception {
    Journal journal = journalService.getJournal(journalName);
    List<URI> collection = journal.getSimpleCollection();
    if (collection.contains(article)) {
      collection.remove(article);
      updateStore(journal);
      flushStore();
      invokeOnCrossPublishListeners(article.toString());
    }
  }

  /**
   * Invokes all objects that are registered to listen to article cross publish event.
   *
   * @param articleId Article ID
   * @throws Exception If listener method failed
   */
  private void invokeOnCrossPublishListeners(String articleId) throws Exception {
    if (onCrossPubListener != null) {
      for (OnCrossPubListener listener : onCrossPubListener) {
        listener.articleCrossPublished(articleId);
      }
    }
  }

  /**
   * Set current Journal issue URI.
   *
   * @param journalName Keyname of current journal
   * @param issueURI the URI of the current issue for the journal being modified.
   */
  public void setJrnlIssueURI(String journalName, URI issueURI) {
    Journal journal = journalService.getJournal(journalName);
    URI newImage = ((issueURI != null) && (issueURI.toString().length() == 0)) ? null : issueURI;
    journal.setCurrentIssue(newImage);
    updateStore(journal);
  }

  /**
   * Update the persistent store with the new journal changes.
   *
   * @param o Object to update
   * @throws org.topazproject.otm.OtmException if the session encounters an error during
   *                      the update.
   */
  private void updateStore(Object o) {
    hibernateTemplate.saveOrUpdate(o);
  }

  /**
   * Update the persistant store with the new journal changes.
   *
   * @throws org.topazproject.otm.OtmException if the sesion encounters an error during
   *                      the update.
   */
  public void flushStore() {
    hibernateTemplate.flush();
  }

  /**************************************************
   * Volume Management Methods                      *
   **************************************************/

  /**
   * Return a Volume object specified by URI.
   *
   * @param volURI the URI of the volume.
   * @return the volume object requested.
   * @throws org.topazproject.otm.OtmException throws OtmException if any one of the Volume URIs supplied
   *                      by the journal does not exist.
   */
  public Volume getVolume(URI volURI) {
    return (Volume)hibernateTemplate.get(Volume.class, volURI);
  }

  /**
   * Uses the list of volume URIs maintained by the journal
   * to create a list of Volume objects.
   *
   * @param journalName Keyname of the current journal
   * @return the list of volumes for the current journal (never null)
   * @throws org.topazproject.otm.OtmException throws OtmException if any one of the Volume URIs supplied
   *                      by the journal does not exist.
   */
  public List<Volume> getVolumes(String journalName) {
    List<Volume> volumes = new ArrayList<Volume>();

    Journal journal = journalService.getJournal(journalName);
    List<URI> volURIs = journal.getVolumes();

    for (final URI volUri : volURIs) {
      Volume volume = getVolume(volUri);

      if (volume != null) {
        volumes.add(volume);
      } else {
        log.error("getVolumes failed to retrieve: " + volUri);
      }
    }
    return volumes;
  }

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
   * @throws org.topazproject.otm.OtmException       thrown when the Volume or Journal cannot be
   *                            saved or updated by the session.
   * @throws java.net.URISyntaxException thrown when values in issueList cannot be converted
   *                            to a URI
   */
  public Volume createVolume(String journalName, URI volURI, String dsplyName, String issueList)
      throws URISyntaxException {

    String displayName = (dsplyName == null) ? "" : dsplyName;

    /* If there is no journal then don't
     * create an orphan volume : return null.
     */
    if (journalName == null) {
      return null;
    }

    Journal journal = journalService.getJournal(journalName);

    // Volume URI already exist return null
    if (hibernateTemplate.get(Volume.class, volURI) != null) {
      return null;
    }

    Volume newVol = new Volume();
    newVol.setId(volURI);
    newVol.setCreated(new Date());
    newVol.setDisplayName(displayName);

    /*
     * Issues come in as a SEPARATOR delimitted string
     * that is split into an ArrayList of strings.
     */
    if (issueList != null && issueList.length() != 0) {
      List<URI> issues = new ArrayList<URI>();

      for (final String issueToAdd : issueList.split(SEPARATORS)) {
        if (issueToAdd.length() > 0) {
          issues.add(URI.create(issueToAdd));
        }
      }
      newVol.setIssueList(issues);
    }

    // save the new volume.
    hibernateTemplate.save(newVol);
    // Add this new volume URI to the Journal list
    List<URI> volURIs = journal.getVolumes();

    // If the URI is not already in the list, add it.
    if (!volURIs.contains(volURI)) {
      volURIs.add(volURI);
      updateStore(journal);
    }

    flushStore();

    return newVol;
  }

  /**
   * Delete a Volume using the volumes URI.  Remove references to it from the journal
   * volume list.
   *
   * @param journalName Keyname of the current journal
   * @param volURI the volume to delete.
   * @throws org.topazproject.otm.OtmException throws OtmException if session cannot
   *                      delete the volume.
   */
  public void deleteVolume(String journalName, URI volURI) {
    // the Volume to update
    Journal journal = journalService.getJournal(journalName);
    Volume volume = (Volume)hibernateTemplate.get(Volume.class, volURI);
    // Update the object store
    hibernateTemplate.delete(volume);
    // Update Journal
    List<URI> jrnlVols = journal.getVolumes();

    if (jrnlVols.contains(volume.getId())) {
      jrnlVols.remove(volume.getId());
      journal.setVolumes(jrnlVols);
    }
    updateStore(journal);
    flushStore();
  }

  /**
   * Update a Volume.
   *
   * @param volume    the volume to update.
   * @param dsplyName the display name for the volume.
   * @param issueList a SEPARATOR delimitted string of issue doi's.
   * @return Volume   the update volume object.
   * @throws org.topazproject.otm.OtmException       throws and OtmException if the session is unable to
   *                            update the volume persistanct store.
   * @throws java.net.URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public Volume updateVolume(Volume volume, String dsplyName, List<URI> issueList)
      throws URISyntaxException {

    volume.setDisplayName(dsplyName);
    volume.setIssueList(issueList);
    updateStore(volume);

    return volume;
  }

  /**
   * Update a Volume using the URI. Retrieves volume from the persistant store
   * using the URI.
   *
   * @param volURI    the volume to update.
   * @param dsplyName the display name for the volume.
   * @param issueList a SEPARATOR delimitted string of issue doi's.
   * @return Volume   the update volume object.
   * @throws org.topazproject.otm.OtmException       throws and OtmException if the session is unable to
   *                            update the volume persistanct store.
   * @throws java.net.URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  public Volume updateVolume(URI volURI, String dsplyName, List<URI> issueList)
      throws URISyntaxException {
    // If the volume doesn't exist return null
    Volume volume = (Volume)hibernateTemplate.get(Volume.class, volURI);

    if (volume != null) {
      return updateVolume(volume, dsplyName, issueList);
    }

    return null;
  }

  /**************************************************
   * Issue Management Methods                       *
   **************************************************/
  /**
   * Delete an Issue and remove it from each volume that references it.
   *
   * @param issue the issue that is to deleted.
   * @throws org.topazproject.otm.OtmException if session is not able to delete issue
   */
  public void deleteIssue(Issue issue) {
    URI issueURI = issue.getId();

    hibernateTemplate.delete(issue);
    // Get all volumes that have this issue in their issueList
    List<Volume> containerVols = getIssueParents(issueURI);

    for (Volume vol : containerVols) {
      vol.getIssueList().remove(issueURI);
      updateStore(vol);
    }

    flushStore();
  }

  /**
   * Delete an Issue specified by URI. Remove it from each volume that references it.
   *
   * @param issueURI the uri of the issue to delete.
   * @throws org.topazproject.otm.OtmException if session is not able to delete issue
   */
  public void deleteIssue(URI issueURI) {
    // the Volume to update
    Issue issue = (Issue)hibernateTemplate.get(Issue.class, issueURI);
    deleteIssue(issue);
  }

  /**
   * Get an Issue specified by URI.
   *
   * @param issueURI the issue's URI.
   * @return the Issue object specified by URI.
   * @throws org.topazproject.otm.OtmException if the session get incounters an error.
   */
  public Issue getIssue(URI issueURI) {
    return (Issue)hibernateTemplate.get(Issue.class, issueURI);
  }

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volumeURI the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws org.topazproject.otm.OtmException if the session get incounters an error.
   */
  public List<Issue> getIssues(URI volumeURI) {
    Volume volume = getVolume(volumeURI);
    return getIssues(volume);
  }

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volume the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws org.topazproject.otm.OtmException if the session get incounters an error.
   */
  public List<Issue> getIssues(Volume volume) {
    List<Issue> issues = new ArrayList<Issue>();

    if (volume.getIssueList() != null) {
      for (final URI issueURI : volume.getIssueList()) {
        final Issue issue = getIssue(issueURI);

        if (issue != null) {
          issues.add(issue);
        } else {
          log.error("Error getting issue: " + issueURI.toString());
        }
      }
    }
    return issues;
  }

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volume the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws org.topazproject.otm.OtmException if the session get incounters an error.
   */
  public String getIssuesCSV(Volume volume) {
    StringBuilder issCSV = new StringBuilder();
    List<Issue> issues = getIssues(volume);
    Iterator iter = issues.listIterator();

    while (iter.hasNext()) {
      Issue i = (Issue) iter.next();
      issCSV.append(i.getId().toString());
      if (iter.hasNext()) {
        issCSV.append(',');
      }
    }
    return issCSV.toString();
  }

  /**
   * Get a list of issues from the specified volume.
   *
   * @param volURI the volume of interest.
   * @return the list of issues associated with the volume (never null).
   * @throws org.topazproject.otm.OtmException if the session get incounters an error.
   */
  public String getIssuesCSV(URI volURI) {
    Volume volume = getVolume(volURI);

    return getIssuesCSV(volume);
  }

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
   * @throws org.topazproject.otm.OtmException throws OtmException if the session fails to save the
   *                      issue or update the volume.
   */
  public Issue createIssue(Volume vol, URI issueURI, URI imgURI, String dsplyName,
                           String articleList) {

    /*
     * Return null if issue exist.
     */
    if (hibernateTemplate.get(Issue.class, issueURI) != null) {
      return null;
    }

    Issue newIssue = new Issue();
    newIssue.setId(issueURI);

    newIssue.setCreated(new Date());
    newIssue.setDisplayName(dsplyName);

    if ((imgURI == null) || imgURI.toString().equals("")) {
      newIssue.setImage(null);
    } else {
      newIssue.setImage(imgURI);
      addImageArticleInfo(imgURI.toString(), newIssue);
    }

    /*
     * Articles are specified in a SEPARATOR delimited
     * string of the doi's for each article associated
     * with the issue.
     */
    if (articleList != null && articleList.length() != 0) {
      for (final String articleToAdd : articleList.split(SEPARATORS)) {
        if (articleToAdd.length() > 0) {
          addArticleToList(newIssue, URI.create(articleToAdd.trim()));
        }
      }
    }
    // Default respect order to false.
    newIssue.setRespectOrder(false);
    hibernateTemplate.save(newIssue);

    // Update the volume.
    vol.getIssueList().add(issueURI);
    updateStore(vol);
    flushStore();

    return newIssue;
  }

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
   * @throws RuntimeException throws RuntimeException if session cannot update the issue.
   * @throws java.net.URISyntaxException if a DOI cannot be converted to a vaild URI
   *                            a syntax exception is thrown.
   */
  @SuppressWarnings("unchecked")
  public Issue updateIssue(URI issueURI, URI imgURI, String dsplyName,
                           List<URI> articleList, boolean respectOrder) throws URISyntaxException {
    // the Issue to update
    Issue issue = (Issue) hibernateTemplate.get(Issue.class, issueURI);

    // If the issue doesn't exist then return null.
    if (issue == null) {
      return null;
    }
    issue.setDisplayName(dsplyName);
    issue.setArticleList(articleList);
    issue.setRespectOrder(respectOrder);

    if ((imgURI == null) || (imgURI.toString().equals(""))) {
      issue.setImage(null);
    } else {
      issue.setImage(imgURI);
      addImageArticleInfo(imgURI.toString(), issue);
    }

    updateStore(issue);
    flushStore();

    return issue;
  }

  /**
   * Add information to the issue from the image article, if it exists
   * @param imageArticleDoi    the doi for the image article
   * @param issue the issue to update
   */
  private void addImageArticleInfo(String imageArticleDoi, Issue issue) {
    try {
      Object[] titleAndDescription = (Object[]) hibernateTemplate.findByCriteria(
          org.hibernate.criterion.DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", imageArticleDoi))
              .setProjection(Projections.projectionList()
                  .add(Projections.property("title"))
                  .add(Projections.property("description")))
      ).get(0);
      issue.setTitle((String) titleAndDescription[0]);
      issue.setDescription((String) titleAndDescription[1]);
    } catch (IndexOutOfBoundsException e) {
      //the image article didn't exist
    }
  }

  /*
  *
  */

  public Issue removeArticle(Issue issue, URI articleURI) {

    removeArticleFromList(issue, articleURI);
    updateStore(issue);

    return issue;
  }

  private void removeArticleFromList(Issue issue, URI articleURI) {
    List<URI> articleList = issue.getArticleList();

    if (articleList.isEmpty() && !issue.getSimpleCollection().isEmpty()) {
      articleList = new ArrayList<URI>(issue.getSimpleCollection());
    }

    if (articleList.contains(articleURI)) {
      articleList.remove(articleURI);
    }

    //Shadow this removal in the simple collection
    if (issue.getSimpleCollection().contains(articleURI)) {
      issue.getSimpleCollection().remove(articleURI);
    }
  }

  /*
   *
   */

  @SuppressWarnings("unchecked")
  public Issue addArticle(Issue issue, URI articleURI) {
    addArticleToList(issue, articleURI);
    updateStore(issue);

    return issue;
  }

  private void addArticleToList(Issue issue, URI articleURI) {
    /*
     * Since we are doing an on-the-fly data migration (unwisely)
     * we need to update articleList if it has not been done yet.
     */
    List<URI> articleList = issue.getArticleList();

    if (articleList.isEmpty() && !issue.getSimpleCollection().isEmpty()) {
      articleList = new ArrayList<URI>(issue.getSimpleCollection());
    }

    //Only add if not there
    if (!articleList.contains(articleURI)) {
      articleList.add(articleURI);
    }

    //Shadow this addition in the simple collection
    if (!issue.getSimpleCollection().contains(articleURI)) {
      issue.getSimpleCollection().add(articleURI);
    }
  }

  /**************************************************
   *                OTM queries.                    *
   **************************************************/

  /**
   * Get a list of volume URIs for this journal context.
   *
   * @param maxResults the maximum number of URIs to put into the list. maxResults = 0 will return all URIs.
   * @param ascending  sort URI's in ascending order if true.
   * @return a list of volumes associated with this journal (never null).
   * @throws org.topazproject.otm.OtmException
   *          if session is not able create and execute a query.
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<Volume> getVolumes(final int maxResults, final boolean ascending) {
    return (List<Volume>) hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria c = session.createCriteria(Volume.class);

        if (ascending) {
          c.addOrder(Order.asc("aggregationUri"));
        } else {
          c.addOrder(Order.desc("aggregationUri"));
        }

        if (maxResults > 0) {
          c.setMaxResults(maxResults);
        }

        List<Volume> volRslt = new ArrayList<Volume>();
        Iterator r = c.list().iterator();

        while (r.hasNext()) {
          Volume v = (Volume) r.next();
          volRslt.add(v);
        }

        return volRslt;
      }
    });
  }

  /**
   * Get a list of issues for this journal context.
   *
   * @param maxResults the maximum number of URIs to put into the list. maxResults = 0 will return all URIs.
   * @param ascending  sort URI's in ascending order if true.
   * @return the list of issue URIs for this journal context.
   * @throws org.topazproject.otm.OtmException
   *          if session is not able create or execute the query.
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<Issue> getIssues(final int maxResults, final boolean ascending) {
    return (List<Issue>) hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria c = session.createCriteria(Issue.class);

        if (ascending) {
          c.addOrder(Order.asc("aggregationUri"));
        } else {
          c.addOrder(Order.desc("aggregationUri"));
        }

        if (maxResults > 0) {
          c.setMaxResults(maxResults);
        }

        StringBuilder qry = new StringBuilder();

        List<Issue> issueRslt = new ArrayList<Issue>();

        Iterator r = c.list().iterator();
        while (r.hasNext()) {
          Issue i = (Issue) r.next();
          issueRslt.add(i);
        }

        return issueRslt;
      }
    });
  }

  /**
   * Get a list of volume URIs that reference this issue.
   *
   * @param issueURI URI of issue to find parents for.
   * @return the list of parent volumes that refernce this issue.
   * @throws org.topazproject.otm.OtmException
   *          if session is not able create or execute the query.
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<Volume> getIssueParents(final URI issueURI) {
    return (List<Volume>) hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {

        List<Volume> volRslt = session.createQuery("from Volume v join v.issueList issues where issueUri = :issueURI")
            .setParameter("issueURI", issueURI.toString())
            .list();

        return volRslt;
      }
    });
  }

  /**************************************************
   * Spring managed setter/getters for fields       *
   **************************************************/
  /**
   * Sets the JournalService.
   *
   * @param journalService The JournalService to set.
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * A faux Journal object that can be accessed by the freemarker template.
   *
   * @param journalName Keyname of the current journal
   * @return faux Journal object JournalInfo.
   */
  @Transactional(readOnly = true)
  public JournalInfo createJournalInfo(String journalName) {

    JournalInfo jrnlInfo = new JournalInfo();

    // If the is no current journal the return null
    if (journalName == null) {
      return jrnlInfo;
    }

    Journal journal = journalService.getJournal(journalName);

    jrnlInfo.setKey(journal.getKey());
    jrnlInfo.seteIssn(journal.geteIssn());

    URI uri = (journal.getCurrentIssue() == null) ? null : journal.getCurrentIssue();
    jrnlInfo.setCurrentIssue((uri != null) ? uri.toString() : null);

    uri = (journal.getImage() == null) ? null : journal.getImage();
    jrnlInfo.setImage((uri != null) ? uri.toString() : null);

    List<URI> jscs = journal.getSimpleCollection();
    if (jscs != null) {
      List<String> slist = new ArrayList<String>(jscs.size());

      for (URI u : jscs) {
        slist.add(u.toString());
      }

      jrnlInfo.setSimpleCollection(slist);
    }

    List<DetachedCriteria> dclist = journal.getSmartCollectionRules();

    if (dclist != null && dclist.size() > 0) {
      StringBuilder sb = new StringBuilder();

      for (DetachedCriteria dc : journal.getSmartCollectionRules()) {
        sb.append(", ").append(dc.toString());
      }

      jrnlInfo.setSmartCollectionRulesDescriptor(sb.substring(2));
    }
    jrnlInfo.setVolumes(getJrnlVolDOIs(journal));
    return jrnlInfo;
  }
}
