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

package org.ambraproject.models;

import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 2/9/12
 */
public class UserLoginTest extends BaseHibernateTest {

  @Test
  public void testSaveLogin() {
    UserLogin login = new UserLogin();
    login.setUserProfileID(12345L);
    login.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.46 Safari/535.11");
    login.setSessionId("12398572345987");
    login.setIP("1233.iu3456.902354");

    Serializable id = hibernateTemplate.save(login);

    UserLogin storedLogin = (UserLogin) hibernateTemplate.get(UserLogin.class, id);
    assertEquals(storedLogin.getUserProfileID(), login.getUserProfileID(), "stored login had incorrect user profile ID");
    assertEquals(storedLogin.getIP(), login.getIP(), "stored login had incorrect IP");
    assertEquals(storedLogin.getUserAgent(), login.getUserAgent(), "stored login had incorrect user-agent");
    assertEquals(storedLogin.getSessionId(), login.getSessionId(), "stored login had incorrect sessionID");
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullUserProfileId() {
    hibernateTemplate.save(new UserLogin());
  }
}
