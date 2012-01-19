/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeGroups;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.user.UserAccountsInterceptor;
import org.ambraproject.web.VirtualJournalContext;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for tests of Action classes, interceptors, or other tests of the web infrastructure. Provides basic Http
 * and struts config objects.
 *
 * @author alex 8/22/11
 */
@ContextConfiguration(locations = "webApplicationContext.xml")
public class BaseWebTest extends BaseTest {

  protected static final String ADMIN_GROUP = "admin";
  protected static final String USER_GROUP = "user";

  /**
   * Set up struts container / context with an admin request.  This is for unit tests that need to access attributes
   * from the session, set up mock action invocations, etc. Unit tests must manually call this method, since testng
   * won't guarantee that {@link #setupAdminContext()} runs before admin group methods, but that {@link
   * #setupUserContext()} does not. That's problematic since these are mutually exclusive contexts.  E.g. suppose we had tests methods
   * <ul>
   *   <li>adminMethod1</li>
   *   <li>adminMethod2</li>
   *   <li>userMethod</li>
   * </ul>
   * If we used <code>@BeforeGroups</code> to set this up, testng could run the methods in the following order:
   * <ol>
   *   <li>setupAdminContext()</li>
   *   <li>adminMethod1()</li>
   *   <li>setupUserContext()</li>
   *   <li>userMethod()</li>
   *   <li>adminMethod2()</li>
   * </ol>
   *
   */
  protected void setupAdminContext() {
    Map<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(UserAccountsInterceptor.AUTH_KEY, DEFAULT_ADMIN_AUTHID);

    setupContext(sessionAttributes);
  }

  /**
   * Set up struts container / context with an admin request.  This is for unit tests that need to access attributes
   * from the session, set up mock action invocations, etc. Unit tests must manually call this method, since testng
   * won't guarantee that {@link #setupAdminContext()} runs before admin group methods, but that {@link
   * #setupUserContext()} does not. That's problematic since these are mutually exclusive contexts.  E.g. suppose we had tests methods
   * <ul>
   *   <li>adminMethod1</li>
   *   <li>adminMethod2</li>
   *   <li>userMethod</li>
   * </ul>
   * If we used <code>@BeforeGroups</code> to set this up, testng could run the methods in the following order:
   * <ol>
   *   <li>setupAdminContext()</li>
   *   <li>adminMethod1()</li>
   *   <li>setupUserContext()</li>
   *   <li>userMethod()</li>
   *   <li>adminMethod2()</li>
   * </ol>
   *
   */
  protected void setupUserContext() {
    Map<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(UserAccountsInterceptor.AUTH_KEY, DEFUALT_USER_AUTHID);
    setupContext(sessionAttributes);
  }

  private void setupContext(Map<String, Object> sessionAttributes) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    for (String attr : sessionAttributes.keySet()) {
      session.setAttribute(attr, sessionAttributes.get(attr));
    }
    request.setSession(session);

    ConfigurationManager configurationManager = new ConfigurationManager();
    configurationManager.addContainerProvider(new XWorkConfigurationProvider());
    Configuration config = configurationManager.getConfiguration();
    Container strutsContainer = config.getContainer();

    ValueStack stack = strutsContainer.getInstance(ValueStackFactory.class).createValueStack();
    stack.getContext().put(ServletActionContext.CONTAINER, strutsContainer);

    ActionContext.setContext(new ActionContext(stack.getContext()));
    ActionContext.getContext().setSession(sessionAttributes);
    ServletActionContext.setContext(ActionContext.getContext());
    ServletActionContext.setRequest(request);
  }


  protected Map<String, Object> getDefaultRequestAttributes() {
    Map<String, Object> requestAttributes = new HashMap<String, Object>();
    requestAttributes.put(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT,
        new VirtualJournalContext("journal", "dfltJournal", "http", 80, "localhost",
            "ambra-webapp", new ArrayList<String>()));
    //make sure the journal exists
    Journal journal = new Journal();
    journal.setId(URI.create("id:base-journal"));
    journal.setKey("journal");
    dummyDataStore.store(journal);

    return requestAttributes;
  }
}
