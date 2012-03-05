/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Library of Science
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

package org.ambraproject.annotation.service;

import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.ArticleEditor;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.ArticleContributor;
import org.topazproject.ambra.models.Citation;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Retraction;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: remove this class and move all functionality to the annotation service bean
 * @author Alex Kudlick Date: 4/18/11
 *         <p/>
 *         org.ambraproject.annotation.service
 */
class HibernateAnnotationUtil {
  /**
   * Create default citation on formal correction based on article's citation.
   * @param correction Formal correction
   * @param template hibernate template
   * @throws Exception if migration fails
   */
  static void createDefaultCitation(FormalCorrection correction, Citation articleCitation, HibernateTemplate template) throws Exception {
    Citation citation = createFormalCorrectionCitation(correction.getId().toString(), articleCitation, template);
    template.saveOrUpdate(citation);
    correction.setBibliographicCitation(citation);
  }

  /**
   * Create default citation on retraction based on article's citation.
   * @param retraction Retraction
   * @param template hibernate template
   * @throws Exception if migration fails
   */
  static void createDefaultCitation(Retraction retraction, Citation articleCitation, HibernateTemplate template) throws Exception {
    Citation citation = createRetractionCitation(retraction.getId().toString(), articleCitation, template);
    template.saveOrUpdate(citation);
    retraction.setBibliographicCitation(citation);
  }

  private static Citation createFormalCorrectionCitation(String annotationId, Citation articleCitation,
        HibernateTemplate template)
      throws Exception {

    Citation citation = new Citation();
    citation.setTitle("Correction: " + articleCitation.getTitle());
    copyCommonProperties(annotationId, articleCitation, citation, template);
    return citation;
  }

  private static Citation createRetractionCitation(String annotationId, Citation articleCitation,
        HibernateTemplate template)
      throws Exception {

    Citation citation = new Citation();
    citation.setTitle("Retraction: " + articleCitation.getTitle());
    copyCommonProperties(annotationId, articleCitation, citation, template);
    return citation;
  }

  private static void copyCommonProperties(String annotationId, Citation articleCitation, Citation citation,
      HibernateTemplate template)
      throws Exception {
    citation.setJournal(articleCitation.getJournal());
    citation.setYear(articleCitation.getYear());
    citation.setDisplayYear("(" + articleCitation.getDisplayYear() + ")");
    citation.setMonth(articleCitation.getMonth());
    citation.setDay(articleCitation.getDay());
    citation.setCitationType(articleCitation.getCitationType());
    citation.setDoi(annotationId.replaceFirst("info:doi/",""));
    citation.setELocationId(null);
    citation.setVolume(null);
    citation.setVolumeNumber(null);
    citation.setIssue(null);
    citation.setUrl(articleCitation.getUrl());
    citation.setSummary(articleCitation.getSummary());
    citation.setPublisherName(articleCitation.getPublisherName());
    citation.setPublisherLocation(articleCitation.getPublisherLocation());
    citation.setPages(articleCitation.getPages());
    citation.setKey(articleCitation.getKey());
    citation.setNote(articleCitation.getNote());

    if (articleCitation.getCollaborativeAuthors() != null && articleCitation.getCollaborativeAuthors().size() > 0) {
      ArrayList<String> collaborativeAuthors = new ArrayList<String>(articleCitation.getCollaborativeAuthors().size());
      for (String collabAuthor : articleCitation.getCollaborativeAuthors()) {
        collaborativeAuthors.add(collabAuthor);
      }
      citation.setCollaborativeAuthors(collaborativeAuthors);
    }

    Annotation annotation = (Annotation) template.get(Annotation.class, URI.create(annotationId));

    List<Article> articles = template.findByCriteria(DetachedCriteria.forClass(Article.class)
          .add(Restrictions.eq("doi", annotation.getAnnotates().toString())));
    Article article = articles.get(0);
    
    // article author information is stored in the article object
    List<ArticleAuthor> authors = article.getAuthors();
    if (authors != null) {
      citation.setAnnotationArticleAuthors(new ArrayList<ArticleContributor>());
      for (ArticleAuthor author : authors) {
        ArticleContributor newAuthor = new ArticleContributor();
        newAuthor.setGivenNames(author.getGivenNames());
        newAuthor.setSurnames(author.getSurnames());
        newAuthor.setSuffix(author.getSuffix());
        newAuthor.setFullName(author.getFullName());
        newAuthor.setIsAuthor(true);

        newAuthor.setId(null);
        template.saveOrUpdate(newAuthor);
        citation.getAnnotationArticleAuthors().add(newAuthor);
      }
    }

    // article editor information is stored in the article object
    List<ArticleEditor> editors = article.getEditors();
    if (editors != null) {
      citation.setAnnotationArticleEditors(new ArrayList<ArticleContributor>());
      for (ArticleEditor editor : editors) {
        ArticleContributor newEditor = new ArticleContributor();
        newEditor.setGivenNames(editor.getGivenNames());
        newEditor.setSurnames(editor.getSurnames());
        newEditor.setSuffix(editor.getSuffix());
        newEditor.setFullName(editor.getFullName());
        newEditor.setIsAuthor(false);

        newEditor.setId(null);
        template.saveOrUpdate(newEditor);
        citation.getAnnotationArticleEditors().add(newEditor);
      }
    }
  }
}
