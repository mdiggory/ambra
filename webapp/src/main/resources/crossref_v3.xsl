<?xml version="1.0" encoding="UTF-8"?>
<!--
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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="no"/>
  <xsl:param name="plosDoiUrl"/>
  <xsl:param name="plosEmail"/>
  <xsl:template match="/">
    <xsl:variable name="currentDateTime" select="current-dateTime()"/>
    <xsl:variable name="timestamp" select="format-dateTime($currentDateTime, '[Y0001][M01][D01][H01][m01][s01]')"/>
    <xsl:variable name="article_doi" select="//article-id[@pub-id-type='doi'][1]"/>
    <doi_batch version="4.3.0" xmlns="http://www.crossref.org/schema/4.3.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.crossref.org/schema/4.3.0 http://www.crossref.org/schema/deposit/crossref4.3.0.xsd">
      <head>
        <doi_batch_id>
          <xsl:value-of select="$article_doi"/>
        </doi_batch_id>
        <timestamp>
          <xsl:value-of select="$timestamp"/>
        </timestamp>
        <depositor>
	        <name>Public Library of Science</name>
          <email_address><xsl:value-of select="$plosEmail"/></email_address>
        </depositor>
        <registrant>Public Library of Science</registrant>
      </head>
      <body>
        <journal>
	        <journal_metadata language="en">
            <full_title>
	            <xsl:value-of select="//journal-title[1]"/>
            </full_title>
            <abbrev_title>
              <xsl:value-of select="//journal-id[@journal-id-type='nlm-ta'][1]"/>
	          </abbrev_title>
	          <issn media_type="electronic">
	            <xsl:value-of select="//issn[@pub-type='epub'][1]"/>
	          </issn>
	        </journal_metadata>
	        <journal_issue>
	          <publication_date media_type="online">
	            <month>
		            <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/month"/>
	            </month>
	            <day>
		            <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/day"/>
	            </day>
	            <year>
		            <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/year"/>
	            </year>
	          </publication_date>
	          <journal_volume>
	            <volume>
		            <xsl:value-of select="article/front/article-meta/volume"/>
	            </volume>
	          </journal_volume>
	          <issue>
	            <xsl:value-of select="article/front/article-meta/issue"/>
	          </issue>
	        </journal_issue>
	        <journal_article publication_type="full_text">
	          <titles>
	            <title>
		            <xsl:value-of select="article/front/article-meta/title-group/article-title"/>
	            </title>
	          </titles>
            <xsl:if test="article/front/article-meta/contrib-group">
	            <contributors>
		            <xsl:for-each select="article/front/article-meta/contrib-group/contrib[@contrib-type='author']">
		              <xsl:choose>
		                <xsl:when test="collab">
                      <organization contributor_role="author">
                        <xsl:choose>
			                    <xsl:when test="position() = 1">
			                      <xsl:attribute name="sequence">first</xsl:attribute>
			                    </xsl:when>
			                    <xsl:otherwise>
			                      <xsl:attribute name="sequence">additional</xsl:attribute>
			                    </xsl:otherwise>
			                  </xsl:choose>
                        <!-- 2/29/12: updated for 3.0 -->
			                  <xsl:apply-templates select="collab"/>
                        <xsl:call-template name="collabContribs"/>
                      </organization>
		                </xsl:when>
		                <xsl:otherwise>
		                  <person_name contributor_role="author">
                        <xsl:choose>
                          <xsl:when test="position() = 1">
                            <xsl:attribute name="sequence">first</xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:attribute name="sequence">additional</xsl:attribute>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="name/given-names">
                          <given_name><xsl:value-of select="name/given-names"/></given_name>
                        </xsl:if>
                        <surname><xsl:value-of select="name/surname"/></surname>
		                  </person_name>
		                </xsl:otherwise>
		              </xsl:choose>
                </xsl:for-each>
		            <xsl:for-each select="article/front/article-meta/contrib-group/contrib[@contrib-type='editor']">
                  <xsl:choose>
                    <xsl:when test="collab">
                      <organization contributor_role="editor">
                        <xsl:choose>
                          <xsl:when test="position() = 1">
                            <xsl:attribute name="sequence">first</xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:attribute name="sequence">additional</xsl:attribute>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:value-of select="collab"/>
                      </organization>
                    </xsl:when>
                    <xsl:otherwise>
                      <person_name contributor_role="editor">
                        <xsl:choose>
                          <xsl:when test="position() = 1">
                            <xsl:attribute name="sequence">first</xsl:attribute>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:attribute name="sequence">additional</xsl:attribute>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="name/given-names">
                          <given_name><xsl:value-of select="name/given-names"/></given_name>
                        </xsl:if>
                        <surname><xsl:value-of select="name/surname"/></surname>
                      </person_name>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:for-each>
	            </contributors>
	          </xsl:if>
	          <publication_date media_type="online">
	            <month>
                <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/month"/>
	            </month>
	            <day>
		            <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/day"/>
	            </day>
	            <year>
		            <xsl:value-of select="article/front/article-meta/pub-date[@pub-type='epub']/year"/>
	            </year>
	          </publication_date>
	          <pages>
	            <first_page><xsl:value-of select="article/front/article-meta/elocation-id"/></first_page>
	          </pages>
	          <publisher_item>
	            <item_number><xsl:value-of select="$article_doi"/></item_number>
	          </publisher_item>
	          <doi_data>
	            <doi><xsl:value-of select="$article_doi"/></doi>
	            <timestamp> <xsl:value-of select="$timestamp"/></timestamp>
	            <resource><xsl:value-of select="$plosDoiUrl"/><xsl:value-of select="$article_doi"/></resource>
	          </doi_data>
	          <component_list>
	            <xsl:for-each select="//fig">
                <xsl:if test="object-id[@pub-id-type='doi']">
		              <component parent_relation="isPartOf">
		                <description><xsl:for-each select="label"/></description>
		                <doi_data>
		                  <doi><xsl:value-of select="object-id[@pub-id-type='doi']"/></doi>
		                  <resource><xsl:value-of select="$plosDoiUrl"/><xsl:value-of select="object-id[@pub-id-type='doi']"/></resource>
		                </doi_data>
		              </component>
                </xsl:if>
	            </xsl:for-each>
	            <xsl:for-each select="//table-wrap[@id]">
                <xsl:if test="object-id[@pub-id-type='doi']">
		              <component parent_relation="isPartOf">
		                <description><xsl:for-each select="label"/></description>
		                  <doi_data>
		                    <doi><xsl:value-of select="object-id[@pub-id-type='doi']"/></doi>
		                    <resource><xsl:value-of select="$plosDoiUrl"/><xsl:value-of select="object-id[@pub-id-type='doi']"/></resource>
		                  </doi_data>
		                </component>
                </xsl:if>
	            </xsl:for-each>
	          </component_list>
	        </journal_article>
	      </journal>
      </body>
    </doi_batch>
  </xsl:template>
  <!-- 2/29/12: added to suppress contribs within collab -->
  <xsl:template name="collabContribs" match="//collab/contrib-group"/>
</xsl:stylesheet>
