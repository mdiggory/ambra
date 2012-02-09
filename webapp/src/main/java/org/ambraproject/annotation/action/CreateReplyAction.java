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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.annotation.service.ReplyService;
import org.ambraproject.util.ProfanityCheckingService;

import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.AnnotationBlob;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action for creating a reply.
 */
@SuppressWarnings("serial")
public class CreateReplyAction extends BaseSessionAwareActionSupport {
  private String replyId;
  private String root;
  private String inReplyTo;
  private String commentTitle;
  private String mimeType = "text/plain";
  private String comment;
  private String ciStatement;
  private boolean isCompetingInterest = false;
  
  private ProfanityCheckingService profanityCheckingService;
  protected ReplyService replyService;

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
        replyId = replyService.createReply(root, inReplyTo, commentTitle, mimeType, comment,
            ciStatement, getCurrentUser());
      } else {
        addProfaneMessages(profaneWordsInBody, "comment", "comment");
        addProfaneMessages(profaneWordsInTitle, "commentTitle", "title");
        addProfaneMessages(profaneWordsCiStatement, "ciStatement", "statement");
        return INPUT;
      }
    } catch (Exception e) {
      log.error("Could not create reply to root: " + root + " and inReplyTo: " + inReplyTo, e);
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
      if (commentTitle.length() > Annotation.MAX_TITLE_LENGTH) {
        addFieldError("commentTitle", "Your title is " + commentTitle.length() +
            " characters long, it can not be longer than " + Annotation.MAX_TITLE_LENGTH + ".");
        invalid = true;
      }
    }

    if (StringUtils.isEmpty(comment)) {
      addFieldError("comment", "You must say something in your comment");
      invalid = true;
    } else {
      if (comment.length() > AnnotationBlob.MAX_BODY_LENGTH) {
        addFieldError("comment", "Your comment is " + comment.length() +
            " characters long, it can not be longer than " + AnnotationBlob.MAX_BODY_LENGTH + ".");
        invalid = true;
      }
    }

    if(this.isCompetingInterest) {
      if (StringUtils.isEmpty(ciStatement)) {
        addFieldError("statement", "You must say something in your competing interest statement");
        invalid = true;
      } else {
        if (ciStatement.length() > AnnotationBlob.MAX_CISTATEMENT_LENGTH) {
          addFieldError("statement", "Your competing interest statement is " +
              ciStatement.length() + " characters long, it can not be longer than " + 
              AnnotationBlob.MAX_CISTATEMENT_LENGTH + ".");
          invalid = true;
        }
      }
    }

    return invalid;
  }

  public String getReplyId() {
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

  public void setRoot(final String root) {
    this.root = root;
  }

  public void setInReplyTo(final String inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  public void setCommentTitle(final String commentTitle) {
    this.commentTitle = commentTitle;
  }

  public void setMimeType(final String mimeType) {
    this.mimeType = mimeType;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  @RequiredStringValidator(message = "The annotation id to which it applies is required")
  public String getRoot() {
    return root;
  }

  @RequiredStringValidator(message = "The annotation/reply id to which it applies is required")
  public String getInReplyTo() {
    return inReplyTo;
  }

  @RequiredStringValidator(message = "A title is required")
  public String getCommentTitle() {
  return commentTitle;
  }

  @RequiredStringValidator(message = "A reply is required")
  public String getComment() {
  return comment;
  }

  public void setProfanityCheckingService(final ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  @Required
  public void setReplyService(final ReplyService replyService) {
    this.replyService = replyService;
  }
}
