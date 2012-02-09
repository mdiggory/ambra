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
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.ambraproject.action.BaseActionSupport;
import org.apache.axis.utils.Admin;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class EnsureRoleInterceptorTest extends BaseWebTest {

  @Autowired
  protected EnsureRoleInterceptor interceptor;

  @Test
  public void testShouldReturnNotSufficientRole() throws Exception {
    MockActionInvocation actionInvocation = new MockActionInvocation();
    actionInvocation.setAction(null);
    actionInvocation.setInvocationContext(ActionContext.getContext());

    final String result = interceptor.intercept(actionInvocation);
    assertEquals(result, Constants.ReturnCode.NOT_SUFFICIENT_ROLE, "Interceptor didn't block action invocation");
  }

  @Test
  public void testShouldForwardToOriginalActionAsUserIsAdmin() throws Exception {
    setupAdminContext();
    final String actionCalledStatus = "actionCalled";
    final MockActionInvocation actionInvocation = new MockActionInvocation() {
      public String invoke() throws Exception {
        return actionCalledStatus;
      }
    };
    actionInvocation.setInvocationContext(ActionContext.getContext());
    final String result = interceptor.intercept(actionInvocation);
    assertEquals(result, actionCalledStatus, "Interceptor didn't allow action invocation to continue");
  }

  @Override
  protected BaseActionSupport getAction() {
    return null;
  }
}
