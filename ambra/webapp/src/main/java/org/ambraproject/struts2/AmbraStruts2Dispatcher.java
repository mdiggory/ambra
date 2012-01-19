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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.FilterDispatcher;


import org.topazproject.ambra.configuration.ConfigurationStore;

/**
 * Override the Struts Constants configurations from our Commons config files. This adds one
 * more override to the Struts constant configuration hierarchy specified in
 * http://struts.apache.org/2.0.14/docs/constant-configuration.html So the overrides order now is
 * struts-defaults.xml,struts-plugin.xml,struts.xml,struts.properties,commons-config. This also
 * means that struts constants now can be specified as a system property. (eg.
 * -Dstruts.devMode=true)
 *
 * @author Pradeep Krishnan
 */
public class AmbraStruts2Dispatcher extends FilterDispatcher {
  private static final Logger      log          = LoggerFactory.getLogger(AmbraStruts2Dispatcher.class);
  private static final String[] keys         = {
    StrutsConstants.STRUTS_DEVMODE, StrutsConstants.STRUTS_I18N_RELOAD,
    StrutsConstants.STRUTS_I18N_ENCODING, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD,
    StrutsConstants.STRUTS_ACTION_EXTENSION, StrutsConstants.STRUTS_TAG_ALTSYNTAX,
    StrutsConstants.STRUTS_URL_HTTP_PORT, StrutsConstants.STRUTS_URL_HTTPS_PORT,
    StrutsConstants.STRUTS_URL_INCLUDEPARAMS, StrutsConstants.STRUTS_OBJECTFACTORY,
    StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, StrutsConstants.STRUTS_CONTINUATIONS_PACKAGE,
    StrutsConstants.STRUTS_CONFIGURATION, StrutsConstants.STRUTS_LOCALE,
    StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND,
    StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME,
    StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, StrutsConstants.STRUTS_VELOCITY_CONFIGFILE,
    StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION, StrutsConstants.STRUTS_VELOCITY_CONTEXTS,
    StrutsConstants.STRUTS_UI_TEMPLATEDIR, StrutsConstants.STRUTS_UI_THEME,
    StrutsConstants.STRUTS_MULTIPART_MAXSIZE, StrutsConstants.STRUTS_MULTIPART_SAVEDIR,
    StrutsConstants.STRUTS_MULTIPART_PARSER, StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,
    StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,
    StrutsConstants.STRUTS_XSLT_NOCACHE, StrutsConstants.STRUTS_CUSTOM_PROPERTIES,
    StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES, StrutsConstants.STRUTS_MAPPER_CLASS,
    StrutsConstants.STRUTS_SERVE_STATIC_CONTENT, StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE,
    StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION,
    StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES, StrutsConstants.STRUTS_MAPPER_COMPOSITE,
    StrutsConstants.STRUTS_ACTIONPROXYFACTORY, StrutsConstants.STRUTS_FREEMARKER_WRAPPER_ALT_MAP,
    StrutsConstants.STRUTS_XWORKCONVERTER, StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE,
    StrutsConstants.STRUTS_XWORKTEXTPROVIDER, StrutsConstants.STRUTS_ID_PARAMETER_NAME,
  };

  @Override
  protected Dispatcher createDispatcher(FilterConfig filterConfig) {
    Map<String, String> params               = new HashMap<String, String>();

    for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
      String name  = (String) e.nextElement();
      String value = filterConfig.getInitParameter(name);
      params.put(name, value);
    }

    Configuration conf = ConfigurationStore.getInstance().getConfiguration();

    for (String name : keys) {
      String val = conf.getString(name);

      if (val != null) {
        log.info("Setting struts constant: " + name + "=" + val);
        params.put(name, val);
      }
    }

    return new Dispatcher(filterConfig.getServletContext(), params);
    }

}
