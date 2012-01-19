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
package org.ambraproject.cas;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * Wrapper around a FilterConfig 
 */
public class ConfigWrapper implements FilterConfig, ServletConfig {
  private FilterConfig filterConfig;
  private ServletConfig servletConfig;
  private final Map<String, String> params;

  public ConfigWrapper(final FilterConfig filterConfig, final Map<String, String> params) {
    this.filterConfig = filterConfig;
    this.params = params;
  }

  public ConfigWrapper(final ServletConfig servletConfig, final Map<String, String> params) {
    this.servletConfig = servletConfig;
    this.params = params;
  }

  /**
   * @see javax.servlet.FilterConfig#getFilterName()
   */
  public String getFilterName() {
    return filterConfig.getFilterName();
  }

  /**
   * @see javax.servlet.ServletConfig#getServletName()
   */
  public String getServletName() {
    return servletConfig.getServletName();
  }

  public ServletContext getServletContext() {
    return filterConfig.getServletContext();
  }

  /**
   * @see javax.servlet.FilterConfig#getInitParameter(String)
   * @see javax.servlet.ServletConfig#getInitParameter(String)
   */
  public String getInitParameter(final String name) {
    return params.get(name);
  }

  /**
   * @see javax.servlet.FilterConfig#getInitParameterNames()
   * @see javax.servlet.ServletConfig#getInitParameterNames()
   */
  public Enumeration getInitParameterNames() {
    return Collections.enumeration(params.keySet());
  }
}
