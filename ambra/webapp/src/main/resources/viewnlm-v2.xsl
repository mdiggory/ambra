<?xml version="1.0" encoding="UTF-8"?>
<!-- ============================================================= -->
<!--  MODULE:    HTML View of NLM Journal Article                  -->
<!--  VERSION:   0.2                                               -->
<!--  DATE:      November 2005                                     -->
<!--                                                               -->
<!-- ============================================================= -->

<!-- ============================================================= -->
<!--  SYSTEM:    NCBI Archiving and Interchange Journal Articles   -->
<!--                                                               -->
<!--  PURPOSE:   Provide an HTML preview of a journal article,     -->
<!--             in a form suitable for reading.                   -->
<!--                                                               -->
<!--  CONTAINS:  Documentation:                                    -->
<!--               D1) Change history                              -->
<!--               D2) Structure of this transform                 -->
<!--               D3) Design of the output                        -->
<!--               D4) Constraints on the input                    -->
<!--                                                               -->
<!--             Infrastructure:                                   -->
<!--               1) Transform element and top-level settings     -->
<!--                  including parameters, variables, keys, and   -->
<!--                  look-up tables                               -->
<!--               2) Root template                                -->
<!--               3) Document template (and make-a-piece)         -->
<!--               4) Utility templates                            -->
<!--               5) Formatting elements                          -->
<!--               6) Suppressed elements                          -->
<!--             Called templates for article parts:               -->
<!--               7) make-html-header                             -->
<!--               8) make-front                                   -->
<!--               9) make-body                                    -->
<!--              10) make-back                                    -->
<!--              11) make-post-publication                        -->
<!--              12) make-end-metadata                            -->
<!--             Narrative content and common structures:          -->
<!--              13) paragraph                                    -->
<!--              13a) Fix long words                              -->
<!--              14) section                                      -->
<!--              15) list                                         -->
<!--              16) display-quote                                -->
<!--              17) speech                                       -->
<!--              18) statement                                    -->
<!--              19) verse-group                                  -->
<!--              20) boxed-text                                   -->
<!--              21) preformat                                    -->
<!--              22) supplementary-material                       -->
<!--              23) display-formula and chem-struct-wrapper      -->
<!--             Inline Elements:                                  -->
<!--              24) formatting elements                          -->
<!--              25) semantic elements                            -->
<!--              26) break and horizontal rule                    -->
<!--             Display Objects:                                  -->
<!--              27) chem-struct                                  -->
<!--              28) tex-math and math                            -->
<!--              29) graphic and media                            -->
<!--              30) array                                        -->
<!--              31) captioning                                   -->
<!--              32) figure (mode put-at-end)                     -->
<!--              33) table-wrap (mode put-at-end)                 -->
<!--             Front mode:                                       -->
<!--              34) journal-meta                                 -->
<!--              35) article-meta                                 -->
<!--              36) title-group                                  -->
<!--              37) the parts of contrib element                 -->
<!--             Back (no mode):                                   -->
<!--              38) Acknowledgements                             -->
<!--              39) Appendix                                     -->
<!--              40) Footnote-group and fn                        -->
<!--              41) Notes                                        -->
<!--              42) Glossary                                     -->
<!--             Links:                                            -->
<!--              43) Target of a reference                        -->
<!--              44) xref                                         -->
<!--              45) external links                               -->
<!--             Titles:                                           -->
<!--              46) Main article divisions                       -->
<!--              47) First-level subdivisions and default         -->
<!--              48) make-abstract-title                          -->
<!--              48) make-abstract-title                          -->
<!--             Unmoded data elements:                            -->
<!--              49) Miscellaneous (epage, series, etc.)          -->
<!--              50) Parts of a date                              -->
<!--              51) Parts of a name                              -->
<!--             Citation and nlm-citation (NLM templates):        -->
<!--              52) ref-list                                     -->
<!--              53) ref                                          -->
<!--              54) citation                                     -->
<!--              55) nlm-citation                                 -->
<!--              56) citation sub-parts                           -->
<!--              57) citation-tag-ends                            -->
<!--                                                               -->
<!--  PROCESSOR DEPENDENCIES:                                      -->
<!--             None: standard XSLT 1.0                           -->
<!--             Tested under Apache Xalan 2.5.1                   -->
<!--                                                               -->
<!--  COMPONENTS REQUIRED:                                         -->
<!--             1) This stylesheet                                -->
<!--             2) CSS styles defined in ViewNLM.css              -->
<!--                                                               -->
<!--  INPUT:     An XML document valid with the NLM                -->
<!--             Publishing DTD.                                   -->
<!--                                                               -->
<!--  OUTPUT:    An HTML preview of the article.                   -->
<!--                                                               -->
<!--  ORIGINAL CREATION DATE:                                      -->
<!--             October 2003                                      -->
<!--                                                               -->
<!-- ============================================================= -->

<!-- ============================================================= -->
<!--  D1) STYLESHEET VERSION / CHANGE HISTORY                      -->
<!-- =============================================================

 No.  CHANGE (reason for / description)   [who]       VERSION DATE

  5.  Changed documentation style from comments
      to (example) doc:documentation/doc:p      v02.04 2005-08-10

  4.  Revised to produce XHTML.                 v02.03 2005-08-10

  3.  Revised to accommodate DTD changes        v02.02 2005-08-22

      - Added mml namespace declaration for MathML
      - Changed the namespace prefix for the utilities
        internal to this transform, from "m" to "util",
        [to avoid confusion with the MathML use of "m",
        which the NLM DTD overrides to "mml" for the sake
        of backwards compatibility].

  2.  Revised to fix typos and infelicities.    v02.01 2005-08-08

      - Reorganized transform for easier reading
          e.g., consolidated mode="none" templates (applied to loose
        bibref models when XML source doesn't provide punctuation).

      - Replaced xsl:text making newlines with a call-template,
        for easier reading and so these can be suppressed
        (conditionally or unconditionally) if desired. Also, now
        a search on xsl:text will find only (real) generated text.

      - Diagnosed issue with display of title-in-left-column,
        content-in-right-column in IE, Firefox.
      - Corrected behavior of many small parts, e.g.,
        self-uri, product/contrib and product/collab, etc.
      - Regularized the mode names and usage for front and back.
      - Set up structure anticipating sub-article and response
        (both of which have same top-level parts as article,
        and are themselves -within- article).
      - Improved punctuation and display of xrefs
        (fn, table-fn, bibr)
      - Corrected behavior of generated text on abstract types.
      - In templates for author-notes and descendents, made
        provision for the presence of a title/label.
      - In template for author name, corrected "pref" to "prefix"
      - In template for speech, corrected logic on excluding speaker
      - Tightened up the test for mode="none" on citation/ref.

      - Changed xsl:output indent to yes (was no)
      - Changed xsl:strip-space element list (was *)
      - Added xsl:preserve-space element list

      - Added doctype calls for Strict HTML DTD (in prep for
        producing XHTML).

  1.  v0.1.                                     v01 2003-11-03

      Based on transform downloaded from NCBI website 10/23/03.

      This version (v0.1) produces readable output
      for a sample set of publishing and archiving articles.
      There is more to do with respect to scope (e.g., the
      permissible variations in content allowed by the
      Archiving DTD).

                                                                   -->
<!-- ============================================================= -->

<!-- ============================================================= -->
<!--  D2) STRUCTURE OF THIS TRANSFORM                              -->
<!-- ============================================================= -->

<!--  The main transform is organized into sections as enumerated
      above.

      It is sometimes preferable to separate element templates,
      named templates, and moded templates. In this case, however,
      that would reduce rather than increase legibility. It is
      easier to follow what the front-matter template is doing
      when the named templates and modes it uses are ready to hand;
      similarly for the back matter and, especially, the references.

      The design gives considerable importance to clarity and
      maintainability, resulting in conventions such as generally
      giving each element type its own template, in preference to
      more concise alternatives.

      In addition, the transform produces explicit new-lines
      to improve legibility of the serialized output. (These are
      in the form <xsl:call-template name="nl-2"/>. )

      This transform is commented to explain the mappings used,
      and (intermittently) the content combinations being handled.
                                                                   -->

<!-- ============================================================= -->
<!--  D3) DESIGN OF THE OUTPUT                                     -->
<!-- ============================================================= -->

<!-- Purpose: An HTML preview of an article, to assist the
              author or editor in finalizing and approving
              the tagging.

     Characteristics arising from purpose:

              - link/target pairs display the ID as a label,
                rather than generating an explicit number.
              - the running-head text, if any, is displayed
                below the title


     Organization of Display:

     A. HTML setup
       1. HTML Metadata

     B. Article

       1. Front: Publication metadata (journal and article)

       2. Content metadata:
                 Title
                 Contributor(s)
                 Abstract(s)

       3. Body:  Sections &c.

       4. Back:  a) From XML "back": acknowledgements,
                   glossary, references, and back-matter notes.

                 b) Figs-and-tables. These are collected from
                    throughout the front, body, and back.

                 c) Content metadata for retrieval - keywords,
                    subject categories. &c.

     C. Sub-article or response, if any

        Has the same 5-part structure as "B. Article".


     Typographic notes:

     A red rule separates the four document divisions listed
     above for article. The major divisions -within- those parts
     are separated by a black rule.

     Content that is composed of repeated alternations of
     minor heading and text - such as the contributor section,
     the figures section, and the references section - is
     displayed as a two-column table, with the title/heading/label
     in the left column and the substance in the right column.

     Generated text is displayed in gray, to distinguish it
     from text derived from the source XML.

-->

<!-- ============================================================= -->
<!--  D4) CONSTRAINTS ON THE INPUT                                 -->
<!-- ============================================================= -->

<!--

1. The present transform doesn't handle:
     - sub-article or response
     - a full-featured narrative in supplementary-material
     - the attributes and elements pertaining to -groups-
       of figures or tables (fig-group, table-wrap-group).
       Their contained fig/table-wrap -are- handled.
     - col, colgroup

2. Article-meta that is not displayed at the top or end
   of the article:

                volume-id
                issue-id
                issue-title
                supplement
                page-range
                conference/conf-num
                conference/conf-sponsor
                conference/conf-theme

3. xlink attributes are suppressed *except for* xlink:href,
   which becomes an href or src attribute as follows:

      a) For inline-graphic, graphic, media:

           <img src="..."> & apply-templates

      b) For phrase-level elements

          <a href="..."> & apply-templates

      c) For block containers and grouping elements:

          <a href="..."> around whatever is being displayed
          as the object identifier, e.g.,

           - label or caption (for a graphic),
           - title (for a bio),
          or, if none such is available,
           - around the generated string "[link]"

4. Attributes and child elements displayed for graphic:

    The id and xlink:href attributes are displayed.
    The label, caption, and alt-text child elements are displayed.

5. Location of media files

   Transform assumes the @xlink:href value is an absolute
   path, not a relative one. To change this assumption:

   a) In the transform, create a variable which records
      the location of the graphics, e.g.,

      <xsl:variable name="graphics-dir"
                    select="'file:///c:/books/mybook/pix'"/>

   b) In the XML, use relative paths:

      <graphic xlink:href="poodle.jpg"/>

   c) Edit the appropriate template(s) in the transform
      to combine these two values:

      <img src="{concat($graphics-dir}, '/', {@xlink:href})"/>

5. Supplementary-material

   Transform assumes that the purpose & scope
   when tagging supplementary-material are:

     - point to an external file, such as a PDF or map
     - perhaps providing a paragraph or two of description
     - not using any of the much-manipulated elements,
       i.e., footnotes, tables, figures, and references.
-->


<!-- ============================================================= -->
<!--  1. TRANSFORM ELEMENT AND TOP-LEVEL SETTINGS                  -->
<!-- ============================================================= -->

<xsl:stylesheet version="2.0"
                id="ViewNLM-v2-04.xsl"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:util="http://dtd.nlm.nih.gov/xsl/util"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:aml="http://topazproject.org/aml/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                exclude-result-prefixes="util xsl dc">

<xsl:output method="html"
            indent="no"
            encoding="UTF-8"
            omit-xml-declaration="yes"/>

<xsl:strip-space elements="abstract ack address annotation app app-group
                           array article article-categories article-meta article-title
                           author-comment author-notes back bio body boxed-text
                           break caption chem-struct chem-struct-wrapper
                           citation col colgroup conference contrib contrib-group
                           copyright-statement date def def-item def-list
                           disp-quote etal fig fig-group fn fn-group front
                           gloss-group glossary glyph-ref graphic history hr
                           inline-graphic journal-meta kwd-group list list-item
                           media mml:math name nlm-citation note notes page-count
                           person-group private-char pub-date publisher ref
                           ref-list response sec speech statement sub-article
                           subj-group supplementary-material table table-wrap
                           table-wrap-foot table-wrap-group tbody term tfoot thead
                           title-group tr trans-abstract verse-group
                           "/>

<xsl:preserve-space elements="preformat"/>

<!--  Run-time parameters -->
<!--  This stylesheet accepts no run-time parameters. -->

<!-- Pub Config-->
<xsl:param name="pubAppContext"></xsl:param>

<!-- Keys -->

<!-- To reduce dependency on a DTD for processing, we declare
     a key to use in lieu of the id() function. -->
<xsl:key name="element-by-id" match="*[@id]" use="@id"/>

<!-- Conversely, we can retrieve referencing elements
     from the node they reference. -->
<xsl:key name="element-by-rid" match="*[@rid]" use="@rid"/>

<!-- Lookup table for person-type strings
     used in nlm-citations -->
<xsl:variable name="person-strings"
              select="document('')/*/util:map[@id='person-strings']/item"/>

<util:map id="person-strings">
  <item source="editor"       singular="editor"
                              plural="editors"/>
  <item source="assignee"     singular="assignee"
                              plural="assignees"/>
  <item source="translator"   singular="translator"
                              plural="translators"/>
  <item source="transed"      singular="translator and editor"
                              plural="translators and editors"/>
  <item source="guest-editor" singular="guest editor"
                              plural="guest editors"/>
  <item source="compiler"     singular="compiler"
                              plural="compilers"/>
  <item source="inventor"     singular="inventor"
                              plural="inventors"/>
  <!-- value 'allauthors' puts no string out -->
</util:map>

<!-- Store the version of the XML file so that we can use to conditionally select different
     options and preserve backward compatibility. -->

<xsl:variable name="dtd-version" select="/article/@dtd-version"/>


<!-- ============================================================= -->
<!--  2. ROOT TEMPLATE - HANDLES HTML FRAMEWORK                    -->
<!-- ============================================================= -->

<xsl:template match="/">
  <xsl:call-template name="nl-1"/>
  <xsl:apply-templates/>
</xsl:template>


<!-- ============================================================= -->
<!--  3. DOCUMENT ELEMENT                                          -->
<!-- ============================================================= -->

<!-- Can add sub-article and response to this match:
      - "make-a-piece" as required;
      - adapt the selection of elements that get managed as a set:
        footnotes, cross-references, tables, and figures. -->

<xsl:template match="article">
  <xsl:call-template name="make-a-piece"/>
</xsl:template>


<!-- ============================================================= -->
<!--  3. "make-a-piece"                                            -->
<!-- ============================================================= -->

<!--  Generalized management of front, body, back, and trailing
      content, presently oeprates for sub-article and response
      exactly as for article. -->

<!--  Organization of output:
         make-front
         make-body
         make-back
         make-figs-and-tables
         make-end-metadata
         ...then...
         do the same for any contained sub-article/response
-->

<!-- initial context node is article -->
<xsl:template name="make-section-id">
  <xsl:attribute name="id">
    <xsl:value-of select="concat('section',count(preceding-sibling::sec)+1)"/>
  </xsl:attribute>
</xsl:template>

<xsl:template name="make-a-piece">

  <!-- variable to be used in div id's to keep them unique -->
  <xsl:variable name="which-piece">
    <xsl:value-of select="concat(local-name(), '-level-', count(ancestor::*))"/>
  </xsl:variable>

  <!-- front matter, in table -->
  <xsl:call-template name="nl-2"/>

    <!-- class is repeated on contained table elements -->
    <xsl:call-template name="nl-1"/>
    <xsl:call-template name="make-front"/>
    <xsl:call-template name="nl-1"/>

  <xsl:text><!-- start : article information --></xsl:text>
    <div class="articleinfo" xpathLocation="noSelect">
     <xsl:call-template name="make-article-meta"/>
  </div>
  <xsl:text><!-- end : article infomation --></xsl:text>

  <!-- Add editors summary box after article meta info and before introduction -->
  <xsl:call-template name="make-editors-summary"/>
  
  <!-- body -->
  <xsl:call-template name="nl-2"/>
  <xsl:call-template name="nl-1"/>
  <xsl:call-template name="make-body"/>
  <xsl:call-template name="nl-1"/>

  <!-- class is repeated on contained table elements -->
  <xsl:call-template name="nl-1"/>
  <xsl:call-template name="make-back"/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!-- 4. UTILITIES                                                  -->
<!-- ============================================================= -->

<xsl:template name="endsWithPunctuation">
  <xsl:param name="value"/>
  <xsl:choose>
    <xsl:when test="substring($value, string-length($value)) = '.'">true</xsl:when>
    <xsl:when test="substring($value, string-length($value)) = '!'">true</xsl:when>
    <xsl:when test="substring($value, string-length($value)) = '?'">true</xsl:when>
    <xsl:otherwise>false</xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  "capitalize" Capitalize a string                             -->
<!-- ============================================================= -->

<xsl:template name="capitalize">
  <xsl:param name="str"/>
  <xsl:value-of select="translate($str,
                                  'abcdefghjiklmnopqrstuvwxyz',
                                  'ABCDEFGHJIKLMNOPQRSTUVWXYZ')"/>
</xsl:template>


<!-- ============================================================= -->
<!--  "language"                                                   -->
<!-- ============================================================= -->

<xsl:template name="language">
  <xsl:param name="lang"/>
  <xsl:choose>
    <xsl:when test="$lang='fr' or $lang='FR'"> (Fre).</xsl:when>
    <xsl:when test="$lang='jp' or $lang='JP'"> (Jpn).</xsl:when>
    <xsl:when test="$lang='ru' or $lang='RU'"> (Rus).</xsl:when>
    <xsl:when test="$lang='de' or $lang='DE'"> (Ger).</xsl:when>
    <xsl:when test="$lang='se' or $lang='SE'"> (Swe).</xsl:when>
    <xsl:when test="$lang='it' or $lang='IT'"> (Ita).</xsl:when>
    <xsl:when test="$lang='he' or $lang='HE'"> (Heb).</xsl:when>
    <xsl:when test="$lang='sp' or $lang='SP'"> (Spa).</xsl:when>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  "cleantitle"                                                 -->
