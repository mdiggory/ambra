/*
 * $HeadURL$
 * $Id$
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

package org.ambraproject.service;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.ambraproject.ApplicationException;
import org.ambraproject.util.FileUtils;
import org.topazproject.xml.transform.cache.CachedSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Convenience class to aggregate common methods used to deal with XML transforms on articles.
 * Used to transform article with annotations, captions of tables/figures, and citation information.
 *
 * @author Stephen Cheng
 * @author Alex Worden
 * @author Joe Osowski
 *
 */
public class XMLServiceImpl implements XMLService {

  private Configuration configuration;

  private static final Logger log = LoggerFactory.getLogger(XMLServiceImpl.class);

  private File xslTemplate;
  private DocumentBuilderFactory factory;
  private String articleRep;
  private Map<String, String> xmlFactoryProperty;

  // designed for Singleton use, set in init(), then Templates are threadsafe for reuse
  private Templates translet;             // initialized from xslTemplate, per bean property

  /**
   * Initialization method called by Spring.
   *
   * @throws org.ambraproject.ApplicationException On Template creation Exceptions.
   */
  public void init() throws ApplicationException {
    // set JAXP properties
    System.getProperties().putAll(xmlFactoryProperty);

    // Create a document builder factory and set the defaults
    factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);

    // set the Templates
    final TransformerFactory tFactory = TransformerFactory.newInstance();

    try {
      translet = tFactory.newTemplates(new StreamSource(xslTemplate));
    } catch (TransformerConfigurationException tce) {
      throw new ApplicationException(tce);
    }
  }

  /**
   * Pass in an XML string fragment, and will return back a string representing the document after
   * going through the XSL transform.
   *
   * @param description
   * @return Transformed document as a String
   * @throws org.ambraproject.ApplicationException
   */
  public String getTransformedDocument(String description) throws ApplicationException {
    try {
      final DocumentBuilder builder = createDocBuilder();
      Document desc =
          builder.parse(new InputSource(new StringReader("<desc>" + description + "</desc>")));
      return getTransformedDocument (desc);
    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error ("Could not transform document", e);
      }
      throw new ApplicationException(e);
    }
  }

  /**
   * Given an XML Document as input, will return an XML string representing the document after
   * transformation.
   *
   * @param doc
   * @return XML String of transformed document
   * @throws org.ambraproject.ApplicationException
   */
  public String getTransformedDocument(Document doc) throws ApplicationException {
    String transformedString;
    try {
      if (log.isDebugEnabled())
        log.debug("Applying XSLT transform to the document...");

      final DOMSource domSource = new DOMSource(doc);
      final Transformer transformer = getTranslet();
      final Writer writer = new StringWriter(1000);

      transformer.transform(domSource, new StreamResult(writer));
      transformedString = writer.toString();
    } catch (Exception e) {
      throw new ApplicationException(e);
    }
    return transformedString;
  }

  public byte[] getTransformedByArray(byte[] xml) throws ApplicationException {
    try {
      Transformer transformer = this.getTranslet();

      Document doc = createDocBuilder().parse(new ByteArrayInputStream(xml));
      DOMSource domSource = new DOMSource(doc);

      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      transformer.transform(domSource, new StreamResult(bs));
      return bs.toByteArray();
    } catch (Exception e) {
      throw new ApplicationException(e);
    }
  }

  public InputStream getTransformedInputStream(InputStream xml) throws ApplicationException {
    try {
      final Writer writer = new StringWriter(1000);
      Transformer transformer = this.getTranslet();

      Document doc = createDocBuilder().parse(xml);
      DOMSource domSource = new DOMSource(doc);
      transformer.transform(domSource, new StreamResult(writer));
      return new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
    } catch (Exception e) {
      throw new ApplicationException(e);
    }
  }

  /**
   * Given a string as input, will return an XML string representing the document after
   * transformation.
   *
   * @param description
   * @return string
   * @throws org.ambraproject.ApplicationException
   */
  public String getTransformedDescription(String description) throws ApplicationException {
    String transformedString;
    try {
      final DocumentBuilder builder = createDocBuilder();
      Document desc = builder.parse(new InputSource(new StringReader("<desc>" + description + "</desc>")));
      final DOMSource domSource = new DOMSource(desc);
      final Transformer transformer = getTranslet();
      final Writer writer = new StringWriter();

      transformer.transform(domSource,new StreamResult(writer));
      transformedString = writer.toString();
    } catch (Exception e) {
      throw new ApplicationException(e);
    }

    // Ambra stylesheet leaves "END_TITLE" as a marker for other processes
    transformedString = transformedString.replace("END_TITLE", "");
    return transformedString;
  }



  /**
   * Convenience method to create a DocumentBuilder with the factory configs
   *
   * @return Document Builder
   * @throws javax.xml.parsers.ParserConfigurationException
   */
  public DocumentBuilder createDocBuilder() throws ParserConfigurationException {
    // Create the builder and parse the file
    final DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(CachedSource.getResolver());
    return builder;
  }

  /**
   * Get a translet, compiled stylesheet, for the xslTemplate.
   *
   * @return Translet for the xslTemplate.
   * @throws javax.xml.transform.TransformerException TransformerException.
   */
  private Transformer getTranslet() throws TransformerException {

    // For each thread, instantiate a new Transformer, and perform the
    // transformations on that thread from a StreamSource to a StreamResult;
    Transformer transformer = translet.newTransformer();
    transformer.setParameter("pubAppContext", configuration.getString("ambra.platform.appContext", ""));
    return transformer;
  }

  /**
   * Setter for XSL Templates.  Takes in a string as the filename and searches for it in resource
   * path and then as a URI.
   *
   * @param xslTemplate The xslTemplate to set.
   * @throws java.net.URISyntaxException
   */
  @Required
  public void setXslTemplate(String xslTemplate)  throws URISyntaxException {
    File file = getAsFile(xslTemplate);
    if (!file.exists()) {
      file = new File(xslTemplate);
    }
    log.debug("XSL template location = " + file.getAbsolutePath());
    this.xslTemplate = file;
  }

  /**
   * Setter method for configuration. Injected through Spring.
   *
   * @param configuration Ambra configuration
   */
  @Required
  public void setAmbraConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Setter for article represenation
   *
   * @param articleRep The articleRep to set.
   */
  @Required
  public void setArticleRep(String articleRep) {
    this.articleRep = articleRep;
  }

  /**
   * @param xmlFactoryProperty The xmlFactoryProperty to set.
   */
  @Required
  public void setXmlFactoryProperty(Map<String, String> xmlFactoryProperty) {
    this.xmlFactoryProperty = xmlFactoryProperty;
  }

  /**
   * @return Returns the articleRep.
   */
  public String getArticleRep() {
    return articleRep;
  }

  /**
   * @param filenameOrURL filenameOrURL
   * @throws java.net.URISyntaxException URISyntaxException
   * @return the local or remote file or url as a java.io.File
   */
  public File getAsFile(final String filenameOrURL) throws URISyntaxException {
    final URL resource = getClass().getResource(filenameOrURL);
    if (null == resource) {
      //access it as a local file resource
      return new File(FileUtils.getFileName(filenameOrURL));
    } else {
      return new File(resource.toURI());
    }
  }
}
