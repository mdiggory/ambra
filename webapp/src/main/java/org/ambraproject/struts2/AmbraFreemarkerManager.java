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
import java.io.Reader;
import java.io.File;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.ScopesHashModel;
import org.apache.struts2.views.freemarker.StrutsClassTemplateLoader;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ambraproject.util.AuthorNameAbbreviationDirective;
import org.ambraproject.util.ArticleFormattingDirective;
import org.ambraproject.util.RandomNumberDirective;
import org.ambraproject.util.SimpleTextDirective;
import org.ambraproject.util.URLParametersDirective;
import org.ambraproject.web.VirtualJournalContext;
import org.topazproject.ambra.configuration.ConfigurationStore;
import org.springframework.beans.factory.annotation.Required;

import com.opensymphony.xwork2.util.ValueStack;

import freemarker.cache.TemplateLoader;
import freemarker.cache.StatefulTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.TemplateException;
import freemarker.template.Configuration;

/**
 * Custom Freemarker Manager to load up the configuration files for css, javascript, and titles of
 * pages
 *
 * @author Stephen Cheng
 */
public class AmbraFreemarkerManager extends FreemarkerManager {

  private static final Logger log = LoggerFactory.getLogger(AmbraFreemarkerManager.class);

  private AmbraFreemarkerConfig freemarkerConfig;
  private org.apache.commons.configuration.Configuration configuration;

  public AmbraFreemarkerManager() {
  }

  /**
   * Sets the custom configuration object via Spring injection
   *
   */
  @Required
  public void setAmbraFreemarkerConfig(AmbraFreemarkerConfig fmConfig) {
    this.freemarkerConfig = fmConfig;
  }

