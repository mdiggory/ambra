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
package org.ambraproject.cas;

import org.apache.commons.lang.StringUtils;

import java.util.Enumeration;
import java.util.Map;

/**
 * Itsy bitsy methods to help with the custom config wrappers around ServletConfig and FilterConfig
 */
public class ConfigWrapperUtil {
  /**
   * Copy the init params to the given map.
   * @param initParamProvider initParamProvider
   * @param params params
   */
  public static void copyInitParams(final InitParamProvider initParamProvider,
                                    final Map<String, String> params) {
    final Enumeration en = initParamProvider.getInitParameterNames();
    while (en.hasMoreElements()) {
      String key = (String) en.nextElement();
      params.put(key, initParamProvider.getInitParameter(key));
    }
  }

  /**
   * Set the init param into the map with the custom value if found or from the init provider otherwise
   * @param initParamName initParamName
   * @param customValue customValue
   * @param defaultInitParamProvider defaultInitParamProvider
   * @param params params
   */
  public static void setInitParamValue(final String initParamName, final String customValue,
                                       final InitParamProvider defaultInitParamProvider,
                                       final Map<String, String> params) {
    String finalValue = StringUtils.isBlank(customValue) ?
      defaultInitParamProvider.getInitParameter(initParamName) : customValue;

    params.put(initParamName, finalValue);
  }
}
