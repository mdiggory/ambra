<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"  xmlns:xlink="http://www.w3.org/1999/xlink"
 xmlns:util="http://dtd.nlm.nih.gov/xsl/util"  xmlns:mml="http://www.w3.org/1998/Math/MathML">

<xsl:output method="html" indent="no" encoding="UTF-8" omit-xml-declaration="yes"/>

<!-- 1/20/12: updated to 3.0 version (changed to assign-id)-->
<xsl:template name="assign-id">
  <xsl:if test="@id">
		<xsl:attribute name="id">
		  <xsl:value-of select="@id"/>
		</xsl:attribute>
	</xsl:if>
</xsl:template>

<!-- 1/20/12: deleted make-src, we don't use (updated 3.0 is assign-src, we don't use) -->

<!-- 1/20/12: updated to 3.0 version (changed to assign-href) -->
<xsl:template name="assign-href">
  <xsl:if test="@xlink:href">
		<xsl:attribute name="href">
		<xsl:value-of select="@xlink:href"/>
		  </xsl:attribute>
	</xsl:if>
</xsl:template>

<!-- 2/10/12: deleted make-email, we don't use (see also email match below) -->

<!-- 1/20/12: deleted capitalize, we don't use -->

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="bold">
  <strong>
	  <xsl:apply-templates/>
	</strong>
</xsl:template>

<!-- 1/20/12: deleted break, we don't use -->

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="email">
  <a href="mailto:{.}">
    <xsl:apply-templates/>
  </a>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="italic">
  <em>
		<xsl:apply-templates/>
	</em>
</xsl:template>

<!-- 1/20/12: no change necessary -->
<xsl:template match="monospace">
  <span class="monospace">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 1/20/12: no change necessary -->
<xsl:template match="overline">
  <span class="overline">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 2/9/12: keep old version -->
<xsl:template match="p">
	<p>
	  <xsl:apply-templates/>
	</p>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="sc">
  <span class="small-caps">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 1/20/12: deleted sc//text, we don't use -->

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="strike">
  <span class="strike">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="sub">
  <sub>
    <xsl:apply-templates/>
  </sub>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="sup">
  <sup>
    <xsl:apply-templates/>
  </sup>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="underline">
  <span class="underline">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 1/20/12: deleted abbrev, we don't use -->

<!-- 2/9/12: keep old version -->
<xsl:template match="inline-graphic">
  <xsl:element name="img">
    <xsl:if test="@xlink:href">
      <xsl:variable name="graphicDOI"><xsl:value-of select="@xlink:href"/></xsl:variable>
        <xsl:attribute name="src">
          <xsl:value-of select="concat('fetchObject.action?uri=',$graphicDOI,'&amp;representation=PNG')"/>
      </xsl:attribute>
      <xsl:attribute name="border">0</xsl:attribute>
    </xsl:if>
  </xsl:element>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="inline-formula">
  <span class="{local-name()}">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- 1/20/12: deleted inline-supplementary-material, we don't use -->

<!-- 1/20/12: deleted glyph-data, we don't use -->

<!-- 1/20/12: updated to 3.0 version (exclude uri, we don't use) -->
<xsl:template match="ext-link">
  <a>
    <xsl:call-template name="assign-href"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<!-- 1/20/12: updated to 3.0 version -->
<xsl:template match="named-content">
  <xsl:choose>
    <xsl:when test="@xlink:href">
      <a>
        <xsl:call-template name="assign-href"/>
        <xsl:call-template name="assign-id"/>
        <xsl:apply-templates/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <span>
        <xsl:attribute name="class"><xsl:value-of select="@content-type" /></xsl:attribute>
        <xsl:call-template name="assign-id"/>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- 2/9/12: keep old version -->
<xsl:template match="title">
  <xsl:apply-templates/>END_TITLE
</xsl:template>

<!-- 2/9/12: keep old version -->
<xsl:template match="text()">
  <xsl:value-of select="translate(., '&#x200A;&#8764;', ' ~') "/>
</xsl:template>

</xsl:stylesheet>
