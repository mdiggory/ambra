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

package org.ambraproject.struts2;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ambraproject.web.VirtualJournalContext;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;
import freemarker.ext.servlet.HttpRequestHashModel;

/**
 * Custom Freemarker Result class so that we can pass the templateFile name into the template
 * in order to have a limited number of templates for the system.
 * 
 * @author Stephen Cheng
 *
 */
public class AmbraFreemarkerResult extends FreemarkerResult {
  private static final Logger log = LoggerFactory.getLogger(AmbraFreemarkerResult.class);
  private String templateFile;
  private boolean noCache = false;

  /**
   * @return Returns the templateFile.
   */
  public String getTemplateFile() {
    return templateFile;
  }

  /**
   * @param templateFile The templateFile to set.
   */
  public void setTemplateFile(String templateFile) {
    this.templateFile = templateFile;
  }

  /**
   * Add journal context path at the beginning of each Freemarker template
   * @param template template
   * @param model model
   * @return super.preTemplateProcess(template, model)
   * @throws IOException
   */
  protected boolean preTemplateProcess(freemarker.template.Template template,
                                       freemarker.template.TemplateModel model) throws IOException {
    SimpleHash modelHash = (SimpleHash) model;
    String templateFileName = templateFile;

    if (templateFile != null && !templateFile.startsWith("/journals/")) {
      try {
        HttpRequestHashModel requestModel = (HttpRequestHashModel)modelHash.get("Request");
        final VirtualJournalContext virtualJournalContext = (VirtualJournalContext) requestModel
          .getRequest()
          .getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);
        if (virtualJournalContext != null) {
          templateFileName = "/journals/" + virtualJournalContext.getJournal() + templateFile;
          if (log.isDebugEnabled()) {
            log.debug("Changed "+templateFile+" to "+templateFileName);
          }
        }

      } catch (TemplateModelException e) {
        throw new RuntimeException("Error in preTemplateProcess for "+templateFile, e);
      }
    }

    modelHash.put("templateFile", templateFileName);
    if (noCache) {
      HttpServletResponse response = ServletActionContext.getResponse();
      // HTTP 1.1 browsers should defeat caching on this header
      response.setHeader("Cache-Control", "no-cache");
      // HTTP 1.0 browsers should defeat caching on this header
      response.setHeader("Pragma", "no-cache");
      // Last resort for those that ignore all of the above
      response.setHeader("Expires", "-1");
    }
    return super.preTemplateProcess(template, model);
  }

  /**
   * @return Returns the noCache.
   */
  public boolean getNoCache() {
    return noCache;
  }

  /**
   * @param noCache The noCache to set.
   */
  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  /**
   * Set the HTTP Status (e.g., 200, 404, 500, etc) of the HTTP Response.
   *
   * @param status The HTTP Status to set in the HTTP Response.
   */
  public void setStatus(int status) {
    ServletActionContext.getResponse().setStatus(status);
  }
}
