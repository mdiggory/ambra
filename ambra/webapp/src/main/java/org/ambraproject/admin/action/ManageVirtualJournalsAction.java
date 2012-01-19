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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Volume;
import org.ambraproject.util.UriUtil;

import java.net.URI;
import java.util.List;

/**
 * Volumes are associated with some journals and hubs. A volume is an aggregation of
 * of issues. Issue are aggregations of articles.
 *
 */
@SuppressWarnings("serial")
public class ManageVirtualJournalsAction extends BaseAdminActionSupport {

  // Past in as parameters
  private String   command;
  private String   journalToModify;
  private URI      curIssueURI;
  private URI      volumeURI;
  private String[] volsToDelete;
  private String   displayName;

  //Used by template
  private List<Volume> volumes;

  private static final Logger log = LoggerFactory.getLogger(ManageVirtualJournalsAction.class);

  /**
  * Enumeration used to dispatch commands within the action.
  */
  public enum MVJ_COMMANDS {
    UPDATE_ISSUE,
    CREATE_VOLUME,
    REMOVE_VOLUMES,
    INVALID;

    /**
     * Convert a string specifying a command to its
     * enumerated equivalent.
     *
     * @param command  string value to convert.
     * @return        enumerated equivalent
     */
    public static MVJ_COMMANDS toCommand(String command) {
      MVJ_COMMANDS a;
      try {
        a = valueOf(command);
      } catch (Exception e) {
        // It's ok just return invalid.
        a = INVALID;
      }
      return a;
    }
  }

  /**
   * Manage Journals.  Display Journals and processes all add/deletes.
   */
  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public String execute() throws Exception  {

    switch( MVJ_COMMANDS.toCommand(command)) {
      case UPDATE_ISSUE:
        update_Issue();
        break;

      case CREATE_VOLUME:
        create_Volume();
        break;

      case REMOVE_VOLUMES:
        removeVolumes();
        break;

       case INVALID:
         repopulate();
         break;
    }
    return SUCCESS;
  }

  private void update_Issue() {
    if (curIssueURI != null) {
      try {
        adminService.setJrnlIssueURI(getCurrentJournal(), curIssueURI);
        addActionMessage("Current Issue (URI) set to: " + curIssueURI);
       } catch (Exception e) {
        addActionMessage("Current Issue not updated due to the following error.");
        addActionMessage(e.getMessage());
      }
    } else {
      addActionMessage("Invalid Current Issue (URI) ");
    }
    repopulate();
  }

  private void create_Volume() {
    if (volumeURI != null) {
      try {
        // Create and add to journal
        Volume v = adminService.createVolume(getCurrentJournal(), volumeURI, displayName, "" );
        if (v != null) {
          addActionMessage("Created Volume: " + v.getId());
        } else {
          addActionMessage("Duplicate Volume URI: " + volumeURI);
        }
      } catch (Exception e) {
        addActionMessage("Volume not created due to the following error.");
        addActionMessage(e.getMessage());
      }
    } else {
      addActionMessage("Invalid Volume URI" );
    }
    repopulate();
  }

  private void removeVolumes() {
    try {
      if (volsToDelete.length > 0) {
          // volsToDelete was supplied by the system so they should be correct
          addActionMessage("Removing the Following Volume URIs:");
          for(String vol : volsToDelete) {
            adminService.deleteVolume(getCurrentJournal(), URI.create(vol));
            addActionMessage("Volume: " + vol );
          }
      }
    } catch (Exception e){
      addActionMessage("Volume remove failed due to the following error.");
      addActionMessage(e.getMessage());
    }
    repopulate();
  }

  private void repopulate() {
    volumes = adminService.getVolumes(getCurrentJournal());
    initJournal();
  }

  /**
   * Gets a list of Volume objects associated with the journal.
   *
   * @return list of Volume objects associated with the journals.
   */
  public List<Volume> getVolumes() {
    return volumes;
  }


  /**
   * Set Journal to modify.
   *
   * @param journalToModify Journal to modify.
   */
  public void setJournalToModify(String journalToModify) {
    this.journalToModify = journalToModify.trim();
  }

  /**
   * Set volume URI.
   *
   * @param vol the volume URI.
   */
  public void setVolumeURI(String vol) {
    try {
      this.volumeURI = UriUtil.validateUri(vol.trim(), "Volume Uri");
    } catch (Exception e) {
      this.volumeURI = null;
      if (log.isDebugEnabled())
        log.debug("setVolume URI conversion failed.");
    }
  }

  /**
   * Get current issue.
   *
   * @return current issue.
   */
  public String getCurIssue() {
    return curIssueURI.toString();
  }

  /**
   * Set current issue.
   *
   * @param currentIssueURI the current issue for this journal.
   */
  public void setCurrentIssueURI(String currentIssueURI) {
    try {
      this.curIssueURI = UriUtil.validateUri(currentIssueURI.trim(), "Issue Uri");
    } catch (Exception e) {
      this.curIssueURI = null;
      if (log.isDebugEnabled())
        log.debug("setIssue URI conversion failed.");
    }
  }

   /**
   * Set display name for a voulume.
   *
   * @param dsplyName the display of the volume.
   */
  public void setDisplayName(String dsplyName) {
    this.displayName = dsplyName.trim();
  }

  /**
   * Set volumes to delete.
   *
   * @param vols .
   */
  public void setVolsToDelete(String[] vols) {
    this.volsToDelete = vols;
  }

  /**
   * Sets the command to execute.
   *
   * @param  command the command to execute for this action.
   */
  @Required
  public void setCommand(String command) {
    this.command = command;                                                                 
  }

}
