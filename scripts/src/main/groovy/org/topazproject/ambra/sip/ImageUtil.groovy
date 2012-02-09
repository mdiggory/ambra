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

import org.apache.commons.configuration.Configuration
import org.topazproject.ambra.util.ToolHelper

/**
 * Create scaled down versions of all images and add them as additional representations
 * to the SIP.
 *
 * @author stevec
 * @author Ronald Tschalär
 */
public class ImageUtil {
  boolean verbose
  String  imConvert
  String  imIdentify
  File    tmpDir

  public ImageUtil(Configuration config, boolean verbose) {
    use (CommonsConfigCategory) {
      def im = config.ambra.services.documentManagement.imageMagick[0]
      imConvert  = im.executablePath ?: 'convert'
      imIdentify = im.identifyPath   ?: 'identify'
      tmpDir     = new File(im.tempDirectory ?: System.getProperty('java.io.tmpdir'))
    }
    this.verbose = verbose
  }

  /** 
   * Resize the given image.
   * 
   * @param inFile  the image to resize
   * @param outName the filename to store it under; if relative then it's stored in the temp
   *                directory
   * @param type    the image type of the output (e.g. 'png')
   * @param width   the new width, or 0 to keep aspect
   * @param height  the new height, or 0 to keep aspect
   * @param quality the image quality to use
   * @return the file containing the resized image
   * @throws ImageProcessingException if an error occurred during resize
   */
  public File resizeImage(File inFile, String outName, String type, int width, int height,
                          int quality)
      throws ImageProcessingException {
    def newFile = new File(tmpDir, outName)
    if (verbose)
      println "Creating ${newFile}"

    if (quality < 0 || quality > 100)
      quality = 100;

    def resize = (width || height) ? "-resize ${width ?: ''}x${height ?: ''}>" : ''
    antExec(imConvert, "\"${inFile.canonicalPath}\" ${resize} -quality ${quality} " +
                       "\"${type}:${newFile}\"")

    return newFile
  }

  /** 
   * Get the dimensions of the given image.
   * 
   * @param inFile  the image to get the dimensions from
   * @return an array of (width, height)
   * @throws ImageProcessingException if an error occurred trying to get the dimensions
   */
  public int[] getDimensions(File inFile) throws ImageProcessingException {
    def props = antExec(imIdentify, "-quiet -format \"%w %h\" \"${inFile.canonicalPath}\"")
    return props.cmdOut.split().collect{ it.toInteger() } as int[]
  }

  private Properties antExec(exe, args) throws ImageProcessingException {
    def ant = new AntBuilder()
    ant.exec(outputproperty:"cmdOut",
             errorproperty: "cmdErr",
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: exe) {
               arg(line: args)
             }

    if (verbose) {
      println "exe:          ${exe} ${args}"
      println "return code:  ${ant.project.properties.cmdExit}"
      println "stderr:       ${ant.project.properties.cmdErr}"
      println "stdout:       ${ant.project.properties.cmdOut}"
    }

    if (ant.project.properties.cmdExit != '0')
      throw new ImageProcessingException(
                  "Error running '${exe} ${args}', exit-status=${ant.project.properties.cmdExit}")

    return ant.project.properties
  }
}
