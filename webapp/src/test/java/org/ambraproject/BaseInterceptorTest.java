/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.ambraproject.action.BaseActionSupport;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base test for tests of interceptors and filters.  This class sets up a MockActionInvocation before each method
 *
 * @author Alex Kudlick Date: 2/10/12
 */
public abstract class BaseInterceptorTest extends BaseWebTest {

  protected MockActionInvocation actionInvocation;

  @Override
  public void setDefaultRequest() {
    //do nothing (we don't want to call setupUserContext twice)
  }

  @BeforeMethod
  public void setupActionInvocation() {
    setupUserContext();
    actionInvocation = new MockActionInvocation() {
      public String invoke() throws Exception {
        return Action.SUCCESS;
      }
    };
    actionInvocation.setInvocationContext(ActionContext.getContext());
  }

  @Override
  protected BaseActionSupport getAction() {
    return null;  //we don't have an action to test
  }

  @Override
  protected void setupAdminContext() {
    super.setupAdminContext();
    actionInvocation.setInvocationContext(ActionContext.getContext());
  }


}
