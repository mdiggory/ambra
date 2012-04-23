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

package org.ambraproject.views;

import org.ambraproject.models.Trackback;
import java.util.Date;

/**
 * An immutable wrapper around {@link org.ambraproject.models.Trackback} for the display layer
 * @author Joe Osowski
 */
public class TrackbackView {
  private final Long ID;
  private final Long articleID;
  private final String url;
  private final String title;
  private final String blogName;
  private final String excerpt;
  private final Date created;
  private final Date lastModified;
  private final String articleDoi;
  private final String articleTitle;
  
  public TrackbackView(final Trackback trackback, final String articleDoi, final String articleTitle) {
    this.articleID = trackback.getArticleID();
    this.url = trackback.getUrl();
    this.title = trackback.getTitle();
    this.blogName = trackback.getBlogName();
    this.excerpt = trackback.getExcerpt();
    this.ID = trackback.getID();
    this.created = trackback.getCreated();
    this.lastModified = trackback.getLastModified();
    this.articleDoi = articleDoi;
    this.articleTitle = articleTitle;
  }

  public Long getID() {
    return ID;
  }

  public Long getArticleID() {
    return articleID;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public String getBlogName() {
    return blogName;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public Date getCreated() {
    return created;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getArticleDoi() {
    return articleDoi;
  }

  public String getArticleTitle() {
    return articleTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TrackbackView that = (TrackbackView) o;

    if (!ID.equals(that.ID)) return false;
    if (!articleID.equals(that.articleID)) return false;
    if (blogName != null ? !blogName.equals(that.blogName) : that.blogName != null) return false;
    if (excerpt != null ? !excerpt.equals(that.excerpt) : that.excerpt != null) return false;
    if (title != null ? !title.equals(that.title) : that.title != null) return false;
    if (url != null ? !url.equals(that.url) : that.url != null) return false;
    if (!articleDoi.equals(that.articleDoi)) return false;
    if (!articleTitle.equals(that.articleTitle)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = ID.hashCode();
    result = 31 * result + articleID.hashCode();
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (blogName != null ? blogName.hashCode() : 0);
    result = 31 * result + (excerpt != null ? excerpt.hashCode() : 0);
    result = 31 * result + articleDoi.hashCode();
    result = 31 * result + articleTitle.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TrackbackView{" +
        "ID=" + ID +
        ", articleID=" + articleID +
        ", url='" + url + '\'' +
        ", title='" + title + '\'' +
        ", blogName='" + blogName + '\'' +
        ", articleDoi='" + articleDoi + '\'' +
        ", articleTitle='" + articleTitle + '\'' +
        '}';
  }
}
