/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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

import org.ambraproject.annotation.Context;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.FlagReasonCode;
import org.ambraproject.models.UserProfile;
import org.ambraproject.views.AnnotationView;

import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Wrapper over annotation(not the same as reply) web service
 */
public interface AnnotationService {
  /**
   * Enum for passing order values to {@link AnnotationService#listAnnotations(Long, java.util.Set,
   * org.ambraproject.annotation.service.AnnotationService.AnnotationOrder)} and {@link
   * AnnotationService#listAnnotationsNoReplies(Long, java.util.Set, org.ambraproject.annotation.service.AnnotationService.AnnotationOrder)}
   */
  public static enum AnnotationOrder {
    /**
     * Order by the created date of the annotation, going oldest to newest
     */
    OLDEST_TO_NEWEST,
    /**
     * Order by the date of the most recent reply to any comment in the thread, newest to oldest. This includes replies
     * to replies. If there are no replies, the creation date of the annotation is used.
     */
    MOST_RECENT_REPLY
  }


  /**
   * Create a comment on an article.  If the xpath passed is null or empty, then it is created as an {@link
   * org.ambraproject.models.AnnotationType#COMMENT}. If it's not empty, it is assumed we are creating an inline comment
   * ({@link org.ambraproject.models.AnnotationType#NOTE})
   *
   * @param user             the user creating the annotation
   * @param articleDoi       the doi of the article being annotated
   * @param title            title
   * @param body             body
   * @param ciStatement      competing interesting statement
   * @param context          if this is an inline note, the location of the highlighted portion in the article
   * @param flagAsCorrection if true, flag the created comment as a potential correction
   * @return the id of the stored annotation
   */
  public Long createComment(UserProfile user, final String articleDoi, final String title, final String body,
                            @Nullable final String ciStatement, @Nullable final Context context, boolean flagAsCorrection);

  /**
   * Create a reply to an annotation
   * @param user the user creating the reply
   * @param parentId the id of the annotation being replied to
   * @param title the title of the reply
   * @param body the body of the reply
   * @param ciStatement the competing interest statement for the reply
   * @return the generated id of the reply object
   */
  public Long createReply(UserProfile user, final Long parentId, final String title, final String body, @Nullable final String ciStatement);

  /**
   * Get a view object wrapper around the specified annotation, with all replies loaded up
   *
   * @param annotationId the id of the annotation to get
   * @return a view wrapper around the annotation, with replies fully populated
   */
  public AnnotationView getFullAnnotationView(Long annotationId);

  /**
   * Get a view wrapper around the annotation object, without replies loaded.  The number of replies will be counted,
   * the last reply date will be loaded up, and a faux reply array will be set on the annotation view with this size and
   * the last entry having created date as the last reply date. This is a hack to show the number of replies and last
   * reply date on the comment page of the article, without loading up replies.
   *
   * @param annotationId the id of the annotation to load
   * @return a view wrapper around the the annotation with no replies
   */
  public AnnotationView getBasicAnnotationView(Long annotationId);

  /**
   * Get a view wrapper around the annotation object, without replies loaded.
   *
   * @param annotationUri the uri of the annotation to load
   * @return a view wrapper around the the annotation with no replies
   */
  public AnnotationView getBasicAnnotationViewByUri(String annotationUri);


  /**
   * Create a flag against an annotation or a reply
   *
   * @param user         Logged in user
   * @param annotationId the id of the annotation being flagged
   * @param reasonCode   reasonCode
   * @param body         body
   * @return unique identifier for the newly created flag
   */
  public Long createFlag(UserProfile user, final Long annotationId, final FlagReasonCode reasonCode,
                         final String body);


  /**
   * List annotations of specified types on an article. Replies must be loaded up in order to display a count of
   * "replies to this comment". This count includes replies to replies.
   *
   * @param articleID       the article to get the annotations for
   * @param annotationTypes only fetch annotations of these types
   * @param order           an {@link AnnotationOrder} flag indicating how to order results
   * @return a list of annotations on the article, with replies loaded up
   */
  public AnnotationView[] listAnnotations(final Long articleID, final Set<AnnotationType> annotationTypes, final AnnotationOrder order);

  /**
   * List annotations of specified types on an article without loading up replies. This means that {@link
   * AnnotationOrder#MOST_RECENT_REPLY} CANNOT be specified as an ordering
   *
   * @param articleID       the article to get the annotations for
   * @param annotationTypes only fetch annotations of these types
   * @param order           an {@link AnnotationOrder} flag indicating how to order results
   * @return a list of annotations on the article, without replies loaded up
   * @throws IllegalArgumentException if {@link AnnotationOrder#MOST_RECENT_REPLY} is specified as the order type
   */
  public AnnotationView[] listAnnotationsNoReplies(final Long articleID, final Set<AnnotationType> annotationTypes, final AnnotationOrder order);

  /**
   * Count the number of annotations on an article
   *
   * @param articleID       the article to count for
   * @param annotationTypes the types of annotation to count.  If null, counts all types, including replies
   * @return the number of comments and corrections on an article, not including replies
   */
  public int countAnnotations(Long articleID, Set<AnnotationType> annotationTypes);

  /**
   * Get a list of all annotations satisfying the given criteria.
   *
   * @param startDate  search for annotation after start date.
   * @param endDate    is the date to search until. If null, search until present date
   * @param annotTypes List of annotation types
   * @param maxResults the maximum number of results to return, or 0 for no limit
   * @param journal    journalName
   * @return the (possibly empty) list of article annotations.
   * @throws ParseException     if any of the dates or query could not be parsed
   * @throws URISyntaxException if an element of annotType cannot be parsed as a URI
   */
  public List<AnnotationView> getAnnotations(final Date startDate, final Date endDate,
                                             final Set<String> annotTypes, final int maxResults, final String journal)
      throws ParseException, URISyntaxException;

}
