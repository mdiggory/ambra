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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Alex Kudlick 2/9/12
 */
public class UserProfileTest extends BaseHibernateTest {

  private static final Logger log = LoggerFactory.getLogger(UserProfileTest.class);

/*
  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullEmail() {
    UserProfile profile = new UserProfile();
    profile.setDisplayName("HarryPotter");

    hibernateTemplate.save(profile);
  }

  @Test(expectedExceptions = {HibernateSystemException.class})
  public void testSaveWithNullDisplayName() {
    UserProfile profile = new UserProfile();
    profile.setEmail("foo@bar.org");

    hibernateTemplate.save(profile);
  }
*/

  @Test
  public void testSetAlertsFromCollection() {
    UserProfile user = new UserProfile();
    user.setAlertsList(Arrays.asList("foo", "bar"));
    assertEquals(user.getAlertsJournals(), "foo,bar", "didn't get alerts set correctly");
    user.setAlertsJournals(null);
    assertEquals(user.getAlertsList().size(), 0, "didn't handle null alerts string");
  }
  
  @Test
  public void testGetWeeklyAlerts() {
    UserProfile user = new UserProfile();
    user.setAlertsJournals("foo" + UserProfile.WEEKLY_ALERT_SUFFIX + UserProfile.ALERTS_SEPARATOR + "bar");
    List<String> expectedAlerts = new ArrayList<String>(1);
    expectedAlerts.add("foo");
    assertEquals(user.getWeeklyAlerts(), expectedAlerts, "User didn't return correct alerts");

    user.setAlertsJournals(null);
    assertEquals(user.getWeeklyAlerts().size(), 0, "didn't handle null alerts string");
  }

  @Test
  public void testGetMonthlyAlerts() {
    UserProfile user = new UserProfile();
    user.setAlertsJournals("foo" + UserProfile.MONTHLY_ALERT_SUFFIX + UserProfile.ALERTS_SEPARATOR + "bar");
    List<String> expectedAlerts = new ArrayList<String>(1);
    expectedAlerts.add("foo");
    assertEquals(user.getMonthlyAlerts(), expectedAlerts, "User didn't return correct alerts");

    user.setAlertsJournals(null);
    assertEquals(user.getMonthlyAlerts().size(), 0, "didn't handle null alerts string");
  }


  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueEmailConstraint() {
    UserProfile profile1 = new UserProfile();
    profile1.setEmail("foo@bar.org");
    profile1.setDisplayName("FooBar");

    UserProfile profile2 = new UserProfile();
    profile2.setEmail("foo@bar.org");
    profile2.setDisplayName("FooBare");

    hibernateTemplate.save(profile1);
    hibernateTemplate.save(profile2);
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueDisplayNameConstraint() {
    UserProfile profile1 = new UserProfile();
    profile1.setEmail("foo@ambraproject.org");
    profile1.setDisplayName("ambra");

    UserProfile profile2 = new UserProfile();
    profile2.setEmail("foo2@ambraproject.org");
    profile2.setDisplayName("ambra");

    hibernateTemplate.save(profile1);
    hibernateTemplate.save(profile2);
  }

  @Test(expectedExceptions = {DataIntegrityViolationException.class})
  public void testUniqueAuthIdConstraint() {
    UserProfile profile1 = new UserProfile();
    profile1.setAuthId("test-authId1");

    UserProfile profile2 = new UserProfile();
    profile2.setAuthId("test-authId1");

    hibernateTemplate.save(profile1);
    hibernateTemplate.save(profile2);
  }

  @Test
  public void testSaveProfile() {
    UserRole role = new UserRole();
    role.setRoleName("test-role-for-profile");
    hibernateTemplate.save(role);

    final UserProfile profile = new UserProfile();
    profile.setEmail("deltron@3030.com");
    profile.setDisplayName("Deltron3030");
    profile.setBiography("Foucault is best known for his critical studies of social institutions, most notably " +
        "psychiatry, medicine, the human sciences and the prison system, as well as for his work on the history " +
        "of human sexuality. His writings on power, knowledge, and discourse have been widely influential in " +
        "academic circles. In the 1960s Foucault was associated with structuralism, a movement from which he " +
        "distanced himself. Foucault also rejected the poststructuralist and postmodernist labels later attributed " +
        "to him, preferring to classify his thought as a critical history of modernity rooted in Immanuel Kant. " +
        "Foucault's project was particularly influenced by Nietzsche, his \"genealogy of knowledge\" being a direct " +
        "allusion to Nietzsche's \"genealogy of morality\". In a late interview he definitively stated: " +
        "\"I am a Nietzschean.\"[1]");
    profile.setAlertsJournals("foo1,foo2");
    profile.setRoles(new HashSet<UserRole>(1));
    profile.getRoles().add(role);

    final Serializable id = hibernateTemplate.save(profile);
    final List<String> expectedAlerts = new ArrayList<String>(2);
    expectedAlerts.add("foo1");
    expectedAlerts.add("foo2");

    //lazy login collection means we need to test inside of a session
    hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        UserProfile storedProfile = (UserProfile) session.get(UserProfile.class, id);

        assertEquals(storedProfile.getEmail(), profile.getEmail(), "storedProfile had incorrect email");
        assertEquals(storedProfile.getDisplayName(), profile.getDisplayName(), "storedProfile had incorrect display name");
        assertEquals(storedProfile.getBiography(), profile.getBiography(), "storedProfile had incorrect biography");
        assertEquals(storedProfile.getRoles().size(), 1, "Didn't save user role");
        assertEquals(storedProfile.getAlertsJournals(), profile.getAlertsJournals(), "saved user didn't have correct alerts text");
        assertEquals(storedProfile.getAlertsList(), expectedAlerts, "saved user didn't return correct alerts list");

        return null;
      }
    });
  }

  @Test(expectedExceptions = {InvalidDataAccessApiUsageException.class})
  public void testDoesNotCascadeToRoles() {
    UserProfile profile = new UserProfile();
    profile.setEmail("emailForRoleTest");
    profile.setDisplayName("nameForRoleTest");
    profile.setRoles(new HashSet<UserRole>(1));
    UserRole role = new UserRole();
    role.setRoleName("role that should not be added to db");
    profile.getRoles().add(role);
    hibernateTemplate.save(profile);
  }
}
