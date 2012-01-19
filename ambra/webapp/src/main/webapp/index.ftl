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
<div id="content">
  <!-- begin : home page wrapper -->
  <div id="wrap">
    <!-- begin : home -->
    <div id="home">
      <!-- begin : layout wrapper -->
      <div class="col">
        <!-- begin : wrapper for cols 1 & 2 -->
        <div id="first" class="col">
        <!-- removed : col 1 -->
        <!-- begin : col 2 -->
          <div class="col last">
          <!-- begin : block journal list -->
            <!-- <div class="block">
              <h2>Ambra Journal List</h2>
              <ul class="articles">
                <li><@s.a href="#" title="Read Open Access Article">
                  Journal One
                </@s.a></li>
                <li><@s.a href="#" title="Read Open Access Article">
                  Journal Two
                </@s.a></li>
                <li><@s.a href="#" title="Read Open Access Article">
                 Journal Three
                </@s.a></li>
                <li><@s.a href="#" title="Read Open Access Article">
                  Journal Four
                </@s.a></li>
				      </ul>
            </div> -->
            
            <div class="explore block">
              <h2>Journal List</h2>
              <p>(#) indicates the number of articles published in each Journal.</p>
              <ul>
                    <li>
                      <a href="#">Overlay Journal (86)</a>
                    </li>
                    <li>
                      <a href="#">Journal of Lorem Ipsum (203)</a>
                    </li>
                    <li>
                      <a href="#">Journal of Erat Volputate (93)</a>
                    </li>
                    <li>
                      <a href="#">Journal of Commodo Consequat (113)</a>
                    </li>
                    <li>
                      <a href="#">Journal fo Quis Nostrud Exerci Tation Ullamcorper (69)</a>
                    </li> 
              </ul>
              <ul>
                    <li>
                      <a href="#">Journal of Obortis Nisl ut Aliquip (94)</a>
                    </li>
                    <li>
                      <a href="#">Journal of Tincidunt ut Laoreet (147)</a>
                    </li>
                    <li>
                      <a href="#">Journal of Suscipit Lobortis Nisl ut Aliquip (102)</a>
                    </li>
                    <li>
                      <a href="#"><strong>More Journals</strong></a>
                    </li>
              </ul>
              <div class="clearer">�</div>
            </div>
            <!-- end : block journal list -->
            <!-- begin : browse widget -->
            <div id="browseWidget" class="block">
              <p>Browse Ambra Articles: <a href="${browseSubjectURL}">By Subject</a> or <a href="${browseDateURL}">By Publication Date</a></p>
            </div>
            <!-- end : browse block -->
            
            <#if categoryInfos?size gt 0>
	    
            <#assign colSize = (categoryInfos?size / 2) + 0.5>
	    
            <!-- begin : explore by subject block -->
            <div class="explore block">
              <h2>Articles by Subject</h2>
              <p>(#) indicates the number of articles published in each subject category.</p>
              <ul>
                <#list categoryInfos?keys as category>
		  <#if (category_index + 1) lte colSize>
		  <#assign categoryId = category?replace("\\s|\'","","r")>
                    <@s.url id="browseURL" action="browse" namespace="/article" catName="${category}" includeParams="none"/>
                    <li>
                      <a id="widget${categoryId}" href="${browseURL}">${category} (${categoryInfos[category]?size})</a>&nbsp;
                      <a href="${freemarker_config.context}/article/feed?category=${category?replace(' ','+')}"><img src="${freemarker_config.context}/images/feed-icon-inline.gif" /></a>
                    </li>
		  </#if>
                </#list>
              </ul>
              <ul>
                <#list categoryInfos?keys as category>
		  <#if (category_index + 1) gt colSize>
                    <#assign categoryId = category?replace("\\s|\'","","r")>
                    <@s.url id="browseURL" action="browse" namespace="/article" catName="${category}" includeParams="none"/>
                    <li>
                      <a id="widget${categoryId}" href="${browseURL}">${category} (${categoryInfos[category]?size})</a>&nbsp;
                      <a href="${freemarker_config.context}/article/feed?category=${category?replace(' ','+')}"><img src="${freemarker_config.context}/images/feed-icon-inline.gif" /></a>
                    </li>
		  </#if>
                </#list>
              </ul>
              <div class="clearer">&nbsp;</div>
            </div><!-- end : explore by subject block -->
            </#if>
            
            <!-- begin : content block -->
            <div class="other block">
              <h2>Other Content</h2>
              <div class="section">
                <h3>Browse content from our partners</h3>
                <p><a href="#">Ipsum Lorem</a>; <a href="#">Ipsum Lorem</a>; <a href="#">Ipsum Lorem</a>; <a href="#">Ipsum Lorem</a></p>
              </div>
              <div class="section lastSection">
                <h3>Browse even more content</h3>
                <p><a href="#">Ipsum Lorem</a>; <a href="#">Ipsum Lorem</a>; <a href="#">Ipsum Lorem</a>;</p>
              </div>
            </div>
            <!-- end : content block -->
          </div>
          <!-- end : col last -->
        </div>
        <!-- end : wrapper for cols 1 & 2 -->
        <!-- begin : wrapper for cols 3 & 4 -->
        <div id="second" class="col">
          <!-- begin : col 3 -->
          <div class="subcol first">
            <!-- begin : block -->
            <div class="block">
              <@s.url action="commentGuidelines" anchor="note" namespace="/static" includeParams="none" id="note"/>
              <@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="rating"/>
 	              <h3>Join the Community</h3>
                <p><a href="${freemarker_config.registrationURL}" title="Register">Register now</a> and share your views. Only registrants can add <a href="${note}" title="Guidelines for Notes, Comments, and Corrections">Notes, Comments</a>, and <a href="${rating}" title="Guidelines for Rating">Ratings</a> to articles in the Hub.</p>
            </div>
            <!-- end : block -->
            <!-- begin : block -->
            <div class="block">
              <@s.url action="checklist" namespace="/static" includeParams="none" id="checklist"/>
              <h3>Submit Your Work</h3>
              <p>Tincidunt ut laoreet dolore magna aliquam erat volputate. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
            </div>
            <!-- end : block -->
            <!-- begin : stay-connected block -->
            <div id="connect" class="block">
              <h3>Stay Connected</h3>
              <ul>
                  <li><img src="images/icon_rss_small.gif" alt="rss icon" /><@s.url action="rssInfo" namespace="/static" includeParams="none" id="rssinfo"/><a href="${Request[freemarker_config.journalContextAttributeKey].baseUrl}${rssPath}"><strong>RSS</strong></a> (<a href="${rssinfo}">What is RSS?</a>)<br />Subscribe to content feed</li>
                  <li><img src="images/icon_join.gif" alt="Join Us" /><a href="${freemarker_config.registrationURL}" title="Join Us: Show Your Support"><strong>Join Us</strong></a><br />Support our organization!</li>
              </ul>
            </div>
            <!-- end : stay-connected block -->
          </div>
          <!-- end : subcol first -->
          <!-- end : col 3 -->
          <!-- begin : col 4 -->
          <div class="subcol last">
            <!-- begin : block banner -->
            <div class="block banner"><!--skyscraper-->
              <a href="#"><img src="images/adBanner_placeholder_120x600.png" alt=""/></a>
            </div>
            <!-- end : block banner -->
          </div>
          <!-- end : subcol last -->
        </div>
        <!-- end : wrapper for cols 3 & 4 -->
        <div id="lower">&nbsp;</div> <!-- displays lower background image -->
      </div>
      <!-- end : col -->
      <!-- begin : partners block -->
      <div class="partner">
        <a href="http://www.fedora-commons.org" title="Fedora-Commons.org"><img src="${freemarker_config.context}/images/home_fedoracommons.png" alt="Fedora-Commons.org"/></a>
        <a href="http://www.mulgara.org/" title="Mulgara.org"><img src="${freemarker_config.context}/images/home_mulgara.gif" alt="Mulgara.org"/></a>
      </div>
      <!-- end : partners block -->
    </div>
    <!-- end : home -->
  </div>
  <!-- end : home page wrapper -->
</div>
<!-- end : main content -->
