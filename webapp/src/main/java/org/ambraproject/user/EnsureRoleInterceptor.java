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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.ambraproject.Constants;
import org.ambraproject.user.service.UserService;

import java.util.Map;

/**
 * Ensures that the user has the required role.
 *
 */
public class EnsureRoleInterceptor extends AbstractInterceptor {
  private static final Logger log = LoggerFactory.getLogger(EnsureRoleInterceptor.class);

  private UserService userService;
  private PlatformTransactionManager transactionManager; //TODO: obviate the need for this.  See Transaction Interceptor

  public String intercept(final ActionInvocation actionInvocation) throws Exception {
    log.debug("EnsureRoleInterceptor called");
    Map session = actionInvocation.getInvocationContext().getSession();
    final String authId = (String)session.get(Constants.AUTH_KEY);
    Boolean allowAdminAction = (Boolean) new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
      @Override
      public Object doInTransaction(TransactionStatus transactionStatus) {
        return userService.allowAdminAction(authId);
      }
    });
    if (allowAdminAction)
      return actionInvocation.invoke();

    return Constants.ReturnCode.NOT_SUFFICIENT_ROLE;
  }

  /**
   * Set the userService
   * @param userService userService
   */
  public void setUserService(final UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }
}
