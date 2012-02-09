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
    <div id="home">
      <!-- begin : layout wrapper -->
      <div class="col">
        <!-- begin : wrapper for cols 1 & 2 -->
        <div id="first" class="col">
        <!-- removed : col 1 -->
        <!-- begin : col 2 -->
          <!-- begin : block -->
          <div class="col last">
            <div id="info" class="block">
             <@s.url action="information" namespace="/static" includeParams="none" id="info"/>
              <h2>What is the Overlay Journal?</h2>
              <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh eusmod tincidunt ut laoreet dolore magna aliquam erat volputate. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
              <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait facilisi.</p>
            </div>
            <!-- end : block -->

            <!-- begin : horizontalTabs -->
            <div class="horizontalTabs">
              <ul id="tabsContainer"></ul>
              <div id="tabPaneSet" class="contentwrap">
                <#include "/article/recentArticles.ftl">
              </div>
            </div>
            <!-- end : horizontalTabs -->
            <!-- begin : content block -->
            <div class="block">
              <h2>Featured Content</h2>
              <@s.url id="featured1" namespace="/article" action="fetchArticle" articleURI="info:doi/10.1371/journal.pgen.1000020"/>
              <@s.url id="featured2" namespace="/article" action="fetchArticle" articleURI="info:doi/10.1371/journal.pgen.1000002"/>
              <div class="article section">
                <h3><@s.a href="${featured1}" title="Read Open-Access Article">Velit Esse Molestie Consequat, vel Illum Dolore eu Feugiat Nulla Facilisis</@s.a></h3>
                <img src="images/thumbPlaceholder_90x90.jpg" alt="article image" />
                <p>At vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zril delenit augue duis dolore te feugait facilisi. Oppeto sino metuo premo regula reprobo utinam.</p>
                <p class="imgCredit">Image Credit: Paul E. Roid, Paparazzi University</p>
                <div class="clearer">&nbsp;</div>
              </div>
              <div class="article section lastSection">
                <h3><@s.a href="${featured2}" title="Read Open-Access Article">Consectetuer Adipiscing Elit, sed Diam Nonummy Nibh</@s.a></h3>
                <img src="images/thumbPlaceholder_90x90.jpg" alt="article image" />
                <p>Eusmod tincidunt ut laoreet dolore magna aliquam erat volputate <@s.a href="${featured1}" title="Read Open-Access Article">Ut wisi enim ad minim</@s.a> veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
                <div class="clearer">&nbsp;</div>
              </div>
            </div>
            <!-- end : content block -->
            <!-- begin : other block -->
            <div class="other block">
              <h2>Highlights From Other Journals</h2>
              <div class="section">
                <h3><a href="#"><em>Journal Ipsum</em></a></h3>
                <ul class="articles">
                  <li><a href="#" title="Read Open Access Article">Huic Brevitas Iustum Multo Distineo vel Vicis</a></li>
                </ul>
              </div>
              <div class="section">
                <h3><a href="#"><em>Journal Lorem</em></a></h3>
                <ul class="articles">
                  <li><a href="#" title="Read Open Access Article">Consectetuer Adipiscing Elit, sed Diam Nonummy Nibh</a></li>
                  <li><a href="#" title="Read Open Access Article">Velit Esse Molestie Consequat, vel Illum Dolore eu Feugiat Nulla Facilisis</a></li>
                </ul>
              </div>
              <div class="section lastSection">
                <h3><a href="#"><em>Journal Dolor</em></a></h3>
                <ul class="articles">
                  <li><a href="#" title="Read Open Access Article">Nostrud Xerci Tation Ulamcorper Suscipit Lobortis Nisl ut Aliquip ex ea Commodo Consequat.</a></li>
                </ul>
              </div>
            </div>
            <!-- end : other block -->
          </div>
          <!-- end : col last -->
        </div>
        <!-- end : wrapper for cols 1 & 2 -->
        <!-- begin : wrapper for cols 3 & 4 -->
        <div id="second" class="col">
          <!-- begin : col 3 -->
          <div class="subcol first">
            <!-- begin : issue block -->
            <div id="issue" class="block"><h3><a href="#">Current Issue</a></h3><a href="#"><img src="images/issueImage_placeholder_251x251.jpg" alt="issue cover image" /></a></div><!-- keep div#issue hmtl all on one line to avoid extra space below issue image in IE -->
            <!-- end : issue block -->
            <!-- begin : mission block -->
            <div id="mission" class="block">
              <p><strong><em><a href="#">Overlay Journal</a></em></strong> is a paulatim singularis, caecus nutus, mara melior euismod. Scisco lobortis dolore vulputate demoveo pala. Autem nunc suscipere ad in in vereor quis patria.</p>
            </div>
            <!-- end : mission block -->
            <!-- begin : block -->
            <div class="block">
              <@s.url action="commentGuidelines" anchor="note" namespace="/static" includeParams="none" id="note"/>
              <@s.url action="ratingGuidelines" namespace="/static" includeParams="none" id="rating"/>
                 <h3>Join the Community</h3>
                <p><a href="${freemarker_config.registrationURL}" title="Register">Register now</a> and share your views. Only registrants can add <a href="${note}" title="Guidelines for Notes, Comments, and Corrections">Notes, Comments</a>, and <a href="${rating}" title="Guidelines for Rating">Ratings</a> to articles in the Overlay Journal.</p>
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
            <!-- begin : block -->
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
        <div id="lower">&nbsp;</div>
      </div>
      <!-- end : col -->
      <!-- begin : block partners -->
      <div class="partner">
        <a href="http://www.fedora-commons.org/" title="Fedora-Commons.org"><img src="images/home_fedoracommons.png" alt="Fedora-Commons.org"/></a>
        <a href="http://www.mulgara.org/" title="Mulgara.org"><img src="images/home_mulgara.gif" alt="Mulgara.org"/></a>
      </div>
      <!-- end : block partners -->
    </div>
    <!-- end : home -->
  </div>
  <!-- end : wrap -->
</div>
<!-- end : content -->
