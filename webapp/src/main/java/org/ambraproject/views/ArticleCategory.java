/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.views;

/**
 * View object for article Categories
 *
 * @author Joe Osowski
 */
public class ArticleCategory {
  private String mainCategory;
  private String subCategory;
  
  public ArticleCategory(String mainCategory, String subCategory) {
    this.mainCategory = mainCategory;
    this.subCategory = subCategory;
  }

  public String getMainCategory() {
    return mainCategory;
  }

  public void setMainCategory(String mainCategory) {
    this.mainCategory = mainCategory;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArticleCategory that = (ArticleCategory) o;

    if (mainCategory != null ? !mainCategory.equals(that.mainCategory) : that.mainCategory != null) return false;
    if (subCategory != null ? !subCategory.equals(that.subCategory) : that.subCategory != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = mainCategory != null ? mainCategory.hashCode() : 0;
    result = 31 * result + (subCategory != null ? subCategory.hashCode() : 0);
    return result;
  }
}
