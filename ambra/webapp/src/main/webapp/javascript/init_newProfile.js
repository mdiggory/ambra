/*
 * $HeadURL::                                                                            $
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var _ldc;
var _profileForm;

dojo.addOnLoad( function() {
  _ldc = dijit.byId("LoadingCycle");
  _profileForm = document.userForm;
  _profileForm.action = _namespace + "/user/createNewUser.action";
  dojo.connect(_profileForm.formSubmit, "onclick", function() {
    _profileForm.submit();
    return true;
  });
});