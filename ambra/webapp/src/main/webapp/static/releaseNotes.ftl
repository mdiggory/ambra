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
  <h1>Release Notes</h1>
  <ul>
        <li><a href="#features">Features Updates</a></li>
        <li><a href="#credits">Site Credits</a></li>
  </ul>
  
  <a name="features"></a>
  <h2>Features Updates</h2>
  <p>
    This site is published using the Open Source journal management and publishing framework being developed by
    <a href="http://www.ambraproject.org/" title="Ambra | Open Source journal management and 
    publishing system">Ambra</a>. This site is updated regularly as new features
    become available. This page summarizes features and known bugs in each updated version.
  </p>

    <h2>Ambra 2.0</h2>
    <p>Released November 11, 2011</p>
    <h3>Features Implemented in Ambra 2.0</h3>
    <ul>
        <li><strong>Topaz, Fedora and Mulgara are no longer supported.</strong></li>
        <li>Ambra 2.0 uses a back-end architecture comprised of <a href="http://danga.com/mogilefs/">MogileFS</a>, 
        an open source distributed filesystem, and <a href="http://www.mysql.com/">MySQL</a>, an open source 
        relational database. <a href="http://www.hibernate.org/">Hibernate</a> has replaced Topaz as the 
        <a href="http://en.wikipedia.org/wiki/Object-relational_mapping">object-relational
        mapping (ORM)</a> library.</li>
    </ul>
    
    <h2>Ambra 1.7 </h2>
    <p>Released August 4, 2011</p>
    <h3>Features Implemented in Ambra 1.7</h3>
    <ul>
        <li>Upgrade to <a href="http://www.jasig.org/cas">CAS 3.4.7</a>. CAS (Central Authentication Service) is the single sign-on used by Ambra.</li>
        <li>Migrate CAS from the <a href="http://www.postgresql.org/">PostgeSQL</a> database to the <a href="http://www.mysql.com/">MySQL</a> database.</li>
        <li>Upgrade to <a href="http://struts.apache.org/">Struts 2.1.8</a>.</li>
        <li>Allow italic, bold, superscript and subscript HTML markup in notes and comments.</li>
        <li>Added <i>citation_journal_title</i> to the Google Scholar indexing metadata.</li>
        <li>Bug fixes:</li>
        <ul>
            <li>In <i>Browse By Publication Date</i>, clicking on a month hyperlink was not displaying the appropriate articles.</li>
            <li><i>Print Article</i> button on article tabs would print the article tab content, not the actual article.</li>
            <li>Entities added to Correction Notes were not displaying correctly.</li>
            <li>Superscripted Xrefs were not displaying correctly in the article slideshow.</li>
        </ul>
   </ul>
   
    <h2>Ambra 1.6 </h2>
    <p>Released June 10, 2011</p>
    <h3>Features Implemented in Ambra 1.6</h3>
    <ul>
        <li>Added figure and table captions to advanced search.</li>
        <li>Decoupled sending CrossRef metadata from the publishing process. This will allow article publishing to continue if there is a failure sending the CrossRef metadata.</li>
        <li>Updated the CrossRef deposit schema to version 4.3.0.</li>
        <li>Improvements to the Google Scholar indexing metadata in the article HTML:</li>
        <ul>
            <li>Fixed corresponding authors and publisher fields.</li>
            <li>Added citing articles.</li>
        </ul>
   </ul>
   
   <h2>Ambra 1.5 </h2>
    <p>Released April 22, 2011</p>
    <h3>Features Implemented in Ambra 1.5</h3>
    <ul>
        <li>Replaced the Browse by Publication Date and Browse by Subject Categories to use the Solr API. Article-level Metrics is now included in the browse results.</li>
        <li>RSS feeds are now pulling research article content directly from the Solr API.</li> 
        <li>A number of bugs were fixed in the RSS feeds:</li>
        <ul>
            <li>The complete list of authors, including collaboration authors, has been added to each article.</li>
            <li>The article publication time was changed from 2011–04–21T07:00:00Z to 2011–04–21T00:00:00Z .</li>
            <li>HTML formatting has been removed from article titles.</li>
            <li>The main article abstract is now included in the RSS feed.</li>
        </ul>
        <li>Updated the templates used for user registration, login and profile pages.
        <li>Fixed the copyright XSL transform to accommodate a new public domain wording.</li>
   </ul>
   
 <h2>Ambra 1.4 </h2>
    <p>Released March 11, 2011</p>
    <h3>Features Implemented in Ambra 1.4</h3>
    <ul>
        <li>Added Google Scholar indexing metadata in the article HTML. See <a href="http://scholar.google.com/intl/en/scholar/inclusion.html">Google Scholar's indexing guidelines</a> for more information.</li>
        <li>Updates to the Administration page:</li>
        <ul>
            <li>Add the article publication date.</li>
            <li>Sort articles by DOI or the article publication date.</li>
            <li>Select all or none for publication of articles.</li>
        </ul>
        <li>Added collaboration authors to articles on the Browse By pages and Issue Table of Contents.</li>
        <li>Support for mp4 and mv4 mime types.</li>
   </ul>

 <h2>Ambra 1.3 </h2>
    <p>Released November 18, 2010</p>
    <h3>Features Implemented in Ambra 1.3</h3>
    <ul>
        <li>Added an auto-ingest feature to allow for scheduling the ingest of articles.</li>  
   </ul>

  <h2>Ambra 1.2 </h2>
    <p>Released September 24, 2010</p>
    <h3>Features Implemented in Ambra 1.2</h3>
    <ul>
        <li>XSL stylesheet improvements for NLM DTD 2.3.</li>  
        <li>Update Ambra to use Topaz 0.9.5.3.  This release of Topaz removes objects filters and improves query performance.</li>
   </ul>

  <h2>Ambra 1.1 </h2>
    <p>Released July 22, 2010</p>
    <h3>Features Implemented in Ambra 1.1</h3>
    <ul>
        <li>Support for NLM DTD 2.3 and new XSL stylesheets.  The single XSL stylesheet has been split into two stylesheets: a generic XSL stylesheet to handle NLM DTD 2.3 and an XSL stylesheet specifically for Ambra.</li>
        <li>Support for HTML iframes for advertising blocks.</li>  
        <li>Caching of CrossRef search results on the "Find this Article Online" page.</li>
        <li>Updates to the copyright information.</li>
        <li>Bug fixes:</li>
        <ul>
            <li>Fix to author and editor search facet sorting.</li>
        </ul>
   </ul>

  <h2>Ambra 1.0 </h2>
    <p>Released June 2, 2010</p>
    <h3>Features Implemented in Ambra 1.0</h3>
    <ul>
        <li>Now using Solr/Lucene for search with new look and feel and increased performance:</li>
            <ul>
                <li>Integrate ALM/COUNTER data into search.</li>
                    <ul>
                        <li>Display number of Downloads by document type, Number and source of Cited and bookmarked.</li>
                        <li>Ability to sort results by date and most viewed.</li>
                    </ul>
                <li>Faceted searching of result set by Journal, Subject Category, Article Type, Where my keywords appear.</li>
                <li>See a list of your Most Recent Searches.</li>
                <li>Explore More about the Authors, Academic Editors and Institutions in the result set.</li>
            </ul>
        <li>Reciprocal Linking in Research Articles.</li>  
        <li>Remove formal corrections and retractions from ToC.</li>
        <li>Enable Odyssey Press EZ Reprint servic.</li>
        <li>Enable PubGet on Cross Ref Page.</li>
        <li>Automate most recently published blocks.</li>
        <li>Add cross ref links for article references.</li>
        <li>Add Twitter to Share This Article.</li>
        <li>Update Dojo to 1.4.</li>
   </ul>
  
    <p>For a complete list of 1.0 features and bug fixes, please see the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=1.0.0_rc1" title="Ambra 1.0.0 RC1 Tickets">Ambra 1.0.0 RC1 Tickets</a>
    , <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=1.0.0_rc2" title="Ambra 1.0.0 RC2 Tickets">Ambra 1.0.0 RC2 Tickets</a>
    and <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=1.0.0_rc3" title="Ambra 1.0.0 RC3 Tickets">Ambra 1.0.0 RC3 Tickets</a>
    </p>

  <h2>Ambra 0.9.6 beta</h2>
  <p>Released January 6, 2010</p>
  <h3>Features Implemented in Ambra 0.9.6</h3>
  <ul>
        <li>No new features were implemented. Happy New Year!</li>
        <li>Bug fixes:</li>
        <ul>
            <li>Changed the article RDFa subject from the publisher to the article URL.</li>
            <li>Minor fixes to Email This Article.</li>
            <li>Encode the plus sign (+) for new user confirmation emails.</li>
            <li>Minor fixes to annotation display.</li>
        </ul>
  </ul>

  <p>For a complete list of 0.9.6 features and bug fixes, please see the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=0.9.6_rc1" title="Ambra 0.9.6 RC1 Tickets">Ambra 0.9.6 RC1 Tickets</a></p>

  <h2>Ambra 0.9.5 beta</h2>
  <p>Released December 3, 2009</p>
  <h3>Features Implemented in Ambra 0.9.5</h3>
    <ul>
        <li>Added a new syndication service to the Admin panel.  This will allow administrators to export article files and metadata to external sources.  PLoS is 
        using this syndication service with an internal application built on <a href="http://camel.apache.org/" title="Apache Camel">Apache Camel</a> to send published files to 
        <a href="http://www.ncbi.nlm.nih.gov/pmc/" title="PubMed Central">PubMed Central</a> and other external repositories.  This is not an out-of-the-box solution as only the service hooks are available in Ambra.</li>
        <li>Added <a href="http://en.wikipedia.org/wiki/RDFa" title="RDFa in Wikipedia">RDFa</a> for the <a href="http://en.wikipedia.org/wiki/Dublin_core" title="Dublin Core in Wikipedia">Dublin Core</a> elements in the article HTML.</li>
        <li>Added a new action to support a Google Scholar XML feed for published articles.</li>
        <li>Update to the article RHC to display articles cross-published in other journals.</li>
        <li>Bug fixes:</li>
        <ul>
            <li>Promote a note to a formal correction and the annotation body was removed.</li>
            <li>Templates from other journals were displayed if freemarker.properties template_update_delay was set.</li>
            <li>Templates changes to fix a number of formatting errors encountered in IE6, IE7 and IE8.</li>
            <li>Minor fixes to annotation displays, formal corrections, formatting and messaging.</li>
        </ul>
    </ul>

  <p>For a complete list of 0.9.5 features and bug fixes, please see the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=0.9.5_rc1" title="Ambra 0.9.5 RC1 Tickets">Ambra 0.9.5 RC1 Tickets</a> 
  and the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=0.9.5_rc2" title="Ambra 0.9.5 RC2 Tickets">Ambra 0.9.5 RC2 Tickets</a></p>

  <h2>Ambra 0.9.4 beta</h2>
  <p>Released September 11, 2009</p>
  <h3>Features Implemented in Ambra 0.9.4</h3>
    <ul>
        <li>Maven build separation of Ambra and Topaz.  Ambra can now be built independently of Topaz.</li>
        <li>A new article tab in the PLoS templates to display a graph with article pageviews, PDF downloads and XML downloads.</li>
        <li>Bug fixes:</li>
        <ul>
            <li>Fixed a prepare-sip NullPointerException for articles that have different DOI format.</li>
            <li>Headings in Issue and Collection table of contents are made plural when appropriate.</li>
            <li>The "Page Not Found" error page, "Site Error" page and an incorrect article DOI return correct Apache error codes.</li>
            <li>CSS fixes.</li>
        </ul>
    </ul>

  <p>For a complete list of 0.9.4 features and bug fixes, please see the <a href="http://ambraproject.org/trac/query?status=closed&group=status&order=priority&milestone=0.9.4_rc1" title="Ambra 0.9.4 RC1 Tickets">Ambra 0.9.4 RC1 Tickets</a></p>


  <h2>Ambra 0.9.3 beta</h2>
  <p>Released August 7, 2009</p>
  <h3>Features Implemented in Ambra 0.9.3</h3>
    <ul>
      <li>Allow users to search across multiple journals.</li>
      <li>Display <a href="http://www.crossref.org/" title="CrossRef">CrossRef</a> citations per article.  A new action was created to display citations to articles as recorded by <a href="http://www.crossref.org/" title="CrossRef">CrossRef</a>.</li>
      <li> Allow admins to override annotation citation information.  Correction citations used to be dynamically generated from the article information. If this article information was part of the correction (e.g. title, author name), then the error is propagated in the Correction citation.  Now, admins can update the citation information for any annotation to display the correct citation.</li>
      <li>Display plural section headings in BrowseIssue.action if more than one article is displayed (configurable in ambra.xml).</li>
      <li>Display an explanation icon per article type (configurable in ambra.xml).</li>
      <li>Provide the article DOI in the page source.  This will be used for serving targeted advertisements.</li>
      <li>A number of outstanding bugs were fixed.</li>
      <li>Separation of the PLoS and Ambra journal templates and CSS re-organization.</li>
      <li>A new annotation feed that includes new notes, comments and ratings per journal.</li>
    </ul>

  <p>For a complete list of 0.9.3 features and bug fixes, please see the <a href="http://ambraproject.org/trac/query?status=closed&group=status&order=priority&milestone=0.9.3_rc1" title="Ambra 0.9.3 RC1 Tickets">Ambra 0.9.3 RC1 Tickets</a>,
  the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=0.9.3_rc2" title="Ambra 0.9.3 RC3 Tickets">Ambra 0.9.3 RC3 Tickets</a>
  and the <a href="http://ambraproject.org/trac/query?status=closed&group=resolution&milestone=0.9.3_rc3" title="Ambra 0.9.3 RC3 Tickets">Ambra 0.9.3 RC3 Tickets</a></p>

  <h2>Ambra 0.9.2 beta</h2>
  <p>Released March 25, 2009</p>
  <h3>Features Implemented in Ambra 0.9.2</h3>
    <ul>
      <li>Redesigned article page to accommodate new features and give a better visual experience to the user.</li>
      <li>Tabs are included on the article page for Related Content and Comments.</li>
      <li>Data from external sources is provided on the <strong>Related Content</strong> tab.  Sources include the number of citations from <a href="http://www.pubmedcentral.nih.gov/">PubMed Central</a> and <a href="http://www.scopus.com/">Scopus</a>; the number of bookmarks from <a href="http://www.citeulike.org/">CiteULike</a> and <a href="http://www.connotea.org/">Connotea</a>; and the number of blog posts linking to the article from <a href="http://www.postgenomic.com/">Postgenomic</a>, <a href="http://blogs.nature.com/">Nature Blogs</a> and <a href="http://www.bloglines.com/">Bloglines</a>.  More sources will be added in the future.</li>
      <li>Links to related articles appear in the right hand column of the article page.</li>
      <li>Links to the appropriate issue or collection appear in the right hand column of the article page.</li>
      <li>Competing interest statements were added to all notes, comments and ratings.</li>
      <li>Related subject categories can be displayed in the right hand column of the article page and the Related Content tab.</li>
      <li>The creation of retraction annotation types.</li>
      <li>The Most Recently Published homepage block can be configured to display a set number of articles, articles from within a published date range and an article white list (e.g. display only research articles).</li>
      <li>Articles can be ordered within an issue and in the table of contents.</li>
      <li>Formal corrections and retractions are now displayed in the table of contents next to the appropriate article.</li>
      <li>The administration portal was overhauled to provide a better workflow for creating volumes and issues.</li>
      <li>The administration portal was updated to allow manual ordering of articles within an issue.</li>
    </ul>
  <p>For more detail on Ambra 0.9.2, see the <a href="http://www.ambraproject.org/trac/query?status=new&status=assigned&status=reopened&status=closed&milestone=0.9.2&order=priority" title="Ambra 0.9.2 Tickets">Ambra 0.9.2 Tickets</a></p>

  <h2>RC 0.9.1 beta</h2>
        <p>Released January 29, 2009</p>
        <h3>Features Implemented in RC 0.9.1 beta</h3>
        <ul>
                <li>Revamp of Ambra search to use Mulgara (does not include update to the search UI)</li>
                <li>Support of article packages greater than 4Gb in size</li>
                <li>Support of article packages in tar format</li>
                <li>Support for very large assets (e.g. supporting info files) outside of memory</li>
                <li>Fixes to formal corrections</li>
                <li>Fixes to citations</li>
                <li>Fixes to citation downloads</li>
                <li>Article-level feeds for top-level notes/comments on an article</li>
                <li>Updates to admin panel for administering notes/comments</li>
                <li>Many fixes to the UI including line wrap for long gene sequences, browser-specific bugs, wrapping long URLs in annotations/articles, etc.</li>
                <li>Cleanup of content models, back-end bug fixes, etc.</li>
        </ul>

        <h2>RC 0.9.0 beta</h2>
        <p>Released September 1, 2008</p>
        <h3>Features Implemented in RC 0.9.0 beta</h3>
        <ul>
                <li>Upgrade to Mulgara 2.0.1</li>
                <li>Upgrade to Ehcache 1.4.1</li>
                <li>Moved to Bitronix Transaction Manager because of bug in JOTM</li>
                <li>Object level caching for applications</li>
                <li>Rework of ingest to perform image processing pre-ingest and improve performance</li>
                <li>Upgrade to Dojo 1.1</li>
                <li>Mulgara write-lock cleanup</li>
                <li>Many enhancements to improve performance and stability</li>
                <li>Many fixes to the UI including volumes/issues, correction annotations, ratings, slideshow figures, etc.</li> 
                <li>Cleanup of content models, back-end bug fixes, etc.</li>
        </ul>

    <h2>RC 0.9.0 rc1 beta</h2>
    <p>Released July 16, 2008</p>
    <h3>Features implemented in RC 0.9.0 rc1 beta</h3>
    <ul>
        <li>Pre-ingest script (prepare-sip) for images transforms.</li>
        <li>Re-architected the cache code.</li>
        <li>Moved transactions to read-only queries where possible.</li>
        <li>Optimized slideshow retrieval/display.</li>
        <li>Optimized many actions (e.g. FetchArticleAction, GetAverageRatingsAction, etc.).</li>
        <li>Lots of Ambra code improvements.</li>
        <li>Upgraded to Dojo 1.1.</li>
        <li>Upgraded to Mulgara 1.2 (multi-core!).</li>
        <li>Upgraded to Lucene 2.3.</li>
        <li>Upgraded to ehCache 1.4.1.</li>
        <li>Many Ambra configuration changes for ease of installation.</li>
    </ul>
  
        <h2>RC 0.8.2.1 beta</h2>
        <p>Released February 26, 2008</p>
        <h3>Features Implemented in RC 0.8.2.1 beta</h3>
        <ul>
                <li>Minor corrections on articles</li>
                <li>Formal corrections on articles</li>
                <li>"View All" corrections page</li>
                <li>Admin interface for converting notes to corrections</li>
                <li>Citations for formal corrections</li>
                <li>Advanced search:</li>
                <ul>
                        <li>Search by author name</li>
                        <li>Search article text </li>
                        <li>Search by date published</li>
                        <li>Search by subject category</li>
                </ul>
                <li>Support for note DOIs in the DOI resolver</li>
                <li>Dynamic "Most Viewed" tab for journal home pages</li>
        </ul>

    <h2>RC 0.8.2 beta</h2>
    <p>Released January 11, 2008</p>
    <h3>Features implemented in RC 0.8.2 beta</h3>
    <ul>
        <li>Fixes to Browse by Issue (Table of Contents) to add more article types and next/previous links</li>
        <li>A dynamic Browse by Journal (Journal Archive) page</li>
        <li>A single rating field for all front matter articles (e.g. Editorial, Opinion)</li>
        <li>Affiliation footnote links are displayed for group authors</li>
        <li>Minor FreeMarker and CSS fixes</li>
        <li>Updates to the volume and issue object models</li>
        <li>Migration from OSCache to ehcache for faster performance</li>
        <li>Administrative interface for viewing and releasing caches</li>
    </ul>
  
    <h2>RC 0.8.1 beta</h2>
    <p>Released October 25, 2007</p>
    <h3>Features implemented in RC 0.8.1 beta</h3>
    <ul>
        <li>Display links to related articles on Article and Browse pages</li>
        <li>Administrative interface for collecting Articles into Issue and Issues into Volumes</li>
        <li>Administrative interface for attaching Issue Image article to Issues, and setting the current Issue</li>
        <li>Browse by Issue (Table of Contents) page</li>
    </ul>

    <h2>RC 0.8 beta</h2>
    <p>Released September 20, 2007</p>
    <h3>Features implemented in RC 0.8 beta</h3>
    <ul>
        <li>Enable multiple journals using a single repository</li>
        <li>Skins for multiple journals</li>
        <li>Filter search results by journal using OTM</li>
        <li>Trackback links for articles</li>
        <li>Citation download for the published article</li>
        <li>A fix to a rating display issue in IE7</li>
        <li>Allow multiple e-mail addresses for "E-mail this Article"</li>
        <li>Administrative modifications to annotations</li>
        <li>Administrative interface for multiple journals</li>
        <li>Migration to the Struts 2 web application framework</li>
    </ul>

    <h2>RC 0.7 beta</h2>
    <p>Released July 9, 2007</p>
    <h3>Features implemented in RC 0.7 beta</h3>
    <ul>
        <li>Ratings feature</li>
        <li>Architecture overhaul</li>
        <li>Removal of a layer of web services and increased performance</li>
        <li>Object Triple Mapping (OTM) which speeds retrieval of objects</li>
        <li>Object Query Language (OQL) that is similar to HQL, an object-oriented query language from Hibernate</li>
        <li>Atom syndication format</li>
    </ul>

    <h2>RC 0.6 beta</h2>
    <p>Released April 20, 2007</p>
    <h3>Features implemented in RC 0.6 beta</h3>
    <ul>
        <li>Browse features</li>
        <li>Full Safari support</li>
        <li>Cross section and cross paragraph annotations now work correctly</li>
        <li>Image cache is properly flushed when an article is deleted via the admin console</li>
        <li>Fix issue where jsession id was appended to search button and JavaScript file requests</li>
        <li>Resend address verification e-mail feature</li>
        <li>Removed alerts e-mail address</li>
        <li>After submitting profile for a new user, you get directed to alerts tab</li>
        <li>Users can change their e-mail address now</li>
        <li>New templates for the profile pages are available for customization</li>
        <li>New organization types on profile page</li>
        <li>Article title and journal title is in title of page now</li>
        <li>Fixed RDF header in Ambra Journal pages</li>
    </ul>

    <a name="credits"></a>
    <h2>Site Credits</h2>
    <p>Many of the icons are adapted from the <a href="http://www.famfamfam.com/lab/icons/silk/">Silk Icon</a> set, courtesy FamFamFam.</p>
  
</div>
<!-- end : main contents -->
