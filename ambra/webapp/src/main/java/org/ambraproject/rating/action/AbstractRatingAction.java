/* $HeadURL$
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
package org.ambraproject.rating.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;

import org.ambraproject.action.BaseSessionAwareActionSupport;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.rating.service.RatingsService;

/**
 * AbstractRatingAction - Common base class to rating related actions.
 *
 * @author jkirton
 */
@SuppressWarnings("serial")
public abstract class AbstractRatingAction extends BaseSessionAwareActionSupport {
  protected static final Logger log = LoggerFactory.getLogger(AbstractRatingAction.class);

  protected ArticleService articleService;
  protected RatingsService ratingsService;
  /**
   * Sets the article OTM service for any sub classes to call
   * 
   * @param articleService the ArticleService to set
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  @Required
  public void setRatingsService(RatingsService rs) {
    this.ratingsService = rs;
  }
}
