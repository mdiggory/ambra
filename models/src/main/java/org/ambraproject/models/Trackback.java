/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

/**
 * @author Alex Kudlick 3/7/12
 */
public class Trackback extends AmbraEntity {
  
  private Long articleID;
  private String url;
  private String title;
  private String blogName;
  private String excerpt;

  public Trackback() {
    super();
  }

  public Trackback(Long articleID, String url) {
    super();
    this.articleID = articleID;
    this.url = url;
  }

  public Long getArticleID() {
    return articleID;
  }

  public void setArticleID(Long articleID) {
    this.articleID = articleID;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBlogName() {
    return blogName;
  }

  public void setBlogName(String blogName) {
    this.blogName = blogName;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Trackback)) return false;

    Trackback trackback = (Trackback) o;

    if (articleID != null ? !articleID.equals(trackback.articleID) : trackback.articleID != null) return false;
    if (blogName != null ? !blogName.equals(trackback.blogName) : trackback.blogName != null) return false;
    if (url != null ? !url.equals(trackback.url) : trackback.url != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = articleID != null ? articleID.hashCode() : 0;
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (blogName != null ? blogName.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Trackback{" +
        "articleID=" + articleID +
        ", url='" + url + '\'' +
        ", title='" + title + '\'' +
        ", blogName='" + blogName + '\'' +
        ", excerpt='" + excerpt + '\'' +
        '}';
  }
}

