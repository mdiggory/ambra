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

import org.topazproject.ambra.models.Reply;
import org.ambraproject.user.AmbraUser;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Wrapper over reply web service
 */
public interface ReplyService extends BaseAnnotationService {

  /**
   * Create a reply that replies to a message.
   *
   * @param root root of this thread
   * @param inReplyTo the message this is in reply to
   * @param title title of this message
   * @param mimeType mimeType of the reply body
   * @param body the reply body
   *
   * @param user
   * @return a new reply
   *
   * @throws Exception on an error
   */
  public String createReply(final String root, final String inReplyTo, final String title,
                            final String mimeType, final String body, final String ciStatement, AmbraUser user)
                     throws Exception;

  /**
   * Delete the subtree and not just mark it as deleted.
   *
   * @param root root of the discussion thread
   * @param inReplyTo the messages that replied to this and their children are to be deleted
   *
   * @throws RuntimeException on an error
   * @throws SecurityException if a security policy prevented this operation
   */
  public void deleteReplies(final String root, final String authId, final String inReplyTo)
                     throws SecurityException;
  /**
   * Delete the sub tree including this message.
   *
   * @param target the reply to delete
   *
   * @throws SecurityException if a security policy prevented this operation
   */
  public void deleteReplies(final String target, final String authId) throws SecurityException;

  /**
   * Get all replies specified by the list of reply ids.
   *
   * @param replyIds a list of annotations Ids to retrieve.
   *
   * @return the (possibly empty) list of replies.
   *
   */
  public List<Reply> getReplies(List<String> replyIds);

  /**
   * Get all replies satisfying the criteria.
   *
   * @param startDate  is the date to start searching from. If null, start from begining of time.
   *                   Can be iso8601 formatted or string representation of Date object.
   * @param endDate    is the date to search until. If null, search until present date
   * @param annotType  a filter list of rdf types for the annotations.
   * @param maxResults the maximum number of results to return, or 0 for no limit
   *
   * @return the (possibly empty) list of replies.
   *
   * @throws java.text.ParseException if any of the dates or query could not be parsed
   * @throws java.net.URISyntaxException if an element of annotType cannot be parsed as a URI
   */
  public List<Reply> getReplies(Date startDate, Date endDate, Set<String> annotType, int maxResults)
  throws ParseException, URISyntaxException;



  /**
   * Gets the reply given its id.
   *
   * @param replyId the replyId
   *
   * @return a reply
   *
   * @throws SecurityException if a security policy prevented this operation
   * @throws IllegalArgumentException if the id does not correspond to a reply
   */
  public Reply getReply(final String replyId)
                         throws SecurityException, IllegalArgumentException;
  /**
   * List all messages in reply-to a specific message in a thread.
   *
   * @param root root of this discussion thread
   * @param inReplyTo return all messages that are in reply to this message
   *
   * @return list of replies
   *
   * @throws SecurityException if a security policy prevented this operation
   */
  public Reply[] listReplies(final String root, final String inReplyTo)
                          throws SecurityException;

  /**
   * Transitively list all replies that are in reply-to a specific message in a thread.
   *
   * @param root root of this discussion thread
   * @param inReplyTo walk down the thread and return all messages that are transitively in reply
   *        to this message
   *
   * @return list of replies
   *
   * @throws SecurityException if a security policy prevented this operation
   */
  public Reply[] listAllReplies(final String root, final String inReplyTo)
                             throws SecurityException;
  /**
   * List the set of replies in a specific administrative state.
   *
   * @param mediator if present only those replies that match this mediator are returned
   * @param state the state to filter the list of replies by or 0 to return replies in any
   *        administartive state
   *
   * @return an array of replies; if no matching replies are found, an empty array is returned
   *
   * @throws SecurityException if a security policy prevented this operation
   */
  public Reply[] listReplies(final String mediator, final int state)
                          throws SecurityException;
}
