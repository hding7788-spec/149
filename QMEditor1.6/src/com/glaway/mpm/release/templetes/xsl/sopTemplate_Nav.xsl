<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:variable name="version" select="technics/QMFawTechnicsInfo/@version"/>
	<xsl:variable name="pplanNumber" select="technics/QMFawTechnicsInfo//@pplanNumber"/>
	<xsl:variable name="technicsNumber" select="technics/QMFawTechnicsInfo//@technicsNumber"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>标准操作规程(SOP)发布界面</title>
				<link rel="stylesheet" type="text/css" href="main.css" />
				<script src="jquery-1.7.2.min.js" type="text/javascript">&#160;</script>
				<script src="releasetools.js" type="text/javascript">&#160;</script>
				<script type="text/javascript" src="release_pv.js">&#160;</script>
				<script>
					$(function(){
						  setTreesHeight();
						$(window).resize(function() {
							setTreesHeight();
						});
						initATagClick();
					});

				</script>
			</head>
			<body>
				<div id="left">
					<div class="copy-news">
						<xsl:apply-templates select="technics/QMFawTechnicsInfo" />
					</div>
					<div class="trees" style="overflow-y:scroll">
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/steps" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/borrowTechnicss" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/SOP/SOPParameter" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/attachs" />
						<!-- PDF预览文件 -->
						<a href="PDFPreview.pdf" target="_BLANK">PDF预览文件</a><br/>

					</div>
					<xsl:if test="technics/QMFawTechnicsInfo//@name1 or technics/QMFawTechnicsInfo//@name2 or technics/QMFawTechnicsInfo//@name3 or technics/QMFawTechnicsInfo//@name4 or technics/QMFawTechnicsInfo//@name5">
						<div class="check-news">
							<xsl:apply-templates select="technics" />
						</div>
					</xsl:if>
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="technics/QMFawTechnicsInfo" >
		<fieldset>
			<legend>
				<h3>基本信息</h3>
			</legend>
			<table width="230" border="0" cellspacing="10" cellpadding="0">
				<tr>
					<td>文件编号：</td>
					<td>
						<xsl:value-of select="@SopNumber"/>
					</td>
				</tr>
				<tr>
					<td>文件名称：</td>
					<td>
						<xsl:value-of select="@technicsName"/>
					</td>
				</tr>
				<tr>
					<td>专业类别：</td>
					<td>
						<xsl:value-of select="@SpecializedType"/>
					</td>
				</tr>
				<tr>
					<td>工序名称：</td>
					<td>
						<xsl:value-of select="@ProceduceName"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<legend><h3>文件信息</h3></legend>
			<table width="230" border="0" cellspacing="10" cellpadding="0">
				<tr>
					<td>文件状态：</td>
					<td><xsl:value-of select="@lifecycle"/></td>
				</tr>
				<tr>
					<td>文件密级：</td>
					<td><xsl:value-of select="@SECRET"/></td>
				</tr>
				<tr>
					<td>编制：<xsl:value-of select="@SHEJI" /></td>
				</tr>
				<tr>
					<td>校对：<xsl:value-of select="@JIAODUI" /></td>
				</tr>
				<tr>
					<td>审核：<xsl:value-of select="@SHENHE" /></td>
				</tr>
				<tr>
					<td>标检：<xsl:value-of select="@BIAOSHEN" /></td>
				</tr>
				<tr>
					<td>批准：<xsl:value-of select="@PIZHUN" /></td>
				</tr>
			</table>
		</fieldset>
	</xsl:template>

	<xsl:template match="technics/QMFawTechnicsInfo/attachs" >
		<ul class="nav2">
			<li>
				<a target="fram	eDivId">
					SOP评审附件
				</a>
				<ul>
					<xsl:for-each select="PAttachInfo">
						<li>
							<a target="_blank">
								<xsl:attribute name="href"><xsl:value-of select="@absolutePath" /></xsl:attribute>
								<xsl:value-of select="@attachName" />.<xsl:value-of select="@attachType" />
							</a>
						</li>
					</xsl:for-each>
				</ul>
			</li>
		</ul>
	</xsl:template>


	<xsl:template match="technics/QMFawTechnicsInfo/steps" >
		<ul class="nav">
			<li>
				<a target="frameDivId" id="top_00">
					<xsl:attribute name="href">content.html#top_00</xsl:attribute>
					<xsl:value-of select="$pplanNumber" />_<xsl:value-of select="$version" />
				</a>
				<ul>
					<xsl:for-each select="QMProcedureInfo">
                        <xsl:variable name="gxStepNumber" select="@stepNumber"/>
						<li>
							<a target="frameDivId" class="nav_anchor">
								<xsl:attribute name="href">content.html#<xsl:value-of select="@stepNumber" /></xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="@GXJS" /></xsl:attribute>
								<xsl:attribute name="cortonaID"><xsl:value-of select="@cortonaID" /></xsl:attribute>
								<xsl:value-of select="@stepNumber" />_<xsl:value-of select="@stepName" />
							</a>
                            <ul>
                                <xsl:for-each select="paces/QMProcedureInfo">
                                    <li>
                                        <a target="frameDivId" class="nav_anchor">
                                            <xsl:attribute name="href">content.html#<xsl:value-of select="@paceId" /></xsl:attribute>
                                            <xsl:attribute name="title"><xsl:value-of select="@GXJS" /></xsl:attribute>
                                            <xsl:attribute name="cortonaID"><xsl:value-of select="@cortonaID" /></xsl:attribute>
                                            <xsl:value-of select="@stepNumber" />
                                        </a>
                                    </li>
                                </xsl:for-each>
                            </ul>
						</li>
					</xsl:for-each>
				</ul>
			</li>
		</ul>

	</xsl:template>


	<xsl:template match="technics/QMFawTechnicsInfo/borrowTechnicss">
		<ul class="nav">
			<li>
				<a target="frameDivId" class="nav_anchor">
					<xsl:attribute name="href">content.html#borrowTechnics</xsl:attribute>
					<xsl:attribute name="cortonaID">borrowTechnics</xsl:attribute>
						依据文件
				</a>
			</li>
		</ul>
	</xsl:template>

	<xsl:template match="technics/QMFawTechnicsInfo/SOP/SOPParameter">
		<ul class="nav">
			<li>
				<a target="frameDivId" class="nav_anchor">
					<xsl:attribute name="href">content.html#ParameterInfo</xsl:attribute>
					<xsl:attribute name="cortonaID">ParameterInfo</xsl:attribute>
						参数项目
				</a>
			</li>
		</ul>
	</xsl:template>


</xsl:stylesheet>