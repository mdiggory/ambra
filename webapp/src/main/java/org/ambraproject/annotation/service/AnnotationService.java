/* $HeadURL::                                                                            $
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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.annotation.service;

import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.ambraproject.user.AmbraUser;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Wrapper over annotation(not the same as reply) web service
 */
public interface AnnotationService extends BaseAnnotationService {

  public Set<Class<? extends ArticleAnnotation>> getAllAnnotationClasses();

  /**
   * Delete an annotation.
   *
   * @param annotationId annotationId
   * @throws RuntimeException      on an error
   * @throws SecurityException if a security policy prevented this operation
   */
  public void deleteAnnotation(final String annotationId, final String authId) throws SecurityException;


  /**
   * Get a list of all annotation Ids satifying the given criteria. All caching is done at the object level by the
   * session.
   *
   * @param startDate  search for annotation after start date.
   * @param endDate    is the date to search until. If null, search until present date
   * @param annotTypes List of annotation types
   * @param maxResults the maximum number of results to return, or 0 for no limit
   * @param journal journalName
   * @return the (possibly empty) list of article ids.
   * @throws ParseException     if any of the dates or query could not be parsed
   * @throws URISyntaxException if an element of annotType cannot be parsed as a URI
   */
  public List<String> getFeedAnnotationIds(Date startDate, Date endDate, Set<String> annotTypes,
                                           int maxResults, String journal )
      throws ParseException, URISyntaxException;

  /**
   * Get a list of all reply Ids satifying the given criteria. All caching is done at the object level by the session.
   *
   * @param startDate  search for replies after start date.
   * @param endDate    is the date to search until. If null, search until present date
   * @param annotTypes List of annotation types
   * @param maxResults the maximum number of results to return, or 0 for no limit
   * @param journal journalName
   * @return the (possibly empty) list of article ids.
   * @throws ParseException     if any of the dates or query could not be parsed
   * @throws URISyntaxException if an element of annotType cannot be parsed as a URI
   */
  public List<String> getReplyIds(Date startDate, Date endDate, Set<String> annotTypes, int maxResults, String journal)
      throws ParseException, URISyntaxException;

  /**
   * Get all annotations specified by the list of annotation ids.
   *
   * @param annotIds a list of annotations Ids to retrieve.
   * @return the (possibly empty) list of annotations.
   */
  public List<Annotation> getAnnotations(List<String> annotIds);

  /**
   * Retrieve all Annotation instances that annotate the given target DOI. If annotationClassTypes is null, then all
   * annotation types are retrieved. If annotationClassTypes is not null, only the Annotation class types in the
   * annotationClassTypes Set are returned. Each Class in annotationClassTypes should extend Annotation. E.G.
   * Comment.class or FormalCorrection.class
   *
   * @param target               target doi that the listed annotations annotate
   * @param annotationClassTypes a set of Annotation class types to filter the results
   * @return a list of annotations
   * @throws RuntimeException      on an error
   * @throws SecurityException if a security policy prevented this operation
   */
  public ArticleAnnotation[] listAnnotations(final String target,
                                             Set<Class<? extends ArticleAnnotation>> annotationClassTypes)
      throws SecurityException;

  /**
   * Loads the article annotation with the given id.
   *
   * @param annotationId annotationId
   * @return an annotation
   * @throws RuntimeException             on an error
   * @throws SecurityException        if a security policy prevented this operation
   * @throws IllegalArgumentException if an annotation with this id does not exist
   */
  public ArticleAnnotation getArticleAnnotation(final String annotationId)
      throws SecurityException, IllegalArgumentException;

  /**
   * Loads the annotation with the given id.
   *
   * @param annotationId annotationId
   * @return an annotation
   * @throws RuntimeException             on an error
   * @throws SecurityException        if a security policy prevented this operation
   * @throws IllegalArgumentException if an annotation with this id does not exist
   */
  public Annotation getAnnotation(final String annotationId)
      throws SecurityException, IllegalArgumentException;

