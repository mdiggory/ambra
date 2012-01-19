/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
import org.apache.commons.configuration.Configuration

/**
 * Create scaled down versions of all images and add them as additional representations
 * to the SIP.
 *
 * @author stevec
 * @author Ronald Tschalär
 */
public class ProcessImages {
  /** Map of article image contexts and their associated representations. */
  private static final Map<String, String[]> repsByCtxt = new HashMap<String, String[]>()

  static {
    String[] smallMediumLarge = [ "PNG_S", "PNG_M", "PNG_L" ]
    String[] singleLarge      = [ "PNG" ]

    repsByCtxt.put('fig',                 smallMediumLarge)
    repsByCtxt.put('table-wrap',          smallMediumLarge)
    repsByCtxt.put('disp-formula',        singleLarge)
    repsByCtxt.put('chem-struct-wrapper', singleLarge)
    repsByCtxt.put('inline-formula',      singleLarge)
  }

  Configuration config
  ImageUtil     imgUtil
  boolean       verbose

  /** 
   * Create a new image processor. 
   * 
   * @param config  the configuration to use
   * @param verbose if true, print out some info while processing
   */
  public ProcessImages(Configuration config, boolean verbose) {
    this.config  = config
    this.imgUtil = new ImageUtil(config, verbose)
    this.verbose = verbose
  }

  /**
   * Create the scaled images and add them to the sip.
   *
   * @param articleFile the sip
   * @param newName     the new sip file's name, or null to overwrite
   */
  public void processImages(String articleFile, String newName) {
    if (verbose) {
      println('Processing file: ' + articleFile)
    }

    SipUtil.updateZip(articleFile, newName) { articleZip, newZip ->
      // get manifest
      ArchiveEntry me = articleZip.getEntry(SipUtil.MANIFEST)
      if (me == null)
        throw new IOException(
            "No manifest found - expecting one entry called '${SipUtil.MANIFEST}' in zip file")

      def manif = SipUtil.getManifestParser().parse(articleZip.getInputStream(me))

      // get the proper image-set
      def art = SipUtil.getArticle(articleZip, manif)
      Configuration imgSet = getImageSet(art)

      // copy and scale
      Map<String, List<File>> imgNames = [:]

      for (entry in articleZip.entries()) {
        if (entry.name == SipUtil.MANIFEST)
          continue              // a new one is written below

        newZip.copyFrom(articleZip, [entry.name])

        if (entry.name.toLowerCase().endsWith('.tif')) {
          File f = File.createTempFile('tmp_', entry.name)
          if (verbose)
            println 'Created temp file: ' + f.getCanonicalPath()
          f.withOutputStream{ it << articleZip.getInputStream(entry) }

          imgNames[entry.name] = []
          processImage(entry.name, imgNames[entry.name], f, imgSet,
                       repsByCtxt[getContext(entry.name, art, manif)])
          f.delete()
        }
      }

      def allNewImgs = imgNames.values().toList().flatten()
      if (verbose)
        println 'Number of resized images: ' + allNewImgs.size()

      // write out the new images
      for (newImg in allNewImgs) {
        if (verbose)
          println 'Adding to zip file: ' + newImg.name

        newZip.writeEntry(newImg.name, newImg.length(), newImg.newInputStream())
        newImg.delete()
      }

      // write out the new manifest
      newZip.putNextEntry(SipUtil.MANIFEST)

      newZip << '<?xml version="1.0" encoding="UTF-8"?>\n'
      newZip << '<!DOCTYPE manifest SYSTEM "manifest.dtd">\n'

      def newManif = new groovy.xml.MarkupBuilder(new OutputStreamWriter(newZip, 'UTF-8'))
      newManif.doubleQuotes = true

      newManif.'manifest' {
        manif.articleBundle.each{ ab ->
          articleBundle {
            article(uri:ab.article.@uri, 'main-entry':ab.article.'@main-entry') {
              for (r in ab.article.representation) {
                representation(name:r.@name, entry:r.@entry)
                for (img in imgNames[r.@entry.text()])
                  representation(name:SipUtil.getRepName(img.name), entry:img.name)
              }
            }

            for (obj in ab.object) {
              object(uri:obj.@uri) {
                for (r in obj.representation) {
                  representation(name:r.@name, entry:r.@entry)
                  for (img in imgNames[r.@entry.text()])
                    representation(name:SipUtil.getRepName(img.name), entry:img.name)
                }
              }
            }
          }
        }
      }

      newZip.closeEntry()
    }
  }

  private void processImage(String name, List<File> imgNames, File file, Configuration imgSet,
                            String[] reps)
      throws ImageProcessingException {
    if (!reps)
      return

    def baseName = name.substring(0, name.lastIndexOf('.') + 1)

    use (CommonsConfigCategory) {
      if (reps.any{ it == 'PNG_S' })
        imgNames.add(imgUtil.resizeImage(file, baseName + 'PNG_S', 'png',
                                         imgSet.small.'@width'[0]?.toInteger() ?: 70, 0,
                                         imgSet.small.'@quality'[0]?.toInteger() ?: 70))

      if (reps.any{ it == 'PNG_M' })
        imgNames.add(imgUtil.resizeImage(file, baseName + 'PNG_M', 'png',
                                         imgSet.medium.'@maxDimension'[0]?.toInteger() ?: 600,
                                         imgSet.medium.'@maxDimension'[0]?.toInteger() ?: 600,
                                         imgSet.medium.'@quality'[0]?.toInteger() ?: 80))

      String lrg = reps.find{ it == 'PNG_L' || it == 'PNG' }
      if (lrg)
        imgNames.add(imgUtil.resizeImage(file, baseName + lrg, 'png', 0, 0,
                                         imgSet.large.'@quality'[0]?.toInteger() ?: 90))
    }
  }

  /**
   * Find the configured image-set for the article.
   */
  private Configuration getImageSet(def art) throws IOException {
    def artType = art.front.'article-meta'.'article-categories'.'subj-group'.
                      find{ it.'@subj-group-type' = 'heading' }.subject.text()

    use (CommonsConfigCategory) {
      def name = config.ambra.articleTypeList.articleType.find{ it.typeHeading == artType }?.
                        imageSetConfigName
      name = name ?: '#default'

      def is = config.ambra.services.documentManagement.imageMagick.imageSetConfigs.imageSet.
                      find{ it.'@name' == name }

      if (verbose) {
        println "article-type: ${artType}"
        println "img-set-name: ${name}"
        println "img-set:      ${is ? 'found' : 'not-found, using hardcoded defaults'}"
      }

      return is
    }
  }

  /**
   * Get the context element in the article for the link that points to the given entry.
   */
  private String getContext(String entryName, def art, def manif) {
    String uri = manif.articleBundle.object.
                       find{ it.representation.'@entry'.text().contains(entryName) }.'@uri'.text()

    def linkInArticle = art.'**'*.'@xlink:href'.find { it.text() == uri }

    if (!linkInArticle)
       throw new IOException("xlink:href=\"${uri}\" not found in the article")

    def ref = linkInArticle.'..'
    return ref.name() == 'supplementary-material' ? ref.name() : ref.'..'.name()
  }
}