  @Required
  public void setAmbraConfiguration( org.apache.commons.configuration.Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Subclass from parent to add the freemarker configuratio object globally
   *
   * @see org.apache.struts2.views.freemarker.FreemarkerManager
   */
  @Override
  protected void populateContext(ScopesHashModel model, ValueStack stack, Object action,
                                 HttpServletRequest request, HttpServletResponse response) {
    super.populateContext(model, stack, action, request, response);
    model.put("freemarker_config", freemarkerConfig);
  }

  @Override
  protected TemplateLoader getTemplateLoader(final ServletContext context) {

    return new StatefulTemplateLoader() {

      private final TemplateLoader templateLoader;

      // anonymous initializer
      {
        String journalTemplatePath = configuration.getString(ConfigurationStore.JOURNAL_TEMPLATE_DIR, null);
        FileTemplateLoader templatePathLoader = null;
        try {
          templatePathLoader = new FileTemplateLoader(new File(journalTemplatePath));
        } catch (IOException e) {
          log.error("Invalid template path " + journalTemplatePath + " in " + ConfigurationStore.JOURNAL_TEMPLATE_DIR);
        }

        this.templateLoader = templatePathLoader != null ?
            new MultiTemplateLoader(new TemplateLoader[]{
                templatePathLoader,
                new WebappTemplateLoader(context),
                new StrutsClassTemplateLoader()
            })
            : new MultiTemplateLoader(new TemplateLoader[]{
            new WebappTemplateLoader(context),
            new StrutsClassTemplateLoader()
        });
      }

      public void closeTemplateSource(Object source) throws IOException {
        templateLoader.closeTemplateSource(source);
      }

      // requests are in form journals/<journal_name>/<package>/template.ftl
      public Object findTemplateSource(String templatePath) throws IOException {

        // First: look into journal-specific templates
        Object templateSource = getJournalSpecificTemplate(templatePath);
        if (templateSource != null)
          return templateSource;

        String trimmedPath = AmbraFreemarkerConfig.trimJournalFromTemplatePath(templatePath);

        // Second: look into site-specific override templates
        if (freemarkerConfig.getDefaultJournalName() != null) {
          templateSource = getDefaultTemplate(trimmedPath);
          if (templateSource != null)
            return templateSource;
        }

        // Third: look in the ambra default folders
        templateSource = templateLoader.findTemplateSource(trimmedPath);
        if (templateSource != null)
          return templateSource;

        /*
         * Fifth: try struts default theme
         * FIXME: theme inheritance is hard coded
         * NOTE: The real fix is in struts. See WW-1832
         */
        if (templatePath.indexOf("ambra-theme") >= 0)
          return templateLoader.findTemplateSource(templatePath.replace("ambra-theme", "simple"));
        else
          return null;

      }

      private Object getDefaultTemplate(String templatePath) throws IOException {

        return templateLoader.findTemplateSource(defaultPath(templatePath));
      }

      private Object getJournalSpecificTemplate(String templatePath) throws IOException {
        String journalSpecificTemplatePath = templatePath;

        // If path doesn't start with journal append current journal context
        if (templatePath.startsWith("journals") || templatePath.startsWith("/journals")) {
          journalSpecificTemplatePath = addWebappToPath(journalSpecificTemplatePath);
        } else {
          journalSpecificTemplatePath = journalizePath(journalSpecificTemplatePath);
        }

        return templateLoader.findTemplateSource(journalSpecificTemplatePath);
      }

      public long getLastModified(Object source) {
        return templateLoader.getLastModified(source);
      }

      public Reader getReader(Object source, String encoding) throws IOException {
        return templateLoader.getReader(source, encoding);
      }

      public void resetState() {
        if (templateLoader instanceof StatefulTemplateLoader)
          ((StatefulTemplateLoader) templateLoader).resetState();
      }

      /**
       * Add journal mapping prefix to template path
       * @param templatePath Template path
       * @return Template path in form /journals/journalName/webapp/templatePath
       */
      private String journalizePath(String templatePath) {

        VirtualJournalContext journalContext = (VirtualJournalContext)ServletActionContext.getRequest().
            getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);

        // Not inside a journal context
        if (journalContext == null)
          return templatePath;

        StringBuilder journalizedPath = new StringBuilder();
        journalizedPath.append("/journals/")
            .append(journalContext.getJournal())
            .append((templatePath.startsWith("/") ? "/webapp" : "/webapp/"))
            .append(templatePath);

        return journalizedPath.toString();

      }

      private String addWebappToPath(String templatePath) {
        StringTokenizer tokenizer = new StringTokenizer(templatePath, "/");
        StringBuilder stringBuilder = new StringBuilder();
        boolean addToNext = false;
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          stringBuilder.append('/').append(token);
          if (addToNext && token.equals("webapp"))
            addToNext = false;
          if (addToNext) {
            stringBuilder.append("/webapp");
            addToNext = false;
          } else if (token.equals("journals"))
            addToNext = true;

        }

        return stringBuilder.toString();

      }

      private String defaultPath(String templatePath) {

        StringBuilder defaultPath = new StringBuilder();

        defaultPath.append("/journals/")
            .append(freemarkerConfig.getDefaultJournalName())
            .append("/webapp")
            .append((templatePath.startsWith("/") ? "" : "/"))
            .append(templatePath);
        return defaultPath.toString();

      }
    };
  }

  /**
   * Attaches custom Freemarker directives as shared variables.
   *
   * @param servletContext Servlet context.
   * @return Freemarker configuration.
   * @throws TemplateException
   */
  @Override
  protected Configuration createConfiguration(ServletContext servletContext) throws TemplateException {
    Configuration configuration = super.createConfiguration(servletContext);
/*
    configuration.setServletContextForTemplateLoading(servletContext, "/");
    try {
      configuration.setDirectoryForTemplateLoading(new File("/"));
    } catch (IOException e) {
      throw new TemplateException("Error setting root directory", e, Environment.getCurrentEnvironment());
    }
*/
    configuration.setCacheStorage(new AmbraTemplateStorage(
        freemarkerConfig.getCache_storage_strong(),
        freemarkerConfig.getCache_storage_soft()));

    configuration.setTemplateUpdateDelay(freemarkerConfig.getTemplateUpdateDelay());
    configuration.setSharedVariable("abbreviation", new AuthorNameAbbreviationDirective());
    configuration.setSharedVariable("articleFormat", new ArticleFormattingDirective());
    configuration.setSharedVariable("simpleText", new SimpleTextDirective());
    configuration.setSharedVariable("URLParameters", new URLParametersDirective());
    configuration.setSharedVariable("randomNumber", new RandomNumberDirective());
    return configuration;
  }
}
