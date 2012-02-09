/* $HeadURL::                                                                            $
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
package org.topazproject.ambra.doi;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topazproject.ambra.configuration.ConfigurationStore;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * The ResolverServlet attempts to translate the DOI in the incoming request into a URI on the ambra server that will
 * display the DOI correctly. Thus far, it handles Article and Annotation DOIs. It makes a call to the semantic DB to
 * determine the type(s) of the given DOI. Depending on the type of the resource described by the DOI, the servlet
 * matches the doi to regular expressions for each known journal (defined in  /etc/ambra/ambra.xml or the hierarchy of
 * config files). Each regular expression maps to a URI on the ambra server that will display the resource described by
 * the given DOI.   In the case of annotation DOIs, they do not contain a reference to the journal, so the annotated
 * root article  must be found first in order to calculate the correct journal base URL.
 * <p/>
 * TODO: This should be re-implemented to be more generic so it can be more easily configured to support more types of
 * DOIs. Assumptions should not be made about the DOI format  containing a reference to the journal. The known journals
 * should be better defined. Better still, the DOI Resolver should simply check for the existence of the DOI in the
 * ambra server and if so, forward to the ambra server.  In turn, ambra server should know how to display the
 * appropriate result for any given DOI.
 *
 * @author Stephen Cheng
 * @author Alex Worden
 */
public class ResolverServlet extends HttpServlet {
  private static final Logger log = LoggerFactory.getLogger(ResolverServlet.class);
  private static final Configuration myConfig = ConfigurationStore.getInstance().getConfiguration();
  private static final String INFO_DOI_PREFIX = myConfig.getString("ambra.aliases.doiPrefix");

  private Pattern[] journalRegExs;
  private Pattern[] figureRegExs;
  private Pattern[] repRegExs;
  private String[] urls;
  private int numJournals;
  private String errorPage;

  private ResolverDAOService resolverDAOService;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    resolverDAOService = (ResolverDAOService) getServletContext().getAttribute("resolverDAOService");
    //initialize the journal regexes
    numJournals = myConfig.getList("ambra.services.doiResolver.mappings.journalMapping.url").size();
    urls = new String[numJournals];
    figureRegExs = new Pattern[numJournals];
    journalRegExs = new Pattern[numJournals];
    repRegExs = new Pattern[numJournals];

    for (int i = 0; i < numJournals; i++) {
      urls[i] = myConfig.getString("ambra.services.doiResolver.mappings.journalMapping(" + i + ").url");

      StringBuilder pat = new StringBuilder("/").
          append(myConfig.getString("ambra.services.doiResolver.mappings.journalMapping(" + i + ").regex"));
      journalRegExs[i] = Pattern.compile(pat.toString());

      pat = new StringBuilder("/").append(myConfig.
          getString("ambra.services.doiResolver.mappings.journalMapping(" + i + ").figureRegex"));
      figureRegExs[i] = Pattern.compile(pat.toString());

      pat = new StringBuilder("/").append(myConfig.
          getString("ambra.services.doiResolver.mappings.journalMapping(" + i + ").repRegex"));
      repRegExs[i] = Pattern.compile(pat.toString());

    }
    errorPage = myConfig.getString("ambra.platform.webserverUrl") +
        myConfig.getString("ambra.platform.errorPage");

