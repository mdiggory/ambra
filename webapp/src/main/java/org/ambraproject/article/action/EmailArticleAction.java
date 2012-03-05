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
package org.ambraproject.article.action;

import org.ambraproject.ApplicationException;
import org.ambraproject.article.service.ArticleService;
import org.ambraproject.article.service.NoSuchArticleIdException;
import org.ambraproject.models.Article;
import org.ambraproject.models.UserProfile;
import org.ambraproject.service.AmbraMailer;
import org.ambraproject.service.XMLService;
import org.ambraproject.user.action.UserActionSupport;
import org.ambraproject.util.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.topazproject.ambra.email.impl.FreemarkerTemplateMailer;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Email the article to another user.
 */
public class EmailArticleAction extends UserActionSupport {
  private String articleURI;
  private String emailTo;
  private String emailFrom;
  private String senderName;
  private String note;
  private String title;
  private String description;
  private String journalName;
  private AmbraMailer ambraMailer;
  private XMLService secondaryObjectService;
  private ArticleService articleService;
  private static final Logger log = LoggerFactory.getLogger(EmailArticleAction.class);
  private static final int MAX_TO_EMAIL = 5;

  /**
   * Render the page with the values passed in
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(readOnly = true)
  public String executeRender() throws Exception {
    if (!validatesArticleURI())
      return INPUT;

    final UserProfile ambraUser = getCurrentUser();
    if (null != ambraUser) {
      senderName = ambraUser.getDisplayName();
      emailFrom = ambraUser.getEmail();
    }
    setArticleTitleAndDesc(articleURI);
    return SUCCESS;
  }

  /**
   * Send the email
   * @return webwork status
   * @throws Exception Exception
   */
  @Transactional(readOnly = true)
  public String executeSend() throws Exception {

    if (!validates())
      return INPUT;

    setArticleTitleAndDesc(articleURI);

    final Map<String, String> mapFields = new HashMap<String, String>();
    mapFields.put("articleURI", articleURI);
    mapFields.put("senderName", senderName);
    mapFields.put(FreemarkerTemplateMailer.USER_NAME_KEY, senderName);
    mapFields.put("note", note);
    mapFields.put("title", title);
    mapFields.put("description", description);
    mapFields.put("journalName", journalName);
    mapFields.put("subject", "An Article from PLoS: " + TextUtils.simpleStripAllTags(title));

    ambraMailer.sendEmailThisArticleEmail(emailTo, emailFrom, mapFields);

    return SUCCESS;
  }

  private void setArticleTitleAndDesc(final String articleURI) throws NoSuchArticleIdException,
                                      ApplicationException {
    final Article article = articleService.getArticle(articleURI, getAuthId());
    title = article.getTitle();

    description = article.getDescription();

    if(description != null && description.trim().length() > 0) {
      description = secondaryObjectService.getTransformedDescription(description);
    } else {
      description = "";
    }
  }

  private boolean validates() {
    boolean isValid = true;

    isValid = validatesArticleURI();

    if (StringUtils.isBlank(emailFrom)) {
      addFieldError("emailFrom", "Your e-mail address cannot be empty");
      isValid = false;
    }

    if (!EmailValidator.getInstance().isValid(emailFrom)) {
      addFieldError("emailFrom", "Invalid e-mail address");
      isValid = false;
    }

    isValid = checkEmails(emailTo) && isValid;
    
    if (StringUtils.isBlank(senderName)) {
      addFieldError("senderName", "Your name cannot be empty");
      isValid = false;
    }

    return isValid;
  }

  /**
   * Validate article uri
   * @return false if article uri is not valid
   */
  private boolean validatesArticleURI() {
    boolean isValid = true;
    if (StringUtils.isBlank(articleURI)) {
      addFieldError("articleURI", "Article URI cannot be empty");
      isValid = false;
    } else {
      try {
        articleURI = URLDecoder.decode(articleURI,"UTF-8");
        new URI(articleURI);
      } catch (UnsupportedEncodingException ex) {
        addFieldError("articleURI", "Must be a valid URI, character encoding is bad.");
     } catch (URISyntaxException ex) {
        addFieldError("articleURI", "Must be a valid URI");
      }
    }

    return isValid;
  }

