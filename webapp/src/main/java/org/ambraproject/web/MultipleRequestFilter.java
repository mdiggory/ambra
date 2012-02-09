/* $HeadURL$
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MultipleReuquestFilter reduced load on the server caused by impatient users clicking on links
 * multiple times.  Incoming requests for the same URL within the same session are blocked until the
 * initial request completes.  Subsequent repeat requests will cause any blocked request to return
 * immediately, and will take it's place as the latest blocked request for that URL. Once the
 * original un-blocked request completes, it will remove the URL from the requestedURLSet and wake
 * any block thread on that URL. Once a block thread wakes to find the URL not in the set, it can
 * complete the request. If it wakes to find the URL still in the set, it assumes it was displaced
 * by another request and will return without performing work on the server. 
 * 
 * @author Alex Worden
 */
public class MultipleRequestFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(MultipleRequestFilter.class);
  private static final String REQUEST_MAP = "REQUEST_MAP";
  private List<Pattern> includePatterns;

  public void destroy() {
    // do nothing
  }

  public void init(FilterConfig config) throws ServletException {
    /* parse all of the initialization parameters, collecting the exclude
     * patterns and the max wait parameters
     */
    Enumeration<String> paramNames = config.getInitParameterNames();
    includePatterns = new LinkedList<Pattern>();
    while (paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      String paramValue = config.getInitParameter(paramName);
      if (paramName.startsWith("includePattern")) {
        // compile the pattern only this once
        Pattern excludePattern = Pattern.compile(paramValue);
        includePatterns.add(excludePattern);
      }
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest)request;
    HttpSession session = httpRequest.getSession();

    Set<String> requestedUrlSet = getRequestSet(session);
    StringBuilder buf = new StringBuilder();
    buf.append(httpRequest.getRequestURI());
    String queryString = httpRequest.getQueryString();
    if ((queryString != null) && (queryString.length() > 0)) {
      buf.append("?");
      buf.append(httpRequest.getQueryString());
    }
    if (!isFilteredRequest(buf.toString())) {
      chain.doFilter(request, response);
      return;
    }

    // Note, we have to add the session id since we're going to sync on this interned key. 
    buf.insert(0, session.getId());
    String urlKey = (buf.toString()).intern();

    synchronized (urlKey) {
      if (requestedUrlSet.contains(urlKey)) {
        urlKey.notifyAll();

        if (log.isDebugEnabled())
          log.debug("Waiting on URL=" + urlKey);
        try {
          urlKey.wait(60000); // Only wait a maximum of one minute
        } catch (InterruptedException e) {
          if (log.isDebugEnabled())
            log.debug("Wait interrupted", e);
        }

        /* If we are woken up and the urlKey is not null, then we were not the latest
         * request for this URL to be woken after the first request had completed, so
         * just return and do nothing for this request. 
         */
        if (requestedUrlSet.contains(urlKey)) {
          if (log.isDebugEnabled())
            log.debug("Blocked (or timed out) Multiple Request to '" + urlKey + "' from '" +
                      httpRequest.getRemoteAddr() + "'");
          return;
        }

        if (log.isDebugEnabled())
          log.debug("Woke from blocking URL as last request. Processing URL=" + urlKey);
      }

      requestedUrlSet.add(urlKey);
    }


    try {
      chain.doFilter(request, response);
    } finally {
      /* Upon completion of this request, remove the urlKey from the requestSet, then 
       * wake any threads that are waiting on the key. 
       */
      synchronized (urlKey) {
        requestedUrlSet.remove(urlKey);
        urlKey.notifyAll();
      }
    }
  }

  /**
   * Determine if this String matches one of the excludePattern init parameters defined in web.xml. 
   * 
   * @param path
   * @return
   */
  private boolean isFilteredRequest(String path) {
    /* iterate through the exclude patterns. If one matches this path, then the request is
     * excluded.
     */
    for (Pattern p : includePatterns) {
      Matcher m = p.matcher(path);
      if (m.matches()) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  private Set<String> getRequestSet(HttpSession session) {
    Set<String> map;
    synchronized (session) {
      if ((map = (Set<String>) session.getAttribute(REQUEST_MAP)) == null) {
        map = Collections.synchronizedSet(new HashSet<String>());
        session.setAttribute(REQUEST_MAP, map);
      }
    }
    return map;
  }
}
