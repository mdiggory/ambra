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

import org.topazproject.ambra.util.ToolHelper

/**
 * Add a manifest to a SIP. This tries to guess which entry is the main article xml,
 * which other entries are other representations of the article, what are the
 * secondary objects, and what are all the entries uri's. It does this by making
 * the following assumptions about the entry names:
 * <ol>
 *   <li>the main article entry is called &lt;article-entry&gt;.xml
 *   <li>entries of secondary objects have names of the form
 *       &lt;article-entry&gt;.&lt;obj&gt;.&lt;ext&gt;
 *   <li>entries that only differ by a trailing .&lt;ext&gt; are different
 *       representations of the same object
 *   <li>an object's uri is 'info:doi/10.1371/journal.&lt;entry&gt;' (minus the extension)
 * </ol>
 * Or put differently:
 * <ol>
 *   <li>all entries have a common prefix
 *   <li>the shortest name that ends in .xml is the article
 *   <li>all entries with same name modulo extension are representations of the
 *       same object
 * </ol>
 *
 * @author Ronald Tschalär
 */
public class AddManifest {
  /**
   * Add the manifest. This overrides any existing manifest.
   *
   * @param fname   the filename of the SIP
   * @param newName if not-null, the name of the resulting SIP; if null, <var>fname</var>
   *                will be overwritten
   */
  public void addManifest(String fname, String newName) throws IOException {
    SipUtil.updateZip(fname, newName) { zf, zout ->
      def entries =
              zf.entries().iterator()*.name.minus(SipUtil.MANIFEST).minus(SipUtil.MANIFEST_DTD)

      // find article entry and representations
      String ae = entries.findAll{ it.endsWith('.xml') }.
                          min({ o1, o2 -> o1.length() <=> o2.length() })
      if (!ae)
        throw new IOException("No article entry found")
      String base = ae.minus(~/\.[^.]+$/)

      def aeRep = entries.grep(~/${base}\.[^.]+/).minus(ae)

      // get a list of secondary objects
      def objs = entries*.minus(~/\.[^.]+$/).unique().minus(base)

      // write the manifest
      zout.putNextEntry(SipUtil.MANIFEST)

      zout << '<?xml version="1.0" encoding="UTF-8"?>\n'
      zout << "<!DOCTYPE manifest SYSTEM \"${SipUtil.MANIFEST_DTD}\">\n"

      def manifest = new groovy.xml.MarkupBuilder(new OutputStreamWriter(zout, 'UTF-8'))
      manifest.doubleQuotes = true

      manifest.'manifest' {
        articleBundle {
          article(uri:toUri(base), 'main-entry':ae) {
            representation(name:'XML', entry:ae)
            for (rep in aeRep)
              representation(name:SipUtil.getRepName(rep), entry:rep)
          }

          for (obj in objs) {
            object(uri:toUri(obj)) {
              for (rep in entries.grep(~/${obj}\.[^.]+/))
                representation(name:SipUtil.getRepName(rep), entry:rep)
            }
          }
        }
      }

      zout.closeEntry()

      // add the dtd
      zout.putNextEntry(SipUtil.MANIFEST_DTD)
      zout << getClass().getResourceAsStream("manifest.dtd")
      zout.closeEntry()

      // copy everything else
      zout.copyFrom(zf, entries)
    }
  }

  private String toUri(String name) throws IOException {
    name = name.substring(name.lastIndexOf('/') + 1);   // strip directories
    return 'info:doi/10.1371/journal.' + URLEncoder.encode(name, 'UTF-8');
  }

  /**
   * Run this from the command line.
   */
  static void main(String[] args) {
    args = ToolHelper.fixArgs(args)
    if (args.length != 1 || args[0] == '-h') {
      System.err.println "Usage: AddManifest <filename>"
      System.exit 1
    }

    new AddManifest().addManifest(args[0], null)
    println "manifest added"
  }
}
