/*
 * $HeadURL$
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

package org.ambraproject.admin.service.impl;

import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.ambraproject.admin.service.SyndicationException;
import org.ambraproject.admin.service.SyndicationService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.journal.JournalService;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.models.Syndication;
import org.ambraproject.models.Article;
import org.ambraproject.queue.MessageService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.Configuration;
import org.ambraproject.service.HibernateServiceImpl;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Manage the syndication process, including creating and updating Syndication objects, as well as pushing syndication
 * messages to a message queue.
 *
 * @author Scott Sterling
 * @author Joe Osowski
 */
public class SyndicationServiceImpl extends HibernateServiceImpl implements SyndicationService {
  private static final Logger log = LoggerFactory.getLogger(SyndicationServiceImpl.class);

  private Configuration configuration;
  private MessageService messageService;
  private JournalService journalService;
  private final DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  /**
   * Set the Service which will be used to push syndication messages to the message queue Injected through Spring.
   *
   * @param messageService The Service through which syndication messages will be pushed to the message queue
   */
  @Required
  public void setMessageService(MessageService messageService) {
    this.messageService = messageService;
  }

  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * Setter method for configuration. Injected through Spring.
   * <p/>
   * Response queues are obtained from configuration file. Beans that consume response queue are named
   * &lt;target_lowercase&gt;ResponseConsumer and should already be defined in Spring context. Example: for PMC, the
   * consumer bean is named <i>pmcResponseConsumer</i>.
   * <p/>
   * In addition to the normal route, two routes for testing are configured for each target: <ul>
   * <li>seda:test&lt;target&gt;Ok - loopback route that always returns success. Example: to simulate a successful queue
   * submission for PMC, send a message to the queue named <i>seda:testPMCOk</i></li> <li>seda:test&lt;target&gt;Fail -
   * loopback route that always returns failure. Example: to simulate a failed queue submission for PMC, send a message
   * to the queue named <i>seda:testPMCFail</i></li> </ul>
   *
   * @param configuration Ambra configuration
   */
  @Required
  public void setAmbraConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }


  /**
   * @param articleTypes All the article types of the Article for which Syndication objects are being created
   * @return Whether to create a Syndication object for this Article
   */
  private boolean isSyndicatableType(Set<String> articleTypes) {
    String articleTypeDoNotCreateSyndication = "http://rdf.plos.org/RDF/articleType/Issue%20Image";
    return !(articleTypes != null && articleTypes.contains(articleTypeDoNotCreateSyndication));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Syndication getSyndication(final String articleDoi, final String syndicationTarget) {
    List<Syndication> results = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Syndication.class)
            .add(Restrictions.eq("target", syndicationTarget))
            .add(Restrictions.eq("doi", articleDoi)),
        0, 1);
    if (results.size() == 0) {
      return null;
    } else {
      return results.get(0);
    }
  }

  @Transactional(rollbackFor = {Throwable.class})
  @Override
  public Syndication updateSyndication(final String articleDoi, final String syndicationTarget, final String status,
                                       final String errorMessage) {
    Syndication syndication = getSyndication(articleDoi, syndicationTarget);
    if (syndication == null) {
      throw new SyndicationException("No such syndication for doi " + articleDoi + " and target " + syndicationTarget);
    }
    syndication.setStatus(status);
    syndication.setErrorMessage(errorMessage);
    hibernateTemplate.update(syndication);

    return syndication;
  }

  @Transactional(rollbackFor = {Throwable.class})
  @Override
  @SuppressWarnings("unchecked")
  public List<Syndication> createSyndications(String articleDoi) throws NoSuchArticleIdException {

    try {
      Article article = (Article) hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.eq("doi", articleDoi)), 0, 1)
          .get(0);
      if (!isSyndicatableType(article.getTypes())) {
        //don't syndicate
        return new ArrayList<Syndication>();
      }
    } catch (IndexOutOfBoundsException e) {
      //the article didn't exist
      throw new NoSuchArticleIdException(articleDoi);
    }
    List<HierarchicalConfiguration> allSyndicationTargets = ((HierarchicalConfiguration)
        configuration).configurationsAt("ambra.services.syndications.syndication");

    if (allSyndicationTargets == null || allSyndicationTargets.size() < 1) { // Should never happen.
      log.warn("There are no Syndication Targets defined in the property: " +
          "ambra.services.syndications.syndication so no Syndication objects were created for " +
          "the article with ID = " + articleDoi);
      return new ArrayList<Syndication>();
    }

    List<Syndication> syndications = new ArrayList<Syndication>(allSyndicationTargets.size());

    for (HierarchicalConfiguration targetNode : allSyndicationTargets) {
      String target = targetNode.getString("[@target]");
      Syndication existingSyndication = getSyndication(articleDoi, target);
      if (existingSyndication != null) {
        syndications.add(existingSyndication);
      } else {
        Syndication syndication = new Syndication(articleDoi, target);
        syndication.setStatus(Syndication.STATUS_PENDING);
        syndication.setSubmissionCount(0);
        hibernateTemplate.save(syndication);
        syndications.add(syndication);
      }
    }
    return syndications;
  }


  @Transactional
  @SuppressWarnings("unchecked")
  @Override
  public List<Syndication> getFailedAndInProgressSyndications(final String journalKey) {
    Integer numDaysInPast = configuration.getInteger(
        "ambra.virtualJournals." + journalKey + ".syndications.display.numDaysInPast", 30);

    // The most recent midnight.  No need to futz about with exact dates.
    final Calendar start = Calendar.getInstance();
    start.set(Calendar.HOUR, 0);
    start.set(Calendar.MINUTE, 0);
    start.set(Calendar.SECOND, 0);
    start.set(Calendar.MILLISECOND, 0);

    final Calendar end = (Calendar) start.clone(); // The most recent midnight (last night)

    start.add(Calendar.DATE, -(numDaysInPast));
    end.add(Calendar.DATE, 1); // Include everything that happened today.

    final Journal journal = journalService.getJournal(journalKey);

    if (journal == null) {
      throw new SyndicationException("Could not find journal for journal key: " + journalKey);
    }

    return (List<Syndication>) hibernateTemplate.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("select {s.*} from syndication s ")
            .append("join article a on s.doi = a.doi ")
            .append("where s.status in ('" + Syndication.STATUS_IN_PROGRESS + "','" + Syndication.STATUS_FAILURE + "')")
            .append("and s.lastModified between '" + mysqlDateFormat.format(start.getTime()) + "'")
            .append(" and '" + mysqlDateFormat.format(end.getTime()) + "' and ")
            .append("a.eIssn = '" + journal.geteIssn() + "'");

        return session.createSQLQuery(sql.toString())
            .addEntity("s", Syndication.class).list();
      }
    });
  }

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public List<Syndication> getSyndications(final String articleDoi) {
    return hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Syndication.class)
            .add(Restrictions.eq("doi", articleDoi)));
  }

  @Transactional(rollbackFor = {Throwable.class})
  @SuppressWarnings("unchecked")
  @Override
  public Syndication syndicate(String articleDoi, String syndicationTarget) throws NoSuchArticleIdException {
    List<Article> matchingArticle = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Article.class)
            .add(Restrictions.eq("doi", articleDoi)), 0, 1);
    if (matchingArticle.size() == 0) {
      throw new NoSuchArticleIdException(articleDoi, "The article may have been deleted."
          + " Please reingest this article before attempting to syndicate to it.");
    }
    final String archiveName = matchingArticle.get(0).getArchiveName();
    if (archiveName == null || archiveName.isEmpty()) {
      throw new SyndicationException("The article " + articleDoi
          + " does not have an archive file associated with it.");
    }

    Syndication syndication = getSyndication(articleDoi, syndicationTarget);
    if (syndication == null) {
      //no existing syndication
      syndication = new Syndication(articleDoi, syndicationTarget);
      syndication.setStatus(Syndication.STATUS_IN_PROGRESS);
      syndication.setErrorMessage(null);
      syndication.setSubmissionCount(1);
      syndication.setLastSubmitTimestamp(new Date());
      hibernateTemplate.save(syndication);
    } else {
      syndication.setStatus(Syndication.STATUS_IN_PROGRESS);
      syndication.setErrorMessage(null);
      syndication.setSubmissionCount(syndication.getSubmissionCount() + 1);
      syndication.setLastSubmitTimestamp(new Date());
      hibernateTemplate.update(syndication);
    }

    try {
      //  Send message.
      messageService.sendSyndicationMessage(syndicationTarget, articleDoi, archiveName);
      log.info("Successfully sent a Message to plos-queue for {} to be syndicated to {}", articleDoi, syndicationTarget);
    } catch (Exception e) {
      log.warn("Error syndicating " + articleDoi + " to " + syndicationTarget, e);
      //update to failure
      updateSyndication(articleDoi, syndicationTarget, Syndication.STATUS_FAILURE, e.getMessage());
    }
    return syndication;
  }
}