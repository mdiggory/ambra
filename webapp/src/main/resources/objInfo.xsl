<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"  xmlns:xlink="http://www.w3.org/1999/xlink"
 xmlns:util="http://dtd.nlm.nih.gov/xsl/util"  xmlns:mml="http://www.w3.org/1998/Math/MathML">

<xsl:output method="html" indent="no" encoding="UTF-8" omit-xml-declaration="yes"/>

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

<xsl:template name="capitalize">
  <xsl:param name="str"/>
  <xsl:value-of select="translate($str,
                          'abcdefghjiklmnopqrstuvwxyz',
                          'ABCDEFGHJIKLMNOPQRSTUVWXYZ')"/>
</xsl:template>

<xsl:template match="bold">
  <b><xsl:apply-templates/></b>
</xsl:template>

<xsl:template match="break">
  <br/>
</xsl:template>

<xsl:template match="email">
  <a>
    <xsl:attribute name="href">mailto:<xsl:apply-templates/></xsl:attribute>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="italic">
  <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match="monospace">
  <span class="monospace">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="overline">
  <span class="overline">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="p">
  <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="sc">
  <!-- handle any tags as usual, until
       we're down to the text strings -->
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

<xsl:template match="sub">
  <sub><xsl:apply-templates/></sub>
</xsl:template>

<xsl:template match="sup">
  <sup><xsl:apply-templates/></sup>
</xsl:template>

<xsl:template match="underline">
  <u><xsl:apply-templates/></u>
</xsl:template>


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
    <xsl:attribute name="src"><xsl:value-of select="concat('fetchObject.action?uri=',$graphicDOI,'&amp;representation=PNG')"/></xsl:attribute>
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
  <a>
    <xsl:call-template name="make-href"/>
    <xsl:call-template name="make-id"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="glyph-data">
  <span class="take-note">
    <xsl:call-template name="make-id"/>
    <xsl:text>[glyph data here: ID=</xsl:text>
    <xsl:value-of select="@id"/>
    <xsl:text>]</xsl:text>
  </span>
</xsl:template>


<xsl:template match="ext-link | uri">
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

<!-- xref for fn, table-fn, or bibr becomes a superior number -->
<xsl:template match="xref[@ref-type='fn']">
  <span class="xref">
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

<xsl:template match="xref[@ref-type='table-fn']">
  <span class="xref">
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


<xsl:template match="title">
  <xsl:apply-templates/>END_TITLE
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="translate(., '&#x200A;&#8764;', ' ~') "/>
</xsl:template>

</xsl:stylesheet>
