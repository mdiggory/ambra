/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.annotation.Commentary;
import org.topazproject.ambra.models.Annotea;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.ByteArrayBlob;
import org.topazproject.ambra.models.Reply;
import org.ambraproject.user.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to convert types between topaz and ambra types for Annotations and Replies
 */
public class AnnotationConverterImpl implements AnnotationConverter {

  private static final Logger log = LoggerFactory.getLogger(AnnotationConverterImpl.class);

  private UserService userService;

  /**
   * @param annotations     an array of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  @Transactional(readOnly = true)
  public WebAnnotation[] convert(final ArticleAnnotation[] annotations, boolean needCreatorName,
                                 boolean needBody) {
    final WebAnnotation wa[] = new WebAnnotation[annotations.length];

    for (int i = 0; i < annotations.length; i++)
      wa[i] = convert(annotations[i], needCreatorName, needBody);

    return wa;
  }

  /**
   * Converts and <code>ArticleAnnotation</code> to <code>List&lt;WebAnnotation&gt;</code>
   *
   * @param annotations     an list of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  @Transactional(readOnly = true)
  public List<WebAnnotation> convert(final List<ArticleAnnotation> annotations,
                                     boolean needCreatorName, boolean needBody) {
    final List<WebAnnotation> wa = new ArrayList<WebAnnotation>();

    for (ArticleAnnotation annotation : annotations) {
      if (annotation != null)
        wa.add(convert(annotation, needCreatorName, needBody));
    }

    return wa;
  }

  /**
   * Converts and <code>Reply</code> to <code>List&lt;WebReply&gt;</code>
   *
   * @param replies         an list of replies
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  @Transactional(readOnly = true)
  public List<WebReply> convertReplies(final List<Reply> replies,
                                       boolean needCreatorName,
                                       boolean needBody) {
    final List<WebReply> wr = new ArrayList<WebReply>();

    for (Reply reply : replies) {
      if (reply != null)
        wr.add(convert(reply, needCreatorName, needBody));
    }

    return wr;
  }

  /**
   * @param annotations     an array of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return an array of Flag objects as required by the web layer
   */
  @Transactional(readOnly = true)
  public Flag[] convertAsFlags(final ArticleAnnotation[] annotations, boolean needCreatorName,
                               boolean needBody) {
    final Flag flags[] = new Flag[annotations.length];

    for (int i = 0; i < annotations.length; i++)
      flags[i] = new Flag(convert(annotations[i], needCreatorName, needBody));

    return flags;
  }

  /**
   * @param annotation      annotation
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return the Annotation
   */
  @Transactional(readOnly = true)
  public WebAnnotation convert(final ArticleAnnotation annotation, boolean needCreatorName,
                               boolean needBody) {
    String creator = needCreatorName ? lookupCreatorName(annotation) : null;
    String body = needBody ? loadBody(annotation) : null;

    return new WebAnnotation(annotation, creator, body);
  }

  /**
   * Creates a hierarchical array of replies based on the flat array passed in.
   *
   * @param replies         an array of Replies
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return an array of Reply objects as required by the web layer
   */
  @Transactional(readOnly = true)
  public WebReply[] convert(final Reply[] replies, boolean needCreatorName, boolean needBody) {
    return convert(replies, null, needCreatorName, needBody);
  }

  /**
   * Creates a hierarchical array of replies based on the flat array passed in. Fills in Commentary com parameter as
   * appropriate
   *
   * @param replies         the list of replies to convert
   * @param com             the commentary
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return the hierarchical replies
   */
  @Transactional(readOnly = true)
  public WebReply[] convert(final Reply[] replies, Commentary com, boolean needCreatorName,
                            boolean needBody) {
    final List<WebReply> webReplies = new ArrayList<WebReply>();
    final LinkedHashMap<String, WebReply> repliesMap =
        new LinkedHashMap<String, WebReply>(replies.length);
    int numReplies = replies.length;
    String latestReplyTime = null;

    String annotationId = null;
    if (numReplies > 0) {
      annotationId = replies[0].getRoot();
      latestReplyTime = replies[numReplies - 1].getCreatedAsString();
    }

    for (final Reply reply : replies) {
      final WebReply convertedObj = convert(reply, needCreatorName, needBody);
      repliesMap.put(reply.getId().toString(), convertedObj);

      final String replyTo = reply.getInReplyTo();
      // Setup the top level replies
      if (replyTo.equals(annotationId)) {
        webReplies.add(convertedObj);
      }
    }

    // Thread the replies in a parent/child structure
    for (final Map.Entry<String, WebReply> entry : repliesMap.entrySet()) {
      final WebReply savedReply = entry.getValue();
      final String inReplyToId = savedReply.getInReplyTo();

      if (!inReplyToId.equals(annotationId)) {
        final WebReply inReplyTo = repliesMap.get(inReplyToId);
        /*
         * If the replies are in reply to another reply and that reply isn't present then just add
         * them to the top. This only happens when the array passed in is a subtree
         */
        if (null == inReplyTo) {
          webReplies.add(savedReply);
        } else {
          inReplyTo.addReply(savedReply);
        }
      }
    }

    WebReply[] returnArray = webReplies.toArray(new WebReply[webReplies.size()]);
    if (com != null) {
      com.setReplies(returnArray);
      com.setLastModified(latestReplyTime);
      com.setNumReplies(numReplies);
    }
    return returnArray;
  }

  /**
   * @param reply           reply
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody        indicates if the annotation body is required
   * @return the reply for the web layer
   */
  public WebReply convert(final Reply reply, boolean needCreatorName, boolean needBody) {
    String creator = needCreatorName ? lookupCreatorName(reply) : null;
    String body = needBody ? loadBody(reply) : null;

    return new WebReply(reply, creator, body);
  }

  /**
   * @param userService The userService to set.
   */
  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  private String loadBody(final Annotea<? extends ByteArrayBlob> annotea)
      throws Error {

    ByteArrayBlob blob = annotea.getBody();
    try {
      return (blob == null || blob.getBody() == null) ? "" : new String(blob.getBody(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new Error("UTF-8 missing", e);
    }
  }

  private String lookupCreatorName(final Annotea<?> annotea) {
    String creator = annotea.getCreator();
    if (creator == null) {
      creator = "anonymous";
    } else {
      try {
        creator = userService.getUserByAccountUri(creator).getDisplayName();
      } catch (Exception e) {
        log.warn("Failed to lookup display name for creator <" + creator + ">", e);
      }
    }

    return creator;
  }
}
