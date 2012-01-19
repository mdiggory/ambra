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

package org.ambraproject.admin.service;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseTest;
import org.topazproject.ambra.models.ArticleContributor;
import org.topazproject.ambra.models.Citation;
import org.topazproject.ambra.models.UserProfile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Dragisa Krsmanovic
 * @author Joe Osowski
 */
public class CitationServiceTest extends BaseTest {

  @Autowired
  protected CitationService citationService;
  @Autowired
  protected SessionFactory sessionFactory;

  @DataProvider(name = "storedCitation")
  public Object[][] getStoredCitation() {
    Citation citation = new Citation();
    citation.setTitle("Citation title");
    citation.setDisplayYear("1999");
    citation.setJournal("PLoS ONE");
    citation.setVolume("01");
    citation.setIssue("01");
    citation.setELocationId("132");
    citation.setDoi("info:doi/10.1371/journal.fake.doi1234");

    List<String> collabAuthors = new ArrayList<String>(2);
    collabAuthors.add("John P. Smith");
    collabAuthors.add("The Heritage Foundation");
    citation.setCollaborativeAuthors(collabAuthors);

    UserProfile author = new UserProfile();
    author.setSurnames("Borges");
    author.setGivenNames("Jorge Luis");
    author.setSuffix("Esquire");
    dummyDataStore.store(author);
    ArrayList<UserProfile> authors = new ArrayList<UserProfile>();
    authors.add(author);
    citation.setAuthors(authors);

    ArticleContributor ac = new ArticleContributor();
    ac.setSurnames("Doe");
    ac.setGivenNames("John");
    ac.setSuffix("Mr");
    dummyDataStore.store(ac);
    ArrayList<ArticleContributor> annotationAuthors = new ArrayList<ArticleContributor>();
    annotationAuthors.add(ac);
    citation.setAnnotationArticleAuthors(annotationAuthors);
    
    citation.setId(URI.create(dummyDataStore.store(citation)));

    return new Object[][]{
        {reload(citation)} //Reload because tests change the citation stored in the db
    };
  }

  /**
   * Helper method to access Citations from the database.  Basically just session.get()
   *
   * @param id the id of the citation to get
   * @return the citation with the given id. Null if none exists
   */
  private Citation getCitation(URI id) {
    Session session = sessionFactory.openSession();
    Citation obj= (Citation) session.get(Citation.class, id);
    for(int i =0; i < obj.getAnnotationArticleAuthors().size(); i++) {
      obj.getAnnotationArticleAuthors().get(i);
    }
    session.close();
    return obj;
  }
  /**
   * Helper method to access UserProfiles from the database.  Basically just session.get()
   *
   * @param id the id of the user profile to get
   * @return the user profile with the given id. Null if none exists
   */
  private UserProfile getUserProfile(URI id) {
    Session session = sessionFactory.openSession();
    UserProfile obj= (UserProfile) session.get(UserProfile.class, id);
    session.close();
    return obj;
  }

  private ArticleContributor getArticleContributor(URI id) {
    Session session = sessionFactory.openSession();
    ArticleContributor ac = (ArticleContributor) session.get(ArticleContributor.class, id);
    session.close();
    return ac;
  }

  /**
   * Helper method to reload the citation with values from the db
   * @param citation the citation to reload
   * @return the citation object with values from the database
   */
  private Citation reload(Citation citation) {
    return getCitation(citation.getId());
  }


  @Test(dataProvider = "storedCitation")
  public void testUpdateCitation(Citation original) {
    String title = "title2";
    String year = "year2";
    String journal = "journal2";
    String volume = "volume2";
    String issue = "issue2";
    String eLocationId = "eLocationID2";
    String doi = "doi2";

    citationService.updateCitation(
        original.getId().toString(),
        title,
        year,
        journal,
        volume,
        issue,
        "  " + eLocationId + "  ",
        doi);
    Citation citation = reload(original);
    assertEquals(citation.getTitle(), title, "Citation title didn't updated");
    assertEquals(citation.getDisplayYear(), year, "Citation display year didn't get updated");
    assertEquals(citation.getVolume(), volume, "Citation volume didn't get updated");
    assertEquals(citation.getIssue(), issue, "Citation issue didn't get updated");
    assertEquals(citation.getJournal(), journal, "Citation journal didn't get updated");
    assertEquals(citation.getELocationId(), eLocationId, "citation eLocationId didn't get updated");
    assertEquals(citation.getDoi(), doi, "Citation doi didn't get updated");
  }


  @Test(expectedExceptions = {HibernateException.class})
  public void testUpdateCitationThatDoesNotExist() {
    String badCitationId = "citation-bad-id";
    String title = "title2";
    String year = "year2";
    String journal = "journal2";
    String volume = "volume2";
    String issue = "issue2";
    String eLocationId = "eLocationID2";
    String doi = "doi2";

    citationService.updateCitation(badCitationId, title, year, journal, volume, issue, "  " + eLocationId + "  ", doi);
  }


