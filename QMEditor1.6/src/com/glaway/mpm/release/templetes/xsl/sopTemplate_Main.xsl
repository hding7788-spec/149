<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="wrlFile" select="technics/QMFawTechnicsInfo//@wrlFile" />
	<xsl:variable name="technicsNumber"
		select="technics/QMFawTechnicsInfo//@technicsNumber" />
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>无标题文档</title>
				<link rel="stylesheet" type="text/css" href="default.css" />
				<meta http-equiv="Cache-Control" content="no-store"></meta>
				<meta http-equiv="Expires" content="-1"></meta>
				<script type="text/javascript" src="jquery-1.7.2.min.js">&#160;</script>
				<script type="text/javascript" src="pv/pvUtils.js">&#160;</script>
				<script type="text/javascript" src="pv/pvlaunch.js">&#160;</script>
				<script type="text/javascript" src="releasetools.js">&#160;</script>
				<script type="text/javascript" src="release_pv.js">&#160;</script>
				<script type="text/javascript" src="pdfobject.js">&#160;</script>
				<script type="text/javascript" src="pv/procedure_api.js">&#160;</script>
				<script type="text/javascript" src="pv/generic_prc.js">&#160;</script>
				<script language="javascript">
					$(function(){
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
				<div id="tbody">
					<div id="newContent">
						<span></span>
					</div>
					<div id="floatFrame" style="position: absolute;visibility:hidden;"></div>
					<div id="message_container"
						style="top:0; left:0; position: absolute;width: 0px; height: 0px; overflow: hidden;">
						<span></span>
					</div>
					<div id="menu_container"
						style="top:0; left:0; position: absolute;width: 0px; height: 0px;">
						<span></span>
					</div>
					<div id="right">
						<div class="sh_div">
							<xsl:attribute name="name">top_00</xsl:attribute>
							<div>
								<a>
									<xsl:attribute name="name">top_00</xsl:attribute>
								</a>
							</div>
							<div class="title" id="share">
								<!--<div><img class="logo" src="image/logo.png" /></div> -->
								<div class="title-font">
									<xsl:value-of select="technics/QMFawTechnicsInfo//@pplanNumber" />
									_
									<xsl:value-of select="technics/QMFawTechnicsInfo//@pplanName" />
								</div>
								<div class="childtitle-font"></div>
							</div>
							<div class="gongyi">
								<div class="gongyishuxing">
									<div class="order-title1">文件属性</div>
									<table>
										<tr>
											<td class="shuxing">文件编号：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@number" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">工艺版本：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@version" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">创建者：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@creatorDisplay" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">修改时间：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@modifyTime" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">专业类别：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@SpecializedType" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">工序名称：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@ProceduceName" />
											</td>
										</tr>
										<tr>
											<td class="shuxing">定制区域：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@CustomArea" /></td>
										</tr>
										<tr>
											<td class="shuxing">操作岗位：</td>
											<td class="neirong">
												<xsl:value-of select="technics/QMFawTechnicsInfo//@OperationJob" />
											</td>
										</tr>
									</table>
								</div>
						<div class="gongyishuoming">
							<div class="order-title2">文件说明</div>
							<table>
										<div class="procedureContent">
									<xsl:value-of select="technics/QMFawTechnicsInfo/TechnicsDescribe" />
								</div>
							</table>
						</div>
					</div>
				</div>

						<xsl:apply-templates select="technics/QMFawTechnicsInfo/steps" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/borrowTechnicss" />
						<xsl:apply-templates select="technics/QMFawTechnicsInfo/SOP/SOPParameter" />

					</div>
				</div>
				<div class="showtup" id="baginFloat">
				</div>
			</body>
		</html>
	</xsl:template>

	<!-- 依据文件 -->
	<xsl:template match="technics/QMFawTechnicsInfo/borrowTechnicss">
		<div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">borrowTechnics</xsl:attribute>
			<a>
				<xsl:attribute name="name">
					borrowTechnics
				</xsl:attribute>
			</a>
			<div class="order-title">依据文件</div>
			<table width="100%" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">文件编号</td>
					<td class="cailiao-title-bg">文件名称</td>
					<td class="cailiao-title-bg">文件类型</td>
				</tr>
				<xsl:for-each select="borrowTechnics">
					<tr>
						<th>
							<xsl:value-of select="@technicsNumber" />
						</th>
						<th>
							<xsl:value-of select="@technicsName" />
						</th>
						<th>
							<xsl:value-of select="@technicsType" />
						</th>
					</tr>
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>

	<!-- 参数项目 -->
	<xsl:template match="technics/QMFawTechnicsInfo/SOP/SOPParameter">
		<div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">ParameterInfo</xsl:attribute>
			<a>
				<xsl:attribute name="name">
					ParameterInfo
				</xsl:attribute>
			</a>
			<div class="order-title">参数项目</div>
			<table width="100%" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">专业类别</td>
					<td class="cailiao-title-bg">工序名称</td>
					<td class="cailiao-title-bg">物资类别</td>
					<td class="cailiao-title-bg">参数值</td>
				</tr>
				<xsl:for-each select="ParameterInfo">
					<tr>
						<th>
							<xsl:value-of select="@number" />
						</th>
						<th>
							<xsl:value-of select="@name" />
						</th>
						<th>
							<xsl:value-of select="@specializedType" />
						</th>
						<th>
							<xsl:value-of select="@procedureName" />
						</th>
						<th>
							<xsl:value-of select="@materialCategory" />
						</th>
						<th>
							<xsl:value-of select="@canshuzhi" />
						</th>
					</tr>
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>


	<xsl:template match="technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces">
		<xsl:for-each select="QMProcedureInfo">
			<div class="sh_div" style="visibility: hidden;">
				<xsl:attribute name="name"><xsl:value-of select="@paceId" /></xsl:attribute>
				<div class="circle"></div>
				<div>
					<div class="order-title order-title-bold">
						<a>
							<xsl:attribute name="name">
								<xsl:value-of select="@paceId" />
							</xsl:attribute>
						</a>
						<xsl:value-of select="@stepNumber" />
						_
						<xsl:value-of select="@stepName" />
					</div>
				</div>
				<div style="margin-left:10px">
					<xsl:choose>
						<xsl:when test="procedureContent != ''">
							<table>
								<tr>
									<td style="border-bottom-style: none;" width="60">工步内容：</td>
									<td style="border-bottom-style: none;">
										<div style="word-break: break-all;text-align:left;font-size: 12px;"
											class="procedureContent">
											<xsl:value-of select="procedureContent" />
										</div>
									</td>
								</tr>
							</table>
						</xsl:when>
						<xsl:otherwise>
							工步内容：
						</xsl:otherwise>
					</xsl:choose>
					<table>
						<tr>
							<!--<td style="border-bottom-style: none;" width="60">工步附图：</td> -->
							<xsl:variable name="pacevar" select="@paceId" />
							<xsl:for-each select="additiontables/additionaltable">
								<xsl:variable name="pdfpath" select="@pdfAbsolutePath" />
								<input type="hidden" id="{$pacevar}pdfpath" name="{$pacevar}pdfpath"
									value="{$pdfpath}" />
								<td>
									<div id="{$pacevar}pdfFrame"></div>
									<!--<a href="#" onclick="showGBFT(this,'{$pdfpath}')"><xsl:value-of
										select="@attachName"/><xsl:value-of select="@attachType"/></a> -->
								</td>
							</xsl:for-each>
						</tr>
						<!--<tr> <td> <div id="baginFloat2"> </div> </td> </tr> -->
					</table>
				</div>
			</div>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="technics/QMFawTechnicsInfo/steps">
		<xsl:for-each select="QMProcedureInfo">
			<!--重复开始 -->
			<!--工序开始 -->
			<xsl:variable name="gxStepNumber" select="@stepNumber" />
			<div class="sh_div" style="visibility: hidden;">
				<xsl:attribute name="name"><xsl:value-of select="@stepNumber" /></xsl:attribute>
				<div class="circle"></div>
				<div>
					<div class="order-title order-title-bold">
						<a>
							<xsl:attribute name="name">
								<xsl:value-of select="@stepNumber" />
							</xsl:attribute>
						</a>
						<xsl:value-of select="@stepNumber" />
						_
						<xsl:value-of select="@stepName" />
					</div>
					<div style="margin-left:10px">
						<xsl:choose>
							<xsl:when test="procedureContent != ''">
								<table>
									<tr>
										<td style="border-bottom-style: none;" width="60">工序内容：</td>
										<td style="border-bottom-style: none;">
											<div style="word-break: break-all;text-align:left;font-size: 12px;"
												class="procedureContent">
												<xsl:value-of select="procedureContent" />
											</div>
										</td>
									</tr>
								</table>
							</xsl:when>
							<xsl:otherwise>
								工序内容：
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
				<!--工序结束 -->

				<!--工艺材料信息开始 -->
	<div class="cailiao">
		<xsl:if test="tools/QMToolInfo">
			<div class="order-title">工装信息</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">工装类别</td>
					<td class="cailiao-title-bg">规格</td>
					<td class="cailiao-title-bg">型号</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="tools/QMToolInfo">
					<tr>
						<td><xsl:value-of select="@toolNum" /></td>
						<td><xsl:value-of select="@toolName" /></td>
						<td><xsl:value-of select="@toolType" /></td>
						<td><xsl:value-of select="@csize" /></td>
						<td><xsl:value-of select="@mindex" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="measures/QMMeasureInfo">
			<div class="order-title">量具信息</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">型号</td>
					<td class="cailiao-title-bg">规格</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="measures/QMMeasureInfo">
					<tr>
						<td><xsl:value-of select="@number" /></td>
						<td><xsl:value-of select="@name" /></td>
						<td><xsl:value-of select="@pindex" /></td>
						<td><xsl:value-of select="@csize" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="knifeTools/QMKnifeToolInfo">
			<div class="order-title">刀具信息</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">类别</td>
					<td class="cailiao-title-bg">材料</td>
					<td class="cailiao-title-bg">夹持直径</td>
					<td class="cailiao-title-bg">刃口长度</td>
					<td class="cailiao-title-bg">总长度</td>
					<td class="cailiao-title-bg">公差</td>
					<td class="cailiao-title-bg">最小加工尺寸</td>
					<td class="cailiao-title-bg">最大加工尺寸</td>
					<td class="cailiao-title-bg">结构形式</td>
					<td class="cailiao-title-bg">接口类型</td>
					<td class="cailiao-title-bg">技术备注</td>
					<td class="cailiao-title-bg">刃口圆角半径</td>
					<td class="cailiao-title-bg">齿数</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="knifeTools/QMKnifeToolInfo">
					<tr>
						<td><xsl:value-of select="@toolNum" /></td>
						<td><xsl:value-of select="@toolName" /></td>
						<td><xsl:value-of select="@knifetype" /></td>
						<td><xsl:value-of select="@cmat" /></td>
						<td><xsl:value-of select="@jczj" /></td>
						<td><xsl:value-of select="@rkcd" /></td>
						<td><xsl:value-of select="@zcd" /></td>
						<td><xsl:value-of select="@gc" /></td>
						<td><xsl:value-of select="@zxjgcc" /></td>
						<td><xsl:value-of select="@zdjgcc" /></td>
						<td><xsl:value-of select="@jgxs" /></td>
						<td><xsl:value-of select="@jklx" /></td>
						<td><xsl:value-of select="@jsbz" /></td>
						<td><xsl:value-of select="@rkyjbj" /></td>
						<td><xsl:value-of select="@cs" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="equips/QMEquipmentInfo">
			<div class="order-title">设备信息</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">设备编号</td>
					<td class="cailiao-title-bg">设备名称</td>
					<td class="cailiao-title-bg">设备型号</td>
					<td class="cailiao-title-bg">设备类别</td>
					<td class="cailiao-title-bg">设备规格</td>
					<td class="cailiao-title-bg">使用数量</td>
				</tr>
				<xsl:for-each select="equips/QMEquipmentInfo">
					<tr>
						<td><xsl:value-of select="@number" /></td>
						<td><xsl:value-of select="@name" /></td>
						<td><xsl:value-of select="@pindex" /></td>
						<td><xsl:value-of select="@equipmentType" /></td>
						<td><xsl:value-of select="@csize" /></td>
						<td><xsl:value-of select="@useCount" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="materials/QMMaterialInfo">
			<div class="order-title">工艺辅料</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编码</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">型号</td>
					<td class="cailiao-title-bg">规格</td>
					<td class="cailiao-title-bg">技术条件</td>
					<td class="cailiao-title-bg">计量单位</td>
					<td class="cailiao-title-bg">附加条件</td>
					<td class="cailiao-title-bg">使用数量</td>
					<td class="cailiao-title-bg">使用车间</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="materials/QMMaterialInfo">
					<tr>
						<td><xsl:value-of select="@materialNumber" /></td>
						<td><xsl:value-of select="@materialName" /></td>
						<td><xsl:value-of select="@clph" /></td>
						<td><xsl:value-of select="@clgg" /></td>
						<td><xsl:value-of select="@jstj" /></td>
						<td><xsl:value-of select="@jldw" /></td>
						<td><xsl:value-of select="@fjtj" /></td>
						<td><xsl:value-of select="@sl" /></td>
						<td><xsl:value-of select="@sycj" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="sdashboard/QMSDashboardInfo">
			<div class="order-title">标准仪器仪表</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">型号</td>
					<td class="cailiao-title-bg">类别</td>
					<td class="cailiao-title-bg">规格</td>
					<td class="cailiao-title-bg">使用数量</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="sdashboard/QMSDashboardInfo">
					<tr>
						<td><xsl:value-of select="@number" /></td>
						<td><xsl:value-of select="@name" /></td>
						<td><xsl:value-of select="@pindex" /></td>
						<td><xsl:value-of select="@equipmentType" /></td>
						<td><xsl:value-of select="@csize" /></td>
						<td><xsl:value-of select="@useCount" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>

		<xsl:if test="unsdashboard/QMUnSDashboardInfo">
			<div class="order-title">非标准仪器仪表</div>
			<table width="736" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<td class="cailiao-title-bg">编号</td>
					<td class="cailiao-title-bg">名称</td>
					<td class="cailiao-title-bg">型号</td>
					<td class="cailiao-title-bg">类别</td>
					<td class="cailiao-title-bg">规格</td>
					<td class="cailiao-title-bg">使用数量</td>
					<td class="cailiao-title-bg">备注</td>
				</tr>
				<xsl:for-each select="unsdashboard/QMUnSDashboardInfo">
					<tr>
						<td><xsl:value-of select="@number" /></td>
						<td><xsl:value-of select="@name" /></td>
						<td><xsl:value-of select="@pindex" /></td>
						<td><xsl:value-of select="@equipmentType" /></td>
						<td><xsl:value-of select="@csize" /></td>
						<td><xsl:value-of select="@useCount" /></td>
						<td><xsl:value-of select="@bz" /></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>
	</div>
</div>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>