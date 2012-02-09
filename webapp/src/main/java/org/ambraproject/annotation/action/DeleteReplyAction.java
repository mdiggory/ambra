/* $HeadURL::                                                                            $
 * $Id:DeleteReplyAction.java 722 2006-10-02 16:42:45Z viru $
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
package org.ambraproject.annotation.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.ReplyService;

/**
 * Action class to delete a given reply.
 */
@SuppressWarnings("serial")
public class DeleteReplyAction extends BaseActionSupport {
  private String id;
  private String root;
  private String inReplyTo;
  protected ReplyService replyService;

  private static final Logger log = LoggerFactory.getLogger(DeleteReplyAction.class);

  /**
   * Delete a reply given a reply id
   * @return operation return code
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String deleteReplyWithId() {
    try {
      replyService.deleteReplies(id, getAuthId());
    } catch (final Exception e) {
      log.error("Could not delete reply: " + id, e);
      addActionError("Reply deletion failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Delete a reply given a root and inReplyTo
   * @return operation return code
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String deleteReplyWithRootAndReplyTo() {
    try {
      replyService.deleteReplies(root, inReplyTo);
    } catch (final Exception e) {
      log.error("Could not delete reply with root: " + root + " replyTo: " + inReplyTo, e);
      addActionError("Reply deletion failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setRoot(final String root) {
    this.root = root;
  }

  public void setInReplyTo(final String inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  public String getId() {
    return id;
  }

  public String getRoot() {
    return root;
  }

  public String getInReplyTo() {
    return inReplyTo;
  }

  @Required
  public void setReplyService(final ReplyService replyService) {
    this.replyService = replyService;
  }
}
