/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.ApplicationException;
import org.ambraproject.BaseTest;
import org.ambraproject.annotation.service.AnnotationService;
import org.topazproject.ambra.models.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;

import static org.testng.Assert.*;

public class FlagManagementServiceTest extends BaseTest{

  @Autowired
  FlagManagementService flagManagementService;

  @Autowired
  AnnotationService annotationService;

  @DataProvider(name="listFlag")
  public Object[][] listFlag() throws MalformedURLException {

    Comment comment = new Comment();
    comment.setId(URI.create("id://test-id"));
    comment.setAnnotates(URI.create("id://test-annotates"));
    comment.setContext("context");
    comment.setCreator("testid");
    comment.setCreated(new Date());

    //Set the body on the comment
    AnnotationBlob body = new AnnotationBlob();
    body.setId("testbodyid");
    body.setBody("testbody".getBytes());
    body.setCIStatement("ci statement");
    comment.setBody(body);

    dummyDataStore.store(body);
    dummyDataStore.store(comment);

    Comment comment2 = new Comment();
    comment2.setId(URI.create("id://test-id-2"));
    comment2.setAnnotates(comment.getId());
    comment2.setContext("context2");
    comment2.setCreator("testid2");
    comment2.setCreated(new Date());

    //Set the body on the comment2
    AnnotationBlob body2 = new AnnotationBlob();
    body2.setId("testbodyid2");
    body2.setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><flag reasonCode=\"resoncode\"><comment>Another reasoncode.</comment></flag>".getBytes());
    body2.setCIStatement("ci statement-2");
    comment2.setBody(body2);

    dummyDataStore.store(body2);
    dummyDataStore.store(comment2);

    UserProfile up = new UserProfile();
    up.setRealName("realname");
    up.setDisplayName("displayname");
    dummyDataStore.store(up);

    UserAccount ua = new UserAccount();
    ua.setId(URI.create("testid2"));
    ua.setProfile(up);
    dummyDataStore.store(ua);

    return new Object[][]{
      {comment}
    };
  }

  /**
   * Test for FlagManagementService.getFlaggedComments()
   * @param annotationId for which flags should be fetched
   * @throws ApplicationException
   */

  @Test(dataProvider = "listFlag")
  public void testGetFlaggedComments(Annotation annotationId) throws ApplicationException{
    Collection<FlaggedCommentRecord> list = flagManagementService.getFlaggedComments();
    assertNotNull(list, "The list is null");
  }
}
