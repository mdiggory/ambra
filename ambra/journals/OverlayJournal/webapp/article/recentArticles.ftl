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
<!-- Recent Research -->
<ul class="articles">
  <@s.url id="art1URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>
	<li><a href="${art1URL}" title="Read Open Access Article">
	Tego Regula Refero Vindico, Foras, Minim Proprius Melior Blandit Occuro eros Dignissim Causa Exputo
	</a></li>
	
  <@s.url id="art2URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>
	<li><a href="${art2URL}" title="Read Open Access Article">
	Et Abluo Patria, Metuo Iusto Sagaciter Exerci
	</a></li>
	
  <@s.url id="art3URL" namespace="/article" action="fetchArticle" includeParams="none" articleURI="#"/>
	<li><a href="${art3URL}" title="Read Open Access Article">
	Huic Brevitas Iustum Multo Distineo vel Vicis
	</a></li>
		
	<!-- Do not edit below this comment -->
	<@s.url action="browse" namespace="/article" field="date" includeParams="none" id="browseDateURL"/>
	<li class="more"><a href="${browseDateURL}">Browse all recently published articles</a></li>
</ul>
