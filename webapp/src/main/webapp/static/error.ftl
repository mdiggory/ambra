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
<!-- begin : main content wrapper -->
<div id="content" class="pageerror">
<@s.url action="feedbackCreate.action" namespace="/static" includeParams="none" id="feedback"/>
<h1>Site Error</h1>
	<p>Sorry, the page you were trying to view cannot be displayed. If you were submitting information, please try again in a few minutes.</p>
	<p>If you want to report an error, please provide a detailed account of the circumstances and e-mail <a href="mailto:${freemarker_config.feedbackEmail}">${freemarker_config.feedbackEmail}</a>.</p>
	<p><strong>Thank you for your patience.</strong></p>
	
</div>
<!-- end : main content wrapper -->