/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.ambra.doi;

import java.util.Arrays;
import java.util.Set;

/**
 * Exception indicating that an infinite loop was detected with annotations annotating each other
 * @author alex 9/7/11
 */
public class AnnotationLoopException extends Exception {
  public AnnotationLoopException() {
  }

  public AnnotationLoopException(String message) {
    super(message);
  }

  public AnnotationLoopException(Set<String> dois) {
    super("Loop detected among annotations: " + Arrays.toString(dois.toArray()));
  }
}
