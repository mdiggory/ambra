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

package org.ambraproject.article.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.ambraproject.BaseWebTest;
import org.ambraproject.Constants;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleView;
import org.ambraproject.models.UserProfile;
import org.ambraproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 2/16/12
 */
public class FetchObjectActionTest extends FetchActionTest {
  
  @Autowired
  protected FetchObjectAction action;

  @Autowired
  protected UserService userService;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }


  @Test
  public void testFetchArticleXML() {
    login(userService.getUserByAuthId(DEFAULT_ADMIN_AUTHID));
    int numViews = dummyDataStore.getAll(ArticleView.class).size();

    action.setUri(getArticleToFetch().getDoi());
    action.setRepresentation("XML");
    String result = action.fetchObjectAction();

    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    assertEquals(dummyDataStore.getAll(ArticleView.class).size(), numViews + 1, "Action didn't record this xml download");
  }
}
