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
package org.ambraproject.annotation.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.AnnotationService;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action to delete a flag
 */
@SuppressWarnings("serial")
public class DeleteFlagAction extends BaseActionSupport {
  private String flagId;
  protected AnnotationService annotationService;

  private static final Logger log = LoggerFactory.getLogger(DeleteFlagAction.class);

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public String execute() throws Exception {
    try {
      annotationService.deleteAnnotation(flagId, getAuthId());
    } catch (final Exception e) {
      log.error("Could not delete flag: " + flagId, e);
      addActionError("Flag deletion failed with error message: " + e.getMessage());
      return ERROR;
    }
    addActionMessage("Flag deleted with id:" + flagId);
    return SUCCESS;
  }

  /**
   * Set the flag Id.
   * @param flagId flagId
   */
  public void setFlagId(final String flagId) {
    this.flagId = flagId;
  }

  /**
   * @return the flag id
   */
  @RequiredStringValidator(message="You must specify the id of the flag that you want to delete")
  public String getFlagId() {
    return flagId;
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }
}
