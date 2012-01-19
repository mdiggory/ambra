/*
 * $HeadURL$
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

package org.ambraproject.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Bean creator for DocumentBuilderFactory.
 *
 * @author Dragisa Krsmanovic
 */
public class DocumentBuilderFactoryCreator {

  private static final Logger log = LoggerFactory.getLogger(DocumentBuilderFactoryCreator.class);

  private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  private static final String DOCUMENT_BUILDER_FACTORY_CLASS = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

  /**
   * Static factory method.
   *
   * @return DocumentBuilderFactory
   */
  public static DocumentBuilderFactory createFactory() {

    log.info("Creating DocumentBuilderFactory: " + DOCUMENT_BUILDER_FACTORY_CLASS);

    DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory
        .newInstance(DOCUMENT_BUILDER_FACTORY_CLASS, DocumentBuilderFactory.class.getClassLoader());

    documentBuilderfactory.setNamespaceAware(true);
    documentBuilderfactory.setValidating(false);
    try {
      documentBuilderfactory.setFeature(LOAD_EXTERNAL_DTD, false);
    } catch (ParserConfigurationException e) {
      log.error("Error setting " + LOAD_EXTERNAL_DTD + " feature.", e);
    }

    return documentBuilderfactory;
  }
}
