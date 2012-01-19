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
package org.ambraproject.annotation.service;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.topazproject.ambra.models.Annotea;
import org.topazproject.ambra.models.ByteArrayBlob;
import org.ambraproject.util.TextUtils;

import java.util.Date;


/**
 * Base class for Annotation and reply.
 *
 * For now it does not bring together all the common attributes as I still prefer delegation for
 * now.  Further uses of these classes on the web layer should clarify the requirements and drive
 * any changes if required.
 *
 * @param <T> the Annotea sub-class being delegated to.
 */
public abstract class BaseAnnotation<T extends Annotea<? extends ByteArrayBlob>> {
  /** An integer constant to indicate a unique value for the  */
  private static final int TRUNCATED_COMMENT_LENGTH = 256;

  protected final T annotea;
  protected final String originalBodyContent;
  protected final String creatorName;

  /**
   * @return the escaped comment.
   */
  public String getComment() {
    return escapeText(getOriginalBodyContent());
  }

  /**
   * Returns a value identical to the {@link #getComment()}
   * method in this class.  This new method has been added because
   * the subclass <strong>WebAnnotation</strong> contains the method
   * {@link WebAnnotation#isComment()}.  The Javascript function
   * <strong>_toRetractionHtmlElement</strong> (in the corrections.js
   * file) mistakenly calls {@link WebAnnotation#isComment()} instead
   * of calling BaseAnnotation.getComment() to display the comment.
   *
   * @return the escaped comment.  Identical to the getComment() method.
   */
  public String getEscapedComment() {
    return getComment();
  }

  /**
   * @return the url linked and escaped comment.
   */
  public String getCommentWithUrlLinking() {
    return TextUtils.hyperlinkEnclosedWithPTags(getComment(),25);
  }

  /**
   * Get the body of the annotation with any discovered inline URLs defined as HTML links 
   * @return the formatted string
   */
  public String getCommentWithUrlLinkingNoPTags() {
    return getCommentWithUrlLinkingNoPTags(false);
  }

  /**
   * Get the body of the annotation with any discovered inline URLs defined as HTML links
   * @param truncate if true limit the length of the returned string to the TRUNCATED_COMMENT_LENGTH
   * constant.
   * @return the formatted string
   */
  public String getCommentWithUrlLinkingNoPTags(boolean truncate) {
    if(truncate) {
      return TextUtils.hyperlink(truncateText(getComment()),25);
    } else {
      return TextUtils.hyperlink(getComment(),25);
    }
  }

  /**
   * @return the url linked and escaped comment with a limit of 256 characters.
   */
  public String getEscapedTruncatedComment() {
    String comment = truncateText(getComment());

    return TextUtils.hyperlinkEnclosedWithPTags(comment, 25);
  }

  protected String truncateText(String text)
  {
    if (StringUtils.isBlank(text)) {
      return text;
    }

    if (text.length() > TRUNCATED_COMMENT_LENGTH) {
      final String abrsfx = "...";
      final int abrsfxlen = 3;
      // attempt to truncate on a word boundary
      int index = TRUNCATED_COMMENT_LENGTH - 1;

      while (!Character.isWhitespace(text.charAt(index)) ||
             index > (TRUNCATED_COMMENT_LENGTH - abrsfxlen - 1)) {
        if (--index == 0)
          break;
      }

      if (index == 0)
        index = TRUNCATED_COMMENT_LENGTH - abrsfxlen - 1;

      text = text.substring(0, index) + abrsfx;
      assert text.length() <= TRUNCATED_COMMENT_LENGTH;
    }

    return text;
  }

  /**
   * Escape text so as to avoid any java scripting maliciousness when rendering it on a web page
   * @param text text
   * @return the escaped text
   */
  protected String escapeText(final String text) {
    return TextUtils.escapeHtml(text);
  }

  /**
   * Get created date.
   * @return created as java.util.Date.
   */
  @JSON(serialize = false)
  public Date getCreatedAsDate() {
    return annotea.getCreated();
  }

  /**
   * Get created date.
   * @return created as java.util.Date.
   */
  public long getCreatedAsMillis() {
    Date d = getCreatedAsDate();
    return (d != null) ? d.getTime() : 0;
  }

  /**
   * Get created.
   * @return created as String.
   */
  public String getCreated() {
    return annotea.getCreatedAsString();
  }

  /**
   * Get creator.
   * @return creator as String.
   */
  public String getCreator() {
    return annotea.getCreator();
  }

  /**
   * @return Returns the creatorName.
   * @throws NullPointerException if the creator name was not loaded
   */
  public String getCreatorName() throws NullPointerException {
    if (creatorName == null)
      throw new NullPointerException("Creator name is not looked-up");
    return creatorName;
  }

  /**
   * Get id.
   * @return id as String.
   */
  public String getId() {
    return annotea.getId().toString();
  }

  /**
   * Get mediator.
   * @return mediator as String.
   */
  public String getMediator() {
    return annotea.getMediator();
  }

  /**
   * Get state.
   * @return state as int.
   */
  public int getState() {
    return annotea.getState();
  }

  /**
   * Get annotation type.
   * @return annotation type as String.
   */
  public String getType() {
    return annotea.getType();
  }

  public String getOriginalBodyContent() throws NullPointerException {
    if (originalBodyContent == null)
      throw new NullPointerException("Body blob is not loaded.");
    return originalBodyContent;
  }

  /**
   * Return Escaped text of CIStatement limited to TRUNCATED_COMMENT_LENGTH
   * @return escaped and truncated CIStatement
   */
  public String getTruncatedCIStatement() {
    return this.truncateText(getCIStatement());
  }

  /**
   * Creates a BaseAnnotation object.
   *
   * @param annotea the annotation
   * @param creatorName the display name of the creator (must be non-null if the view requires it)
   * @param originalBodyContent body as text (must be non-null if the view requires it)
   */
  public BaseAnnotation(T annotea, String creatorName, String originalBodyContent) {
    this.annotea = annotea;
    this.creatorName = creatorName;
    this.originalBodyContent = originalBodyContent;
  }

  public abstract String getCommentTitle();
  public abstract String getCIStatement();
}
