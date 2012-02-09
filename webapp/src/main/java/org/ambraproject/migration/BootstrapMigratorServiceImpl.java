/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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
package org.ambraproject.migration;

import org.ambraproject.models.Version;
import org.apache.commons.configuration.Configuration;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.topazproject.ambra.configuration.ConfigurationStore;
import org.ambraproject.service.HibernateServiceImpl;

import org.hibernate.Transaction;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Does migrations on startup.
 *
 * Note we store version as integers to avoid floating point / decimal rounding issues with mysql and java
 * So just multiply release values by 100
 *
 * @author Joe Osowski
 */
public class BootstrapMigratorServiceImpl extends HibernateServiceImpl implements BootstrapMigratorService {
  private static Logger log = LoggerFactory.getLogger(BootstrapMigratorServiceImpl.class);

  private double dbVersion;
  private double binaryVersion;
  private boolean isSnapshot;

  /**
   * Apply all migrations.
   *
   * @throws Exception on an error
   */
  public void migrate() throws Exception {
    Configuration conf = ConfigurationStore.getInstance().getConfiguration();

    setVersionData();

    //If this is a snapshot, we're developing and we don't need to do this check
    if(isSnapshot == false) {
      //Throws an exception if the database version is further into
      //the future then this version of the ambra war
      if(binaryVersion < dbVersion) {
        log.error("Binary version: " + binaryVersion + ", DB version: " + dbVersion);
        throw new Exception("The ambra war is out of date with the database, " +
          "update this war file to the latest version.");
      }
    }

    waitForOtherMigrations();

    if(dbVersion < 220) {
      migrate210();
    }

    /*if(dbVersion < 222) {
      migrate201();
    }

    if(dbVersion < 223) {
      migrate202();
    }*/
  }

