/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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

package org.ambraproject.admin.service.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ambraproject.admin.service.CrossRefPosterService;

import java.io.File;
import java.io.IOException;

public class CrossRefPosterServiceImp implements CrossRefPosterService {

  private static final Logger log = LoggerFactory.getLogger(CrossRefPosterServiceImp.class);

  private String doiXrefUrl;

  public void init() {
  }

  public void setDoiXrefUrl(final String doiXrefUrl) {
    this.doiXrefUrl = doiXrefUrl;
  }

  public int post(File file) throws IOException {
    PostMethod poster = new PostMethod(doiXrefUrl);
    HttpClient client = new HttpClient();

    Part[] parts = {new FilePart("fname", file.getName(), file)};

    poster.setRequestEntity(new MultipartRequestEntity(parts, poster.getParams()));
    client.getHttpConnectionManager().getParams().setConnectionTimeout(25000);
    long time = System.currentTimeMillis();
    int responseCode = client.executeMethod(poster);
    if (log.isInfoEnabled())
      log.info("CrossRef call to " + doiXrefUrl + " finished with response "+ responseCode + " in " + 
          (System.currentTimeMillis() - time) + " ms");
    return responseCode;
  }
}
