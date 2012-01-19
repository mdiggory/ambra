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

package org.ambraproject.model;

import org.topazproject.otm.AbstractBlob;
import org.topazproject.otm.OtmException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Dragisa Krsmanovic
 */
public class MockBlob extends AbstractBlob {

  private ByteArrayOutputStream outputStream = null;
  private ByteArrayInputStream inputStream = null;
  private String content = null;

  /**
   * Use this when you want to write to the blob
   * @param id Blob id
   */
  public MockBlob(String id) {
    super(id);
    outputStream = new ByteArrayOutputStream();
  }

  /**
   * Use this when you want to read from this blob
   * @param id Blob id
   * @param content Text that is in the blob
   */
  public MockBlob(String id, String content) {
    super(id);
    inputStream = new ByteArrayInputStream(content.getBytes());
    this.content = content;
  }

  protected InputStream doGetInputStream() throws OtmException {
    return inputStream;
  }

  protected OutputStream doGetOutputStream() throws OtmException {
    return outputStream;
  }

  protected void writing(OutputStream out) {
  }

  public ChangeState getChangeState() {
    return ChangeState.NONE;
  }

  public ChangeState mark() {
    return ChangeState.NONE;
  }

  public boolean exists() throws OtmException {
    return true;
  }

  public boolean create() throws OtmException {
    return true;
  }

  public boolean delete() throws OtmException {
    return true;
  }

  @Override
  public String toString() {
    if (outputStream != null)
      return outputStream.toString();
    if (inputStream != null)
      return content;
    return null;
  }
}
