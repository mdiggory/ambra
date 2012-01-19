/* $HeadURL::                                                                            $
 * $Id:ListReplyAction.java 722 2006-10-02 16:42:45Z viru $
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

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.ReplyService;
import org.ambraproject.annotation.service.WebAnnotation;
import org.ambraproject.annotation.service.WebReply;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.models.Article;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action class to get a list of replies to annotations.
 */
@SuppressWarnings("serial")
public class ListReplyAction extends BaseActionSupport {
  private static final Logger log = LoggerFactory.getLogger(ListReplyAction.class);

  private String root;
  private String inReplyTo;
  private WebReply[] replies;
  private WebAnnotation baseAnnotation;
  private Article article;
  private ArticleService articleService;
  protected ReplyService replyService;
  protected AnnotationConverter converter;
  protected AnnotationService annotationService;

  @Transactional(readOnly = true)
  @Override
  public String execute() throws Exception {
    try {
      replies = converter.convert(replyService.listReplies(root, inReplyTo), true, true);
    } catch (final Exception e) {
      log.error("ListReplyAction.execute() failed for root: " + root, e);
      addActionError("Reply fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * List all the replies for a given root and inRelyTo in a threaded tree
   * structure.
   *
   * @return webwork status for the call
   */
  @Transactional(readOnly = true)
  public String listAllReplies() {
    try {
      /*
       * Allow a single 'root' param to be accepted. If 'inReplyTo' is null or
       * empty string, set to root value.
       * This results in that single annotation being displayed.
       */
      if ((inReplyTo == null) || inReplyTo.length() == 0) {
        inReplyTo = root;
      }
      if (log.isDebugEnabled()) {
        log.debug("listing all Replies for root: " + root);
      }
      baseAnnotation = converter.convert(annotationService.getArticleAnnotation(root), true, true);
      replies = converter.convert(replyService.listAllReplies(root, inReplyTo), true, true);
      final String articleId = baseAnnotation.getAnnotates();
      article = articleService.getArticle(articleId, getAuthId());

    } catch (Exception ae) {
      log.info("Could not list all replies for root: " + root, ae);
      addActionError("Reply fetching failed with error message: " + ae.getMessage());
      return ERROR;
    }

    return SUCCESS;
  }

  /**
   * @param articleService ArticleService Spring Injected
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  public void setRoot(final String root) {
    this.root = root;
  }

  public void setInReplyTo(final String inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  public WebReply[] getReplies() {
    return replies;
  }

  @RequiredStringValidator(message = "root is required")
  public String getRoot() {
    return root;
  }

  public String getInReplyTo() {
    return inReplyTo;
  }

  /**
   * @return Returns the baseAnnotation.
   */
  public WebAnnotation getBaseAnnotation() {
    return baseAnnotation;
  }

  /**
   * @param baseAnnotation The baseAnnotation to set.
   */
  public void setBaseAnnotation(WebAnnotation baseAnnotation) {
    this.baseAnnotation = baseAnnotation;
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
