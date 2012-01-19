/*
 * $HeadURL: http://ambraproject.org/svn/ambra/branches/ambra-2.2/ambra/plos-commons/file-store/src/main/java/org/plos/filestore/FSIDMapper.java $
 * $Id: FSIDMapper.java 9699 2011-10-24 21:45:08Z wtoconnor $
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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
 * The FSIDMapper is used to map PLoS DOI's to file store identifiers. Currently we only support
 * DOI's specific to PLoS but mehtods can be added here to handle other identifiers that may need
 * mappings ie PMED ID's.
 *
 * @author Bill OConnor
 *
 * WTO: To Do - need to look at handling image article DOI's correctly ... ignoring that for now.
 * WTO: Need to add prefix to interface to handle non PLoS DOI's.
 */
public class FSIDMapper {

  // These regex patterns match 2 version of PLoS DOI's currently in use.
  // WTO: need to match image articles...
  private static Pattern p1 = Pattern.compile("(info:doi)/([0-9\\.]+)/journal\\.([a-z]+)\\.([0-9]+)([\\._a-z0-9]*)");
  private static Pattern p2 = Pattern.compile("(info%3Adoi)%2F([0-9\\.]+)%2Fjournal\\.([a-z]+)\\.([0-9]+)([\\._a-z0-9]*)");
  private static Pattern p3 = Pattern.compile("(info:doi)/([0-9\\.]+)/(image\\.[a-z]+\\.v[0-9]+\\.i[0-9]+)([\\._a-z0-9]*)");
  private static Pattern p4 = Pattern.compile("(info:doi)/([0-9\\.]+)/journal\\.(image\\.[a-z]+\\.v[0-9]+\\.i[0-9]+)([\\._a-z0-9]*)");

  /*
   * Make a file store identifier from a DOI and type. FSID are
   * case insensitive.
   *
   * ex: doi - info:doi/10.1371/journal.pone.0000087
   *     type - "xml"
   *     FSID - /10.1371/pone.0000087/pone.0000087.xml
   *
   * @param prefix - doi prefix
   * @param suffix - doi suffix
   * @param ext - doi extension  for equations and images
   * @param type - file type ie pdf, xml etc.
   * @return - a files store identifier string.
   *
   */
  private static String makeFSID(String prefix, String suffix, String ext, String type) {
    StringBuilder fsid = new StringBuilder();

    fsid.append("/")
      .append(prefix).append("/")
      .append(suffix).append("/")
      .append(suffix);

    if (!ext.equals("")) {
      fsid.append(ext);
    }

    fsid.append(".").append(type);

    return fsid.toString().toLowerCase();
  }

  /*
   * Given a PLoS DOI and file type return an FSID string. Four DOI formats
   * can be used for conversions.
   *
   * ex 1: info:doi/10.1371/journal.pone.0000001
   * ex 2: info%3Adoi%2F10.1371%2Fjournal.pone.0000001
   * ex 3: info%3Adoi%2F10.1371%2Fjournal.pone.0000001.e0002  - equation example
   * ex 4: info%3Adoi%2F10.1371%2Fjournal.image.pone.v03.i08  - image article example
   *
   * @param prefix - DOI
   * @param type - file type ie pdf, xml etc.
   * @return - a files store identifier string.
   */
  public static String doiTofsid(String doi, String type) {
    // DOI's are case insensitive.
    doi = doi.toLowerCase();
    String extension = "";

    Matcher m1 = p1.matcher(doi);

    if (m1.matches()) {
      String suffix = m1.group(3) + "." + m1.group(4);

      if (m1.groupCount() == 5)  {
        extension = m1.group(5);
      }
      return makeFSID(m1.group(2),suffix, extension, type);
    }

    Matcher m3 = p3.matcher(doi);
    if (m3.matches()) {

      if (m3.groupCount() == 4)  {
        extension = m3.group(4);
      }
      return makeFSID(m3.group(2),m3.group(3), extension, type);
    }

    //WTO: ToDo is this match actually possible? Might just remove
    Matcher m2 = p2.matcher(doi);
    if (m2.matches()) {
      String suffix = m2.group(3) + "." + m2.group(4);

      if (m2.groupCount() == 5)  {
        extension = m2.group(5);
      }
      return makeFSID(m2.group(2),suffix, extension, type);
    }

    Matcher m4 = p4.matcher(doi);
    if (m4.matches()) {
      String suffix = m4.group(3);

      if (m4.groupCount() == 5)  {
        extension = m4.group(5);
      }
      return makeFSID(m4.group(2),suffix, extension, type);
    }

    return new String("");
  }

  /*
   * The basic idea here is the FSID is based on the file names
   * contained in the zip file. When we ingest we need to add stuff
   * to the file name in the zip. This is a PLoS specific at the moment.
   *
   * @param doi - DOI
   * @param fileName - file name + type ie pdf, xml etc.
   * @return - a files store identifier string.
   */
  public static String zipToFSID(String doi, String fileName) {
    // DOI's are case insensitive.
    doi = doi.toLowerCase();
    fileName = fileName.toLowerCase(); //store files case-insensitively

    Matcher m1 = p1.matcher(doi);
    if (m1.matches()) {
      return "/" + m1.group(2) + "/" + m1.group(3) + "." + m1.group(4) + "/" + fileName;
    }

    //I'm not sure this use case ever occurs
    Matcher m2 = p2.matcher(doi);
    if (m2.matches()) {
      return "/" + m2.group(2) + "/" + m2.group(3) + "/" + fileName;
    }

    Matcher m3 = p3.matcher(doi);
    if (m3.matches()) {
      return "/" + m3.group(2) + "/" + m3.group(3) + "/" + fileName;
    }

    Matcher m4 = p4.matcher(doi);
    if (m4.matches()) {
      return "/" + m4.group(2) + "/" + m4.group(3) + "/" + fileName;
    }

    return new String("");

  }

}
