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
package org.topazproject.ambra.web;

import com.opensymphony.xwork2.ActionSupport;

import org.topazproject.ambra.OtherConstants;

/**
 * An easy way to access any constants from the OtherConstants 
 */
public class OtherConstantsAction extends ActionSupport {
  private OtherConstants otherConstants;
  /**
   * @return webwork status
   * @throws Exception
   */
  public String execute() throws Exception {
    return SUCCESS;
  }

  /**
   * Getter for otherConstants.
   * @param key key of the object
   * @return Value for otherConstants.
   */
  public Object get(final String key) {
    return otherConstants.getValue(key);
  }

  /**
   * Setter for property otherConstants.
   * @param otherConstants Value to otherConstants.
   */
  public void setOtherConstants(final OtherConstants otherConstants) {
    this.otherConstants = otherConstants;
  }
}
