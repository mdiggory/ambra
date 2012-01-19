/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/main/java/org/plos/filestore/FileStoreException.java $
 * $Id: FileStoreException.java 9117 2011-06-10 22:07:29Z josowski $
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

package org.ambraproject.filestore;

/**
 * @author Alex Kudlick Date: Mar 8, 2011
 *         <p/>
 *         org.ambraproject.filestore
 */
public class FileStoreException extends Exception{
  public FileStoreException() {
  }

  public FileStoreException(String message) {
    super(message);
  }

  public FileStoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public FileStoreException(Throwable cause) {
    super(cause);
  }
}
