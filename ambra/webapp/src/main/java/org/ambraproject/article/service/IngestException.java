/* $HeadURL::                                                                                     $
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

package org.ambraproject.article.service;

/**
 * Singals an error with the ingest.
 *
 * @author Ronald Tschalär
 */
public class IngestException extends Exception {
  /**
   * Create a new exception instance.
   *
   * @param message a message describing the error
   */
  public IngestException(String message) {
    super(message);
  }

  /**
   * Create a new exception instance.
   *
   * @param message a message describing the error
   * @param cause   the exception that caused the error
   */
  public IngestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * This is just here so axis will generate a service version with a contructor that takes the
   * message.
   *
   * @return the message
   */
  public String getMessage() {
    return super.getMessage();
  }
}
