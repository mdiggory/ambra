/*
 * $HeadURL$
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

package org.ambraproject.admin.action;

import org.ambraproject.action.BaseActionSupport;
import org.ambraproject.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Required;

public class BaseAdminActionSupport  extends BaseActionSupport {

  protected AdminService adminService;

  // Fields Used by template
  private AdminService.JournalInfo journalInfo;

  /**
    * Gets the JournalInfo value object for access in the view.
    *
    * @return Current virtual Journal value object
    */
   public AdminService.JournalInfo getJournal() {
     return journalInfo;
   }

  /**
   * Sets the AdminService.
   *
   * @param  adminService The adminService to set.
   */
  @Required
  public void setAdminService(AdminService adminService) {
    this.adminService = adminService;
  }

  protected void initJournal() {
    journalInfo = adminService.createJournalInfo(getCurrentJournal());
  }

}
