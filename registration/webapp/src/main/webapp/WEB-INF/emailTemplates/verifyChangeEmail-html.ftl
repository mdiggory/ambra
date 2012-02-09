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
You have requested to change your account from ${user.loginName} to ${user.newLoginName}.<br/>
<br/>
Please click on this <a href="${url}?loginName=${user.loginName}&amp;emailVerificationToken=${user.emailVerificationToken}">link</a> to 
verify your new e-mail address.
<br/><br/>
Or cut and paste the link below if you have problems:
<br/>
${url}?loginName=${user.loginName}&amp;emailVerificationToken=${user.emailVerificationToken}

</body>
</html>