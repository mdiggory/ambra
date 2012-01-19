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

import org.ambraproject.annotation.Commentary;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.Reply;

import java.util.List;

/**
 * A utility class to convert types between topaz and ambra types for
 * Annotations and Replies
 */
public interface AnnotationConverter {

  /**
   * @param annotations an array of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  public WebAnnotation[] convert(final ArticleAnnotation[] annotations, boolean needCreatorName,
                                 boolean needBody);
  /**
   * Converts and <code>ArticleAnnotation</code> to <code>List&lt;WebAnnotation&gt;</code>
   *
   * @param annotations an list of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  public List<WebAnnotation> convert(final List<ArticleAnnotation> annotations,
                                     boolean needCreatorName,boolean needBody);

  /**
   * Converts and <code>Reply</code> to <code>List&lt;WebReply&gt;</code>
   *
   * @param replies an list of replies
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return an array of Annotation objects as required by the web layer
   */
  public List<WebReply> convertReplies(final List<Reply> replies,
                                boolean needCreatorName,
                                boolean needBody);

  /**
   * @param annotations an array of annotations
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return an array of Flag objects as required by the web layer
   */
  public Flag[] convertAsFlags(final ArticleAnnotation[] annotations, boolean needCreatorName,
                               boolean needBody);

  /**
   * @param annotation annotation
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return the Annotation
   */
  public WebAnnotation convert(final ArticleAnnotation annotation, boolean needCreatorName,
                               boolean needBody);

  /**
   * Creates a hierarchical array of replies based on the flat array passed in.
   *
   * @param replies an array of Replies
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return an array of Reply objects as required by the web layer
   */
  public WebReply[] convert(final Reply[] replies, boolean needCreatorName, boolean needBody);

  /**
   * Creates a hierarchical array of replies based on the flat array passed in.
   * Fills in Commentary com parameter as appropriate
   *
   * @param replies the list of replies to convert
   * @param com the commentary
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return the hierarchical replies
   */
  public WebReply[] convert(final Reply[] replies, Commentary com, boolean needCreatorName,
                            boolean needBody);
  /**
   * @param reply reply
   * @param needCreatorName indicates if a display-name of the creator needs to be fetched
   * @param needBody indicates if the annotation body is required
   * @return the reply for the web layer
   */
  public WebReply convert(final Reply reply, boolean needCreatorName, boolean needBody);

}
