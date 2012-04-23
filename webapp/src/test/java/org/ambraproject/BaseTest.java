/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. |
 */

package org.ambraproject;

import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.RelatedArticleInfo;
import org.ambraproject.models.Annotation;
import org.ambraproject.models.AnnotationType;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleAuthor;
import org.ambraproject.models.ArticleEditor;
import org.ambraproject.models.ArticleRelationship;
import org.ambraproject.models.Category;
import org.ambraproject.models.CitedArticle;
import org.ambraproject.models.CitedArticleAuthor;
import org.ambraproject.models.CitedArticleEditor;
import org.ambraproject.models.CorrectedAuthor;
import org.ambraproject.testutils.DummyDataStore;
import org.ambraproject.views.AnnotationView;
import org.ambraproject.views.ArticleCategory;
import org.ambraproject.views.AuthorView;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.topazproject.ambra.models.Journal;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;

/**
 * Base class for tests of Ambra Service Beans.  This is provided just so they can all use the same applicationContext
 * xml file; Bean tests should just test methods of the interface and have an instance autowired (see {@link
 * org.ambraproject.annotation.service.AnnotationServiceTest} for an example.
 *
 * @author Alex Kudlick Date: 4/29/11
 *         <p/>
 *         org.topazproject.ambra
 */
@ContextConfiguration(locations = "nonWebApplicationContext.xml")
@Test(singleThreaded = true)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

  /**
   * Instance provided so that tests can store dummy data in the same test database that the autowired beans are using.
   * Tests should use this to seed the database with data to test.
   */
  @Autowired
  protected DummyDataStore dummyDataStore;
  public static final String DEFAULT_ADMIN_AUTHID = "AdminAuthorizationID";
  public static final String DEFUALT_USER_AUTHID = "DummyTestUserAuthorizationID";
  public static final Journal defaultJournal = new Journal();

  static {
    defaultJournal.setId(URI.create("id:base-journal"));
    defaultJournal.setKey("journal");
    defaultJournal.seteIssn("defaultEIssn");
  }

  /**
   * Helper method to compare dates.  This compares down to the minute, and checks that the seconds are within 1, since
   * rounding can occur when storing to an hsql db
   *
   * @param actual   - the date from mysql to compare
   * @param expected - the date from topaz to compare
   */
  protected static void assertMatchingDates(Date actual, Date expected) {
    if (actual == null || expected == null) {
      assertTrue(actual == null && expected == null, "one date was null and the other wasn't");
    } else {
      Calendar actualCal = new GregorianCalendar();
      actualCal.setTime(actual);
      Calendar expectedCal = new GregorianCalendar();
      expectedCal.setTime(expected);
      assertEquals(actualCal.get(Calendar.YEAR), expectedCal.get(Calendar.YEAR), "Dates didn't have matching years");
      assertEquals(actualCal.get(Calendar.MONTH), expectedCal.get(Calendar.MONTH), "dates didn't have matching months");
      assertEquals(actualCal.get(Calendar.DAY_OF_MONTH), expectedCal.get(Calendar.DAY_OF_MONTH), "dates didn't have matching days of month");
      assertEquals(actualCal.get(Calendar.DAY_OF_WEEK), expectedCal.get(Calendar.DAY_OF_WEEK), "dates didn't have matching days of week");
      assertEquals(actualCal.get(Calendar.HOUR), expectedCal.get(Calendar.HOUR), "dates didn't have matching hours");
      assertEquals(actualCal.get(Calendar.MINUTE), expectedCal.get(Calendar.MINUTE), "dates didn't have matching minutes");
      int secondMin = expectedCal.get(Calendar.SECOND) - 1;
      int secondMax = expectedCal.get(Calendar.SECOND) + 1;
      int actualSecond = actualCal.get(Calendar.SECOND);
      assertTrue(secondMin <= actualSecond && actualSecond <= secondMax,
          "date didn't have correct second; expected something in [" + secondMin + "," + secondMax +
              "]; but got " + actualSecond);
    }
  }

  /**
   * Helper method to compare article properties
   *
   * @param actual   - actual article
   * @param expected - article with expected properties
   */
  protected void compareArticles(ArticleInfo actual, Article expected) {
    assertNotNull(actual, "returned null article");
    assertEquals(actual.getDoi(), expected.getDoi(), "Article had incorrect doi");
    assertEquals(actual.geteIssn(), expected.geteIssn(), "returned incorrect eIssn");
    assertEquals(actual.getTitle(), expected.getTitle(), "Article had incorrect Title");
    assertEquals(actual.getDescription(), expected.getDescription(), "Article had incorrect description");
    assertEqualsNoOrder(actual.getCategories().toArray(), expected.getCategories().toArray(), "Incorrect categories");
  }

  /**
   * Helper method to compare article properties
   *
   * @param actual   - actual article
   * @param expected - article with expected properties
   */
  protected void compareArticles(Article actual, Article expected) {
    assertNotNull(actual, "returned null article");
    assertEquals(actual.getDoi(), expected.getDoi(), "Article had incorrect doi");

    assertEquals(actual.geteIssn(), expected.geteIssn(), "returned incorrect eIssn");
    assertEquals(actual.getUrl(), expected.getUrl(), "returned incorrect url");


    assertEquals(actual.getRights(), expected.getRights(),
        "Article had incorrect rights");
    assertEquals(actual.getLanguage(), expected.getLanguage(),
        "Article had incorrect language");
    assertEquals(actual.getPublisherLocation(), expected.getPublisherLocation(),
        "Article had incorrect publisher");
    assertEquals(actual.getFormat(), expected.getFormat(),
        "Article had incorrect format");
    assertEquals(actual.getTitle(), expected.getTitle(),
        "Article had incorrect Title");
    assertEquals(actual.getDescription(), expected.getDescription(),
        "Article had incorrect description");
    assertEquals(actual.getArchiveName(), expected.getArchiveName(), "Article had incorrect archive name");

    assertEqualsNoOrder(actual.getCategories().toArray(), expected.getCategories().toArray(), "Incorrect categories");

    if (expected.getAssets() != null) {
      assertEquals(actual.getAssets().size(), expected.getAssets().size(), "incorrect number of assets");
      for (int i = 0; i < actual.getAssets().size(); i++) {
        compareAssets(actual.getAssets().get(i), expected.getAssets().get(i));
      }
    }
    if (expected.getCitedArticles() != null) {
      assertEquals(actual.getCitedArticles().size(), expected.getCitedArticles().size(), "Returned incorrect number of references");
      for (int i = 0; i < actual.getCitedArticles().size(); i++) {
        compareCitedArticles(actual.getCitedArticles().get(i), expected.getCitedArticles().get(i));
      }
    } else {
      assertTrue(actual.getCitedArticles() == null || actual.getCitedArticles().size() == 0,
          "Returned non-empty references when none were expected");
    }


    if (expected.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "returned null author list");
      assertEquals(actual.getAuthors().size(), expected.getAuthors().size(),
          "returned incorrect number of authors");
      for (int i = 0; i < expected.getAuthors().size(); i++) {
        ArticleAuthor actualAuthor = actual.getAuthors().get(i);
        ArticleAuthor expectedAuthor = expected.getAuthors().get(i);
        assertEquals(actualAuthor.getFullName(), expectedAuthor.getFullName(), "Article Author had incorrect Real Name");
        assertEquals(actualAuthor.getGivenNames(), expectedAuthor.getGivenNames(), "Article Author had incorrect given name");
        assertEquals(actualAuthor.getSurnames(), expectedAuthor.getSurnames(), "Article Author had incorrect surname");
      }
    }
    if (expected.getEditors() != null) {
      assertNotNull(actual.getEditors(), "returned null editor list");
      assertEquals(actual.getEditors().size(), expected.getEditors().size(),
          "returned incorrect number of editors");
      for (int i = 0; i < expected.getEditors().size(); i++) {
        ArticleEditor actuaEditor = actual.getEditors().get(i);
        ArticleEditor expectedEditor = expected.getEditors().get(i);
        assertEquals(actuaEditor.getFullName(), expectedEditor.getFullName(), "Article Editor had incorrect Real Name");
        assertEquals(actuaEditor.getGivenNames(), expectedEditor.getGivenNames(), "Article Editor had incorrect given name");
        assertEquals(actuaEditor.getSurnames(), expectedEditor.getSurnames(), "Article Editor had incorrect surname");
      }
    }
    if (expected.getRelatedArticles() != null) {
      assertNotNull(actual.getRelatedArticles(), "null list of related articles");
      assertEquals(actual.getRelatedArticles().size(), expected.getRelatedArticles().size(), "Incorrect number of related articles");
      for (int i = 0; i < actual.getRelatedArticles().size(); i++) {
        ArticleRelationship actualRelatedArticle = actual.getRelatedArticles().get(i);
        ArticleRelationship expectedRelatedArticle = expected.getRelatedArticles().get(i);
        assertEquals(actualRelatedArticle.getOtherArticleDoi(), expectedRelatedArticle.getOtherArticleDoi(),
            "related article " + i + " had incorrect otherArticleDoi");
        assertEquals(actualRelatedArticle.getType(), expectedRelatedArticle.getType(),
            "related article " + i + " had incorrect type");
        assertTrue(actualRelatedArticle.getParentArticle() == actual, "related article had incorrect parent article");
      }
    }
  }

  protected void compareCitedArticles(CitedArticle actual, CitedArticle expected) {
    assertNotNull(actual, "Returned null citation");
    assertEquals(actual.getKey(), expected.getKey(), "Returned incorrect citation Key");

    assertEquals(actual.getYear(), expected.getYear(), "Returned incorrect citation Year; key: " + expected.getKey());
    assertEquals(actual.getDisplayYear(), expected.getDisplayYear(), "Returned incorrect citation Display Year; key: " + expected.getKey());
    assertEquals(actual.getMonth(), expected.getMonth(), "Returned incorrect citation Month; key: " + expected.getKey());
    assertEquals(actual.getDay(), expected.getDay(), "Returned incorrect citation Day; key: " + expected.getKey());

    assertEquals(actual.getVolumeNumber(), expected.getVolumeNumber(), "Returned incorrect citation Volume Number; key: " + expected.getKey());
    assertEquals(actual.getVolume(), expected.getVolume(), "Returned incorrect citation Volume; key: " + expected.getKey());
    assertEquals(actual.getIssue(), expected.getIssue(), "Returned incorrect citation Issue; key: " + expected.getKey());
    assertEquals(actual.getTitle(), expected.getTitle(), "Returned incorrect citation Title; key: " + expected.getKey());
    assertEquals(actual.getPublisherLocation(), expected.getPublisherLocation(), "Returned incorrect citation Publisher Location; key: " + expected.getKey());
    assertEquals(actual.getPublisherName(), expected.getPublisherName(), "Returned incorrect citation Publisher name; key: " + expected.getKey());

    assertEquals(actual.getPages(), expected.getPages(), "Returned incorrect citation Page; key: " + expected.getKey());
    assertEquals(actual.geteLocationID(), expected.geteLocationID(), "Returned incorrect citation eLocationId; key: " + expected.getKey());

    assertEquals(actual.getJournal(), expected.getJournal(), "Returned incorrect citation Journal; key: " + expected.getKey());
    assertEquals(actual.getNote(), expected.getNote(), "Returned incorrect citation Note; key: " + expected.getKey());

    if (expected.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "Citation had null editors list when non-null was expected");
      assertEquals(actual.getAuthors().size(), expected.getAuthors().size(), "returned incorrect number of editors");
      for (int i = 0; i < actual.getAuthors().size(); i++) {
        CitedArticleAuthor actualAuthor = actual.getAuthors().get(i);
        CitedArticleAuthor expectedUserProfile = expected.getAuthors().get(i);
        assertEquals(actualAuthor.getFullName(), expectedUserProfile.getFullName(), "Editor had incorrect Real Name");
        assertEquals(actualAuthor.getGivenNames(), expectedUserProfile.getGivenNames(), "Editor had incorrect given name");
        assertEquals(actualAuthor.getSurnames(), expectedUserProfile.getSurnames(), "Editor had incorrect surname");
      }
    }
    if (expected.getEditors() != null) {
      assertNotNull(actual.getEditors(), "Citation had null editors list when non-null was expected");
      assertEquals(actual.getEditors().size(), expected.getEditors().size(), "returned incorrect number of editors");
      for (int i = 0; i < actual.getEditors().size(); i++) {
        CitedArticleEditor actualEditor = actual.getEditors().get(i);
        CitedArticleEditor expectedEditor = expected.getEditors().get(i);
        assertEquals(actualEditor.getFullName(), expectedEditor.getFullName(), "Editor had incorrect Real Name");
        assertEquals(actualEditor.getGivenNames(), expectedEditor.getGivenNames(), "Editor had incorrect given name");
        assertEquals(actualEditor.getSurnames(), expectedEditor.getSurnames(), "Editor had incorrect surname");
      }
    }

    if (expected.getCollaborativeAuthors() != null) {
      assertEquals(actual.getCollaborativeAuthors().toArray(), expected.getCollaborativeAuthors().toArray(),
          "returned incorrect collaborative authors");
    }
    assertEquals(actual.getUrl(), expected.getUrl(), "Returned incorrect citation URL; key: " + expected.getKey());
    assertEquals(actual.getDoi(), expected.getDoi(), "Returned incorrect citation doi'; key: " + expected.getKey());
    assertEquals(actual.getSummary(), expected.getSummary(), "Returned incorrect citation Summary; key: " + expected.getKey());
    assertEquals(actual.getCitationType(), expected.getCitationType(), "Returned incorrect citation Citation Type; key: " + expected.getKey());

  }

  protected void compareAssets(ArticleAsset actual, ArticleAsset expected) {
    assertEquals(actual.getDoi(), expected.getDoi(),
        "asset had incorrect doi");
    assertEquals(actual.getContentType(), expected.getContentType(),
        "asset had incorrect content type");
    assertEquals(actual.getExtension(), expected.getExtension(),
        "asset had incorrect name");
    assertEquals(actual.getSize(), expected.getSize(),
        "asset had incorrect size");
    assertEquals(actual.getTitle(), expected.getTitle(),
        "asset had incorrect title");
    assertEquals(actual.getDescription(), expected.getDescription(),
        "asset had incorrect description");
  }

  protected void checkArticleInfo(ArticleInfo actual, Article expectedArticle,
                                  Article[] expectedRelatedArticles) {
    //basic properties
    assertEquals(actual.getDoi(), expectedArticle.getDoi(), "returned article info with incorrect id");
    assertEquals(actual.getTitle(), expectedArticle.getTitle(),
        "returned article info with incorrect title");
    assertEquals(actual.getDate(), expectedArticle.getDate(),
        "returned article info with incorrect date");
    assertEquals(actual.getDescription(), expectedArticle.getDescription(), "incorrect description");
    assertEquals(actual.getRights(), expectedArticle.getRights(), "incorrect rights");
    //check collaborative authors
    if (expectedArticle.getCollaborativeAuthors() != null) {
      assertNotNull(actual.getCollaborativeAuthors(), "returned null collaborative authors");
      assertEqualsNoOrder(actual.getCollaborativeAuthors().toArray(),
          expectedArticle.getCollaborativeAuthors().toArray(),
          "incorrect collaborative authors");
    }
    //check categories
    if (expectedArticle.getCategories() == null || expectedArticle.getCategories().size() == 0) {
      assertTrue(actual.getCategories() == null || actual.getCategories().size() == 0,
          "returned subjects when none were expected");
    } else {
      assertNotNull(actual.getCategories(), "returned null subjects");

      Set<String> collapsedCategories = new HashSet<String>(expectedArticle.getCategories().size());

      for (Category category : expectedArticle.getCategories()) {
        boolean foundCategory = false;

        collapsedCategories.add(category.getMainCategory());
        collapsedCategories.add(category.getSubCategory());

        for(ArticleCategory cat : actual.getCategories())
        {
          if(cat.getMainCategory().equals(category.getMainCategory())) {
            if(cat.getSubCategory().equals(category.getSubCategory())) {
              foundCategory = true;
            }
          }
        }

        assertTrue(foundCategory, "Didn't return category: " + category.getMainCategory());
      }

      assertEqualsNoOrder(collapsedCategories.toArray(), actual.getTransposedCategories().toArray());
    }

    //check authors
    if (expectedArticle.getAuthors() != null) {
      assertNotNull(actual.getAuthors(), "returned null list of authors");
      assertEquals(actual.getAuthors().size(), expectedArticle.getAuthors().size(), "returned incorrect number of authors");
      for (ArticleAuthor author : expectedArticle.getAuthors()) {
        assertTrue(actual.getAuthors().contains(author.getFullName()), "didn't return author: " + author.getFullName());
      }
    }

    //check related articles
    if (expectedRelatedArticles == null || expectedRelatedArticles.length == 0) {
      assertTrue(actual.getRelatedArticles() == null || actual.getRelatedArticles().size() == 0,
          "returned related articles when none were expected");
    } else {
      assertNotNull(actual.getRelatedArticles(), "returned null list of related articles");
      assertEquals(actual.getRelatedArticles().size(), expectedRelatedArticles.length,
          "returned incorrect number of related articles");
      for (Article otherArticle : expectedRelatedArticles) {
        boolean foundMatch = false;
        for (RelatedArticleInfo actualRelatedArticle : actual.getRelatedArticles()) {
          if (otherArticle.getTitle().equals(actualRelatedArticle.getTitle()) &&
              otherArticle.getDoi().equals(actualRelatedArticle.getUri().toString())) {
            foundMatch = true;
            break;
          }
        }
        if (!foundMatch) {
          fail("Didn't include an entry for related article: " + otherArticle.getDoi());
        }
      }
    }
  }

  protected void checkAnnotationProperties(AnnotationView result, Annotation expected) {
    if (expected.getType() == AnnotationType.MINOR_CORRECTION) {
      assertEquals(result.getTitle(), "Minor Correction: " + expected.getTitle(), "Annotation view had incorrect title");
    } else if (expected.getType() == AnnotationType.FORMAL_CORRECTION) {
      assertEquals(result.getTitle(), "Formal Correction: " + expected.getTitle(), "Annotation view had incorrect title");
    } else if (expected.getType() == AnnotationType.RETRACTION) {
      assertEquals(result.getTitle(), "Retraction: " + expected.getTitle(), "Annotation view had incorrect title");
    }
    assertEquals(result.getBody(), "<p>" + expected.getBody() + "</p>", "Annotation view had incorrect body");
    assertEquals(result.getCompetingInterestStatement(),
        expected.getCompetingInterestBody() == null ? "" : expected.getCompetingInterestBody(),
        "Annotation view had incorrect ci statement");
    assertEquals(result.getAnnotationUri(), expected.getAnnotationUri(), "Annotation view had incorrect annotation uri");
    assertEquals(result.getXpath(), expected.getXpath(), "Annotation view had incorrect xpath");
    assertEquals(result.getCreatorID(), expected.getCreator().getID(), "Annotation view had incorrect creator id");
    assertEquals(result.getCreatorDisplayName(), expected.getCreator().getDisplayName(), "Annotation view had incorrect creator name");

    if (Arrays.asList(AnnotationType.FORMAL_CORRECTION, AnnotationType.MINOR_CORRECTION,
        AnnotationType.RETRACTION).contains(expected.getType())) {
      assertTrue(result.isCorrection(), "Result should have been created as a correction");
    } else {
      assertFalse(result.isCorrection(), "Result should not have been created as a correction");
    }

    if (expected.getAnnotationCitation() == null) {
      assertNull(result.getCitation(), "returned non-null citation when null was expected");
    } else {
      assertNotNull(result.getCitation(), "returned null citation when non-null was expected");
      assertEquals(result.getCitation().getTitle(), expected.getAnnotationCitation().getTitle(),
          "Returned citation with incorrect title");
      assertEquals(result.getCitation().geteLocationId(), expected.getAnnotationCitation().getELocationId(),
          "Returned citation with incorrect eLocationId");
      assertEquals(result.getCitation().getJournal(), expected.getAnnotationCitation().getJournal(),
          "Returned citation with incorrect journal");
      assertEquals(result.getCitation().getYear(), expected.getAnnotationCitation().getYear(),
          "Returned citation with incorrect year");

      assertEquals(result.getCitation().getVolume(), expected.getAnnotationCitation().getVolume(),
          "Returned citation with incorrect volume");
      assertEquals(result.getCitation().getIssue(), expected.getAnnotationCitation().getIssue(),
          "Returned citation with incorrect issue");
      assertEquals(result.getCitation().getSummary(), expected.getAnnotationCitation().getSummary(),
          "Returned citation with incorrect summary");
      assertEquals(result.getCitation().getNote(), expected.getAnnotationCitation().getNote(),
          "Returned citation with incorrect note");

      if (expected.getAnnotationCitation().getCollaborativeAuthors() != null) {
        assertEqualsNoOrder(result.getCitation().getCollabAuthors(), expected.getAnnotationCitation().getCollaborativeAuthors().toArray(),
            "Returned citation with incorrect collab authors");
      } else {
        assertTrue(ArrayUtils.isEmpty(result.getCitation().getCollabAuthors()),
            "Returned non-empty collab authors when empty was expected");
      }
      if (expected.getAnnotationCitation().getAuthors() != null) {
        assertNotNull(result.getCitation().getAuthors(), "Returned null authors when authors were expected");
        assertEquals(result.getCitation().getAuthors().length, expected.getAnnotationCitation().getAuthors().size(),
            "Returned incorrect number of authors");
        for (int i = 0; i < result.getCitation().getAuthors().length; i++) {
          AuthorView actualAuthor = result.getCitation().getAuthors()[i];
          CorrectedAuthor expectedAuthor = expected.getAnnotationCitation().getAuthors().get(i);
          assertEquals(actualAuthor.getGivenNames(), expectedAuthor.getGivenNames(),
              "Author " + (i + 1) + " had incorrect given names");
          assertEquals(actualAuthor.getSurnames(), expectedAuthor.getSurName(),
              "Author " + (i + 1) + " had incorrect surnames");
          assertEquals(actualAuthor.getSuffix(), expectedAuthor.getSuffix(),
              "Author " + (i + 1) + " had incorrect suffix");
        }
      }

    }
  }
}
