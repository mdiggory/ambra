/* $HeadURL::                                                                                    $
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

package org.topazproject.ambra.sip

import org.apache.commons.configuration.Configuration

/**
 * A category to allow easier traversal of commons-configuration objects. Specifically, it adds
 * support for the '.' operator. If invoked on a Configuration this returns a String (if the
 * name is an existing configuration key or an attribute), null (if it specifies a not-present
 * attribute), or a List&lt;Configuration&gt; otherwise (which may be empty); if invoked on a
 * List&lt;Configuration&gt; then the operator returns a List of Configuration or String.
 * This also recognizes @foo to access attribute 'foo'. Example:
 * <pre>
 *   config.ambra.services.foo.'@bar'[0]
 * </pre>
 *
 * @author Ronald Tschalär
 */
class CommonsConfigCategory {
  static Object get(Configuration config, String key) {
    if (key.startsWith('@'))
      return config.getString("[${key}]", null)
    if (config.containsKey(key))
      return config.getString(key)

    List res = []
    for (int idx = 0; !config.subset("${key}(${idx})").isEmpty(); idx++)
      res.add(config.subset("${key}(${idx})"))

    return res
  }

  static Object get(List<Configuration> configList, String key) {
    return configList.collect{ get(it, key) }.flatten().findAll{ it }
  }
}
