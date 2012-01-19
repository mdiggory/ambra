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

/**
 * Generates the database schema based on Hibernate configuration
 *
 * @author Alex Kudlick Date: 6/2/11
 *         <p/>
 *         org.ambraproject
 */
public class GenerateSchemaSQL {

  /**
   * Generate a SQL file to create the schema
   *
   * @param args - First argument should be "update" if tables in the schema already exist and need to be dropped;
   *             "create" if the schema is brand new. The second argument should be one of
   *             <p/>
   *             <ul><li>ORACLE</li><li>MYSQL</li><li>HSQL</li></ul>
   *             <p/>
   *             according the sql dialect to be used (case insensitive)
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      throw new IllegalArgumentException("Must provide two arguments: update|create sql_dialect\n valid SQL dialect options are oracle, hsql, and mysql");
    }

    boolean update = true;
    if ("create".equals(args[0])) {
      update = false;
    } else if (!"update".equals(args[0])) {
      throw new IllegalArgumentException("Didn't recognize argument " + args[0] +
          "; must be either \"update\" or \"create\"");
    }
    SchemaGenerator gen = new SchemaGenerator(update);
    SchemaGenerator.Dialect dialect = SchemaGenerator.Dialect.fromString(args[1]);
    gen.generateSQL(dialect);
  }

}
