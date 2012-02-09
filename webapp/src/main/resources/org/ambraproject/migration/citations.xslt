<?xml version="1.0" encoding="UTF-8"?>
<!--
  $HeadURL::                                                                                      $
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
<!DOCTYPE xsl:stylesheet [
    <!ENTITY xsd            "http://www.w3.org/2001/XMLSchema#" >
    <!ENTITY rdf            "http://www.w3.org/1999/02/22-rdf-syntax-ns#" >
    <!ENTITY dc             "http://purl.org/dc/elements/1.1/" >
    <!ENTITY dcterms        "http://purl.org/dc/terms/">
    <!ENTITY dctype         "http://purl.org/dc/dcmitype/">
    <!ENTITY oai_dc         "http://www.openarchives.org/OAI/2.0/oai_dc/">
    <!ENTITY bibtex         "http://purl.org/net/nknouf/ns/bibtex#">
    <!ENTITY prism          "http://prismstandard.org/namespaces/1.2/basic/">
    <!ENTITY foaf           "http://xmlns.com/foaf/0.1/">
    <!ENTITY nlmpub         "http://dtd.nlm.nih.gov/publishing/">
    <!ENTITY plos           "http://rdf.plos.org/RDF/">
    <!ENTITY plosct         "http://rdf.plos.org/RDF/citation/type#">
    <!ENTITY topaz          "http://rdf.topazproject.org/RDF/">
]>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:my="my:extract.citation.pmc#"
    exclude-result-prefixes="my">

  <!--
    - Culled out from pmc2obj.xslt used by ingest. Anything to do with
    - Representations and Secondary Objects have been removed. So is 
    - the knowledge about manifest.xml and zip entries. Everything
    - else should be identical.
    -->

  <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

  <xsl:param name="doi-url-prefix"   select="'http://dx.doi.org/'" as="xs:string"/>
  <xsl:param name="initial-state"    select="1" as="xs:integer"/>
  <xsl:param name="article-uri"      as="xs:string"/>

  <xsl:variable name="article"       select="/article" as="element(article)"/>
  <xsl:variable name="meta"          select="$article/front/article-meta"
      as="element(article-meta)"/>
  <xsl:variable name="jnl-meta"      select="$article/front/journal-meta"
      as="element(journal-meta)"/>
  <xsl:variable name="article-doi"   select="$meta/article-id[@pub-id-type = 'doi']"
      as="xs:string"/>

  <!-- generate the ObjectList -->
  <xsl:template match="/">
    <ObjectList logMessage="Re-processing of article '{$meta/title-group/article-title}'">
      <xsl:call-template name="main-obj"/>
    </ObjectList>
  </xsl:template>

  <!-- templates for the main (pmc) entry -->

  <!-- generate the Article object -->
  <xsl:template name="main-obj">
    <Article id="{$article-uri}">
      <id><xsl:value-of select="$article-uri"/></id>
      <articleType>&plos;articleType/<xsl:value-of select="$article/@article-type"/></articleType>
      <xsl:for-each select="$meta/article-categories/subj-group[@subj-group-type = 'heading']/subject">
        <articleType>&plos;articleType/<xsl:value-of select="encode-for-uri(.)"/></articleType>
      </xsl:for-each>

      <xsl:if test="$jnl-meta/issn[@pub-type = 'epub']">
        <eIssn><xsl:value-of select="$jnl-meta/issn[@pub-type = 'epub']"/></eIssn>
      </xsl:if>

      <dublinCore>
        <xsl:call-template name="main-dc"/>
      </dublinCore>

      <xsl:for-each 
          select="$meta/article-categories/subj-group[@subj-group-type = 'Discipline']/subject">
        <categories>
          <xsl:variable name="main-cat" as="xs:string"
              select="if (contains(., '/')) then substring-before(., '/') else ."/>
          <xsl:variable name="sub-cat" as="xs:string" select="substring-after(., '/')"/>

          <mainCategory><xsl:value-of select="$main-cat"/></mainCategory>
          <xsl:if test="$sub-cat">
            <subCategory><xsl:value-of select="$sub-cat"/></subCategory>
          </xsl:if>
        </categories>
      </xsl:for-each>

      <state><xsl:value-of select="$initial-state"/></state>

      <xsl:for-each select="$meta/related-article">
        <relatedArticles>
          <article><xsl:value-of select="@xlink:href"/></article>
          <relationType><xsl:value-of select="@related-article-type"/></relationType>
        </relatedArticles>
      </xsl:for-each>
    </Article>
  </xsl:template>

  <xsl:template name="main-dc">
    <identifier><xsl:value-of select="$article-uri"/></identifier>
    <title><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="$meta/title-group/article-title"/></xsl:call-template></title>
    <type>&dctype;Text</type>
    <format>text/xml</format>
    <language>en</language>
    <xsl:if test="$meta/pub-date">
      <date><xsl:value-of select="my:format-date(my:select-date($meta/pub-date))"/></date>
      <issued><xsl:value-of select="my:format-date(my:select-date($meta/pub-date))"/></issued>
      <available><xsl:value-of select="my:format-date(my:select-date($meta/pub-date))"/></available>
    </xsl:if>
    <xsl:if test="$meta/history/date[@date-type = 'received']">
      <submitted><xsl:value-of select="my:format-date($meta/history/date[@date-type = 'received'])"/></submitted>
    </xsl:if>
    <xsl:if test="$meta/history/date[@date-type = 'accepted']">
      <accepted><xsl:value-of select="my:format-date($meta/history/date[@date-type = 'accepted'])"/></accepted>
    </xsl:if>
    <xsl:for-each select="$meta/contrib-group/contrib[@contrib-type = 'author']">
      <creators><xsl:value-of select="my:format-contrib-name(.)"/></creators>
    </xsl:for-each>
    <xsl:for-each select="$meta/contrib-group/contrib[@contrib-type = 'contributor']">
      <contributors><xsl:value-of select="my:format-contrib-name(.)"/></contributors>
    </xsl:for-each>
    <xsl:for-each
        select="$meta/article-categories/subj-group[@subj-group-type = 'Discipline']/subject">
        <subjects><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="."/></xsl:call-template></subjects>
    </xsl:for-each>
    <xsl:if test="$meta/abstract">
      <description><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="my:select-abstract($meta/abstract)"/></xsl:call-template></description>
    </xsl:if>
    <xsl:if test="$jnl-meta/publisher/publisher-name">
      <publisher><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="$jnl-meta/publisher/publisher-name"/></xsl:call-template></publisher>
    </xsl:if>
    <xsl:if test="$meta/copyright-statement">
      <rights><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="$meta/copyright-statement"/></xsl:call-template></rights>
    </xsl:if>

    <conformsTo>&nlmpub;<xsl:value-of select="$article/@dtd-version"/>/journalpublishing.dtd</conformsTo>
    <xsl:if test="$meta/copyright-year">
      <copyrightYear><xsl:value-of select="$meta/copyright-year"/></copyrightYear>
    </xsl:if>

    <xsl:call-template name="gen-bib-cit"/>
    <xsl:call-template name="gen-ref"/>
    <xsl:call-template name="gen-lic"/>
  </xsl:template>

  <!-- templates for all secondary entries -->


  <!-- templates for various sub-parts -->

  <xsl:template name="gen-bib-cit">
    <bibliographicCitation>
      <xsl:variable name="pub-date" as="xs:string?"
          select="if ($meta/pub-date) then my:format-date(my:select-date($meta/pub-date)) else ()"/>
      <xsl:call-template name="gen-citation">
        <xsl:with-param name="type"
            select="xs:anyURI('&bibtex;Article')"/>
        <xsl:with-param name="key"
            select="()"/>
        <xsl:with-param name="year"
            select="if ($pub-date) then xs:integer(substring($pub-date, 1, 4)) else ()"/>
        <xsl:with-param name="dispYear"
            select="if ($pub-date) then substring($pub-date, 1, 4) else ()"/>
        <xsl:with-param name="month"
            select="if ($pub-date) then substring($pub-date, 6, 2) else ()"/>
        <xsl:with-param name="day"
            select="if ($pub-date) then substring($pub-date, 9, 2) else ()"/>
        <xsl:with-param name="volume"
            select="$meta/volume"/>
        <xsl:with-param name="volNum"
            select="xs:integer($meta/volume)"/>
        <xsl:with-param name="issue"
            select="$meta/issue"/>
        <xsl:with-param name="title"
            select="$meta/title-group/article-title"/>
        <xsl:with-param name="pub-loc"
            select="$jnl-meta/publisher/publisher-loc"/>
        <xsl:with-param name="pub-name"
            select="$jnl-meta/publisher/publisher-name"/>
        <xsl:with-param name="pages"
            select="if ($meta/counts/page-count) then concat('1-', $meta/counts/page-count/@count)
                    else ()"/>
        <xsl:with-param name="elocation-id"
            select="$meta/elocation-id"/>
        <xsl:with-param name="journal"
             select="if ($jnl-meta/journal-id[@journal-id-type='nlm-ta']) then
             $jnl-meta/journal-id[@journal-id-type='nlm-ta']
             else $jnl-meta/journal-title"/>
        <xsl:with-param name="note"
            select="$meta/author-notes/fn[1]"/>
        <xsl:with-param name="editors"
            select="$meta/contrib-group/contrib[@contrib-type = 'editor']/name"/>
        <xsl:with-param name="authors"
            select="$meta/contrib-group/contrib[@contrib-type = 'author']/name"/>
        <xsl:with-param name="collab-authors"
            select="$meta/contrib-group/contrib[@contrib-type = 'author']/collab"/>
        <xsl:with-param name="url"
            select="xs:anyURI(concat($doi-url-prefix, encode-for-uri($article-doi)))"/>
        <xsl:with-param name="doi"
            select="$article-doi"/>
        <xsl:with-param name="summary"
            select="if ($meta/abstract) then my:select-abstract($meta/abstract) else ()"/>
      </xsl:call-template>
    </bibliographicCitation>
  </xsl:template>

  <xsl:template name="gen-ref">
    <xsl:for-each select="$article/back/ref-list/ref">
      <references>
        <xsl:call-template name="gen-citation">
          <xsl:with-param name="type"
              select="if (citation/@citation-type) then my:map-cit-type(citation/@citation-type)
                      else ()"/>
          <xsl:with-param name="key"      select="label"/>
          <xsl:with-param name="year"     select="my:find-int(citation/year[1], 4)"/>
          <xsl:with-param name="dispYear" select="citation/year[1]"/>
          <xsl:with-param name="month"    select="citation/month[1]"/>
          <xsl:with-param name="day"      select="citation/day[1]"/>
          <xsl:with-param name="volume"   select="citation/volume[1]"/>
          <xsl:with-param name="volNum"   select="my:find-int(citation/volume[1], 1)"/>
          <xsl:with-param name="issue"    select="citation/issue[1]"/>
          <xsl:with-param name="title"
              select="if (citation/article-title) then citation/article-title[1]
                      else if (citation/source)   then citation/source[1]
                      else ()"/>
          <xsl:with-param name="pub-loc"  select="citation/publisher-loc[1]"/>
          <xsl:with-param name="pub-name" select="citation/publisher-name[1]"/>
          <!-- citation/fpage and citation/lpage seems to appear more than once.
               So this is a deviation from pmc2obj.xslt -->
          <xsl:with-param name="pages"
              select="if (citation/page-range) then citation/page-range[1]
                      else if (citation/lpage) then concat(citation/fpage[1], '-', citation/lpage[1])
                      else citation/fpage[1]"/>
          <xsl:with-param name="elocation-id" select="citation/fpage[1]"/>
          <xsl:with-param name="journal"
              select="if (citation/@citation-type = 'journal' or
                          citation/@citation-type = 'confproc')
                        then citation/source[1] else ()"/>
          <xsl:with-param name="note"     select="citation/comment[1]"/>
          <xsl:with-param name="editors"
              select="citation/person-group[@person-group-type = 'editor']/name"/>
          <xsl:with-param name="authors"
              select="citation/person-group[@person-group-type = 'author']/name"/>
          <xsl:with-param name="url"      select="citation/@xlink:role"/>
          <xsl:with-param name="summary"  select="()"/>
        </xsl:call-template>
      </references>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="gen-citation">
    <xsl:param name="type"         as="xs:anyURI?"/>
    <xsl:param name="key"          as="xs:string?"/>
    <xsl:param name="year"         as="xs:integer?"/>
    <xsl:param name="dispYear"     as="xs:string?"/>
    <xsl:param name="month"        as="xs:string?"/>
    <xsl:param name="day"          as="xs:string?"/>
    <xsl:param name="volume"       as="xs:string?"/>
    <xsl:param name="volNum"       as="xs:integer?"/>
    <xsl:param name="issue"        as="xs:string?"/>
    <xsl:param name="title"/>
    <xsl:param name="pub-loc"      as="xs:string?"/>
    <xsl:param name="pub-name"     as="xs:string?"/>
    <xsl:param name="pages"        as="xs:string?"/>
    <xsl:param name="elocation-id" as="xs:string?"/>
    <xsl:param name="journal"      as="xs:string?"/>
    <xsl:param name="note"         as="xs:string?"/>
    <xsl:param name="editors"      as="element(name)*"/>
    <xsl:param name="authors"      as="element(name)*"/>
    <xsl:param name="collab-authors" as="element(collab)*"/>
    <xsl:param name="url"          as="xs:anyURI?"/>
    <xsl:param name="doi"          as="xs:string?"/>
    <xsl:param name="summary"/>

    <xsl:if test="$type">
      <citationType><xsl:value-of select="$type"/></citationType>
    </xsl:if>

    <xsl:if test="$key">
      <key><xsl:value-of select="$key"/></key>
    </xsl:if>

    <xsl:if test="$year">
      <year><xsl:value-of select="$year"/></year>
    </xsl:if>
    <xsl:if test="$dispYear">
      <displayYear><xsl:value-of select="$dispYear"/></displayYear>
    </xsl:if>
    <xsl:if test="$month">
      <month><xsl:value-of select="$month"/></month>
    </xsl:if>
    <xsl:if test="$day">
      <day><xsl:value-of select="$day"/></day>
    </xsl:if>
    <xsl:if test="$volume">
      <volume><xsl:value-of select="$volume"/></volume>
    </xsl:if>
    <xsl:if test="$volNum">
      <!--  Hacked to get around integer out of range exception -->
      <!-- 
      <volumeNumber><xsl:value-of select="$volNum"/></volumeNumber>
       -->
      <volumeNumber><xsl:value-of select="0"/></volumeNumber>
    </xsl:if>
    <xsl:if test="$issue">
      <issue><xsl:value-of select="$issue"/></issue>
    </xsl:if>

    <xsl:if test="$title">
      <title><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="$title"/></xsl:call-template></title>
    </xsl:if>

    <xsl:if test="$pub-loc">
      <publisherLocation><xsl:value-of select="$pub-loc"/></publisherLocation>
    </xsl:if>
    <xsl:if test="$pub-name">
      <publisherName><xsl:value-of select="$pub-name"/></publisherName>
    </xsl:if>
    <xsl:if test="$pages">
      <pages><xsl:value-of select="$pages"/></pages>
    </xsl:if>
    <xsl:if test="$elocation-id">
      <eLocationId><xsl:value-of select="$elocation-id"/></eLocationId>
    </xsl:if>
    <xsl:if test="$journal">
      <journal><xsl:value-of select="$journal"/></journal>
    </xsl:if>
    <xsl:if test="$note">
      <note><xsl:value-of select="$note"/></note>
    </xsl:if>
    <xsl:if test="$summary/node()">
      <summary><xsl:call-template name="xml-to-str"><xsl:with-param name="xml" select="$summary"/></xsl:call-template></summary>
    </xsl:if>
    <xsl:if test="$url">
      <url><xsl:value-of select="$url"/></url>
    </xsl:if>
    <xsl:if test="$doi">
      <doi><xsl:value-of select="$doi"/></doi>
    </xsl:if>

    <xsl:for-each select="$editors">
      <editors>
        <xsl:call-template name="gen-user"/>
      </editors>
    </xsl:for-each>

    <xsl:for-each select="$authors">
      <authors>
        <xsl:call-template name="gen-user"/>
      </authors>
    </xsl:for-each>

    <xsl:for-each select="$collab-authors">
      <collaborativeAuthors><xsl:value-of select="."/></collaborativeAuthors>
    </xsl:for-each>

  </xsl:template>

  <xsl:template name="gen-lic">
    <!-- XXX: License either needs to be non-abstract or we need a subclass.
              Also, lic should not be hardcoded!
    <license>
      <id>http://creativecommons.org/licenses/by-sa/3.0/</id>
    </license>
    -->
  </xsl:template>

  <!-- generate a minimal user entry -->
  <xsl:template name="gen-user">
    <realName><xsl:value-of select="my:format-name(.)"/></realName>
    <xsl:if test="given-names">
      <givenNames><xsl:value-of select="given-names"/></givenNames>
    </xsl:if>
    <xsl:if test="surname">
      <surnames><xsl:value-of select="surname"/></surnames>
    </xsl:if>
    <xsl:if test="suffix">
      <suffix><xsl:value-of select="suffix"/></suffix>
    </xsl:if>
  </xsl:template>

  <!-- Helper functions -->


  <!-- pmc structured name to simple string (for dc:creator etc) -->
  <xsl:function name="my:format-contrib-name" as="xs:string">
    <xsl:param name="contrib" as="element(contrib)"/>

    <xsl:choose>
      <xsl:when test="$contrib/name">
        <xsl:value-of select="my:format-name($contrib/name)"/>
      </xsl:when>

      <xsl:when test="$contrib/collab">
        <xsl:value-of select="$contrib/collab"/>
      </xsl:when>

      <xsl:when test="$contrib/string-name">
        <xsl:value-of select="$contrib/string-name"/>
      </xsl:when>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="my:format-name" as="xs:string">
    <xsl:param name="name" as="element(name)"/>
    <xsl:value-of select="
      if ($name/@name-style = 'eastern') then
        string-join(($name/surname, $name/given-names, $name/suffix), ' ')
      else
        string-join(($name/given-names, $name/surname, $name/suffix), ' ')
      "/>
  </xsl:function>

  <!-- Select the date to use for dc:date. The order of preference is:
     - 'epub', 'epub-ppub', 'ppub', 'ecorrected', 'pcorrected', no-type, first -->
  <xsl:function name="my:select-date" as="element(pub-date)">
    <xsl:param name="date" as="element(pub-date)+"/>

    <xsl:variable name="pref-date" select="(
      for $t in ('epub', 'epub-ppub', 'ppub', 'ecorrected', 'pcorrected')
        return $date[@pub-type = $t]
      )[1]"/>

    <xsl:sequence select="
      if ($pref-date) then $pref-date
      else if ($date[not(@pub-type)]) then $date[not(@pub-type)]
      else $date[1]
      "/>
  </xsl:function>

  <!-- pmc structured date to ISO-8601 (YYYY-MM-DD); seasons results in first day of the season,
     - or Jan 1st in the case of winter (to get the year right); missing fields are defaulted
     - from the current time -->
  <xsl:function name="my:format-date" as="xs:string">
    <xsl:param name="date" as="element()"/>

    <xsl:variable name="year" as="xs:integer" select="
        if ($date/year) then $date/year else year-from-date(current-date())"/>

    <xsl:value-of select="concat(
      $year,
      '-',
      if ($date/season) then
        if (lower-case($date/season) = 'spring') then '03-21'
        else if (lower-case($date/season) = 'summer') then '06-21'
        else if (lower-case($date/season) = 'fall') then '09-23'
        else if (lower-case($date/season) = 'winter') then '01-01'
        else ''
      else
        concat(
          my:twochar(if ($date/month) then $date/month
                     else if ($year != year-from-date(current-date())) then 1
                     else month-from-date(current-date())),
          '-',
          my:twochar(if ($date/day) then $date/day
                     else if ($year = year-from-date(current-date()) and
                              $date/month and $date/month = month-from-date(current-date())) then
                         day-from-date(current-date())
                     else 1)
        )
      , ' 00:00:00 UTC')"/>
  </xsl:function>

  <xsl:function name="my:twochar" as="xs:string">
    <xsl:param    name="str" as="xs:integer"/>
    <xsl:variable name="s" select="xs:string($str)"/>
    <xsl:value-of select="
        if (string-length($s) = 1) then concat('0', $s) else $s
      "/>
  </xsl:function>

  <!-- Select the abstract to use for dc:description. The order of preference is:
     - 'short', 'web-summary', 'toc', 'summary', 'ASCII', no-type, first -->
  <xsl:function name="my:select-abstract" as="element(abstract)">
    <xsl:param name="abstracts" as="element(abstract)+"/>

    <xsl:variable name="pref-abstract" select="(
      for $t in ('short', 'web-summary', 'toc', 'summary', 'ASCII')
        return $abstracts[@abstract-type = $t]
      )[1]"/>

    <xsl:sequence select="
      if ($pref-abstract) then $pref-abstract
      else if ($abstracts[not(@abstract-type)]) then $abstracts[not(@abstract-type)]
      else $abstracts[1]
      "/>
  </xsl:function>

  <!-- NLM citation-type to (bibtex or PLoS) URI mapping -->
  <xsl:function name="my:map-cit-type" as="xs:anyURI">
    <xsl:param name="cit-type" as="xs:string"/>
    <xsl:variable name="uri" as="xs:string" select="
      if      ($cit-type = 'book')       then '&bibtex;Book'
      else if ($cit-type = 'commun')     then '&plosct;Informal'
      else if ($cit-type = 'confproc')   then '&bibtex;Conference'
      else if ($cit-type = 'discussion') then '&plosct;Discussion'
      else if ($cit-type = 'gov')        then '&plosct;Government'
      else if ($cit-type = 'journal')    then '&bibtex;Article'
      else if ($cit-type = 'list')       then '&plosct;List'
      else if ($cit-type = 'other')      then '&bibtex;Misc'
      else if ($cit-type = 'patent')     then '&plosct;Patent'
      else if ($cit-type = 'thesis')     then '&plosct;Thesis'
      else if ($cit-type = 'web')        then '&plosct;Web'
      else '&bibtex;Misc'
      "/>
    <xsl:sequence select="xs:anyURI($uri)"/>
  </xsl:function>

  <!-- Find the first integer with given minimal length in the string -->
  <xsl:function name="my:find-int" as="xs:integer?">
    <xsl:param name="str" as="xs:string?"/>
    <xsl:param name="min" as="xs:integer"/>
    <!-- this should simply be replace($str, '(.*?(\d{$min,}))?.*', '$2', 's') but that is not
       - allowed in xpath because the regexp can match the zero-length string. So we have to do
       - this in two steps. -->
    <xsl:variable name="tmp" as="xs:string"
        select="replace($str, concat('.*?(\d{', $min, ',})'), '$1', 's')"/>
    <xsl:variable name="num" as="xs:string"
        select="replace($tmp, '\D.*', '', 's')"/>
    <xsl:sequence select="if ($num) then xs:integer($num) else ()"/>
  </xsl:function>

  <!-- serialize an xml string -->
  <xsl:template name="xml-to-str">
    <xsl:param name="xml"/>
    <xsl:apply-templates mode="serialize" select="$xml/node()"/>
  </xsl:template>

  <xsl:template match="*" mode="serialize">
    <xsl:text/>&lt;<xsl:value-of select="name()"/>
    <xsl:variable name="attr-ns-uris" as="xs:anyURI*"
        select="for $attr in (@*) return namespace-uri($attr)"/>
    <xsl:for-each select="namespace::*[name() != 'xml']">
      <xsl:if test=". = namespace-uri(..) or . = $attr-ns-uris">
        <xsl:text> xmlns</xsl:text>
        <xsl:if test="name()">
          <xsl:text />:<xsl:value-of select="name()" />
        </xsl:if>
        <xsl:value-of select="concat('=&quot;', ., '&quot;')"/>
      </xsl:if>
    </xsl:for-each>
    <xsl:for-each select="@*">
      <xsl:value-of select="concat(' ', name(), '=&quot;', my:xml-escape(.), '&quot;')"/>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test="node()">
        <xsl:text>></xsl:text>
        <xsl:apply-templates select="node()" mode="serialize"/>
        <xsl:text/>&lt;/<xsl:value-of select="name()"/><xsl:text>></xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>/></xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()" mode="serialize">
    <xsl:value-of select="my:xml-escape(.)"/>
  </xsl:template>

  <xsl:function name="my:xml-escape" as="xs:string">
    <xsl:param name="str" as="xs:string"/>
    <xsl:value-of select="replace(replace(replace($str, '&amp;', '&amp;amp;'), '&lt;', '&amp;lt;'), '&gt;', '&amp;gt;')"/>
  </xsl:function>
</xsl:stylesheet>
