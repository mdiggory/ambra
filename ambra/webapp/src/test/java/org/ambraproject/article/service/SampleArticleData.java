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

package org.ambraproject.article.service;

import org.ambraproject.models.*;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Data provider class that returns an article's xml and the fully populated article
 *
 * @author Alex Kudlick Date: 6/8/11 <p/>                                       categories.add(category);
 *         <p/>
 *         org.ambraproject.article.service
 */
public class SampleArticleData {

  @DataProvider(name = "sampleArticle")
  public static Object[][] getSampleArticleData() throws Exception {
    File testFile = new File(SampleArticleData.class.getClassLoader().getResource("test-ingest.zip").toURI());
    ZipFile archive = new ZipFile(testFile);
    Article article = new Article();
    article.setDoi("info:doi/10.1371/journal.pmed.0050082");
    article.seteIssn("1549-1676");
    article.setArchiveName("test-ingest.zip");
    article.setUrl("http://dx.doi.org/10.1371%2Fjournal.pmed.0050082");

    article.setTitle("Exposure to War as a Risk Factor for Mental Disorders");
    article.setFormat("text/xml");
    article.setLanguage("en");
    article.setPublisherName("Public Library of Science");
    article.setRights("Benedek and Ursano. This is an open-access article distributed under the terms of the " +
        "Creative Commons Attribution License, which permits unrestricted use, distribution, and reproduction in any " +
        "medium, provided the original author and source are credited.");
    article.setDescription("<p>The authors discuss a new study on the prevalence of mental disorders in Lebanon.</p>");

    article.setVolume("5");
    article.setIssue("4");
    article.setTitle("Exposure to War as a Risk Factor for Mental Disorders");
    article.setPublisherLocation("San Francisco, USA");
    article.setPublisherName("Public Library of Science");
    article.setJournal("PLoS Med");

    article.setCitedArticles(getExpectedReferences());

    Set<Category> categories = new HashSet<Category>(2);
    Category category1 = new Category();
    category1.setMainCategory("Mental Health");
    categories.add(category1);
    Category category2 = new Category();
    category2.setMainCategory("Public Health and Epidemiology");
    categories.add(category2);
    article.setCategories(categories);

    Set<String> types = new HashSet<String>();
    types.add("http://rdf.plos.org/RDF/articleType/article-commentary");
    types.add("http://rdf.plos.org/RDF/articleType/Perspective");

    article.setTypes(types);

    List<ArticleAsset> assets = new ArrayList<ArticleAsset>(2);
    ArticleAsset xml = new ArticleAsset();
    xml.setExtension("XML");
    xml.setContentType("text/xml");
    xml.setSize(32124);
    xml.setDoi("info:doi/10.1371/journal.pmed.0050082");
    assets.add(xml);

    ArticleAsset pdf = new ArticleAsset();
    pdf.setExtension("PDF");
    pdf.setContentType("application/pdf");
    pdf.setDoi("info:doi/10.1371/journal.pmed.0050082");
    pdf.setSize(91325);
    assets.add(pdf);
    article.setAssets(assets);


    List<ArticleAuthor> authors = new ArrayList<ArticleAuthor>(2);
    ArticleAuthor author1 = new ArticleAuthor();
    author1.setFullName("David M Benedek");
    author1.setGivenNames("David M");
    author1.setSurnames("Benedek");
    author1.setSuffix("");
    authors.add(author1);
    ArticleAuthor author2 = new ArticleAuthor();
    author2.setFullName("Robert J Ursano");
    author2.setGivenNames("Robert J");
    author2.setSurnames("Ursano");
    author2.setSuffix("");
    authors.add(author2);

    article.setAuthors(authors);

    List<ArticleRelationship> relatedArticles = new ArrayList<ArticleRelationship>(1);
    ArticleRelationship relationship1 = new ArticleRelationship();
    relationship1.setOtherArticleDoi("info:doi/10.1371/journal.pmed.0050061");
    relationship1.setType("companion");
    relationship1.setParentArticle(article);
    relatedArticles.add(relationship1);

    ArticleRelationship relationship2 = new ArticleRelationship();
    relationship2.setOtherArticleDoi("info:doi/10.1371/journal.pbio.0000064");
    relationship2.setType("companion");
    relationship2.setParentArticle(article);
    relatedArticles.add(relationship2);

    article.setRelatedArticles(relatedArticles);

    return new Object[][]{
        {archive, article}
    };
  }

