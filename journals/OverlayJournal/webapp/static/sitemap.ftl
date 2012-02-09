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

<h1><em>Overlay Journal</em> Site Map</h1>

<h2>Home</h2>
<ul>
    <@s.url action="home" namespace="/" includeParams="none" id="homeURL"/>
    <@s.url action="rssFeeds" namespace="/static" includeParams="none" id="rssFeedURL"/>
    <@s.url action="rssInfo" namespace="/static" includeParams="none" id="rssInfoURL"/>
    <@s.url action="releaseNotes" namespace="/static" includeParams="none" id="releaseURL"/>
    <@s.url namespace="/user/secure" includeParams="none" id="loginURL" action="secureRedirect" goTo="${thisPage}"/>
	
    <li><@s.a href="${homeURL}" title="Overlay Journal | Home page">Home page</@s.a></li>
    <li><@s.a href="${rssFeedURL}" title="Overlay Journal | RSS Feeds">RSS Feeds</@s.a>
		  <ul>
        <li><@s.a href="${rssInfoURL}" title="Overlay Journal | About RSS Feeds">About Overlay Journal RSS Feeds</@s.a></li>
      </ul>
    </li>
    <li><@s.a href="${loginURL}" title="Overlay Journal | Account Login">Login</@s.a></li>
    <li><@s.a href="${freemarker_config.registrationURL}" title="Overlay Journal | Create a New Account">Create Account</@s.a></li>
    <li><@s.a href="${feedbackURL}" title="Overlay Journal | Send Us Your Feedback">Send Us Feedback</@s.a></li>
    <li><@s.a href="${releaseURL}" title="Overlay Journal | Release Notes">Release Notes</@s.a></li>
</ul>

<h2>Browse Articles</h2>
<ul>
	<@s.url action="browseIssue" field="issue" namespace="/article" includeParams="none" id="browseIssueURL"/>
	<@s.url action="browse" field="date" namespace="/article" includeParams="none" id="browseDateURL"/>
    <@s.url action="browse" namespace="/article" includeParams="none" id="browseSubURL"/>
	
	<li><@s.a href="${tocStatic}" title="Overlay Journal | Current Issue">Current Issue</@s.a></li><!-- Note that this is a temporary var to static TOC. Once dynamic TOC is in place, should be changed back to "browseIssueURL" -->
    <li><@s.a href="${browseDateURL}" title="Overlay Journal | Browse by Publication Date">By Publication Date</@s.a></li>
    <li><@s.a href="${browseSubURL}" title="Overlay Journal | Browse by Subject">By Subject</@s.a></li>
</ul>

<h2>For Readers</h2>
<ul>
    <@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="commentURL"/>
    <@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="ratingURL"/>
    <@s.url action="help" namespace="/static" includeParams="none" id="helpURL"/>
    <@s.url action="downloads" namespace="/static" includeParams="none" id="downloadsURL"/>
	
    <li><@s.a href="${commentURL}" title="Overlay Journal | Guidelines for Notes, Comments, and Corrections">Guidelines for Notes, Comments, and Corrections</@s.a></li>
    <li><@s.a href="${ratingURL}" title="Overlay Journal | Guidelines for Rating">Guidelines for Rating</@s.a></li>
    <li><@s.a href="${helpURL}" title="Overlay Journal | Help Using this Site">Help Using This Site</@s.a></li>
</ul>

<h2>Overlay Journals</h2>
<ul>
  <li><@s.a href="#" title="Overlay Journal"><em>Overlay Journal</em></@s.a></li>
</ul>

</div>
<!-- end : main contents -->