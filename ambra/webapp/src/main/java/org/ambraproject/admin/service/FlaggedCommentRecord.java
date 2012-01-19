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

package org.ambraproject.admin.service;

import org.topazproject.ambra.models.Annotea;

public class FlaggedCommentRecord implements Comparable<FlaggedCommentRecord> {
  private String root;
  private String creator;
  private String created;
  private String reasonCode;
  private String flagComment;
  private String targetTitle;
  private String flagId;
  private String target;
  private String creatorid;
  private String targetType;
  private boolean isGeneralComment;
  private boolean broken;

  public FlaggedCommentRecord(String flagId, String target, String targetTitle, String flagComment,
                              String created, String creator, String creatorid, String root,
                              String reasonCode, String targetType, boolean isGeneralComment,
                              boolean broken) {
    this.target = target;
    this.targetTitle = targetTitle;
    this.root = root;
    this.creator = creator;
    this.created = created;
    this.flagComment = flagComment;
    this.reasonCode = reasonCode;
    this.flagId = flagId;
    this.creatorid = creatorid;
    this.targetType = targetType;
    this.isGeneralComment = isGeneralComment;
    this.broken = broken;
  }

  public String getTargetDisplayURL() {
    if (getIsAnnotation()) {
      return "viewAnnotation.action?annotationId=" + target;
    } else if (getIsRating()) {
      return "viewRating.action?ratingId=" + target;
    } else if (getIsReply()) {
      return "viewReply.action?replyId=" + target;
    }

    // not possible
    return "";
  }

  /**
   * Get the type, Class name, of the target.
   *
   * @return Type of the target.
   */
  public String getTargetType() {
    return targetType;
  }

  public boolean isTargetType(String testType) {
    return targetType.equals(testType);
  }

  public boolean isCorrection() {
    return (Annotea.WEB_TYPE_FORMAL_CORRECTION.equals(targetType) ||
        Annotea.WEB_TYPE_MINOR_CORRECTION.equals(targetType) ||
        Annotea.WEB_TYPE_RETRACTION.equals(targetType));
  }

  public boolean isFormalCorrection() {
    return (Annotea.WEB_TYPE_FORMAL_CORRECTION.equals(getTargetType()));
  }

  public boolean isMinorCorrection() {
    return (Annotea.WEB_TYPE_MINOR_CORRECTION.equals(getTargetType()));
  }

  public boolean isRetraction() {
    return (Annotea.WEB_TYPE_RETRACTION.equals(getTargetType()));
  }

  /**
   * Is this a Flag for an Annotation?  (Actually a Comment.)
   *
   * @return true if Flag for an Annotation, else false.
   */
  public boolean getIsAnnotation() {
    return Annotea.WEB_TYPE_COMMENT.equals(targetType) ||
        Annotea.WEB_TYPE_NOTE.equals(targetType) ||
        isCorrection();
  }

  /**
   * Is this a Flag for a Rating?
   *
   * @return true if Flag for a Rating, else false.
   */
  public boolean getIsRating() {

    return targetType.equals(Annotea.WEB_TYPE_RATING);

  }

  /**
   * Is this a Flag for a Reply?
   *
   * @return true if Flag for a Reply, else false.
   */
  public boolean getIsReply() {

    return targetType.equals(Annotea.WEB_TYPE_REPLY);

  }

  public String getRoot() {
    return (null == root) ? "" : root;
  }

  public String getCreator() {
    if (null != creator)
      return creator;
    else
      return "Cannot locate user-name";
  }

  public String getFlagComment() {
    return flagComment;
  }

  public String getCreated() {
    return created;
  }

  public String getReasonCode() {
    return reasonCode;
  }

  public String getTargetTitle() {
    return targetTitle;
  }

  public String getFlagId() {
    return flagId;
  }

  public String getTarget() {
    return target;
  }

  public String getCreatorid() {
    return creatorid;
  }

  public boolean getIsGeneralComment() {
    return isGeneralComment;
  }

  public void setIsGeneralComment(boolean generalComment) {
    this.isGeneralComment = generalComment;
  }

  public boolean isBroken() {
    return broken;
  }

  public void setBroken(boolean broken) {
    this.broken = broken;
  }

  public int compareTo (FlaggedCommentRecord o) {
    if (created == null) {
      if ((o == null) || (o.getCreated() == null)) {
        return 0;
      } else {
        return -1;
      }
    }
    if ((o == null) || (o.getCreated() == null)) {
      return 1;
    }
    return created.compareTo(o.getCreated());
  }
}
