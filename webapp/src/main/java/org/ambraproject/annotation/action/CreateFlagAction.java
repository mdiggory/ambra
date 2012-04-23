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

import org.ambraproject.Constants;
import org.ambraproject.models.FlagReasonCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.annotation.service.AnnotationService;

/**
 * Create a flag for a given annotation or reply
 */
@SuppressWarnings("serial")
public class CreateFlagAction extends BaseSessionAwareActionSupport {
  private Long target;
  private String comment;
  private Long flagId;
  private String reasonCode;
  protected AnnotationService annotationService;

  private static final Logger log = LoggerFactory.getLogger(CreateFlagAction.class);

  @Transactional(rollbackFor = { Throwable.class })
  @Override
  public String execute() throws Exception {
    if (isInvalid())
      return INPUT;

    try {
      flagId = annotationService.createFlag(
          getCurrentUser(),
          target,
          FlagReasonCode.fromString(reasonCode),
          comment);
    } catch (final Exception e) {
      log.error("Could not create flag for target: " + target, e);
      addActionError("Flag creation failed with error message: " + e.getMessage());
      return ERROR;
    }

    addActionMessage("Flag created with id:" + flagId);
    
    return SUCCESS;
  }

  private boolean isInvalid() {
    boolean invalid = false;

    if (StringUtils.isEmpty(comment)) {
      addFieldError("comment", "You must say something in your flag comment");
      invalid = true;
    } else {
      if (comment.length() > Constants.Length.COMMENT_BODY_MAX) {
        addFieldError("comment", "Your flag comment is " + comment.length() +
            " characters long, it can not be longer than " + Constants.Length.COMMENT_BODY_MAX + ".");
        invalid = true;
      }
    }

    return invalid;
  }

  /**
   * Set the comment of the annotation
   * @param comment comment
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }

  public void setTarget(Long target) {
    this.target = target;
  }

  /**
   * Set the reason code for this flag
   * @param reasonCode reasonCode
   */
  public void setReasonCode(final String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public Long getFlagId() {
    return flagId;
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }
}
