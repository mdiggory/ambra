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

<h1><em>Ambra</em> Site Map</h1>

<h2>Home</h2>
<ul>
    <@s.url action="home" namespace="/" includeParams="none" id="homeURL"/>
    <@s.url action="rssFeeds" namespace="/static" includeParams="none" id="rssFeedURL"/>
    <@s.url action="rssInfo" namespace="/static" includeParams="none" id="rssInfoURL"/>
    <@s.url action="releaseNotes" namespace="/static" includeParams="none" id="releaseURL"/>
    <@s.url namespace="/user/secure" includeParams="none" id="loginURL" action="secureRedirect" goTo="${thisPage}"/>
	
    <li><@s.a href="${homeURL}" title="Ambra | Home page">Home page</@s.a></li>
    <li><@s.a href="${rssFeedURL}" title="Ambra | RSS Feeds">RSS Feeds</@s.a>
		  <ul>
        <li><@s.a href="${rssInfoURL}" title="Ambra | About RSS Feeds">About Ambra RSS Feeds</@s.a></li>
      </ul>
    </li>
    <li><@s.a href="${loginURL}" title="Ambra | Account Login">Login</@s.a></li>
    <li><@s.a href="${freemarker_config.registrationURL}" title="Ambra | Create a New Account">Create Account</@s.a></li>
    <li><@s.a href="${feedbackURL}" title="Ambra | Send Us Your Feedback">Send Us Feedback</@s.a></li>
    <li><@s.a href="${releaseURL}" title="Ambra | Release Notes">Release Notes</@s.a></li>
</ul>

<h2>Browse Articles</h2>
<ul>
	  <@s.url action="browse" field="date" namespace="/article" includeParams="none" id="browseDateURL"/>
    <@s.url action="browse" namespace="/article" includeParams="none" id="browseSubURL"/>
    
    <li><@s.a href="${browseDateURL}" title="Ambra | Browse by Publication Date">By Publication Date</@s.a></li>
    <li><@s.a href="${browseSubURL}" title="Ambra | Browse by Subject">By Subject</@s.a></li>
</ul>

<h2>For Readers</h2>
<ul>
    <@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="commentURL"/>
    <@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="ratingURL"/>
    <@s.url action="help" namespace="/static" includeParams="none" id="helpURL"/>
    
    <li><@s.a href="${commentURL}" title="Ambra | Guidelines for Notes, Comments, and Corrections">Guidelines for Notes, Comments, and Corrections</@s.a></li>
    <li><@s.a href="${ratingURL}" title="Ambra | Guidelines for Rating">Guidelines for Rating</@s.a></li>
    <li><@s.a href="${helpURL}" title="Ambra | Help Using this Site">Help Using This Site</@s.a></li>
</ul>

<h2>Overlay Journals</h2>
<ul>
  <li><@s.a href="#" title="Ambra Overlay Journal"><em>Ambra Overlay Journal</em></@s.a></li>
</ul>

</div>
<!-- end : main contents -->