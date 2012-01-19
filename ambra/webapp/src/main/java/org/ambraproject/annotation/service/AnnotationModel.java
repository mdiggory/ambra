
/* $HeadURL::                                                                            $
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

import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.URI;

/**
 * Annotation meta-data.
 *
 * @author Pradeep Krishnan
 */
public class AnnotationModel {
  static final URI a               = URI.create("http://www.w3.org/2000/10/annotation-ns#");
  static final URI r               = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
  static final URI d               = URI.create("http://purl.org/dc/elements/1.1/");
  static final URI dt              = URI.create("http://purl.org/dc/terms/");
  static final URI topaz           = URI.create("http://rdf.topazproject.org/RDF/");
  static final URI nil             = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
  static final URI a_Annotation    = a.resolve("#Annotation");
  static final URI r_type          = r.resolve("#type");
  static final URI a_annotates     = a.resolve("#annotates");
  static final URI a_context       = a.resolve("#context");
  static final URI d_creator       = d.resolve("creator");
  static final URI d_title         = d.resolve("title");
  static final URI a_created       = a.resolve("#created");
  static final URI a_body          = a.resolve("#body");
  static final URI dt_replaces     = dt.resolve("replaces");
  static final URI dt_isReplacedBy = dt.resolve("isReplacedBy");
  static final URI dt_mediator     = dt.resolve("mediator");
  static final URI topaz_state     = topaz.resolve("state");

  /**
   * Append the xmlns attributes used by annotation meta data to an element. Useful for declaring
   * this in a container node so as to reduce verbosity in the child nodes.
   *
   * @param element A container element to which NS attributes have to be appended.
   */
  public static void appendNSAttr(Element element) {
    String xmlns = "http://www.w3.org/2000/xmlns/";

    element.setAttributeNS(xmlns, "xmlns:r", r.toString());
    element.setAttributeNS(xmlns, "xmlns:a", a.toString());
    element.setAttributeNS(xmlns, "xmlns:d", d.toString());
    element.setAttributeNS(xmlns, "xmlns:dt", dt.toString());
    element.setAttributeNS(xmlns, "xmlns:topaz", topaz.toString());
  }

  /**
   * Append annotation meta data to a parent node.
   *
   * @param parent the annotation node
   * @param annotation the annotation to append
   */
  public static void appendToNode(final Node parent, final ArticleAnnotation annotation) {
    String   rNs     = r.toString();
    String   aNs     = a.toString();
    String   dNs     = d.toString();
    String   dtNs    = dt.toString();
    String   topazNs = topaz.toString();

    Document document = parent.getOwnerDocument();
    Element  node;

    node = document.createElementNS(rNs, "r:type");
    node.setAttributeNS(rNs, "r:resource", annotation.getType());
    parent.appendChild(node);

    node = document.createElementNS(aNs, "a:annotates");
    node.setAttributeNS(rNs, "r:resource", "" + annotation.getAnnotates());
    parent.appendChild(node);

    node = document.createElementNS(aNs, "a:context");
    node.appendChild(document.createTextNode(annotation.getContext()));
    parent.appendChild(node);

    node = document.createElementNS(dNs, "d:creator");
    node.setAttributeNS(rNs, "r:resource", annotation.getCreator());
    parent.appendChild(node);

    node = document.createElementNS(aNs, "a:created");
    node.appendChild(document.createTextNode(annotation.getCreatedAsString()));
    parent.appendChild(node);

    if (annotation.getBody() != null) {
      node = document.createElementNS(aNs, "a:body");
      node.setAttributeNS(rNs, "r:resource", annotation.getBody().getId());
      parent.appendChild(node);
    }

    Annotation<?> supersedes = annotation.getSupersedes();
    if (supersedes != null) {
      node = document.createElementNS(dtNs, "dt:replaces");
      node.setAttributeNS(rNs, "r:resource", supersedes.getId().toString());
      parent.appendChild(node);
    }

    Annotation<?> supersededBy = annotation.getSupersededBy();
    if (supersededBy != null) {
      node = document.createElementNS(dtNs, "dt:isReplacedBy");
      node.setAttributeNS(rNs, "r:resource", supersededBy.getId().toString());
      parent.appendChild(node);
    }

    String title = annotation.getTitle();
    if (title != null) {
      node = document.createElementNS(dNs, "d:title");
      node.appendChild(document.createTextNode(title));
      parent.appendChild(node);
    }

    String mediator = annotation.getMediator();
    if (mediator != null) {
      node = document.createElementNS(dtNs, "dt:mediator");
      node.appendChild(document.createTextNode(mediator));
      parent.appendChild(node);
    }

    node = document.createElementNS(topazNs, "topaz:state");
    node.appendChild(document.createTextNode("" + annotation.getState()));
    parent.appendChild(node);
  }
}
