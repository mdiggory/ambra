package org.ambraproject.annotation.action;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.model.article.ArticleInfo;
import org.springframework.beans.factory.annotation.Required;

/**
 * Basic action for the start discussion page. just fetches up article doi and title.
 * @author Alex Kudlick 4/12/12
 */
public class StartDiscussionAction extends BaseActionSupport {

  private ArticleService articleService;
  private ArticleInfo articleInfo;
  private String articleURI;


  @Override
  public String execute() throws Exception {
    articleInfo = articleService.getBasicArticleView(articleURI);
    return SUCCESS;
  }

  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  public void setArticleURI(String articleURI) {
    this.articleURI = articleURI;
  }

  public ArticleInfo getArticleInfo() {
    return articleInfo;
  }
}
