/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.solr;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseHttpTest;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick 9/16/11
 */
public class SolrHttpServiceTest extends BaseHttpTest {

  @Autowired
  protected SolrHttpService solrHttpService;


  @DataProvider(name = "requestParameters")
  public Object[][] getRequestParameters(){
    Map<String, String> params1 = new HashMap<String, String>();
    params1.put("q", "mosquitoes");

    Map<String, String> params2 = new HashMap<String, String>();
    params2.put("fl", "id,title,abstract");
    params2.put("fq", "doc_type:full AND !article_type_facet:\"Issue Image\"");

    return new Object[][]{
        {params1},
        {params2}
    };
  }

  @Test(dataProvider = "requestParameters")
  public void testMakeRequest(final Map<String, String> params) throws InterruptedException, SolrException {
    //define behaviour for the http server
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        //check that we got sent the correct headers
        Map<String, Object> headers = exchange.getIn().getHeaders();
        if (!params.containsKey("q")) {
          assertEquals(headers.get("q"), "*:*", "http request didn't get correct default 'q' parameter");
        }
        for (String key : params.keySet()) {
          assertTrue(headers.containsKey(key), "Http request didn't contain parameter: " + key);
          assertEquals(headers.get(key), params.get(key), "Http request didn't have correct value for header: " + key);
        }
        //return a valid xml response so the bean can parse it
        exchange.getOut().setBody(testSolrXml);
      }
    });
    Document result = solrHttpService.makeSolrRequest(params);
    assertNotNull(result, "returned null document");
    assertEquals(result.getElementsByTagName("doc").getLength(), 10,
        "didn't parse xml for correct number of result nodes");
  }
}
