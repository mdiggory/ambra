/*
 * $HeadURL$
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject.annotation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.ambraproject.testutils.MockAmbraUser;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.Reply;
import org.topazproject.ambra.models.ReplyBlob;
import org.topazproject.ambra.models.ReplyThread;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import static org.testng.Assert.*;

/**
 * Test for ReplyService implementations.  One thing to note is that every Reply in ambra is actually a ReplyThread ...
 * sigh. The services should expect reply thread objects
 *
 * @author Alex Kudlick Date: 5/4/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
public class ReplyServiceTest extends BaseTest {
    private static final String DEFAULT_ADMIN_AUTHID = "AdminAuthorizationID";
  @Autowired
  protected ReplyService replyService;

  /**
   * Data Provider for methods that need replies which haven't been saved to the store
   *
   * @return - 1-element arrays of dummy replies that haven't been saved to the store
   * @throws UnsupportedEncodingException - from String.getBytes(Charset) to set in a reply body
   */
  @DataProvider(name = "dummy replies")
  public Object[][] dummyReplies() throws UnsupportedEncodingException {
    //We need the replies to point to an annotation
    String root = dummyDataStore.store(new Comment());

    Reply reply = new ReplyThread();
    reply.setRoot(root);
    reply.setInReplyTo("id://first-comment");
    reply.setTitle("Very Important Reply");
    reply.setCreator("Alan Turing");
    reply.setCreated(new Date());

    Reply replyWithBody = new ReplyThread();
    replyWithBody.setRoot(root);
    replyWithBody.setInReplyTo("id://the-first-reply");
    replyWithBody.setTitle("This is a reply with a body");
    replyWithBody.setCreator("John Searle");
    reply.setCreated(new Date());

    ReplyBlob body = new ReplyBlob();
    body.setCIStatement("CI Statement");
    body.setBody("Test reply  body".getBytes(replyService.getEncodingCharset()));

    return new Object[][]{
        {reply},
        {replyWithBody}
    };
  }

  /**
   * Test replyService.createReply() method
   *
   * @param reply - a reply with properties to use that hasn't been saved to the data store;
   * @throws Exception - from createReply()
   */
  @Test(dataProvider = "dummy replies")
  public void testCreateReply(Reply reply) throws Exception {
    String id = replyService.createReply(
        reply.getRoot(),
        reply.getInReplyTo(),
        reply.getTitle(),
        "text/plain",
        "Test body with hash code: " + reply.hashCode(),
        "ci statement",
        new MockAmbraUser(reply.getCreator())
    );
    assertNotNull(id, "generated null Id for reply");
    assertFalse(id.isEmpty(), "generated empty id for reply");
  }

  /**
   * Data provider for methods that need replies that are saved to the store
   *
   * @return - 2-element arrays of an id and reply object, in order
   * @throws UnsupportedEncodingException - from String.getBytes(Charset)
   */
  @DataProvider(name = "saved replies")
  public Object[][] savedReplies() throws UnsupportedEncodingException {
    Object[][] dummyReplies = dummyReplies();
    Object[][] savedReplies = new Object[dummyReplies.length][2];
    for (int i = 0; i < savedReplies.length; i++) {
      Reply reply = (Reply) dummyReplies[i][0];
      savedReplies[i] = new Object[]{dummyDataStore.store(reply), reply};
    }

    return savedReplies;
  }

  /**
   * @return - 1 array with a list of ids and a corresponding list of expected Replies
   * @throws UnsupportedEncodingException - from string.getBytes(Charset)
   */
  @DataProvider(name = "saved reply list")
  public Object[][] savedRepyList() throws UnsupportedEncodingException {
    Object[][] savedReplies = savedReplies();
    List<Reply> replies = new ArrayList<Reply>(savedReplies.length);
    List<String> ids = new ArrayList<String>(savedReplies.length);

    for (Object[] savedReply : savedReplies) {
      ids.add((String) savedReply[0]);
      replies.add((Reply) savedReply[1]);
    }

    return new Object[][]{
        {ids, replies}
    };
  }

  /**
   * Test the replyService.getReply() method
   *
   * @param replyId       - Id of reply which has been saved to the data store
   * @param expectedReply - reply with expected properties set on it
   */
  @Test(dataProvider = "saved replies")
  public void testGetReply(String replyId, Reply expectedReply) {
    Reply reply = replyService.getReply(replyId);
    assertNotNull(reply, "returned null reply");
    assertEquals(reply.getId(), URI.create(replyId), "didn't return reply with correct id");
    compareBasicReplyProperties(reply, expectedReply);
    compareReplyBodies(reply.getBody(), expectedReply.getBody());
  }

  @Test(dataProvider = "saved reply list")
  public void testGetReplies(List<String> replyIds, List<Reply> expectedReplies) {
    List<Reply> replies = replyService.getReplies(replyIds);
    assertNotNull(replies, "returned null list of replies");
    assertEquals(replies.size(), expectedReplies.size(), "didn't return correct number of replies");
    for (int i = 0; i < replies.size(); i++) {
      assertEquals(replies.get(i).getId(), URI.create(replyIds.get(i)), "returned reply with incorrect id");
      compareBasicReplyProperties(replies.get(i), expectedReplies.get(i));
    }
  }

  @DataProvider(name = "root and inReplyTo list")
  public Object[][] rootAndInReplyTo() {
    String firstRoot = "id://root-1";
    String firstInReplyTo = "id://inReplyTo-1";

    Reply reply1 = new ReplyThread();
    reply1.setRoot(firstRoot);
    reply1.setInReplyTo(firstInReplyTo);

    Reply reply2 = new ReplyThread();
    reply2.setRoot(firstRoot);
    reply2.setInReplyTo(firstInReplyTo);

    String[] expectedIds1 = new String[]{
        dummyDataStore.store(reply1),
        dummyDataStore.store(reply2)
    };

    Reply reply3 = new ReplyThread();
    String secondRoot = "id://new-root";
    reply3.setRoot(secondRoot);
    String secondInReplyTo = "id://new-in-reply-to";
    reply3.setInReplyTo(secondInReplyTo);
    String[] expectedIds2 = new String[]{
        dummyDataStore.store(reply3)
    };

    return new Object[][]{
        {firstRoot, firstInReplyTo, expectedIds1},
        {secondRoot, secondInReplyTo, expectedIds2}
    };
  }

  @Test(dataProvider = "root and inReplyTo list")
  public void testListRepliesByRoot(String root, String inReplyTo, String[] expectedIds) {
    Reply[] replies = replyService.listReplies(root, inReplyTo);
    assertNotNull(replies, "returned null list of replies");
    assertEquals(replies.length, expectedIds.length, "didn't return correct number of replies");
    String[] actualIds = new String[replies.length];
    for (int i = 0; i < actualIds.length; i++) {
      actualIds[i] = replies[i].getId().toString();
    }
    assertEqualsNoOrder(actualIds, expectedIds, "Didn't return expected ids");
  }

  @DataProvider(name = "mediator and state list")
  public Object[][] mediatorAndStateList() {
    String firstMediator = "id://first-mediator";
    int firstState = -23487;

    Reply reply1 = new ReplyThread();
    reply1.setMediator(firstMediator);
    reply1.setState(firstState);

    Reply reply2 = new ReplyThread();
    reply2.setMediator(firstMediator);
    reply2.setState(firstState);

    String[] expectedIds1 = new String[]{
        dummyDataStore.store(reply1),
        dummyDataStore.store(reply2)
    };

    String secondMediator = "id://second-test-mediator";
    int secondState = 47;
    Reply reply3 = new ReplyThread();
    reply3.setMediator(secondMediator);
    reply3.setState(secondState);

    String[] expectedIds2 = new String[]{
        dummyDataStore.store(reply3)
    };

    return new Object[][]{
        {firstMediator, firstState, expectedIds1},
        {secondMediator, secondState, expectedIds2}
    };
  }

  @Test(dataProvider = "mediator and state list")
  public void testListRepliesByMediatorAndState(String mediator, int state, String[] expectedIds) {
    Reply[] replies = replyService.listReplies(mediator, state);
    assertNotNull(replies, "returned null list of replies");
    assertEquals(replies.length, expectedIds.length, "didn't return correct number of replies");
    String[] actualIds = new String[replies.length];
    for (int i = 0; i < actualIds.length; i++) {
      actualIds[i] = replies[i].getId().toString();
    }
    assertEqualsNoOrder(actualIds, expectedIds, "Didn't return expected ids");
  }

  @DataProvider(name = "start and end dates")
  public Object[][] startAndEndDates() {
    Calendar tenMinutesAgo = Calendar.getInstance();
    tenMinutesAgo.add(Calendar.MINUTE, -10);

    return new Object[][]{
        {tenMinutesAgo.getTime(), new Date(), 1},
        {tenMinutesAgo.getTime(), new Date(), 0},
        {tenMinutesAgo.getTime(), new Date(), 2}
    };
  }

  /**
   * Test for replyService.getReplies() which filters by date
   *
   * @param startDate - start date to use
   * @param endDate   - end date to use
   * @param limit     - max number of replies to return
   * @throws Exception - from replyService.getReplies()
   */
  @Test(dataProvider = "start and end dates", dependsOnMethods = {"testCreateReply"})
  public void testGetRepliesByDate(Date startDate, Date endDate, int limit) throws Exception {
    Set<String> annotTypes = AnnotationServiceTest.getAnnotationTypes();

    List<Reply> replies = replyService.getReplies(startDate, endDate, annotTypes, limit);
    assertNotNull(replies, "null list of replies");
    assertTrue(replies.size() > 0, "empty list of replies");
    //limit = 0 specifies no limit
    assertTrue(limit == 0 || replies.size() <= limit, "returned more ids than the specified limit");

    for (Reply reply : replies) {
      assertNotNull(reply, "returned null reply");
    }

  }

  @Test(dataProvider = "root and inReplyTo list")
  public void testDeleteReplies(String root, String inReplyTo, String[] deletedReplyIds) {

    replyService.deleteReplies(root, DEFAULT_ADMIN_AUTHID, inReplyTo);
    for (String id : deletedReplyIds) {
      try {
        replyService.getReply(id);
        fail("reply service should've thrown Illegal argument exception for deleted reply");
      } catch (IllegalArgumentException e) {
        //expected
      }
    }
  }

  @DataProvider(name = "nested reply threads")
  public Object[][] nestedReplyThreads() {
    ReplyThread rootThread = new ReplyThread();
    String inReplyTo = "id://test-in-reply-to";
    rootThread.setInReplyTo(inReplyTo);

    ReplyThread reply1 = new ReplyThread();
    String firstThreadId = dummyDataStore.store(reply1);

    ReplyThread reply2 = new ReplyThread();
    reply2.setInReplyTo(firstThreadId);
    String secondThreadId = dummyDataStore.store(reply2);

    List<ReplyThread> replyThreads = new LinkedList<ReplyThread>();
    replyThreads.add(reply1);
    replyThreads.add(reply2);
    rootThread.setReplies(replyThreads);
    String rootThreadId = dummyDataStore.store(rootThread);

    List<String> ids = new LinkedList<String>();
    ids.add(rootThreadId);
    ids.add(firstThreadId);
    ids.add(secondThreadId);

    return new Object[][]{
        {rootThreadId, inReplyTo, ids}
    };
  }

  @Test(dataProvider = "nested reply threads")
  public void testDeleteTargetReplies(String rootId, String notUsedInThisTest, List<String> deletedReplyIds) {
    replyService.deleteReplies(rootId,DEFAULT_ADMIN_AUTHID);
    for (String id : deletedReplyIds) {
      try {
        replyService.getReply(id);
        fail("reply service should've thrown IllegalArgumentException on deleted reply");
      } catch (IllegalArgumentException e) {
        //expected
      }
    }
  }

  @DataProvider(name = "reply chain")
  public Object[][] replyChain() {
    String root = "id://root-1";
    String firstInReplyTo = "id://test-in-reply-to";

    //Reply chain goes head1 <- child1 <- child2
    Reply head1 = new ReplyThread();
    head1.setRoot(root);
    head1.setInReplyTo(firstInReplyTo);
    String head1Id = dummyDataStore.store(head1);

    Reply child1 = new ReplyThread();
    child1.setRoot(root);
    child1.setInReplyTo(head1Id);
    String child1Id = dummyDataStore.store(child1);
    Reply child2 = new ReplyThread();
    child2.setRoot(root);
    child2.setInReplyTo(child1Id);
    String child2Id = dummyDataStore.store(child2);

    Reply head2 = new ReplyThread();
    head2.setRoot(root);
    head2.setInReplyTo(firstInReplyTo);
    String head2Id = dummyDataStore.store(head2);

    return new Object[][]{
        {root, firstInReplyTo, new String[]{head1Id, child1Id, child2Id, head2Id}}
    };
  }

  @Test(dataProvider = "reply chain")
  public void testListAllReplies(String rootId, String inReplyTo, String[] expectedIds) {
    //Test where root and inReplyTo are the same
    Reply[] replies = replyService.listAllReplies(rootId, rootId);
    assertNotNull(replies, "returned null list of replies");
    assertTrue(replies.length > 0, "returned empty list of replies");

    //Test normal use
    replies = replyService.listAllReplies(rootId, inReplyTo);
    assertNotNull(replies, "returned null list of replies");
    assertTrue(replies.length > 0, "returned empty list of replies");

    String[] actualIds = new String[replies.length];
    for (int i = 0; i < replies.length; i++) {
      actualIds[i] = replies[i].getId().toString();
    }

    assertEqualsNoOrder(actualIds, expectedIds, "Didn't return correct replies");

  }

  /**
   * Helper method to compare some basic properties of replies
   *
   * @param actualReply   - the actual reply
   * @param expectedReply - reply with expected properties set
   */
  private void compareBasicReplyProperties(Reply actualReply, Reply expectedReply) {
    assertEquals(actualReply.getRoot(), expectedReply.getRoot(), "Reply didn't have correct 'root'");
    assertEquals(actualReply.getInReplyTo(), expectedReply.getInReplyTo(), "Reply didn't have correct 'inReplyTo'");
    assertEquals(actualReply.getTitle(), expectedReply.getTitle(), "Reply didn't have correct title");
    assertEquals(actualReply.getCreator(), expectedReply.getCreator(), "Reply didn't have correct creator");
  }

  /**
   * Compare properties of reply blobs
   *
   * @param actualBody   - the actual reply blob
   * @param expectedBody - blob with expected properties set
   */
  private void compareReplyBodies(ReplyBlob actualBody, ReplyBlob expectedBody) {
    if (expectedBody == null) {
      assertNull(actualBody, "Expected null reply body");
      return;
    }
    assertEquals(actualBody.getBody(), expectedBody.getBody(),
        "Reply body didn't have correct body array");
    assertEquals(actualBody.getCIStatement(), expectedBody.getCIStatement(),
        "reply body didn't have correct ci statement");
  }
}
