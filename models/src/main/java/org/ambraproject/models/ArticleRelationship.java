/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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
package org.ambraproject.models;

/**
 * Relationship between two articles. The 'other article id' could be null if it is an article which is not in the
 * system.
 *
 * @author Alex Kudlick 11/9/11
 */
public class ArticleRelationship extends AmbraEntity {

  private Article parentArticle;

  private Long otherArticleID;
  private String otherArticleDoi;
  private String type;

  public Long getOtherArticleID() {
    return otherArticleID;
  }

  public void setOtherArticleID(Long otherArticleID) {
    this.otherArticleID = otherArticleID;
  }

  public String getOtherArticleDoi() {
    return otherArticleDoi;
  }

  public void setOtherArticleDoi(String otherArticleDoi) {
    this.otherArticleDoi = otherArticleDoi;
  }

  public Article getParentArticle() {
    return parentArticle;
  }

  public void setParentArticle(Article parentArticle) {
    this.parentArticle = parentArticle;
  }
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if(o == null) return false;
    if (!(o instanceof ArticleRelationship)) return false;


    ArticleRelationship relationship = (ArticleRelationship) o;

    if (getID() != null ? !getID().equals(relationship.getID()) : relationship.getID() != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return getID() != null ? getID().hashCode() : 0;
  }


}
