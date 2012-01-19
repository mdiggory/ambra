/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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

import java.util.Collection;

/**
 * The virtual journal context.
 *
 * Various values relating to the virtual journal context and the current Request.
 * Designed to be put into the Request as an attribute for easy access by the web application.
 * In particular, allows templates full consistent access to the same information that is available
 * to the Java Action.
 *
 * Note that templates do not have full access to the Request, so a Request
 * attribute is used to bridge between the two.
 *
 * Application usage:
 * <pre>
 * VirtualJournalContext requestContent = ServletRequest.getAttribute(PUB_VIRTUALJOURNAL_CONTEXT);
 * String requestJournal = requestContext.getJournal();
 * </pre>
 */
public class VirtualJournalContext {
  /** ServletRequest attribute for the virtual journal context. */
  public static final String PUB_VIRTUALJOURNAL_CONTEXT = "ambra.virtualjournal.context";

  private final String journal;
  private final String mappingPrefix;
  private final String defaultMappingPrefix;
  private final String requestScheme;
  private final int    requestPort;
  private final String requestServerName;
  private final String requestContext;
  private final String baseUrl;
  private final String baseHostUrl;
  private final Collection<String> virtualJournals;

  /**
   * Construct an immutable VirtualJournalContext.
   */
  public VirtualJournalContext(final String journal,
                               final String defaultJournal,
                               final String requestScheme,
                               final int requestPort,
                               final String requestServerName,
                               final String requestContext,
                               final Collection<String> virtualJournals) {

    this.journal           = journal;
    this.requestScheme     = requestScheme;
    this.requestPort       = requestPort;
    this.requestServerName = requestServerName;
    this.requestContext    = requestContext;
    this.mappingPrefix = "journals/" + journal;
    this.defaultMappingPrefix = "journals/" + defaultJournal;

    StringBuilder urlBaseValue = new StringBuilder();

    // assume that we're dealing with http or https schemes for now
    // TODO: understand how scheme can be null in request? for now, guard against null
    if (requestScheme != null) {
      urlBaseValue.append(requestScheme).append("://").append(requestServerName);

      //assume that we don't want to put the default ports numbers on the URL
      if (("http".equals(requestScheme.toLowerCase()) && requestPort != 80) ||
          ("https".equals(requestScheme.toLowerCase()) && requestPort != 443)) {
        urlBaseValue.append(":").append(requestPort);
      }
    }
    this.baseHostUrl = urlBaseValue.toString();
    urlBaseValue.append(requestContext);
    this.baseUrl = urlBaseValue.toString();

    this.virtualJournals = virtualJournals;
  }


  private static String[] createMapping(
      String virtualContextPath, String virtualServletPath, String virtualPathInfo) {
    return new String[]{virtualContextPath, virtualServletPath, virtualPathInfo,
        virtualContextPath + virtualServletPath + (virtualPathInfo != null ? virtualPathInfo : "")};
  }

  /**
   * Virtualize URI values.
   *
   * If URI is already prefixed with the mappingPrefix, it is already virtualized.<br/>
   * If URI is prefixed with the virtual journal name, replace with mappingPrefix to
   *   virtualize.<br/>
   * Else, prefix with mappingPrefix to virtualize.
   *
   * @return String[] of mapped {contextPath, servletPath, pathInfo, requestUri}
   */
  public String[] virtualizeUri(
      final String contextPath, final String servletPath, final String pathInfo) {

    // in tests below, be flexible with servletPath and pathInfo, not all containers adhere tightly
    // to Servlet SRV.4.4.  e.g. prefix could be in servletPath or pathInfo.

    // does URI already contain mappingPrefix?
    if (servletPath != null && servletPath.startsWith(mappingPrefix)
        || pathInfo != null && pathInfo.startsWith(mappingPrefix)) {
      return createMapping(contextPath, servletPath, pathInfo);
    }

    // does URI contain journal name?
    if (servletPath != null && servletPath.startsWith("/" + journal)) {
      return createMapping(
          contextPath, mappingPrefix + servletPath.substring(journal.length() + 1), pathInfo);
    }
    if (pathInfo != null && pathInfo.startsWith("/" + journal)) {
      return createMapping(
          contextPath, servletPath, mappingPrefix + pathInfo.substring(journal.length() + 1));
    }

    // need to add mappingPrefix to URI
    if (servletPath != null && servletPath.length() > 0) {
       return createMapping(contextPath, mappingPrefix + servletPath, pathInfo);
    } else {
      return createMapping(contextPath, servletPath, mappingPrefix + pathInfo);
    }
  }


