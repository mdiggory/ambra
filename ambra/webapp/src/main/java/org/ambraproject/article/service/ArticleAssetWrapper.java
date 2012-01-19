/* $HeadURL:: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/webapp/src/main#$
 * $Id: SecondaryObject.java 9820 2011-11-23 21:14:43Z ssterling $
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
import java.util.Set;

import org.ambraproject.models.ArticleAsset;
import org.springframework.beans.factory.annotation.Required;

/**
 * Wrapper around ArticleAsset
 */
public class ArticleAssetWrapper implements Serializable {

  private String repSmall;
  private String repMedium;
  private String repLarge;
  private String transformedDescription;
  private String transformedCaptionTitle;
  private String plainCaptionTitle;
  private String description;
  private String doi;
  private String contextElement;
  private String title;

  static final long serialVersionUID = 7439718780407844715L;

  public ArticleAssetWrapper(final ArticleAsset articleAsset,
                         final String repSmall, final String repMedium, final String repLarge) {
    this.description = articleAsset.getDescription();
    this.doi = articleAsset.getDoi();
    this.contextElement = articleAsset.getContextElement();
    this.title = articleAsset.getTitle();
    this.repSmall = repSmall;
    this.repMedium = repMedium;
    this.repLarge = repLarge;
  }

  /**
   *
   * @return
   */
  public String getContextElement() {
    return contextElement;
  }

  /**
   *
   * @return
   */
  public String getUri() {
    return doi;
  }
  
  public String getDoi() {
    return doi;
  }

  /**
   *
   * @return title
   */
  public String getTitle() {
    return (title == null) ? "" : title;
  }

  /**
   *
   * @return description
   */
  public String getDescription(){
    return (description == null) ? "" : description;
  }

  /**
   * need to refactor this
   *
   */
  /*public Set<Representation> getRepresentations() {
    return objectInfo.getRepresentations();
  }*/

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArticleAssetWrapper that = (ArticleAssetWrapper) o;

    if (contextElement != null ? !contextElement.equals(that.contextElement) : that.contextElement != null)
      return false;
    if (description != null ? !description.equals(that.description) : that.description != null) return false;
    if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
    if (plainCaptionTitle != null ? !plainCaptionTitle.equals(that.plainCaptionTitle) : that.plainCaptionTitle != null)
      return false;
    if (repLarge != null ? !repLarge.equals(that.repLarge) : that.repLarge != null) return false;
    if (repMedium != null ? !repMedium.equals(that.repMedium) : that.repMedium != null) return false;
    if (repSmall != null ? !repSmall.equals(that.repSmall) : that.repSmall != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (transformedCaptionTitle != null ? !transformedCaptionTitle.equals(that.transformedCaptionTitle) : that.transformedCaptionTitle != null)
      return false;
    if (transformedDescription != null ? !transformedDescription.equals(that.transformedDescription) : that.transformedDescription != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = repSmall != null ? repSmall.hashCode() : 0;
    result = 31 * result + (repMedium != null ? repMedium.hashCode() : 0);
    result = 31 * result + (repLarge != null ? repLarge.hashCode() : 0);
    result = 31 * result + (transformedDescription != null ? transformedDescription.hashCode() : 0);
    result = 31 * result + (transformedCaptionTitle != null ? transformedCaptionTitle.hashCode() : 0);
    result = 31 * result + (plainCaptionTitle != null ? plainCaptionTitle.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (doi != null ? doi.hashCode() : 0);
    result = 31 * result + (contextElement != null ? contextElement.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ArticleAssetWrapper{" +
        "doi='" + doi + '\'' +
        ", repSmall='" + repSmall + '\'' +
        ", repMedium='" + repMedium + '\'' +
        ", repLarge='" + repLarge + '\'' +
        '}';
  }
}
