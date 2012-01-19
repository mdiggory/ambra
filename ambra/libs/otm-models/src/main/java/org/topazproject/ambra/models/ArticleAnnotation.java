/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
package org.topazproject.ambra.models;

import org.topazproject.otm.CascadeType;
import org.topazproject.otm.FetchType;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;

/**
 * ArticleAnnotation interface represents a model Class that provides the necessary information
 * to  annotate an article. It is implemented by Comment, MinorCorrection, and FormalCorrection,
 * and may be implemented by other model classes that shall represent annotation types that are
 * overlaid on an Article.  Since Comment and Correction are semantically different, and inherit
 * from a different semantic hierarchy, this interface allows the UI to process different
 * annotation types more generically.
 *
 * @author Alex Worden
 */
@Entity()
public abstract class ArticleAnnotation extends Annotation<AnnotationBlob> {

  /**
   * Gets the body as a blob.
   *
   * Note: We have to override both set and get method because Java introspection does not work
   * well with generics and would return method with generic type instead of the concrete
   * implementation.
   *
   * See: http://bugs.sun.com/view_bug.do?bug_id=6528714
   *
   * @return Returns the body of the article annotation
   */
  @Override
  public AnnotationBlob getBody() {
    return super.getBody();
  }

  /**
   * Sets the body as a blob. Note that the override here is only
   * for the changed cascade type. The CascadeType.deleteOrphan is
   * removed since the conversion of Comment to Correction re-uses
   * the body by switching/moving the reference.

   * @param body The body of the article annotation
   */
  @Override
  @Predicate(uri = "annotea:body", cascade = { CascadeType.peer, CascadeType.delete })
  public void setBody(AnnotationBlob body) {
    super.setBody(body);
  }
}
