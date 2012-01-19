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

package org.ambraproject.admin.action;

import org.ambraproject.models.Syndication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.admin.service.SyndicationService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.util.UriUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Display all of the Syndication objects associated to one Article and allow the user to <ul> <li>Syndicate the article
 * to syndication targets, if the article has not been previously submitted to those targets</li> <li>Resyndicate the
 * article to targets, if previous syndication attempts had failed</li> <li>Mark "in progress" syndications as
 * "failed"</li> </ul>
 */
public class ArticleSyndicationHistory extends BaseAdminActionSupport {
  private static final Logger log = LoggerFactory.getLogger(ManageFlagsAction.class);

  private List<Syndication> synHistory;
  private SyndicationService syndicationService;
  private String doi;
  private String[] synTargets;

  /**
   * Default Struts action, populate to be used in the freemarker templates
   *
   * @return SUCCESS
   */
  public String execute() {
    try {
      setCommonFields();
    } catch (Exception e) {
      String err = "Failed to find history for DOI:";
      if (doi != null)
        err = err + doi + ".<br/>";
      log.error(err, e);
      addActionError(err + e);
    }
    return this.hasActionErrors() ? ERROR : SUCCESS;
  }

  /**
   * ReSyndicate the selected targets for the current article
   *
   * @return ERROR or SUCCESS
   */
  public String resyndicate() {
    if (doi != null) {
      if ((synTargets != null) && (synTargets.length > 0)) {
        for (String target : synTargets) {

          Syndication syndication;
          try {
            syndication = syndicationService.syndicate(doi, target);
          } catch (NoSuchArticleIdException e) {
            addActionError("Article " + doi + " doesn't exist");
            log.warn("Attempted to resyndicate to a non-existent article: " + doi, e);
            continue;
          }

          if (Syndication.STATUS_FAILURE.equals(syndication.getStatus())) {
            addActionError("Syndication failed for DOI: " + doi + "<br/>"
                + syndication.getErrorMessage());
          }
        }
      } else {
        addActionError("No targets selected for syndication for DOI: " + doi);
      }
    } else {
      addActionError("Invalid or unspecified Article DOI.");
    }

    // Set the common fields after processing so that the new Syndication status is shown.
    try {
      setCommonFields();
    } catch (Exception e) {
      String err = "Failed to find history for DOI: ";
      if (doi != null)
        err = err + doi + "<br/>";
      log.error(err, e);
      addActionError(err + e);
    }
    return this.hasActionErrors() ? ERROR : SUCCESS;
  }

  /**
   * Update syndication statuses to FAILURE for the selected targets of the current article
   *
   * @return ERROR or SUCCESS
   */
  public String markSyndicationAsFailed() {
    if (doi != null) {
      if ((synTargets != null) && (synTargets.length > 0)) {
        for (String target : synTargets) {
          syndicationService.updateSyndication(
              doi, target, Syndication.STATUS_FAILURE,
              "Status manually changed to " + Syndication.STATUS_FAILURE);
        }
      } else {
        addActionError("Could not mark Syndication as failed: No targets selected for DOI: "
            + doi);
      }
    } else {
      addActionError("Could not mark Syndication as failed: Invalid or unspecified Article DOI.");
    }

    // Set the common fields after processing so that the new Syndication status is shown.
    try {
      setCommonFields();
    } catch (Exception e) {
      String err = "Failed to find history for DOI: ";
      if (doi != null)
        err = err + doi + "<br/>";
      log.error(err, e);
      addActionError(err + e);
    }
    return this.hasActionErrors() ? ERROR : SUCCESS;
  }

  /**
   * TODO: Check if the article exists (i.e., has not been deleted). TODO:   If not, then do not show the "Resyndicate"
   * button(s) on the screen.
   */
  private void setCommonFields() {
    // create a faux journal object for template
    initJournal();

    // Set the list of all Syndications for this Article.
    if (doi != null) {
      try {
        synHistory = syndicationService.getSyndications(doi);
      } catch (NoSuchArticleIdException e) {
        addActionError("Could not set common field because article " + doi + " does not exist");
      }
      if (synHistory.size() == 0)
        addActionMessage("No syndications were found");
    } else {
      addActionError("Invalid or unspecified Article DOI.");
    }
  }

  /**
   * Get all of the Syndications associated to the doi.
   *
   * @return All of the Syndications associated to the doi
   */
  public List<Syndication> getSyndicationHistory() {
    return synHistory;
  }

  /**
   * Get a list of all syndications that are not in an "in progress" state. These are the Syndications that can be
   * resent through this page.
   *
   * @return a list of Syndications
   */
  public List<Syndication> getFinishedSyndications() {
    List<Syndication> finishedSyndications = new ArrayList<Syndication>();

    for (Syndication syn : getSyndicationHistory()) {
      if (!syn.getStatus().equals(Syndication.STATUS_IN_PROGRESS)) {
        finishedSyndications.add(syn);
      }
    }

    return finishedSyndications;
  }

  /**
   * Sets service used to syndicate these articles to external organizations
   *
   * @param syndicationService The service used to syndicate these articles to external organizations
   */
  @Required
  public void setSyndicationService(SyndicationService syndicationService) {
    this.syndicationService = syndicationService;
  }

  /**
   * Set the current article URI
   *
   * @param doi The article URI
   */
  public void setArticle(String doi) {
    this.doi = doi.trim();
  }

  /**
   * Get the current article ID
   *
   * @return article id
   */
  public String getArticle() {
    return doi;
  }

  /**
   * Set the targets to resyndicate
   *
   * @param targets a list of syndication targets
   */
  public void setTarget(String[] targets) {
    synTargets = targets;
  }
}
