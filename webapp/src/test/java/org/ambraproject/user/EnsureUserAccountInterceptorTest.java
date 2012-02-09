/* $HeadURL::                                                                            $
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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.user;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.ambraproject.action.BaseActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;

import static org.testng.Assert.assertEquals;

public class EnsureUserAccountInterceptorTest extends BaseWebTest {

  @Autowired
  protected EnsureUserAccountInterceptor interceptor;

  @Test
  public void testShouldForwardToCreateNewAccount() throws Exception {
    final MockActionInvocation actionInvocation = new MockActionInvocation();
    ActionContext.getContext().getSession().remove(Constants.AMBRA_USER_KEY);
    ActionContext.getContext().getSession().put(Constants.SINGLE_SIGNON_USER_KEY, "ASDASDASD12312313EDB");
    actionInvocation.setAction(null);
    actionInvocation.setInvocationContext(ActionContext.getContext());

    final String result = interceptor.intercept(actionInvocation);
    assertEquals(result, Constants.ReturnCode.NEW_PROFILE, "Interceptor didn't redirect correctly");
    ActionContext.getContext().getSession().remove(Constants.SINGLE_SIGNON_USER_KEY);
  }

  @Test
  public void testShouldForwardToUpdateNewAccount() throws Exception {
    final String GUID = "ASDASDASD12312313EDB";
    final AmbraUser ambraUser = new AmbraUser(GUID);
    ambraUser.setUserId("topazId");
    ambraUser.setEmail("viru@home.com");
    ambraUser.setDisplayName(null); //Display name is not set
    ambraUser.setRealName("virender");

    ActionContext.getContext().getSession().put(
        Constants.SINGLE_SIGNON_USER_KEY, "SINGLE_SIGNON_KEY_ASDASDASD12312313EDB");
    ActionContext.getContext().getSession().put(Constants.AMBRA_USER_KEY, ambraUser);
    final MockActionInvocation actionInvocation = new MockActionInvocation();
    actionInvocation.setInvocationContext(ActionContext.getContext());

    final String result = interceptor.intercept(actionInvocation);
    assertEquals(result, Constants.ReturnCode.UPDATE_PROFILE, "Interceptor didn't redirect correctly");
    ActionContext.getContext().getSession().remove(Constants.SINGLE_SIGNON_USER_KEY);
    ActionContext.getContext().getSession().remove(Constants.AMBRA_USER_KEY);
  }

  @Test
  public void testShouldForwardToOriginalAction() throws Exception {
    setupAdminContext();
    final String GUID = "ASDASDASD12312313EDB";
    final AmbraUser ambraUser = new AmbraUser(GUID);
    ambraUser.setUserId("topazId");
    ambraUser.setEmail("viru@home.com");
    ambraUser.setDisplayName("Viru");  //Display name is already set
    ambraUser.setRealName("virender");

    final String actionCalledStatus = "actionCalled";
    final MockActionInvocation actionInvocation = new MockActionInvocation() {
      public String invoke() throws Exception {
        return actionCalledStatus;
      }
    };
    actionInvocation.setInvocationContext(ActionContext.getContext());
    ActionContext.getContext().getSession().put(
        Constants.SINGLE_SIGNON_USER_KEY, "SINGLE_SIGNON_KEY_ASDASDASD12312313EDB");
    ActionContext.getContext().getSession().put(Constants.AMBRA_USER_KEY, ambraUser);

    final String result = interceptor.intercept(actionInvocation);
    assertEquals(result, actionCalledStatus, "Interceptor didn't allow action invocation to proceed");
    ActionContext.getContext().getSession().remove(Constants.SINGLE_SIGNON_USER_KEY);
    ActionContext.getContext().getSession().remove(Constants.AMBRA_USER_KEY);
  }

  @Override
  protected BaseActionSupport getAction() {
    return null;
  }
}