<!-- ============================================================= -->

<xsl:template name="cleantitle">
  <xsl:param name="str"/>
  <xsl:value-of select="translate($str,'. ,-_','')"/>
</xsl:template>


<!-- ============================================================= -->
<!--  "newlines"                                                   -->
<!-- ============================================================= -->

<!-- produces newlines in output, to increase legibility of XML    -->
<xsl:template name="nl-1">
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

<xsl:template name="nl-2">
  <xsl:text>&#xA;</xsl:text>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>


<!-- ============================================================= -->
<!--  make-id, make-src, make-href, make-email                     -->
<!-- ============================================================= -->
<xsl:template name="makeXpathLocation">
  <xsl:variable name="xpathLocation">
    <xsl:call-template name="createXpath">
      <xsl:with-param name="theNode" select="."/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:attribute name="xpathLocation">
    <xsl:value-of select="$xpathLocation"/>
  </xsl:attribute>
</xsl:template>

<!-- use when we want to constrain the user selection to be at the element level only -->
<xsl:template name="makeElementXpathLocation">
  <xsl:call-template name="makeXpathLocation"/>
  <xsl:attribute name="elmntslctn">
    <xsl:text>true</xsl:text>
  </xsl:attribute>
</xsl:template>

<xsl:template name="makeXpathLocationParam">
  <xsl:param name="node" select="."/>
  <xsl:variable name="xpathLocation">
    <xsl:call-template name="createXpath">
      <xsl:with-param name="theNode" select="$node"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:attribute name="xpathLocation">
    <xsl:value-of select="$xpathLocation"/>
  </xsl:attribute>
</xsl:template>

<xsl:template name="createXpath">
  <xsl:param name="theNode" select="."/>
  <xsl:choose>
    <xsl:when test="$theNode[1]">
      <xsl:choose>
        <xsl:when test="not($theNode[1]/..)">
          <xsl:text>/</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="$theNode[1]/ancestor-or-self::*[not(self::aml:annotated)]">
            <xsl:text/>/<xsl:value-of select="name()"/>
            <xsl:text/>[<xsl:value-of select="count(preceding-sibling::*[name() = name(current())]) + 1"/>]<xsl:text/>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="$theNode">
      <xsl:choose>
        <xsl:when test="not($theNode/..)">
          <xsl:text>/</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="$theNode/ancestor-or-self::*[not(self::aml:annotated)]">
            <xsl:text/>/<xsl:value-of select="name()"/>
            <xsl:text/>[<xsl:value-of select="count(preceding-sibling::*[name() = name(current())]) + 1"/>]<xsl:text/>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template name="make-id">
  <xsl:if test="@id">
    <xsl:attribute name="id">
      <xsl:value-of select="@id"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template name="make-src">
  <xsl:if test="@xlink:href">
    <xsl:attribute name="src">
      <xsl:value-of select="@xlink:href"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template name="make-href">
  <xsl:if test="@xlink:href">
    <xsl:attribute name="href">
      <xsl:value-of select="@xlink:href"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template name="make-email">
  <xsl:if test="@xlink:href">
    <xsl:attribute name="href">
      <xsl:value-of select="concat('mailto:', @xlink:href)"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>


<!-- ============================================================= -->
<!--  display-id                                                   -->
<!-- ============================================================= -->

<xsl:template name="display-id">

  <xsl:variable name="display-phrase">
    <xsl:choose>
      <xsl:when test="self::disp-formula"><xsl:text>Formula ID</xsl:text></xsl:when>
      <xsl:when test="self::chem-struct-wrapper">
        <xsl:text>Chemical Structure Wrapper ID</xsl:text>
      </xsl:when>
      <xsl:when test="self::chem-struct"><xsl:text>Chemical Structure ID</xsl:text></xsl:when>
      <xsl:otherwise><xsl:text>ID</xsl:text></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:if test="@id">
    <span class="gen">
      <xsl:text>[</xsl:text>
      <xsl:value-of select="$display-phrase"/>
      <xsl:text>: </xsl:text>
    </span>
    <xsl:value-of select="@id"/>
    <span class="gen">
      <xsl:text>]</xsl:text>
    </span>
  </xsl:if>
</xsl:template>


<!-- ============================================================= -->
<!--  "table-setup": left column wide or narrow                    -->
<!-- ============================================================= -->

<xsl:template name="table-setup-l-wide">
  <xsl:call-template name="nl-1"/>
  <tr><td width="30%"/><td/></tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template name="table-setup-l-narrow">
  <xsl:call-template name="nl-1"/>
  <tr><td width="10%"/><td/></tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template name="table-setup-even">
  <xsl:call-template name="nl-1"/>
  <tr><td width="50%"/><td/></tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!-- "make-figs-and-tables"                                        -->
<!-- ============================================================= -->

<!-- initial context node is article -->
<xsl:template match="fig | table-wrap">
  <xsl:variable name="figId"><xsl:value-of select="@id"/></xsl:variable>
  <xsl:variable name="apos">'</xsl:variable>
  <xsl:variable name="imageURI"><xsl:value-of select="graphic/@xlink:href"/></xsl:variable>
  <xsl:variable name="slideshowURL">
    <xsl:value-of select="concat($pubAppContext, '/article/slideshow.action?uri=',
                                 substring($imageURI, 1, (string-length($imageURI)-5)),
                                 '&amp;imageURI=', $imageURI)"/>
  </xsl:variable>
  <xsl:variable name="jsWindow">
    <xsl:value-of select="concat('window.open(this.href,',$apos,'plosSlideshow',$apos,',',$apos,
                                 'directories=no,location=no,menubar=no,resizable=yes,status=no,',
                                 'scrollbars=yes,toolbar=no,height=600,width=850', $apos,
                                 ');return false;')"/>
  </xsl:variable>
<xsl:if test="graphic">
  <div class="figure">
    <xsl:call-template name="makeXpathLocation"/>
    <xsl:element name="a">
      <xsl:attribute name="name"><xsl:value-of select="$figId"/></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$figId"/></xsl:attribute>
      <xsl:attribute name="title">Click for larger image </xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="$slideshowURL"/></xsl:attribute>
      <xsl:attribute name="onclick"><xsl:value-of select="$jsWindow"/></xsl:attribute>
      <xsl:element name="img">
        <xsl:attribute name="xpathLocation">noSelect</xsl:attribute>
        <xsl:attribute name="border">1</xsl:attribute>
        <xsl:attribute name="src">
          <xsl:value-of select="concat($pubAppContext,
                                       '/article/fetchObject.action?uri=',
                                       $imageURI,'&amp;representation=PNG_S')"/>
        </xsl:attribute>
        <xsl:attribute name="align">left</xsl:attribute>
        <xsl:attribute name="alt">thumbnail</xsl:attribute>
        <xsl:attribute name="class">thumbnail</xsl:attribute>
      </xsl:element>
    </xsl:element>
     <p><strong>
      <xsl:call-template name="makeXpathLocationParam" >
        <xsl:with-param name="node" select="label"/>
      </xsl:call-template>
      <xsl:element name="a">
        <xsl:attribute name="xpathLocation">noSelect</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="$slideshowURL"/></xsl:attribute>
        <xsl:attribute name="onclick"><xsl:value-of select="$jsWindow"/></xsl:attribute>
          <span>
            <xsl:apply-templates select="label"/></span>
      </xsl:element>
      <xsl:if test="caption/title">
      <xsl:text> </xsl:text>
      <span>
        <xsl:call-template name="makeXpathLocationParam" >
          <xsl:with-param name="node" select="caption/title"/>
        </xsl:call-template>
        <xsl:apply-templates select="caption/title"/>
      </span>
      </xsl:if>
    </strong></p>
    <xsl:apply-templates select="caption/node()[not(self::title)]"/>
    <xsl:if test="object-id[@pub-id-type='doi']">
      <span xpathLocation="noSelect"><xsl:apply-templates select="object-id[@pub-id-type='doi']"/></span>
    </xsl:if>
    <div class="clearer" />
  </div>
</xsl:if>
 <xsl:if test="not(graphic)">
   <xsl:apply-templates />
</xsl:if>
</xsl:template>

<xsl:template name="make-figs-and-tables">

  <xsl:if test="body//fig[not(parent::fig-group)] | back//fig[not(parent::fig-group)]">
    <hr class="section-rule"/>
    <xsl:call-template name="nl-1"/>

    <span class="tl-main-part">Figures</span>
    <xsl:call-template name="nl-1"/>

    <table width="100%" class="bm">
      <xsl:call-template name="table-setup-l-wide"/>

      <!-- each figure is a row -->
      <xsl:apply-templates select="body//fig | back//fig" mode="put-at-end"/>
    </table>

  </xsl:if>

  <xsl:if test="body//table-wrap | back//table-wrap">
    <hr class="section-rule"/>
    <xsl:call-template name="nl-1"/>

    <span class="tl-main-part">Tables</span>
    <xsl:call-template name="nl-1"/>

    <xsl:apply-templates select="body//table-wrap | back//table-wrap" mode="put-at-end"/>
   <xsl:call-template name="nl-1"/>
  </xsl:if>

</xsl:template>


<!-- ============================================================= -->
<!-- 6. SUPPRESSED ELEMENTS                                        -->
<!-- ============================================================= -->

<!-- suppressed in no-mode (processed in mode "front") -->
<xsl:template match="journal-meta | article-meta"/>

<!-- not handled by this transform -->
<xsl:template match="sub-article | response"/>

<!-- xlink attributes are generally suppressed; note however that
     @xlink:href is used in some element templates. -->
<xsl:template match="@xlink:*"/>

<!-- Tables and figures are displayed at the end of the document,
     using mode "put-at-end".
     So, in no-mode, we suppress them: -->
<xsl:template match="fig-group | table-wrap-group"/>


<!-- ============================================================= -->
<!-- CALLED TEMPLATES FOR ARTICLE PARTS                            -->
<!-- ============================================================= -->


<!-- ============================================================= -->
<!--  7. MAKE-HTML-HEADER                                          -->
<!-- ============================================================= -->

<xsl:template name="make-html-header">
  <head>
    <xsl:call-template name="nl-1"/>
    <title>
      <xsl:choose>
        <xsl:when test="/article/front/journal-meta/journal-id
                        [@journal-id-type='pubmed']">
          <xsl:value-of select="/article/front/journal-meta/journal-id
                                [@journal-id-type='pubmed']"/>
          <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:when test="/article/front/journal-meta/journal-id
                       [@journal-id-type='publisher']">
          <xsl:value-of select="/article/front/journal-meta/journal-id
                                [@journal-id-type='publisher']"/>
          <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:when test="/article/front/journal-meta/journal-id">
          <xsl:value-of select="/article/front/journal-meta/journal-id
                                [1][@journal-id-type]"/>
          <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
      <xsl:for-each select="/article/front/article-meta/volume">
        <xsl:text>Vol. </xsl:text>
        <xsl:apply-templates/>
        <xsl:text> </xsl:text>
      </xsl:for-each>
      <xsl:for-each select="/article/front/article-meta/issue">
        <xsl:text>Issue </xsl:text>
        <xsl:apply-templates/>
        <xsl:text>: </xsl:text>
      </xsl:for-each>
      <xsl:if test="/article/front/article-meta/fpage">
        <xsl:choose>
          <xsl:when test="../lpage">
            <xsl:text>pp. </xsl:text>
            <xsl:value-of select="/article/front/article-meta/fpage"/>
            <xsl:text>-</xsl:text>
            <xsl:value-of select="/article/front/article-meta/lpage"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>p. </xsl:text>
            <xsl:value-of select="/article/front/article-meta/fpage"/>
            <xsl:text> </xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </title>
    <xsl:call-template name="nl-1"/>
    <link rel="stylesheet" type="text/css" href="ViewNLM.css"/>
    <xsl:call-template name="nl-1"/>
  </head>
</xsl:template>

<xsl:template name="fund-compete">
  <xsl:for-each select="/article/back/fn-group">
    <xsl:if test="fn[@fn-type='financial-disclosure']">
      <p><strong>Funding:</strong><xsl:text> </xsl:text>
      <xsl:apply-templates select="fn[@fn-type='financial-disclosure']/p"/></p>
    </xsl:if>
    <xsl:if test="fn[@fn-type='conflict']">
      <p><strong>Competing interests:</strong><xsl:text> </xsl:text> 
      <xsl:apply-templates select="fn[@fn-type='conflict']/p"/></p>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="makeInitials">
  <xsl:param name="string" />
  <xsl:param name="delimiter" select="' '" />
  <xsl:choose>
    <xsl:when test="$delimiter and contains($string, $delimiter)">
     <xsl:value-of select="substring(substring-before($string,$delimiter), 1,1)" />
      <xsl:call-template name="makeInitials">
        <xsl:with-param name="string" select="substring-after($string,$delimiter)" />
        <xsl:with-param name="delimiter" select="$delimiter" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="substring($string,1,1)" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="makeInitials2">
  <xsl:param name="x" />
  <xsl:for-each select="tokenize($x,'\s+')">
    <xsl:choose>
      <xsl:when test="contains(.,'-')">
        <xsl:for-each select="tokenize(.,'-')">
          <xsl:value-of select="substring(.,1,1)"/>
          <xsl:if test="position()!=last()">-</xsl:if>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="substring(.,1,1)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<!-- Make article meta data -->
<xsl:template name="make-article-meta">
  <xsl:for-each select="front/article-meta">
    <p>
      <strong>Citation: </strong>
      <xsl:for-each select="contrib-group/contrib[@contrib-type='author'][position() &lt; 7]">
        <xsl:choose>
          <xsl:when test="position() = 6">
            <xsl:text>et al. </xsl:text>
          </xsl:when>
        <xsl:otherwise>
          <!-- added this in to handle group author in citation -->
          <xsl:choose> 
            <xsl:when test="collab">
              <xsl:apply-templates select="collab"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="name/surname"/>
              <xsl:if test="name/given-names">
              <xsl:text> </xsl:text>
              </xsl:if>
              <xsl:call-template name="makeInitials2">
                <xsl:with-param name="x"><xsl:value-of select="name/given-names"/></xsl:with-param>
              </xsl:call-template>
              <!-- Included missing suffix and made sure it doesn't have a trailing period -->
              <xsl:if test="string-length(name/suffix) > 0">
                <xsl:text> </xsl:text>
                <xsl:choose>
                  <xsl:when test="substring(name/suffix,string-length(name/suffix))='.'">
                    <xsl:value-of select="substring(name/suffix,1,string-length(name/suffix)-1)"/>
                  </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="name/suffix"/>
                </xsl:otherwise>
                </xsl:choose>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:if test="position() != last()">
            <xsl:text>, </xsl:text>
          </xsl:if>
        </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:text> (</xsl:text>
      <xsl:value-of select="pub-date[@pub-type='collection']/year |
                            pub-date[@pub-type='ppub']/year"/>
      <xsl:text>) </xsl:text>
      
      <!-- Fixed bug where a title with a ? or ! at end 
           was followed by a period in the citation. -->
      <xsl:apply-templates select="title-group/article-title" mode="none"/>
      <xsl:variable name="at" select="normalize-space(title-group/article-title)"/>
      <xsl:if test="substring($at,string-length($at))!='?' and  
                    substring($at,string-length($at))!='!'">
        <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
      <xsl:value-of select="../journal-meta/journal-id[@journal-id-type='nlm-ta']"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="volume"/>(<xsl:value-of select="issue"/>):
      <xsl:value-of select="elocation-id"/>.
      doi:<xsl:value-of select="article-id[@pub-id-type='doi']"/>
    </p>

    <!-- Created a new way to format the editors list in the citation box -->
    <xsl:for-each-group select="//contrib-group/contrib[@contrib-type='editor']" group-by="role">
      <xsl:call-template name="editors-list">
        <xsl:with-param name="r" select="//contrib-group/contrib[@contrib-type='editor' 
          and role=current-grouping-key()]"/>
      </xsl:call-template>
    </xsl:for-each-group>

    <xsl:call-template name="editors-list">
      <xsl:with-param name="r" select="//contrib-group/contrib[@contrib-type='editor'
        and not(role)]"/>
    </xsl:call-template>

    <!-- end of contrib -->
    <p>
      <xsl:if test="history/date[@date-type='received']">
        <strong>Received:</strong> <xsl:text> </xsl:text>
        <xsl:apply-templates select="history/date[@date-type='received']/month" mode="none"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="history/date[@date-type='received']/day"/><xsl:text>, </xsl:text>
        <xsl:value-of select="history/date[@date-type='received']/year"/><xsl:text>; </xsl:text>
      </xsl:if>
      <xsl:if test="history/date[@date-type='accepted']">
        <strong>Accepted:</strong> <xsl:text> </xsl:text>
        <xsl:apply-templates select="history/date[@date-type='accepted']/month" mode="none"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="history/date[@date-type='accepted']/day"/><xsl:text>, </xsl:text>
        <xsl:value-of select="history/date[@date-type='accepted']/year"/><xsl:text>; </xsl:text>
      </xsl:if>
      <strong>Published:</strong> <xsl:text> </xsl:text>
      <xsl:apply-templates select="pub-date[@pub-type='epub']/month" mode="none"/>
      <xsl:text> </xsl:text>
      <xsl:if test="pub-date[@pub-type='epub']/day">
        <xsl:value-of select="pub-date[@pub-type='epub']/day"/><xsl:text>, </xsl:text>
      </xsl:if>
      <xsl:value-of select="pub-date[@pub-type='epub']/year"/>
    </p>
    
    <!-- Output the copyright statement differently with v2.3 of the dtd -->
    <p>
      <xsl:choose>
        <xsl:when test="$dtd-version &lt; 2.3">
          <xsl:choose>
            <!-- Modified to output the word "copyright" for the header if the expression
            "Open-Access License" appears anywhere in the copyright-statement. -->
            <xsl:when test="copyright-statement[contains(., 'Attribution') 
              or contains(.,'Open-Access License')]">
              <strong>Copyright:</strong><xsl:text>  &#169; </xsl:text>
              <xsl:apply-templates select="copyright-year" /><xsl:text> </xsl:text>
              <xsl:apply-templates select="copyright-statement" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="copyright-statement" />
            </xsl:otherwise>
          </xsl:choose>			
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="permissions/license/@license-type='open-access'">
              <xsl:apply-templates select="permissions/copyright-statement"/><xsl:text> </xsl:text>
            </xsl:when>
          </xsl:choose>
          <xsl:apply-templates select="permissions/license/p/node()"/>
        </xsl:otherwise>
      </xsl:choose>
    </p>

    <!-- copyright: show statement -or- year -->
    <!-- Most recent version of DTD recommends using the <permissions> wrapper
         for the copyright data. We handle both cases here. -->
    <xsl:call-template name="fund-compete"/>
    <xsl:if test="../../back/glossary">
      <p>
        <strong><xsl:value-of select="../..//back/glossary/title"/>: </strong>
        <xsl:for-each select="../../back/glossary/def-list/def-item">
          <xsl:apply-templates select="term"/>,
          <xsl:apply-templates select="def"/>
          <xsl:if test="position() != last()">; </xsl:if>
        </xsl:for-each>
      </p>
    </xsl:if>
    <xsl:if test="author-notes/corresp">
      <p>
        <xsl:apply-templates select="author-notes/corresp" mode="front"/>
      </p>
    </xsl:if>
     <xsl:if test="contrib-group/contrib[@contrib-type='author'][@equal-contrib='yes']">
      <p>
        <a name="equal-contrib"></a><xsl:text>#</xsl:text> 
        These authors contributed equally to this work.
      </p>
    </xsl:if>
    <xsl:for-each select="author-notes/fn[@fn-type='current-aff']">
      <p>
        <xsl:apply-templates select="." mode="front"/>
      </p>
    </xsl:for-each>
    <xsl:for-each select="author-notes/fn[@fn-type='deceased']">
      <p>
        <xsl:apply-templates select="." mode="front"/>
      </p>
    </xsl:for-each>

    <!-- added additional test for automatic deceased footnote. -->
    <xsl:if test="contrib-group/contrib/@deceased='yes' 
                  and not(author-notes/fn[@fn-type='deceased'])">
      <p><a name="deceased"></a><sup>&#x2020;</sup> Deceased.</p>
    </xsl:if>
    <xsl:for-each select="author-notes/fn[@fn-type='other']">
      <p>
        <xsl:apply-templates select="." mode="front"/>
      </p>
    </xsl:for-each>
       
  <!-- that's it for article-meta; return to previous context -->
  </xsl:for-each>

  <!-- The following hack for legacy articles allows fn-type="other" and <fn> with no fn-type to
       be output at the bottom of the citation section. -->
  <xsl:for-each select="//back/fn-group/fn[@fn-type='other']/node() |
                        //back/fn-group/fn[not(@fn-type)]/node()">
    <p><xsl:apply-templates/></p>
  </xsl:for-each>  
  
