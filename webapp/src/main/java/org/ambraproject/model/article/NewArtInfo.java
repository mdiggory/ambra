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
package org.ambraproject.model.article;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Projection;
import org.topazproject.otm.annotations.View;

/**
 * The info about a single article that the UI needs.
 */
@View(query = "select cat, a.id articleId, a.dublinCore.date date from Article a " +
              "where cat := a.categories.mainCategory and a.id = :id;")
public class NewArtInfo implements Serializable {
  public URI          id;
  public Date         date;
  public String       category;

  /**
   * Set id.
   *
   * @param id the value to set.
   */
  @Id
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Set date.
   *
   * @param date the value to set.
   */
  @Projection("date")
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Set category.
   *
   * @param category the value to set.
   */
  @Projection("cat")
  public void setCategory(String category) {
    this.category = category;
  }
}
