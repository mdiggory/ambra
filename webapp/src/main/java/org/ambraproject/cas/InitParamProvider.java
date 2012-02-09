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

import java.util.Enumeration;

/**
 * An interface to make it easier to reuse code when working with
 * config wrappers for ServletConfig and FilterConfig
 */
public interface InitParamProvider {
  /**
   * Get init parameter names
   * @return Enumeration
   */
  Enumeration getInitParameterNames();

  /**
   * Get the init parameter for a given key
   * @param key parameter name
   * @return value
   */
  String getInitParameter(final String key);
}
