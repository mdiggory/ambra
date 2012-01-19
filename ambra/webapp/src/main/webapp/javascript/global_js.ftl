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
<script type="text/javascript">
  var _namespace="${freemarker_config.getContext()}";
	<#if Session?exists && Session[freemarker_config.userAttributeKey]?exists>
		var loggedIn = true;
	<#else>
  	var loggedIn = false;
	</#if>

  // Safari v3.1.1 "console.debug" issue (http://trac.dojotoolkit.org/ticket/6849) workaround
  if (/3[\.0-9]+ Safari/.test(navigator.appVersion)) {
    window.console = {
      origConsole: window.console,
      log: function(s){ this.origConsole.log(s); },
      info: function(s){ this.origConsole.info(s); },
      error: function(s){ this.origConsole.error(s); },
      warn: function(s){ this.origConsole.warn(s); }
    };
  }

  var djConfig = {
    // don't debug for IE - as dojo's firebug lite module is error prone in IE
		isDebug: false,
    parseOnLoad: true
	};
</script>
<#list freemarker_config.getJavaScript(templateFile, journalContext) as x>
	<#if x?ends_with(".ftl")>
<script type="text/javascript">
<#include "${x}">
</script>	
  <#elseif x?contains("dojo.js")>
    <#if freemarker_config.dojoDebug>
<script type="text/javascript" src="${freemarker_config.context}/javascript/dojo/dojo/dojo.js.uncompressed.js"></script>
    <#else>
<script type="text/javascript" src="${freemarker_config.context}/javascript/dojo/dojo/dojo.js"></script>
    </#if>
	<#elseif x?contains("ambra.js")>
    <#if freemarker_config.dojoDebug>
<script type="text/javascript" src="${freemarker_config.context}/javascript/dojo/dojo/ambra.js.uncompressed.js"></script>
    <#else>
<script type="text/javascript" src="${freemarker_config.context}/javascript/dojo/dojo/ambra.js"></script>
    </#if>
  <#else>
<script type="text/javascript" src="${x}"></script>	
	</#if>
</#list>