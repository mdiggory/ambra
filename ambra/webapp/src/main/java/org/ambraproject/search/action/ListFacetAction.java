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
package org.ambraproject.search.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * A simple action to retrieve and display facet information
 *
 * @author Joe Osowski
 */
public class ListFacetAction extends SearchAction {
  //I extend SearchAction as we need pretty much all the same parameters plus one. 
  private static final Logger log = LoggerFactory.getLogger(SearchAction.class);
  private String facetName;
  
  /**
   * For the current set of parameters, just retrieve the facet data
   * @return ERROR or SUCCESS
   */
  @Transactional(readOnly = true)
  public String listFacet() {
    //We only allow a very small set of options and all these options should be set by the system.  '
    //hence, we have unforgiving input validation
    if(searchType == null) {
      addActionError("Search failed");
      log.error("Invalid Search Type specified.");
      return ERROR;
    }

    if(!(searchType.equals("simple") || searchType.equals("unformatted")
        || searchType.equals("findAnArticle"))) {
      addActionError("Search failed");
      log.error("Invalid Search Type specified of " + searchType);
      return ERROR;
    }

    if(facetName == null) {
      addActionError("Search failed");
      log.error("facetName must be specified.");
      return ERROR;
    }

    if(!(facetName.equals("subjects") || facetName.equals("articleTypes"))) {
      addActionError("Search failed");
      log.error("Invalid Facet name specified of " + facetName);
      return ERROR;
    }

    if(searchType.equals("simple"))
      return executeSimpleSearch();

    if(searchType.equals("unformatted"))
      return executeUnformattedSearch();

    if(searchType.equals("findAnArticle"))
      return executeFindAnArticleSearch();

    log.error("Invalid Search Type specified of " + searchType);

    return ERROR;
  }

  public String getFacetName() {
    return facetName;
  }

  public void setFacetName(String facetName) {
    this.facetName = facetName;
  }

  public void setSearchType(String searchType)
  {
    super.searchType = searchType;
  }
}
