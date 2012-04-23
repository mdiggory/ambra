/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.views;

import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.UserProfile;
import org.ambraproject.util.TextUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * Immutable view wrapper around annotations
 *
 * @author Alex Kudlick 3/12/12
 */
public class AnnotationView {
  private static final int TRUNCATED_COMMENT_LENGTH = 256;
  private static final AnnotationView[] EMPTY_ARRAY = new AnnotationView[0];
  public static final Comparator<Annotation> REPLY_COMPARATOR = new Comparator<Annotation>() {
    @Override
    public int compare(Annotation reply1, Annotation reply2) {
      return reply1.getCreated().compareTo(reply2.getCreated());
    }
  };
  private final String originalTitle;
  private final String title;
  private final String body;
  private final String originalBody;
  private final String truncatedBody;
  private final String bodyWithUrlLinkingNoPTags;
  private final String truncatedBodyWithUrlLinkingNoPTags;
  private final String competingInterestStatement;
  private final String truncatedCompetingInterestStatement;
  private final String annotationUri;
  private final String xpath;
  private final Long ID;
  private final Long creatorID;
  private final String creatorDisplayName;
  private final String creatorFormattedName;
  private final Long articleID;
  private final Long parentID;
  private final String articleDoi;
  private final String articleTitle;
  private final Date created;
  private final String createdFormatted;
  private final AnnotationType type;
  private final AnnotationView[] replies;
  private final Date lastReplyDate;
  private final int totalNumReplies;
  private final AnnotationCitationView citation;

  /**
   * Create a new AnnotationView
   *
   * @param annotation    the annotation being wrapped
   * @param articleTitle  the title of the article that is annotated
   * @param articleDoi    the doi of the article that is annotated
   * @param fullReplyTree a full map of all child replies, from Id -> all replies to the annotation with that id.
   *                      Allowed to be null or empty
   */
  public AnnotationView(Annotation annotation, String articleDoi,
                        String articleTitle,
                        @Nullable Map<Long, List<Annotation>> fullReplyTree) {
    this.ID = annotation.getID();
    this.articleDoi = articleDoi;
    this.articleTitle = articleTitle;
    if (annotation.getAnnotationUri() != null) {
      this.annotationUri = annotation.getAnnotationUri().replaceFirst("info:doi/", "");
    } else {
      this.annotationUri = null;
    }
    this.originalTitle = annotation.getTitle();
    String escapedTitle = TextUtils.escapeHtml(annotation.getTitle());
    switch (annotation.getType()) {
      case FORMAL_CORRECTION:
        this.title = "Formal Correction: " + escapedTitle;
        break;
      case MINOR_CORRECTION:
        this.title = "Minor Correction: " + escapedTitle;
        break;
      case RETRACTION:
        this.title = "Retraction: " + escapedTitle;
        break;
      default:
        this.title = escapedTitle;
    }

    if (annotation.getBody() == null) {
      this.originalBody = "";
      this.body = "";
      this.truncatedBody = "";
      this.bodyWithUrlLinkingNoPTags = "";
      this.truncatedBodyWithUrlLinkingNoPTags = "";
    } else {
      this.originalBody = annotation.getBody();
      this.body = TextUtils.hyperlinkEnclosedWithPTags(TextUtils.escapeHtml(annotation.getBody()), 25);
      this.truncatedBody = TextUtils.hyperlinkEnclosedWithPTags(truncateText(TextUtils.escapeHtml(annotation.getBody())), 25);
      this.bodyWithUrlLinkingNoPTags = TextUtils.hyperlink(TextUtils.escapeHtml(annotation.getBody()), 25);
      this.truncatedBodyWithUrlLinkingNoPTags = TextUtils.hyperlink(truncateText(TextUtils.escapeHtml(annotation.getBody())), 25);
    }

    if (annotation.getCompetingInterestBody() == null) {
      this.competingInterestStatement = "";
      this.truncatedCompetingInterestStatement = "";
    } else {
      this.competingInterestStatement = TextUtils.escapeHtml(annotation.getCompetingInterestBody());
      this.truncatedCompetingInterestStatement = truncateText(TextUtils.escapeHtml(annotation.getCompetingInterestBody()));
    }

    this.xpath = annotation.getXpath();
    this.creatorID = annotation.getCreator().getID();
    this.creatorDisplayName = annotation.getCreator().getDisplayName();
    this.creatorFormattedName = createFormattedName(annotation.getCreator());
    this.articleID = annotation.getArticleID();
    this.parentID = annotation.getParentID();
    this.type = annotation.getType();

    if (annotation.getAnnotationCitation() == null) {
      this.citation = null;
    } else {
      this.citation = new AnnotationCitationView(annotation.getAnnotationCitation());
    }

    //Defensive copy
    Calendar date = Calendar.getInstance();
    date.setTime(annotation.getCreated());
    this.created = date.getTime();

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    fmt.setTimeZone(new SimpleTimeZone(0, "UTC"));
    this.createdFormatted = fmt.format(created);

    if (fullReplyTree == null) {
      this.replies = EMPTY_ARRAY;
      this.lastReplyDate = this.created;
      this.totalNumReplies = 0;
    } else {
      List<Annotation> repliesToThis = fullReplyTree.get(annotation.getID());
      if (repliesToThis == null || repliesToThis.isEmpty()) {
        this.replies = EMPTY_ARRAY;
      } else {
        //sort the replies by date so our reply list will be ordered, earliest first
        Collections.sort(repliesToThis, REPLY_COMPARATOR);
        this.replies = new AnnotationView[repliesToThis.size()];
        for (int i = 0; i < repliesToThis.size(); i++) {
          this.replies[i] = new AnnotationView(repliesToThis.get(i), articleDoi, articleTitle, fullReplyTree);
        }
      }
      //now populate lastReplyDate and totalNumReplies
      this.totalNumReplies = calculateTotalNumReplies();
      this.lastReplyDate = calculateMostRecentReplyDate();
    }
  }