  @DataProvider(name = "alteredZip")
  public static Object[][] getAlteredZip() throws Exception {
    File original = new File(SampleArticleData.class.getClassLoader().getResource("altered-ingest-original.zip").toURI());
    File altered = new File(SampleArticleData.class.getClassLoader().getResource("altered-ingest-new.zip").toURI());
    ZipFile originalArchiveAddFiles = new ZipFile(original);
    ZipFile alteredArchiveAddFiles = new ZipFile(altered);
    List<String> newImageDois = new ArrayList<String>(39);
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g001");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g001");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g001");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g001");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g002");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g002");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g002");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g002");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g003");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g003");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g003");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g003");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g004");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g004");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g004");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g004");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g005");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g005");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g005");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g005");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g006");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g006");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g006");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g006");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g007");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g007");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g007");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g007");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g008");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g008");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g008");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.g008");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.s001");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.s002");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.s003");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.s004");
    newImageDois.add("info:doi/10.1371/journal.pgen.1002295.s005");

    File removedImgsAltered = new File(SampleArticleData.class.getClassLoader().getResource("ingest-remove-img-altered.zip").toURI());
    File removedImgsOrig = new File(SampleArticleData.class.getClassLoader().getResource("ingest-remove-img-orig.zip").toURI());

    LinkedList<String> secondList = new LinkedList<String>();
    secondList.add("info:doi/10.1371/journal.pmed.1001027");
    secondList.add("info:doi/10.1371/journal.pmed.1001027");

    return new Object[][]{
        {originalArchiveAddFiles, alteredArchiveAddFiles, newImageDois},
        {new ZipFile(removedImgsOrig), new ZipFile(removedImgsAltered), secondList}
    };
  }

  @DataProvider(name = "sampleAssets")
  public static Object[][] getSampleAssets() throws Exception {
    File testFile = new File(SampleArticleData.class.getClassLoader().getResource("test-ingest-with-parts.zip").toURI());
    ZipFile archive = new ZipFile(testFile);

    List<ArticleAsset> assets = new LinkedList<ArticleAsset>();
    ArticleAsset xml = new ArticleAsset();
    xml.setDoi("info:doi/10.1371/journal.pntd.0000241");
    xml.setExtension("XML");
    xml.setContentType("text/xml");
    xml.setContextElement("table-wrap");
    xml.setSize(106254);
    assets.add(xml);

    ArticleAsset pdf = new ArticleAsset();
    pdf.setDoi("info:doi/10.1371/journal.pntd.0000241");
    pdf.setExtension("PDF");
    pdf.setContentType("application/pdf");
    pdf.setContextElement("table-wrap");
    pdf.setSize(132178);
    assets.add(pdf);

    ArticleAsset asset1 = new ArticleAsset();
    asset1.setDoi("info:doi/10.1371/journal.pntd.0000241.t001");
    asset1.setExtension("TIF");
    asset1.setContentType("image/tiff");
    asset1.setContextElement("table-wrap");
    asset1.setSize(245880);
    assets.add(asset1);

    ArticleAsset asset2 = new ArticleAsset();
    asset2.setExtension("PNG_S");
    asset2.setContentType("image/png");
    asset2.setDoi("info:doi/10.1371/journal.pntd.0000241.t001");
    asset2.setContextElement("table-wrap");
    asset2.setSize(14433);
    assets.add(asset2);

    ArticleAsset asset3 = new ArticleAsset();
    asset3.setExtension("PNG_M");
    asset3.setContentType("image/png");
    asset3.setDoi("info:doi/10.1371/journal.pntd.0000241.t001");
    asset3.setContextElement("table-wrap");
    asset3.setSize(95902);
    assets.add(asset3);

    ArticleAsset asset4 = new ArticleAsset();
    asset4.setExtension("PNG_L");
    asset4.setContentType("image/png");
    asset4.setSize(138172);
    asset4.setDoi("info:doi/10.1371/journal.pntd.0000241.t001");
    asset4.setContextElement("table-wrap");
    assets.add(asset4);


    ArticleAsset asset5 = new ArticleAsset();
    asset5.setExtension("TIF");
    asset5.setContentType("image/tiff");
    asset5.setSize(400768);
    asset5.setDoi("info:doi/10.1371/journal.pntd.0000241.t002");
    asset5.setContextElement("table-wrap");
    assets.add(asset5);

    ArticleAsset asset6 = new ArticleAsset();
    asset6.setExtension("PNG_S");
    asset6.setContentType("image/png");
    asset6.setSize(18685);
    asset6.setDoi("info:doi/10.1371/journal.pntd.0000241.t002");
    asset6.setContextElement("table-wrap");
    assets.add(asset6);

    ArticleAsset asset7 = new ArticleAsset();
    asset7.setExtension("PNG_M");
    asset7.setContentType("image/png");
    asset7.setSize(161218);
    asset7.setDoi("info:doi/10.1371/journal.pntd.0000241.t002");
    asset7.setContextElement("table-wrap");
    assets.add(asset7);

    ArticleAsset asset8 = new ArticleAsset();
    asset8.setExtension("PNG_L");
    asset8.setContentType("image/png");
    asset8.setSize(227830);
    asset8.setDoi("info:doi/10.1371/journal.pntd.0000241.t002");
    asset8.setContextElement("table-wrap");
    assets.add(asset8);

    return new Object[][]{
        {archive, assets}
    };
  }

  private static List<CitedArticle> getExpectedReferences() {
    List<CitedArticle> references = new ArrayList<CitedArticle>(18);

    CitedArticle citation = new CitedArticle();
    citation.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation.setKey("1");
    citation.setYear(1991);
    citation.setDisplayYear("1991");
    citation.setTitle("Psychiatric disorders in America: The epidemiologic catchment area study");
    citation.setPublisherLocation("New York");
    citation.setPublisherName("Free Press");
    citation.setNote("editors");
    references.add(citation);


    CitedArticle citation2 = new CitedArticle();
    citation2.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation2.setKey("2");
    citation2.setYear(2000);
    citation2.setDisplayYear("2000");
    citation2.setVolume("34");
    citation2.setVolumeNumber(34);
    citation2.setTitle("Australia's mental health: An overview of the general population survey.");
    citation2.setPages("197-205");
    citation2.seteLocationID("197");
    citation2.setJournal("Aust NZ J Psychiatry");
    references.add(citation2);


    CitedArticle citation3 = new CitedArticle();
    citation3.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation3.setKey("3");
    citation3.setYear(1998);
    citation3.setDisplayYear("1998");
    citation3.setVolume("33");
    citation3.setVolumeNumber(33);
    citation3.setTitle("Prevalence of psychiatric disorder in the general population: Results of the Netherlands Mental Health Survey and Incidences Study (NEMESIS).");
    citation3.setPages("587-595");
    citation3.seteLocationID("587");
    citation3.setJournal("Soc Psychiatry Epidemiol");
    references.add(citation3);


    CitedArticle citation4 = new CitedArticle();
    citation4.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation4.setKey("4");
    citation4.setYear(2001);
    citation4.setDisplayYear("2001");
    citation4.setVolume("36");
    citation4.setVolumeNumber(36);
    citation4.setTitle("Al Ain Community Psychiatry Survey I. Prevalence and socio-demographic correlates.");
    citation4.setPages("20-28");
    citation4.seteLocationID("20");
    citation4.setJournal("Soc Psychiatr Epidemiol");

    references.add(citation4);

    CitedArticle citation5 = new CitedArticle();
    citation5.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation5.setKey("5");
    citation5.setYear(1989);
    citation5.setDisplayYear("1989");
    citation5.setVolume("155");
    citation5.setVolumeNumber(155);
    citation5.setTitle("Epidemiology of mental disorders in young adults of a newly urbanized area in Khartoum, Sudan.");
    citation5.setPages("44-47");
    citation5.seteLocationID("44");
    citation5.setJournal("Br J Psychiatry");

    references.add(citation5);

    CitedArticle citation6 = new CitedArticle();
    citation6.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation6.setKey("6");
    citation6.setYear(1998);
    citation6.setDisplayYear("1998");
    citation6.setVolume("248");
    citation6.setVolumeNumber(248);
    citation6.setTitle("Major depression and external stressors: The Lebanon War.");
    citation6.setPages("225-230");
    citation6.seteLocationID("225");
    citation6.setJournal("Eur Arch Psychiatry Clin Neurosci");

    references.add(citation6);

    CitedArticle citation7 = new CitedArticle();
    citation7.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation7.setKey("7");
    citation7.setYear(2002);
    citation7.setDisplayYear("2002");
    citation7.setVolume("92");
    citation7.setVolumeNumber(92);
    citation7.setTitle("Population attributable fractions of psychiatric disorders and behavioral outcomes associated with combat exposures among U.S. men.");
    citation7.setPages("59-63");
    citation7.seteLocationID("59");
    citation7.setJournal("Am J Public Health");

    references.add(citation7);

    CitedArticle citation8 = new CitedArticle();
    citation8.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation8.setKey("8");
    citation8.setYear(2004);
    citation8.setDisplayYear("2004");
    citation8.setVolume("351");
    citation8.setVolumeNumber(351);
    citation8.setTitle("Combat duty in Iraq and Afghanistan: Mental health problems and barriers to care.");
    citation8.setPages("13-22");
    citation8.seteLocationID("13");
    citation8.setJournal("N Engl J Med");

    references.add(citation8);

    CitedArticle citation9 = new CitedArticle();
    citation9.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation9.setKey("9");
    citation9.setYear(1980);
    citation9.setDisplayYear("1980");
    citation9.setTitle("Diagnostic and statistical manual");
    citation9.setPublisherLocation("Washington (D. C.)");
    citation9.setPublisherName("American Psychiatric Press");

    references.add(citation9);

    CitedArticle citation10 = new CitedArticle();
    citation10.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation10.setKey("10");
    citation10.setYear(1981);
    citation10.setDisplayYear("1981");
    citation10.setVolume("38");
    citation10.setVolumeNumber(38);
    citation10.setTitle("National Institute of Mental Health diagnostic interview schedule: Its history, characteristics and validity.");
    citation10.setPages("381-389");
    citation10.seteLocationID("381");
    citation10.setJournal("Arch Gen Psychiatry");

    references.add(citation10);

    CitedArticle citation11 = new CitedArticle();
    citation11.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation11.setKey("11");
    citation11.setYear(2004);
    citation11.setDisplayYear("2004");
    citation11.setVolume("13");
    citation11.setVolumeNumber(13);
    citation11.setTitle("The World Mental Health (WMH) survey initiative version of the World Health Organization (WHO) Composite International Diagnostic Interview (CIDI).");
    citation11.setPages("95-121");
    citation11.seteLocationID("95");
    citation11.setJournal("Int J Methods Psychiatr Res");

    references.add(citation11);

    CitedArticle citation12 = new CitedArticle();
    citation12.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation12.setKey("12");
    citation12.setYear(2007);
    citation12.setDisplayYear("2007");
    citation12.setTitle("Individual and community responses to disasters.");
    citation12.setPublisherLocation("Cambridge");
    citation12.setPublisherName("Cambridge University Press");
    citation12.setPages("3-26");
    citation12.seteLocationID("3");
    citation12.setNote("In");

    List<CitedArticleEditor> editors1 = new ArrayList<CitedArticleEditor>(4);
    CitedArticleEditor editor1_1 = new CitedArticleEditor();
    editor1_1.setFullName("RJ Ursano");
    editor1_1.setGivenNames("RJ");
    editor1_1.setSurnames("Ursano");
    editors1.add(editor1_1);

    CitedArticleEditor editor1_2 = new CitedArticleEditor();
    editor1_2.setFullName("CS Fullerton");
    editor1_2.setGivenNames("CS");
    editor1_2.setSurnames("Fullerton");
    editors1.add(editor1_2);
    CitedArticleEditor editor1_3 = new CitedArticleEditor();
    editor1_3.setFullName("L Weisaeth");
    editor1_3.setGivenNames("L");
    editor1_3.setSurnames("Weisaeth");
    editors1.add(editor1_3);

    CitedArticleEditor editor1_4 = new CitedArticleEditor();
    editor1_4.setFullName("B Raphael");
    editor1_4.setGivenNames("B");
    editor1_4.setSurnames("Raphael");
    editors1.add(editor1_4);

    citation12.setEditors(editors1);

    references.add(citation12);

    CitedArticle citation13 = new CitedArticle();
    citation13.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation13.setKey("13");
    citation13.setYear(2007);
    citation13.setDisplayYear("2007");
    citation13.setTitle("Armed conflicts report.");
    citation13.setNote("Available: http://www.ploughshares.ca/libraries/ACRText/ACR-TitlePageRev.htm. Accessed 29 February 2008");

    references.add(citation13);

    CitedArticle citation14 = new CitedArticle();
    citation14.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation14.setKey("14");
    citation14.setYear(2003);
    citation14.setDisplayYear("2003");
    citation14.setTitle("Preparing for the psychological consequences of terrorism: A public health strategy.");
    citation14.setNote("Available: http://www.nap.edu/catalog.php?record_id=10717. Accessed 29 February 2008");

    references.add(citation14);

    CitedArticle citation15 = new CitedArticle();
    citation15.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation15.setKey("15");
    citation15.setYear(2007);
    citation15.setDisplayYear("2007a");
    citation15.setTitle("Public health and disaster mental health: Preparing, responding and recovering.");
    citation15.setPublisherLocation("Cambridge");
    citation15.setPublisherName("Cambridge University Press");
    citation15.setPages("311-326");
    citation15.seteLocationID("311");
    citation15.setNote("In");

    List<CitedArticleEditor> editors2 = new ArrayList<CitedArticleEditor>();
    CitedArticleEditor editor2_1 = new CitedArticleEditor();
    editor2_1.setFullName("RJ Ursano");
    editor2_1.setGivenNames("RJ");
    editor2_1.setSurnames("Ursano");
    editors2.add(editor2_1);

    CitedArticleEditor editor2_2 = new CitedArticleEditor();
    editor2_2.setFullName("CS Fullerton");
    editor2_2.setGivenNames("CS");
    editor2_2.setSurnames("Fullerton");
    editors2.add(editor2_2);

    CitedArticleEditor editor2_3 = new CitedArticleEditor();
    editor2_3.setFullName("L Weisaeth");
    editor2_3.setGivenNames("L");
    editor2_3.setSurnames("Weisaeth");
    editors2.add(editor2_3);

    CitedArticleEditor editor2_4 = new CitedArticleEditor();
    editor2_4.setFullName("B Raphael");
    editor2_4.setGivenNames("B");
    editor2_4.setSurnames("Raphael");
    editors2.add(editor2_4);

    citation15.setEditors(editors2);

    references.add(citation15);

    CitedArticle citation16 = new CitedArticle();
    citation16.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation16.setKey("16");
    citation16.setYear(2008);
    citation16.setDisplayYear("2008");
    citation16.setVolume("5");
    citation16.setVolumeNumber(5);
    citation16.setTitle("Lifetime prevalence of mental disorders in Lebanon: First onset, treatment, and exposure to war.");
    citation16.setPages("e61");
    citation16.seteLocationID("e61");
    citation16.setJournal("PLoS Med");
    citation16.setNote("doi:10.1371/journal.pmed.0050061");

    references.add(citation16);

    CitedArticle citation17 = new CitedArticle();
    citation17.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Article");
    citation17.setKey("17");
    citation17.setYear(2007);
    citation17.setDisplayYear("2007");
    citation17.setVolume("6");
    citation17.setVolumeNumber(6);
    citation17.setTitle("Lifetime prevalence and age-of-onset distributions of mental disorders in the World Health Organization's World Mental Health Survey.");
    citation17.setPages("168-176");
    citation17.seteLocationID("168");
    citation17.setJournal("Initiative World Psychiatry");

    references.add(citation17);

    CitedArticle citation18 = new CitedArticle();
    citation18.setCitationType("http://purl.org/net/nknouf/ns/bibtex#Misc");
    citation18.setKey("18");
    citation18.setYear(2001);
    citation18.setDisplayYear("2001");
    citation18.setTitle("Global health atlas.");
    citation18.setNote("Available: http://www.who.int/globalatlas/DataQuery/default.asp. Accessed 29 February 2008");
    references.add(citation18);

    return references;
  }

}