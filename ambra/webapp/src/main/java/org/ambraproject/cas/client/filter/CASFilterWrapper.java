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
package org.ambraproject.cas.client.filter;

import org.apache.commons.configuration.Configuration;
import org.ambraproject.cas.ConfigWrapper;
import org.ambraproject.cas.ConfigWrapperUtil;
import org.ambraproject.cas.InitParamProvider;
import org.topazproject.ambra.configuration.ConfigurationStore;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper to read the configuration values from our own configuration file.
 */
public class CASFilterWrapper extends CASFilter {
  /**
   * @see javax.servlet.Servlet
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    final Map<String, String> params = new HashMap<String, String>(6);

    final InitParamProvider initParamProvider = new InitParamProvider(){
      public Enumeration<?> getInitParameterNames() {
        return filterConfig.getInitParameterNames();
      }
      public String getInitParameter(final String key) {
        return filterConfig.getInitParameter(key);
      }
    };

    ConfigWrapperUtil.copyInitParams(initParamProvider, params);

    final Configuration configuration = ConfigurationStore.getInstance().getConfiguration();

    final String ambraServerHost = configuration.getString("ambra.platform.host");
    final String casProxyValidateUrl = configuration.getString("ambra.services.cas.url.proxy-validate");
    final String casLoginUrl = configuration.getString("ambra.services.cas.url.login");

    ConfigWrapperUtil.setInitParamValue(CASFilter.LOGIN_INIT_PARAM,
            casLoginUrl, initParamProvider, params);
    ConfigWrapperUtil.setInitParamValue(CASFilter.VALIDATE_INIT_PARAM,
            casProxyValidateUrl, initParamProvider, params);
    ConfigWrapperUtil.setInitParamValue(CASFilter.SERVERNAME_INIT_PARAM,
            ambraServerHost, initParamProvider, params);

    final FilterConfig customFilterConfig = new ConfigWrapper(filterConfig, params);
    super.init(customFilterConfig);
  }
}
