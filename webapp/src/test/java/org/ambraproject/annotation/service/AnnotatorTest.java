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

package org.ambraproject.annotation.service;

import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.UserProfile;
import org.ambraproject.views.AnnotationView;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertTrue;
import org.w3c.dom.Document;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;
import java.util.TimeZone;

/**
 * @author Dragisa Krsmanovic
 */
public class AnnotatorTest {

  @BeforeTest
  public void setUp() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory.newInstance("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", getClass().getClassLoader());
    documentBuilderfactory.setNamespaceAware(true);
    documentBuilderfactory.setValidating(false);
    documentBuilderfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    XMLUnit.setControlDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setTestDocumentBuilderFactory(documentBuilderfactory);
    XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
    XMLUnit.setTransformerFactory("net.sf.saxon.TransformerFactoryImpl");
    XMLUnit.setXSLTVersion("2.0");
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreWhitespace(true);
  }
  
  @Test
  public void testAnnotateAsDocument()
      throws ParseException, ParserConfigurationException, URISyntaxException,
      TransformerException, IOException, SAXException {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT-8"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

    Long articleId = 1L;

    AnnotationView[] annotations = new AnnotationView[2];

    Annotation fc = new Annotation();
    fc.setID(1L);
    fc.setType(AnnotationType.FORMAL_CORRECTION);
    fc.setAnnotationUri("formalCorrection:1234");
    fc.setArticleID(articleId);
    fc.setBody("Annotation One");
    // #xpointer(string-range(/article[1]/body[1]/sec[1]/p[1], '', 17, 3)[1])
    fc.setXpath(articleId +
      "#xpointer(string-range(%2farticle%5b1%5d%2fbody%5b1%5d%2fsec%5b1%5d%2fp%5b1%5d%2c+''%2c+17%2c+3)%5b1%5d)");
    fc.setCreated(dateFormat.parse("03/22/09"));
    fc.setCreator(new UserProfile("authID", "e@mail.net", "user:1"));
    fc.setTitle("Formal Correction Title");
    annotations[0] = new AnnotationView(fc, "test-title", "test-doi", null);

    Annotation r = new Annotation();
    r.setID(2L);
    r.setType(AnnotationType.RETRACTION);
    r.setAnnotationUri("retraction:3245");
    r.setArticleID(articleId);
    r.setBody("Annotation Two");
    // #xpointer(string-range(/article[1]/front[1]/article-meta[1]/abstract[1]/p[1], '', 10, 4)[1])
    r.setXpath(articleId +
        "#xpointer(string-range(%2Farticle%5B1%5D%2Ffront%5B1%5D%2Farticle-meta%5B1%5D%2Fabstract%5B1%5D%2Fp%5B1%5D%2C+''%2C+10%2C+4)%5B1%5D)");
    r.setCreated(dateFormat.parse("12/01/08"));
    r.setCreator(new UserProfile("authID2", "e@mail2.net", "user:2"));
    r.setTitle("Retraction Title");
    annotations[1] = new AnnotationView(r, "test-title", "test-doi", null);

    Document doc = XMLUnit.buildTestDocument(new InputSource(getClass().getResourceAsStream("/annotation/document.xml")));
    Document expected = XMLUnit.buildControlDocument(new InputSource(getClass().getResourceAsStream("/annotation/result.xml")));
    Document result = Annotator.annotateAsDocument(doc, annotations);

    System.out.println(XMLUnit.getTransformerFactory().getClass().getName());
    System.out.println(XMLUnit.getControlDocumentBuilderFactory().getClass().getName());
    System.out.println(XMLUnit.getTestDocumentBuilderFactory().getClass().getName());
    System.out.println(XMLUnit.getSAXParserFactory().getClass().getName());
    
    Diff diff = new Diff(expected, result);
    assertTrue(diff.identical(), diff.toString());

  }


}