  private int calculateTotalNumReplies() {
    int replyCount = 0;
    replyCount += this.replies.length;
    for (AnnotationView reply : this.replies) {
      replyCount += reply.calculateTotalNumReplies();
    }
    return replyCount;
  }

  //since all the replies have been sorted by the time we call this method, we can just check the last entry in the reply array
  private Date calculateMostRecentReplyDate() {
    Date replyDate = this.created;
    if (this.replies.length > 0) {
      replyDate = this.replies[this.replies.length - 1].calculateMostRecentReplyDate();
    }
    return replyDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AnnotationView that = (AnnotationView) o;

    if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
    if (annotationUri != null ? !annotationUri.equals(that.annotationUri) : that.annotationUri != null) return false;
    if (articleDoi != null ? !articleDoi.equals(that.articleDoi) : that.articleDoi != null) return false;
    if (articleTitle != null ? !articleTitle.equals(that.articleTitle) : that.articleTitle != null) return false;
    if (articleID != null ? !articleID.equals(that.articleID) : that.articleID != null) return false;
    if (parentID != null ? !parentID.equals(that.parentID) : that.parentID != null) return false;
    if (body != null ? !body.equals(that.body) : that.body != null) return false;
    if (citation != null ? !citation.equals(that.citation) : that.citation != null) return false;
    if (competingInterestStatement != null ? !competingInterestStatement.equals(that.competingInterestStatement) : that.competingInterestStatement != null)
      return false;
    if (creatorID != null ? !creatorID.equals(that.creatorID) : that.creatorID != null) return false;
    if (creatorDisplayName != null ? !creatorDisplayName.equals(that.creatorDisplayName) : that.creatorDisplayName != null) return false;
    if (creatorFormattedName != null ? !creatorFormattedName.equals(that.creatorFormattedName) : that.creatorFormattedName != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (type != that.type) return false;
    if (xpath != null ? !xpath.equals(that.xpath) : that.xpath != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (body != null ? body.hashCode() : 0);
    result = 31 * result + (competingInterestStatement != null ? competingInterestStatement.hashCode() : 0);
    result = 31 * result + (annotationUri != null ? annotationUri.hashCode() : 0);
    result = 31 * result + (xpath != null ? xpath.hashCode() : 0);
    result = 31 * result + (ID != null ? ID.hashCode() : 0);
    result = 31 * result + (creatorID != null ? creatorID.hashCode() : 0);
    result = 31 * result + (creatorDisplayName != null ? creatorDisplayName.hashCode() : 0);
    result = 31 * result + (creatorFormattedName != null ? creatorFormattedName.hashCode() : 0);
    result = 31 * result + (articleID != null ? articleID.hashCode() : 0);
    result = 31 * result + (parentID != null ? parentID.hashCode() : 0);
    result = 31 * result + (articleDoi != null ? articleDoi.hashCode() : 0);
    result = 31 * result + (articleTitle != null ? articleTitle.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (citation != null ? citation.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AnnotationView{" +
        "title='" + title + '\'' +
        ", body='" + body + '\'' +
        ", competingInterestStatement='" + competingInterestStatement + '\'' +
        ", annotationUri='" + annotationUri + '\'' +
        ", xpath='" + xpath + '\'' +
        ", creatorID=" + creatorID +
        ", creatorDisplayName='" + creatorDisplayName + '\'' +
        ", articleID=" + articleID +
        ", parentID=" + parentID +
        ", articleDoi='" + articleDoi + '\'' +
        ", articleTitle='" + articleTitle + '\'' +
        ", type=" + type +
        '}';
  }

  protected String truncateText(String text) {
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

  public String getTitle() {
    return title;
  }

  private String createFormattedName(UserProfile up) {
    StringBuilder name = new StringBuilder();

    if (up.getGivenNames() != null && !up.getGivenNames().equals("")) {
      name.append(up.getGivenNames());
    }

    if (up.getSurname() != null && !up.getSurname().equals("")) {
      if (name.length() > 0) {
        name.append(' ');
      }

      name.append(up.getSurname());
    }

    if (name.length() == 0)
      name.append(up.getDisplayName());

    return name.toString();
  }

  public String getBody() {
    return body;
  }

  public String getTruncatedBody() {
    return this.truncatedBody;
  }

  public String getBodyWithUrlLinkingNoPTags() {
    return bodyWithUrlLinkingNoPTags;
  }

  public String getTruncatedBodyWithUrlLinkingNoPTags() {
    return truncatedBodyWithUrlLinkingNoPTags;
  }

  public String getCompetingInterestStatement() {
    return competingInterestStatement;
  }

  public String getTruncatedCompetingInterestStatement() {
    return truncatedCompetingInterestStatement;
  }

  public AnnotationView[] getReplies() {
    return replies.clone();
  }

  public String getAnnotationUri() {
    return annotationUri;
  }

  public String getXpath() {
    return xpath;
  }

  public Long getID() {
    return ID;
  }

  public Long getCreatorID() {
    return creatorID;
  }

  public String getCreatorDisplayName() {
    return creatorDisplayName;
  }

  public String getCreatorFormattedName() {
    return creatorFormattedName;
  }

  public Long getArticleID() {
    return articleID;
  }

  public Long getParentID() {
    return parentID;
  }

  public Date getCreated() {
    //Defensive copy
    Calendar date = Calendar.getInstance();
    date.setTime(created);
    return date.getTime();
  }

  public String getCreatedFormatted() {
    return createdFormatted;
  }

  public long getCreatedAsMillis() {
    return created.getTime();
  }

  public AnnotationCitationView getCitation() {
    return citation;
  }

  public AnnotationType getType() {
    return type;
  }

  public boolean isCorrection() {
    return type.isCorrection();
  }

  public String getArticleDoi() {
    return articleDoi;
  }

  public String getArticleTitle() {
    return articleTitle;
  }

  public String getOriginalBody() {
    return originalBody;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }

  public Date getLastReplyDate() {
    return lastReplyDate;
  }

  public int getTotalNumReplies() {
    return totalNumReplies;
  }
}
