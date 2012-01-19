/*
 * $HeadURL::                                                                            $ $Id:
 * AmbraStreamResult.java 946 2006-11-03 22:23:42Z viru $
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
package org.ambraproject.struts2;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.struts2.dispatcher.StreamResult;

import org.ambraproject.web.HttpResourceServer;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * Custom webwork result class to stream back objects from OTM blobs. Takes appropriate http
 * headers and sets them the response stream as well as taking in an optional parameter indicating
 * whether to set the content-diposition to an attachment.
 *
 * Reading inputStream requires a transaction. That can be acomplished if action implements
 * TransactionAware interface.
 * 
 */
public class AmbraStreamResult extends StreamResult {

  private boolean isAttachment = false;
  private static final Logger log = LoggerFactory.getLogger(AmbraStreamResult.class);
  private HttpResourceServer server = new HttpResourceServer();

  /*
   * inherited javadoc
   */
  protected void doExecute(String finalLocation, ActionInvocation invocation)
      throws Exception {

    final InputStream inputStream = (InputStream) invocation.getStack().findValue("inputStream");
    Date date = (Date) invocation.getStack().findValue("lastModified");

    HttpResourceServer.Resource resource;
    long lastModified = (date == null) ? System.currentTimeMillis() : date.getTime();

    if (log.isDebugEnabled()) {
      log.debug("LastModified "+new Date(lastModified));
    }

    HttpServletResponse oResponse    =
      (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);

    // If the filestore is setup to handle reproxy we add information
    // to the response header so that perlbal can go directly to the
    // file via http instead of ambra wasting time delivering it.
    String resproxyURL = (String) invocation.getStack().findValue("xReproxyList");
    if (resproxyURL != null) {
      oResponse.addHeader("X-Reproxy-URL", resproxyURL );
      String reproxyCacheSettings = (String) invocation.getStack().findValue("reproxyCacheSettings");
      if (reproxyCacheSettings != null)
        oResponse.addHeader("X-Reproxy-Cache-For", reproxyCacheSettings );
    }

    String name = "--unnamed--";
    // Set the content-disposition
    if (this.contentDisposition != null) {
      name = getProperty("contentDisposition", this.contentDisposition, invocation);
      oResponse.addHeader("Content-disposition", (isAttachment ? "attachment; " : "") + name);
    } else if (isAttachment) {
      oResponse.addHeader("Content-disposition", "attachment;");
    }

    String contentType = getProperty("contentType", this.contentType, invocation);

    Long contentLength = (Long) invocation.getStack().findValue("contentLength");
    if (contentLength == null)
      throw new IllegalArgumentException("'contentLength' must be set in '"
        + invocation.getAction().getClass());

    if (log.isDebugEnabled())
      log.debug("Received InputStream of length="+contentLength);

    resource = new HttpResourceServer.Resource(name, contentType, contentLength, lastModified) {
      public byte[] getContent() {
        return null;
      }

      public InputStream streamContent() throws IOException {
        return inputStream;
      }
    };

    HttpServletRequest  oRequest = (HttpServletRequest) invocation.getInvocationContext().get(HTTP_REQUEST);

    server.serveResource(oRequest, oResponse, resource, resproxyURL);
  }

  private String getProperty(final String propertyName, final String param,final ActionInvocation invocation)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    final Object action        = invocation.getAction();
    final String methodName    = "get" + propertyName.substring(0, 1).toUpperCase()
      + propertyName.substring(1);
    final Method method        = action.getClass().getMethod(methodName);
    final Object o             = method.invoke(action);
    final String propertyValue = o.toString();

    if (null == propertyValue) {
      return conditionalParse(param, invocation);
    }

    return propertyValue;
  }

  /**
   * Tests if the content disposition-type is "attachment".
   *
   * @return Returns the isAttachment.
   */
  public boolean isAttachment() {
    return isAttachment;
  }

  /**
   * If set to true, will add attachment to content disposition
   *
   * @param isAttachment The isAttachment to set.
   */
  public void setIsAttachment(boolean isAttachment) {
    this.isAttachment = isAttachment;
  }
}