</xsl:template>

<xsl:template name="editors-list">
  <xsl:param name="r"/>
  <p>
    <xsl:for-each select="$r">

      <!-- for the first item, print out the role first, i.e. Editor -->
      <xsl:if test="position()=1"> 
        <strong>
          <xsl:choose>
            <xsl:when test="role">
              <xsl:value-of select="role"/>
            </xsl:when>
            <xsl:otherwise>
              Academic Editor
            </xsl:otherwise>
          </xsl:choose>

          <!-- add an s at end of role to make it plural -->
          <xsl:if test="last() > 1">s</xsl:if>
          <xsl:text>: </xsl:text> 
        </strong>
      </xsl:if>
      <xsl:apply-templates select="name | collab" mode="front"/>
      <xsl:apply-templates select="*[not(self::name)
                                   and not(self::collab)
                                   and not(self::xref)
                                   and not(self::degrees)
                                   and not(self::role)]"
                           mode="front"/>
      <xsl:variable name="matchto" select="xref/@rid"/>
      <xsl:if test="../following-sibling::aff">

        <!-- use commas between name & aff if single editor; else use parens -->
        <xsl:choose>
          <xsl:when test="position() = 1 and position() = last()">
            <xsl:text>, </xsl:text>
            <xsl:apply-templates select="../following-sibling::aff[@id=$matchto]" mode="editor"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text> (</xsl:text>
            <xsl:apply-templates select="../following-sibling::aff[@id=$matchto]" mode="editor"/>
            <xsl:text>)</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
      <!-- appropriately place commas and "and" -->
      <xsl:if test="position() != last()">
        <xsl:text>, </xsl:text>
      </xsl:if>
      <xsl:if test="position() = last()-1">
        <xsl:text>and </xsl:text>
      </xsl:if>
    </xsl:for-each>
  </p>
</xsl:template>

<xsl:template name="make-editors-summary">
  <xsl:for-each select="front/article-meta/abstract[@abstract-type='editor']">
    <div class="editorsAbstract">
      <xsl:call-template name="makeXpathLocation"/>
      <xsl:call-template name="words-for-abstract-title"/>
      <xsl:apply-templates select="*[not(self::title)]"/>
    </div>
  </xsl:for-each>
</xsl:template>

<!-- Added the following template rules to correctly copy and map 
     different markup within glossary definitions -->
<xsl:template match="def-item//p">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="def-item//named-content">
  <span class="{@content-type}">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template 
  match="def-item//sup | def-item//sub | def-item//em | def-item//strong">
  <xsl:element name="{local-name()}">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- Output def-lists in the body of the text 
     (note: different than def-list in the glossary as above) -->
<xsl:template match="body//def-list">
  <dl>
    <xsl:for-each select="def-item">
      <dt><xsl:apply-templates select="term"/></dt>
      <dd><xsl:apply-templates select="def"/></dd>
    </xsl:for-each>
  </dl>
</xsl:template>


<!-- ============================================================= -->
<!--  8. MAKE-FRONT                                                -->
<!-- ============================================================= -->

<!-- initial context node is /article -->
<xsl:template name="make-front">
  <xsl:call-template name="nl-1"/>

  <!-- change context to front/article-meta -->
  <xsl:for-each select="front/article-meta">
    <xsl:apply-templates select="title-group" mode="front"/>
    <p class="authors" xpathLocation="noSelect">
      <xsl:for-each select="contrib-group/contrib[@contrib-type='author']">
        <xsl:choose>
        
        <!-- the following test for @xlink probably is not used any more and can be removed -->
        <xsl:when test="@xlink:href">
          <xsl:element name="a">
            <xsl:call-template name="make-href"/>
            <xsl:call-template name="make-id"/>
            <xsl:apply-templates select="name" mode="front"/>
          </xsl:element>
          <xsl:element name="span">
            <xsl:attribute name="rel">dc:creator</xsl:attribute>
            <xsl:element name="span">
              <xsl:attribute name="property">foaf:name</xsl:attribute>
              <xsl:apply-templates select="name" mode="front-refs"/>
            </xsl:element>
          </xsl:element>
          <xsl:apply-templates select="collab" mode="front"/>
        </xsl:when>
           
        <!-- email tag is new with version 2.3 of the dtd. Currently disabled -->
        <!--
        <xsl:when test="email">
          <xsl:element name="a">
            <xsl:attribute name="class">author-link</xsl:attribute>
            <xsl:attribute name="href">
              <xsl:value-of select="concat('mailto:',email)"/>\
            </xsl:attribute>
            <xsl:apply-templates select="name" mode="front"/>
          </xsl:element>
          <xsl:apply-templates select="name" mode="front-refs"/>
          <xsl:apply-templates select="collab" mode="front"/>
        </xsl:when>
        -->
            
        <xsl:otherwise>          
          <xsl:element name="span">
            <xsl:attribute name="rel">dc:creator</xsl:attribute>
            <xsl:element name="span">
              <xsl:attribute name="property">foaf:name</xsl:attribute>
              <xsl:apply-templates select="name" mode="front"/>
            </xsl:element>
          </xsl:element>
          <xsl:apply-templates select="name" mode="front-refs"/>
          <xsl:apply-templates select="collab" mode="front"/>
        </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="position() != last()">
          <xsl:text>, </xsl:text>
        </xsl:if>
        
        <!-- the name element handles any contrib/xref and contrib/degrees -->
        <xsl:apply-templates select="*[not(self::name)
                                     and not(self::collab)
                                     and not(self::xref)
                                     and not(self::email)
                                     and not(self::degrees)
                                     and not(self::aff)]"
                             mode="front"/>
      </xsl:for-each> 

    <!-- end of contrib -->
    </p>
    <p class="affiliations" xpathLocation="noSelect">
      <xsl:for-each select="contrib-group/aff | contrib-group/contrib[@contrib-type='author']/aff">
        <xsl:apply-templates select="label"/>
        <xsl:if test="label">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="institution" />
        <xsl:if test="institution">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="addr-line" />
        <xsl:if test="position() != last()">
          <xsl:text>, </xsl:text>
        </xsl:if>
      </xsl:for-each>

      <!-- each aff that is NOT directly inside a contrib
           also makes a row: empty left, details at right -->
      <xsl:for-each select="aff">
        <xsl:variable name="rid"><xsl:value-of select="@id"/></xsl:variable>
        <xsl:if test="../contrib-group/contrib[@contrib-type='author']/xref[@ref-type='aff' 
                      and @rid=$rid]">
          <xsl:element name="a">
            <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
          </xsl:element>
          <xsl:apply-templates select="label"/>
            <xsl:if test="label">
              <xsl:text> </xsl:text>
            </xsl:if>
          <xsl:apply-templates select="institution" />
          <xsl:if test="institution">
            <xsl:text>, </xsl:text>
          </xsl:if>
          <xsl:apply-templates select="addr-line" />
          <xsl:if test="following-sibling::aff">
            <xsl:variable name="nextId">
              <xsl:value-of select="following-sibling::aff[1]/@id"/>
            </xsl:variable>
            <xsl:if test="../contrib-group/contrib[@contrib-type='author']/xref[@ref-type='aff' 
                          and @rid=$nextId]">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </p>

    <!-- New Table: titles and author group -->
    <!-- All data comes from front/article-meta -->
    <!-- abstract(s) -->
    <xsl:for-each select="abstract[not(@abstract-type) 
                          or (@abstract-type !='toc' 
                          and @abstract-type != 'teaser' 
                          and @abstract-type != 'editor' 
                          and @abstract-type != 'patient')]">
      <div class="abstract">      
        <xsl:call-template name="makeXpathLocation"/>
        <xsl:call-template name="words-for-abstract-title"/>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </div>
    </xsl:for-each> 
    <!-- end of abstract or trans-abstract -->

    <!-- end of the titles-and-authors context; return to previous context -->
    </xsl:for-each>
    <xsl:call-template name="nl-2"/>

<!-- end of big front-matter pull -->
</xsl:template>

<!-- Hack to remove leading and trailing space from addr-line elements 
     that were incorrectly written this way. 
     Utlimately, XML files should be cleaned up rather than this hack. -->
<xsl:template match="addr-line">
  <xsl:value-of select="normalize-space()"/>
</xsl:template>


<!-- ============================================================= -->
<!--  9. MAKE-BODY                                                 -->
<!-- ============================================================= -->

<!-- initial context node is article -->
<xsl:template name="make-body">

  <!-- change context node -->
  <xsl:for-each select="body">
    <xsl:call-template name="nl-1"/>
    <xsl:call-template name="nl-1"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </xsl:for-each>
</xsl:template>


<!-- ============================================================= -->
<!--  10. MAKE-BACK                                                -->
<!-- ============================================================= -->

<!-- initial context node is article -->
<xsl:template name="make-back">

  <!-- change context node to back -->
  <xsl:for-each select="back">
    <xsl:apply-templates select="title"/>
    <xsl:apply-templates select="ack"/>
    <xsl:call-template name="author-contrib"/>
    <xsl:apply-templates select="notes"/>
    <xsl:apply-templates select="*[not(self::title) and not(self::fn-group) and not(self::ack) 
                                 and not(self::notes)]"/>
    <xsl:call-template name="nl-1"/>
    <xsl:for-each select="//abstract[@abstract-type='patient']">
      <div class="patient">
        <a id="patient" name="patient" toc="patient" title="Patient Summary"/>
        <h3 xpathLocation="noSelect"><xsl:value-of select="title"/><xsl:call-template name="topAnchor"/></h3>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </div>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<xsl:template name="author-contrib">
  <xsl:if test="../front/article-meta/author-notes/fn[@fn-type='con']">
    <div class="contributions"><a id="authcontrib" name="authcontrib" toc="authcontrib"
      title="Author Contributions"/><h3 xpathLocation="noSelect">Author Contributions<xsl:call-template name="topAnchor"/></h3>
      <p xpathLocation="noSelect">
        <xsl:apply-templates select="../front/article-meta/author-notes/fn[@fn-type='con']"/>
      </p>
    </div>
  </xsl:if>
</xsl:template>


<!-- ============================================================= -->
<!--  11. MAKE-POST-PUBLICATION                                    -->
<!-- ============================================================= -->

<!-- At present the transform does not support
     subarticles and responses. To include that
     support in the present structure, fill out
     this template, call the "make-a-piece"
     template to ensure the details are handled
     in the same way and by the same templates
     as for the main article body. -->


<!-- ============================================================= -->
<!--  12. MAKE-END-METADATA                                        -->
<!-- ============================================================= -->

<!-- This metadata is displayed after the back and figs-and-tables
     because (when it it exists) it will be too long to display
     with the other metadata that is displayed before the body.    -->

<!-- It is metadata for retrieval: categories, keywords, etc.      -->

<!-- The context node when this template is called is the article
     or, when supported, the sub-article or response.              -->

<xsl:template name="make-end-metadata">

  <!-- change context node -->
  <xsl:for-each select="front/article-meta">
    <xsl:if test="article-categories
                | kwd-group
                | related-article
                | conference">
      <hr class="part-rule"/>
      <table width="100%" class="fm">
        <xsl:call-template name="table-setup-l-wide"/>
        <xsl:call-template name="nl-1"/>
        <tr>
          <xsl:call-template name="nl-1"/>
          <td colspan="2" valign="top">

            <!-- hierarchical subjects -->
            <xsl:apply-templates select="article-categories"/>
            <br/>

            <!-- keyword group -->
            <xsl:apply-templates select="kwd-group"/>

            <!-- related article -->
            <xsl:apply-templates select="related-article"/>

            <!-- conference information -->
            <xsl:apply-templates select="conference"/>
          </td>
          <xsl:call-template name="nl-1"/>
        </tr>
        <xsl:call-template name="nl-1"/>
      </table>
      <xsl:call-template name="nl-1"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>


<!-- ============================================================= -->
<!--  Article Categories                                           -->
<!-- ============================================================= -->

<xsl:template match="article-categories">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="subj-group">
  <xsl:if test="not(parent::subj-group)">
      <span class="gen"><xsl:text>Article Categories:</xsl:text></span>
  </xsl:if>
  <ul>
    <xsl:apply-templates/>
  </ul>
</xsl:template>

<xsl:template match="subject">
  <li>
    <xsl:apply-templates/>
  </li>
</xsl:template>

<!-- There may be many series-title elements; there
     may be one series-text (description) element. -->
<xsl:template match="series-title">
  <xsl:if test="not(preceding-sibling::series-title)">
    <span class="gen"><xsl:text>Series: </xsl:text></span>
  </xsl:if>
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
  <xsl:if test="not(following-sibling::*)">
    <br/>
  </xsl:if>
</xsl:template>

<xsl:template match="series-text">
  <xsl:apply-templates/>
  <br/>
</xsl:template>


<!-- ============================================================= -->
<!--  Keywords                                                     -->
<!-- ============================================================= -->

<!-- kwd-group and its kwd occur only in article-meta -->
<xsl:template match="kwd-group">
  <span class="gen">
    <xsl:call-template name="make-id"/>
    <xsl:text>Keywords: </xsl:text>
  </span>
  <xsl:apply-templates/>
  <br/>
</xsl:template>

<xsl:template match="kwd">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </span>

  <xsl:call-template name="make-keyword-punct"/>
</xsl:template>

<xsl:template name="make-keyword-punct">

  <xsl:choose>
    <xsl:when test="following-sibling::kwd">
      <xsl:text>, </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>.</xsl:text>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>


<!-- ============================================================= -->
<!--  Related article                                              -->
<!-- ============================================================= -->

<xsl:template match="related-article">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <span class="gen">
          <xsl:text>Related Article(s): </xsl:text>
        </span>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="gen">
        <xsl:call-template name="make-id"/>
        <xsl:text>Related Article(s): </xsl:text>
      </span>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
  <br/>
</xsl:template>


<!-- ============================================================= -->
<!--  Conference                                                   -->
<!-- ============================================================= -->

<xsl:template match="conference">
  <span class="gen"><xsl:text>Conference: </xsl:text></span>
  <xsl:call-template name="make-conference"/>
  <br/>
</xsl:template>

<!-- doesn't use conf-num, conf-sponsor, conf-theme -->
<xsl:template name="make-conference">
  <xsl:apply-templates select="conf-acronym" mode="add-period"/>
  <xsl:apply-templates select="conf-name" mode="add-period"/>
  <xsl:apply-templates select="conf-loc" mode="add-period"/>
  <xsl:apply-templates select="conf-date" mode="add-period"/>
</xsl:template>

<xsl:template match="*" mode="add-period">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>


<!-- ============================================================= -->
<!--  NARRATIVE CONTENT AND COMMON STRUCTURES                      -->
<!-- ============================================================= -->


<!-- ============================================================= -->
<!--  13. PARAGRAPH WITH ITS SUBTLETIES                            -->
<!-- ============================================================= -->

<xsl:template match="p">
  <p>
    <xsl:call-template name="makeXpathLocation" >
    </xsl:call-template>
    <xsl:apply-templates/>
  </p>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- The first p in a footnote displays the fn symbol or,
     if no symbol, the fn ID -->
<xsl:template match="fn/p[1]">
  <xsl:choose>
    <xsl:when test="parent::fn/@fn-type='financial-disclosure'"><xsl:apply-templates/></xsl:when>
    <xsl:when test="parent::fn/@fn-type='conflict'"><xsl:apply-templates/></xsl:when>
    <xsl:otherwise>
      <p>
        <xsl:call-template name="make-id"/>
        <xsl:if test="../@symbol | ../@id">
        <sup>
          <xsl:choose>
            <xsl:when test="../@symbol">
              <xsl:value-of select="../@symbol"/>
            </xsl:when>
            <xsl:when test="../@id">
              <xsl:value-of select="../@id"/>
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
        </sup>
      </xsl:if>
      <xsl:apply-templates/>
      </p>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="speech/p[1]">
  <p>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates select="preceding-sibling::speaker" mode="show-it-here"/>
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<!-- ================================================================== -->
<!--  13a. FIND ALL TEXT NODES AND                                      -->
<!--          FIX WORDS LONGER THEN 20 characters                       -->
<!--          DO SOME TEXT PROCESSING                                   -->
<!-- ================================================================== -->

