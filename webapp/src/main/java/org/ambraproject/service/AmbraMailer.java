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
package org.ambraproject.service;

import java.util.Map;

public interface AmbraMailer {
  /**
   * Send an email when the user selects to email an article to a friend
   * @param toEmailAddress toEmailAddress
   * @param fromEmailAddress fromEmailAddress
   * @param mapFields mapFields to fill up the template with the right values
   */
  public void sendEmailThisArticleEmail(final String toEmailAddress, final String fromEmailAddress,
      final Map<String, String> mapFields);

  public void sendFeedback(final String fromEmailAddress, final Map<String, Object> mapFields);

  public void sendIngestNotify(final Map<String, Object> mapFields);

  public void sendError(String message);
}
