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

package org.ambraproject.article.service;

import org.ambraproject.models.ArticleAsset;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ambraproject.filestore.FSIDMapper;
import org.ambraproject.filestore.FileStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.ApplicationException;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.Annotator;
import org.ambraproject.article.AuthorExtra;
import org.ambraproject.article.CitationReference;
import org.ambraproject.cache.Cache;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.ambraproject.service.HibernateServiceImpl;
import org.ambraproject.service.XMLService;
import org.ambraproject.util.TextUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.activation.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetch article service.
 */
public class FetchArticleServiceImpl extends HibernateServiceImpl implements FetchArticleService {
  private static final String ARTICLE_LOCK = "ArticleHtmlCache-Lock-";

  private XMLService articleTransformService;

  private static final Logger log = LoggerFactory.getLogger(FetchArticleServiceImpl.class);
  private AnnotationService annotationService;
  private ArticleService articleService;
  private FileStoreService fileStoreService;
  private Cache articleHtmlCache;

  private String getTransformedArticle(final String articleURI, final String authId)
      throws ApplicationException, NoSuchArticleIdException {
    try {
      return articleTransformService.getTransformedDocument(getAnnotatedContentAsDocument(articleURI, authId));
    } catch (ApplicationException ae) {
      throw ae;
    } catch (NoSuchArticleIdException nsae) {
      throw nsae;
    } catch (Exception e) {
      throw new ApplicationException (e);
    }
  }

  /**
   * Get the URI transformed as HTML.
   * @param articleURI articleURI
   * @param authId the authorization ID of the current user
   * @return String representing the annotated article as HTML
   * @throws org.ambraproject.ApplicationException ApplicationException
   */
  public String getURIAsHTML(final String articleURI, final String authId) throws Exception {

    //TODO: Remove this; it adds unnecessary db queries since this is coming from FetchArticleAction which also calls getArticle()
    // quick way to check for permission and the state of the article
    // we don't want to return a cached result when we should not
    articleService.getArticle(articleURI, authId);
    
    final Object lock = (ARTICLE_LOCK + articleURI).intern();  // lock @ Article level

    String content = articleHtmlCache.get(articleURI,
      new Cache.SynchronizedLookup<String, Exception>(lock) {
        public String lookup() throws Exception {
          return getTransformedArticle(articleURI, authId);
        }
      });
    
    return content;
  }

  /**
   * Return the annotated content as a String
   * @param articleURI articleURI
   * @param authId the authorization ID of the current user
   *
   * @return an the annotated content as a String
   * @throws javax.xml.parsers.ParserConfigurationException ParserConfigurationException
   * @throws org.xml.sax.SAXException SAXException
   * @throws java.io.IOException IOException
   * @throws java.net.URISyntaxException URISyntaxException
   * @throws org.ambraproject.ApplicationException ApplicationException
   * @throws NoSuchArticleIdException NoSuchArticleIdException
   * @throws javax.xml.transform.TransformerException TransformerException
   */
  public String getAnnotatedContent(final String articleURI, final String authId)
      throws ParserConfigurationException, SAXException, IOException, URISyntaxException,
             ApplicationException, NoSuchArticleIdException,TransformerException{
    return TextUtils.getAsXMLString(getAnnotatedContentAsDocument(articleURI, authId));
  }

  /**
   *
   * @param articleDOI - the DOI of the (Article) content
   * @param authId the authorization ID of the current user
   *
   * @return Article DOM document
   * @throws java.io.IOException
   * @throws NoSuchArticleIdException
   * @throws javax.xml.parsers.ParserConfigurationException
   * @throws org.xml.sax.SAXException
   * @throws org.ambraproject.ApplicationException
   */
  private Document getAnnotatedContentAsDocument(final String articleDOI, final String authId)
      throws IOException, NoSuchArticleIdException, ParserConfigurationException, SAXException,
             ApplicationException {
    DataSource content;

    try {
      content = getArticleXML(articleDOI, authId);
    } catch (NoSuchArticleIdException ex) {
      throw new NoSuchArticleIdException(articleDOI,
                                         "(representation=" + articleTransformService.getArticleRep() + ")",
                                         ex);
    }

    final ArticleAnnotation[] annotations = annotationService.listAnnotations(articleDOI, null);
    return applyAnnotationsOnContentAsDocument(content, annotations);
  }

