/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.ambra.cas;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The HttpServletRequest parameter named <em>guid</em> contains the user's ID.
 * This value is used to look up the user's Email Address from the database.
 * <p/>
 * This Filter acts as a patch for displaying the user's email address on the Ambra
 *   "edit profile" pages.
 * <p/>
 * At the end of this class's <em>Filter.doFilter(...)</em> method,
 *   <em>FilterChain.doFilter(...)</em> is NOT called.
 * Instead of following the usual pattern for Filters, this class instead just writes back to the
 *   Response (using a PrintWriter) the String that it acquired from the database lookup.
 * It is <em>assumed</em> that the query succeeded and that the result is the user's Email Address.
 * There are no provisions for gracefully handling any sort of failure.
 * <p/>
 * The most typical failure mode is to see the (CAS-generated) login page appear in the middle of
 *   the Edit User Profile page, where the user's Email Address should be.
 * This results from the user (meaning the user submitting the URL, who is not necessarily
 *   the user indicated by the ID in the <em>guid</em> parameter) not being logged in.
 * When an unauthenticated user tries to submit any URL to CAS, the default behavior
 *   is to redirect them to the Login page.
 * Since this Filter just returns the String that it got from the database query,
 *   an unauthenticated user's request will always result in the Login page,
 *   which is then passed back to Ambra and displayed to the user.
 * <p/>
 * This failure mode replicates the behavior which existed in the "cas-mod" project.
 */
public class GetGuidReturnEmailFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(GetGuidReturnEmailFilter.class);
  
  private DatabaseService databaseService;

  /**
   * Set the Database Service (which will be used to query the user's Email Address) by way of
   *   the Web Application Context, which is managed by the supporting framework.
   *
   * @param filterConfig Standard Filter configuration values, most notably the Servlet Context
   * @throws ServletException Thrown if there is a problem getting or setting the Database Service
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    try {
      databaseService = new DatabaseService(WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()));
    } catch (final Exception e) {
      log.error("Failed to initialize GetGuidReturnEmailFilter.", e);
      throw new ServletException(e);
    }
  }

  /**
   * For the HttpServletRequest Parameter <em>guid</em>, query the user database for that
   *   user's Email Address, write that Email Address to the HttpServletResponse, and then exit.
   * <p/>
   * Returns <strong>only</strong> the user's Email Address.
   * <p/>
   * <strong>Caveat</strong>: Does <strong>not</strong> call <em>FilterChain.doFilter(...)</em>,
   *   but rather simply writes the String (which it hopes is the Email Address) to the Response
   *   and exits.
   *
   * @param request The incoming HttpServletRequest from which the Parameter <em>guid</em> is read
   * @param response The HttpServletResponse to which the results String is written
   * @param filterChain Passed in, but not used;
   *   <em>FilterChain.doFilter(...)</em> is <strong>never called</strong>.
   * @throws IOException Unlikely to be thrown except for an infrastructure failure
   * @throws ServletException Potentially thrown by interactions with <em>request</em>
   *   and <em>response</em>, but never explicitly raised in this method
   */
  public void doFilter(final ServletRequest request, final ServletResponse response,
                       final FilterChain filterChain) throws IOException, ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (log.isDebugEnabled()) {
      log.debug("Received the guid parameter: " + request.getParameter("guid"));
    }
 
    httpResponse.setHeader("Pragma", "no-cache");
    httpResponse.setHeader("Cache-Control", "no-store");
    httpResponse.setDateHeader("Expires", -1);

    final PrintWriter writer = response.getWriter();
    try {
      String emailFromGuid = databaseService.getEmailAddressFromGuid(request.getParameter("guid"));
      if (log.isDebugEnabled()) {
        log.debug("Now passing back the email address: " + emailFromGuid);
      }
      writer.write(emailFromGuid);
    } catch (Exception e) {
      log.error("Unable to query an email address or to write that email address to ServletResponse." +
          "  Attempted to query an email address for the guid = " + request.getParameter("guid"), e);
      //  TODO: Replace this with some clever logic.  This is NOT something to show to the user.
      writer.write("fake_guid_returned_from_cas");
    }

    // This next command is somewhat unfortunate.  A Filter should always invoke "doFilter(...)" at this point, but doing so can break this fragile email lookup.
    //   A typical failure mode is to see the login page in the middle of the Edit User Profile page, where the user's Email Address should be.
    // The core issue is that one of the other Filters may require authentication, so it calls the "/login" URL and the default behavior
    //   for that URL is to send the user to the Login page.  Ambra is querying for a single String (the user's email address), so it takes the String
    //   (which might be the entire Login page) and shows it to the user.
    // It is, therefore, recommended that Ambra acquire its users' email addresses in a different manner.
    return;
    // filterChain.doFilter(httpRequest, response);
  }

  /**
   * Does nothing.  Exists to satify the Filter interface.
   */
  public void destroy() {
  }
}
