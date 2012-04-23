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
package org.ambraproject.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;


/**
 * Tool for generating ddl from hibernate config.  Inspiration taken from <a href="http://jandrewthompson.blogspot.com/2009/10/how-to-generate-ddl-scripts-from.html">this
 * blog</a>
 *
 * @author Alex Kudlick Date: 6/2/11
 *         <p/>
 *         org.ambraproject.service
 */
public class SchemaGenerator {
  private Configuration configuration;
  private boolean updateSchema;
  private String outputDir;

  /**
   * Create a new schema generator
   *
   * @param updateSchema - true if the tables in the schema already exist and need to be dropped, false to create the
   *                     schema from scratch
   */
  public SchemaGenerator(boolean updateSchema, String outputDir) {
    this.configuration = new Configuration().configure();
    configuration.setProperty("format_sql", "true");
    this.updateSchema = updateSchema;
    this.outputDir = outputDir;
  }

  /**
   * Create a new schema generator that won't drop tables
   */
  public SchemaGenerator() {
    this(false, new File("").getAbsolutePath());
  }

  /**
   * Generate the sql for creating the schema
   *
   * @param dialect - Database dialect to use
   */
  public void generateSQL(Dialect dialect) {
    configuration.setProperty("hibernate.dialect", dialect.getDialectClass());

    SchemaExport export = new SchemaExport(configuration);
    export.setDelimiter(";");
    String outputFile = this.outputDir + File.separator + "ddl_" + dialect.name().toLowerCase() + ".sql";
    export.setOutputFile(outputFile);
    export.execute(false, false, false, !updateSchema);
  }

  /**
   * Run the schema creation script
   *
   * @param jdbcUrl  - the jdbc url for the database in which to run the script
   * @param dialect- the sql dialect for the database
   * @param username - the username for the database
   * @param password - the password to use
   */
  public void createSchema(String jdbcUrl, Dialect dialect, String username, String password) {
    configuration.setProperty("connection.url", jdbcUrl);
    configuration.setProperty("connection.username", username);
    configuration.setProperty("connection.password", password);
    configuration.setProperty("dialect", dialect.getDialectClass());
    configuration.setProperty("connection.driver_class", dialect.getDriverClass());
    SchemaExport export = new SchemaExport(configuration);
    export.setDelimiter(";");
    export.execute(false, true, false, !updateSchema);
  }

  /**
   * Holds the classnames of hibernate dialects for easy reference.
   */
  public static enum Dialect {
    ORACLE("org.hibernate.dialect.Oracle10gDialect", "oracle.jdbc.driver.OracleDriver"),
    MYSQL("org.hibernate.dialect.MySQLDialect", "com.mysql.jdbc.Driver"),
    HSQL("org.hibernate.dialect.HSQLDialect", "org.hsqldb.jdbcDriver");

    private String dialectClass;
    private String driverClass;

    private Dialect(String dialectClass, String driverClass) {
      this.dialectClass = dialectClass;
      this.driverClass = driverClass;
    }

    public String getDialectClass() {
      return dialectClass;
    }

    public String getDriverClass() {
      return driverClass;
    }

    public static Dialect fromString(String arg) {
      if ("oracle".equals(arg.toLowerCase())) {
        return ORACLE;
      } else if ("mysql".equals(arg.toLowerCase())) {
        return MYSQL;
      } else if ("hsql".equals(arg.toLowerCase())) {
        return HSQL;
      }
      throw new IllegalArgumentException("Didn't recognize dialect: " + arg +
          ";\n Must be one of: oracle mysql hsql (case insensitive)");
    }
  }
}
