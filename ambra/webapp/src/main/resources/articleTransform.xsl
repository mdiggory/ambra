<?xml version="1.0" encoding="UTF-8"?><!-- 10/9/08: viewnlm contains xml version, we added encoding -->

<!-- 10/8/09: viewnlm contains informational comments (module, system, purpose, contents, change history, etc). we added one line (13a) to contents. -->
<!--              13a) Fix long words                              -->

<!-- 10/22/09: plos-specific stylesheet. contains plos-specific templates and modified viewnlm templates. imports and overrides viewnlm.
      all templates that have been modified from viewnlm, even slightly, are included here.
      includes section headers from viewnlm if we have modified anything within that section.
      includes comments about sections and templates that are contained in viewnlm and not replicated here.
-->

<!-- ============================================================= -->
<!--  1. TRANSFORM ELEMENT AND TOP-LEVEL SETTINGS                  -->
<!-- ============================================================= -->

<!-- 10/22/09: plos modifications (stylesheet vs transform, version 2.0, new namespaces) -->
<xsl:stylesheet version="2.0"
    id="ViewNLM-v2-04.xsl"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:util="http://dtd.nlm.nih.gov/xsl/util"
    xmlns:mml="http://www.w3.org/1998/Math/MathML"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:aml="http://topazproject.org/aml/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="util xsl xlink mml xs aml dc">

    <!-- 10/8/09: plos-specific instruction. import generic viewnlm; added to support separation of generic and plos-specific logic. -->
    <xsl:import href="viewnlm-v2.3.xsl"/> 

    <!-- 10/22/09: plos modifications (indent, doctypes)-->
    <xsl:output method="html"
        indent="no"
        encoding="UTF-8"
        omit-xml-declaration="yes"/>

    <!-- 10/8/09: plos modifications (added elements) -->
    <xsl:strip-space elements="abstract ack address annotation app app-group
        array article article-categories article-meta
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

    <!--1/28/10: plos modifications (to counter viewnlm strip-space="*") -->
    <xsl:preserve-space elements="*"/>

    <!-- 10/8/09: viewnlm contains preserve-space and runtime parameters -->

    <!-- 10/9/08: plos-specific param -->
    <!-- pub config -->
    <xsl:param name="pubAppContext"></xsl:param>

    <!-- 10/29/09: plos-specific variable -->
    <!-- store the version of the xml file so that we can conditionally select different options and preserve backward compatibility. -->
    <xsl:variable name="dtd-version" select="/article/@dtd-version"/>

    <!-- ============================================================= -->
    <!--  2. ROOT TEMPLATE - HANDLES HTML FRAMEWORK                    -->
    <!-- ============================================================= -->

    <!-- 10/9/08: plos modifications (major) -->
    <xsl:template match="/">
        <xsl:call-template name="nl-1"/>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  3. DOCUMENT ELEMENT                                          -->
    <!-- ============================================================= -->

    <!-- 10/8/09: viewnlm contains template article, which calls make-a-piece -->

    <!-- ============================================================= -->
    <!--  3. "make-a-piece"                                            -->
    <!-- ============================================================= -->

    <!--  10/8/09: viewnlm contains comments -->

    <!-- 10/9/09: plos-specific template -->
    <!-- initial context node is article -->
    <xsl:template name="make-section-id">
        <xsl:attribute name="id">
            <xsl:value-of select="concat('section',count(preceding-sibling::sec)+1)"/>
        </xsl:attribute>
    </xsl:template>

    <!-- 10/9/09: plos modifications (major) -->
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
        <xsl:text><!-- end : article information --></xsl:text>
        <!-- add editors summary box after article meta info and before introduction -->
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

    <!-- 10/9/09: plos-specific template -->
    <!-- 10/23/09: used to prevent double punctuation in references when xml also contains punctuation -->
    <xsl:template name="endsWithPunctuation">
        <xsl:param name="value"/>
        <xsl:choose>
            <xsl:when test="substring($value, string-length($value)) = '.'">true</xsl:when>
            <xsl:when test="substring($value, string-length($value)) = '!'">true</xsl:when>
            <xsl:when test="substring($value, string-length($value)) = '?'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- 10/9/09: viewnlm contains templates (capitalize, language, cleantitle, newlines) -->

    <!-- ============================================================= -->
    <!--  make-id, make-src, make-href, make-email                     -->
    <!-- ============================================================= -->

    <!-- 10/13/09: plos-specific template -->
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

    <!-- 10/13/09: plos-specific template -->
    <!-- use when we want to constrain the user selection to be at the element level only -->
    <xsl:template name="makeElementXpathLocation">
      <xsl:call-template name="makeXpathLocation"/>
      <xsl:attribute name="elmntslctn">
          <xsl:text>true</xsl:text>
      </xsl:attribute>
    </xsl:template>

    <!-- 10/13/09: plos-specific template -->
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

    <!-- 10/13/09: plos-specific template -->
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

    <!-- 10/13/09: viewnlm contains the remaining templates (make-id, make-src, make-href, make-email, make-anchor) -->

    <!-- 10/13/09: viewnlm contains display-id, table-setup sections -->

    <!-- ============================================================= -->
    <!-- "make-figs-and-tables"                                        -->
    <!-- ============================================================= -->

    <!-- 10/13/09: plos-specific template -->
    <!-- 11/17/09: creates box for figures and tables within article body. also creates slideshow. -->
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
                            <xsl:apply-templates select="label"/>
                        </span>
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

    <!-- 10/13/09: viewnlm contains remaining template in this section (make-figs-and-tables) -->

    <!-- ============================================================= -->
    <!-- 6. SUPPRESSED ELEMENTS                                        -->
    <!-- ============================================================= -->

    <!-- 10/13/09: viewnlm contains most templates in this section, and the comments that go with them, eg suppressed in no mode (journal-meta | article-meta, sub-article | response, @xlink) -->

    <!-- 10/13/09: plos modifications (major) -->
    <!-- tables and figures are displayed at the end of the document, using mode "put-at-end". so, in no-mode, we suppress them -->
    <xsl:template match="fig-group | table-wrap-group"/>

    <!-- ============================================================= -->
    <!-- CALLED TEMPLATES FOR ARTICLE PARTS                            -->
    <!-- ============================================================= -->


    <!-- ============================================================= -->
    <!--  7. MAKE-HTML-HEADER                                          -->
    <!-- ============================================================= -->

    <!-- 10/13/09: viewnlm contains template (make-html-header) -->

    <!-- 10/13/09: plos-specific template -->
    <!-- 10/13/09: used for financial disclosure and competing interests footnotes in metadata -->
    <xsl:template name="fund-compete">
        <xsl:for-each select="/article/back/fn-group">
          <xsl:if test="fn[@fn-type='financial-disclosure']">
            <p><strong>Funding:</strong><xsl:text> </xsl:text>
            <xsl:apply-templates select="fn[@fn-type='financial-disclosure']/p"/></p>
          </xsl:if>
          <xsl:if test="fn[@fn-type='conflict']">
            <p><strong>Competing interests:</strong><xsl:text> </xsl:text>
              <xsl:apply-templates select="fn[@fn-type='conflict']/p"/>
            </p>
          </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- 10/13/09: plos-specific template -->
    <!-- 10/13/09: used for author initials -->
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

    <!-- 10/13/09: plos-specific template -->
    <!-- 10/13/09: used for author initials -->
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

    <!-- 10/23/09: plos-specific template, includes elements from viewnlm (history/date, pub-date, copyright, permissions; moved from make-front & modified) -->
    <!-- make article metadata. includes citation, history, pub dates, copyright & permissions, glossary, author-notes, fund-compete, editors-list, fn-type='other' and fn without fn-type -->
    <xsl:template name="make-article-meta">

      <xsl:for-each select="front/article-meta">

        <!-- article citation -->
        <p>
          <strong>Citation: </strong>
          <!-- 10/29/09: authors -->
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
                  <!-- included missing suffix and made sure it doesn't have a trailing period -->
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
          <!-- 10/29/09: pub year -->
          <xsl:text> (</xsl:text>
            <xsl:value-of select="pub-date[@pub-type='collection']/year | pub-date[@pub-type='ppub']/year"/>
          <xsl:text>) </xsl:text>
         <!-- 10/29/09: article title -->
          <xsl:apply-templates select="title-group/article-title" mode="none"/>
          <xsl:variable name="at" select="normalize-space(title-group/article-title)"/>
          <!-- fixed bug where a title with a ? or ! at end was followed by a period in the citation. -->
          <xsl:if test="substring($at,string-length($at))!='?' and substring($at,string-length($at))!='!'">
            <xsl:text>.</xsl:text>
          </xsl:if>
          <xsl:text> </xsl:text>
          <!-- 10/29/09: journal/volume/issue/enumber/doi -->
          <xsl:value-of select="../journal-meta/journal-id[@journal-id-type='nlm-ta']"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="volume"/>(<xsl:value-of select="issue"/>):
          <xsl:value-of select="elocation-id"/>.
            doi:<xsl:value-of select="article-id[@pub-id-type='doi']"/>
        </p>

        <!-- created a new way to format the editors list in the citation box -->
        <xsl:for-each-group select="//contrib-group/contrib[@contrib-type='editor']" group-by="role">
 	        <xsl:call-template name="editors-list">
 	  	     <xsl:with-param name="r" select="//contrib-group/contrib[@contrib-type='editor' and role=current-grouping-key()]"/>
 	  	    </xsl:call-template>
 	    </xsl:for-each-group>
 	      <xsl:call-template name="editors-list">
 	  	   <xsl:with-param name="r" select="//contrib-group/contrib[@contrib-type='editor' and not(role)]"/>
 	      </xsl:call-template>

        <!-- 10/27/09: history/date, pub-date. modified from viewnlm (and moved from make-front) -->
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

        <!-- 10/27/09: copyright section modified from viewnlm (and moved from make-front) -->
        <!-- 11/15/10: reversed logic so that adding text is default,
            exclude specific licenses that shouldn't have the added text.
            simplified: removed 2.3 dtd-version choice, as we're not upgrading to 2.3. -->
        <p>
            <xsl:choose>
                <xsl:when test="copyright-statement[contains(., 'Public Domain') or contains(., 'public domain')]">
                    <xsl:apply-templates select="copyright-statement" />
                </xsl:when>
                <xsl:otherwise>
                    <strong>Copyright:</strong><xsl:text> &#169; </xsl:text>
                    <xsl:apply-templates select="copyright-year" /><xsl:text> </xsl:text>
                  <xsl:apply-templates select="copyright-statement" />
                </xsl:otherwise>
            </xsl:choose>
        </p>

        <!-- 10/29/09: funding and competing-interests footnotes -->
        <xsl:call-template name="fund-compete"/>

        <!-- glossary (abbreviations) -->
        <xsl:if test="../../back/glossary">
          <p>
            <strong><xsl:value-of select="../..//back/glossary/title"/>: </strong>
            <xsl:for-each select="../../back/glossary/def-list/def-item">
              <xsl:apply-templates select="term"/>, <xsl:apply-templates select="def "/><xsl:if test="position() != last()">; </xsl:if>
            </xsl:for-each>
          </p>
        </xsl:if>
        <!-- corresponding author footnote -->
        <xsl:if test="author-notes/corresp">
          <p>
            <xsl:apply-templates select="author-notes/corresp" mode="front"/>
          </p>
        </xsl:if>
        <!-- equal contribution footnote -->
        <xsl:if test="contrib-group/contrib[@contrib-type='author'][@equal-contrib='yes']">
          <p>
            <a name="equal-contrib"></a><xsl:text>#</xsl:text>
              These authors contributed equally to this work.
          </p>
        </xsl:if>
        <!-- current-aff footnote -->
        <xsl:for-each select="author-notes/fn[@fn-type='current-aff']">
          <p>
            <xsl:apply-templates select="." mode="front"/>
          </p>
        </xsl:for-each>
        <!-- deceased author footnote -->
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

      <!-- used to display fn-type="other" and fn with no fn-type at the bottom of the citation section. -->
      <xsl:for-each select="//back/fn-group/fn[@fn-type='other']/node() | //back/fn-group/fn[not(@fn-type)]/node()">
        <p><xsl:apply-templates/></p>
      </xsl:for-each>

    </xsl:template>

    <!-- 10/23/09: plos-specific template -->
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

    <!-- 10/23/09: plos-specific template -->
    <xsl:template name="make-editors-summary">
      <xsl:for-each select="front/article-meta/abstract[@abstract-type='editor']">
        <div class="editorsAbstract">
          <xsl:call-template name="makeXpathLocation"/>
          <xsl:call-template name="words-for-abstract-title"/>
          <xsl:apply-templates select="*[not(self::title)]"/>
        </div>
      </xsl:for-each>
    </xsl:template>

    <!-- 10/26/09: plos comment: added the following template rules to correctly copy and map different markup within glossary definitions -->

    <!-- 10/26/09: plos-specific template -->
    <xsl:template match="def-item//p">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- 10/26/09: plos-specific template -->
    <xsl:template match="def-item//named-content">
      <span class="{@content-type}">
        <xsl:apply-templates/>
      </span>
    </xsl:template>

    <!-- 10/26/09: plos-specific template -->
    <xsl:template match="def-item//sup | def-item//sub | def-item//em | def-item//strong">
      <xsl:element name="{local-name()}">
        <xsl:apply-templates/>
      </xsl:element>
    </xsl:template>

    <!-- 10/26/09: plos-specific template -->
    <!-- output def-lists in the body of the text (note: different than def-list in the glossary as above) -->
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

    <!-- 10/19/09: plos modifications (major) -->
    <xsl:template name="make-front">
      <xsl:call-template name="nl-1"/>

      <!-- change context to front/article-meta -->
      <xsl:for-each select="front/article-meta">
        <xsl:apply-templates select="title-group" mode="front"/>
        <p class="authors" xpathLocation="noSelect">
          <xsl:for-each select="contrib-group/contrib[@contrib-type='author']">
            <xsl:choose>

              <!-- 4/15/10: added dc & foaf, brought up to date with current production -->
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

              <!-- email tag is new with version 2.3 of the dtd. currently disabled -->
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

        <!-- 10/27/09: plos-specific section. used to create the author affiliation line -->
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
            <!-- 7/21/10: enabled display of italics and other formatting in addr-line. xref addr-line normalize-space at the end of this section -->
            <xsl:apply-templates select="addr-line/node()" />
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>

          <!-- each aff that is NOT directly inside a contrib also makes a row: empty left, details at right -->
          <xsl:for-each select="aff">
            <xsl:variable name="rid"><xsl:value-of select="@id"/></xsl:variable>
            <xsl:if test="../contrib-group/contrib[@contrib-type='author']/xref[@ref-type='aff' and @rid=$rid]">
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
              <!-- 7/21/10: enabled display of italics and other formatting in addr-line. xref addr-line normalize-space at the end of this section -->
              <xsl:apply-templates select="addr-line/node()" />
              <xsl:if test="following-sibling::aff">
                <xsl:variable name="nextId">
                  <xsl:value-of select="following-sibling::aff[1]/@id"/>
                </xsl:variable>
                <xsl:if test="../contrib-group/contrib[@contrib-type='author']/xref[@ref-type='aff' and @rid=$nextId]">
                  <xsl:text>, </xsl:text>
                </xsl:if>
              </xsl:if>
            </xsl:if>
          </xsl:for-each>
        </p>

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

    <!-- 10/19/09: plos-specific template -->
    <!-- remove leading and trailing space from addr-line elements that were incorrectly written this way -->
    <!--<xsl:template match="addr-line">
      <xsl:value-of select="normalize-space()"/>
    </xsl:template>                 -->

   <!-- 7/21/10: plos-specific template (part 1: fixes stray spaces in addr-line after enabling display of italics and other formatting. need to account for mixed element content) -->
    <xsl:template match="addr-line/node()[last()][self::text()]">
      <xsl:variable name="x" select="normalize-space(concat(.,'x'))"/>
      <xsl:value-of select="substring(normalize-space(concat('x',.)),2)"/>
    </xsl:template>

    <!-- 7/21/10: plos-specific template (part 2: fixes stray spaces in addr-line after enabling display of italics and other formatting. need to account for mixed element content) -->
    <xsl:template match="addr-line[not(*)]">
     <xsl:value-of select="normalize-space(.)"/>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  9. MAKE-BODY                                                 -->
    <!-- ============================================================= -->

    <!-- 10/19/09: plos modifications (minor) -->
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

    <!-- 10/19/09: plos modifications (major) -->

    <!-- initial context node is article -->
    <xsl:template name="make-back">
      <!-- change context node to back -->
      <xsl:for-each select="back">
        <xsl:apply-templates select="title"/>
        <xsl:apply-templates select="ack"/>
        <xsl:call-template name="author-contrib"/>
        <xsl:apply-templates select="notes"/>
        <xsl:apply-templates select="*[not(self::title) and not(self::fn-group) and not(self::ack) and not(self::notes)]"/>
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

    <!-- 10/19/09: plos-specific template -->
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

    <!-- 10/19/09: viewnlm contains sections 11 (make-post-publication) and 12 (make-end-metadata). we don't use section 12 -->

    <!-- ============================================================= -->
    <!--  NARRATIVE CONTENT AND COMMON STRUCTURES                      -->
    <!-- ============================================================= -->

    <!-- ============================================================= -->
    <!--  13. PARAGRAPH WITH ITS SUBTLETIES                            -->
    <!-- ============================================================= -->

   <!-- 2/18/10: plos modifications, using 2.3 version -->
   <xsl:template match="p">
       <xsl:choose>
		<xsl:when test="parent::list-item">
		  <xsl:apply-templates/>
		  <xsl:if test="following-sibling::p">
			<br/>
		  </xsl:if>
	    </xsl:when>
		<xsl:otherwise>
		  <p>
            <xsl:call-template name="makeXpathLocation"/>
			<xsl:apply-templates/>
		  </p>
		</xsl:otherwise>
	  </xsl:choose>
	  <xsl:call-template name="nl-1"/>
	</xsl:template>

    <!-- 10/19/09: plos modifications (major) -->
    <!-- the first p in a footnote displays the fn symbol or, if no symbol, the fn id -->
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

    <!-- 10/19/09: viewnlm contains template speech/p[1] -->

    <!-- 10/19/09: entire 13a section is plos-specific -->
    <!-- ================================================================== -->
    <!--  13a. FIND ALL TEXT NODES AND                                      -->
    <!--          FIX WORDS LONGER THEN 20 characters                       -->
    <!--          DO SOME TEXT PROCESSING                                   -->
    <!-- ================================================================== -->

    <xsl:template match="text()">
      <!-- do some character transformations first-->
      <xsl:variable name="str" select="translate(., '&#8194;&#x200A;&#8764;&#x02236;&#x02208;', '  ~:&#x404;') "/>

      <xsl:choose>
        <!-- no need to progress further if the entire element is less then 40 characters -->
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

    <!-- break words longer then len characters -->
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
          <xsl:value-of select="$str"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!-- 10/19/09: viewnlm contains template def/p[1]-->
 
    <!-- ============================================================= -->
    <!--  14. SECTION                                                  -->
    <!-- ============================================================= -->


    <!-- 10/29/09: plos modifications -->
    <!-- the first body/sec puts out no rule at its top, because body already puts out a part-rule at its top;
    subsequent body/secs do put out a section-rule -->
    <xsl:template match="body/sec">
      <xsl:call-template name="nl-1"/>
      <div>
        <xsl:call-template name="make-section-id"/>
        <xsl:call-template name="makeXpathLocation" />
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

    <!-- 10/29/09: plos modifications -->
    <!-- no other level of sec puts out a rule -->
    <xsl:template match="sec">
      <xsl:apply-templates/>
      <xsl:call-template name="nl-1"/>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  15. LIST and its Internals                                   -->
    <!-- ============================================================= -->

    <!-- 10/29/09: plos modifications (minor) -->
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

    <!-- 2/18/10: plos modifications, to accommodate 2.3 version of p -->
    <xsl:template match="list-item">
	  <xsl:call-template name="nl-1"/>
		<li>
          <xsl:call-template name="makeXpathLocation"/>
          <!-- 6/1/10: added support for prefix-word list attribute -->
          <xsl:if test="../@prefix-word">
            <xsl:value-of select="../@prefix-word"/>
            <xsl:text> </xsl:text>
          </xsl:if>
		  <xsl:apply-templates/>
		</li>
	  <xsl:call-template name="nl-1"/>
	</xsl:template>

    <!-- 10/21/09: viewnlm contains section 16 (display-quote) -->

    <!-- ============================================================= -->
	<!--  17. SPEECH and its internals                                 -->
	<!-- ============================================================= -->

    <!-- 7/22/10: viewnlm contains most of the templates in this section -->

    <!-- 7/22/10: plos modifications. we don't currently use this, so overriding with empty template, but if we ever reinstate it, we'll need to replace the viewnlm <b> with <strong> -->
    <xsl:template match="speaker" mode="show-it-here" />

    <!-- ============================================================= -->
	<!--  18. STATEMENT and its internals                              -->
	<!-- ============================================================= -->

    <!-- 7/22/10: viewnlm contains template statement -->

    <!-- 7/22/10: plos modifications. we don't currently use this, so overriding with empty template, but if we ever reinstate it, we'll need to replace viewnlm <b> with <strong> -->
    <xsl:template match="statement/label | statement/title" />

    <!-- 7/22/10: viewnlm contains section 19 (verse-group and its internals) -->

    <!-- ============================================================= -->
    <!--  20. BOXED-TEXT                                               -->
    <!-- ============================================================= -->

    <!-- 10/21/09: plos modifications (major) -->
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

    <!-- 10/21/09: viewnlm contains section 21 (preformat) -->

    <!-- ============================================================= -->
    <!--  22. SUPPLEMENTARY MATERIAL                                   -->
    <!-- ============================================================= -->

    <!-- 10/21/09: plos modifications (major) -->
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

    <!-- 10/21/09: plos modifications (major) -->
    <!-- both are grouping elements to keep parts together -->
    <xsl:template match="disp-formula | chem-struct-wrapper">
      <br/>
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
        <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:element>
      <!-- added span class='equation' surrounding equations -->
      <span class="equation">
        <xsl:apply-templates select="*[not(self::label)]"/>
        <xsl:apply-templates select="label"/>
      </span>
      <br/>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  24. FORMATTING ELEMENTS                                      -->
    <!-- ============================================================= -->

    <!-- 10/21/09: viewnlm contains most templates in this section -->

    <!-- 7/22/10: plos modifications -->
    <xsl:template match="bold">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>

    <!-- 7/22/10: plos modifications -->
	<xsl:template match="italic">
		<em>
			<xsl:apply-templates/>
		</em>
	</xsl:template>

    <!-- 4/23/10: plos modifications -->
    <xsl:template match="sc//text()">
        <xsl:param name="str" select="."/>
        <xsl:call-template name="capitalize">
            <xsl:with-param name="str" select="$str"/>
        </xsl:call-template>
    </xsl:template>


    <!-- ============================================================= -->
    <!--  25. SEMANTIC ELEMENTS                                        -->
    <!-- ============================================================= -->

    <!-- 10/21/09: viewnlm contains template abbrev -->

    <!-- 10/21/09: plos modifications (major) -->
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


    <!-- 10/21/09: viewnlm contains the remaining templates in this section (inline-formula, inline-supplementary-material, glyph-data) -->

    <!-- ============================================================= -->
    <!--  Named Content                                                -->
    <!-- ============================================================= -->

    <!-- 10/21/09: plos modifications (major) -->
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

    <!-- 10/21/09: viewnlm contains section 26 (break and horizontal rule), section 27 (chem-struct), and section 28 (tex-math and mml:math). -->

    <!-- ============================================================= -->
    <!--  29. GRAPHIC and MEDIA                                        -->
    <!-- ============================================================= -->

    <!-- 10/21/09: plos modifications (major) -->
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

    <!-- 10/21/09: viewnlm contains template media -->

    <!-- 10/21/09: viewnlm contains section 30 (array) -->

    <!-- ============================================================= -->
    <!--  31. CAPTIONING                                               -->
    <!-- ============================================================= -->

    <!-- 10/21/09: plos modifications (major) -->
    <!-- the chooses before and after the element content tweak the display as appropriate -->
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

    <!-- 10/21/09: viewnlm 2.3 contains new templates (label, label mode none, label mode nscitiation -->

    <!-- 10/21/09: plos modifications (major) -->
    <xsl:template match="caption">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 10/21/09: viewnlm contains template long-desc -->

    <!-- 10/21/09: plos modifications (minor) -->
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

    <!-- 10/21/09: viewnlm contains section 32 (figure, mode put-at-end), much of section 33 (table-wrap, mode put-at-end; mode front), section 34 (journal-meta) -->

    <!-- 5/4/10: plos modifications - viewnlm 2.3 changed this. we override with v2 version -->
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

    <!-- 5/4/10: plos modifications - viewnlm 2.3 changed this. we override with v2 version -->
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


    <!-- ============================================================= -->
    <!--  35) ARTICLE-META (in order of appearance in output)          -->
    <!-- ============================================================= -->

    <!-- 10/22/09: viewnlm contains templates: ext-link, supplementary-material, self-uri, product (mode front, * mode product -->

    <!-- 2/23/10: viewnlm contains copyright-statement|copyright-year|copyright-holder. we don't use it -->

    <!-- 10/22/09: viewnlm 2.3 contains template license. we don't currently use it (and won't until 3.0 upgrade) -->

    <!-- 10/22/09: viewnlm contains the remaining templates in this section: history/date mode front, pub-date mode front, volume mode front, issue mode front, elocation-id mode front, fpage mode front, lpage mode front, article-id, contract-num & contract sponsor mode front, custom-meta-wrap -->

    <!-- 10/22/09: viewnlm 2.3 contains template custom-meta-wrap. we don't use it -->

    <!-- ============================================================= -->
    <!--  36) TITLE-GROUP                                              -->
    <!-- ============================================================= -->

    <!-- 10/30/09: viewnlm comments  -->
    <!-- title-group -->
    <!-- appears only in article-meta -->
    <!-- the fn-group, if any, is output in the "back" of the html page, together with any other fn-group. -->

    <!-- 10/30/09: plos modifications (major) -->
    <xsl:template match="title-group" mode="front">
      <xsl:apply-templates select="subtitle" mode="front"/>
    </xsl:template>

    <!-- 10/30/09: plos modifications (minor) -->
    <xsl:template match="article-title" mode="front">
      <h1 xpathLocation="noSelect">
        <xsl:apply-templates/>
      </h1>
      <xsl:call-template name="nl-1"/>
    </xsl:template>

    <!-- 10/30/09: plos modifications (major) -->
    <xsl:template match="subtitle" mode="front">
      <h2 xpathLocation="noSelect">
        <xsl:apply-templates/>
      </h2>
    </xsl:template>

    <!-- 6/9/10: plos-specific template (part 1: fixes stray spaces in article citation. need to account for mixed element content) -->
    <xsl:template match="title-group/article-title/node()[last()][self::text()]" mode="none">
      <xsl:variable name="x" select="normalize-space(concat(.,'x'))"/>
      <xsl:value-of select="substring(normalize-space(concat('x',.)),2)"/>
    </xsl:template>

    <!-- 6/9/10: plos-specific template (part 2: fixes stray spaces in article citation. need to account for mixed element content) -->
    <xsl:template match="title-group/article-title[not(*)]" mode="none">
     <xsl:value-of select="normalize-space(.)"/>
    </xsl:template>                  

    <!-- 10/30/09: viewnlm contains the remaining templates in this section (trans-title, alt-title) -->

    <!-- ============================================================= -->
    <!--  37) PARTS OF CONTRIB                                         -->
    <!-- ============================================================= -->

    <!-- 10/30/09: plos modifications (minor) -->
    <!-- collab -->
    <!-- a mixed-content model; process it as given -->
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

    <!-- 10/30/09: plos-specific template -->
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

    <!-- 10/30/09: viewnlm comments -->
    <!-- name -->
    <!-- uses mode="contrib" within -->

    <!-- 10/30/09: plos modfications (minor) -->
    <xsl:template match="name" mode="front">
      <xsl:apply-templates select="prefix"      mode="contrib"/>
      <xsl:apply-templates select="given-names" mode="contrib-abbr"/>
      <xsl:apply-templates select="surname"     mode="contrib"/>
      <xsl:apply-templates select="suffix"      mode="contrib"/>
      <xsl:apply-templates select="../degrees"  mode="contrib"/>
    </xsl:template>

    <!-- 10/30/09: plos-specific comment -->
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

    <!-- 10/30/09: plos-specific template -->
    <xsl:template match="name" mode="front-refs">
      <xsl:apply-templates select="../xref[@ref-type='aff']" mode="contrib"/>
      <xsl:if test="../@equal-contrib='yes'">
        <sup><a href="#equal-contrib">#</a></sup>
      </xsl:if>
      <xsl:apply-templates select="../xref[@ref-type='fn']" mode="contrib"/>
      <!-- if the deceased attribute is set and there isn't already a deceased footnote,
        output a dagger. however, be careful in checking for the existence of an editor defined
        deceased fn. -->
      <xsl:if test="../@deceased='yes' and not(../xref/sup='‡') and not(../ref/sup='&amp;dagger;')
        and not(../ref/sup='&amp;Dagger;')">
        <sup><a href="#deceased">&#x2020;</a></sup>
      </xsl:if>
      <xsl:apply-templates select="../xref[@ref-type='corresp']" mode="contrib"/>
      <xsl:apply-templates select="../xref[@ref-type='author-notes']" mode="contrib"/>
    </xsl:template>

    <!-- 11/2/09: plos-specific template -->
    <xsl:template match="name" mode="article-meta">
      <xsl:apply-templates select="prefix"      mode="contrib"/>
      <xsl:apply-templates select="given-names" mode="contrib"/>
      <xsl:apply-templates select="surname"     mode="contrib"/>
      <xsl:apply-templates select="suffix"      mode="contrib"/>
      <xsl:apply-templates select="../degrees"  mode="contrib"/>
      <xsl:apply-templates select="../xref"     mode="contrib"/>
    </xsl:template>

    <!-- 11/2/09: plos-specific template -->
    <!-- added abbreviate-name template to correctly put a period after a single initial middle name -->
    <xsl:template match="given-names" mode="contrib-abbr">
      <xsl:call-template name="abbreviate-name">
        <xsl:with-param name="n" select="."/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </xsl:template>

    <!-- 11/2/09: plos-specific template -->
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

    <!-- 6/4/10: plos modifications (minor) -->
    <xsl:template match="suffix" mode="contrib">
		<xsl:text> </xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

    <!-- 11/3/09: viewnlm contains the next few templates (surname mode contrib, degrees mode contrib) -->

    <!-- 11/3/09: plos modifications (major) -->
    <!-- the formatting is sometimes in the source XML, e.g., <sup><italic>a</italic></sup> -->
    <xsl:template match="xref[@ref-type='author-notes']" mode="contrib">
      <xsl:choose>
        <xsl:when test="not(.//italic) and not (.//sup)">
          <sup><em>
            <xsl:element name="a">
              <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
              <xsl:apply-templates/>
            </xsl:element>
          </em></sup>
        </xsl:when>
        <xsl:when test="not(.//italic)">
          <em>
            <xsl:element name="a">
              <xsl:attribute name="href">#<xsl:value-of select="@rid"/></xsl:attribute>
              <xsl:attribute name="class">fnoteref</xsl:attribute>
              <xsl:value-of select="sup"/>
            </xsl:element>
          </em>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!-- 11/3/09: plos-specific template -->
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

    <!-- 11/3/09: plos modifications (major) -->
    <!-- the formatting is sometimes in the source XML, e.g., <sup><italic>a</italic></sup> -->
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

    <!-- 11/3/09: plos-specific template -->
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

    <!-- 11/3/09: viewnlm contains templates (various author-comment & bio templates, address, institution, address & children) -->

    <!-- 11/3/09: plos modifications (major) -->
    <!-- aff -->
    <!-- these affs are inside a contrib element -->
    <xsl:template match="aff" mode="front">
      <xsl:apply-templates select="institution" /><xsl:text>, </xsl:text>
      <xsl:apply-templates select="addr-line" />
    </xsl:template>

    <!-- 6/4/10: plos modifications (major) -->
    <!-- on-behalf-of -->
	<xsl:template match="on-behalf-of" mode="front">
	  <xsl:text>, </xsl:text>
		<xsl:apply-templates/>
	</xsl:template>
    
    <!-- 11/3/09: viewnlm contains templates (aff-outside-contrib, role, email, author-notes mode front) -->

    <!-- 7/22/10: plos modifications -->
    <xsl:template match="author-notes/title" mode="front">
		<strong>
			<xsl:apply-templates/>
		</strong>
		<br/>
		<xsl:call-template name="nl-1"/>
	</xsl:template>

    <!-- 11/3/09: plos modifications (major) -->
    <!-- author-notes/corresp -->
    <!-- mixed-content; process it as given -->
    <xsl:template match="author-notes/corresp" mode="front">
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:element>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/3/09: plos modifications (major) -->
    <!-- author-notes/fn -->
    <!-- optional label, one or more paras -->
    <!-- unmoded (author-notes only appears in article-meta) -->
    <xsl:template match="author-notes/fn[@fn-type='current-aff']" mode="front">
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:element>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/3/09: plos-specific template -->
    <xsl:template match="author-notes/fn[@fn-type='deceased']" mode="front">
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:element>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/3/09: plos-specific template -->
    <xsl:template match="author-notes/fn[@fn-type='other']" mode="front">
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:element>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 2/11/10: plos modifications (revert to v2 version; 2.3 version causes extra period) -->
    <!-- author-notes/fn/label -->
    <xsl:template match="author-notes/fn/label">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/3/09: plos modifications (major) -->
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

    <!-- 11/3/09: viewnlm contains comments -->

    <!-- ============================================================= -->
    <!-- BACK (unmoded templates)                                      -->
    <!-- ============================================================= -->

    <!-- ============================================================= -->
    <!--  38. BACK MATTER: ACKNOWLEDGEMENTS                            -->
    <!-- ============================================================= -->

    <!-- 11/3/09: plos modifications (major) -->
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

    <!-- 5/25/10: plos modifications (use v2 version) -->
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

    <!-- 11/4/09: viewnlm contains template fn-group -->

    <!-- ============================================================= -->
    <!--  Footnote                                                     -->
    <!-- ============================================================= -->

    <!-- 11/4/09: plos modifications (major) -->
    <!-- symbol or id is displayed by the first para within the fn     -->
    <xsl:template match="fn">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  41. BACK-MATTER: NOTES                                       -->
    <!-- ============================================================= -->

    <!-- 11/4/09: plos modifications -->
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

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="notes/sec/title">
      <h3 xpathLocation="noSelect"><xsl:value-of select="."/><xsl:call-template name="topAnchor"/></h3>
    </xsl:template>

    <!-- 11/4/09: viewnlm contains the remaining template in this section (note) -->

    <!-- ============================================================= -->
    <!--  42. BACK MATTER: GLOSSARY                                    -->
    <!-- ============================================================= -->

    <!-- 2/24/10: plos doesn't use this, override viewnlm -->
    <xsl:template match="glossary"/>

    <!-- 2/24/10: plos doesn't use this, override viewnlm -->
    <xsl:template match="gloss-group"/>

    <!-- 2/24/10: plos doesn't use this, override viewnlm -->
    <xsl:template match="def-list"/>

    <!-- 2/24/10: plos doesn't use this, override viewnlm -->
    <xsl:template match="def-item"/>

    <!-- 1/22/10: plos modifications -->
    <xsl:template match="term">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 1/22/10: plos modifications -->
    <xsl:template match="def">
       <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/4/09: viewnlm contains section 43 (target of a reference) -->

    <!-- ============================================================= -->
    <!--  44. XREFS                                                    -->
    <!-- ============================================================= -->

    <!-- 11/4/09: plos modifications (major) -->
    <!-- xref for fn, table-fn, or bibr becomes a superior number -->
    <xsl:template match="xref[@ref-type='fn']">
      <span class="xref">
        <xsl:call-template name="make-id"/>
        <sup>

          <!-- if immediately-preceding sibling was an xref, punctuate (otherwise assume desired punctuation is in the source).-->
          <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
            <span class="gen"><xsl:text>, </xsl:text></span>
          </xsl:if>
          <!-- displays the element content (if any), not the @rid -->
          <a href="#{@rid}"><xsl:apply-templates/></a>
        </sup>
      </span>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="xref[@ref-type='table-fn']">
      <span class="xref">
        <xsl:call-template name="make-id"/>
        <sup>
          <!-- if immediately-preceding sibling was an xref, punctuate (otherwise assume desired punctuation is in the source).-->
          <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
            <span class="gen"><xsl:text>, </xsl:text></span>
          </xsl:if>
          <!-- displays the footnote symbols (if any).
            removed the hyperlink because table footnotes are not displayed on the web page,
            therefore there is nothing to hyperlink to. -->
          <xsl:apply-templates/>
        </sup>
      </span>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="xref[@ref-type='bibr']">
      <!-- if immediately-preceding sibling was an xref, punctuate (otherwise assume desired punctuation is in the source).-->
      <xsl:if test="local-name(preceding-sibling::node()[1])='xref'">
        <xsl:text>,</xsl:text>
      </xsl:if>
      <a href="#{@rid}"><xsl:apply-templates/></a>
    </xsl:template>

    <!-- 11/4/09: viewnlm contains template (text normalize-space) -->

    <!-- 11/4/09: plos modifications (major) -->
    <!-- In xref of type fig or of type table, the element content is the figure/table number
        and typically part of a sentence,
        so -not- a superior number. -->
    <xsl:template match="xref[@ref-type='fig'] | xref[@ref-type='table']">
      <a href="#{@rid}">
        <xsl:apply-templates/>
      </a>
    </xsl:template>

    <!-- 11/4/09: plos modifications (minor) -->
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

    <!-- 11/4/09: plos modifications (minor) -->
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

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="email">
      <a>
        <xsl:attribute name="href">mailto:<xsl:apply-templates/></xsl:attribute>
        <xsl:apply-templates/>
      </a>
    </xsl:template>

    <!-- 11/4/09: viewnlm contains template (mailto) -->

    <!-- ============================================================= -->
    <!--  46. TITLES: MAIN ARTICLE DIVISIONS                           -->
    <!-- ============================================================= -->

    <!-- 11/4/09: plos modifications (major) -->
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

    <!-- 11/4/09: plos-specific template -->
    <!-- article main main body heading -->
    <xsl:template match="body/sec/title">
      <!-- 6/2/10: added if test to fix empty h3 in articles that have empty title in the first body/sec -->
      <xsl:if test="string(.)">
        <h3 xpathLocation="noSelect">
          <xsl:apply-templates/><xsl:call-template name="topAnchor"/>
        </h3>
      </xsl:if>
    </xsl:template>

    <!-- ============================================================= -->
    <!--  47. TITLES: FIRST-LEVEL DIVISIONS AND DEFAULT                -->
    <!-- ============================================================= -->

    <!-- 11/4/09: plos modifications (major) -->
    <!-- first-level divisions and default -->
    <xsl:template match="ack/sec/title | app/sec/title | boxed-text/title | gloss-group/title">
      <xsl:call-template name="nl-1"/>
      <span class="tl-lowest-section"><xsl:apply-templates/></span>
      <xsl:call-template name="nl-1"/>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <!-- article second level heading -->
    <xsl:template match="body/sec/sec/title">
      <xsl:call-template name="nl-1"/>
      <h4>
        <xsl:call-template name="makeXpathLocation" >
        </xsl:call-template>
        <xsl:apply-templates/>
      </h4>
      <xsl:call-template name="nl-1"/>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <!-- article third level heading -->
    <xsl:template match="body/sec/sec/sec/title">
      <h5>
        <xsl:call-template name="makeXpathLocation" >
        </xsl:call-template>
        <xsl:apply-templates/>
        <!-- added test to prevent double periods from appearing. -->
        <!-- 6/1/10: added test for question mark. -->
        <xsl:if test="not(ends-with(normalize-space(),'.')) and not(ends-with(normalize-space(),'?'))">
          <xsl:text>.</xsl:text>
        </xsl:if>
      </h5>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="abstract/sec/title">
      <xsl:call-template name="nl-1"/>
      <!-- don't output an abstract's title if it's blank -->
      <xsl:if test="string-length() &gt; 0">
        <h3 xpathLocation="noSelect">
          <xsl:apply-templates/>
        </h3>
      </xsl:if>
      <xsl:call-template name="nl-1"/>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="ref-list[not(ancestor::back)]/title">
      <a>
        <xsl:attribute name="id"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="replace(lower-case(.),' ','')"/></xsl:attribute>
      </a>
      <h3 xpathLocation="noSelect">
        <xsl:apply-templates/>
      </h3>
    </xsl:template>

    <!-- 11/4/09: plos-specific template -->
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

    <!-- 11/4/09: plos-specific template -->
    <xsl:template match="caption/title">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/4/09: viewnlm 2.3 contains template (list/title) -->

    <!-- 11/4/09: viewnlm 2.3 contains template (def-list/title). we override in section 46 -->

    <!-- 11/4/09: plos modifications (major) -->
    <!-- default: any other titles found -->
    <xsl:template match="title">
      <xsl:choose>
        <!-- if there's a title, use it -->
        <xsl:when test="count(ancestor::sec) > 1">
          <xsl:call-template name="nl-1"/>
          <h4>
            <xsl:call-template name="makeXpathLocation">
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

    <!-- 11/4/09: plos modifications (major) -->
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

    <!-- 11/4/09: viewnlm contains templates (epage, series) -->

    <!-- 11/4/09: plos modifications (major) -->
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

    <!-- 11/4/09: viewnlm contains template (annotation) -->

    <!-- permissions -->
      <xsl:template match="permissions"/>

    <!-- 1/28/10: plos modifications (major) -->
    <!-- copyright-statement whether or not part of permissions -->

      <xsl:template match="copyright-statement">
        <xsl:apply-templates/>
      </xsl:template>


    <!-- 11/4/09: viewnlm contains template (license) -->

    <!-- 11/4/09: viewlnm contains section 50 (unmoded data elements parts of a date) -->

    <!-- ============================================================= -->
    <!--  51. UNMODED DATA ELEMENTS: PARTS OF A NAME                   -->
    <!-- ============================================================= -->

    <!-- 11/5/09: plos modifications (minor) -->
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

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="aff" mode="editor">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/5/09: viewnlm contains the remaining templates in this section (aff, etal) -->

    <!-- 11/5/09: viewnlm contains the section citation and nlm-citation, which consists only of comments -->

    <!-- ============================================================= -->
    <!--  52. BACK MATTER: REF-LIST                                    -->
    <!-- ============================================================= -->

    <!-- 11/5/09: plos modifications (major) -->
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
        <xsl:apply-templates select="p"/>
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
                  <!-- 6/7/10: added test to fix authors not being pulled into 'find this article' when no person-group-type -->
                  <xsl:choose>
                    <xsl:when test="$cit/person-group[not(@person-group-type)]">
                      <xsl:value-of select="$cit/person-group[1]/name[1]/surname"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$cit/person-group[@person-group-type='author'][1]/name[1]/surname"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:variable name="findURL">
                  <xsl:value-of select="concat($pubAppContext,'/article/findArticle.action?author=',
                    $author, '&amp;title=', $artTitle)"/>
                </xsl:variable>
                <!-- only output 'find this article' link if there is no ext-link already in the citation -->
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

    <!-- 2/25/10: viewnlm 2.3 changed this (major changes), we don't use -->
    <xsl:template match="ref"/>

    <!-- 2/25/10: viewnlm 2.3 changed this (major changes), we don't use -->
    <xsl:template match="ref/label | citation/label"/>

    <!-- ============================================================= -->
    <!--  54. CITATION (for NLM Archiving DTD)                         -->
    <!-- ============================================================= -->

    <!-- 2/24/10: viewnlm contains template ref/citation. this contains 2.3 changes that we don't want, but we don't use this template (higher-priority templates handle ref processing) -->

    
    <!-- ============================================================= -->
    <!--  55. NLM-CITATION (for NLM Publishing DTD)                    -->
    <!-- ============================================================= -->

    <!-- 11/5/09: viewnlm contains comments -->

    <!-- 11/5/09: plos modifications (major) -->
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
          <xsl:apply-templates select="page-count" mode="book"/>
        </xsl:when>
        <xsl:when test="person-group[not(@person-group-type)]">
          <span class="authors">
            <!-- 6/3/10: added mode, to fix spacing when person-group or collab is followed by collab -->
            <xsl:apply-templates select="person-group" mode="book"/>
            <xsl:apply-templates select="collab" mode="book"/>
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

    <!-- 11/5/09: plos modifications (major) -->
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

    <!-- 11/5/09: plos modifications (major) -->
    <!-- Government and other reports, other, web, and commun -->
    <xsl:template match="ref/citation[@citation-type='gov']
      | ref/citation[@citation-type='web']
      | ref/citation[@citation-type='commun']
      | ref/nlm-citation[@citation-type='gov']
      | ref/nlm-citation[@citation-type='web']
      | ref/nlm-citation[@citation-type='commun']">
      <xsl:apply-templates select="person-group" mode="book"/>
      <xsl:apply-templates select="collab"/>
      <xsl:apply-templates select="year | month | time-stamp | season | access-date"
        mode="book"/>
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
          <xsl:apply-templates select="article-title|gov"
            mode="book"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="fpage | lpage"
        mode="book"/>
      <xsl:call-template name="citation-tag-ends"/>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
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

    <!-- 11/5/09: plos modifications (major) -->
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

    <!-- 11/5/09: plos modifications (major) -->
    <!-- If none of the above citation-types applies,
      use mode="none". This generates punctuation. -->
    <!-- (e.g., citation-type="journal")              -->
    <xsl:template match="citation">
      <span class="authors">
        <!-- 6/3/10: added mode, to fix spacing when person-group or collab is followed by collab -->
        <xsl:apply-templates select="person-group" mode="none"/>
        <xsl:apply-templates select="collab" mode="none"/>
      </span>
      <xsl:apply-templates select="*[not(self::annotation)
        and not(self::edition) and not(self::person-group)
        and not(self::collab) and not(self::comment)]|text()"
        mode="none"/>
      <xsl:call-template name="citation-tag-ends"/>
    </xsl:template>

    <!-- 11/5/09: plos-specific template -->
    <!-- modified the above citation template to work with legacy nlm-citations for journal articles.
      since the ordering of these nlm-citations child elements does not correspond to the
      output ordering in the citation, we explicitly write out the ordering below. -->
    <xsl:template match="nlm-citation">
      <span class="authors">
        <!-- 6/3/10: added mode, to fix spacing when person-group or collab is followed by collab -->
        <xsl:apply-templates select="person-group" mode="none"/>
        <xsl:apply-templates select="collab" mode="none"/>
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

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="person-group" mode="book">
      <xsl:call-template name="make-persons-in-mode"/>
    </xsl:template>

    <!-- 11/5/09: plos-specific template -->
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

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="person-group[@person-group-type='translator'] | person-group[@group-type='transed']" mode="book">
      <xsl:apply-templates />
      <xsl:text>, translator; </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <!-- if given names aren't all-caps, use book mode -->
    <xsl:template name="make-persons-in-mode">
      <xsl:apply-templates mode="book"/>
    </xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
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

    <!-- 11/5/09: viewnlm contains the remaining template in this section (choose-person-group-end-punct) -->

    <!-- ============================================================= -->
    <!--  56. Citation subparts (mode "none" separately at end)        -->
    <!-- ============================================================= -->

    <!-- 11/5/09: plos modifications (minor) -->
    <!-- names -->
    <xsl:template match="name" mode="nscitation">
      <xsl:value-of select="surname"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="given-names"/>
      <xsl:text>, </xsl:text>
    </xsl:template>

    <!-- 7/30/10: plos modifications (minor) -->
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
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="suffix"/>
          </xsl:if>
        </xsl:when>

        <!-- if no given-names -->
        <xsl:otherwise>
          <xsl:apply-templates select="surname"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>

        <!-- 6/3/10: added to fix spacing and punctuation in refs -->
        <xsl:when test="../following-sibling::collab">
          <xsl:text>, </xsl:text>
        </xsl:when>

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

            <!-- if not part of person-group with person-group-type -->
            <!-- 6/8/10: fixed punctuation for refs with no person-group-type -->
            <xsl:otherwise>
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
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!-- 1/27/10: plos modifications (minor) -->
    <!-- 6/3/10: fixed spacing and punctuation in refs -->
    <xsl:template match="collab" mode="book">
	    <xsl:apply-templates/>
        <xsl:choose>
		<xsl:when test="@collab-type='compilers'">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@collab-type"/>
		</xsl:when>
		<xsl:when test="@collab-type='assignee'">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@collab-type"/>
		</xsl:when>
        <xsl:when test="following-sibling::collab">
            <xsl:text>, </xsl:text>
        </xsl:when>
        </xsl:choose>
	</xsl:template>

    <!-- 11/5/09: viewnlm contains templates (etal mode book, aff mode book) -->

    <!-- publication info -->

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="article-title" mode="nscitation">
      <xsl:apply-templates/>
      <xsl:text> </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="article-title" mode="book">
      <xsl:apply-templates/>
      <xsl:if test="not(ends-with(normalize-space(),'.'))">
        <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="article-title" mode="editedbook">
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 6/15/10: plos modifications (minor) -->
    	<xsl:template match="article-title" mode="conf">
		<xsl:apply-templates/>
		<xsl:choose>
			<xsl:when test="../conf-name">
                <xsl:if test="not(ends-with(normalize-space(),'.'))">
				    <xsl:text>.</xsl:text>
                    </xsl:if>
               <xsl:text> </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>; </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="article-title" mode="inconf">
      <xsl:apply-templates/>
      <xsl:variable name="punc">
        <xsl:call-template name="endsWithPunctuation">
          <xsl:with-param name="value" select="."/>
        </xsl:call-template>
      </xsl:variable>
      <!-- fixed bug where the word false wasn't quoted -->
      <xsl:if test="$punc = 'false'">
        <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="publisher-loc">
      <xsl:apply-templates/>
      <xsl:text>: </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos-specific template --> 
    <xsl:template match="publisher-name">
      <xsl:apply-templates/>
      <xsl:text>.</xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="source" mode="nscitation">
      <xsl:apply-templates/>
      <xsl:variable name="punc">
        <xsl:call-template name="endsWithPunctuation">
          <xsl:with-param name="value" select="."/>
        </xsl:call-template>
      </xsl:variable>
      <!-- fixed bug where the word false wasn't quoted -->
      <xsl:if test="$punc = 'false'">
        <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:text> </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
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
          <!-- fixed bug where the word false wasn't quoted -->
          <xsl:if test="$punc='false'">
            <xsl:text>.</xsl:text>
          </xsl:if>
          <xsl:text> </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!-- 11/5/09: viewnlm contains templates (source mode conf, trans-source mode book) -->

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="volume" mode="nscitation">
      <xsl:text> </xsl:text><xsl:apply-templates/><xsl:text>: </xsl:text>
    </xsl:template>

    <!-- 4/23/10: plos modifications -->
    <!-- 6/2/10: fixed punctuation and spacing -->
    <xsl:template match="volume | edition" mode="book">
        <xsl:apply-templates/>
        <xsl:choose>
          <xsl:when test="@collab-type='compilers'">
            <xsl:text>, </xsl:text>
            <xsl:value-of select="@collab-type"/>
          </xsl:when>
          <xsl:when test="@collab-type='assignee'">
            <xsl:text>, </xsl:text>
            <xsl:value-of select="@collab-type"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>. </xsl:text>
          </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- 11/5/09: viewnlm contains template (month mode nscitation) -->    

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="day" mode="nscitation">
      <xsl:apply-templates/><xsl:text>. </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="year" mode="nscitation">
      <xsl:text> (</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>) </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
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

    <!-- 11/5/09: viewnlm contains templates (time-stamp mode nscitation, time-stamp mode book, access-date mode nscitation, access-date mode book, season mode book) -->

    <!-- 11/5/09: plos modifications (major) -->
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

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="page-count" mode="book">
      <xsl:value-of select="@count"/><xsl:text> p.</xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="fpage" mode="book">
      <xsl:if test="../lpage">
        <!-- fix old journal articles that were coded as type other, but actually
          had a volume, source and page numbers -->
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

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="lpage" mode="book">
      <xsl:if test="../fpage">
        <xsl:apply-templates/>
        <xsl:text>.</xsl:text>
      </xsl:if>
    </xsl:template>

    <!-- 11/5/09: viewnlm contains templates (lpage mode nscitation, pub-id mode nscitation, annotation mode nscitation, comment mode nscitation, conf-name|conf-date mode conf, conf-loc mode conf) -->

    <!-- 11/5/09: viewnlm 2.3 contains 2 templates (formatting elements for citations) -->

    <!-- ============================================================= -->
    <!--  "firstnames"                                                 -->
    <!-- ============================================================= -->

    <!-- 11/5/09: plos modifications (major) -->
    <!-- called by match="name" in book mode, as part of citation handling when given-names is not all-caps -->
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
      when there is no significant text node inside the ref. -->

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="name" mode="none">
      <xsl:value-of select="surname"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="given-names"/>
      <xsl:text>. </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="article-title" mode="none">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="volume" mode="none">
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
      <!-- bug fix: f there is an issue number, then don't print the colon and
        space following the volume # -->
      <xsl:if test="not(../issue)">
        <xsl:text>: </xsl:text>
      </xsl:if>
    </xsl:template>

    <!-- 11/5/09: viewnlm contains templates (edition mode none, supplement mode none) -->

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="issue" mode="none">
      <xsl:if test="not(starts-with(normalize-space(),'('))">
        <xsl:text>(</xsl:text>
      </xsl:if>
      <xsl:apply-templates/>
      <xsl:if test="not(ends-with(normalize-space(),')'))">
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:choose>
        <!-- 5/7/10: added test for elocation-id. -->
        <xsl:when test="../fpage or ../lpage or ../elocation-id">
          <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>.</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <!-- 5/7/10: plos-specific template. -->
    <xsl:template match="elocation-id" mode="none">
	   <xsl:apply-templates/>
       <xsl:text>. </xsl:text>
    </xsl:template>

    <!-- 7/20/10: plos modifications -->
    <xsl:template match="publisher-loc" mode="none">
		<xsl:apply-templates/>
        <xsl:choose>
            <xsl:when test="not(following-sibling::*)">
                <xsl:text>.</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>: </xsl:text>
            </xsl:otherwise>
        </xsl:choose>	
	</xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="publisher-name" mode="none">
      <xsl:apply-templates/>
      <xsl:text>. </xsl:text>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <xsl:template match="person-group" mode="none">
      <xsl:apply-templates select="node()" mode="book"/>
    </xsl:template>

    <!-- 6/3/10: plos modifications (major) -->
    <!-- 6/3/10: fixed spacing between multiple collab authors in references -->
    <xsl:template match="collab" mode="none">
		<xsl:apply-templates/>
        <xsl:choose>
		  <xsl:when test="@collab-type">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@collab-type"/>
		  </xsl:when>
          <xsl:when test="following-sibling::collab">
		    <xsl:text>, </xsl:text>
	      </xsl:when>
		  <xsl:otherwise>
            <xsl:text> </xsl:text>
		  </xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <!-- 11/5/09: plos modifications (minor) -->
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

    <!-- 11/5/09: viewnlm contains template (trans-title mode none) -->

    <!-- 11/5/09: plos modifications (major) -->
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

    <!-- 11/5/09: viewnlm contains template (day mode none) -->

    <!-- 11/5/09: plos modifications (major) -->
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

   <!-- 11/5/09: viewnlm contains templates (access-date mode none, season mode none) -->

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="comment" mode="citation">
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 11/5/09: plos modifications (major) -->
    <!-- 11/5/09: plos comment: BUG in the following fpage template for the def of hermano -->
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

    <!-- 11/5/09: plos modifications (minor) -->
    <xsl:template match="lpage" mode="none">
      <xsl:apply-templates/><xsl:text>.</xsl:text>
    </xsl:template>

    <!-- 11/5/09: viewnlm 2.3 contains template (lpage mode write) -->

    <!-- 11/5/09: viewnlm contains templates (gov mode none, patent mode none) -->

    <!-- 11/5/09: plos-specific template -->
    <xsl:template match="aff/label">
      <strong><xsl:apply-templates/></strong>
    </xsl:template>

    <!-- 5/25/10: plos modifications -->
    <xsl:template match="pub-id" mode="none">
      <xsl:apply-templates/>
    </xsl:template>

    <!-- 2/24/10: viewnlm 2.3 contains template (comment mode none) -->

    <!-- ============================================================= -->
    <!--  57. "CITATION-TAG-ENDS"                                      -->
    <!-- ============================================================= -->

    <!-- 11/6/09: plos modifications (major) -->
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
      <!-- only output a single comment tag that appears as the very last child of the
        citation. -->
      <xsl:variable name="x" select="child::*[position()=last()]"/>
      <xsl:if test="local-name($x)='comment' and not(starts-with($x,'p.')) and not(starts-with($x,'In:') and not(starts-with($x,'pp.')))">
        <xsl:text> </xsl:text><xsl:apply-templates select="$x"/>
      </xsl:if>
      <xsl:apply-templates select="annotation" mode="citation"/>
    </xsl:template>

    <!-- 11/6/09: plos-specific template -->
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

    <!-- 11/6/09: plos-specific template -->
    <xsl:template match="aml:annotated">
      <xsl:call-template name="createAnnotationSpan"/>
    </xsl:template>

    <!-- 11/6/09: plos-specific template -->
    <xsl:template name="topAnchor">
      <xsl:if test="string-length(normalize-space(.)) > 0">&#160;<a href="#top">Top</a></xsl:if>
    </xsl:template>

</xsl:stylesheet>