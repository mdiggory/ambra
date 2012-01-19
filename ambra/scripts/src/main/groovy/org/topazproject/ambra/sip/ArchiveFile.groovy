/* $HeadURL::                                                                                    $
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

package org.topazproject.ambra.sip

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorException
import org.apache.commons.compress.compressors.CompressorStreamFactory

/**
 * An ArchiveFile - similar in line to ZipFile
 */
class ArchiveFile {
  static def archiverTypes = ['.zip':'zip', '.tar':'tar',
                              '.tgz':'tar', '.tar.gz':'tar', 
                              '.tbz':'tar', '.tar.bz':'tar',
                              '.tb2':'tar', '.tbz2':'tar', '.tar.bz2':'tar']
  static def compressorTypes = ['.tgz':'gz', '.tar.gz':'gz',
                                '.tbz':'bzip2', '.tar.bz':'bzip2',
                                '.tb2':'bzip2', '.tbz2':'bzip2', '.tar.bz2':'bzip2']
  private def tmpFiles     = [:]
  private def elements     = []
  private def openStreams  = []
  private File file
  private def ct, at, name

  @Override
  protected void finalize() throws Throwable {
    release()
    super.finalize()
  }

  ArchiveFile(String name) throws IOException {
    this(new File(name))
  }

  ArchiveFile(File file) throws IOException {
    at = getArchiveType(file)
    if (at == null)
      throw new IOException("Not an archive file: " + file)

    ct = getCompressorType(file)

    InputStream st = new FileInputStream(file)
    try {
      ArchiveInputStream zis = getArchiveInputStream(st, ct, at)
      st = zis
      ArchiveEntry ze
      while((ze = zis.getNextEntry()) != null) {
        elements.add(ze)
        OutputStream out = null
        File f = null
        try {
          f = File.createTempFile("ambra-unzip-entry", "")
          f.deleteOnExit()
          out = f.newOutputStream()
          out << zis
          tmpFiles.put(ze.getName(), f)
          f = null
        } finally {
          if (out != null)
            out.close()
          if (f != null)
            f.delete()
        }
      }
    } finally {
      try {
        st.close()
      } catch (Throwable t) {

      }
    }
    this.file = file
    this.name = file.getName()
  }

  int size() {
    if (file == null)
      throw new IllegalStateException("Archive is closed.")
    return elements.size()
  }


  void close() throws IOException {
    release()
    file = null
  }

  private void release() {
    for (st in openStreams) {
      try {
        st.unregister()
        st.close()
      } catch (Throwable t) {

      }
    }

    openStreams.clear()

    for (f in tmpFiles.values()) {
      try {
        f.delete()
      } catch (Throwable t) {
      }
    }

    tmpFiles.clear()
  }

  Iterator<? extends ArchiveEntry> entries() {
    if (file == null)
      throw new IllegalStateException("Archive is closed.")

    return elements.iterator()
  }

  ArchiveEntry getEntry(String name) {
    if (file == null)
      throw new IllegalStateException("Archive is closed.")

    for (ArchiveEntry e : elements)
      if (e.getName().equals(name))
        return e

    return null
  }

  InputStream getInputStream(String n) throws IOException {
    if (file == null)
      throw new IllegalStateException("Archive is closed.")

    File f = tmpFiles.get(n)
    if (f == null)
      throw new IOException("No such entry <" + n + "> in " + name)

    return new EntryStream(f, openStreams)
  }

  InputStream getInputStream(ArchiveEntry ze) throws IOException {
    getInputStream(ze.name)
  }

  static String getArchiveType(File file) {
    String name = file.getName().toLowerCase()
    for (e in archiverTypes)
      if (name.endsWith(e.key))
        return e.value

    return null
  }

  static String getCompressorType(File file) {
    String name = file.getName().toLowerCase()
    for (e in compressorTypes)
      if (name.endsWith(e.key))
        return e.value

    return null
  }

  static ArchiveInputStream getArchiveInputStream(InputStream st, 
                                       String compressor, String archiver) throws IOException {
    try {
      if (compressor != null) {
        if ("bzip2".equals(compressor))
          st.read(new byte[2])  // Remove "Bz"
        CompressorStreamFactory csf = new CompressorStreamFactory()
        st = csf.createCompressorInputStream(compressor, st)
      }
      ArchiveStreamFactory asf = new ArchiveStreamFactory()
      return asf.createArchiveInputStream(archiver, st)
    } catch (CompressorException e) {
      IOException ioe = new IOException("Failed to create stream")
      ioe.initCause(e)
      throw ioe
    } catch (ArchiveException e) {
      IOException ioe = new IOException("Failed to create stream")
      ioe.initCause(e)
      throw ioe
    }
  }
}

private static class EntryStream extends FileInputStream {
  private Collection<InputStream> streams
  public EntryStream(File file, Collection<InputStream> streams) {
    super(file)
    this.streams = streams
    if (streams != null)
      streams.add(this)
  }

  void close() throws IOException {
    if (streams != null)
      streams.remove(this)
    super.close()
  }

  void unregister() {
    streams = null
  }

}