  /**
   * Update the annotation body and context.
   *
   * @param id      the annotation id
   * @param body
   * @param context the context to set
   * @throws RuntimeException             on an error
   * @throws SecurityException        if a security policy prevented this operation
   * @throws IllegalArgumentException if an annotation with this id does not exist
   */
  public void updateBodyAndContext(String id, String body, String context, String authId)
      throws SecurityException, IllegalArgumentException, UnsupportedEncodingException;

  /**
   * List the set of annotations in a specific administrative state.
   *
   * @param mediator if present only those annotations that match this mediator are returned
   * @param state    the state to filter the list of annotations by or 0 to return annotations in any administrative
   *                 state
   * @return an array of annotation metadata; if no matching annotations are found, an empty array is returned
   * @throws RuntimeException      if some error occurred
   * @throws SecurityException if a security policy prevented this operation
   */
  public ArticleAnnotation[] listAnnotations(final String mediator, final int state)
      throws SecurityException;

  /**
   * Replaces the Annotation (indicated by the <code>srcAnnotationId</code> DOI) with a new Annotation of type
   * <code>newAnnotationClassType</code>. Convertion requires that a new class type be used, so the old annotation
   * properties are copied over to the new type, even though the DOI remains the same.
   * <p/>
   * The given newAnnotationClassType should implement the interface ArticleAnnotation. Known annotation classes that
   * implement this interface are Comment, FormalCorrection, MinorCorrection, and Retraction.
   *
   * @param srcAnnotationId        the DOI of the annotation to convert
   * @param newAnnotationClassType the Class of the new annotation type. Should implement ArticleAnnotation
   * @return the id of the annotation, identical to <code>srcAnnotationId</code>
   * @throws Exception on an error
   */
  public String convertAnnotationToType(final String srcAnnotationId,
                                        final Class<? extends ArticleAnnotation> newAnnotationClassType) throws Exception;

  /**
   * Create an annotation.
   *
   * @param annotationClass the class of annotation
   * @param mimeType        mimeType of the annotation body
   * @param target          target of this annotation
   * @param context         context the context within the target that this applies to
   * @param olderAnnotation olderAnnotation that the new one will supersede
   * @param title           title of this annotation
   * @param body            body of this annotation
   * @param ciStatement     competing interest statement of this annotation
   * @param isPublic        to set up public permissions
   * @param user            logged in user
   * @return a the new annotation id
   * @throws Exception on an error
   */
  public String createAnnotation(Class<? extends ArticleAnnotation> annotationClass,
                                 final String mimeType, final String target,
                                 final String context, final String olderAnnotation,
                                 final String title, final String body, final String ciStatement,
                                 boolean isPublic, AmbraUser user)
      throws Exception;

  /**
   * Create an annotation.
   *
   * @param target          target that an annotation is being created for
   * @param context         context
   * @param olderAnnotation olderAnnotation that the new one will supersede
   * @param title           title
   * @param mimeType        mimeType
   * @param body            body
   * @param ciStatement     competing interesting statement
   * @param isPublic        isPublic
   * @param user            logged in user
   * @return unique identifier for the newly created annotation
   * @throws Exception on an error
   */
  public String createComment(final String target, final String context,
                              final String olderAnnotation, final String title,
                              final String mimeType, final String body, final String ciStatement,
                              final boolean isPublic, AmbraUser user) throws Exception;

  /**
   * Create a flag against an annotation or a reply
   *
   * @param target     target that a flag is being created for
   * @param reasonCode reasonCode
   * @param body       body
   * @param mimeType   mimeType
   * @param user       Logged in user
   * @return unique identifier for the newly created flag
   * @throws Exception on an error
   */
  public String createFlag(final String target, final String reasonCode,
                           final String body, final String mimeType, AmbraUser user) throws Exception;

}
