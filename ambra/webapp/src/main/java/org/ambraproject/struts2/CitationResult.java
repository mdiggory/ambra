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

package org.ambraproject.struts2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to generate citations to client by making the resulting file an attachment and supplying the
 * correct file extension specified in xwork.xml
 * 
 * 
 * @author stevec
 *
 */
public class CitationResult extends FreemarkerResult  {
  private static final Logger log = LoggerFactory.getLogger(CitationResult.class);

  private String fileExtension;

  protected boolean preTemplateProcess(freemarker.template.Template template,
      freemarker.template.TemplateModel model) throws IOException{

    String doi = (String)invocation.getStack().findValue("doi");
    HttpServletResponse response = ServletActionContext.getResponse();
    try {
      response.addHeader("Content-disposition", "attachment; filename=" +
                                                URLEncoder.encode(doi, "UTF-8") + fileExtension);
    } catch (UnsupportedEncodingException uee) {
      response.addHeader("Content-disposition", "attachment; filename=citation" + fileExtension);
    }
    return super.preTemplateProcess(template, model);
  }

  /**
   * @param fileExtension The fileExtension to set.
   */
  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }
}
