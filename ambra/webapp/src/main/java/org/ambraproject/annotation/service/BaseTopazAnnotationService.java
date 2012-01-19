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

import org.springframework.beans.factory.annotation.Required;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.Correction;
import org.ambraproject.permission.service.PermissionsService;
import org.topazproject.otm.Session;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for Annotaion and Reply web service wrappers
 */
public abstract class BaseTopazAnnotationService implements BaseAnnotationService {
  private String encodingCharset = "UTF-8";
  private String applicationId;
  protected Session session;
  protected PermissionsService permissionsService;

  public final Set<Class<? extends ArticleAnnotation>> CORRECTION_SET =
  new HashSet<Class<? extends ArticleAnnotation>>();

  public final Set<Class<? extends ArticleAnnotation>> COMMENT_SET =
  new HashSet<Class<? extends ArticleAnnotation>>();
  {
    CORRECTION_SET.add(Correction.class);
    COMMENT_SET.add(Comment.class);
  }

  public Set<Class<? extends ArticleAnnotation>> getCommentSet() {
    return COMMENT_SET;
  }

  public Set<Class<? extends ArticleAnnotation>> getCorrectionSet() {
    return CORRECTION_SET;
  }

  /**
   * Set the id of the application
   * @param applicationId applicationId
   */
  @Required
  public void setApplicationId(final String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * @return the encoding charset
   */
  public String getEncodingCharset() {
    return encodingCharset;
  }

  /**
   * @param encodingCharset charset for encoding the data to be persisting in
   */
  public void setEncodingCharset(final String encodingCharset) {
    this.encodingCharset = encodingCharset;
  }

  /**
   * @return the application id
   */
  public String getApplicationId() {
    return applicationId;
  }

  protected String getContentType(final String mimeType) {
    return mimeType + ";charset=" + getEncodingCharset();
  }

  /**
   * Set the OTM session. Called by spring's bean wiring.
   *
   * @param session the otm session
   */
  @Required
  public void setOtmSession(Session session) {
    this.session = session;
  }

  /**
   * Set the PermissionsService
   *
   * @param permissionsService permissionWebService
   */
  public void setPermissionsService(final PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }
}
