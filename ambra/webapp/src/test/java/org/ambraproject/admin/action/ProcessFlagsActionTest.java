/* $HeadURL$
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

package org.ambraproject.admin.action;

import com.opensymphony.xwork2.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.ambraproject.BaseWebTest;
import org.ambraproject.annotation.service.AnnotationService;
import org.topazproject.ambra.models.Annotation;
import org.topazproject.ambra.models.AnnotationBlob;
import org.topazproject.ambra.models.Citation;
import org.topazproject.ambra.models.Comment;
import org.topazproject.ambra.models.FormalCorrection;
import org.topazproject.ambra.models.Journal;
import org.ambraproject.models.Article;

import java.net.URI;
import java.net.URISyntaxException;

import static org.testng.Assert.*;

/**
 * @author Dragisa Krsmanovic
 */

public class ProcessFlagsActionTest extends BaseWebTest {
  @Autowired
  protected ManageFlagsAction action;
  @Autowired
  protected AnnotationService annotationService; //Just using annotation service to check the annotation in the db

  @DataProvider(name = "commentWithFlags")
  public Object[][] getCommentWithFlags() throws URISyntaxException {
    String annotationId = "info:doi/123.456/annotation1";
    String flag1Id = "info:doi/123.456/flag1";

    Journal j = new Journal();
    j.setKey("journal");
    j.seteIssn("journaleIssn");
    dummyDataStore.store(j);

    Comment oldAnnotation = new Comment();
    oldAnnotation.setAnnotates(URI.create("articleId"));
    oldAnnotation.setId(URI.create(annotationId));
    oldAnnotation.setBody(new AnnotationBlob());
    dummyDataStore.store(oldAnnotation);

    Comment flag1 = new Comment();
    flag1.setId(URI.create(flag1Id));
    flag1.setBody(new AnnotationBlob());
    dummyDataStore.store(flag1);

    Article article = new Article();
    article.setDoi(oldAnnotation.getAnnotates().toString());
    dummyDataStore.store(article);

    Citation articleCitation = new Citation();
    articleCitation.setCitationType("cit-type");
    dummyDataStore.store(articleCitation);

    return new Object[][]{
      {annotationId, new String[]{flag1Id + "_" + annotationId}}
    };
  }

  @Test(dataProvider = "commentWithFlags")
  public void testConvertToFormalCorrection(String annotationId, String[] flags) throws Exception {
    setupAdminContext();

    action.setConvertToFormalCorrection(flags);
    action.setRequest(getDefaultRequestAttributes());

    assertFalse(annotationService.getAnnotation(annotationId) instanceof FormalCorrection,
        "Annotation to convert was already a formal correction");
    String result = null;
    try {
      result = action.processFlags();
    } catch (Exception e) {
      String message = "Action invocation threw exception";
      for (String error : action.getActionErrors()) {
        message += "\n*" + error;
      }
      fail(message, e);
    }
    assertEquals(result, Action.SUCCESS,
        "action to convert formal correction with " + flags.length + " flags didn't succeed");

    for (String paramStr : flags) {
      String[] tokens = paramStr.split("_");

      Annotation newAnnotation = annotationService.getAnnotation(tokens[1]);

      assertTrue(newAnnotation instanceof FormalCorrection,
          "Annotation wasn't converted to formal correction");
      assertEquals(newAnnotation.getType(),FormalCorrection.RDF_TYPE,"Annotation didn't get correct type");

      try {
        // make sure the flag is deleted
        Annotation flag = annotationService.getAnnotation(tokens[0]);
        fail("The flag " + tokens[0] + " should have been deleted.");
      } catch (IllegalArgumentException e) {
        // success
      }
    }
  }
}
