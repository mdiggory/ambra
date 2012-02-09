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
<body>
<@s.form name="feedbackForm" cssClass="pone-form" action="feedback" method="post" title="Feedback">
  <@s.hidden name="page"/>
  <tr>
    <td>Name:</td>
    <td>
      <@s.textfield name="name" size="50" required="true"/>
    </td>
  </tr>
  <tr>
    <td>E-mail address:</td>
    <td>
      <@s.textfield name="fromEmailAddress" size="50" required="true"/>
    </td>
  </tr>
  <tr>
    <td>Subject:</td>
    <td>
      <@s.textfield name="subject" size="50"/>
    </td>
  </tr>
  <tr>
    <td>Message:</td>
    <td>
      <@s.textarea name="note" cols="50" rows="5" value="%{'love you. love you. love you'}"/>
    </td>
  </tr>
  <@s.submit value="Submit Feedback"/>
</@s.form>
</body>
</html>