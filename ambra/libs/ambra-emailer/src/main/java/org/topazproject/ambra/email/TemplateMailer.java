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

package org.topazproject.ambra.email;

import java.util.Map;

/**
 * A contract for all template based emailers.
 */
public interface TemplateMailer {
  /**
   * Send a mail with both a text and a HTML version.
   * @param toEmailAddress          the email address where to send the email
   * @param fromEmailAddress fromEmailAddress
   * @param subject subject of the email
   * @param context        a {@link java.util.Map} of objects to expose to the template engine
   * @param textTemplateFilename textTemplateFilename
   * @param htmlTemplateFilename htmlTemplateFilename
   */
  void mail(final String toEmailAddress, final String fromEmailAddress, final String subject,
            final Map<String, Object> context, final String textTemplateFilename,
            final String htmlTemplateFilename);

  /**
   * Mail to multiple email addresses with both a text and a HTML version.
   * Each email comes with it's corresponding context to use for the templates
   * @param emailAddressContextMap email address and it's corresponding context
   * @param subject subject of the email
   * @param textTemplateFilename textTemplateFilename
   * @param htmlTemplateFilename htmlTemplateFilename
   */
  void massMail(final Map<String, Map<String, Object>> emailAddressContextMap, final String subject,
                final String textTemplateFilename, final String htmlTemplateFilename);
}