<xsl:template match="text()">
  <!-- We do some character transformations first-->
  <xsl:variable name="str" select="translate(., '&#8194;&#x200A;&#8764;&#x02236;&#x02208;', '  ~:&#x404;') "/>

  <xsl:choose>
    <!-- No need to progress further in the entire element is less then 40 characters -->
    <xsl:when test="string-length($str) &gt; 40">
      <xsl:call-template name="linebreaklongwords">
        <xsl:with-param name="str" select="$str" />
        <xsl:with-param name="len" select="40" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$str"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Break words longer then len characters -->
<xsl:template name="linebreaklongwords">
  <xsl:param name="str"/>
  <xsl:param name="len"/>
  <xsl:for-each select="tokenize($str,'\s')">
    <xsl:choose>
      <xsl:when test="string-length(.) &gt; $len">
        <xsl:call-template name="linebreaklongwordsub">
          <xsl:with-param name="str" select="." />
          <xsl:with-param name="len" select="$len" />
          <!-- zero length space -->
          <xsl:with-param name="char"><xsl:text>&#8203;</xsl:text></xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="position()=last()">
            <xsl:copy-of select="."/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="."/><xsl:text> </xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="linebreaklongwordsub">
  <xsl:param name="str"/>
  <xsl:param name="len"/>
  <xsl:param name="char"/>
  <xsl:choose>
    <xsl:when test="string-length($str) &gt; $len">
      <xsl:value-of select="substring($str,1,$len)"/>
      <xsl:value-of select="$char"/>
      <xsl:call-template name="linebreaklongwordsub">
        <xsl:with-param name="str" select="substring($str,$len + 1)" />
        <xsl:with-param name="len" select="$len" />
        <xsl:with-param name="char" select="$char" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$str"/><xsl:text> </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- prevent the first def/p from causing a p tag
     which would display an unwanted break -->
<!--
<xsl:template match="def/p[1]">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>
-->


<!-- ============================================================= -->
<!--  14. SECTION                                                  -->
<!-- ============================================================= -->

<!-- the first body/sec puts out no rule at its top,
     because body already puts out a part-rule at its top;
     subsequent body/secs do put out a section-rule -->
<xsl:template match="body/sec">
  <xsl:call-template name="nl-1"/>
  <div>
    <xsl:call-template name="make-section-id"/>
    <xsl:call-template name="makeXpathLocation" >
    </xsl:call-template>
    <xsl:if test="descendant::title[1] != ''">
      <xsl:element name="a">
        <xsl:attribute name="id"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
        <xsl:attribute name="toc"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="descendant::title[1]"></xsl:value-of>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>
    <xsl:apply-templates/>
  </div>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- no other level of sec puts out a rule -->
<xsl:template match="sec">
  <xsl:apply-templates/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  15. LIST and its Internals                                   -->
<!-- ============================================================= -->

<xsl:template match="list">
  <xsl:call-template name="nl-1"/>
  <xsl:choose>
    <xsl:when test="@list-type='bullet'">
      <xsl:call-template name="nl-1"/>
      <ul>
        <xsl:call-template name="nl-1"/>
        <xsl:apply-templates/>
        <xsl:call-template name="nl-1"/>
      </ul>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="nl-1"/>
      <ol>
        <xsl:attribute name="class">
          <xsl:value-of select="@list-type"></xsl:value-of>
        </xsl:attribute>
        <xsl:call-template name="nl-1"/>
        <xsl:apply-templates/>
        <xsl:call-template name="nl-1"/>
      </ol>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="list-item">
  <xsl:call-template name="nl-1"/>
  <li>
    <xsl:apply-templates/>
  </li>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  16. DISPLAY-QUOTE                                            -->
<!-- ============================================================= -->

<xsl:template match="disp-quote">
  <xsl:call-template name="nl-1"/>
  <blockquote>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </blockquote>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  17. SPEECH and its internals                                 -->
<!-- ============================================================= -->

<!-- first p will pull in the speaker
     in mode "show-it-here" -->
<xsl:template match="speech">
  <blockquote>
    <xsl:call-template name="make-id"/>
    <xsl:call-template name="nl-1"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </blockquote>
</xsl:template>

<xsl:template match="speaker" mode="show-it-here">
  <b><xsl:apply-templates/></b>
</xsl:template>

<!-- in no mode -->
<xsl:template match="speaker"/>


<!-- ============================================================= -->
<!--  18. STATEMENT and its internals                              -->
<!-- ============================================================= -->

<xsl:template match="statement">
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:call-template name="nl-1"/>
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="statement/label | statement/title">
  <xsl:call-template name="nl-1"/>
  <p><b><xsl:apply-templates/></b></p>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  19. VERSE-GROUP and its internals                            -->
<!-- ============================================================= -->

<xsl:template match="verse-group">
  <xsl:call-template name="nl-1"/>
  <blockquote>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </blockquote>
</xsl:template>

<xsl:template match="verse-line">
  <xsl:call-template name="nl-1"/>
  <xsl:apply-templates/>
  <br/>
</xsl:template>


<!-- ============================================================= -->
<!--  20. BOXED-TEXT                                               -->
<!-- ============================================================= -->

<xsl:template match="boxed-text">
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
    <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <xsl:element name="div">
    <xsl:attribute name="class">box</xsl:attribute>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>


<!-- ============================================================= -->
<!--  21. PREFORMAT                                                -->
<!-- ============================================================= -->

<xsl:template match="preformat" name="format-as-line-for-line">
  <pre><xsl:call-template name="make-id"/><xsl:apply-templates/></pre>
</xsl:template>


<!-- ============================================================= -->
<!--  22. SUPPLEMENTARY MATERIAL                                   -->
<!-- ============================================================= -->

<xsl:template match="supplementary-material">
  <xsl:variable name="the-label">
    <xsl:choose>
      <xsl:when test="label">
        <xsl:value-of select="label"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>Supplementary Material</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:element name="a">
  <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
  <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <p>
    <strong xPathLocation="noSelect">
     <xsl:element name="a">
       <xsl:variable name="objURI"><xsl:value-of select="@xlink:href"/></xsl:variable>
       <xsl:attribute name="href">
        <xsl:value-of select="concat($pubAppContext,'/article/fetchSingleRepresentation.action?uri=',
                                     $objURI)"/>
      </xsl:attribute>
      <xsl:apply-templates select="label"/>
    </xsl:element>
    <xsl:apply-templates select="caption/title"/>
    </strong>
  </p>
  <xsl:apply-templates select="caption/p"/>
</xsl:template>


<!-- ============================================================= -->
<!--  23. DISPLAY FORMULA, CHEM-STRUCT-WRAPPER                     -->
<!-- ============================================================= -->

<!-- both are grouping elements to keep parts together -->
<xsl:template match="disp-formula | chem-struct-wrapper">
  <br/>
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
    <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <!-- Added span class='equation' surrounding equations -->
  <span class="equation">
    <xsl:apply-templates select="*[not(self::label)]"/>
    <xsl:apply-templates select="label"/>
  </span>
  <br/>
</xsl:template>


<!-- ============================================================= -->
<!--  24. FORMATTING ELEMENTS                                      -->
<!-- ============================================================= -->

<xsl:template match="bold"><b><xsl:apply-templates/></b></xsl:template>

<xsl:template match="italic"><i><xsl:apply-templates/></i></xsl:template>

<xsl:template match="monospace">
  <span class="monospace"><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="overline">
  <span class="overline">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="sc">
  <!-- handle any tags as usual, until we're down to the text strings -->
  <small><xsl:apply-templates/></small>
</xsl:template>

<xsl:template match="sc//text()">
  <xsl:param name="str" select="."/>
  <xsl:call-template name="capitalize">
    <xsl:with-param name="str" select="$str"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="strike">
  <s><xsl:apply-templates/></s>
</xsl:template>

<xsl:template match="sub"><sub><xsl:apply-templates/></sub></xsl:template>

<xsl:template match="sup"><sup><xsl:apply-templates/></sup></xsl:template>

<xsl:template match="underline"><u><xsl:apply-templates/></u></xsl:template>

<!-- ============================================================= -->
<!--  25. SEMANTIC ELEMENTS                                        -->
<!-- ============================================================= -->

<xsl:template match="abbrev">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="inline-graphic">
  <xsl:element name="img">
    <xsl:if test="@xlink:href">
      <xsl:variable name="graphicDOI"><xsl:value-of select="@xlink:href"/></xsl:variable>
      <xsl:attribute name="src">
        <xsl:value-of select="concat($pubAppContext,'/article/fetchObject.action?uri=',$graphicDOI,
                                     '&amp;representation=PNG')"/>
      </xsl:attribute>
      <xsl:attribute name="border">0</xsl:attribute>
    </xsl:if>
  </xsl:element>
</xsl:template>

<xsl:template match="inline-formula">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- is meant be a link: we assume the xlink:href
     attribute is used, although it is not
     required by the DTD. -->
<xsl:template match="inline-supplementary-material">
  <xsl:call-template name="nl-1"/>
  <a>
    <xsl:call-template name="make-href"/>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="glyph-data">
  <xsl:call-template name="nl-1"/>
  <span class="take-note">
    <xsl:call-template name="make-id"/>
    <xsl:text>[glyph data here: ID=</xsl:text>
    <xsl:value-of select="@id"/>
    <xsl:text>]</xsl:text>
  </span>
</xsl:template>


<!-- ============================================================= -->
<!--  Named Content                                                -->
<!-- ============================================================= -->

<xsl:template match="named-content">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="@content-type">
        <span>
          <xsl:attribute name="class"><xsl:value-of select="@content-type" /></xsl:attribute>
          <xsl:call-template name="make-id"/>
          <xsl:apply-templates/>
        </span>   
      </xsl:if>
      <xsl:if test="not(@content-type)">
        <span class="capture-id">
          <xsl:call-template name="make-id"/>
          <xsl:apply-templates/>
        </span>
       </xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  26. BREAK AND HORIZONTAL RULE                                -->
<!-- ============================================================= -->

<xsl:template match="break" name="make-break">
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="hr" name="make-rule">
  <xsl:call-template name="nl-1"/>
  <hr/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  27. CHEM-STRUCT                                              -->
<!-- ============================================================= -->

<xsl:template match="chem-struct">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:call-template name="display-id"/>
    <xsl:choose>
      <xsl:when test="@xlink:href">
        <a>
          <xsl:call-template name="make-href"/>
          <xsl:apply-templates/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </span>
</xsl:template>


<!-- ============================================================= -->
<!--  28. TEX-MATH and MML:MATH                                    -->
<!-- ============================================================= -->

<xsl:template match="tex-math">
  <span class="take-note">
    <xsl:text>[tex-math code here]</xsl:text>
  </span>
</xsl:template>

<!-- can presume this is meant to be inline -->
<xsl:template match="inline-formula//mml:math">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- we don't -know- mml:math in general to be inline,
     so treat it as block.
     Put it in a table to get a pretty border. -->
<xsl:template match="mml:math">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <table border="1">
        <tr>
          <td valign="top">
            <a>
              <xsl:call-template name="make-href"/>
              <xsl:call-template name="make-id"/>
              <xsl:apply-templates/>
            </a>
          </td>
        </tr>
      </table>
    </xsl:when>
    <xsl:otherwise>
      <table border="1">
        <tr>
          <td valign="top">
            <span>
              <xsl:call-template name="make-id"/>
              <xsl:apply-templates/>
            </span>
          </td>
        </tr>
      </table>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  29. GRAPHIC and MEDIA                                        -->
<!-- ============================================================= -->

<xsl:template match="graphic">
  <xsl:element name="img">
    <xsl:if test="@xlink:href">
    <xsl:variable name="graphicDOI"><xsl:value-of select="@xlink:href"/></xsl:variable>
    <xsl:attribute name="src">
      <xsl:value-of select="concat($pubAppContext,'/article/fetchObject.action?uri=',
                                   $graphicDOI,'&amp;representation=PNG')"/>
    </xsl:attribute>
    </xsl:if>
  </xsl:element>
</xsl:template>

<xsl:template match="media">
  <a>
    <xsl:call-template name="make-href"/>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </a>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  30. ARRAY                                                    -->
<!-- ============================================================= -->

<xsl:template match="array">
  <hr width="40%" align="left" noshade="1"/>
  <xsl:call-template name="nl-1"/>
  <table>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </table>
  <xsl:call-template name="nl-1"/>
  <hr width="40%" align="left" noshade="1"/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  31. CAPTIONING                                               -->
<!-- ============================================================= -->

<!-- the chooses before and after the element content
     tweak the display as appropriate -->
<xsl:template match="label | alt-text | attrib">

  <!-- element-specific handling before content: -->
  <xsl:choose>
    <xsl:when test="ancestor::disp-formula">

      <!-- Set class to 'note' for the labels on equations -->
      <span class="note"><xsl:call-template name="makeXpathLocation" /><xsl:apply-templates/></span>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>

        <!-- alt-text gets a generated label-->
        <xsl:when test="self::alt-text">
          <xsl:if test="not(ancestor::fig)
                        and not(ancestor::table)"><br/></xsl:if>
          <span class="gen">
            <xsl:call-template name="make-id"/>
            <xsl:text>Alternate Text: </xsl:text>
          </span>
        </xsl:when>

        <!-- attrib is preceded by spaces plus em-dash -->
        <xsl:when test="self::attrib">
          <xsl:text>&#8194;&#8194;&#8212;</xsl:text>
        </xsl:when>
      </xsl:choose>
      <xsl:apply-templates/>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>

  <!-- element-specific handling after content: -->
  <xsl:choose>

    <!-- alt-text and long-desc get a break after -->
    <xsl:when test="self::alt-text | self::long-desc"><br/></xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="caption">
  <xsl:apply-templates/>
</xsl:template>

<!-- mixed-content; used in figures, tables, etc. -->
<xsl:template match="long-desc">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </span>
  <br/>
</xsl:template>

<xsl:template match="object-id">
  <xsl:choose>
    <xsl:when test="@pub-id-type">
      <xsl:value-of select="@pub-id-type"/>
    </xsl:when>
    <xsl:otherwise>
      <span class="gen">
        <xsl:text>Object ID</xsl:text>
      </span>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>:</xsl:text>
  <xsl:apply-templates/>
</xsl:template>


<!-- ============================================================= -->
<!--  32. FIGURE, MODE PUT-AT-END                                  -->
<!-- ============================================================= -->

<!-- each figure is a row -->
<xsl:template match="fig" mode="put-at-end">

    <!-- left column:  graphic
         right column: captioning elements - label, caption, etc. -->
    <tr>
    <xsl:call-template name="nl-1"/>
      <td valign="top">
        <xsl:apply-templates select="graphic"/>
        <br/>
        <span class="gen">
          <xsl:call-template name="make-id"/>
          <xsl:text>[Figure ID: </xsl:text>
        </span>
        <xsl:value-of select="@id"/>
        <span class="gen">
          <xsl:text>] </xsl:text>
        </span>
      </td>
      <xsl:call-template name="nl-1"/>
      <td valign="top">
        <xsl:apply-templates select="child::*[not(self::graphic)]"/>
      </td>
      <xsl:call-template name="nl-1"/>
    </tr>
    <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  33. TABLE-WRAP, MODE PUT-AT-END                              -->
<!-- ============================================================= -->

<xsl:template match="table-wrap" mode="put-at-end">
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates select="@id"/>
    <xsl:apply-templates/>
    <br/>
  </div>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="table-wrap/@id">
  <span class="gen">
    <xsl:text>[TableWrap ID: </xsl:text>
  </span>
  <xsl:value-of select="."/>
  <span class="gen">
    <xsl:text>] </xsl:text>
  </span>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="table">
  <table width="100%" class="bm">
    <xsl:if test="@frame">
      <xsl:attribute name="frame">
        <xsl:value-of select="@frame"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@rules">
      <xsl:attribute name="rules">
        <xsl:value-of select="@rules"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:call-template name="nl-1"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </table>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="thead">
  <thead>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </thead>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="th">
  <th>
    <xsl:call-template name="make-id"/>
    <xsl:if test="@colspan">
      <xsl:attribute name="colspan">
        <xsl:value-of select="@colspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </th>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="tr">
  <tr>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="td">
  <td valign="top">
    <xsl:call-template name="make-id"/>
    <xsl:if test="@colspan">
      <xsl:attribute name="colspan">
        <xsl:value-of select="@colspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@rowspan">
      <xsl:attribute name="rowspan">
        <xsl:value-of select="@rowspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </td>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="tfoot | table-wrap-foot">
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </div>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!-- MODE front                                                    -->
<!-- ============================================================= -->

<!--
<xsl:template match="journal-meta/journal-id
                   | journal-meta/journal-title
                   | journal-meta/journal-abbrev-title
                   | journal-meta/publisher"/>
-->


<!-- ============================================================= -->
<!--  34) JOURNAL-META (in order of appearance in output)          -->
<!-- ============================================================= -->

<!-- journal-id -->
<xsl:template match="journal-id[@journal-id-type]" mode="front">
  <span class="gen"><xsl:text>Journal ID (</xsl:text></span>
  <xsl:value-of select="@journal-id-type"/>
  <span class="gen"><xsl:text>): </xsl:text></span>
  <xsl:value-of select="."/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- abbrev-journal-title -->
<xsl:template match="abbrev-journal-title" mode="front">
  <span class="gen"><xsl:text>Journal Abbreviation: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- issn -->
<xsl:template match="issn" mode="front">
  <span class="gen"><xsl:text>ISSN: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- publisher -->
<!-- required name, optional location -->
<xsl:template match="publisher" mode="front">
  <xsl:apply-templates mode="front"/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="publisher-name" mode="front">
  <span class="gen"><xsl:text>Publisher: </xsl:text></span>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="publisher-loc" mode="front">

  <!-- if present, follows a publisher-name, so produces a comma -->
  <xsl:text>, </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<!-- notes -->
<xsl:template match="notes" mode="front">
  <span class="gen">Notes: </span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  35) ARTICLE-META (in order of appearance in output)          -->
<!-- ============================================================= -->

