/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/main/java/org/plos/filestore/FileStoreService.java $
 * $Id: FileStoreService.java 9658 2011-10-05 21:59:11Z wtoconnor $
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/**
 * Interface for interacting with the ambra file store; most serving of files should be done via
 * a redirect to the file store's server. All files in the store should have a unique ID; requests
 * are made via that id
 *
 * <p/>
 * User: Alex Kudlick Date: Mar 8, 2011
 * <p/>
 * org.ambraproject.filestore
 */
public interface FileStoreService {

  /**
   * Query whether filestore provides HTTP redirects to files. The
   * underlying assumption is that the filestore is/also knows about
   * the reproxy server.
   *
   * @return - true if filestore can provide redirects for HTTP access to files.
   */
  public Boolean hasXReproxy();

  /**
   * Get the reproxy cache settings
   *
   * @return - string of cache setting
   */
  public String getReproxyCacheSettings();

  /**
   * Get a URL array which can be used as a links to the specified file
   * via XReproxy-url http header redirection.
   *
   * @param fsid - the file system's id for the file requested
   * @return - URL that can be used as a redirect for the file
   * @throws FileStoreException - if the given id does not correspond to a file in the store
   */
  public URL[] getRedirectURL(String fsid) throws FileStoreException;

  /**
   * Get a list of the files in the file store for the given doi (e.g. article xml, images, etc.)
   *
   * @param fsid - the file system's id for the file requested
   * @return a map from the file names (keys) to the file ids (values) for the given doi
   * @throws FileStoreException - if the specified file doesn't exist in the store
   */
  public Map<String, String> listFiles(String fsid) throws FileStoreException;

  /**
   * Given a unique id and byte count return a OutputStream that can
   * be used write a file to the filestore. If ID is already
   * associated with an existing file that data is lost.
   *
   * @param fsid  - the file system's id for the file requested,
   * @param byteCount - size of data
   * @throws FileStoreException
   * @return  -
   */
  public OutputStream getFileOutStream(String fsid,long byteCount) throws FileStoreException;

  /**
   * Given a unique id return a InputStream that can be used to
   * transfer a file from the filestore.
   *
   * @param fsid  -the file system's id for the file requested,
   * @throws FileStoreException
   * @return  - InputStream
   */
  public InputStream getFileInStream(String fsid) throws FileStoreException;

  /**
   * Copy from filestore using ID to the destination file (destFile).
   *
   * @param fsid  - the file system's id for the file requested,
   * @param destFile - destination file to read and transfer to filestore using id as
   *                   a unique identifier.
   * @throws FileStoreException - if there is a problem storing the file
   * @return - the destination file
   */
  public File copyFileFromStore(String fsid, File destFile) throws FileStoreException;

  /**
   * Copy from the source file (srcFile) to the filestore using ID.
   *
   * @param fsid  - the file system's id for the file to store (use {@link FSIDMapper} to generate fsids),
   * @param srcFile - destination file to read and transfer to filestore using id as
   *                   a unique identifier.
   * @throws FileStoreException - if there is a problem writing to the file
   */
  public void copyFileToStore(String fsid, File srcFile) throws FileStoreException;

  /**
   * Copy from an InputStream to the OutputStream.This doesn't have
   * much to do with filestore other than it is a common operation.
   * It is provided here for convenience.
   *
   * @param from  - unique identifier for this file,
   * @param to - destination file to read and transfer to filestore using id as
   *                   a unique identifier.
   * @throws IOException
   */
  public void copy(InputStream from, OutputStream to) throws IOException;

  /**
   * Get the file identified by the unique id  and reurn it
   * in a byte array.
   *
   * @param fsid  - the file system's id for the file requested,
   * @throws FileStoreException
   */
  public byte[] getFileByteArray(String fsid) throws FileStoreException;

  /**
   * Delete the file identified by the unique id.
   *
   * @param fsid  - the file system's id for the file requested,
   * @throws FileStoreException
   */
  public void deleteFile(String fsid) throws FileStoreException;

  /**
   * Rename the a unique identifier. There will be no exception
   * if oldId does not already exist.
   *
   * @param oldId  - old unique identifier for this file.
   * @param newId  - new unique identifier for this file,
   * @throws FileStoreException
   */
  public void renameFile(String oldId, String newId) throws FileStoreException;

}