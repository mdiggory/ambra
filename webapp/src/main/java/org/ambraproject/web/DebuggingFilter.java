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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Arrays;

/**
 * Filter to be used for debugging when required.
 *
  <filter>
    <filter-name>DebuggingFilter</filter-name>
    <filter-class>org.ambraproject.web.DebuggingFilter</filter-class>
  </filter>

  <filter-mapping >
    <filter-name>DebuggingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
 */
public class DebuggingFilter implements Filter {
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void doFilter(final ServletRequest request, final ServletResponse response,
                       final FilterChain filterChain) throws IOException, ServletException {
    try {
      final HttpServletRequest origRequest = (HttpServletRequest) request;
      final CharResponseWrapper servletResponse =
        new CharResponseWrapper((HttpServletResponse) response);

      dumpRequest(origRequest, "before chain");

      filterChain.doFilter(origRequest, servletResponse);

      final PrintWriter out = response.getWriter();
      out.write(dumpResponse(servletResponse));
      out.close();

      dumpRequest(origRequest, "after chain");

    } catch (Exception ex) {
      ex.printStackTrace();
      log("Exception raised in DebuggingFilter");
    }
  }

  public void destroy() {
  }

  void log(final String message) {
    System.out.println(message);
  }

  private String dumpResponse(final CharResponseWrapper wrapper) throws IOException {
    final String response;
    log("Response ContentType:" + wrapper.getContentType());
    if (wrapper.getContentType().equals("text/html")) {
      CharArrayWriter caw = new CharArrayWriter();
      caw.write(wrapper.toString());
      response = caw.toString();
    } else {
      response = wrapper.toString();
    }
    log("Response generated:\n" + response);
    return response;
  }

  private void dumpRequest(final HttpServletRequest request, final String prefix) {
    log(prefix + "----------------" + System.currentTimeMillis());

    log("url:" + request.getRequestURL());
    log("query string:" + request.getQueryString());
    {
      log("Request Attributes:");
      final Enumeration attribs = request.getAttributeNames();
      while (attribs.hasMoreElements()) {
        final String attribName = (String) attribs.nextElement();
        log(attribName + ":" + request.getAttribute(attribName).toString());
      }
    }

    {
      log("Request Parameters:");
      final Enumeration params = request.getParameterNames();
      while (params.hasMoreElements()) {
        final String paramName = (String) params.nextElement();
        log(paramName + ":" + Arrays.toString(request.getParameterValues(paramName)));
      }
    }
  }
}

class CharResponseWrapper extends HttpServletResponseWrapper {
  private CharArrayWriter output;

  public String toString() {
    return output.toString();
  }

  public CharResponseWrapper(HttpServletResponse response) {
    super(response);
    output = new CharArrayWriter();
  }

  public PrintWriter getWriter() {
    return new PrintWriter(output);
  }
}
