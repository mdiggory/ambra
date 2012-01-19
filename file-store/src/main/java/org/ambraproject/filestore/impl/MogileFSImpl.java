/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/main/java/org/plos/filestore/impl/MogileFSImpl.java $
 * $Id: MogileFSImpl.java 9658 2011-10-05 21:59:11Z wtoconnor $
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

package org.ambraproject.filestore.impl;

import com.guba.mogilefs.PooledMogileFSImpl;
import com.guba.mogilefs.NoTrackersException;
import com.guba.mogilefs.BadHostFormatException;
import org.ambraproject.filestore.FileStoreService;
import org.ambraproject.filestore.FileStoreException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link org.ambraproject.filestore.FileStoreService} that uses MogileFS
 * for storing files;
 *
 * @author Bill OConnor
 *
 **/

public class MogileFSImpl extends PooledMogileFSImpl implements FileStoreService {

  private String storageClass = "default";
  private Boolean reproxyEnable = Boolean.FALSE;
  private String reproxyCacheSettings;

  /**
   * The constructor for our mogile implementation for the FileStoreService
   * @param domain
   * @param hosts
   * @param maxTrackerConnections
   * @param maxIdleConnections
   * @param maxIdleTimeMillis
   * @throws NoTrackersException
   * @throws BadHostFormatException
   */
  public MogileFSImpl (
    String domain, String hosts, Integer maxTrackerConnections, Integer maxIdleConnections,
    Integer maxIdleTimeMillis, Boolean reproxyEnable, String rerpoxyCacheSettings
  ) throws NoTrackersException, BadHostFormatException {
    super(domain, hosts.split(","), maxTrackerConnections, maxIdleConnections, maxIdleTimeMillis);

    this.reproxyEnable = reproxyEnable;
    this.storageClass = "";
    this.reproxyCacheSettings = rerpoxyCacheSettings;
  }

  public Boolean hasXReproxy() {
    return this.reproxyEnable;
  }

  /**
   * {@link org.ambraproject.filestore.FileStoreService#}
   */
  public String getReproxyCacheSettings() {
    return this.reproxyCacheSettings;
  }

  /*
  *    {@link org.ambraproject.filestore.FileStoreService#}
  */
  public InputStream getFileInStream(String id) throws FileStoreException {
    try {
      return getFileStream(id);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:getFileInStream ", e);
    }
  }

  public byte[] getFileByteArray(String id) throws FileStoreException {
    try {
      return getFileBytes(id);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:getFileByteArray ", e);
    }
  }

  public URL[] getRedirectURL(String id) throws FileStoreException {
    URL[] urls;
    try {
      String[] paths = getPaths(id, true);
      int pathCount = paths.length;
      urls = new URL[pathCount];

      for(int i = 0; i < pathCount; i++) {
        urls[i] = new URL(paths[i]);
      }
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:getRedirectURL ", e);
    }
    return urls;
  }

  public Map<String, String> listFiles(String doi) throws FileStoreException {
    HashMap<String, String> map = new HashMap<String, String>();
    Object[] objects;

    try {
      objects = listKeys(doi);
    } catch (Exception ex) {
      throw new FileStoreException(ex.getMessage(), ex);
    }

    if (objects == null) {
      return map; //if the article hasn't been ingested, return empty map
    }

    //Our implementation returns an array of one object
    //The first object being an array of strings
    String[] files;

    try {
      files = (String[])objects[0];
    } catch (Exception ex) {
      throw new FileStoreException(ex.getMessage(), ex);
    }

    for(String file : files)
    {
      map.put(file, file);
    }

    return map;
  }

  public OutputStream getFileOutStream(String id, long byteCount) throws FileStoreException {
    try {
      return newFile(id, this.storageClass, byteCount);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:getFileOutStream ", e);
    }
  }

  public void copyFileToStore(String id, File destFile) throws FileStoreException {
    try {
      storeFile(id, storageClass, destFile);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:copyFileTo ", e);
    }
  }

  public void copy(InputStream from, OutputStream to) throws IOException {
    byte[] b = new byte[4*1024];
    int read;
    while ((read = from.read(b)) != -1) {
      to.write(b, 0, read);
    }
  }

  public File copyFileFromStore(String id, File srcFile) throws FileStoreException {
    try {
      return getFile(id, srcFile);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:copyFileFrom ", e);
    }
  }

  public void deleteFile(String id) throws FileStoreException {
    try {
      delete(id);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:deleteFile ", e);
    }
  }

  public void renameFile(String oldId, String newId) throws FileStoreException {
    try {
      rename(oldId, newId);
    } catch (Exception e) {
      throw new FileStoreException("MogileFS:renameFile ", e);
    }
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }

}
