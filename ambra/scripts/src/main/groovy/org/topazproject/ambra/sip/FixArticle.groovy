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

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import groovy.xml.FactorySupport

import net.sf.saxon.TransformerFactoryImpl

import org.apache.commons.compress.archivers.ArchiveEntry
import org.topazproject.ambra.configuration.ConfigurationStore
import org.topazproject.ambra.util.ToolHelper
import org.topazproject.xml.transform.cache.CachedSource
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

/**
 * Fix up the article.xml . This currently involves fixing up the links.
 *
 * @author Ronald Tschalär
 */
public class FixArticle {
  static final FIX_LINKS = "fix_article_links.xslt"

  /**
   * Fix the article links.
   *
   * @param fname   the filename of the SIP
   * @param newName if not-null, the name of the resulting SIP; if null, <var>fname</var>
   *                will be overwritten
   */
  public void fixLinks(String fname, String newName) throws IOException {
    SipUtil.updateZip(fname, newName) { zf, zout ->
      // get manifest
      ArchiveEntry me = zf.getEntry(SipUtil.MANIFEST)
      if (me == null)
        throw new IOException(
            "No manifest found - expecting one entry called '${SipUtil.MANIFEST}' in zip file")

      def manif = SipUtil.getManifestParser().parse(zf.getInputStream(me))

      // get article
      String aeName = manif.articleBundle.article.'@main-entry'.text()
      ArchiveEntry ae = zf.getEntry(aeName)
      if (ae == null)
        throw new IOException("article entry '${aeName}' not found in zip")

      // fix up article
      StreamSource xslt =
        new StreamSource(inputStream:getClass().getResourceAsStream(FIX_LINKS), systemId:FIX_LINKS)
      Transformer t = getTrnsfFact().newTransformer(xslt)
      def conf = ConfigurationStore.getInstance().getConfiguration()
      def doiPrefix = conf.getString("ambra.aliases.doiPrefix")
      t.setParameter("doiPrefix", doiPrefix)

      XMLReader r = FactorySupport.createSaxParserFactory().newSAXParser().getXMLReader()
      r.setEntityResolver(new ManifestDTDResolver())
      t.setParameter('manifest', new SAXSource(r, getInpSrc(zf, me)))

      ByteArrayOutputStream baos = new ByteArrayOutputStream()
      t.transform(new CachedSource(getInpSrc(zf, ae)), new StreamResult(baos))

      zout.writeEntry(aeName, baos.toByteArray())

      // copy everything else
      zout.copyFrom(zf, zf.entries().iterator()*.name.minus(aeName))
    }
  }

  private TransformerFactory getTrnsfFact() {
    TransformerFactory tFactory = new TransformerFactoryImpl()
    tFactory.setAttribute("http://saxon.sf.net/feature/strip-whitespace", "none")
    tFactory.setURIResolver({ href, base ->
      println "href='${href}', base='${base}'"
      return null
    } as URIResolver)
    return tFactory
  }

  private InputSource getInpSrc(ArchiveFile zf, ArchiveEntry ze) {
    return new InputSource(byteStream:zf.getInputStream(ze), systemId:ze.name)
  }

  /**
   * Run this from the command line.
   */
  static void main(String[] args) {
    args = ToolHelper.fixArgs(args)
    if (args.length != 1 || args[0] == '-h') {
      System.err.println "Usage: FixArticle <filename>"
      System.exit 1
    }

    new FixArticle().fixLinks(args[0], null)
    println "article links fixed"
  }
}
