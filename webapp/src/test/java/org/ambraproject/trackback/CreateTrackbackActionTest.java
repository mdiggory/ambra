package org.ambraproject.trackback;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.ambraproject.BaseHttpTest;
import org.ambraproject.BaseWebTest;
import org.ambraproject.models.Article;
import org.ambraproject.models.Trackback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Normally, a test for an action class should extend {@link org.ambraproject.BaseWebTest}, but since the trackback
 * action makes http requests to verify the blog, we need the {@link BaseHttpTest#httpEndpoint} to be set up
 *
 * @author Alex Kudlick 4/3/12
 */
public class CreateTrackbackActionTest extends BaseHttpTest {

  @Autowired
  protected CreateTrackbackAction action;

  @BeforeClass
  public void setupRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");

    ConfigurationManager configurationManager = new ConfigurationManager();
    configurationManager.addContainerProvider(new XWorkConfigurationProvider());
    Configuration config = configurationManager.getConfiguration();
    Container strutsContainer = config.getContainer();

    ValueStack stack = strutsContainer.getInstance(ValueStackFactory.class).createValueStack();
    stack.getContext().put(ServletActionContext.CONTAINER, strutsContainer);

    ActionContext.setContext(new ActionContext(stack.getContext()));
    ServletActionContext.setContext(ActionContext.getContext());
    ServletActionContext.setRequest(request);
  }

  @BeforeMethod
  public void resetAction() {
    action.setDoi(null);
    action.setTitle(null);
    action.setExcerpt(null);
    action.setUrl(null);
    action.setError(0);
    action.setErrorMessage(null);
    action.setRequest(BaseWebTest.getDefaultRequestAttributes());
  }

  @Test
  @DirtiesContext
  public void testExecuteWithValidBlog() throws Exception {
    final Article article = new Article("id:article-for-createTrackbackActionTest");
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);
    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head>" +
            "<body>\n" +
            "<p>A cool blog with a <a href=\"http://www.journal.org/article/" + article.getDoi() + "\">link</a> to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });

    action.setDoi(article.getDoi()); //trackback id is actually the doi
    action.setUrl(endpointUrl);
    action.setExcerpt("Hello this is an excerpt");
    action.setBlog_name("My Cool Blog");
    action.setTitle("A Cool Blog");

    String result = action.execute();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");

    assertEquals(action.getError(), 0, "Action returned an error");
    assertNull(action.getErrorMessage(),"Action returned an error message");

    Trackback storedTrackback = null;
    for (Trackback t : dummyDataStore.getAll(Trackback.class)) {
      if (t.getArticleID().equals(article.getID()) && t.getUrl().equals(endpointUrl)) {
        storedTrackback = t;
        break;
      }
    }
    assertNotNull(storedTrackback, "action didn't store a trackback");
    assertEquals(storedTrackback.getBlogName(), action.getBlog_name(), "Stored trackback had incorrect blog name");
    assertEquals(storedTrackback.getExcerpt(), action.getExcerpt(), "stored trackback had incorrect excerpt");
    assertEquals(storedTrackback.getTitle(), action.getTitle(), "stored trackback had incorrect title");

    //try to store the trackback again
    result = action.execute();
    assertEquals(result, Action.ERROR, "Action didn't return error when executing with duplicate trackback");
    assertEquals(action.getError(), 1, "Action didn't return error when executing with duplicate trackback");
    assertNotNull(action.getErrorMessage(), "Action didn't return error message when executing with duplicate trackback");

  }

  @Test
  @DirtiesContext
  public void testExecuteWithInValidBlog() throws Exception {
    final Article article = new Article("id:article-for-createTrackbackActionTestWithInvalidBlog");
    article.seteIssn(defaultJournal.geteIssn());
    dummyDataStore.store(article);

    httpEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody("<html>\n" +
            "<head><title>My Cool Blog</title></head>" +
            "<body>\n" +
            "<p>A cool blog with a no to the article.\n</p>" +
            "</body>\n" +
            "</html>");
      }
    });

    action.setDoi(article.getDoi()); //trackback id is actually the doi
    action.setUrl(endpointUrl);
    action.setExcerpt("Hello this is an excerpt");
    action.setBlog_name("My Cool Blog");
    action.setTitle("A Cool Blog");

    String result = action.execute();
    assertEquals(result, Action.ERROR, "Action didn't return error");

    assertEquals(action.getError(), 1, "Action didn't return an error");
    assertNotNull(action.getErrorMessage(),"Action didn't return an error message");

    Trackback storedTrackback = null;
    for (Trackback t : dummyDataStore.getAll(Trackback.class)) {
      if (t.getArticleID().equals(article.getID()) && t.getUrl().equals(endpointUrl)) {
        storedTrackback = t;
        break;
      }
    }
    assertNull(storedTrackback, "action created a trackback with an invalid blog");
  }

}
