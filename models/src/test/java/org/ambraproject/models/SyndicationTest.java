package org.ambraproject.models;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

/**
 * @author Alex Kudlick  11/17/11
 */
public class SyndicationTest extends BaseHibernateTest {

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testShouldFailOnNullDoi() {
    hibernateTemplate.save(new Syndication());
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueConstraint() {
    String doi = "doi";
    String target = "target";
    Syndication syndication1 = new Syndication();
    syndication1.setDoi(doi);
    syndication1.setTarget(target);

    Syndication syndication2 = new Syndication();
    syndication2.setDoi(doi);
    syndication2.setTarget(target);

    hibernateTemplate.save(syndication1);
    hibernateTemplate.save(syndication2); //should fail
  }

  @Test
  public void testSaveSyndication() {
    long testStart = new Date().getTime();
    Syndication syndication = new Syndication();
    syndication.setDoi("doi");
    syndication.setTarget("target");
    syndication.setStatus(Syndication.STATUS_FAILURE);
    syndication.setSubmissionCount(2);
    syndication.setLastSubmitTimestamp(new Date());

    Long id = (Long) hibernateTemplate.save(syndication);

    syndication = (Syndication) hibernateTemplate.get(Syndication.class, id);

    assertNotNull(syndication.getCreated(), "syndication didn't get create date set");
    assertEquals(syndication.getDoi(), "doi", "incorrect doi");
    assertEquals(syndication.getTarget(), "target", "incorrect target");
    assertEquals(syndication.getStatus(), Syndication.STATUS_FAILURE, "incorrect status");
    assertEquals(syndication.getSubmissionCount(), 2, "incorrect submission count");
    assertTrue(syndication.getLastSubmitTimestamp().getTime() >= testStart,
        "syndication didn't get last submit timestamp set correctly");
  }
}
