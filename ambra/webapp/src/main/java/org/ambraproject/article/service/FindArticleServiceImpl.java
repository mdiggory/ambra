/*
 * $HeadURL$
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

package org.ambraproject.article.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.crossref.CrossRefArticle;
import org.ambraproject.crossref.CrossRefLookupService;
import org.ambraproject.pubget.PubGetLookupService;

import java.util.List;

/**
 * @author Dragisa Krsmanovic
 * @see org.ambraproject.article.service.FindArticleService
 */
public class FindArticleServiceImpl implements FindArticleService {

  private static final Logger log = LoggerFactory.getLogger(FindArticleServiceImpl.class);

  private CrossRefLookupService crossRefLookupService;
  private PubGetLookupService pubGetLookupService;

  public ThisArticleFound findArticle(String title, String author) {
    ThisArticleFound article;

    try {
      article = performSearch(title, author);
    } catch (Exception e) {
      // Usually this is timeout exception on CrossRef
      // In case of exception, don't put anything in cache so the query is re-run next time.
      log.warn("Error searching for article title:" + title + " author:" + author, e);
      article = new ThisArticleFound(null, null);
    }
    return article;
  }

  private ThisArticleFound performSearch(String title, String author) throws Exception {
    List<CrossRefArticle> results = crossRefLookupService.findArticles(title, author);

    if (results.isEmpty()) {
      log.debug("No articles found on CrossRef");
      return new ThisArticleFound(null, null);
    } else {
      CrossRefArticle article = results.get(0);
      if (log.isDebugEnabled()) {
        log.debug("Found article " + article);
      }
      return new ThisArticleFound(article.getDoi(), pubGetLookupService.getPDFLink(article.getDoi()));
    }
  }

  @Required
  public void setCrossRefLookupService(CrossRefLookupService crossRefLookupService) {
    this.crossRefLookupService = crossRefLookupService;
  }

  @Required
  public void setPubGetLookupService(PubGetLookupService pubGetLookupService) {
    this.pubGetLookupService = pubGetLookupService;
  }
}
