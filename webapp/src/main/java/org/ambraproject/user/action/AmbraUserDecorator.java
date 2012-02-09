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
package org.ambraproject.user.action;

import org.ambraproject.user.AmbraUser;
import org.ambraproject.util.TextUtils;

/**
 * A wrapper around AmbraUser to be used to render AmbraUser attributes as non-malicious chars
 */
public class AmbraUserDecorator extends AmbraUser {
  private final AmbraUser ambraUser;

  public AmbraUserDecorator(final AmbraUser ambraUser) {
    super(ambraUser);
    this.ambraUser = ambraUser;
  }

  public String getBiography() {
    return getSafe(ambraUser.getBiography());
  }

  public String getBiographyText() {
    return getSafe(ambraUser.getBiographyText());
  }

  public String getCity() {
    return getSafe(ambraUser.getCity());
  }

  public String getCountry() {
    return getSafe(ambraUser.getCountry());
  }

  public String getDisplayName() {
    return getSafe(ambraUser.getDisplayName());
  }

  public String getEmail() {
    return getSafe(ambraUser.getEmail());
  }

  public String getGender() {
    return getSafe(ambraUser.getGender());
  }

  public String getGivenNames() {
    return getSafe(ambraUser.getGivenNames());
  }

  public String getHomePage() {
    return getSafe(ambraUser.getHomePage());
  }

  public String getInterestsText() {
    return getSafe(ambraUser.getInterestsText());
  }

  public String getOrganizationName() {
    return getSafe(ambraUser.getOrganizationName());
  }

  public String getOrganizationType() {
    return getSafe(ambraUser.getOrganizationType());
  }

  public String getPositionType() {
    return getSafe(ambraUser.getPositionType());
  }

  public String getPostalAddress() {
    return getSafe(ambraUser.getPostalAddress());
  }

  public String getPublications() {
    return getSafe(ambraUser.getPublications());
  }

  public String getRealName() {
    return getSafe(ambraUser.getRealName());
  }

  public String getResearchAreasText() {
    return getSafe(ambraUser.getResearchAreasText());
  }

  public String getSurnames() {
    return getSafe(ambraUser.getSurnames());
  }

  public String getTitle() {
    return getSafe(ambraUser.getTitle());
  }

  public String getWeblog() {
    return getSafe(ambraUser.getWeblog());
  }

  private String getSafe(final String value) {
    return TextUtils.escapeHtml(value);
  }
}
