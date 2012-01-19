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

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import org.apache.camel.ProducerTemplate;

/**
 * @author Dragisa Krsmanovic
 */
public class CamelSenderTest {
  private static final String DESTINATION = "destination";
  private static final String TEXT = "body";

  @Test
  public void sendTextMessage() {
    CamelSender sender = new CamelSender();
    ProducerTemplate producer = createMock(ProducerTemplate.class);
    sender.setProducerTemplate(producer);

    producer.sendBody(eq(DESTINATION), eq(TEXT));
    expectLastCall().once();
    replay(producer);

    sender.sendMessage(DESTINATION, TEXT);

    verify(producer);
  }

}
