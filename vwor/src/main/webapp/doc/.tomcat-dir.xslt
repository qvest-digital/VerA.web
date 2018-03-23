<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">
  <xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" indent="no"/>
  <xsl:template name="basename">
    <xsl:param name="arg"/>
    <xsl:choose>
      <xsl:when test="contains($arg, '/')">
        <xsl:call-template name="basename">
          <xsl:with-param name="arg" select="substring-after($arg, '/')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$arg"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="listing">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
      <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>VerA.web ${project.version} documentation index</title>
      </head>
      <body>
        <h1>VerA.web ${project.version} docs</h1>
        <ul>
          <li>
            <a href="../">&#x2BAA; Back to VerA.web</a>
          </li>
          <xsl:apply-templates select="entries"/>
        </ul>
        <address>Apache Tomcat</address>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="entries">
    <xsl:apply-templates select="entry">
      <xsl:sort select="@urlPath"/>
    </xsl:apply-templates>
  </xsl:template>
  <xsl:template match="entry">
    <xsl:variable name="basePath">
      <xsl:call-template name="basename">
        <xsl:with-param name="arg" select="@urlPath"/>
      </xsl:call-template>
    </xsl:variable>
    <li><a href="{$basePath}"><xsl:value-of select="$basePath"/></a></li>
  </xsl:template>
</xsl:stylesheet>
