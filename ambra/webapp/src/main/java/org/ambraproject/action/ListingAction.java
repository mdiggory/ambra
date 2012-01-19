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
package org.ambraproject.action;

import com.opensymphony.xwork2.ActionSupport;

import static org.ambraproject.Constants.SelectValues;

import java.util.Map;

/**
 * A provider of all listings for select boxes in an html page.
 */
public class ListingAction extends ActionSupport {
  private Map otherConstants;

  /**
   * Getter for otherConstants.
   * @param key key of the object
   * @return Value for otherConstants.
   */
  public Object get(final String key) {
    return otherConstants.get(key);
  }

  /**
   * Setter for property otherConstants.
   * @param otherConstants Value to otherConstants.
   */
  public void setOtherConstants(final Map otherConstants) {
    this.otherConstants = otherConstants;
  }

  public String execute() throws Exception {
    return SUCCESS;
  }

  /** return a map of all Organization Types */
  public Map<String, String> getAllOrganizationTypes() {
    return SelectValues.getAllOrganizationTypes();
  }

  /** return a map of all titles */
  public Map<String, String> getAllTitles() {
    return SelectValues.getAllTitles();
  }

  /** return a map of all position types */
  public Map<String, String> getAllPositionTypes() {
    return SelectValues.getAllPositionTypes();
  }

  /** return a map of all url descriptions */
  public Map<String, String> getAllUrlDescriptions() {
    return SelectValues.getAllUrlDescriptions();
  }
}
