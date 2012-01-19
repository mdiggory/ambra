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

package org.ambraproject.testutils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientMock;
import org.apache.commons.httpclient.HttpMethod;

import java.io.UnsupportedEncodingException;

/**
 * Wrapper around {@link org.apache.commons.httpclient.HttpClientMock} that allows for setting
 *
 * @author Alex Kudlick 9/19/11
 */
public class MockHttpClient extends HttpClient {

  private String responseBody;
  private int responseStatus;

  @Override
  public int executeMethod(HttpMethod method) throws UnsupportedEncodingException {
    return new HttpClientMock(responseStatus, responseBody).executeMethod(method);
  }

  public void setResponse(int responseStatus, String responseBody) {
    this.responseStatus = responseStatus;
    this.responseBody = responseBody;
  }
}
