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
 * 
 */
package org.topazproject.ambra.web;

import com.opensymphony.xwork2.ActionSupport;

import org.topazproject.ambra.OtherConstants;

/**
 * @author stevec
 *
 */
public class BaseAction extends ActionSupport {
  protected static final String EMAIL_REGEX =
    "^[a-zA-Z0-9!#$%&?'`{|/}*+=^._~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  private OtherConstants otherConstants;

  /**
   * @return Returns the otherConstants.
   */
  public OtherConstants getOtherConstants() {
    return otherConstants;
  }

  /**
   * @param otherConstants The otherConstants to set.
   */
  public void setOtherConstants(OtherConstants otherConstants) {
    this.otherConstants = otherConstants;
  }
}
