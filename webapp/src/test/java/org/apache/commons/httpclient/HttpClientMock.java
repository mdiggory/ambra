package org.apache.commons.httpclient;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Mock HttpClient
 *
 * See <a href="http://blog.newsplore.com/2010/02/09/unit-testing-with-httpclient">Unit testing with Commons HttpClient library</a>
 */
public class HttpClientMock extends HttpClient {
  private int expectedResponseStatus;
  private String expectedResponseBody;
  private HttpMethod method;

  public HttpClientMock(int responseStatus, String responseBody) {
    this.expectedResponseStatus = responseStatus;
    this.expectedResponseBody = responseBody;
  }

  @Override
  public int executeMethod(HttpMethod method) throws UnsupportedEncodingException {
    this.method = method;

    ((HttpMethodBase) method).setResponseStream(
        new ByteArrayInputStream(expectedResponseBody.getBytes("UTF-8")));
    return expectedResponseStatus;
  }

  public HttpMethod getMethod() {
    return method;
  }
}
