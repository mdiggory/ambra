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
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.compressors.CompressorException
import org.apache.commons.compress.compressors.CompressorStreamFactory

/**
 * An ArchiveOutputStream - mainly to take care of the requirement
 * from commons-compress tar implementation that the entry header,
 * including the size of the entry needs to be written out first.
 * So this implementation wraps a commons-compress ArchiveOutputStream
 * and buffers the writes and the whole entry is written out when the
 * closeEntry() is called and when we have the entry size.
 *<p>
 * In addition, a few convenience methods like copyFrom() and writeEntry()
 * are added so that most of the work done in SIP preparation can bypass
 * the buffering here and write the entries directly to the underlying
 * stream.
 * </p>
 */
class ArchiveOutputStream extends OutputStream {
  private ByteArrayOutputStream buffer = null
  private String entry = null
  private org.apache.commons.compress.archivers.ArchiveOutputStream out

  ArchiveOutputStream (OutputStream st, String compressor, String archiver) throws IOException {
    try {
      if (compressor != null) {
        if (compressor.equals("bzip2"))
          st << "BZ"
        CompressorStreamFactory csf = new CompressorStreamFactory()
        st = csf.createCompressorOutputStream(compressor, st)
      }
      ArchiveStreamFactory asf = new ArchiveStreamFactory()
      out = asf.createArchiveOutputStream(archiver, st)
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

  void putNextEntry(String name) throws IOException {
    if (entry != null)
      throw new IOException("An entry is already active.")
    entry = name
    if (buffer != null)
      buffer.reset()
    else
      buffer = new ByteArrayOutputStream(4096)
  }

  void closeEntry() throws IOException {
    if (entry == null)
      throw new IOException("No active entry to close.")
    writeEntry(entry, buffer.toByteArray())
    entry = null
  }

  void writeEntry(String name, byte[] buffer) throws IOException {
    writeEntry(name, buffer.length, new ByteArrayInputStream(buffer))
  }

  void writeEntry(String name, long size, InputStream st) throws IOException {
  //  println "Writing entry[name = " + name + ", size = " + size + "]"
    def e;
    if (out instanceof ZipArchiveOutputStream)
      e = new ZipArchiveEntry(name)
    else if (out instanceof TarArchiveOutputStream) {
      e = new TarArchiveEntry(name)
      e.setSize(size)
    } else {
      // Should not happen
      throw new Error("Unknown archive type")
    }

    out.putArchiveEntry(e)
    out << st
    out.closeArchiveEntry()
  }

  void copyFrom(ArchiveFile zf, def entries) throws IOException {
    for (e in entries)
      copyEntry(zf, e)
  }

  private void copyEntry(ArchiveFile zf, String name) throws IOException {
    copyEntry(zf, zf.getEntry(name))
  }

  private void copyEntry(ArchiveFile zf, ArchiveEntry from) throws IOException {
    writeEntry(from.name, from.size, zf.getInputStream(from))
  }

  @Override
  void write(int b) throws IOException {
    if (entry == null)
      throw new IOException("No active entry to append.")

    buffer.write(b)
  }

  @Override
  void write(byte[] b, int off, int len) throws IOException {
    if (entry == null)
      throw new IOException("No active entry to append.")

    buffer.write(b, off, len)
  }

  @Override
  void flush() throws IOException {
    out.flush()
  }

  @Override
  void close() throws IOException {
    out.close()
  }
}

