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

package org.ambraproject.article.action;

import java.util.List;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.ApplicationException;
import org.ambraproject.service.XMLService;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.BrowseService;
import org.ambraproject.journal.JournalService;
import org.ambraproject.model.IssueInfo;
import org.ambraproject.model.VolumeInfo;
import org.topazproject.ambra.models.Journal;

public class BrowseVolumeAction extends BaseActionSupport {
  private static final Logger log = LoggerFactory.getLogger(BrowseArticlesAction.class);
  private BrowseService browseService;
  private JournalService journalService;
  private IssueInfo currentIssue;
  private int currentIssueNumber;
  private VolumeInfo currentVolume;
  private List<VolumeInfo> volumeInfos;
  private String currentIssueDescription;
  private XMLService secondaryObjectService;

  @Override
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    // JournalService, OTM usage wants to be in a Transaction
    Journal currentJournal = journalService.getJournal(getCurrentJournal());

    //WTO: ToDo there is a lot of needless code here - must clean up.
    if (currentJournal == null) {
      log.error("Unable to retrieve the Journal object for Current Journal " + getCurrentJournal());
      return ERROR;
    }

    //  Creates the list of Volumes in this Journal (at the bottom of the page).
    volumeInfos = browseService.getVolumeInfosForJournal(currentJournal);

    if (currentJournal.getCurrentIssue() != null) {
      currentIssue = browseService.getIssueInfo(currentJournal.getCurrentIssue());
    }
    // The Current Issue field may not map to an actual Issue.
    if (currentIssue == null) {
      // Current Issue has not been set for this Journal,
      // so get the most recent issue from the most recent volume.
      URI mostRecentIssueUri = browseService.getLatestIssueFromLatestVolume(currentJournal);
      if (mostRecentIssueUri != null) {
        currentIssue = browseService.getIssueInfo(mostRecentIssueUri);
      }
    }
    if (currentIssue != null) {
      if (currentIssue.getParentVolume() != null) {
        currentVolume = null;
        for (VolumeInfo vol : volumeInfos) {
          if (vol.getId().equals(currentIssue.getParentVolume())) {
            currentVolume = vol;
            break;
          }
        }

        int issueNum = 1;
        for (IssueInfo issue : currentVolume.getIssueInfos()) {
          if (issue.getId().equals(currentIssue.getId())) {
            currentIssueNumber = issueNum;
            break;
          }
          issueNum++;
        }
      } else {
        // Figure out what issue number the currentIssue is in its volume
        for (VolumeInfo vol : volumeInfos) {
          int issueNum = 1;
          for (IssueInfo issue : vol.getIssueInfos()) {
            if (issue.getId().equals(currentIssue.getId())) {
              currentIssueNumber = issueNum;
              currentVolume = vol;  //  Display the Volume that contains the Issue being shown.
              break;
            }
            issueNum++;
          }
        }
      }

      // Translate the currentIssue description to HTML
      if (currentIssue.getDescription() != null) {
        try {
          currentIssueDescription =
            secondaryObjectService.getTransformedDescription(currentIssue.getDescription());
        } catch (ApplicationException e) {
          log.error("Failed to translate issue description to HTML.", e);
          // Just use the untranslated issue description
          currentIssueDescription = currentIssue.getDescription();
        }
      } else {
        log.error("The currentIssue description was null. Issue DOI='"+currentIssue.getId()+"'");
        currentIssueDescription = "No description found for this issue";
      }
    }

    return SUCCESS;
  }

  /**
   * The sequence number of the current issue. This is calculated in the execute
   * method and displayed in the view.
   * 
   * @return the current issue number
   */
  public int getCurrentIssueNumber() {
    return currentIssueNumber;
  }

  /**
   * The current issue as defined for the Journal.
   * 
   * @return the current issue
   */
  public IssueInfo getCurrentIssue() {
    return currentIssue;
  }

  /**
   * Called by Spring injection when this class is loaded...
   * 
   * @param browseService
   *          The browseService to set.
   */
  @Required
  public void setBrowseService(BrowseService browseService) {
    this.browseService = browseService;
  }

  /**
   * Called by Spring injection when this class is loaded...
   * 
   * @param journalService
   *          The JournalService to set.
   */
  @Required
  public void setJournalService(JournalService journalService) {
    this.journalService = journalService;
  }

  /**
   * Returns the last linked volume for this journal - which is assumed to be
   * the current volume.
   * 
   * @return the current volume for this journal
   */
  public VolumeInfo getCurrentVolume() {
    return currentVolume;
  }

  /**
   * @return the VolumeInfos.
   */
  public List<VolumeInfo> getVolumeInfos() {
    return volumeInfos;
  }

  /**
   * @param secondaryObjectService The XMLService to set.
   */
  @Required
  public void setSecondaryObjectService(XMLService secondaryObjectService) {
    this.secondaryObjectService = secondaryObjectService;
  }

  public String getCurrentIssueDescription() {
    return currentIssueDescription;
  }
}
