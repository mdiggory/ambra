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

import org.w3c.dom.Document;

/**
 * Message sender interface. 
 * @author Dragisa Krsmanovic
 */
public interface MessageSender {

  /**
   * Send a text message.
   * @param destination URL of the destination "activemq:plos.pmc" fro example.
   * @param body Message body,
   */
  void sendMessage(String destination, String body);

  /**
   * Send a DOM Document message.
   * @param destination URL of the destination "activemq:plos.pmc" fro example.
   * @param body Message body,
   */
  void sendMessage(String destination, Document body);
}