  private DataSource getArticleXML(final String articleDoi, final String authId)
    throws NoSuchArticleIdException {
    String fsid = FSIDMapper.doiTofsid(articleDoi, "XML");

    if (fsid == null)
      throw new NoSuchArticleIdException(articleDoi);

    List assets = hibernateTemplate.findByCriteria(DetachedCriteria.forClass(ArticleAsset.class)
          .add(Restrictions.eq("doi", articleDoi))
          .add(Restrictions.eq("extension", "XML")));

    if(assets.size() == 0)
      throw new NoSuchArticleIdException(articleDoi);

    return new ByteArrayDataSource(fileStoreService, fsid, (ArticleAsset)assets.get(0));
  }

  private Document applyAnnotationsOnContentAsDocument(DataSource content,
                                                       ArticleAnnotation[] annotations)
          throws ApplicationException
  {
    Document doc = null;

    if (log.isDebugEnabled())
      log.debug("Parsing article xml ...");

    try {
      doc = articleTransformService.createDocBuilder().parse(content.getInputStream());
    } catch (Exception e){
      throw new ApplicationException(e.getMessage(), e);
    }

    try {
      if (annotations.length == 0)
        return doc;

      if (log.isDebugEnabled())
        log.debug("Applying " + annotations.length + " annotations to article ...");

      return Annotator.annotateAsDocument(doc, annotations);
    } catch (Exception e){
      if (log.isErrorEnabled()) {
        log.error("Could not apply annotations to article: " + content.getName(), e);
      }
      throw new ApplicationException("Applying annotations failed for resource:" +
                                     content.getName(), e);
    }
  }

  /**
   * Setter for annotationService
   *
   * @param annotationService annotationService
   */
  @Required
  public void setAnnotationService(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }


  /**
   * @param articleService The articleService to set.
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @param articleTransformService The articleXmlUtils to set.
   */
  @Required
  public void setArticleTransformService(XMLService articleTransformService) {
    this.articleTransformService = articleTransformService;
  }

  /**
   * Get the article xml
   * @param articleURI article uri
   * @param authId the authorization ID of the current user
   *
   * @return article xml
   */
  public Document getArticleDocument(String articleURI, final String authId) {
    Document doc = null;
    DataSource content = null;

    try {
      content = getArticleXML(articleURI, authId);
    } catch (Exception e) {
      log.warn("Article " + articleURI + " not found.");
      return null;
    }

    try {
      doc = articleTransformService.createDocBuilder().parse(content.getInputStream());
    } catch (Exception e) {
      log.error("Error parsing the article xml for article " + articleURI, e);
      return null;
    }

    return doc;
  }

