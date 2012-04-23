/* $HeadURL::                                                                            $
 * $Id:CreateReplyAction.java 722 2006-10-02 16:42:45Z viru $
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
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.util.ProfanityCheckingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Action for creating a reply.
 */
public class CreateReplyAction extends BaseSessionAwareActionSupport {
  private Long replyId;
  private Long inReplyTo;
  private String commentTitle;
  private String comment;
  private String ciStatement;
  private boolean isCompetingInterest = false;
  
  private ProfanityCheckingService profanityCheckingService;
  private AnnotationService annotationService;

  private static final Logger log = LoggerFactory.getLogger(CreateReplyAction.class);

  @Transactional(rollbackFor = { Throwable.class })
  @Override
  public String execute() throws Exception {
    if (isInvalid())
      return INPUT;
    
    try {
      final List<String> profaneWordsInTitle = profanityCheckingService.validate(commentTitle);
      final List<String> profaneWordsInBody = profanityCheckingService.validate(comment);
      final List<String> profaneWordsCiStatement = profanityCheckingService.validate(ciStatement);

      if (profaneWordsInBody.isEmpty() && profaneWordsInTitle.isEmpty() &&
          profaneWordsCiStatement.isEmpty()) {
        //create the reply
        replyId = annotationService.createReply(getCurrentUser(), inReplyTo, commentTitle, comment, ciStatement);

      } else {
        addProfaneMessages(profaneWordsInBody, "comment", "comment");
        addProfaneMessages(profaneWordsInTitle, "commentTitle", "title");
        addProfaneMessages(profaneWordsCiStatement, "ciStatement", "statement");
        return INPUT;
      }
    } catch (Exception e) {
      log.error("Could not create reply to: " + inReplyTo, e);
      addActionError("Reply creation failed with error message: " + e.getMessage());
      return ERROR;
    }
    addActionMessage("Reply created with id:" + replyId);

    return SUCCESS;
  }

  private boolean isInvalid() {
   /**
    * This is a little odd that part of validation happens here and
    * part of it occurs as validators on the object properties
    * TODO: Revisit and recombine?  Or perhaps author a generic validator that can handle
    * the logic defined below
    **/
    boolean invalid = false;

    if (StringUtils.isEmpty(commentTitle)) {
      addFieldError("commentTitle", "A title is required.");
      invalid = true;
    } else {
      if (commentTitle.length() > Constants.Length.COMMENT_TITLE_MAX) {
        addFieldError("commentTitle", "Your title is " + commentTitle.length() +
            " characters long, it can not be longer than " + Constants.Length.COMMENT_TITLE_MAX + ".");
        invalid = true;
      }
    }

    if (StringUtils.isEmpty(comment)) {
      addFieldError("comment", "You must say something in your comment");
      invalid = true;
    } else {
      if (comment.length() > Constants.Length.COMMENT_BODY_MAX) {
        addFieldError("comment", "Your comment is " + comment.length() +
            " characters long, it can not be longer than " + Constants.Length.COMMENT_BODY_MAX + ".");
        invalid = true;
      }
    }

    if(this.isCompetingInterest) {
      if (StringUtils.isEmpty(ciStatement)) {
        addFieldError("statement", "You must say something in your competing interest statement");
        invalid = true;
      } else {
        if (ciStatement.length() > Constants.Length.CI_STATEMENT_MAX) {
          addFieldError("statement", "Your competing interest statement is " +
              ciStatement.length() + " characters long, it can not be longer than " + 
              Constants.Length.CI_STATEMENT_MAX + ".");
          invalid = true;
        }
      }
    }

    return invalid;
  }

  public Long getReplyId() {
    return replyId;
  }

  /**
   * Set the competing interest statement of the annotation
   * @param ciStatement Statement
   */
  public void setCiStatement(final String ciStatement) {
    this.ciStatement = ciStatement;
  }

  /**
   * Set wether this reply has a competing interest statement or not
   * @param isCompetingInterest does this annotation have competing interests?
   */
  public void setIsCompetingInterest(final boolean isCompetingInterest) {
    this.isCompetingInterest = isCompetingInterest;
  }

  public void setInReplyTo(final Long inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  public void setCommentTitle(final String commentTitle) {
    this.commentTitle = commentTitle;
  }
  public void setComment(final String comment) {
    this.comment = comment;
  }

  @Required
  public void setProfanityCheckingService(final ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  @Required
  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }
}
