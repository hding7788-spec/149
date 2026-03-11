<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:variable name="version" select="technics/QMFawTechnicsInfo/@version"/>
<xsl:variable name="pplanNumber" select="technics/QMFawTechnicsInfo//@pplanNumber"/>
	<xsl:variable name="technicsNumber" select="technics/QMFawTechnicsInfo//@technicsNumber"/>
<xsl:template match="/">
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>三维工艺发布页面</title>
	<link rel="stylesheet" type="text/css" href="main.css" />
	<script src="jquery-1.7.2.min.js" type="text/javascript">&#160;</script>
	<script src="releasetools.js" type="text/javascript">&#160;</script>
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
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/additiontables" />
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/technicsStateTables" />
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/borrowTechnicss" />
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/FZGY" />
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/GYDE" />
        	<xsl:apply-templates select="technics/QMFawTechnicsInfo/CLDE" />

        	<!-- PDF预览文件 -->
			<a href="PDFPreview.pdf" target="_BLANK">PDF预览文件</a><br/>
			<a target = "_blank" href="http://pdm.149.sast.casc/Windchill/netmarkets/jsp/ext/glaway/mpm/checkTable/showSummaryTable.jsp?technicsNumber={$technicsNumber}">检验记录及照片样张查看</a>
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
  		<legend><h3>基本信息</h3></legend>
		   <table width="230" border="0" cellspacing="10" cellpadding="0">
		      <tr>
		        <td>零件编号：</td>
		        <td><xsl:value-of select="@partNumber"/></td>
		      </tr>
		      <tr>
		        <td>零件名称：</td>
		        <td><xsl:value-of select="@partName"/></td>
		      </tr>
		      <tr>
		        <td>PBOM版本：</td>
		        <td><xsl:value-of select="@partVersion"/></td>
		      </tr>
		   </table>
	</fieldset>
  	<fieldset>
  		<legend><h3>工艺信息</h3></legend>
	    <table width="230" border="0" cellspacing="10" cellpadding="0">
	      <tr>
	        <td>工艺文件状态：</td>
	        <td><xsl:value-of select="@lifecycle"/></td>
	      </tr>
	      <tr>
	        <td>文件密级：</td>
	        <td><xsl:value-of select="@SECRET"/></td>
	      </tr>
		  <tr>
			<td>签署审批信息：</td>
		  </tr>
		  <tr>
		  	<td>编制者：<xsl:value-of select="@SHEJI" /></td>
		  </tr>
		  <tr>
		    <td>校对者：<xsl:value-of select="@JIAODUI" /></td>
		  </tr>
          <tr>
            <td>审核者：<xsl:value-of select="@SHENHE" /></td>
          </tr>
		  <tr>
		    <td>内部会签者：<xsl:value-of select="@NEIBUHUIQIAN" /></td>
		  </tr>
		  <tr>
		    <td>外部会签者：<xsl:value-of select="@WAIBUHUIQIAN" /></td>
		  </tr>
		  <tr>
		    <td>标审者：<xsl:value-of select="@BIAOSHEN" /></td>
		  </tr>
		  <tr>
		  <td>批准者：<xsl:value-of select="@PIZHUNZHE" /></td>
		  </tr>
	    </table>
	</fieldset>
	<fieldset>
		<legend>
			<h3>设计文件</h3>
		</legend>
		<ul>
			<li>说明文档
				<ol>
					<xsl:for-each select="designFile/DES/refrenceDoc">
						<li>
							<a target="_blank">
								<xsl:if test="@type='doc'">
									<xsl:choose>
										<xsl:when test="@pvsName=''">
											<a href="javascript:void(0);" onclick="alert('无对应电子签名文件');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@pvsName='locked'">
													<a href="javascript:void(0);" onclick="alert('文件密级非公开或内部');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
												</xsl:when>
												<xsl:otherwise>
													<xsl:attribute name="href">model/<xsl:value-of select="@pvsName"/></xsl:attribute>
													<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>

								</xsl:if>
								<xsl:if test="@type='epm'">
									<xsl:attribute name="href">creoAnnotation.html?number=<xsl:value-of select="@number"/>$$<xsl:value-of select="@pvsName"/></xsl:attribute>
									<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
								</xsl:if>
							</a>
						</li>
					</xsl:for-each>
				</ol>
			</li>
		</ul>
		<ul>
			<li>
				参考文档
				<ol>
					<xsl:for-each select="designFile/REF/refrenceDoc">
						<li>
							<a target="_blank">
								<xsl:if test="@type='doc'">
									<xsl:choose>
										<xsl:when test="@pvsName=''">
											<a href="javascript:void(0);" onclick="alert('无对应电子签名文件');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@pvsName='locked'">
													<a href="javascript:void(0);" onclick="alert('文件密级非公开或内部');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
												</xsl:when>
												<xsl:otherwise>
													<xsl:attribute name="href">model/<xsl:value-of select="@pvsName"/></xsl:attribute>
													<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>

								</xsl:if>
								<xsl:if test="@type='epm'">
									<xsl:attribute name="href">creoAnnotation.html?number=<xsl:value-of select="@number"/>$$<xsl:value-of select="@pvsName"/></xsl:attribute>
									<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
								</xsl:if>
							</a>
						</li>
					</xsl:for-each>
				</ol>
			</li>
		</ul>
		<ul>
			<li>CAD文档
				<ol>
					<xsl:for-each select="designFile/CAD/refrenceDoc">
						<li>
							<a target="_blank">
								<xsl:if test="@type='doc'">
									<xsl:choose>
										<xsl:when test="@pvsName=''">
											<a href="javascript:void(0);" onclick="alert('无对应电子签名文件');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@pvsName='locked'">
													<a href="javascript:void(0);" onclick="alert('文件密级非公开或内部');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
												</xsl:when>
												<xsl:otherwise>
													<xsl:attribute name="href">model/<xsl:value-of select="@pvsName"/></xsl:attribute>
													<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>

								</xsl:if>
								<xsl:if test="@type='epm'">
									<xsl:choose>
										<xsl:when test="@pvsName=''">
											<a href="javascript:void(0);" onclick="alert('无可视化文件');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="@pvsName='locked'">
													<a href="javascript:void(0);" onclick="alert('文件密级非公开或内部');"><xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)</a>
												</xsl:when>
												<xsl:otherwise>
													<xsl:attribute name="href">creoAnnotation.html?number=<xsl:value-of select="@number"/>$$<xsl:value-of select="@pvsName"/></xsl:attribute>
													<xsl:value-of select="@name"/>(<xsl:value-of select="@number"/>)
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>

								</xsl:if>
							</a>
						</li>
					</xsl:for-each>
				</ol>
			</li>
		</ul>
	</fieldset>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/additiontables" >
	<ul class="nav">
      <li>
      	<a target="frameDivId">
			工艺附表
     	</a>
         <ul>
         	<xsl:for-each select="additionaltable">
         		<li>
       				<a target="_blank">
         				<xsl:attribute name="href"><xsl:value-of select="@absolutePath" /></xsl:attribute>
         				<xsl:value-of select="@attachName" />
       				</a>
   				</li>
         	</xsl:for-each>
         </ul>
        </li>
       </ul>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/technicsStateTables" >
	<ul class="nav">
      <li>
      	<a target="frameDivId">
			工艺状态表
     	</a>
         <ul>
         	<xsl:for-each select="technicsStateTable">
         		<li>
       				<a target="frameDivId" class="nav_anchor">
       					<xsl:attribute name="href">content.html#<xsl:value-of select="@zzdw" /></xsl:attribute>
         				<xsl:value-of select="@zzdw" />_<xsl:value-of select="@sydw" />_<xsl:value-of select="@gyzt" />
       				</a>
   				</li>
         	</xsl:for-each>
         </ul>
        </li>
       </ul>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/borrowTechnicss" >
	<ul class="nav">
    	<li>
      		<a target="frameDivId" class="nav_anchor">
				<xsl:attribute name="href">content.html#borrowTechnics</xsl:attribute>
				<xsl:attribute name="cortonaID">borrowTechnics</xsl:attribute>
				典型/通用工艺
			</a>
        </li>
    </ul>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/FZGY" >
	<ul class="nav">
    	<li>
      		<a target="frameDivId" class="nav_anchor">
				<xsl:attribute name="href">content.html#FZTECHNICS</xsl:attribute>
				<xsl:attribute name="cortonaID">FZTECHNICS</xsl:attribute>
				辅制工艺
			</a>
        </li>
    </ul>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/GYDE" >
	<ul class="nav">
    	<li>
      		<a target="frameDivId" class="nav_anchor">
				<xsl:attribute name="href">content.html#GYDE</xsl:attribute>
				<xsl:attribute name="cortonaID">GYDE</xsl:attribute>
				工艺定额
			</a>
        </li>
    </ul>