  /**
   * Get the author affiliations for a given article
   * @param doc article xml
   * @param doc article xml
   * @return author affiliations
   */
  public ArrayList<AuthorExtra> getAuthorAffiliations(Document doc) {

    ArrayList<AuthorExtra> list = new ArrayList<AuthorExtra>();

    if (doc == null) {
      return list;
    }

    try {
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();

      XPathExpression expr = xpath.compile("//contrib-group/contrib[@contrib-type='author']");
      Object result = expr.evaluate(doc, XPathConstants.NODESET);

      NodeList contribList = (NodeList) result;

      XPathExpression surNameExpr = xpath.compile("//name/surname");
      XPathExpression givenNameExpr = xpath.compile("//name/given-names");
      XPathExpression affExpr = xpath.compile("//xref[@ref-type='aff']");

      for (int i = 0; i < contribList.getLength(); i++) {
        String surName = null;
        String givenName = null;
        String affId = null;
        String affiliation = null;

        Node contribNode = contribList.item(i);

        // get surname
        DocumentFragment df = doc.createDocumentFragment();
        df.appendChild(contribNode);

        Object resultObj = surNameExpr.evaluate(df, XPathConstants.NODE);
        Node resultNode = (Node) resultObj;
        if (resultNode != null) {
          surName = resultNode.getTextContent();
        }

        // get given name
        resultObj = givenNameExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          givenName = resultNode.getTextContent();
        }

        // get affiliation id
        resultObj = affExpr.evaluate(contribNode, XPathConstants.NODESET);
        NodeList resultNodeList = (NodeList) resultObj;
        ArrayList<String> affiliations = new ArrayList<String>();
        if (resultNodeList != null) {
          for (int j = 0; j < resultNodeList.getLength(); j++) {
            Node xrefNode = resultNodeList.item(j);
            NamedNodeMap nnm = xrefNode.getAttributes();
            Node rid = nnm.getNamedItem("rid");
            affId = rid.getTextContent();

            XPathExpression affExpr2 = xpath.compile("//aff[@id='" + affId + "']/addr-line");
            Object affObj = affExpr2.evaluate(doc, XPathConstants.NODE);
            Node addrLineNode = (Node) affObj;
            if (addrLineNode != null) {
              affiliation = addrLineNode.getTextContent();
              affiliations.add(affiliation);
            }
          }
        }

        if (surName != null && givenName != null) {
          AuthorExtra as = new AuthorExtra();
          as.setAuthorName(givenName + " " + surName);
          as.setAffiliations(affiliations);
          list.add(as);
        }
      }

    } catch (Exception e) {
      log.error("Error occurred while gathering the author affiliations.", e);
    }

