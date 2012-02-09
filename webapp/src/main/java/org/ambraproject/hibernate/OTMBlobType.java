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

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.usertype.UserType;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.topazproject.otm.AbstractBlob;
import org.topazproject.otm.Blob;
import org.topazproject.otm.OtmException;

/**
 * @author Joe Osowski
 */
public class OTMBlobType implements UserType
{
  public int[] sqlTypes()
  {
        int[] types = new int[1];

    types[0] = Types.BLOB;

    return types;
  }

  public Class returnedClass() {
    return Blob.class;
  }

  public boolean equals(Object o, Object o1) throws HibernateException
  {
    //TODO: This should work fine for copying objects from topaz, but should not make it to production.
    if (o == null) {
      if (o1 == null) {
        return true;
      } else {
        return false;
      }
    }

    return o.equals(o1);
  }

  public int hashCode(Object o) throws HibernateException
  {
    if(o instanceof Blob) {
      return ((Blob)o).hashCode();
    } else {
      throw new HibernateException("Not of type blob");
    }
  }

  public Object nullSafeGet(ResultSet resultSet, String[] names, Object o) throws HibernateException, SQLException
  {
    java.sql.Blob sBlob = resultSet.getBlob(names[0]);

    if(!resultSet.wasNull()) {
      try {
        return newBlob(names[0], sBlob);
      } catch (Exception ex) {
        throw new HibernateException(ex.getMessage(),ex);
      }
    }

    return null;
  }

  public void nullSafeSet(PreparedStatement statement, java.lang.Object value, int index) throws HibernateException, SQLException
  {
    if (value == null) {
      statement.setNull(index, Types.BLOB);
    } else {
      if(value instanceof org.topazproject.otm.Blob) {
        //TODO: I don't think the following line works
        statement.setBlob(index, ((org.topazproject.otm.Blob)value).getInputStream());
      } else {
        throw new HibernateException("Object not of correct type: " + value.getClass().getName());
      }
    }

  }

  public Object deepCopy(Object o) throws HibernateException {
    //TODO: This should work fine for copying objects from topaz, but should not make it to production.
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

  private org.topazproject.otm.Blob newBlob(String id, final java.sql.Blob sBlob) {
    return new AbstractBlob(id) {

      @Override
      protected InputStream doGetInputStream() throws OtmException {
        try {
          return sBlob.getBinaryStream();
        } catch (SQLException ex) {
          throw new OtmException(ex.getMessage(),ex);
        }
      }

      @Override
      protected OutputStream doGetOutputStream() throws OtmException {
        try {
          return sBlob.setBinaryStream(0);
        } catch (SQLException ex) {
          throw new OtmException(ex.getMessage(),ex);
        }
      }

      @Override
      protected void writing(OutputStream outputStream) {
       //TODO: Do we need to implement?
      }

      public ChangeState getChangeState() {
        //TODO: Do we need to implement?
        return null;
      }

      public ChangeState mark() {
        //TODO: Do we need to implement?
        return null;
      }

      public boolean exists() throws OtmException {
        //TODO: Do we need to implement?
        throw new OtmException("Not implemented.");
      }

      public boolean create() throws OtmException {
        //TODO: Do we need to implement?
        throw new OtmException("Not implemented.");
      }

      public boolean delete() throws OtmException {
        //TODO: Do we need to implement?
        throw new OtmException("Not implemented.");
      }
    };
  }
}


