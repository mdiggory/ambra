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
package org.topazproject.ambra.service;

/**
 * Provides service implementations for all kinds of services.
 */
public class ServiceFactory {
  private PersistenceService persistenceService;
  private RegistrationService registrationService;

  /**
   * @return registrationService
   */
  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  /**
   * @param registrationService registrationService to set
   */
  public void setRegistrationService(final RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  /**
   * @return persistenceService
   */
  public PersistenceService getPersistenceService() {
    return persistenceService;
  }

  /**
   * @param persistenceService persistenceService to set
   */
  public void setPersistenceService(final PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }
}
