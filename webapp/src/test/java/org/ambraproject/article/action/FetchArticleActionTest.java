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
import org.ambraproject.Constants;
import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.models.Article;
import org.ambraproject.models.ArticleAsset;
import org.ambraproject.models.ArticleView;
import org.ambraproject.models.Category;
import org.ambraproject.models.UserProfile;
import org.ambraproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.testng.Assert.assertEquals;

/**
 * @author Alex Kudlick 2/16/12
 */
public class FetchArticleActionTest extends FetchActionTest {

  @Autowired
  protected FetchArticleAction action;

  @Autowired
  protected UserService userService;

  @Override
  protected BaseActionSupport getAction() {
    return action;
  }
  
  @Test
  public void testFetchArticle() {
    //put the user in the session
    UserProfile user = userService.getUserByAuthId(DEFUALT_USER_AUTHID);
    login(user);
    int numArticleViews = dummyDataStore.getAll(ArticleView.class).size();

    action.setArticleURI(article.getDoi());
    String result = action.fetchArticle();
    assertEquals(result, Action.SUCCESS, "Action didn't return success");
    compareArticles(action.getArticleInfo(), article);

    assertEquals(dummyDataStore.getAll(ArticleView.class).size(), numArticleViews + 1,
        "Action didn't record this as an article view");
  }
}