<!-- ext-link -->
<xsl:template match="ext-link" mode="front">
  <span class="gen">
    <xsl:call-template name="make-id"/>
    <xsl:text>Link: </xsl:text>
  </span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- supplementary-material -->
<!-- Begins with:
    Object Identifier <object-id>, zero or more
    Label (Of a Figure, Reference, Etc.) <label>, zero or one
    Caption of a Figure, Table, Etc. <caption>, zero or one
    Any combination of:
      All the accessibility elements:
        Alternate Title Text (For a Figure, Etc.) <alt-text>
        Long Description <long-desc>
      All the address linking elements:
        Email Address <email>
        External Link <ext-link>
        Uniform Resource Indicator (URI) <uri>

  Then an ordinary combination of para-level elements

  Ending with:
    Any combination of:
    Attribution <attrib>
    Copyright Statement <copyright-statement>
-->

<xsl:template match="supplementary-material" mode="front">
  <span class="gen"><xsl:text>Supplementary Material:</xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- self-uri -->
<xsl:template match="self-uri" mode="front">
  <a href="@xlink:href"><span class="gen"><xsl:text>Self URI: </xsl:text></span></a>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- product -->
<!-- uses mode="product" within -->
<xsl:template match="product" mode="front">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <span class="gen"><xsl:text>Product Information: </xsl:text></span>
        <xsl:apply-templates mode="product"/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="gen"><xsl:text>Product Information: </xsl:text></span>
      <xsl:apply-templates mode="product"/>
    </xsl:otherwise>
  </xsl:choose>
  <br/><br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- The product element allows a mixed-content model,
     but perhaps sometimes only element nodes will be used.
     Rough test:
       - if the next sibling is another element,
         add a space to make the content somewhat legible. -->
<xsl:template match="*" mode="product">
  <xsl:apply-templates/>
  <xsl:if test="generate-id(following-sibling::node()[1])
                 =generate-id(following-sibling::*[1])">
  <xsl:text> </xsl:text>
  </xsl:if>
</xsl:template>

<!-- copyright-statement, copyright-year, copyright-holder -->
<!--<xsl:template match="copyright-statement | copyright-year | copyright-holder" mode="front">
  <xsl:apply-templates/>
  <xsl:call-template name="nl-1"/>
</xsl:template>-->

<!-- history -->
<xsl:template match="history/date" mode="front">
  <xsl:variable name="the-type">
    <xsl:choose>
      <xsl:when test="@date-type='accepted'">Accepted</xsl:when>
      <xsl:when test="@date-type='received'">Received</xsl:when>
      <xsl:when test="@date-type='rev-request'">Revision Requested</xsl:when>
      <xsl:when test="@date-type='rev-recd'">Revision Received</xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:if test="@date-type">
    <span class="gen">
      <xsl:value-of select="$the-type"/>
      <xsl:text> </xsl:text>
    </span>
  </xsl:if>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- pub-date -->
<xsl:template match="pub-date" mode="front">
  <xsl:choose>
    <xsl:when test="@pub-type='ppub'">
      <span class="gen">Print </span>
    </xsl:when>
    <xsl:when test="@pub-type='epub'">
      <span class="gen">Electronic </span>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@pub-type"/>
    </xsl:otherwise>
  </xsl:choose>
  <span class="gen"><xsl:text> publication date: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- volume -->
<xsl:template match="volume" mode="front">
  <span class="gen"><xsl:text>Volume: </xsl:text></span>
  <xsl:apply-templates/>
  <xsl:if test="../issue">
    <xsl:text> </xsl:text>
  </xsl:if>
</xsl:template>

<!-- issue -->
<xsl:template match="issue" mode="front">
  <span class="gen"><xsl:text>Issue: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- elocation-id -->
<xsl:template match="elocation-id" mode="front">
  <span class="gen"><xsl:text>E-location ID: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- fpage, lpage -->
<xsl:template match="fpage" mode="front">
  <span class="gen"><xsl:text>First Page: </xsl:text></span>
  <xsl:apply-templates/>
  <xsl:choose>
    <xsl:when test="../lpage">
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <br/>
      <xsl:call-template name="nl-1"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="lpage" mode="front">
  <span class="gen"><xsl:text>Last Page: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- article-id -->
<xsl:template match="article-id">
  <xsl:choose>
    <xsl:when test="@pub-id-type='coden'">
      <span class="gen"><xsl:text>Coden: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='doi'">
      <span class="gen"><xsl:text>DOI: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='medline'">
      <span class="gen"><xsl:text>Medline Id: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='pii'">
      <span class="gen"><xsl:text>Publisher Item Identifier: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='pmid'">
      <span class="gen"><xsl:text>PubMed Id: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='publisher-id'">
      <span class="gen"><xsl:text>Publisher Id: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='sici'">
      <span class="gen"><xsl:text>Serial Item and Contribution Identifier: </xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='doaj'">
      <span class="gen"><xsl:text>Directory of Open Access Journals</xsl:text></span>
    </xsl:when>
    <xsl:when test="@pub-id-type='other'">
      <span class="gen"><xsl:text>Article Id: </xsl:text></span>
    </xsl:when>
    <xsl:otherwise>
      <span class="gen"><xsl:text>ID: </xsl:text></span>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- contract-num, contract-sponsor -->
<xsl:template match="contract-num | contract-sponsor" mode="front">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
        <br/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </span>
      <br/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  36) TITLE-GROUP                                              -->
<!-- ============================================================= -->

<!-- title-group -->
<!-- Appears only in article-meta -->
<!-- The fn-group, if any, is output in the "back" of the
     HTML page, together with any other fn-group. -->
<xsl:template match="title-group" mode="front">
  <xsl:apply-templates select="subtitle" mode="front"/>
</xsl:template>

<xsl:template match="article-title" mode="front">
  <h1 xpathLocation="noSelect">
    <xsl:apply-templates/>
  </h1>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- subtitle runs in with title -->
<xsl:template match="subtitle" mode="front">
  <h2 xpathLocation="noSelect">
    <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match="trans-title" mode="front">
  <span class="tl-section-level">
    <span class="gen">Translated title: </span>
    <xsl:apply-templates/>
  </span>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="alt-title" mode="front">
  <span class="tl-default">
    <xsl:choose>
      <xsl:when test="@alt-title-type='right-running-head'">
        <span class="gen">Title for RRH: </span>
      </xsl:when>
      <xsl:otherwise>
        <span class="gen">Alternate Title: </span>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
  </span>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  37) PARTS OF CONTRIB                                         -->
<!-- ============================================================= -->

<!-- collab -->
<!-- A mixed-content model; process it as given -->
<xsl:template match="collab" mode="front">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="../xref" mode="contrib"/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="../xref" mode="contrib"/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="collab" mode="article-meta">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="../xref" mode="contrib"/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="../xref" mode="contrib"/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- name -->
<!-- uses mode="contrib" within -->
<xsl:template match="name" mode="front">
  <xsl:apply-templates select="prefix"      mode="contrib"/>
  <xsl:apply-templates select="given-names" mode="contrib-abbr"/>
  <xsl:apply-templates select="surname"     mode="contrib"/>
  <xsl:apply-templates select="suffix"      mode="contrib"/>
  <xsl:apply-templates select="../degrees"  mode="contrib"/>
</xsl:template>

<!-- Ordering of author footnotes is as follows:

1,2 - Indicates affiliation(s). 
      Use numbers when there is more than one affiliation. Comma separate multiple affiliations.
      <xref ref-type="aff"><sup>1</sup></xref>
 
