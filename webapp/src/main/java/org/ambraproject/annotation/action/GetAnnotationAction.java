/* $HeadURL::                                                                            $
 * $Id:GetAnnotationAction.java 722 2006-10-02 16:42:45Z viru $
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

import org.ambraproject.views.AnnotationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.annotation.service.AnnotationService;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Used to fetch an annotation given an id.
 *
 */
@SuppressWarnings("serial")
public class GetAnnotationAction extends BaseActionSupport {
  private Long annotationId;
  private AnnotationView annotation;
  protected AnnotationService annotationService;

  private static final Logger log = LoggerFactory.getLogger(GetAnnotationAction.class);

  @Transactional(readOnly = true)
  @Override
  public String execute() throws Exception {
    try {
      annotation = annotationService.getFullAnnotationView(annotationId);
    } catch (Exception e) {
      log.error("Could not retreive annotation with id: " + annotationId, e);
      addActionError("Annotation fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * Set the annotationId for the annotation to fetch
   * @param annotationId annotationId
   */
  public void setAnnotationId(final Long annotationId) {
    this.annotationId = annotationId;
  }

  @RequiredFieldValidator(message = "Annotation Id is a required field")
  public Long getAnnotationId() {
    return annotationId;
  }

  public AnnotationView getAnnotation() {
    return annotation;
  }

  /**
   * @return Returns the creatorUserName.
   */
  public String getCreatorUserName() {
    return annotation.getCreatorDisplayName();
  }

  /**
   * Returns Milliseconds representation of the CIS start date
   * @return Milliseconds representation of the CIS start date 
   * @throws Exception on bad config data or config entry not found.
   */
  public long getCisStartDateMillis() throws Exception {
    try {
      return DateFormat.getDateInstance(DateFormat.SHORT).parse(this.configuration.getString("ambra.platform.cisStartDate")).getTime();
    } catch (ParseException ex) {
      throw (Exception) new Exception("Could not find or parse the cisStartDate node in the ambra platform configuration.  Make sure the ambra/platform/cisStartDate node exists.").initCause(ex);
    }
  }

  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }
}
