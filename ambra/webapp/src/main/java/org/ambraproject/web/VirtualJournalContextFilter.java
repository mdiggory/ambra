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

package org.ambraproject.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.NDC;

import org.topazproject.ambra.configuration.ConfigurationStore;

/**
 * A Filter that sets the {@link VirtualJournalContext} as an attribute in the ServletRequest.
 *
 * Application usage:
 * <pre>
 * VirtualJournalContext requestContent = ServletRequest.getAttribute(PUB_VIRTUALJOURNAL_CONTEXT);
 * String requestJournal = requestContext.getJournal();
 * </pre>
 *
 * See WEB-INF/classes/ambra/configuration/defaults.xml for configuration examples.
 */
public class VirtualJournalContextFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(VirtualJournalContextFilter.class);

  public static final String CONF_VIRTUALJOURNALS          = "ambra.virtualJournals";
  public static final String CONF_VIRTUALJOURNALS_DEFAULT  = CONF_VIRTUALJOURNALS + ".default";
  public static final String CONF_VIRTUALJOURNALS_JOURNALS = CONF_VIRTUALJOURNALS + ".journals";

  private static final Configuration configuration = ConfigurationStore.getInstance().getConfiguration();

  /*
   * @see javax.servlet.Filter#init
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    // settings & overrides are in the Configuration
    if (configuration == null) {
      // should never happen
      final String errorMessage = "No Configuration is available to set Virtual Journal context";
      log.error(errorMessage);
      throw new ServletException(errorMessage);
    }
  }

  /*
   * @see javax.servlet.Filter#destroy
   */
  public void destroy() {
    // nothing to do
  }

  /*
   * @see javax.servlet.Filter#doFilter
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final Collection<String> virtualJournals = configuration.getList(CONF_VIRTUALJOURNALS_JOURNALS);

    String defaultJournal = configuration.getString(CONF_VIRTUALJOURNALS_DEFAULT);

    // need to do <rule> based processing
    String journalName = findMatchingVirtualJournal(configuration, (HttpServletRequest) request);

    if (journalName != null) {
      if (log.isTraceEnabled()) {
        log.trace("journal from rules: journal = \"" + journalName + "\"");
      }
    } else {
      // was a simple config <default> specified?

      journalName = defaultJournal;

      if (log.isTraceEnabled()) {
        log.trace("journal from defaults: journal = \"" + journalName + "\"");
      }
    }

    // empty if default not set
    if (journalName == null) {
      journalName = "";

      if (log.isTraceEnabled()) {
        log.trace("Default journal not set");
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("journal = \"" + journalName + "\" for "
          + ((HttpServletRequest)request).getRequestURL());
    }

    // put virtualJournal context in the ServletRequest for webapp to use
    request.setAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT,
      new VirtualJournalContext(
          journalName,
          defaultJournal,
          request.getScheme(),
          request.getServerPort(),
          request.getServerName(),
          ((HttpServletRequest) request).getContextPath(), virtualJournals));

    /*
     * Establish a "Nested Diagnostic Context" for logging, e.g. prefix log entries w/journal name
     * http://logging.apache.org/log4j/docs/api/org/apache/log4j/NDC.html
     */
    NDC.push(journalName);

    try {
      // continue the Filter chain ...
      filterChain.doFilter(request, response);
    } finally {
      // cleanup "Nested Diagnostic Context" for logging
      NDC.pop();
      NDC.remove(); // TODO: appropriate place to cleanup for Thread?
    }
  }

  /**
   * Process all &lt;${journal-name}&gt;&lt;rules&gt;&lt;${http-header-name}&gt;s looking for a match.
   * This method is only used to fetch rules from configuration and find matching journal based on request.
   *
   * @param configuration <code>Configuration</code> that contains the rules.
   * @param request <code>HttpServletRequest</code> to apply the rules against.
   * @return VirtualJournalContext.  May be <code>null</code>.
   */
  private String findMatchingVirtualJournal(
    Configuration configuration, HttpServletRequest request) {

    String virtualJournal = null;

    // process all <virtualjournal><journals> entries looking for a match
    final List<String> journals = configuration.getList(CONF_VIRTUALJOURNALS_JOURNALS);
    final Iterator<String> onJournal = journals.iterator();
    while (onJournal.hasNext() && virtualJournal == null) {
      final String journal = onJournal.next();

      if (log.isTraceEnabled()) {
        log.trace("processing virtual journal: " + journal);
      }

      // get the <rules> for this journal
      final String rulesPrefix = CONF_VIRTUALJOURNALS + "." + journal + ".rules";
      final Iterator rules = configuration.getKeys(rulesPrefix);
      while (rules.hasNext()) {
        final String rule       = (String) rules.next();
        final String httpHeader = rule.substring(rulesPrefix.length() + 1);
        final String httpValue  = configuration.getString(rule);

        if (log.isTraceEnabled()) {
          log.trace("processing rule: " + httpHeader + " = " + httpValue);
        }

        // test Request HTTP header value against match
        final String reqHttpValue = request.getHeader(httpHeader);
        if (log.isTraceEnabled()) {
          log.trace("testing Request: " + httpHeader + "=" + reqHttpValue);
        }
        if (reqHttpValue == null) {
          if (httpValue == null) {
            virtualJournal = journal;
            break;
          }
          continue;
        }

        if (reqHttpValue.matches(httpValue)) {
          virtualJournal = journal;
          break;
        }
      }
    }

    // return match or null
    return virtualJournal;
  }
}
