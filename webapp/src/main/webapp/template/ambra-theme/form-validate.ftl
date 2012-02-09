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
<#if parameters.validate?default(false) == true>
	<script src="${base}/struts/xhtml/validation.js"></script>
	<#if parameters.onsubmit?exists>
		${tag.addParameter('onsubmit', "${parameters.onsubmit}; customOnsubmit(); return validateForm_${parameters.id}();")}
	<#else>
		${tag.addParameter('onsubmit', "customOnsubmit(); return validateForm_${parameters.id}();")}
	</#if>
</#if>
<#if !(fieldErrors.size() == 0)>
  <p class="required">Please correct the errors below. </p>
</#if>        
