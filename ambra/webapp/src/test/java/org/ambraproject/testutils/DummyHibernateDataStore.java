/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2011 by Public Library of Science
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

package org.ambraproject.testutils;

import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.Category;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link DummyDataStore} using a hibernate session factory to create sessions and store data.  This
 * should be autowired with the same context as the tests; see HibernateServiceBeanContext.xml
 *
 * @author Alex Kudlick Date: 5/2/11
 *         <p/>
 *         org.topazproject.ambra
 */
public class DummyHibernateDataStore implements DummyDataStore {

  private HibernateTemplate hibernateTemplate;
  private Map<String, ClassMetadata> allClassMetadata;

  @Required
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    allClassMetadata = sessionFactory.getAllClassMetadata();
  }

  @Override
  public String store(Object object) {
    try {
      return hibernateTemplate.save(object).toString();
    } catch (Exception e) {
      //for constraint violation exceptions, just check if the object already exists in the db,
      // so we can reuse data providers and call store() multiple times
      return getStoredId(object);
    }
  }

  @Override
  public <T> List<String> store(List<T> objects) {
    List<String> ids = new ArrayList<String>();
    if (objects != null) {
      for (Object o : objects) {
        ids.add(store(o));
      }
    }
    return ids;
  }

  private String getStoredId(Object object) {
    try {
      if (object instanceof Article) {
        return hibernateTemplate.findByCriteria(
            DetachedCriteria.forClass(Article.class)
            .add(Restrictions.eq("doi",((Article) object).getDoi()))
            .setProjection(Projections.id())
        ).get(0).toString();
      } else if (object instanceof ArticleAsset) {
        return hibernateTemplate.findByCriteria(
            DetachedCriteria.forClass(ArticleAsset.class)
            .add(Restrictions.eq("doi",((ArticleAsset) object).getDoi()))
            .add(Restrictions.eq("extension",((ArticleAsset) object).getExtension()))
            .setProjection(Projections.id())
        ).get(0).toString();
      } else if (object instanceof Category) {
        return hibernateTemplate.findByCriteria(
            DetachedCriteria.forClass(ArticleAsset.class)
                .add(Restrictions.eq("mainCategory",((Category) object).getMainCategory()))
                .add(Restrictions.eq("subCategory",((Category) object).getSubCategory()))
                .setProjection(Projections.id())
        ).get(0).toString();
      } else {
          //check if the object has an id set on it
          String idPropertyName = allClassMetadata.get(object.getClass().getName())
                  .getIdentifierPropertyName();
          String idGetterName = "get" + idPropertyName.substring(0, 1).toUpperCase() + idPropertyName.substring(1);
          return object.getClass().getMethod(idGetterName).invoke(object).toString();
      }
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void update(Object object) {
    hibernateTemplate.update(object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Serializable id, Class<T> clazz) {
    return (T) hibernateTemplate.get(clazz, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> getAll(Class<T> clazz) {
    return hibernateTemplate.findByCriteria(DetachedCriteria.forClass(clazz));
  }
}
