<#--
  $HeadURL::                                                                            $
  $Id$
  
  Copyright (c) 2007-2010 by Public Library of Science
  http://plos.org
  http://ambraproject.org
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<html>
    <head>
        <title>Welcome</title>
    </head>
    <body>
        <h1>Welcome to the ambra-registration webapp</h1>

        <p>
            <fieldset>
                <legend>A few things for you to do</legend>
                <p>
                    <@s.url id="registerURL" action="register" />
                    <@s.a href="%{registerURL}">Register</@s.a>
                </p>

                <p>
                    <@s.url id="changePasswordURL" action="changePassword" />
                    <@s.a href="%{changePasswordURL}">Change Password</@s.a>
                </p>

                <p>
                    <@s.url id="forgotPasswordURL" action="forgotPassword" />
                    <@s.a href="%{forgotPasswordURL}">Forgot Password</@s.a>
                </p>
            </fieldset>
        </p>
    </body>
</html>
