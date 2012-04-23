package org.ambraproject.trackback;

import org.ambraproject.BaseHttpTest;
import org.ambraproject.models.Article;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 4/2/12
 */
public class TrackbackHttpTest extends BaseHttpTest {

  @Autowired
  protected TrackbackService trackbackService;

  @Test
  @DirtiesContext
  public void testValidBlog() throws InterruptedException, IOException {
    final String doi = "id:test-doi";
    final String encodedDoi = "id%3Atest-doi";
    Article article = new Article(doi);
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://journal.org/article/" + doi + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, doi),
        "Trackback service didn't return true with unencoded doi");
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, encodedDoi),
        "Trackback service didn't return true with encoded doi");
  }

  @Test
  @DirtiesContext
  public void testValidBlogWithWwwLink() throws InterruptedException, IOException {
    final String doi = "id:test-doi";
    final String encodedDoi = "id%3Atest-doi";
    Article article = new Article(doi);
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://www.journal.org/article/" + doi + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, doi),
        "Trackback service didn't return true with unencoded doi");
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, encodedDoi),
        "Trackback service didn't return true with encoded doi");
  }

  @Test
  @DirtiesContext
  public void testValidBlogEncodedDoi() throws InterruptedException, IOException {
    final String doi = "id:test-doi";
    final String encodedDoi = "id%3Atest-doi";

    Article article = new Article(doi);
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);

    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head>" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://journal.org/article/" + encodedDoi + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, doi),
        "Trackback service didn't return true with undencoded doi");
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, encodedDoi),
        "Trackback service didn't return with encoded doi");
  }

  @Test
  @DirtiesContext
  public void testValidBlogEncodedDoiWwwLink() throws InterruptedException, IOException {
    final String doi = "id:test-doi";
    final String encodedDoi = "id%3Atest-doi";

    Article article = new Article(doi);
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);

    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head>" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://www.journal.org/article/" + encodedDoi + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, doi),
        "Trackback service didn't return true with undencoded doi");
    assertTrue(trackbackService.blogLinksToArticle(endpointUrl, encodedDoi),
        "Trackback service didn't return with encoded doi");
  }

  @Test
  @DirtiesContext
  public void testInvalidBlog() throws InterruptedException, IOException {
    Article article = new Article("id:foo");
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);

    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>" +
            "<head><title>My Cool Blog</title></head" +
            "<body>" +
            "<p>A cool blog with no link to the article.</p>" +
            "</body>" +
            "</html>");
      }
    });
    assertFalse(trackbackService.blogLinksToArticle(endpointUrl, article.getDoi()),
        "Trackback service didn't return false for invalid blog");
  }

  @Test
  @DirtiesContext
  public void testBlogLinksToOtherSite() throws InterruptedException, IOException {
    final String doi = "id:test-doi";

    Article article = new Article(doi);
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);

    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head>" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://somejournal.net/article/" + doi + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });
    assertFalse(trackbackService.blogLinksToArticle(endpointUrl, doi),
        "Link to other site should't have be considered valid");
  }
}
