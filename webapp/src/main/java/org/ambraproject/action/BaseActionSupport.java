/* $HeadURL::                                                                            $
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
package org.ambraproject.action;

import com.opensymphony.xwork2.ActionSupport;
import org.ambraproject.Constants;
import org.ambraproject.web.VirtualJournalContext;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for all actions.
 */
public abstract class BaseActionSupport extends ActionSupport implements RequestAware {
  private static final Logger  log  = LoggerFactory.getLogger(BaseActionSupport.class);

  protected Configuration configuration;
  protected Map requestAttributes;

  private static final String FEED_DEFAULT_NAME = "ambra.services.feed.defaultName";
  private static final String FEED_BASE_PATH = "ambra.services.feed.basePath";
  private static final String FEED_DEFAULT_FILE = "ambra.services.feed.defaultFile";

  public void setRequest(Map map) {
    requestAttributes = map;
  }

  /**
   * This overrides the deprecated super implementation and returns an empty implementation as we
   * want to avoid JSON serializing the deprecated implementation when it tries to serialize
   * an Action when the result type is ajaxJSON.
   *
   * @return a empty list
   */
  @Override
  public Collection getErrorMessages() {
      return Collections.EMPTY_LIST;
  }

  /**
   * This overrides the deprecated super inplementation and returns an empty implementation as we
   * want to avoid JSON serializing the deprecated implementation when it tries to serialize
   * an Action when the result type is ajaxJSON.
   *
   * @return a empty map
   */
  @Override
  public Map getErrors() {
      return Collections.EMPTY_MAP;
  }

  /**
   * Return the number of fields with errors.
   * @return number of fields with errors
   */
  public int getNumFieldErrors() {
    return getFieldErrors().size();
  }

  /**
   * @return the name of the rss feed for a page (will be prefixed by the journal name)
   */
  public String getRssName() {
    return configuration.getString(FEED_DEFAULT_NAME, "New Articles");
  }

  /**
   * @return the URL path for the rss feed for a page
   */
  public String getRssPath() {
    return configuration.getString(FEED_BASE_PATH, "/article/") +
        configuration.getString(FEED_DEFAULT_FILE, "feed");
  }

  /**
   * Setter method for configuration. Injected through Spring.
   * 
   * @param configuration Ambra configuration
   */
  @Required
  public void setAmbraConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }


  /**
   * Add profane words together into a message.
   * @param profaneWords profaneWords
   * @param fieldName fieldName
   * @param readableFieldName readableFieldName
   */
  protected void addProfaneMessages(final List<String> profaneWords, final String fieldName,
                                    final String readableFieldName) {
    if (!profaneWords.isEmpty()) {
      final String joinedWords = StringUtils.join(profaneWords.toArray(), ", ");
      String  msg;
      if (profaneWords.size() > 1) {
        msg = "these words";
      } else {
        msg = "this word";
      }
      addFieldError(fieldName, "Profanity filter found: " + joinedWords + ". Please remove " +
                               msg + ".");
    }
  }

  /**
   * Retrieve VirtualJournalContext that is bound to the request by VirtualJournalContextFilter
   * @see org.ambraproject.web.VirtualJournalContextFilter
   * @return Virtual journal context
   */
  protected VirtualJournalContext getVirtualJournalContext() {
    return (VirtualJournalContext) requestAttributes.get(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);
  }

  /**
   * Returns key of the current journal based on the request url
   * @return Journal key, NULL if no journal context
   */
  public String getCurrentJournal() {
    VirtualJournalContext context = getVirtualJournalContext();
    return (context == null) ? null : context.getJournal();
  }

  public String getAuthId() {
    HttpServletRequest request = ServletActionContext.getRequest();

    if(request == null) {
      throw new RuntimeException("HttpServletRequest is null");
    }

    HttpSession httpSession = request.getSession(true);

    if(httpSession == null) {
      throw new RuntimeException("HttpSession is null");
    }

    return (String)httpSession.getAttribute(Constants.AUTH_KEY);
  }
}
