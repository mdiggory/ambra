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

package org.ambraproject.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * This filter looks for all requests that match regexp expression passed in the "regexp" paramater
 * and returns 403 (Forbidden) response code.
 *
 * @author Dragisa Krsmanovic
 */
public class GatekeeperFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(GatekeeperFilter.class);

  private Pattern pattern;

  public void init(FilterConfig filterConfig) throws ServletException {
    String regexp = filterConfig.getInitParameter("regexp");
    if (regexp == null)
      throw new ServletException("\"regexp\" parameter not set");

    this.pattern = Pattern.compile(regexp);
  }

  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain)
      throws IOException, ServletException {

    if (servletRequest instanceof HttpServletRequest) {
      String servletPath = ((HttpServletRequest) servletRequest).getServletPath();
      if (servletPath != null && pattern.matcher(servletPath).matches()) {
        log.error("Forbidden request for " + servletPath);
        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }

  public void destroy() {
  }
}
