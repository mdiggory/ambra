/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.models;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 3/7/12
 */
public class AnnotationTest extends BaseHibernateTest {

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullCreator() {
    hibernateTemplate.save(new Annotation(null, AnnotationType.COMMENT, 12l));
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullType() {
    UserProfile creator = new UserProfile("authIdForNullType", "email@nullType.org", "displayNameForNullType");
    hibernateTemplate.save(creator);
    hibernateTemplate.save(new Annotation(creator, null, 12l));
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullArticleID() {
    UserProfile creator = new UserProfile("authIdForNullArticleId", "email@nullArticleID.org", "displayNameForNullArticleID");
    hibernateTemplate.save(creator);
    hibernateTemplate.save(new Annotation(creator, AnnotationType.COMMENT, null));
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testDoesNotAllowRatingConstruction() {
    new Annotation(null, AnnotationType.RATING, null);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testDoesNotAllowRatingTypeSet() {
    Annotation annotation = new Annotation();
    annotation.setType(AnnotationType.RATING);
  }

  @Test
  public void testSaveBasicAnnotation() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    UserProfile creator = new UserProfile(
        "authIdForInsertAnnotation",
        "email@InsertAnnotation.org",
        "displayNameForInsertAnnotation"
    );
    hibernateTemplate.save(creator);
    Annotation annotation = new Annotation();
    annotation.setCreator(creator);
    annotation.setAnnotationUri("fakeAnnotationUriForInsert");
    annotation.setArticleID(1l);
    annotation.setType(AnnotationType.COMMENT);
    annotation.setTitle("What Happened to Frederick");
    annotation.setBody("With their love for each other growing stronger, David finally agrees to tell " +
        "Kathryn about his relationship with Mary Margaret and put an end to his loveless marriage. " +
        "Meanwhile, in the fairytale land that was, while runaway groom Prince Charming searches for " +
        "Snow White, he agrees to aid Abigail on a dangerous mission to recover something precious " +
        "that was lost to her.");

    Serializable id = hibernateTemplate.save(annotation);

    Annotation storedAnnotation = (Annotation) hibernateTemplate.get(Annotation.class, id);
    assertNotNull(storedAnnotation, "Didn't store annotation");
    assertEquals(storedAnnotation.getAnnotationUri(), annotation.getAnnotationUri(), "Didn't store annotation uri");
    assertEquals(storedAnnotation.getArticleID(), annotation.getArticleID(), "Didn't store article id");
    assertEquals(storedAnnotation.getType(), annotation.getType(), "Didn't store type");
    assertEquals(storedAnnotation.getTitle(), annotation.getTitle(), "Didn't store correct title");
    assertEquals(storedAnnotation.getBody(), annotation.getBody(), "Didn't store correct body");
    assertNotNull(storedAnnotation.getCreator(), "didn't link to creator");
    assertEquals(storedAnnotation.getCreator().getAuthId(), annotation.getCreator().getAuthId(), "linked to incorrect creator");

    assertNotNull(storedAnnotation.getCreated(), "Annotation didn't get created date set");
    assertTrue(storedAnnotation.getLastModified().getTime() >= testStart, "Created date wasn't after test start");
  }

  @Test
  public void testSaveWithCitation() {
    UserProfile creator = new UserProfile("authIdForSaveWithCitation", "email@saveWithCitation.org", "displayNameForSaveWithCitation");
    hibernateTemplate.save(creator);
    Long articleID = (Long) hibernateTemplate.save(new Article("if:doi-for-saveWithCitation"));
    Annotation annotation = new Annotation(creator, AnnotationType.RETRACTION, articleID);
    annotation.setAnnotationCitation(new AnnotationCitation());
    annotation.getAnnotationCitation().setTitle("test title");
    annotation.getAnnotationCitation().setELocationId("test eLocationId");

    Serializable id = hibernateTemplate.save(annotation);
    Annotation storedAnnotation = (Annotation) hibernateTemplate.get(Annotation.class, id);
    assertNotNull(storedAnnotation.getAnnotationCitation(), "Annotation didn't cascade save to annotation citation");
    assertNotNull(storedAnnotation.getAnnotationCitation().getCreated(), "Citation didn't get created date set");
    assertEquals(storedAnnotation.getAnnotationCitation().getTitle(), annotation.getAnnotationCitation().getTitle(),
        "stored citation had incorrect title");
    assertEquals(storedAnnotation.getAnnotationCitation().getELocationId(), annotation.getAnnotationCitation().getELocationId(),
        "stored citation had incorrect eLocationId");
  }

  @Test
  public void testAddCitation() {
    long testStart = Calendar.getInstance().getTimeInMillis();
    UserProfile creator = new UserProfile(
        "authIdForAnnotationWithCitation",
        "email@AnnotationCitation.org",
        "displayNameForAnnotationCitation"
    );
    hibernateTemplate.save(creator);
    Annotation annotation = new Annotation(creator, AnnotationType.REPLY, 1234l);
    Serializable id = hibernateTemplate.save(annotation);
    AnnotationCitation citation = new AnnotationCitation();
    citation.setIssue("foo");
    citation.setVolume("bar");
    citation.setAuthors(new ArrayList<CorrectedAuthor>(2));
    citation.getAuthors().add(new CorrectedAuthor());
    citation.getAuthors().add(new CorrectedAuthor());

    citation.setCollaborativeAuthors(new ArrayList<String>(1));
    citation.getCollaborativeAuthors().add("foo mcfoo foundation");

    annotation.setAnnotationCitation(citation);
    hibernateTemplate.update(annotation);

    Annotation storedAnnotation = (Annotation) hibernateTemplate.get(Annotation.class, id);
    assertNotNull(storedAnnotation.getAnnotationCitation(), "didn't associate citation to the annotation");
    assertEquals(storedAnnotation.getAnnotationCitation().getIssue(), citation.getIssue(),
        "associated citation had incorrect issue");
    assertEquals(storedAnnotation.getAnnotationCitation().getVolume(), citation.getVolume(),
        "associated citation had incorrect volume");
    assertNotNull(storedAnnotation.getAnnotationCitation().getCreated(), "Citation didn't get created date set");
    assertEquals(storedAnnotation.getAnnotationCitation().getAuthors().size(), citation.getAuthors().size(),
        "didn't cascade create to authors");
    for (CorrectedAuthor author : storedAnnotation.getAnnotationCitation().getAuthors()) {
      assertNotNull(author, "Added a null author to annotation list");
      assertNotNull(author.getCreated(), "Author didn't get created date set");
    }
    assertEquals(storedAnnotation.getAnnotationCitation().getCollaborativeAuthors().toArray(),
        citation.getCollaborativeAuthors().toArray(), "Didn't create citation's collab authors");
    assertNotNull(storedAnnotation.getLastModified(), "annotation didn't get last modified date set");
    assertTrue(storedAnnotation.getLastModified().getTime() >= testStart,
        "last modified date wasn't after test start");
  }

  @Test
  public void testConstructCitationFromArticle() throws ParseException {
    Article article = new Article("id:test-doi-for-creating-citation");
    article.setTitle("Once Upon A Time");
    article.setIssue("test issue");
    article.setVolume("test volume");
    article.setJournal("test journal");
    article.setUrl("http://dx.plos.org/test-doi-for-creating-citation");
    article.setPublisherName("PLoS");
    article.setPublisherLocation("San Francisco");
    article.seteLocationId("12341-234214");
    article.setDescription("test description");

    article.setCollaborativeAuthors(new ArrayList<String>(2));
    article.getCollaborativeAuthors().add("The Skoll Foundation");
    article.getCollaborativeAuthors().add("The Bill and Melinda Gates Foundation");

    article.setDate(new SimpleDateFormat("yyyy-MM-DD").parse("2100-03-18"));

    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setGivenNames("Emma");
    author1.setSurnames("Swan");
    author1.setSuffix("Dr.");
    authors.add(author1);

    ArticleAuthor author2 = new ArticleAuthor();
    author2.setGivenNames("Harry");
    author2.setSurnames("Potter");
    author2.setSuffix("PhD");
    authors.add(author2);

    article.setAuthors(authors);

    AnnotationCitation citation = new AnnotationCitation(article);
    assertEquals(citation.getTitle(), article.getTitle(), "Didn't get correct title");
    assertEquals(citation.getIssue(), article.getIssue(), "Didn't get correct issue");
    assertEquals(citation.getVolume(), article.getVolume(), "Didn't get correct volume");
    assertEquals(citation.getIssue(), article.getIssue(), "Didn't get correct issue");
    assertEquals(citation.getJournal(), article.getJournal(), "Didn't get correct journal");
    assertEquals(citation.getUrl(), article.getUrl(), "Didn't get correct url");
    assertEquals(citation.getSummary(), article.getDescription(), "Didn't get correct summary");
    assertEquals(citation.getCollaborativeAuthors().toArray(), article.getCollaborativeAuthors().toArray(),
        "Didn't get correct collab authors");
    assertEquals(citation.getYear(), "2100", "citation didn't get correct year");

    assertEquals(citation.getAuthors().size(), article.getAuthors().size(), "Didn't get correct number of authors");
    for (int i = 0; i < article.getAuthors().size(); i++) {
      ArticleAuthor expected = article.getAuthors().get(i);
      CorrectedAuthor actual = citation.getAuthors().get(i);
      assertEquals(actual.getGivenNames(), expected.getGivenNames(), "Author " + i + " had incorrect given names");
      assertEquals(actual.getSurName(), expected.getSurnames(), "Author " + i + " had incorrect surnames");
      assertEquals(actual.getSuffix(), expected.getSuffix(), "Author " + i + " had incorrect suffix");
    }
  }

  @Test
  public void testDoesNotCascadeDeleteToCreator() {
    UserProfile creator = new UserProfile(
        "authIdForCascadeDelete",
        "email@CascadeDelete.org",
        "displayNameForCascadeDelete"
    );
    Serializable creatorId = hibernateTemplate.save(creator);
    Annotation annotation = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, 23l);
    hibernateTemplate.save(annotation);
    hibernateTemplate.delete(annotation);
    assertNotNull(hibernateTemplate.get(UserProfile.class, creatorId), "Annotation deleted creator");
  }

  @Test
  public void testCascadeDeleteToCitation() {
    UserProfile creator = new UserProfile(
        "authIdForCascadeDeleteCitation",
        "email@CascadeDeleteCitation.org",
        "displayNameForCascadeDeleteCitation"
    );
    hibernateTemplate.save(creator);
    Annotation annotation = new Annotation(creator, AnnotationType.FORMAL_CORRECTION, 23l);
    Serializable annotationId = hibernateTemplate.save(annotation);

    //Add the citation
    AnnotationCitation citation = new AnnotationCitation();
    Serializable citationId = hibernateTemplate.save(citation);
    annotation.setAnnotationCitation(citation);
    hibernateTemplate.update(annotation);

    //Make sure it got attached
    assertNotNull(((Annotation) hibernateTemplate.get(Annotation.class, annotationId)).getAnnotationCitation(),
        "Citation didn't get attached on update");

    //delete annotation
    hibernateTemplate.delete(annotation);
    assertNull(hibernateTemplate.get(AnnotationCitation.class, citationId), "Citation didn't get deleted");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testLoadTypeFromStringRepresentation() {
    final Long userId = (Long) hibernateTemplate.save(new UserProfile(
        "authIdForLoadType",
        "email@LoadType.org",
        "displayNameForLoadType"
    ));
    final Long articleId = (Long) hibernateTemplate.save(new Article("id:doi-for-LoadType"));

    hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        session.createSQLQuery(
            "insert into annotation (created, lastModified, userProfileID, articleID, type, annotationURI) " +
                "values (?,?,?,?,?,?)")
            .setParameter(0, Calendar.getInstance().getTime(), StandardBasicTypes.DATE)
            .setParameter(1, Calendar.getInstance().getTime(), StandardBasicTypes.DATE)
            .setParameter(2, userId, StandardBasicTypes.LONG)
            .setParameter(3, articleId, StandardBasicTypes.LONG)
            .setParameter(4, "Comment", StandardBasicTypes.STRING)
            .setParameter(5, "unique-annotation-uri-for-loadTypeFromString", StandardBasicTypes.STRING)
            .executeUpdate();
        return null;
      }
    });

    List<Annotation> results = hibernateTemplate.find(
        "from Annotation where annotationUri = ?", "unique-annotation-uri-for-loadTypeFromString");
    assertEquals(results.size(), 1, "didn't store annotation correctly");
    assertEquals(results.get(0).getType(), AnnotationType.COMMENT, "Type wasn't loaded correctly");
  }
}
