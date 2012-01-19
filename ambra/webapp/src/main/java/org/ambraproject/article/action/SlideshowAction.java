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
package org.ambraproject.article.action;

import java.util.ArrayList;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.ArticleAssetService;
import org.ambraproject.article.service.ArticleAssetWrapper;
import org.ambraproject.service.XMLService;

/**
 * Fetch the secondary objects for a given uri
 */
public class SlideshowAction extends BaseActionSupport {
  private String uri;
  private ArticleAssetWrapper[] articleAssetWrapper;
  private ArticleAssetService articleAssetService;
  private XMLService secondaryObjectService;
  private static final Logger log = LoggerFactory.getLogger(SlideshowAction.class);

  /**
   * Action to return list of Secondary object for an article that are enclosed in Tables (table-warp)
   * and Figures (fig) tags.
   *
   * @return webork status string
   * @throws Exception
   */
  @Override
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    try {
      articleAssetWrapper = articleAssetService.listFiguresTables(uri, getAuthId());
      ArrayList<ArticleAssetWrapper> figTables = new ArrayList<ArticleAssetWrapper>(articleAssetWrapper.length);
      String allTransformed;
      String[] elems;
      StringBuilder desc;
      String doi;

      for (ArticleAssetWrapper s: articleAssetWrapper) {
        figTables.add(s);
        try {
          allTransformed = secondaryObjectService.getTransformedDocument(s.getDescription());
          if (log.isDebugEnabled()){
            log.debug("Transformed figure captions for article: " + uri);
            log.debug(allTransformed);
          }
          elems = allTransformed.split("END_TITLE");
          desc = new StringBuilder();
          doi = s.getDoi();
          if (elems.length > 1) {
            s.setTransformedCaptionTitle(elems[0]);
            s.setPlainCaptionTitle(elems[0].replaceAll("<.*>",""));
            desc.append(elems[1]);
            if ((doi != null) && (doi.length() > 0)) {
              desc.append("doi:").append(doi);
            }
            s.setTransformedDescription(desc.toString());
          } else if (elems.length == 1) {
            desc.append(elems[0]);
            if ((doi != null) && (doi.length() > 0)) {
              desc.append("doi:").append(doi);
            }
            s.setTransformedDescription(desc.toString());
          }
        } catch (Exception e) {
          log.warn("Could not transform description for Object: " + getUri(), e);
          s.setTransformedDescription(s.getDescription());
        }
      }

      articleAssetWrapper = figTables.toArray(new ArticleAssetWrapper[figTables.size()]);

      if(articleAssetWrapper.length == 0) {
        log.debug("There are no objects for URI: {}", uri);
        return INPUT;
      }

    } catch (Exception ex) {
      log.info("Couldn't retrieve secondary object for URI: " + uri, ex);
      return INPUT;
    }
    return SUCCESS;
  }
  @RequiredStringValidator(message = "Object URI is required.")
  public String getUri() {
    return uri;
  }

  /**
   * Set the uri
   * @param uri uri
   */
  public void setUri(final String uri) {
    this.uri = uri;
  }

  /**
   * Get the secondary objects.
   * @return secondary objects
   */
  public ArticleAssetWrapper[] getSecondaryObjects() {
    return articleAssetWrapper;
  }

  /**
   * Set the secondary objects
   * @param articleAssetService articleAssetService
   */
  @Required
  public void setArticleAssetService(final ArticleAssetService articleAssetService) {
    this.articleAssetService = articleAssetService;
  }

  /**
   * @param secondaryObjectService The secondaryObjectUtil to set.
   */
  @Required
  public void setSecondaryObjectService(XMLService secondaryObjectService) {
    this.secondaryObjectService = secondaryObjectService;
  }
}
