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

import org.topazproject.ambra.auth.AuthConstants;
import org.topazproject.ambra.auth.db.DatabaseException;
import org.topazproject.ambra.auth.service.UserService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Returns the email address given a user's GUID
 * File to change when hosting
 * esup-cas-quick-start-2.0.6-1/jakarta-tomcat-5.0.28/webapps/cas/WEB-INF/web.xml
 *  <pre>
 *
 *  &lt;servlet&gt;
 *    &lt;servlet-name&gt;Email&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;org.topazproject.ambra.auth.web.GetEmailAddress&lt;/servlet-class&gt;
 *  &lt;/servlet&gt;
 *
 *  &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;Email&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/email&lt;/url-pattern&gt;
 *  &lt;/servlet-mapping&gt;
 *
 *  </pre>
 */
public class GetEmailAddress extends HttpServlet {
  private UserService userService;

  protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", -1);

    final PrintWriter writer = response.getWriter();
    try {
      writer.write(userService.getEmailAddress(request.getParameter("guid")));
    } catch (DatabaseException e) {
      throw new ServletException(e);
    }
  }

  public void init(final ServletConfig servletConfig) throws ServletException {
    try {
      userService = (UserService)
        (servletConfig.getServletContext().getAttribute(AuthConstants.USER_SERVICE));
    } catch (final Exception ex) {
      throw new ServletException(ex);
    }
  }

  protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response);
  }
}
