/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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

package org.ambraproject.solr;

import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.article.service.MostViewedArticleService;
import org.ambraproject.util.Pair;
import org.ambraproject.util.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alex Kudlick Date: 4/19/11
 *         <p/>
 *         org.ambraproject.solr
 */
public class SolrMostViewedArticleService implements MostViewedArticleService {
  private SolrFieldConversion solrFieldConverter;
  private SolrHttpService solrHttpService;
  /**
   * Cache for the most viewed results. This is a one-off caching implementation, but since this is a spring-injected
   * bean, there will only be one cache per ambra instance. Also, the cache will contain 2 strings per article (doi and
   * title) for each of the journals, which is 100 (relatively small) strings if the limit for articles is 5 and there
   * are 10 journals
   */
  private ConcurrentMap<String, MostViewedCache> cachedMostViewedResults = new ConcurrentHashMap<String, MostViewedCache>();
  private static final String NAME_ATTR = "name";
  private static final String DOI_ATTR = "id";
  private static final String TITLE_ATTR = "title_display";

  @Override
  public List<Pair<String, String>> getMostViewedArticles(String journal, int limit, Integer numDays) throws SolrException {
    //check if we still have valid results in the cache
    MostViewedCache cache = cachedMostViewedResults.get(journal);
    if (cache != null && cache.isValid()) {
      return cache.getArticles();
    }

    Map<String, String> params = new HashMap<String, String>();
    params.put("fl", DOI_ATTR + "," + TITLE_ATTR);
    params.put("fq", "doc_type:full AND !article_type_facet:\"Issue Image\" AND cross_published_journal_key:" + journal);
    params.put("start", "0");
    params.put("rows", String.valueOf(limit));
    params.put("indent", "off");
    String sortField = (numDays != null) ? solrFieldConverter.getViewCountingFieldName(numDays)
        : solrFieldConverter.getAllTimeViewsField();
    params.put("sort", sortField + " desc");

    Document doc = solrHttpService.makeSolrRequest(params);

    List<Pair<String, String>> articles = new ArrayList<Pair<String, String>>(limit);

    //get the children of the "result" node
    XPath xPath = XPathFactory.newInstance().newXPath();
    try {
      Integer count = Integer.valueOf(xPath.evaluate("count(//result/doc)", doc));
      for (int i = 1; i <= count; i++) {
        String doi = xPath.evaluate("//result/doc[" + i + "]/str[@name = '" + DOI_ATTR + "']/text()", doc);
        String title = xPath.evaluate("//result/doc[" + i + "]/str[@name = '" + TITLE_ATTR + "']/text()", doc);
        articles.add(new Pair<String, String>(doi, title));
      }
    } catch (XPathExpressionException e) {
      throw new SolrException("Error parsing solr xml response", e);
    }

    //cache the results
    cachedMostViewedResults.put(journal, new MostViewedCache(articles));
    return articles;
  }

  @Required
  public void setSolrFieldConverter(SolrFieldConversion solrFieldConverter) {
    this.solrFieldConverter = solrFieldConverter;
  }

  @Required
  public void setSolrHttpService(SolrHttpService solrHttpService) {
    this.solrHttpService = solrHttpService;
  }
}
