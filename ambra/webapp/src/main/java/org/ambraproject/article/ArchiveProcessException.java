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

package org.ambraproject.article;

/**
 * @author Alex Kudlick Date: 6/7/11
 *         <p/>
 *         org.ambraproject.article
 */
public class ArchiveProcessException extends Exception {
  public ArchiveProcessException() {
  }

  public ArchiveProcessException(String message) {
    super(message);
  }

  public ArchiveProcessException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArchiveProcessException(Throwable cause) {
    super(cause);
  }
}
