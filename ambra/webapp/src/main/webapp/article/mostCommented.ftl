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
<!-- Recently Annotated -->
<ul class="articles">
  <@s.url id="art1URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>
	<li><a href="${art1URL}" title="Read Open Access Article">
	Lorem Ipsum Dolor sit Amet, Consectetuer Adipiscing Elit
	</a></li>
	
  <@s.url id="art2URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>	
	<li><a href="${art2URL}" title="Read Open Access Article">
	Ut Wisi Enim ad Minim Veniam, Quis Nostrud Exerci Tation Ullamcorper Suscipit Lobortis Nisl ut Aliquip ex ea Commodo
	</a></li>
	
  <@s.url id="art3URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>		
	<li><a href="${art3URL}" title="Read Open Access Article">
	Duis Autem vel eum Iriure Dolor in Hendrerit in Vulputate Velit Esse Molestie Consequat, vel Illum Dolore eu Feugiat Nulla Facilisis
	</a></li>
  
  <!-- Do not edit below this comment -->
	<@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="comment"/>
	<li class="more">Learn how to <a href="${comment}">add comments and start discussions</a> on articles.</li>
</ul>