  /**
   * Virtualize URI values to default journal.
   *
   * If URI is already prefixed with the mappingPrefix, replace with default journal mappingPrefix.<br/>
   * If URI is prefixed with the virtual journal name, replace with default journal mappingPrefix.<br/>
   * Else, prefix with default journal mappingPrefix to virtualize.
   *
   * @return String[] of mapped {contextPath, servletPath, pathInfo, requestUri}
   */
  public String[] siteDefaultUri(
      final String contextPath, final String servletPath, final String pathInfo) {

    // in tests below, be flexible with servletPath and pathInfo, not all containers adhere tightly
    // to Servlet SRV.4.4.  e.g. prefix could be in servletPath or pathInfo.

    // does URI already contain defaultMappingPrefix?
    if (servletPath != null && servletPath.startsWith(defaultMappingPrefix)
        || pathInfo != null && pathInfo.startsWith(defaultMappingPrefix)) {
      return createMapping(contextPath, servletPath, pathInfo);
    }

    // does URI contain mappingPrefix?
    if (servletPath != null && servletPath.startsWith(mappingPrefix)) {
      return createMapping(
          contextPath, servletPath.replaceFirst(mappingPrefix, defaultMappingPrefix), pathInfo);
    }
    if (pathInfo != null && pathInfo.startsWith(mappingPrefix)) {
      return createMapping(
          contextPath, servletPath, pathInfo.replaceFirst(mappingPrefix, defaultMappingPrefix));
    }

    // does URI contain journal name?
    if (servletPath != null && servletPath.startsWith("/" + journal)) {
      return createMapping(
          contextPath, defaultMappingPrefix + servletPath.substring(journal.length() + 1), pathInfo);
    }
    if (pathInfo != null && pathInfo.startsWith("/" + journal)) {
      return createMapping(
          contextPath, servletPath, defaultMappingPrefix + pathInfo.substring(journal.length() + 1));
    }

    // need to add defaultMappingPrefix to URI
    if (servletPath != null && servletPath.length() > 0) {
      return createMapping(contextPath, defaultMappingPrefix + servletPath, pathInfo);
    } else {
      return createMapping(contextPath, servletPath, defaultMappingPrefix + pathInfo);
    }
  }

  /**
   * Default URI values.
   *
   * If URI is already prefixed with the mappingPrefix, remove it.<br/>
   * If URI is prefixed with the virtual journal name, remove it.<br/>
   * Else, already default values.
   *
   * @return String[] of defaulted {contextPath, servletPath, pathInfo, requestUri}
   */
  public String[] defaultUri(
      final String contextPath, final String servletPath, final String pathInfo) {

    /*
     * in tests below, be flexible with servletPath and pathInfo, not all containers adhere tightly
     * to Servlet SRV.4.4.  e.g. prefix could be in servletPath or pathInfo.
     */

    // does URI already contain mappingPrefix?
    if (servletPath != null && servletPath.startsWith(mappingPrefix)) {
      return createMapping(contextPath, servletPath.substring(mappingPrefix.length()), pathInfo);
    }
    if (pathInfo != null && pathInfo.startsWith(mappingPrefix)) {
      return createMapping(contextPath, servletPath, pathInfo.substring(mappingPrefix.length()));
    }

    // does URI contain journal name?
    if (servletPath != null && servletPath.startsWith("/" + journal)) {
      return createMapping(contextPath, servletPath.substring(journal.length() + 1), pathInfo);
    }
    if (pathInfo != null && pathInfo.startsWith("/" + journal)) {
      return createMapping(contextPath, servletPath, pathInfo.substring(journal.length() + 1));
    }

    // already defaulted
    return createMapping(contextPath, servletPath, pathInfo);
  }

  /**
   * Get the virtual journal name.
   *
   * @return Journal name, may be <code>null</code>.
   */
  public String getJournal() {
    return journal;
  }

  /**
   * Get the virtual journal Request scheme.
   *
   * @return Request scheme.
   */
  public String getRequestScheme() {
    return requestScheme;
  }

  /**
   * Get the virtual journal Request port.
   *
   * @return Request port.
   */
  public int getRequestPort() {
    return requestPort;
  }

  /**
   * Get the virtual journal Request server name.
   *
   * @return Request server name.
   */
  public String getRequestServerName() {
    return requestServerName;
  }

  /**
   * Get the virtual journal Request context.
   *
   * @return Request context.
   */
  public String getRequestContext() {
    return requestContext;
  }

  /**
   * Get the base url of the request which consists of the scheme, server name, server port, and
   * context.
   *
   * @return string representing the base request URL
   */
  public String getBaseUrl () {
    return baseUrl;
  }

  /**
   * Get the base host url of the request which consists of the scheme, server name, and server port
   *
   * @return string representing the base host URL
   */
  public String getBaseHostUrl () {
    return baseHostUrl;
  }

  /**
   * Get the virtual journals that are available.
   *
   * @return Available virtual journals.
   */
  public Collection<String> getVirtualJournals() {
    return virtualJournals;
  }
}
