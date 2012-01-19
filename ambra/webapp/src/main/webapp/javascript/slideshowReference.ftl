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
<#list secondaryObjects as image>
  <@s.url id="imageUrl" namespace="/article" includeParams="none"  action="fetchObject" uri="${image.uri}"/>
  <@s.url id="imageAttachUrl" namespace="/article" includeParams="none"  action="fetchObjectAttachment" uri="${image.uri}"/>
  <@s.url id="imageLargeUrl" namespace="/article" includeParams="none"  action="showImageLarge" uri="${image.uri}"/>
	ambra.slideshow.slides[${image_index}] = {imageUri: '${imageUrl?js_string}', imageLargeUri: '${imageLargeUrl}', imageAttachUri: '${imageAttachUrl?js_string}',
	                title: '<strong>${image.title?js_string}.</strong> ${image.transformedCaptionTitle?js_string}',
	                titlePlain: '${image.title?js_string} ${image.plainCaptionTitle?js_string}',
	                description: '${image.transformedDescription?js_string}'};
</#list>