  private boolean checkEmails (String emailList) {
    if(StringUtils.isBlank(emailList)) {
      addFieldError ("emailTo", "To e-mail address cannot be empty");
      return false;
    } else {
      final StringTokenizer emailTokens = new StringTokenizer(emailList, " \t\n\r\f,");
      if (emailTokens.countTokens() > MAX_TO_EMAIL) {
        addFieldError ("emailTo", "Maximum of " + MAX_TO_EMAIL + " email addresses");
        return false;
      }
      EmailValidator validator = EmailValidator.getInstance();
      ArrayList <String> invalidEmails = new ArrayList<String> ();

      while (emailTokens.hasMoreTokens()) {
        String email = emailTokens.nextToken();
        if (!validator.isValid(email)) {
          invalidEmails.add(email);
        }
      }
      final int numInvalid = invalidEmails.size();
      if (numInvalid != 0) {
        StringBuilder errorMsg = new StringBuilder ("Invalid e-mail address");
        if (numInvalid > 1) {
          errorMsg.append ("es: ");
        } else {
          errorMsg.append(": ");
        }
        Iterator<String> iter = invalidEmails.iterator();
        while (iter.hasNext()){
          errorMsg.append (iter.next());
          if (iter.hasNext()) {
            errorMsg.append(", ");
          }
        }
        addFieldError ("emailTo", errorMsg.toString());
      }
      return (numInvalid == 0);
    }
  }

  /**
   * Getter for articleURI.
   * @return Value of articleURI.
   */
  public String getArticleURI() {
    return articleURI;
  }

  /**
   * Setter for articleURI.
   * @param articleURI Value to set for articleURI.
   */
  public void setArticleURI(final String articleURI) {
    this.articleURI = articleURI;
  }

  /**
   * Getter for emailFrom.
   * @return Value of emailFrom.
   */
  public String getEmailFrom() {
    return emailFrom;
  }

  /**
   * Setter for emailFrom.
   * @param emailFrom Value to set for emailFrom.
   */
  public void setEmailFrom(final String emailFrom) {
    this.emailFrom = emailFrom;
  }

  /**
   * Getter for emailTo.
   * @return Value of emailTo.
   */
  public String getEmailTo() {
    return emailTo;
  }

  /**
   * Setter for emailTo.
   * @param emailTo Value to set for emailTo.
   */
  public void setEmailTo(final String emailTo) {
    this.emailTo = emailTo;
  }

  /**
   * Getter for note.
   * @return Value of note.
   */
  public String getNote() {
    return note;
  }

  /**
   * Setter for note.
   * @param note Value to set for note.
   */
  public void setNote(final String note) {
    this.note = note;
  }

  /**
   * Getter for senderName.
   * @return Value of senderName.
   */
  public String getSenderName() {
    return senderName;
  }

  /**
   * Setter for senderName.
   * @param senderName Value to set for senderName.
   */
  public void setSenderName(final String senderName) {
    this.senderName = senderName;
  }

  /**
   * Setter for ambraMailer.
   * @param ambraMailer Value to set for ambraMailer.
   */
  @Required
  public void setAmbraMailer(final AmbraMailer ambraMailer) {
    this.ambraMailer = ambraMailer;
  }

  /**
   * Setter for fetchArticleService.
   * @param articleService Value to set for ArticleService.
   */
  @Required
  public void setArticleService(ArticleService articleService) {
    this.articleService = articleService;
  }

  /**
   * @param secondaryObjectService The XMLService to set.
   */
  @Required
  public void setSecondaryObjectService(XMLService secondaryObjectService) {
    this.secondaryObjectService = secondaryObjectService;
  }

  /**
   * Getter for description.
   * @return Value of description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter for title.
   * @return Value of title.
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
   * @return Returns the journalName.
   */
  public String getJournalName() {
    return journalName;
  }

  /**
   * @param journalName The journalName to set.
   */
  public void setJournalName(String journalName) {
    this.journalName = journalName;
  }

  /**
   * @return Returns the mAX_TO_EMAIL.
   */
  public int getMaxEmails() {
    return MAX_TO_EMAIL;
  }
}
