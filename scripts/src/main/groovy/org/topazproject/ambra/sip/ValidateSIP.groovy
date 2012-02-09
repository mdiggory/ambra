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

import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException

import org.apache.commons.compress.archivers.ArchiveEntry
import org.topazproject.ambra.util.ToolHelper

/**
 * Validate the SIP.
 *
 * <p>Checks currently performed:
 * <ul>
 *  <li>a manifest must be present and valid according to the dtd
 *  <li>main article xml must exist, and must also be listed in representations
 *  <li>all href and role links in the article must be absolute, and if a link
 *      uri has the article uri as a prefix then it must point to an entry in
 *      the sip; also all internal links (idref's) must point to existing id's
 *      in the article document.
 *  <li>all entries in the sip must be pointed to by links in the article (modulo
 *      representations)
 * </ul>
 *
 * @author Ronald Tschalär
 */
public class ValidateSIP {
  /**
   * Validate the SIP file.
   *
   * @param fname the filename of the SIP
   * @throws IOException on errors reading the zip
   * @throws ValidationException if any validation errors were encountered
   */
  public void validate(String fname) throws IOException, ValidationException {
    ValidationException ve = new ValidationException()

    ArchiveFile zf = new ArchiveFile(fname)

    ArchiveEntry me = zf.getEntry(SipUtil.MANIFEST)
    if (me == null) {
      ve.addError(
          "No manifest found - expecting one entry called '${SipUtil.MANIFEST}' in zip file")
      throw ve
    }

    XmlSlurper slurper = SipUtil.getManifestParser()
    slurper.setErrorHandler(new ManifestErrorHandler(ve:ve))
    def manif = slurper.parse(zf.getInputStream(me))

    validateManifest(zf, manif, ve)

    def art = SipUtil.getArticle(zf, manif)

    validateLinks(zf, manif, art, ve)
    validateEntries(zf, manif, art, ve)

    if (ve.getErrors().size() > 0)
      throw ve
  }

  /**
   * Check that article is defined and present.
   */
  private void validateManifest(ArchiveFile zf, def manif, ValidationException ve) throws IOException {
    def art = manif.articleBundle.article

    // validate the main-entry attribute
    if (zf.getEntry(art.'@main-entry'.text()) == null)
      ve.addError("no entry found in zip for article main-entry '${art.'@main-entry'}'")

    if (!art.representation.find{ it.@entry == art.'@main-entry' })
      ve.addError("no representation found for article main-entry '${art.'@main-entry'}'")

    // check manifest references entries in the zip, and all entries are referenced
    for (ent in manif.articleBundle.'*'.representation.'@entry'.list()*.text()) {
      if (!zf.getEntry(ent))
        ve.addError("manifest references a non-existent entry in zip: '${ent}'")
    }

    for (ent in zf.entries().iterator()*.name.
                   findAll{ it != SipUtil.MANIFEST && it != SipUtil.MANIFEST_DTD })
      if (!manif.articleBundle.'*'.representation.'@entry'.list()*.text().find{ it == ent }) {
        ve.addError("manifest does not reference entry in zip: '${ent}'")
    }
  }

  /**
   * Check that links are absolute, local links point to something in the manifest, and internal
   * refs reference an existing id.
   */
  private void validateLinks(ArchiveFile zf, def manif, def art, ValidationException ve)
      throws IOException {
    String artUri = manif.articleBundle.article.'@uri';

    // validate href's
    for (link in art.'**'*.'@xlink:href'*.text().findAll{ it }) {
      if (!link.toURI().isAbsolute())
        ve.addError("Link '${link}' is not absolute")

      if (isLocalLink(link, artUri) &&
          !manif.articleBundle.object.'@uri'.find{ it.text() == link })
        ve.addError("Link '${link}' seems to point to a local object but no entry was found " +
                    "for it in the manifest")
    }

    // validate role's
    for (link in art.'**'*.'@xlink:role'*.text().findAll{ it }) {
      if (!link.toURI().isAbsolute())
        ve.addError("Role link '${link}' is not absolute")
    }

    // validate idref's
    def ids = art.'**'*.'@id'*.text().findAll{ it }

    for (refs in art.'**'*.'@rid'*.text().findAll{ it }) {
      for (ref in refs.split('\\s+')) {
        if (!ids.contains(ref))
          ve.addError("rid '${ref}' does not reference an existing id")
      }
    }

    for (refs in art.'**'.findAll{ it -> it.th || it.td }.'@headers'*.text().findAll{ it }) {
      for (ref in refs.split('\\s+')) {
        if (!ids.contains(ref))
          ve.addError("rid '${ref}' does not reference an existing id")
      }
    }

    for (ref in art.'**'*.'@alternate-form-of'*.text().findAll{ it }) {
      if (!ids.contains(ref))
        ve.addError("alternate-form-of '${ref}' does not reference an existing id")
    }

    for (ref in art.'**'*.'@xref'*.text().findAll{ it }) {
      if (!ids.contains(ref))
        ve.addError("xref '${ref}' does not reference an existing id")
    }

    ids = art.'**'*.'glyph-data'*.'@id'*.text().findAll{ it }
    for (ref in art.'**'*.'glyph-ref'.'@glyph-data'*.text().findAll{ it != '' }) {
      if (!ids.contains(ref))
        ve.addError("glyph-ref '${ref}' does not reference an existing glyph-data id")
    }
  }

  /**
   * Check that all entries in the manifest are referenced in the article (no orphans).
   */
  private void validateEntries(ArchiveFile zf, def manif, def art, ValidationException ve)
      throws IOException {
    String artUri = manif.articleBundle.article.'@uri';
    def refs = art.'**'*.'@xlink:href'.findAll{ isLocalLink(it.text(), artUri) }*.text()

    for (entry in manif.articleBundle.object.'@uri'.findAll{ it -> !refs.contains(it.text()) })
      ve.addError("Found unreferenced entry in manifest: uri='${entry}'")
  }

  private boolean isLocalLink(String uri, String artUri) {
    return URLDecoder.decode(uri, 'UTF-8').startsWith(URLDecoder.decode(artUri, 'UTF-8'))
  }

  /**
   * Run this from the command line.
   */
  static void main(String[] args) {
    args = ToolHelper.fixArgs(args)
    if (args.length != 1 || args[0] == '-h') {
      System.err.println "Usage: ValidateSIP <filename>"
      System.exit 1
    }

    new ValidateSIP().validate(args[0])
    println "No problems found"
  }
}

class ValidationException extends Exception {
  private List<String> errors = new ArrayList<String>()

  public void addError(String msg) {
    errors.add(msg)
  }

  public List<String> getErrors() {
    return errors
  }

  public String getMessage() {
    return errors.join(System.properties['line.separator'])
  }
}

private class ManifestErrorHandler implements ErrorHandler {
  ValidationException ve

  public void warning(SAXParseException exception) {
    ve.addError(exception.toString())
  }

  public void error(SAXParseException exception) {
    ve.addError(exception.toString())
  }

  public void fatalError(SAXParseException exception) {
    ve.addError(exception.toString())
  }
}
