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

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.Types;

/**
 * @author Joe Osowski
 */
public class URIType implements UserType {
  Logger log = LoggerFactory.getLogger(URIType.class);

  public int[] sqlTypes()
  {
    int[] types = new int[1];

    types[0] = Types.VARCHAR;

    return types;
  }

  public Class returnedClass()
  {
    return URI.class;
  }

  public boolean equals(java.lang.Object o, java.lang.Object o1) throws org.hibernate.HibernateException {
    if (o == null) {
      if (o1 == null) {
        return true;
      } else {
        return false;
      }
    }

    URI u;
    URI u1;

    if(o instanceof String) {
      try {
        u = new URI((String)o);
      } catch(URISyntaxException ex) {
        throw new HibernateException(ex.getMessage(),ex);
      }
    } else {
      u = (URI)o;
    }

    if(o1 instanceof String) {
      try {
        u1 = new URI((String)o1);
      } catch(URISyntaxException ex) {
        throw new HibernateException(ex.getMessage(),ex);
      }
    } else {
      u1 = (URI)o1;
    }

    return ((URI) u).equals(u1);
  }

  public int hashCode(java.lang.Object o) throws org.hibernate.HibernateException {
    if (o == null) {
      return 0;
    }

    if(o instanceof String) {
      try {
        return (new URI((String)o)).hashCode();
      } catch(URISyntaxException ex) {
        throw new HibernateException(ex.getMessage(),ex);
      }
    } else {
      return ((URI) o).hashCode();
    }
  }

  public Object nullSafeGet(java.sql.ResultSet resultSet, java.lang.String[] names, java.lang.Object o)
    throws org.hibernate.HibernateException, java.sql.SQLException
  {
    URI uri = null;

    String strURI = resultSet.getString(names[0]);

    if(!resultSet.wasNull()) {
      try {
        return new URI(strURI);
      } catch (URISyntaxException ex) {
        throw new HibernateException(ex.getMessage(),ex);
      }
    }

    return uri;
  }


  public void nullSafeSet(PreparedStatement statement, java.lang.Object value, int index)
    throws org.hibernate.HibernateException, java.sql.SQLException
  {
    if (value == null) {
      statement.setString(index, null);
    } else {
      if(value instanceof String) {
        try {
          //Test syntax
          new URI((String)value);
          statement.setString(index,(String)value);
        } catch(URISyntaxException ex) {
          throw new HibernateException(ex.getMessage(),ex);
        }
      } else {
        statement.setString(index, ((URI)value).toString());
      }
    }
  }

  public Object deepCopy(java.lang.Object o) throws org.hibernate.HibernateException
  {
    return o;
  }

  public boolean isMutable()
  {
    return true;
  }

  public Serializable disassemble(java.lang.Object value) throws org.hibernate.HibernateException
  {
    return (Serializable)value;
  }

  public Object assemble(java.io.Serializable cached, java.lang.Object owner)
    throws org.hibernate.HibernateException
  {
    return cached;
  }

  public Object replace(java.lang.Object original, java.lang.Object target, java.lang.Object owner)
    throws org.hibernate.HibernateException
  {
   return original;
  }
}
