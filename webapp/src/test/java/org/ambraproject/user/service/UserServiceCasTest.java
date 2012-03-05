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

package org.ambraproject.user.service;

import org.ambraproject.BaseHttpTest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for the methods of {@link UserService} that talk to CAS
 * @author Alex Kudlick 2/16/12
 */
public class UserServiceCasTest extends BaseHttpTest {

  @Autowired
  protected UserService userService;

  @Test
  @DirtiesContext
  public void testGetEmailFromCas() {
    final String authId = "testAuthIdForGettingEmail";
    final String email = "email@getEmailTest.org";
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        assertEquals(exchange.getIn().getHeader("guid"), authId, "user service didn't send correct guid");
        exchange.getOut().setBody(email);
      }
    });
    String result = userService.fetchUserEmailFromCas(authId);
    assertEquals(result,email,"User Service didn't return correct email");
  }

}
