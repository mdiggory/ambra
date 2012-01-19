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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.search.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.queue.MessageSender;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.xpath.*;

/**
 * DummyMessageSender for sending messages to SOLR.  In the normal world this class is actually a proxy to the
 * queue.  But being we only want to make sure articles are being indexed here.  This just acts as a thin
 * wrapper to the SOLR server
 */

public class DummySOLRMessageSender implements MessageSender {
  private SolrServer solrServer;

  private static final String indexQueue = "activemq:plos.solr.article.index?transacted=false";
  private static final String deleteQueue = "seda:search.delete";
  private static final String indexPublishQueue = "seda:search.index";
  private static final String indexAll = "seda:search.indexall";

  @Required
  public void setSolrServerFactory(SolrServerFactory solrServerFactory)
  {
    solrServer = solrServerFactory.getServer();
  }

  /**
   * Send a text message.
   * @param destination URL of the destination "activemq:plos.pmc" fro example.
   * @param body Message body,
   */
  public void sendMessage(String destination, String body)
  {
      //Don't worry about index all.  (Thats a queue task)  But we can check the delete here
      if(deleteQueue.equals(destination)) {
        try {
          String solrID = body.replaceAll("info:doi/","");
          solrServer.deleteById(solrID);
          solrServer.commit();
        } catch (Exception ex) {
          throw new RuntimeException(ex.getMessage(), ex);
        }
      }

  }

  /**
   * Send a DOM Document message.
   * @param destination URL of the destination "activemq:plos.pmc" for example.
   * @param body Message body,
   */
  public void sendMessage(String destination, Document body)
  {
    if(indexQueue.equals(destination) ||
      indexPublishQueue.equals(destination)
      ) {
      try {
        //We just want to confirm that SOLR gets something, so lets just index article ID and eIssn

        SolrInputDocument sid = new SolrInputDocument();

        Node res = XPathSingleNodeQuery(body, "//article/front/article-meta/article-id[@pub-id-type='doi']/text()");
        sid.addField("id", res.getTextContent());

        res = XPathSingleNodeQuery(body, "//article/front/journal-meta/issn[@pub-type='epub']/text()");
        sid.addField("eissn", res.getTextContent());

        solrServer.add(sid);
        solrServer.commit();

      } catch (Exception ex) {
        throw new RuntimeException(ex.getMessage(), ex);
      }
    }

  }

  private Node XPathSingleNodeQuery(Document dom, String statement) throws XPathExpressionException {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile(statement);

    return (Node)expr.evaluate(dom, XPathConstants.NODE);
  }
}