  @Test(dataProvider = "storedCitation")
  public void testAddAuthor(Citation citation) {
    int sizeBefore = citation.getAuthors().size();

    String surnames = "New Surname";
    String givenNames = "NewGivenName";
    String suffix = "NewSuffix";

    String authorId = citationService.addAuthor(citation.getId().toString(), surnames, givenNames, suffix);

    UserProfile newAuthor = getUserProfile(URI.create(authorId));

    assertNotNull(newAuthor, "new author didn't get stored to the database");
    assertEquals(newAuthor.getSurnames(), surnames,"stored author didn't have correct surnames");
    assertEquals(newAuthor.getGivenNames(), givenNames,"stored author didn't have correct given names");
    assertEquals(newAuthor.getSuffix(), suffix,"stored author didn't have correct suffix");

    citation = reload(citation);

    assertEquals(citation.getAuthors().size(), sizeBefore + 1,"Citation didn't get updated with new author");

    //Check that the author is attached to the citation
    for (UserProfile p : citation.getAuthors()) {
      if (p.getId().equals(newAuthor.getId())) {
        assertEquals(newAuthor.getSurnames(), p.getSurnames(),
            "Author attached to citation didn't have correct surnames");
        assertEquals(newAuthor.getGivenNames(), p.getGivenNames(),
            "Author attached to citation didn't have correct given names");
        assertEquals(newAuthor.getSuffix(), p.getSuffix(),
            "Author attached to citation didn't have correct suffix");

        return;
      }
    }

    fail("New author not found as part of authors collection associated with citation.");
  }


  @Test(dataProvider = "storedCitation")
  public void testDeleteAuthor(Citation citation) {
    UserProfile author1 = citation.getAuthors().get(0);
    String authorId = author1.getId().toString();
    int sizeBefore = citation.getAuthors().size();

    citationService.deleteAuthor(citation.getId().toString(), authorId);

    citation = reload(citation);

    assertEquals(citation.getAuthors().size(), sizeBefore - 1);
    assertFalse(citation.getAuthors().contains(author1));
  }

  @Test(dataProvider = "storedCitation",expectedExceptions = {HibernateException.class})
  public void testDeleteAuthorThatDoesNotExist(Citation citation) {
    String authorId = "WrongID";

    citationService.deleteAuthor(citation.getId().toString(), authorId);
  }

  @DataProvider(name = "dummyAuthor")
  public Object[][] getDummyAuthor() {
    Citation citation = (Citation) getStoredCitation()[0][0];
    UserProfile author = new UserProfile();
    author.setId(URI.create("id:not-in-citation"));
    String userId = dummyDataStore.store(author);
    return new Object[][]{
        {citation.getId().toString(), userId}
    };
  }

  @DataProvider(name = "dummyAnnotationAuthor")
  public Object[][] getDummyAnnotationAuthor() {
    Citation citation = (Citation) getStoredCitation()[0][0];
    ArticleContributor author = new ArticleContributor();
    author.setGivenNames("oldGivenName");
    author.setSurnames("oldSurName");
    author.setSuffix("oldSuffix");
    String userId = dummyDataStore.store(author);
    return new Object[][]{
        {citation.getId().toString(), userId}
    };
  }

  @Test(dataProvider = "dummyAuthor", expectedExceptions = {HibernateException.class},
      dependsOnMethods = "testUpdateAuthor", alwaysRun = true)
  public void testDeleteAuthorThatIsNotInTheCitation(String citationId, String authorId) {
    citationService.deleteAuthor(citationId, authorId);
  }


  @Test(dataProvider = "dummyAuthor")
  public void testUpdateAuthor(String notUsed, String userId) {
    assertNotNull(getUserProfile(URI.create(userId)),"DataProvider didn't store user to the database");
    String surnames = "New Surname";
    String givenNames = "NewGivenName";
    String suffix = "         ";

    citationService.updateAuthor(userId, " " + surnames + " ", givenNames, suffix);

    UserProfile author = getUserProfile(URI.create(userId));

    assertNotNull(author,"Author wasn't stored to the database");
    assertEquals(author.getSurnames(), surnames,"Author didn't get surnames updated");
    assertEquals(author.getGivenNames(), givenNames,"Author didn't get given names updated");
    assertNull(author.getSuffix(),"Author didn't get suffix updated");
  }


  @Test(dataProvider = "storedCitation")
  public void testAddCollaborativeAuthor(Citation citation) {
    int sizeBefore = citation.getCollaborativeAuthors().size();

    String newCollabAuthor = "newCollabAuthor";
    citationService.addCollaborativeAuthor(citation.getId().toString(), newCollabAuthor);

    citation = reload(citation);

    assertEquals(citation.getCollaborativeAuthors().size(), sizeBefore + 1,
        "Collaborative author didn't get added to citation");
    assertTrue(citation.getCollaborativeAuthors().contains(newCollabAuthor),
        "Collaborative author didn't get added to citation");
  }


