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

package org.ambraproject.pubget;

/**
 * De-serialized JSON result from PubGet Widgetizer
 *
 * @author Dragisa Krsmanovic
 */
public class PubGetArticle {

  private String doi;
  private Values values;

  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public Values getValues() {
    return values;
  }

  public void setValues(Values values) {
    this.values = values;
  }

  public static class Values {
    private String pmid;
    private String id;
    private String link;
    private String doi;

    public String getPmid() {
      return pmid;
    }

    public void setPmid(String pmid) {
      this.pmid = pmid;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }

    public String getDoi() {
      return doi;
    }

    public void setDoi(String doi) {
      this.doi = doi;
    }
  }
}