  /*
  * Run the migration for ambra 2.10 to 2.20
  **/
  private void migrate210() {
    log.info("Migration from 210 starting");
    //First create version table and add one row

    final boolean isSnapshot = this.isSnapshot;

    hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        log.debug("Creating new tables.");

        execSQLScript(session, "migrate_ambra_2_2_0_part1.sql");

        Version v = new Version();
        v.setName("Ambra 2.20");
        v.setVersion(220);
        v.setUpdateInProcess(true);
        session.save(v);

        log.debug("Tables created, now migrating data and removing old tables.");

        //We execute step #2 in a slightly different way as this file has SQL delimited in a different fashion
        //since it creates a trigger
        String sqlScript = "";
        try {
          log.debug("migrate_ambra_2_2_0_part2.sql started");
          sqlScript = getSQLScript("migrate_ambra_2_2_0_part2.sql");
          log.debug("migrate_ambra_2_2_0_part2.sql completed");
        } catch(IOException ex) {
          throw new HibernateException(ex.getMessage(), ex);
        }

        session.createSQLQuery(sqlScript).executeUpdate();

        execSQLScript(session, "migrate_ambra_2_2_0_part3.sql");

        //step 4 also creates a trigger, so we need to execute it the same as with step 2
        try {
          log.debug("migrate_ambra_2_2_0_part4.sql started");
          sqlScript = getSQLScript("migrate_ambra_2_2_0_part4.sql");
          log.debug("migrate_ambra_2_2_0_part4.sql completed");
        } catch(IOException ex) {
          throw new HibernateException(ex.getMessage(), ex);
        }
        session.createSQLQuery(sqlScript).executeUpdate();


        execSQLScript(session, "migrate_ambra_2_2_0_part5.sql");
        execSQLScript(session, "migrate_ambra_2_2_0_part6.sql");

        v.setUpdateInProcess(false);
        session.update(v);

        return null;
      }
    });

    log.info("Migration from 210 complete");
  }

  /*
  * Wait for other migrations to complete.  This will prevent two instances of ambra from attempting to execute the
  * same migration
  * */
  private void waitForOtherMigrations() throws InterruptedException
  {
    while(isMigrateRunning()) {
      log.debug("Waiting for another migration to complete.");
      Thread.sleep(10000);
    }
  }

  /*
  * Determine if a migration is already running
  **/
  private boolean isMigrateRunning()
  {
    return (Boolean)hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        SQLQuery q = session.createSQLQuery("show tables");
        List<String> tables = q.list();

        //Check to see if the version table exists.
        //If it does not exist then no migrations have been run yet
        if(!tables.contains("version")) {
          return false;
        }

        //If we get this far, return the version column out of the database
        Criteria c = session.createCriteria(Version.class)
          .setProjection(Projections.max("version"));

        int version = (Integer)c.uniqueResult();

        c = session.createCriteria(Version.class)
          .add(Restrictions.eq("version", version));

        Version v = (Version)c.uniqueResult();

        return (v == null)?false:v.getUpdateInProcess();
      }
    });
  }

  /*
  * Load a mysql script from a resource
  * */
  private static String getSQLScript(String filename) throws IOException {
    InputStream is = BootstrapMigratorServiceImpl.class.getResourceAsStream(filename);
    StringBuilder out = new StringBuilder();

    byte[] b = new byte[4096];
    for (int n; (n = is.read(b)) != -1;) {
      out.append(new String(b, 0, n));
    }

    return out.toString();
  }

  private static String[] getSQLCommands(String filename) throws IOException
  {
    String sqlString = getSQLScript(filename);
    ArrayList<String> sqlCommands = new ArrayList<String>();

    String sqlCommandsTemp[] = sqlString.split(";");

    for(String sqlCommand : sqlCommandsTemp)
    {
      if(sqlCommand.trim().length() > 0) {
        sqlCommands.add(sqlCommand);
      }
    }
    return sqlCommands.toArray(new String[0]);
  }

  private void setVersionData() throws IOException
  {
    setBinaryVersion();
    setDatabaseVersion();
  }

  /**
   * Get the current version of the binaries
   * <p/>
   * Assumptions about the version number: 
   *   Only contains single-digit integers between dots (e.g., 2.2.1.6.9.3)
   *
   * @return binary version
   * @throws IOException when the class loader fails
   */
  private void setBinaryVersion() throws IOException
  {
    InputStream is = BootstrapMigratorServiceImpl.class.getResourceAsStream("version.properties");

    Properties prop = new Properties();
    prop.load(is);

    String sVersion = (String)prop.get("version");

    if(sVersion.indexOf("-SNAPSHOT") > 0) {
      sVersion = sVersion.substring(0, sVersion.indexOf("-SNAPSHOT"));
      this.isSnapshot = true;
    }

    Double dVersion;

    //  If the version has multiple dots, then it cannot be directly parsed as a Double.
    String[] versionArray = sVersion.split("\\.");
    if (versionArray.length < 3) {  //  example version numbers: 2 and 2.2 and 2.46 and 3.65
      dVersion = Double.parseDouble(sVersion) * 100;  //         200   220     246      365
    } else {  //  example version numbers: 2.1.1 and 2.2.5.3 and
      dVersion = Double.parseDouble(versionArray[0] + "." + versionArray[1]) * 100;
      for (int i=2; i < versionArray.length ; i++) {
        dVersion = dVersion + Math.pow((new Double(versionArray[i])).doubleValue(), (new Double(1-i)).doubleValue());
      }
    }

    this.binaryVersion = dVersion.intValue();
  }

  /*
  * Get the current version of the database
  * */
  @SuppressWarnings("unchecked")
  private void setDatabaseVersion()
  {
    this.dbVersion = ((Integer)hibernateTemplate.execute(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        SQLQuery q = session.createSQLQuery("show tables");
        List<String> tables = q.list();

        //Check to see if the version table exists.
        //If it does not exist then it's ambra 2.00
        if(!tables.contains("version")) {
          return 210;
        }

        //If we get this far, return the version column out of the database
        Criteria c = session.createCriteria(Version.class)
          .setProjection(Projections.max("version"));

        Integer i = (Integer)c.uniqueResult();

        return (i == null)?210:c.uniqueResult();
      }
    }));
  }

  private void execSQLScript(Session session, String sqlScript) throws SQLException, HibernateException
  {
    log.debug("{} started.", sqlScript);
    String sqlStatements[] = { "" };

    Transaction transaction = session.getTransaction();

    try {
      sqlStatements = getSQLCommands(sqlScript);
    } catch(IOException ex) {
      throw new HibernateException(ex.getMessage(), ex);
    }

    transaction.begin();

    for(String sqlStatement : sqlStatements) {
      log.debug("Running: {}", sqlStatement);
      session.createSQLQuery(sqlStatement).executeUpdate();
    }

    transaction.commit();
    log.debug("{} completed.", sqlScript);
  }
}
