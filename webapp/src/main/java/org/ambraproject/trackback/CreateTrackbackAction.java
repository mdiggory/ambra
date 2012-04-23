/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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

package org.ambraproject.trackback;

import org.ambraproject.action.BaseActionSupport;
import org.apache.struts2.ServletActionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Required;

/**
 * Class to process trackback requests from external sites.  Writes information to store if previous one does not exist
 * and spam checking is passed.
 *
 * @author Stephen Cheng
 */
public class CreateTrackbackAction extends BaseActionSupport {
  private static final Logger log = LoggerFactory.getLogger(CreateTrackbackAction.class);
  private int error = 0;
  private String errorMessage = "";
  private String title;
  private String url;
  private String excerpt;
  private String blog_name;
  private String doi;

  private TrackbackService trackbackService;

  /**
   * Main action execution.
   */
  public String execute() throws Exception {
    if (!ServletActionContext.getRequest().getMethod().equals("POST")) {
      log.debug("Returning error because HTTP POST was not used.");
      return returnError("HTTP method must be POST");
    }

    //check to ensure that the blog actually has a link to the article
    try {
      if (!trackbackService.blogLinksToArticle(url, doi)) {
        log.debug("Blog at {} didn't contain a link to article {}", url, doi);
        return returnError("Trackback failed validation");
      }
    } catch (Exception e) {
      log.error("Error validating trackback at " + url, e);
      return returnError("Error validating trackback");
    }

    //create the trackback
    try {
      trackbackService.createTrackback(doi, url, title, blog_name, excerpt);
      log.info("Successfully created trackback for {} with url {}", doi, url);
    } catch (DuplicateTrackbackException e) {
      return returnError("A trackback already exists for that article and url");
    } catch (IllegalArgumentException e) {
      return returnError(e.getMessage());
    } catch (Exception e) {
      log.error("Error creating trackback for article: " + doi + " and url: " + url, e);
      return returnError("Error creating trackback");
    }
    return SUCCESS;
  }

  /**
   * Sets the error message and error number
   *
   * @param errMsg the error message
   * @return the constant "ERROR" string
   */
  private String returnError(String errMsg) {
    error = 1;
    errorMessage = errMsg;
    return ERROR;
  }

  /**
   * @return Returns the blog_name.
   */
  public String getBlog_name() {
    return blog_name;
  }

  /**
   * @param blog_name The blog_name to set.
   */
  public void setBlog_name(String blog_name) {
    this.blog_name = blog_name;
  }

  /**
   * @return Returns the error.
   */
  public int getError() {
    return error;
  }

  /**
   * @param error The error to set.
   */
  public void setError(int error) {
    this.error = error;
  }

  /**
   * @return Returns the errorMessage.
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage The errorMessage to set.
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * @return Returns the excerpt.
   */
  public String getExcerpt() {
    return excerpt;
  }

  /**
   * @param excerpt The excerpt to set.
   */
  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return Returns the url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url The url to set.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return Returns the doi.
   */
  public String getDoi() {
    return doi;
  }

  /**
   * @param doi The doi to set.
   */
  public void setDoi(String doi) {
    this.doi = doi;
  }

  /**
   * @param trackBackservice The trackBackService to set.
   */
  @Required
  public void setTrackBackService(TrackbackService trackBackservice) {
    this.trackbackService = trackBackservice;
  }
}
