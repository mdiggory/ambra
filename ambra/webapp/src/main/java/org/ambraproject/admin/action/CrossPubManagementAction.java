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

package org.ambraproject.admin.action;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

public class CrossPubManagementAction extends BaseAdminActionSupport {
  // Fields set by templates
  private String       command;
  private String       articlesToAdd;
  private String[]     articlesToRemove;

  private static final Logger log = LoggerFactory.getLogger(CrossPubManagementAction.class);
  /**
   *
   */
  private enum XP_COMMANDS {
    ADD_ARTICLES,
    REMOVE_ARTICLES,
    INVALID;

    /**
     * Convert a string specifying an action to its
     * enumerated equivalent.
     *
     * @param action  string value to convert.
     * @return        enumerated equivalent
     */
    public static XP_COMMANDS toCommand(String action) {
      XP_COMMANDS a;
      try {
        a = valueOf(action);
      } catch (Exception e) {
        // It's ok just return invalid.
        a = INVALID;
      }
      return a;
    }
  }
  /**
   * Main entry point for Cross Publication management action.
   *
   */
  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public String execute() throws Exception  {

    switch( XP_COMMANDS.toCommand(command)) {
      case ADD_ARTICLES: {
        List<URI> articles = adminService.parseCSV(articlesToAdd);
        for(URI articleUri : articles) {
          if (adminService.validURI(articleUri)) {
            adminService.addXPubArticle(getCurrentJournal(), articleUri);
            addActionMessage("Article: " + articleUri + " cross published in journal.");
          } else {
            addActionMessage("Unable to add: " + articleUri);
          }
        }
        break;
      }
      case REMOVE_ARTICLES: {
        for(String articleId : articlesToRemove) {
          URI articleUri = URI.create(articleId);
          adminService.removeXPubArticle(getCurrentJournal(), articleUri);
          addActionMessage("Cross published article: " + articleUri + " removed.");
        }
        break;
      }
      case INVALID:
        break;
    }
    // create a faux journal object for template
    initJournal();
    return SUCCESS;
  }

  /**
   * Set Articles to add.
   *
   * @param articlesToAdd a comma separated list of articles to add.
   */
  public void setArticlesToAdd(String articlesToAdd) {
    this.articlesToAdd = articlesToAdd;
  }

  /**
   * Set Articles to delete.
   *
   * @param articlesToRemove Array of articles to delete.
   */
  public void setArticlesToRemove(String[] articlesToRemove) {
    this.articlesToRemove = articlesToRemove;
  }

  /**
   * Sets the Action to execute.
   *
   * @param command the command to execute.
   */
  @Required
  public void setCommand(String command) {
    this.command = command;
  }

}
