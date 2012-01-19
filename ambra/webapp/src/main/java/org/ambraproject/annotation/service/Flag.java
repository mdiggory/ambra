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
package org.ambraproject.annotation.service;

import org.ambraproject.annotation.FlagUtil;
import org.ambraproject.ApplicationException;

/**
 * Used to flag an Annotaion or a Reply.
 */
public class Flag {
  private final WebAnnotation annotation;

  public Flag(final WebAnnotation annotation) {
    this.annotation = annotation;
  }

  public String getAnnotates() {
    return annotation.getAnnotates();
  }

  public String getCreator() {
    return annotation.getCreator();
  }

  public String getCreatorName() {
    return annotation.getCreatorName();
  }

  public int getState() {
    return annotation.getState();
  }

  public String getId() {
    return annotation.getId();
  }

  public String getCreated() {
    return annotation.getCreated();
  }

  public String getComment() throws ApplicationException {
    return FlagUtil.getComment(getOriginalComment());
  }

  public String getReasonCode() throws ApplicationException {
    return FlagUtil.getReasonCode(getOriginalComment());
  }

  private String getOriginalComment() {
    return annotation.getOriginalBodyContent();
  }
}
