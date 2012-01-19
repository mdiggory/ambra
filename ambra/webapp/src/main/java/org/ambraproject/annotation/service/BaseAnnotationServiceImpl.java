/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.annotation.service;

import org.springframework.beans.factory.annotation.Required;
import org.topazproject.ambra.models.*;
import org.ambraproject.permission.service.PermissionsService;
import org.ambraproject.service.HibernateServiceImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Kudlick Date: 4/29/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class BaseAnnotationServiceImpl extends HibernateServiceImpl implements BaseAnnotationService {
  private String encodingCharset = "UTF-8";
  private String applicationId;
  protected PermissionsService permissionsService;

  //Provided to convert Rdf types to classes
  private static final Map<String, Class> classesByType = new HashMap<String, Class>();
  static {
    classesByType.put(Comment.RDF_TYPE, Comment.class);
    classesByType.put(Reply.RDF_TYPE, Reply.class);
    classesByType.put(ReplyThread.RDF_TYPE, ReplyThread.class);
    classesByType.put(MinorCorrection.RDF_TYPE, MinorCorrection.class);
    classesByType.put(FormalCorrection.RDF_TYPE, FormalCorrection.class);
    classesByType.put(Retraction.RDF_TYPE, Retraction.class);
    classesByType.put(Rating.RDF_TYPE, Rating.class);
    classesByType.put(RatingSummary.RDF_TYPE, RatingSummary.class);
    classesByType.put(Trackback.RDF_TYPE, Trackback.class);
  }

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

  protected Class classForType(String rdfType) {
    return classesByType.get(rdfType);
  }

  protected Set<Class> classesForTypes(Set<String> rdfTypes) {
    Set<Class> classes = new HashSet<Class>(rdfTypes.size());
    for (String type : rdfTypes) {
      classes.add(classesByType.get(type));
    }
    return classes;
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
   * Set the PermissionsService
   *
   * @param permissionsService permissionWebService
   */
  public void setPermissionsService(final PermissionsService permissionsService) {
    this.permissionsService = permissionsService;
  }

}
