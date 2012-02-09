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

package org.ambraproject.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topazproject.ambra.configuration.ConfigurationStore;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;

/**
 * <p>Filter that handles static resources like images, css, javascript, html etc. stored in journal
 * override directory specified in ambra.virtualJournals.templateDir configuration parameter.</p>
 *
 * <p>It needs to be invoked after @see VirtualJournalMappingFilter and struts2 filter</p>
 *  
 * @author Dragisa Krsmanovic
 */
public class JournalStaticResourceFilter implements Filter {

  private static final Logger log = LoggerFactory.getLogger(JournalStaticResourceFilter.class);

  private String templatePath = null;
  private HttpResourceServer server = null;

  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("JournalStaticResourceFilter init");
    this.templatePath = ConfigurationStore.getInstance().getConfiguration()
        .getString(ConfigurationStore.JOURNAL_TEMPLATE_DIR, null);
    this.server = new HttpResourceServer();
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    boolean callNext = true;

    if (templatePath != null && servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      String path = request.getPathInfo();
      if (path != null && path.startsWith(templatePath)) {
        File file = new File(path);
        if (file.isFile() && file.canRead()) {
          server.serveResource(request, (HttpServletResponse)servletResponse,
              new HttpResourceServer.FileResource(file), null);
          callNext = false;
        } else {
          log.error("Cannot open " + path);
        }
      }
    }

    if (callNext)
      filterChain.doFilter(servletRequest, servletResponse);
  }

  public void destroy() {

  }
}
