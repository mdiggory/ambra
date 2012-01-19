/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.topazproject.ambra.email.impl.FreemarkerTemplateMailer;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AmbraMailerImpl extends FreemarkerTemplateMailer implements AmbraMailer {

  private static final Log log = LogFactory.getLog(AmbraMailerImpl.class);

  private Map<String, String> emailThisArticleMap;
  private Map<String, String> feedbackEmailMap;
  private Map<String, String> autoIngestEmailMap;
  private Map<String, String> errorEmailMap;

  /**
   * Setter for emailThisArticleMap.
   * @param emailThisArticleMap emailThisArticleMap
   */
  public void setEmailThisArticleMap(final Map<String, String> emailThisArticleMap) {
    this.emailThisArticleMap = Collections.unmodifiableMap(emailThisArticleMap);
  }

  /**
   * Setter for feedbackEmailMap.
   * @param feedbackEmailMap Value to set for feedbackEmailMap.
   */
  public void setFeedbackEmailMap(final Map<String, String> feedbackEmailMap) {
    this.feedbackEmailMap = feedbackEmailMap;
  }

  /**
   * Setter for autoIngestEmailMap.
   * @param autoIngestEmailMap Value to set for autoIngestEmailMap.
   */
  public void setAutoIngestEmailMap(final Map<String, String> autoIngestEmailMap) {
    this.autoIngestEmailMap = autoIngestEmailMap;
  }

  /**
   * Setting for errorEmailMap
   * @param errorEmailMap
   */
  public void setErrorEmailMap(final Map<String, String> errorEmailMap) {
    this.errorEmailMap = errorEmailMap;
  }

  /**
   * Send an email when the user selects to email an article to a friend
   * @param toEmailAddress toEmailAddress
   * @param fromEmailAddress fromEmailAddress
   * @param mapFields mapFields to fill up the template with the right values
   */
  public void sendEmailThisArticleEmail(final String toEmailAddress, final String fromEmailAddress,
      final Map<String, String> mapFields) {
    final HashMap<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(emailThisArticleMap);
    newMapFields.putAll(mapFields);
    sendEmail(toEmailAddress, fromEmailAddress, newMapFields);
  }

  public void sendFeedback(final String fromEmailAddress, final Map<String, Object> mapFields) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(feedbackEmailMap);
    newMapFields.putAll(mapFields);
    sendEmail(feedbackEmailMap.get(TO_EMAIL_ADDRESS), fromEmailAddress, newMapFields);
  }

  public void sendIngestNotify(final Map<String, Object> mapFields) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();
    newMapFields.putAll(autoIngestEmailMap);
    newMapFields.putAll(mapFields);
    sendEmail(autoIngestEmailMap.get(TO_EMAIL_ADDRESS), getFromEmailAddress(), newMapFields);
  }

  public void sendError(String message) {
    final Map<String, Object> newMapFields = new HashMap<String, Object>();

    String host = "couldn't get hostname";

    try {
      InetAddress netAddress = InetAddress.getLocalHost();
      host = netAddress.getHostName();
    } catch (Exception ex) {
      log.error(ex.getMessage(),ex);
    }

    newMapFields.putAll(errorEmailMap);
    newMapFields.put("error", message);
    newMapFields.put("host", host);

    sendEmail(errorEmailMap.get(TO_EMAIL_ADDRESS), getFromEmailAddress(), newMapFields);
  }
}
