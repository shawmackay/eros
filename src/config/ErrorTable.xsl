<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"></xsl:output>
    <!-- template rule matching source root element -->
    <xsl:template match="/">
	<ROWSET>
            <ROW num="1">
                <xsl:element name="ERST_ID"/>
                <xsl:apply-templates/>
            </ROW>
        </ROWSET>
    </xsl:template>

    <xsl:template match="DATE">
        <xsl:element name="TIME_STAMP">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="MESSAGE">
        <xsl:element name="MESSAGE">
	        <xsl:choose>
		        <xsl:when test="string-length(.) &gt; 500">
	                <xsl:value-of select="substring(.,1,500)"/>
		        </xsl:when>
		        <xsl:otherwise>
    		        <xsl:value-of select="."/>
	    	    </xsl:otherwise>
	        </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template match="STACKTRACE">
        <xsl:element name="STACKTRACE">
	        <xsl:choose>
		        <xsl:when test="string-length(.) &gt; 1000">
	                <xsl:value-of select="substring(.,1,1000)"/>
		        </xsl:when>
		        <xsl:otherwise>
		            <xsl:value-of select="."/>
		        </xsl:otherwise>
	        </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template match="APPLICATION">
        <xsl:element name="APPLICATION">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ARGUMENTS">
	    <xsl:element name="ARGUMENTS">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="HOSTNAME">
        <xsl:element name="HOST">
            <xsl:value-of select="/LOGGINGDETAIL/HOSTNAME"/>
            <xsl:text>$</xsl:text>
            <xsl:value-of select="/LOGGINGDETAIL/HOSTADDRESS"/>
            <xsl:text>$</xsl:text>
            <xsl:value-of select="/LOGGINGDETAIL/USERNAME"/>
            <xsl:text>$</xsl:text>
            <xsl:value-of select="/LOGGINGDETAIL/OPERATINGSYSTEM"/>
            <xsl:text>$</xsl:text>
            <xsl:value-of select="/LOGGINGDETAIL/JAVAVERSION"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="HOSTADDRESS">
    </xsl:template>

    <xsl:template match="USERNAME">
    </xsl:template>

    <xsl:template match="OPERATINGSYSTEM">
    </xsl:template>

    <xsl:template match="JAVAVERSION">
    </xsl:template>

    <xsl:template match="GROUPS">
        <xsl:element name="GROUPS">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="LEVEL">
        <xsl:element name="ERROR_LEVEL">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="CODE">
    </xsl:template>

</xsl:stylesheet>

