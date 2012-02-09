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
<!-- begin : main content -->
<div id="content" class="static">

<h1>For Readers</h1>


<p class="intro">If you need help using or finding something on the site, select one of the following links:</p>
  
<@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="commentURL"/>
<@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="ratingURL"/>
<@s.url action="help" namespace="/static" includeParams="none" id="helpURL"/>
<@s.url action="sitemap" namespace="/static" includeParams="none" id="siteURL"/>

<ul>
    <li><@s.a href="${commentURL}" title="Ambra Journal | Guidelines for Notes, Comments, and Corrections">Guidelines for Notes, Comments, and Corrections</@s.a> - Guidelines for adding, viewing, and responding to Notes, Comments, and Corrections.</li>
    <li><@s.a href="${ratingURL}" title="Ambra Journal | Guidelines for Rating">Guidelines for Rating</@s.a> - Guidelines for using the article rating system</li>
    <li><@s.a href="${helpURL}" title="Ambra Journal | Help Using this Site">Help Using this Site</@s.a> - Answers to common questions</li>

    <li><@s.a href="${siteURL}" title="Ambra Journal | Site Map">Site Map</@s.a> - Directory of main pages on the <em>Ambra Journal</em> site</li>

</ul>
		
</div>
<!-- end : main contents -->