    if (log.isTraceEnabled()) {
      for (int i = 0; i < numJournals; i++) {
        log.trace("JournalRegEx: " + journalRegExs[i].toString() + "  ; figureRegEx: " +
            figureRegExs[i].toString() + "  ; url: " + urls[i]);
      }

      log.trace(("Error Page is: " + errorPage));
    }
  }

  /**
   * Tries to resolve a Ambra doi from CrossRef into an application specific URL First, tries to make sure the DOI looks
   * like it is properly formed.  If it looks like an article DOI, will attempt to do a type lookup in mysql.  If it
   * looks like a figure or a table, will attempt to construct an article DOI and do that lookup. Otherwise, will fail
   * and send to Ambra Page not Found error page.
   *
   * @param req  the servlet request
   * @param resp the servlet response
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String doi = req.getPathInfo();

    if (log.isTraceEnabled()) {
      log.trace("Incoming doi = " + doi);
    }

    if (doi == null) {
      failWithError(resp);

      return;
    }

    try {
      doi = URLDecoder.decode(doi.trim(), "UTF-8");
    } catch (UnsupportedEncodingException uee) {
      doi = doi.trim();
    }

    try {
      String redirectURL = constructURL(doi);

      log.debug("DOI ResolverServlet sending redirect to URL: {}", redirectURL);

      resp.sendRedirect(redirectURL);
    } catch (Exception e) {
      log.warn("Could not resolve doi: " + doi, e);
      failWithError(resp);
    }
  }

  /**
   * Just forwards to the Ambra Page Not Found error page
   *
   * @param req  the servlet request
   * @param resp the servlet response
   */
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    failWithError(resp);
  }

  private boolean doiIsArticle(String doi) {
    URI doiURI;
    //Check that the doi is well-formed
    try {
      doiURI = new URI(INFO_DOI_PREFIX + doi);
    } catch (URISyntaxException use) {
      log.warn("Couldn't create URI for doi:" + doi, use);
      return false;
    }

    try {
      return resolverDAOService.doiIsArticle(doiURI.toString());
    } catch (Exception e) {
      log.error("Error checking type for " + doiURI.toString(), e);
      return false;
    }
  }

  private boolean doiIsAnnotation(String doi) {
    URI doiURI;
    //Check that the doi is well-formed
    try {
      doiURI = new URI(INFO_DOI_PREFIX + doi);
    } catch (URISyntaxException use) {
      log.warn("Couldn't create URI for doi:" + doi, use);
      return false;
    }

    try {
      return resolverDAOService.doiIsAnnotation(doiURI.toString());
    } catch (Exception e) {
      log.error("Error checking type for " + doiURI.toString(), e);
      return false;
    }
  }

  private String constructURL(String doi) {
    StringBuilder redirectURL;

    Pattern journalRegEx;
    Pattern figureRegEx;
    Pattern repRegEx;

    for (int i = 0; i < numJournals; i++) {
      journalRegEx = journalRegExs[i];
      figureRegEx = figureRegExs[i];
      repRegEx = repRegExs[i];

      if (journalRegEx.matcher(doi).matches()) {

        if (doiIsArticle(doi)) {
          redirectURL = new StringBuilder(urls[i]);

          try {
            redirectURL.append(myConfig.getString("ambra.platform.articleAction")).
                append(URLEncoder.encode(INFO_DOI_PREFIX, "UTF-8")).
                append(URLEncoder.encode(doi, "UTF-8"));
          } catch (UnsupportedEncodingException uee) {
            log.debug("Couldn't encode URL with UTF-8 encoding", uee);

            redirectURL.append(myConfig.getString("ambra.platform.articleAction")).
                append(URLEncoder.encode(INFO_DOI_PREFIX)).append(URLEncoder.encode(doi));
          }

          log.debug("Matched: {}; redirecting to: {}", doi, redirectURL);

          return redirectURL.toString();
        }
      }

      if (figureRegEx.matcher(doi).matches()) {
        String possibleArticleDOI = doi.substring(0, doi.length() - 5);

        if (doiIsArticle(possibleArticleDOI)) {
          redirectURL = new StringBuilder(urls[i]);
          redirectURL.append(myConfig.getString("ambra.platform.figureAction1")).append(INFO_DOI_PREFIX).
              append(possibleArticleDOI).append(myConfig.getString("ambra.platform.figureAction2")).
              append(INFO_DOI_PREFIX).append(doi);

          log.debug("Matched: {}; redirecting to: {}", doi, redirectURL);

          return redirectURL.toString();
        }
      }

      if (repRegEx.matcher(doi).matches()) {
        // example doi string
        // 10.1371/journal.pmed.1000435.pdf
        int index = doi.lastIndexOf(".");
        String possibleArticleDOI = doi.substring(0, index);
        String representation = doi.substring(index + 1).toUpperCase();

        if (doiIsArticle(possibleArticleDOI)) {
          redirectURL = new StringBuilder(urls[i]);

          try {
            // example url
            // plosmedicine.org/article/fetchObjectAttachment.action?uri=info%3Adoi%2F10.1371%2Fjournal.pmed.1000435&representation=PDF
            redirectURL.append(myConfig.getString("ambra.platform.fetchObjectAction")).
                append(URLEncoder.encode(INFO_DOI_PREFIX, "UTF-8")).
                append(URLEncoder.encode(possibleArticleDOI, "UTF-8")).
                append("&").append(URLEncoder.encode("representation", "UTF-8")).append("=").
                append(URLEncoder.encode(representation, "UTF-8"));
          } catch (UnsupportedEncodingException uee) {
            log.debug("Couldn't encode URL with UTF-8 encoding", uee);

            redirectURL.append(myConfig.getString("ambra.platform.fetchObjectAction")).
                append(URLEncoder.encode(INFO_DOI_PREFIX)).append(URLEncoder.encode(possibleArticleDOI)).
                append("&").append(URLEncoder.encode("representation")).append("=").
                append(URLEncoder.encode(representation));
          }

          log.debug("Matched: {}; redirecting to: {}", doi, redirectURL);

          return redirectURL.toString();
        }
      }
    }

    String doiUriStr = INFO_DOI_PREFIX + doi;

    if (doiIsAnnotation(doi)) {
      try {
        String annotatedRoot = resolverDAOService.getAnnotatedRoot(doiUriStr);
        String rootUri = matchDoiToJournal(annotatedRoot);

        if (rootUri != null) {
          StringBuilder urlBuf = new StringBuilder(rootUri);
          urlBuf.append(myConfig.getString("ambra.platform.annotationAction")).
              append(URLEncoder.encode(doiUriStr, "UTF-8"));

          return urlBuf.toString();
        }
      } catch (Exception e) {
        log.error("Exception occurred attempting to resolve doi '" + doiUriStr + "' to an annotation", e);

        return errorPage;
      }
    }

    log.debug("Could not match: {}; redirecting to: {}", doiUriStr, errorPage);

    return errorPage;
  }

  private String matchDoiToJournal(String doi) {
    if (doi.startsWith(INFO_DOI_PREFIX)) {
      doi = doi.substring(INFO_DOI_PREFIX.length());
    }

    for (int i = 0; i < journalRegExs.length; i++) {
      if (journalRegExs[i].matcher(doi).matches()) {
        return urls[i];
      }
    }

    return null;
  }

  private void failWithError(HttpServletResponse resp) {
    try {
      resp.sendRedirect(errorPage);
    } catch (Exception e) {
      log.warn("Couldn't redirect user to error page", e);
    }
  }
}
