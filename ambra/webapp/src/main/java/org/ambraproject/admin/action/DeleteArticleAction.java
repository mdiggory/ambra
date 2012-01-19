/* $HeadURL:: http://ambraproject.org/svn/ambra/branches/ANH-Conversion/ambra/webapp/src#$
 * $Id: AdminTopAction.java 9514 2011-09-07 18:48:42Z josowski $
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
import org.ambraproject.admin.service.DocumentManagementService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.util.UriUtil;

public class DeleteArticleAction extends BaseAdminActionSupport {
  private static final Logger log = LoggerFactory.getLogger(DeleteArticleAction.class);

  private DocumentManagementService documentManagementService;

  // Fields used for delete
  private String article;
  private String action;

  /**
   * @return SUCCESS/ERROR
   * @throws Exception
   */
  @Override
  public String execute() throws Exception {

    // create a faux journal object for template
    if (!setCommonFields())
      return ERROR;

    return SUCCESS;
  }

  /**
   * Struts action method
   *
   * @return Struts result
   * @throws Exception when error occurs
   */
  public String deleteArticle() throws Exception {
    if (article != null) {
      article = article.trim();
    }

    try {
      UriUtil.validateUri(article, "Article Uri");

      documentManagementService.delete(article, getAuthId());

      addActionMessage("Successfully deleted article: " + article);

      try {
        documentManagementService.revertIngestedQueue(article);
      } catch (Exception ioe) {
        addActionError("Error cleaning up spool directories.");
        log.warn("Error cleaning up spool directories for '" + article +
            "' - manual cleanup required", ioe);
      }
    } catch (NoSuchArticleIdException e) {
      addActionError("Article: " + article + " does not exist.");
    } catch (IllegalArgumentException e) {
      addActionError("Invalid article URI: " + article);
      log.info("Invalid article URI: {}", article);
    } catch (Exception e) {
      addActionError("Failed to successfully delete article: " + article + ". <br>" + e);
      log.error("Failed to successfully delete article: " + article, e);
    }

    // create a faux journal object for template
    if (!setCommonFields())
      return ERROR;

    return SUCCESS;
  }

  /**
   * All the individual actions handled by adminTopAction need to provide a common set on information for the ftl to
   * display.
   *
   * @return true if there was no error when setting the fields
   */
  private boolean setCommonFields() {
    // create a faux journal object for template.  Ensures correct display of page.
    initJournal();

    return true;
  }

  /**
   * Sets the DocumentManagementService.
   *
   * @param documentManagementService The document management service
   */
  @Required
  public void setDocumentManagementService(DocumentManagementService documentManagementService) {
    this.documentManagementService = documentManagementService;
  }

  /**
   * Form field setter
   *
   * @param a article id
   */
  public void setArticle(String a) {
    article = a;
  }

  /**
   * Form field setter for action The action parameter is the value of the button pressed on the front End, and
   * determines what actions to take
   *
   * @param s the current action
   */
  public void setAction(String s) {
    action = s;
  }

  /**
   * Form field setter
   *
   * @return the current action
   */
  public String getAction() {
    return action;
  }
}