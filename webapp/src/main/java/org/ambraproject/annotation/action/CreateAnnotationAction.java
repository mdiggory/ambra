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
package org.ambraproject.annotation.action;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import org.ambraproject.Constants;
import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.annotation.Context;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.cache.Cache;
import org.ambraproject.util.ProfanityCheckingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Action to create an annotation. It also does profanity validation on the user content.
 */
@SuppressWarnings("serial")
public class CreateAnnotationAction extends BaseSessionAwareActionSupport {
  private String target;
  private String commentTitle;
  private String ciStatement;
  private String comment;
  private String mimeType = "text/plain";
  private String annotationId;
  private boolean isPublic = false;
  private boolean isCompetingInterest = false;
  private String noteType;
  private String startPath;
  private int startOffset;
  private String endPath;
  private int endOffset;
  private String supercedes;

  private ProfanityCheckingService profanityCheckingService;
  protected AnnotationService annotationService;
  private Cache articleHtmlCache;
  private static final Logger log = LoggerFactory.getLogger(CreateAnnotationAction.class);

  /**
   * {@inheritDoc}
   * Also does some profanity check for commentTitle and comment before creating the annotation.
   */
  @Override
  @Transactional(rollbackFor = {Throwable.class})
  public String execute() throws Exception {
    if (isInvalid())
      return INPUT;

    try {
      final List<String> profaneWordsInTitle = profanityCheckingService.validate(commentTitle);
      final List<String> profaneWordsInBody = profanityCheckingService.validate(comment);
      final List<String> profaneWordsInCIStatement = profanityCheckingService.validate(ciStatement);

      if (profaneWordsInBody.isEmpty() && profaneWordsInTitle.isEmpty() &&
          profaneWordsInCIStatement.isEmpty()) {
        boolean flagAsCorrection = "correction".equals(noteType);
        annotationId = annotationService.createComment(
            getCurrentUser(),
            target,
            commentTitle,
            comment,
            ciStatement,
            new Context(startPath, startOffset, endPath, endOffset, target),
            flagAsCorrection).toString();

        articleHtmlCache.remove(target);

      } else {
        addProfaneMessages(profaneWordsInBody, "comment", "comment");
        addProfaneMessages(profaneWordsInTitle, "commentTitle", "title");
        addProfaneMessages(profaneWordsInCIStatement, "ciStatement", "statement");
        return INPUT;
      }
    } catch (final Exception e) {
      log.error("Could not create annotation", e);
      addActionError("Annotation creation failed with error message: " + e.getMessage());
      return ERROR;
    }
    addActionMessage("Annotation created with id:" + annotationId);
    return SUCCESS;
  }

  private boolean isInvalid() {
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

    if (this.isCompetingInterest) {
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

  /**
   * Set the target that it annotates.
   *
   * @param target target
   */
  public void setTarget(final String target) {
    this.target = target;
  }

  /**
   * Set the commentTitle of the annotation
   *
   * @param commentTitle commentTitle
   */
  public void setCommentTitle(final String commentTitle) {
    this.commentTitle = commentTitle;
  }

  /**
   * Set the comment of the annotation
   *
   * @param comment comment
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }

  /**
   * Set the competing interest statement of the annotation
   *
   * @param ciStatement Statement
   */
  public void setCiStatement(final String ciStatement) {
    this.ciStatement = ciStatement;
  }

  /**
   * @param isPublic set the visibility of annotation
   */
  public void setIsPublic(final boolean isPublic) {
    this.isPublic = isPublic;
  }

  /**
   * Set the mimeType of the annotation
   *
   * @param mimeType mimeType
   */
  public void setMimeType(final String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Get the id of the newly created annotation
   *
   * @return annotation id
   */
  public String getAnnotationId() {
    return annotationId;
  }

  /**
   * @return the target
   */
  @RequiredStringValidator(message = "You must specify the target that this annotation is applied on")
  public String getTarget() {
    return target;
  }

  /**
   * @return the commentTitle
   */
  public String getCommentTitle() {
    return commentTitle;
  }

  /**
   * @return the annotation content
   */
  public String getComment() {
    return comment;
  }

  /**
   * @return the mime type
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Set the profanityCheckingService
   *
   * @param profanityCheckingService profanityCheckingService
   */
  @Required
  public void setProfanityCheckingService(final ProfanityCheckingService profanityCheckingService) {
    this.profanityCheckingService = profanityCheckingService;
  }

  /**
   * @param isCompetingInterest does this annotation have competing interests?
   */
  public void setIsCompetingInterest(final boolean isCompetingInterest) {
    this.isCompetingInterest = isCompetingInterest;
  }

  /**
   * @return whether the annotation is public
   */
  public boolean getIsPublic() {
    return isPublic;
  }

  /**
   * @param noteType the note type
   */
  public void setNoteType(final String noteType) {
    this.noteType = noteType;
  }

  /**
   * @return the note type
   */
  public String getNoteType() {
    return noteType;
  }

  /**
   * @return the end point offset
   */
  public int getEndOffset() {
    return endOffset;
  }

  /**
   * @param endOffset set the end point offset
   */
  public void setEndOffset(final int endOffset) {
    this.endOffset = endOffset;
  }

  /**
   * @return return the end point path
   */
  //@RequiredStringValidator(message="You must specify a value")
  public String getEndPath() {
    return endPath;
  }

  /**
   * @param endPath set the end point path
   */
  public void setEndPath(final String endPath) {
    this.endPath = endPath;
  }

  /**
   * @return the start point offset
   */
  public int getStartOffset() {
    return startOffset;
  }

  /**
   * @param startOffset set the start point offset
   */
  public void setStartOffset(final int startOffset) {
    this.startOffset = startOffset;
  }

  /**
   * @return the start point path
   */
  //@RequiredStringValidator(message="You must specify a value")
  public String getStartPath() {
    return startPath;
  }

  /**
   * @param startPath set start point path
   */
  public void setStartPath(final String startPath) {
    this.startPath = startPath;
  }

  /**
   * @return the older annotation that it supersedes
   */
  public String getSupercedes() {
    return supercedes;
  }

  /**
   * @param supercedes the older annotation that it supersedes
   */
  public void setSupercedes(final String supercedes) {
    this.supercedes = supercedes;
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  /**
   * @param articleHtmlCache The Article(transformed) cache to use
   */
  @Required
  public void setArticleHtmlCache(Cache articleHtmlCache) {
    this.articleHtmlCache = articleHtmlCache;
  }
}