</xsl:template>

<xsl:template match="technics/QMFawTechnicsInfo/CLDE" >
	<ul class="nav">
    	<li>
      		<a target="frameDivId" class="nav_anchor">
				<xsl:attribute name="href">content.html#CLDE</xsl:attribute>
				<xsl:attribute name="cortonaID">CLDE</xsl:attribute>
				材料定额
			</a>
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
         		<li>
         			<a target="frameDivId" class="nav_anchor">
         				<xsl:attribute name="href">content.html#<xsl:value-of select="@stepNumber" /></xsl:attribute>
         				<xsl:attribute name="title"><xsl:value-of select="@GXJS" /></xsl:attribute>
         				<xsl:attribute name="cortonaID"><xsl:value-of select="@cortonaID" /></xsl:attribute>
         				<xsl:value-of select="@stepNumber" />_<xsl:value-of select="@stepName" />_<xsl:value-of select="@workShop" />_<xsl:value-of select="@workType" />
       				</a>
   				</li>
         	</xsl:for-each>
         </ul>
         <!--
         <li>
        	 <a target="_blank" id="top_desc" href="technics_desc.mht">
        		 工艺说明
        	 </a>
         </li>
          -->
        </li>
       </ul>

</xsl:template>

<xsl:template match="technics" >
    <table width="223" border="0" cellspacing="5" cellpadding="0">
    <tr>
      <td class="check-news-bg"><span>工艺<br/></span><span>环节</span></td>
      <td class="check-news-bg">负责人</td>
      <td class="check-news-bg">日期</td>
      <td class="check-news-bg">备注</td>
    </tr>
    <xsl:if test="QMFawTechnicsInfo//@name1">
    	<tr>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@name1" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@user1" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@date1" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@comment1" /></td>
	    </tr>
    </xsl:if>
	<xsl:if test="QMFawTechnicsInfo//@name2">
    	<tr>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@name2" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@user2" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@date2" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@comment2" /></td>
	    </tr>
    </xsl:if>
    <xsl:if test="QMFawTechnicsInfo//@name3">
    	<tr>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@name3" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@user3" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@date3" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@comment3" /></td>
	    </tr>
    </xsl:if>
    <xsl:if test="QMFawTechnicsInfo//@name4">
    	<tr>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@name4" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@user4" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@date4" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@comment4" /></td>
	    </tr>
    </xsl:if>
    <xsl:if test="QMFawTechnicsInfo//@name5">
    	<tr>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@name5" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@user5" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@date5" /></td>
	      <td><xsl:value-of select="QMFawTechnicsInfo//@comment5" /></td>
	    </tr>
    </xsl:if>
  </table>
</xsl:template>
</xsl:stylesheet>