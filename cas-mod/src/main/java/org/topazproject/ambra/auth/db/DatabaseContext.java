/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
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
package org.topazproject.ambra.auth.db;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Properties;

/**
 * Provides a database connection pool, including prepared statement connection pooling.
 * Initializes DBCP environment and provides access for creating JDBC connections
 * and prepared Statements.
 *
 * Massive code lifting from http://www.devx.com/Java/Article/29795/0/page/2
 */
public class DatabaseContext {
  private static final Log log = LogFactory.getLog(DatabaseContext.class);

  private PoolingDataSource dataSource;
  private GenericObjectPool connectionPool;
  private static DatabaseContext databaseContext;

  /**
   * Construct a db context
   * @param jdbcDriver jdbcDriver
   * @param dbProperties dbProperties including url, user, password
   * @param initialSize initialSize of the pool
   * @param maxActive maxActive number of connections, after which it will block until a connection is
   *                  released
   * @param validationQuery to validate that the connection is still valid
   * @throws DatabaseException DatabaseException
   */
  private DatabaseContext(final String jdbcDriver, final Properties dbProperties, final int initialSize,
                          final int maxActive, final String validationQuery) throws DatabaseException {
    try {
      Class.forName(jdbcDriver);
    } catch (final ClassNotFoundException e) {
      throw new DatabaseException("Unable to load the db driver:" + jdbcDriver, e);
    }

    final ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory(dbProperties.getProperty("url"),
                                               dbProperties.getProperty("user"),
                                               dbProperties.getProperty("password"));

    connectionPool = new GenericObjectPool();
    connectionPool.setTestOnReturn(true);
    connectionPool.setMaxActive(maxActive);
    connectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    connectionPool.getMaxActive();

    final KeyedObjectPoolFactory stmtPool = new GenericKeyedObjectPoolFactory(null);

    /*
     * During instantiation, the PoolableConnectionFactory class registers itself to the
     * GenericObjectPool instance passed in its constructor. This factory class is used to create
     * new instances of the JDBC connections.
     */
    new PoolableConnectionFactory(connectionFactory, connectionPool, stmtPool, validationQuery, false,
                                  true);

    for (int i = 0; i < initialSize; i++) {
      try {
        connectionPool.addObject();
      } catch (final Exception e) {
        throw new DatabaseException("Error initlaizing initial number of connections", e);
      }
    }
    dataSource = new PoolingDataSource(connectionPool);
  }

  public Connection getConnection() throws DatabaseException {
    try {
      return dataSource.getConnection();
    } catch (final SQLException e) {
      throw new DatabaseException("Unable to get a connection from context", e);
    }
  }

  /**
   * Return a prepared statement from the pool
   * @param connection connection
   * @param query query
   * @return a prepared statement from the pool
   * @throws DatabaseException DatabaseException
   */
  public PreparedStatement getPreparedStatement(final Connection connection, final String query)
    throws DatabaseException {
    try {
      return connection.prepareStatement(query);
    } catch (final SQLException e) {
      throw new DatabaseException("Unable to prepare statement", e);
    }
  }

  /**
   * Returns the status of the connection pool.
   * @return the status of the connection pool
   */
  public String getStatus() {
    return "[Active:" + connectionPool.getNumActive() + ", Idle:" + connectionPool.getNumIdle() + "]";
  }

  public void close() throws DatabaseException {
    if (connectionPool != null) {
      connectionPool.clear();
      try {
        connectionPool.close();
      } catch (final Exception e) {
        throw new DatabaseException("Unable to shutdown Database context");
      }
    }
  }

  /**
   * Convenience method that returns a single string as a result
   * @param sqlQuery sqlQuery
   * @param whereClauseParam1 whereClauseParam1
   * @return a string value
   * @throws DatabaseException DatabaseException
   * @throws SQLException SQLException
   */
  public String getSingleStringValueFromDb(final String sqlQuery, final String whereClauseParam1)
    throws DatabaseException, SQLException {
    String returnValue = null;

    Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
      connection = getConnection();
      preparedStatement = getPreparedStatement(connection, sqlQuery);
      preparedStatement.setString(1, whereClauseParam1);
      final ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      returnValue = resultSet.getString(1);
      resultSet.close();
    } finally {
        if (preparedStatement != null) {
          preparedStatement.close();
        }
        if (connection != null) {
          connection.close();
        }
    }

    return returnValue;
  }

  /**
   * This method is not thread safe during construction time as it will be called by the
   * servlet container listener during application startup.
   *
   * @param jdbcDriver jdbcDriver
   * @param dbProperties dbProperties
   * @param initialSize initialSize
   * @param maxActive maxActive
   * @param validationQuery validationQuery
   * @return the instance of DatabaseContext
   * @throws DatabaseException DatabaseException
   */
  public static DatabaseContext createDatabaseContext(final String jdbcDriver, final Properties dbProperties, final int initialSize, final int maxActive, final String validationQuery) throws DatabaseException {
    databaseContext = new DatabaseContext(jdbcDriver, dbProperties, initialSize, maxActive,
                                          validationQuery);
    log.debug("DatabaseContext constructed");
    return databaseContext;
  }

  /**
   * @return the singleton instance of the DatabaseContext
   * @throws DatabaseException DatabaseException
   */
  public static DatabaseContext getInstance() throws DatabaseException {
    if (null == databaseContext) {
      final String message = "Database context uninitialized yet. Invoke createDatabaseContext first";
      log.debug(message);
      throw new DatabaseException(message);
    }
    return databaseContext;
  }
}
