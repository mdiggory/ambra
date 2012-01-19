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
	<title>${freemarker_config.getTitle(templateFile)}</title>

	<#list freemarker_config.getCss(templateFile) as x>
      <link rel="stylesheet" type="text/css" media="screen" href="${x}" />
  </#list> 

	<#list freemarker_config.getJavaScript(templateFile) as x>
     <script language="javascript" type="text/javascript" src="${x}"></script>
  </#list> 

	</head>
	<body>

	