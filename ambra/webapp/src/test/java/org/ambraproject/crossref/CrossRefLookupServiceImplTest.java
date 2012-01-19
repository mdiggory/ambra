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

package org.ambraproject.crossref;

import org.apache.commons.httpclient.HttpClientMock;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Dragisa Krsmanovic
 */
public class CrossRefLookupServiceImplTest {

  @Test
  public void testFindArticles() throws Exception {
    CrossRefLookupServiceImpl service = new CrossRefLookupServiceImpl();


    HttpClientMock mockHttpClient = new HttpClientMock(200,
                                                       "00278424,10916490|Proceedings of the National Academy of Sciences|Zhou|94|24|13215|1997|full_text||10.1073/pnas.94.24.13215");
    service.setHttpClient(mockHttpClient);
    service.setCrossRefUrl("http://test.org?qdata=");

    CrossRefArticle expectedArticle = new CrossRefArticle();
    expectedArticle.setIsbn("00278424,10916490");
    expectedArticle.setTitle("Proceedings of the National Academy of Sciences");
    expectedArticle.setFirstAuthor("Zhou");
    expectedArticle.setVolume("94");
    expectedArticle.setEditionNumber("24");
    expectedArticle.setPage("13215");
    expectedArticle.setYear("1997");
    expectedArticle.setResourceType("full_text");
    expectedArticle.setDoi("10.1073/pnas.94.24.13215");


    List<CrossRefArticle> result = service.findArticles("Proc. Natl Acad. Sci. USA", "Zhou");

    assertEquals(result.size(), 1, "Expected 1 result");
    assertEquals(result.get(0), expectedArticle);

    assertEquals(mockHttpClient.getMethod().getURI().toString(),
                 "http://test.org?qdata=Proc.+Natl+Acad.+Sci.+USA%7CZhou%7C%7C%7C");
  }

  @Test
  public void testPunctuationCharacters() throws Exception {
    CrossRefLookupServiceImpl service = new CrossRefLookupServiceImpl();


    HttpClientMock mockHttpClient = new HttpClientMock(200,
                                                       "00278424,10916490|Proceedings of the National Academy of Sciences|Zhou|94|24|13215|1997|full_text||10.1073/pnas.94.24.13215");
    service.setHttpClient(mockHttpClient);
    service.setCrossRefUrl("http://test.org?qdata=");

    CrossRefArticle expectedArticle = new CrossRefArticle();
    expectedArticle.setIsbn("00278424,10916490");
    expectedArticle.setTitle("Proceedings of the National Academy of Sciences");
    expectedArticle.setFirstAuthor("Zhou");
    expectedArticle.setVolume("94");
    expectedArticle.setEditionNumber("24");
    expectedArticle.setPage("13215");
    expectedArticle.setYear("1997");
    expectedArticle.setResourceType("full_text");
    expectedArticle.setDoi("10.1073/pnas.94.24.13215");


    List<CrossRefArticle> result = service.findArticles("Proc; Natl/ Acad? Sci: USA & Canada\n a = b", "Zhou");

    assertEquals(result.size(), 1, "Expected 1 result");
    assertEquals(result.get(0), expectedArticle);

    assertEquals(mockHttpClient.getMethod().getURI().toString(), 
                 "http://test.org?qdata=Proc%3B+Natl%2F+Acad%3F+Sci%3A+USA+%26+Canada%0A+a+%3D+b%7CZhou%7C%7C%7C");
  }


  @Test
  public void testFind2Articles() throws Exception {
    CrossRefLookupServiceImpl service = new CrossRefLookupServiceImpl();


    HttpClientMock mockHttpClient = new HttpClientMock(200,
                                                       "00278424,10916490|Proceedings of the National Academy of Sciences|Zhou|94|24|13215|1997|full_text||10.1073/pnas.94.24.13215\n" +
                                                           "00278425,10916491|Foo|O'Zhou|95|25|13216|2007|||10.1073/pnas.94.24.13216");
    service.setHttpClient(mockHttpClient);
    service.setCrossRefUrl("http://test.org?qdata=");

    CrossRefArticle expectedArticle1 = new CrossRefArticle();
    expectedArticle1.setIsbn("00278424,10916490");
    expectedArticle1.setTitle("Proceedings of the National Academy of Sciences");
    expectedArticle1.setFirstAuthor("Zhou");
    expectedArticle1.setVolume("94");
    expectedArticle1.setEditionNumber("24");
    expectedArticle1.setPage("13215");
    expectedArticle1.setYear("1997");
    expectedArticle1.setResourceType("full_text");
    expectedArticle1.setDoi("10.1073/pnas.94.24.13215");

    CrossRefArticle expectedArticle2 = new CrossRefArticle();
    expectedArticle2.setIsbn("00278425,10916491");
    expectedArticle2.setTitle("Foo");
    expectedArticle2.setFirstAuthor("O'Zhou");
    expectedArticle2.setVolume("95");
    expectedArticle2.setEditionNumber("25");
    expectedArticle2.setPage("13216");
    expectedArticle2.setYear("2007");
    expectedArticle2.setDoi("10.1073/pnas.94.24.13216");

    List<CrossRefArticle> result = service.findArticles("Proc. Natl Acad. Sci. USA", "Zhou");

    assertEquals(result.size(), 2, "Expected 2 result");
    assertEquals(result.get(0), expectedArticle1);
    assertEquals(result.get(1), expectedArticle2);

    assertEquals(mockHttpClient.getMethod().getURI().toString(),
                 "http://test.org?qdata=Proc.+Natl+Acad.+Sci.+USA%7CZhou%7C%7C%7C");


  }

}
