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
import org.testng.annotations.BeforeClass;
import org.ambraproject.ApplicationException;
import static org.easymock.EasyMock.*;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * @author Dragisa Krsmanovic
 */
public class MessageServiceImplTest {

  private static final String DOI = "info:doi/123.456/journal.plosone.1234";
  private static final String archive = "journal.plosone.1234.zip";

  private Configuration configuration;
  private static final String ADDITIONAL_BODY1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<ambraMessage>" +
        "<doi>" + DOI + "</doi>" +
        "<archive>" + archive + "</archive>" +
        "<pmc>" +
          "<mailto>test@plos.org</mailto>" +
          "<mailtoCC>testCC@plos.org</mailtoCC>" +
          "<mailtoSender>do-not-reply@plos.org</mailtoSender>" +
        "</pmc>" +
      "</ambraMessage>";

  private static final String ADDITIONAL_BODY2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<ambraMessage>" +
        "<doi>" + DOI + "</doi>" +
        "<archive>" + archive + "</archive>" +
      "</ambraMessage>";

  @BeforeClass
  public void readConfiguration() throws ConfigurationException {
    String fileName = getClass().getClassLoader().getResource("queue/configuration.xml").getFile();
    configuration = new XMLConfiguration(fileName);
  }

  @Test
  public void sendSyndicationMessage() throws ApplicationException {

    MessageServiceImpl service = new MessageServiceImpl();
    MessageSender sender = createMock(MessageSender.class);
    sender.sendMessage(eq("activemq:plos.pmc"), eq(ADDITIONAL_BODY1));
    expectLastCall().once();
    service.setSender(sender);
    service.setAmbraConfiguration(configuration);
    replay(sender);

    service.sendSyndicationMessage("PMC", DOI, archive);

    verify(sender);
  }

  @Test
  public void sendSyndicationMessageNoAdditionalBody() throws ApplicationException {

    MessageServiceImpl service = new MessageServiceImpl();
    MessageSender sender = createMock(MessageSender.class);
    sender.sendMessage(eq("activemq:plos.pmc"), eq(ADDITIONAL_BODY2));
    expectLastCall().once();
    service.setSender(sender);
    service.setAmbraConfiguration(configuration);
    replay(sender);

    service.sendSyndicationMessage("FOO", DOI, archive);

    verify(sender);
  }

  @Test(expectedExceptions = {ApplicationException.class})
  public void sendSyndicationMessageNoQueueDefined() throws ApplicationException {

    MessageServiceImpl service = new MessageServiceImpl();
    MessageSender sender = createMock(MessageSender.class);
    service.setSender(sender);
    service.setAmbraConfiguration(configuration);
    replay(sender);

    service.sendSyndicationMessage("BAR", DOI, archive);

    verify(sender);
  }


}
