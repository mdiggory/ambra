/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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

import org.ambraproject.admin.service.DocumentManagementService;
import org.ambraproject.admin.service.SyndicationService;
import org.ambraproject.article.ArchiveProcessException;
import org.ambraproject.filestore.FSIDMapper;
import org.ambraproject.filestore.FileStoreException;
import org.ambraproject.filestore.FileStoreService;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.ambraproject.service.HibernateServiceImpl;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.models.Issue;
import org.w3c.dom.Document;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Alex Kudlick
 */
public class HibernateIngesterImpl extends HibernateServiceImpl implements Ingester {
  private static final Logger log = LoggerFactory.getLogger(HibernateIngesterImpl.class);

  private FileStoreService fileStoreService;
  private SyndicationService syndicationService;
  private IngestArchiveProcessor ingestArchiveProcessor;
  private DocumentManagementService documentManagementService;

  /**
   * Set the IngestArchiveProcessor to use to create an Article object from the XML
   *
   * @param ingestArchiveProcessor - the xml processor to use
   */
  @Required
  public void setIngestArchiveProcessor(IngestArchiveProcessor ingestArchiveProcessor) {
    this.ingestArchiveProcessor = ingestArchiveProcessor;
  }

  /**
   * Set the documentManagementService, used to remove files from the filesystem on reingest
   *
   * @param documentManagementService the document management service to use
   */
  @Required
  public void setDocumentManagementService(DocumentManagementService documentManagementService) {
    this.documentManagementService = documentManagementService;
  }

  /**
   * Set the {@link FileStoreService} to use to store files
   *
   * @param fileStoreService - the filestore to use
   */
  @Required
  public void setFileStoreService(FileStoreService fileStoreService) {
    this.fileStoreService = fileStoreService;
  }

  /**
   * Set the Syndication Service to use in creating syndications for ingested articles
   *
   * @param syndicationService - the {@link SyndicationService} to set
   */
  @Required
  public void setSyndicationService(SyndicationService syndicationService) {
    this.syndicationService = syndicationService;
  }


  /**
   * TODO: Rollback from the filestore if there's a problem storing the files
   *
   * @param archive - the archive to ingest
   * @param force   if true then don't check whether this article already exists but just save this new article.
   * @return the new article
   * @throws DuplicateArticleIdException if an article exists with the same URI as the new article and <var>force</var>
   *                                     is false
   * @throws IngestException             if there's any other problem ingesting the article
   */
  @Transactional(rollbackFor = Throwable.class)
  @SuppressWarnings("unchecked")
  public Article ingest(ZipFile archive, boolean force)
    throws DuplicateArticleIdException, IngestException {
    Article article = null;
    try {
      final Document articleXml = ingestArchiveProcessor.extractArticleXml(archive);
      article = ingestArchiveProcessor.processArticle(archive, articleXml);
      updateWithExistingCategories(article);

      //Check if we already have an article
      Article existingArticle = null;
      List<Article> results = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Article.class)
          .add(Restrictions.eq("doi", article.getDoi())), 0, 1);
      if (results.size() > 0) {
        existingArticle = results.get(0);
      }
      // if the article is in Disabled state, we allow ingest without force
      if (!force && existingArticle != null && existingArticle.getState() != Article.STATE_DISABLED) {
        throw new DuplicateArticleIdException(article.getDoi());
      }

      if (existingArticle != null) {
        updateArticle(article, existingArticle);
        article = existingArticle;
      } else {
        saveArticle(article);
      }

      // For every RelatedArticle object, create a reciprocal link from old Article to this Article.
      addReciprocalRelatedArticleAssociations(article);

      //if this is an image article, update the issues for which this is the image
      if (article.getDoi().contains("image")) {
        updateIssueForImageArticle(article);
      }
      //create syndications
      syndicationService.createSyndications(article.getDoi());

      //Store files to the file store
      storeFiles(archive, article.getDoi());