[ - Indicates equal authorship (in XML/HTML marked up as # instead of with yin-yang image).
    <contrib contrib-type="author" equal-contrib="yes">
 
¶ - Indicates additional sets of equally contributing authors.
    <xref ref-type="fn"><sup>&#xb6;</sup></xref>
 
¤ - Indicates current address. 
    Append letters when there is more than one current address (¤a, ¤b, ¤c, etc.)
    <xref ref-type="fn"><sup>&curren;</sup></xref> or <xref ref-type="fn" rid="">
    <sup>&curren;a</sup></xref>
 
† - Indicates author is deceased
    <xref ref-type="fn"><sup>&dagger;</sup></xref>
 
[Other custom footnote symbol(s), as indicated in manuscript]
    <xref ref-type="fn"><sup>[some kind of symbol here]</sup></xref>
 
* - Indicates corresponding author <corresp>
    <xref ref-type="corresp">
-->
  
<xsl:template match="name" mode="front-refs">
  <xsl:apply-templates select="../xref[@ref-type='aff']" mode="contrib"/>
    <xsl:if test="../@equal-contrib='yes'">
        <sup><a href="#equal-contrib">#</a></sup>
  </xsl:if>
  <xsl:apply-templates select="../xref[@ref-type='fn']" mode="contrib"/>
  
  <!-- Checking if the deceased attribute is set and there isn't already a deceased footnote,
       output a dagger. However, be careful in checking for the existence of an editor defined
       deceased fn. -->
  <xsl:if test="../@deceased='yes' and not(../xref/sup='‡') and not(../ref/sup='&amp;dagger;') 
                and not(../ref/sup='&amp;Dagger;')">
    <sup><a href="#deceased">&#x2020;</a></sup>
  </xsl:if>
  <xsl:apply-templates select="../xref[@ref-type='corresp']" mode="contrib"/>  
  <xsl:apply-templates select="../xref[@ref-type='author-notes']" mode="contrib"/>  
</xsl:template>

<xsl:template match="name" mode="article-meta">
  <xsl:apply-templates select="prefix"      mode="contrib"/>
  <xsl:apply-templates select="given-names" mode="contrib"/>
  <xsl:apply-templates select="surname"     mode="contrib"/>
  <xsl:apply-templates select="suffix"      mode="contrib"/>
  <xsl:apply-templates select="../degrees"  mode="contrib"/>
  <xsl:apply-templates select="../xref"     mode="contrib"/>
</xsl:template>

<xsl:template match="prefix | given-names" mode="contrib">
  <xsl:apply-templates/>
  <xsl:text> </xsl:text>
</xsl:template>

<!-- added abbreviate-name template to correctly put a period after a single initial middle name -->
<xsl:template match="given-names" mode="contrib-abbr">
  <xsl:call-template name="abbreviate-name">
	  <xsl:with-param name="n" select="."/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template name="abbreviate-name">
  <xsl:param name="n"/>		
  <xsl:variable name="x" select="normalize-space($n)"/>
  <xsl:value-of select="$x"/>
  <xsl:if test="substring($x,string-length($x),1) != '.' 
    and (string-length($x) = 1 
    or (string-length($x) > 1 
    and substring($x,string-length($x)-1,1)=' '))">
    <xsl:text>.</xsl:text>
  </xsl:if>
</xsl:template>	

<xsl:template match="surname" mode="contrib">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="suffix" mode="contrib">
  <xsl:text>, </xsl:text><xsl:apply-templates/>
</xsl:template>

<xsl:template match="degrees" mode="contrib">
  <xsl:text>, </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<!-- the formatting is sometimes in the source XML,
     e.g., <sup><italic>a</italic></sup> -->
<xsl:template match="xref[@ref-type='author-notes']" mode="contrib">
  <xsl:choose>
    <xsl:when test="not(.//italic) and not (.//sup)">
      <sup><i>
      <xsl:element name="a">
        <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
        <xsl:apply-templates/>
      </xsl:element>
      </i></sup>
    </xsl:when>
    <xsl:when test="not(.//italic)">
      <i>
      <xsl:element name="a">
        <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
        <xsl:attribute name="class">fnoteref</xsl:attribute> 
        <xsl:value-of select="sup"/>
      </xsl:element>
      </i>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xref[@ref-type='corresp']" mode="contrib">
  <xsl:if test="./sup">
    <sup>
    <xsl:element name="a">
      <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
      <xsl:attribute name="class">fnoteref</xsl:attribute>
       <xsl:value-of select="sup"/>
    </xsl:element>
    </sup>
  </xsl:if>
</xsl:template>

<!-- the formatting is sometimes in the source XML,
     e.g., <sup><italic>a</italic></sup> -->
<xsl:template match="xref[@ref-type='aff']" mode="contrib">
  <xsl:if test="./sup">
    <sup>
      <xsl:element name="a">
        <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
        <xsl:value-of select="sup"/>
      </xsl:element>
    </sup>
  </xsl:if>
  <xsl:if test="following-sibling::xref[@ref-type='aff']"><sup>,</sup></xsl:if>
</xsl:template>

<xsl:template match="xref[@ref-type='fn']" mode="contrib">
  <xsl:if test="./sup">
    <sup>
      <xsl:element name="a">
        <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
        <xsl:value-of select="sup"/>
      </xsl:element>
    </sup>
  </xsl:if>
  <xsl:if test="following-sibling::xref[@ref-type='fn']"><sup>,</sup></xsl:if>
</xsl:template>

<!-- author-comment -->
<!-- optional title, one-or-more paras -->
<xsl:template match="author-comment | bio" mode="front">
  <xsl:variable name="the-title">
    <xsl:choose>
      <xsl:when test="title">
        <xsl:apply-templates select="title" mode="front"/>
      </xsl:when>
      <xsl:when test="self::author-comment">
        <xsl:text>Author Comment: </xsl:text>
      </xsl:when>
      <xsl:when test="self::bio">
        <xsl:text>Bio: </xsl:text>
      </xsl:when>
      <!-- no logical otherwise -->
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:value-of select="$the-title"/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="make-id"/>
      <xsl:value-of select="$the-title"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="*[not(self::title)]" mode="front"/>
</xsl:template>
<xsl:template match="author-comment/title | bio/title" mode="front">
  <xsl:apply-templates/>
</xsl:template>

<!-- author-comment/p and bio/p in HTML give too much vertical
     space for the display situation; so we force them to produce
     only breaks. -->
<xsl:template match="author-comment/p | bio/p" mode="front">
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- parts of contrib: address -->
<xsl:template match="address" mode="front">
  <span class="gen">
    <xsl:call-template name="make-id"/>
    <xsl:text>Address: </xsl:text>
  </span>
  <xsl:apply-templates mode="front"/>
  <br/>
</xsl:template>

<xsl:template match="institution" mode="front">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="following-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="address/*" mode="front">
  <xsl:apply-templates/>
  <xsl:if test="following-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
</xsl:template>

<!-- aff -->
<!-- These affs are inside a contrib element -->
<xsl:template match="aff" mode="front">
  <xsl:apply-templates select="institution" /><xsl:text>, </xsl:text>
  <xsl:apply-templates select="addr-line" />
</xsl:template>

<!-- aff -->
<!-- These affs are NOT inside a contrib element -->
<xsl:template match="aff" mode="aff-outside-contrib">
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- on-behalf-of -->
<xsl:template match="on-behalf-of" mode="front">
  <span class="gen"><xsl:text>On behalf of: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- role -->
<xsl:template match="role" mode="front">
  <span class="gen"><xsl:text>Role: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- email -->
<xsl:template match="email" mode="front">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <span class="gen"><xsl:text>Email: </xsl:text></span>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="gen"><xsl:text>Email: </xsl:text></span>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- author-notes -->
<xsl:template match="author-notes" mode="front">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates mode="front"/>
  </span>
</xsl:template>

<!-- author-notes/title -->
<xsl:template match="author-notes/title" mode="front">
  <b><xsl:apply-templates/></b>
  <br/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- author-notes/corresp -->
<!-- mixed-content; process it as given -->
<xsl:template match="author-notes/corresp" mode="front">
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <xsl:apply-templates/>
</xsl:template>

<!-- author-notes/fn -->
<!-- optional label, one or more paras -->
<!-- unmoded (author-notes only appears in article-meta) -->
<xsl:template match="author-notes/fn[@fn-type='current-aff']" mode="front">
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="author-notes/fn[@fn-type='deceased']" mode="front">
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="author-notes/fn[@fn-type='other']" mode="front">
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
  </xsl:element>
  <xsl:apply-templates/>
</xsl:template>

<!-- author-notes/fn/label -->
<xsl:template match="author-notes/fn/label">
  <xsl:apply-templates/>
</xsl:template>

<!-- author-notes/fn/p[1] -->
<xsl:template match="author-notes/fn/p[1]" priority="2">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:choose>
      <xsl:when test="parent::fn/@fn-type='com'">
        <span class="gen">
          <xsl:text>Communicated by footnote: </xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='con'">
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='cor'">
        <span class="gen">
          <xsl:text>Correspondence: </xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='financial-disclosure'">
        <span class="gen">
          <xsl:text>Financial Disclosure: </xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='current-aff'">
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:when test="parent::fn/@symbol">
        <sup>
          <xsl:value-of select="parent::fn/@symbol"/>
        </sup>
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:when test="@fn-type">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="@fn-type"/>
        <xsl:text>]</xsl:text>
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='deceased'">
        <xsl:value-of select="@fn-type"/>
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:when test="parent::fn/@fn-type='other'">
		  <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <span class="gen">
          <xsl:text>*</xsl:text>
        </span>
        <xsl:text> </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- author-notes/fn/p processed as ordinary unmoded p-->
<!-- abstract and trans-abstract are handled entirely
     within the make-front template -->


<!-- ============================================================= -->
<!-- BACK (unmoded templates)                                      -->
<!-- ============================================================= -->


<!-- ============================================================= -->
<!--  38. BACK MATTER: ACKNOWLEDGEMENTS                            -->
<!-- ============================================================= -->

<xsl:template match="ack">
  <xsl:call-template name="nl-1"/>
  <xsl:if test="position()>1">
    <hr class="section-rule"/>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
  <div xpathLocation="noSelect" >
    <xsl:call-template name="make-id"/>
    <xsl:if test="not(title)">
      <a id="ack" name="ack" toc="ack" title="Acknowledgments"/><h3 xpathLocation="noSelect">Acknowledgments<xsl:call-template name="topAnchor"/></h3>
      <xsl:call-template name="nl-1"/>
    </xsl:if>
    <xsl:apply-templates/>
  </div>
</xsl:template>


<!-- ============================================================= -->
<!--  39. BACK-MATTER: APPENDIX                                    -->
<!-- ============================================================= -->

<xsl:template match="app">
  <xsl:text>&#xA;</xsl:text>
  <xsl:if test="position()>1">
    <hr class="section-rule"/>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </div>
</xsl:template>


<!-- ============================================================= -->
<!--  40. BACK-MATTER: FOOTNOTE-GROUP and FN                       -->
<!-- ============================================================= -->

<xsl:template match="fn-group">
  <xsl:call-template name="nl-1"/>
  <xsl:if test="position()>1">
    <hr class="section-rule"/>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
  <xsl:apply-templates/>
  <xsl:call-template name="nl-1"/>
</xsl:template>


<!-- ============================================================= -->
<!--  Footnote                                                     -->
<!-- ============================================================= -->

<!-- symbol or id is displayed by the first para within the fn     -->
<xsl:template match="fn">
    <xsl:apply-templates/>
</xsl:template>


<!-- ============================================================= -->
<!--  41. BACK-MATTER: NOTES                                       -->
<!-- ============================================================= -->

<xsl:template match="notes">
  <xsl:call-template name="nl-1"/>
  <xsl:if test="position()>1">
    <hr class="section-rule"/>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
    <xsl:call-template name="nl-1"/>
  </div>
</xsl:template>

<xsl:template match="notes/sec/title">
 <h3 xpathLocation="noSelect"><xsl:value-of select="."/><xsl:call-template name="topAnchor"/></h3>
</xsl:template>

<xsl:template match="note">
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <small><xsl:apply-templates/></small>
  </span>
</xsl:template>


<!-- ============================================================= -->
<!--  42. BACK MATTER: GLOSSARY                                    -->
<!-- ============================================================= -->

<xsl:template match="glossary">
  <!--
  <xsl:call-template name="nl-1"/>
  <xsl:if test="position()>1">
    <hr class="section-rule"/>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
  <div class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:if test="not(title)">
      <span class="tl-main-part">
        <xsl:call-template name="make-id"/>
        <xsl:text>Glossary</xsl:text>
      </span>
      <xsl:call-template name="nl-1"/>
    </xsl:if>
    <xsl:apply-templates/>
  </div>
  <xsl:call-template name="nl-1"/>
  -->
</xsl:template>

<!--
<xsl:template match="gloss-group">
  <xsl:call-template name="nl-1"/>
  <xsl:if test="not(title)">
    <span class="tl-main-part">Glossary</span>
    <xsl:call-template name="nl-1"/>
  </xsl:if>
  <xsl:apply-templates/>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="def-list">
  <xsl:apply-templates select="title"/>
  <xsl:call-template name="nl-1"/>
  <table width="100%" cellpadding="2" class="bm">
    <xsl:call-template name="nl-1"/>
    <xsl:call-template name="table-setup-l-wide"/>
    <xsl:if test="term-head|def-head">
      <tr>
        <td valign="top" align="right"><i><xsl:apply-templates select="term-head"/></i></td>
        <td valign="top"><i><xsl:apply-templates select="def-head"/></i></td>
      </tr>
      <xsl:call-template name="nl-1"/>
    </xsl:if>
    <xsl:apply-templates select="def-item"/>
  <xsl:call-template name="nl-1"/>
  </table>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="def-item">
  <tr>
    <xsl:call-template name="make-id"/>
    <xsl:call-template name="nl-1"/>
    <xsl:apply-templates/>
  </tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="term">
  <td valign="top" align="right">
    <xsl:call-template name="make-id"/>
    <b><xsl:apply-templates/></b>
  </td>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="def">
  <td valign="top">
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </td>
  <xsl:call-template name="nl-1"/>
</xsl:template>
-->


<!-- ============================================================= -->
<!--  43. TARGET OF A REFERENCE                                    -->
<!-- ============================================================= -->

<xsl:template match="target">
  <a>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>


<!-- ============================================================= -->
<!--  44. XREFS                                                    -->
<!-- ============================================================= -->

<!-- xref for fn, table-fn, or bibr becomes a superior number -->
<xsl:template match="xref[@ref-type='fn']">
  <span class="xref">
    <xsl:call-template name="make-id"/>
    <sup>

      <!-- if immediately-preceding sibling was an xref, punctuate
           (otherwise assume desired punctuation is in the source).-->
      <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
        <span class="gen"><xsl:text>, </xsl:text></span>
      </xsl:if>
      
      <!-- Displays the element content (if any), not the @rid -->
      <a href="#{@rid}"><xsl:apply-templates/></a>
    </sup>
  </span>
</xsl:template>

<xsl:template match="xref[@ref-type='table-fn']">
  <span class="xref">
    <xsl:call-template name="make-id"/>
    <sup>
      <!-- if immediately-preceding sibling was an xref, punctuate
           (otherwise assume desired punctuation is in the source).-->
      <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
        <span class="gen"><xsl:text>, </xsl:text></span>
      </xsl:if>
      
      <!-- Displays the footnote symbols (if any). 
           Removed the hyperlink because table footnotes are not displayed on the web page,
           therefore there is nothing to hyperlink to. -->
     	<xsl:apply-templates/>
    </sup>
  </span>
</xsl:template>

<xsl:template match="xref[@ref-type='bibr']">

  <!-- if immediately-preceding sibling was an xref, punctuate
       (otherwise assume desired punctuation is in the source).-->
  <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
    <xsl:text>,</xsl:text>
  </xsl:if>
  <a href="#{@rid}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="text()[normalize-space(.)='-']">
  <xsl:choose>

    <!-- if a hyphen is the only thing in a text node
         and it's between two xrefs, we conclude that
         it's expressing a range, and we superscript it -->
    <xsl:when test="local-name(following-sibling::node()[1])='xref'
                    and local-name(preceding-sibling::node()[1])='xref'">
      <sup>-</sup>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>-</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- In xref of type fig or of type table,
     the element content is the figure/table number
     and typically part of a sentence,
     so -not- a superior number. -->
<xsl:template match="xref[@ref-type='fig'] | xref[@ref-type='table']">
    <a href="#{@rid}">
      <xsl:apply-templates/>
    </a>
</xsl:template>

<!-- default: if none of the above ref-types -->
<xsl:template match="xref">
  <xsl:call-template name="make-id"/>
  <a href="#{@rid}">
    <xsl:choose>

      <!-- if xref not empty -->
      <xsl:when test="child::node()">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>

        <!-- if empty -->
        <xsl:value-of select="@rid"/>
      </xsl:otherwise>
    </xsl:choose>
  </a>
</xsl:template>


<!-- ============================================================= -->
<!--  45. EXTERNAL LINKS                                           -->
<!-- ============================================================= -->

<!-- xlink:href attribute makes a link -->
<xsl:template match="ext-link | uri">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-href"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span class="capture-id">
        <xsl:call-template name="make-id"/>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="email">
  <a>
  <xsl:attribute name="href">mailto:<xsl:apply-templates/></xsl:attribute>
  <xsl:apply-templates/>
  </a>
</xsl:template>

<!-- xlink:href attribute makes a link -->
<xsl:template match="mailto">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="make-email"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  46. TITLES: MAIN ARTICLE DIVISIONS                           -->
<!-- ============================================================= -->

<!-- main or top-level divisions -->
<xsl:template match="abstract/title
                     | back/title | app-group/title | app/title
                     | glossary/title | def-list/title | ack/title
                     | back/notes/title">
  <xsl:call-template name="nl-1"/>
  <h2 xpathLocation="noSelect">
    <xsl:apply-templates/><xsl:call-template name="topAnchor"/>
  </h2>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="body/sec/title">

  <!-- article main main body heading -->
  <h3 xpathLocation="noSelect">
    <xsl:apply-templates/><xsl:call-template name="topAnchor"/>
  </h3>
</xsl:template>


<!-- ============================================================= -->
<!--  47. TITLES: FIRST-LEVEL DIVISIONS AND DEFAULT                -->
<!-- ============================================================= -->

<!-- first-level divisions and default -->
<xsl:template match="ack/sec/title | app/sec/title | boxed-text/title | gloss-group/title">
  <xsl:call-template name="nl-1"/>
  <span class="tl-lowest-section"><xsl:apply-templates/></span>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="body/sec/sec/title">

  <!-- article second level heading -->
  <xsl:call-template name="nl-1"/>
  <h4>
  <xsl:call-template name="makeXpathLocation" >
  </xsl:call-template>
  <xsl:apply-templates/>
  </h4>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="body/sec/sec/sec/title">

  <!-- article third level heading -->
  <h5>
    <xsl:call-template name="makeXpathLocation" >
    </xsl:call-template>
    <xsl:apply-templates/>

    <!-- This code should probably not output a period, but have kept it here just in case. 
         This extra test was added to stop double periods from appearing. -->
    <xsl:if test="not(ends-with(normalize-space(),'.'))">
      <xsl:text>.</xsl:text>
    </xsl:if>
  </h5>
</xsl:template>

<xsl:template match="abstract/sec/title">
  <xsl:call-template name="nl-1"/>
  <!-- Be careful not to output an abstract's title if it's blank -->
  <xsl:if test="string-length() &gt; 0">
    <h3 xpathLocation="noSelect">
      <xsl:apply-templates/>
    </h3>
  </xsl:if>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<xsl:template match="ref-list[not(ancestor::back)]/title">
  <a>
    <xsl:attribute name="id"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
    <xsl:attribute name="name"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
  </a>
  <h3 xpathLocation="noSelect">
    <xsl:apply-templates/>
  </h3>
</xsl:template>


<xsl:template match="back/ref-list/title">
  <a>
    <xsl:attribute name="id"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
    <xsl:attribute name="name"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
    <xsl:attribute name="toc"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
    <xsl:attribute name="title">
      <xsl:choose>
        <xsl:when test="string-length(.) &gt; 0">
          <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
          References
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </a>
  <h3 xpathLocation="noSelect">
    <xsl:apply-templates/><xsl:call-template name="topAnchor"/>
  </h3>
</xsl:template>

<xsl:template match="caption/title">
  <xsl:apply-templates/>
</xsl:template>

<!-- default: any other titles found -->
<xsl:template match="title">
  <xsl:choose>

    <!-- if there's a title, use it -->
    <xsl:when test="count(ancestor::sec) > 1">
      <xsl:call-template name="nl-1"/>
      <h4>
        <xsl:call-template name="makeXpathLocation" >
        </xsl:call-template>
        <xsl:apply-templates/>
      </h4>
      <xsl:call-template name="nl-1"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="nl-1"/>
      <h3 xpathLocation="noSelect">
        <xsl:apply-templates/>
      </h3>
      <xsl:call-template name="nl-1"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  48. TITLES: MAKE-ABSTRACT-TITLE                              -->
<!-- ============================================================= -->

<!-- called by template for abstract and trans-abstract -->
<xsl:template name="words-for-abstract-title">
  <xsl:variable name="idx" select="count(preceding-sibling::abstract)"/>
  <xsl:variable name="abs_id">abstract<xsl:value-of select="$idx"/></xsl:variable>
  <xsl:choose>

    <!-- if there's a title, use it -->
    <xsl:when test="title">
      <xsl:element name="a">
        <xsl:attribute name="id"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="toc"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="title"/></xsl:attribute>
      </xsl:element>
      <xsl:apply-templates select="title"/>
    </xsl:when>

    <!-- abstract with no title -->
    <xsl:when test="self::abstract">
      <xsl:element name="a">
        <xsl:attribute name="id"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="toc"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="title">Abstract</xsl:attribute>
      </xsl:element>
      <h2 xpathLocation="noSelect"><xsl:text>Abstract</xsl:text><xsl:call-template name="topAnchor"/></h2>
    </xsl:when>

    <!-- trans-abstract with no title -->
    <xsl:when test="self::trans-abstract">
      <xsl:element name="a">
        <xsl:attribute name="id"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="toc"><xsl:value-of select="$abs_id"/></xsl:attribute>
        <xsl:attribute name="title">Abstract, Translated</xsl:attribute>
      </xsl:element>
      <h2 xpathLocation="noSelect"><xsl:text>Abstract, Translated</xsl:text><xsl:call-template name="topAnchor"/></h2>
    </xsl:when>

    <!-- there is no logical otherwise -->
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  49. UNMODED DATA ELEMENTS: MISCELLANEOUS                     -->
<!-- ============================================================= -->

<!-- epage -->
<xsl:template match="epage">
  <span class="gen"><xsl:text>Electronic Page: </xsl:text></span>
  <xsl:apply-templates/>
  <br/>
</xsl:template>

<!-- series -->
<xsl:template match="series">
  <xsl:text> (</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>).</xsl:text>
</xsl:template>

<!-- comment -->
<xsl:template match="comment">
  <xsl:if test="not(self::node()='.')">
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
    <xsl:if test="substring(.,string-length(.)) != '.'">
		<xsl:text>. </xsl:text>
	</xsl:if>
  </xsl:if>
</xsl:template>

<!-- annotation -->
<xsl:template match="annotation">
  <br/>
  <xsl:text> [</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]</xsl:text>
  <br/>
</xsl:template>

<!-- permissions -->
<!--
<xsl:template match="permissions">
  <xsl:choose>
    <xsl:when test="copyright-statement">
      <xsl:apply-templates select="copyright-statement"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="copyright-year">
        <p>
          <span class="gen">
            <xsl:text>Copyright: </xsl:text>
          </span>
          <xsl:apply-templates select="copyright-year"/>
          <xsl:apply-templates select="copyright-holder"/>
         </p>
      </xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
-->

<!-- copyright-statement whether or not part of permissions -->
<!--
<xsl:template match="copyright-statement">
  <p><xsl:apply-templates/></p>
</xsl:template>
-->


<!-- ============================================================= -->
<!--  50. UNMODED DATA ELEMENTS: PARTS OF A DATE                   -->
<!-- ============================================================= -->

<xsl:template match="day">
  <span class="gen">
    <xsl:text>Day: </xsl:text>
  </span>
  <xsl:apply-templates/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="month">
  <span class="gen">
    <xsl:text>Month: </xsl:text>
  </span>
  <xsl:apply-templates/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="season">
  <span class="gen">
    <xsl:text>Season: </xsl:text>
  </span>
  <xsl:apply-templates/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="year">
  <span class="gen">
    <xsl:text>Year: </xsl:text>
  </span>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="stringdate">
  <span class="gen">
    <xsl:text>Stringdate: </xsl:text>
  </span>
  <xsl:apply-templates/>
</xsl:template>


<!-- ============================================================= -->
<!--  51. UNMODED DATA ELEMENTS: PARTS OF A NAME                   -->
<!-- ============================================================= -->


<xsl:template match="name">
  <xsl:variable name="nodetotal" select="count(../*)"/>
  <xsl:variable name="position" select="position()"/>
  <xsl:choose>
    <xsl:when test="given-names">
      <xsl:apply-templates select="surname"/>
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="given-names"/>
      <xsl:if test="suffix">
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="suffix"/>
      </xsl:if>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-templates select="surname"/>
      </xsl:otherwise>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="following-sibling::aff"/>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$nodetotal=$position">
          <xsl:choose>
            <xsl:when test="parent::person-group/@person-group-type">
              <xsl:choose>
                <xsl:when test="parent::person-group/@person-group-type='author'">
                  <xsl:text></xsl:text>
                </xsl:when>
                <xsl:otherwise/>
              </xsl:choose>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>, </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="aff" mode="editor">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="aff">
  <xsl:variable name="nodetotal" select="count(../*)"/>
  <xsl:variable name="position" select="position()"/>
  <span class="capture-id">
    <xsl:call-template name="make-id"/>
    <xsl:text> (</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>)</xsl:text>
  </span>
  <xsl:choose>
    <xsl:when test="$nodetotal=$position">. </xsl:when>
    <xsl:otherwise>, </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="etal">
  <xsl:text>et al.</xsl:text>
  <xsl:choose>
    <xsl:when test="parent::person-group/@person-group-type">
      <xsl:choose>
        <xsl:when test="parent::person-group/@person-group-type='author'">
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  CITATION AND NLM-CITATION                                    -->
<!-- ============================================================= -->

<!-- NLM Archiving DTD:
       - citation uses mode nscitation.

     NLM Publishing DTD:
       - nlm-citation uses several modes,
         including book, edited-book, conf, and "none".
-->


<!-- ============================================================= -->
<!--  52. BACK MATTER: REF-LIST                                    -->
<!-- ============================================================= -->

<xsl:template match="ref-list">
<div xpathLocation="noSelect">
  <xsl:choose>
    <xsl:when test="not(title)">
      <a id="refs" name="refs" toc="refs" title="References"/>
      <h3 xpathLocation="noSelect">References</h3>
      <xsl:call-template name="nl-1"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="title"/>
    </xsl:otherwise>
  </xsl:choose>
  <ol class="references" xpathLocation="noSelect">
    <xsl:for-each select="ref">
    <xsl:sort data-type="number" select="label"/>
      <li xpathLocation="noSelect">
        <a>
          <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
        </a>
        <xsl:variable name="cit" select="citation | nlm-citation"/>
        <xsl:apply-templates select="$cit"/>
        <xsl:text> </xsl:text>
        <xsl:if test="$cit[@citation-type='journal']">
          <xsl:variable name="apos">'</xsl:variable>
          <xsl:variable name="artTitle"><xsl:value-of select="$cit/article-title"/></xsl:variable>
          <xsl:variable name="author">
            <xsl:value-of
              select="$cit/person-group[@person-group-type='author'][1]/name[1]/surname"/>
          </xsl:variable>
          <xsl:variable name="findURL">
            <xsl:value-of select="concat($pubAppContext,'/article/findArticle.action?author=',
                                         $author, '&amp;title=', $artTitle)"/>
          </xsl:variable>

          <xsl:if test="not(nlm-citation//ext-link | citation//ext-link)">
            <xsl:element name="a">
              <xsl:attribute name="class">find</xsl:attribute>
              <xsl:attribute name="href"><xsl:value-of select="$findURL"/></xsl:attribute>
              Find this article online
            </xsl:element>
          </xsl:if>
        </xsl:if>
      </li>
    </xsl:for-each>
  </ol>
  </div>
</xsl:template>

<!-- suppress the ref-list title so it doesn't reappear -->
<xsl:template match="ref-list/title" mode="nscitation"/>


<!-- ============================================================= -->
<!--  53. REF                                                      -->
<!-- ============================================================= -->

<!-- each ref is a table row -->
<xsl:template match="ref">
  <tr>
    <xsl:call-template name="nl-1"/>
    <td id="{@id}" valign="top" align="right">
      <xsl:if test="not(label)">
        <xsl:value-of select="@id"/>
      </xsl:if>
      <xsl:apply-templates select="label"/>
    </td>
    <xsl:call-template name="nl-1"/>
    <td valign="top">
      <xsl:apply-templates select="citation|nlm-citation"/>
    </td>
    <xsl:call-template name="nl-1"/>
  </tr>
  <xsl:call-template name="nl-1"/>
</xsl:template>

<!-- becomes content of table cell, column 1-->
<xsl:template match="ref/label">
    <b><i><xsl:apply-templates/><xsl:text>. </xsl:text></i></b>
</xsl:template>


<!-- ============================================================= -->
<!--  54. CITATION (for NLM Archiving DTD)                         -->
<!-- ============================================================= -->

<!-- The citation model is mixed-context, so it is processed
     with an apply-templates (as for a paragraph)
       -except-
     if there is no PCDATA (only elements), spacing and punctuation
     also must be supplied = mode nscitation. -->

<!--<xsl:template match="ref/citation">-->

<!--    <xsl:choose>-->
      <!-- if has no significant text content, presume that
           punctuation is not supplied in the source XML
           = transform will supply it. -->
<!--      <xsl:when test="not(text()[normalize-space()])">
        <xsl:apply-templates mode="none"/>
      </xsl:when>-->

      <!-- if have only element content, presume that
           punctuation not supplied = generate it. -->
<!--      <xsl:otherwise>-->
<!--        <xsl:apply-templates mode="nscitation"/>-->
<!--      </xsl:otherwise>
    </xsl:choose>-->

<!--</xsl:template>-->


<!-- ============================================================= -->
<!--  55. NLM-CITATION (for NLM Publishing DTD)                    -->
<!-- ============================================================= -->

<!-- The nlm-citation model allows only element content, so
     it takes a pull template and adds punctuation. -->

<!-- Processing of nlm-citation uses several modes, including
     citation, book, edited-book, conf, inconf, and mode "none".   -->

<!-- Each citation-type is handled in its own template. -->


<!-- Book or thesis -->
<xsl:template match="ref/citation[@citation-type='book']
                   | ref/citation[@citation-type='thesis']
                   | ref/citation[@citation-type='other']
                   | ref/nlm-citation[@citation-type='book']
                   | ref/nlm-citation[@citation-type='thesis']
                   | ref/nlm-citation[@citation-type='other']">   
  <xsl:variable name="augroupcount" select="count(person-group) + count(collab)"/>
  <xsl:choose>
    <xsl:when test="$augroupcount>1 and
                    person-group[@person-group-type!='author'] and
                    article-title ">
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='author']" mode="book"/>
        <xsl:apply-templates select="collab" mode="book"/>
        <xsl:apply-templates select="etal"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
      <xsl:apply-templates select="article-title" mode="editedbook"/>
      <xsl:text> In:</xsl:text>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                 | person-group[@person-group-type='allauthors']
                                 | person-group[@person-group-type='translator']
                                 | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
    </xsl:when>
    <xsl:when test="person-group[not(@person-group-type)]">
      <span class="authors">
        <xsl:apply-templates select="person-group"/>
        <xsl:apply-templates select="collab"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date" mode="book"/>
      <xsl:apply-templates select="article-title" mode="book"/>
      <xsl:apply-templates select="source" mode="book"/>
      <xsl:apply-templates select="edition" mode="book"/>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                   | person-group[@person-group-type='translator']
                                   | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="volume" mode="book"/>
      <xsl:apply-templates select="issue" mode="none"/>
      <xsl:apply-templates select="trans-source" mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc" mode="none"/>
      <xsl:apply-templates select="fpage | lpage" mode="book"/>
      <xsl:apply-templates select="page-count" mode="book"/>
    </xsl:when>
    <xsl:when test="person-group[@person-group-type='author'] or
                    person-group[@person-group-type='compiler']">
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='author']
                                 | person-group[@person-group-type='compiler']"
                            mode="book"/>
        <xsl:apply-templates select="collab"
                           mode="book"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
       <xsl:apply-templates select="article-title" mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                 | person-group[@person-group-type='translator']
                                 | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="issue" mode="none"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
      <xsl:apply-templates select="page-count" mode="book"/>
    </xsl:when>
    <xsl:otherwise>
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='editor']
                                 | person-group[@person-group-type='translator']
                                 | person-group[@person-group-type='transed']
                                 | person-group[@person-group-type='guest-editor']"
                           mode="book"/>
        <xsl:apply-templates select="collab"
                           mode="book"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
     <xsl:apply-templates select="article-title" mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="issue" mode="none"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
      <xsl:apply-templates select="page-count" mode="book"/>
    </xsl:otherwise>
  </xsl:choose>
 <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!-- Conference proceedings -->
<xsl:template match="ref/citation[@citation-type='confproc']
                   | ref/nlm-citation[@citation-type='confproc']">
  <xsl:variable name="augroupcount" select="count(person-group) + count(collab)"/>
  <xsl:choose>
    <xsl:when test="$augroupcount>1 and person-group[@person-group-type!='author']">
      <xsl:apply-templates select="person-group[@person-group-type='author']"
                           mode="book"/>
      <xsl:apply-templates select="collab"/>
      <xsl:apply-templates select="article-title"
                           mode="inconf"/>
      <xsl:text>In: </xsl:text>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                 | person-group[@person-group-type='allauthors']
                                 | person-group[@person-group-type='translator']
                                 | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="source"
                           mode="conf"/>
      <xsl:apply-templates select="conf-name | conf-date | conf-loc"
                           mode="conf"/>
      <xsl:apply-templates select="publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="publisher-name"
                           mode="none"/>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="person-group"
                           mode="book"/>
      <xsl:apply-templates select="collab"
                           mode="book"/>
      <xsl:apply-templates select="article-title"
                           mode="conf"/>
      <xsl:apply-templates select="source" mode="conf"/>
      <xsl:apply-templates select="conf-name | conf-date | conf-loc"
                           mode="conf"/>
      <xsl:apply-templates select="publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="publisher-name"
                           mode="none"/>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
      <xsl:apply-templates select="page-count" mode="book"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!-- Government and other reports, other, web, and commun -->
<xsl:template match="ref/citation[@citation-type='gov']
                   | ref/citation[@citation-type='web']
                   | ref/citation[@citation-type='commun']
                   | ref/nlm-citation[@citation-type='gov']
                   | ref/nlm-citation[@citation-type='web']
                   | ref/nlm-citation[@citation-type='commun']">
  <xsl:apply-templates select="person-group" mode="book"/>
  <xsl:apply-templates select="collab"/>
  <xsl:apply-templates select="year | month | time-stamp | season | access-date" mode="book"/>
  <xsl:choose>
    <xsl:when test="publisher-loc | publisher-name">
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:choose>
        <xsl:when test="@citation-type='web'">
          <xsl:apply-templates select="edition"
                               mode="none"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="edition"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="publisher-name"
                           mode="none"/>
      <xsl:apply-templates select="article-title|gov"
                           mode="none"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"/>
      <xsl:apply-templates select="publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="publisher-name"
                           mode="none"/>
      <xsl:apply-templates select="article-title|gov" mode="book"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="fpage | lpage"
                       mode="book"/>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!-- Patents  -->
<xsl:template match="ref/citation[@citation-type='patent']
                   | ref/nlm-citation[@citation-type='patent']">
  <xsl:apply-templates select="person-group"
                       mode="book"/>
  <xsl:apply-templates select="collab"
                       mode="book"/>
  <xsl:apply-templates select="article-title | trans-title"
                       mode="none"/>
  <xsl:apply-templates select="source"
                       mode="none"/>
  <xsl:apply-templates select="patent"
                       mode="none"/>
  <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                       mode="book"/>
  <xsl:apply-templates select="fpage | lpage"
                       mode="book"/>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!-- Discussion  -->
<xsl:template match="ref/citation[@citation-type='discussion']
                   | ref/nlm-citation[@citation-type='discussion']">
  <xsl:apply-templates select="person-group"
                       mode="book"/>
  <xsl:apply-templates select="collab"/>
  <xsl:apply-templates select="article-title"
                       mode="editedbook"/>
  <xsl:text>In: </xsl:text>
  <xsl:apply-templates select="source"
                       mode="none"/>
  <xsl:if test="publisher-name | publisher-loc">
    <xsl:text> [</xsl:text>
    <xsl:apply-templates select="publisher-loc"
                         mode="none"/>
    <xsl:value-of select="publisher-name"/>
    <xsl:text>]; </xsl:text>
  </xsl:if>
  <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                       mode="book"/>
  <xsl:apply-templates select="fpage | lpage"
                       mode="book"/>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!--
<xsl:template match="ref/citation[@citation-type='journal']">
  <xsl:variable name="augroupcount" select="count(person-group) + count(collab)"/>
  <xsl:choose>
    <xsl:when test="$augroupcount>1 and
                    person-group[@person-group-type!='author'] and
                    article-title ">
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='author']" mode="book"/>
        <xsl:apply-templates select="collab" mode="book"/>
        <xsl:apply-templates select="etal"/>
      </span>
      <xsl:apply-templates select="article-title" mode="editedbook"/>
      <xsl:text>In: </xsl:text>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                   | person-group[@person-group-type='allauthors']
                                   | person-group[@person-group-type='translator']
                                   | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                         mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>
      <xsl:apply-templates select="fpage | lpage"
                           mode="book"/>
    </xsl:when>
    <xsl:when test="person-group[@person-group-type='author'] or
                    person-group[@person-group-type='compiler']">
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='author']
                                 | person-group[@person-group-type='compiler']"
                           mode="book"/>
        <xsl:apply-templates select="collab"
                           mode="book"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="person-group[@person-group-type='editor']
                                   | person-group[@person-group-type='translator']
                                   | person-group[@person-group-type='transed'] "
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>

      <xsl:apply-templates select="article-title | fpage | lpage"
                           mode="book"/>
    </xsl:when>
    <xsl:otherwise>
      <span class="authors">
        <xsl:apply-templates select="person-group[@person-group-type='editor']
                                 | person-group[@person-group-type='translator']
                                 | person-group[@person-group-type='transed']
                                 | person-group[@person-group-type='guest-editor']"
                           mode="book"/>
        <xsl:apply-templates select="collab"
                           mode="book"/>
      </span>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
                           mode="book"/>
      <xsl:apply-templates select="source"
                           mode="book"/>
      <xsl:apply-templates select="edition"
                           mode="book"/>
      <xsl:apply-templates select="volume"
                           mode="book"/>
      <xsl:apply-templates select="trans-source"
                           mode="book"/>
      <xsl:apply-templates select="publisher-name | publisher-loc"
                           mode="none"/>

      <xsl:apply-templates select="article-title | fpage | lpage"
                           mode="book"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>
-->

<!-- If none of the above citation-types applies,
     use mode="none". This generates punctuation. -->
<!-- (e.g., citation-type="journal"              -->
<xsl:template match="citation">
  <span class="authors">
    <xsl:apply-templates select="person-group"/>
    <xsl:apply-templates select="collab"/>
  </span>
  <xsl:apply-templates select="*[not(self::annotation) 
                               and not(self::edition) and not(self::person-group) 
                               and not(self::collab) and not(self::comment)]|text()"
                       mode="none"/>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>

<!-- Modified the above citation template to work with legacy nlm-citations for journal articles.
     However, since the ordering of these nlm-citations child elements does not correspond to the
     output ordering in the citation, I needed to explitly write out the ordering below. -->
<xsl:template match="nlm-citation">
  <span class="authors">
    <xsl:apply-templates select="person-group"/>
    <xsl:apply-templates select="collab"/>
  </span>
 <xsl:apply-templates select="year" mode="none"/>
 <xsl:apply-templates select="article-title" mode="none"/>
 <xsl:apply-templates select="*[not(self::annotation) 
                              and not(self::edition) and not(self::person-group) 
                              and not(self::collab) and not(self::comment) and not(self::year) 
                              and not (self::article-title)]|text()"
          	  	       mode="none"/>
  <xsl:call-template name="citation-tag-ends"/>
</xsl:template>


<!-- ============================================================= -->
<!-- person-group, mode=book                                       -->
<!-- ============================================================= -->

<xsl:template match="person-group" mode="book">

  <!-- XX needs fix, value is not a nodeset on the when -->
  <!--
  <xsl:choose>
    <xsl:when test="@person-group-type='editor'
                  | @person-group-type='assignee'
                  | @person-group-type='translator'
                  | @person-group-type='transed'
                  | @person-group-type='guest-editor'
                  | @person-group-type='compiler'
                  | @person-group-type='inventor'
                  | @person-group-type='allauthors'">
      <xsl:call-template name="make-persons-in-mode"/>
      <xsl:call-template name="choose-person-type-string"/>
      <xsl:call-template name="choose-person-group-end-punct"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates mode="book"/>
    </xsl:otherwise>
  </xsl:choose>
-->

  <xsl:call-template name="make-persons-in-mode"/>
</xsl:template>

<xsl:template match="person-group[@person-group-type='editor']" mode="book">
  <xsl:text> </xsl:text>
  <xsl:apply-templates />
  <xsl:choose>
    <xsl:when test="count (name) > 1">
      <xsl:text>, editors. </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>, editor. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="person-group[@person-group-type='translator'] | person-group[@group-type='transed']" mode="book">
  <xsl:apply-templates />
  <xsl:text>, translator; </xsl:text>
</xsl:template>

<!-- if given names aren't all-caps, use book mode -->
<xsl:template name="make-persons-in-mode">
    <xsl:apply-templates mode="book"/>
</xsl:template>


<xsl:template name="choose-person-type-string">
  <xsl:variable name="person-group-type">
    <xsl:value-of select="@person-group-type"/>
  </xsl:variable>
  <xsl:choose>

    <!-- allauthors is an exception to the usual choice pattern -->
    <xsl:when test="$person-group-type='allauthors'"/>

    <!-- the usual choice pattern: singular or plural? -->
    <xsl:when test="count(name) > 1 or etal ">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="($person-strings[@source=$person-group-type]/@plural)"/>
    </xsl:when>
    </xsl:choose>
</xsl:template>


<xsl:template name="choose-person-group-end-punct">

  <xsl:choose>
    <!-- compiler is an exception to the usual choice pattern -->
    <xsl:when test="@person-group-type='compiler'">
      <xsl:text>. </xsl:text>
    </xsl:when>

    <!-- the usual choice pattern: semi-colon or period? -->
    <xsl:when test="following-sibling::person-group">
      <xsl:text>; </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ============================================================= -->
<!--  56. Citation subparts (mode "none" separately at end)        -->
<!-- ============================================================= -->

<!-- names -->
<xsl:template match="name" mode="nscitation">
  <xsl:value-of select="surname"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="given-names"/>
  <xsl:text>, </xsl:text>
</xsl:template>


<xsl:template match="name" mode="book">
  <xsl:variable name="nodetotal" select="count(../*)"/>
  <xsl:variable name="penult" select="count(../*)-1"/>
  <xsl:variable name="position" select="position()"/>
    <xsl:choose>

      <!-- if given-names -->
      <xsl:when test="given-names">
        <xsl:apply-templates select="surname"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="firstnames" >
          <xsl:with-param name="nodetotal" select="$nodetotal"/>
          <xsl:with-param name="position" select="$position"/>
          <xsl:with-param name="names" select="given-names"/>
          <xsl:with-param name="pgtype">
          <xsl:choose>
            <xsl:when test="parent::person-group[@person-group-type]">
              <xsl:value-of select="parent::person-group/@person-group-type"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'author'"/>
            </xsl:otherwise>
          </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:if test="suffix">
          <xsl:text>, </xsl:text>
          <xsl:apply-templates select="suffix"/>
        </xsl:if>
      </xsl:when>

      <!-- if no given-names -->
      <xsl:otherwise>
        <xsl:apply-templates select="surname"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>

      <!-- if have aff -->
      <xsl:when test="following-sibling::aff"/>

      <!-- if don't have aff -->
      <xsl:otherwise>
        <xsl:choose>

          <!-- if part of person-group -->
          <xsl:when test="parent::person-group/@person-group-type">
            <xsl:choose>

              <!-- if author -->
              <xsl:when test="parent::person-group/@person-group-type='author'">
                <xsl:choose>
                  <xsl:when test="$nodetotal=$position"> </xsl:when>
                  <xsl:when test="$penult=$position">
                    <xsl:choose>
                      <xsl:when test="following-sibling::etal">, </xsl:when>
                      <xsl:otherwise>, </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>, </xsl:otherwise>
                </xsl:choose>
              </xsl:when>

              <!-- if not author -->
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="$nodetotal=$position"/>
                  <xsl:when test="$penult=$position">
                    <xsl:choose>
                      <xsl:when test="following-sibling::etal">, </xsl:when>
                      <xsl:otherwise>; </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>; </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>

          <!-- if not part of person-group -->
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$nodetotal=$position">. </xsl:when>
                <xsl:when test="$penult=$position">
                  <xsl:choose>
                    <xsl:when test="following-sibling::etal">, </xsl:when>
                    <xsl:otherwise>; </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:otherwise>; </xsl:otherwise>
               </xsl:choose>
             </xsl:otherwise>
         </xsl:choose>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template match="collab" mode="book">
  <xsl:apply-templates/>
    <xsl:if test="@collab-type='compilers'">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="@collab-type"/>
    </xsl:if>
    <xsl:if test="@collab-type='assignee'">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="@collab-type"/>
    </xsl:if>
</xsl:template>

<xsl:template match="etal" mode="book">
  <xsl:text>et al.</xsl:text>
  <xsl:choose>
    <xsl:when test="parent::person-group/@person-group-type">
      <xsl:choose>
        <xsl:when test="parent::person-group/@person-group-type='author'">
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- affiliations -->
<xsl:template match="aff" mode="book">
  <xsl:variable name="nodetotal" select="count(../*)"/>
  <xsl:variable name="position" select="position()"/>
  <xsl:text> (</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>)</xsl:text>
  <xsl:choose>
    <xsl:when test="$nodetotal=$position">. </xsl:when>
    <xsl:otherwise>, </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- publication info -->
<xsl:template match="article-title" mode="nscitation">
  <xsl:apply-templates/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="article-title" mode="book">
  <xsl:apply-templates/>
  <xsl:if test="not(ends-with(normalize-space(),'.'))">
    <xsl:text>.</xsl:text>
  </xsl:if>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="article-title" mode="editedbook">
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="article-title" mode="conf">
  <xsl:apply-templates/>
  <xsl:choose>
    <xsl:when test="../conf-name">
      <xsl:text>. </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>; </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="article-title" mode="inconf">
  <xsl:apply-templates/>
  <xsl:variable name="punc">
    <xsl:call-template name="endsWithPunctuation">
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
  </xsl:variable>

  <!-- Fixed bug where the word false wasn't quoted -->
  <xsl:if test="$punc = 'false'">
    <xsl:text>.</xsl:text>
  </xsl:if>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="publisher-loc">
  <xsl:apply-templates/>
  <xsl:text>: </xsl:text>
</xsl:template>

<xsl:template match="publisher-name">
  <xsl:apply-templates/>
  <xsl:text>.</xsl:text>
</xsl:template>

<xsl:template match="source" mode="nscitation">
  <xsl:apply-templates/>
  <xsl:variable name="punc">
    <xsl:call-template name="endsWithPunctuation">
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
  </xsl:variable>

  <!-- Fixed bug where the word false wasn't quoted -->
  <xsl:if test="$punc = 'false'">
    <xsl:text>.</xsl:text>
  </xsl:if>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="source" mode="book">
  <xsl:choose>
    <xsl:when test="../trans-source">
      <xsl:apply-templates/>
      <xsl:choose>
        <xsl:when test="../volume | ../edition">
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text> </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
      <xsl:variable name="punc">
        <xsl:call-template name="endsWithPunctuation">
          <xsl:with-param name="value" select="."/>
        </xsl:call-template>
      </xsl:variable>

      <!-- Fixed bug where the word false wasn't quoted -->
      <xsl:if test="$punc='false'">
        <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="source" mode="conf">
  <xsl:apply-templates/>
  <xsl:text>; </xsl:text>
</xsl:template>

<xsl:template match="trans-source" mode="book">
  <xsl:text> [</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]. </xsl:text>
</xsl:template>

<xsl:template match="volume" mode="nscitation">
  <xsl:text> </xsl:text><xsl:apply-templates/><xsl:text>: </xsl:text>
</xsl:template>

<xsl:template match="volume | edition" mode="book">
  <xsl:apply-templates/>
  <xsl:if test="@collab-type='compilers'">
    <xsl:text>, </xsl:text>
    <xsl:value-of select="@collab-type"/>
  </xsl:if>
  <xsl:if test="@collab-type='assignee'">
    <xsl:text>, </xsl:text>
    <xsl:value-of select="@collab-type"/>
  </xsl:if>
</xsl:template>

<!-- dates -->
<xsl:template match="month" mode="nscitation">
  <xsl:apply-templates/><xsl:text>.</xsl:text>
</xsl:template>

<xsl:template match="month" mode="book">
  <xsl:variable name="month" select="."/>
  <xsl:choose>
    <xsl:when test="$month='01' or $month='1' or $month='January'">Jan</xsl:when>
    <xsl:when test="$month='02' or $month='2' or $month='February'">Feb</xsl:when>
    <xsl:when test="$month='03' or $month='3' or $month='March'">Mar</xsl:when>
    <xsl:when test="$month='04' or $month='4' or $month='April'">Apr</xsl:when>
    <xsl:when test="$month='05' or $month='5' or $month='May'">May</xsl:when>
    <xsl:when test="$month='06' or $month='6' or $month='June'">Jun</xsl:when>
    <xsl:when test="$month='07' or $month='7' or $month='July'">Jul</xsl:when>
    <xsl:when test="$month='08' or $month='8' or $month='August'">Aug</xsl:when>
    <xsl:when test="$month='09' or $month='9' or $month='September'">Sep</xsl:when>
    <xsl:when test="$month='10' or $month='October'">Oct</xsl:when>
    <xsl:when test="$month='11' or $month='November'">Nov</xsl:when>
    <xsl:when test="$month='12' or $month='December'">Dec</xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$month"/>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:if test="../day">
    <xsl:text> </xsl:text>
    <xsl:value-of select="../day"/>
  </xsl:if>

  <xsl:choose>
    <xsl:when test="../time-stamp">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="../time-stamp"/>
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:when test="../access-date"/>
    <xsl:otherwise>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="day" mode="nscitation">
  <xsl:apply-templates/><xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="year" mode="nscitation">
  <xsl:text> (</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="year" mode="book">
  <xsl:choose>
    <xsl:when test="../month or ../season or ../access-date">
      <xsl:apply-templates/>
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> (</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>) </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="time-stamp" mode="nscitation">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="time-stamp" mode="book"/>

<xsl:template match="access-date" mode="nscitation">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="access-date" mode="book">
  <xsl:text> [</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]. </xsl:text>
</xsl:template>

<xsl:template match="season" mode="book">
  <xsl:apply-templates/>
    <xsl:if test="@collab-type='compilers'">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="@collab-type"/>
    </xsl:if>
    <xsl:if test="@collab-type='assignee'">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="@collab-type"/>
    </xsl:if>
  <xsl:text>. </xsl:text>
</xsl:template>

<!-- pages -->
<xsl:template match="fpage" mode="nscitation">
  <xsl:apply-templates/>
  <xsl:if test="../lpage">
    <xsl:text>-</xsl:text>
    <xsl:value-of select="../lpage"/>
  </xsl:if>
  <xsl:if test="../@citation-type=book">
    <xsl:text> p</xsl:text>
  </xsl:if>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="page-count" mode="book">
	<xsl:value-of select="@count"/><xsl:text> p.</xsl:text>
</xsl:template>

<xsl:template match="fpage" mode="book">
  <xsl:if test="../lpage">

    <!-- Fix old journal articles that were coded as type other, but actually
         had a volume, source and page numbers. While in reality the XML should have been 
         changed to make these citations type='journal', there are too many such instances 
         and so we placed this hack in the xsl. 
         Hopefully, it does not break any other cases. -->
    <xsl:choose>
      <xsl:when test="name(preceding-sibling::node()[1])='volume'">
        <xsl:text>: </xsl:text>
      </xsl:when>
      <xsl:otherwise>  
        <xsl:text>pp. </xsl:text>
	    </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
    <xsl:text>&#8211;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="lpage" mode="book">
    <xsl:if test="../fpage">
      <xsl:apply-templates/>
      <xsl:text>.</xsl:text>
    </xsl:if>
</xsl:template>

<xsl:template match="lpage" mode="nscitation"/>

<!-- misc stuff -->
<xsl:template match="pub-id[@pub-id-type='pmid']" mode="nscitation">
  <xsl:variable name="pmid" select="."/>
  <xsl:variable name="href" 
                select="'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;dopt=abstract&amp;list_uids='"/>
  <xsl:text> [</xsl:text>
  <a>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($href,$pmid)"/>
    </xsl:attribute>
    <xsl:attribute name="target">
      <xsl:text>_new</xsl:text>
    </xsl:attribute>PubMed
  </a>
  <xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="annotation" mode="nscitation">
  <blockquote><xsl:apply-templates/></blockquote>
</xsl:template>

<xsl:template match="comment" mode="nscitation">
  <xsl:if test="not(self::node()='.')">
    <br/>
    <small><xsl:apply-templates/></small>
  </xsl:if>
</xsl:template>

<xsl:template match="conf-name | conf-date" mode="conf">
  <xsl:apply-templates/>
  <xsl:text>; </xsl:text>
</xsl:template>

<xsl:template match="conf-loc" mode="conf">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>


<!-- ============================================================= -->
<!--  "firstnames"                                                 -->
<!-- ============================================================= -->

<!-- called by match="name" in book mode,
     as part of citation handling
     when given-names is not all-caps -->
<xsl:template name="firstnames" >
  <xsl:param name="nodetotal"/>
  <xsl:param name="position"/>
  <xsl:param name="names"/>
  <xsl:param name="pgtype"/>
  <xsl:if test="$names">
    <xsl:apply-templates select="$names"/>
  </xsl:if>
</xsl:template>



<!-- ============================================================= -->
<!-- mode=none                                                     -->
<!-- ============================================================= -->

<!-- This mode assumes no punctuation is provided in the XML.
     It is used, among other things, for the citation/ref
     when there is no significant text node inside the ref.        -->

<xsl:template match="name" mode="none">
  <xsl:value-of select="surname"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="given-names"/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="article-title" mode="none">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="volume" mode="none">
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
  <!-- Bug fix. If there is an issue number, then don't print the colon and
       space following the volume # -->
  <xsl:if test="not(../issue)">
    <xsl:text>: </xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="edition" mode="none">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="supplement" mode="none">
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="issue" mode="none">
  <xsl:if test="not(starts-with(normalize-space(),'('))">
    <xsl:text>(</xsl:text>
  </xsl:if>
  <xsl:apply-templates/>
  <xsl:if test="not(ends-with(normalize-space(),')'))">
    <xsl:text>)</xsl:text>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="../fpage or ../lpage">
      <xsl:text>: </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>.</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="publisher-loc" mode="none">
  <xsl:apply-templates/>
  <xsl:text>: </xsl:text>
</xsl:template>

<xsl:template match="publisher-name" mode="none">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="person-group" mode="none">
  <xsl:apply-templates select="node()" mode="book"/>
</xsl:template>

<xsl:template match="collab" mode="none">
  <xsl:apply-templates/>
  <xsl:if test="@collab-type">
    <xsl:text>, </xsl:text>
    <xsl:value-of select="@collab-type"/>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="following-sibling::collab">
      <xsl:text>; </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="source" mode="none">
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
  <xsl:choose>
    <xsl:when test="../access-date">
      <xsl:if test="../edition">
        <xsl:text> (</xsl:text>
        <xsl:apply-templates select="../edition" mode="plain"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text>. </xsl:text>
    </xsl:when>
    <xsl:when test="../volume | ../fpage">
      <xsl:if test="../edition">
        <xsl:text> (</xsl:text><xsl:apply-templates select="../edition" mode="plain"/><xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="../edition">
        <xsl:text> (</xsl:text>
        <xsl:apply-templates select="../edition" mode="plain"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="trans-title" mode="none">
  <xsl:text> [</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]. </xsl:text>
</xsl:template>

<xsl:template match="month" mode="none">
 <xsl:variable name="month" select="."/>
  <xsl:choose>
    <xsl:when test="$month='01' or $month='1' ">January</xsl:when>
    <xsl:when test="$month='02' or $month='2' ">February</xsl:when>
    <xsl:when test="$month='03' or $month='3' ">March</xsl:when>
    <xsl:when test="$month='04' or $month='4' ">April</xsl:when>
    <xsl:when test="$month='05' or $month='5' ">May</xsl:when>
    <xsl:when test="$month='06' or $month='6'">June</xsl:when>
    <xsl:when test="$month='07' or $month='7'">July</xsl:when>
    <xsl:when test="$month='08' or $month='8' ">August</xsl:when>
    <xsl:when test="$month='09' or $month='9' ">September</xsl:when>
    <xsl:when test="$month='10' ">October</xsl:when>
    <xsl:when test="$month='11' ">November</xsl:when>
    <xsl:when test="$month='12' ">December</xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$month"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="day" mode="none"/>

<xsl:template match="year" mode="none">
  <xsl:choose>
    <xsl:when test="../month or ../season or ../access-date">
      <xsl:apply-templates mode="none"/>
      <xsl:text> </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> (</xsl:text>
      <xsl:apply-templates mode="none"/>
      <xsl:text>) </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="access-date" mode="none">
  <xsl:text> [</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>];</xsl:text>
</xsl:template>

<xsl:template match="season" mode="none">
  <xsl:apply-templates/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="comment" mode="citation">
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<!-- BUG in the following fpage template for the def of hermano -->
<xsl:template match="fpage" mode="none">
  <xsl:variable name="fpgct" select="count(../fpage)"/>
  <xsl:variable name="lpgct" select="count(../lpage)"/>
  <xsl:variable name="hermano" select="name(following-sibling::node()[1])"/>
  <xsl:choose>
    <xsl:when test="preceding-sibling::fpage">
      <xsl:choose>
        <xsl:when test="following-sibling::fpage">
          <xsl:text> </xsl:text>
          <xsl:apply-templates/>
          <xsl:if test="$hermano='lpage'">
            <xsl:text>&#8211;</xsl:text>
            <xsl:apply-templates select="following-sibling::lpage[1]" mode="none"/>
          </xsl:if>
          <xsl:text>,</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text> </xsl:text>
          <xsl:apply-templates/>
          <xsl:if test="$hermano='lpage'">
            <xsl:text>&#8211;</xsl:text>
            <xsl:apply-templates select="following-sibling::lpage[1]" mode="none"/>
          </xsl:if>
          <xsl:text>.</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
      <xsl:choose>
        <xsl:when test="$hermano='lpage'">
          <xsl:text>&#8211;</xsl:text>
        </xsl:when>
       <xsl:when test="$hermano='fpage'">
          <xsl:text>,</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>.</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="lpage" mode="none">
  <xsl:apply-templates/><xsl:text>.</xsl:text>
</xsl:template>

<xsl:template match="gov" mode="none">
  <xsl:choose>
    <xsl:when test="../trans-title">
      <xsl:apply-templates/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
      <xsl:text>. </xsl:text>
    </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template match="patent" mode="none">
  <xsl:apply-templates/>
  <xsl:text>. </xsl:text>
</xsl:template>

<xsl:template match="aff/label">
  <strong><xsl:apply-templates/></strong>
</xsl:template>

<!-- ============================================================= -->
<!--  57. "CITATION-TAG-ENDS"                                      -->
<!-- ============================================================= -->

<xsl:template name="citation-tag-ends">
  <xsl:apply-templates select="series" mode="citation"/>

  <!-- If language is not English -->
  <!-- XX review logic -->
  <xsl:if test="article-title[@xml:lang!='en']
                or article-title[@xml:lang!='EN']">
    <xsl:call-template name="language">
      <xsl:with-param name="lang" select="article-title/@xml:lang"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:if test="source[@xml:lang!='en']
              or source[@xml:lang!='EN']">
    <xsl:call-template name="language">
      <xsl:with-param name="lang" select="source/@xml:lang"/>
    </xsl:call-template>
  </xsl:if>

  <!-- Hack to fix bad XML in which incorrect <comment> tags are placed within a citation.
       We should only output a single comment tag that appears as the very last child of the 
       citation. -->
	<xsl:variable name="x" select="child::*[position()=last()]"/>
	<xsl:if test="local-name($x)='comment' and not(starts-with($x,'p.')) and not(starts-with($x,'In:') and not(starts-with($x,'pp.')))">
		<xsl:text> </xsl:text><xsl:apply-templates select="$x"/>
	</xsl:if>	
  <xsl:apply-templates select="annotation" mode="citation"/>
</xsl:template>

<xsl:template name="createAnnotationSpan">
  <xsl:variable name="regionId" select="@aml:id"/>
  <xsl:variable name="regionNumComments" select="number(/article/aml:regions/aml:region[@aml:id=$regionId]/@aml:numComments)"/>
  <xsl:variable name="regionNumMinorCorrections" select="number(/article/aml:regions/aml:region[@aml:id=$regionId]/@aml:numMinorCorrections)"/>
  <xsl:variable name="regionNumFormalCorrections" select="number(/article/aml:regions/aml:region[@aml:id=$regionId]/@aml:numFormalCorrections)"/>
  <xsl:variable name="regionNumRetractions" select="number(/article/aml:regions/aml:region[@aml:id=$regionId]/@aml:numRetractions)"/>
  <xsl:element name="span">

    <!-- convey the number of comments, minor/formal corrections, and retractions --> 
    <xsl:attribute name="num_c"><xsl:value-of select="$regionNumComments"/></xsl:attribute>
    <xsl:attribute name="num_mc"><xsl:value-of select="$regionNumMinorCorrections"/></xsl:attribute>
    <xsl:attribute name="num_fc"><xsl:value-of select="$regionNumFormalCorrections"/></xsl:attribute>
    <xsl:attribute name="num_retractions"><xsl:value-of select="$regionNumRetractions"/></xsl:attribute>

    <!-- populate the span tag's class attribute based on the presence of comments vs. corrections --> 
    <xsl:attribute name="class">

      <!-- we're always considered a note -->
      <xsl:text>note public</xsl:text>
      <xsl:if test="$regionNumMinorCorrections &gt; 0"><xsl:text> minrcrctn</xsl:text></xsl:if>
      <xsl:if test="$regionNumFormalCorrections &gt; 0"><xsl:text> frmlcrctn</xsl:text></xsl:if>
      <xsl:if test="$regionNumRetractions &gt; 0"><xsl:text> retractionCssStyle</xsl:text></xsl:if>
    </xsl:attribute>
    <xsl:attribute name="title">User Annotation</xsl:attribute>
    <xsl:attribute name="annotationId">
      <xsl:for-each select="/article/aml:regions/aml:region[@aml:id=$regionId]/aml:annotation">
        <xsl:value-of select="@aml:id"/>
        <xsl:if test="(following-sibling::aml:annotation)">
          <xsl:text>,</xsl:text>
        </xsl:if>
       </xsl:for-each>
    </xsl:attribute>

    <!-- only add an annotation to the display list if this is the beginning of the annotation -->
    <xsl:variable name="displayAnn">
      <xsl:variable name="annId" select="@aml:id"/>
      <xsl:if test="@aml:first">
        <xsl:for-each select="/article/aml:regions/aml:region[@aml:id=$regionId]/aml:annotation">
          <xsl:variable name="localAnnId" select="@aml:id"/>
          <xsl:if test="count(../preceding-sibling::aml:region/aml:annotation[@aml:id=$localAnnId]) = 0">
            <xsl:text>,</xsl:text>
            <xsl:value-of select="@aml:id"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </xsl:variable>

    <xsl:if test="not($displayAnn='')">
      <xsl:element name="a">
        <xsl:attribute name="href">#</xsl:attribute>
        <xsl:attribute name="class">bug public</xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('annAnchor',@aml:id)"/>
        </xsl:attribute>
        <xsl:attribute name="displayId">
          <!-- get rid of first comma in list -->
          <xsl:value-of select="substring($displayAnn,2)"/>
        </xsl:attribute>
        <xsl:attribute name="onclick">return(ambra.displayComment.show(this));</xsl:attribute>
        <xsl:attribute name="onmouseover">ambra.displayComment.mouseoverComment(this);</xsl:attribute>
        <xsl:attribute name="onmouseout">ambra.displayComment.mouseoutComment(this);</xsl:attribute>
        <xsl:attribute name="title">Click to preview this note</xsl:attribute>
      </xsl:element>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="aml:annotated">
  <xsl:call-template name="createAnnotationSpan"/>
</xsl:template>

<xsl:template name="topAnchor">
  <xsl:if test="string-length(normalize-space(.)) > 0">&#160;<a href="#top">Top</a></xsl:if>
</xsl:template>
  
</xsl:stylesheet>
