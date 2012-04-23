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
package org.ambraproject;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.apache.commons.collections.map.ListOrderedMap;

import java.util.Map;

/**
 * Some of the constants for the Ambra application.
 */
public interface Constants {
  String RECENT_SEARCHES_KEY = "RECENT_SEARCHES";
  String AMBRA_USER_KEY = "AMBRA_USER";
  String USER_ID_KEY = "org.ambraproject.user-id";
  String AUTH_KEY = CASFilter.CAS_FILTER_USER;
  String SINGLE_SIGNON_RECEIPT = CASFilter.CAS_FILTER_RECEIPT;
  String SINGLE_SIGNON_EMAIL_KEY = "org.ambraproject.sso.email";
  /** Authentication method used for anonymous user, otherwise it is normally CAS */
  String ANONYMOUS_USER_AUTHENTICATION = "ANONYMOUS_USER_AUTHENTICATION";
  String ROOT_PACKAGE = "org.topazproject.ambra";
  String ADMIN_ROLE = "admin";

  /**
   * Defines the length of various fields used by Webwork Annotations
   */
  interface Length {
    String EMAIL = "256";
    String PASSWORD = "256";
    String DISPLAY_NAME_MIN = "4";
    String DISPLAY_NAME_MAX = "18";
    int COMMENT_TITLE_MAX = 500;
    int COMMENT_BODY_MAX = 64000;
    int CI_STATEMENT_MAX = 5000;
  }

  /**
   * Return Code to be used for WebWork actions
   */
  interface ReturnCode {
    String NEW_PROFILE = "new-profile";
    String UPDATE_PROFILE = "update-profile";
    String NOT_SUFFICIENT_ROLE = "role-insufficient";
  }

  /**
   * Masks used for denoting the state for annotations and replies
   */
  interface StateMask {
    int PUBLIC = 0x001; //binary 0001
    int FLAG = 0x002;   //binary 0010
    int DELETE = 0x004; //binary 0100
  }

  /**
   * Permission constants
   */
  public interface Permission {
    String ALL_PRINCIPALS = "http://rdf.topazproject.org/RDF/permissions#all";
  }

  public static class SelectValues {
    /** return a map of all url descriptions */
    public static Map<String, String> getAllUrlDescriptions() {
      final Map<String, String> allUrlDescriptions = new ListOrderedMap();
      allUrlDescriptions.put("", "Choose One");
      allUrlDescriptions.put("Personal", "Personal");
      allUrlDescriptions.put("Laboratory", "Laboratory");
      allUrlDescriptions.put("Departmental", "Departmental");
      allUrlDescriptions.put("Blog", "Blog");
      return allUrlDescriptions;
    }

    /** return a map of all position types */
    public static Map<String, String> getAllPositionTypes() {
      final Map<String, String> allPositionTypes = new ListOrderedMap();
      allPositionTypes.put("", "Choose One");
      allPositionTypes.put("Head of Department/Director", "Head of Department/Director");
      allPositionTypes.put("Professor/Group Leader", "Professor/Group Leader");
      allPositionTypes.put("Physician", "Physician");
      allPositionTypes.put("Post-Doctoral researcher", "Post-Doctoral researcher");
      allPositionTypes.put("Post-Graduate student", "Post-Graduate student");
      allPositionTypes.put("Undergraduate student", "Undergraduate student");
      allPositionTypes.put("Other", "Other");
      return allPositionTypes;
    }

    /** return a map of all Organization Types */
    public static Map<String, String> getAllOrganizationTypes() {
      final Map<String, String> allOrgTypes = new ListOrderedMap();
      allOrgTypes.put("", "Choose One");
      allOrgTypes.put("University/College", "University/College");
      allOrgTypes.put("Governmental", "Governmental");
      allOrgTypes.put("Hospital/Healthcare", "Hospital/Healthcare");
      allOrgTypes.put("Industry or Private Sector", "Industry or Private Sector");
      allOrgTypes.put("Library", "Library");
      allOrgTypes.put("Media/Communications", "Media/Communications");
      allOrgTypes.put("Research Institute", "Research Institute");
      allOrgTypes.put("Other", "Other");
      return allOrgTypes;
    }

    /** return a map of all titles */
    public static Map<String, String> getAllTitles() {
      final Map<String, String> allTitles = new ListOrderedMap();
      allTitles.put("", "");
      allTitles.put("Professor", "Professor");
      allTitles.put("Dr", "Dr.");
      allTitles.put("Mr", "Mr.");
      allTitles.put("Mrs", "Mrs.");
      allTitles.put("Ms", "Ms.");
      allTitles.put("Other", "Other");
      return allTitles;
    }
  }
}
