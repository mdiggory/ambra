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

package org.ambraproject.article.service;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseHttpTest;
import org.ambraproject.solr.SolrException;
import org.ambraproject.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test the {@link MostViewedArticleService}.  There are two aspects to this:
 * <p/>
 * <ol> <li>That you make the correct solr request</li> <li>That you correctly parse the results</li> </ol>
 *
 * @author Alex Kudlick 9/19/11
 */
public class MostViewedArticleServiceTest extends BaseHttpTest {

  @Autowired
  protected MostViewedArticleService mostViewedArticleService;

  private static final String EXPECTED_FQ_PARAM = "doc_type:full AND !article_type_facet:\"Issue Image\" AND cross_published_journal_key:journal";
  private static final String JOURNAL = "journal";
  private static final int NUM_DAYS = 14;
  private static final String VIEW_FIELD = "two_week_field";

  @DataProvider(name = "mostViewed")
  public Object[][] mostViewedDataProvider() throws IOException {
    List<Pair<String, String>> expectedResults = new ArrayList<Pair<String, String>>(10);
    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010001",
        "Ab Initio Prediction of Transcription Factor Targets Using Structural Knowledge"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010002",
        "What Makes Ribosome-Mediated Transcriptional Attenuation Sensitive to Amino Acid Limitation?"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010003",
        "Predicting Functional Gene Links from Phylogenetic-Statistical Analyses of Whole Genomes"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010004",
        "<i>PLoS Computational Biology:</i> A New Community Journal"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010005",
        "An Open Forum for Computational Biology"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010006",
        "“Antedisciplinary” Science"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010007",
        "Susceptibility to Superhelically Driven DNA Duplex Destabilization: A Highly Conserved Property of Yeast Replication Origins"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010008",
        "Combinatorial Pattern Discovery Approach for the Folding Trajectory Analysis of a <i>β</i>-Hairpin"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010009",
        "Improving the Precision of the Structure–Function Relationship by Considering Phylogenetic Context"));

    expectedResults.add(new Pair<String, String>(
        "10.1371/journal.pcbi.0010010",
        "Extraction of Transcript Diversity from Scientific Literature"));

    return new Object[][]{
        {expectedResults}
    };
  }

  @Test(dataProvider = "mostViewed")
  public void testGetMostViewed(List<Pair<String, String>> expectedResults) throws SolrException {
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        assertEquals(in.getHeader("sort", String.class), VIEW_FIELD + " desc", "solr request didn't have correct sort field");
        assertEquals(in.getHeader("fq", String.class), EXPECTED_FQ_PARAM, "solr request didn't have correct fq param");

        exchange.getOut().setBody(testSolrXml);
      }
    });

    //Check that the solr xml was parsed correctly
    List<Pair<String, String>> mostViewedArticles = mostViewedArticleService.getMostViewedArticles(JOURNAL, 10, NUM_DAYS);
    assertNotNull(mostViewedArticles, "returned null list of most viewed articles");
    assertEquals(mostViewedArticles.size(), expectedResults.size(), "returned incorrect number of articles");
    for (int i = 0; i < mostViewedArticles.size(); i++) {
      Pair<String, String> actual = mostViewedArticles.get(i);
      Pair<String, String> expected = expectedResults.get(i);
      assertEquals(actual.getFirst(),expected.getFirst(),"Didn't have correct doi for entry " + i);
      assertEquals(actual.getSecond(),expected.getSecond(),"Didn't have correct title for entry " + i);
    }
  }
}
