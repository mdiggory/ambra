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

import java.util.ArrayList;
import java.util.Collection;

import org.topazproject.ambra.models.Reply;
import org.topazproject.ambra.models.ReplyBlob;

/**
 * Ambra wrapper around the Reply from topaz service. It provides
 * - A way to escape title/body text when returning the result to the web layer
 * - a separation from any topaz changes
 */
public class WebReply extends BaseAnnotation<Reply> {
  private Collection<WebReply> replies = new ArrayList<WebReply>();

  /**
   * Creates a WebReply object.
   *
   * @param reply the reply
   * @param creatorName the display name of the creator (must be non-null if the view requires it)
   * @param originalBodyContent body as text (must be non-null if the view requires it)
   */
  public WebReply(Reply reply, String creatorName, String originalBodyContent) {
    super(reply, creatorName, originalBodyContent);
  }

  /**
   * Get inReplyTo.
   *
   * @return inReplyTo as String.
   */
  public String getInReplyTo() {
    return annotea.getInReplyTo();
  }

  /**
   * Get root.
   *
   * @return root as String.
   */
  public String getRoot() {
    return annotea.getRoot();
  }

  /**
   * Get title.
   *
   * @return title as String.
   */
  @Override
  public String getCommentTitle() {
    return escapeText(annotea.getTitle());
  }

  /**
   * Add a (child) reply to this reply
   * @param reply reply
   */
  public void addReply(final WebReply reply) {
    replies.add(reply);
  }

  /**
   * @return the replies to this reply
   */
  public WebReply[] getReplies() {
    return replies.toArray(new WebReply[replies.size()]);
  }

 /**
  * Return Escaped text of the CIStatement.
  * @return CIStatement as String.
  */
  @Override
  public String getCIStatement() {
    ReplyBlob r = annotea.getBody();

    if(r != null) {
      return escapeText(r.getCIStatement());
    } else {
      return null;
    }
  }
}