      return article;
    } catch (ArchiveProcessException e) {
      throw new IngestException("Error processing zip archive to extract article information; archive" + archive.getName(), e);
    } catch (IOException e) {
      throw new IngestException("Error reading entries from zip archive: " + archive.getName(), e);
    } catch (FileStoreException e) {
      throw new IngestException("Error storing blobs to the file store; article: "
        + article.getDoi() + ", archive: " + archive.getName(), e);
    } catch (DataAccessException e) {
      throw new IngestException("Error storing information for article " + article.getDoi() + " to the SQL database", e);
    } catch (HibernateException e) {
      throw new IngestException("Error storing information for article " + article.getDoi() + " to the SQL database", e);
    } catch (NoSuchArticleIdException e) {
      throw new IngestException("Article wasn't stored to the database", e);
    }
  }

  /**
   * Update the article to reference any already existing categories in the database.
   * @param article the article to update
   */
  private void updateWithExistingCategories(Article article) {
    Set<Category> correctCategories = new HashSet<Category>(article.getCategories().size());
    for (Category category : article.getCategories()) {
      try {
        Category existingCategory;
        if (category.getSubCategory() != null) {
          existingCategory = (Category) hibernateTemplate.findByCriteria(
              DetachedCriteria.forClass(Category.class)
                  .add(Restrictions.eq("mainCategory", category.getMainCategory()))
                  .add(Restrictions.eq("subCategory", category.getSubCategory())), 0, 1).get(0);
        } else {
          existingCategory = (Category) hibernateTemplate.findByCriteria(
              DetachedCriteria.forClass(Category.class)
                  .add(Restrictions.eq("mainCategory", category.getMainCategory()))
                  .add(Restrictions.isNull("subCategory")), 0, 1).get(0);
        }
        correctCategories.add(existingCategory);
      } catch (IndexOutOfBoundsException e) {
        //category must not have existed
        correctCategories.add(category);
      }
    }
    article.setCategories(correctCategories);
  }

  @SuppressWarnings("unchecked")
  private void updateIssueForImageArticle(Article imageArticle) {
    List<Issue> issues = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Issue.class)
            .add(Restrictions.eq("image", URI.create(imageArticle.getDoi()))));
    for (Issue issue : issues) {
      issue.setDescription(imageArticle.getDescription());
      issue.setTitle(imageArticle.getTitle());
      hibernateTemplate.update(issue);
    }
  }

  private void saveArticle(Article article) {
    log.debug("Saving article information for article: {}", article.getDoi());
    hibernateTemplate.save(article);
  }

  /**
   * Update an existing article by copying properties from the new one over.  Note that we can't call saveOrUpdate,
   * since the new article is not a persistent instance, but has all the properties that we want.
   * <p/>
   * See <a href="http://stackoverflow.com/questions/4779239/update-persistent-object-with-transient-object-using-hibernate">this
   * post on stack overflow</a> for more information
   *
   * For collections, we clear the old property and add all the new entries, relying on 'delete-orphan' to delete the old objects.
   * The downside of this approach is that it results in a delete statement for each entry in the old collection, and an insert statement for
   * each entry in the new collection.  There a couple of things we could do to optimize this:
   * <ol>
   *   <li>Write a sql statement to delete the old entries in one go</li>
   *   <li>copy over collection properties recursively instead of clearing the old collection.  e.g. for {@link Article#assets}, instead of
   *   clearing out the old list, we would find the matching asset by DOI and Extension, and update its properties</li>
   * </ol>
   *
   * Option number 2 is messy and a lot of code (I've done it before)
   *
   * @param article         the new article, parsed from the xml
   * @param existingArticle the article pulled up from the database
   * @throws IngestException if there's a problem copying properties or updating
   */
  @SuppressWarnings("unchecked")
  private void updateArticle(final Article article, final Article existingArticle) throws IngestException {
    log.debug("ReIngesting (force ingest) article: {}", existingArticle.getDoi());
    //Hibernate deletes orphans after inserting the new rows, which violates a unique constraint on (doi, extension) for assets
    //this temporary change gets around the problem, before the old assets are orphaned and deleted
    hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        session.createSQLQuery(
            "update articleAsset " +
                "set doi = concat('old-',doi), " +
                "extension = concat('old-',extension) " +
                "where articleID = :articleID"
        ).setParameter("articleID",existingArticle.getID())
         .executeUpdate();
        return null;
      }
    });


    final BeanWrapper source = new BeanWrapperImpl(article);
    final BeanWrapper destination = new BeanWrapperImpl(existingArticle);

    try {
      //copy properties
      for (final PropertyDescriptor property : destination.getPropertyDescriptors()) {
        final String name = property.getName();
        if (!name.equals("ID") &&
          !name.equals("created") &&
          !name.equals("class")) {
          //Collections shouldn't be dereferenced but have elements added
          //See http://www.onkarjoshi.com/blog/188/hibernateexception-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenced-by-the-owning-entity-instance/
          if (Collection.class.isAssignableFrom(property.getPropertyType())) {
            Collection orig = (Collection) destination.getPropertyValue(name);
            orig.clear();
            orig.addAll((Collection) source.getPropertyValue(name));
          } else {
            //just set the new value
            destination.setPropertyValue(name, source.getPropertyValue(name));
          }
        }
      }
      //Circular relationship in related articles
      for (ArticleRelationship articleRelationship : existingArticle.getRelatedArticles()) {
        articleRelationship.setParentArticle(existingArticle);
      }
    } catch (Exception e) {
      throw new IngestException("Error copying properties for article " + article.getDoi(), e);
    }

    hibernateTemplate.update(existingArticle);
  }

  /**
   * Process files from the archive and store them to the {@link FileStoreService}
   *
   * @param archive - the archive being ingested
   * @param doi
   * @throws java.io.IOException - if there's a problem reading from the zip file
   * @throws org.ambraproject.filestore.FileStoreException
   *                             - if there's a problem writing files to the file store
   */
  private void storeFiles(final ZipFile archive, String doi)
    throws IOException, FileStoreException {
    log.info("Removing existing files (if any) for {}", doi);

    try {
      documentManagementService.removeFromFileSystem(doi);
    } catch (Exception e) {
      throw new FileStoreException("Error removing existing files from the file store", e);
    }

    log.info("Storing files from archive {} to the file store", archive.getName());
    Enumeration<? extends ZipEntry> entries = archive.entries();

    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (!entry.getName().equalsIgnoreCase("manifest.dtd")) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
          inputStream = archive.getInputStream(entry);
          outputStream = fileStoreService.getFileOutStream(
            FSIDMapper.zipToFSID(doi, entry.getName()), entry.getSize());
          fileStoreService.copy(inputStream, outputStream);
        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              log.warn("Error closing input stream while writing files", e);
            }
          }
          if (outputStream != null) {
            try {
              outputStream.close();
            } catch (IOException e) {
              log.warn("Error closing output stream while writing files", e);
            }
          }
        }
      }
    }
    log.info("Finished storing files from archive {}", archive.getName());
  }

  /**
   * Add/update reciprocal related article links.  There are two situations in which this is relevant:
   * <ol>
   *   <li>
   *     We are ingesting Article B. Article A already exists, and relates to Article B. We need to update Article A's relationship to set the 'otherArticleID' property,
   *     and make sure that Article B has a link back to Article A.
   *   </li>
   *   <li>
   *     We ingesting Article B, which has a relationship pointing to Article A.  Article A already exists.
   *     We need to make sure Article A has a link back to Article B, and update the 'otherArticleID' property on both of the relationships.
   *   </li>
   * </ol>
   *
   * @param newArticle The Article which is being ingested
   */
  @SuppressWarnings("unchecked")
  private void addReciprocalRelatedArticleAssociations(Article newArticle) {
    //keep track of the other articles we already updated
    Set<String> otherArticleDois = new HashSet<String>(newArticle.getRelatedArticles().size());

    //For each of the articles that the new one links to, update the reciprocal relations
    for (ArticleRelationship relationship : newArticle.getRelatedArticles()) {
      otherArticleDois.add(relationship.getOtherArticleDoi());
      Article otherArticle;
      //Set the 'otherArticleID' property for any new relationships created by this article
      try {
        otherArticle = (Article) hibernateTemplate.findByCriteria(
            DetachedCriteria.forClass(Article.class)
                .add(Restrictions.eq("doi", relationship.getOtherArticleDoi())),
            0, 1).get(0);
      } catch (IndexOutOfBoundsException e) {
        //other article didn't exist
        continue;
      }
      relationship.setOtherArticleID(otherArticle.getID());
      hibernateTemplate.update(relationship);

      //Now ensure that there is a reciprocal link, i.e. that the 'other article' links back to the new one
      boolean createNewRelationship = true;
      //so we have to check if the other article already has a link to this one
      for (ArticleRelationship otherArticleRelationship : otherArticle.getRelatedArticles()) {
        if (otherArticleRelationship.getOtherArticleDoi().equals(newArticle.getDoi())) {
          createNewRelationship = false;
          otherArticleRelationship.setOtherArticleID(newArticle.getID());
          hibernateTemplate.update(otherArticleRelationship);
          break;
        }
      }
      //if the other article didn't already have a link to this one, we need to make a new one
      if (createNewRelationship) {
        ArticleRelationship reciprocalLink = new ArticleRelationship();
        reciprocalLink.setParentArticle(otherArticle);
        reciprocalLink.setOtherArticleID(newArticle.getID());
        reciprocalLink.setOtherArticleDoi(newArticle.getDoi());
        reciprocalLink.setType(relationship.getType());
        otherArticle.getRelatedArticles().add(reciprocalLink);
        hibernateTemplate.update(otherArticle);
      }
    }

    //Now we need to find any existing articles that link to the new one, (that we didn't just update) and update the relationships
    List<Article> articlesLinkingToNewOne;
    if (!otherArticleDois.isEmpty()) {
      //articles linking to this one that we didn't already visit
      articlesLinkingToNewOne = hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .add(Restrictions.not(Restrictions.in("doi", otherArticleDois)))
              .createCriteria("relatedArticles")
              .add(Restrictions.eq("otherArticleDoi", newArticle.getDoi())));
    } else {
      //hibernate throws a sql grammar exception if you do a restrictions.in() with an empty collection
      articlesLinkingToNewOne = hibernateTemplate.findByCriteria(
          DetachedCriteria.forClass(Article.class)
              .createCriteria("relatedArticles")
              .add(Restrictions.eq("otherArticleDoi", newArticle.getDoi())));
    }
    for (Article otherArticle : articlesLinkingToNewOne) {
      //update the other article's relationship
      for (ArticleRelationship otherRelationship : otherArticle.getRelatedArticles()) {
        if (otherRelationship.getOtherArticleDoi().equals(newArticle.getDoi())) {
          otherRelationship.setOtherArticleID(newArticle.getID());
          hibernateTemplate.update(otherRelationship);
          //create a relationship linking to the other article
          ArticleRelationship relationship = new ArticleRelationship();
          relationship.setParentArticle(newArticle);
          relationship.setOtherArticleID(otherArticle.getID());
          relationship.setOtherArticleDoi(otherArticle.getDoi());
          relationship.setType(otherRelationship.getType());
          newArticle.getRelatedArticles().add(relationship);
        }
      }
    }
    //if we added new relationships, update the new article
    if (articlesLinkingToNewOne.size() > 0) {
      hibernateTemplate.update(newArticle);
    }
  }
}
