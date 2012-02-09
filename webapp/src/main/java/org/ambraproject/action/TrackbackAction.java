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

package org.ambraproject.action;

import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.struts2.ServletActionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.roller.util.LinkbackExtractor;

import org.springframework.beans.factory.annotation.Required;

import org.ambraproject.annotation.service.TrackbackService;

/**
 * Class to process trackback requests from external sites.  Writes information to
 * store if previous one does not exist and spam checking is passed.
 *
 * @author Stephen Cheng
 *
 */
public class TrackbackAction extends BaseActionSupport {
  private static final Logger log = LoggerFactory.getLogger(TrackbackAction.class);
  private int error = 0;
  private String errorMessage = "";
  private String title;
  private String url;
  private String excerpt;
  private String blog_name;
  private String trackbackId;

  private TrackbackService trackbackService;

  private static final String ARTICLE_ACTION = "ambra.platform.articleAction";

  /**
   * Main action execution.
   *
   */
  public String execute() throws Exception {

    if (!ServletActionContext.getRequest().getMethod().equals("POST")) {
      if (log.isDebugEnabled()) {
        log.debug("Returning error because HTTP POST was not used.");
      }
      return returnError ("HTTP method must be POST");
    }

    final URL permalink;
    final URI trackback;

    try {
      permalink = new URL (url);
    } catch (Exception e) {
      if (log.isInfoEnabled()) {
        log.info ("Could not construct URL with parameter: " + url);
      }
      return returnError("URL invalid");
    }

    try {
      trackback = new URI (trackbackId);
    } catch (Exception e) {
      if (log.isInfoEnabled()) {
        log.info ("Could not construct URI with parameter: " + trackbackId);
      }

      return returnError("Object URI invalid");
    }

    String articleURL = getArticleUrl(getVirtualJournalContext().getBaseUrl(), trackbackId);
    LinkbackExtractor linkback = new LinkbackExtractor(url, articleURL);

    if (linkback.getExcerpt() == null) {
      if (log.isDebugEnabled()) {
        log.debug("Trackback failed verification url: " + url);
        log.debug("Trackback failed verification articleURL: " + articleURL);
      }
      return returnError("Trackback failed validation");
    }

    boolean inserted = trackbackService.saveTrackBack(title, blog_name, excerpt,
        permalink, trackback, url, trackbackId);

    //If inserted is false, it was a dupe and nothing no data has changed.

    if (log.isInfoEnabled() && inserted) {
              StringBuilder msg = new StringBuilder("Successfully inserted trackback for resource: ")
                                           .append (trackbackId);
        log.info(msg.toString());
    }

    if (log.isDebugEnabled() && inserted) {
        StringBuilder msg = new StringBuilder("Successfully inserted trackback for resource: ")
                                           .append (trackbackId)
                                           .append ("; with title: ").append (title)
                                           .append ("; url: ").append (url)
                                           .append ("; excerpt: ").append (excerpt)
                                           .append ("; blog_name: ").append (blog_name);
        log.debug(msg.toString());
    }

    return SUCCESS;
  }

  /**
   * Sets the error message and error number
   * @param errMsg the error message
   * @return the constant "ERROR" string
   */
  private String returnError (String errMsg) {
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
   * @return Returns the trackbackId.
   */
  public String getTrackbackId() {
    return trackbackId;
  }

  /**
   * @param trackbackId The trackbackId to set.
   */
  public void setTrackbackId(String trackbackId) {
    this.trackbackId = trackbackId;
  }

  /**
   * Returns the URL to the fetch article action defined in the config file under the
   * "ambra.platform.articleAction" setting
   * @param baseURL the base URL of the journal
   * @param articleURI the URI of the article
   * @return the full URL path to the article
   */
  private String getArticleUrl (String baseURL, String articleURI) {
    String escapedURI;
    try {
      escapedURI = URLEncoder.encode(articleURI, "UTF-8");
    } catch (UnsupportedEncodingException ue) {
      escapedURI = articleURI;
    }

    StringBuilder url = new StringBuilder(baseURL).append("/").
                                                   append(configuration.getString(ARTICLE_ACTION)).
                                                   append(escapedURI);

    if (log.isDebugEnabled()) {
      log.debug("article url to find is: " + url.toString());
    }

    return url.toString();
  }

  /**
   * @param trackBackservice The trackBackService to set.
   */
  @Required
  public void setTrackBackService(TrackbackService trackBackservice) {
    this.trackbackService = trackBackservice;
  }
}
