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
package org.ambraproject.web;

import java.io.IOException;
import java.io.File;

import java.net.MalformedURLException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.configuration.Configuration;
import org.topazproject.ambra.configuration.ConfigurationStore;

/**
 * A Filter that maps incoming URI Requests to an appropriate virtual journal resources. If a
 * virtual journal context is set, a lookup is done to see if an override for the requested
 * resource exists for the virtual journal.  If so, the Request is wrapped with the override
 * values and passed on to the FilterChain.  If not, the Request is wrapped with default values
 * for the resource and then passed to the FilterChain.
 */
public class VirtualJournalMappingFilter implements Filter {
  private static final Logger log            = LoggerFactory.getLogger(VirtualJournalMappingFilter.class);
  private ServletContext   servletContext = null;
  private Configuration configuration = null;

  /*
   * @see javax.servlet.Filter#init
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    // need ServletContext to get "real" path/file names
    this.servletContext = filterConfig.getServletContext();
    this.configuration = ConfigurationStore.getInstance().getConfiguration();
  }

  /*
   * @see javax.servlet.Filter#destroy
   */
  public void destroy() {

  }

  /*
   * @see javax.servlet.Filter#doFilter
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest r      = (HttpServletRequest) request;
      HttpServletRequest mapped = mapRequest(r);

      if (log.isDebugEnabled()) {
        if (mapped == r)
          log.debug("Passed thru unchanged " + r.getRequestURI());
        else
          log.debug("Mapped " + r.getRequestURI() + " to " + mapped.getRequestURI());
      }

      request = mapped;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Map the request to a resource in the journal context.. If resource exists within the
   * virtual journal context, return a WrappedRequest with the mappingPrefix,  else return a
   * WrappedRequest for the default resource.
   *
   * @param request <code>HttpServletRequest</code> to apply the lookup against.
   *
   * @return WrappedRequest for the resource.
   *
   * @throws ServletException on an error
   */
  private HttpServletRequest mapRequest(HttpServletRequest request)
                                 throws ServletException {
    VirtualJournalContext context =
      (VirtualJournalContext) request.getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);

    if (context == null)
      return request;

    String journal = context.getJournal();

    if (journal == null)
      return request;

    String cp      = request.getContextPath();
    String sp      = request.getServletPath();
    String pi      = request.getPathInfo();

    // Find resource in journal
    String[] mapped = getMappedPaths(context.virtualizeUri(cp, sp, pi));

    // Find resource in default journal
    if (mapped == null)
      mapped = getMappedPaths(context.siteDefaultUri(cp, sp, pi));

    // Find resource in app defaults
    if (mapped == null)
      mapped = getMappedPaths(context.defaultUri(cp, sp, pi));

    if ((mapped != null) && mapped[3].equals(request.getRequestURI()))
      return request;

    if (mapped == null)
      return request;
    else
      return wrapRequest(request, mapped);
  }

  private String[] getMappedPaths(String[] paths)
      throws ServletException {

    // strip contextPath ("/ambra-webapp") from resource path
    String resource = paths[3].substring(paths[0].length());

    if (resource.startsWith("journals") || resource.startsWith("/journals")) {
      String path = getJournalResourcePath(resource);
      String fullPath = path.startsWith("/") ? path : "/" + path;

      if (resourceExistsInPath(fullPath)) {
        return new String[]{paths[0], "", fullPath, paths[0] + fullPath};
      }
    } else if (resourceExistsInServletContext(resource)) {
        return paths;
    }

    return null;
  }

  /**
   * Path /journal/journal-name/resource changes to /journal/journal-name/webapp/resource
   * @param resource Resource path
   * @return Absolute resource path
   */
  private String getJournalResourcePath(String resource) {
    String templatePath = configuration.getString(ConfigurationStore.JOURNAL_TEMPLATE_DIR, null);

    StringTokenizer tokenizer = new StringTokenizer(resource,"/");
    StringBuilder stringBuilder = new StringBuilder();
    boolean addWebapp = false;
    while(tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.equals("journals") && stringBuilder.length() == 0) {
        addWebapp = true;
      }
      stringBuilder.append('/').append(token);
      if (addWebapp && !token.equals("journals")) {
        stringBuilder.append("/webapp");
        addWebapp = false;
      }
    }

    return templatePath + stringBuilder.toString();
  }

  /**
   * Search in servlet context - struts directory
   * @param resource path to resource
   * @return true if resource exists in servlet context
   * @throws ServletException Servlet exception
   */
  private boolean resourceExistsInServletContext(String resource) throws ServletException {
    try {
      return servletContext.getResource(resource) != null;
    } catch (MalformedURLException mre) {
      throw new ServletException("Invalid resource path: " + resource, mre);
    }
  }

  /**
   * Search in file system
   * @param resource path to resource
   * @return true if resource exists in servlet context
   * @throws ServletException Servlet exception
   */
  private boolean resourceExistsInPath(String resource) throws ServletException {
    File file = new File(resource);
    return file.isFile() && file.canRead();
  }

  /**
   * Wrap an HttpServletRequest with arbitrary URI values.
   *
   * @param request the request to wrap
   * @param paths the paths to substitute
   *
   * @return the wrapped request instance
   *
   * @throws IllegalArgumentException DOCUMENT ME!
   */
  public static HttpServletRequest wrapRequest(final HttpServletRequest request,
                                               final String[] paths) {
    if ((paths == null) || (paths.length != 4))
      throw new IllegalArgumentException("Invalid path list");

    return new HttpServletRequestWrapper(request) {
        public String getRequestURI() {
          return paths[3];
        }

        public String getContextPath() {
          return paths[0];
        }

        public String getServletPath() {
          return paths[1];
        }

        public String getPathInfo() {
          return paths[2];
        }
      };
  }
}
