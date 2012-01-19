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
package org.ambraproject.annotation.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.Flag;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action class to get a list of flags for a given uri.
 */
@SuppressWarnings("serial")
public class ListFlagAction extends BaseActionSupport {
  private String target;
  private Flag[] flags;
  protected AnnotationService annotationService;
  protected AnnotationConverter converter;

  private static final Logger log = LoggerFactory.getLogger(ListFlagAction.class);

  @Transactional(readOnly = true)
  @Override
  public String execute() throws Exception {
    try {
      flags = converter.convertAsFlags(
          annotationService.listAnnotations(target, annotationService.getCommentSet()), true, true);
    } catch (final Exception e) {
      log.error("Could not list flags for target: " + target, e);
      addActionError("Flag fetch failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * @return a list of flags
   */
  public Flag[] getFlags() {
    return flags;
  }

  /**
   * Set the target that it annotates.
   * @param target target
   */
  public void setTarget(final String target) {
    this.target = target;
  }

  /**
   * @return the target of the annotation
   */
  @RequiredStringValidator(message="You must specify the target that you want to list the flags for")
  public String getTarget() {
    return target;
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  @Required
  public void setAnnotationConverter(AnnotationConverter converter) {
    this.converter = converter;
  }
}
