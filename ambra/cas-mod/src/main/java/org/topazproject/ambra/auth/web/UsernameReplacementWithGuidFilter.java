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
package org.topazproject.ambra.auth.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.topazproject.ambra.auth.AuthConstants;
import org.topazproject.ambra.auth.db.DatabaseException;
import org.topazproject.ambra.auth.service.UserService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Arrays;

/**
 * Replaces the Username with the user's GUID so that the username is the GUID for any responses to
 * clients.  File to change when hosting
 * esup-cas-quick-start-2.0.6-1/jakarta-tomcat-5.0.28/webapps/cas/WEB-INF/web.xml
 *
 *  <pre>
 *
 *  &lt;filter&gt;
 *    &lt;filter-name&gt;UsernameReplacementWithGuidFilter&lt;/filter-name&gt;
 *    &lt;filter-class&gt;org.topazproject.ambra.auth.web.UsernameReplacementWithGuidFilter&lt;/filter-class&gt;
 *  &lt;/filter&gt;
 *
 *  &lt;filter-mapping&gt;
 *    &lt;filter-name&gt;UsernameReplacementWithGuidFilter&lt;/filter-name&gt;
 *    &lt;url-pattern&gt;/login&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 *
 *  </pre>
 */
public class UsernameReplacementWithGuidFilter implements Filter {
  private static final Log log = LogFactory.getLog(UsernameReplacementWithGuidFilter.class);
  private UserService userService;
  public String USERNAME_PARAMETER = "username";

  public void init(final FilterConfig filterConfig) throws ServletException {
    try {
      userService = (UserService) filterConfig.getServletContext().getAttribute(AuthConstants.USER_SERVICE);
    } catch (final Exception ex) {
      log.error("UsernameReplacementWithGuidFilter init failed:", ex);
      throw new ServletException(ex);
    }
  }

  public void doFilter(final ServletRequest request, final ServletResponse response,
                       final FilterChain filterChain) throws IOException, ServletException {
    try {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if (log.isDebugEnabled()) {
        dumpRequest(httpRequest, "before chain");
      }

      final String usernameParameter = request.getParameter(USERNAME_PARAMETER);

      if (!((null == usernameParameter) || (usernameParameter.length() == 0))) {
        httpRequest = new UsernameRequestWrapper(httpRequest, usernameParameter);
      }

      filterChain.doFilter(httpRequest, response);

    } catch (final IOException ex) {
      log.error("", ex);
      throw ex;
    } catch (final ServletException ex) {
      log.error("", ex);
      throw ex;
    }
  }

  private class UsernameRequestWrapper extends HttpServletRequestWrapper {
    private final String username;

    public UsernameRequestWrapper(final HttpServletRequest httpRequest, final String username) {
      super(httpRequest);
      this.username = username;
    }

    public String getParameter(final String parameterName) {
      if (USERNAME_PARAMETER.equals(parameterName)) {
        final String guid = getUserGuid(username);
        log.debug("guid:" + guid);
        return guid;
      }
      return super.getParameter(parameterName);
    }

    private String getUserGuid(final String username) {
      try {
        return userService.getGuid(username);
      } catch (final DatabaseException e) {
        log.debug("No account found for userId:" + username, e);
        return "";
      }
    }
  }

  private String dumpResponse(final CharResponseWrapper wrapper) throws IOException {
    log.debug("Response ContentType:" + wrapper.getContentType());
    CharArrayWriter caw = new CharArrayWriter();
    caw.write(wrapper.toString());
    final String response = caw.toString();
    log.debug("Response generated:");
    log.debug(response);
    return response;
  }

  private void dumpRequest(final HttpServletRequest request, final String prefix) {
    log.debug(prefix + "----------------" + System.currentTimeMillis());
    log.debug("url:" + request.getRequestURL());
    log.debug("query string:" + request.getQueryString());

    log.debug("Request Attributes:");
    final Enumeration attribs = request.getAttributeNames();
    while (attribs.hasMoreElements()) {
      final String attribName = (String) attribs.nextElement();
      log.debug(attribName + ":" + request.getAttribute(attribName).toString());
    }

    log.debug("Request Parameters:");
    final Enumeration params = request.getParameterNames();
    while (params.hasMoreElements()) {
      final String paramName = (String) params.nextElement();
      log.debug(paramName + ":" + Arrays.toString(request.getParameterValues(paramName)));
    }
  }

  private void dumpSession(final HttpSession initialSession) {
    log.debug("Session Attributes:");
    final Enumeration attribs1 = initialSession.getAttributeNames();
    while (attribs1.hasMoreElements()) {
      final String attribName1 = (String) attribs1.nextElement();
      log.debug(attribName1 + ":" + initialSession.getAttribute(attribName1).toString());
    }
  }

  public void destroy() {
  }
}

class CharResponseWrapper extends HttpServletResponseWrapper {
  private CharArrayWriter output;

  public String toString() {
    return output.toString();
  }

  public CharResponseWrapper(final HttpServletResponse response) {
    super(response);
    output = new CharArrayWriter();
  }

  public PrintWriter getWriter() {
    return new PrintWriter(output);
  }
}
