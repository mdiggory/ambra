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

/**
 * Skeleton action class to provide a getter/setter for the toGo parameter.  Here simply
 * for future use in case we want to do something on a redirect.
 * 
 * @author Stephen Cheng
 *
 */
public class RedirectAction extends BaseActionSupport {
  private String goTo;

  /**
   * This execute method always returns SUCCESS
   * 
   */
  public String execute() throws Exception {
    return SUCCESS;
  }

  /**
   * @return Returns the goTo.
   */
  public String getGoTo() {
    return goTo;
  }

  /**
   * @param goTo The goTo to set.
   */
  public void setGoTo(String goTo) {
    this.goTo = goTo;
  }
}
