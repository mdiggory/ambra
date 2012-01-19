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

import org.springframework.beans.factory.annotation.Required;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.ConfigurationKey;
import org.apache.commons.configuration.XMLConfiguration;
import org.ambraproject.ApplicationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author Dragisa Krsmanovic
 */
public class MessageServiceImpl implements MessageService {

  private MessageSender sender;

  private Configuration ambraConfiguration;

  @Required
  public void setSender(MessageSender sender) {
    this.sender = sender;
  }

  @Required
  public void setAmbraConfiguration(Configuration ambraConfiguration) {
    this.ambraConfiguration = ambraConfiguration;

  }

  public void sendSyndicationMessage(String target, String articleId, String archive)
      throws ApplicationException {
    List<HierarchicalConfiguration> syndications = ((HierarchicalConfiguration) ambraConfiguration)
        .configurationsAt("ambra.services.syndications.syndication");

    String queue = null;
    String additionalBodyContent = null;
    if (syndications != null) {
      for (HierarchicalConfiguration syndication : syndications) {
        if (target.equals(syndication.getString("[@target]"))) {
          queue = syndication.getString("queue", null);
          HierarchicalConfiguration message = (HierarchicalConfiguration) syndication.subset("message");
          additionalBodyContent = createAdditionalBodyFromConfiguration(message);
        }
      }
    }

    if (queue == null) {
      throw new ApplicationException(target + " queue not configured");
    }

    sender.sendMessage(queue, createBody(articleId, archive, additionalBodyContent));

  }

  private String createBody(String articleId, String archive, String additionalBodyContent) {
    StringBuilder body = new StringBuilder();
    body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        .append("<ambraMessage>")
        .append("<doi>").append(articleId).append("</doi>")
        .append("<archive>").append(archive).append("</archive>");

    if (additionalBodyContent != null) {
      body.append(additionalBodyContent);
    }

    body.append("</ambraMessage>");

    return body.toString();
  }

  /**
   * If there is content inside <syndication><message> configuration append it to message.
   *
   * @param configuration Sub configuration under "message" tag.
   * @return XML code snippet.
   */
  private static String createAdditionalBodyFromConfiguration(HierarchicalConfiguration configuration) {
    if (configuration == null || configuration.isEmpty()) {
      return null;
    }
    Visitor visitor = new Visitor();
    configuration.getRoot().visit(visitor, new ConfigurationKey(""));
    return visitor.body.toString();
  }

  /**
   * Visitor class for rendering XML string from configuration.
   * Warning: will not handle XML attributes in configuration.
   */
  private static class Visitor extends HierarchicalConfiguration.NodeVisitor {
    private StringBuilder body = new StringBuilder();

    @Override
    public void visitBeforeChildren(HierarchicalConfiguration.Node node, ConfigurationKey configurationKey) {
      String name = node.getName();
      if (name != null) {
        body.append('<').append(name).append('>');
      }
      Object value = node.getValue();
      if (value != null) {
        body.append((String) value);
      }
      super.visitBeforeChildren(node, configurationKey);
    }

    @Override
    public void visitAfterChildren(HierarchicalConfiguration.Node node, ConfigurationKey configurationKey) {
      String name = node.getName();
      if (name != null) {
        body.append("</").append(name).append('>');
      }
      super.visitAfterChildren(node, configurationKey);
    }
  }

}
