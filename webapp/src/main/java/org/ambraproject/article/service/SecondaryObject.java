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
package org.ambraproject.article.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;

/**
 * Display wrapper around a group of ArticleAssets.
 */
public class SecondaryObject implements Serializable {

  private String contextElement;
  private String uri;
  private String description;
  private Set<ArticleAsset> representations;

  private String transformedDescription;
  private String transformedCaptionTitle;
  private String plainCaptionTitle;

  private String repSmall;
  private String repMedium;
  private String repLarge;

//  static final long serialVersionUID = 7439718780407844715L;

  public SecondaryObject(final String contextElement, final String uri,
                         final String description, final Set<ArticleAsset> representations,
                         final String repSmall, final String repMedium, final String repLarge)  {
    this.contextElement          = contextElement;
    this.uri                     = uri;
    this.description             = description;
    this.representations         = representations;

    this.repSmall                = repSmall;
    this.repMedium               = repMedium;
    this.repLarge                = repLarge;
  }

  /**
   * @return the context element of this object
   */
  public String getContextElement() {
    return contextElement;
  }

  public String getUri() {
    return uri;
  }

  public String getDescription() {
    return description;
  }

  public Set<ArticleAsset> getRepresentations() {
    return representations;
  }

  /**
   * TODO: remove this useless method.
   * Always returns NULL.
   */
  public String getDoi() {
    // TODO: doi: munging not really resolved
    return null;  // objectInfo.getDoi();
  }

  /**
   * @return the thumbnail representation for the images
   */
  public String getRepSmall() {
    return repSmall;
  }

  /**
   * @return the representation for medium size image
   */
  public String getRepMedium() {
    return repMedium;
  }

  /**
   * @return the representation for maximum size image
   */
  public String getRepLarge() {
    return repLarge;
  }

  /**
   * @return Returns the plainTitle.
   */
  public String getPlainCaptionTitle() {
    return (plainCaptionTitle == null) ? "" : plainCaptionTitle;
  }

  /**
   * @return Returns the transformedDescription.
   */
  public String getTransformedDescription() {
    return (transformedDescription == null) ? "" : transformedDescription;
  }

  /**
   * @return Returns the transformedTitle.
   */
  public String getTransformedCaptionTitle() {
    return (transformedCaptionTitle == null) ? "" : transformedCaptionTitle;
  }

  /**
   * @param plainTitle The plainTitle to set.
   */
  public void setPlainCaptionTitle(String plainTitle) {
    this.plainCaptionTitle = plainTitle;
  }

  /**
   * @param transformedDescription The transformedDescription to set.
   */
  public void setTransformedDescription(String transformedDescription) {
    this.transformedDescription = transformedDescription;
  }

  /**
   * @param transformedTitle The transformedTitle to set.
   */
  public void setTransformedCaptionTitle(String transformedTitle) {
    this.transformedCaptionTitle = transformedTitle;
  }

  public static SecondaryObject[] getSecondaryObjects(final Article article,
                                                      final String[] contextFilter,
                                                      final String repSmall,
                                                      final String repMedium,
                                                      final String repLarge) {
    if (article == null || article.getAssets() == null || article.getAssets().size() < 1) {
      return null;
    }

    LinkedHashMap<String, SecondaryObject> secondaryObjects = new LinkedHashMap<String, SecondaryObject>();

    for (ArticleAsset asset : article.getAssets()) {
      //  If there is a defined "contextFilter", then only create SecondaryObject objects for
      //    the ArticleAsset objects which match the "contextFilter".
      if (contextFilter != null && contextFilter.length > 0) {
        boolean isAssetInContextFilter = false;
        for (String context : contextFilter) {
          if (context.equals(asset.getContextElement())) {
            isAssetInContextFilter = true;
            break;
          }
        }
        if ( ! isAssetInContextFilter) {
          continue;  //  Move on to the next ArticleAsset.
        }
      }

      if ( ! secondaryObjects.containsKey(asset.getDoi())) {
        secondaryObjects.put(asset.getDoi(), new SecondaryObject(
            asset.getContextElement(),    //  contextElement
            asset.getDoi(),               //  URI
            // TODO: Description will come from ArticleAsset.getDescription(), once that property/method exists.
            "PLACEHOLDER Description set in SecondaryObject.getSecondaryObjects(...)",     //  Description
            new HashSet<ArticleAsset>(),  //  Representations
            repSmall, repMedium, repLarge
        ));
      }

      secondaryObjects.get(asset.getDoi()).getRepresentations().add(asset);
    }

    return (SecondaryObject[])(new ArrayList<SecondaryObject>(secondaryObjects.values()).toArray());
  }
}