<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="/Report">
		<!-- Ignore the report's name to prevent renaming-related run failures -->
		<!-- Select the report name attribute -->
		<!-- <xsl:apply-templates select="@Name"/> -->

		<!-- Select all report attributes -->
		<!-- <xsl:apply-templates select="@*"/> -->

		<!-- Select the first and last checkpoint -->
		<!-- <xsl:apply-templates select="Checkpoint[1]"/> -->
		<!-- <xsl:apply-templates select="Checkpoint[last()]"/> -->

		<!-- Select all checkpoints -->
		<xsl:apply-templates select="node()"/>

		<!-- Select the checkpoint with name "Pipe Example" -->
		<!-- <xsl:apply-templates select="Checkpoint[@Name='Pipe Example']"/> -->
	</xsl:template>


	<!-- Ignore content of checkpoint referentienummer that contains a UUID -->
	<xsl:template match="*[local-name()='Checkpoint' and @Name='referentienummer' and @Type='Infopoint' and @Level='1']">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:choose>
				<xsl:when test="string-length(.) > 10"><xsl:value-of select="'IGNORED'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="concat(current-dateTime(), ' Wrong or empty referentienummer ''', ., '''')"/></xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

	<!-- Ignore content of element referentienummer that contains a UUID -->
	<xsl:template match="*[local-name()='referentienummer']">
		<xsl:element namespace="{namespace-uri()}" name="{name()}">
			<xsl:choose>
				<xsl:when test="string-length(.) > 10"><xsl:value-of select="'IGNORED'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="concat(current-dateTime(), ' Wrong or empty referentienummer ''', ., '''')"/></xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<!-- Ignore content of element identificatie that contains an id -->
	<xsl:template match="*[local-name()='identificatie']">
		<xsl:element namespace="{namespace-uri()}" name="{name()}">
			<xsl:value-of select="substring(., 1, 2)"/>
			<xsl:value-of select="'IGNORED'"/>
		</xsl:element>
	</xsl:template>

	<!-- Ignore content of element tijdstipBericht that contains a timestamp -->
	<xsl:template match="*[local-name()='tijdstipBericht']">
		<xsl:element namespace="{namespace-uri()}" name="{name()}">
			<xsl:value-of select="substring(., 1, 2)"/>
			<xsl:value-of select="'IGNORED'"/>
			<xsl:value-of select="string-length(.) - 2"/>
			<xsl:value-of select="'CHARS'"/>
		</xsl:element>
	</xsl:template>

	<!-- Ignore hostname in checkpoint url that will change when switching backend between fieldlab and local -->
	<xsl:template match="*[local-name()='Checkpoint' and @Name='url' and @Type='Inputpoint' and @Level='2']">
		<Checkpoint Name="url" Type="Inputpoint" Level="2">
			<xsl:value-of select="replace(replace(replace(., 'fieldlab.westeurope.cloudapp.azure.com', 'IGNORED'), 'fieldlab.westeurope.cloudapp.azure.com', 'IGNORED'), 'openzaak.local', 'IGNORED')"/>
		</Checkpoint>
	</xsl:template>

	<!-- General template -->
	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
