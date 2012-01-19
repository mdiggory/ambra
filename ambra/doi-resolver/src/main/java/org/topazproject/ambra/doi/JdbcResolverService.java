/* $HeadURL:: $
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
package org.topazproject.ambra.doi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Resolver for the rdf:type of a DOI-URI.
 *
 * @author Alex Kudlick
 */
public class JdbcResolverService implements ResolverDAOService {
  private static final Logger log = LoggerFactory.getLogger(JdbcResolverService.class);
  private final JdbcTemplate jdbcTemplate;

  public JdbcResolverService(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public boolean doiIsArticle(String doi) {
    log.debug("looking up doi " + doi + " in Article table");
    int count = jdbcTemplate
        .queryForInt("select count(*) from Article where articleUri = ?",
            new Object[]{doi});
    return count != 0;
  }

  @Override
  public boolean doiIsAnnotation(String doi) {
    log.debug("looking up doi " + doi + " in Annotation table");
    int count = jdbcTemplate
        .queryForInt("select count(*) from Annotation where annotationUri = ?",
            new Object[]{doi});
    return count != 0;
  }

  @Override
  public String getAnnotatedRoot(String doi) throws AnnotationLoopException {
    //Keep track of previously visited dois so we don't end up in an infinite loop
    return loopSafeGetAnnotatedRoot(doi, new HashSet<String>());
  }

  @SuppressWarnings("unchecked")
  private String loopSafeGetAnnotatedRoot(String doi, Set<String> previousDois) throws AnnotationLoopException {
    List<Map> results = jdbcTemplate.queryForList(
        "select annotates from Annotation where annotationUri = ?",
        new Object[]{doi}
    );
    String annotates;
    if (results.size() == 0 || (annotates = (String) results.get(0).get("ANNOTATES")) == null) {
      return doi;
    } else if (previousDois.contains(annotates)) {
      throw new AnnotationLoopException(previousDois);
    } else {
      previousDois.add(annotates);
      return loopSafeGetAnnotatedRoot(annotates, previousDois);
    }
  }
}
