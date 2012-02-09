/* $HeadURL::                                                                            $
 * $Id:PermissionsTest.java 722 2006-10-02 16:42:45Z viru $
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
package org.ambraproject.annotation.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.permission.service.PermissionsService;

public class PermissionsTest extends BaseTest {
  private String[] roles   = new String[] { "admin" };
  private String user = "user:joe";
  private String user1 = "user:joe1";

  @Autowired
  protected PermissionsService service;

  //TODO:Write Tests here
  @Test
  public void testSomething()
  {
    Assert.assertTrue(true);
  }
}
