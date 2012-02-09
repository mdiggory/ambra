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
  <h1>Browse Articles</h1>
  <p class="intro">Browse Ambra contents by:</p>
  
  <@s.url action="browse" field="date" namespace="/article" includeParams="none" id="browseDateURL"/>
  <@s.url action="browse" namespace="/article" includeParams="none" id="browseSubURL"/>
  <ul>
    <li><@s.a href="${browseDateURL}" title="Ambra | Browse by Publication Date">By Publication Date</@s.a> - Browse articles by choosing a specific week or month of publication</li>
    <li><@s.a href="${browseSubURL}" title="Ambra | Browse by Subject">By Subject</@s.a> - Browse articles published in a specific subject area</li>
  </ul>
</div>
<!-- end : main contents -->