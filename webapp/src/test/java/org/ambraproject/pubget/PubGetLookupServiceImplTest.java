/*
 * $HeadURL$
 * $Id$
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

package org.ambraproject.pubget;

import org.apache.commons.httpclient.HttpClientMock;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Dragisa Krsmanovic
 */
public class PubGetLookupServiceImplTest {
  @Test
  public void testGetPDFLink() throws Exception {

    PubGetLookupServiceImpl service = new PubGetLookupServiceImpl();
    HttpClientMock mockHttpClient = new HttpClientMock(200,
       "vals = [{\"doi\": \"10.1371/journal.pone.0000988\", \"values\": {\"pmid\": \"17912365\", \"id\": \"de1fedebc3bb6119132cc5f6fd5f8ff1\", \"link\": \"http://pubget.com/paper/17912365\", \"doi\": \"10.1371/journal.pone.0000988\"}}]; ");
    service.setHttpClient(mockHttpClient);
    service.setPubGetUrl("http://test.org");

    String result = service.getPDFLink("10.1371/journal.pone.0000988");

    String expectedQueryUrl = "http://test.org?oa_only=true&dois=10.1371/journal.pone.0000988";
    assertEquals(mockHttpClient.getMethod().getURI().toString(), expectedQueryUrl);
    assertEquals(result, "http://pubget.com/paper/17912365");
  }

  @Test
  public void testPubGetNotConfigured() throws Exception {

    PubGetLookupServiceImpl service = new PubGetLookupServiceImpl();
    HttpClientMock mockHttpClient = new HttpClientMock(200,
       "vals = [{\"doi\": \"10.1371/journal.pone.0000988\", \"values\": {\"pmid\": \"17912365\", \"id\": \"de1fedebc3bb6119132cc5f6fd5f8ff1\", \"link\": \"http://pubget.com/paper/17912365\", \"doi\": \"10.1371/journal.pone.0000988\"}}]; ");
    service.setHttpClient(mockHttpClient);

    String result = service.getPDFLink("10.1371/journal.pone.0000988");

    assertNull(result);
  }

  @Test
  public void testGarbledResponse() throws Exception {

    PubGetLookupServiceImpl service = new PubGetLookupServiceImpl();
    HttpClientMock mockHttpClient = new HttpClientMock(200,
       "[this is junk]");
    service.setHttpClient(mockHttpClient);

    String result = service.getPDFLink("10.1371/journal.pone.0000988");

    assertNull(result);
  }

}
