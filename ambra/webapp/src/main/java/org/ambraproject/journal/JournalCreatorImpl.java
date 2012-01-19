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
package org.ambraproject.journal;

import org.apache.commons.configuration.Configuration;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.service.HibernateServiceImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * A listener class for creating journals on startup. This is equivalent of executing the
 * createJournal.groovy script.
 *
 * @author Joe Osowski
 */
public class JournalCreatorImpl extends HibernateServiceImpl implements JournalCreator {
  private static Logger log = LoggerFactory.getLogger(JournalCreatorImpl.class);

  private Configuration configuration;

  /**
   * Create all configured journals.
   *
   * @throws Error to abort
   */
  public void createJournals() {
    try {
      createJournals(hibernateTemplate);
    } catch (Exception e) {
      throw new Error("A journal creation operation failed. Aborting ...", e);
    }
  }

  /**
   * Create all configured journals.
   *
   * @param template the hibernate template to use
   * @return the number of journals created/updated
   *
   * @throws org.topazproject.otm.OtmException on an error
   */
  private int createJournals(HibernateTemplate template) {
    @SuppressWarnings("unchecked")
    List<String> keys = configuration.getList("ambra.virtualJournals.journals");

    if ((keys == null) || (keys.size() == 0)) {
      log.info("No journals to create");

      return 0;
    }

    for (String key : keys)
      createJournal(template, key);

    return keys.size();
  }

  /**
   * Journal create the journal
   *
   * @param template the hibernate template to use
   * @param key the journal key
   * @throws org.topazproject.otm.OtmException on an error
   * @throws Error on a fatal error
   */
  private void createJournal(HibernateTemplate template, final String key) {

    template.execute(new HibernateCallback() {
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        log.info("Attempting create/update journal with key '" + key + "'");

        String cKey = "ambra.virtualJournals." + key + ".eIssn";
        String eIssn = configuration.getString(cKey);

        if (eIssn == null)
          throw new Error("Missing config entry '" + cKey + "'");

        String title = configuration.getString("ambra.virtualJournals." + key + ".description");

        @SuppressWarnings("unchecked")
        List<Journal> journals = session.createCriteria(Journal.class)
            .add(Restrictions.eq("key", key)).list();

        Journal journal;

        if (journals.size() != 0) {
          journal = journals.get(0);
        } else {
          journal = new Journal();
          journal.setKey(key);
        }

        if (title != null) {
          journal.setTitle(title);
        }
        journal.seteIssn(eIssn);

        //Generate the journal Id first so the dublin core can have a matching identifier
        session.saveOrUpdate(journal);

        if (journals.size() != 0)
          log.info("Updated journal with key '" + key + "'");
        else
          log.info("Created journal with key '" + key + "'");

        return null;
      }
    });
  }

  /**
   * Setter method for configuration. Injected through Spring.
   * @param configuration Ambra configuration
   */
  @Required
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
