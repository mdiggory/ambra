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

import org.topazproject.otm.annotations.Entity;

/**
 * Class that will represent comments by users persisted as comment annotations by Ambra.
 *
 * @author Pradeep Krishnan
 */
@Entity(types = {Comment.RDF_TYPE})
public class Comment extends ArticleAnnotation {
  private static final long serialVersionUID = 2167106987991125344L;
  public  static final String RDF_TYPE = Annotea.W3C_TYPE_NS + "Comment";

  public String getType() {
    return RDF_TYPE;
  }

  @Override
  public String getWebType() {
    return (getContext() != null) ? WEB_TYPE_NOTE : WEB_TYPE_COMMENT;
  }
}
