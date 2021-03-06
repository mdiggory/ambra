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

import org.ambraproject.Constants;
import org.ambraproject.models.UserProfile;

/**
 * User Profile Action that is called by the member user to update their profile
 * (distinct from the one that might be called by admin to edit a user profile)
 */
public class MemberUserProfileAction extends UserProfileAction {
  @Override
  protected String getUserAuthId() {
    return (String) session.get(Constants.AUTH_KEY);
  }

  //Have to cache the user after saving because, if it was an old user with no display name and they just added one,
  // EnsureUserAccountInterceptor needs to know
  @Override
  @SuppressWarnings("unchecked")
  protected void afterSave(UserProfile savedUser) {
    session.put(Constants.AMBRA_USER_KEY, savedUser);
  }
}
