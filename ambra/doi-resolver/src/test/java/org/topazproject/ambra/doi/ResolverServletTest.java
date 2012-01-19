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

package org.topazproject.ambra.doi;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.topazproject.ambra.configuration.ConfigurationStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import static org.testng.Assert.assertEquals;

/**
 * Test for the {@link ResolverServlet}
 *
 * @author alex 9/7/11
 */
public class ResolverServletTest extends BaseResolverTest {

  private static final String ERROR_PAGE = "http://ambrajournal.example.org/static/pageNotFound.action";
  private static final String FIRST_JOURNAL_URL = "http://ambrajournal.example.org/";
  private static final String SECOND_JOURNAL_URL = "http://overlayjournal.example.org/";

  private ResolverServlet resolverServlet;

  @BeforeClass(dependsOnMethods = "createDB")
  public void setup() throws ConfigurationException, ServletException {
    URL configLocation = getClass().getClassLoader().getResource("test-config.xml");
    ConfigurationStore.getInstance().loadConfiguration(configLocation);
    resolverServlet = new ResolverServlet();

    //Initialize it the servlet
    MockServletConfig servletConfig = new MockServletConfig();
    servletConfig.getServletContext().setAttribute("resolverDAOService", new JdbcResolverService(dataSource));
    resolverServlet.init(servletConfig);
  }

  @DataProvider(name = "dois")
  public Object[][] getDois() {
    insertArticleRow("info:doi/10.1371/ambr.1234567");
    insertArticleRow("info:doi/10.1371/ovrj.v06.i09");
    insertArticleRow("info:doi/10.1371/ovrj.1234567");
    insertAnnotationRow("info:doi/10.1371/test-annotation", "info:doi/10.1371/ovrj.1234567");

    MockHttpServletRequest article1 = new MockHttpServletRequest();
    article1.setPathInfo("/10.1371/ambr.1234567");

    MockHttpServletRequest encodedArticle1 = new MockHttpServletRequest();
    encodedArticle1.setPathInfo("%2F10.1371%2Fambr.1234567");

    MockHttpServletRequest article2 = new MockHttpServletRequest();
    article2.setPathInfo("%2F10.1371%2Fovrj.v06.i09");

    MockHttpServletRequest figure = new MockHttpServletRequest();
    figure.setPathInfo("%2F10.1371%2Fambr.1234567.g001");

    MockHttpServletRequest representation = new MockHttpServletRequest();
    representation.setPathInfo("%2F10.1371%2Fovrj.1234567.pdf");

    MockHttpServletRequest annotation = new MockHttpServletRequest();
    annotation.setPathInfo("%2F10.1371%2Ftest-annotation");

    return new Object[][]{
        {article1, FIRST_JOURNAL_URL + "article/info%3Adoi%2F10.1371%2Fambr.1234567"},
        {encodedArticle1, FIRST_JOURNAL_URL + "article/info%3Adoi%2F10.1371%2Fambr.1234567"},
        {figure, FIRST_JOURNAL_URL + "article/slideshow.action?uri=info:doi/10.1371/ambr.1234567" +
            "&imageURI=info:doi/10.1371/ambr.1234567.g001"},
        {article2, SECOND_JOURNAL_URL + "article/info%3Adoi%2F10.1371%2Fovrj.v06.i09"},
        {representation, SECOND_JOURNAL_URL + "article/fetchObjectAttachment.action?" +
            "uri=info%3Adoi%2F10.1371%2Fovrj.1234567&representation=PDF"},
        {annotation, SECOND_JOURNAL_URL + "annotation/info%3Adoi%2F10.1371%2Ftest-annotation"}
    };
  }

  @Test(dataProvider = "dois")
  public void testDoGet(HttpServletRequest request, String expectedRedirect) {
    MockHttpServletResponse response = new MockHttpServletResponse();
    resolverServlet.doGet(request, response);
    assertEquals(response.getRedirectedUrl(), expectedRedirect, "servlet didn't redirect correctly");
  }

  @DataProvider(name = "badRequests")
  public Object[][] getBadRequests() {
    MockHttpServletRequest request1 = new MockHttpServletRequest();
    request1.setPathInfo("%2F10.1371%2Fbogus-doi");

    MockHttpServletRequest request2 = new MockHttpServletRequest();
    request2.setPathInfo("%2F10.1371%2Fovrj.v06.i09.pdf");

    return new Object[][]{
        {request1},
        {request2}
    };
  }

  @Test(dataProvider = "badRequests", dependsOnMethods = {"testDoGet"})
  public void testBadRequests(HttpServletRequest request) {
    MockHttpServletResponse response = new MockHttpServletResponse();
    resolverServlet.doGet(request, response);
    assertEquals(response.getRedirectedUrl(), ERROR_PAGE, "servlet didn't redirect to error page");
  }

  @Test
  public void testDoPost() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    resolverServlet.doPost(request, response);
    assertEquals(response.getRedirectedUrl(), ERROR_PAGE,
        "Servlet didn't redirect to error page on doPost()");
  }
}
