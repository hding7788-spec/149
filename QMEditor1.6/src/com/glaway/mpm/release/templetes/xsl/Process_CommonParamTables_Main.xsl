<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:variable name="wrlFile" select="technics/QMFawTechnicsInfo//@wrlFile"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>质量记录表</title>
				<link rel="stylesheet" type="text/css" href="default.css" />
				<meta http-equiv="Cache-Control" content="no-store"></meta>
				<meta http-equiv="Expires" content="-1"></meta>
				<script type="text/javascript" src="jquery-1.7.2.min.js">&#160;</script>
				<script type="text/javascript" src="pv/pvUtils.js">&#160;</script>
				<script type="text/javascript" src="pv/pvlaunch.js">&#160;</script>
				<script type="text/javascript" src="releasetools.js">&#160;</script>
				<script type="text/javascript" src="release_pv.js">&#160;</script>
				<script type="text/javascript" src="pv/procedure_api.js">&#160;</script>
				<script type="text/javascript" src="pv/generic_prc.js">&#160;</script>
				<script language="javascript">
			$(function(){
				<xsl:if test="$wrlFile != ''">
					propSceneName = '<xsl:value-of select='$wrlFile'/>'+'.wrl';
					on_load();
				</xsl:if>
				valTdData();
				setProcessImg();
				imgSrcValData();
				initPageContent();
				showContentByDivName("top_00");
				replaseProcessContent();
			});
			<xsl:if test="$wrlFile != ''">
				$(window).unload(function(){on_unload();});
			</xsl:if>

		</script>
			</head>
			<body>
			<div id="newContent"><span></span></div>
			<div class="title" id="share"></div>
				<xsl:apply-templates select="technics/QMFawTechnicsInfo/steps"/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="technics/QMFawTechnicsInfo/steps">
	<xsl:for-each select="QMProcedureInfo">
	<xsl:variable name="gxStepNumber" select="@stepNumber"/>
	<div class="sh_div" style="visibility: hidden;">
	<xsl:attribute name="name"><xsl:value-of select="@stepNumber"/></xsl:attribute>
	<div  style="overflow:auto">
	<fieldset>
  		<legend><h3 style="font-family:Simsun">质量记录表</h3></legend>
		<table width="708" style="white-space:nowrap;font-family:Simsun;">
		<xsl:for-each select="commonParamTables/parameterTable/parameter/values[@number = '0']">
			<tr>
				<xsl:for-each select="value">
					<xsl:if test="@isShow = 'true'">
						<td width="90" style="text-align:center" class="cailiao-title-bg"><xsl:value-of select="@columnName"/></td>
					</xsl:if>
				</xsl:for-each>
			</tr>
			</xsl:for-each>
			<xsl:for-each select="commonParamTables/parameterTable/parameter/values">
				<tr>
					<xsl:for-each select="value">
						<xsl:if test="@isShow = 'true'">
							<td class="procedureContent"><xsl:value-of select="attribute"/></td>
						</xsl:if>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
		</fieldset>
		</div>
		</div>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>