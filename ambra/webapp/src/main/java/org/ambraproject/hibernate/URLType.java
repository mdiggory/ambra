/*
 * $HeadURL:
 * $Id:
 *
 * Copyright (c) 2006-2011 by Public Library of Science
 *
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Hibernate UserType for storing URLs in a database.  Copied from <a href="http://www.blog.lessrain.com/storing-files-as-urls-with-hibernate-usertype/">Paul
 * at LessRain</a>
 *
 * @author Alex Kudlick Date: Mar 16, 2011
 *         <p/>
 *         org.ambraproject.hibernate
 */
public class URLType implements UserType {
  static final Log log = LogFactory.getLog(URLType.class);

  public URLType() {
  }

  public int[] sqlTypes() {
    return new int[]{Types.VARCHAR};
  }

  @SuppressWarnings("unchecked")
  public Class returnedClass() {
    return URL.class;
  }

  public boolean equals(Object x, Object y) {
    return (x == y) || (x != null && y != null && (x.equals(y)));
  }

  public Object nullSafeGet(ResultSet inResultSet, String[] names, Object o) throws SQLException {
    String val = (String) Hibernate.STRING.nullSafeGet(inResultSet, names[0]);
    URL url = null;
    try {
      url = new URL(val);
    } catch (MalformedURLException e) {
      log.error("problem creating URL from " + val);
    }
    return url;
  }

  public void nullSafeSet(PreparedStatement inPreparedStatement, Object o, int i) throws SQLException {
    URL val = (URL) o;
    String url = "";
    if (val != null) {
      url = val.toString();
    }
    inPreparedStatement.setString(i, url);
  }

  public Object deepCopy(Object o) {
    if (o == null) {
      return null;
    }
    URL deepCopy = null;
    try {
      deepCopy = new URL(o.toString());
    } catch (MalformedURLException e) {
      log.error("Problem creating deepcopy of URL" + o.toString());
    }
    return deepCopy;
  }

  public boolean isMutable() {
    return true;
  }

  public Object assemble(Serializable cached, Object owner) {
    return deepCopy(cached);
  }

  public Serializable disassemble(Object value) {
    return (Serializable) deepCopy(value);
  }

  public Object replace(Object original, Object target, Object owner) {
    return deepCopy(original);
  }

  public int hashCode(Object x) {
    return x.hashCode();
  }
}