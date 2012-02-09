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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.solr;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Implementation of {@link org.ambraproject.solr.SolrHttpService} that uses URL objects to make http requests to
 * the solr server.
 * <p/>
 * This bean should be injected via spring to the action classes that make use of it, so there will only be one copy of
 * the bean per ambra instance
 * <p/>
 * @author Alex Kudlick Date: Feb 15, 2011
 * <p/>
 * org.ambraproject.solr
 */
public class SolrHttpServiceImpl implements SolrHttpService {

  private static final Logger log = LoggerFactory.getLogger(SolrHttpServiceImpl.class);
  private String solrUrl;
  private Configuration config;

  private static final String XML = "xml";
  private static final String URL_CONFIG_PARAM = "ambra.services.search.server.url";
  private static final String RETURN_TYPE_PARAM = "wt";
  private static final String Q_PARAM = "q";
  private static final String NO_FILTER = "*:*";
  /**
   * number of milliseconds to wait on a url connection to SOLR
   */
  private static final int CONNECTION_TIMEOUT = 100;

  @Override
  public Document makeSolrRequest(Map<String, String> params) throws SolrException {
    if (solrUrl == null || solrUrl.isEmpty()) {
      setSolrUrl(config.getString(URL_CONFIG_PARAM));
    }

    //make sure the return type is xml
    if (!params.keySet().contains(RETURN_TYPE_PARAM) || !params.get(RETURN_TYPE_PARAM).equals(XML)) {
      params.put(RETURN_TYPE_PARAM, XML);
    }
    //make sure that we include a 'q' parameter
    if (!params.keySet().contains(Q_PARAM)) {
      params.put(Q_PARAM, NO_FILTER);
    }

    String queryString = "?";
    for (String param : params.keySet()) {
      String value = params.get(param);
      if (queryString.length() > 1) {
        queryString += "&";
      }
      queryString += (cleanInput(param) + "=" + cleanInput(value));
    }

    URL url;
    String urlString = solrUrl + queryString;
    log.debug("Making Solr http request to " + urlString);
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      throw new SolrException("Bad Solr Url: " + urlString, e);
    }

    InputStream urlStream = null;
    Document doc = null;
    try {
      URLConnection connection = url.openConnection();
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      connection.connect();
      urlStream = connection.getInputStream();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(urlStream);
    } catch (IOException e) {
      throw new SolrException("Error connecting to the Solr server at " + solrUrl, e);
    } catch (ParserConfigurationException e) {
      throw new SolrException("Error configuring parser xml parser for solr response", e);
    } catch (SAXException e) {
      throw new SolrException("Solr Returned bad XML for url: " + urlString, e);
    } finally {
      //Close the input stream
      if (urlStream != null) {
        try {
          urlStream.close();
        } catch (IOException e) {
          log.error("Error closing url stream to Solr", e);
        }
      }
    }

    return doc;
  }

  /**
   * Clean a string input for insertion into a url
   *
   * @param param
   * @return
   */
  private String cleanInput(String param) {
    try {
      return URLEncoder.encode(param, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return param;
    }
  }

  public void setSolrUrl(String solrUrl) {
    if (solrUrl.contains("/select")) {
      this.solrUrl = solrUrl;
    } else {
      this.solrUrl = solrUrl.endsWith("/") ? solrUrl + "select" : solrUrl + "/select";
    }
    this.solrUrl = this.solrUrl.replaceAll("\\?", "");
  }

  public void setConfig(Configuration config) {
    this.config = config;
  }


}
