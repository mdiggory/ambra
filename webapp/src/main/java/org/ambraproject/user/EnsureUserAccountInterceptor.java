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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.ambraproject.models.UserLogin;
import org.ambraproject.models.UserProfile;
import org.ambraproject.user.service.UserService;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.ambraproject.Constants.AMBRA_USER_KEY;
import static org.ambraproject.Constants.AUTH_KEY;
import static org.ambraproject.Constants.ReturnCode;
import static org.ambraproject.Constants.SINGLE_SIGNON_EMAIL_KEY;
import static org.ambraproject.Constants.SINGLE_SIGNON_RECEIPT;

/**
 * Ensures that the user has a profile if the user does something which requires membership Get the user object for the
 * logged in user or redirect the user to set up his profile.
 */
public class EnsureUserAccountInterceptor extends AbstractInterceptor {
  private UserService userService;
  private PlatformTransactionManager transactionManager; //TODO: obviate the need for this.  See transaction interceptor
  private static final Logger log = LoggerFactory.getLogger(EnsureUserAccountInterceptor.class);

  /**
   * Check for a user matching the SSO ticket, if one exists.  Forwards to new profile page if no matching user exists.
   * <p/>
   * The work flow is as such:
   * <p/>
   * 1. Check if there is a ticket from CAS
   * If not, then we do nothing.
   * If there is, then we:
   * 2. Check if a user object is in the session.
   * a. If not, look up the user in the database, and put it in the session.
   * i.  if the user is not in the database, this must be a new profile.  forward to profile creation page.
   * 3. Update the database to ensure that the email address on file for this user matches the one from CAS
   * TODO: this next step may be deprecated
   * 4. Check if the user object in the session has a display name
   * a. If not, the user is an old account.  forward to the update profile page
   *
   * @param actionInvocation
   * @return
   * @throws Exception
   */
  public String intercept(final ActionInvocation actionInvocation) throws Exception {
    log.debug("ensure user account interceptor called");

    Map<String, Object> session = actionInvocation.getInvocationContext().getSession();

    //STEP 1: check if there is an auth id from cas
    final String authId = (String) session.get(AUTH_KEY);
    if (authId == null) {
      //No auth id, nothing to do here
      if (log.isDebugEnabled()) {
        log.debug("no single sign on user key");
        log.debug("ticket is: " + session.get(SINGLE_SIGNON_RECEIPT));
      }
      return actionInvocation.invoke();
    } else {
      //STEP 2: check if there's a user object in the session
      UserProfile ambraUser = (UserProfile) session.get(AMBRA_USER_KEY);
      if (ambraUser == null) {
        //No user object, so we must just be returning from CAS.  Look up the user in the db, and record their login
        final HttpServletRequest request = ServletActionContext.getRequest();
        ambraUser = (UserProfile) new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
          @Override
          public Object doInTransaction(TransactionStatus transactionStatus) {
            return userService.login(authId, new UserLogin(
                request.getRequestedSessionId(), //session id
                request.getRemoteAddr(), //ip
                request.getHeader("user-agent") //user-agent
            ));
          }
        });
        if (ambraUser == null) {
          //No matching user in the database. redirect to the profile creation page
          log.debug("This is a new user with auth id: {}", authId);
          return ReturnCode.NEW_PROFILE;
        }
        //put the user in the session
        session.put(AMBRA_USER_KEY, ambraUser);
      }

      //STEP 3: make sure that ambra's email address matches the one from CAS
      String emailFromCAS = (String) session.get(SINGLE_SIGNON_EMAIL_KEY);
      if (emailFromCAS == null) {
        emailFromCAS = userService.fetchUserEmailFromCas(authId);
        session.put(SINGLE_SIGNON_EMAIL_KEY, emailFromCAS);
      }
      if (ambraUser.getEmail() == null || (!ambraUser.getEmail().equals(emailFromCAS))) {
        userService.updateEmail(ambraUser.getID(), emailFromCAS);
      }

      //STEP 4: Check if the user has a display name  (this is only relevant for old users)
      if (!StringUtils.hasText(ambraUser.getDisplayName())) {
        return ReturnCode.UPDATE_PROFILE;
      }
      //continue with the action invocation
      return actionInvocation.invoke();
    }
  }

  @Required
  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Required
  public void setUserService(final UserService userService) {
    this.userService = userService;
  }
}
