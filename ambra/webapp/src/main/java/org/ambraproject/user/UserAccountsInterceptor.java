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

package org.ambraproject.user;

import java.net.URLEncoder;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts2.ServletActionContext;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.ambraproject.ApplicationException;
import org.ambraproject.Constants;
import org.topazproject.ambra.models.UserAccount;
import org.ambraproject.user.service.UserService;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * A webwork interceptor that maps the authentication id to an internal ambra-user-id. The ambra
 * user-id is setup in the HttpSession using the key {@link #USER_KEY}. The original authentication
 * id is available using the session key {@link #AUTH_KEY}. In addition a wrapper for
 * <code>HttpServletRequest</code> is setup so that the <code>getRemoteUser</code> and
 * <code>getUserPrincipal</code> returns the ambra-user.
 *
 * @author Pradeep Krishnan
 */
@SuppressWarnings("serial")
public class UserAccountsInterceptor extends AbstractInterceptor {
  private static Logger log = LoggerFactory.getLogger(UserAccountsInterceptor.class);

  /**
   * The session attribute key used to store the ambra-user-id in HttpSession.
   */
  public static String USER_KEY = "org.topazproject.user-id";

  /**
   * The session attribute key used to store the user-account-state in HttpSession.
   */
  public static String STATE_KEY = "org.topazproject.account-state";

  /**
   * The session attribute key used to store the authenticated-user-id in HttpSession.
   */
  public static String AUTH_KEY = "org.topazproject.auth-id";

  private UserService userService;
  private PlatformTransactionManager transactionManager; //TODO: obviate the need for this.  See Transaction Interceptor
  private boolean     wrap = false;

  /**
   * Internal key used for detecting whether or not this interceptor 
   * has already been applied for the targeted action.  
   * This check is necessary when considering action chaining. 
   */
  private static final String REENTRANT_KEY = UserAccountsInterceptor.class.getName() +
                                              ".reentrant";

  /**
   * Checks for and sets the {@link #REENTRANT_KEY} value for the current {@link com.opensymphony.xwork2.ActionContext}
   * and reports on whether or not it was previously set.
   * @param invocation The {@link ActionInvocation}
   * @return <code>true</code> if this interceptor has already been applied for the current
   *                           {@link com.opensymphony.xwork2.ActionContext}.
   */
  private boolean reentrantCheck(ActionInvocation invocation) {
    Object obj = invocation.getInvocationContext().get(REENTRANT_KEY);
    if (obj == null) {
      invocation.getInvocationContext().put(REENTRANT_KEY, true);
    }
    return obj != null;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    if (!reentrantCheck(invocation)) {
      String user = lookupUser(ServletActionContext.getRequest());
      if (wrap)
        ServletActionContext.setRequest(wrapRequest(ServletActionContext.getRequest(), user));
    }
    return invocation.invoke();
  }

  /**
   * Wraps the HttpServletRequest so that <code>getRemoteUser</code> and
   * <code>getUserPrincipal</code>  will return the mapped topaz-user.
   *
   * @param request the servlet request to wrap
   * @param user the topaz-user
   *
   * @return the wrapped request
   */
  protected HttpServletRequest wrapRequest(HttpServletRequest request, final String user) {
    final Principal principal = new Principal() {
        public String getName() {
          return user;
        }
      };

    return new HttpServletRequestWrapper(request) {
        @Override
        public String getRemoteUser() {
          return user;
        }

        @Override
        public Principal getUserPrincipal() {
          return principal;
        }
      };
  }

  /**
   * Looks up the ambra-user for the currently authenticated user and saves it in
   * <code>HttpSession</code>.
   *
   * @throws Exception when an error occurs while obtaining the mapping
   */
  protected String lookupUser(HttpServletRequest request) throws Exception {
    HttpSession session = request.getSession(true);
    String      user    = (String) session.getAttribute(USER_KEY);
    final String      authId  = getAuthenticationId(request);
    String      current = (String) session.getAttribute(AUTH_KEY);
    boolean     same    = (current == null) ? (authId == null) : current.equals(authId);

    if ((user != null) && same) {
      if (log.isDebugEnabled())
        log.debug("Changed user to '" + user + "' using value found in session-id: "
                  + session.getId());

      return user;
    }

    session.setAttribute(USER_KEY, "anonymous:user/");  // so policies let us get the account
    session.setAttribute(STATE_KEY, 0);                 // so policies let us get the account
    UserAccount ua = null;
    if(authId != null) {
      ua = (UserAccount) new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
        @Override
        public Object doInTransaction(TransactionStatus transactionStatus) {
          try {
            return userService.getUserAccountByAuthId(authId);
          } catch (ApplicationException e) {
            return null;
          }
        }
      });
    }

    if (ua != null)
      user = ua.getId().toString();
    else
      user = "anonymous:user/" + ((authId == null) ? "" : URLEncoder.encode(authId, "UTF-8"));

    session.setAttribute(USER_KEY, user);
    session.setAttribute(STATE_KEY, ua != null ? ua.getState() : 0);
    session.setAttribute(AUTH_KEY, authId);

    if (log.isDebugEnabled())
      log.debug("Changed user to '" + user + "' from '" + authId + "' with state '" +
                session.getAttribute(STATE_KEY) + "' and cached in session-id: " + session.getId());

    return user;
  }

  /**
   * Returns the current authentication id for a servlet request.
   *
   * @param request the servlet request
   *
   * @return returns the authenticated-id
   */
  protected String getAuthenticationId(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    return (String)session.getAttribute(Constants.SINGLE_SIGNON_USER_KEY);
  }

  /**
   * Set the userService
   *
   * @param userService userService
   */
  @Required
  public void setUserService(final UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * Set whether to wrap requests, setting the user to the looked-up user-id. Called by spring's
   * bean wiring.
   *
   * @param wrap true if wrapping is desired
   */
  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }
}
