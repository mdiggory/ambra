/*
 * $HeadURL$
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

package org.ambraproject.queue;

import org.ambraproject.models.Syndication;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.ApplicationException;
import org.ambraproject.admin.service.SyndicationService;

import java.util.StringTokenizer;

/**
 * Message consumer for responses coming from PMC queue.
 * Responses are simple lists of values delimited by ";".
 * Format is articleId;result;errorMessage
 * result can be either "OK" or "FAILED
 * errorMessage is optional.
 *
 * @author Dragisa Krsmanovic
 */
public class PMCResponseConsumer {

  private static final Logger log = LoggerFactory.getLogger(PMCResponseConsumer.class);

  public static final String OK = "OK";
  public static final String FAILED = "FAILED";


  private SyndicationService syndicationService;
  private static final String UNKNOWN = "UNKNOWN";
  private static final String SEPARATOR = "|";

  /**
   * Spring setter for syndication service
   *
   * @param syndicationService SyndicationService
   */
  @Required
  public void setSyndicationService(SyndicationService syndicationService) {
    this.syndicationService = syndicationService;
  }

  /**
   * Message handler method. Message comes in articleId|result|errorMessage format.
   *
   * @param body Message body.
   * @throws ApplicationException If operation fails, URISyntaxException if
   *                              <code>updateSyndication</code> method fails to create the Syndication's unique Id.
   * @throws Exception in operation failed.
   */
  @Handler
  public void handleResponse(@Body String body) throws Exception {

    if (body == null) {
      throw new ApplicationException("PMC response message is null");
    }

    log.info("Received syndication response: " + body);

    StringTokenizer tokenizer = new StringTokenizer(body, SEPARATOR);

    String doi;
    String result;
    String errorMessage = null;

    if (tokenizer.hasMoreTokens()) {
      doi = tokenizer.nextToken();
    } else {
      throw new ApplicationException("Invalid PMC response message received: " + body);
    }

    if (tokenizer.hasMoreTokens()) {
      result = tokenizer.nextToken();
    } else {
      throw new ApplicationException("Invalid PMC response message received: " + body);
    }

    if (tokenizer.hasMoreTokens()) {
      errorMessage = tokenizer.nextToken();
    }

    if (doi == null || doi.equalsIgnoreCase(UNKNOWN)) {
      log.error("Received " + result + " response for unknow DOI. Error message: " + errorMessage);
    } else {
      syndicationService.updateSyndication(
          doi,
          "PMC",
          OK.equalsIgnoreCase(result) ? Syndication.STATUS_SUCCESS : Syndication.STATUS_FAILURE,
          errorMessage);
    }
  }

}
