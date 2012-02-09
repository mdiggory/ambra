/* $HeadURL::                                                                             $
 * $Id::                                                         $
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.Commentary;
import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.ReplyService;
import org.ambraproject.annotation.service.WebAnnotation;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.models.Article;
import org.topazproject.ambra.models.ArticleAnnotation;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

/**
 * Action class to get a list of all commentary for an article and the threads associated
 * with each base comment.
 *
 * @author Stephen Cheng
 * @author jkirton
 * @author Alex Worden
 */
@SuppressWarnings("serial")
public class GetCommentaryAction extends BaseActionSupport {
  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  protected String target;
  private Commentary[] commentary;
  private Article article;
  private ArticleService articleService;
  protected ReplyService replyService;
  protected AnnotationConverter converter;
  protected AnnotationService annotationService;

  /**
   * Provides a list of comments for the target.
   * @return Array of {@link WebAnnotation}s representing the target's comments
   */
  @Transactional(readOnly = true)
  public String listComments() {
    return list(annotationService.getCommentSet());
  }

  /**
   * Provides a list of corrections for the target.
   * @return Array of {@link WebAnnotation}s representing the target's corrections
   */
  @Transactional(readOnly = true)
  public String listCorrections() {
    return list(annotationService.getCorrectionSet());
  }

  /**
   * Pulls either all corrections or all non-correction comments for the given target.
   * @param set Pull corrections?
   * @return status
   */
  private String list(Set<Class<? extends ArticleAnnotation>> set) {
    try {
      article = articleService.getArticle(target, getAuthId());
      WebAnnotation[] annotations =
        converter.convert(annotationService.listAnnotations(target, set), true, false);
      commentary = new Commentary[annotations.length];
      Commentary com = null;
      if (annotations.length > 0) {
        for (int i = 0; i < annotations.length; i++) {
          com = new Commentary();
          com.setAnnotation(annotations[i]);
          try {
            converter.convert(replyService.listAllReplies(annotations[i].getId(),
                                                          annotations[i].getId()), com, false,
                                                           false);
          } catch (SecurityException t) {
            // don't error if you can't list the replies
            com.setNumReplies(0);
            com.setReplies(null);
          }
          commentary[i] = com;
        }
        Arrays.sort(commentary, new Commentary.Sorter());
      }
    } catch (final Exception e) {
      log.error("Failed to create commentary for articleID: " + target, e);
      addActionError("Commentary creation failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  @Transactional(readOnly = true)
  public String getArticleMetaInfo () throws Exception {
    article = articleService.getArticle(target, getAuthId());
    return SUCCESS;
  }

  /**
   * Set the target that it annotates.
   * @param target target
   */
  public void setTarget(final String target) {
    this.target = target;
  }

  /**
   * @return the target of the annotation
   */
  @RequiredStringValidator(message="You must specify the target that you want to list the annotations for")
  public String getTarget() {
    return target;
  }

  /**
   * @param articleService The ArticleService to set.
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @return Returns the articleInfo.
   */
  public Article getArticleInfo() {
    return article;
  }

  /**
   * @param articleInfo The articleInfo to set.
   */
  public void setArticleInfo(Article articleInfo) {
    this.article = articleInfo;
  }

  /**
   * @return The commentary array
   */
  public Commentary[] getCommentary() {
    return commentary;
  }

  @Required
  public void setReplyService(final ReplyService replyService) {
    this.replyService = replyService;
  }

  @Required
  public void setAnnotationConverter(AnnotationConverter converter) {
    this.converter = converter;
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }
}
