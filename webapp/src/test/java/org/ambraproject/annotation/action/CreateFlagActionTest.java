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

package org.ambraproject.annotation.action;

import com.opensymphony.xwork2.Action;
import org.ambraproject.BaseWebTest;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Flag;
import org.ambraproject.models.FlagReasonCode;
import org.ambraproject.models.UserProfile;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alex Kudlick 3/15/12
 */
public class CreateFlagActionTest extends BaseWebTest {
  @Autowired
  protected CreateFlagAction action;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }
  
  @Test
  public void testCreateFlag() throws Exception {
    UserProfile user = new UserProfile("authIdForCreateFlagAction", "email@createFlagAction.org", "displayNameForCreateFlagAction");
    dummyDataStore.store(user);
    login(user);
    Annotation annotation = new Annotation(user, AnnotationType.REPLY, 2134l);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    String comment = "This is spam!";
    action.setTarget(annotationId);
    action.setReasonCode("spam");
    action.setComment(comment);

    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 0,
        "Action returned field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));
    assertNotNull(action.getFlagId(), "Action had null flag id");

    Flag storedFlag = dummyDataStore.get(Flag.class, action.getFlagId());
    
    assertNotNull(storedFlag, "action didn't store flag to the database");
    assertEquals(storedFlag.getReason(), FlagReasonCode.SPAM, "Stored flag had incorrect reason code");
    assertEquals(storedFlag.getComment(), comment, "stored flag had incorrect comment");
    assertNotNull(storedFlag.getFlaggedAnnotation(), "stored flag had no annotation");
    assertEquals(storedFlag.getFlaggedAnnotation().getID(), annotationId, "stored flag had incorrect annotation");
  }

  @Test
  public void testCreateWithNoComment() throws Exception {
    UserProfile user = new UserProfile(
        "authIdForCreateWithNoComment",
        "email@createFlagActionWithNoComment.org",
        "displayNameForCreateWithNoComment");
    dummyDataStore.store(user);
    login(user);
    Annotation annotation = new Annotation(user, AnnotationType.REPLY, 2134l);
    Long annotationId = Long.valueOf(dummyDataStore.store(annotation));

    action.setTarget(annotationId);
    action.setReasonCode("spam");
    action.setComment(null);

    String result = action.execute();
    assertEquals(result, Action.INPUT, "Action didn't return input");
    assertEquals(action.getActionErrors().size(), 0,
        "Action returned unexpected error messages: " + StringUtils.join(action.getActionErrors(), ";"));
    assertEquals(action.getFieldErrors().size(), 1,
        "Action returned unexpected field errors: " + StringUtils.join(action.getFieldErrors().values(), ";"));
    assertNotNull(action.getFieldErrors().get("comment"), "action didn't have error message for comment");
  }
}