    return list;

  }

  /**
   * Get references for a given article
   * @param doc article xml
   * @return references
   */
  public ArrayList<CitationReference> getReferences(Document doc) {
    ArrayList<CitationReference> list = new ArrayList<CitationReference>();

    if (doc == null) {
      return list;
    }

    try {
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr = xpath.compile("//back/ref-list[title='References']/ref");
      Object result = expr.evaluate(doc, XPathConstants.NODESET);

      NodeList refList = (NodeList) result;

      if (refList.getLength() == 0) {
        expr = xpath.compile("//back/ref-list/ref");
        result = expr.evaluate(doc, XPathConstants.NODESET);
        refList = (NodeList) result;
      }

      XPathExpression typeExpr = xpath.compile("//citation | //nlm-citation");
      XPathExpression titleExpr = xpath.compile("//article-title");
      XPathExpression authorsExpr = xpath.compile("//person-group[@person-group-type='author']/name");
      XPathExpression journalExpr = xpath.compile("//source");
      XPathExpression volumeExpr = xpath.compile("//volume");
      XPathExpression numberExpr = xpath.compile("//label");
      XPathExpression fPageExpr = xpath.compile("//fpage");
      XPathExpression lPageExpr = xpath.compile("//lpage");
      XPathExpression yearExpr = xpath.compile("//year");
      XPathExpression publisherExpr = xpath.compile("//publisher-name");

      for (int i = 0; i < refList.getLength(); i++) {

        Node refNode = refList.item(i);
        CitationReference citation = new CitationReference();

        DocumentFragment df = doc.createDocumentFragment();
        df.appendChild(refNode);

        // citation type
        Object resultObj = typeExpr.evaluate(df, XPathConstants.NODE);
        Node resultNode = (Node) resultObj;
        if (resultNode != null) {
          NamedNodeMap nnm = resultNode.getAttributes();
          Node nnmNode = nnm.getNamedItem("citation-type");
          // some old articles do not have this attribute
          if (nnmNode != null) {
            citation.setCitationType(nnmNode.getTextContent());
          }
        }

        // title
        resultObj = titleExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setTitle(resultNode.getTextContent());
        }

        // authors
        resultObj = authorsExpr.evaluate(df, XPathConstants.NODESET);
        NodeList resultNodeList = (NodeList) resultObj;
        ArrayList<String> authors = new ArrayList<String>();
        for (int j = 0; j < resultNodeList.getLength(); j++) {
          Node nameNode = resultNodeList.item(j);
          NodeList namePartList = nameNode.getChildNodes();
          String surName = "";
          String givenName = "";
          for (int k = 0; k < namePartList.getLength(); k++) {
            Node namePartNode = namePartList.item(k);
            if (namePartNode.getNodeName().equals("surname")) {
              surName = namePartNode.getTextContent();
            } else if (namePartNode.getNodeName().equals("given-names")) {
              givenName = namePartNode.getTextContent();
            }
          }
          authors.add(givenName + " " + surName);
        }

        citation.setAuthors(authors);

        // journal title
        resultObj = journalExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setJournalTitle(resultNode.getTextContent());
        }

        // volume
        resultObj = volumeExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setVolume(resultNode.getTextContent());
        }

        // citation number
        resultObj = numberExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setNumber(resultNode.getTextContent());
        }

        // citation pages
        String firstPage = null;
        String lastPage = null;
        resultObj = fPageExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          firstPage = resultNode.getTextContent();
        }

        resultObj = lPageExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          lastPage = resultNode.getTextContent();
        }

        if (firstPage != null) {
          if (lastPage != null) {
            citation.setPages(firstPage + "-" + lastPage);
          } else {
            citation.setPages(firstPage);
          }
        }

        // citation year
        resultObj = yearExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setYear(resultNode.getTextContent());
        }

        // citation publisher
        resultObj = publisherExpr.evaluate(df, XPathConstants.NODE);
        resultNode = (Node) resultObj;
        if (resultNode != null) {
          citation.setPublisher(resultNode.getTextContent());
        }

        list.add(citation);
      }

    } catch (Exception e) {
      log.error("Error occurred while gathering the citation references.", e);
    }

    return list;

  }

  /**
   * Returns abbreviated journal name
   * @param doc article xml
   * @return abbreviated journal name
   */
  public String getJournalAbbreviation(Document doc) {
    String journalAbbrev = "";

    if (doc == null) {
      return journalAbbrev;
    }

    try {
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();

      XPathExpression expr = xpath.compile("//journal-meta/journal-id[@journal-id-type='nlm-ta']");
      Object resultObj = expr.evaluate(doc, XPathConstants.NODE);
      Node resultNode = (Node) resultObj;
      if (resultNode != null) {
        journalAbbrev = resultNode.getTextContent();
      }
    } catch (Exception e) {
      log.error("Error occurred while getting abbreviated journal name.", e);
    }

    return journalAbbrev;
  }

  /**
   * @param articleHtmlCache The Article(transformed) cache to use
   */
  @Required
  public void setArticleHtmlCache(Cache articleHtmlCache) {
    this.articleHtmlCache = articleHtmlCache;
  }

  /**
   * @param fileStoreService The fileStoreService to use
   */
  @Required
  public void setFileStoreService(FileStoreService fileStoreService) {
    this.fileStoreService = fileStoreService;
  }

  private static class ByteArrayDataSource implements DataSource {
    private final FileStoreService fileStoreService;
    private final String fsid;
    private final ArticleAsset asset;

    public ByteArrayDataSource(FileStoreService fileStoreService, String fsid, ArticleAsset asset) {
      this.fileStoreService = fileStoreService;
      this.fsid = fsid;
      this.asset = asset;
    }

    public String getName() {
      return asset.getDoi() + "#" + asset.getExtension();
    }

    public String getContentType() {
      String ct = asset.getContentType();
      return (ct != null) ? ct : "application/octet-stream";
    }

    public InputStream getInputStream() throws IOException {
      InputStream fs = null;

      try {
        fs = fileStoreService.getFileInStream(fsid);
      } catch (Exception e) {
        throw new IOException(e.getMessage(), e);
      }
      return fs;
    }

    public OutputStream getOutputStream() throws IOException {
      throw new IOException("writing not supported");
    }
  }
}