  @Test(dataProvider = "storedCitation")
  public void testDeleteCollaborativeAuthor(Citation citation) {
    int sizeBefore = citation.getCollaborativeAuthors().size();
    String author = citation.getCollaborativeAuthors().get(0);

    citationService.deleteCollaborativeAuthor(citation.getId().toString(), 0);

    citation = reload(citation);

    assertEquals(citation.getCollaborativeAuthors().size(), sizeBefore - 1, "Collaborative author didn't get deleted");
    assertFalse(citation.getCollaborativeAuthors().contains(author),"Collaborative author didn't get deleted");
  }

  @Test(dataProvider = "storedCitation")
  public void testUpdateCollaborativeAuthor(Citation citation) {
    int updatingIndex = 0;
    int sizeBefore = citation.getCollaborativeAuthors().size();

    String oldAuthorValue = citation.getCollaborativeAuthors().get(updatingIndex);
    String newAuthorValue = "UpdatedAuthor";

    citationService.updateCollaborativeAuthor(citation.getId().toString(), updatingIndex, " " + newAuthorValue + " ");

    citation = reload(citation);

    String dbAuthorValue = citation.getCollaborativeAuthors().get(updatingIndex);

    assertEquals(citation.getCollaborativeAuthors().size(), sizeBefore,
        "Updated citation lost or gained a collaborative author");

    assertNotSame(oldAuthorValue, newAuthorValue,"Updated author changed place in citation's list");
    assertEquals(dbAuthorValue, newAuthorValue, "Collab Author didn't get updated");
  }

  @Test(dataProvider = "storedCitation")
  public void testAddAnnotationAuthor(Citation citation) {
    int sizeBefore = citation.getAnnotationArticleAuthors().size();

    String surnames = "New Surname";
    String givenNames = "NewGivenName";
    String suffix = "NewSuffix";

    String authorId = citationService.addAnnotationAuthor(citation.getId().toString(), surnames, givenNames, suffix);

    ArticleContributor newAuthor = getArticleContributor(URI.create(authorId));

    assertNotNull(newAuthor, "new author didn't get stored to the database");
    assertEquals(newAuthor.getSurnames(), surnames,"stored author didn't have correct surnames");
    assertEquals(newAuthor.getGivenNames(), givenNames,"stored author didn't have correct given names");
    assertEquals(newAuthor.getSuffix(), suffix,"stored author didn't have correct suffix");

    citation = reload(citation);

    assertEquals(citation.getAnnotationArticleAuthors().size(), sizeBefore + 1,"Citation didn't get updated with new author");

    //Check that the author is attached to the citation
    for (ArticleContributor p : citation.getAnnotationArticleAuthors()) {
      if (p.getId().equals(newAuthor.getId())) {
        assertEquals(newAuthor.getSurnames(), p.getSurnames(),
            "Author attached to citation didn't have correct surnames");
        assertEquals(newAuthor.getGivenNames(), p.getGivenNames(),
            "Author attached to citation didn't have correct given names");
        assertEquals(newAuthor.getSuffix(), p.getSuffix(),
            "Author attached to citation didn't have correct suffix");

        return;
      }
    }

    fail("New author not found as part of authors collection associated with citation.");
  }

  @Test(dataProvider = "dummyAnnotationAuthor")
  public void testUpdateAnnotationAuthor(String notUsed, String userId) {
    assertNotNull(getArticleContributor(URI.create(userId)),"DataProvider didn't store user to the database");
    String surnames = "New Surname";
    String givenNames = "NewGivenName";
    String suffix = "         ";

    citationService.updateAnnotationAuthor(userId, " " + surnames + " ", givenNames, suffix);

    ArticleContributor author = getArticleContributor(URI.create(userId));

    assertNotNull(author,"Author wasn't stored to the database");
    assertEquals(author.getSurnames(), surnames,"Author didn't get surnames updated");
    assertEquals(author.getGivenNames(), givenNames,"Author didn't get given names updated");
    assertNull(author.getSuffix(),"Author didn't get suffix updated");
  }

  @Test(dataProvider = "storedCitation")
  public void testDeleteAnnotationAuthor(Citation citation) {
    ArticleContributor author1 = citation.getAnnotationArticleAuthors().get(0);
    String authorId = author1.getId().toString();
    int sizeBefore = citation.getAnnotationArticleAuthors().size();

    citationService.deleteAnnotationAuthor(citation.getId().toString(), authorId);

    citation = reload(citation);

    assertEquals(citation.getAnnotationArticleAuthors().size(), sizeBefore - 1);
    assertFalse(citation.getAnnotationArticleAuthors().contains(author1));
  }
}
