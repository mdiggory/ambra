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
  <ul id="nav">
    <li><a href="${homeURL}" tabindex="101">Home</a></li>
    <@s.url action="browse" namespace="/static" includeParams="none" id="browseURL"/>
    <@s.url action="browse" namespace="/article" includeParams="none" id="browseSubjectURL"/>
    <@s.url action="browse" namespace="/article" field="date" includeParams="none" id="browseDateURL"/>
    <li><a href="${browseURL}" tabindex="102">Browse Articles</a>
        <ul>
          <li><a href="${browseDateURL}">By Publication Date</a></li>
          <li><a href="${browseSubjectURL}">By Subject</a></li>
        </ul>
    </li>
    <@s.url action="users" namespace="/static" includeParams="none" id="users"/>
    <li><a href="${users}" tabindex="104">For Readers</a>
        <ul>
        <@s.url action="commentGuidelines" namespace="/static" includeParams="none" id="comment"/>
        <@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="rating"/>
        <@s.url action="help" namespace="/static" includeParams="none" id="help"/>
        <@s.url action="sitemap" namespace="/static" includeParams="none" id="site"/>
          <li><a href="${comment}">Guidelines for Notes, Comments, and Corrections</a></li>
          <li><a href="${rating}">Guidelines for Rating</a></li>
          <li><a href="${help}">Help Using this Site</a></li>
          <li><a href="${site}">Site Map</a></li>
        </ul>
      </li>
			<#assign hubUrl = '#' />
      <li class="journalnav"><a href="${hubUrl}" tabindex="109">Overlay Journals</a>
        <ul>
          <li><a href="${hubUrl}" title="Ambra Overlay Journal">Ambra Overlay Journal</a></li>
        </ul>
      </li>
    </ul>
