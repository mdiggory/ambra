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

import java.util.Set;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.WebAnnotation;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Retraction;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Action class to get a list of annotations.
 */
@SuppressWarnings("serial")
public class ListAnnotationAction extends BaseActionSupport {
  private String target;
  private WebAnnotation[] annotations;
  protected AnnotationService annotationService;
  protected AnnotationConverter converter;

  private static final Logger log = LoggerFactory.getLogger(ListAnnotationAction.class);

  /**
   * Loads all annotations for a given target.
   * @return status
   */
  private String loadAnnotations(Set<Class<? extends ArticleAnnotation>> annotationTypeClasses,
                                 boolean needBody) {
    try {
      annotations = converter.convert(
          annotationService.listAnnotations(target, annotationTypeClasses), true, needBody);
    } catch (final Exception e) {
      log.error("Could not list annotations for target: " + target, e);
      addActionError("Annotation fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  @Override
  @Transactional(readOnly = true)
  public String execute() throws Exception {
    return loadAnnotations(null, false);
  }

  /**
   * @return Only those annotations that represent formal corrections.
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public String fetchFormalCorrections() {
    return loadAnnotations((Set) Collections.singleton(FormalCorrection.class), true);
  }

  /**
   * @return Only those annotations that represent retractions.
   */
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public String fetchRetractions() {
    return loadAnnotations((Set) Collections.singleton(Retraction.class), true);
  }

  /**
   * @return a list of annotations
   */
  public WebAnnotation[] getAnnotations() {
    return annotations;
  }

  /**
   * @return List of associated formal corrections
   */
  public WebAnnotation[] getFormalCorrections() {
    return annotations;
  }

  /**
   * @return List of associated retractions
   */
  public WebAnnotation[] getRetractions() {
    return annotations;
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
  @RequiredStringValidator(message="You must specify the target that you want to list the annotations for")
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
