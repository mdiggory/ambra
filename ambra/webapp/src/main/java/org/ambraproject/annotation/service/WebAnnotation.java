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

import java.net.URI;

import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.Correction;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.MinorCorrection;
import org.topazproject.ambra.models.Retraction;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.AnnotationBlob;
import org.topazproject.ambra.models.Citation;

/**
 *  View level wrapper for Annotations. It provides
 * - A way to escape title/body text when returning the result to the web layer
 * - a separation from any content model changes
 */
public class WebAnnotation extends BaseAnnotation<ArticleAnnotation> {
  /**
   * Get the target(probably a uri) that it annotates
   * @return target
   */
  public String getAnnotates() {
    URI u = annotea.getAnnotates();
    return (u == null) ? null : u.toString();
  }

  /**
   * Get context.
   * @return context as String.
   */
  public String getContext() {
    return annotea.getContext();
  }

  /**
   * Get supersededBy.
   * @return supersededBy as String.
   */
  public String getSupersededBy() {
    Annotation<?> a = annotea.getSupersededBy();
    return (a == null) ? null : a.getId().toString();
  }

  /**
   * Get supersedes.
   * @return supersedes as String.
   */
  public String getSupersedes() {
    Annotation<?> a = annotea.getSupersedes();
    return (a == null) ? null : a.getId().toString();
  }

 /**
  * Escaped text of title.
  * @return title as String.
  */
  @Override
  public String getCommentTitle() {
    String title;
    if (isMinorCorrection()) {
      title = "Minor Correction: " + annotea.getTitle();
    }
    else if (isFormalCorrection()) {
      title = "Formal Correction: " + annotea.getTitle();
    }
    else if (isRetraction()) {
      title = "Retraction: " + annotea.getTitle();
    }
    else {
      title = annotea.getTitle();
    }
    return escapeText(title);
  }

  /**
   * Escaped text of title, without any modifications.
   * @return title as String, without any modifications.
   */
   public String getTitle() {
     return escapeText(annotea.getTitle());
   }

  public boolean isFormalCorrection() {
    return annotea instanceof FormalCorrection;
  }

  public boolean isMinorCorrection() {
    return annotea instanceof MinorCorrection;
  }

  public boolean isRetraction() {
    return annotea instanceof Retraction;
  }

  public boolean isCorrection() {
    return annotea instanceof Correction;
  }

  public boolean isComment() {
    return annotea instanceof Comment;
  }

 /**
  * Escaped text of CIStatement.
  * @return CIStatement as String.
  */
  @Override
  public String getCIStatement() {
    AnnotationBlob b = annotea.getBody();

    if(b != null) {
      return escapeText(b.getCIStatement());
    } else {
      return null;
    }
  }

  /**
   * Creates a WebAnnotation object.
   *
   * @param annotation the annotation
   * @param creatorName the display name of the creator (must be non-null if the view requires it)
   * @param originalBodyContent body as text (must be non-null if the view requires it)
   */
  public WebAnnotation(ArticleAnnotation annotation, String creatorName,
                       String originalBodyContent) {
    super(annotation, creatorName, originalBodyContent);
    // init lazy-loaded fields
    Citation citation = getCitation();
    if (citation != null) {
      citation.getEditors();
      citation.getAuthors();
      citation.getCollaborativeAuthors();
    }
  }

  /**
   * Provide a display name for the various types of annotations on articles.
   *
   * @return display name as String.
   */
  public String getDisplayName() {
    if (isComment()) {
      return (getContext() != null) ? "Note" : "Comment";
    } else if (isFormalCorrection()) {
      return "Formal Correction";
    } else if (isMinorCorrection()) {
      return "Minor Correction";
    } else if (isRetraction()) {
      return "Retraction";
    } else if (isCorrection()) {
      return "Correction";
    }
    return null;
  }

  public Citation getCitation() {
    if (annotea instanceof FormalCorrection)
      return ((FormalCorrection)annotea).getBibliographicCitation();
    if (annotea instanceof Retraction)
      return ((Retraction)annotea).getBibliographicCitation();
    return null;
  }
}
