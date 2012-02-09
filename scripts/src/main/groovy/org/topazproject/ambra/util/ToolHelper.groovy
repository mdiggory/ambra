/* $HeadURL::                                                                                     $
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
package org.topazproject.ambra.util

import org.apache.commons.lang.text.StrMatcher
import org.apache.commons.lang.text.StrTokenizer

import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.XMLConfiguration

import org.topazproject.ambra.configuration.ConfigurationStore

/**
 * Fix mave java:exec command line parsing. Should not impact args run outside
 * maven unless they contain spaces.
 *
 * @param args The command line arguments
 * @return A fixed version of the args
 */
static String[] fixArgs(String[] args) {
  if (args.size() > 0 && args[0] == null) args = [ ]
  if (args != null && args.length == 1)
    args = new StrTokenizer(args[0], StrMatcher.trimMatcher(), StrMatcher.quoteMatcher()).tokenArray
  return args
}

/**
 * Must initialize configuration before anybody tries to use it (at class load time)
 *
 * @param xmlConfigFileOverride The filename of an xml file that provides configuration overrides.
 * @return A commons-config Configuration instance
 */
static Configuration loadConfiguration(xmlConfigFileOverride) {
  ConfigurationStore.instance.loadDefaultConfiguration()
  def conf = new CompositeConfiguration()
  if (xmlConfigFileOverride)
    conf.addConfiguration(new XMLConfiguration(xmlConfigFileOverride))
  conf.addConfiguration(ConfigurationStore.instance.configuration)
  return conf
}

/**
 * Extract integers from a string
 *
 * @param value the string to extract integers from
 * @param minLen minimum length of integer string
 * @param maxLen maximum lenght of integer string
 */
static String[] findInt(value, minLen, maxLen) {
  def matches = []
  (value?.toString() =~ "[0-9]{$minLen,$maxLen}").each { match ->
    matches.add(match)
  }

  matches.size() == 0 ? null : matches
}
