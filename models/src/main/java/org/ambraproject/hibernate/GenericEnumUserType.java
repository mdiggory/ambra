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

package org.ambraproject.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * User type to persist enums with the xml mappings.  Copied from
 * <a href="http://stackoverflow.com/questions/4744179/java-lang-verifyerror-on-hibernate-specific-usertype>here.</a>
 * <p/>
 * The configuration needs to specify the <var>enumClass</var> parameter, at a minimum.  Here's an example mapping:
 * <p/>
 * <code>
 * <property name="type" column="type" not-null="true">
 * <type name="org.ambraproject.hibernate.GenericEnumUserType">
 * <param name="enumClass">org.ambraproject.models.ArticleView$Type</param>
 * </type>
 * </property>
 * </code>
 *
 * The dollar sign indicates an inner class
 */
public class GenericEnumUserType implements UserType, ParameterizedType {

  private Class<? extends Enum> enumClass;
  private Class<?> identifierType;
  private Method identifierMethod;
  private Method valueOfMethod;
  private static final String defaultIdentifierMethodName = "name";
  private static final String defaultValueOfMethodName = "valueOf";
  private AbstractSingleColumnStandardBasicType type;
  private int[] sqlTypes;

  @Override
  public void setParameterValues(Properties parameters) {
    String enumClassName = parameters.getProperty("enumClass");
    try {
      enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
    } catch (ClassNotFoundException exception) {
      throw new HibernateException("Enum class not found", exception);
    }

    String identifierMethodName = parameters.getProperty("identifierMethod", defaultIdentifierMethodName);

    try {
      identifierMethod = enumClass.getMethod(identifierMethodName,
          new Class[0]);
      identifierType = identifierMethod.getReturnType();
    } catch (Exception exception) {
      throw new HibernateException("Failed to optain identifier method",
          exception);
    }

    TypeResolver tr = new TypeResolver();
    type = (AbstractSingleColumnStandardBasicType) tr.basic(identifierType.getName());
    if (type == null) {
      throw new HibernateException("Unsupported identifier type " + identifierType.getName());
    }
    sqlTypes = new int[]{type.sqlType()};

    String valueOfMethodName = parameters.getProperty("valueOfMethod",
        defaultValueOfMethodName);

    try {
      valueOfMethod = enumClass.getMethod(valueOfMethodName,
          new Class[]{identifierType});
    } catch (Exception exception) {
      throw new HibernateException("Failed to optain valueOf method",
          exception);
    }
  }

  @Override
  public Class returnedClass() {
    return enumClass;
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
      throws HibernateException, SQLException {
    Object identifier = type.get(rs, names[0]);
    try {
      return valueOfMethod.invoke(enumClass, new Object[]{identifier});
    } catch (Exception exception) {
      throw new HibernateException("Exception while invoking valueOfMethod of enumeration class: ",
          exception);
    }
  }

  public void nullSafeSet(PreparedStatement st, Object value, int index)
      throws HibernateException, SQLException {
    try {
      Object identifier = value != null ? identifierMethod.invoke(value,
          new Object[0]) : null;
      st.setObject(index, identifier);
    } catch (Exception exception) {
      throw new HibernateException("Exception while invoking identifierMethod of enumeration class: ",
          exception);

    }
  }

  @Override
  public int[] sqlTypes() {
    return sqlTypes;
  }

  @Override
  public Object assemble(Serializable cached, Object owner)
      throws HibernateException {
    return cached;
  }

  @Override
  public Object deepCopy(Object value)
      throws HibernateException {
    return value;
  }

  @Override
  public Serializable disassemble(Object value)
      throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public boolean equals(Object x, Object y)
      throws HibernateException {
    return x == y;
  }

  @Override
  public int hashCode(Object x)
      throws HibernateException {
    return x.hashCode();
  }

  public boolean isMutable() {
    return false;
  }

  public Object replace(Object original, Object target, Object owner)
      throws HibernateException {
    return original;
  }
}
