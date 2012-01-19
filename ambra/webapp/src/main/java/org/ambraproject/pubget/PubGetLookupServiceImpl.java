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

package org.ambraproject.pubget;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Calls PubGet Widgetizer service.
 *
 * @author Dragisa Krsmanovic
 */
public class PubGetLookupServiceImpl implements PubGetLookupService {

  private static final Logger log = LoggerFactory.getLogger(PubGetLookupServiceImpl.class);


  private HttpClient httpClient;
  private String pubGetUrl;

  @Required
  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public void setPubGetUrl(String pubGetUrl) {
    this.pubGetUrl = pubGetUrl;
  }

  public String getPDFLink(String doi) throws Exception {

    String pdfLink = null;

    if (StringUtils.isNotBlank(pubGetUrl)) {

      String query = pubGetUrl + "?oa_only=true&dois=" + doi;

      log.debug("Executing query " + query);

      GetMethod getter = new GetMethod(query);

      try {
        long timestamp = System.currentTimeMillis();

        int response = httpClient.executeMethod(getter);
        
        if (log.isDebugEnabled()) {
          log.debug("Http call finished in " + (System.currentTimeMillis() - timestamp) + " ms");
        }

        if (response == 200) {
          String responseBody = getter.getResponseBodyAsString();
          log.debug("Response body: " + responseBody);

          int jsonStart = responseBody.indexOf("[");

          if (StringUtils.isNotBlank(responseBody) && jsonStart >= 0) {

            responseBody = responseBody.substring(jsonStart);

            Gson gson = new Gson();
            PubGetArticle[] articles = gson.fromJson(responseBody, PubGetArticle[].class);

            if (articles.length > 0) {
              pdfLink = articles[0].getValues().getLink();
              log.debug("Found PubGet link: " + pdfLink);
            }

          } else {
            log.warn("Result not in JSON format: " + responseBody);
          }

        } else {
          log.error("Received response code " + response + " when executing query " + pubGetUrl);
        }

      } finally {
        // be sure the connection is released back to the connection manager
        getter.releaseConnection();
      }
    } else {
      log.warn("PubGetHost is not configured");
    }

    return pdfLink;
  }

}
