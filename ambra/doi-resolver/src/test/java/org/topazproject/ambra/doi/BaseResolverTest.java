/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2011 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.ambra.doi;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import javax.sql.DataSource;

/**
 * Base class for DOI resolver tests. Creates an empty tables with two tables: Article and Annotations.  Access to the
 * database is available via the {@link #dataSource} property, and rows can be inserted via the {@link
 * #insertArticleRow(String)} and {@link #insertAnnotationRow(String, String)}
 *
 * @author alex 9/7/11
 */
public class BaseResolverTest {

  protected DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  @BeforeClass
  public void createDB() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
    dataSource.setUrl("jdbc:hsqldb:mem:testdb");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    this.dataSource = dataSource;
    jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.execute(
        "drop table if exists Article;" +
            "drop table if exists Annotation;" +
            "create table Article (" +
            "  articleUri varchar(255)," +
            "  primary key (articleUri)" +
            ");" +
            "create table Annotation (" +
            "  annotationUri varchar(255)," +
            "  annotates varchar(255)," +
            "  primary key (annotationUri)" +
            ");");
  }

  /**
   * Helper method to insert a row into the embedded Article table
   *
   * @param doi the doi column to insert
   */
  protected void insertArticleRow(String doi) {
    jdbcTemplate.execute("insert into Article values ('" + doi + "');");
  }

  /**
   * Helper method to insert a row into the embedded Annotation table
   *
   * @param doi       the doi of the annotation row to insert
   * @param annotates the annotates column of the row to insert.  May be null
   */
  protected void insertAnnotationRow(String doi, String annotates) {
    if (annotates != null) {
      jdbcTemplate.execute("insert into Annotation values ('" + doi + "', + '" + annotates + "')");
    } else {
      jdbcTemplate.execute("insert into Annotation values ('" + doi + "', NULL)");
    }
  }

}
